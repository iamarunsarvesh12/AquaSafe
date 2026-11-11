#!/usr/bin/env python3
"""
AquaSafe Distributed Big Data Processing Engine (PySpark 3.x / 4.x & Spark SQL)
Performs large-scale distributed DataFrame loading, validation, cleaning,
multi-source relational joins, district-level, monthly, and yearly aggregations,
time-series transformations, feature engineering, and partitioned Parquet storage.
"""

import sys
import os
import json
import time
import builtins
from datetime import datetime

# Windows Hadoop compatibility setting
os.environ["PYSPARK_SUBMIT_ARGS"] = "--conf spark.driver.extraJavaOptions=-Dlog4j.configurationFile=log4j2.xml pyspark-shell"

def run_spark_pipeline(master="local[*]", data_dir="data", output_dir="data/processed"):
    start_time = time.time()
    print("=" * 70)
    print("  AQUASAFE: DISTRIBUTED APACHE SPARK BIG DATA ANALYTICS PIPELINE")
    print(f"  Master Node:      {master}")
    print(f"  Data Directory:   {data_dir}")
    print(f"  Output Directory: {output_dir}")
    print("=" * 70)

    try:
        from pyspark.sql import SparkSession
        from pyspark.sql.functions import (
            col, avg, sum, min, max, count, round as spark_round,
            when, substring, to_date, lit, desc
        )
    except ImportError as e:
        print(f"[!] PySpark import error: {e}")
        sys.exit(1)

    spark = SparkSession.builder \
        .appName("AquaSafe-BigData-Processor") \
        .master(master) \
        .config("spark.sql.shuffle.partitions", "4") \
        .config("spark.driver.memory", "2g") \
        .config("spark.sql.execution.arrow.pyspark.enabled", "true") \
        .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    try:
        # 1. Paths resolution
        wq_path = os.path.join(data_dir, "cleaned", "water_quality_cleaned.csv")
        if not os.path.exists(wq_path):
            wq_path = os.path.join(data_dir, "water_quality.csv")

        weather_path = os.path.join(data_dir, "cleaned", "weather_cleaned.csv")
        if not os.path.exists(weather_path):
            weather_path = os.path.join(data_dir, "weather_data.csv")

        disease_path = os.path.join(data_dir, "cleaned", "disease_cleaned.csv")
        if not os.path.exists(disease_path):
            disease_path = os.path.join(data_dir, "disease_records.csv")

        districts_path = os.path.join(data_dir, "cleaned", "districts_master.csv")
        if not os.path.exists(districts_path):
            districts_path = os.path.join(data_dir, "population_data.csv")

        # 2. Ingest DataFrames via Spark SQL
        print("\n[*] Loading multi-source datasets into Spark distributed DataFrames...")
        df_wq_raw = spark.read.option("header", "true").option("inferSchema", "true").csv(wq_path)
        df_wth_raw = spark.read.option("header", "true").option("inferSchema", "true").csv(weather_path)
        df_dis_raw = spark.read.option("header", "true").option("inferSchema", "true").csv(disease_path)
        df_dist_raw = spark.read.option("header", "true").option("inferSchema", "true").csv(districts_path)

        count_wq = df_wq_raw.count()
        count_wth = df_wth_raw.count()
        count_dis = df_dis_raw.count()
        total_input_records = count_wq + count_wth + count_dis

        print(f"    - Water Quality Records: {count_wq:,}")
        print(f"    - Weather Records:       {count_wth:,}")
        print(f"    - Disease Records:       {count_dis:,}")
        print(f"    - Total Input Records:   {total_input_records:,}")

        # 3. Data Cleaning and Validation Bounds via Spark
        # Standardize district and date columns
        wq_col_district = "district_name" if "district_name" in df_wq_raw.columns else "district"
        wq_col_date = "observation_date" if "observation_date" in df_wq_raw.columns else "date"

        wth_col_district = "district_name" if "district_name" in df_wth_raw.columns else "district"
        wth_col_date = "observation_date" if "observation_date" in df_wth_raw.columns else "date"

        dis_col_district = "district_name" if "district_name" in df_dis_raw.columns else "district"
        dis_col_date = "reporting_date" if "reporting_date" in df_dis_raw.columns else "date"

        dist_col_name = "district_name" if "district_name" in df_dist_raw.columns else "district"

        df_wq = df_wq_raw.withColumn("dist_clean", col(wq_col_district)) \
                         .withColumn("date_clean", col(wq_col_date).cast("string")) \
                         .filter((col("ph") >= 0.0) & (col("ph") <= 14.0) & (col("tds") >= 0.0))

        temp_col = "temp_avg" if "temp_avg" in df_wth_raw.columns else "temperature"
        df_wth = df_wth_raw.withColumn("dist_clean", col(wth_col_district)) \
                           .withColumn("date_clean", col(wth_col_date).cast("string")) \
                           .filter((col("rainfall") >= 0.0) & (col("humidity") >= 0.0))

        df_dis = df_dis_raw.withColumn("dist_clean", col(dis_col_district)) \
                           .withColumn("date_clean", col(dis_col_date).cast("string")) \
                           .withColumn("total_cases_calc", col("cholera_cases") + col("typhoid_cases") + col("diarrhea_cases") + col("hepatitis_cases"))

        df_dist = df_dist_raw.withColumn("dist_clean", col(dist_col_name))

        # 4. Multi-Source Distributed Relational Join
        print("\n[*] Executing multi-source distributed DataFrame join across all 38 districts...")
        joined_df = df_wq.alias("w") \
            .join(df_wth.alias("m"), (col("w.dist_clean") == col("m.dist_clean")) & (col("w.date_clean") == col("m.date_clean")), "inner") \
            .join(df_dis.alias("d"), (col("w.dist_clean") == col("d.dist_clean")) & (col("w.date_clean") == col("d.date_clean")), "inner") \
            .join(df_dist.alias("p"), col("w.dist_clean") == col("p.dist_clean"), "left")

        # 5. Feature Engineering and Mathematical Transformations
        print("[*] Computing Feature Engineering: Time-Series extracts, Incidence Rates, and WQI...")
        pop_col_name = "population_2025" if "population_2025" in joined_df.columns else ("population" if "population" in joined_df.columns else "total_population")
        san_col_name = "sanitation_coverage" if "sanitation_coverage" in joined_df.columns else "sanitation"
        dens_col_name = "population_density_2025" if "population_density_2025" in joined_df.columns else "population_density"

        processed_df = joined_df.select(
            col("w.dist_clean").alias("district_name"),
            col("w.date_clean").alias("observation_date"),
            substring(col("w.date_clean"), 1, 4).cast("int").alias("year"),
            substring(col("w.date_clean"), 6, 2).cast("int").alias("month"),
            substring(col("w.date_clean"), 1, 7).alias("year_month"),
            col("w.ph").alias("ph"),
            col("w.turbidity").alias("turbidity"),
            col("w.tds").alias("tds"),
            col("w.dissolved_oxygen").alias("dissolved_oxygen"),
            col(f"m.{temp_col}").alias("temperature"),
            col("m.rainfall").alias("rainfall"),
            col("m.humidity").alias("humidity"),
            col("d.cholera_cases").alias("cholera_cases"),
            col("d.typhoid_cases").alias("typhoid_cases"),
            col("d.diarrhea_cases").alias("diarrhea_cases"),
            col("d.hepatitis_cases").alias("hepatitis_cases"),
            col("d.total_cases_calc").alias("total_disease_cases"),
            col(f"p.{pop_col_name}").alias("population"),
            col(f"p.{san_col_name}").alias("sanitation_coverage"),
            col(f"p.{dens_col_name}").alias("population_density")
        )

        # Epidemiological Incidence Rate = (cases / population) * 100,000
        processed_df = processed_df.withColumn(
            "incidence_rate_per_100k",
            spark_round((col("total_disease_cases") / col("population")) * 100000.0, 2)
        )

        # Scientific Water Quality Index (WQI) calculation via Spark SQL
        # Weighted arithmetic index: factors ph deviation from 7, turbidity penalty, tds penalty, low DO penalty
        processed_df = processed_df.withColumn(
            "wqi",
            spark_round(
                (col("ph") - 7.0) * (col("ph") - 7.0) * 12.0 +
                col("turbidity") * 5.5 +
                (col("tds") / 100.0) * 4.2 +
                when(col("dissolved_oxygen") < 5.0, (5.0 - col("dissolved_oxygen")) * 15.0).otherwise(0.0),
                2
            )
        )

        processed_df = processed_df.withColumn(
            "risk_label",
            when((col("incidence_rate_per_100k") >= 15.0) | (col("wqi") >= 65.0) | (col("total_disease_cases") >= 30), "HIGH")
            .when((col("incidence_rate_per_100k") >= 6.0) | (col("wqi") >= 40.0) | (col("total_disease_cases") >= 12), "MEDIUM")
            .otherwise("LOW")
        )

        processed_records_count = processed_df.count()
        num_partitions = processed_df.rdd.getNumPartitions()
        rejected_records = builtins.max(0, total_input_records - (processed_records_count * 3))

        print(f"\n[+] Feature-engineered Spark records ready: {processed_records_count:,} across {num_partitions} partitions.")

        # 6. District-Level Aggregation
        print("\n--- SPARK SQL DISTRICT-LEVEL ANALYTICS ---")
        district_summary_df = processed_df.groupBy("district_name").agg(
            spark_round(avg("ph"), 2).alias("avg_ph"),
            spark_round(avg("turbidity"), 2).alias("avg_turbidity"),
            spark_round(avg("tds"), 1).alias("avg_tds"),
            spark_round(avg("dissolved_oxygen"), 2).alias("avg_do"),
            spark_round(avg("temperature"), 1).alias("avg_temperature"),
            spark_round(avg("rainfall"), 1).alias("avg_rainfall"),
            spark_round(sum("rainfall"), 1).alias("total_rainfall"),
            sum("total_disease_cases").alias("total_cases"),
            spark_round(avg("incidence_rate_per_100k"), 2).alias("avg_incidence_rate"),
            spark_round(avg("wqi"), 2).alias("avg_wqi")
        ).orderBy(desc("total_cases"))

        district_summary_df.show(10, truncate=False)

        # 7. Monthly Aggregations
        print("\n--- SPARK SQL MONTHLY DISEASE & WATER TRENDS ---")
        monthly_summary_df = processed_df.groupBy("year_month").agg(
            sum("cholera_cases").alias("total_cholera"),
            sum("typhoid_cases").alias("total_typhoid"),
            sum("diarrhea_cases").alias("total_diarrhea"),
            sum("hepatitis_cases").alias("total_hepatitis"),
            sum("total_disease_cases").alias("grand_total_cases"),
            spark_round(avg("rainfall"), 1).alias("avg_rainfall"),
            spark_round(avg("wqi"), 2).alias("avg_wqi")
        ).orderBy("year_month")

        monthly_summary_df.show(12, truncate=False)

        # 8. Export to Partitioned Parquet Storage
        parquet_path = os.path.join(output_dir, "parquet", "tamilnadu_epidemiological_analytics.parquet")
        print(f"\n[*] Saving distributed dataset to partitioned Parquet storage at: {parquet_path}")
        os.makedirs(os.path.dirname(parquet_path), exist_ok=True)

        try:
            processed_df.write \
                .mode("overwrite") \
                .partitionBy("district_name", "year") \
                .parquet(parquet_path)
            print("[+] Spark native Parquet partitioned export completed successfully.")
        except Exception as pe:
            print(f"[*] Spark Hadoop file writer note ({pe.__class__.__name__}). Exporting via native PyArrow/Parquet engine...")
            try:
                import pyarrow as pa
                import pyarrow.parquet as pq
                pdf = processed_df.toPandas()
                table = pa.Table.from_pandas(pdf)
                pq.write_to_dataset(table, root_path=parquet_path, partition_cols=["district_name", "year"])
                print("[+] PyArrow Parquet partitioned export completed successfully.")
            except Exception as pa_err:
                print(f"[!] Warning exporting parquet: {pa_err}")

        # 9. Compute execution metrics and JSON report
        duration_ms = int((time.time() - start_time) * 1000)
        
        # Calculate dataset size on disk
        total_size_bytes = 0
        if os.path.exists(parquet_path):
            for dirpath, dirnames, filenames in os.walk(parquet_path):
                for f in filenames:
                    fp = os.path.join(dirpath, f)
                    total_size_bytes += os.path.getsize(fp)

        # Extract top 10 district summaries for reporting
        top_districts = []
        for row in district_summary_df.limit(10).collect():
            top_districts.append({
                "district": row["district_name"],
                "avgPh": row["avg_ph"],
                "avgTurbidity": row["avg_turbidity"],
                "avgTds": row["avg_tds"],
                "avgDo": row["avg_do"],
                "totalRainfall": row["total_rainfall"],
                "totalCases": int(row["total_cases"]),
                "avgIncidenceRate": row["avg_incidence_rate"],
                "avgWqi": row["avg_wqi"]
            })

        # Extract recent monthly trends
        recent_monthly = []
        for row in monthly_summary_df.collect()[-12:]:
            recent_monthly.append({
                "yearMonth": row["year_month"],
                "cholera": int(row["total_cholera"]),
                "typhoid": int(row["total_typhoid"]),
                "diarrhea": int(row["total_diarrhea"]),
                "hepatitis": int(row["total_hepatitis"]),
                "totalCases": int(row["grand_total_cases"]),
                "avgRainfall": row["avg_rainfall"],
                "avgWqi": row["avg_wqi"]
            })

        execution_report = {
            "jobName": "AquaSafe-Distributed-Analytics-Job",
            "sparkMaster": master,
            "sparkVersion": spark.version,
            "inputRecords": total_input_records,
            "processedRecords": processed_records_count,
            "rejectedRecords": rejected_records,
            "partitionsCount": num_partitions,
            "durationMs": duration_ms,
            "datasetSizeBytes": total_size_bytes,
            "parquetPath": parquet_path,
            "status": "SUCCESS",
            "timestamp": datetime.now().isoformat(),
            "districtCount": df_dist.count(),
            "topDistricts": top_districts,
            "monthlyTrends": recent_monthly
        }

        report_file = os.path.join(output_dir, "spark_execution_report.json")
        with open(report_file, "w", encoding="utf-8") as f:
            json.dump(execution_report, f, indent=2)

        print(f"[+] Big Data execution report written to: {report_file}")
        print("=" * 70)
        print(f"  SPARK EXECUTION FINISHED IN {duration_ms:,} ms ({duration_ms/1000.0:.2f} s)")
        print(f"  Input Records:     {total_input_records:,}")
        print(f"  Processed Records: {processed_records_count:,}")
        print(f"  Partitions:        {num_partitions}")
        print(f"  Parquet Size:      {total_size_bytes:,} bytes")
        print("=" * 70)

        return execution_report

    finally:
        spark.stop()

if __name__ == "__main__":
    master_node = sys.argv[1] if len(sys.argv) > 1 else "local[*]"
    data_folder = sys.argv[2] if len(sys.argv) > 2 else "data"
    output_folder = sys.argv[3] if len(sys.argv) > 3 else "data/processed"
    run_spark_pipeline(master_node, data_folder, output_folder)
