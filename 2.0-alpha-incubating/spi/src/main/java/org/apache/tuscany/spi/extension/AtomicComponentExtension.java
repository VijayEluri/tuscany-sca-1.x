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
package org.apache.tuscany.spi.extension;

import java.net.URI;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.ComponentException;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.wire.ProxyService;

/**
 * An extension point for atomic component type, which new implementation types may extend
 *
 * @version $$Rev$$ $$Date$$
 */
public abstract class AtomicComponentExtension extends AbstractComponentExtension implements AtomicComponent {
    protected ScopeContainer scopeContainer;
    protected Scope scope;
    protected ProxyService proxyService;
    protected WorkContext workContext;
    private final int initLevel;
    private final long maxIdleTime;
    private final long maxAge;

    protected AtomicComponentExtension(URI name, ProxyService proxyService, WorkContext workContext, int initLevel) {
        this(name, proxyService, workContext, initLevel, -1, -1);
    }

    protected AtomicComponentExtension(URI name,
                                       ProxyService proxyService,
                                       WorkContext workContext,
                                       int initLevel,
                                       long maxIdleTime,
                                       long maxAge) {
        super(name);
        assert !(maxIdleTime > 0 && maxAge > 0);
        this.proxyService = proxyService;
        this.workContext = workContext;
        this.initLevel = initLevel;
        this.maxIdleTime = maxIdleTime;
        this.maxAge = maxAge;
    }

    public Scope getScope() {
        return scope;
    }

    public int getInitLevel() {
        return initLevel;
    }

    public boolean isEagerInit() {
        return initLevel > 0;
    }

    public boolean isDestroyable() {
        return false;
    }

    public long getMaxIdleTime() {
        return maxIdleTime;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        this.scopeContainer = scopeContainer;
        scope = scopeContainer.getScope();
    }

    public void start() throws CoreRuntimeException {
        super.start();
        scopeContainer.register(this);
    }

    public void init(Object instance) throws TargetInitializationException {

    }

    public void destroy(Object instance) throws TargetDestructionException {

    }

    public void removeInstance() throws ComponentException {
        scopeContainer.remove(this);
    }

}
