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
package org.apache.tuscany.sdo.test;

import java.math.BigDecimal;

import org.apache.tuscany.sdo.util.SDOUtil;

import com.example.simple.Quote;
import com.example.simple.SimpleFactory;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLHelper;


public class SimpleStaticTest
{
  /**
   * Simple Static SDO 2 test.
   */
  public static void main(String[] args)
  {
    try
    {
      SDOUtil.registerStaticTypes(SimpleFactory.class);
      
      //Quote quote = (Quote)DataFactory.INSTANCE.create(Quote.class);
      Quote quote = SimpleFactory.INSTANCE.createQuote();
      
      quote.setSymbol("fbnt");
      quote.setCompanyName("FlyByNightTechnology");
      quote.setPrice(new BigDecimal("1000.0"));
      quote.setOpen1(new BigDecimal("1000.0"));
      quote.setHigh(new BigDecimal("1000.0"));
      quote.setLow(new BigDecimal("1000.0"));
      quote.setVolume(1000);
      quote.setChange1(1000);

      //Quote child = (Quote)((DataObject)quote).createDataObject(8);
      Quote child = SimpleFactory.INSTANCE.createQuote();
      quote.getQuotes().add(child);
      child.setPrice(new BigDecimal("2000.0"));

      XMLHelper.INSTANCE.save((DataObject)quote, "http://www.example.com/simple", "stockQuote", System.out);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
