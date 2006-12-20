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

import java.lang.reflect.Method;
import java.net.URI;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.implementation.java.ConstructorDefinition;
import org.apache.tuscany.spi.implementation.java.JavaMappedProperty;
import org.apache.tuscany.spi.implementation.java.JavaMappedReference;
import org.apache.tuscany.spi.implementation.java.JavaMappedService;
import org.apache.tuscany.spi.implementation.java.PojoComponentType;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundWire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.implementation.system.wire.SystemInboundWire;
import org.apache.tuscany.core.injection.SingletonObjectFactory;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class SystemComponentBuilderTestCase extends TestCase {

    CompositeComponent parent;
    DeploymentContext deploymentContext;
    SystemComponentBuilder builder = new SystemComponentBuilder();
    ModuleScopeContainer container;

    /**
     * Verifies lifecycle callbacks are made
     */
    public void testLifecycleBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setInitLevel(50);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.COMPOSITE);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.start();
        container.onEvent(new CompositeStart(this, null));
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertTrue(foo.initialized);
        container.onEvent(new CompositeStop(this, null));
        assertTrue(foo.destroyed);
    }

    /**
     * Verifies properties are built properly
     */
    public void testPropertyBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setInitLevel(50);
        Method initMethod = FooImpl.class.getMethod("init");
        initMethod.setAccessible(true);
        type.setInitMethod(initMethod);
        Method destroyMethod = FooImpl.class.getMethod("destroy");
        destroyMethod.setAccessible(true);
        type.setDestroyMethod(destroyMethod);
        type.setImplementationScope(Scope.COMPOSITE);
        JavaMappedProperty mappedProp = new JavaMappedProperty();
        mappedProp.setName("prop");
        Method propMethod = FooImpl.class.getMethod("setProp", String.class);
        propMethod.setAccessible(true);
        mappedProp.setMember(propMethod);
        type.add(mappedProp);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        PropertyValue<String> propVal = new PropertyValue<String>();
        propVal.setName("prop");
        propVal.setValueFactory(new SingletonObjectFactory<String>("value"));
        definition.add(propVal);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertEquals("value", foo.prop);
        container.onEvent(new CompositeStop(this, null));
    }

    /**
     * Verifies references are built properly
     */
    public void testRefBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        Method refMethod = FooImpl.class.getMethod("setRef", Foo.class);
        refMethod.setAccessible(true);
        mappedReference.setMember(refMethod);
        ServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        ReferenceTarget target = new ReferenceTarget();
        target.setReferenceName("ref");
        target.addTarget(new URI("foo"));
        definition.add(target);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        OutboundWire wire = component.getOutboundWires().get("ref").get(0);
        SystemInboundWire inbound = EasyMock.createMock(SystemInboundWire.class);
        FooImpl targetFoo = new FooImpl();
        EasyMock.expect(inbound.getTargetService()).andReturn(targetFoo);
        EasyMock.replay(inbound);
        wire.setTargetWire(inbound);
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertNotNull(foo.ref);
        container.onEvent(new CompositeStop(this, null));
        EasyMock.verify(inbound);
    }

    /**
     * Verifies autowires are built properly
     */
    public void testAutowireBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.COMPOSITE);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        mappedReference.setAutowire(true);
        Method refMethod = FooImpl.class.getMethod("setRef", Foo.class);
        refMethod.setAccessible(true);
        mappedReference.setMember(refMethod);
        ServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ConstructorDefinition<FooImpl> ctorDef = new ConstructorDefinition<FooImpl>(FooImpl.class.getConstructor());
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl.class);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        FooImpl targetFoo = new FooImpl();
        EasyMock.expect(parent.resolveSystemInstance(EasyMock.eq(Foo.class))).andReturn(targetFoo);
        EasyMock.replay(parent);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.start();
        FooImpl foo = (FooImpl) component.getServiceInstance();
        assertNotNull(foo.ref);
        container.onEvent(new CompositeStop(this, null));
        EasyMock.verify(parent);
    }

    /**
     * Verifies constructor-based autowiring
     */
    public void testAutowireConstructorBuild() throws Exception {
        PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type =
            new PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>>();
        type.setImplementationScope(Scope.COMPOSITE);
        ConstructorDefinition<FooImpl2> ctorDef =
            new ConstructorDefinition<FooImpl2>(FooImpl2.class.getConstructor(Foo.class));
        ctorDef.getInjectionNames().add("ref");
        type.setConstructorDefinition(ctorDef);
        SystemImplementation impl = new SystemImplementation();
        impl.setComponentType(type);
        impl.setImplementationClass(FooImpl2.class);
        JavaMappedReference mappedReference = new JavaMappedReference();
        mappedReference.setName("ref");
        mappedReference.setAutowire(true);
        ServiceContract contract = new JavaServiceContract(Foo.class);
        mappedReference.setServiceContract(contract);
        type.add(mappedReference);
        ComponentDefinition<SystemImplementation> definition = new ComponentDefinition<SystemImplementation>(impl);
        CompositeComponent parent = EasyMock.createNiceMock(CompositeComponent.class);
        FooImpl targetFoo = new FooImpl();
        EasyMock.expect(parent.resolveSystemInstance(EasyMock.eq(Foo.class))).andReturn(targetFoo);
        EasyMock.replay(parent);
        AtomicComponent component = builder.build(parent, definition, deploymentContext);
        component.start();
        container.onEvent(new CompositeStart(this, null));
        FooImpl2 foo = (FooImpl2) component.getServiceInstance();
        assertNotNull(foo.getRef());
        container.onEvent(new CompositeStop(this, null));
        EasyMock.verify(parent);
    }

    protected void setUp() throws Exception {
        super.setUp();
        parent = EasyMock.createNiceMock(CompositeComponent.class);
        container = new ModuleScopeContainer(null);
        container.start();
        deploymentContext = EasyMock.createMock(DeploymentContext.class);
        EasyMock.expect(deploymentContext.getModuleScope()).andReturn(container).atLeastOnce();
        EasyMock.replay(deploymentContext);
    }

    private static interface Foo {

    }

    private static class FooImpl implements Foo {
        private boolean initialized;
        private boolean destroyed;
        private String prop;
        private Foo ref;

        public FooImpl() {
        }

        public void init() {
            if (initialized) {
                fail();
            }
            initialized = true;
        }

        public void destroy() {
            if (destroyed) {
                fail();
            }
            destroyed = true;
        }

        public boolean isInitialized() {
            return initialized;
        }

        public String getProp() {
            return prop;
        }

        public void setProp(String prop) {
            this.prop = prop;
        }

        public Foo getRef() {
            return ref;
        }

        public void setRef(Foo ref) {
            this.ref = ref;
        }
    }

    private static class FooImpl2 implements Foo {
        private Foo ref;

        public FooImpl2(@Autowire Foo ref) {
            this.ref = ref;
        }

        public Foo getRef() {
            return ref;
        }

    }
}
