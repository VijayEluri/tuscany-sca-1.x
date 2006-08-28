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

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;

import org.apache.tuscany.core.implementation.composite.CompositeComponentImpl;
import org.apache.tuscany.core.implementation.java.mock.components.Source;
import org.apache.tuscany.core.implementation.java.mock.components.SourceImpl;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.wire.jdk.JDKWireService;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Invocation;
import org.jmock.core.Stub;

/**
 * @version $$Rev$$ $$Date$$
 */
public class JavaComponentBuilderTestCase extends MockObjectTestCase {
    private DeploymentContext deploymentContext;

    @SuppressWarnings("unchecked")
    public void testBuild() throws Exception {
        CompositeComponent parent = new CompositeComponentImpl(null, null, null, null);

        PojoComponentType sourceType = new PojoComponentType();
        sourceType.setImplementationScope(Scope.MODULE);
        JavaMappedReference reference = new JavaMappedReference();
        reference.setName("target");
        reference.setMember(SourceImpl.class.getMethod("setTarget", Target.class));
        sourceType.add(reference);

        ServiceContract sourceContract = new JavaServiceContract(Source.class);
        ServiceDefinition sourceServiceDefinition = new JavaMappedService();
        sourceServiceDefinition.setName("Source");
        sourceServiceDefinition.setServiceContract(sourceContract);

        sourceType.add(sourceServiceDefinition);
        sourceType.setConstructorDefinition(new ConstructorDefinition(SourceImpl.class.getConstructor((Class[]) null)));
        JavaImplementation sourceImpl = new JavaImplementation();
        sourceImpl.setComponentType(sourceType);
        sourceImpl.setImplementationClass(SourceImpl.class);
        ComponentDefinition<JavaImplementation> sourceComponentDefinition =
            new ComponentDefinition<JavaImplementation>(sourceImpl);

        JavaComponentBuilder builder = new JavaComponentBuilder();
        builder.setWireService(new JDKWireService());
        JavaAtomicComponent<Source> component =
            (JavaAtomicComponent<Source>) builder.build(parent, sourceComponentDefinition, deploymentContext);
        deploymentContext.getModuleScope().start();
        component.start();
        Source source = component.getServiceInstance();
        assertNotNull(source);
        component.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        deploymentContext = new RootDeploymentContext(null, null, createMock(), null);
    }

    private ScopeContainer createMock() {
        Mock mock = mock(ScopeContainer.class);
        mock.expects(once()).method("start");
        mock.expects(atLeastOnce()).method("register");
        mock.expects(atLeastOnce()).method("getScope").will(returnValue(Scope.MODULE));
        mock.expects(atLeastOnce()).method("getInstance").will(new Stub() {
            private Map<AtomicComponent, Object> cache = new HashMap<AtomicComponent, Object>();

            public Object invoke(Invocation invocation) throws Throwable {
                AtomicComponent component = (AtomicComponent) invocation.parameterValues.get(0);
                Object instance = cache.get(component);
                if (instance == null) {
                    instance = component.createInstance();
                    cache.put(component, instance);
                }
                return instance;
            }

            public StringBuffer describeTo(StringBuffer stringBuffer) {
                return null;
            }
        });
        return (ScopeContainer) mock.proxy();
    }

}
