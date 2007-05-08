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
package org.apache.tuscany.core.scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.assembly.Implementation;
import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.ScopedImplementationProvider;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.scope.ScopeContainer;
import org.apache.tuscany.scope.ScopeContainerFactory;
import org.apache.tuscany.scope.ScopeRegistry;

/**
 * The default implementation of a scope registry
 * 
 * @version $Rev$ $Date$
 */
public class ScopeRegistryImpl implements ScopeRegistry {
    private final Map<Scope, ScopeContainerFactory> scopeCache = new ConcurrentHashMap<Scope, ScopeContainerFactory>();

    public void register(ScopeContainerFactory factory) {
        scopeCache.put(factory.getScope(), factory);
    }

    public ScopeContainer getScopeContainer(RuntimeComponent component) {
        if (component.getScopeContainer() != null) {
            return component.getScopeContainer();
        }
        Implementation impl = component.getImplementation();
        if (impl instanceof ScopedImplementationProvider) {
            ScopedImplementationProvider provider = (ScopedImplementationProvider)impl;
            Scope scope = provider.getScope();
            if (scope == null) {
                scope = Scope.STATELESS;
            }
            ScopeContainerFactory factory = scopeCache.get(scope);
            ScopeContainer container = factory.createScopeContainer(component);
            component.setScopeContainer(container);
            return container;
        }
        return null;
    }

}
