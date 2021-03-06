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

package org.apache.tuscany.sca.node.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.ComponentService;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.assembly.CompositeService;
import org.apache.tuscany.sca.assembly.xml.CompositeDocumentProcessor;
import org.apache.tuscany.sca.contribution.Artifact;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionService;
import org.apache.tuscany.sca.contribution.service.util.FileHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.core.assembly.ActivationException;
import org.apache.tuscany.sca.core.assembly.CompositeActivator;
import org.apache.tuscany.sca.implementation.node.ConfiguredNodeImplementation;
import org.apache.tuscany.sca.implementation.node.NodeImplementationFactory;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.MonitorRuntimeException;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;
import org.apache.tuscany.sca.node.SCAClient;
import org.apache.tuscany.sca.node.SCAContribution;
import org.apache.tuscany.sca.node.SCANode;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentContext;
import org.osoa.sca.CallableReference;
import org.osoa.sca.ServiceReference;
import org.osoa.sca.ServiceRuntimeException;

/**
 * A local representation of the SCADomain running on a single node
 * 
 * @version $Rev$ $Date$
 */
public class NodeImpl implements SCANode, SCAClient {

    private static final Logger logger = Logger.getLogger(NodeImpl.class.getName());

    // The node configuration name, used for logging
    private String configurationName;

    // The Tuscany runtime that does the hard work
    private RuntimeBootStrapper runtime;
    private CompositeActivator compositeActivator;
    private XMLInputFactory inputFactory;
    private ModelFactoryExtensionPoint modelFactories;
    private StAXArtifactProcessorExtensionPoint artifactProcessors;
    private URLArtifactProcessorExtensionPoint documentProcessors;
    private Monitor monitor;

    private List<Contribution> contributions;
    // The composite loaded into this node
    private Composite composite;

    /** 
     * Constructs a new SCA node.
     *  
     * @param configurationURI the URI of the node configuration information.
     */
    NodeImpl(String configurationURI) {
        configurationName = configurationURI;
        logger.log(Level.INFO, "Creating node: " + configurationName);

        try {
            // Initialize the runtime
            initRuntime();

            // Read the node configuration feed
            StAXArtifactProcessor<ConfiguredNodeImplementation> configurationProcessor =
                artifactProcessors.getProcessor(ConfiguredNodeImplementation.class);
            URL configurationURL = new URL(configurationURI);
            InputStream is = configurationURL.openStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            reader.nextTag();
            ConfiguredNodeImplementation configuration = configurationProcessor.read(reader);
            is.close();

            // Resolve contribution URLs
            for (Contribution contribution : configuration.getContributions()) {
                URL contributionURL = new URL(configurationURL, contribution.getLocation());
                contribution.setLocation(contributionURL.toString());
            }

            // Resolve composite URL
            URL compositeURL = new URL(configurationURL, configuration.getComposite().getURI());
            configuration.getComposite().setURI(compositeURL.toString());

            // Configure the node
            configureNode(configuration, null);

        } catch (ServiceRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Construct a node by discovering the node configuration on the classpath
     * @param classLoader
     * @param compositeURI
     */
    NodeImpl() {
        configurationName = "default";
        logger.log(Level.INFO, "Creating node: " + configurationName);

        try {
            initRuntime();

            ConfiguredNodeImplementation config = findNodeConfiguration(null, null);
            configureNode(config, null);
        } catch (ServiceRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Construct a node by discovering the node configuration (composite+contrbutions) on the classpath
     * @param classLoader
     * @param compositeURI
     */
    NodeImpl(ClassLoader classLoader, String compositeURI) {
        configurationName = compositeURI;
        logger.log(Level.INFO, "Creating node: " + configurationName);

        if (compositeURI != null) {
            //URI uri = URI.create(compositeURI);
            URI uri;
            try {
                uri = new URI(null, compositeURI, null);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid Composite URI: " + compositeURI, e);
            }
            
            if (uri.isAbsolute()) {
                throw new IllegalArgumentException("Composite URI must be a resource name: " + compositeURI);
            }
        }
        try {
            // Initialize the runtime
            initRuntime();

            ConfiguredNodeImplementation config = findNodeConfiguration(compositeURI, classLoader);
            configureNode(config, null);
        } catch (ServiceRuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServiceRuntimeException(e);
        }
    }

    /**
     * Discover the contribution on the classpath
     * @param compositeURI
     * @param classLoader
     * @return A configured node implementation
     * @throws Exception
     */
    private ConfiguredNodeImplementation findNodeConfiguration(final String compositeURI, ClassLoader classLoader)
        throws Exception {
        NodeImplementationFactory nodeImplementationFactory =
            modelFactories.getFactory(NodeImplementationFactory.class);
        ConfiguredNodeImplementation config = nodeImplementationFactory.createConfiguredNodeImplementation();

        // Default to thread context classloader
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        String contributionArtifactPath = compositeURI;
        URL contributionArtifactURL = null;
        if (compositeURI != null) {
            contributionArtifactURL = getResource(classLoader, compositeURI);
            if (contributionArtifactURL == null) {
                throw new IllegalArgumentException("Composite not found: " + contributionArtifactPath);
            }
            // Set to relative URI to avoid duplicate loading
            Composite composite = createComposite(compositeURI);
            config.setComposite(composite);
        } else {
            // No composite is specified, tring to search the SCA metadata files
            contributionArtifactPath = Contribution.SCA_CONTRIBUTION_META;
            contributionArtifactURL = getResource(classLoader, Contribution.SCA_CONTRIBUTION_META);

            if (contributionArtifactURL == null) {
                contributionArtifactPath = Contribution.SCA_CONTRIBUTION_GENERATED_META;
                contributionArtifactURL = getResource(classLoader, Contribution.SCA_CONTRIBUTION_GENERATED_META);
            }
            if (contributionArtifactURL == null) {
                contributionArtifactPath = Contribution.SCA_CONTRIBUTION_DEPLOYABLES;
                contributionArtifactURL = getResource(classLoader, Contribution.SCA_CONTRIBUTION_DEPLOYABLES);
            }
            
            // No contribution can be discovered
            if (contributionArtifactURL == null) {
                throw new IllegalArgumentException("No default contribution can be discovered on the classpath");
            }
            
            // No composite will be created, all deployable composites will be used later
        }

        Contribution c = getContribution(contributionArtifactURL, contributionArtifactPath);
        config.getContributions().add(c);

        return config;
    }

    private Contribution getContribution(URL contributionArtifactURL, String contributionArtifactPath) {
        URL contributionURL = getContributionURL(contributionArtifactURL, contributionArtifactPath);

        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);

        SCAContribution contribution = new SCAContribution(contributionURL.toString(), contributionURL.toString());
        Contribution c = createContribution(contributionFactory, contribution);
        return c;
    }

    public static URL getContributionURL(URL contributionArtifactURL, String contributionArtifactPath) {
        URL contributionURL = null;
        // "jar:file://....../something.jar!/a/b/c/app.composite"
        try {
            String url = contributionArtifactURL.toExternalForm();
            String protocol = contributionArtifactURL.getProtocol();
            String escapedContributionArtifactPath = new URI(null, contributionArtifactPath, null).toASCIIString();
            
            if ("file".equals(protocol)) {
                // directory contribution
                if (url.endsWith(escapedContributionArtifactPath)) {
                    final String location = url.substring(0, url.lastIndexOf(escapedContributionArtifactPath));
                    // workaround from evil URL/URI form Maven
                    // contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                    // Allow privileged access to open URL stream. Add FilePermission to added to
                    // security policy file.
                    try {
                        contributionURL = AccessController.doPrivileged(new PrivilegedExceptionAction<URL>() {
                            public URL run() throws IOException {
                                return FileHelper.toFile(new URL(location)).toURI().toURL();
                            }
                        });
                    } catch (PrivilegedActionException e) {
                        throw (MalformedURLException)e.getException();
                    }
                }

            } else if ("jar".equals(protocol)) {
                // jar contribution
                String location = url.substring(4, url.lastIndexOf("!/"));
                // workaround for evil URL/URI from Maven
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();

            } else if ("wsjar".equals(protocol)) {
                // See https://issues.apache.org/jira/browse/TUSCANY-2219
                // wsjar contribution 
                String location = url.substring(6, url.lastIndexOf("!/"));
                // workaround for evil url/uri from maven 
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();

            } else if ("zip".equals(protocol)) {
                // See https://issues.apache.org/jira/browse/TUSCANY-2598
                // zip contribution, remove the zip prefix and pad with file:
                String location = "file:"+url.substring(4, url.lastIndexOf("!/"));
                contributionURL = FileHelper.toFile(new URL(location)).toURI().toURL();
                
            } else if (protocol != null && (protocol.equals("bundle") || protocol.equals("bundleresource"))) {
                contributionURL =
                    new URL(contributionArtifactURL.getProtocol(), contributionArtifactURL.getHost(),
                            contributionArtifactURL.getPort(), "/");
            }
        } catch (MalformedURLException mfe) {
            throw new IllegalArgumentException(mfe);
        } catch (URISyntaxException use) {
            throw new IllegalArgumentException(use);
        }
        return contributionURL;
    }

    private static URL getResource(final ClassLoader classLoader, final String compositeURI) {
        return AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return classLoader.getResource(compositeURI);
            }
        });
    }

    /** 
     * Constructs a new SCA node.
     *  
     * @param compositeURI
     * @param contributions
     */
    NodeImpl(String compositeURI, SCAContribution[] contributions) {
        configurationName = compositeURI;
        logger.log(Level.INFO, "Creating node: " + configurationName);

        try {
            // Initialize the runtime
            initRuntime();

            URI uri = compositeURI == null ? null : URI.create(compositeURI);
            ConfiguredNodeImplementation configuration = null;
            if (contributions == null || contributions.length == 0) {
                if (uri != null && uri.getScheme() != null) {
                    throw new IllegalArgumentException("No SCA contributions are provided");
                }
                configuration = findNodeConfiguration(compositeURI, null);
            } else {

                // Create a node configuration
                NodeImplementationFactory nodeImplementationFactory =
                    modelFactories.getFactory(NodeImplementationFactory.class);
                configuration = nodeImplementationFactory.createConfiguredNodeImplementation();

                Composite composite = compositeURI == null ? null : createComposite(compositeURI);
                configuration.setComposite(composite);
  

                // Create contribution models
                ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
                for (SCAContribution c : contributions) {
                    Contribution contribution = createContribution(contributionFactory, c);
                    configuration.getContributions().add(contribution);
                }
            }

            // Configure the node
            configureNode(configuration, null);

        } catch (ServiceRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private Composite createComposite(String compositeURI) {
        // Create composite model
        AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
        Composite composite = assemblyFactory.createComposite();
        composite.setURI(compositeURI);
        composite.setUnresolved(true);
        return composite;
    }

    /** 
     * Constructs a new SCA node.
     *  
     * @param compositeURI
     * @param compositeContent
     * @param contributions
     */
    NodeImpl(String compositeURI, String compositeContent, SCAContribution[] contributions) {
        configurationName = compositeURI;
        logger.log(Level.INFO, "Creating node: " + configurationName);

        try {
            // Initialize the runtime
            initRuntime();

            ConfiguredNodeImplementation configuration = null;
            if (contributions == null || contributions.length == 0) {
                configuration = findNodeConfiguration(compositeURI, null);
            } else {
                // Create a node configuration
                NodeImplementationFactory nodeImplementationFactory =
                    modelFactories.getFactory(NodeImplementationFactory.class);
                configuration = nodeImplementationFactory.createConfiguredNodeImplementation();

                // Defer reading the composite content until the contributions have been loaded (TUSCANY-3569)

                // Create contribution models
                ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
                for (SCAContribution c : contributions) {
                    Contribution contribution = createContribution(contributionFactory, c);
                    configuration.getContributions().add(contribution);
                }
            }

            // Configure the node
            configureNode(configuration, compositeContent);

        } catch (ServiceRuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceRuntimeException(e);
        }
    }

    private static Contribution createContribution(ContributionFactory contributionFactory, SCAContribution c) {
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(c.getURI());
        contribution.setLocation(c.getLocation());
        contribution.setUnresolved(true);
        return contribution;
    }

    /**
     * Initialize the Tuscany runtime.
     * 
     * @throws Exception
     */
    private void initRuntime() throws Exception {

        // Create a node runtime
        runtime = new RuntimeBootStrapper(Thread.currentThread().getContextClassLoader());
        runtime.start();

        // Get the various factories we need
        ExtensionPointRegistry registry = runtime.getExtensionPointRegistry();
        modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
        inputFactory = modelFactories.getFactory(XMLInputFactory.class);

        // Create the required artifact processors
        artifactProcessors = registry.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);

        documentProcessors = registry.getExtensionPoint(URLArtifactProcessorExtensionPoint.class);

        // Save the composite activator
        compositeActivator = runtime.getCompositeActivator();

        // save the monitor
        UtilityExtensionPoint utilities = registry.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        monitor = monitorFactory.createMonitor();
    }

    /**
     * Escape the space in URL string
     * @param uri
     * @return
     */
    private static URI createURI(String uri) {
        if (uri.indexOf(' ') != -1) {
            uri = uri.replace(" ", "%20");
        }
        return URI.create(uri);
    }

    private void configureNode(ConfiguredNodeImplementation configuration, String compositeContent) throws Exception {

        // Find if any contribution JARs already available locally on the classpath
        Map<String, URL> localContributions = localContributions();

        // Load the specified contributions
        ContributionService contributionService = runtime.getContributionService();
        contributions = new ArrayList<Contribution>();
        for (Contribution contribution : configuration.getContributions()) {
            URI uri = createURI(contribution.getLocation());
            if (uri.getScheme() == null) {
                uri = new File(contribution.getLocation()).toURI();
            }
            URL contributionURL = uri.toURL();

            // Extract contribution file name
            String file = contributionURL.getPath();
            int i = file.lastIndexOf('/');
            if (i != -1 && i < file.length() - 1) {
                file = file.substring(i + 1);

                // If we find the local contribution file on the classpath, use it in
                // place of the original contribution URL
                URL localContributionURL = localContributions.get(file);
                if (localContributionURL != null) {
                    contributionURL = localContributionURL;
                }
            }

            // Load the contribution
            logger.log(Level.INFO, "Loading contribution: " + contributionURL);
            contributions.add(contributionService.contribute(contribution.getURI(), contributionURL, false));
            analyzeProblems();
        }

        composite = configuration.getComposite();

        // Read the composite content after the contributions have been loaded, so that the
        // policySet definitions provided by the contributions are available (TUSCANY-3569)
        if (composite == null && compositeContent != null) {
            logger.log(Level.INFO, "Loading composite: " + configurationName);
            CompositeDocumentProcessor compositeDocProcessor =
                (CompositeDocumentProcessor)documentProcessors.getProcessor(Composite.class);
            composite =
                compositeDocProcessor.read(URI.create(configurationName), new ByteArrayInputStream(compositeContent
                    .getBytes("UTF-8")));
            analyzeProblems();
            configuration.setComposite(composite);
        }

        if(composite != null && composite.isUnresolved()) {
            ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
            Artifact compositeFile = contributionFactory.createArtifact();
            compositeFile.setUnresolved(true);
            compositeFile.setURI(composite.getURI());
            for (Contribution c : contributions) {
                ModelResolver resolver = c.getModelResolver();
                Artifact resolved = resolver.resolveModel(Artifact.class, compositeFile);
                if (resolved != null && resolved.isUnresolved() == false) {
                    composite = (Composite) resolved.getModel();
                    break;
                }
            }
        }

        // FIXME: This is a hack to get a list of deployable composites. By design, the deployment composite should
        // has been configured
        if (composite == null) {
            List<Composite> deployables = new ArrayList<Composite>();
            for (Contribution c : contributions) {
                deployables.addAll(c.getDeployables());
            }
            aggregate(deployables);
            configuration.setComposite(composite);
        }

        Contribution contribution = null;
        if (composite.getName() == null) {
            // Load the specified composite
            URL compositeURL;

            URI uri = createURI(configuration.getComposite().getURI());
            if (uri.getScheme() == null) {

                // If the composite URI is a relative URI, try to resolve it within the contributions
                contribution = contribution(contributions, uri.toString());
                if (contribution == null) {
                    throw new IllegalArgumentException("Composite is not found in contributions: " + uri);
                }
                compositeURL = new URL(location(contribution, uri.toString()));

            } else {

                // If the composite URI is an absolute URI, use it as is
                compositeURL = uri.toURL();
            }

            URLArtifactProcessor<Composite> compositeDocProcessor = documentProcessors.getProcessor(Composite.class);
            // Read the composite
            logger.log(Level.INFO, "Loading composite: " + compositeURL);
            // InputStream is = compositeURL.openStream();
            // XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            try {
                composite = compositeDocProcessor.read(null, uri, compositeURL);
            } catch (ContributionReadException e) {
                // ignore - errors will be detected by analyzeProblems() call below
            }
            // reader.close();

            analyzeProblems();

        }
        // And resolve the composite within the scope of the last contribution
        if (contribution == null && contributions.size() != 0) {
            contribution = contributions.get(contributions.size() - 1);
        }

        // Resolve the given composite within the scope of the selected contribution
        if (contribution != null) {
            StAXArtifactProcessor<Composite> compositeProcessor = artifactProcessors.getProcessor(Composite.class);
            compositeProcessor.resolve(composite, contribution.getModelResolver());
            analyzeProblems();
        }
        // Create a top level composite to host our composite
        // This is temporary to make the activator happy
        AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
        Composite tempComposite = assemblyFactory.createComposite();
        tempComposite.setName(new QName("http://tuscany.apache.org/xmlns/sca/1.0", "temp"));
        tempComposite.setURI("http://tuscany.apache.org/xmlns/sca/1.0");

        // Include the node composite in the top-level composite 
        tempComposite.getIncludes().add(composite);

        // set the top level composite on the composite activator as 
        // logic in callable reference resolution relies on this being 
        // available
        compositeActivator.setDomainComposite(tempComposite);

        // Build the composite
        runtime.buildComposite(composite);

        analyzeProblems();
    }

    /**
     * Create a deployment composite that includes a list of deployable composites
     * @param composites
     */
    private void aggregate(List<Composite> composites) {
        if (composites.size() == 0) {
            throw new IllegalArgumentException("No deployable composite is declared");
        } else if (composites.size() == 1) {
            composite = composites.get(0);
        } else {
            // Include all composites
            AssemblyFactory assemblyFactory = runtime.getAssemblyFactory();
            Composite aggregated = assemblyFactory.createComposite();
            aggregated.setName(new QName("http://tuscany.apache.org/xmlns/sca/1.0", "aggregated"));
            aggregated.setURI("http://tuscany.apache.org/xmlns/sca/1.0/aggregated");
            aggregated.getIncludes().addAll(composites);
            composite = aggregated;
        }
    }

    /**
     * Returns the artifact representing the given composite.
     * 
     * @param contribution
     * @param compositeURI
     * @return
     */
    private String location(Contribution contribution, String uri) {
        if (uri != null && uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Artifact compositeFile = contributionFactory.createArtifact();
        compositeFile.setUnresolved(true);
        compositeFile.setURI(uri);
        ModelResolver resolver = contribution.getModelResolver();
        Artifact resolved = resolver.resolveModel(Artifact.class, compositeFile);
        if (resolved != null && !resolved.isUnresolved()) {
            return resolved.getLocation();
        } else {
            return null;
        }
    }

    /**
     * Returns the contribution containing the given composite.
     * 
     * @param contributions
     * @param compositeURI
     * @return
     */
    private Contribution contribution(List<Contribution> contributions, String compositeURI) {
        if (compositeURI != null && compositeURI.startsWith("/")) {
            compositeURI = compositeURI.substring(1);
        }
        ContributionFactory contributionFactory = modelFactories.getFactory(ContributionFactory.class);
        Artifact compositeFile = contributionFactory.createArtifact();
        compositeFile.setUnresolved(true);
        compositeFile.setURI(compositeURI);
        for (Contribution c : contributions) {
            ModelResolver resolver = c.getModelResolver();
            Artifact resolved = resolver.resolveModel(Artifact.class, compositeFile);
            if (resolved != null && !resolved.isUnresolved()) {
                return c;
            }
        }
        return null;
    }

    private void analyzeProblems() throws Exception {

        for (Problem problem : monitor.getProblems()) {
            if ((problem.getSeverity() == Severity.ERROR) && (!problem.getMessageId().equals("SchemaError"))) {
                if (problem.getCause() != null) {
                    throw new ServiceRuntimeException(new MonitorRuntimeException(problem.getCause()));
                } else {
                    throw new ServiceRuntimeException(new MonitorRuntimeException(problem.toString()));
                }
            }
        }
    }

    public void start() {
        logger.log(Level.INFO, "Starting node: " + configurationName);

        try {

            // Activate the composite
            compositeActivator.activate(composite);

            // Start the composite
            compositeActivator.start(composite);

        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public void stop() {
        logger.log(Level.INFO, "Stopping node: " + configurationName);

        try {

            // Stop the composite
            compositeActivator.stop(composite);

            // Deactivate the composite
            compositeActivator.deactivate(composite);

            runtime.stop();
        } catch (ActivationException e) {
            throw new ServiceRuntimeException(e);
        }
    }

    public <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException {
        return (R)runtime.getProxyFactory().cast(target);
    }

    public <B> B getService(Class<B> businessInterface, String serviceName) {

        ServiceReference<B> serviceReference = getServiceReference(businessInterface, serviceName);
        if (serviceReference == null) {
            throw new ServiceRuntimeException("Service not found: " + serviceName);
        }
        return serviceReference.getService();
    }

    public <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String name) {

        // Extract the component name
        String componentName;
        String serviceName;
        int i = name.indexOf('/');
        if (i != -1) {
            componentName = name.substring(0, i);
            serviceName = name.substring(i + 1);

        } else {
            componentName = name;
            serviceName = null;
        }

        // Lookup the component 
        Component component = null;

        for (Component compositeComponent : composite.getComponents()) {
            if (compositeComponent.getName().equals(componentName)) {
                component = compositeComponent;
                break;
            }
        }

        if (component == null) {
            throw new ServiceRuntimeException("The service " + name + " has not been contributed to the domain");
        }
        RuntimeComponentContext componentContext = null;

        // If the component is a composite, then we need to find the
        // non-composite component that provides the requested service
        if (component.getImplementation() instanceof Composite) {
            for (ComponentService componentService : component.getServices()) {
                if (serviceName == null || serviceName.equals(componentService.getName())) {
                    CompositeService compositeService = (CompositeService)componentService.getService();
                    if (compositeService != null) {
                        if (serviceName != null) {
                            serviceName = "$promoted$" + component.getName() + "$slash$" + serviceName;
                        }
                        componentContext =
                            ((RuntimeComponent)compositeService.getPromotedComponent()).getComponentContext();
                        
                        if (componentContext == null){
                        	// there is a break in the promotion chain so just let and exception 
                        	// be thrown
                        	break;
                        }
                        return componentContext.createSelfReference(businessInterface, compositeService
                            .getPromotedService());
                    }
                    break;
                }
            }
            // No matching service found
            throw new ServiceRuntimeException("Composite service not found: " + name);
        } else {
            componentContext = ((RuntimeComponent)component).getComponentContext();
            if (serviceName != null) {
                return componentContext.createSelfReference(businessInterface, serviceName);
            } else {
                return componentContext.createSelfReference(businessInterface);
            }
        }
    }

    /**
     * Returns the extension point registry used by this node.
     * 
     * @return
     */
    public ExtensionPointRegistry getExtensionPointRegistry() {
        return runtime.getExtensionPointRegistry();
    }

    /**
     * Returns the composite being run by this node.
     * 
     * @return
     */
    public Composite getComposite() {
        return composite;
    }

    /**
     * Returns contribution JARs available on the classpath.
     * 
     * @return
     */
    private static Map<String, URL> localContributions() {
        Map<String, URL> localContributions = new HashMap<String, URL>();
        collectJARs(localContributions, Thread.currentThread().getContextClassLoader());
        return localContributions;
    }

    /**
     * Collect JARs on the classpath of a URLClassLoader
     * @param urls
     * @param cl
     */
    private static void collectJARs(Map<String, URL> urls, ClassLoader cl) {
        if (cl == null) {
            return;
        }

        // Collect JARs from the URLClassLoader's classpath
        if (cl instanceof URLClassLoader) {
            URL[] jarURLs = ((URLClassLoader)cl).getURLs();
            if (jarURLs != null) {
                for (URL jarURL : jarURLs) {
                    String file = jarURL.getPath();
                    int i = file.lastIndexOf('/');
                    if (i != -1 && i < file.length() - 1) {
                        file = file.substring(i + 1);
                        urls.put(file, jarURL);
                    }
                }
            }
        }

        // Collect JARs from the parent ClassLoader
        collectJARs(urls, cl.getParent());
    }

    public CompositeActivator getCompositeActivator() {
        return compositeActivator;
    }
    
    public RuntimeBootStrapper getRuntime() {
        return runtime;
    }

}
