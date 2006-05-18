/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.invocation;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.container.java.invocation.mock.SimpleTargetImpl;
import org.apache.tuscany.core.message.Message;
import org.apache.tuscany.core.message.MessageFactory;
import org.apache.tuscany.core.message.impl.MessageFactoryImpl;

/**
 * Tests invoking on a different interface from the one actually implemented by the target
 * 
 * @version $Rev$ $Date$
 */
public class MediationTestCase extends TestCase {

    private Method hello;

    private MessageFactory msgFactory = new MessageFactoryImpl();

    public void setUp() throws Exception {
        hello = Hello.class.getMethod("hello", String.class);
    }

    public void testMediation() throws Exception {
        StaticJavaComponentTargetInvoker invoker = new StaticJavaComponentTargetInvoker(hello, new SimpleTargetImpl());
        Message msg = msgFactory.createMessage();
        msg.setBody("foo");
        Assert.assertEquals("foo", invoker.invoke(msg).getBody());
    }

    public interface Hello {

        public String hello(String message) throws Exception;

    }
}
