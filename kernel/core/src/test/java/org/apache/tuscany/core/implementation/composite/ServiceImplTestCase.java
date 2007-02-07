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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;

import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.ServiceBinding;

import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * @version $Rev$ $Date$
 */
public class ServiceImplTestCase extends TestCase {

    public void testStart() {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        binding.setService(EasyMock.isA(Service.class));
        binding.start();
        EasyMock.replay(binding);
        Service service = new ServiceImpl(URI.create("foo#bar"), null, null);
        service.addServiceBinding(binding);
        service.start();
        EasyMock.verify(binding);

    }

    public void testStop() {
        ServiceBinding binding = EasyMock.createMock(ServiceBinding.class);
        binding.setService(EasyMock.isA(Service.class));
        binding.stop();
        EasyMock.replay(binding);
        Service service = new ServiceImpl(URI.create("foo#bar"), null, null);
        service.addServiceBinding(binding);
        service.stop();
        EasyMock.verify(binding);

    }

}
