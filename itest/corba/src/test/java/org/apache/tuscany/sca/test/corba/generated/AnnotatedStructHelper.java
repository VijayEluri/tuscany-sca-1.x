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
* org/apache/tuscany/sca/test/corba/generated/AnnotatedStructHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from itest_scenario.idl
* sobota, 16 sierpie 2008 15:31:35 CEST
*/

abstract public class AnnotatedStructHelper
{
  private static String  _id = "IDL:org/apache/tuscany/sca/test/corba/generated/AnnotatedStruct/AnnotatedStruct:1.0";

  public static void insert (org.omg.CORBA.Any a, org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  private static boolean __active = false;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      synchronized (org.omg.CORBA.TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active)
          {
            return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
          }
          __active = true;
          org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [1];
          org.omg.CORBA.TypeCode _tcOf_members0 = null;
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_array_tc (2, _tcOf_members0 );
          _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (org.apache.tuscany.sca.test.corba.generated.StringArrayHelper.id (), "StringArray", _tcOf_members0);
          _members0[0] = new org.omg.CORBA.StructMember (
            "stringArray",
            _tcOf_members0,
            null);
          __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (org.apache.tuscany.sca.test.corba.generated.AnnotatedStructHelper.id (), "AnnotatedStruct", _members0);
          __active = false;
        }
      }
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct read (org.omg.CORBA.portable.InputStream istream)
  {
    org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct value = new org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct ();
    value.stringArray = org.apache.tuscany.sca.test.corba.generated.StringArrayHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.apache.tuscany.sca.test.corba.generated.AnnotatedStruct value)
  {
    org.apache.tuscany.sca.test.corba.generated.StringArrayHelper.write (ostream, value.stringArray);
  }

}
