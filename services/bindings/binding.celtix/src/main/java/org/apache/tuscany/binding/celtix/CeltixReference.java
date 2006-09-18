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
package org.apache.tuscany.binding.celtix;

import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import commonj.sdo.helper.TypeHelper;
import org.objectweb.celtix.Bus;

/**
 * The implementation of a {@link org.apache.tuscany.spi.component.Reference} configured with the Celtix binding
 *
 * @version $Rev$ $Date$
 */
public class CeltixReference<T> extends ReferenceExtension {

    private Bus bus;
    private Port port;
    private Definition wsdlDef;
    private Service wsdlService;
    private TypeHelper typeHelper;

    public CeltixReference(String name,
                           Class<T> interfaze,
                           CompositeComponent parent,
                           WireService wireService,
                           WebServiceBinding binding,
                           Bus theBus,
                           TypeHelper theTypeHelper) {
        super(name, interfaze, parent, wireService);
        this.wsdlDef = binding.getWSDLDefinition();
        this.port = binding.getWSDLPort();
        this.wsdlService = binding.getWSDLService();
        this.bus = theBus;
        this.typeHelper = theTypeHelper;
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        return new CeltixInvoker(operation.getName(), bus, port, wsdlService, wsdlDef, typeHelper);
    }
}
