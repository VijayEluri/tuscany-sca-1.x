/**
 *
 * Copyright 2006 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.implementation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;

/**
 * A convenience class for annotation processors which alleviates the need to implement unused callbacks
 *
 * @version $Rev$ $Date$
 */
public abstract class ImplementationProcessorSupport implements ImplementationProcessor {
    private IntrospectionRegistry registry;

    @Autowire
    public void setRegistry(IntrospectionRegistry registry) {
        this.registry = registry;
    }

    @Init(eager = true)
    public void init() {
        registry.registerProcessor(this);
    }

    @Destroy
    public void destroy() {
        registry.unregisterProcessor(this);
    }

    public void visitClass(CompositeComponent<?> parent, Class<?> clazz,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context)
        throws ProcessingException {
    }

    public void visitSuperClass(CompositeComponent<?> parent, Class<?> clazz,
                                PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                DeploymentContext context)
        throws ProcessingException {
    }

    public void visitMethod(CompositeComponent<?> parent, Method method,
                            PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                            DeploymentContext context)
        throws ProcessingException {
    }

    public void visitConstructor(CompositeComponent<?> parent, Constructor<?> constructor,
                                 PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                                 DeploymentContext context)
        throws ProcessingException {
    }

    public void visitField(CompositeComponent<?> parent, Field field,
                           PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                           DeploymentContext context) throws ProcessingException {
    }

    public void visitEnd(CompositeComponent<?> parent, Class<?> clazz,
                         PojoComponentType<JavaMappedService, JavaMappedReference, JavaMappedProperty<?>> type,
                         DeploymentContext context) throws ProcessingException {

    }
}
