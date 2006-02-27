/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context.impl;

import java.lang.reflect.InvocationHandler;

import org.apache.tuscany.core.context.AbstractContext;
import org.apache.tuscany.core.context.ContextInitException;
import org.apache.tuscany.core.context.CoreRuntimeException;
import org.apache.tuscany.core.context.EntryPointContext;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.TargetException;
import org.apache.tuscany.core.invocation.jdk.JDKInvocationHandler;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.MessageFactory;

/**
 * The default implementation of an entry point context
 * 
 * @version $Rev$ $Date$
 */
public class EntryPointContextImpl extends AbstractContext implements EntryPointContext {

    private MessageFactory messageFactory;

    private ProxyFactory proxyFactory;

    private Object target;

    private InvocationHandler invocationHandler;

    // ----------------------------------
    // Constructors
    // ----------------------------------

    /**
     * Creates a new entry point
     * 
     * @param name the entry point name
     * @param proxyFactory the proxy factory containing the invocation chains for the entry point
     * @param parentContext the containing aggregate of the entry point
     * @param messageFactory a factory for generating invocation messages
     * @throws ContextInitException if an error occurs creating the entry point
     */
    public EntryPointContextImpl(String name, ProxyFactory proxyFactory, MessageFactory messageFactory)
            throws ContextInitException {
        super(name);
        assert (proxyFactory != null) : "Proxy factory was null";
        assert (messageFactory != null) : "Message factory was null";
        this.proxyFactory = proxyFactory;
        this.messageFactory = messageFactory;
        invocationHandler = new JDKInvocationHandler(messageFactory, proxyFactory.getProxyConfiguration()
                .getInvocationConfigurations());
    }

    // ----------------------------------
    // Methods
    // ----------------------------------

    public Object getInstance(QualifiedName qName) throws TargetException {
        return invocationHandler;
    }

    public Object getInstance(QualifiedName qName, boolean notify) throws TargetException {
        return getInstance(qName);
    }

    public void start() throws ContextInitException {
        lifecycleState = RUNNING;
    }

    public void stop() throws CoreRuntimeException {
        lifecycleState = STOPPED;
    }

    // ----------------------------------
    // InstanceContext methods
    // ----------------------------------

    public Object getImplementationInstance() throws TargetException {
        return invocationHandler;
    }

    public Object getImplementationInstance(boolean notify) throws TargetException {
        return getImplementationInstance();
    }

}
