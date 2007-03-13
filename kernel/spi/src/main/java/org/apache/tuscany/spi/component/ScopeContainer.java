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

import org.apache.tuscany.spi.Lifecycle;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.model.Scope;


/**
 * Manages the lifecycle and visibility of instances associated with a an {@link AtomicComponent}.
 *
 * @version $Rev$ $Date$
 * @param <KEY> the type of IDs that this container uses to identify its contexts.
 * For example, for COMPOSITE scope this could be the URI of the composite component,
 * or for HTTP Session scope it might be the HTTP session ID.
 */
public interface ScopeContainer<GROUP, KEY> extends Lifecycle, RuntimeEventListener {

    /**
     * Returns the Scope that this container supports.
     *
     * @return the Scope that this container supports
     */
    Scope getScope();

    /**
     * Create a group for associating components together.
     *
     * @param groupId an identifier for the group
     */
    void createGroup(GROUP groupId);

    /**
     * Remove a group that associates components together.
     *
     * @param groupId an identifier for the group
     */
    void removeGroup(GROUP groupId);

    /**
     * Start a new context with the supplied ID.
     *
     * @param contextId an ID that uniquely identifies the context.
     */
    void startContext(KEY contextId);

    /**
     * Stop the context with the supplied ID.
     *
     * @param contextId an ID that uniquely identifies the context.
     */
    void stopContext(KEY contextId);

    /**
     * Registers a component with the scope.
     *
     * @param component the component to register
     */
    void register(AtomicComponent component);

    /**
     * Unregisters a component with the scope.
     *
     * @param component the component to unregister
     */
    void unregister(AtomicComponent component);

    /**
     * Returns an instance wrapper associated with the current scope context, creating one if necessary
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @param contextId the id for the scope context
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
    <T> InstanceWrapper<T> getWrapper(AtomicComponent component, KEY contextId) throws TargetResolutionException;

    /**
     * Returns an implementation instance associated with the current scope context.
     * If no instance is found, a {@link TargetNotFoundException} is thrown.
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @param contextId the id for the scope context
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
    <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component, KEY contextId)
        throws TargetResolutionException;

    /**
     * Return a wrapper after use (for example, after invoking the instance).
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @param contextId the id for the scope context
     * @param wrapper the wrapper for the target instance being returned
     * @throws TargetDestructionException if there was a problem returning the target instance
     */
    <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper, KEY contextId)
        throws TargetDestructionException;

    /**
     * Returns an instance wrapper associated with the current scope context, creating one if necessary
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
    @Deprecated
    <T> InstanceWrapper<T> getWrapper(AtomicComponent component) throws TargetResolutionException;

    /**
     * Returns an implementation instance associated with the current scope context.
     * If no instance is found, a {@link TargetNotFoundException} is thrown.
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @return the wrapper for the target instance
     * @throws TargetResolutionException if there was a problem instantiating the target instance
     */
    @Deprecated
    <T> InstanceWrapper<T> getAssociatedWrapper(AtomicComponent component) throws TargetResolutionException;

    /**
     * Return a wrapper after use (for example, after invoking the instance).
     *
     * @param <T> the type of the target instance
     * @param component the component
     * @param wrapper the wrapper for the target instance being returned
     * @throws TargetDestructionException if there was a problem returning the target instance
     */
    @Deprecated
    <T> void returnWrapper(AtomicComponent component, InstanceWrapper<T> wrapper) throws TargetDestructionException;

    /**
     * Removes a component implementation instance associated with the current context from persistent storage
     *
     * @param component the owning component
     * @throws PersistenceException if there was a problem removing the instance
     */
    void remove(AtomicComponent component) throws PersistenceException;
}
