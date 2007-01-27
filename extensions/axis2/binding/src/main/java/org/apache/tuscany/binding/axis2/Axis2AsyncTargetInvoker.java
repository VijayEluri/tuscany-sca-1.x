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
package org.apache.tuscany.binding.axis2;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;

import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class Axis2AsyncTargetInvoker extends Axis2TargetInvoker {

    private Axis2ReferenceCallbackTargetInvoker callbackInvoker;

    public Axis2AsyncTargetInvoker(ServiceClient serviceClient,
                                   QName wsdlOperationName,
                                   Options options,
                                   SOAPFactory soapFactory, WorkContext workContext) {
        super(serviceClient, wsdlOperationName, options, soapFactory, workContext);
    }

    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        throw new InvocationTargetException(new InvocationRuntimeException("Operation not supported"));
    }

    private Object invokeTarget(final Object payload, LinkedList<Object> callbackRoutingChain)
        throws InvocationTargetException {
        try {
            Object[] args = (Object[]) payload;
            OperationClient operationClient = createOperationClient(args);
            callbackInvoker.setCallbackRoutingChain(callbackRoutingChain);
            Axis2ReferenceCallback callback = new Axis2ReferenceCallback(callbackInvoker);
            operationClient.setCallback(callback);

            // FIXME Synchronize with callback thread to get return value
            CountDownLatch doneSignal = new CountDownLatch(1);
            callbackInvoker.setSignal(doneSignal);

            operationClient.execute(false);

            try {
                doneSignal.await();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            // FIXME returning value from callback thread
            Object response = callbackInvoker.getReturnPayload();
            return response;
        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), msg.getCallbackRoutingChain());
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public void setCallbackTargetInvoker(Axis2ReferenceCallbackTargetInvoker callbackInvoker) {
        this.callbackInvoker = callbackInvoker;
    }
}
