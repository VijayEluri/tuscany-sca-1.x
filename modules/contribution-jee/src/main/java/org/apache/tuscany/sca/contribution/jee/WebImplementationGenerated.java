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
package org.apache.tuscany.sca.contribution.jee;

import org.apache.tuscany.sca.assembly.Implementation;



/**
 * The model representing a WEB implementation in an SCA assembly model when the 
 * WEB implementation has been generated by introspecting a non-enhanced EAR
 */
public interface WebImplementationGenerated extends Implementation {
    
    /**
     * Return the WebModuleInfo which generated this Web component implementation 
     * @return
     */
    WebModuleInfo getWebInfo();
    
    /**
     * Set the WebModuleInfo which generated this Web component implementation 
     * @param webInfo
     */
    void setWebInfo(WebModuleInfo webInfo);

}