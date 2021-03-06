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

package org.apache.tuscany.sca.binding.http.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.sca.assembly.OperationSelector;
import org.apache.tuscany.sca.assembly.WireFormat;
import org.apache.tuscany.sca.binding.http.HTTPBinding;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;
import org.apache.tuscany.sca.policy.PolicySetAttachPoint;


/**
 * Implementation of the HTTP binding model.
 * 
 * @version $Rev$ $Date$
 */
class HTTPBindingImpl implements HTTPBinding, PolicySetAttachPoint {
    
    private String name;
    private String uri;
    
    private WireFormat wireFormat;
    private OperationSelector operationSelector;    
    
    private List<Intent> requiredIntents = new ArrayList<Intent>();
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private IntentAttachPointType intentAttachPointType;
    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();


    public String getName() {
        return name;
    }

    public String getURI() {
        return uri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    
    public boolean isUnresolved() {
        return false;
    }

    public void setUnresolved(boolean unresolved) {
        // The sample binding is always resolved
    }
    
// Wireformat and Operation selection
    
    public WireFormat getRequestWireFormat() {
        return wireFormat;
    }
    
    public void setRequestWireFormat(WireFormat wireFormat) {
        this.wireFormat = wireFormat;
    }
    
    public WireFormat getResponseWireFormat() {
        return wireFormat;
    }
    
    public void setResponseWireFormat(WireFormat wireFormat) {
        this.wireFormat = wireFormat;
    }    
    
    public OperationSelector getOperationSelector() {
        return operationSelector;
    }
    
    public void setOperationSelector(OperationSelector operationSelector) {
        this.operationSelector = operationSelector;
    }    
    
    //Policy related getters/setters
    
    public List<PolicySet> getPolicySets() {
        return policySets;
    }
    
    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return intentAttachPointType;
    }
    
    public void setType(IntentAttachPointType intentAttachPointType) {
        this.intentAttachPointType = intentAttachPointType;
    }

    public void setPolicySets(List<PolicySet> policySets) {
        this.policySets = policySets; 
    }

    public void setRequiredIntents(List<Intent> intents) {
        this.requiredIntents = intents;
    }    

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }     

}
