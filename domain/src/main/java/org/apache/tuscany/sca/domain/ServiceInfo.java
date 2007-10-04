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

package org.apache.tuscany.sca.domain;

import org.osoa.sca.annotations.Remotable;


/**
 * A collection of info for a registered service
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
@Remotable
public interface ServiceInfo {
    
    /**
     * Compare this service info against the data provided and return true if it matches
     * return false otherwise
     * 
     * @param domainUri
     * @param serviceName
     * @param bindingName
     * @return
     */
    public boolean match(String domainUri, String serviceName, String bindingName);
    
    /**
     * Get the URL of this service. 
     * 
     * @return
     */
    public String getUrl();   
      
    /** 
     * Returns a string representation of the information for a service
     * 
     * @return
     */
    public String toString();
 
}
