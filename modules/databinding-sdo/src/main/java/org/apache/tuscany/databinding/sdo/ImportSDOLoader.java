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
package org.apache.tuscany.databinding.sdo;

import static org.apache.tuscany.databinding.sdo.ImportSDO.IMPORT_SDO;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessor;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessorRegistry;

import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

/**
 * Loader that handles &lt;import.sdo&gt; elements.
 * 
 * @version $Rev$ $Date$
 */
public class ImportSDOLoader implements StAXArtifactProcessor<ImportSDO> {
    private StAXArtifactProcessorRegistry processorRegistry;
    private HelperContextRegistry helperContextRegistry;

    public ImportSDOLoader(HelperContextRegistry helperContextRegistry) {
        super();
        this.helperContextRegistry = helperContextRegistry;
    }

    public QName getXMLType() {
        return IMPORT_SDO;
    }

    public ImportSDO read(XMLStreamReader reader) throws ContributionReadException {
        assert IMPORT_SDO.equals(reader.getName());

        HelperContext helperContext = null;
        // FIXME: [rfeng] How to associate the TypeHelper with deployment
        // context?
        synchronized (helperContextRegistry) {
            helperContext = helperContextRegistry.getHelperContext(deploymentContext.getComponentId());
            if (helperContext == null) {
                helperContext = SDOUtil.createHelperContext();
                helperContextRegistry.register(deploymentContext.getComponentId(), helperContext);
            }
        }

        importFactory(reader, deploymentContext, helperContext);
        importWSDL(reader, deploymentContext, helperContext);
        LoaderUtil.skipToEndElement(reader);
        return new ImportSDO(helperContext);
    }

    private void importFactory(XMLStreamReader reader, DeploymentContext deploymentContext, HelperContext helperContext)
        throws LoaderException {
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            try {
                // set TCCL as SDO needs it
                ClassLoader cl = deploymentContext.getClassLoader();
                Thread.currentThread().setContextClassLoader(cl);
                Class<?> factoryClass = cl.loadClass(factoryName);
                register(factoryClass, helperContext);
            } catch (Exception e) {
                throw new LoaderException(e.getMessage(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

    private static void register(Class factoryClass, HelperContext helperContext) throws Exception {
        Field field = factoryClass.getField("INSTANCE");
        Object factory = field.get(null);
        Method method = factory.getClass().getMethod("register", new Class[] {HelperContext.class});
        method.invoke(factory, new Object[] {helperContext});

        // FIXME: How do we associate the application HelperContext with the one
        // imported by the composite
        HelperContext defaultContext = HelperProvider.getDefaultContext();
        method.invoke(factory, new Object[] {defaultContext});
    }

    private void importWSDL(XMLStreamReader reader, HelperContext helperContext)
        throws LoaderException {
        String location = reader.getAttributeValue(null, "location");
        if (location == null) {
            location = reader.getAttributeValue(null, "wsdlLocation");
        }
        if (location != null) {
            try {
                URL wsdlURL = null;
                URI uri = URI.create(location);
                if (uri.isAbsolute()) {
                    wsdlURL = uri.toURL();
                }
                wsdlURL = deploymentContext.getClassLoader().getResource(location);
                if (null == wsdlURL) {
                    LoaderException loaderException = new LoaderException("WSDL location error");
                    loaderException.setResourceURI(location);
                    throw loaderException;
                }
                InputStream xsdInputStream = wsdlURL.openStream();
                try {
                    XSDHelper xsdHelper = helperContext.getXSDHelper();
                    xsdHelper.define(xsdInputStream, wsdlURL.toExternalForm());
                } finally {
                    xsdInputStream.close();
                }
                // FIXME: How do we associate the application HelperContext with
                // the one
                // imported by the composite
                HelperContext defaultContext = HelperProvider.getDefaultContext();
                xsdInputStream = wsdlURL.openStream();
                try {
                    XSDHelper xsdHelper = defaultContext.getXSDHelper();
                    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
                    try {
                        // set TCCL as SDO needs it
                        ClassLoader cl = deploymentContext.getClassLoader();
                        Thread.currentThread().setContextClassLoader(cl);
                        xsdHelper.define(xsdInputStream, wsdlURL.toExternalForm());
                    } finally {
                        Thread.currentThread().setContextClassLoader(oldCL);
                    }
                } finally {
                    xsdInputStream.close();
                }
            } catch (IOException e) {
                LoaderException sfe = new LoaderException(e.getMessage());
                sfe.setResourceURI(location);
                throw sfe;
            }
        }
    }
}
