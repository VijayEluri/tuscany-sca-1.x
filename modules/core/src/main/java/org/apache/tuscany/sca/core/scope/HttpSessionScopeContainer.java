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
package org.apache.tuscany.sca.core.scope;

import org.apache.tuscany.sca.core.event.HttpSessionEnd;
import org.apache.tuscany.sca.event.Event;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.scope.InstanceWrapper;
import org.apache.tuscany.sca.scope.Scope;
import org.apache.tuscany.sca.scope.TargetResolutionException;

/**
 * A scope context which manages atomic component instances keyed on HTTP
 * session
 * 
 * @version $Rev$ $Date$
 */
public class HttpSessionScopeContainer extends AbstractScopeContainer<Object> {

    public HttpSessionScopeContainer(RuntimeComponent component) {
        super(Scope.SESSION, component);
    }

    public void onEvent(Event event) {
        checkInit();
        if (event instanceof HttpSessionEnd) {
            Object key = ((HttpSessionEnd)event).getSessionID();
            // FIXME: Remove the session id
        }
    }

    public synchronized void start() {
        if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
            throw new IllegalStateException("Scope must be in UNINITIALIZED or STOPPED state [" + lifecycleState + "]");
        }
        lifecycleState = RUNNING;
    }

    public synchronized void stop() {
        lifecycleState = STOPPED;
    }

    protected InstanceWrapper getInstanceWrapper(boolean create) throws TargetResolutionException {
//        Object key = workContext.getIdentifier(Scope.SESSION);
        // FIXME: Need to fix this
        Object key ="http-session-id";
        assert key != null : "HTTP session key not bound in work context";
        InstanceWrapper ctx = wrappers.get(key);
        if (ctx == null && !create) {
            return null;
        }
        if (ctx == null) {
            ctx = super.createInstanceWrapper();
            ctx.start();
            wrappers.put(key, ctx);
        }
        return ctx;
    }
    
    @Override
    public InstanceWrapper getWrapper(Object contextId) throws TargetResolutionException {
        return getInstanceWrapper(true);
    }    

}
