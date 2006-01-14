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

import org.eclipse.emf.ecore.EObject;

/**
 */
public interface TableRegistry {
	/**
	 * Get the table with the specified name and primary key
	 * @param tableName
	 * @param primaryKey
	 * @return EDataObject
	 */
	public EObject get(String tableName, List primaryKey);

	/**
	 * Add the table with the specified name and primary key
	 * @param tableName
	 * @param primaryKey
	 * @param value
	 */
	public void put(String tableName, List primaryKey, EObject value);

	public boolean contains(String name, List list);
}