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
package org.apache.tuscany.das.rdb.test;

/*
 * Test capability to participate in an extenrlly managed transaction. The
 * client is managing the transaction boundary so the DAS will not issue
 * commit/rollback
 * 
 */

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.DAS;
import org.apache.tuscany.das.rdb.test.data.CustomerData;
import org.apache.tuscany.das.rdb.test.framework.DasTest;

import commonj.sdo.DataObject;

public class PassiveConnectionTests extends DasTest {

    protected void setUp() throws Exception {
        super.setUp();
        new CustomerData(getAutoConnection()).refresh();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Read and modify a customer. Uses a "passive" connection
     */
    public void testReadModifyApply() throws Exception {

        // Create and initialize a DAS connection and initialize for externally
        // managed transaction boundaries
        java.sql.Connection c = getConnection();

        try {
        	DAS das = DAS.FACTORY.createDAS();
            // Read customer 1
            Command select = das.createCommand("Select * from CUSTOMER where ID = 1");
            select.setConnection(c);
            DataObject root = select.executeQuery();

            DataObject customer = (DataObject) root.get("CUSTOMER[1]");

            // Modify customer
            customer.set("LASTNAME", "Pavick");

           das.applyChanges(root);

            // Verify changes
            root = select.executeQuery();
            assertEquals("Pavick", root.getString("CUSTOMER[1]/LASTNAME"));

            // Since the DAS is not managing tx boundaries, I must
        } catch (Exception e) {
            c.rollback();
        } finally {
            c.commit();
        }

    }

}
