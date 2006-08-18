/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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
package org.apache.tuscany.osgi.binding;

import org.apache.tuscany.api.TuscanyRuntimeException;

/**
 * @version $Rev: 424773 $ $Date: 2006-07-23 14:14:09 -0400 (Sun, 23 Jul 2006) $
 */
public abstract class OSGiBindingException extends TuscanyRuntimeException {
    protected OSGiBindingException() {
    }

    protected OSGiBindingException(String message) {
        super(message);
    }

    protected OSGiBindingException(String message, Throwable cause) {
        super(message, cause);
    }

    protected OSGiBindingException(Throwable cause) {
        super(cause);
    }
}
