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
package org.apache.tuscany.container.javascript;

import java.util.Iterator;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.tuscany.container.javascript.rhino.RhinoSCAConfig;
import org.apache.tuscany.spi.idl.java.JavaServiceContract;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistry;
import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Introspects JavaScript files for SCA configuration
 * 
 */
public class JavaScriptIntrospector {

    private WSDLDefinitionRegistry wsdlRegistry;

    public JavaScriptIntrospector(@Autowire WSDLDefinitionRegistry wsdlRegistry) {
        this.wsdlRegistry = wsdlRegistry;
    }

    public JavaScriptComponentType introspectScript(RhinoSCAConfig scaConfig, ClassLoader cl) {
        JavaScriptComponentType componentType = new JavaScriptComponentType();
        introspectJavaInterface(componentType, cl, scaConfig.getJavaInterface());
        introspectWSDLInterface(componentType, cl, scaConfig.getWSDLNamespace(), scaConfig.getWSDLPortType(), scaConfig.getWSDLLocation());
        introspectReferences(componentType, cl, scaConfig.getReferences());
        introspectProperties(componentType, cl, scaConfig.getProperties());
        introspectScope(componentType, scaConfig.getScope());
        return componentType;
    }

    private void introspectScope(JavaScriptComponentType componentType, Scope scope) {
        if (scope != null) {
            componentType.setLifecycleScope(scope);
        }
    }

    @SuppressWarnings("unchecked")
    private void introspectJavaInterface(ComponentType componentType, ClassLoader cl, String serviceClass) {
        if (serviceClass != null) {
            ServiceDefinition service = new ServiceDefinition();
            ServiceContract sc = new JavaServiceContract();
            try {
                sc.setInterfaceClass(Class.forName(serviceClass.toString(), true, cl));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            service.setServiceContract(sc);
            componentType.add(service);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void introspectWSDLInterface(ComponentType componentType, ClassLoader cl, String wsdlNamespace, String wsdlPortType, String wsdlLocation) {
        if (wsdlNamespace == null && wsdlPortType == null && wsdlLocation == null) {
            return;
        }

        PortType portType = null;
        if (wsdlLocation != null) {
            portType = readWSDLPortType(wsdlNamespace, wsdlPortType, wsdlLocation, portType);
        } else {
            portType = getPortType(wsdlNamespace, wsdlPortType);
        }

        ServiceDefinition service = new ServiceDefinition();
        WSDLServiceContract wsdlSC = new WSDLServiceContract();
        wsdlSC.setPortType(portType);
        service.setServiceContract(wsdlSC);
        componentType.add(service);
    }

    private PortType readWSDLPortType(String wsdlNamespace, String wsdlPortType, String wsdlLocation, PortType portType) {
        Definition wsdlDefinition;
        try {
            WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
            reader.setFeature("javax.wsdl.verbose", false);
            wsdlDefinition = reader.readWSDL(wsdlLocation.toString());
        } catch (WSDLException e) {
            throw new RuntimeException(e);

        }
        Map portTypes = wsdlDefinition.getPortTypes();
        for (Iterator i = portTypes.keySet().iterator(); i.hasNext();) {
            QName portTypeQN = (QName) i.next();
            if (wsdlNamespace != null) {
                if (!portTypeQN.getNamespaceURI().equals(wsdlNamespace)) {
                    continue;
                }
            }
            if (wsdlPortType != null) {
                if (!portTypeQN.getLocalPart().equals(wsdlPortType)) {
                    continue;
                }
            }
            if (portType != null) {
                throw new RuntimeException("multiple matching portTypes in wsdl: " + wsdlLocation);
            }
            portType = (PortType) portTypes.get(portTypeQN);
        }
        if (portType == null) {
            throw new RuntimeException("portType not found in wsdl: " + wsdlLocation);
        }
        return portType;
    }

    private PortType getPortType(String wsdlNamespace, String wsdlPortType) {
        if (wsdlPortType == null) {
            throw new IllegalArgumentException("must specify the wsdlPortType in script SCA config");
        }
        PortType portType = null;
        if (wsdlNamespace != null) {
            QName portTypeQN = new QName(wsdlNamespace.toString(), wsdlPortType.toString());
            portType = wsdlRegistry.getPortType(portTypeQN);
            if (portType == null) {
                throw new IllegalArgumentException("no WSDL registered for portType: " + portTypeQN);
            }
        } else {
            // wsdlRegistry.getPortType(wsdlPortType.toString());
            if (portType == null) {
                throw new IllegalArgumentException("no WSDL registered for portType:" + wsdlPortType);
            }
        }
        return portType;
    }


    private void introspectProperties(ComponentType componentType, ClassLoader cl, Map properties) {
    }

    private void introspectReferences(ComponentType componentType, ClassLoader cl, Map references) {
    }

}
