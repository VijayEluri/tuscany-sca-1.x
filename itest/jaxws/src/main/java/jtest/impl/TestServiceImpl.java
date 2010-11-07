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

package jtest.impl;

import org.osoa.sca.annotations.Service;

import jtest.AbstractException;
import jtest.ConcreteException;
import jtest.TestAbstract;
import jtest.TestService;

/**
 * The test service implementation
 */
@Service(TestService.class)
public class TestServiceImpl implements TestService {

    public void sendAbstract(TestAbstract data1, TestAbstract data2) {
        System.out.println("data1 is instance of class " + data1.getClass().getName());
        System.out.println("data2 is instance of class " + data2.getClass().getName());
        System.out.println(data1.getGreeting() + " " + data2.getGreeting());
    }

    public void throwAbstract() throws AbstractException {
        throw new ConcreteException();
    }
}
