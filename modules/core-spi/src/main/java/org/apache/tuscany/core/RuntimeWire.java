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

import java.util.List;

import org.apache.tuscany.assembly.Binding;
import org.apache.tuscany.assembly.ComponentReference;
import org.apache.tuscany.assembly.ComponentService;
import org.apache.tuscany.spi.wire.InvocationChain;

/**
 * The runtime wire that connects a component reference to a component service
 * (or an external service) over the selected binding
 * 
 * @version $Rev$ $Date$
 */
public interface RuntimeWire {
    /**
     * Get the source of the wire
     * 
     * @return
     */
    ComponentReference getSource();

    /**
     * Get the reference binding
     * 
     * @return
     */
    Binding getSourceBinding();

    /**
     * Get the target of the wire. It will be null if the binding is
     * 
     * @return
     */
    ComponentService getTarget();

    /**
     * @return
     */
    Binding getTargetBinding();

    /**
     * Returns the invocation chains for service operations associated with the
     * wire
     * 
     * @return the invocation chains for service operations associated with the
     *         wire
     */
    List<InvocationChain> getInvocationChains();

    /**
     * Returns the invocation chains for callback service operations associated
     * with the wire
     * 
     * @return the invocation chains for callback service operations associated
     *         with the wire
     */
    List<InvocationChain> getCallbackInvocationChains();

}
