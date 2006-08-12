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
package org.apache.tuscany.databinding.impl;

import java.io.StringWriter;
import java.io.Writer;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.databinding.trax.Node2String;
import org.apache.tuscany.databinding.trax.Node2Writer;
import org.apache.tuscany.databinding.trax.String2SAX;
import org.apache.tuscany.databinding.xml.SAX2DOMPipe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 */
public class MediatorImplTestCase extends TestCase {
    private static final String IPO_XML = "<?xml version=\"1.0\"?>" + "<ipo:purchaseOrder"
            + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + "  xmlns:ipo=\"http://www.example.com/IPO\""
            + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\"" + "  orderDate=\"1999-12-01\">"
            + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">" + "    <name>Helen Zoe</name>" + "    <street>47 Eden Street</street>"
            + "    <city>Cambridge</city>" + "    <postcode>CB1 1JR</postcode>" + "  </shipTo>" + "  <billTo xsi:type=\"ipo:USAddress\">"
            + "    <name>Robert Smith</name>" + "    <street>8 Oak Avenue</street>" + "    <city>Old Town</city>" + "    <state>PA</state>"
            + "    <zip>95819</zip>" + "  </billTo>" + "  <items>" + "    <item partNum=\"833-AA\">"
            + "      <productName>Lapis necklace</productName>" + "      <quantity>1</quantity>" + "      <USPrice>99.95</USPrice>"
            + "      <ipo:comment>Want this for the holidays</ipo:comment>" + "      <shipDate>1999-12-05</shipDate>" + "    </item>" + "  </items>"
            + "</ipo:purchaseOrder>";

    private MediatorImpl mediator;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        TransformerRegistry registry = new TransformerRegistryImpl();
        registry.registerTransformer(new String2SAX());
        registry.registerTransformer(new SAX2DOMPipe());
        registry.registerTransformer(new Node2String());
        registry.registerTransformer(new Node2Writer());

        mediator = new MediatorImpl();
        mediator.setRegistry(registry);
    }

    public void testTransform1() {
        Object node = mediator.mediate(IPO_XML, String.class, Node.class, null);
        Assert.assertTrue(node instanceof Document);
        Element root = ((Document) node).getDocumentElement();
        Assert.assertEquals(root.getNamespaceURI(), "http://www.example.com/IPO");
        Assert.assertEquals(root.getLocalName(), "purchaseOrder");
    }

    public void testTransform2() {
        Writer writer = new StringWriter();
        mediator.mediate(IPO_XML, writer, String.class, Writer.class, null);
        String str = writer.toString();
        Assert.assertTrue(str != null && str.indexOf("<shipDate>1999-12-05</shipDate>") != -1);
    }

}
