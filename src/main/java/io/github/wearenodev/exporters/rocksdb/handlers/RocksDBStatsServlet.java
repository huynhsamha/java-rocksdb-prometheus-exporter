package io.github.wearenodev.exporters.rocksdb.handlers;

import io.github.wearenodev.exporters.rocksdb.managers.RegistryManager;
import io.github.wearenodev.exporters.rocksdb.managers.RocksDBProperties;
import io.github.wearenodev.exporters.rocksdb.models.JRocksDB;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.MetricsServlet;
import org.rocksdb.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RocksDBStatsServlet extends MetricsServlet {

    private static final Logger _Logger = Logger.getLogger(RocksDBStatsServlet.class.getName());

    private static final CollectorRegistry Registry = RegistryManager.RocksDBStats;

    private static final Gauge GaugeProperties = Gauge.build().name("rocksdb_props")
            .help("RocksDB Column Family Properties")
            .labelNames("cf", "prop")
            .create().register(Registry);

    private static final Gauge GaugeStats = Gauge.build().name("rocksdb_stats")
            .help("RocksDB Statistics")
            .labelNames("ticker")
            .create().register(Registry);

    private static final Set<String> CurrCFNames = new HashSet<>();

    private final JRocksDB jrocksDB;

    public RocksDBStatsServlet(CollectorRegistry registry, JRocksDB jrocksDB) {
        super(registry);
        this.jrocksDB = jrocksDB;

        updateCurrentCFNames(this.jrocksDB);
    }

    private static void updateCurrentCFNames(JRocksDB jrocksDB) {
        _Logger.info("Current CF Names: " + CurrCFNames);
        synchronized (CurrCFNames) {
            CurrCFNames.clear();
            List<ColumnFamilyHandle> listCFHandles = jrocksDB.getListCFHandles();
            for (ColumnFamilyHandle cfHandle : listCFHandles) {
                try {
                    String cfName = new String(cfHandle.getName());
                    CurrCFNames.add(cfName);
                } catch (RocksDBException ex) {
                    _Logger.warning("Exception ex=" + ex.getMessage());
                }
            }
        }
        _Logger.info("Updated CF Names: " + CurrCFNames);
    }

    public static void onChangeListCFHandles(JRocksDB jrocksDB) {
        _Logger.info("onChangeListCFHandles");
        Set<String> LastCFNames = new HashSet<>();
        LastCFNames.addAll(CurrCFNames);

        updateCurrentCFNames(jrocksDB);

        List<String> props = RocksDBProperties.SORTED_ROCKSDB_PROPERTIES;
        for (String cfName : LastCFNames) {
            if (!CurrCFNames.contains(cfName)) { // CF is dropped
                for (String prop : props) {
                    GaugeProperties.remove(cfName, prop);
                }
            }
        }
    }

    private void updateGaugeProps() {
        RocksDB rocksDB = jrocksDB.getRocksDB();
        List<ColumnFamilyHandle> listCFHandles = jrocksDB.getListCFHandles();
        try {
            List<String> props = RocksDBProperties.SORTED_ROCKSDB_PROPERTIES;
            for (String prop : props) {
                try {
                    long totalValues = rocksDB.getAggregatedLongProperty(prop);
                    GaugeProperties.labels("All_CFs", prop).set(totalValues);
                } catch (RocksDBException ex) {
                    _Logger.warning("Exception: prop=" + prop + "; ex=" + ex.getMessage());
                }
                for (ColumnFamilyHandle cfHandle : listCFHandles) {
                    try {
                        String cfName = new String(cfHandle.getName());
                        long value = rocksDB.getLongProperty(cfHandle, prop);
                        GaugeProperties.labels(cfName, prop).set(value);

                    } catch (RocksDBException ex) {
                        _Logger.warning("Exception: prop=" + prop + "; ex=" + ex.getMessage());
                    }
                }
            }

        } catch (Exception ex) {
            _Logger.warning("Failed updateGaugeProps: ex=" + ex.getMessage());
        }
    }

    private void updateGaugeStats() {
        for (TickerType ticker : TickerType.values()) {
            updateGaugeStats(ticker);
        }
    }

    private void updateGaugeStats(TickerType ticker) {
        Statistics stats = jrocksDB.getRocksDBStats();
        try {
            long value = stats.getTickerCount(ticker);
            GaugeStats.labels(ticker.toString()).set(value);

        } catch (Exception ex) {
            _Logger.log(Level.WARNING, "Failed updateGaugeStats: ticker=" + ticker, ex);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (jrocksDB.getRocksDB() != null) {
            updateGaugeProps();
        }
        if (jrocksDB.getRocksDBStats() != null) {
            updateGaugeStats();
        }
        super.doGet(req, resp);
    }

}
