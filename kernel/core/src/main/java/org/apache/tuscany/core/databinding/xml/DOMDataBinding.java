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
package org.apache.tuscany.core.databinding.xml;


import org.w3c.dom.Node;

import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.DataBindingExtension;

/**
 * DOM DataBinding
 *
 * @version $Rev$ $Date$
 */
public class DOMDataBinding extends DataBindingExtension {
    public static final String NAME = Node.class.getName();

    public DOMDataBinding() {
        super(Node.class);
    }

    @Override
    public WrapperHandler getWrapperHandler() {
        return new DOMWrapperHandler();
    }

    public Object copy(Object source) {
        if (Node.class.isAssignableFrom(source.getClass())) {
            Node nodeSource = (Node) source;
            Node2String strTransformer = new Node2String();
            String stringCopy = strTransformer.transform(nodeSource, null);

            String2Node nodeTransformer = new String2Node();
            return nodeTransformer.transform(stringCopy, null);
        }

        return super.copy(source);
    }
}
