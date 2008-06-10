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

package org.apache.tuscany.sca.binding.corba.testing;

import java.lang.reflect.Array;

import junit.framework.TestCase;

import org.apache.tuscany.sca.binding.corba.exceptions.CorbaException;
import org.apache.tuscany.sca.binding.corba.exceptions.RequestConfigurationException;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaRequest;
import org.apache.tuscany.sca.binding.corba.impl.reference.DynaCorbaResponse;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.DivByZeroException;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.DummyObject;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.NotSupportedException;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.SimpleStruct;
import org.apache.tuscany.sca.binding.corba.testing.hierarchy.SomeStruct;
import org.apache.tuscany.sca.binding.corba.testing.servants.ArraysSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.CalcServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.ObjectManagerServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.PrimitivesSetterServant;
import org.apache.tuscany.sca.binding.corba.testing.servants.TestObjectServant;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;

/**
 * @version $Rev$ $Date$
 */
public class CorbaTypesTestCase extends TestCase {

	private Process tnameservProcess;
	private Object refPrimitivesSetter;
	private Object refArraysSetter;
	private Object refTestObject;
	private Object refCalcObject;
	private Object refObjectManager;

	/**
	 * Spawns tnameserv process (must be in PATH). Initializes test servants and
	 * stores it's references so tests can use it.
	 */
	public void setUp() {
		try {
			String[] args = { "-ORBInitialPort", "11100" };

			tnameservProcess = Runtime.getRuntime().exec(
					"tnameserv " + args[0] + " " + args[1]);

			try {
				// let the tnameserv have time to start
				Thread.sleep(TestConstants.TNAMESERV_SPAWN_WAIT);
			} catch (Exception e) {
				e.printStackTrace();
			}

			ORB orb = ORB.init(args, null);
			Object nameService = orb.resolve_initial_references("NameService");
			NamingContext namingContext = NamingContextHelper
					.narrow(nameService);

			PrimitivesSetterServant singleSetter = new PrimitivesSetterServant();
			ArraysSetterServant arraysSetter = new ArraysSetterServant();
			TestObjectServant complexObject = new TestObjectServant();
			CalcServant calcObject = new CalcServant();
			ObjectManagerServant objectManager = new ObjectManagerServant();

			orb.connect(singleSetter);
			orb.connect(arraysSetter);

			NameComponent nc;
			NameComponent[] path;

			nc = new NameComponent("PrimitivesSetter", "");
			path = new NameComponent[] { nc };
			namingContext.rebind(path, singleSetter);

			nc = new NameComponent("ArraysSetter", "");
			path = new NameComponent[] { nc };
			namingContext.rebind(path, arraysSetter);

			nc = new NameComponent("TestObject", "");
			path = new NameComponent[] { nc };
			namingContext.rebind(path, complexObject);

			nc = new NameComponent("CalcObject", "");
			path = new NameComponent[] { nc };
			namingContext.rebind(path, calcObject);
			
			nc = new NameComponent("ObjectManager", "");
			path = new NameComponent[] { nc };
			namingContext.rebind(path, objectManager);

			NamingContextExt nce = NamingContextExtHelper.narrow(orb
					.resolve_initial_references("NameService"));

			refArraysSetter = nce.resolve(nce.to_name("ArraysSetter"));
			refPrimitivesSetter = nce.resolve(nce.to_name("PrimitivesSetter"));
			refTestObject = nce.resolve(nce.to_name("TestObject"));
			refCalcObject = nce.resolve(nce.to_name("CalcObject"));
			refObjectManager = nce.resolve(nce.to_name("ObjectManager"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kills previously spawned tnameserv process.
	 */
	public void tearDown() {
		tnameservProcess.destroy();
		try {
			// let the tnameserv have time to die
			Thread.sleep(TestConstants.TNAMESERV_SPAWN_WAIT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests remote operation, basing on given reference, operation name,
	 * arguments, expected return type and content
	 * 
	 * @param ref
	 *            remote object
	 * @param operationName
	 *            operation to invoke
	 * @param clazz
	 *            expected return type
	 * @param arguments
	 *            array of operation arguments
	 * @param equalTo
	 *            expected return content
	 */
	private void dynaTestInvoker(Object ref, String operationName,
			Class<?> clazz, java.lang.Object[] arguments,
			java.lang.Object equalTo) {

		try {
			DynaCorbaRequest request = new DynaCorbaRequest(ref, operationName);
			request.setOutputType(clazz);
			for (int i = 0; arguments != null && i < arguments.length; i++) {
				request.addArgument(arguments[i]);
			}

			DynaCorbaResponse response = request.invoke();
			java.lang.Object content = (java.lang.Object) response.getContent();
			assertTrue(content.getClass().equals(clazz));
			if (equalTo != null && equalTo.getClass().isArray()) {
				for (int i = 0; i < Array.getLength(equalTo); i++) {
					assertTrue(Array.get(content, i).equals(
							Array.get(equalTo, i)));
				}
			} else {
				assertTrue(content.equals(equalTo));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error while invoking " + operationName);
		}
	}

	/**
	 * Tests passing (and getting as result) varied primitives
	 */
	public void test_setPrimitives() {

		dynaTestInvoker(refPrimitivesSetter, "setBoolean", Boolean.class,
				new Boolean[] { true }, true);
		dynaTestInvoker(refPrimitivesSetter, "setOctet", Byte.class,
				new Byte[] { 1 }, (byte) 1);
		dynaTestInvoker(refPrimitivesSetter, "setChar", Character.class,
				new Character[] { 'A' }, 'A');
		dynaTestInvoker(refPrimitivesSetter, "setShort", Short.class,
				new Short[] { 1 }, (short) 1);
		dynaTestInvoker(refPrimitivesSetter, "setLong", Integer.class,
				new Integer[] { 1 }, (int) 1);
		dynaTestInvoker(refPrimitivesSetter, "setLongLong", Long.class,
				new Long[] { (long) 1 }, (long) 1);
		dynaTestInvoker(refPrimitivesSetter, "setFloat", Float.class,
				new Float[] { (float) 1 }, (float) 1);
		dynaTestInvoker(refPrimitivesSetter, "setDouble", Double.class,
				new Double[] { (double) 1 }, (double) 1);
		dynaTestInvoker(refPrimitivesSetter, "setString", String.class,
				new String[] { "1" }, "1");

	}

	/**
	 * Tests passing (and getting as result) varied types sequences
	 */
	public void test_setArrays() {

		dynaTestInvoker(refArraysSetter, "setBoolean", Boolean[].class,
				new Boolean[][] { new Boolean[] { false, true } },
				new Boolean[] { false, true });

		dynaTestInvoker(refArraysSetter, "setChar", Character[].class,
				new Character[][] { new Character[] { 'A', 'B' } },
				new Character[] { 'A', 'B' });

		dynaTestInvoker(refArraysSetter, "setOctet", Byte[].class,
				new Byte[][] { new Byte[] { 1, 2 } }, new Byte[] { 1, 2 });

		dynaTestInvoker(refArraysSetter, "setShort", Short[].class,
				new Short[][] { new Short[] { 1, 2 } }, new Short[] { 1, 2 });

		dynaTestInvoker(refArraysSetter, "setLong", Integer[].class,
				new Integer[][] { new Integer[] { 1, 2 } }, new Integer[] { 1,
						2 });

		dynaTestInvoker(refArraysSetter, "setLongLong", Long[].class,
				new Long[][] { new Long[] { new Long(1), new Long(2) } },
				new Long[] { new Long(1), new Long(2) });

		dynaTestInvoker(
				refArraysSetter,
				"setFloat",
				Float[].class,
				new Float[][] { new Float[] { new Float(1.0), new Float(2.0) } },
				new Float[] { new Float(1.0), new Float(2.0) });

		dynaTestInvoker(refArraysSetter, "setDouble", Double[].class,
				new Double[][] { new Double[] { new Double(1.0),
						new Double(2.0) } }, new Double[] { new Double(1.0),
						new Double(2.0) });

		dynaTestInvoker(refArraysSetter, "setString", String[].class,
				new String[][] { new String[] { "A", "B" } }, new String[] {
						"A", "B" });

	}

	/**
	 * Tests passing (and getting as result) complex structure
	 */
	public void test_TestObject_setStruct() {
		DynaCorbaRequest request = new DynaCorbaRequest(refTestObject,
				"setStruct");

		SomeStruct struct = new SomeStruct();
		SimpleStruct inner = new SimpleStruct();
		inner.field1 = TestConstants.STR_1;
		inner.field2 = TestConstants.INT_1;
		struct.innerStruct = inner;
		struct.str_list = TestConstants.STR_ARR_2;
		struct.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
		struct.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
		struct.str = TestConstants.STR_1;

		request.addArgument(struct);
		request.setOutputType(SomeStruct.class);

		try {
			DynaCorbaResponse response = request.invoke();
			SomeStruct result = (SomeStruct) response.getContent();
			int sum = 0;
			for (int i = 0; i < result.twoDimSeq.length; i++) {
				for (int j = 0; j < result.twoDimSeq[i].length; j++) {
					sum++;
					assertEquals(TestConstants.INT_ARRAY_2_DIM[i][j],
							result.twoDimSeq[i][j]);
				}
			}
			sum = 0;
			for (int i = 0; i < result.threeDimSeq.length; i++) {
				for (int j = 0; j < result.threeDimSeq[i].length; j++) {
					for (int k = 0; k < result.threeDimSeq[i][j].length; k++) {
						sum++;
						assertEquals(TestConstants.INT_ARRAY_3_DIM[i][j][k],
								result.threeDimSeq[i][j][k]);
					}
				}
			}
			assertEquals(TestConstants.STR_1, result.str);
			assertEquals(TestConstants.STR_ARR_2[0], result.str_list[0]);
			assertEquals(TestConstants.STR_ARR_2[1], result.str_list[1]);
			assertEquals(TestConstants.STR_1, result.innerStruct.field1);
			assertEquals(TestConstants.INT_1, result.innerStruct.field2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured during tests: " + e);
		}
	}

	/**
	 * Test passing (and getting as result) simple two-field structure
	 */
	public void test_TestObject_setSimpleStruct() {
		SimpleStruct struct = new SimpleStruct();
		struct.field1 = TestConstants.STR_1;
		struct.field2 = TestConstants.INT_1;
		DynaCorbaRequest request = new DynaCorbaRequest(refTestObject,
				"setSimpleStruct");
		request.setOutputType(SimpleStruct.class);
		request.addArgument(struct);
		try {
			DynaCorbaResponse response = request.invoke();
			SimpleStruct retStruct = (SimpleStruct) response.getContent();
			assertTrue(retStruct.field1.equals(struct.field1)
					&& retStruct.field2 == struct.field2);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured during tests: " + e);
		}
	}

	/**
	 * Tests passing (and getting as result) two dim. sequence of long.
	 */
	public void test_TestObject_setLongSeq2() {
		int[][] arr1 = new int[2][2];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				arr1[i][j] = (int) (Math.random() * 1000);
			}
		}
		DynaCorbaRequest request = new DynaCorbaRequest(refTestObject,
				"setLongSeq2");
		request.setOutputType(arr1.getClass());
		request.addArgument(arr1);
		try {
			DynaCorbaResponse response = request.invoke();
			int[][] arr2 = (int[][]) response.getContent();
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 2; j++) {
					assertEquals(arr1[i][j], arr2[i][j]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception occured during tests: " + e);
		}
	}

	/**
	 * Tests passing multiple complex attributes.
	 */
	public void test_TestObject_pickStructFromArgs() {
		SomeStruct arg1 = new SomeStruct();
		SomeStruct arg2 = new SomeStruct();
		SomeStruct arg3 = new SomeStruct();

		SimpleStruct inner = new SimpleStruct();
		inner.field1 = TestConstants.STR_1;
		inner.field2 = TestConstants.INT_1;

		arg1.innerStruct = inner;
		arg2.innerStruct = inner;
		arg3.innerStruct = inner;

		arg1.str = TestConstants.STR_1;
		arg2.str = TestConstants.STR_2;
		arg3.str = TestConstants.STR_3;

		arg1.str_list = TestConstants.STR_ARR_1;
		arg2.str_list = TestConstants.STR_ARR_2;
		arg3.str_list = TestConstants.STR_ARR_2;

		arg1.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
		arg2.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;
		arg3.threeDimSeq = TestConstants.INT_ARRAY_3_DIM;

		arg1.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
		arg2.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;
		arg3.twoDimSeq = TestConstants.INT_ARRAY_2_DIM;

		DynaCorbaRequest request = new DynaCorbaRequest(refTestObject,
				"pickStructFromArgs");
		request.setOutputType(SomeStruct.class);
		request.addArgument(arg1);
		request.addArgument(arg2);
		request.addArgument(arg3);
		request.addArgument(1);
		try {

			DynaCorbaResponse response = request.invoke();
			SomeStruct result = (SomeStruct) response.getContent();

			// just make sure that servant returned right structure
			assertTrue(result.str.equals(TestConstants.STR_1));
		} catch (Exception e) {
			fail("Exception occured during tests " + e);
			e.printStackTrace();
		}

	}

	/**
	 * Tests handling user defined remote exception (single declared)
	 */
	public void test_singleException() {
		DynaCorbaRequest request1 = new DynaCorbaRequest(refCalcObject, "div");
		request1.addArgument(2d);
		request1.addArgument(2d);
		request1.setOutputType(Double.class);
		request1.addExceptionType(DivByZeroException.class);
		try {
			request1.invoke();
		} catch (Exception e) {
			fail();
		}

		DynaCorbaRequest request2 = new DynaCorbaRequest(refCalcObject, "div");
		request2.addArgument(2d);
		request2.addArgument(0d);
		request2.setOutputType(Double.class);
		request2.addExceptionType(DivByZeroException.class);
		try {
			request2.invoke();
		} catch (DivByZeroException e) {
			assertTrue(e.info != null && e.arguments != null
					&& e.arguments.arg1 == 2 && e.arguments.arg2 == 0);
		} catch (Exception exc) {
			fail();
		}
	}

	/**
	 * Tests handling user defined multiple exceptions
	 */
	public void test_multipleExceptions() {
		DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject,
				"divForSmallArgs");
		request.addArgument(101d);
		request.addArgument(101d);
		request.setOutputType(Double.class);
		request.addExceptionType(DivByZeroException.class);
		request.addExceptionType(NotSupportedException.class);
		try {
			request.invoke();
		} catch (Exception e) {
			assertTrue(e instanceof RequestConfigurationException);
		}
	}

	/**
	 * Tests handling exceptions while user defined no exceptions
	 */
	public void test_noDeclaredException() {
		DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject, "div");
		request.addArgument(1d);
		request.addArgument(0d);
		request.setOutputType(Double.class);
		try {
			request.invoke();
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof RequestConfigurationException);
		}
	}

	/**
	 * Tests handling non existing operation situation
	 */
	public void test_noOperationException() {
		DynaCorbaRequest request = new DynaCorbaRequest(refCalcObject,
				"thisOperationSurelyDoesNotExist");
		try {
			request.invoke();
			fail();
		} catch (Exception e) {
			assertTrue(e instanceof CorbaException);
		}
	}
	
	/**
	 * Tests obtaining references to other objects and using them with specified user interface
	 */
	public void test_enchancedReferences() {
		try {
			DynaCorbaRequest request = new DynaCorbaRequest(refObjectManager, "getDummyObject");
			request.setOutputType(DummyObject.class);
			DynaCorbaResponse response = request.invoke();
			DummyObject dummy = (DummyObject) response.getContent();
			DummyObject dummy2 = dummy.cloneObject();
			assertNotSame(dummy.getLong(), dummy2.getLong());
		} catch (Exception e) {
			fail();
		}
	}

}
