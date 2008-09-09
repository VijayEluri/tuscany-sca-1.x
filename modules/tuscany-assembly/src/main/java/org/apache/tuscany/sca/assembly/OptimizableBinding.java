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
package org.apache.tuscany.sca.assembly;

/**
 * Represent a reference binding that supports optimized SCA local wiring between component 
 * references and services 
 * 
 * @version $Rev: 575759 $ $Date: 2007-09-14 10:09:31 -0700 (Fri, 14 Sep 2007) $
 * 
 */
public interface OptimizableBinding extends Binding, Cloneable {

    /**
     * @param component
     */
    void setTargetComponent(Component component);
    
    /**
     * @param service
     */
    void setTargetComponentService(ComponentService service);
    
    /**
     * @param binding
     */
    void setTargetBinding(Binding binding);
    
    /**
     * @return
     */
    Binding getTargetBinding();
    
    /**
     * @return
     */
    Component getTargetComponent();
    
    /**
     * @return
     */
    ComponentService getTargetComponentService();

    /**
     * Clone the binding
     * @return
     */
    Object clone() throws CloneNotSupportedException;

}
