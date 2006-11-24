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
package org.apache.tuscany.spi;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * Raised when an error is encountered reactivating a serialized instance
 *
 * @version $Rev$ $Date$
 */
public class ReactivationException extends TuscanyRuntimeException {
    public ReactivationException() {
    }

    public ReactivationException(String message) {
        super(message);
    }

    public ReactivationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReactivationException(Throwable cause) {
        super(cause);
    }
}
