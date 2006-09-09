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
package org.apache.tuscany.databinding.sdo;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.extension.TransformerExtension;
import org.apache.tuscany.sdo.helper.XMLStreamHelper;
import org.apache.tuscany.sdo.util.SDOUtil;

import commonj.sdo.DataObject;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

public class DataObject2XMLStreamReader extends TransformerExtension<DataObject, XMLStreamReader> implements
        PullTransformer<DataObject, XMLStreamReader> {

    public XMLStreamReader transform(DataObject source, TransformationContext context) {
        try {
            TypeHelper typeHelper = SDODataTypeHelper.getTypeHelper(context, true);
            XMLStreamHelper streamHelper = SDOUtil.createXMLStreamHelper(typeHelper);
            Object logicalType = context.getSourceDataType().getLogical();
            if (logicalType instanceof QName) {
                QName elementName = (QName) logicalType;
                XMLHelper xmlHelper = SDOUtil.createXMLHelper(typeHelper);
                XMLDocument document = xmlHelper.createDocument(source, elementName.getNamespaceURI(), elementName.getLocalPart());
                return streamHelper.createXMLStreamReader(document);
            } else {
                return streamHelper.createXMLStreamReader(source);
            }
        } catch (XMLStreamException e) {
            // TODO: Add context to the exception
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return DataObject.class;
    }

    public Class getTargetType() {
        return XMLStreamReader.class;
    }

    public int getWeight() {
        return 10;
    }

}
