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

package org.apache.tuscany.core;

import org.apache.tuscany.assembly.Component;
import org.apache.tuscany.scope.ScopeContainer;
import org.osoa.sca.ComponentContext;

/**
 * The runtime component
 * @version $Rev$ $Date$
 */
public interface RuntimeComponent extends Component, ComponentContext {
    /**
     * Set the implementation-specific configuration for this component
     * @param configuration
     */
    void setImplementationConfiguration(Object configuration);
    /**
     * Get the implementation-specific configuation for this component
     * @return
     */
    Object getImplementationConfiguration();
    
    /**
     * Set the associated scope container
     * @param scopeContainer
     */
    void setScopeContainer(ScopeContainer scopeContainer);
    /**
     * Get the assoicated scope container
     * @return
     */
    ScopeContainer getScopeContainer();
}
