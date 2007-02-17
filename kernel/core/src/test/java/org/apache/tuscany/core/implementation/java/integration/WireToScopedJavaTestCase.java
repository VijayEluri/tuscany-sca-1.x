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
package org.apache.tuscany.core.implementation.java.integration;

import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.ComponentStart;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.component.event.HttpSessionStart;
import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.component.scope.HttpSessionScopeContainer;
import org.apache.tuscany.core.component.scope.RequestScopeContainer;
import org.apache.tuscany.core.component.scope.StatelessScopeContainer;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.component.TargetImpl;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.core.wire.jdk.JDKWireService;

/**
 * Validates wiring from a wire to Java atomic component by scope
 *
 * @version $$Rev$$ $$Date$$
 */
public class WireToScopedJavaTestCase extends TestCase {
    private WorkContext workContext = new WorkContextImpl();
    private WireService wireService = new JDKWireService(new WorkContextImpl(), null);

    public void testToStatelessScope() throws Exception {
        StatelessScopeContainer scope = new StatelessScopeContainer(workContext, null);
        scope.start();
        final Wire wire = getWire(scope);
        Target service = wireService.createProxy(Target.class, wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals(null, service.getString());
        scope.stop();
    }

    public void testToRequestScope() throws Exception {
        final RequestScopeContainer scope = new RequestScopeContainer(workContext, null);
        scope.start();

        scope.onEvent(new RequestStart(this));

        final Wire wire = getWire(scope);
        Target service = wireService.createProxy(Target.class, wire);
        assertNotNull(service);
        service.setString("foo");

        // another request
        Executor executor = Executors.newSingleThreadExecutor();
        FutureTask<Void> future = new FutureTask<Void>(new Runnable() {
            public void run() {
                scope.onEvent(new RequestStart(this));
                Target service2 = wireService.createProxy(Target.class, wire);
                Target target2 = wireService.createProxy(Target.class, wire);
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
        HttpSessionScopeContainer scope = new HttpSessionScopeContainer(workContext, null);
        scope.start();
        Object session1 = new Object();
        workContext.setIdentifier(Scope.SESSION, session1);
        scope.onEvent(new HttpSessionStart(this, session1));

        final Wire wire = getWire(scope);
        Target service = wireService.createProxy(Target.class, wire);
        Target target = wireService.createProxy(Target.class, wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());

        workContext.clearIdentifier(Scope.SESSION);

        //second session
        Object session2 = new Object();
        workContext.setIdentifier(Scope.SESSION, session2);
        scope.onEvent(new HttpSessionStart(this, session2));

        Target service2 = wireService.createProxy(Target.class, wire);
        assertNotNull(service2);
        assertNull(service2.getString());
        Target target2 = wireService.createProxy(Target.class, wire);
        service2.setString("bar");
        assertEquals("bar", service2.getString());
        assertEquals("bar", target2.getString());

        scope.onEvent(new HttpSessionEnd(this, session2));
        workContext.clearIdentifier(Scope.SESSION);

        workContext.setIdentifier(Scope.SESSION, session1);
        assertEquals("foo", service.getString());

        scope.onEvent(new HttpSessionEnd(this, session1));

        scope.stop();
    }

    public void testToCompositeScope() throws Exception {
        CompositeScopeContainer scope = new CompositeScopeContainer(null);
        scope.start();
        scope.onEvent(new ComponentStart(this, null));
        final Wire wire = getWire(scope);
        Target service = wireService.createProxy(Target.class, wire);
        Target target = wireService.createProxy(Target.class, wire);
        assertNotNull(service);
        service.setString("foo");
        assertEquals("foo", service.getString());
        assertEquals("foo", target.getString());
        scope.onEvent(new ComponentStop(this, null));
        scope.stop();
    }

    private Wire getWire(ScopeContainer scope) throws Exception {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setImplementationClass(TargetImpl.class);
        configuration.setInstanceFactory(new PojoObjectFactory<TargetImpl>(TargetImpl.class.getConstructor()));
        configuration.setWorkContext(workContext);
        configuration.setName(new URI("source"));
        configuration.setName(new URI("target"));

        JavaAtomicComponent target = new JavaAtomicComponent(configuration);
        target.setScopeContainer(scope);

        Wire wire = createWire("target#Target", Target.class, target);

        target.start();
        return wire;
    }

    private static <T> Wire createWire(String targetName, Class<T> interfaze, JavaAtomicComponent target)
        throws InvalidServiceContractException {
        Wire wire = new WireImpl();
        JavaServiceContract contract = new JavaServiceContract(interfaze);
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        wire.setSourceContract(contract);
        createChains(interfaze, wire);
        wire.setTargetUri(URI.create(targetName));
        wire.setSourceUri(URI.create("component#ref"));
        for (InvocationChain chain : wire.getInvocationChains().values()) {
            chain.setTargetInvoker(target.createTargetInvoker("target", chain.getOperation()));
        }
        return wire;
    }

    private static void createChains(Class<?> interfaze, Wire wire)
        throws InvalidServiceContractException {
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        ServiceContract<?> contract = registry.introspect(interfaze);
        for (Operation operation : contract.getOperations().values()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            wire.addInvocationChain(operation, chain);
        }
    }

}
