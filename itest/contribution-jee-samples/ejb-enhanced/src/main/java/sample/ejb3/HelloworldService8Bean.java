/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package sample.ejb3;

import javax.ejb.Stateless;

import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;

/**
 * HelloworldService EJB implementation  SCA enhancement

 */
@Stateless
public class HelloworldService8Bean implements HelloworldService8, HelloworldLocal8 {
    
    @Reference
    protected HelloworldService8 hwReference;
    
    @Property 
    protected String hwProperty;

    public String getGreetings(String name) {
        String greeting = "Hello remote " + name;
        System.out.println(greeting);
        return greeting;
    }
    
    public String getGreetingsLocal(String name) {
        String greeting = "Hello local " + name;
        System.out.println(greeting);
        return greeting;
    }

}
