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

package org.apache.tuscany.core.model.physical.instancefactory;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;

/**
 * Reflection based instance factory.
 * 
 * @version $Revision$ $Date$
 */
public class ReflectiveIFProviderDefinition extends InstanceFactoryProviderDefinition {
    
    // Implementation class
    private String implementationClass;
    
    // Constructor arguments
    private List<String> constructorArguments = new LinkedList<String>();
    
    // Init method
    private String initMethod;
    
    // Destroy method
    private String destroyMethod;
    
    // Constructor injection sites
    private List<URI> constructorNames = new LinkedList<URI>();
    
    // Injection sites
    private List<InjectionSite> injectionSites = new LinkedList<InjectionSite>();

    /**
     * returns the constructor argument.
     * @return the constructorArguments Fully qualified names of the constructor 
     * atgument types.
     */
    public List<String> getConstructorArguments() {
        return Collections.unmodifiableList(constructorArguments);
    }

    /**
     * Adds a constructor argument type.
     * @param constructorArgument the constructorArguments to set
     */
    public void addConstructorArgument(String constructorArgument) {
        constructorArguments.add(constructorArgument);
    }

    /**
     * Returns constructor injection names.
     * @return the constructorNames Constructor injection names.
     */
    public List<URI> getConstructorNames() {
        return Collections.unmodifiableList(constructorNames);
    }

    /**
     * Adds a constructor injection name.
     * @param constructorName Constructor injection name.
     */
    public void addConstructorNames(URI constructorName) {
        constructorNames.add(constructorName);
    }

    /**
     * Gets the destroy method.
     * @return Destroy method name.
     */
    public String getDestroyMethod() {
        return destroyMethod;
    }

    /**
     * Sets the destroy method.
     * @param destroyMethod Destroy method name.
     */
    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    /**
     * Gets the implementation class.
     * @return Implementation class.
     */
    public String getImplementationClass() {
        return implementationClass;
    }

    /**
     * Sets the implementation class.
     * @param implementationClass Implementation class.
     */
    public void setImplementationClass(String implementationClass) {
        this.implementationClass = implementationClass;
    }

    /**
     * Gets the init method.
     * @return Init method name.
     */
    public String getInitMethod() {
        return initMethod;
    }

    /**
     * Sets the init method.
     * @param initMethod Init method name.
     */
    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    /**
     * Gets the injection sites.
     * @return Injection sites.
     */
    public List<InjectionSite> getInjectionSites() {
        return Collections.unmodifiableList(injectionSites);
    }

    /**
     * Adds an injection site.
     * @param injectionSiteInjection site.
     */
    public void addInjectionSite(InjectionSite injectionSite) {
        injectionSites.add(injectionSite);
    }

}
