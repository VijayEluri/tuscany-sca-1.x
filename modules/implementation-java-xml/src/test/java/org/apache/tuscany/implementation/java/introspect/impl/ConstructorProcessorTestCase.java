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
package org.apache.tuscany.implementation.java.introspect.impl;

import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getProperty;
import static org.apache.tuscany.implementation.java.introspect.impl.ModelHelper.getReference;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.tuscany.assembly.Multiplicity;
import org.apache.tuscany.implementation.java.impl.JavaImplementationDefinition;
import org.apache.tuscany.implementation.java.impl.Parameter;
import org.apache.tuscany.implementation.java.introspect.impl.ConstructorProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.DuplicateConstructorException;
import org.apache.tuscany.implementation.java.introspect.impl.InvalidConstructorException;
import org.apache.tuscany.implementation.java.introspect.impl.PropertyProcessor;
import org.apache.tuscany.implementation.java.introspect.impl.ReferenceProcessor;
import org.apache.tuscany.interfacedef.java.introspect.DefaultJavaInterfaceIntrospector;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * @version $Rev$ $Date$
 */
public class ConstructorProcessorTestCase extends TestCase {
    private ConstructorProcessor processor = new ConstructorProcessor();

    public void testDuplicateConstructor() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        try {
            processor.visitClass(BadFoo.class, type);
            fail();
        } catch (DuplicateConstructorException e) {
            // expected
        }
    }

    public void testConstructorAnnotation() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Foo> ctor1 = Foo.class.getConstructor(String.class);
        processor.visitConstructor(ctor1, type);
        assertEquals("foo", type.getConstructorDefinition().getParameters()[0].getName());
    }

    public void testNoAnnotation() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<NoAnnotation> ctor1 = NoAnnotation.class.getConstructor();
        processor.visitConstructor(ctor1, type);
        assertNull(type.getConstructorDefinition());
    }

    public void testBadAnnotation() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<BadAnnotation> ctor1 = BadAnnotation.class.getConstructor(String.class, Foo.class);
        try {
            processor.visitConstructor(ctor1, type);
            fail();
        } catch (InvalidConstructorException e) {
            // expected
        }
    }

    public void testMixedParameters() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Mixed> ctor1 = Mixed.class.getConstructor(String.class, String.class, String.class);
        processor.visitConstructor(ctor1, type);

        ReferenceProcessor referenceProcessor = new ReferenceProcessor(new DefaultJavaInterfaceIntrospector());
        PropertyProcessor propertyProcessor = new PropertyProcessor();
        Parameter[] parameters = type.getConstructorDefinition().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
        }

        assertEquals("_ref0", parameters[0].getName());
        assertEquals("foo", parameters[1].getName());
        assertEquals("bar", parameters[2].getName());
    }

    private static class BadFoo {

        @org.osoa.sca.annotations.Constructor("foo")
        public BadFoo(String foo) {

        }

        @org.osoa.sca.annotations.Constructor( {"foo", "bar"})
        public BadFoo(String foo, String bar) {

        }
    }

    private static class Foo {
        @org.osoa.sca.annotations.Constructor("foo")
        public Foo(String foo) {

        }
    }

    private static class NoAnnotation {
        public NoAnnotation() {
        }
    }

    private static class BadAnnotation {
        @org.osoa.sca.annotations.Constructor("foo")
        public BadAnnotation(String foo, Foo ref) {
        }
    }

    public static final class Mixed {
        @org.osoa.sca.annotations.Constructor
        public Mixed(@Reference
        String param1, @Property(name = "foo")
        String param2, @Reference(name = "bar")
        String param3) {
        }
    }

    public static final class Multiple {
        @org.osoa.sca.annotations.Constructor
        public Multiple(@Reference
        Collection<String> param1, @Property(name = "foo")
        String[] param2, @Reference(name = "bar", required = true)
        List<String> param3, @Property(name = "abc")
        Set<String> param4, @Reference(name = "xyz")
        String[] param5) {
        }
    }

    public void testMultiplicity() throws Exception {
        JavaImplementationDefinition type = new JavaImplementationDefinition();
        Constructor<Multiple> ctor1 = Multiple.class.getConstructor(Collection.class,
                                                                    String[].class,
                                                                    List.class,
                                                                    Set.class,
                                                                    String[].class);
        processor.visitConstructor(ctor1, type);
        ReferenceProcessor referenceProcessor = new ReferenceProcessor(new DefaultJavaInterfaceIntrospector());
        PropertyProcessor propertyProcessor = new PropertyProcessor();
        Parameter[] parameters = type.getConstructorDefinition().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            referenceProcessor.visitConstructorParameter(parameters[i], type);
            propertyProcessor.visitConstructorParameter(parameters[i], type);
        }

        org.apache.tuscany.assembly.Reference ref0 = getReference(type, "_ref0");
        assertNotNull(ref0);
        assertEquals(Multiplicity.ZERO_N, ref0.getMultiplicity());
        org.apache.tuscany.assembly.Reference ref1 = getReference(type, "bar");
        assertNotNull(ref1);
        assertEquals(Multiplicity.ONE_N, ref1.getMultiplicity());
        org.apache.tuscany.assembly.Reference ref2 = getReference(type, "xyz");
        assertNotNull(ref2);
        assertEquals(Multiplicity.ZERO_N, ref2.getMultiplicity());
        org.apache.tuscany.assembly.Property prop1 = getProperty(type, "foo");
        assertNotNull(prop1);
        assertTrue(prop1.isMany());
        org.apache.tuscany.assembly.Property prop2 = getProperty(type, "abc");
        assertNotNull(prop2);
        assertTrue(prop2.isMany());
    }

}
