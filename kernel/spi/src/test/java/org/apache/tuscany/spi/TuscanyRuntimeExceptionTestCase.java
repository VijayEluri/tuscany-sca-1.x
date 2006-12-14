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
package org.apache.tuscany.spi;

import junit.framework.TestCase;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * @version $Rev$ $Date$
 */
public class TuscanyRuntimeExceptionTestCase extends TestCase {

    public void testIdentifier() throws Exception {
        TuscanyRuntimeException e = new TestException("bar", "foo");
        assertEquals("foo", e.getIdentifier());
    }

    public void testAddContext() throws Exception {
        TuscanyRuntimeException e = new TestException();
        e.addContextName("foo");
        e.addContextName("bar");
        assertEquals("foo", e.returnContextNames().get(0));
        assertEquals("bar", e.returnContextNames().get(1));
    }

    public void testEmptyContext() throws Exception {
        TuscanyRuntimeException e = new TestException();
        assertEquals(0, e.returnContextNames().size());
    }

    public void testEmptyGetMessage() throws Exception {
        TuscanyRuntimeException e = new TestException();
        e.getMessage();
    }

    public void testFullMessage() throws Exception {
        TuscanyRuntimeException e = new TestException();
        e.addContextName("foo");
        e.getMessage();
    }


    private class TestException extends TuscanyRuntimeException {

        public TestException() {
        }

        public TestException(String message, String identifier) {
            super(message, identifier);
        }
    }
}
