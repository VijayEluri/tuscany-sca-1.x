/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.container.java.mock.binding.foo;

import org.apache.tuscany.model.assembly.AssemblyContext;
import org.apache.tuscany.model.assembly.AssemblyVisitor;
import org.apache.tuscany.model.assembly.Binding;

/**
 * Represents a mock binding that echoes back a single parameter
 * 
 * @version $Rev$ $Date$
 */
public class FooBinding implements Binding {

    public FooBinding() {
    }

    public String getURI() {
        return null;
    }

    public void setURI(String value) {
    }

    public void initialize(AssemblyContext modelContext) {
    }

    public void freeze() {
    }

    public boolean accept(AssemblyVisitor visitor) {
        return true;
    }

    private Object contextFactory;

    public void setContextFactory(Object factory) {
        contextFactory = factory;
    }

    public Object getContextFactory() {
        System.out.println("retting");
        return contextFactory;
    }

}
