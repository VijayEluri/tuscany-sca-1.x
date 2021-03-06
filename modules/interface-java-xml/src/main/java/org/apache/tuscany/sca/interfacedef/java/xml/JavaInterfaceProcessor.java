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

package org.apache.tuscany.sca.interfacedef.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.Extension;
import org.apache.tuscany.sca.assembly.ExtensionFactory;
import org.apache.tuscany.sca.assembly.xml.Constants;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXAttributeProcessor;
import org.apache.tuscany.sca.contribution.resolver.ClassReference;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.InvalidInterfaceException;
import org.apache.tuscany.sca.interfacedef.java.JavaInterface;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceContract;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.monitor.impl.ProblemImpl;

/**
 *
 * @version $Rev$ $Date$
 */
public class JavaInterfaceProcessor implements StAXArtifactProcessor<JavaInterfaceContract>, JavaConstants {

    private JavaInterfaceFactory javaFactory;
    private ExtensionFactory extensionFactory;
    private StAXAttributeProcessor<Object> extensionAttributeProcessor;
    private Monitor monitor;

    public JavaInterfaceProcessor(ModelFactoryExtensionPoint modelFactories, 
                                  StAXArtifactProcessor extensionProcessor,
                                  StAXAttributeProcessor extensionAttributeProcessor,
                                  Monitor monitor) {
        this.javaFactory = modelFactories.getFactory(JavaInterfaceFactory.class);
        this.extensionFactory = modelFactories.getFactory(ExtensionFactory.class);
        this.extensionAttributeProcessor = extensionAttributeProcessor;
        this.monitor = monitor;
    }
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
     */
    private void error(String message, Object model, Exception ex) {
    	 if (monitor != null) {
    		 Problem problem = new ProblemImpl(this.getClass().getName(), "interface-javaxml-validation-messages", Severity.ERROR, model, message, ex);
    	     monitor.problem(problem);
    	 }        
     }
     
     /**
      * Report a error.
      * 
      * @param problems
      * @param message
      * @param model
      */
     private void error(String message, Object model, Object... messageParameters) {
     	 if (monitor != null) {
     		 Problem problem = new ProblemImpl(this.getClass().getName(), "interface-javaxml-validation-messages", Severity.ERROR, model, message,(Object[])messageParameters);
     	     monitor.problem(problem);
     	 }        
     }
    
    private JavaInterface createJavaInterface(String interfaceName) {
        JavaInterface javaInterface = javaFactory.createJavaInterface();
        javaInterface.setUnresolved(true);
        javaInterface.setName(interfaceName);
        return javaInterface;
    }

    public JavaInterfaceContract read(XMLStreamReader reader) throws ContributionReadException, XMLStreamException {
        
        // Read an <interface.java>
        JavaInterfaceContract javaInterfaceContract = javaFactory.createJavaInterfaceContract();
        String interfaceName = reader.getAttributeValue(null, INTERFACE);
        if (interfaceName != null) {
            JavaInterface javaInterface = createJavaInterface(interfaceName);
            javaInterfaceContract.setInterface(javaInterface);
        }

        String callbackInterfaceName = reader.getAttributeValue(null, CALLBACK_INTERFACE);
        if (callbackInterfaceName != null) {
            JavaInterface javaCallbackInterface = createJavaInterface(callbackInterfaceName);
            javaInterfaceContract.setCallbackInterface(javaCallbackInterface);
        }

        // Handle extended attributes
        for (int a = 0; a < reader.getAttributeCount(); a++) {
            QName attributeName = reader.getAttributeName(a);
            if( attributeName.getNamespaceURI() != null && attributeName.getNamespaceURI().length() > 0) {
                if( (! Constants.SCA10_NS.equals(attributeName.getNamespaceURI()) && 
                    (! Constants.SCA10_TUSCANY_NS.equals(attributeName.getNamespaceURI()) ))) {
                    Object attributeValue = extensionAttributeProcessor.read(attributeName, reader);
                    Extension attributeExtension;
                    if (attributeValue instanceof Extension) {
                        attributeExtension = (Extension) attributeValue;
                    } else {
                        attributeExtension = extensionFactory.createExtension(attributeName, attributeValue, true);
                    }
                    javaInterfaceContract.getAttributeExtensions().add(attributeExtension);
                }
            }
        }        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && INTERFACE_JAVA_QNAME.equals(reader.getName())) {
                break;
            }
        }
        return javaInterfaceContract;
    }
    
    public void write(JavaInterfaceContract javaInterfaceContract, XMLStreamWriter writer) throws ContributionWriteException, XMLStreamException {
        
        // Write an <interface.java>
        writer.writeStartElement(Constants.SCA10_NS, INTERFACE_JAVA);
        JavaInterface javaInterface = (JavaInterface)javaInterfaceContract.getInterface();
        
        if (javaInterface != null && javaInterface.getName() != null) {
            writer.writeAttribute(INTERFACE, javaInterface.getName());
        }
        
        JavaInterface javaCallbackInterface = (JavaInterface)javaInterfaceContract.getCallbackInterface();
        if (javaCallbackInterface != null && javaCallbackInterface.getName() != null) {
            writer.writeAttribute(CALLBACK_INTERFACE, javaCallbackInterface.getName());
        }

        // Write extended attributes
        for(Extension extension : javaInterfaceContract.getAttributeExtensions()) {
            if(extension.isAttribute()) {
                extensionAttributeProcessor.write(extension, writer);
            }
        } 
        
        writer.writeEndElement();
    }
    
    private JavaInterface resolveJavaInterface(JavaInterface javaInterface, ModelResolver resolver) throws ContributionResolveException {
        
        if (javaInterface != null && javaInterface.isUnresolved()) {

            // Resolve the Java interface
            javaInterface = resolver.resolveModel(JavaInterface.class, javaInterface);
            if (javaInterface.isUnresolved()) {

                // If the Java interface has never been resolved yet, do it now
                ClassReference classReference = new ClassReference(javaInterface.getName());
                classReference = resolver.resolveModel(ClassReference.class, classReference);
                Class javaClass = classReference.getJavaClass();
                if (javaClass == null) {
                    error("ClassNotFoundException", resolver, javaInterface.getName());
                    return javaInterface;
                    //throw new ContributionResolveException(new ClassNotFoundException(javaInterface.getName()));
                }
                
                try {                        
                    // Introspect the Java interface and populate the interface and
                    // operations
                    javaFactory.createJavaInterface(javaInterface, javaClass);
                
                } catch (InvalidInterfaceException e) {
                	ContributionResolveException ce = new ContributionResolveException(e);
                	error("ContributionResolveException", javaFactory, ce);
                    return javaInterface;
                	//throw ce;
                }

                // Cache the resolved interface
                javaInterface.setUnresolved(false);
                resolver.addModel(javaInterface);
            }
        }
        return javaInterface;
    }
    
    public void resolve(JavaInterfaceContract javaInterfaceContract, ModelResolver resolver) throws ContributionResolveException {
        
        // Resolve the interface and callback interface
        JavaInterface javaInterface = resolveJavaInterface((JavaInterface)javaInterfaceContract.getInterface(), resolver);
        javaInterfaceContract.setInterface(javaInterface);
        
        JavaInterface javaCallbackInterface = resolveJavaInterface((JavaInterface)javaInterfaceContract.getCallbackInterface(), resolver);
        javaInterfaceContract.setCallbackInterface(javaCallbackInterface);
    }
    
    public QName getArtifactType() {
        return INTERFACE_JAVA_QNAME;
    }
    
    public Class<JavaInterfaceContract> getModelType() {
        return JavaInterfaceContract.class;
    }
}
