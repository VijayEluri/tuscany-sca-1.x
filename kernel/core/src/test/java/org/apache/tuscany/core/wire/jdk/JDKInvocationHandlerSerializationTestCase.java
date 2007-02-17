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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.InteractionScope;
import org.apache.tuscany.spi.model.Operation;
import static org.apache.tuscany.spi.model.Operation.NO_CONVERSATION;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.InvokerInterceptor;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandlerSerializationTestCase extends TestCase {
    private Wire wire;
    private WorkContext workContext;
    private TargetInvoker invoker;

    public void testSerializeDeserialize() throws Throwable {
        JDKInvocationHandler handler =
            new JDKInvocationHandler(Foo.class, wire, workContext);
        handler.invoke(Foo.class.getMethod("invoke"), null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream ostream = new ObjectOutputStream(stream);
        ostream.writeObject(handler);

        ObjectInputStream istream = new ObjectInputStream(new ByteArrayInputStream(stream.toByteArray()));
        JDKInvocationHandler externalizable = (JDKInvocationHandler) istream.readObject();

        externalizable.setWorkContext(workContext);
        externalizable.reactivate();
        externalizable.invoke(Foo.class.getMethod("invoke"), null);
        EasyMock.verify(invoker);
        EasyMock.verify(wire);
    }

    protected void setUp() throws Exception {
        super.setUp();
        SCAObject container = EasyMock.createMock(SCAObject.class);
        ServiceContract<Foo> contract = new ServiceContract<Foo>() {
        };
        contract.setInterfaceClass(Foo.class);
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        EasyMock.expect(container.getUri()).andReturn(URI.create("bar")).atLeastOnce();

        wire = EasyMock.createMock(Wire.class);
        Map<Operation<?>, InvocationChain> map = new HashMap<Operation<?>, InvocationChain>();
        Operation<Object> operation = new Operation<Object>("invoke", null, null, null, false, null, NO_CONVERSATION);
        ServiceContract<Object> opContract = new ServiceContract<Object>() {
        };
        contract.setInterfaceClass(Foo.class);
        contract.setInteractionScope(InteractionScope.NONCONVERSATIONAL);
        operation.setServiceContract(opContract);
        map.put(operation, createChain(operation));
        EasyMock.expect(wire.getSourceContract()).andReturn(contract).atLeastOnce();
        URI uri = URI.create("#foo");
        EasyMock.expect(wire.getSourceUri()).andReturn(uri).atLeastOnce();
        EasyMock.expect(wire.getInvocationChains()).andReturn(map).times(2);
        EasyMock.replay(wire);
        List<Wire> list = new ArrayList<Wire>();
        list.add(wire);
        AtomicComponent component = EasyMock.createMock(AtomicComponent.class);
        EasyMock.expect(component.getWires("foo")).andReturn(list);
        EasyMock.replay(component);
        workContext = new WorkContextImpl();
        workContext.setCurrentAtomicComponent(component);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        workContext.setCurrentAtomicComponent(null);
    }

    private InvocationChain createChain(Operation operation) {
        invoker = EasyMock.createMock(TargetInvoker.class);
        EasyMock.expect(invoker.invoke(EasyMock.isA(Message.class))).andReturn(new MessageImpl()).times(2);
        EasyMock.expect(invoker.isCacheable()).andReturn(false).atLeastOnce();
        EasyMock.replay(invoker);
        InvocationChain chain = new InvocationChainImpl(operation);
        chain.setTargetInvoker(invoker);
        chain.addInterceptor(new InvokerInterceptor());
        return chain;
    }

    public class Foo {

        public void invoke() {
        }
    }

}
