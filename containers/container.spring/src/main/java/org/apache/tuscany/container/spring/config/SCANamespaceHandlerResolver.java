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
package org.apache.tuscany.container.spring.config;

import org.apache.tuscany.spi.model.CompositeComponentType;

import org.springframework.beans.factory.xml.DefaultNamespaceHandlerResolver;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.sca.config.ScaNamespaceHandler;

/**
 * Overrides the default Spring namespace resolver to automatically register {@link SCANamespaceHandler} instead of
 * requiring a value to be supplied in a Spring configuration
 * <p/>
 * TODO: Figure out how to activate this impl
 *
 * @version $$Rev$$ $$Date$$
 */
public class SCANamespaceHandlerResolver extends DefaultNamespaceHandlerResolver {

    private static final String SCA_NAMESPACE = "http://www.springframework.org/schema/sca";

    private ScaNamespaceHandler handler;

    public SCANamespaceHandlerResolver(ClassLoader classLoader, CompositeComponentType componentType) {
        super(classLoader);
        handler = new ScaNamespaceHandler(/*componentType*/);
    }

    public SCANamespaceHandlerResolver(String handlerMappingsLocation,
                                       ClassLoader classLoader,
                                       CompositeComponentType componentType) {
        super(classLoader, handlerMappingsLocation);
        handler = new ScaNamespaceHandler(/*componentType*/);
    }

    public NamespaceHandler resolve(String namespaceUri) {
        if (SCA_NAMESPACE.equals(namespaceUri)) {
            return handler;
        }
        return super.resolve(namespaceUri);
    }
}
