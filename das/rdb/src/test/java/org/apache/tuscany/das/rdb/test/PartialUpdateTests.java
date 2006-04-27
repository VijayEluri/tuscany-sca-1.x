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

import java.sql.SQLException;

import org.apache.tuscany.das.rdb.ApplyChangesCommand;
import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.test.data.CustomerData;
import org.apache.tuscany.das.rdb.test.framework.DasTest;

import commonj.sdo.DataObject;

public class PartialUpdateTests extends DasTest {

    protected void setUp() throws Exception {
        super.setUp();
        new CustomerData(getAutoConnection()).refresh();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public PartialUpdateTests() {
        super();
    }

    public void testPartialUpdate() throws SQLException {
        Command readCustomers = Command.FACTORY.createCommand("select * from CUSTOMER where ID = 1");
        readCustomers.setConnection(getConnection());

        // Read
        DataObject root = readCustomers.executeQuery();

        DataObject customer = root.getDataObject("CUSTOMER[1]");
        // Verify
        assertEquals(1, customer.getInt("ID"));

        Command update = Command.FACTORY.createCommand("update CUSTOMER set LASTNAME = 'modified' where ID = 1");
        update.setConnection(getConnection());
        update.execute();

        customer.setString("ADDRESS", "main street");

        ApplyChangesCommand apply = Command.FACTORY.createApplyChangesCommand();
        apply.setConnection(getConnection());
        apply.execute(root);

        root = readCustomers.executeQuery();

        // If partial update was not used, LASTNAME would not be 'modified'
        customer = root.getDataObject("CUSTOMER[1]");
        assertEquals(1, customer.getInt("ID"));
        assertEquals("modified", customer.getString("LASTNAME"));
        assertEquals("main street", customer.getString("ADDRESS"));
    }

    public void testPartialInsert() throws SQLException {
        Command readCustomers = Command.FACTORY.createCommand("select * from CUSTOMER where ID = 1");
        readCustomers.setConnection(getConnection());

        // Read
        DataObject root = readCustomers.executeQuery();

        // Create a new customer
        DataObject newCust = root.createDataObject("CUSTOMER");
        newCust.set("ID", new Integer(100));
        newCust.set("ADDRESS", "5528 Wells Fargo Drive");
        // Purposely do not set lastname to let it default to 'Garfugengheist'
        // newCust.set("LASTNAME", "Gerkin" );

        ApplyChangesCommand apply = Command.FACTORY.createApplyChangesCommand();
        apply.setConnection(getConnection());
        apply.execute(root);

        Command readNewCust = Command.FACTORY.createCommand("select * from CUSTOMER where ID = 100");
        readNewCust.setConnection(getConnection());
        root = readNewCust.executeQuery();

        // If partial insert was not used, LASTNAME would not be
        // 'Garfugengheist'
        newCust = root.getDataObject("CUSTOMER[1]");
        assertEquals(100, newCust.getInt("ID"));
        assertEquals("Garfugengheist", newCust.getString("LASTNAME"));
        assertEquals("5528 Wells Fargo Drive", newCust.getString("ADDRESS"));

    }
}
