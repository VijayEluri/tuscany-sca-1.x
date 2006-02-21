/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
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
package org.apache.tuscany.core.context;

import org.apache.tuscany.model.assembly.Scope;


/**
 * Includes Context-related constants
 *
 * @version $Rev$ $Date$
 */
public interface ContextConstants {

    public static final Scope AGGREGATE_SCOPE = Scope.AGGREGATE;
    public static final Scope MODULE_SCOPE = Scope.MODULE;
    public static final Scope SESSION_SCOPE = Scope.SESSION;
    public static final Scope REQUEST_SCOPE = Scope.REQUEST;
    public static final Scope STATELESS = Scope.INSTANCE;

    public static final String NAME_SEPARATOR = "/";

}
