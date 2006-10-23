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
package calculator;

import org.apache.tuscany.test.SCATestCase;

import calculator.client.CalculatorClient;

/**
 * This shows how to test the Calculator service component.
 */
public class CalculatorTestClient extends SCATestCase {

    protected void setUp() throws Exception {

        addExtension("ruby.extension", getClass().getClassLoader()
            .getResource("META-INF/sca/ruby.system.scdl"));
        addExtension("rmi.extension", getClass().getClassLoader()
            .getResource("META-INF/sca/rmi_extension.scdl"));
        addExtension("js.extension", getClass().getClassLoader()
            .getResource("META-INF/sca/js.system.scdl"));
        addExtension("axis2.extension", getClass().getClassLoader()
            .getResource("META-INF/sca/binding.axis2.scdl"));
        addExtension("databinding.sdo.extension", getClass().getClassLoader()
            .getResource("META-INF/sca/databinding.sdo.scdl"));

        setApplicationSCDL(getClass().getClassLoader().getResource("META-INF/sca/default.scdl"));
        super.setUp();

    }

    public void testCalculatorClient() throws Exception {
        CalculatorClient.main(new String[0]);
    }

    protected void tearDown() throws Exception {
        // super.tearDown();
    }

}
