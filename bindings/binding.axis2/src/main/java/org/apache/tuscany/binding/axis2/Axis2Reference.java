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
package org.apache.tuscany.binding.axis2;


import java.lang.reflect.Method;
import java.util.List;
import javax.wsdl.Definition;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.extension.ReferenceExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

import commonj.sdo.helper.TypeHelper;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.tuscany.binding.axis2.util.SDODataBinding;
import org.apache.tuscany.binding.axis2.util.TuscanyAxisConfigurator;
import org.apache.tuscany.binding.axis2.util.WebServiceOperationMetaData;
import org.apache.tuscany.binding.axis2.util.WebServicePortMetaData;
import org.apache.tuscany.idl.wsdl.WSDLOperation;


/**
 * Axis2Reference uses Axis2 to invoke a remote web service
 */
public class Axis2Reference<T> extends ReferenceExtension {
    private static final String OM_DATA_BINDING = OMElement.class.getName();

    private WebServicePortMetaData wsPortMetaData;
    private ServiceClient serviceClient;
    private TypeHelper typeHelper;
    private WorkContext workContext;

    @SuppressWarnings("unchecked")
    public Axis2Reference(String theName,
                          CompositeComponent parent,
                          WireService wireService,
                          WebServiceBinding wsBinding,
                          ServiceContract contract,
                          TypeHelper typeHelper,
                          WorkContext workContext) {
        super(theName, (Class<T>) contract.getInterfaceClass(), parent, wireService);
        try {
            Definition wsdlDefinition = wsBinding.getWSDLDefinition();
            wsPortMetaData =
                new WebServicePortMetaData(wsdlDefinition, wsBinding.getWSDLPort(), wsBinding.getURI(), false);
            serviceClient = createServiceClient(wsdlDefinition, wsPortMetaData);
            this.typeHelper = typeHelper;
            this.workContext = workContext;
        } catch (AxisFault e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public TargetInvoker createTargetInvoker(ServiceContract contract, Operation operation) {
        Axis2TargetInvoker invoker;
        try {
            //FIXME: SDODataBinding needs to pass in TypeHelper and classLoader as parameters.
            invoker = createOperationInvoker(serviceClient, operation, typeHelper, wsPortMetaData, false);
            // HACK to set the databinding
            operation.setDataBinding(OM_DATA_BINDING);
            WSDLOperation op = (WSDLOperation) operation.getMetaData().get(WSDLOperation.class.getName());
            if (op != null) {
                op.setDataBinding(OM_DATA_BINDING);
            }
        } catch (AxisFault e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return invoker;
    }

    public TargetInvoker createAsyncTargetInvoker(OutboundWire wire, Operation operation) {
        Axis2AsyncTargetInvoker invoker;
        try {
            //FIXME: SDODataBinding needs to pass in TypeHelper and classLoader as parameters.
            invoker =
                (Axis2AsyncTargetInvoker)createOperationInvoker(serviceClient, operation, typeHelper, wsPortMetaData, true);
            //FIXME: This makes the (BIG) assumption that there is only one callback method
            // Relaxing this assumption, however, does not seem to be trivial, it may depend on knowledge
            // of what actual callback method was invoked by the service at the other end
            Method callbackMethod = findCallbackMethod();
            Axis2ReferenceCallbackTargetInvoker callbackInvoker =
                new Axis2ReferenceCallbackTargetInvoker(callbackMethod,
                        inboundWire.getServiceContract(),
                        inboundWire,
                        wireService,
                        workContext);
            invoker.setCallbackTargetInvoker(callbackInvoker);
        } catch (AxisFault e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return invoker;
    }
    
    private Method findCallbackMethod() {
        return inboundWire.getServiceContract().getCallbackClass().getDeclaredMethods()[0];
    }

    /**
     * Create an Axis2 ServiceClient
     */
    private ServiceClient createServiceClient(Definition wsdlDefinition,
                                              WebServicePortMetaData wsPortMetaData) throws AxisFault {

        TuscanyAxisConfigurator tuscanyAxisConfigurator = new TuscanyAxisConfigurator();
        ConfigurationContext configurationContext = tuscanyAxisConfigurator.getConfigurationContext();
        QName serviceQName = wsPortMetaData.getServiceName();
        String portName = wsPortMetaData.getPortName().getLocalPart();
        AxisService axisService =
            AxisService.createClientSideAxisService(wsdlDefinition, serviceQName, portName, new Options());
        return new ServiceClient(configurationContext, axisService);
    }

    /**
     * Create and configure an Axis2TargetInvoker for each operations
     */
    private Axis2TargetInvoker createOperationInvoker(ServiceClient serviceClient,
                                                      Operation m,
                                                      TypeHelper typeHelper,
                                                      WebServicePortMetaData wsPortMetaData,
                                                      boolean isAsync)
        throws AxisFault {
        SOAPFactory soapFactory = OMAbstractFactory.getSOAP11Factory();
        String portTypeNS = wsPortMetaData.getPortTypeName().getNamespaceURI();

        String methodName = m.getName();

        WebServiceOperationMetaData operationMetaData = wsPortMetaData.getOperationMetaData(methodName);
        boolean isWrapped = operationMetaData.isDocLitWrapped();
        List<?> sig = operationMetaData.getOperationSignature();

        SDODataBinding dataBinding =
            new SDODataBinding(typeHelper, isWrapped, sig.size() > 0 ? (QName) sig.get(0) : null);

        Options options = new Options();
        options.setTo(new EndpointReference(wsPortMetaData.getEndpoint()));
        options.setProperty(HTTPConstants.CHUNKED, Boolean.FALSE);

        String wsdlOperationName = operationMetaData.getBindingOperation().getOperation().getName();

        String soapAction = wsPortMetaData.getOperationMetaData(wsdlOperationName).getSOAPAction();
        if (soapAction != null && soapAction.length() > 1) {
            options.setAction(soapAction);
        }

        QName wsdlOperationQName = new QName(portTypeNS, wsdlOperationName);

        Axis2TargetInvoker invoker;
        if (isAsync) {
            invoker = new Axis2AsyncTargetInvoker(serviceClient, wsdlOperationQName, options, dataBinding, soapFactory, inboundWire);
        } else {
            invoker = new Axis2TargetInvoker(serviceClient, wsdlOperationQName, options, dataBinding, soapFactory);
        }
        
        return invoker;
    }

}
