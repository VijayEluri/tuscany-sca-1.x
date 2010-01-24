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

package scatours;

import java.io.IOException;
import org.apache.tuscany.sca.node.launcher.DomainManagerLauncher;

public class IntroducingDomainAPILauncher {

    public static void main(String[] args) throws Exception {
    	DomainManagerLauncher dml = DomainManagerLauncher.newInstance();
    	Object dm = dml.createDomainManager("../../domainconfig/introducing/target/classes");
        dm.getClass().getMethod("start").invoke(dm);
    	
        System.out.println("Point your browser at - http://localhost:9990/ui/home/ ");
        System.out.println("Domain started - Press enter to shutdown.");

        try {
            System.in.read();
        } catch (IOException e) {
        }

        dm.getClass().getMethod("stop").invoke(dm);
    }
}
