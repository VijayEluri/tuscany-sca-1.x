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
package org.apache.tuscany.spi.model;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a service offered by a component, that has a 1..n bindings associated with it.
 *
 * @version $Rev$ $Date$
 */
public class BoundServiceDefinition extends ServiceDefinition {
    private List<BindingDefinition> bindings;
    private URI target;

    public BoundServiceDefinition(String name,
                                  ServiceContract contract,
                                  List<BindingDefinition> bindings,
                                  boolean remotable,
                                  URI target) {
        super(name, contract, remotable);
        this.target = target;
        this.bindings = bindings;
    }

    public BoundServiceDefinition() {
        bindings = new ArrayList<BindingDefinition>();
    }

    public List<BindingDefinition> getBindings() {
        return Collections.unmodifiableList(bindings);
    }

    public void addBinding(BindingDefinition binding) {
        this.bindings.add(binding);
    }

    public URI getTarget() {
        return target;
    }

    public void setTarget(URI target) {
        this.target = target;
    }

}
