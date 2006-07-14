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
 * These tests attempt to duplicate customer reported errors and then to verify
 * any necessary fix.
 * 
 */

import java.util.Iterator;
import java.util.Random;

import org.apache.tuscany.das.rdb.Command;
import org.apache.tuscany.das.rdb.ConfigHelper;
import org.apache.tuscany.das.rdb.DAS;
import org.apache.tuscany.das.rdb.test.data.CompanyData;
import org.apache.tuscany.das.rdb.test.data.CompanyDeptData;
import org.apache.tuscany.das.rdb.test.data.CustomerData;
import org.apache.tuscany.das.rdb.test.data.DepEmpData;
import org.apache.tuscany.das.rdb.test.data.DepartmentData;
import org.apache.tuscany.das.rdb.test.data.EmployeeData;
import org.apache.tuscany.das.rdb.test.data.OrderData;
import org.apache.tuscany.das.rdb.test.framework.DasTest;

import commonj.sdo.DataObject;

public class DefectTests extends DasTest {

    protected void setUp() throws Exception {
        super.setUp();
        new CustomerData(getAutoConnection()).refresh();
        new OrderData(getAutoConnection()).refresh();
        
        new CompanyData(getAutoConnection()).refresh();
        new DepartmentData(getAutoConnection()).refresh();
        new EmployeeData(getAutoConnection()).refresh();
        new CompanyDeptData(getAutoConnection()).refresh();
        new DepEmpData(getAutoConnection()).refresh();
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Read a specific customer
     */
    public void testDiltonsInsert() throws Exception {

        // String sql = "insert into conmgt.serverstatus (statusid,
        // managedserverid, timestamp) values (316405209, 316405209, '2005-11-23
        // 19:29:52.636')";
        String sql = "insert into conmgt.serverstatus (managedserverid, timestamp) values (316405209, '2005-11-23 19:29:52.636')";
        DAS das = DAS.FACTORY.createDAS(getConnection());
        Command insert = das.createCommand(sql);       
        insert.execute();

        // Verify
        Command select = das
                .createCommand("Select * from conmgt.serverstatus where statusid = 316405209");        
        DataObject root = select.executeQuery();
        assertEquals(1, root.getList("conmgt.serverstatus").size());

    }
    

    public void testUpdateChildThatHasGeneratedKey() throws Exception {

        DAS das = DAS.FACTORY.createDAS(getConfig("CompanyConfig.xml"));
                        
        //Read a specific company based on the known ID
        Command readCust = das.getCommand("all companies and departments");
        DataObject root = readCust.executeQuery();       
        DataObject lastCustomer = root.getDataObject("COMPANY[3]");
        Iterator i = lastCustomer.getList("departments").iterator();
        Random generator = new Random();
        int random = generator.nextInt(1000) + 1;
        DataObject department;
        while (i.hasNext()) {
            department = (DataObject)i.next();
            System.out.println("Modifying department: " + department.getString("NAME"));
            department.setString("NAME", "Dept-" + random);
            random = random + 1;
        } 
        
       das.applyChanges(root);
        
    }  
         
    
    /**
     * Tuscany-433
     * 
     * Read and modify a customer.  
     * Provide needed Create/Update/Delete statements programatically
     * 
     * Fails if provided udpate statement does not include all parameters
     */
    public void testReadModifyApply()
        throws Exception
    {


        // Provide updatecommand programmatically via config
        ConfigHelper helper = new ConfigHelper();
        helper.addUpdateStatement("update CUSTOMER set LASTNAME = ? where ID = ?", "CUSTOMER", "LASTNAME ID");         
        
    	DAS das = DAS.FACTORY.createDAS(helper.getConfig(), getConnection());
    	
        //Read customer 1
        Command select = das.createCommand( "Select * from CUSTOMER where ID = 1" );        
        DataObject root = select.executeQuery();

        DataObject customer = (DataObject) root.get( "CUSTOMER[1]" );

        //Modify customer
        customer.set( "LASTNAME", "Pavick" );

        das.applyChanges( root );

        //Verify changes
        root = select.executeQuery();
        assertEquals( "Pavick", root.getString( "CUSTOMER[1]/LASTNAME" ) );

    }

    /**
     * 
     * Tuscany-433
     * 
     * 
     * Read and modify a customer.  
     * Provide needed Create/Update/Delete statements via xml file
     * 
     * Same as in CrudWithChangeHistory but provided partial update statement.
     * That is, it does not update all columns
     * 
     * Fails if provided udpate statement does not include all parameters
     * 
     */
    public void testReadModifyApply1()
        throws Exception
    {
    	DAS das = DAS.FACTORY.createDAS(getConfig("basicCustomerMappingWithCUD2.xml"), getConnection());
        //Read customer 1
        Command select = das.createCommand( "Select * from CUSTOMER where ID = 1" );       
        DataObject root = select.executeQuery();

        DataObject customer = (DataObject) root.get( "CUSTOMER[1]" );

        //Modify customer
        customer.set( "LASTNAME", "Pavick" );

        //Build apply changes command
        das.applyChanges( root );

        //Verify changes
        root = select.executeQuery();
        assertEquals( "Pavick", root.getString( "CUSTOMER[1]/LASTNAME" ) );

    }    

    
    
    
    
    
}