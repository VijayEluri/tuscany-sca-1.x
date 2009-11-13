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
package org.apache.tuscany.sca.domain.search;

import org.apache.lucene.document.Document;

/**
 * This interface should be implemented to provide a way to create
 * {@link Result} objects from a field name and its value or a {@link Document}
 * resulted from a search.
 * 
 * @see Result
 * @version $Rev$ $Date$
 */
public interface ResultFactory<T extends Result> {

    /**
     * Creates a {@link Result} instance from a field name and its value.
     * 
     * @param field the field name
     * @param value the value
     * @return the {@link Result} instance
     */
    T createResult(String field, String value);

    /**
     * Creates a {@link Result} instance from a {@link Document} object resulted
     * from a search.
     * 
     * @param document the result {@link Document}
     * @return the {@link Result} instance
     */
    T createResult(Document document);

}
