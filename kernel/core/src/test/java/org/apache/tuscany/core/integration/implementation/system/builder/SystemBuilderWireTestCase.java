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
package org.apache.tuscany.core.integration.implementation.system.builder;

import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.WorkContext;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;

import junit.framework.TestCase;
import org.apache.tuscany.core.builder.ConnectorImpl;
import org.apache.tuscany.core.component.WorkContextImpl;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.component.scope.ModuleScopeContainer;
import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.core.implementation.system.builder.SystemBindingBuilder;
import org.apache.tuscany.core.implementation.system.builder.SystemComponentBuilder;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponent;
import org.apache.tuscany.core.implementation.system.component.SystemCompositeComponentImpl;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.implementation.system.model.SystemImplementation;
import org.apache.tuscany.core.mock.component.Source;
import org.apache.tuscany.core.mock.component.Target;
import org.apache.tuscany.core.mock.factories.MockComponentFactory;

/**
 * Validates that system builders and the default connector create properly wired contexts
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemBuilderWireTestCase extends TestCase {
    private DeploymentContext deploymentContext;

    /**
     * Validates building a wire from an atomic context to an atomic context
     */
    public void testAtomicWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContainer scope = new ModuleScopeContainer(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();

        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null, connector, null);

        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            MockComponentFactory.createSourceWithTargetReference();

        AtomicComponent sourceComponent = builder.build(parent, sourceComponentDefinition, deploymentContext);
        AtomicComponent targetComponent = builder.build(parent, targetComponentDefinition, deploymentContext);

        parent.register(sourceComponent);
        parent.register(targetComponent);
        parent.prepare();
        parent.start();
        scope.onEvent(new CompositeStart(this, parent));
        Source source = (Source) parent.getChild("source").getServiceInstance();
        assertNotNull(source);
        Target target = (Target) parent.getChild("target").getServiceInstance();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        scope.onEvent(new CompositeStop(this, parent));
        parent.stop();
        scope.stop();
    }

    /**
     * Validates building a wire from an atomic context to a reference context
     */
    public void testAtomicToReferenceWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContainer scope = new ModuleScopeContainer(work);
        scope.start();

        Connector connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeComponent grandParent = new SystemCompositeComponentImpl("grandparent", null, null, null, null);
        SystemCompositeComponent parent = new SystemCompositeComponentImpl("parent", grandParent, grandParent, null,
            null);

        // create a context in the grandparent that the reference will be autowired to
        ComponentDefinition<SystemImplementation> targetComponentDefinition = MockComponentFactory.createTarget();
        AtomicComponent targetComponentComponent = builder.build(parent, targetComponentDefinition, deploymentContext);
        grandParent.register(targetComponentComponent);

        BoundReferenceDefinition<SystemBinding> targetReferenceDefinition = MockComponentFactory.createBoundReference();
        ComponentDefinition<SystemImplementation> sourceComponentDefinition =
            MockComponentFactory.createSourceWithTargetReference();

        AtomicComponent sourceComponent = builder.build(parent, sourceComponentDefinition, deploymentContext);
        Reference reference = bindingBuilder.build(parent, targetReferenceDefinition, deploymentContext);

        parent.register(sourceComponent);
        parent.register(reference);
        connector.connect(reference.getInboundWire(), reference.getOutboundWire(), true);
        connector.connect(sourceComponent);
        grandParent.register(parent);
        grandParent.start();
        scope.onEvent(new CompositeStart(this, parent));
        Source source = (Source) parent.getChild("source").getServiceInstance();
        assertNotNull(source);
        Target target = (Target) parent.getChild("target").getServiceInstance();
        assertNotNull(target);
        assertSame(target, source.getTarget());
        scope.onEvent(new CompositeStop(this, parent));
        grandParent.stop();
        scope.stop();
    }


    /**
     * Validates building a wire from a service context to an atomic context
     */
    public void testServiceToAtomicWireBuild() throws Exception {
        WorkContext work = new WorkContextImpl();
        ScopeContainer scope = new ModuleScopeContainer(work);
        scope.start();

        ConnectorImpl connector = new ConnectorImpl();
        SystemComponentBuilder builder = new SystemComponentBuilder();
        SystemBindingBuilder bindingBuilder = new SystemBindingBuilder();

        SystemCompositeComponent parent = new SystemCompositeComponentImpl(null, null, null, null, null);

        BoundServiceDefinition<SystemBinding> serviceDefinition = MockComponentFactory.createBoundService();
        ComponentDefinition<SystemImplementation> componentDefinition = MockComponentFactory.createTarget();

        AtomicComponent sourceComponent = builder.build(parent, componentDefinition, deploymentContext);
        parent.register(sourceComponent);

        Service service = bindingBuilder.build(parent, serviceDefinition, deploymentContext);
        parent.register(service);

        connector.connect(service.getInboundWire(), service.getOutboundWire(), true);

        connector.connect(sourceComponent);
        String serviceName = service.getOutboundWire().getTargetName().getPortName();
        connector.connect(sourceComponent,
            parent,
            service.getOutboundWire(),
            sourceComponent.getInboundWire(serviceName),
            true);
        parent.start();
        scope.onEvent(new CompositeStart(this, parent));
        Target target = (Target) parent.getChild("serviceDefinition").getServiceInstance();
        assertNotNull(target);
        Target target2 = (Target) parent.getChild("target").getServiceInstance();
        assertNotNull(target);
        assertSame(target, target2);
        scope.onEvent(new CompositeStop(this, parent));
        parent.stop();
        scope.stop();

    }

    protected void setUp() throws Exception {
        super.setUp();
        ModuleScopeContainer moduleScope = new ModuleScopeContainer();
        moduleScope.start();
        deploymentContext = new RootDeploymentContext(null, null, moduleScope, null);

    }
}
