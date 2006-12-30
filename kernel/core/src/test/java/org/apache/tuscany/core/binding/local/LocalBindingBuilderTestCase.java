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
package org.apache.tuscany.core.binding.local;

import org.apache.tuscany.spi.component.ServiceBinding;
import org.apache.tuscany.spi.model.BoundServiceDefinition;

import junit.framework.TestCase;

/**
 * @version $Rev$ $Date$
 */
public class LocalBindingBuilderTestCase extends TestCase {

    public void testBuild() throws Exception {
        LocalBindingBuilder builder = new LocalBindingBuilder();
        BoundServiceDefinition def = new BoundServiceDefinition();
        def.setName("foo");
        ServiceBinding binding = builder.build(null, def, null, null);
        assertEquals(LocalServiceBinding.class, binding.getClass());
    }
}
