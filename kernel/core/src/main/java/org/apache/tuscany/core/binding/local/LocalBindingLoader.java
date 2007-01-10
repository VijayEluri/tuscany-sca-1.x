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
package org.apache.tuscany.core.binding.local;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.StAXElementLoader;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.wire.Wire;

/**
 * Loader responsible for handling the local binding
 *
 * @version $Rev$ $Date$
 */
public class LocalBindingLoader implements StAXElementLoader {

    public QName getXMLType() {
        return Wire.LOCAL_BINDING;
    }

    public ModelObject load(CompositeComponent parent,
                            ModelObject object,
                            XMLStreamReader reader,
                            DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        String uri = reader.getAttributeValue(null, "uri");
        if (uri != null) {
            try {
                return new LocalBindingDefinition(new URI(uri));
            } catch (URISyntaxException e) {
                throw new LoaderException(e);
            }
        }
        return new LocalBindingDefinition();
    }
}
