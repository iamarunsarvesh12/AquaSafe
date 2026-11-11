# AquaSafe: Big Data Architecture & Apache Spark Specification

This document details the Apache Spark and PySpark distributed data processing pipeline, partitioning topology, multi-source joining strategies, and columnar Parquet storage architecture powering **AquaSafe**.

---

## 1. Big Data Architecture & Execution Pipeline

```
[ Raw Multi-Source Data Layers ]
  - Water Quality: 2,698 raw observations
  - Weather Data: 2,698 raw observations
  - Disease Data: 2,698 raw surveillance records
  - District Master: 38 demographic records
                    |
                    v
[ Spark Ingestion & Standardization ]
  - Distributed DataFrame reader with explicit schema
  - District name normalization (e.g. "Tiruchirappalli", "Kanchipuram")
  - ISO-8601 Date Parsing
                    |
                    v
[ Data Validation & Cleaning (Spark SQL) ]
  - Missing value detection & statistical imputation
  - Range validation (pH 0-14, Turbidity >= 0, Temp -10 to 60)
  - Duplicate record deduplication on [district, observation_date]
                    |
                    v
[ Distributed Multi-Source Joining ]
  - Broadcast Hash Join with districts_master DataFrame
  - Partition-wise Inner Joins between water, weather, and disease
                    |
                    v
[ Spark Feature Engineering ]
  - Weighted Arithmetic WQI computation
  - 7-day and 30-day lag & moving averages using Spark Window Functions
  - Standardized disease incidence rates per 100k
                    |
                    v
[ Parquet Columnar Storage Layer ]
  - Partitioned by: [district_name, year] (10 physical partitions)
  - Compression: Snappy binary codec
  - Destination: data/processed/parquet/tamilnadu_epidemiological_analytics.parquet
```

---

## 2. Apache Spark Configuration & Deployment Modes

### Local Mode (Development & Desktop Testing):
- **Master URL:** `local[*]` (utilizes all available CPU cores)
- **Driver Memory:** 2 GB
- **Executor Memory:** 2 GB
- **Shuffle Partitions:** 10
- **Storage Fallback:** In environments where Windows Hadoop binaries (`winutils.exe`) are absent, the pipeline transparently falls back to the native `pyarrow.parquet` dataset writer with multi-key partition trees, ensuring zero setup friction.

### Distributed Cluster Mode (Production & Cloud Scaling):
- **Master URL:** `spark://master:7077` or Kubernetes `k8s://https://k8s-apiserver:6443`
- **Dynamic Allocation:** Enabled (`spark.dynamicAllocation.enabled=true`)
- **Min / Max Executors:** 2 min / 20 max
- **Executor Cores:** 4 cores per executor
- **Executor Memory:** 8 GB per executor

---

## 3. Distributed Join Strategy

Because the multi-source data encompasses four distinct domains, naive cross-joining would cause high memory pressure. The pipeline executes:

1. **Broadcast Join on District Master:**
   - The 38-district master table is small (< 1 MB). Spark broadcasts it to all executor worker nodes using `broadcast(districts_df)`, eliminating shuffle operations across the cluster network.
2. **Co-Partitioned Equi-Join on `(district_name, observation_date)`:**
   - Water quality, weather, and disease records are repartitioned by `district_name` prior to joining, allowing executors to perform localized hash joins entirely in memory.

---

## 4. Feature Engineering with Spark Window Functions

Temporal moving averages and lags are calculated without pulling data into driver memory:

```python
from pyspark.sql.window import Window
import pyspark.sql.functions as F

# 7-Day Moving Window by District ordered by Observation Date
window_7d = (Window.partitionBy("district_name")
             .orderBy("observation_date")
             .rowsBetween(-6, 0))

df = df.withColumn("rainfall_7d_sma", F.avg("rainfall").over(window_7d))
df = df.withColumn("turbidity_7d_sma", F.avg("turbidity").over(window_7d))
```

---

## 5. Columnar Parquet Storage & Partitioning Strategy

The processed analytics dataset is stored at:
`data/processed/parquet/tamilnadu_epidemiological_analytics.parquet/`

### Directory Partition Tree:
```
tamilnadu_epidemiological_analytics.parquet/
├── district_name=Ariyalur/
│   ├── year=2021/part-0000.snappy.parquet
│   ├── year=2022/part-0000.snappy.parquet
│   └── ...
├── district_name=Chennai/
│   ├── year=2021/part-0000.snappy.parquet
│   └── ...
└── ... (All 38 districts)
```

### Storage Efficiency Comparison:
| Metric | Raw Multi-Source CSVs | Processed Parquet Storage | Improvement Factor |
|---|---|---|---|
| Total File Size | ~16.2 MB | **3.35 MB** | **4.8x Compression** |
| Disk I/O per District Query | Read entire 16 MB | Read single partition (~85 KB) | **188x Faster Query I/O** |
| Serialization Format | Uncompressed UTF-8 Text | Snappy Compressed Columnar | Zero schema parsing overhead |

---

## 6. Official Spark Execution Report

Execution metrics recorded in `data/processed/spark_execution_report.json` and logged in database table `spark_job_history`:

- **Job Name:** `PySpark_TamilNadu_ETL_Analytics`
- **Total Multi-Source Input Records:** **8,094 records** (2,698 Water + 2,698 Weather + 2,698 Disease)
- **Successfully Cleaned & Joined Records:** **2,698 records**
- **Rejected / Invalid Outlier Records:** **0 records**
- **Partitions Generated:** **10 physical partitions**
- **Total Pipeline Execution Latency:** **6,840 milliseconds** (6.84 seconds)
- **Spark Execution Status:** **SUCCESS**
- **Partition Schema:** `[district_name, year]`
- **Compression Codec:** `SNAPPY`
- **Output Parquet Size:** **3,355,869 bytes** (~3.35 MB)

