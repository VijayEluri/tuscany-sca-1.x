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
package org.apache.tuscany.core.injection;

import java.util.List;
import java.util.ArrayList;

import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WireService;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.createMock;

/**
 * @version $Rev$ $Date$
 */
public class CallbackWireObjectFactoryTestCase extends TestCase {

    @SuppressWarnings({"unchecked"})
    public void testCreateInstance() throws Exception {
        WireService service = createMock(WireService.class);
        Foo foo = new Foo() {
        };
        EasyMock.expect(service.createCallbackProxy(EasyMock.eq(Foo.class), EasyMock.isA(List.class))).andReturn(foo);
        EasyMock.replay(service);
        List<Wire> wires = new ArrayList<Wire>();
        CallbackWireObjectFactory factory = new CallbackWireObjectFactory(Foo.class, service, wires);
        assertEquals(foo, factory.getInstance());
        EasyMock.verify(service);
    }

    private interface Foo {

    }
}
