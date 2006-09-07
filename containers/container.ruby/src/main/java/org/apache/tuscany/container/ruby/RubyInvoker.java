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
package org.apache.tuscany.container.ruby;

import java.lang.reflect.InvocationTargetException;

import org.apache.tuscany.container.ruby.rubyscript.RubyScriptInstance;
import org.apache.tuscany.spi.extension.TargetInvokerExtension;

/**
 * Dispatches to a JavaScript implementation instance
 * 
 * @version $$Rev$$ $$Date$$
 */
public class RubyInvoker extends TargetInvokerExtension {

    private RubyComponent context;

    private String functionName;

    private Class returnType;

    public RubyInvoker(String functionName, RubyComponent context, Class returnType) {
        this.functionName = functionName;
        this.context = context;
        this.returnType = returnType;
    }

    /**
     * Invokes a function on a script instance
     */
    public Object invokeTarget(final Object payload) throws InvocationTargetException {
        RubyScriptInstance target = context.getTargetInstance();
        return target.invokeFunction(functionName,
                                     (Object[]) payload,
                                     returnType);
    }

}
