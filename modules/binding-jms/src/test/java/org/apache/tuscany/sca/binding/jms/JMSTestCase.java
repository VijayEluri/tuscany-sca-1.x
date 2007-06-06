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
package org.apache.tuscany.sca.binding.jms;

import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;

import org.apache.activemq.broker.BrokerService;

/**
 * This shows how to test the JMS binding using a simple HelloWorld application.
 */
public class JMSTestCase extends TestCase {

    private HelloWorldService helloWorldService;
    private SCADomain         scaDomain;
    private BrokerService     broker = null;

    protected void setUp() throws Exception {
        // start the activemq broker
        if (broker == null){
            BrokerService broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            broker.start();
        }
        
        // that the sca runtime
        scaDomain = SCADomain.newInstance("JMSBindingTest.composite");
        helloWorldService = scaDomain.getService(HelloWorldService.class, "HelloWorldClientComponent");
    }

    protected void tearDown() throws Exception {
        scaDomain.close();
    }

    public void testHelloWorld() throws Exception {
        assertEquals("Hello Fred", helloWorldService.sayHello("Fred"));
    }
}
