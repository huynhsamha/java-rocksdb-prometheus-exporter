package io.github.huynhsamha.exporters.rocksdb.shared.controllers;

import io.github.huynhsamha.exporters.rocksdb.shared.entity.UserInfo;
import io.github.huynhsamha.exporters.rocksdb.shared.DemoRocksDB;
import org.apache.commons.lang3.SerializationUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class UserInfoCtrl {

    private static final RocksDB db = DemoRocksDB.getDb();
    private static final ColumnFamilyHandle cfHandle = DemoRocksDB.getCFHandle("UserInfo");

    public static String buildKey(int uid) {
        return String.valueOf(uid);
    }

    public static byte[] buildBytesKey(int uid) {
        return buildKey(uid).getBytes();
    }

    public static String buildKey(UserInfo o) {
        return buildKey(o.getId());
    }

    public static byte[] buildBytesKey(UserInfo o) {
        return buildKey(o).getBytes();
    }

    public static byte[] toBytes(UserInfo o) {
        try {
            return SerializationUtils.serialize(o);
        } catch (Exception ex) {
            return null;
        }
    }

    public static UserInfo fromBytes(byte[] bytes) {
        try {
            return SerializationUtils.deserialize(bytes);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void put(UserInfo o) {
        byte[] value = toBytes(o);
        if (value == null) return;
        try {
            db.put(cfHandle, buildBytesKey(o), value);
        } catch (RocksDBException ex) {
            System.err.println(ex);
        }
    }

    public static UserInfo get(int id) {
        byte[] key = buildBytesKey(id);
        try {
            byte[] value = db.get(cfHandle, key);
            return fromBytes(value);
        } catch (RocksDBException ex) {
            System.err.println(ex);
            return  null;
        }
    }

}
