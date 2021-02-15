package io.github.huynhsamha.exporters.rocksdb.managers;

import io.prometheus.client.CollectorRegistry;

public class RegistryManager {

    public static final CollectorRegistry Default = CollectorRegistry.defaultRegistry;
    public static final CollectorRegistry RocksDBStats = new CollectorRegistry(true);

}
