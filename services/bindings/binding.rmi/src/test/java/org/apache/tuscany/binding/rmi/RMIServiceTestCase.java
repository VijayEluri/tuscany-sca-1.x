/**
 *
 * Copyright 2006 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.binding.rmi;

import junit.framework.TestCase;

import org.apache.tuscany.binding.rmi.host.RMIHostImpl;
import org.apache.tuscany.host.rmi.RMIHostRuntimeException;
import org.easymock.EasyMock;

import org.apache.tuscany.spi.wire.WireInvocationHandler;
import org.apache.tuscany.spi.wire.WireService;
import org.apache.tuscany.spi.wire.InboundWire;

public class RMIServiceTestCase extends TestCase {

    @SuppressWarnings("unchecked")
    public void testGetPort() {
        RMIService s = new RMIService(null, null, null, null, null, null, null, null);
        assertEquals(0, s.getPort("0"));
    }

    @SuppressWarnings("unchecked")
    public void testGenerateRemoteInterface() {
        RMIService s = new RMIService("foo27", null, null, null, null, null, null, null);
        s.generateRemoteInterface(Runnable.class);
    }

    @SuppressWarnings("unchecked")
    public void testCreateRmiService() {
        WireService service = EasyMock.createNiceMock(WireService.class);
        EasyMock.replay(service);
        RMIService s = new RMIService("bla023", null, service, new RMIHostImpl(), null, "9996", "bla", Runnable.class) {
        };
        s.start();
        try {
            s.stop();
        } catch (RMIHostRuntimeException e) {
            // expected
        }
    }
}
