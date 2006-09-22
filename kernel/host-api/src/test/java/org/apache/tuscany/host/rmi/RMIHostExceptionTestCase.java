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
package org.apache.tuscany.host.rmi;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class RMIHostExceptionTestCase extends TestCase {
    private static final Throwable CAUSE = new Throwable("Cause");
    private static final String MESSAGE = "Message";

    public void testNoArgConstructor() {
        Exception ex = new RMIHostException();
        assertNull(ex.getMessage());
        assertNull(ex.getCause());
    }

    public void testMessageConstructor() {
        Exception ex = new RMIHostException(MESSAGE);
        assertSame(MESSAGE, ex.getMessage());
        assertNull(ex.getCause());
    }

    public void testThrowableConstructor() {
        Exception ex = new RMIHostException(CAUSE);
        assertEquals(CAUSE.getClass().getName() + ": " + CAUSE.getMessage(), ex.getMessage());
        assertSame(CAUSE, ex.getCause());
    }

    public void testMessageThrowableConstructor() {
        Exception ex = new RMIHostException(MESSAGE, CAUSE);
        assertSame(MESSAGE, ex.getMessage());
        assertSame(CAUSE, ex.getCause());
    }

}
