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

package org.apache.tuscany.sca.contribution.java;

import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.java.impl.ContributionClassLoader;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * The default implementation of the ContributionClassLoaderProvider
 */
public class DefaultContributionClassLoaderProvider implements ContributionClassLoaderProvider {

    public DefaultContributionClassLoaderProvider() {
        super();
    }
    
    /**
     * returns null as it is the default provider and applies when no specific
     * provider has been specified
     */
    public String getContributionType() {
        return null;
    }

    public ClassLoader getClassLoader(Contribution contribution, ClassLoader parent) {
        return new ContributionClassLoader(contribution, parent);
    }

}
