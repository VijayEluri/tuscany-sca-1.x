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

import junit.framework.Assert;

import org.apache.tuscany.spi.model.DataType;

import com.example.ipo.sdo.PurchaseOrderType;

import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * 
 */
public class XMLDocument2XMLStreamReaderTestCase extends SDOTransformerTestCaseBase {

    @Override
    protected DataType<?> getSourceDataType() {
        return new DataType<QName>(XMLDocument.class.getName(), XMLDocument.class, orderQName);
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataType<Class<XMLStreamReader>>(XMLStreamReader.class, XMLStreamReader.class);
    }

    public final void testTransform() throws XMLStreamException {
        XMLDocument document =
                XMLHelper.INSTANCE.createDocument(dataObject, orderQName.getNamespaceURI(), orderQName.getLocalPart());
        XMLStreamReader reader = new XMLDocument2XMLStreamReader().transform(document, context);
        XMLDocument document2 = new XMLStreamReader2XMLDocument().transform(reader, reversedContext);
        Assert.assertEquals(orderQName.getNamespaceURI(), document2.getRootElementURI());
        Assert.assertEquals(orderQName.getLocalPart(), document2.getRootElementName());
        Assert.assertTrue(document2.getRootObject() instanceof PurchaseOrderType);
    }

}
