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
package helloworld;


public class HelloWorldImpl implements HelloWorldService {

    public String sayHello(String name) {
        return "Hello " + name;
    }

    public String sayHello2(String firstName, String lastName) {
        return "Hello " + firstName + " " + lastName;
    }
    
    public BeanA sayHello3(BeanA bean) {
        bean.setS("XYZ");
        return bean;
    }
    
    public String[] sayHello4(String[] name) {
    	String returnString = "Hello";
    	
    	for (int i=0 ; i < name.length; i++){
    		returnString += " " + name[i];
    	}
    	
    	String [] response = {returnString};
        return response;
    }
    
    public BeanA[] sayHello5(BeanA[] beans){
    	beans[0].setS("Hello " + beans[0].getS());
    	return beans;
    }
    
    public String[] sayHello6(BeanA[] beans, String[] names, String anotherName){
    	String returnString = "Hello";
    	
    	returnString += " " + beans[0].getS();
    	
    	for (int i=0 ; i < names.length; i++){
    		returnString += " " + names[i];
    	}
    	
    	returnString += " " + anotherName;
    	
    	String [] response = {returnString};
        return response;
    }  
}
