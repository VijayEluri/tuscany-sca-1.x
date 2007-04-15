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

package org.apache.tuscany.interfacedef.java.module;

import java.util.Map;

import org.apache.tuscany.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.interfacedef.java.xml.JavaInterfaceProcessor;
import org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry;
import org.apache.tuscany.spi.bootstrap.ModuleActivator;

/**
 * @version $Rev$ $Date$
 */
public class JavaInterfaceRuntimeModuleActivator implements ModuleActivator {

    public Map<Class, Object> getExtensionPoints() {
        return null;
    }

    /**
     * @see org.apache.tuscany.spi.bootstrap.ModuleActivator#start(org.apache.tuscany.spi.bootstrap.ExtensionPointRegistry)
     */
    public void start(ExtensionPointRegistry extensionPointRegistry) {
        
        // Register <interface.wsdl> processor
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPointRegistry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessors.addExtension(new JavaInterfaceProcessor());
        
    }

    public void stop(ExtensionPointRegistry registry) {
    }

}
