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
package org.apache.tuscany.core.implementation.composite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.CoreRuntimeException;
import org.apache.tuscany.spi.builder.Connector;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.component.DuplicateNameException;
import org.apache.tuscany.spi.component.IllegalTargetException;
import org.apache.tuscany.spi.component.Reference;
import org.apache.tuscany.spi.component.SCAObject;
import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.Service;
import org.apache.tuscany.spi.component.TargetException;
import org.apache.tuscany.spi.event.Event;
import org.apache.tuscany.spi.extension.CompositeComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.wire.InboundWire;
import org.apache.tuscany.spi.wire.TargetInvoker;

import org.apache.tuscany.core.component.AutowireComponent;
import org.apache.tuscany.core.component.AutowireResolutionException;
import org.apache.tuscany.core.component.ComponentInitException;
import org.apache.tuscany.core.component.event.CompositeStart;
import org.apache.tuscany.core.component.event.CompositeStop;
import org.apache.tuscany.core.implementation.system.component.SystemService;

/**
 * The base implementation of a composite context
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractCompositeComponent extends CompositeComponentExtension implements AutowireComponent {

    public static final int DEFAULT_WAIT = 1000 * 60;

    // Blocking latch to ensure the module is initialized exactly once prior to servicing requests
    protected CountDownLatch initializeLatch = new CountDownLatch(1);

    protected final Object lock = new Object();

    // Indicates whether the module context has been initialized
    protected boolean initialized;

    // a mapping of service type to component name
    protected final Map<Class, SCAObject> autowireInternal = new ConcurrentHashMap<Class, SCAObject>();
    protected final Map<Class, SystemService> autowireExternal = new ConcurrentHashMap<Class, SystemService>();

    protected AutowireComponent autowireContext;

    protected ScopeContainer scopeContainer;

    /**
     * @param name            the name of the SCA composite
     * @param parent          the SCA composite parent
     * @param autowireContext the component that should be to resolve autowires
     * @param connector
     * @param propertyValues  the values of this composite's Properties
     */
    public AbstractCompositeComponent(String name,
                                      CompositeComponent parent,
                                      AutowireComponent autowireContext,
                                      Connector connector,
                                      Map<String, Document> propertyValues) {
        super(name, parent, connector, propertyValues);
        this.autowireContext = autowireContext;
    }

    public void setScopeContainer(ScopeContainer scopeContainer) {
        assert this.scopeContainer == null;
        this.scopeContainer = scopeContainer;
        addListener(scopeContainer);
    }

    public void start() {
        synchronized (lock) {
            if (lifecycleState != UNINITIALIZED && lifecycleState != STOPPED) {
                throw new IllegalStateException("Composite not in UNINITIALIZED state");
            }

            if (scopeContainer != null) {
                scopeContainer.start();
            }
            for (SCAObject child : children.values()) {
                child.start();
            }
            initializeLatch.countDown();
            initialized = true;
            lifecycleState = INITIALIZED;
        }
        publish(new CompositeStart(this, this));
    }

    public void stop() {
        if (lifecycleState == STOPPED) {
            return;
        }

        publish(new CompositeStop(this, this));
        for (SCAObject child : children.values()) {
            child.stop();
        }
        if (scopeContainer != null) {
            scopeContainer.stop();
        }

        // need to block a start until reset is complete
        initializeLatch = new CountDownLatch(2);
        lifecycleState = STOPPING;
        initialized = false;
        // allow initialized to be called
        initializeLatch.countDown();
        lifecycleState = STOPPED;
    }

    public void register(SCAObject child) {
        if (children.get(child.getName()) != null) {
            DuplicateNameException e = new DuplicateNameException("A context is already registered with name");
            e.setIdentifier(child.getName());
            e.addContextName(getName());
            throw e;
        }
        children.put(child.getName(), child);
        if (child instanceof Service) {
            Service service = (Service) child;
            synchronized (services) {
                services.add(service);
            }
            registerAutowire(service);
        } else if (child instanceof Reference) {
            Reference context = (Reference) child;
            synchronized (references) {
                references.add(context);
            }
            registerAutowire(context);
        } else if (child instanceof AtomicComponent) {
            AtomicComponent atomic = (AtomicComponent) child;
            registerAutowire(atomic);
        } else if (child instanceof CompositeComponent) {
            CompositeComponent component = (CompositeComponent) child;
            if (lifecycleState == RUNNING && component.getLifecycleState() == UNINITIALIZED) {
                component.start();
            }
            registerAutowire(component);
            addListener(component);
        }

    }

    public void publish(Event event) {
        checkInit();
        super.publish(event);
    }

    public <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        if (AutowireComponent.class.equals(instanceInterface)) {
            return instanceInterface.cast(this);
        }
        SCAObject context = autowireInternal.get(instanceInterface);
        if (context != null) {
            try {
                if (context instanceof AtomicComponent || context instanceof Reference
                    || context instanceof SystemService) {
                    return instanceInterface.cast(context.getServiceInstance());
                } else {
                    IllegalTargetException e = new IllegalTargetException("Autowire target must be a system "
                        + "service, atomic component, or reference");
                    e.setIdentifier(instanceInterface.getName());
                    e.addContextName(getName());
                    throw e;
                }
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else if (autowireContext != null) {
            try {
                // resolve to parent
                return autowireContext.resolveInstance(instanceInterface);
            } catch (AutowireResolutionException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException {
        SystemService service = autowireExternal.get(instanceInterface);
        if (service != null) {
            try {
                return instanceInterface.cast(service.getServiceInstance());
            } catch (CoreRuntimeException e) {
                e.addContextName(getName());
                throw e;
            }
        } else {
            return null;
        }
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return null;
    }

    public TargetInvoker createAsyncTargetInvoker(InboundWire wire, Operation operation) {
        return null;
    }

    public void prepare() {
        // Connect services and references first so that their wires are linked first
        List<SCAObject> childList = new ArrayList<SCAObject>();
        for (SCAObject child : children.values()) {
            if(child instanceof Component) {
                childList.add(child);
            } else {
                childList.add(0, child);
            }
        }
        for (SCAObject child : childList) {
            // connect all children
            // TODO for composite wires, should delegate down
            connector.connect(child);
            child.prepare();
        }
    }

    public Object getServiceInstance() throws TargetException {
        Service service = services.get(0);
        if (service == null) {
            throw new TargetException("Component has no services");
        }
        return service.getServiceInstance();
    }

    protected void registerAutowireExternal(Class<?> interfaze, SystemService context) {
        assert interfaze != null;
        if (autowireExternal.containsKey(interfaze)) {
            return;
        }
        autowireExternal.put(interfaze, context);
    }

    protected void registerAutowireInternal(Class<?> interfaze, SCAObject context) {
        // assert interfaze != null : "Interface was null";
        if (interfaze == null) {
            // The ServiceContract is not from Java
            return;
        }
        if (autowireInternal.containsKey(interfaze)) {
            return;
        }
        autowireInternal.put(interfaze, context);
    }

    protected void registerAutowire(CompositeComponent component) {
        List<Service> services = component.getServices();
        for (Service service : services) {
            registerAutowireInternal(service.getInterface(), service);
        }
    }

    protected void registerAutowire(AtomicComponent component) {
        List<Class<?>> services = component.getServiceInterfaces();
        for (Class<?> service : services) {
            registerAutowireInternal(service, component);
        }
    }

    protected void registerAutowire(Reference context) {
        registerAutowireInternal(context.getInterface(), context);
    }

    protected void registerAutowire(Service context) {
        if (context instanceof SystemService) {
            SystemService systemContext = (SystemService) context;
            registerAutowireExternal(systemContext.getInterface(), systemContext);
        }
    }

    /**
     * Blocks until the module context has been initialized
     */
    protected void checkInit() {
        if (!initialized) {
            try {
                /* block until the module has initialized */
                boolean success = initializeLatch.await(AbstractCompositeComponent.DEFAULT_WAIT,
                    TimeUnit.MILLISECONDS);
                if (!success) {
                    throw new ComponentInitException("Timeout waiting for context to initialize");
                }
            } catch (InterruptedException e) { // should not happen
            }
        }

    }
}
