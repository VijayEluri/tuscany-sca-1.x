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
package org.apache.tuscany.core.component;

import org.apache.tuscany.core.component.scope.InstanceWrapper;

/**
 * Interface for the instance factory used for creating Java component
 * instances. The implementation for this class may be generated dynamically by
 * the naster runtime during deployment. The instnace factory is expected to be
 * used by the physical component builder framework for creating component
 * instances.
 * 
 * @version $Rev$ $Date$
 * @param <T> Type of the instance generated by the factory.
 */
public interface InstanceFactory<T> {

    /**
     * Creates a new instance of the component. The implementations are expected
     * to wire the component using the necessary object factories. Teh expected
     * convention is for the implementations to accept the factories in the
     * constructor as a typed array.
     * 
     * @return A wrapper for the created component instance.
     */
    InstanceWrapper<T> newInstance();
}
