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
package org.apache.tuscany.sca.assembly;


/**
 * A test interface model.
 * 
 * @version $Rev: 604880 $ $Date: 2007-12-17 06:30:07 -0800 (Mon, 17 Dec 2007) $
 */
public class TestBinding implements Binding {
    public TestBinding(AssemblyFactory factory) {
    }

    public String getName() {
        return null;
    }

    public String getURI() {
        return "http://test";
    }

    public void setName(String name) {
    }

    public void setURI(String uri) {
    }

    public void setUnresolved(boolean unresolved) {
    }
    
    public boolean isUnresolved() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }     
}
