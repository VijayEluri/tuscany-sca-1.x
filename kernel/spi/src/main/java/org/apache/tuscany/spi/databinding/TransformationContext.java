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
package org.apache.tuscany.spi.databinding;

import java.util.Map;

import org.apache.tuscany.spi.model.DataType;

/**
 * Context for data transformation
 */
public interface TransformationContext {
    /**
     * Get the source data type
     *
     * @return the source data type
     */
    DataType getSourceDataType();

    /**
     * Get the target data type
     *
     * @return the target datatype
     */
    DataType getTargetDataType();

    /**
     * Set the source data type
     *
     * @param sourceDataType the source data type
     */
    void setSourceDataType(DataType sourceDataType);

    /**
     * Set the target data type
     *
     * @param targetDataType the target data type
     */
    void setTargetDataType(DataType targetDataType);

    /**
     * Get the classloader
     *
     * @return the classloader
     */
    ClassLoader getClassLoader();

    /**
     * Get a map of metadata
     *
     * @return the map of metadata
     */
    Map<Class<?>, Object> getMetadata();

}
