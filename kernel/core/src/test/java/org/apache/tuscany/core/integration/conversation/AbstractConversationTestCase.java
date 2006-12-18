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
package org.apache.tuscany.core.integration.conversation;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.services.store.StoreMonitor;
import org.apache.tuscany.spi.wire.WirePostProcessorRegistry;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.builder.WirePostProcessorRegistryImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.scope.ConversationalScopeContainer;
import org.apache.tuscany.core.implementation.java.JavaAtomicComponent;
import org.apache.tuscany.core.services.store.memory.MemoryStore;
import org.easymock.classextension.EasyMock;

/**
 * Provides helper methods for setting up a partial runtime for conversational test cases.
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractConversationTestCase extends TestCase {
    protected ScopeContainer container;
    protected MemoryStore store;
    protected WorkContext workContext;
    protected ConnectorImpl connector;
    protected JavaAtomicComponent target;


    protected void createRuntime() {
        workContext = new WorkContextImpl();
        WirePostProcessorRegistry processorRegistry = new WirePostProcessorRegistryImpl();
        connector = new ConnectorImpl(null, processorRegistry, null, workContext);
        store = new MemoryStore(EasyMock.createNiceMock(StoreMonitor.class));
        container = new ConversationalScopeContainer(store, workContext, null);

    }

    protected void initializeRuntime() {
        store.init();
        container.start();
    }

}
