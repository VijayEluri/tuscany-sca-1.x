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
package org.apache.tuscany.sca.binding.jms.wireformat.jmsobject.runtime;

import javax.jms.Session;

import org.apache.tuscany.sca.binding.jms.context.JMSBindingContext;
import org.apache.tuscany.sca.binding.jms.impl.JMSBinding;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessor;
import org.apache.tuscany.sca.binding.jms.provider.JMSMessageProcessorUtil;
import org.apache.tuscany.sca.binding.jms.provider.JMSResourceFactory;
import org.apache.tuscany.sca.binding.jms.wireformat.jmsobject.WireFormatJMSObject;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Interceptor;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.apache.tuscany.sca.runtime.RuntimeWire;

/**
 * Policy handler to handle PolicySet related to Logging with the QName
 * {http://tuscany.apache.org/xmlns/sca/1.0/impl/java}LoggingPolicy
 *
 * @version $Rev$ $Date$
 */
public class WireFormatJMSObjectServiceInterceptor implements Interceptor {
    private Invoker next;
    private RuntimeWire runtimeWire;
    private JMSResourceFactory jmsResourceFactory;
    private JMSBinding jmsBinding;
    private JMSMessageProcessor requestMessageProcessor;
    private JMSMessageProcessor responseMessageProcessor;

    public WireFormatJMSObjectServiceInterceptor(JMSBinding jmsBinding, JMSResourceFactory jmsResourceFactory, RuntimeWire runtimeWire) {
        super();
        this.jmsBinding = jmsBinding;
        this.runtimeWire = runtimeWire;
        this.jmsResourceFactory = jmsResourceFactory;
        this.requestMessageProcessor = JMSMessageProcessorUtil.getRequestMessageProcessor(jmsBinding);
        this.responseMessageProcessor = JMSMessageProcessorUtil.getResponseMessageProcessor(jmsBinding);
    }

    public Message invoke(Message msg) {
        if (jmsBinding.getRequestWireFormat() instanceof WireFormatJMSObject){
            msg = invokeRequest(msg);
        }
        
        msg = getNext().invoke(msg);
        
        //if it's oneway return back
        Operation operation = msg.getOperation();
        if (operation != null && operation.isNonBlocking()) {
            return msg;
        }

        if (jmsBinding.getResponseWireFormat() instanceof WireFormatJMSObject){
            msg = invokeResponse(msg);
        }
        
        return msg;
    }
    
    public Message invokeRequest(Message msg) {
        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        javax.jms.Message jmsMsg = context.getJmsMsg();

        Object requestPayload = requestMessageProcessor.extractPayloadFromJMSMessage(jmsMsg);
        
        if (requestPayload != null && requestPayload.getClass().isArray()) {
            msg.setBody(requestPayload);
        } else {
            msg.setBody(new Object[] {requestPayload});
        }
          
        return msg;
    }
    
    public Message invokeResponse(Message msg) {
        // get the jms context
        JMSBindingContext context = msg.getBindingContext();
        Session session = context.getJmsResponseSession();

        javax.jms.Message responseJMSMsg = null;
        if (msg.isFault()) {
            responseJMSMsg = responseMessageProcessor.createFaultMessage(session, (Throwable)msg.getBody());
        } else {
            responseJMSMsg = responseMessageProcessor.insertPayloadIntoJMSMessage(session, msg.getBody());
        } 
    
        msg.setBody(responseJMSMsg);
        
        return msg;
    }        

    public Invoker getNext() {
        return next;
    }

    public void setNext(Invoker next) {
        this.next = next;
    }
}
