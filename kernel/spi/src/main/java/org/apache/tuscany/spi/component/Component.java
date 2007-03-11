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
import java.util.Map;

import org.osoa.sca.ComponentContext;

import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.Wire;

/**
 * The runtime instantiation of an SCA component
 *
 * @version $$Rev$$ $$Date$$
 */
public interface Component extends Invocable {

    /**
     * Returns the component scope
     *
     * @return the component scope
     */
    @Deprecated
    Scope getScope();

    /**
     * Returns the SCA ComponentContext for this component.
     *
     * @return the SCA ComponentContext for this component
     */
    ComponentContext getComponentContext();

    /**
     * Sets the scope container associated with the component
     *
     * @param scopeContainer the scope container associated with the component
     */
    void setScopeContainer(ScopeContainer scopeContainer);

    /**
     * Returns a collection of wires for the component associated with a reference name
     *
     * @return a collection of wires for the component associated with a reference name
     */
    List<Wire> getWires(String name);

    /**
     * Returns the default property values associated with the component.
     *
     * @return default property values associated with the component.
     */
    Map<String, PropertyValue<?>> getDefaultPropertyValues();

    /**
     * Sets the default property values associated with the component.
     *
     * @param defaultPropertyValues Default property values associated with the component.
     */
    void setDefaultPropertyValues(Map<String, PropertyValue<?>> defaultPropertyValues);

    /**
     * Returns true if invocation dispatching can be optimized, i.e. invocation chains are not required
     *
     * @return true if invocation dispatching can be optimized, i.e. invocation chains are not required
     */
    @Deprecated
    boolean isOptimizable();

    /**
     * Registers a service of this composite.
     *
     * @param service the service to add as a child
     * @throws RegistrationException
     */
    @Deprecated
    void register(Service service) throws RegistrationException;

    /**
     * Registers a reference of this composite.
     *
     * @param reference the reference to add as a child
     * @throws RegistrationException
     */
    @Deprecated
    void register(Reference reference) throws RegistrationException;

    /**
     * Returns the service with the given name or null if not found
     *
     * @param name the service name which is relative to the composite
     * @return the service with the given name or null if not found
     */
    @Deprecated
    Service getService(String name);

    /**
     * Returns the reference with the given name or null if not found
     *
     * @param name the reference name which is relative to the composite
     * @return the reference with the given name or null if not found
     */
    @Deprecated
    Reference getReference(String name);

    /**
     * Attaches a callback wire to the comoponent
     *
     * @param wire the wire to attach
     */
    void attachCallbackWire(Wire wire);

    /**
     * Attaches a wire to a component reference
     *
     * @param wire the wire to attach
     */
    void attachWire(Wire wire);

    /**
     * Attaches a set of wires to a component reference. Used for multiplicity.
     *
     * @param wires the wire to attach
     */
    void attachWires(List<Wire> wires);

}
