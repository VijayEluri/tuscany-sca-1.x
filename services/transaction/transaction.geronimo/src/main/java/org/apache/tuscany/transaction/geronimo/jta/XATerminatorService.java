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
package org.apache.tuscany.transaction.geronimo.jta;

import javax.resource.spi.XATerminator;

import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.annotation.Autowire;

import org.apache.geronimo.transaction.ExtendedTransactionManager;
import org.apache.geronimo.transaction.context.TransactionContextManager;
import org.apache.geronimo.transaction.manager.XidImporter;

/**
 * Wraps the Geronimo <code>XATerminator</code> implementation to bootstrap it as a system service
 *
 * @version $Rev$ $Date$
 */
@Service(XATerminator.class)
public class XATerminatorService extends TransactionContextManager {

    public XATerminatorService(@Autowire ExtendedTransactionManager tm, @Autowire XidImporter importer) {
        super(tm, importer);
    }
}
