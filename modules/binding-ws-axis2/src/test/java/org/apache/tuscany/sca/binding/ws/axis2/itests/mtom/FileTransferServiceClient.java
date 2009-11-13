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
package org.apache.tuscany.sca.binding.ws.axis2.itests.mtom;

import javax.activation.DataHandler;
import java.awt.Image;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.transform.Source;
import org.apache.axiom.om.OMElement;

/**
 * This is the client interface of the File Transfer service.
 */
public interface FileTransferServiceClient {

    public String uploadImageFileForward (Image attachment) throws Exception;
    
    public String uploadSourceFileForward (Source attachment) throws Exception;
    
    public String uploadDataHandlerFileForward (DataHandler attachment) throws Exception;    
    
    public String uploadOMElementFileForward (OMElement attachment) throws Exception;
    
    public String sendMyExceptionForward (MyException attachment) throws Exception;
}

