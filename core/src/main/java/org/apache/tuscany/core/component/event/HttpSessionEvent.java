/**
 *
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.component.event;

/**
 * A base implementation of HTTP-based session events in the runtime
 *
 * @version $$Rev: 415032 $$ $$Date: 2006-06-17 10:28:07 -0700 (Sat, 17 Jun 2006) $$
 */
public abstract class HttpSessionEvent extends AbstractEvent {

    private Object id;

    public HttpSessionEvent(Object source, Object id) {
        super(source);
        assert id != null : "Session id was null";
        this.id = id;
    }


    public Object getSource() {
        return source;
    }

    public Object getId() {
        return id;
    }

}
