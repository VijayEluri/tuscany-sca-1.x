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
package org.apache.tuscany.sca.invocation;

import org.apache.tuscany.sca.interfacedef.ConversationSequence;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.runtime.EndpointReference;
import org.osoa.sca.CallableReference;

/**
 * Represents a request, response, or exception flowing through a wire
 *
 * @version $Rev $Date
 */
public interface Message {

    /**
     * Returns the body of the message, which will be the payload or parameters associated with the wire
     * @return The body of the message
     */
    <T> T getBody();

    /**
     * Sets the body of the message.
     * @param body The body of the message
     */
    <T> void setBody(T body);

    /**
     * Get the conversation id
     * @return The conversation ID
     */
    Object getConversationID();

    /**
     * Set the conversation id
     * @param conversationId The conversation ID
     */
    void setConversationID(Object conversationId);

    /**
     * Get the end point reference of the source reference
     * @return The end point reference of the reference originating the message
     */
    EndpointReference getFrom();

    /**
     * Set the end point reference of the reference originating the message
     * @param from The end point reference of the reference originating the message
     */
    void setFrom(EndpointReference from);

    /**
     * Get the end point reference of target service
     * @return The end point reference of the service that the message targets
     */
    EndpointReference getTo();

    /**
     * Set the end point reference of target service
     * @param to The end point reference of the service that the message targets
     */
    void setTo(EndpointReference to);

    /**
     * Returns the id of the message
     * @return The message Id
     */
    Object getMessageID();

    /**
     * Sets the id of the message
     * @param messageId The message ID
     */
    void setMessageID(Object messageId);

    /**
     * Returns the correlation id of the message or null if one is not available. Correlation ids are used by transports
     * for message routing.
     * @return The correlation Id
     */
    Object getCorrelationID();

    /**
     * Sets the correlation id of the message. Correlation ids are used by transports for message routing.
     * @param correlationId The correlation Id
     */
    void setCorrelationID(Object correlationId);

    /**
     * Determines if the message represents a fault/exception
     *
     * @return true If the message body is a fault object, false if the body is a normal payload
     */
    boolean isFault();

    /**
     * Set the message body with a fault object. After this method is called, isFault() returns true.
     *
     * @param fault The fault object represents an exception
     */
    <T> void setFaultBody(T fault);

    /**
     * Returns the conversational sequence the message is associated with, NONE, START, CONTINUE, or END on TargetInvoker}
     *
     * @return The conversational sequence the message is associated with
     */
    ConversationSequence getConversationSequence();

    /**
     * Sets the conversational sequence the message is associated with, NONE, START, CONTINUE, or END
     *
     * @param sequence The conversational sequence
     */
    void setConversationSequence(ConversationSequence sequence);

    /**
     * Returns the operation that created the message.
     *
     * @return The operation that created the message
     */
    Operation getOperation();

    /**
     * Sets the operation that created the message.
     *
     * @param op The operation that created the message
     */
    void setOperation(Operation op);

    /**
     * Get the associated callable reference
     * @param <B>
     * @return The callable reference
     */
    <B> CallableReference<B> getCallableReference();
    
    /**
     * Set the callable reference
     * @param <B>
     * @param callableReference
     */
    <B> void setCallableReference(CallableReference<B> callableReference);

}
