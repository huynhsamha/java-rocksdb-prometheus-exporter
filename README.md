# Java RocksDB Prometheus Exporter

Prometheus Exporter for [RocksJava](https://github.com/facebook/rocksdb/wiki/RocksJava-Basics). Monitor Java RocksDB by using Prometheus and Grafana.

## Quick start

#### Maven

In development

#### Gradle

In development

#### Jar Dependencies

In development

## Usage

### Exposing metrics from Java

This is a simple example how we can expose RocksDB metrics. Your RocksDB metrics will be exposed at http://localhost:9098/rocksdb_stats.

```java
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
```

### RocksDB Metrics

There are 2 metric types exported from http://localhost:9098/rocksdb_stats

+ `rocksdb_props`: stats for Column Families (CF), including `default`, `All_CFs` and your custom CFs.
+ `rocksdb_stats`: stats for RocksDB from `TickerType` of RocksDB Statistics.

#### rocksdb_props

+ Format: `rocksdb_props{cf = "[CF_NAME]", prop = "[CF_PROPERTIY]"}`
+ `cf`: Column Family name, including `default`, `All_CFs` and your custom CFs.
+ `prop`: Column Family property, from [rocksdb/include/rocksdb/db.h](https://github.com/facebook/rocksdb/blob/v6.15.5/include/rocksdb/db.h#L950-L985)

#### rocksdb_stats

+ Format: `rocksdb_stats{ticker = "[TICKER_TYPE_NAME]"}`
+ `ticker`: `TickerType` of RocksDB Statistics, [Java Doc](https://javadoc.io/static/org.rocksdb/rocksdbjni/6.4.6/org/rocksdb/TickerType.html)


## Prometheus Configure

Add a job *rocksdb_stats* to your **prometheus.yml**

```yaml
scrape_configs:
  - job_name: rocksdb_stats
    scrape_interval: 5s
    scrape_timeout:  5s
    metrics_path: "/rocksdb_stats"
    static_configs:
    - targets: # list your RocksDB instances
      - localhost:9098
```


## Grafana Dashboard

In development

## Java Doc

In development

