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
package org.apache.tuscany.das.rdb.graphbuilder.impl;

import java.util.List;

import commonj.sdo.DataObject;

/**
 */
public class SingleTableRegistry implements TableRegistry {

	//private HashMap<List,DataObject> values = new HashMap<List,DataObject>();

	public SingleTableRegistry() {
		// Empty Constructor
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.ws.sdo.mediator.jdbc.graphbuilder.impl.TableRegistry#get(java.lang.String, java.util.List)
	 */
	public DataObject get(String tableName, List primaryKey) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.ws.sdo.mediator.jdbc.graphbuilder.impl.TableRegistry#put(java.lang.String, java.util.List, java.lang.Object)
	 */
	public void put(String tableName, List primaryKey, DataObject value) {
		// do nothing
		
	}

	public boolean contains(String name, List list) {
		return false;
	}

}
