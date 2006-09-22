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

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.idl.SimpleTypeMapper;
import org.apache.tuscany.sdo.util.SDOUtil;
import org.apache.tuscany.spi.idl.TypeInfo;

import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;

/**
 * SDO Java/XML mapping for simple XSD types
 */
public class SDOSimpleTypeMapper implements SimpleTypeMapper {
    public static final String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

    public SDOSimpleTypeMapper() {
        super();
    }

    public Object toJavaObject(TypeInfo simpleType, String value, TransformationContext context) {
        QName typeName = simpleType.getQName();
        Type type = null;
        if (URI_2001_SCHEMA_XSD.equals(typeName.getNamespaceURI())) {
            type = SDOUtil.getXSDSDOType(typeName.getLocalPart());
        } else {
            TypeHelper typeHelper = SDODataTypeHelper.getTypeHelper(context);
            type = typeHelper.getType(typeName.getNamespaceURI(), typeName.getLocalPart());
        }
        return SDOUtil.createFromString(type, value);
    }

    public String toXMLLiteral(TypeInfo simpleType, Object obj, TransformationContext context) {
        QName typeName = simpleType.getQName();
        Type type = null;
        if (URI_2001_SCHEMA_XSD.equals(typeName.getNamespaceURI())) {
            type = SDOUtil.getXSDSDOType(typeName.getLocalPart());
        } else {
            TypeHelper typeHelper = SDODataTypeHelper.getTypeHelper(context);
            if (typeHelper == null) {
                typeHelper = TypeHelper.INSTANCE;
            }
            type = typeHelper.getType(typeName.getNamespaceURI(), typeName.getLocalPart());
        }
        return SDOUtil.convertToString(type, obj);
    }

}
