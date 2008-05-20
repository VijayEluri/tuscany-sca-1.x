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
package org.apache.tuscany.sca.implementation.das.provider;

import org.apache.tuscany.das.rdb.DAS;
import org.apache.tuscany.sca.data.engine.DataAccessEngine;
import org.apache.tuscany.sca.data.engine.DataAccessEngineManager;
import org.apache.tuscany.sca.implementation.das.DASImplementation;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.provider.ImplementationProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * DAS Implementation provider
 * 
 * @version $Rev$ $Date$
 */
public class DASImplementationProvider implements ImplementationProvider {
    
    private RuntimeComponent component;
    private DASImplementation implementation;
    private final DataAccessEngineManager dataAccessEngineManager;

    /**
     * Constructs a new DAS implementation.
     */
    public DASImplementationProvider(RuntimeComponent component, DASImplementation implementation) {
        this.component = component;
        this.implementation = implementation;
        this.dataAccessEngineManager = new DataAccessEngineManager();
    }

    public Invoker createInvoker(RuntimeComponentService service, Operation operation) {
        DAS das = null;
        try {
            das = dataAccessEngineManager.getDAS(implementation.getConfig(), implementation.getConnectionInfo());
        } catch(Exception e) {
            e.printStackTrace();
            //what now ?
        }
        DASInvoker invoker = new DASInvoker(operation, new DataAccessEngine(das) );
        return invoker;
    }

    public boolean supportsOneWayInvocation() {
        return false;
    }

    public void start() {
        System.out.println("Starting " + component.getName());
    }

    public void stop() {
        System.out.println("Stopping " + component.getName());
    }

}