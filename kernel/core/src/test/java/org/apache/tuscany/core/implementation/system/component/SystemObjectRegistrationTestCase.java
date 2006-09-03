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
package org.apache.tuscany.core.implementation.system.component;

import javax.naming.ConfigurationException;

import org.apache.tuscany.spi.component.DuplicateNameException;

import junit.framework.TestCase;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;

/**
 * @version $Rev$ $Date$
 */
public class SystemObjectRegistrationTestCase extends TestCase {
    private SystemCompositeComponent<?> systemContext;

    public void testRegistration() throws Exception {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        MockComponent resolvedInstance = (MockComponent) systemContext.getChild("foo").getServiceInstance();
        assertSame(instance, resolvedInstance);
    }

    public void testDuplicateRegistration() throws ConfigurationException {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        try {
            systemContext.registerJavaObject("foo", MockComponent.class, instance);
            fail();
        } catch (DuplicateNameException e) {
            // ok
        }
    }

    public void testAutowireToObject() {
        MockComponent instance = new MockComponent();
        systemContext.registerJavaObject("foo", MockComponent.class, instance);
        assertSame(instance, systemContext.resolveInstance(MockComponent.class));
        assertNull(systemContext.resolveExternalInstance(MockComponent.class));
    }

    protected void setUp() throws Exception {
        super.setUp();
        systemContext = new SystemCompositeComponentImpl(null, null, null, null, null);
        systemContext.start();
        systemContext.publish(new CompositeStart(this, null));
    }

    protected void tearDown() throws Exception {
        systemContext.publish(new CompositeStop(this, null));
        systemContext.stop();
        super.tearDown();
    }

    private static class MockComponent {
        public String hello(String message) {
            return message;
        }
    }
}
