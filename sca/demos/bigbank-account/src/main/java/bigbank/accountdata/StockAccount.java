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
package bigbank.accountdata;

/**
 * @version $$Rev$$ $$Date: 2007-04-09 12:03:34 -0700 (Mon, 09 Apr
 *          2007) $$
 */

public class StockAccount {
    private String accountNumber;
    private String symbol;
    private int quantity;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String n) {
        this.accountNumber = n;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(int a) {
        this.quantity = a;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String s) {
        this.symbol = s;
    }

    public String toString() {
        return accountNumber + ", symbol:" + symbol + ", quantity:" + quantity;
    }
}
