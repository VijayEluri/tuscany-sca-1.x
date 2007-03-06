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

package org.apache.tuscany.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.osoa.sca.ComponentContext;

/**
 * The Tuscany container
 */
public abstract class SCAContainer {
    public static final String DEFAULT_SYSTEM_SCDL = "META-INF/tuscany/default-system.composite";
    public static final String SYSTEM_SCDL = "system.composite";
    public static final String EXTENSION_SCDL = "META-INF/sca/extension.composite";
    public static final String SERVICE_SCDL = "META-INF/sca/service.composite";
    public static final String META_APPLICATION_SCDL = "META-INF/sca/application.composite";
    public static final String APPLICATION_SCDL = "application.composite";

    private static SCAContainer instance;

    /**
     * Read the service name from a configuration file
     * @param classLoader
     * @param name
     * @return
     * @throws IOException
     */
    private static String getServiceName(ClassLoader classLoader, String name) throws IOException {
        InputStream is = classLoader.getResourceAsStream("META-INF/services/" + name);
        if (is == null) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else if (!line.startsWith("#")) {
                    return line.trim();
                }
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return null;
    }

    /**
     * Returns a TuscanyContainer instance. If the system property
     * "org.apache.tuscany.api.TuscanyContainer" is set, its value is used as
     * the name of the implementation class. Otherwise, if the resource
     * "META-INF/services/org.apache.tuscany.api.TuscanyContainer" can be loaded
     * from the supplied classloader. Otherwise, it will use
     * "org.apache.tuscany.core.bootstrap.DefaultSCAContainer" as the default.
     * The named class is loaded from the supplied classloader and instantiated
     * using its default (no-arg) constructor.
     * 
     * @return
     */
    private static SCAContainer newInstance(final ClassLoader classLoader) {

        try {
            final String name = SCAContainer.class.getName();
            String className = AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(name);
                }
            });

            if (className == null) {
                className = getServiceName(classLoader, name);
            }
            if (className == null) {
                className = "org.apache.tuscany.core.bootstrap.DefaultSCAContainer";
            }
            Class cls = Class.forName(className, true, classLoader);
            return (SCAContainer)cls.newInstance();  // NOPMD lresende
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get an instance of Tuscany container
     * 
     * @return The instance
     */
    public synchronized static SCAContainer getInstance() { // NOPMD
        if (instance != null) {
            return instance;
        }
        ClassLoader classLoader = SCAContainer.class.getClassLoader();
        instance = newInstance(classLoader);
        return instance;
    }

    /**
     * Start the Tuscany runtime using default SCDLs
     */
    public static void start() {
        try {
            getInstance().startup(null, null, null, null);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the Tuscany container with the given SCDLs
     * 
     * @param system The URL for the system SCDL
     * @param extensions An array of URLs for extensions
     * @param application The URL for the application SCDL
     */
    public static void start(URL system, URL[] extensions, URL application, String compositePath) {
        try {
            getInstance().startup(system, extensions, application, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the Tuscany container with the given SCDL
     * 
     * @param application The URL for the application SCDL
     */
    public static void start(URL application, String compositePath) {
        try {
            getInstance().startup(null, null, application, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the Tuscany container with the given SCDL
     * 
     * @param compositePath The path of the application SCDL
     */
    public static void start(String compositePath) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL applicationURL = cl.getResource(compositePath);
            getInstance().startup(null, null, applicationURL, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Start the Tuscany container with the given SCDL
     * 
     * @param compositePath The path of the system SCDL
     * @param compositePath The path of the application SCDL
     */
    public static void start(String system, String compositePath) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            URL systemURL = cl.getResource(system);
            URL applicationURL = cl.getResource(compositePath);
            getInstance().startup(systemURL, null, applicationURL, compositePath);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the ComponentContext by name
     * 
     * @param componentName
     * @return
     */
    public static ComponentContext getComponentContext(String componentName) {
        return getInstance().getContext(componentName);
    }

    
    /**
     * Get access to a system service
     * @param serviceName
     * @return
     */
    protected abstract Object getSystemService(String serviceName);
    
    /**
     * Stop the Tuscany container
     */
    public static void stop() {
        try {
            getInstance().shutdown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Look up the ComponentContext by name
     * @param componentName
     * @return
     */
    protected abstract ComponentContext getContext(String componentName);

    /**
     * Start up the container
     * @param system The URL of the SCDL for tuscany system composite
     * @param extensions The URLs of the SCDLs for tuscany extension composites
     * @param application The URL of the SCDL for the application composite
     * @param compositePath The path of the application composite relative to the application URL
     * @throws Exception
     */
    protected abstract void startup(URL system, URL[] extensions, URL application, String compositePath)
        throws Exception;

    /**
     * Shutdown the container
     * @throws Exception
     */
    protected abstract void shutdown() throws Exception;
}
