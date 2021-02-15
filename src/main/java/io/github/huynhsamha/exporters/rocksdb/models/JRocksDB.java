package io.github.huynhsamha.exporters.rocksdb.models;

import io.github.huynhsamha.exporters.rocksdb.handlers.RocksDBStatsServlet;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.Statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * RocksDB information from application, including RocksDB, Statistics and Column Families
 */
public class JRocksDB {

    private static final Logger _Logger = Logger.getLogger(JRocksDB.class.getName());

    /**
     * RocksDB instance from Application
     */
    private final RocksDB rocksDB;

    /**
     * RocksDB statistics instance from Application
     */
    private final Statistics rocksDBStats;

    /**
     * RocksDB Column Families from Application
     */
    private final List<ColumnFamilyHandle> listCFHandles;

    /**
     * Wrap RocksDB instance from Application
     * @param rocksDB RocksDB instance from Application
     * @param rocksDBStats RocksDB statistics instance from Application
     * @param listCFHandles RocksDB Column Families from Application
     */
    public JRocksDB(RocksDB rocksDB, Statistics rocksDBStats, List<ColumnFamilyHandle> listCFHandles) {
        this.rocksDB = rocksDB;
        this.rocksDBStats = rocksDBStats;
        this.listCFHandles = listCFHandles;
    }

    /**
     * @return RocksDB instance from Application
     */
    public RocksDB getRocksDB() {
        return rocksDB;
    }

    /**
     * @return RocksDB statistics instance from Application
     */
    public Statistics getRocksDBStats() {
        return rocksDBStats;
    }

    /**
     * @return RocksDB Column Families from Application
     */
    public List<ColumnFamilyHandle> getListCFHandles() {
        return listCFHandles;
    }

    /**
     * Notify to other components when Column Families from application changes, such as create a new CF, or drop a CF.
     */
    private synchronized void onChangeListCFHandles() {
        RocksDBStatsServlet.onChangeListCFHandles(this);
    }

    /**
     * Update list of Column Families when the application needs to update
     * @param newListCFHandles new list of Column Families
     */
    public void setListCFHandles(List<ColumnFamilyHandle> newListCFHandles) {
        _Logger.info("setListCFHandles: newListCFHandles=" + newListCFHandles);
        if (newListCFHandles == null) {
            _Logger.warning("setListCFHandles: New list CF Handles is null!");
            return;
        }
        List<ColumnFamilyHandle> cloneNewListCFHandles = new ArrayList<>(newListCFHandles);
        synchronized (listCFHandles) {
            listCFHandles.clear();
            listCFHandles.addAll(cloneNewListCFHandles);
        }
        onChangeListCFHandles();
    }

    /**
     * Add a new Column Family from application
     * @param newCFHandle a new Column Family that is created from application
     */
    public void addNewCFHandles(ColumnFamilyHandle newCFHandle) {
        _Logger.info("addNewCFHandles: newCFHandle=" + newCFHandle);
        if (newCFHandle == null) {
            _Logger.warning("addNewCFHandles: New CF Handle is null!");
            return;
        }
        synchronized (listCFHandles) {
            listCFHandles.add(newCFHandle);
        }
        onChangeListCFHandles();
    }

    /**
     * Remove a dropped Column Family from application
     * @param cfHandle the Column Family that application has dropped it
     */
    public void removeCFHandles(ColumnFamilyHandle cfHandle) {
        _Logger.info("removeCFHandles: cfHandle=" + cfHandle);
        if (cfHandle == null) {
            _Logger.warning("removeCFHandles: CF Handle is null!");
            return;
        }
        synchronized (listCFHandles) {
            listCFHandles.remove(cfHandle);
        }
        onChangeListCFHandles();
    }

}
