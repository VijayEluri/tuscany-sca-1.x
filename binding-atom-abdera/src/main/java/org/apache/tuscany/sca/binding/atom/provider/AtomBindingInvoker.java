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
package org.apache.tuscany.sca.binding.atom.provider;

import java.io.InputStreamReader;
import java.io.StringWriter;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParserOptions;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.tuscany.sca.binding.atom.collection.NotFoundException;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.invocation.Invoker;
import org.apache.tuscany.sca.invocation.Message;
import org.osoa.sca.ServiceRuntimeException;

/**
 * Invoker for the Atom binding.
 * 
 * @version $Rev$ $Date$
 */
class AtomBindingInvoker implements Invoker {

    Operation operation;
    String uri;
    HttpClient httpClient;
    String authorizationHeader;
    
    //FIXME Support conversion to/from data API entries 

    AtomBindingInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
        this.operation = operation;
        this.uri = uri;
        this.httpClient = httpClient;
        this.authorizationHeader = authorizationHeader;
    }

    public Message invoke(Message msg) {
        // Shouldn't get here, as the only supported methods are
        // defined in the ResourceCollection interface, and implemented
        // by specific invoker subclasses
        throw new UnsupportedOperationException(operation.getName());
    }

    /**
     * Get operation invoker
     */
    public static class GetInvoker extends AtomBindingInvoker {

        public GetInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Get an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri + "/" + id);
            getMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200) {
                	Document<Entry> doc = Abdera.getNewParser().parse(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                    Entry feedEntry = doc.getRoot();

                    msg.setBody(feedEntry);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                // Will be released by the abdera parser
                //getMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Post operation invoker
     */
    public static class PostInvoker extends AtomBindingInvoker {

        public PostInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Post an entry
            Entry feedEntry = (Entry)((Object[])msg.getBody())[0];

            // Send an HTTP POST
            PostMethod postMethod = new PostMethod(uri);
            postMethod.setRequestHeader("Authorization", authorizationHeader);
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                feedEntry.writeTo(writer);
                postMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                postMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(postMethod);
                int status = postMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200 || status == 201) {
                	Document<Entry> doc = Abdera.getNewParser().parse(new InputStreamReader(postMethod.getResponseBodyAsStream()));
                    Entry createdEntry = doc.getRoot();

                    msg.setBody(createdEntry);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                // Will be released by the abdera parser
                //postMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Put operation invoker
     */
    public static class PutInvoker extends AtomBindingInvoker {

        public PutInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // Put an entry
            Object[] args = (Object[])msg.getBody();
            String id = (String)args[0];
            Entry feedEntry = (Entry)args[1];

            // Send an HTTP PUT
            PutMethod putMethod = new PutMethod(uri + "/" + id);
            putMethod.setRequestHeader("Authorization", authorizationHeader);
            try {

                // Write the Atom entry
                StringWriter writer = new StringWriter();
                feedEntry.writeTo(writer);
                putMethod.setRequestHeader("Content-type", "application/atom+xml; charset=utf-8");
                putMethod.setRequestEntity(new StringRequestEntity(writer.toString()));

                httpClient.executeMethod(putMethod);
                int status = putMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200 || status == 201) {
                    try {
                    	Document<Entry> doc = Abdera.getNewParser().parse(new InputStreamReader(putMethod.getResponseBodyAsStream()));
                        Entry updatedEntry = doc.getRoot();

                        msg.setBody(updatedEntry);
                    } catch (Exception e) {
                        // Returning the updated entry is optional
                    }

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                // Will be released by the abdera parser
                //putMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * Delete operation invoker
     */
    public static class DeleteInvoker extends AtomBindingInvoker {

        public DeleteInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Delete an entry
            String id = (String)((Object[])msg.getBody())[0];

            // Send an HTTP DELETE
            DeleteMethod deleteMethod = new DeleteMethod(uri + "/" + id);
            deleteMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(deleteMethod);
                int status = deleteMethod.getStatusCode();

                // Read the Atom entry
                if (status == 200) {
                    msg.setBody(null);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                deleteMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * GetAll operation invoker
     */
    public static class GetAllInvoker extends AtomBindingInvoker {

        public GetAllInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {

            // Get a feed

            // Send an HTTP GET
            GetMethod getMethod = new GetMethod(uri);
            getMethod.setRequestHeader("Authorization", authorizationHeader);
            try {
                httpClient.executeMethod(getMethod);
                int status = getMethod.getStatusCode();

                // Read the Atom feed
                if (status == 200) {
                    Document<Feed> doc = Abdera.getNewParser().parse(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                    Feed feed = doc.getRoot();

                    msg.setBody(feed);

                } else if (status == 404) {
                    msg.setFaultBody(new NotFoundException());
                } else {
                    msg.setFaultBody(new ServiceRuntimeException("HTTP status code: " + status));
                }

            } catch (Exception e) {
                msg.setFaultBody(new ServiceRuntimeException(e));
            } finally {
                // Will be released by the abdera parser
                //getMethod.releaseConnection();
            }

            return msg;
        }
    }

    /**
     * PostMedia operation invoker
     */
    public static class PostMediaInvoker extends AtomBindingInvoker {

        public PostMediaInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

    /**
     * PutMedia operation invoker
     */
    public static class PutMediaInvoker extends AtomBindingInvoker {

        public PutMediaInvoker(Operation operation, String uri, HttpClient httpClient, String authorizationHeader) {
            super(operation, uri, httpClient, authorizationHeader);
        }

        @Override
        public Message invoke(Message msg) {
            // TODO implement
            return super.invoke(msg);
        }
    }

}
