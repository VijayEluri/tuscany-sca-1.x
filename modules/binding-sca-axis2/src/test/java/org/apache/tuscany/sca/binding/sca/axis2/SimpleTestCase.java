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
package org.apache.tuscany.sca.binding.sca.axis2;

import java.net.URL;

import junit.framework.Assert;

import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldClient;
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldServiceLocal;
import org.apache.tuscany.sca.binding.sca.axis2.helloworld.HelloWorldServiceRemote;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.distributed.domain.DistributedSCADomain;
import org.apache.tuscany.sca.host.embedded.impl.EmbeddedSCADomain;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleTestCase extends BaseTest {
    
    public static EmbeddedSCADomain domainA;
    public static EmbeddedSCADomain domainB;

    @BeforeClass
    public static void init() throws Exception {
        System.out.println("Setting up distributed nodes");

        try {
            // create and start domainA
            domainA = createDomain("nodeA");
            domainB = createDomain("nodeB");
            startDomain(domainA);
            startDomain(domainB);

        } catch (Exception ex) {
            System.err.println("Exception when creating domain " + ex.getMessage());
            ex.printStackTrace(System.err);
            throw ex;
        }     
    }

    @AfterClass
    public static void destroy() throws Exception {
        // stop the nodes and hence the domains they contain        
        stopDomain(domainA);
        stopDomain(domainB);
    }    
    
    @Test
    public void testHelloWorldLocal() throws Exception {  
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientLocal");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        
    }
    
    @Test
    public void testHelloWorldRemote() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientRemote");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        
    }    
    
    @Test
    public void testHelloWorldLocalAndRemote() throws Exception {
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientLocalAndRemote"); 
        HelloWorldClient helloWorldClientB = domainB.getService(HelloWorldClient.class, "BHelloWorldClientLocalAndRemote"); 
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientB.getGreetings("fred"), "Hello fred");
    }   
    
    @Test
    public void testHelloWorldMultipleServices() throws Exception {
        HelloWorldClient helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientMultipleServices"); 
        HelloWorldClient helloWorldClientA2 = domainA.getService(HelloWorldClient.class, "AHelloWorldClientMultipleServices2");
        HelloWorldClient helloWorldClientB = domainB.getService(HelloWorldClient.class, "BHelloWorldClientMultipleServices");        
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientA2.getGreetings("fred"), "Hello fred");
        Assert.assertEquals(helloWorldClientB.getGreetings("fred"), "Hello fred");
    }   
    
    //@Test
    public void testHelloWorldMultipleBindings() throws Exception {  
        HelloWorldClient helloWorldClientA;
        helloWorldClientA = domainA.getService(HelloWorldClient.class, "AHelloWorldClientMultipleBindings");
        Assert.assertEquals(helloWorldClientA.getGreetings("fred"), "Hello fred");
        
    }   
  
}
