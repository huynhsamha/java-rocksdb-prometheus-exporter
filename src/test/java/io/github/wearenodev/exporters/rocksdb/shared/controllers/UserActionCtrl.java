package io.github.wearenodev.exporters.rocksdb.shared.controllers;

import io.github.wearenodev.exporters.rocksdb.shared.DemoRocksDB;
import io.github.wearenodev.exporters.rocksdb.shared.entity.ActionInfo;
import org.apache.commons.lang3.SerializationUtils;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.util.ArrayList;
import java.util.List;

public class UserActionCtrl {

    private static final RocksDB db = DemoRocksDB.getDb();
    private static final ColumnFamilyHandle cfHandle = DemoRocksDB.getCFHandle("UserAction");

    public static String buildPrefixKey(int uid) {
        return uid + "/";
    }

    public static String buildKey(int uid, long actionId) {
        return uid + "/" + actionId;
    }

    public static String buildKey(ActionInfo o) {
        return buildKey(o.getUid(), o.getId());
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

    public static List<ActionInfo> scan(int uid) {
        String strSeekKey = buildPrefixKey(uid);
        byte[] seekKey = strSeekKey.getBytes();
        List<ActionInfo> res = new ArrayList<>();
        RocksIterator it = db.newIterator(cfHandle);
        it.seek(seekKey);
        for (; it.isValid(); it.next()) {
            String key = new String(it.key());
            if (!key.startsWith(strSeekKey)) break;
            byte[] value = it.value();
            ActionInfo actionInfo = fromBytes(value);
            res.add(actionInfo);
        }
        return res;
    }

}
