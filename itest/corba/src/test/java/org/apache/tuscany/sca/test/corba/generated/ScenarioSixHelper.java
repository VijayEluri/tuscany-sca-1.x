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
* org/apache/tuscany/sca/test/corba/generated/ScenarioSixHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from itest_scenario.idl
* sobota, 16 sierpie 2008 15:31:35 CEST
*/

abstract public class ScenarioSixHelper
{
  private static String  _id = "IDL:org/apache/tuscany/sca/test/corba/generated/ScenarioSix:1.0";

  public static void insert (org.omg.CORBA.Any a, org.apache.tuscany.sca.test.corba.generated.ScenarioSix that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static org.apache.tuscany.sca.test.corba.generated.ScenarioSix extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (org.apache.tuscany.sca.test.corba.generated.ScenarioSixHelper.id (), "ScenarioSix");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static org.apache.tuscany.sca.test.corba.generated.ScenarioSix read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_ScenarioSixStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, org.apache.tuscany.sca.test.corba.generated.ScenarioSix value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static org.apache.tuscany.sca.test.corba.generated.ScenarioSix narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.apache.tuscany.sca.test.corba.generated.ScenarioSix)
      return (org.apache.tuscany.sca.test.corba.generated.ScenarioSix)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.apache.tuscany.sca.test.corba.generated._ScenarioSixStub stub = new org.apache.tuscany.sca.test.corba.generated._ScenarioSixStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static org.apache.tuscany.sca.test.corba.generated.ScenarioSix unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof org.apache.tuscany.sca.test.corba.generated.ScenarioSix)
      return (org.apache.tuscany.sca.test.corba.generated.ScenarioSix)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      org.apache.tuscany.sca.test.corba.generated._ScenarioSixStub stub = new org.apache.tuscany.sca.test.corba.generated._ScenarioSixStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
