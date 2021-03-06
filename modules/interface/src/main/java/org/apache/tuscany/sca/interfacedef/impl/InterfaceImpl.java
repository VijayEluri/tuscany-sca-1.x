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
package org.apache.tuscany.sca.interfacedef.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Interface;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.policy.Intent;
import org.apache.tuscany.sca.policy.IntentAttachPointType;
import org.apache.tuscany.sca.policy.PolicySet;

/**
 * Represents a service interface.
 *
 * @version $Rev$ $Date$
 */
public class InterfaceImpl implements Interface {

    private boolean remotable;
    private boolean conversational;
    private OperationList operations = new OperationList();
    private boolean unresolved;

    private List<PolicySet> applicablePolicySets = new ArrayList<PolicySet>();
    private IntentAttachPointType type;
    private List<PolicySet> policySets = new ArrayList<PolicySet>();
    private List<Intent> requiredIntents = new ArrayList<Intent>();


    public boolean isRemotable() {
        return remotable;
    }

    public void setRemotable(boolean local) {
        this.remotable = local;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public boolean isUnresolved() {
        return unresolved;
    }

    public void setUnresolved(boolean undefined) {
        this.unresolved = undefined;
    }

    /**
     * @return the conversational
     */
    public boolean isConversational() {
        return conversational;
    }

    /**
     * @param conversational the conversational to set
     */
    public void setConversational(boolean conversational) {
        this.conversational = conversational;
    }

    private class OperationList extends ArrayList<Operation> {
        private static final long serialVersionUID = -903469106307606099L;

        @Override
        public Operation set(int index, Operation element) {
            element.setInterface(InterfaceImpl.this);
            return super.set(index, element);
        }

        @Override
        public void add(int index, Operation element) {
            element.setInterface(InterfaceImpl.this);
            super.add(index, element);
        }

        @Override
        public boolean add(Operation o) {
            o.setInterface(InterfaceImpl.this);
            return super.add(o);
        }

        @Override
        public boolean addAll(Collection<? extends Operation> c) {
            for (Operation op : c) {
                op.setInterface(InterfaceImpl.this);
            }
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Operation> c) {
            for (Operation op : c) {
                op.setInterface(InterfaceImpl.this);
            }
            return super.addAll(index, c);
        }

    }

    @Deprecated
    public void setDefaultDataBinding(String dataBinding) {
        for (Operation op : getOperations()) {
            if (op.getDataBinding() == null) {
                op.setDataBinding(dataBinding);
                DataType<List<DataType>> inputType = op.getInputType();
                if (inputType != null) {
                    for (DataType d : inputType.getLogical()) {
                        if (d.getDataBinding() == null) {
                            d.setDataBinding(dataBinding);
                        }
                    }
                }
                DataType outputType = op.getOutputType();
                if (outputType != null && outputType.getDataBinding() == null) {
                    outputType.setDataBinding(dataBinding);
                }
                List<DataType> faultTypes = op.getFaultTypes();
                if (faultTypes != null) {
                    for (DataType d : faultTypes) {
                        if (d.getDataBinding() == null) {
                            d.setDataBinding(dataBinding);
                        }
                        DataType ft = (DataType) d.getLogical();
                        if (ft.getDataBinding() == null) {
                            ft.setDataBinding(dataBinding);
                        }

                    }
                }

                if (op.isInputWrapperStyle()) {
                    WrapperInfo inputWrapperInfo = op.getInputWrapper();
                    if (inputWrapperInfo != null) {
                        DataType<List<DataType>> unwrappedInputType = inputWrapperInfo.getUnwrappedInputType();
                        if (unwrappedInputType != null) {
                            for (DataType d : unwrappedInputType.getLogical()) {
                                if (d.getDataBinding() == null) {
                                    d.setDataBinding(dataBinding);
                                }
                            }
                        }
                    }
                }

                if (op.isOutputWrapperStyle()) {
                    WrapperInfo outputWrapperInfo = op.getOutputWrapper();
                    if (outputWrapperInfo != null){
                        DataType unwrappedOutputType = outputWrapperInfo.getUnwrappedOutputType();
                        if (unwrappedOutputType != null && unwrappedOutputType.getDataBinding() == null) {
                            unwrappedOutputType.setDataBinding(dataBinding);
                        }
                    }
                }
            }
        }
    }

    private void setDataBinding(DataType dataType, String dataBinding) {
        if ("java:array".equals(dataType.getDataBinding())) {
            setDataBinding((DataType)dataType.getLogical(), dataBinding);
        } else {
            dataType.setDataBinding(dataBinding);
        }
    }

    public void resetInterfaceInputTypes(Interface newInterface){
        for (int i = 0; i < getOperations().size(); i++) {
            // only remote interfaces only have a data type model defined
            // and in this case operations cannot be overloaded so match
            // operations by name
            Operation oldOperation = getOperations().get(i);
            Operation newOperation = null;

            for (Operation tmpOperation : newInterface.getOperations()){
                if (tmpOperation.getName().equals(oldOperation.getName())){
                    newOperation = tmpOperation;
                }
            }

            if (newOperation == null){
                break;
            }

            // set input types
            oldOperation.setInputType(newOperation.getInputType());

            // set wrapper
            if (newOperation.isInputWrapperStyle()) {
                oldOperation.setInputWrapperStyle(true);
                oldOperation.setInputWrapper(newOperation.getInputWrapper());
            }
        }
    }

    public void resetInterfaceOutputTypes(Interface newInterface){
        for (int i = 0; i < getOperations().size(); i++) {
            // only remote interfaces only have a data type model defined
            // and in this case operations cannot be overloaded so match
            // operations by name
            Operation oldOperation = getOperations().get(i);
            Operation newOperation = null;

            for (Operation tmpOperation : newInterface.getOperations()){
                if (tmpOperation.getName().equals(oldOperation.getName())){
                    newOperation = tmpOperation;
                }
            }

            if (newOperation == null){
                break;
            }

            // set output types
            oldOperation.setOutputType(newOperation.getOutputType());

            // set fault types
            oldOperation.setFaultTypes(newOperation.getFaultTypes());

            // set wrapper
            if (newOperation.isOutputWrapperStyle()) {
                oldOperation.setOutputWrapperStyle(true);
                oldOperation.setOutputWrapper(newOperation.getOutputWrapper());
            }
        }
    }

    public void resetDataBinding(String dataBinding) {
        for (Operation op : getOperations()) {
            op.setDataBinding(dataBinding);
            DataType<List<DataType>> inputType = op.getInputType();
            if (inputType != null) {
                for (DataType d : inputType.getLogical()) {
                    setDataBinding(d, dataBinding);
                }
            }
            DataType outputType = op.getOutputType();
            if (outputType != null) {
                setDataBinding(outputType, dataBinding);
            }
            List<DataType> faultTypes = op.getFaultTypes();
            if (faultTypes != null) {
                for (DataType d : faultTypes) {
                    setDataBinding(d, dataBinding);
                    setDataBinding((DataType) d.getLogical(), dataBinding);
                }
            }

            if (op.isInputWrapperStyle()) {
                WrapperInfo inputWrapperInfo = op.getInputWrapper();
                if (inputWrapperInfo != null) {
                    DataType<List<DataType>> unwrappedInputType = inputWrapperInfo.getUnwrappedInputType();
                    if (unwrappedInputType != null) {
                        for (DataType d : unwrappedInputType.getLogical()) {
                            setDataBinding(d, dataBinding);
                        }
                    }
                }
            }

            if (op.isOutputWrapperStyle()) {
                WrapperInfo outputWrapperInfo = op.getOutputWrapper();
                if (outputWrapperInfo != null){
                    DataType unwrappedOutputType = outputWrapperInfo.getUnwrappedOutputType();
                    if (unwrappedOutputType != null) {
                        setDataBinding(unwrappedOutputType, dataBinding);
                    }
                }
            }
        }
    }

    public boolean isDynamic() {
        return false;
    }

    public List<PolicySet> getApplicablePolicySets() {
        return applicablePolicySets;
    }

    public List<PolicySet> getPolicySets() {
        return policySets;
    }

    public List<Intent> getRequiredIntents() {
        return requiredIntents;
    }

    public IntentAttachPointType getType() {
        return type;
    }

    public void setType(IntentAttachPointType type) {
        this.type = type;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        InterfaceImpl copy = (InterfaceImpl)super.clone();
        copy.operations = new OperationList();
        for (Operation operation : this.operations) {
            Operation clonedOperation = (Operation)operation.clone();
            copy.operations.add(clonedOperation);
        }
        return copy;
    }

}
