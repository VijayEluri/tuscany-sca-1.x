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
package org.apache.tuscany.core.wire;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InboundInvocationChain;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.Interceptor;
import org.apache.tuscany.spi.wire.OutboundInvocationChain;
import org.apache.tuscany.spi.wire.OutboundWire;

/**
 * Default implementation of an outbound wire
 *
 * @version $Rev$ $Date$
 */
public class OutboundWireImpl<T> implements OutboundWire<T> {

    private ServiceContract serviceContract;
    private Class<T>[] callbackInterfaces;
    private Map<Operation<?>, OutboundInvocationChain> chains = new HashMap<Operation<?>, OutboundInvocationChain>();
    private Map<Operation<?>, InboundInvocationChain> callbackTargetChains =
        new HashMap<Operation<?>, InboundInvocationChain>();
    private String referenceName;
    private QualifiedName targetName;
    private InboundWire<T> targetWire;
    private String containerName;

    public T getTargetService() throws TargetException {
        if (targetWire != null) {
            // optimized, no interceptors or handlers on either end
            return targetWire.getTargetService();
        }
        throw new TargetException("Target wire not optimized");
    }

    public ServiceContract getServiceContract() {
        return serviceContract;
    }

    public void setServiceContract(ServiceContract serviceContract) {
        this.serviceContract = serviceContract;
    }

    public void addInterface(Class<?> claz) {
        throw new UnsupportedOperationException("Additional proxy interfaces not yet supported");
    }

    @SuppressWarnings("unchecked")
    public void setCallbackInterface(Class<T> interfaze) {
        callbackInterfaces = new Class[]{interfaze};
    }

    public Class<T> getCallbackInterface() {
        return callbackInterfaces[0];
    }

    public void addCallbackInterface(Class<?> claz) {
        throw new UnsupportedOperationException("Additional callback interfaces not yet supported");
    }

    public Class[] getImplementedCallbackInterfaces() {
        return callbackInterfaces;
    }

    public void setTargetWire(InboundWire<T> wire) {
        this.targetWire = wire;
    }

    public Map<Operation<?>, OutboundInvocationChain> getInvocationChains() {
        return chains;
    }

    public void addInvocationChains(Map<Operation<?>, OutboundInvocationChain> chains) {
        this.chains.putAll(chains);
    }

    public void addInvocationChain(Operation<?> operation, OutboundInvocationChain chain) {
        chains.put(operation, chain);
    }

    public Map<Operation<?>, InboundInvocationChain> getTargetCallbackInvocationChains() {
        return callbackTargetChains;
    }

    public void addTargetCallbackInvocationChains(Map<Operation<?>, InboundInvocationChain> chains) {
        callbackTargetChains.putAll(chains);
    }

    public void addTargetCallbackInvocationChain(Operation operation, InboundInvocationChain chain) {
        callbackTargetChains.put(operation, chain);
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public QualifiedName getTargetName() {
        return targetName;
    }

    public void setTargetName(QualifiedName targetName) {
        this.targetName = targetName;
    }

    public boolean isOptimizable() {
        for (OutboundInvocationChain chain : chains.values()) {
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null && current != chain.getTargetInterceptor()) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }

        for (InboundInvocationChain chain : callbackTargetChains.values()) {
            if (chain.getTargetInvoker() != null && !chain.getTargetInvoker().isOptimizable()) {
                return false;
            }
            if (chain.getHeadInterceptor() != null) {
                Interceptor current = chain.getHeadInterceptor();
                while (current != null) {
                    if (!current.isOptimizable()) {
                        return false;
                    }
                    current = current.getNext();
                }
            }
        }

        return true;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String name) {
        this.containerName = name;
    }
}
