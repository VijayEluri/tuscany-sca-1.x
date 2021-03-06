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
package supplychain.shipper;


/**
 * This class implements the Shipper service.
 */
public class OSGiVersionedShipperServiceImpl implements Shipper {

    private OSGiVersionedShipperImpl shipperImpl;

    private String shipperName;
    
    private int version;

    OSGiVersionedShipperServiceImpl(OSGiVersionedShipperImpl shipperImpl, String shipperName, int version) {
        this.shipperImpl = shipperImpl;
        this.shipperName = shipperName;
        this.version = version;
    }

    public void processShipment(String order) {

        System.out.println("Shipper.processShipment, version " + version + " customer is " + shipperImpl.customer);
        shipperImpl.customer.notifyShipment(order + ", shipped (" + shipperName + ")");

    }

}
