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

package helloworld;

import greetings.GreetingsTestServer;
import junit.framework.TestCase;

import org.apache.tuscany.sca.host.embedded.SCADomain;
import org.apache.tuscany.sca.host.embedded.SCATestCaseRunner;

/**
 * Tests the BPEL Helloworld Service
 * 
 * @version $Rev$ $Date$
 */
public class HelloWorldTestCase extends TestCase {
    private SCADomain scaDomain;
    
    //private SCATestCaseRunner server;
    
    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void setUp() throws Exception {
        scaDomain = SCADomain.newInstance("helloworld/helloworld.composite");
        
        //server =  new SCATestCaseRunner(GreetingsTestServer.class);
        //server.before();
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    protected void tearDown() throws Exception {
        //server.after();
        scaDomain.close();
    }
    
    public void testServiceInvocation() {
        HelloWorldService bpelService = scaDomain.getService(HelloWorldService.class, "HelloWorldService");
        String response = bpelService.hello("Luciano");
        assertEquals("Hello Luciano", response);
    }
}