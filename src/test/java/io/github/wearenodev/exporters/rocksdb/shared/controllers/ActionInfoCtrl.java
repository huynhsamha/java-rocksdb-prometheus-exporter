package io.github.wearenodev.exporters.rocksdb.shared.controllers;

import io.github.wearenodev.exporters.rocksdb.shared.DemoRocksDB;
import io.github.wearenodev.exporters.rocksdb.shared.entity.ActionInfo;
import org.apache.commons.lang3.SerializationUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class ActionInfoCtrl {

    private static final RocksDB db = DemoRocksDB.getDb();
    private static final ColumnFamilyHandle cfHandle = DemoRocksDB.getCFHandle("ActionInfo");

    public static String buildKey(long actionId) {
        return String.valueOf(actionId);
    }

    public static byte[] buildBytesKey(long actionId) {
        return buildKey(actionId).getBytes();
    }

    public static String buildKey(ActionInfo o) {
        return buildKey(o.getId());
    }

    public static byte[] buildBytesKey(ActionInfo o) {
        return buildKey(o).getBytes();
    }

    public static byte[] toBytes(ActionInfo o) {
        try {
            return SerializationUtils.serialize(o);
        } catch (Exception ex) {
            return null;
        }
    }

    public static ActionInfo fromBytes(byte[] bytes) {
        try {
            return SerializationUtils.deserialize(bytes);
        } catch (Exception ex) {
            return null;
        }
    }

    public static void put(ActionInfo o) {
        byte[] value = toBytes(o);
        if (value == null) return;
        try {
            db.put(cfHandle, buildBytesKey(o), value);
        } catch (RocksDBException ex) {
            System.err.println(ex);
        }
    }

    public static ActionInfo get(long actionId) {
        byte[] key = buildBytesKey(actionId);
        try {
            byte[] value = db.get(cfHandle, key);
            return fromBytes(value);
        } catch (RocksDBException ex) {
            System.err.println(ex);
            return null;
        }
    }

}
