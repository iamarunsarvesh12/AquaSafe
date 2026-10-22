#!/usr/bin/env python3
"""
AquaSafe Big Data Analytics: Apache Spark & Hadoop HDFS Integration Script
Performs distributed filtering, grouping, aggregation, and statistical summarization
across water quality, meteorological, and disease surveillance datasets.
"""

import sys
import os

try:
    from pyspark.sql import SparkSession
    from pyspark.sql.functions import col, avg, sum, max, min, substring
except ImportError:
    print("PySpark is not installed or not in PYTHONPATH. Please install pyspark to execute this job.")
    sys.exit(1)

def run_spark_job(master="local[*]", data_dir="data"):
    print("=" * 60)
    print("AquaSafe Apache Spark Big Data Analytics Pipeline")
    print(f"Spark Master: {master}")
    print(f"Data Source Directory: {data_dir}")
    print("=" * 60)

    spark = SparkSession.builder \
        .appName("AquaSafe-BigData-Processor") \
        .master(master) \
        .config("spark.sql.shuffle.partitions", "2") \
        .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    try:
        # 1. Load Data
        wq_path = os.path.join(data_dir, "water_quality.csv")
        weather_path = os.path.join(data_dir, "weather_data.csv")
        disease_path = os.path.join(data_dir, "disease_records.csv")

        df_wq = spark.read.option("header", "true").option("inferSchema", "true").csv(wq_path)
        df_weather = spark.read.option("header", "true").option("inferSchema", "true").csv(weather_path)
        df_disease = spark.read.option("header", "true").option("inferSchema", "true").csv(disease_path)

        print(f"[*] Water Quality records processed: {df_wq.count()}")
        print(f"[*] Weather records processed:       {df_weather.count()}")
        print(f"[*] Disease records processed:       {df_disease.count()}")

        # 2. Water Quality Grouping & Aggregation
        print("\n--- Spark District Water Quality Averages ---")
        wq_agg = df_wq.groupBy("district").agg(
            avg("ph").alias("avg_ph"),
            avg("turbidity").alias("avg_turbidity"),
            avg("tds").alias("avg_tds"),
            avg("dissolved_oxygen").alias("avg_do")
        ).orderBy("district")
        wq_agg.show(10, truncate=False)

        # 3. Weather Aggregations
        print("\n--- Spark District Weather Averages ---")
        weather_agg = df_weather.groupBy("district").agg(
            avg("temperature").alias("avg_temp"),
            avg("rainfall").alias("avg_rainfall"),
            avg("humidity").alias("avg_humidity"),
            max("rainfall").alias("max_rainfall")
        ).orderBy("district")
        weather_agg.show(10, truncate=False)

        # 4. Monthly Disease Aggregations
        print("\n--- Spark Monthly Disease Aggregations ---")
        df_disease_cases = df_disease.withColumn(
            "total_cases",
            col("cholera_cases") + col("typhoid_cases") + col("diarrhea_cases") + col("hepatitis_cases")
        ).withColumn("year_month", substring(col("date"), 1, 7))

        monthly_agg = df_disease_cases.groupBy("year_month").agg(
            sum("cholera_cases").alias("total_cholera"),
            sum("typhoid_cases").alias("total_typhoid"),
            sum("diarrhea_cases").alias("total_diarrhea"),
            sum("hepatitis_cases").alias("total_hepatitis"),
            sum("total_cases").alias("grand_total")
        ).orderBy("year_month")
        monthly_agg.show(12, truncate=False)

        print("\n[+] Apache Spark Big Data processing completed successfully.")

    finally:
        spark.stop()

if __name__ == "__main__":
    master_node = sys.argv[1] if len(sys.argv) > 1 else "local[*]"
    dataset_folder = sys.argv[2] if len(sys.argv) > 2 else "data"
    run_spark_job(master_node, dataset_folder)
