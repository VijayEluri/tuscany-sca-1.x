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

package org.apache.tuscany.sca.implementation.java.impl;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModuleActivator;
import org.apache.tuscany.sca.implementation.java.JavaImplementationFactory;
import org.apache.tuscany.sca.implementation.java.introspect.impl.JSR250PolicyProcessor;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A module activator for the Java implementation model.
 *
 * @version $Rev$ $Date$
 */
public class JSR250Activator implements ModuleActivator {

    public void start(ExtensionPointRegistry registry) {
        ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
        
        JavaImplementationFactory javaImplementationFactory = modelFactories.getFactory(JavaImplementationFactory.class);
        javaImplementationFactory.addClassVisitor(new JSR250PolicyProcessor(assemblyFactory, policyFactory));
        
    }

    public void stop(ExtensionPointRegistry registry) {
        
    }

}
