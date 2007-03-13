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
package org.apache.tuscany.core.builder.physical;

import org.apache.tuscany.spi.builder.physical.WireAttacher;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class WireAttacherRegistryTestCase extends TestCase {
    WireAttacherRegistryImpl registry;

    @SuppressWarnings("unchecked")
    public void testSourceAttachDispatch() throws Exception {
        Component component = EasyMock.createMock(Component.class);
        EasyMock.replay(component);
        WireAttacher attacher = EasyMock.createMock(WireAttacher.class);
        attacher.attach(EasyMock.isA(Component.class), EasyMock.isA(PhysicalWireSourceDefinition.class));
        EasyMock.replay(attacher);
        registry.register(component.getClass(), attacher);
        registry.attach(component, new PhysicalWireSourceDefinition());
        EasyMock.verify(attacher);
    }

    @SuppressWarnings("unchecked")
    public void testTargetAttachDispatch() throws Exception {
        Component component = EasyMock.createMock(Component.class);
        EasyMock.replay(component);
        WireAttacher attacher = EasyMock.createMock(WireAttacher.class);
        attacher.attach(EasyMock.isA(Component.class), EasyMock.isA(PhysicalWireTargetDefinition.class));
        EasyMock.replay(attacher);

        registry.register(component.getClass(), attacher);
        registry.attach(component, new PhysicalWireTargetDefinition());
        EasyMock.verify(attacher);
    }

    protected void setUp() throws Exception {
        super.setUp();
        registry = new WireAttacherRegistryImpl();
    }

}
