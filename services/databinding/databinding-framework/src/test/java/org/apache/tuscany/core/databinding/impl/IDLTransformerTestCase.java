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
package org.apache.tuscany.core.databinding.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.tuscany.core.databinding.impl.DataBindingRegistryImpl;
import org.apache.tuscany.core.databinding.impl.Input2InputTransformer;
import org.apache.tuscany.core.databinding.impl.MediatorImpl;
import org.apache.tuscany.core.databinding.impl.Output2OutputTransformer;
import org.apache.tuscany.core.databinding.impl.TransformationContextImpl;
import org.apache.tuscany.core.databinding.impl.TransformerRegistryImpl;
import org.apache.tuscany.core.databinding.xml.DOMDataBinding;
import org.apache.tuscany.core.databinding.xml.Node2Object;
import org.apache.tuscany.core.databinding.xml.Node2String;
import org.apache.tuscany.core.databinding.xml.Object2Node;
import org.apache.tuscany.core.databinding.xml.String2Node;
import org.apache.tuscany.idl.wsdl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.idl.wsdl.WSDLOperation;
import org.apache.tuscany.idl.wsdl.XMLSchemaRegistryImpl;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.model.DataType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class IDLTransformerTestCase extends TestCase {
    private static final String IPO_XML =
            "<?xml version=\"1.0\"?>" + "<order1" + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + "  xmlns:ipo=\"http://www.example.com/IPO\""
                    + "  xsi:schemaLocation=\"http://www.example.com/IPO ipo.xsd\"" + "  orderDate=\"1999-12-01\">"
                    + "  <shipTo exportCode=\"1\" xsi:type=\"ipo:UKAddress\">" + "    <name>Helen Zoe</name>"
                    + "    <street>47 Eden Street</street>" + "    <city>Cambridge</city>"
                    + "    <postcode>CB1 1JR</postcode>" + "  </shipTo>" + "  <billTo xsi:type=\"ipo:USAddress\">"
                    + "    <name>Robert Smith</name>" + "    <street>8 Oak Avenue</street>"
                    + "    <city>Old Town</city>" + "    <state>PA</state>" + "    <zip>95819</zip>" + "  </billTo>"
                    + "  <items>" + "    <item partNum=\"833-AA\">" + "      <productName>Lapis necklace</productName>"
                    + "      <quantity>1</quantity>" + "      <USPrice>99.95</USPrice>"
                    + "      <ipo:comment>Want this for the holidays</ipo:comment>"
                    + "      <shipDate>1999-12-05</shipDate>" + "    </item>" + "  </items>" + "</order1>";

    private static final QName PORTTYPE_NAME = new QName("http://example.com/order.wsdl", "OrderPortType");

    private WSDLDefinitionRegistryImpl registry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        registry = new WSDLDefinitionRegistryImpl();
        registry.setSchemaRegistry(new XMLSchemaRegistryImpl());
    }

    public void testTransform() throws Exception {
        URL url = getClass().getResource("order.wsdl");
        Definition definition = registry.loadDefinition(null, url);
        PortType portType = definition.getPortType(PORTTYPE_NAME);
        Operation operation = portType.getOperation("checkOrderStatus", null, null);
        WSDLOperation wsdlOp = new WSDLOperation(operation, Node.class.getName(), registry.getSchemaRegistry());
        org.apache.tuscany.spi.model.Operation<?> op = wsdlOp.getOperation();
        assertTrue(op.isWrapperStyle());

        MediatorImpl m = new MediatorImpl();
        TransformerRegistryImpl tr = new TransformerRegistryImpl();
        tr.registerTransformer(new String2Node());
        tr.registerTransformer(new Node2String());
        tr.registerTransformer(new Node2Object());
        tr.registerTransformer(new Object2Node());
        m.setTransformerRegistry(tr);
        DataBindingRegistry dataBindingRegistry = new DataBindingRegistryImpl();
        dataBindingRegistry.register(new DOMDataBinding());
        m.setDataBindingRegistry(dataBindingRegistry);

        Object[] source = new Object[] { "cust001", IPO_XML, Integer.valueOf(1) };
        Input2InputTransformer t = new Input2InputTransformer();
        t.setDataBindingRegistry(dataBindingRegistry);
        t.setMediator(m);

        TransformationContext context = new TransformationContextImpl();
        List<DataType<Class>> types = new ArrayList<DataType<Class>>();
        types.add(new DataType<Class>(Object.class.getName(), String.class, String.class));
        types.add(new DataType<Class>("java.lang.String", String.class, String.class));
        types.add(new DataType<Class>(Object.class.getName(), int.class, int.class));
        DataType<List<DataType<Class>>> inputType1 =
                new DataType<List<DataType<Class>>>("idl:input", Object[].class, types);
        context.setSourceDataType(inputType1);
        context.setTargetDataType(op.getInputType());
        Object[] results = t.transform(source, context);
        assertEquals(1, results.length);
        assertTrue(results[0] instanceof Element);
        Element element = (Element) results[0];
        assertEquals("http://example.com/order.xsd", element.getNamespaceURI());
        assertEquals("checkOrderStatus", element.getLocalName());

        TransformationContext context1 = new TransformationContextImpl();
        DataType<DataType> sourceType = new DataType<DataType>("idl:output", Object.class, op.getOutputType());
        sourceType.setMetadata(org.apache.tuscany.spi.model.Operation.class.getName(), op.getOutputType().getMetadata(
                org.apache.tuscany.spi.model.Operation.class.getName()));
        
        context1.setSourceDataType(sourceType);
        DataType<DataType> targetType =
                new DataType<DataType>("idl:output", Object.class, new DataType<Class>("java.lang.Object",
                        String.class, String.class));
        context1.setTargetDataType(targetType);

        Document factory = DOMHelper.newDocument();
        Element responseElement =
                factory.createElementNS("http://example.com/order.wsdl", "p:checkOrderStatusResponse");
        Element status = factory.createElement("status");
        responseElement.appendChild(status);
        status.appendChild(factory.createTextNode("shipped"));
        Output2OutputTransformer t2 = new Output2OutputTransformer();
        t2.setMediator(m);
        t2.setDataBindingRegistry(dataBindingRegistry);
        Object st = t2.transform(responseElement, context1);
        assertEquals("shipped", st);

    }
    
}
