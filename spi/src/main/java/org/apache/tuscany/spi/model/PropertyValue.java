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
package org.apache.tuscany.spi.model;

import org.apache.tuscany.spi.ObjectFactory;

/**
 * Represents a configured component property
 *
 * @version $Rev$ $Date$
 */
public class PropertyValue<T> extends ModelObject {
    private String name;
    private ObjectFactory<T> valueFactory;

    public PropertyValue() {
    }

    public PropertyValue(String name, ObjectFactory<T> valueFactory) {
        this.name = name;
        this.valueFactory = valueFactory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObjectFactory<T> getValueFactory() {
        return valueFactory;
    }

    public void setValueFactory(ObjectFactory<T> valueFactory) {
        this.valueFactory = valueFactory;
    }
}
