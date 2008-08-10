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
package scatours.hotel;

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
public class HotelImpl implements Search {
    
    private List<HotelInfo> hotels = new ArrayList<HotelInfo>();
    
    @Callback
    protected SearchCallback searchCallback; 

    @Init
    public void init() {
        hotels.add(new HotelInfo("Deep Bay Hotel", 
                             "Wonderful sea views and a relaxed atmosphere", 
                             "ANU", 
                             "06/12/08",
                             "200",
                             100,
                             "USD",
                             "http://localhost:8085/tbd" ));
        hotels.add(new HotelInfo("Long Bay Hotel", 
                             "Friendly staff and an ocean breeze", 
                             "ANU", 
                             "06/12/08",
                             "200",
                             100,
                             "USD",
                             "http://localhost:8085/tbd" ));
        hotels.add(new HotelInfo("City Hotel", 
                             "Smart rooms and early breakfasts", 
                             "NY", 
                             "06/12/08",
                             "200",
                             100,
                             "USD",
                             "http://localhost:8085/tbd" ));
        hotels.add(new HotelInfo("County Hotel", 
                             "The smell of the open country", 
                             "SOU", 
                             "06/12/08",
                             "200",
                             100,
                             "USD",
                             "http://localhost:8085/tbd" ));
    }
    
    public TripItem[] searchSynch(TripLeg tripLeg) {
        List<TripItem> items = new ArrayList<TripItem>();
        
        // find available hotels
        for(HotelInfo hotel : hotels){
            if (hotel.getLocation().equals(tripLeg.getToLocation())){
                TripItem item = new TripItem("",
                                             "",
                                             "Hotel",
                                             hotel.getName(), 
                                             hotel.getDescription(), 
                                             hotel.getLocation(), 
                                             tripLeg.getFromDate(),
                                             tripLeg.getToDate(),
                                             hotel.getPricePerBed(),
                                             hotel.getCurrency(),
                                             hotel.getLink());
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
