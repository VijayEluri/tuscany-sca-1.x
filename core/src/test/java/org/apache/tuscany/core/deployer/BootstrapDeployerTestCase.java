/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.deployer;

import java.net.URL;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;

import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.Deployer;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.PropertyValue;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.bootstrap.Bootstrapper;
import org.apache.tuscany.core.bootstrap.DefaultBootstrapper;
import org.apache.tuscany.core.implementation.system.model.SystemBinding;
import org.apache.tuscany.core.implementation.system.model.SystemCompositeImplementation;
import org.apache.tuscany.core.mock.component.BasicInterface;
import org.apache.tuscany.core.monitor.NullMonitorFactory;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

/**
 * Verifies the default boostrap deployer
 *
 * @version $Rev$ $Date$
 */
public class BootstrapDeployerTestCase extends MockObjectTestCase {
    private DeployerImpl deployer;
    private DeploymentContext deploymentContext;
    private ComponentDefinition<SystemCompositeImplementation> componentDefinition;
    private SystemCompositeImplementation implementation;
    private Mock parent;

    public void testBoot1Load() throws LoaderException {
        Mock mock = mock(CompositeComponent.class);
        CompositeComponent parent = (CompositeComponent) mock.proxy();
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        deployer.load(parent, componentDefinition, deploymentContext);
        CompositeComponentType<ServiceDefinition, ?, ?> composite = implementation.getComponentType();
        assertNotNull(composite);
        assertEquals("boot1", composite.getName());

        // check parse of <service>
        Map<String, ServiceDefinition> services = composite.getServices();
        assertEquals(1, services.size());
        BoundServiceDefinition serviceDefinition = (BoundServiceDefinition) services.get("service");
        assertNotNull(serviceDefinition);
        assertEquals("service", serviceDefinition.getName());
        assertEquals(BasicInterface.class, serviceDefinition.getServiceContract().getInterfaceClass());
        assertTrue(serviceDefinition.getBinding() instanceof SystemBinding);

        // check parse of <component>
        Map<String, ComponentDefinition<? extends Implementation<?>>> components = composite.getComponents();
        assertEquals(1, components.size());
        ComponentDefinition<? extends Implementation<?>> component = components.get("component");
        assertNotNull(component);
        PropertyValue<?> propVal = component.getPropertyValues().get("publicProperty");
        assertEquals("propval", propVal.getValueFactory().getInstance());

        // check introspection of implementation
        ComponentType<?, ?, ?> componentType = component.getImplementation().getComponentType();
        assertNotNull(componentType); // details checked in SystemComponentTypeLoaderTestCase

        // check included component
        Map<String, Include> includes = composite.getIncludes();
        assertEquals(1, includes.size());
        Include include = includes.get("boot1-include");
        assertNotNull(include);
        CompositeComponentType included = include.getIncluded();
        assertNotNull(included);
        assertEquals(1, included.getComponents().size());
    }

    public void testBoot1Deployment() throws LoaderException {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot1.scdl");
        implementation.setScdlLocation(scdl);
        parent.expects(once()).method("register").withAnyArguments();

        // load the boot1 file using the bootstrap deployer
        componentDefinition.setName("simple");
        Component<?> component = deployer.deploy((CompositeComponent<?>) parent.proxy(), componentDefinition);
        assertNotNull(component);
        parent.verify();
    }

    public void testBoot2Deployment() throws LoaderException {
        URL scdl = BootstrapDeployerTestCase.class.getResource("boot2.scdl");
        implementation.setScdlLocation(scdl);
        parent.expects(once()).method("register").withAnyArguments();

        // load the boot2 file using the bootstrap deployer
        componentDefinition.setName("newDeployer");
        Component<?> component = deployer.deploy((CompositeComponent<?>) parent.proxy(), componentDefinition);
        assertNotNull(component);
        parent.verify();
        component.start();
        Deployer newDeployer = (Deployer) component.getServiceInstance("deployer");
        assertNotNull(newDeployer);

/*      // FIXME
        // load the boot2 file using the newly loaded deployer
        parent.reset();
        parent.expects(once()).method("register").withAnyArguments();
        componentDefinition.setName("newDeployer2");
        component = newDeployer.deploy((CompositeComponent<?>) parent.proxy(), componentDefinition);
        assertNotNull(component);
        parent.verify();
        component.start();
        Deployer newDeployer2 = (Deployer) component.getServiceInstance("deployer");
        assertNotNull(newDeployer2);
*/
    }

    protected void setUp() throws Exception {
        super.setUp();
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        Bootstrapper bootstrapper = new DefaultBootstrapper(new NullMonitorFactory(), xmlFactory);
        deployer = (DeployerImpl) bootstrapper.createDeployer();
        deploymentContext = new RootDeploymentContext(null, xmlFactory, null, null);
        implementation = new SystemCompositeImplementation();
        implementation.setClassLoader(getClass().getClassLoader());
        componentDefinition = new ComponentDefinition<SystemCompositeImplementation>(implementation);
        parent = mock(CompositeComponent.class);
    }
}
