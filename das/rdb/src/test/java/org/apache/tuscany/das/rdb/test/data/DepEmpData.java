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

import org.apache.tuscany.das.rdb.test.framework.RelationshipData;


public class DepEmpData extends RelationshipData {

	private static Object[][] data = { 
		{"Advanced Technologies", "John Jones"},
		{"Advanced Technologies", "Jane Doe"},
		{"Advanced Technologies", "Al Smith"}
	};
	
	
	public DepEmpData(Connection c) {
		super(c, data);
	}

	protected String getParentRetrievalStatement() {
		return "select department.id from department where department.name = ?";
	}

	protected String getChildUpdateStatement() {
		return "update employee set employee.departmentid = ? where employee.name = ?";
	}

}
