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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.core.RuntimeComponent;
import org.apache.tuscany.core.component.event.HttpSessionEnd;
import org.apache.tuscany.scope.InstanceWrapper;
import org.apache.tuscany.scope.Scope;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;

/**
 * A scope context which manages atomic component instances keyed on HTTP
 * session
 * 
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeContainer extends AbstractScopeContainer {
    private final WorkContext workContext;
    private final Map<RuntimeComponent, Map<Object, InstanceWrapper>> contexts;
    private final Map<Object, List<InstanceWrapper>> destroyQueues;

    public HttpSessionScopeContainer(WorkContext workContext) {
        super(Scope.SESSION);
        this.workContext = workContext;
        contexts = new ConcurrentHashMap<RuntimeComponent, Map<Object, InstanceWrapper>>();
        destroyQueues = new ConcurrentHashMap<Object, List<InstanceWrapper>>();
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof HttpSessionEnd) {
            Object key = ((HttpSessionEnd)event).getId();
            shutdownInstances(key);
            workContext.clearIdentifier(key);
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        contexts.clear();
        synchronized (destroyQueues) {
            destroyQueues.clear();
        }
        lifecycleState = STOPPED;
    }

    public void register(RuntimeComponent component, Object groupId) {
        contexts.put(component, new ConcurrentHashMap<Object, InstanceWrapper>());
        // component.addListener(this);
    }

    public void unregister(RuntimeComponent component) {
        // FIXME should all the instances associated with this component be
        // destroyed already
        contexts.remove(component);
        // component.removeListener(this);
        super.unregister(component);
    }

    protected InstanceWrapper getInstanceWrapper(RuntimeComponent component, boolean create)
        throws TargetResolutionException {
        Object key = workContext.getIdentifier(Scope.SESSION);
        assert key != null : "HTTP session key not bound in work context";
        Map<Object, InstanceWrapper> wrappers = contexts.get(component);
        InstanceWrapper ctx = wrappers.get(key);
        if (ctx == null && !create) {
            return null;
        }
        if (ctx == null) {
            ctx = super.createInstanceWrapper(component);
            ctx.start();
            wrappers.put(key, ctx);
            List<InstanceWrapper> destroyQueue = destroyQueues.get(key);
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceWrapper>();
                destroyQueues.put(key, destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void shutdownInstances(Object key) {
        List<InstanceWrapper> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null) {
            for (Map<Object, InstanceWrapper> map : contexts.values()) {
                map.remove(key);
            }
            ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
            synchronized (destroyQueue) {
                while (iter.hasPrevious()) {
                    try {
                        iter.previous().stop();
                    } catch (TargetDestructionException e) {
                        // monitor.destructionError(e);
                    }
                }
            }
        }
    }

}
