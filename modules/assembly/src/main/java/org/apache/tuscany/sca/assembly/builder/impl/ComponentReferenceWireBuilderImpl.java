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

package org.apache.tuscany.sca.assembly.builder.impl;

import java.util.Map;
import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Binding;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.EndpointFactory;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilder;
import org.apache.tuscany.sca.assembly.builder.CompositeBuilderException;
import org.apache.tuscany.sca.interfacedef.InterfaceContractMapper;
import org.apache.tuscany.sca.monitor.Monitor;

/**
 * A composite builder that wires component references.
 *
 * @version $Rev$ $Date$
 */
public class ComponentReferenceWireBuilderImpl extends BaseWireBuilderImpl implements CompositeBuilder {

    public ComponentReferenceWireBuilderImpl(AssemblyFactory assemblyFactory, EndpointFactory endpointFactory,
                                             InterfaceContractMapper interfaceContractMapper, Monitor monitor,
                                             Map<Binding, Binding> bindingMap) {
        super(assemblyFactory, endpointFactory, interfaceContractMapper, monitor, bindingMap);
    }

    public void build(Composite composite) throws CompositeBuilderException {
        wireComponentReferences(composite);
    }
}
