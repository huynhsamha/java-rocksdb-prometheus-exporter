package io.github.wearenodev.exporters.rocksdb;

import io.github.wearenodev.exporters.rocksdb.models.JRocksDB;
import org.rocksdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestExporter {

    static int port = 9098;
    static String logDirPath = "/tmp/rocksdb-demo/log";
    static String dataDirPath = "/tmp/rocksdb-demo/data";

    static {
        RocksDB.loadLibrary();

        ensureDir(logDirPath);
        ensureDir(dataDirPath);
    }

    public static void ensureDir(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static ColumnFamilyDescriptor createCFDesc(String cfName) {
        ColumnFamilyOptions op = new ColumnFamilyOptions();
        // TODO: set your CF options
        return new ColumnFamilyDescriptor(cfName.getBytes(), op);
    }

    public static void main(String[] args) {

        Statistics stats = new Statistics();
        DBOptions options = new DBOptions()
                .setCreateIfMissing(true)
                .setCreateMissingColumnFamilies(true)
                .setDbLogDir(logDirPath)
                .setStatistics(stats);

        // TODO: set your DB options

        List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
        List<ColumnFamilyHandle> cfHandles = new ArrayList<>(); // will be filled by rocksdb when open

        // Default CF is required in RocksDB
        cfDescriptors.add(createCFDesc(new String(RocksDB.DEFAULT_COLUMN_FAMILY)));

        // TODO: set up your CFs
        cfDescriptors.add(createCFDesc("UserInfo"));
        cfDescriptors.add(createCFDesc("UserAction"));
        cfDescriptors.add(createCFDesc("ActionInfo"));

        try {
            System.out.println("Open a demo RocksDB instance...");
            RocksDB db = RocksDB.open(options, dataDirPath, cfDescriptors, cfHandles);

            System.out.println("Set up and start RocksDB exporter...");
            JRocksDB jRocksDB = new JRocksDB(db, stats, cfHandles);
            JRocksDBExporter exporter = new JRocksDBExporter(port, jRocksDB);

            exporter.start();

            System.out.println("Started!");

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

}
