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

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.ResultSetShape;
import org.apache.tuscany.das.rdb.SDODataTypes;
import org.apache.tuscany.das.rdb.test.data.CustomerData;
import org.apache.tuscany.das.rdb.test.framework.DasTest;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

/**
 * Test ability to specify format(shape) of the ResultSet. This is necessary
 * when the JDBC driver in use does not provide adequate support for
 * ResultSetMetadata. Also, we expect that specifying the result set shape will
 * increase performance.
 * 
 */
public class ResultSetShapeTests extends DasTest {

    protected void setUp() throws Exception {
        super.setUp();
        new CustomerData(getAutoConnection()).refresh();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Read a specific customer
     */
    public void testReadSingle() throws Exception {

        // Using literals in the select forces invalid resultset metadata
        String sqlString = "Select 99, 'Roosevelt', '1600 Pennsylvania Avenue' from customer";
        String[] columns = { "ID", "LASTNAME", "ADDRESS" };
        String[] tables = { "CUSTOMER", "CUSTOMER", "CUSTOMER" };
        Type[] types = { SDODataTypes.LONG, SDODataTypes.STRING, SDODataTypes.STRING };

        ResultSetShape shape = new ResultSetShape(tables, columns, types);

        // Create and initialize command to read customers
        Command readCustomers = Command.FACTORY.createCommand(sqlString,
                getConfig("CustomerConfigWithIDConverter.xml"));
        // Specify result shape
        readCustomers.setResultSetShape(shape);

        readCustomers.setConnection(getConnection());

        // Read
        DataObject root = readCustomers.executeQuery();

        // Verify
        assertEquals(5, root.getList("CUSTOMER").size());
        assertEquals(99, root.getInt("CUSTOMER[1]/ID"));
        assertEquals("Roosevelt", root.getString("CUSTOMER[1]/LASTNAME"));
        assertEquals("1600 Pennsylvania Avenue", root.getString("CUSTOMER[1]/ADDRESS"));

    }

    /**
     * Read a specific customer This duplicates the previous tests but does not
     * provide the shape info. Since the select will not return valid metadata,
     * this test is expected to fail
     */
    public void testReadSingleVerifyShapeUse() throws Exception {

        // Using literals in the select forces invalid resultset metadata
        String sqlString = "Select 99, 'Roosevelt', '1600 Pennsylvania Avenue' from customer";

        // Create and initialize command to read customers
        Command readCustomers = Command.FACTORY.createCommand(sqlString);
        readCustomers.setConnection(getConnection());

        // Read
        DataObject root = readCustomers.executeQuery();

        // Verify
        try {
            assertEquals(5, root.getList("CUSTOMER").size());
            fail("Should fail since there will be no feature named CUSTOMER");
        } catch (IllegalArgumentException e) {
            // OK
        }

    }

}
