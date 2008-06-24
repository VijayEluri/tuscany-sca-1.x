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

package org.apache.tuscany.sca.binding.corba.testing.generated;

/**
* org/apache/tuscany/sca/binding/corba/testing/generated/_TestObjectImplBase.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from general_tests.idl
* monday, 23 june 2008 14:12:28 CEST
*/

public abstract class _TestObjectImplBase extends org.omg.CORBA.portable.ObjectImpl implements
    org.apache.tuscany.sca.binding.corba.testing.generated.TestObject, org.omg.CORBA.portable.InvokeHandler {

    // Constructors
    public _TestObjectImplBase() {
    }

    private static java.util.Hashtable _methods = new java.util.Hashtable();
    static {
        _methods.put("pickStructFromArgs", new java.lang.Integer(0));
        _methods.put("setStruct", new java.lang.Integer(1));
        _methods.put("setSimpleStruct", new java.lang.Integer(2));
        _methods.put("setLongSeq1", new java.lang.Integer(3));
        _methods.put("setLongSeq2", new java.lang.Integer(4));
        _methods.put("setLongSeq3", new java.lang.Integer(5));
    }

    public org.omg.CORBA.portable.OutputStream _invoke(String $method,
                                                       org.omg.CORBA.portable.InputStream in,
                                                       org.omg.CORBA.portable.ResponseHandler $rh) {
        org.omg.CORBA.portable.OutputStream out = null;
        java.lang.Integer __method = (java.lang.Integer)_methods.get($method);
        if (__method == null)
            throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

        switch (__method.intValue()) {
            case 0: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/pickStructFromArgs
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct arg1 =
                    org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.read(in);
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct arg2 =
                    org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.read(in);
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct arg3 =
                    org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.read(in);
                int structNumber = in.read_long();
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct $result = null;
                $result = this.pickStructFromArgs(arg1, arg2, arg3, structNumber);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.write(out, $result);
                break;
            }

            case 1: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/setStruct
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct arg =
                    org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.read(in);
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStruct $result = null;
                $result = this.setStruct(arg);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.SomeStructHelper.write(out, $result);
                break;
            }

            case 2: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/setSimpleStruct
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHolder arg =
                    new org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHolder();
                arg.value = org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHelper.read(in);
                org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStruct $result = null;
                $result = this.setSimpleStruct(arg);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHelper.write(out, $result);
                org.apache.tuscany.sca.binding.corba.testing.generated.SimpleStructHelper.write(out, arg.value);
                break;
            }

            case 3: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/setLongSeq1
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Holder arg =
                    new org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Holder();
                arg.value = org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Helper.read(in);
                int $result[] = null;
                $result = this.setLongSeq1(arg);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Helper.write(out, $result);
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq1Helper.write(out, arg.value);
                break;
            }

            case 4: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/setLongSeq2
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Holder arg =
                    new org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Holder();
                arg.value = org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Helper.read(in);
                int $result[][] = null;
                $result = this.setLongSeq2(arg);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Helper.write(out, $result);
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq2Helper.write(out, arg.value);
                break;
            }

            case 5: // org/apache/tuscany/sca/binding/corba/testing/generated/TestObject/setLongSeq3
            {
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Holder arg =
                    new org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Holder();
                arg.value = org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Helper.read(in);
                int $result[][][] = null;
                $result = this.setLongSeq3(arg);
                out = $rh.createReply();
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Helper.write(out, $result);
                org.apache.tuscany.sca.binding.corba.testing.generated.long_seq3Helper.write(out, arg.value);
                break;
            }

            default:
                throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }

        return out;
    } // _invoke

    // Type-specific CORBA::Object operations
    private static String[] __ids = {"IDL:org/apache/tuscany/sca/binding/corba/testing/generated/TestObject:1.0"};

    public String[] _ids() {
        return (String[])__ids.clone();
    }

} // class _TestObjectImplBase
