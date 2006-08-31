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
package org.apache.tuscany.binding.jsonrpc;

import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.extension.ServiceExtension;
import org.apache.tuscany.spi.host.ServletHost;
import org.apache.tuscany.spi.wire.WireService;
import org.osoa.sca.annotations.Destroy;

/**
 * @version $Rev$ $Date$
 */
public class JSONRPCService<T> extends ServiceExtension<T> {
    private ServletHost servletHost;

    public JSONRPCService(String theName, Class<T> interfaze, CompositeComponent parent, WireService wireService, ServletHost servletHost) {

        super(theName, interfaze, parent, wireService);

        this.servletHost = servletHost;
    }

    public void start() {
        super.start();

        JSONRPCEntryPointServlet servlet = new JSONRPCEntryPointServlet(getName(), this.getServiceInstance());
        servletHost.registerMapping("/" + getName(), servlet);
        servletHost.registerMapping("/SCA/scripts", new ScriptGetterServlet());
    }

    @Destroy
    public void stop() {
        servletHost.unregisterMapping("/" + getName());
        servletHost.unregisterMapping("/SCA/scripts");

        super.stop();
    }

}
