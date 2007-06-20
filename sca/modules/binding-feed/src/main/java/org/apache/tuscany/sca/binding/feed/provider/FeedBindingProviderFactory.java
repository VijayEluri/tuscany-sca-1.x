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

package org.apache.tuscany.sca.binding.feed.provider;

import org.apache.tuscany.sca.binding.feed.FeedBinding;
import org.apache.tuscany.sca.http.ServletHost;
import org.apache.tuscany.sca.provider.BindingProviderFactory;
import org.apache.tuscany.sca.provider.ReferenceBindingProvider;
import org.apache.tuscany.sca.provider.ServiceBindingProvider;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Implementation of the Feed binding provider factory.
 */
public class FeedBindingProviderFactory implements BindingProviderFactory<FeedBinding> {

    ServletHost servletHost;

    public FeedBindingProviderFactory(ServletHost servletHost) {
        this.servletHost = servletHost;
    }

    public ReferenceBindingProvider createReferenceBindingProvider(RuntimeComponent component,
                                                                   RuntimeComponentReference reference,
                                                                   FeedBinding binding) {
        return new FeedReferenceBindingProvider(component, reference, binding);
    }

    public ServiceBindingProvider createServiceBindingProvider(RuntimeComponent component,
                                                               RuntimeComponentService service,
                                                               FeedBinding binding) {
        return new FeedServiceBindingProvider(component, service, binding, servletHost);
    }

    public Class<FeedBinding> getModelType() {
        return FeedBinding.class;
    }
}
