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
package calculator.warning;

import java.io.File;

import junit.framework.TestCase;

import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.impl.DefaultMonitorImpl;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;
import org.apache.tuscany.sca.node.impl.NodeImpl;
import org.osoa.sca.ServiceRuntimeException;

/**
 * This shows how to test the Calculator service component.
 */
public class DuplicateReferenceNameTestCase extends TestCase {

    private CalculatorService calculatorService;
    private SCANode node;
    private boolean duplicateReferenceException = false;

    @Override
    protected void setUp() throws Exception {
        try {
            SCANodeFactory nodeFactory = SCANodeFactory.newInstance();
            node = nodeFactory.createSCANode(new File("src/main/resources/DuplicateReferenceName/Calculator.composite").toURL().toString(),
            		                 new SCAContribution("TestContribution", 
            		                                     new File("src/main/resources/DuplicateReferenceName").toURL().toString()));
            node.start();
            calculatorService = ((SCAClient)node).getService(CalculatorService.class, "CalculatorServiceComponent");
        } catch (ServiceRuntimeException ex) {
            if (ex.getMessage().equals("org.apache.tuscany.sca.monitor.MonitorRuntimeException: Duplicate component reference name: Component = CalculatorServiceComponent Reference = addService")){
                duplicateReferenceException = true;
            }
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if (node != null){
            node.stop();
        }
    }

    public void testCalculator() throws Exception {
        assertTrue(duplicateReferenceException); 
    }
}
