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

package org.apache.tuscany.sca.workspace.admin.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ContributionFactory;
import org.apache.tuscany.sca.contribution.DefaultModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.DefaultStAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.processor.ExtensibleStAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessorExtensionPoint;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataDocumentProcessor;
import org.apache.tuscany.sca.contribution.xml.ContributionMetadataProcessor;
import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.apache.tuscany.sca.workspace.Workspace;
import org.apache.tuscany.sca.workspace.WorkspaceFactory;
import org.apache.tuscany.sca.workspace.admin.WorkspaceCollection;
import org.apache.tuscany.sca.workspace.dependency.impl.ContributionDependencyAnalyzer;
import org.apache.tuscany.sca.workspace.processor.impl.ContributionInfoProcessor;
import org.apache.tuscany.sca.workspace.xml.WorkspaceProcessor;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;
import org.w3c.dom.Document;

/**
 * Implementation of a contribution workspace service component. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
public class WorkspaceCollectionImpl implements WorkspaceCollection {

    @Property
    public String workspaceFileName;
    
    private ContributionFactory contributionFactory;
    private AssemblyFactory assemblyFactory;
    private WorkspaceFactory workspaceFactory;
    private Workspace workspace;
    private WorkspaceProcessor workspaceProcessor;
    private XMLOutputFactory outputFactory;
    private XMLInputFactory inputFactory;
    private ContributionInfoProcessor contributionProcessor;
    
    /**
     * Initialize the workspace administration component.
     */
    @Init
    public void init() throws IOException, ContributionReadException, XMLStreamException, ParserConfigurationException {
        
        // Create extension points
        ModelFactoryExtensionPoint factories = new DefaultModelFactoryExtensionPoint();
        StAXArtifactProcessorExtensionPoint staxProcessors = new DefaultStAXArtifactProcessorExtensionPoint(factories);
        inputFactory = factories.getFactory(XMLInputFactory.class);
        outputFactory = factories.getFactory(XMLOutputFactory.class);
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.TRUE);
        ExtensibleStAXArtifactProcessor extensionProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
        
        // Create factories
        contributionFactory = factories.getFactory(ContributionFactory.class);
        assemblyFactory = factories.getFactory(AssemblyFactory.class);
        workspaceFactory = factories.getFactory(WorkspaceFactory.class);

        // Create workspace and contribution artifact processors
        workspaceProcessor = new WorkspaceProcessor(workspaceFactory, contributionFactory, extensionProcessor);
        ContributionMetadataProcessor metadataProcessor = new ContributionMetadataProcessor(assemblyFactory, contributionFactory, extensionProcessor);
        ContributionMetadataDocumentProcessor metadataDocumentProcessor = new ContributionMetadataDocumentProcessor(metadataProcessor, inputFactory); 
        contributionProcessor = new ContributionInfoProcessor(contributionFactory, metadataDocumentProcessor);

        // Read workspace.xml
        URI uri = URI.create(workspaceFileName);
        File file = new File(uri);
        if (file.exists()) {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            FileInputStream is = new FileInputStream(file);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);
            workspace = workspaceProcessor.read(reader);
        } else {
            workspace = workspaceFactory.createWorkspace();
        }
    }
    
    public Entry<String, Item>[] getAll() {

        // Return all the contributions
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (Contribution contribution: workspace.getContributions()) {
            entries.add(entry(contribution));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {

        // Returns the contribution with the given URI key
        for (Contribution contribution: workspace.getContributions()) {
            if (key.equals(contribution.getURI())) {
                Item item = new Item();
                item.setTitle(contribution.getURI());
                item.setLink(contribution.getLocation());
                return item;
            }
        }
        throw new NotFoundException(key);
    }

    public String post(String key, Item item) {
        
        // Adds a new contribution to the workspace
        Contribution contribution = contributionFactory.createContribution();
        contribution.setURI(key);
        contribution.setLocation(item.getLink());
        workspace.getContributions().add(contribution);
        
        // Write the workspace
        writeWorkspace();
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        
        // Update a contribution already in the workspace
        Contribution newContribution = contributionFactory.createContribution();
        newContribution.setURI(key);
        newContribution.setLocation(item.getLink());
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.set(i, newContribution);
                
                // Write the workspace
                writeWorkspace();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public void delete(String key) throws NotFoundException {
        
        // Delete a contribution from the workspace
        List<Contribution> contributions = workspace.getContributions();
        for (int i = 0, n = contributions.size(); i < n; i++) {
            if (contributions.get(i).getURI().equals(key)) {
                contributions.remove(i);

                // Write the workspace
                writeWorkspace();
                
                return;
            }
        }
        throw new NotFoundException(key);
    }

    public Entry<String, Item>[] query(String queryString) {
        if (queryString.startsWith("importedBy=")) {
            
            // Return the collection of dependencies of the specified contribution
            List<Entry<String, Item>> entries = new ArrayList<Entry<String,Item>>();
            
            // Read the contribution metadata into a temporary workspace
            Workspace dependencyWorkspace = workspaceFactory.createWorkspace();
            for (Contribution c: workspace.getContributions()) {
                URI uri = URI.create(c.getURI());
                URL url;
                try {
                    url = new URL(c.getLocation());
                } catch (MalformedURLException e) {
                    throw new ServiceRuntimeException();
                }
                Contribution contribution;
                try {
                    contribution = contributionProcessor.read(null, uri, url);
                } catch (ContributionReadException e) {
                    throw new ServiceRuntimeException(e);
                }
                dependencyWorkspace.getContributions().add(contribution);
            }
            
            // Calculate the contribution dependencies
            String key = queryString.substring(11);
            for (Contribution contribution: workspace.getContributions()) {
                if (key.equals(contribution.getURI())) {
                    
                    ContributionDependencyAnalyzer analyzer = new ContributionDependencyAnalyzer();
                    Set<Contribution> dependencies = analyzer.calculateContributionDependencies(dependencyWorkspace, contribution);
                    
                    // Build the collection of dependencies
                    for (Contribution dependency: dependencies) {
                        entries.add(entry(dependency));
                    }
                }
            }

            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns an entry representing a contribution
     * @param contribution
     * @return
     */
    private static Entry<String, Item> entry(Contribution contribution) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(contribution.getURI());
        Item item = new Item();
        item.setTitle(contribution.getURI());
        item.setLink(contribution.getLocation());
        entry.setData(item);
        return entry;
    }
    
    /**
     * Write the workspace back to disk
     */
    private void writeWorkspace() {
        try {
            // First write to a byte stream
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(bos);
            workspaceProcessor.write(workspace, writer);
            
            // Parse again to pretty format the document
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(new ByteArrayInputStream(bos.toByteArray()));
            OutputFormat format = new OutputFormat();
            format.setIndenting(true);
            format.setIndent(2);
            
            // Write to workspace.xml
            URI uri = URI.create(workspaceFileName);
            FileOutputStream os = new FileOutputStream(new File(uri));
            XMLSerializer serializer = new XMLSerializer(os, format);
            serializer.serialize(document);
            
        } catch (FileNotFoundException e) {
            throw new ServiceRuntimeException(e);
        } catch (ContributionWriteException e) {
            throw new ServiceRuntimeException(e);
        } catch (XMLStreamException e) {
            throw new ServiceRuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
