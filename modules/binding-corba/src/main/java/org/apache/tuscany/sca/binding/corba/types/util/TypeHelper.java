/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

package org.apache.tuscany.sca.binding.corba.types.util;

import org.apache.tuscany.sca.binding.corba.types.TypeTreeNode;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * @version $Rev$ $Date$
 */
public interface TypeHelper {
	
	/**
	 * Gets type definition for CORBA API.
	 * @param node 
	 * @return
	 */
	TypeCode getType(TypeTreeNode node);
	
	/**
	 * Reads CORBA object
	 * @param node
	 * @param is
	 * @return
	 */
	Object read(TypeTreeNode node, InputStream is);
	
	/**
	 * Writes CORBA object
	 * @param node
	 * @param os
	 * @param data
	 */
	void write(TypeTreeNode node, OutputStream os, Object data);
	
}