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
package org.apache.tuscany.core.invocation.impl;

import org.apache.tuscany.core.invocation.Interceptor;
import org.apache.tuscany.core.invocation.InvocationRuntimeException;
import org.apache.tuscany.core.invocation.TargetInvoker;
import org.apache.tuscany.core.message.Message;

/**
 * Serves as a tail interceptor on a target invocation chain. This implementation dispatches to the target invoker
 * passed inside the invocation message. Target invokers are passed from the source in order to allow for caching of
 * target instances.
 * 
 * @see org.apache.tuscany.core.invocation.TargetInvoker
 * @version $Rev$ $Date$
 */
public class InvokerInterceptor implements Interceptor {

    public InvokerInterceptor() {
    }

    public Message invoke(Message msg) throws InvocationRuntimeException {
        TargetInvoker invoker = msg.getTargetInvoker();
        if (invoker == null) {
            throw new InvocationRuntimeException("No target invoker specified on message");
        }
        return invoker.invoke(msg);
    }

    public void setNext(Interceptor next) {
        throw new IllegalStateException("This interceptor must be the last one in an target interceptor chain");
    }

}
