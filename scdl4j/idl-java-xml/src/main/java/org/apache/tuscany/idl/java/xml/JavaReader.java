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

package org.apache.tuscany.idl.java.xml;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.idl.java.JavaFactory;
import org.apache.tuscany.idl.java.JavaInterface;
import org.apache.tuscany.scdl.stax.Loader;

public class JavaReader implements Loader<JavaInterface>, JavaConstants {

    private JavaFactory javaFactory;
    
    public JavaReader(JavaFactory javaFactory) {
            this.javaFactory = javaFactory;
    }
    
    public JavaInterface load(XMLStreamReader reader) throws XMLStreamException {

        // Read an <interface.java>
        JavaInterface javaInterface = javaFactory.createJavaInterface();
        javaInterface.setUnresolved(true);
        javaInterface.setName(reader.getAttributeValue(null, INTERFACE));
        
        // Skip to end element
        while (reader.hasNext()) {
            if (reader.next() == END_ELEMENT && INTERFACE_JAVA_QNAME.equals(reader.getName()))
                break;
        }
        return javaInterface;
    }
}
