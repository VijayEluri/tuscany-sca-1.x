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


public class CustomerData extends TestData {

	private static Object[][] customerData = {
			{new Integer(1), "Williams", "1212 foobar lane"}, 
			{new Integer(2), "Daniel", "156 Brentfield Loop"},
			{new Integer(3), "Williams", "456 penny lane"},
			{new Integer(4), "Williams", "5000 pineville"},
			{new Integer(5), "Williams", "100000 firefly lane"}
			};

	public CustomerData(Connection connection) {
		super(connection, customerData);
	}

	
	public String getTableName() {
		return "CUSTOMER";
	}

	

}
