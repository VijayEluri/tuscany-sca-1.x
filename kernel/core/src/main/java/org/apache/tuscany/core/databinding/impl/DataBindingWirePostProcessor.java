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

package org.apache.tuscany.core.databinding.impl;

import java.util.Collection;
import java.util.Map;

import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.InvocationChain;
import org.apache.tuscany.spi.wire.Wire;
import org.apache.tuscany.spi.wire.WirePostProcessorExtension;

/**
 * This processor is responsible to add an interceptor to invocation chain if the source and target operations have
 * different databinding requirements
 *
 * @version $Rev$ $Date$
 */
public class DataBindingWirePostProcessor extends WirePostProcessorExtension {
    private Mediator mediator;

    @Constructor({"mediator"})
    public DataBindingWirePostProcessor(@Reference Mediator mediator) {
        super();
        this.mediator = mediator;
    }

    public void process(Wire wire) {
        Map<Operation<?>, InvocationChain> chains = wire.getInvocationChains();
        for (Map.Entry<Operation<?>, InvocationChain> entry : chains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            ServiceContract<?> serviceContract = wire.getTargetContract();
            if (serviceContract == null) {
                continue;
            }
            Collection<? extends Operation<?>> operations = serviceContract.getOperations().values();
            Operation<?> targetOperation = getTargetOperation(operations, sourceOperation.getName());
            String sourceDataBinding = sourceOperation.getDataBinding();
            String targetDataBinding = targetOperation.getDataBinding();
            if (sourceDataBinding == null && targetDataBinding == null) {
                continue;
            }
            if (sourceDataBinding == null || targetDataBinding == null
                || !sourceDataBinding.equals(targetDataBinding)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor =
                    new DataBindingInteceptor(wire, sourceOperation, targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }

        // Check if there's a callback
        Map callbackOperations = wire.getSourceContract().getCallbackOperations();
        if (callbackOperations == null || callbackOperations.isEmpty()) {
            return;
        }
        //Object targetAddress = UriHelper.getBaseName(source.getUri());
        Map<Operation<?>, InvocationChain> callbackChains = wire.getCallbackInvocationChains();
        if (callbackChains == null) {
            // callback chains could be null
            return;
        }
        for (Map.Entry<Operation<?>, InvocationChain> entry : callbackChains.entrySet()) {
            Operation<?> sourceOperation = entry.getKey();
            Operation<?> targetOperation =
                getTargetOperation(wire.getCallbackInvocationChains().keySet(), sourceOperation
                    .getName());
            String sourceDataBinding = sourceOperation.getDataBinding();
            String targetDataBinding = targetOperation.getDataBinding();
            if (sourceDataBinding == null && targetDataBinding == null) {
                continue;
            }
            if (sourceDataBinding == null || targetDataBinding == null
                || !sourceDataBinding.equals(targetDataBinding)) {
                // Add the interceptor to the source side because multiple
                // references can be wired
                // to the same service
                DataBindingInteceptor interceptor =
                    new DataBindingInteceptor(wire, sourceOperation, targetOperation);
                interceptor.setMediator(mediator);
                entry.getValue().addInterceptor(0, interceptor);
            }
        }
    }


    private Operation getTargetOperation(Collection<? extends Operation<?>> operations, String operationName) {
        for (Operation<?> op : operations) {
            if (op.getName().equals(operationName)) {
                return op;
            }
        }
        return null;
    }

}
