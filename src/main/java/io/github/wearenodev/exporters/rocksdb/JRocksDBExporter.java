package io.github.wearenodev.exporters.rocksdb;

import io.github.wearenodev.exporters.rocksdb.handlers.RocksDBStatsServlet;
import io.github.wearenodev.exporters.rocksdb.managers.RegistryManager;
import io.github.wearenodev.exporters.rocksdb.models.JRocksDB;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JRocksDBExporter {

    private static final Logger _Logger = Logger.getLogger(JRocksDBExporter.class.getName());

    private final int port;
    private final JRocksDB jrocksDB;
    private Server server;
    private boolean started = false;

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

    public int getPort() {
        return port;
    }

    public JRocksDB getJrocksDB() {
        return jrocksDB;
    }

    public boolean isStarted() {
        return started;
    }
}
