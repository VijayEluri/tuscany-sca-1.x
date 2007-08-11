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
package org.apache.tuscany.sca.core.component;

import java.util.List;
import javax.security.auth.Subject;

import org.apache.tuscany.sca.core.invocation.CallbackWireObjectFactory;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.core.invocation.ThreadMessageContext;
import org.apache.tuscany.sca.factory.ObjectFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;
import org.apache.tuscany.sca.runtime.RuntimeWire;
import org.osoa.sca.CallableReference;
import org.osoa.sca.RequestContext;
import org.osoa.sca.ServiceReference;

/**
 * @version $Rev$ $Date$
 */
public class RequestContextImpl implements RequestContext {

    private ProxyFactory proxyService;

    public RequestContextImpl(ProxyFactory proxyService) {
        this.proxyService = proxyService;
    }

    public Subject getSecuritySubject() {
        throw new UnsupportedOperationException();
    }

    public String getServiceName() {
        return ThreadMessageContext.getMessageContext().getTo().getContract().getName();
    }

    public <B> ServiceReference<B> getServiceReference() {
        EndpointReference to = ThreadMessageContext.getMessageContext().getTo();
        RuntimeComponentService service = (RuntimeComponentService) to.getContract();
        RuntimeComponent component = (RuntimeComponent) to.getComponent();
        JavaInterface javaInterface = (JavaInterface) service.getInterfaceContract().getInterface();
        return (ServiceReference<B>) component.createSelfReference(javaInterface.getJavaClass(), service.getName());
    }

    public <CB> CB getCallback() {
        return (CB) getCallbackReference().getService();
    }

    public <CB> CallableReference<CB> getCallbackReference() {
        EndpointReference to = ThreadMessageContext.getMessageContext().getTo();
        RuntimeComponentService service = (RuntimeComponentService) to.getContract();
        RuntimeComponentReference callbackReference = (RuntimeComponentReference)service.getCallbackReference();
        if (callbackReference == null) {
            return null;
        }
        JavaInterface javaInterface = (JavaInterface) callbackReference.getInterfaceContract().getInterface();
        Class<CB> javaClass = (Class<CB>)javaInterface.getJavaClass();
        List<RuntimeWire> wires = callbackReference.getRuntimeWires();
        CallbackWireObjectFactory factory = new CallbackWireObjectFactory(javaClass, proxyService, wires);
        factory.resolveTarget();
        return (CallableReference<CB>) new CallableReferenceImpl<CB>(javaClass, (ObjectFactory<CB>)factory);
    }
}
