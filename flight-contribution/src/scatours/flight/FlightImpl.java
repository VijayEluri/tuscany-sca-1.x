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
package scatours.flight;

import java.util.ArrayList;
import java.util.List;

import org.osoa.sca.annotations.Callback;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

import scatours.common.Search;
import scatours.common.SearchCallback;
import scatours.common.TripItem;
import scatours.common.TripLeg;

/**
 * An implementation of the Hotel service
 */
@Scope("STATELESS")
@Service(interfaces={Search.class})
public class FlightImpl implements Search {
    
    private List<FlightInfo> flights = new ArrayList<FlightInfo>();
    
    @Callback
    protected SearchCallback searchCallback; 

    @Init
    public void init() {
        flights.add(new FlightInfo("IA26", 
                                   "Island Airlines Boeing 747",
                                   "LGW",
                                   "ANU",
                                   "06/12/08",
                                   "06/12/08",
                                   "350",
                                   250,
                                   "USD",
                                   "http://localhost:8085/tbd" ));
        flights.add(new FlightInfo("IA27", 
                                   "Island Airlines Boeing 747",
                                   "ANU",
                                   "LGW",
                                   "13/12/08",
                                   "13/12/08",
                                   "350",
                                   250,
                                   "USD",
                                   "http://localhost:8085/tbd" ));

    }
    
    public TripItem[] searchSynch(TripLeg tripLeg) {
        List<TripItem> items = new ArrayList<TripItem>();
        
        // find outbound leg
        for(FlightInfo flight : flights){
            if ((flight.getFromLocation().equals(tripLeg.getFromLocation())) &&
                (flight.getToLocation().equals(tripLeg.getToLocation())) &&
                (flight.getFromDate().equals(tripLeg.getFromDate()))){
                TripItem item = new TripItem("1", 
                                             "Flight",
                                             flight.getName(), 
                                             flight.getDescription(), 
                                             flight.getFromLocation() + " - " + flight.getToLocation(),
                                             flight.getFromDate(),
                                             flight.getToDate(),
                                             flight.getPricePerSeat(),
                                             flight.getCurrency(),
                                             flight.getLink());
                items.add(item);
            }
        }
        
        // find return leg
        for(FlightInfo flight : flights){
            if ((flight.getFromLocation().equals(tripLeg.getToLocation())) &&
                (flight.getToLocation().equals(tripLeg.getFromLocation())) &&
                (flight.getFromDate().equals(tripLeg.getToDate()))){
                TripItem item = new TripItem("1", 
                                             "Flight",
                                             flight.getName(), 
                                             flight.getDescription(), 
                                             flight.getFromLocation() + " - " + flight.getToLocation(),
                                             flight.getFromDate(),
                                             tripLeg.getToDate(),
                                             flight.getPricePerSeat(),
                                             flight.getCurrency(),
                                             flight.getLink());
                items.add(item);
            }
        }        
        
        return items.toArray(new TripItem[items.size()]);
    }
    
    public void searchAsynch(TripLeg tripLeg) {
        
        // return available hotels
        searchCallback.searchResults(searchSynch(tripLeg));  
    }
}
