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
package org.apache.tuscany.core.wire;

import java.lang.reflect.Method;

import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.idl.java.JavaInterfaceProcessorRegistry;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.Operation;

import junit.framework.TestCase;
import org.apache.tuscany.core.mock.component.SimpleTarget;
import org.apache.tuscany.core.mock.component.SimpleTargetImpl;
import org.apache.tuscany.core.mock.wire.MockHandler;
import org.apache.tuscany.core.mock.wire.MockStaticInvoker;
import org.apache.tuscany.core.mock.wire.MockSyncInterceptor;
import org.apache.tuscany.core.idl.java.JavaInterfaceProcessorRegistryImpl;

/**
 * Tests error propagation through an innvocation
 *
 * @version $Rev$ $Date$
 */
public class InvocationConfigurationErrorTestCase extends TestCase {

    private ServiceContract<?> contract;
    private Method hello;

    public InvocationConfigurationErrorTestCase() {
        super();
    }

    public InvocationConfigurationErrorTestCase(String arg0) {
        super(arg0);
    }

    public void setUp() throws Exception {
        super.setUp();
        JavaInterfaceProcessorRegistry registry = new JavaInterfaceProcessorRegistryImpl();
        contract = registry.introspect(SimpleTarget.class);
        hello = SimpleTarget.class.getMethod("hello", String.class);
    }

    public void testInvokeWithHandlers() throws Exception {
        Operation operation = contract.getOperations().get("hello");
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockHandler sourceRequestHandler = new MockHandler();
        MockHandler sourceResponseHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addResponseHandler(sourceResponseHandler);
        source.addInterceptor(sourceInterceptor);

        InboundInvocationChain target = new InboundInvocationChainImpl(operation);
        MockHandler targetRequestHandler = new MockHandler();
        MockHandler targetResponseHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addResponseHandler(targetResponseHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.prepare();
        target.prepare();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        assertTrue(response.getBody() instanceof IllegalArgumentException);
        assertEquals(1, sourceRequestHandler.getCount());
        assertEquals(1, sourceResponseHandler.getCount());
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetRequestHandler.getCount());
        assertEquals(1, targetResponseHandler.getCount());
        assertEquals(1, targetInterceptor.getCount());
    }

    public void testInvokeWithRequestHandlers() throws Exception {
        Operation operation = contract.getOperations().get("hello");
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockHandler sourceRequestHandler = new MockHandler();
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addRequestHandler(sourceRequestHandler);
        source.addInterceptor(sourceInterceptor);

        InboundInvocationChain target = new InboundInvocationChainImpl(operation);
        MockHandler targetRequestHandler = new MockHandler();
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addRequestHandler(targetRequestHandler);
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetRequestChannel(new MessageChannelImpl(target.getRequestHandlers()));
        source.setTargetResponseChannel(new MessageChannelImpl(target.getResponseHandlers()));
        source.prepare();
        target.prepare();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        assertTrue(response.getBody() instanceof IllegalArgumentException);
        assertEquals(1, sourceRequestHandler.getCount());
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetRequestHandler.getCount());
        assertEquals(1, targetInterceptor.getCount());
    }

    /**
     * Tests basic wiring of a source to a target, including handlers and interceptors
     */
    public void testInvokeWithInterceptorsOnly() throws Exception {
        Operation operation = contract.getOperations().get("hello");
        OutboundInvocationChain source = new OutboundInvocationChainImpl(operation);
        MockSyncInterceptor sourceInterceptor = new MockSyncInterceptor();
        source.addInterceptor(sourceInterceptor);

        InboundInvocationChain target = new InboundInvocationChainImpl(operation);
        MockSyncInterceptor targetInterceptor = new MockSyncInterceptor();
        target.addInterceptor(targetInterceptor);
        target.addInterceptor(new InvokerInterceptor());

        // connect the source to the target
        source.setTargetInterceptor(target.getHeadInterceptor());
        source.prepare();
        target.prepare();
        MockStaticInvoker invoker = new MockStaticInvoker(hello, new SimpleTargetImpl());
        source.setTargetInvoker(invoker);

        Message msg = new MessageImpl();
        msg.setTargetInvoker(invoker);
        Message response = source.getHeadInterceptor().invoke(msg);
        assertTrue(response.getBody() instanceof IllegalArgumentException);
        assertEquals(1, sourceInterceptor.getCount());
        assertEquals(1, targetInterceptor.getCount());

    }

}

