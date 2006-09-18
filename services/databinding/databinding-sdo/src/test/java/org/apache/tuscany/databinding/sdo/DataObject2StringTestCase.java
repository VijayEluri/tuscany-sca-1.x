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

package org.apache.tuscany.databinding.sdo;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.apache.tuscany.spi.model.DataType;

import com.example.ipo.sdo.PurchaseOrderType;
import commonj.sdo.DataObject;

/**
 * 
 */
public class DataObject2StringTestCase extends SDOTransformerTestCaseBase {
    @Override
    protected DataType<?> getSourceDataType() {
        return new DataType<QName>(binding, PurchaseOrderType.class, orderQName);
    }

    @Override
    protected DataType<?> getTargetDataType() {
        return new DataType<Class<String>>(String.class, String.class);
    }

    public final void testTransform() {
        String xml = new DataObject2String().transform(dataObject, context);
        Assert.assertTrue(xml.indexOf("<city>San Jose</city>") != -1);
        DataObject po = new String2DataObject().transform(xml, reversedContext);
        Assert.assertTrue(po instanceof PurchaseOrderType);
        PurchaseOrderType orderType = (PurchaseOrderType) po;
        Assert.assertEquals("San Jose", orderType.getBillTo().getCity());
    }

}
