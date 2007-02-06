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
package org.apache.tuscany.core.databinding.javabeans;

import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Transformer to convert data from DOM Node to JavaBean
 */
@Service(Transformer.class)
public class DOMNode2JavaBeanTransformer extends XML2JavaBeanTransformer<Node> {
    
    @Override
    public List<Node> getChildElements(Node parent) throws XML2JavaMapperException {
        NodeList nodeList = parent.getChildNodes();
        ArrayList<Node> nodeArrayList = new ArrayList<Node>(nodeList.getLength());
        for (int count = 0 ; count < nodeList.getLength() ; ++count) {
            nodeArrayList.add(nodeList.item(count));
        }
        
        return nodeArrayList;
    }

    @Override
    public String getElementName(Node element) throws XML2JavaMapperException {
        return element.getNodeName();
    }

    @Override
    public String getText(Node element) throws XML2JavaMapperException {
        if (element instanceof Document) {
            element = ((Document) element).getDocumentElement();
        }
        return element.getTextContent();
    }

    @Override
    public boolean isTextElement(Node element) throws XML2JavaMapperException {
        if (element.getNodeType() == 3) {
            return true;
        } else {
            return false;
        }
    }

    public Class getSourceType() {
        return Node.class;
    }
}
