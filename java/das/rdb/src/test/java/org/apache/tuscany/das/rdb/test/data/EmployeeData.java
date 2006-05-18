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
import java.sql.Types;

import org.apache.tuscany.das.rdb.test.framework.TestDataWithExplicitColumns;


public class EmployeeData extends TestDataWithExplicitColumns {

	private static int[] columnTypes = {Types.VARCHAR, Types.VARCHAR, Types.SMALLINT};

	private static Object[][] employeeData = { 
        { "John Jones", "E0001", new Boolean(false) },
		{ "Mary Smith", "E0002", new Boolean(true)},
		{ "Jane Doe", "E0003", new Boolean(false)},
		{ "Al Smith", "E0004", new Boolean(true) } };

	private static String[] employeeColumns = { "NAME", "SN", "MANAGER" };
	
	public EmployeeData(Connection connection) {
		super(connection, employeeData, employeeColumns, columnTypes);
	}
	
	public String getTableName() {
		return "EMPLOYEE";
	}

}
