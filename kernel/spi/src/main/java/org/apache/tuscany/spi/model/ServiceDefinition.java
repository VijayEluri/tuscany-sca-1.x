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
package org.apache.tuscany.spi.model;

/**
 * Represents a service offered by a component
 *
 * @version $Rev$ $Date$
 */
public class ServiceDefinition extends ModelObject {
    private String name;
    private ServiceContract serviceContract;
    private boolean remotable;
    private String callbackRefName;

    public ServiceDefinition() {
    }

    public ServiceDefinition(String name, ServiceContract serviceContract, boolean remotable) {
        this.name = name;
        this.serviceContract = serviceContract;
        this.remotable = remotable;
    }

    public ServiceDefinition(String name, ServiceContract serviceContract, boolean remotable, String callbackRefName) {
        this.name = name;
        this.serviceContract = serviceContract;
        this.remotable = remotable;
        this.callbackRefName = callbackRefName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceContract<?> getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public boolean isRemotable() {
        return remotable;
    }

    public void setRemotable(boolean remotable) {
        this.remotable = remotable;
    }

    /**
     * Returns the callback name.
     */
    public String getCallbackReferenceName() {
        return callbackRefName;
    }

    /**
     * Sets the callback name
     * @param name
     */
    public void setCallbackReferenceName(String name) {
        this.callbackRefName = name;
    }

}
