package org.apache.tuscany.sca.binding.corba.testing.generated;


/**
* Tester/long_long_listHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from tester.idl
* pi�tek, 30 maj 2008 17:04:42 CEST
*/

public final class long_long_listHolder implements org.omg.CORBA.portable.Streamable
{
  public long value[] = null;

  public long_long_listHolder ()
  {
  }

  public long_long_listHolder (long[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = org.apache.tuscany.sca.binding.corba.testing.generated.long_long_listHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    org.apache.tuscany.sca.binding.corba.testing.generated.long_long_listHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return org.apache.tuscany.sca.binding.corba.testing.generated.long_long_listHelper.type ();
  }

}