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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.tuscany.spi.ObjectCreationException;
import org.apache.tuscany.spi.component.AtomicComponent;
import org.apache.tuscany.spi.component.TargetDestructionException;
import org.apache.tuscany.spi.component.TargetInitializationException;
import org.apache.tuscany.spi.component.TargetResolutionException;
import org.apache.tuscany.spi.extension.AbstractComponentExtension;
import org.apache.tuscany.spi.model.Operation;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.wire.TargetInvoker;
import org.apache.tuscany.spi.wire.Wire;

/**
 * An {@link org.apache.tuscany.spi.component.AtomicComponent} used when registering objects directly into a composite
 *
 * @version $$Rev$$ $$Date$$
 */
public class SystemSingletonAtomicComponent<S, T extends S> extends AbstractComponentExtension
    implements AtomicComponent {
    private T instance;
    private List<ServiceContract> serviceContracts = new ArrayList<ServiceContract>();

    public SystemSingletonAtomicComponent(URI name, ServiceContract<S> contract, T instance) {
        super(name);
        this.instance = instance;
        this.serviceContracts.add(contract);
    }

    public SystemSingletonAtomicComponent(URI name, List<ServiceContract<?>> services, T instance) {
        super(name);
        this.instance = instance;
        for (ServiceContract<?> contract : services) {
            serviceContracts.add(contract);
        }
    }

    public Scope getScope() {
        return Scope.COMPOSITE;
    }

    public boolean isEagerInit() {
        return false;
    }

    public boolean isDestroyable() {
        return false;
    }

    public int getInitLevel() {
        return 0;
    }

    public long getMaxIdleTime() {
        return -1;
    }

    public long getMaxAge() {
        return -1;
    }

    public T getTargetInstance() throws TargetResolutionException {
        return instance;
    }

    public void init(Object instance) throws TargetInitializationException {

    }

    public void destroy(Object instance) throws TargetDestructionException {

    }

    public Object createInstance() throws ObjectCreationException {
        throw new UnsupportedOperationException();
    }

    public void removeInstance() {
        throw new UnsupportedOperationException();
    }

    public boolean isOptimizable() {
        return true;
    }

    public void attachWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public void attachWires(List<Wire> wires) {
        throw new UnsupportedOperationException();
    }


    public List<Wire> getWires(String name) {
        throw new UnsupportedOperationException();
    }

    public void attachCallbackWire(Wire wire) {
        throw new UnsupportedOperationException();
    }

    public TargetInvoker createTargetInvoker(String targetName, Operation operation) {
        return null;
    }

    public List<ServiceContract> getServiceContracts() {
        return serviceContracts;
    }

}
