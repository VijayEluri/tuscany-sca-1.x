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
package org.apache.tuscany.core.services.management.jmx.runtime;

import javax.management.MBeanServer;

import org.apache.tuscany.host.RuntimeInfo;

/**
 * Runtime info that gives access to the underlying JMX MBean server.
 * 
 * @version $Revision$ $Date$
 *
 */
public interface JmxRuntimeInfo extends RuntimeInfo {
    
    /** Component name. */
    String COMPONENT_NAME = "JmxRuntimeInfo";
    
    /**
     * Returns a reference to the mbean server used by the host.
     * @return A reference to the host mbean server.
     */
    MBeanServer getMBeanServer();
    
    /**
     * Returns the management domain used by the runtime.
     * @return Management domain used by the runtime.
     */
    String getManagementDomain();

}
