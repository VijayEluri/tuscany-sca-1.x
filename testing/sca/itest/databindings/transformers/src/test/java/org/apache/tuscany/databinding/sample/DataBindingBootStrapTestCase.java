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
package org.apache.tuscany.databinding.sample;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import junit.framework.TestCase;

import org.apache.tuscany.api.SCARuntime;
import org.apache.tuscany.databinding.jaxb.JAXBContextHelper;
import org.apache.tuscany.databinding.jaxb.Reader2JAXB;
import org.apache.tuscany.databinding.sdo.String2DataObject;
import org.apache.tuscany.databinding.xmlbeans.XMLStreamReader2XmlObject;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.idl.XMLType;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.CompositeContext;
import org.osoa.sca.CurrentCompositeContext;

import com.example.ipo.jaxb.PurchaseOrderType;
import com.example.ipo.xmlbeans.PurchaseOrderDocument;
import commonj.sdo.DataObject;

/**
 * @version $Rev$ $Date$
 */
public class DataBindingBootStrapTestCase extends TestCase {
    private static final String IPO_XML =
        "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
            + "  xmlns:ipo=\"http://www.example.com/IPO\""
            + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\""
            + "  orderDate=\"1999-12-01\">"
            + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">"
            + "    <name>Helen Zoe</name>"
            + "    <street>47 Eden Street</street>"
            + "    <city>Cambridge</city>"
            + "    <postcode>CB1 1JR</postcode>"
            + "  </shipTo>"
            + "  <billTo xsi:type=\"ipo:USAddress\">"
            + "    <name>Robert Smith</name>"
            + "    <street>8 Oak Avenue</street>"
            + "    <city>Old Town</city>"
            + "    <state>PA</state>"
            + "    <zip>95819</zip>"
            + "  </billTo>"
            + "  <items>"
            + "    <item partNum=\"833-AA\">"
            + "      <productName>Lapis necklace</productName>"
            + "      <quantity>1</quantity>"
            + "      <USPrice>99.95</USPrice>"
            + "      <ipo:comment>Want this for the holidays</ipo:comment>"
            + "      <shipDate>1999-12-05</shipDate>"
            + "    </item>"
            + "  </items>"
            + "</ipo:purchaseOrder>";

    private Client client;
    private String contextPath = "com.example.ipo.jaxb";

    @SuppressWarnings("unchecked")
    public void testDataBindingBootstrap() throws Exception {
        DataType targetDataType = new DataType<XMLType>(Object.class, XMLType.UNKNOWN);
        targetDataType.setMetadata(JAXBContextHelper.JAXB_CONTEXT_PATH, contextPath);
        TransformationContext tContext = createMock(TransformationContext.class);
        expect(tContext.getTargetDataType()).andReturn(targetDataType).anyTimes();
        replay(tContext);

        String2DataObject t1 = new String2DataObject();
        DataObject po1 = t1.transform(IPO_XML, null);
        client.call(po1);

        PurchaseOrderDocument.Factory.newInstance();

        XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(IPO_XML));
        XMLStreamReader2XmlObject t2 = new XMLStreamReader2XmlObject();
        PurchaseOrderDocument po2 = (PurchaseOrderDocument)t2.transform(reader, null);
        client.call(po2.getPurchaseOrder());

        QName root = new QName("http://www.example.com/IPO", "purchaseOrder");
        DataType targetDataType1 = new DataType<XMLType>(PurchaseOrderType.class, new XMLType(root, null));
        TransformationContext tContext1 = createMock(TransformationContext.class);
        expect(tContext1.getTargetDataType()).andReturn(targetDataType1).anyTimes();
        replay(tContext1);
        Reader2JAXB t3 = new Reader2JAXB();
        PurchaseOrderType po3 = (PurchaseOrderType)t3.transform(new StringReader(IPO_XML), tContext1);
        client.call(po3);

    }

    protected void setUp() throws Exception {
    	SCARuntime.start("DataBindingTest.composite");
        CompositeContext context = CurrentCompositeContext.getContext();
        client = context.locateService(Client.class, "Client");
    }
    
    protected void tearDown() throws Exception {
    	SCARuntime.stop();
    }
}
