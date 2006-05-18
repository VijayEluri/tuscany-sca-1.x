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
package org.apache.tuscany.das.rdb.test.mappings;

import org.apache.tuscany.das.rdb.Converter;

public class IntegerToBooleanConverter implements Converter {

	public IntegerToBooleanConverter() {
		super();
	}

	public Object getPropertyValue(Object columnData) {
		Integer value = (Integer) columnData;
		return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
	}
	
	public Object getColumnValue(Object propertyData) {
		Boolean value = (Boolean) propertyData;
		if ( value.booleanValue())
			return new Integer(1);
		else
			return new Integer(0);
	}

}
