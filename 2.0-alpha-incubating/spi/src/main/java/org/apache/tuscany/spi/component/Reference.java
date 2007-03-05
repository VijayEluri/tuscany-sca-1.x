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
package org.apache.tuscany.spi.component;

import java.util.List;

import org.apache.tuscany.spi.model.ServiceContract;

/**
 * The runtime instantiation of an SCA reference
 *
 * @version $Rev$ $Date$
 */
public interface Reference extends SCAObject {

    /**
     * Returns the contract for the reference.
     *
     * @return the contract for the reference.
     */
    ServiceContract<?> getServiceContract();

    /**
     * Returns the collection of bindings configured for the reference.
     *
     * @return the collection of bindings configured for the reference.
     */
    List<ReferenceBinding> getReferenceBindings();

    /**
     * Adds a binding the reference is configured with.
     *
     * @param binding the  binding the reference is configured with.
     */
    void addReferenceBinding(ReferenceBinding binding);
}
