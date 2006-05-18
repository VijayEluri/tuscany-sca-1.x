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
package org.apache.tuscany.das.rdb.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * DeleteList will sort delete operations so that child objects are deleted
 * before their parents
 * 
 * 
 */
public class DeleteList {

	private HashMap opsByTableName = new HashMap();

	private ArrayList order;
	
	private ArrayList deleteOperations = new ArrayList();

	public DeleteList() {
		super();
	}

	public void add(ChangeOperation op) {
		if (( order == null  ) || ( op.getTableName() == null ) ) {
			deleteOperations.add(op);
		} else {
			String name = op.getTableName();
			ArrayList ops = (ArrayList) opsByTableName.get(name);
			if (ops == null)
				ops = new ArrayList();

			ops.add(op);
			opsByTableName.put(name, ops);
		}
	}

	public Collection getSortedList() {
		if  (( order != null  ) && ( opsByTableName.keySet().size() > 0) ) {
			Iterator i = this.order.iterator();
			while (i.hasNext()) {
				String name = (String) i.next();
				deleteOperations.addAll((Collection) opsByTableName.get(name));
			}
		}
		
		return deleteOperations;
	}

	public void setOrder(ArrayList deleteOrder) {
		this.order = deleteOrder;
	}

}
