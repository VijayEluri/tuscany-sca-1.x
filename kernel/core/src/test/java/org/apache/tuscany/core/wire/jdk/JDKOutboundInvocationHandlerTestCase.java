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
package org.apache.tuscany.core.wire.jdk;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.OutboundWireImpl;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * @version $Rev$ $Date$
 */
public class JDKOutboundInvocationHandlerTestCase extends TestCase {

    public void testToString() {
        OutboundWireImpl wire = new OutboundWireImpl();
        ServiceContract contract = new JavaServiceContract(Foo.class);
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        wire.setServiceContract(contract);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.toString());
    }

    public void testHashCode() {
        OutboundWireImpl wire = new OutboundWireImpl();
        ServiceContract contract = new JavaServiceContract(Foo.class);
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        wire.setServiceContract(contract);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(Foo.class, wire, null);
        Foo foo = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Foo.class}, handler);
        assertNotNull(foo.hashCode());
    }

    public void testConversational() throws Throwable {
        OutboundWire outboundWire = createMock(OutboundWire.class);
        Map<Operation<?>, OutboundInvocationChain> outboundChains =
            new HashMap<Operation<?>, OutboundInvocationChain>();
        DataType<Type> type1 = new DataType<Type>(String.class, String.class);
        List<DataType<Type>> types = new ArrayList<DataType<Type>>();
        types.add(type1);
        DataType<List<DataType<Type>>> inputType1 = new DataType<List<DataType<Type>>>(Object[].class, types);
        DataType<Type> outputType1 = new DataType<Type>(String.class, String.class);
        Operation<Type> op1 = new Operation<Type>("test", inputType1, outputType1, null);
        ServiceContract<Type> outboundContract = new JavaServiceContract(Foo.class);
        outboundContract.setInteractionScope(InteractionScope.CONVERSATIONAL);
        op1.setServiceContract(outboundContract);

        WorkContext wc = new WorkContextImpl();
        MockInvoker invoker = new MockInvoker(wc);
        OutboundInvocationChain outboundChain = createMock(OutboundInvocationChain.class);
        expect(outboundChain.getTargetInvoker()).andReturn(invoker).anyTimes();
        expect(outboundChain.getHeadInterceptor()).andReturn(null).anyTimes();
        replay(outboundChain);
        outboundChains.put(op1, outboundChain);
        expect(outboundWire.getInvocationChains()).andReturn(outboundChains).anyTimes();
        expect(outboundWire.getReferenceName()).andReturn("fooRef").atLeastOnce();
        expect(outboundWire.getContainer()).andReturn(null).anyTimes();
        expect(outboundWire.getServiceContract()).andReturn(outboundContract).anyTimes();
        replay(outboundWire);

        String convID = UUID.randomUUID().toString();
        wc.setIdentifier(Scope.CONVERSATION, convID);
        invoker.setCurrentConversationID(convID);

        outboundContract.setRemotable(true);
        invoker.setRemotableTest(true);
        JDKOutboundInvocationHandler handler = new JDKOutboundInvocationHandler(Foo.class, outboundWire, wc);
        handler.invoke(Foo.class.getMethod("test", String.class), new Object[]{"bar"});
        String currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
        assertSame(convID, currentConvID);

        outboundContract.setRemotable(false);
        invoker.setRemotableTest(false);
        JDKOutboundInvocationHandler handler2 = new JDKOutboundInvocationHandler(Foo.class, outboundWire, wc);
        handler2.invoke(Foo.class.getMethod("test", String.class), new Object[]{"bar"});
        currentConvID = (String) wc.getIdentifier(Scope.CONVERSATION);
        assertSame(convID, currentConvID);
    }

    private interface Foo {
        String test(String s);
    }

    private class MockInvoker implements TargetInvoker {

        private WorkContext wc;
        private String currentConversationID;
        private boolean remotableTest;

        public MockInvoker(WorkContext wc) {
            this.wc = wc;
        }

        public void setCurrentConversationID(String id) {
            currentConversationID = id;
        }

        public void setRemotableTest(boolean remotableTest) {
            this.remotableTest = remotableTest;
        }

        public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
            assertEquals("bar", Array.get(payload, 0));
            String convID = (String) wc.getIdentifier(Scope.CONVERSATION);
            if (remotableTest) {
                assertNotSame(convID, currentConversationID);
            } else {
                assertSame(convID, currentConversationID);
            }
            return "response";
        }

        public Message invoke(Message msg) throws InvocationRuntimeException {
            fail();
            return null;
        }

        public boolean isCacheable() {
            return false;
        }

        public void setCacheable(boolean cacheable) {

        }

        public boolean isOptimizable() {
            return false;
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
