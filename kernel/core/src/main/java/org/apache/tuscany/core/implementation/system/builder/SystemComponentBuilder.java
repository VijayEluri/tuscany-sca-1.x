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
package org.apache.tuscany.core.implementation.system.builder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.host.ResourceHost;
import org.apache.tuscany.spi.builder.BuilderConfigException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentBuilderExtension;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.implementation.java.Resource;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.wire.OutboundWire;

import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWireImpl;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundAutowire;
import org.apache.tuscany.core.implementation.system.wire.SystemOutboundWireImpl;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.injection.ResourceObjectFactory;

/**
 * Produces system atomic components from a component definition
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemComponentBuilder extends ComponentBuilderExtension<SystemImplementation> {
    private ResourceHost host;

    protected Class<SystemImplementation> getImplementationType() {
        return SystemImplementation.class;
    }

    @Autowire
    public void setHost(ResourceHost host) {
        this.host = host;
    }

    @SuppressWarnings("unchecked")
    public AtomicComponent build(CompositeComponent parent,
                                 ComponentDefinition<SystemImplementation> definition,
                                 DeploymentContext deploymentContext) throws BuilderConfigException {
        PojoComponentType<ServiceDefinition, JavaMappedReference, JavaMappedProperty<?>> componentType =
            definition.getImplementation().getComponentType();

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setParent(parent);
        configuration.setScopeContainer(deploymentContext.getModuleScope());
        if (definition.getInitLevel() != null) {
            configuration.setInitLevel(definition.getInitLevel());
        } else {
            configuration.setInitLevel(componentType.getInitLevel());
        }
        Method initMethod = componentType.getInitMethod();
        if (initMethod != null) {
            configuration.setInitInvoker(new MethodEventInvoker<Object>(initMethod));
        }
        Method destroyMethod = componentType.getDestroyMethod();
        if (destroyMethod != null) {
            configuration.setDestroyInvoker(new MethodEventInvoker<Object>(destroyMethod));
        }
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            configuration.addServiceInterface(serviceDefinition.getServiceContract().getInterfaceClass());
        }
        // setup property injection sites
        for (JavaMappedProperty<?> property : componentType.getProperties().values()) {
            configuration.addPropertySite(property.getName(), property.getMember());
        }
        // setup reference injection sites
        for (JavaMappedReference reference : componentType.getReferences().values()) {
            Member member = reference.getMember();
            if (member != null) {
                // could be null if the reference is mapped to a constructor
                configuration.addReferenceSite(reference.getName(), member);
            }
        }

        for (Resource resource : componentType.getResources().values()) {
            Member member = resource.getMember();
            if (member != null) {
                // could be null if the resource is mapped to a constructor
                configuration.addResourceSite(resource.getName(), member);
            }
        }

        // setup constructor injection
        ConstructorDefinition<?> ctorDef = componentType.getConstructorDefinition();
        Constructor<?> constr = ctorDef.getConstructor();
        PojoObjectFactory<?> instanceFactory = new PojoObjectFactory(constr);
        configuration.setInstanceFactory(instanceFactory);
        configuration.getConstructorParamNames().addAll(ctorDef.getInjectionNames());
        configuration.setName(definition.getName());
        SystemAtomicComponentImpl component = new SystemAtomicComponentImpl(configuration);
        // handle properties
        Map<String, PropertyValue<?>> propertyValues = definition.getPropertyValues();
        processProperties(propertyValues, componentType.getProperties().values(), component);
        // handle inbound wires
        for (ServiceDefinition serviceDefinition : componentType.getServices().values()) {
            Class<?> interfaze = serviceDefinition.getServiceContract().getInterfaceClass();
            String name = serviceDefinition.getName();
            SystemInboundWire wire = new SystemInboundWireImpl(name, interfaze, component);
            component.addInboundWire(wire);
        }
        // handle references
        processReferences(definition, componentType.getReferences(), parent, component);
        // FIXME we need a way to build configuration references from autowires in the loader to eliminate this eval
        for (ReferenceDefinition reference : componentType.getReferences().values()) {
            if (reference.isAutowire()) {
                Class interfaze = reference.getServiceContract().getInterfaceClass();
                OutboundWire wire =
                    new SystemOutboundAutowire(reference.getName(), interfaze, parent, reference.isRequired());
                component.addOutboundWire(wire);
            }
        }

        // handle resources
        for (Resource resource : componentType.getResources().values()) {
            String name = resource.getName();
            boolean optional = resource.isOptional();
            Class<Object> type = (Class<Object>) resource.getType();
            ResourceObjectFactory<Object> factory;
            String mappedName = resource.getMappedName();
            if (mappedName == null) {
                // by type
                factory = new ResourceObjectFactory<Object>(type, optional, parent, host);
            } else {
                factory = new ResourceObjectFactory<Object>(type, mappedName, optional, parent, host);
            }
            component.addResourceFactory(name, factory);

        }

        return component;
    }

    private void processReferences(ComponentDefinition<SystemImplementation> definition,
                                   Map<String, JavaMappedReference> references,
                                   CompositeComponent parent,
                                   SystemAtomicComponentImpl component) {
        // no proxies needed for system components
        for (ReferenceTarget target : definition.getReferenceTargets().values()) {
            String referenceName = target.getReferenceName();
            JavaMappedReference referenceDefiniton = references.get(referenceName);
            Class interfaze = referenceDefiniton.getServiceContract().getInterfaceClass();
            OutboundWire wire;
            if (referenceDefiniton.isAutowire()) {
                boolean required = referenceDefiniton.isRequired();
                wire = new SystemOutboundAutowire(referenceName, interfaze, parent, required);
            } else {
                //FIXME support multiplicity!
                assert target.getTargets().size() == 1 : "Multiplicity not yet implemented";
                QualifiedName targetName = new QualifiedName(target.getTargets().get(0).getPath());
                wire = new SystemOutboundWireImpl(referenceName, targetName, interfaze);
            }
            component.addOutboundWire(wire);
        }
    }

    private void processProperties(Map<String, PropertyValue<?>> propertyValues,
                                   Collection<JavaMappedProperty<?>> properties,
                                   SystemAtomicComponentImpl component) {
        for (JavaMappedProperty<?> property : properties) {
            PropertyValue value = propertyValues.get(property.getName());
            ObjectFactory<?> factory;
            if (value != null) {
                factory = value.getValueFactory();
            } else {
                factory = property.getDefaultValueFactory();
            }
            if (factory != null) {
                component.addPropertyFactory(property.getName(), factory);
            }
        }
    }
}
