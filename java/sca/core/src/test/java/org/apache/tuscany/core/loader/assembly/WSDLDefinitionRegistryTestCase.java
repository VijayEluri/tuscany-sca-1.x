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
package org.apache.tuscany.core.loader.assembly;

import java.net.URL;
import java.io.IOException;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import junit.framework.TestCase;
import org.apache.tuscany.core.loader.impl.WSDLDefinitionRegistryImpl;
import org.apache.tuscany.common.resource.ResourceLoader;
import org.apache.tuscany.common.resource.impl.ResourceLoaderImpl;

/**
 * @version $Rev$ $Date$
 */
public class WSDLDefinitionRegistryTestCase extends TestCase {
    private static final String NS = "http://www.example.org";
    private WSDLDefinitionRegistryImpl wsdlRegistry;
    private ResourceLoader rl;


    public void testLoadFromAbsoluteWSDLLocation() {
        try {
            Definition def = wsdlRegistry.loadDefinition(NS + ' ' + rl.getResource("org/apache/tuscany/core/loader/assembly/example.wsdl"), rl);
            assertNotNull(def.getPortType(new QName(NS, "HelloWorld")));
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (WSDLException e) {
            fail(e.getMessage());
        }
    }

    public void testLoadFromRelativeWSDLLocation() {
        try {
            Definition def = wsdlRegistry.loadDefinition(NS + " org/apache/tuscany/core/loader/assembly/example.wsdl", rl);
            assertNotNull(def.getPortType(new QName(NS, "HelloWorld")));
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (WSDLException e) {
            fail(e.getMessage());
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        wsdlRegistry = new WSDLDefinitionRegistryImpl();
        wsdlRegistry.setMonitor(NULL_MONITOR);
        rl = new ResourceLoaderImpl(getClass().getClassLoader());
    }

    private static final WSDLDefinitionRegistryImpl.Monitor NULL_MONITOR = new WSDLDefinitionRegistryImpl.Monitor() {
        public void readingWSDL(String namespace, URL location) {
        }

        public void cachingDefinition(String namespace, URL location) {
        }
    };
}
