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

package scatours.client.impl;

import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import com.tuscanyscatours.common.Search;
import com.tuscanyscatours.common.SearchCallback;
import com.tuscanyscatours.common.TripItem;
import com.tuscanyscatours.common.TripLeg;

@Service(Runnable.class)
public class InteractionRemoteClientImpl implements Runnable, SearchCallback {

    @Reference
    protected Search hotelSearchRemote;

    public void run() {
        System.out.println("\nCalling hotel component over a remote binding");
        TripLeg tripLeg = getTestTripLeg();
        TripItem[] tripItems = hotelSearchRemote.searchSynch(tripLeg);
        for (TripItem tripItem : tripItems) {
            System.out.println("Found hotel - " + tripItem.getName());
        }
    }

    public void searchResults(TripItem[] items) {
        // we are calling the hotel component synchronously here
        // so the callback interface is not used
    }

    public void setPercentComplete(String searchComponent, int percentComplete) {
        // Not used in this sample
    }

    private TripLeg getTestTripLeg() {
        TripLeg tripLeg = new TripLeg();
        tripLeg.setFromLocation("LGW");
        tripLeg.setToLocation("FLR");
        tripLeg.setFromDate("06/12/09 00:00");
        tripLeg.setToDate("13/12/09 00:00");
        tripLeg.setNoOfPeople("1");
        tripLeg.setId("TRIP27");
        return tripLeg;
    }
}
