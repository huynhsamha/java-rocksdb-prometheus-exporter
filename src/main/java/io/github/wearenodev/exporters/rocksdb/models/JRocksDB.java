package io.github.wearenodev.exporters.rocksdb.models;

import io.github.wearenodev.exporters.rocksdb.JRocksDBExporter;
import io.github.wearenodev.exporters.rocksdb.handlers.RocksDBStatsServlet;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.Statistics;

import java.util.List;
import java.util.logging.Logger;

public class JRocksDB {

    private static final Logger _Logger = Logger.getLogger(JRocksDB.class.getName());

    private final RocksDB rocksDB;
    private final Statistics rocksDBStats;
    private final List<ColumnFamilyHandle> listCFHandles;

    public JRocksDB(RocksDB rocksDB, Statistics rocksDBStats, List<ColumnFamilyHandle> listCFHandles) {
        this.rocksDB = rocksDB;
        this.rocksDBStats = rocksDBStats;
        this.listCFHandles = listCFHandles;
    }

    public RocksDB getRocksDB() {
        return rocksDB;
    }

    public Statistics getRocksDBStats() {
        return rocksDBStats;
    }

    public List<ColumnFamilyHandle> getListCFHandles() {
        return listCFHandles;
    }

    public void setListCFHandles(List<ColumnFamilyHandle> newListCFHandles) {
        _Logger.info("setListCFHandles: newListCFHandles=" + newListCFHandles);
        if (newListCFHandles == null) {
            _Logger.warning("setListCFHandles: New list CF Handles is null!");
            return;
        }
        synchronized (listCFHandles) {
            listCFHandles.clear();
            listCFHandles.addAll(newListCFHandles);
            RocksDBStatsServlet.onChangeListCFHandles(this);
        }
    }

    public void addNewCFHandles(ColumnFamilyHandle newCFHandle) {
        _Logger.info("addNewCFHandles: newCFHandle=" + newCFHandle);
        if (newCFHandle == null) {
            _Logger.warning("addNewCFHandles: New CF Handle is null!");
            return;
        }
        synchronized (listCFHandles) {
            listCFHandles.add(newCFHandle);
        }
    }

    public void removeCFHandles(ColumnFamilyHandle cfHandle) {
        _Logger.info("removeCFHandles: cfHandle=" + cfHandle);
        if (cfHandle == null) {
            _Logger.warning("removeCFHandles: CF Handle is null!");
            return;
        }
        synchronized (listCFHandles) {
            listCFHandles.remove(cfHandle);
            RocksDBStatsServlet.onChangeListCFHandles(this);
        }
    }

}
