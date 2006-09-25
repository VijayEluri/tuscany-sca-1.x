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
package org.apache.tuscany.core.databinding.xml;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tuscany.spi.databinding.PullTransformer;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.extension.TransformerExtension;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * Push DOM InputSource to Node
 * 
 */
@Service(Transformer.class)
public class InputSource2Node extends TransformerExtension<InputSource, Node> implements PullTransformer<InputSource, Node> {
    private static final Source2ResultTransformer transformer = new Source2ResultTransformer();

    public Node transform(InputSource source, TransformationContext context) {
        try {
            Source streamSource = new StreamSource(source.getCharacterStream());
            DOMResult result = new DOMResult();
            transformer.transform(streamSource, result, context);
            return result.getNode();
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class getSourceType() {
        return InputSource.class;
    }

    public Class getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 40;
    }

}
