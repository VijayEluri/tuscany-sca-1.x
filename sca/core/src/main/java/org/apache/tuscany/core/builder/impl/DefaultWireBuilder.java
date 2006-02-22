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
package org.apache.tuscany.core.builder.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.builder.BuilderConfigException;
import org.apache.tuscany.core.builder.WireBuilder;
import org.apache.tuscany.core.context.QualifiedName;
import org.apache.tuscany.core.context.ScopeContext;
import org.apache.tuscany.core.invocation.InvocationConfiguration;
import org.apache.tuscany.core.invocation.spi.ProxyFactory;
import org.apache.tuscany.core.message.channel.impl.MessageChannelImpl;
import org.osoa.sca.annotations.Scope;

/**
 * The top-most wire builder configured in a runtime. Responsible for constructing wires from source and target chains,
 * this implementation first bridges the chains and then delegates to any other wire builders.
 * 
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class DefaultWireBuilder implements WireBuilder {

    public DefaultWireBuilder() {
    }

    // other configured wire builders
    private List<WireBuilder> builders = new ArrayList();

    /**
     * Adds a wire builder to delegate to
     */
    public void addWireBuilder(WireBuilder builder) {
        builders.add(builder);
    }

    public void connect(ProxyFactory sourceFactory, ProxyFactory targetFactory, Class targetType, boolean downScope,
            ScopeContext targetScopeContext) {
        QualifiedName targetName = sourceFactory.getProxyConfiguration().getTargetName();
        // get the proxy chain for the target
        if (targetFactory != null) {
            // if null, the target side has no interceptors or handlers
            Map<Method, InvocationConfiguration> targetInvocationConfigs = targetFactory.getProxyConfiguration().getInvocationConfigurations();
            for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration()
                    .getInvocationConfigurations().values()) {
                // match invocation chains
//<<<<<<< .mine
////                InvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig
////                        .getOperationType());
//                // should be done w/.equals():
//                InvocationConfiguration targetInvocationConfig = null;
//                for (Map.Entry<OperationType,InvocationConfiguration> entry : targetInvocationConfigs.entrySet()) {
//                    if (entry.getKey().getName().equals(sourceInvocationConfig.getOperationType().getName())){
//                        targetInvocationConfig = entry.getValue();
//                        break;
//                    }
//                }  
//=======
                InvocationConfiguration targetInvocationConfig = targetInvocationConfigs.get(sourceInvocationConfig.getMethod());
//>>>>>>> .r379382
                // if handler is configured, add that
                if (targetInvocationConfig.getRequestHandlers() != null) {
                    sourceInvocationConfig.setTargetRequestChannel(new MessageChannelImpl(targetInvocationConfig.getRequestHandlers()));
                    sourceInvocationConfig.setTargetResponseChannel(new MessageChannelImpl(targetInvocationConfig.getResponseHandlers()));
                } else {
                    // no handlers, just connect interceptors
                    if (targetInvocationConfig.getTargetInterceptor() == null){
                        BuilderConfigException e = new BuilderConfigException("No target handler or interceptor for operation");
                        e.setIdentifier(targetInvocationConfig.getMethod().getName());
                        throw e;
                    }
                    sourceInvocationConfig.addTargetInterceptor(targetInvocationConfig.getTargetInterceptor());
                }
            }
        }
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.connect(sourceFactory, targetFactory, targetType, downScope, targetScopeContext);
        }
        // signal that wire build process is complete
        boolean optimizable = true;
        for (InvocationConfiguration sourceInvocationConfig : sourceFactory.getProxyConfiguration().getInvocationConfigurations()
                .values()) {
            sourceInvocationConfig.build();
            // TODO optimize if no proxy needed using NullProxyFactory
        }
    }
    
    public void completeTargetChain(ProxyFactory targetFactory, Class targetType,ScopeContext targetScopeContext) throws BuilderConfigException {
        // delegate to other wire builders
        for (WireBuilder builder : builders) {
            builder.completeTargetChain(targetFactory, targetType, targetScopeContext);
        }
    }

}
