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
package org.apache.tuscany.databinding.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.PullTransformer;

public class XMLStreamReader2JAXB implements PullTransformer<XMLStreamReader, Object> {
    // TODO: How to pass context information
    private String contextPath;
    
    public XMLStreamReader2JAXB(String contextPath) {
        super();
        this.contextPath = contextPath;
    }
    
    public Object transform(XMLStreamReader source, TransformationContext transformationContext) {
        if (source == null)
            return null;
        try {
            JAXBContext context = JAXBContext.newInstance(contextPath);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(source);
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public Class<XMLStreamReader> getSourceType() {
        return XMLStreamReader.class;
    }

    public Class<Object> getTargetType() {
        return Object.class;
    }

    public int getWeight() {
        return 10;
    }



}
