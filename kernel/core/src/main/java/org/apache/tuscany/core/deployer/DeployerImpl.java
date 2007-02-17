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
package org.apache.tuscany.core.deployer;

import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.builder.BuilderRegistry;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeContainerMonitor;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.loader.Loader;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.resolver.ResolutionException;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.core.component.event.ComponentStop;
import org.apache.tuscany.core.component.scope.CompositeScopeContainer;
import org.apache.tuscany.core.resolver.AutowireResolver;

/**
 * Default implementation of Deployer.
 *
 * @version $Rev$ $Date$
 */
public class DeployerImpl implements Deployer {
    private XMLInputFactory xmlFactory;
    private Builder builder;
    private ScopeContainerMonitor monitor;
    private Loader loader;
    private AutowireResolver resolver;
    private Connector connector;

    public DeployerImpl(XMLInputFactory xmlFactory,
                        Loader loader,
                        Builder builder,
                        AutowireResolver resolver,
                        Connector connector) {
        this.xmlFactory = xmlFactory;
        this.loader = loader;
        this.builder = builder;
        this.resolver = resolver;
        this.connector = connector;
    }

    public DeployerImpl() {
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    @Autowire
    public void setLoader(LoaderRegistry loader) {
        this.loader = loader;
    }

    @Autowire
    public void setBuilder(BuilderRegistry builder) {
        this.builder = builder;
    }

    @Monitor
    public void setMonitor(ScopeContainerMonitor monitor) {
        this.monitor = monitor;
    }

    @Autowire
    public void setResolver(AutowireResolver resolver) {
        this.resolver = resolver;
    }

    @Autowire
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public <I extends Implementation<?>> Component deploy(CompositeComponent parent,
                                                          ComponentDefinition<I> componentDefinition)
        throws LoaderException, BuilderException, ResolutionException {
        final ScopeContainer scopeContainer = new CompositeScopeContainer(monitor);
        scopeContainer.start();
        DeploymentContext deploymentContext = new RootDeploymentContext(null, xmlFactory, scopeContainer, null);
        deploymentContext.getPathNames().add(componentDefinition.getUri().toString());
        // load the model
        load(parent, componentDefinition, deploymentContext);
        // resolve autowires
        resolver.resolve(null, componentDefinition);
        // build runtime artifacts
        Component component = (Component) build(parent, componentDefinition, deploymentContext);
        // create a listener so the scope container is shutdown when the top-level composite stops
        RuntimeEventListener listener = new RuntimeEventListener() {
            public void onEvent(Event event) {
                scopeContainer.onEvent(event);
                if (event instanceof ComponentStop) {
                    scopeContainer.stop();
                }
            }
        };
        component.addListener(listener);
        connector.connect(componentDefinition);
        return component;
    }

    /**
     * Load the componentDefinition type information for the componentDefinition being deployed. For a typical
     * deployment this will result in the SCDL definition being loaded.
     *
     * @param componentDefinition the componentDefinition being deployed
     * @param deploymentContext   the current deployment context
     */
    protected <I extends Implementation<?>> void load(CompositeComponent parent,
                                                      ComponentDefinition<I> componentDefinition,
                                                      DeploymentContext deploymentContext) throws LoaderException {
        loader.loadComponentType(parent, componentDefinition.getImplementation(), deploymentContext);
    }

    /**
     * Build the runtime context for a loaded componentDefinition.
     *
     * @param parent              the context that will be the parent of the new sub-context
     * @param componentDefinition the componentDefinition being deployed
     * @param deploymentContext   the current deployment context
     * @return the new runtime context
     */
    protected <I extends Implementation<?>> SCAObject build(CompositeComponent parent,
                                                            ComponentDefinition<I> componentDefinition,
                                                            DeploymentContext deploymentContext)
        throws BuilderException {
        return builder.build(parent, componentDefinition, deploymentContext);
    }

}
