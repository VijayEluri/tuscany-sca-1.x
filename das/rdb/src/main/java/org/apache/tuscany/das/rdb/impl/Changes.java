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
import java.util.Iterator;

/**
 * Mangages a set of graph-change operations.  This simple implementaiton can be 
 * replaced with a version that provides R/I sorting
 * 
 */
public class Changes {

	private InsertList inserts = new InsertList();
	private UpdateList updates = new UpdateList();
	private DeleteList deletes = new DeleteList();
	

	public void addInsert(ChangeOperation c) {
		inserts.add(c);
	}
	
	public void addUpdate(ChangeOperation c) {
		updates.add(c);
	}
	
	public void addDelete(ChangeOperation c) {
		deletes.add(c);
	}

	/**
	 * Execute all my change
	 */
	public void execute() {
		
		Iterator i = inserts.getSortedList().iterator();
		while ( i.hasNext() ) {
			ChangeOperation c = (ChangeOperation) i.next();
			c.execute();
		}
		
		i = updates.getSortedList().iterator();
		while ( i.hasNext() ) {
			ChangeOperation c = (ChangeOperation) i.next();
			c.execute();
		}
		
		
		i = deletes.getSortedList().iterator();
		while (i.hasNext() ) {
			ChangeOperation c = (ChangeOperation) i.next();
			c.execute();
		}
	
	}
	
	public void setInsertOrder(ArrayList insertOrder) {
		inserts.setOrder(insertOrder);
	}

	public void setDeleteOrder(ArrayList deleteOrder) {
		deletes.setOrder(deleteOrder);
	}

}
