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

package org.apache.tuscany.sca.implementation.node.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.tuscany.sca.implementation.data.collection.Entry;
import org.apache.tuscany.sca.implementation.data.collection.Item;
import org.apache.tuscany.sca.implementation.data.collection.ItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.LocalItemCollection;
import org.apache.tuscany.sca.implementation.data.collection.NotFoundException;
import org.osoa.sca.ServiceRuntimeException;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;

/**
 * Implementation of a node process collection service. 
 *
 * @version $Rev$ $Date$
 */
@Scope("COMPOSITE")
@Service(interfaces={ItemCollection.class, LocalItemCollection.class})
public class NodeProcessCollectionImpl implements ItemCollection, LocalItemCollection {

    private final static Logger logger = Logger.getLogger(NodeProcessCollectionImpl.class.getName());    

    private List<SCANodeVM> nodeVMs = new ArrayList<SCANodeVM>();

    /**
     * Initialize the component.
     */
    @Init
    public void initialize() {
    }
    
    public Entry<String, Item>[] getAll() {
        
        // Return all the running VMs
        List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
        for (SCANodeVM vm: nodeVMs) {
            entries.add(entry(vm));
        }
        return entries.toArray(new Entry[entries.size()]);
    }

    public Item get(String key) throws NotFoundException {

        // Return the specified VM
        SCANodeVM vm = vm(key);
        if (vm == null) {
            throw new NotFoundException();
        }
        
        return item(vm);
    }

    public String post(String key, Item item) {
        
        // Start a new VM and add it to the collection
        SCANodeVM vm = vm(key);
        if (vm != null) {
            return key;
        }
        
        vm = new SCANodeVM(key);
        nodeVMs.add(0, vm);
        try {
            vm.start();
        } catch (IOException e) {
            throw new ServiceRuntimeException(e);
        }
        
        return key;
    }

    public void put(String key, Item item) throws NotFoundException {
        throw new UnsupportedOperationException();
    }

    public void delete(String key) throws NotFoundException {
        
        // Stop a VM and remove it from the collection
        SCANodeVM vm = vm(key);
        if (vm != null) {
            try {
                vm.stop();
            } catch (InterruptedException e) {
                throw new ServiceRuntimeException(e);
            }
            nodeVMs.remove(vm);
        } else {
            throw new NotFoundException();
        }
    }
    
    public Entry<String, Item>[] query(String queryString) {
        if (queryString.startsWith("node=")) {
            
            // Return the log for the specified VM
            String key = queryString.substring(queryString.indexOf('=') + 1);
            List<Entry<String, Item>> entries = new ArrayList<Entry<String, Item>>();
            for (SCANodeVM vm: nodeVMs) {
                if (vm.getNodeName().equals(key)) {
                    entries.add(entry(vm));
                }
            }
            return entries.toArray(new Entry[entries.size()]);
            
        } else {
            throw new UnsupportedOperationException();
        }
    }
    
    /**
     * Returns the specified VM.
     * 
     * @param key
     * @return
     */
    private SCANodeVM vm(String key) {
        for (SCANodeVM vm: nodeVMs) {
            if (key.equals(vm.getNodeName())) {
                return vm;
            }
        }
        return null;
    }

    /**
     * Returns an entry representing a VM.
     * 
     * @param vm
     * @return
     */
    private static Entry<String, Item> entry(SCANodeVM vm) {
        Entry<String, Item> entry = new Entry<String, Item>();
        entry.setKey(vm.getNodeName());
        entry.setData(item(vm));
        return entry;
    }
    
    /**
     * Returns an item representing a VM.
     * 
     * @param vm
     * @return
     */
    private static Item item(SCANodeVM vm) {
        Item item = new Item();
        String key = vm.getNodeName();
        item.setTitle(title(key));
        item.setLink("/node-image/" + vm.getNodeName());
        item.setContents("<span id=\"log\" style=\"white-space: nowrap; font-size: small\">" + vm.getLog().toString() + "</span>");
        return item;
    }
    
    /**
     * Represent a child Java VM running an SCA node.
     */
    private static class SCANodeVM {
        private String nodeName;
        private StringBuffer log;
        private Process process;
        private Thread monitor;
        private int status;
        
        SCANodeVM(String nodeName) {
            log = new StringBuffer();
            this.nodeName =nodeName;
        }
        
        /**
         * Starts a node in a new VM.
         */
        private void start() throws IOException {
            
            // Build the Java VM command line
            Properties props = System.getProperties();
            String java = props.getProperty("java.home") + "/bin/java";
            String cp = props.getProperty("java.class.path");
            String main = NodeImplementationLauncher.class.getName();
            String url = "http://localhost:9990/node-image/" + nodeName;
            final String[] command = new String[]{ java, "-cp", cp, main , url};
            
            // Start the VM
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            process = builder.start();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Start a thread to monitor the process
            monitor = new Thread(new Runnable() {
                public void run() {
                    try {
                        for (;;) {
                            String s = reader.readLine();
                            if (s != null) {
                                System.out.println(s);
                                log.append(s + "<br>");
                            } else {
                                break;
                            }
                        }
                        status = process.waitFor();
                    } catch (IOException e) {
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            monitor.start();
        }

        /**
         * Returns the composite used to start this VM.
         * @return
         */
        String getNodeName() {
            return nodeName;
        }
        
        /**
         * Returns the log for this VM.
         * 
         * @return
         */
        StringBuffer getLog() {
            return log;
        }

        /**
         * Returns true if the VM is alive
         * 
         * @return
         */
        private boolean isAlive() {
            return monitor.isAlive();
        }
        
        /**
         * Returns the VM status code.
         * @return
         */
        int getStatus() {
            return status;
        }

        /**
         * Stops the VM.
         * 
         * @throws InterruptedException
         */
        private void stop() throws InterruptedException {
            process.destroy();
            monitor.join();
        }
    }
    
    /**
     * Returns a node title.
     * 
     * @param key
     * @return
     */
    private static String title(String key) {
        return key;
    }

}
