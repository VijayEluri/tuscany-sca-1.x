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
package org.apache.tuscany.spi.builder.physical;

import org.apache.tuscany.spi.builder.WiringException;
import org.apache.tuscany.spi.component.Component;
import org.apache.tuscany.spi.model.physical.PhysicalWireSourceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalWireTargetDefinition;
import org.apache.tuscany.spi.wire.Wire;

/**
 * A registry for wire attachers
 *
 * @version $Rev$ $Date$
 */
public interface WireAttacherRegistry {

    <C extends Component,
        PWSD extends PhysicalWireSourceDefinition,
        PWTD extends PhysicalWireTargetDefinition> void register(Class<?> clazz, WireAttacher<C, PWSD, PWTD> attacher);

    <C extends Component, PWSD extends PhysicalWireSourceDefinition> void attach(C source,
                                                                                 Component target,
                                                                                 Wire wire,
                                                                                 PWSD definition)
        throws WiringException;

    /**
     * Attaches the wire to the target component.
     *
     * @param component  Component.
     * @param wire       the wire
     * @param definition Target wire definition. @throws WiringException
     * @throws WiringException if an error is encountered during the attach operation
     */
    <C extends Component, PWTD extends PhysicalWireTargetDefinition> void attach(C component,
                                                                                 Wire wire,
                                                                                 PWTD definition)
        throws WiringException;

}
