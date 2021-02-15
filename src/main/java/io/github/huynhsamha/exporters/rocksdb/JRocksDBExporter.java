package io.github.huynhsamha.exporters.rocksdb;

import io.github.huynhsamha.exporters.rocksdb.handlers.RocksDBStatsServlet;
import io.github.huynhsamha.exporters.rocksdb.managers.RegistryManager;
import io.github.huynhsamha.exporters.rocksdb.models.JRocksDB;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP Server export RocksDB metrics for Prometheus.
 */
public class JRocksDBExporter {

    private static final Logger _Logger = Logger.getLogger(JRocksDBExporter.class.getName());

    /**
     * HTTP port for expose metrics
     */
    private final int port;

    /**
     * RocksDB from application, including RocksDB, Statistics and Column Families
     */
    private final JRocksDB jrocksDB;

    /**
     * HTTP Server
     */
    private Server server;

    /**
     * Flag that marks the server is running
     */
    private boolean started = false;

    /**
     * Init exporter information, including HTTP port and RocksDB instance
     * @param port HTTP port for expose metrics
     * @param jrocksDB RocksDB from application, including RocksDB, Statistics and Column Families
     */
    public JRocksDBExporter(int port, JRocksDB jrocksDB) {
        this.port = port;
        this.jrocksDB = jrocksDB;
    }

    private void setupServer() {
        server = new Server(port);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MetricsServlet(RegistryManager.Default)), "/metrics");
        context.addServlet(new ServletHolder(new RocksDBStatsServlet(RegistryManager.RocksDBStats, jrocksDB)), "/rocksdb_stats");
    }

    /**
     * Start HTTP Server
     */
    public void start() {
        if (server == null) setupServer();
        try {
            server.start();
            started = true;
            _Logger.info("RocksDB exporter is running on port " + port);

        } catch (Exception ex) {
            _Logger.log(Level.WARNING, "Failed to start RocksDB exporter on port " + port, ex);
        }
    }

    /**
     * Stop HTTP Server
     */
    public void stop() {
        if (!started) {
            return;
        }
        try {
            server.stop();
            started = false;
            _Logger.warning("RocksDB exporter has been stopped");

        } catch (Exception ex) {
            _Logger.log(Level.WARNING, "Failed to stop RocksDB exporter", ex);
        }
    }

    /**
     * @return Port that exporter uses
     */
    public int getPort() {
        return port;
    }

    /**
     * @return RocksDB information, from application
     */
    public JRocksDB getJRocksDB() {
        return jrocksDB;
    }

    /**
     * @return Server is running or not
     */
    public boolean isStarted() {
        return started;
    }
}
