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
package org.apache.tuscany.sca.contribution.service;

import javax.xml.namespace.QName;

/**
 * Exception that indicates an element was encountered that could not be handled.
 *
 * @version $Rev: 560434 $ $Date: 2007-07-27 18:23:02 -0700 (Fri, 27 Jul 2007) $
 */
public class UnrecognizedElementException extends ContributionReadException {
    private static final long serialVersionUID = 2549543622209829032L;
    private final QName element;

    /**
     * Constructor that indicates which element could not be handled.
     * @param element the element that could not be handled
     */
    public UnrecognizedElementException(QName element) {
        super("Unrecognized element: " + element.toString());
        this.element = element;
    }

    /**
     * Returns the element that could not be handled.
     * @return the element that could not be handled.
     */
    public QName getElement() {
        return element;
    }
}
