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
package org.apache.tuscany.service.persistence.store.jdbc.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.tuscany.spi.services.store.StoreWriteException;

import org.apache.tuscany.service.persistence.store.jdbc.Converter;
import org.apache.tuscany.service.persistence.store.jdbc.TCCLObjectInputStream;

/**
 * Base class for <code>Converter</code> implementations
 *
 * @version $Rev$ $Date$
 */
public abstract class AbstractConverter implements Converter {
    public static final int OWNER = 1;
    public static final int MOST_SIGNIFICANT_BITS = 2;
    public static final int LEAST_SIGNIFICANT_BITS = 3;
    public static final int EXPIRATION = 4;
    public static final int DATA = 5;
    public static final int OBJECT_UPDATE = 1;
    public static final int OWNER_UPDATE = 2;
    public static final int MOST_SIGNIFICANT_BITS_UPDATE = 3;
    public static final int LEAST_SIGNIFICANT_BITS_UPDATE = 4;

    protected String findSql = "SELECT * FROM CONVERSATION_STATE WHERE OWNER = ? AND ID_1 = ? AND ID_2 = ?";
    protected String insertSql =
        "INSERT INTO CONVERSATION_STATE (OWNER, ID_1, ID_2, EXPIRATION, OBJECT) VALUES (?, ?, ?, ?, ?)";
    protected String updateSql = "UPDATE CONVERSATION_STATE SET OBJECT = ? WHERE OWNER = ? AND ID_1 = ? AND ID_2 = ?";
    protected String selectUpdateSql =
        "SELECT ID_1 FROM CONVERSATION_STATE WHERE OWNER = ? AND ID_1 = ? AND ID_2 = ? FOR UPDATE";
    protected String deleteSql = "DELETE FROM CONVERSATION_STATE WHERE ID_1 = ? AND ID_2 = ?";
    protected String deleteExpiredSql = "DELETE FROM CONVERSATION_STATE WHERE EXPIRATION <= ?";
    protected String deleteRecordSql = "DELETE FROM CONVERSATION_STATE WHERE OWNER = ? AND ID_1 = ? AND ID_2 = ?";

    public String getInsertSql() {
        return insertSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getFindSql() {
        return findSql;
    }

    public String getDeleteSql() {
        return deleteSql;
    }

    public String getDeleteExpiredSql() {
        return deleteExpiredSql;
    }

    public String getSelectUpdateSql() {
        return selectUpdateSql;
    }

    public String getDeleteRecordSql() {
        return deleteRecordSql;
    }

    public boolean findAndLock(PreparedStatement stmt, String ownerId, UUID id)
        throws StoreWriteException {
        try {
            stmt.setString(OWNER, ownerId);
            stmt.setLong(MOST_SIGNIFICANT_BITS, id.getMostSignificantBits());
            stmt.setLong(LEAST_SIGNIFICANT_BITS, id.getLeastSignificantBits());
            return stmt.executeQuery().next();
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        }
    }

    public void delete(PreparedStatement stmt, String ownerId, UUID id) throws StoreWriteException {
        try {
            stmt.setString(OWNER, ownerId);
            stmt.setLong(MOST_SIGNIFICANT_BITS, id.getMostSignificantBits());
            stmt.setLong(LEAST_SIGNIFICANT_BITS, id.getLeastSignificantBits());
        } catch (SQLException e) {
            throw new StoreWriteException(e);
        }
    }

    protected byte[] serialize(Serializable serializable) throws IOException {
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bas);
        out.writeObject(serializable);
        return bas.toByteArray();
    }

    protected Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        return new TCCLObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }

}
