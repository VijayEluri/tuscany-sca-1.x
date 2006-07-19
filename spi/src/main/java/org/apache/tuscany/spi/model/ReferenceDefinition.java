/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.spi.model;

/**
 * Represents a component reference
 *
 * @version $Rev$ $Date$
 */
public class ReferenceDefinition extends ModelObject {
    private String name;
    private ServiceContract serviceContract;
    private Multiplicity multiplicity;
    private boolean autowire;
    private boolean required;

    public ReferenceDefinition() {
    }

    public ReferenceDefinition(String name, ServiceContract serviceContract) {
        this.name = name;
        this.serviceContract = serviceContract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public boolean isAutowire() {
        return autowire;
    }

    public void setAutowire(boolean autowire) {
        this.autowire = autowire;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
