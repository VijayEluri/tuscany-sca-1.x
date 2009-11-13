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

package org.apache.tuscany.sca.vtest.javaapi.apis.servicereference;

import org.apache.tuscany.sca.vtest.utilities.ServiceFinder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class tests the ServiceReference interface described in 1.7.4 of
 * the SCA Java Annotations & APIs Specification 1.0.
 */
public class ServiceReferenceTestCase {

    protected static String compositeName = "servicereference.composite";
    protected static AComponent a;
    protected static BComponent b;

    @BeforeClass
    public static void init() throws Exception {
        try {
            System.out.println("Setting up");
            ServiceFinder.init(compositeName);
            a = ServiceFinder.getService(AComponent.class, "AComponent");
            b = ServiceFinder.getService(BComponent.class, "BComponent");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void destroy() throws Exception {
        System.out.println("Cleaning up");
        ServiceFinder.cleanup();
    }

    /**
     * Lines 915-916 <br>
     * getConversationID() - Returns the id supplied by the user that will be
     * associated with conversations initiated through this reference. <br>
     * setConversationID(Object conversationId) � Set the id to associate with
     * any conversation started through this reference. If the value supplied is
     * null then the id will be generated by the implementation. Throws an
     * IllegalStateException if a conversation is currently associated with this
     * reference.
     * 
     * @throws Exception
     */
    @Test
    public void testConversationID() throws Exception {
        a.testConversationID();
    }

    /**
     * Lines 917 <br>
     * setCallbackID(Object callbackID) � Sets the callback ID.
     * 
     * @throws Exception
     */
    @Test
    public void testSetCallbackID() throws Exception {
        Assert.assertEquals("CallBackFromB", a.getCallbackResult());
    }

    /**
     * Lines 918 <br>
     * getCallback() � Returns the callback object.
     * 
     * @throws Exception
     */
    @Test
    public void testGetCallback() throws Exception {
        a.testGetCallback();
    }

    /**
     * Lines 919 <br>
     * setCallback(Object callback) � Sets the callback object.
     * 
     * @throws Exception
     */
    @Test
    public void testSetCallback() throws Exception {
        a.redirectCallback();
    }

}
