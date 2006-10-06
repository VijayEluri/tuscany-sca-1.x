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

package org.apache.tuscany.core.property;

import javax.xml.namespace.QName;

import org.apache.tuscany.core.databinding.xml.DOMDataBinding;
import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.ObjectFactory;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.databinding.DataBinding;
import org.apache.tuscany.spi.databinding.DataBindingRegistry;
import org.apache.tuscany.spi.databinding.Mediator;
import org.apache.tuscany.spi.idl.ElementInfo;
import org.apache.tuscany.spi.idl.TypeInfo;
import org.apache.tuscany.spi.loader.PropertyObjectFactory;
import org.apache.tuscany.spi.model.DataType;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.PropertyValue;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Node;

@Service(PropertyObjectFactory.class)
@Scope("MODULE")
public class PropertyObjectFactoryImpl implements PropertyObjectFactory {
    private DataBindingRegistry registry;
    private Mediator mediator;

    public PropertyObjectFactoryImpl() {
    }

    @Constructor({"registry", "mediator"})
    public PropertyObjectFactoryImpl(@Autowire DataBindingRegistry registry, @Autowire Mediator mediator) {
        super();
        this.registry = registry;
        this.mediator = mediator;
    }

    @SuppressWarnings("unchecked")
    public <T> ObjectFactory<T> createObjectFactory(Property<T> property, PropertyValue<T> value) {
        if (mediator == null) {
            return new SimplePropertyObjectFactory(property, value.getValue());
        }
        return new ObjectFactoryImpl(property, value);
    }

    public class ObjectFactoryImpl<P> implements ObjectFactory<P> {
        private Property<P> property;
        private PropertyValue<P> propertyValue;
        private DataType<QName> sourceDataType;
        private DataType<?> targetDataType;

        public ObjectFactoryImpl(Property<P> property, PropertyValue<P> propertyValue) {
            this.property = property;
            this.propertyValue = propertyValue;
            sourceDataType = new DataType<QName>(DOMDataBinding.NAME, Node.class, this.property.getXmlType());
            TypeInfo typeInfo = new TypeInfo(property.getXmlType(), true, null);
            ElementInfo elementInfo = new ElementInfo(null, typeInfo);
            sourceDataType.setMetadata(ElementInfo.class.getName(), elementInfo);
            Class javaType = this.property.getJavaType();
            String dataBinding = (String)property.getExtensions().get(DataBinding.class.getName());
            if (dataBinding != null) {
                targetDataType = new DataType<Class>(dataBinding, javaType, javaType);
            } else {
                targetDataType = registry.introspectType(javaType);
                if (targetDataType == null) {
                    targetDataType = new DataType<Class>("java.lang.Object", javaType, javaType);
                }
            }
        }

        @SuppressWarnings("unchecked")
        public P getInstance() throws ObjectCreationException {
            return (P)mediator.mediate(propertyValue.getValue(), sourceDataType, targetDataType, null);
        }
    }

}
