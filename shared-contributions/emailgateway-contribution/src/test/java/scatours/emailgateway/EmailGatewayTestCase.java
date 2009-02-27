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

package scatours.emailgateway;

import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 */
public class EmailGatewayTestCase {
    private static SCANode node;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        SCANodeFactory factory = SCANodeFactory.newInstance();
        node = factory.createSCANodeFromClassLoader("emailgateway.composite", EmailGateway.class.getClassLoader());
        node.start();
    }
    
    @Test
    public void testEmailGateway() {
        SCAClient client = (SCAClient) node;
        EmailGateway cc = client.getService(EmailGateway.class, "EmailGatewayClient");
        ObjectFactory objectFactory = new ObjectFactory();
        EmailType email = objectFactory.createEmailType();
        email.setTo("Fred");
        email.setTitle("An email");
        email.setBody("A message");
        System.out.println(cc.sendEmail(email));
    }
    
    @Test
    //@Ignore
    public void testWaitForInput() {
        System.out.println("Press a key to end");
 /*       try {
            System.in.read();
        } catch (Exception ex) {
        }
        System.out.println("Shutting down");
*/
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (node != null) {
            node.stop();
            node = null;
        }
    }

}
