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

package org.apache.tuscany.sca.test.corba.generated;


/**
* org/apache/tuscany/sca/test/corba/generated/_ScenarioSixImplBase.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from itest_scenario.idl
* niedziela, 17 sierpie 2008 23:35:36 CEST
*/

public abstract class _ScenarioSixImplBase extends org.omg.CORBA.portable.ObjectImpl
                implements org.apache.tuscany.sca.test.corba.generated.ScenarioSix, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors
  public _ScenarioSixImplBase ()
  {
  }

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("passStringArray", new java.lang.Integer (0));
    _methods.put ("passAnnotatedStruct", new java.lang.Integer (1));
    _methods.put ("passRichUnion", new java.lang.Integer (2));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // org/apache/tuscany/sca/test/corba/generated/ScenarioSix/passStringArray
       {
         String arg[][] = org.apache.tuscany.sca.test.corba.generated.StringArrayHelper.read (in);
         String $result[][] = null;
         $result = this.passStringArray (arg);
         out = $rh.createReply();
         org.apache.tuscany.sca.test.corba.generated.StringArrayHelper.write (out, $result);
         break;
       }

       case 1:  // org/apache/tuscany/sca/test/corba/generated/ScenarioSix/passAnnotatedStruct
       {
         org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct arg = org.apache.tuscany.sca.test.corba.generated.AnnotatedStructHelper.read (in);
         org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct $result = null;
         $result = this.passAnnotatedStruct (arg);
         out = $rh.createReply();
         org.apache.tuscany.sca.test.corba.generated.AnnotatedStructHelper.write (out, $result);
         break;
       }

       case 2:  // org/apache/tuscany/sca/test/corba/generated/ScenarioSix/passRichUnion
       {
         org.apache.tuscany.sca.test.corba.generated.RichUnion arg = org.apache.tuscany.sca.test.corba.generated.RichUnionHelper.read (in);
         org.apache.tuscany.sca.test.corba.generated.RichUnion $result = null;
         $result = this.passRichUnion (arg);
         out = $rh.createReply();
         org.apache.tuscany.sca.test.corba.generated.RichUnionHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:org/apache/tuscany/sca/test/corba/generated/ScenarioSix:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }


} // class _ScenarioSixImplBase
