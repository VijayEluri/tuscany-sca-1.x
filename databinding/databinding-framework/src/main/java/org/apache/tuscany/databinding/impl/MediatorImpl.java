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
package org.apache.tuscany.databinding.impl;

import java.util.List;
import java.util.Map;

import org.apache.tuscany.databinding.DataBindingRegistry;
import org.apache.tuscany.databinding.DataPipe;
import org.apache.tuscany.databinding.Mediator;
import org.apache.tuscany.databinding.PullTransformer;
import org.apache.tuscany.databinding.PushTransformer;
import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.TransformationException;
import org.apache.tuscany.databinding.Transformer;
import org.apache.tuscany.databinding.TransformerRegistry;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.model.DataType;
import org.osoa.sca.annotations.Scope;

/**
 * Default Mediator implementation
 */
@Scope("MODULE")
public class MediatorImpl implements Mediator {

    private DataBindingRegistry dataBindingRegistry;

    private TransformerRegistry transformerRegistry;

    @Autowire
    public void setTransformerRegistry(TransformerRegistry transformerRegistry) {
        this.transformerRegistry = transformerRegistry;
    }

    /**
     * @param dataBindingRegistry the dataBindingRegistry to set
     */
    @Autowire
    public void setDataBindingRegistry(DataBindingRegistry dataBindingRegistry) {
        this.dataBindingRegistry = dataBindingRegistry;
    }

    /**
     * @see org.apache.tuscany.databinding.Mediator#mediate(java.lang.Object, org.apache.tuscany.spi.model.DataType,
     *      org.apache.tuscany.spi.model.DataType, Map)
     */
    @SuppressWarnings("unchecked")
    public Object mediate(
            Object source,
            DataType sourceDataType,
            DataType targetDataType,
            Map<Class<?>, Object> metadata) {
        if (source == null) {
            // Shortcut for null value
            return null;
        }
        if (sourceDataType == null) {
            sourceDataType = dataBindingRegistry.introspectType(source);
        }
        if (sourceDataType == null) {
            return source;
        } else if (sourceDataType.equals(targetDataType)) {
            return source;
        }

        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);

        Object result = source;
        int size = path.size();
        for (int i = 0; i < size; i++) {
            Transformer transformer = path.get(i);
            TransformationContext context =
                    createTransformationContext(sourceDataType, targetDataType, size, i, transformer, metadata);
            // the source and target type
            if (transformer instanceof PullTransformer) {
                // For intermediate node, set data type to null
                result = ((PullTransformer) transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                DataPipe dataPipe = (i < size - 1) ? (DataPipe) path.get(++i) : null;
                ((PushTransformer) transformer).transform(result, dataPipe.getSink(), context);
                result = dataPipe.getResult();
            }
        }

        return result;
    }

    private TransformationContext createTransformationContext(
            DataType sourceDataType,
            DataType targetDataType,
            int size,
            int index,
            Transformer transformer,
            Map<Class<?>, Object> metadata) {
        DataType sourceType =
                (index == 0) ? sourceDataType
                        : new DataType<Object>(transformer.getSourceBinding(), Object.class, null);
        DataType targetType =
                (index == size - 1) ? targetDataType : new DataType<Object>(transformer.getTargetBinding(),
                        Object.class, null);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        TransformationContext context = new TransformationContextImpl(sourceType, targetType, classLoader, metadata);
        return context;
    }

    @SuppressWarnings("unchecked")
    public void mediate(
            Object source,
            Object target,
            DataType sourceDataType,
            DataType targetDataType,
            Map<Class<?>, Object> metadata) {
        if (source == null) {
            // Shortcut for null value
            return;
        }
        if (sourceDataType == null) {
            sourceDataType = dataBindingRegistry.introspectType(source);
        }
        if (sourceDataType == null) {
            return;
        } else if (sourceDataType.equals(targetDataType)) {
            return;
        }

        List<Transformer> path = getTransformerChain(sourceDataType, targetDataType);
        Object result = source;
        int size = path.size();
        for (int i = 0; i < size; i++) {
            Transformer transformer = path.get(i);
            TransformationContext context =
                    createTransformationContext(sourceDataType, targetDataType, size, i, transformer, metadata);

            if (transformer instanceof PullTransformer) {
                result = ((PullTransformer) transformer).transform(result, context);
            } else if (transformer instanceof PushTransformer) {
                DataPipe dataPipe = (i < size - 1) ? (DataPipe) path.get(++i) : null;
                Object sink = dataPipe != null ? dataPipe.getSink() : target;
                ((PushTransformer) transformer).transform(result, sink, context);
                result = (dataPipe != null) ? dataPipe.getResult() : null;
            }
        }
    }

    private List<Transformer> getTransformerChain(DataType sourceDataType, DataType targetDataType) {
        String sourceId = sourceDataType.getDataBinding();
        String targetId = targetDataType.getDataBinding();
        List<Transformer> path = transformerRegistry.getTransformerChain(sourceId, targetId);
        if (path == null) {
            TransformationException ex = new TransformationException("No path found for the transformation");
            ex.addContextName("Source: " + sourceId);
            ex.addContextName("Target: " + targetId);
            throw ex;
        }
        return path;
    }
}
