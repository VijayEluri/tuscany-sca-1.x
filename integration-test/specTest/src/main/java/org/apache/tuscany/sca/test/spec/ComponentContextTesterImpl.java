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
package org.apache.tuscany.sca.test.spec;

import org.osoa.sca.ComponentContext;
import org.osoa.sca.annotations.Context;
import org.osoa.sca.annotations.Reference;

/**
 * Component that tests ComponentContext functions.
 *
 * @version $Rev$ $Date$
 */
public class ComponentContextTesterImpl implements ComponentContextTester {
    @Context
    public ComponentContext context;

    @Reference
    public IdentityService getServiceTest;

    public boolean isContextInjected() {
        return context != null;
    }

    public String getURI() {
        return context.getURI();
    }

    public String getServiceIdentity(String name) {
        IdentityService service = context.getService(IdentityService.class, name);
        return service.getURI();
    }
}
