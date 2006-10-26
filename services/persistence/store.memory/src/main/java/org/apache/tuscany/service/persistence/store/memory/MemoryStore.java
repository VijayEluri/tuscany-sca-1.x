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
package org.apache.tuscany.service.persistence.store.memory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osoa.sca.annotations.Destroy;
import org.osoa.sca.annotations.Init;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

import org.apache.tuscany.api.annotation.Monitor;
import org.apache.tuscany.service.persistence.store.RecoveryListener;
import org.apache.tuscany.service.persistence.store.Store;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import org.apache.tuscany.service.persistence.store.StoreWriteException;

/**
 * Implements a non-durable, non-transactional store using a simple in-memory map
 *
 * @version $Rev$ $Date$
 */
@Scope("MODULE")
public class MemoryStore implements Store {

    private Map<Object, Record> store;
    // TODO integrate with a core threading scheme
    private ScheduledExecutorService scheduler;
    private long reaperInterval = 300000;
    private StoreMonitor monitor;

    public MemoryStore(@Monitor StoreMonitor monitor) {
        this.monitor = monitor;
        this.store = new ConcurrentHashMap<Object, Record>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Property
    public void setReaperInterval(long reaperInterval) {
        this.reaperInterval = reaperInterval;
    }

    public long getReaperInterval() {
        return reaperInterval;
    }

    @Init(eager = true)
    public void init() {
        monitor.start("In-memory store started");
        scheduler.scheduleWithFixedDelay(new Reaper(), reaperInterval, reaperInterval, TimeUnit.MILLISECONDS);
    }

    @Destroy
    public void destroy() {
        scheduler.shutdown();
        monitor.stop("In-memory store stopped");
    }

    public void writeRecord(Object id, Object record) throws StoreWriteException {
        writeRecord(id, record, NEVER, false);
    }

    public void writeRecord(Object id, Object record, long expiration) throws StoreWriteException {
        writeRecord(id, record, expiration, false);
    }

    public void writeRecord(Object id, Object record, boolean force) throws StoreWriteException {
        store.put(id, new Record(record, NEVER));

    }

    public void writeRecord(Object id, Object record, long expiration, boolean force) throws StoreWriteException {
        store.put(id, new Record(record, expiration));
    }

    public Object readRecord(Object id) {
        Record record = store.get(id);
        if (record != null) {
            return record.data;
        }
        return null;
    }

    public void removeRecords() {
        store.clear();
    }

    public void recover(RecoveryListener listener) {
        monitor.beginRecover();
        listener.onBegin();
        for (Object id : store.keySet()) {
            monitor.recover(id);
            listener.onRecord(id);
        }
        listener.onEnd();
        monitor.endRecover();
    }

    private class Record {
        private Object data;
        private long expiration = NEVER;

        public Record(Object data, long expiration) {
            this.data = data;
            this.expiration = expiration;
        }
    }

    private class Reaper implements Runnable {

        public void run() {
            long now = System.currentTimeMillis();
            for (Map.Entry<Object, Record> entry : store.entrySet()) {
                final long expiration = entry.getValue().expiration;
                if (expiration != NEVER && now >= expiration) {
                    store.remove(entry.getKey());
                }
            }
        }
    }

}
