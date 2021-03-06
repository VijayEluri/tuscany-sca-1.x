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
package org.apache.tuscany.sca.itest.trninq;

import java.rmi.RemoteException;

import org.ifxforum.xsd._1.DepAcctTrnInqRq_Type;
import org.ifxforum.xsd._1.DepAcctTrnInqRs_Type;

/**
 * @version $Rev$ $Date$
 */
public class TrnInqClient implements TrninqInterface {

    //service interface
    private TrninqInterface transactionInquiryService;

    // setting service interface
    public void setTransactionInquiryService(TrninqInterface transactionInquiryService) {
        this.transactionInquiryService = transactionInquiryService;
    }

    public DepAcctTrnInqRs_Type DepAcctTrnInq(DepAcctTrnInqRq_Type req) throws RemoteException {
        return transactionInquiryService.DepAcctTrnInq(req);
    }

}
