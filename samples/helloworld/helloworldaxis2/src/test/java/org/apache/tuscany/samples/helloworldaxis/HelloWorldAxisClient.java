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
package org.apache.tuscany.samples.helloworldaxis;


public class HelloWorldAxisClient {

    final static String urlstrTuscanyService = "http://localhost:8080/helloworldws-SNAPSHOT/services/HelloWorldService";

    public String getGreetings(String urlstr, String name) throws Exception {
        HelloWorldServiceImplStub stub = new HelloWorldServiceImplStub(urlstr);

        GetGreetings input = new GetGreetings();
        input.setIn0(name);

        GetGreetingsResponse response=stub.getGreetings(input);

        return response.getGetGreetingsReturn();
    }

    /**
     * @param args either &lt;name&gt; or &lt;service url&gt; &lt;name&gt;
     * @throws ServiceException
     */
    public static void main(String[] args) throws Exception {

        String urlstr = args.length > 1 ? args[0]
                : urlstrTuscanyService;

        String name = args.length < 1 ? "World" : (args.length > 1 ? args[1]
                : args[0]);

        System.out.println((new HelloWorldAxisClient()).getGreetings(urlstr, name));

    }

}
