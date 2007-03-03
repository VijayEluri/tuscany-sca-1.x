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
package org.apache.tuscany.core.marshaller.java;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.core.marshaller.AbstractPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;

/**
 * Marshaller for Java physical component definitions.
 * 
 * @version $Revision$ $Date$
 */
public class JavaPhysicalComponentDefinitionMarshaller extends AbstractPhysicalComponentDefinitionMarshaller<JavaPhysicalComponentDefinition> {

    // Instance factory
    private static final String INSTANCE_FACTORY = "instanceFactory";

    // QName for the root element
    private static final QName QNAME = new QName("http://tuscany.apache.org/xmlns/marshaller/component/java/1.0-SNAPSHOT", "component");

    /**
     * Gets the qualified name of the XML fragment for the marshalled model object.
     * @return {"http://tuscany.apache.org/xmlns/marshaller/component/java/1.0-SNAPSHOT", "component"}
     */
    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    /**
     * Retursn the type of the model object.
     * @return <code>JavaPhysicalComponentDefinition.class</code>.
     */
    @Override
    protected Class<JavaPhysicalComponentDefinition> getModelObjectType() {
        return JavaPhysicalComponentDefinition.class;
    }
    
    /**
     * Create the concrete PCD.
     * @return An instance of<code>JavaPhysicalComponentDefinition</code>.
     */
    @Override
    protected JavaPhysicalComponentDefinition getConcreteModelObject() {
        return new JavaPhysicalComponentDefinition();
    }
    
    /**
     * Handles extensions for unmarshalling Java physical component definitions 
     * including the marshalling of base64 encoded instance factory byte code.
     * 
     * @param componentDefinition Physical component definition.
     * @param reader Reader from which marshalled data is read.
     */
    @Override
    protected void handleExtensions(JavaPhysicalComponentDefinition componentDefinition, XMLStreamReader reader) {
        String name = reader.getName().getLocalPart();
        if(INSTANCE_FACTORY.equals(name)) {
            byte[] base64ByteCode = reader.getText().getBytes();
            byte[] byteCode = Base64.decodeBase64(base64ByteCode);
            componentDefinition.setInstanceFactoryByteCode(byteCode);
        }
    }
    
    /**
     * Handles extensions for marshalling Java physical component definitions 
     * including the marshalling of base64 encoded instance factory byte code.
     * 
     * @param componentDefinition Physical component definition.
     * @param reader Writer to which marshalled data is written.
     */
    @Override
    protected void handleExtensions(JavaPhysicalComponentDefinition componentDefinition, XMLStreamWriter writer) {
    }

}
