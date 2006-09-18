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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.ModelObject;
import org.osoa.sca.Version;
import org.osoa.sca.annotations.Constructor;

import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XSDHelper;

/**
 * Loader that handles &lt;import.sdo&gt; elements.
 * 
 * @version $Rev$ $Date$
 */
public class ImportSDOLoader extends LoaderExtension {
    public static final QName IMPORT_SDO = new QName(Version.XML_NAMESPACE_1_0, "import.sdo");

    @Constructor( { "registry" })
    public ImportSDOLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPORT_SDO;
    }

    public ModelObject load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        assert IMPORT_SDO.equals(reader.getName());

        // FIXME: [rfeng] How to associate the TypeHelper with deployment context?
        TypeHelper typeHelper = TypeHelper.INSTANCE;
        if (deploymentContext != null && deploymentContext.getParent() != null) {
            typeHelper = (TypeHelper) deploymentContext.getParent().getExtension(TypeHelper.class.getName());
            if (typeHelper == null) {
                typeHelper = SDOUtil.createTypeHelper();
                deploymentContext.getParent().putExtension(TypeHelper.class.getName(), typeHelper);
            }
        }

        importFactory(reader, deploymentContext);
        importWSDL(reader, deploymentContext, typeHelper);
        LoaderUtil.skipToEndElement(reader);
        return new SDOType(typeHelper);
    }

    private void importFactory(XMLStreamReader reader, DeploymentContext deploymentContext) throws LoaderException {
        String factoryName = reader.getAttributeValue(null, "factory");
        if (factoryName != null) {
            ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
            try {
                // set TCCL as SDO needs it
                ClassLoader cl = deploymentContext.getClassLoader();
                Thread.currentThread().setContextClassLoader(cl);
                Class<?> factoryClass = cl.loadClass(factoryName);
                // FIXME: We require the SDO to provide an API to register static types in a given TypeHelper
                SDOUtil.registerStaticTypes(factoryClass);
            } catch (ClassNotFoundException e) {
                throw new LoaderException(e.getMessage(), e);
            } finally {
                Thread.currentThread().setContextClassLoader(oldCL);
            }
        }
    }

    private void importWSDL(XMLStreamReader reader, DeploymentContext deploymentContext, TypeHelper typeHelper)
        throws LoaderException {
        String location = reader.getAttributeValue(null, "location");
        if (location == null)
            location = reader.getAttributeValue(null, "wsdlLocation");
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
                    XSDHelper xsdHelper = SDOUtil.createXSDHelper(typeHelper);
                    xsdHelper.define(xsdInputStream, wsdlURL.toExternalForm());
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

    public static class SDOType extends ModelObject {
        private TypeHelper typeHelper;

        public SDOType(TypeHelper typeHelper) {
            super();
            this.typeHelper = typeHelper;
        }

        public TypeHelper getTypeHelper() {
            return typeHelper;
        }
    }
}
