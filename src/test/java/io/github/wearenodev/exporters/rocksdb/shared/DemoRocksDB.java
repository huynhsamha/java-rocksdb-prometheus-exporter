package io.github.wearenodev.exporters.rocksdb.shared;

import io.github.wearenodev.exporters.rocksdb.shared.utils.FileUtils;
import org.rocksdb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoRocksDB {

    static String logDirPath = "/tmp/rocksdb-demo/log";
    static String dataDirPath = "/tmp/rocksdb-demo/data";
    static RocksDB db;
    static DBOptions options;
    static Statistics stats;
    static List<ColumnFamilyDescriptor> cfDescriptors;
    static List<ColumnFamilyHandle> cfHandles;
    static Map<String, ColumnFamilyHandle> mapCFHandles = new HashMap<>();

    static {
        RocksDB.loadLibrary();

        FileUtils.ensureDir(logDirPath);
        FileUtils.ensureDir(dataDirPath);

        initDB();
    }

    private static ColumnFamilyDescriptor createCFDesc(String cfName) {
        ColumnFamilyOptions op = new ColumnFamilyOptions();
        // TODO: set your CF options
        return new ColumnFamilyDescriptor(cfName.getBytes(), op);
    }

    private static void initDB() {
        stats = new Statistics();
        options = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true)
                .setDbLogDir(logDirPath)
                .setStatistics(stats);
        // TODO: set your DB options

        cfDescriptors = new ArrayList<>();
        cfHandles = new ArrayList<>(); // will be filled by rocksdb when open

        // Default CF is required in RocksDB
        cfDescriptors.add(createCFDesc(new String(RocksDB.DEFAULT_COLUMN_FAMILY)));

        // TODO: set up your CFs
        cfDescriptors.add(createCFDesc("UserInfo"));
        cfDescriptors.add(createCFDesc("UserAction"));
        cfDescriptors.add(createCFDesc("ActionInfo"));
    }

    public static void openDB() {
        try {
            System.out.println("Open a demo RocksDB instance...");
            db = RocksDB.open(options, dataDirPath, cfDescriptors, cfHandles);

            // store CF Handles to local Map by CF name
            loadMapCFHandles();

            System.out.println("Open RocksDB successfully!");

        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(1);
        }
    }

    private static void loadMapCFHandles() {
        mapCFHandles.put(new String(RocksDB.DEFAULT_COLUMN_FAMILY), cfHandles.get(0));
        mapCFHandles.put("UserInfo", cfHandles.get(1));
        mapCFHandles.put("UserAction", cfHandles.get(2));
        mapCFHandles.put("ActionInfo", cfHandles.get(3));
    }

    public static ColumnFamilyHandle addNewCF(String cfName) {
        ColumnFamilyDescriptor cfDesc = createCFDesc(cfName);
        try {
            ColumnFamilyHandle cfHandle = db.createColumnFamily(cfDesc);
            cfDescriptors.add(cfDesc);
            cfHandles.add(cfHandle);
            mapCFHandles.put(cfName, cfHandle);
            return cfHandle;

        } catch (RocksDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ColumnFamilyHandle removeCF(String cfName) {
        ColumnFamilyHandle cfHandle = mapCFHandles.get(cfName);
        try {
            db.dropColumnFamily(cfHandle);
            cfHandles.remove(cfHandle);
            mapCFHandles.remove(cfName);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return cfHandle;
    }

    public static ColumnFamilyHandle getCFHandle(String cfName) {
        return mapCFHandles.getOrDefault(cfName, null);
    }

    public static RocksDB getDb() {
        return db;
    }

    public static Statistics getStats() {
        return stats;
    }

    public static List<ColumnFamilyHandle> getCfHandles() {
        return cfHandles;
    }

}
