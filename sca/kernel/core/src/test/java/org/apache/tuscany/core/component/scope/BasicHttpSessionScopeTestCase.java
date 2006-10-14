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
package org.apache.tuscany.core.component.scope;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.core.implementation.PojoConfiguration;
import org.apache.tuscany.spi.component.SystemAtomicComponent;
import org.apache.tuscany.core.implementation.system.component.SystemAtomicComponentImpl;
import org.apache.tuscany.core.injection.EventInvoker;
import org.apache.tuscany.core.injection.MethodEventInvoker;
import org.apache.tuscany.core.injection.PojoObjectFactory;
import org.apache.tuscany.core.mock.component.SessionScopeInitDestroyComponent;

/**
 * @version $$Rev$$ $$Date$$
 */
public class BasicHttpSessionScopeTestCase extends TestCase {

    private EventInvoker<Object> initInvoker;
    private EventInvoker<Object> destroyInvoker;
    private PojoObjectFactory<?> factory;

    public void testLifecycleManagement() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        HttpSessionScopeContainer scopeContext = new HttpSessionScopeContainer(workContext);
        scopeContext.start();
        SystemAtomicComponent atomicContext = createContext(scopeContext);
        // start the request
        Object session = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session);
        SessionScopeInitDestroyComponent o1 =
            (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());
        assertFalse(o1.isDestroyed());
        SessionScopeInitDestroyComponent o2 =
            (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertSame(o1, o2);
        scopeContext.onEvent(new HttpSessionEnd(this, session));
        assertTrue(o1.isDestroyed());
        scopeContext.stop();
    }

    public void testSessionIsolation() throws Exception {
        WorkContext workContext = new WorkContextImpl();
        HttpSessionScopeContainer scopeContext = new HttpSessionScopeContainer(workContext);
        scopeContext.start();

        SystemAtomicComponent atomicContext = createContext(scopeContext);

        Object session1 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session1);
        SessionScopeInitDestroyComponent o1 =
            (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertTrue(o1.isInitialized());

        Object session2 = new Object();
        workContext.setIdentifier(HttpSessionScopeContainer.HTTP_IDENTIFIER, session2);
        SessionScopeInitDestroyComponent o2 =
            (SessionScopeInitDestroyComponent) scopeContext.getInstance(atomicContext);
        assertNotSame(o1, o2);

        scopeContext.onEvent(new HttpSessionEnd(this, session1));
        assertTrue(o1.isDestroyed());
        assertFalse(o2.isDestroyed());
        scopeContext.onEvent(new HttpSessionEnd(this, session2));
        assertTrue(o2.isDestroyed());
        scopeContext.stop();
    }

    protected void setUp() throws Exception {
        super.setUp();
        factory = new PojoObjectFactory<SessionScopeInitDestroyComponent>(
            SessionScopeInitDestroyComponent.class.getConstructor((Class[]) null));
        initInvoker = new MethodEventInvoker<Object>(
            SessionScopeInitDestroyComponent.class.getMethod("init", (Class[]) null));
        destroyInvoker = new MethodEventInvoker<Object>(
            SessionScopeInitDestroyComponent.class.getMethod("destroy", (Class[]) null));
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private SystemAtomicComponent createContext(ScopeContainer scopeContainer) {
        PojoConfiguration configuration = new PojoConfiguration();
        configuration.setScopeContainer(scopeContainer);
        configuration.addServiceInterface(SessionScopeInitDestroyComponent.class);
        configuration.setInstanceFactory(factory);
        configuration.setInitInvoker(initInvoker);
        configuration.setDestroyInvoker(destroyInvoker);
        SystemAtomicComponentImpl context = new SystemAtomicComponentImpl("foo", configuration);
        context.start();
        return context;
    }
}
