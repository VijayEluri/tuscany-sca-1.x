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
package org.apache.tuscany.core.component.scope;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.model.Scope;

import org.apache.tuscany.core.component.event.RequestEnd;
import org.apache.tuscany.core.component.event.RequestStart;

/**
 * A scope context which manages atomic component instances keyed on the current request context
 *
 * @version $Rev$ $Date$
 */
public class RequestScopeContainer extends AbstractScopeContainer {

    private final Map<AtomicComponent, Map<Thread, InstanceWrapper>> contexts;
    private final Map<Thread, List<InstanceWrapper>> destroyQueues;

    public RequestScopeContainer() {
        this(null);
    }

    public RequestScopeContainer(WorkContext workContext) {
        super("Request Scope", workContext);
        contexts = new ConcurrentHashMap<AtomicComponent, Map<Thread, InstanceWrapper>>();
        destroyQueues = new ConcurrentHashMap<Thread, List<InstanceWrapper>>();
    }

    public Scope getScope() {
        return Scope.REQUEST;
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof RequestStart) {
            for (Map.Entry<AtomicComponent, Map<Thread, InstanceWrapper>> entry : contexts.entrySet()) {
                if (entry.getKey().isEagerInit()) {
                    getInstance(entry.getKey());
                }
            }
        } else if (event instanceof RequestEnd) {
            shutdownInstances(Thread.currentThread());
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

    public void register(AtomicComponent component) {
        contexts.put(component, new ConcurrentHashMap<Thread, InstanceWrapper>());
    }

    protected InstanceWrapper getInstanceWrapper(AtomicComponent component) throws TargetException {
        Map<Thread, InstanceWrapper> instanceContextMap = contexts.get(component);
        assert instanceContextMap != null : "Atomic component not registered";
        InstanceWrapper ctx = instanceContextMap.get(Thread.currentThread());
        if (ctx == null) {
            ctx = new InstanceWrapperImpl(component, component.createInstance());
            ctx.start();
            instanceContextMap.put(Thread.currentThread(), ctx);
            List<InstanceWrapper> destroyQueue = destroyQueues.get(Thread.currentThread());
            if (destroyQueue == null) {
                destroyQueue = new ArrayList<InstanceWrapper>();
                destroyQueues.put(Thread.currentThread(), destroyQueue);
            }
            synchronized (destroyQueue) {
                destroyQueue.add(ctx);
            }
        }
        return ctx;
    }

    private void shutdownInstances(Thread key) {
        List<InstanceWrapper> destroyQueue = destroyQueues.remove(key);
        if (destroyQueue != null && destroyQueue.size() > 0) {
            Thread thread = Thread.currentThread();
            for (Map<Thread, InstanceWrapper> map : contexts.values()) {
                map.remove(thread);
            }
            ListIterator<InstanceWrapper> iter = destroyQueue.listIterator(destroyQueue.size());
            synchronized (destroyQueue) {
                while (iter.hasPrevious()) {
                    try {
                        iter.previous().stop();
                    } catch (TargetException e) {
                        // TODO send a monitoring event
                    }
                }
            }
        }
    }

}
