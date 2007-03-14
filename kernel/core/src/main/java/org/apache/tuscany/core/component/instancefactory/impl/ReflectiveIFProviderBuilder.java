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
package org.apache.tuscany.core.component.instancefactory.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.core.component.ReflectiveInstanceFactoryProvider;
import org.apache.tuscany.core.component.instancefactory.IFProviderBuilderException;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSiteMapping;
import org.apache.tuscany.core.model.physical.instancefactory.InjectionSource;
import org.apache.tuscany.core.model.physical.instancefactory.MemberSite;
import org.apache.tuscany.core.model.physical.instancefactory.ReflectiveIFProviderDefinition;

/**
 * IF provider builder for reflective IF provider.
 *
 * @version $Date$ $Revision$
 */
public class ReflectiveIFProviderBuilder<T> extends
    AbstractIFProviderBuilder<ReflectiveInstanceFactoryProvider<T>, ReflectiveIFProviderDefinition> {

    @Override
    protected Class<ReflectiveIFProviderDefinition> getIfpdClass() {
        return ReflectiveIFProviderDefinition.class;
    }

    @SuppressWarnings("unchecked")
    public ReflectiveInstanceFactoryProvider<T> build(ReflectiveIFProviderDefinition ifpd, ClassLoader cl)
        throws IFProviderBuilderException {

        try {

            Class implClass = cl.loadClass(ifpd.getImplementationClass());

            Constructor ctr = getConstructor(ifpd, cl, implClass);

            Method initMethod = getInitMethod(ifpd, implClass);

            Method destroyMethod = getDestroyMethod(ifpd, implClass);

            List<InjectionSource> ctrInjectSites = ifpd.getCdiSources();

            Map<InjectionSource, Member> injectionSites = getInjectionSites(ifpd, implClass);
            return new ReflectiveInstanceFactoryProvider<T>(ctr,
                ctrInjectSites,
                injectionSites,
                initMethod,
                destroyMethod);

        } catch (ClassNotFoundException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchMethodException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (NoSuchFieldException ex) {
            throw new IFProviderBuilderException(ex);
        } catch (IntrospectionException ex) {
            throw new IFProviderBuilderException(ex);
        }
    }

    /*
     * Get injection sites.
     */
    private Map<InjectionSource, Member> getInjectionSites(ReflectiveIFProviderDefinition ifpd, Class implClass)
        throws NoSuchFieldException, IntrospectionException, IFProviderBuilderException {

        Map<InjectionSource, Member> injectionSites = new HashMap<InjectionSource, Member>();
        for (InjectionSiteMapping injectionSite : ifpd.getInjectionSites()) {

            InjectionSource source = injectionSite.getSource();
            MemberSite memberSite = injectionSite.getSite();
            ElementType elementType = memberSite.getElementType();
            String name = memberSite.getName();

            Member member = null;
            if (memberSite.getElementType() == ElementType.FIELD) {
                member = implClass.getDeclaredField(name);
            } else if (elementType == ElementType.METHOD) {
                for (PropertyDescriptor pd : Introspector.getBeanInfo(implClass).getPropertyDescriptors()) {
                    if (name.equals(pd.getName())) {
                        member = pd.getWriteMethod();
                    }
                }
            }
            if (member == null) {
                throw new IFProviderBuilderException("Unknown injection site " + name);
            }
            injectionSites.put(source, member);
        }
        return injectionSites;
    }

    /*
     * Get destroy method.
     */
    private Method getDestroyMethod(ReflectiveIFProviderDefinition ifpd, Class implClass) throws NoSuchMethodException {
        Method destroyMethod = null;
        String destroyMethodName = ifpd.getDestroyMethod();
        if (destroyMethod != null) {
            destroyMethod = implClass.getDeclaredMethod(destroyMethodName);
        }
        return destroyMethod;
    }

    /*
     * Get init method.
     */
    private Method getInitMethod(ReflectiveIFProviderDefinition ifpd, Class implClass) throws NoSuchMethodException {
        Method initMethod = null;
        String initMethodName = ifpd.getInitMethod();
        if (initMethodName != null) {
            initMethod = implClass.getDeclaredMethod(initMethodName);
        }
        return initMethod;
    }

    /*
     * Gets the matching constructor.
     */
    private Constructor getConstructor(ReflectiveIFProviderDefinition ifpd, ClassLoader cl, Class implClass)
        throws ClassNotFoundException, NoSuchMethodException {
        Class[] ctrArgs = new Class[ifpd.getConstructorArguments().size()];
        int i = 0;
        for (String ctrArgClass : ifpd.getConstructorArguments()) {
            ctrArgs[i++] = cl.loadClass(ctrArgClass);
        }
        Constructor ctr = implClass.getDeclaredConstructor(ctrArgs);
        return ctr;
    }

}
