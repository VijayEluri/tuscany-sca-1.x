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

package scatours.launcher;

import java.io.File;

import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.node.SCANodeFactory;

/**
 * This utility locates SCA contributions by name so that the launcher can work with various environments where 
 * contributions are stored differently.
 */
public class LauncherUtil {

    /**
     * Locate an SCA contribution by name
     * @param name The name of the SCA contribution archive
     * @return The SCAContribution
     */
    public static SCAContribution locate(String name) {
        // Try to use the target/classes directory inside Eclipse/Maven
        File file = new File("../../contributions/" + name + "/target/classes");
        if (!file.exists()) {
            // Try to use the target/<contribution>.jar that is generated by maven build
            file = new File("../../contributions/" + name + "/target/" + name + ".jar");
            if (!file.exists()) {
                // Try to use the contribution jar under the contributions folder of the distribution
                file = new File("../contributions/scatours-contribution-" + name + ".jar");
                if (!file.exists()) {
                    throw new IllegalArgumentException("Contribution " + name + " cannot be located.");
                }
            }
        }
        return new SCAContribution(name, file.toURI().toString());
    }

    /**
     * Locate an SCA contribution by replacing the ${name} in the pattern 
     * @param urlPattern The url pattern that contains ${name}
     * @param name The name of the contribution archive
     * @return The SCAContribution
     */
    public static SCAContribution locate(String urlPattern, String name) {
        String url = urlPattern.replace("${name}", name);
        return new SCAContribution(name, url);
    }

    /**
     * Create an SCA node from a list of contribution names
     * @param composite
     * @param contributionNames
     * @return
     */
    public static SCANode createNode(String composite, String... contributionNames) {
        SCAContribution[] contributions = new SCAContribution[contributionNames.length];
        int index = 0;
        for (String name : contributionNames) {
            contributions[index++] = locate(name);
        }
        SCANode node = SCANodeFactory.newInstance().createSCANode(composite, contributions);
        return node;
    }
}
