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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.tuscany.das.rdb.util.DebugUtil;


public class ConnectionImpl {

	private Connection connection;
	private static final boolean debug = false;

	private boolean managingTransaction = true;

	public ConnectionImpl(Connection connection) {
		this.connection = connection;

		try {
			if (connection.getAutoCommit())
				throw new RuntimeException("AutoCommit must be off!");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public Connection getJDBCConnection() {
		return connection;
	}

	public void cleanUp() {
		try {
			if (managingTransaction) {
				DebugUtil.debugln(getClass(), debug, "Committing Transaction");
				connection.commit();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void errorCleanUp() {
		try {
			if (managingTransaction) {
				DebugUtil.debugln(getClass(), debug, "Rolling back Transaction");
				connection.rollback();
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public PreparedStatement prepareStatement(String queryString)
			throws SQLException {
		DebugUtil.debugln(getClass(), debug, "Preparing Statement: " + queryString);
		return connection.prepareStatement(queryString, java.sql.Statement.RETURN_GENERATED_KEYS);
	}

	public PreparedStatement preparePagedStatement(String queryString) throws SQLException {
		DebugUtil.debugln(getClass(), debug, "Preparing Statement: " + queryString);
		return connection.prepareStatement(queryString, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	public void setManageTransactions(boolean manageTransactions) {
		managingTransaction = manageTransactions;
		
	}

	public CallableStatement prepareCall(String queryString) throws SQLException {
		return connection.prepareCall(queryString);
	}

	

}
