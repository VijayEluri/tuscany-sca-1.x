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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.tuscany.das.rdb.config.Config;
import org.apache.tuscany.das.rdb.config.Parameter;
import org.apache.tuscany.das.rdb.util.DebugUtil;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;

public class SPCommandImpl extends ReadCommandImpl {
    
    public SPCommandImpl(String sqlString, Config config, List params) {
        super(sqlString, config, null);
        Iterator i = params.iterator();
        for (int idx = 1; i.hasNext(); idx++) {
			Parameter p = (Parameter) i.next();

			int index = p.getColumnType().lastIndexOf('.');
			String pkg = p.getColumnType().substring(0, index);
			String typeName = p.getColumnType().substring(index + 1);

			Type sdoType = TypeHelper.INSTANCE.getType(pkg, typeName);
			

			int direction = ParameterImpl.IN;
			if ("OUT".equalsIgnoreCase(p.getDirection()))
				direction = ParameterImpl.OUT;
			else if ("INOUT".equalsIgnoreCase(p.getDirection()))
				direction = ParameterImpl.IN_OUT;
			parameters.findOrCreateParameterWithIndex(idx, direction, sdoType);
		}    	
        
    }

	public DataObject executeQuery() {

		boolean success = false;
		try {
			ResultSet rs = statement.executeCall(parameters);
			success = true;
			
			return buildGraph(rs);
		} catch (SQLException e) {
			DebugUtil.debugln(getClass(), debug, e);
			throw new RuntimeException(e);
		} finally {
			if (success)
				statement.getConnection().cleanUp();
			else
				statement.getConnection().errorCleanUp();
		}
	}
	
	public void execute() {

		boolean success = false;
		try {
			statement.executeUpdateCall(parameters);
			success = true;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (success)
				statement.getConnection().cleanUp();
			else
				statement.getConnection().errorCleanUp();
		}

	}

}
