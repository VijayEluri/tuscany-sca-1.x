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

package org.apache.tuscany.sca.contribution.resolver;

import java.util.HashMap;
import java.util.Map;


/**
 * A default implementation of a model resolver based on a map.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModelResolver implements ModelResolver {
    
    private Map<Object, Object> map = new HashMap<Object, Object>();
    
    public DefaultModelResolver() {
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        if (resolved != null) {
            
            // Return the resolved object
            return modelClass.cast(resolved);
            
        } else {
            
            // Return the unresolved object
            return unresolved;
        }
    }
    
    public void addModel(Object resolved) {
        map.put(resolved, resolved);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(resolved);
    }
    
    // FIXME: TUSCANY-2499: temporarily  give access to the models to get the jms binding
    //  use of definitions.xml working while the definitions.xml processing is being refactored
    public Map getModels() {
        return map;
    }
    
}
