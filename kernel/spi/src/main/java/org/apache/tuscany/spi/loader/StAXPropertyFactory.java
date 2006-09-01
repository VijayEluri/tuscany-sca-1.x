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
package org.apache.tuscany.spi.loader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.model.Property;

/**
 * A factory that will create an ObjectFactory for a property by parsing a StAX XMLStreamReader.
 *
 * @version $Rev$ $Date$
 */
public interface StAXPropertyFactory {
    /**
     * Return an ObjectFactory for instances of a property defined in an XML stream. The ObjectFactory must return
     * instances that can safely be supplied to component implementations. If the instance is mutable and isolation
     * between components is required, then the factory must clone or otherwise protect the implementation from
     * unexpected modifications by other implementation instances.
     *
     * @param reader   the reader to use to access the XML stream
     * @param property the Property definition that the resulting ObjectFactory must be able to assign to
     * @return an ObjectFactory that can produce instances that can be assigned to the supplied Property
     * @throws XMLStreamException if there is a problem reading the stream
     * @throws LoaderException    if there is a problem creating the ObjectFactory
     */
    <T> ObjectFactory<T> createObjectFactory(XMLStreamReader reader, Property<T> property)
        throws XMLStreamException, LoaderException;
}
