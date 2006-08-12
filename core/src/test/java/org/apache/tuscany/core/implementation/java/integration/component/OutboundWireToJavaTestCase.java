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
package org.apache.tuscany.core.implementation.java.integration.component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContainer;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.component.scope.RequestScopeContainer;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.implementation.java.mock.MockFactory;
import org.apache.tuscany.core.implementation.java.mock.components.Target;
import org.apache.tuscany.core.implementation.java.mock.components.TargetImpl;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.util.MethodHashMap;
import org.apache.tuscany.core.wire.OutboundInvocationChainImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * Validates wiring from a service context to Java atomic contexts by scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class OutboundWireToJavaTestCase extends TestCase {
    private WorkContext workContext = new WorkContextImpl();
    private WireService wireService = new JDKWireService();

    public void testToStatelessScope() throws Exception {
        StatelessScopeContainer scope = new StatelessScopeContainer(workContext);
        scope.start();
        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals(null, service.getString());
        scope.stop();
    }

    public void testToRequestScope() throws Exception {
        final RequestScopeContainer scope = new RequestScopeContainer(workContext);
        scope.start();

        scope.onEvent(new RequestStart(this));

        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");

        // another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                scope.onEvent(new RequestStart(this));
                Target service2 = wireService.createProxy(wire);
                Target target2 = wireService.createProxy(wire);
                assertEquals(null, service2.getString());
                service2.setString("bar");
                assertEquals("bar", service2.getString());
                assertEquals("bar", target2.getString());
                scope.onEvent(new RequestEnd(this));
            }
        }, null);
        executor.execute(future);
        future.get();

        assertEquals("foo", service.getString());
        scope.onEvent(new RequestEnd(this));
        scope.stop();
    }

    public void testToSessionScope() throws Exception {
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(workContext);
        scope.start();
        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session1);
        scope.onEvent(new HttpSessionStart(this, session1));

        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        Target target = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());

        workContext.clearIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER);

        //second session
        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session2);
        scope.onEvent(new HttpSessionStart(this, session2));

        Target service2 = wireService.createProxy(wire);
        assertNotNull(service2);
        assertNull(service2.getString());
        Target target2 = wireService.createProxy(wire);
        service2.setString("bar");
        assertEquals("bar", service2.getString());
        assertEquals("bar", target2.getString());

        scope.onEvent(new HttpSessionEnd(this, session2));
        workContext.clearIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER);

        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session1);
        assertEquals("foo", service.getString());

        scope.onEvent(new HttpSessionEnd(this, session1));

        scope.stop();
    }

    public void testToModuleScope() throws Exception {

        ModuleScopeContainer scope = new ModuleScopeContainer(workContext);
        scope.start();
        scope.onEvent(new CompositeStart(this, null));
        final OutboundWire<Target> wire = getWire(scope);
        Target service = wireService.createProxy(wire);
        Target target = wireService.createProxy(wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());
        scope.onEvent(new CompositeStop(this, null));
        scope.stop();
    }

    @SuppressWarnings("unchecked")
    private OutboundWire<Target> getWire(ScopeContainer scope) throws NoSuchMethodException {
        ConnectorImpl connector = new ConnectorImpl();
        OutboundWire<Target> wire = createOutboundWire(new QualifiedName("target/Target"), Target.class);

        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scope);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.addServiceInterface(Target.class);
        JavaAtomicComponent<?> atomicComponent = new JavaAtomicComponent("target", configuration, null);
        InboundWire targetWire = MockFactory.createTargetWire("Target", Target.class);
        atomicComponent.addInboundWire(targetWire);
        connector.connect(atomicComponent, atomicComponent, wire, atomicComponent.getInboundWire("Target"), false);
        atomicComponent.start();
        return wire;
    }

    public static <T> OutboundWire<T> createOutboundWire(QualifiedName targetName, Class<T> interfaze) {
        OutboundWire<T> wire = new OutboundWireImpl<T>();
        wire.setBusinessInterface(interfaze);
        wire.setTargetName(targetName);
        wire.addInvocationChains(createInvocationChains(interfaze));
        return wire;
    }

    private static Map<Method, OutboundInvocationChain> createInvocationChains(Class<?> interfaze) {
        Map<Method, OutboundInvocationChain> invocations = new MethodHashMap<OutboundInvocationChain>();
        Method[] methods = interfaze.getMethods();
        for (Method method : methods) {
            OutboundInvocationChain chain = new OutboundInvocationChainImpl(method);
            invocations.put(method, chain);
        }
        return invocations;
    }

}
