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

import java.lang.reflect.Method;
import java.net.URI;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Contract;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.core.wire.InvocationChainImpl;
import org.apache.tuscany.core.wire.WireImpl;
import org.apache.tuscany.interfacedef.Operation;
import org.apache.tuscany.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.interfacedef.java.impl.JavaInterfaceContractImpl;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.apache.tuscany.interfacedef.java.introspect.JavaInterfaceIntrospectorExtensionPoint;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.MessageImpl;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class JDKInvocationHandlerProxyTestCase extends TestCase {
    private JavaInterfaceIntrospectorExtensionPoint introspector = new DefaultJavaInterfaceIntrospector();
    private Method clientHello;

    /**
     * Verifies a handler configured to use a different interface than the wire
     * target can dispatch
     */
    public void testDifferentInterface() throws Throwable {
        Wire wire = new WireImpl();

        Contract contract = new DefaultAssemblyFactory().createComponentReference();
        JavaInterfaceContract interfaceContract = new JavaInterfaceContractImpl();
        contract.setInterfaceContract(interfaceContract);
        interfaceContract.setInterface(introspector.introspect(Target.class));
        for (Operation operation : contract.getInterfaceContract().getInterface().getOperations()) {
            InvocationChain chain = new InvocationChainImpl(operation);
            wire.addInvocationChain(operation, chain);
        }
        wire.setSourceContract(contract.getInterfaceContract());
        wire.setSourceUri(URI.create("foo#bar"));
        TargetInvoker targetInvoker = EasyMock.createMock(TargetInvoker.class);
        MessageImpl response = new MessageImpl();
        EasyMock.expect(targetInvoker.invokeTarget(null, TargetInvoker.NONE, null)).andReturn(response);
        EasyMock.expect(targetInvoker.isCacheable()).andReturn(false);
        EasyMock.replay(targetInvoker);
        wire.getInvocationChains().values().iterator().next().setTargetInvoker(targetInvoker);

        JDKInvocationHandler handler = new JDKInvocationHandler(Client.class, wire, null);
        assertSame(response, handler.invoke(null, clientHello, null));
        EasyMock.verify(targetInvoker);
    }

    protected void setUp() throws Exception {
        super.setUp();
        clientHello = Client.class.getMethod("hello");
    }

    private interface Target {
        String hello();
    }

    private interface Client {
        String hello();
    }

}
