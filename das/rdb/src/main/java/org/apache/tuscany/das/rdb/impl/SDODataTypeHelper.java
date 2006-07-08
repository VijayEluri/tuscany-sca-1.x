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

import java.sql.Types;


import commonj.sdo.Type;

public class SDODataTypeHelper {
	
	static public int sqlTypeFor(Type sdoType) {
        if (sdoType == null)
            return Types.OTHER;
        
		if ( sdoType == SDODataTypes.BOOLEAN)
			return Types.BOOLEAN;
		else if ( sdoType == SDODataTypes.STRING)
			return Types.VARCHAR;
		else if ( sdoType == SDODataTypes.BYTE)
			return Types.TINYINT;
		else if ( sdoType == SDODataTypes.BYTES)
			return Types.BINARY;
		else if ( sdoType == SDODataTypes.CHARACTER)
			return Types.CHAR;
		else if ( sdoType == SDODataTypes.DATE)
			return Types.DATE;
		else if ( sdoType == SDODataTypes.DATETIME)
			return Types.DATE;
		else if ( sdoType == SDODataTypes.DAY )
			return java.sql.Types.BINARY;
		else if ( sdoType == SDODataTypes.DECIMAL )
			return java.sql.Types.DECIMAL;
		else if ( sdoType == SDODataTypes.DOUBLE )
			return java.sql.Types.DOUBLE;
		else if ( sdoType == SDODataTypes.DURATION )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.FLOAT )
			return java.sql.Types.REAL;
		else if ( sdoType == SDODataTypes.INT )
			return java.sql.Types.INTEGER;
		else if ( sdoType == SDODataTypes.INTEGER )
			return java.sql.Types.INTEGER;
		else if ( sdoType == SDODataTypes.LONG )
			return java.sql.Types.BIGINT;
		else if ( sdoType == SDODataTypes.MONTH )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.MONTHDAY )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.OBJECT )
			return java.sql.Types.JAVA_OBJECT;
 		else if ( sdoType == SDODataTypes.SHORT )
			return java.sql.Types.SMALLINT;
		else if ( sdoType == SDODataTypes.STRING )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.STRINGS )
			return java.sql.Types.OTHER;
		else if ( sdoType == SDODataTypes.TIME )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.URI )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.YEAR )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.YEARMONTH )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.YEARMONTHDAY )
			return java.sql.Types.VARCHAR;
		else if ( sdoType == SDODataTypes.BOOLEANOBJECT )
			return java.sql.Types.BOOLEAN;
		else if ( sdoType == SDODataTypes.BYTEOBJECT )
			return java.sql.Types.TINYINT;
		else if ( sdoType == SDODataTypes.CHARACTEROBJECT )
			return java.sql.Types.CHAR;
		else if ( sdoType == SDODataTypes.DOUBLEOBJECT )
			return java.sql.Types.DOUBLE;
		else if ( sdoType == SDODataTypes.FLOATOBJECT )
			return java.sql.Types.REAL;
		else if ( sdoType == SDODataTypes.INTEGEROBJECT )
			return java.sql.Types.INTEGER;
		else if ( sdoType == SDODataTypes.LONGOBJECT )
			return java.sql.Types.BIGINT;
		else if ( sdoType == SDODataTypes.SHORTOBJECT )
			return java.sql.Types.SMALLINT;
		else
			throw new RuntimeException("Not a valid SDO Type " + sdoType);
		
	} 
}
