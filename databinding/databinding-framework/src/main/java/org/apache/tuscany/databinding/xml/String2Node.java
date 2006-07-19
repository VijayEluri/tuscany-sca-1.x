/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.databinding.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class String2Node implements PullTransformer<String, Node> {

    public Node transform(String source, TransformationContext context) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(source));
            return builder.parse(inputSource);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<String> getSourceType() {
        return String.class;
    }

    public Class<Node> getTargetType() {
        return Node.class;
    }

    public int getWeight() {
        return 50;
    }

}
