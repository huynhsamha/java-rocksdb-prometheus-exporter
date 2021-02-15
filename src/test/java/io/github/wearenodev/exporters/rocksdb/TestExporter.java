package io.github.wearenodev.exporters.rocksdb;

import io.github.wearenodev.exporters.rocksdb.models.JRocksDB;
import io.github.wearenodev.exporters.rocksdb.shared.DemoRocksDB;
import io.github.wearenodev.exporters.rocksdb.shared.MockDemoRocksDB;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.Statistics;

import java.util.List;

public class TestExporter {

    static int port = 9098;

    public static void main(String[] args) {

        try {
            System.out.println("Open a demo RocksDB instance...");
            DemoRocksDB.openDB();

            // get rocksdb info for exporter
            RocksDB db = DemoRocksDB.getDb();
            Statistics stats = DemoRocksDB.getStats();
            List<ColumnFamilyHandle> cfHandles = DemoRocksDB.getCfHandles();

            System.out.println("Set up and start RocksDB exporter...");
            JRocksDB jRocksDB = new JRocksDB(db, stats, cfHandles);
            JRocksDBExporter exporter = new JRocksDBExporter(port, jRocksDB);

            exporter.start();

            System.out.println("Started!");

            // update + query DB in background
            MockDemoRocksDB.doBackgroundJob();

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

}
