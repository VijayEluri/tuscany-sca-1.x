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
package org.apache.tuscany.sca.core.databinding.transformers;

import java.util.List;

import org.apache.tuscany.sca.databinding.DataBinding;
import org.apache.tuscany.sca.databinding.Mediator;
import org.apache.tuscany.sca.databinding.PullTransformer;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.TransformationException;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.impl.BaseTransformer;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;
import org.osoa.sca.annotations.Reference;

/**
 * This is a special transformer to transform the output from one IDL to the
 * other one
 *
 * @version $Rev$ $Date$
 */
public class Output2OutputTransformer extends BaseTransformer<Object, Object> implements
    PullTransformer<Object, Object> {

    protected Mediator mediator;

    /**
     * @param wrapperHandler
     */
    public Output2OutputTransformer() {
        super();
    }

    /**
     * @param mediator the mediator to set
     */
    @Reference
    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }

    @Override
    public String getSourceDataBinding() {
        return DataBinding.IDL_OUTPUT;
    }

    @Override
    public String getTargetDataBinding() {
        return DataBinding.IDL_OUTPUT;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getSourceType()
     */
    @Override
    protected Class<Object> getSourceType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.impl.BaseTransformer#getTargetType()
     */
    @Override
    protected Class<Object> getTargetType() {
        return Object.class;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.Transformer#getWeight()
     */
    @Override
    public int getWeight() {
        return 10;
    }

    private String getDataBinding(Operation operation) {
        WrapperInfo wrapper = operation.getOutputWrapper();
        if (wrapper != null) {
            return wrapper.getDataBinding();
        } else {
            return null;
        }
    }

    private WrapperHandler getWrapperHandler(String dataBindingId, boolean required) {
        WrapperHandler wrapperHandler = null;
        if (dataBindingId != null) {
            DataBinding dataBinding = mediator.getDataBindings().getDataBinding(dataBindingId);
            wrapperHandler = dataBinding == null ? null : dataBinding.getWrapperHandler();
        }
        if (wrapperHandler == null && required) {
            throw new TransformationException("No wrapper handler is provided for databinding: " + dataBindingId);
        }
        return wrapperHandler;
    }

    /**
     * Match the structure of the wrapper element. If it matches, then we can do
     * wrapper to wrapper transformation. Otherwise, we do child to child.
     * @param w1
     * @param w2
     * @return
     */
    private boolean matches(WrapperInfo w1, WrapperInfo w2) {
        if (w1 == null || w2 == null) {
            return false;
        }
        if (!w1.getWrapperElement().equals(w2.getWrapperElement())) {
            return false;
        }

        // Compare the child elements
        List<ElementInfo> list1 = w1.getChildElements();
        List<ElementInfo> list2 = w2.getChildElements();
        if (list1.size() != list2.size()) {
            return false;
        }
        // FXIME: [rfeng] At this point, the J2W generates local elments under the namespace
        // of the interface instead of "". We only compare the local parts only to work around
        // the namespace mismatch
        for (int i = 0; i < list1.size(); i++) {
            String n1 = list1.get(i).getQName().getLocalPart();
            String n2 = list2.get(i).getQName().getLocalPart();

            // TUSCANY-3298: In the following situation:
            //  1. The child is a java.util.Map type
            //  2. The child's name is a Java keyword (e.g., return)
            //  3. Tuscany is using a generated JAXB wrapper class for WSDL generation
            // the Java to WSDL generation process results in the WSDL element name
            // having a leading underscore added to the actual element name.  This is
            // because of a known JAXB issue that prevents the @XmlElement annotation
            // being used on a java.util.Map type property field in the wrapper bean
            // (see https://jaxb.dev.java.net/issues/show_bug.cgi?id=268).
            // To prevent the compatibility match from failing in this situation,
            // we strip any leading underscore before doing the comparison.
            if (!stripLeadingUnderscore(n1).equals(stripLeadingUnderscore(n2))) {
                return false;
            }
        }
        return true;
    }

    private static String stripLeadingUnderscore(String name) {
        return name.startsWith("_") ? name.substring(1) : name;
    }

    @SuppressWarnings("unchecked")
    public Object transform(Object response, TransformationContext context) {
        try {
            DataType<DataType> sourceType = context.getSourceDataType();
            Operation sourceOp = context.getSourceOperation();
            boolean sourceWrapped = sourceOp != null && sourceOp.isOutputWrapperStyle() && sourceOp.getOutputWrapper() != null;
            boolean sourceBare = sourceOp != null && !sourceOp.isOutputWrapperStyle() && sourceOp.getOutputWrapper() == null;

            WrapperHandler sourceWrapperHandler = null;
            String sourceDataBinding = getDataBinding(sourceOp);
            sourceWrapperHandler = getWrapperHandler(sourceDataBinding, sourceWrapped);

            DataType<DataType> targetType = context.getTargetDataType();
            Operation targetOp = (Operation)context.getTargetOperation();
            boolean targetWrapped = targetOp != null && targetOp.isOutputWrapperStyle() && targetOp.getOutputWrapper() != null;
            boolean targetBare = targetOp != null && !targetOp.isOutputWrapperStyle() && targetOp.getOutputWrapper() == null;

            WrapperHandler targetWrapperHandler = null;
            String targetDataBinding = getDataBinding(targetOp);
            targetWrapperHandler = getWrapperHandler(targetDataBinding, targetWrapped);

            if ((!sourceWrapped &&!sourceBare) && targetWrapped) {
                // Unwrapped --> Wrapped
                WrapperInfo wrapper = targetOp.getOutputWrapper();
                ElementInfo wrapperElement = wrapper.getWrapperElement();
                List<ElementInfo> childElements = wrapper.getChildElements();
                Class<?> targetWrapperClass = wrapper != null ? wrapper.getWrapperClass() : null;

                // If the source can be wrapped, wrapped it first
                if (sourceWrapperHandler != null) {
                    WrapperInfo sourceWrapperInfo = sourceOp.getOutputWrapper();
                    DataType sourceWrapperType =
                        sourceWrapperInfo != null ? sourceWrapperInfo.getWrapperType() : null;

                    if (sourceWrapperType != null && matches(sourceOp.getOutputWrapper(), targetOp.getOutputWrapper())) {
                        Class<?> sourceWrapperClass = sourceWrapperType.getPhysical();

                        Object sourceWrapper = sourceWrapperHandler.create(sourceOp, false);
                        if (sourceWrapper != null) {
                            if (!childElements.isEmpty()) {
                                // Set the return value
                                sourceWrapperHandler.setChildren(sourceWrapper,
                                                                 new Object[] {response},
                                                                 sourceOp,
                                                                 false);
                            }
                            Object targetWrapper =
                                mediator.mediate(sourceWrapper, sourceWrapperType, targetType.getLogical(), context
                                    .getMetadata());
                            return targetWrapper;
                        }
                    }
                }
                Object targetWrapper = targetWrapperHandler.create(targetOp, false);

                if (childElements.isEmpty()) {
                    // void output
                    return targetWrapper;
                }

                DataType<XMLType> argType = wrapper.getUnwrappedOutputType();
                Object child = response;
                child = mediator.mediate(response, sourceType.getLogical(), argType, context.getMetadata());
                targetWrapperHandler.setChildren(targetWrapper, new Object[] {child}, targetOp, false);
                return targetWrapper;
            } else if (sourceWrapped && (!targetWrapped && !targetBare)) {
                // Wrapped to Unwrapped
                Object sourceWrapper = response;
                List<ElementInfo> childElements = sourceOp.getOutputWrapper().getChildElements();
                if (childElements.isEmpty()) {
                    // The void output
                    return null;
                }
                if (targetWrapperHandler != null) {
                    ElementInfo wrapperElement = sourceOp.getOutputWrapper().getWrapperElement();

                    // FIXME: This is a workaround for the wsdless support as it passes in child elements
                    // under the wrapper that only matches by position
                    if (sourceWrapperHandler.isInstance(sourceWrapper, sourceOp, false)) {

                        WrapperInfo targetWrapperInfo = targetOp.getOutputWrapper();
                        DataType targetWrapperType =
                            targetWrapperInfo != null ? targetWrapperInfo.getWrapperType() : null;

                        if (targetWrapperType != null && matches(sourceOp.getOutputWrapper(), targetOp.getOutputWrapper())) {
                            Object targetWrapper =
                                mediator.mediate(sourceWrapper, sourceType.getLogical(), targetWrapperType, context
                                    .getMetadata());
                            return targetWrapperHandler.getChildren(targetWrapper, targetOp, false).get(0);
                        }
                    }
                }
                Object child = sourceWrapperHandler.getChildren(sourceWrapper, sourceOp, false).get(0);
                DataType<?> childType = sourceOp.getOutputWrapper().getUnwrappedOutputType();
                return mediator.mediate(child, childType, targetType.getLogical(), context.getMetadata());
            } else {
                // FIXME: Do we want to handle wrapped to wrapped?
                return mediator.mediate(response, sourceType.getLogical(), targetType.getLogical(), context
                    .getMetadata());
            }
        } catch (Exception e) {
            throw new TransformationException(e);
        }
    }

}
