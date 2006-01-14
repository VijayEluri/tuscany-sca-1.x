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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.tuscany.das.rdb.util.DebugUtil;


/**
 * 
 * A ResultSetRow is used to transform a single row of a ResultSet into a set of
 * EDataObjects.
 */
public class ResultSetRow {

	private final ResultMetadata metadata;

	private HashMap tableMap = new HashMap();

	private ArrayList allTableData;
	
	private boolean debug = false;

	/**
	 * Method ResultSetRow.
	 * 
	 * @param rs
	 *            A ResultSet positioned on the desired row
	 * @param ePackage
	 *            The package used to create EDataObjects
	 */
	public ResultSetRow(ResultSet rs, ResultMetadata m) throws SQLException {
		this.metadata = m;
		if (m.isRecursive())
			processRecursiveRow(rs);
		else
			processRow(rs);
	}

	/**
	 * Processes a single row in the ResultSet Method processRow.
	 * 
	 * @param rs
	 */
	private void processRow(ResultSet rs) throws SQLException {

		DebugUtil.debugln(getClass(), debug, "");
		for (int i = 1; i <= metadata.getResultSetSize(); i++) {
			Object data = getObject(rs, i);

			TableData table = getRawData(metadata.getTablePropertyName(i));
			DebugUtil.debugln(getClass(), debug, "Adding column: "
					+ metadata.getColumnPropertyName(i) + "\tValue: " + data
					+ "\tTable: " + metadata.getTablePropertyName(i));
			table.addData(metadata.getColumnPropertyName(i), metadata
					.isPKColumn(i), data);
		}

	}

	public void processRecursiveRow(ResultSet rs) throws SQLException {
		this.allTableData = new ArrayList();
		int i = 1;
		DebugUtil.debugln(getClass(), debug, "");
		while (i <= metadata.getResultSetSize()) {
			DebugUtil.debugln(getClass(), debug, "");
			TableData table = new TableData(metadata.getTablePropertyName(i));
			this.allTableData.add(table);
			
			while ( (i <= metadata.getResultSetSize()) && (metadata.isPKColumn(i))) {
				Object data = getObject(rs, i);
				DebugUtil.debugln(getClass(), debug, "Adding column: "
						+ metadata.getColumnPropertyName(i) + "\tValue: " + data
						+ "\tTable: " + metadata.getTablePropertyName(i));
				table.addData(metadata.getColumnPropertyName(i), true, data);
				i++;
			}

			while ( (i <= metadata.getResultSetSize()) &&
					(!metadata.isPKColumn(i))) {
				Object data = getObject(rs, i);
				DebugUtil.debugln(getClass(), debug, "Adding column: "
						+ metadata.getColumnPropertyName(i) + "\tValue: " + data
						+ "\tTable: " + metadata.getTablePropertyName(i));
				table.addData(metadata.getColumnPropertyName(i), false, data);
				i++;
			}
		}
	}

	/**
	 * @param rs
	 * @param metadata
	 * @param i
	 * @return
	 */
	private Object getObject(ResultSet rs,  int i)
			throws SQLException {

		Object data = rs.getObject(i);

		if (rs.wasNull())
			return null;
		else
			return metadata.getConverter(i).getPropertyValue(data);
	}

	/**
	 * Returns a HashMap that holds data for the specified table
	 * 
	 * @param tableName
	 *            The name of the table
	 * @return HashMap
	 */
	public TableData getTable(String tableName) {
		return (TableData) tableMap.get(tableName);
	}

	/**
	 * Returns a HashMap that holds data for the specified table If the HashMap
	 * doesn't exist, it will be created. This is used internally to build the
	 * ResultSetRow, whereas getTable is used externally to retrieve existing
	 * table data.
	 * 
	 * @param tableName
	 *            The name of the table
	 * @return HashMap
	 */
	private TableData getRawData(String tableName) {

		TableData table = (TableData) tableMap.get(tableName);

		if (table == null) {
			table = new TableData(tableName);
			tableMap.put(tableName, table);
		}

		return table;
	}
	
	public ArrayList getAllTableData() {
		if ( this.allTableData == null ) {
			this.allTableData = new ArrayList();
			this.allTableData.addAll(tableMap.values());
		}
		DebugUtil.debugln(getClass(), debug, allTableData);
		
		return this.allTableData;
	}

}
