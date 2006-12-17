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

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.services.store.Store;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ConversationalScopeContainerMaxIdleTimeTestCase extends TestCase {

    private ScopeContainer container;
    private WorkContext context;
    private Store store;
    private AtomicComponent component;

    public void testMaxIdleTimeUpdate() throws Exception {
        context.setIdentifier(Scope.CONVERSATION, "12345");
        container.getInstance(component);
        EasyMock.verify(store);
    }

    protected void setUp() throws Exception {
        super.setUp();
        Foo foo = new Foo();
        context = new WorkContextImpl();
        component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getName()).andReturn("foo").atLeastOnce();
        EasyMock.expect(component.getMaxIdleTime()).andReturn(600000L).atLeastOnce();
        EasyMock.replay(component);
        store = EasyMock.createMock(Store.class);
        EasyMock.expect(store.readRecord(EasyMock.isA(SCAObject.class), EasyMock.isA(String.class))).andReturn(foo);
        store.updateRecord(EasyMock.isA(SCAObject.class),
            EasyMock.isA(String.class),
            EasyMock.eq(foo),
            EasyMock.anyLong());
        EasyMock.replay(store);
        container = new ConversationalScopeContainer(store, context);
        container.start();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        context.clearIdentifier(Scope.CONVERSATION);
        container.stop();
    }

    private class Foo {

    }

}
