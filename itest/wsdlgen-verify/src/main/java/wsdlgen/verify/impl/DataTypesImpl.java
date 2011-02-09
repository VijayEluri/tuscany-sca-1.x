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

package wsdlgen.verify.impl;

import java.util.List;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import commonj.sdo.DataObject;
import wsdlgen.verify.ComplexNumber;
import wsdlgen.verify.DataTypes;

public class DataTypesImpl implements DataTypes {

    public void testSimpleInt(int simple) {
    }

    public void testSimpleArrayInt(int[] simple) {
    }

    public void testSimpleMultiArrayInt(int[][] simple) {
    }

    public void testSimpleMulti3ArrayInt(int[][][] simple) {
    }

    public void testList(List any) {
    }

    public void testSimpleListString(List<String> simple) {
    }

    public List<String> testReturnSimpleListString() {
        return null;
    }

    public void testListByteArray(List<byte[]> byteArrayList) {
    }

    public void testListWildcard(List<?> wild) {
    }

    public void testComplex(ComplexNumber complex) {
    }

    public void testByteArray(byte[] byteArray) {
    }

    public void testException() throws Exception {
    }

    public DataObject testDynamicSDO() {
        return null;
    }

    public void testWebParam(int simple) {
    }

    public void testWebParamArray(int[] array) {
    }
}