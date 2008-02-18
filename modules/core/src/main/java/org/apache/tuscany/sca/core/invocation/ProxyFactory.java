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
package org.apache.tuscany.sca.core.invocation;

import java.util.List;

import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;

/**
 * Creates proxies that implement Java interfaces and invocation handlers for fronting wires
 *
 * @version $$Rev$$ $$Date$$
 */

public interface ProxyFactory {

    /**
     * Creates a Java proxy for the given wire
     *
     * @param interfaze the interface the proxy implements
     * @param wire      the wire to proxy
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createProxy(Class<T> interfaze, RuntimeWire wire) throws ProxyCreationException;

    /**
     * Creates a Java proxy for the given callable reference
     *
     * @param callableReference The callable reference
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createProxy(CallableReference<T> callableReference) throws ProxyCreationException;

    /**
     * Creates a Java proxy for the service contract callback
     *
     * @param interfaze the interface the proxy should implement
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createCallbackProxy(Class<T> interfaze, List<RuntimeWire> wires) throws ProxyCreationException;

    /**
     * Creates a Java proxy for the given callback reference
     *
     * @param callableReference The callable reference
     * @return the proxy
     * @throws ProxyCreationException
     */
    <T> T createCallbackProxy(CallbackReferenceImpl<T> callbackReference) throws ProxyCreationException;

    /**
     * Cast a proxy to a CallableReference.
     *
     * @param target a proxy generated by this implementation
     * @return a CallableReference (or subclass) equivalent to this prozy
     * @throws IllegalArgumentException if the object supplied is not a proxy
     */
    <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException;
    
    /**
     * Test if a given class is a generated proxy class by this factory
     * @param clazz A java class or interface
     * @return true if the class is a generated proxy class by this factory 
     */
    boolean isProxyClass(Class<?> clazz);

}
