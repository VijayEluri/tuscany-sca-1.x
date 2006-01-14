/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.das.rdb.test.data;

import java.sql.Connection;

import org.apache.tuscany.das.rdb.test.framework.TestData;


public class OrderDetailsData extends TestData {

	//CREATE TABLE ORDERDETAILS (ORDERID INT NOT NULL, PRODUCTID INT NOT NULL, PRICE FLOAT, PRIMARY KEY (ORDERID, PRODUCTID))

	public static Object[][] orderDetailsData = {
			{new Integer(1), new Integer(1), new Float(1.1)},
			{new Integer(1), new Integer(2), new Float(1.2)},
			{new Integer(2), new Integer(1), new Float(2.1)},
			{new Integer(2), new Integer(2), new Float(2.2)}
	};
	
	public OrderDetailsData(Connection c) {
		super(c, orderDetailsData);
	}
	
	public String getTableName() {
		return "ORDERDETAILS";
	}

}
