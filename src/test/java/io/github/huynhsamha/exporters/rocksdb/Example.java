package io.github.huynhsamha.exporters.rocksdb;

import io.github.huynhsamha.exporters.rocksdb.models.JRocksDB;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.Statistics;

import java.util.List;

public class Example {

    public static void main(String[] args) {

        // HTTP server port
        int port = 9098;

        // RocksDB instance from your application
        RocksDB db = null; // db instance
        Statistics stats = null; // stats your RocksDB instance
        List<ColumnFamilyHandle> cfHandles = null; // stats your Column Families

        try {
            // wrap RocksDB instance to jRocksDB
            JRocksDB jRocksDB = new JRocksDB(db, stats, cfHandles);

            // init an exporter instance for your RocksDB
            JRocksDBExporter exporter = new JRocksDBExporter(port, jRocksDB);

            // start HTTP server on port for exposing RocksDB metrics
            exporter.start();

            System.out.println("Server is running on port " + port);
            System.out.println(">>> RocksDB metrics is exposed at http://localhost:" + port + "/rocksdb_stats");

        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}
