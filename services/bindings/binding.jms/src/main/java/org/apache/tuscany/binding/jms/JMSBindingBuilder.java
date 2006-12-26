/**
 *
 * Copyright 2006 The Apache Software Foundation
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
package org.apache.tuscany.binding.jms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.jms.Destination;
import javax.naming.NamingException;

import org.apache.axiom.om.OMElement;
import org.apache.tuscany.idl.wsdl.WSDLServiceContract;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.BindingBuilderExtension;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ServiceContract;

/**
 * Builds a Service or Reference for JMS binding.
 * 
 * @version $Rev: 449970 $ $Date: 2006-09-26 06:05:35 -0400 (Tue, 26 Sep 2006) $
 */

public class JMSBindingBuilder extends BindingBuilderExtension<JMSBinding> {

    private static final String DEFAULT_JMS_RESOURCE_FACTORY =
        "org.apache.tuscany.binding.jms.SimpleJMSResourceFactory";

    private static final String OM_DATA_BINDING = OMElement.class.getName();

    protected Class<JMSBinding> getBindingType() {
        return JMSBinding.class;
    }

    public Service build(CompositeComponent parent,
                         BoundServiceDefinition<JMSBinding> serviceDefinition,
                         DeploymentContext deploymentContext) {

        JMSBinding jmsBinding = serviceDefinition.getBinding();
        Class<?> interfaze = serviceDefinition.getServiceContract().getInterfaceClass();

        ServiceContract serviceContract = serviceDefinition.getServiceContract();
        jmsBinding.setXMLFormat(serviceContract instanceof WSDLServiceContract);

        JMSResourceFactory jmsResourceFactory = getJMSResourceFactory(jmsBinding);

        if (serviceContract instanceof WSDLServiceContract) {
            serviceContract.setDataBinding(OM_DATA_BINDING);
        }

        OperationAndDataBinding requestODB =
            getRequestOperationAndDatabinding(jmsBinding, deploymentContext.getClassLoader());
        OperationAndDataBinding responseODB =
            getRequestOperationAndDatabinding(jmsBinding, deploymentContext.getClassLoader());

        Service service =
            new JMSService(serviceDefinition.getName(), parent, jmsBinding, jmsResourceFactory,
                           requestODB, responseODB, interfaze);
        service.setBindingServiceContract(serviceContract);

        return service;
    }

    public JMSReference build(CompositeComponent parent,
                           BoundReferenceDefinition<JMSBinding> referenceDefinition,
                           DeploymentContext deploymentContext) {

        String name = referenceDefinition.getName();
        Class<?> interfaze = referenceDefinition.getServiceContract().getInterfaceClass();
        ServiceContract serviceContract;
        try {
            serviceContract = (ServiceContract)referenceDefinition.getServiceContract().clone();
        } catch (CloneNotSupportedException e) {
            throw new JMSBindingException("Couldn't clone the Service Contract", e);
        }
        serviceContract.setDataBinding(OM_DATA_BINDING);

        JMSBinding jmsBinding = referenceDefinition.getBinding();
        JMSResourceFactory jmsResourceFactory = getJMSResourceFactory(jmsBinding);

        Destination requestDest;
        Destination replyDest = null;
        try {
            requestDest = jmsResourceFactory.lookupDestination(jmsBinding.getDestinationName());
            if (jmsBinding.getResponseDestinationName() != null) {
                replyDest = jmsResourceFactory.lookupDestination(jmsBinding.getResponseDestinationName());
            }
        } catch (NamingException e) {
            throw new JMSBindingException(e);
        }

        OperationAndDataBinding requestODB =
            getRequestOperationAndDatabinding(jmsBinding, deploymentContext.getClassLoader());
        OperationAndDataBinding responseODB =
            getRequestOperationAndDatabinding(jmsBinding, deploymentContext.getClassLoader());

        JMSReference reference =
            new JMSReference(name, parent, jmsBinding, jmsResourceFactory, requestODB, responseODB,
                             interfaze, requestDest, replyDest);
        reference.setBindingServiceContract(serviceContract);
        return reference;

    }

    private JMSResourceFactory getJMSResourceFactory(JMSBinding jmsBinding) {
        String className = jmsBinding.getJmsResourceFactoryName();
        if (className != null && !className.equals("")) {
            try {
                Class factoryClass = Class.forName(className != null ? className : DEFAULT_JMS_RESOURCE_FACTORY);
                Constructor constructor = factoryClass.getDeclaredConstructor(new Class[] {JMSBinding.class});
                return (JMSResourceFactory)constructor.newInstance(jmsBinding);
            } catch (ClassNotFoundException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (SecurityException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (NoSuchMethodException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (IllegalArgumentException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (InstantiationException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (IllegalAccessException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            } catch (InvocationTargetException e) {
                throw new JMSBindingException("Error loading the JMSResourceFactory", e);
            }
        } else {
            return new SimpleJMSResourceFactory(jmsBinding);
        }

    }

    protected OperationAndDataBinding getRequestOperationAndDatabinding(JMSBinding jmsBinding, ClassLoader cl) {
        String className = jmsBinding.getRequestOperationAndDatabindingName();
        OperationAndDataBinding operationAndDataBinding = instantiateClass(jmsBinding, cl, className);
        return operationAndDataBinding;
    }

    protected OperationAndDataBinding getResponseOperationAndDatabinding(JMSBinding jmsBinding, ClassLoader cl) {
        String className = jmsBinding.getResponseOperationAndDatabindingName();
        OperationAndDataBinding operationAndDataBinding = instantiateClass(jmsBinding, cl, className);
        return operationAndDataBinding;
    }

    protected OperationAndDataBinding instantiateClass(JMSBinding jmsBinding, ClassLoader cl, String className) {
        OperationAndDataBinding operationAndDataBinding;
        if (cl == null) {
            cl = this.getClass().getClassLoader();
        }
        try {
            Class clazz;
            try {
                clazz = cl.loadClass(className);
            } catch (ClassNotFoundException e) {
                clazz = this.getClass().getClassLoader().loadClass(className);
            }
            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {JMSBinding.class});
            operationAndDataBinding = (OperationAndDataBinding)constructor.newInstance(jmsBinding);

        } catch (Throwable e) {
            throw new JMSBindingException("Exception instantiating OperationAndDataBinding class", e);
        }
        return operationAndDataBinding;
    }
}
