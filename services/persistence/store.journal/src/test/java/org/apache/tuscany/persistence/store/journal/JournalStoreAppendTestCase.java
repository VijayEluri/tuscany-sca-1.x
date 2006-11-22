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
package org.apache.tuscany.persistence.store.journal;

import java.util.UUID;

import org.apache.tuscany.spi.component.SCAObject;

import junit.framework.TestCase;
import org.apache.tuscany.service.persistence.store.Store;
import org.apache.tuscany.service.persistence.store.StoreMonitor;
import org.easymock.IAnswer;
import org.easymock.classextension.EasyMock;
import org.objectweb.howl.log.LogEventListener;

/**
 * @version $Rev$ $Date$
 */
public class JournalStoreAppendTestCase extends TestCase {
    @SuppressWarnings({"FieldCanBeLocal"})
    private JournalStore store;
    private SCAObject owner;

    public void testOrderedShutdown() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        Journal journal = EasyMock.createMock(Journal.class);
        journal.setLogEventListener(EasyMock.isA(LogEventListener.class));
        journal.open();
        journal.close();
        EasyMock.replay(journal);
        store = new JournalStore(monitor, journal) {
        };
        store.init();
        store.destroy();
        EasyMock.verify(journal);
    }

    public void testAppendRecord() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        Journal journal = EasyMock.createMock(Journal.class);
        journal.setLogEventListener(EasyMock.isA(LogEventListener.class));
        journal.open();
        final UUID id = UUID.randomUUID();
        EasyMock.expect(journal.writeHeader(EasyMock.isA(byte[].class), EasyMock.eq(false)))
            .andStubAnswer(new IAnswer() {
                public Object answer() throws Throwable {
                    Header header = new Header();
                    header.setFields(new byte[][]{(byte[]) EasyMock.getCurrentArguments()[0]});
                    // deserialize the message to test the format is correct
                    SerializationHelper.deserializeHeader(header);
                    assertTrue("Operation not INSERT", Header.INSERT == header.getOperation());
                    assertTrue("Expiration incorrect", Store.NEVER == header.getExpiration());
                    assertTrue("Least significant id incorrect",
                        id.getLeastSignificantBits() == header.getLeastSignificant());
                    assertTrue("Most significant id incorrect",
                        id.getMostSignificantBits() == header.getMostSignificant());
                    assertTrue("Records incorrect", 1 == header.getNumBlocks());
                    assertTrue("Owner id incorrect", "foo".equals(header.getOwnerId()));
                    return 1L;
                }
            });
        EasyMock.expect(journal.writeBlock(EasyMock.isA(byte[].class), EasyMock.isA(byte[].class), EasyMock.eq(true)))
            .andStubAnswer(new IAnswer() {
                public Object answer() throws Throwable {
                    byte[] payload = (byte[]) EasyMock.getCurrentArguments()[0];
                    assertTrue("Block data incorrect", "test".equals(SerializationHelper.deserialize(payload)));
                    return 1L;
                }
            });
        journal.close();
        EasyMock.replay(journal);
        store = new JournalStore(monitor, journal) {
        };
        store.init();
        store.appendRecord(owner, id, "test", Store.NEVER);
        store.destroy();
        EasyMock.verify(journal);
    }

    /**
     * Verifies that a written record will be cached. This is verified by the fact that long-term storage is never
     * accessed.
     */
    public void testAppendRecordCache() throws Exception {
        StoreMonitor monitor = EasyMock.createMock(StoreMonitor.class);
        Journal journal = EasyMock.createMock(Journal.class);
        journal.setLogEventListener(EasyMock.isA(LogEventListener.class));
        journal.open();
        final UUID id = UUID.randomUUID();
        EasyMock.expect(journal.writeHeader(EasyMock.isA(byte[].class), EasyMock.eq(false))).andReturn(1L);
        EasyMock.expect(journal.writeBlock(EasyMock.isA(byte[].class), EasyMock.isA(byte[].class), EasyMock.eq(true)))
            .andReturn(1L);
        journal.close();
        EasyMock.replay(journal);
        store = new JournalStore(monitor, journal) {
        };
        store.init();
        store.appendRecord(owner, id, "test", Store.NEVER);
        assertEquals("test", store.readRecord(owner, id));
        store.destroy();
        EasyMock.verify(journal);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.cleanupLog();
        owner = EasyMock.createMock(SCAObject.class);
        EasyMock.expect(owner.getCanonicalName()).andReturn("foo").atLeastOnce();
        EasyMock.replay(owner);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        TestUtils.cleanupLog();
    }
}
