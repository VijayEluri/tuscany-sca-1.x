/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.sdo.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.eclipse.emf.ecore.util.ExtendedMetaData;

import commonj.sdo.DataObject;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * A helper to convert XML documents into DataObects and 
 * DataObjects into XML documnets.
 */
public class XMLHelperImpl implements XMLHelper
{
  ExtendedMetaData extendedMetaData;
  
  public XMLHelperImpl(ExtendedMetaData extendedMetaData)
  {
    this.extendedMetaData = extendedMetaData;
  }
  
  public XMLHelperImpl(TypeHelper typeHelper)
  {
    this.extendedMetaData = ((TypeHelperImpl)typeHelper).extendedMetaData;
  }
  
  public XMLDocument load(String inputString)
  {
    try
    {
      return load(new StringReader(inputString), null, null);
    }
    catch (IOException e)
    {
      throw new RuntimeException(e); // should never happen
    }
  }

  public XMLDocument load(InputStream inputStream) throws IOException
  {
    return load(inputStream, null, null);
  }
  
  public XMLDocument load(InputStream inputStream, String locationURI, Object options) throws IOException
  {
    XMLDocumentImpl document = new XMLDocumentImpl(extendedMetaData, options);
    document.load(inputStream, locationURI, options);
    return document;
  }

  public XMLDocument load(Reader inputReader, String locationURI, Object options) throws IOException
  {
    XMLDocumentImpl document = new XMLDocumentImpl(extendedMetaData, options);
    document.load(inputReader, locationURI, options);
    return document;
  }
  
  public String save(DataObject dataObject, String rootElementURI, String rootElementName)
  {
    StringWriter stringWriter = new StringWriter();
    try
    {
      save(createDocument(dataObject, rootElementURI, rootElementName), stringWriter, null);
      return stringWriter.toString();
    }
    catch (IOException e)
    {
      throw new RuntimeException(e); // should never happen
    }
  }

  public void save(DataObject dataObject, String rootElementURI, String rootElementName, OutputStream outputStream) throws IOException
  {
    save(createDocument(dataObject, rootElementURI, rootElementName), outputStream, null);
  }
  
  public void save(XMLDocument xmlDocument, OutputStream outputStream, Object options) throws IOException
  {
    ((XMLDocumentImpl)xmlDocument).save(outputStream, options);
  }

  public void save(XMLDocument xmlDocument, Writer outputWriter, Object options) throws IOException
  {
    ((XMLDocumentImpl)xmlDocument).save(outputWriter, options);
   }

  public XMLDocument createDocument(DataObject dataObject, String rootElementURI, String rootElementName)
  {
    return new XMLDocumentImpl(extendedMetaData, dataObject, rootElementURI, rootElementName);
  }
}
