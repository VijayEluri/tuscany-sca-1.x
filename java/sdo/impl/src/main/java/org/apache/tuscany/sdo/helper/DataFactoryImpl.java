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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.TypeHelper;

/**
 * A Factory for creating DataObjects.  
 * The created DataObjects are not connected to any other DataObjects.
 */
public class DataFactoryImpl implements DataFactory
{
  protected TypeHelper typeHelper;
  
  public DataFactoryImpl(TypeHelper typeHelper)
  {
    this.typeHelper = typeHelper;
  }

  public DataObject create(String uri, String typeName)
  {
    Type type = typeHelper.getType(uri, typeName);
    return create(type);
  }

  public DataObject create(Class interfaceClass)
  {
    //TODO more efficient implementation ... this is a really bad one!
    Type type = typeHelper.getType(interfaceClass);
    return create(type);
  }
  
  public DataObject create(Type type)
  {
    if (type instanceof EClass)
    {
      EClass eClass = (EClass)type;
      return (DataObject)EcoreUtil.create(eClass);
    }
    throw new IllegalArgumentException();
  }
}
