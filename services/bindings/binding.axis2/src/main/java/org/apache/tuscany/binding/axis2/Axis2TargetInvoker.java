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
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.wire.InvocationRuntimeException;
import org.apache.tuscany.spi.wire.Message;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.wsdl.WSDLConstants;

/**
 * Axis2TargetInvoker uses an Axis2 OperationClient to invoke a remote web service
 */
public class Axis2TargetInvoker implements TargetInvoker {

    private QName wsdlOperationName;

    private Options options;

    private SOAPFactory soapFactory;

    private ServiceClient serviceClient;

    public Axis2TargetInvoker(ServiceClient serviceClient, QName wsdlOperationName, Options options,
                              SOAPFactory soapFactory) {
        this.wsdlOperationName = wsdlOperationName;
        this.options = options;
        this.soapFactory = soapFactory;
        this.serviceClient = serviceClient;
    }

    /**
     * Invoke a WS operation
     *
     * @param payload
     * @param sequence
     * @return
     * @throws InvocationTargetException
     */
    public Object invokeTarget(final Object payload, final short sequence) throws InvocationTargetException {
        try {
            Object[] args = (Object[]) payload;
            OperationClient operationClient = createOperationClient(args);

            operationClient.execute(true);

            MessageContext responseMC = operationClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            return responseMC.getEnvelope().getBody().getFirstElement();

        } catch (AxisFault e) {
            throw new InvocationTargetException(e);
        }

    }

    protected OperationClient createOperationClient(Object[] args) throws AxisFault {
        SOAPEnvelope env = soapFactory.getDefaultEnvelope();
        if (args != null && args.length > 0) {
            SOAPBody body = env.getBody();
            for (Object bc : args) {
                if (bc instanceof OMElement) {
                    body.addChild((OMElement) bc);
                } else {
                    throw new IllegalArgumentException(
                        "Can't handle mixed payloads betweem OMElements and other types.");
                }
            }
        }
        MessageContext requestMC = new MessageContext();
        requestMC.setEnvelope(env);
        // Axis2 operationClients can not be shared so create a new one for each request
        OperationClient operationClient = serviceClient.createClient(wsdlOperationName);
        operationClient.setOptions(options);
        operationClient.addMessageContext(requestMC);
        return operationClient;
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        try {
            Object resp = invokeTarget(msg.getBody(), NONE);
            msg.setBody(resp);
        } catch (Throwable e) {
            msg.setBodyWithFault(e);
        }
        return msg;
    }

    public Axis2TargetInvoker clone() throws CloneNotSupportedException {
        try {
            return (Axis2TargetInvoker) super.clone();
        } catch (CloneNotSupportedException e) {
            // will not happen
            return null;
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public void setCacheable(boolean cacheable) {

    }

    public boolean isOptimizable() {
        return false;
    }

}
