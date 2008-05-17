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

package org.apache.tuscany.sca.domain.model;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * A contribution.
 * 
 * @version $Rev: 552343 $ $Date: 2007-09-07 12:41:52 +0100 (Fri, 07 Sep 2007) $
 */
public interface ContributionModel {
    
    /**
     * Retrieve the contribution object
     * 
     * @return contribution 
     */
    Contribution getContribution();
    
    /**
     * Set the contribution object
     * 
     * @param contribution
     */    
    void setContribution(Contribution contribution);        
    
    /**
     * Retrieve the contribution URI
     * 
     * @return contribution URI
     */
    String getContributionURI();
    
    /**
     * Set the contribution URI
     * 
     * @param contributionURI
     */    
    void setContributionURI(String contributionURI);    
    
    /**
     * Retrieve the contribution URL
     * 
     * @return contribution URL
     */    
    String getContributionURL();
   
    /**
     * Set the contribution URL
     * 
     * @param contributionURL
     */    
    void setContributionURL(String contributionURL);
    
    Map<QName, CompositeModel> getComposites();       
    Map<QName, CompositeModel> getDeployableComposites(); 
    Map<QName, CompositeModel> getDeployedComposites(); 
}