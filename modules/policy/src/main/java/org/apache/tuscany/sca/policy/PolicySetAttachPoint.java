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
package org.apache.tuscany.sca.policy;

import java.util.List;

/**
 * Base interface for all assembly model objects that can have policy sets
 * attached to them.
 *
 * @version $Rev$ $Date$
 */
public interface PolicySetAttachPoint extends IntentAttachPoint {

    /**
     * Returns a list of policy sets. See the Policy Framework specification for
     * a description of this attribute.
     * 
     * @return a list of policy sets.
     */
    List<PolicySet> getPolicySets();
    
    
    /**
     * Returns a list of policy sets defined in the domain, that are applicable to this
     * PolicySetAttachPoint. An applicable PolicySet is one that include this PolicySetAttachPoint
     * as part of its 'appliesTo' XPath attribute.
     * 
     * @return a list of policy sets applicable to this PolicySetAttachPoint
     */
    List<PolicySet> getApplicablePolicySets();
}
