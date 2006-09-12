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

package org.apache.tuscany.idl.wsdl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.wsdl.Fault;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.xml.namespace.QName;

import org.apache.tuscany.spi.idl.InvalidServiceContractException;
import org.apache.tuscany.spi.model.DataType;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaType;

/**
 * Metadata for a WSDL operation
 */
public class WSDLOperation {
    protected XMLSchemaRegistry schemaRegistry;

    protected Operation operation;

    private String defaultDataBinding;

    protected org.apache.tuscany.spi.model.Operation<QName> operationModel;

    protected DataType<List<DataType<QName>>> inputType;

    protected DataType<QName> outputType;

    protected List<DataType<QName>> faultTypes;

    /**
     * @param operation The WSDL4J operation
     * @param defaultDataBinding The default databinding
     * @param schemaRegistry The XML Schema registry
     */
    public WSDLOperation(Operation operation, String defaultDataBinding, XMLSchemaRegistry schemaRegistry) {
        super();
        this.operation = operation;
        this.defaultDataBinding = defaultDataBinding;
        this.schemaRegistry = schemaRegistry;
        this.wrapper = new Wrapper();
    }

    private Wrapper wrapper;

    private Boolean wrapperStyle;

    /**
     * Test if the operation qualifies wrapper style as defined by the JAX-WS 2.0 spec
     * 
     * @return true if the operation qualifies wrapper style, otherwise false
     */
    public boolean isWrapperStyle() {
        if (wrapperStyle == null) {
            wrapperStyle =
                    Boolean.valueOf(wrapper.getInputChildElements() != null
                            && (operation.getOutput() == null || wrapper.getOutputChildElements() != null));
        }
        return wrapperStyle.booleanValue();
    }

    public Wrapper getWrapper() {
        if (!isWrapperStyle()) {
            throw new IllegalStateException("The operation is not wrapper style.");
        } else {
            return wrapper;
        }
    }

    /**
     * @return
     * @throws InvalidServiceContractException 
     */
    public DataType<List<DataType<QName>>> getInputType() throws InvalidServiceContractException {
        if (inputType == null) {
            Input input = operation.getInput();
            Message message = (input == null) ? null : input.getMessage();
            inputType = getMessageType(message);
            inputType.setMetadata(WSDLOperation.class.getName(), this);
        }
        return inputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public DataType<QName> getOutputType() throws InvalidServiceContractException {
        if (outputType == null) {
            Output output = operation.getOutput();
            Message outputMsg = (output == null) ? null : output.getMessage();

            List outputParts = (outputMsg == null) ? null : outputMsg.getOrderedParts(null);
            if (outputParts != null || outputParts.size() > 0) {
                if (outputParts.size() > 1) {
                    // We don't support output with multiple parts
                    throw new NotSupportedWSDLException("Multi-part output is not supported");
                }
                Part part = (Part) outputParts.get(0);
                outputType = new WSDLPart(part).getDataType();
                outputType.setMetadata(WSDLOperation.class.getName(), this);
            }
        }
        return outputType;
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public List<DataType<QName>> getFaultTypes() throws InvalidServiceContractException {
        if (faultTypes == null) {
            Collection faults = operation.getFaults().values();
            faultTypes = new ArrayList<DataType<QName>>();
            for (Object f : faults) {
                Fault fault = (Fault) f;
                Message faultMsg = fault.getMessage();
                List faultParts = faultMsg.getOrderedParts(null);
                if (faultParts.size() != 1) {
                    throw new NotSupportedWSDLException("The fault message MUST have a single part");
                }
                Part part = (Part) faultParts.get(0);
                WSDLPart wsdlPart = new WSDLPart(part);
                faultTypes.add(wsdlPart.getDataType());
            }
        }
        return faultTypes;
    }

    private DataType<List<DataType<QName>>> getMessageType(Message message) throws InvalidServiceContractException {
        List<DataType<QName>> partTypes = new ArrayList<DataType<QName>>();
        if (message != null) {
            Collection parts = message.getOrderedParts(null);
            for (Object p : parts) {
                WSDLPart part = new WSDLPart((Part) p);
                partTypes.add(part.getDataType());
            }
        }
        return new DataType<List<DataType<QName>>>(defaultDataBinding, Object[].class, partTypes);
    }

    /**
     * @return
     * @throws NotSupportedWSDLException
     */
    public org.apache.tuscany.spi.model.Operation<QName> getOperation() throws InvalidServiceContractException {
        if (operationModel == null) {
            boolean oneway = (operation.getOutput() == null);
            operationModel =
                    new org.apache.tuscany.spi.model.Operation<QName>(operation.getName(), getInputType(),
                            getOutputType(), getFaultTypes(), oneway, defaultDataBinding);
            operationModel.addMetaData(WSDLOperation.class.getName(), this);
        }
        return operationModel;
    }

    /**
     * Metadata for a WSDL part
     */
    public class WSDLPart {
        private Part part;

        private XmlSchemaElement element;

        private DataType<QName> dataType;

        public WSDLPart(Part part) throws InvalidWSDLException {
            this.part = part;
            QName elementName = part.getElementName();
            if (elementName != null) {
                element = schemaRegistry.getElement(elementName);
                if (element == null) {
                    throw new InvalidWSDLException("Element cannot be resolved: " + elementName);
                }
            } else {
                // Create an faked XSD element to host the metadata
                element = new XmlSchemaElement();
                element.setName(part.getName());
                element.setQName(new QName(null, part.getName()));
                QName typeName = part.getTypeName();
                if (typeName != null) {
                    XmlSchemaType type = schemaRegistry.getType(typeName);
                    if (type == null) {
                        throw new InvalidWSDLException("Type cannot be resolved: " + typeName);
                    }
                    element.setSchemaType(type);
                    element.setSchemaTypeName(type.getQName());
                }
            }
            dataType = new DataType<QName>(defaultDataBinding, Object.class, element.getQName());
            dataType.setMetadata(WSDLPart.class.getName(), this);
        }

        /**
         * @return the element
         */
        public XmlSchemaElement getElement() {
            return element;
        }

        /**
         * @return the part
         */
        public Part getPart() {
            return part;
        }

        /**
         * @return the dataType
         */
        public DataType<QName> getDataType() {
            return dataType;
        }
    }

    /**
     * The "Wrapper Style" WSDL operation is defined by The Java API for XML-Based Web Services (JAX-WS) 2.0
     * specification, section 2.3.1.2 Wrapper Style.
     * <p>
     * A WSDL operation qualifies for wrapper style mapping only if the following criteria are met:
     * <ul>
     * <li>(i) The operation�s input and output messages (if present) each contain only a single part
     * <li>(ii) The input message part refers to a global element declaration whose localname is equal to the operation
     * name
     * <li>(iii) The output message part refers to a global element declaration
     * <li>(iv) The elements referred to by the input and output message parts (henceforth referred to as wrapper
     * elements) are both complex types defined using the xsd:sequence compositor
     * <li>(v) The wrapper elements only contain child elements, they must not contain other structures such as
     * wildcards (element or attribute), xsd:choice, substitution groups (element references are not permitted) or
     * attributes; furthermore, they must not be nillable.
     * </ul>
     */
    public class Wrapper {
        private XmlSchemaElement inputWrapperElement;

        private XmlSchemaElement outputWrapperElement;

        private List<XmlSchemaElement> inputElements;

        private List<XmlSchemaElement> outputElements;

        private DataType<List<DataType<QName>>> unwrappedInputType;

        private DataType<QName> unwrappedOutputType;

        private List<XmlSchemaElement> getChildElements(XmlSchemaElement element) {
            if (element == null) {
                return null;
            }
            XmlSchemaType type = element.getSchemaType();
            if (!(type instanceof XmlSchemaComplexType)) {
                // Has to be a complexType
                return null;
            }
            XmlSchemaComplexType complexType = (XmlSchemaComplexType) type;
            if (complexType.getAttributes().getCount() != 0 || complexType.getAnyAttribute() != null) {
                // No attributes
                return null;
            }
            XmlSchemaParticle particle = complexType.getParticle();
            if (particle == null) {
                // No particle
                return Collections.emptyList();
            }
            if (!(particle instanceof XmlSchemaSequence)) {
                return null;
            }
            XmlSchemaSequence sequence = (XmlSchemaSequence) complexType.getParticle();
            XmlSchemaObjectCollection items = sequence.getItems();
            List<XmlSchemaElement> childElements = new ArrayList<XmlSchemaElement>();
            for (int i = 0; i < items.getCount(); i++) {
                XmlSchemaObject schemaObject = items.getItem(i);
                if (!(schemaObject instanceof XmlSchemaElement)) {
                    return null;
                }
                XmlSchemaElement childElement = (XmlSchemaElement) schemaObject;
                if (childElement.getName() == null || childElement.getRefName() != null || childElement.isNillable()) {
                    return null;
                }
                // TODO: Do we support maxOccurs >1 ?
                if (childElement.getMaxOccurs() > 1) {
                    return null;
                }
                childElements.add(childElement);
            }
            return childElements;
        }

        /**
         * Return a list of child XSD elements under the wrapped request element
         * 
         * @return a list of child XSD elements or null if if the request element is not wrapped
         */
        public List<XmlSchemaElement> getInputChildElements() {
            if (inputElements != null) {
                return inputElements;
            }
            Input input = operation.getInput();
            if (input != null) {
                Message inputMsg = input.getMessage();
                Collection parts = inputMsg.getParts().values();
                if (parts.size() != 1) {
                    return null;
                }
                Part part = (Part) parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    return null;
                }
                if (!operation.getName().equals(elementName.getLocalPart())) {
                    return null;
                }
                inputWrapperElement = schemaRegistry.getElement(elementName);
                if (inputWrapperElement == null) {
                    return null;
                }
                inputElements = getChildElements(inputWrapperElement);
                return inputElements;
            } else {
                return null;
            }
        }

        /**
         * Return a list of child XSD elements under the wrapped response element
         * 
         * @return a list of child XSD elements or null if if the response element is not wrapped
         */
        public List<XmlSchemaElement> getOutputChildElements() {
            if (outputElements != null) {
                return outputElements;
            }
            Output output = operation.getOutput();
            if (output != null) {
                Message outputMsg = output.getMessage();
                Collection parts = outputMsg.getParts().values();
                if (parts.size() != 1) {
                    return null;
                }
                Part part = (Part) parts.iterator().next();
                QName elementName = part.getElementName();
                if (elementName == null) {
                    return null;
                }
                outputWrapperElement = schemaRegistry.getElement(elementName);
                if (outputWrapperElement == null) {
                    return null;
                }
                outputElements = getChildElements(outputWrapperElement);
                // FIXME: Do we support multiple child elements for the response?
                return outputElements;
            } else {
                return null;
            }
        }

        /**
         * @return the inputWrapperElement
         */
        public XmlSchemaElement getInputWrapperElement() {
            return inputWrapperElement;
        }

        /**
         * @return the outputWrapperElement
         */
        public XmlSchemaElement getOutputWrapperElement() {
            return outputWrapperElement;
        }

        public DataType<List<DataType<QName>>> getUnwrappedInputType() {
            if (unwrappedInputType == null) {
                List<DataType<QName>> types = new ArrayList<DataType<QName>>();
                for (XmlSchemaElement element : getInputChildElements()) {
                    DataType<QName> type = new DataType<QName>(defaultDataBinding, Object.class, element.getQName());
                    type.setMetadata(XmlSchemaElement.class.getName(), element);
                    types.add(type);
                }
                unwrappedInputType = new DataType<List<DataType<QName>>>("idl:unwrapped.input", Object[].class, types);
            }
            return unwrappedInputType;
        }

        public DataType<QName> getUnwrappedOutputType() throws InvalidServiceContractException {
            if (unwrappedOutputType == null) {
                List<XmlSchemaElement> elements = getOutputChildElements();
                if (elements != null || elements.size() > 0) {
                    if (elements.size() > 1) {
                        // We don't support output with multiple parts
                        throw new NotSupportedWSDLException("Multi-part output is not supported");
                    }
                    XmlSchemaElement element = elements.get(0);
                    unwrappedOutputType = new DataType<QName>(defaultDataBinding, Object.class, element.getQName());
                    unwrappedOutputType.setMetadata(XmlSchemaElement.class.getName(), element);
                }
            }
            return unwrappedOutputType;
        }
    }

}
