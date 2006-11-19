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
package org.apache.tuscany.container.spring.impl;

import org.apache.tuscany.spi.component.Service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.expect;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @version $Rev$ $Date$
 */
public class SpringCompositeComponentTestCase extends TestCase {

    public void testAppContextStart() {
        AbstractApplicationContext appContext = EasyMock.createMock(AbstractApplicationContext.class);
        appContext.refresh();
        appContext.setParent(EasyMock.isA(ApplicationContext.class));
        appContext.start();
        replay(appContext);
        SpringCompositeComponent component = new SpringCompositeComponent("spring", appContext, null, null, null);
        component.start();
        verify(appContext);
    }

    public void testChildStart() {
        AbstractApplicationContext appContext = EasyMock.createNiceMock(AbstractApplicationContext.class);
        replay(appContext);
        Service service = EasyMock.createMock(Service.class);
        EasyMock.expect(service.getName()).andReturn("foo").anyTimes();
        service.start();
        service.getInterface();
        EasyMock.expectLastCall().andReturn(Object.class);
        expect(service.isSystem()).andReturn(false).atLeastOnce();
        replay(service);
        SpringCompositeComponent component = new SpringCompositeComponent("spring", appContext, null, null, null);
        component.register(service);
        component.start();
        verify(service);
    }


}
