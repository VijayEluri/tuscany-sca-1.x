/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.container.groovy;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import groovy.lang.GroovyObject;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.extension.AtomicComponentExtension;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.OutboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.WireService;

/**
 * The Groovy atomic component implementation. Groovy implementations may be "scripts" or classes.
 */
public class GroovyAtomicComponent<T> extends AtomicComponentExtension<T> {
    private final Class<? extends GroovyObject> groovyClass;
    private final List<Class<?>> services;
    private final List<PropertyInjector> injectors;

    public GroovyAtomicComponent(String name,
                                 Class<? extends GroovyObject> groovyClass,
                                 List<Class<?>>services,
                                 List<PropertyInjector> injectors,
                                 CompositeComponent parent,
                                 ScopeContainer scopeContainer,
                                 WireService wireService) {
        super(name, parent, scopeContainer, wireService, 0);
        this.scope = scopeContainer.getScope();

        assert groovyClass != null;
        assert services != null;
        assert injectors != null;

        this.groovyClass = groovyClass;
        this.services = Collections.unmodifiableList(services);
        this.injectors = injectors;
    }

    public List<Class<?>> getServiceInterfaces() {
        return services;
    }

    public TargetInvoker createTargetInvoker(String serviceName, Method method) {
        return new GroovyInvoker(method.getName(), this);
    }

    public Object createInstance() throws ObjectCreationException {
        GroovyObject instance;
        try {
            instance = groovyClass.newInstance();
        } catch (IllegalAccessException e) {
            throw new ObjectCreationException(e);
        } catch (InstantiationException e) {
            throw new ObjectCreationException(e);
        }

        // inject properties
        for (PropertyInjector injector : injectors) {
            injector.inject(instance);
        }

        // inject wires
        for (List<OutboundWire> referenceWires : getOutboundWires().values()) {
            for (OutboundWire<?> wire : referenceWires) {
                instance.setProperty(wire.getReferenceName(), wireService.createProxy(wire));
            }
        }
        return instance;
    }

    public GroovyObject getTargetInstance() throws TargetException {
        return scopeContainer.getInstance(this);
    }

    public T getServiceInstance() throws TargetException {
        //TODO this should return a default service from a wire
        return (T) scopeContainer.getInstance(this);
    }

    public Object getServiceInstance(String service) throws TargetException {
        InboundWire<?> wire = getInboundWire(service);
        if (wire == null) {
            TargetException e = new TargetException("ServiceDefinition not found"); // TODO better error message
            e.setIdentifier(service);
            throw e;
        }
        return wireService.createProxy(wire);
    }
}
