/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.tuscany.core.implementation.java;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilder;
import org.apache.tuscany.spi.builder.physical.PhysicalComponentBuilderRegistry;
import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.physical.PhysicalOperationDefinition;
import org.apache.tuscany.spi.services.classloading.ClassLoaderRegistry;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.ProxyService;

import org.apache.tuscany.core.builder.physical.WireAttachException;
import org.apache.tuscany.core.component.InstanceFactoryProvider;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireSourceDefinition;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalWireTargetDefinition;
import org.apache.tuscany.core.wire.WireObjectFactory;

/**
 * The physical component builder for Java implementation types. Responsible for creating the Component runtime artifact
 * from a physical component definition
 *
 * @version $Rev$ $Date$
 * @param <T> the implementation class for the defined component
 */
public class JavaPhysicalComponentBuilder<T>
    implements PhysicalComponentBuilder<JavaPhysicalComponentDefinition<T>, JavaComponent<T>>,
    WireAttacher<JavaComponent, JavaPhysicalWireSourceDefinition, JavaPhysicalWireTargetDefinition> {

    // Classloader registry
    private ClassLoaderRegistry classLoaderRegistry;

    // Scope registry
    private ScopeRegistry scopeRegistry;

    private WorkContext workContext;

    private ProxyService proxyService;

    /**
     * Injects builder registry.
     *
     * @param registry PhysicalComponentBuilder registry.
     */
    @Reference
    public void setBuilderRegistry(PhysicalComponentBuilderRegistry registry) {
        registry.register(JavaPhysicalComponentDefinition.class, this);
    }


    @Reference
    public void setWorkContext(WorkContext workContext) {
        this.workContext = workContext;
    }

    @Reference
    public void setProxyService(ProxyService proxyService) {
        this.proxyService = proxyService;
    }

    /**
     * Builds a component from its physical component definition.
     *
     * @param componentDefinition Physical component definition of the component to be built.
     * @return A component instance that is ready to go live.
     * @throws BuilderException If unable to build the component.
     */
    public JavaComponent<T> build(JavaPhysicalComponentDefinition<T> componentDefinition) throws BuilderException {

        URI componentId = componentDefinition.getComponentId();
        InstanceFactoryProvider<T> provider = componentDefinition.getProvider();
        JavaComponent<T> component = new JavaComponent<T>(componentId, provider, null, 0, -1, -1);

        setScopeContainer(componentDefinition, component);

        setInstanceFactoryClass(componentDefinition, component);

        return component;
    }

    /**
     * Injects classloader registry.
     *
     * @param classLoaderRegistry Class loader registry.
     */
    @Reference
    public void setClassLoaderRegistry(ClassLoaderRegistry classLoaderRegistry) {
        this.classLoaderRegistry = classLoaderRegistry;
    }

    /**
     * Injects scope registry.
     *
     * @param scopeRegistry Scope registry.
     */
    @Reference
    public void setScopeRegistry(ScopeRegistry scopeRegistry) {
        this.scopeRegistry = scopeRegistry;
    }

    /*
     * Sets the instance factory class.
     */
    private void setInstanceFactoryClass(JavaPhysicalComponentDefinition componentDefinition, JavaComponent component) {
/*
        // TODO use MPCL to load IF class
        URI classLoaderId = componentDefinition.getClassLoaderId();
        byte[] instanceFactoryByteCode = componentDefinition.getInstanceFactoryByteCode(); //NOPMD
        ClassLoader appCl = classLoaderRegistry.getClassLoader(classLoaderId); //NOPMD
        ClassLoader systemCl = getClass().getClassLoader(); //NOPMD        
        ClassLoader mpcl = null; //NOPMD
        Class<InstanceFactory<?>> instanceFactoryClass = null;
        component.setInstanceFactoryClass(instanceFactoryClass);
*/
    }

    /*
     * Set the scope container.
     */
    private void setScopeContainer(JavaPhysicalComponentDefinition componentDefinition, JavaComponent component) {
        Scope scope = componentDefinition.getScope();
        ScopeContainer scopeContainer = scopeRegistry.getScopeContainer(scope);
        component.setScopeContainer(scopeContainer);
    }

    /**
     * Attaches the source to the component.
     *
     * @param component Component.
     * @param wire
     * @param source    Source.
     */
    @SuppressWarnings({"unchecked"})
    public void attach(JavaComponent component, Wire wire, JavaPhysicalWireSourceDefinition source) {
        Class<?> type = component.getMemberType(wire.getSourceUri());
        WireObjectFactory<?> factory = new WireObjectFactory(type, wire, proxyService);
        component.setObjectFactory(wire.getSourceUri(), factory);
    }

    /**
     * Attaches the target to the component.
     *
     * @param component Component.
     * @param wire      the wire to attach
     * @param target    Target.
     */
    public void attach(JavaComponent component, Wire wire, JavaPhysicalWireTargetDefinition target)
        throws WireAttachException {
        ScopeContainer scopeContainer = component.getScopeContainer();
        Class<?> implementationClass = component.getImplementationClass();
        ClassLoader loader = implementationClass.getClassLoader();
        // attach the invoker interceptor to forward invocation chains
        for (Map.Entry<PhysicalOperationDefinition, InvocationChain> entry : wire.getPhysicalInvocationChains()
            .entrySet()) {
            PhysicalOperationDefinition operation = entry.getKey();
            InvocationChain chain = entry.getValue();
            List<String> params = operation.getParameters();
            Class<?>[] paramTypes = new Class<?>[params.size()];
            assert loader != null;
            for (int i = 0; i < params.size(); i++) {
                String param = params.get(i);
                try {
                    paramTypes[i] = loader.loadClass(param);
                } catch (ClassNotFoundException e) {
                    URI sourceUri = wire.getSourceUri();
                    URI targetUri = wire.getTargetUri();
                    throw new WireAttachException("Implementation class not found", sourceUri, targetUri, e);
                }
            }
            Method method;
            try {
                method = implementationClass.getMethod(operation.getName(), paramTypes);
            } catch (NoSuchMethodException e) {
                URI sourceUri = wire.getSourceUri();
                URI targetUri = wire.getTargetUri();
                throw new WireAttachException("No matching method found", sourceUri, targetUri, e);
            }
            chain.addInterceptor(new JavaInvokerInterceptor(method, component, scopeContainer, workContext));
        }
    }

}
