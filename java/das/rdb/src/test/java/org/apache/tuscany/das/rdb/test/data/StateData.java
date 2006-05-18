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
import java.sql.SQLException;

import org.apache.tuscany.das.rdb.test.framework.TestData;


public class StateData extends TestData {

	public StateData(Connection c) {
		super(c, stateData);
	}

	public String getTableName() {
		return "STATES";
	}
	
	private static Object[][] stateData = {
		{new Integer(1), "NC"}, 
		{new Integer(2), "CO"},
		{new Integer(3), "CA"}
		};

	public void doDeletes() throws SQLException {
		deleteRowsFromTable();
	}

	public void doInserts() throws SQLException {
		insertRows();
		
	}


}
