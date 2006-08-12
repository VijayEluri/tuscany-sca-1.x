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
package org.apache.tuscany.databinding.xml;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;

import org.apache.tuscany.databinding.DataBinding;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public class SAXBinding implements DataBinding {
    public static final String NAME = "SAX";

    private ContentHandler contentHandler;

    public Result createResult(Class resultType) {
        return new SAXResult(contentHandler);
    }

    public Source createSource(Object source, Class sourceType) {
        if (source instanceof InputSource)
            return new SAXSource((InputSource) source);
        else
            throw new IllegalArgumentException();
    }

    public boolean isSink() {
        return true;
    }

    public String getName() {
        return NAME;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

}
