package com.aquasafe.spark;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.*;

/**
 * AquaSafe Distributed Big Data Processing Job for Apache Spark and Hadoop HDFS.
 * Performs large-scale distributed data loading, filtering, grouping, aggregation,
 * district-level epidemiological analytics, water quality statistics, and weather analysis.
 */
public class AquaSafeSparkJob {

    public static void main(String[] args) {
        String master = args.length > 0 ? args[0] : "local[*]";
        String hdfsBase = args.length > 1 ? args[1] : "hdfs://localhost:9000/aquasafe/data/";

        System.out.println("==========================================================");
        System.out.println("Starting AquaSafe Apache Spark Big Data Analytics Job");
        System.out.println("Master: " + master);
        System.out.println("HDFS Path: " + hdfsBase);
        System.out.println("==========================================================");

        SparkSession spark = SparkSession.builder()
                .appName("AquaSafe-BigData-Analytics")
                .master(master)
                .config("spark.sql.shuffle.partitions", "4")
                .getOrCreate();

        try {
            // 1. Load Datasets from HDFS or Local Storage
            Dataset<Row> waterQualityDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(hdfsBase + "water_quality.csv");

            Dataset<Row> weatherDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(hdfsBase + "weather_data.csv");

            Dataset<Row> diseaseDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv(hdfsBase + "disease_records.csv");

            System.out.println("Loaded Water Quality count: " + waterQualityDF.count());
            System.out.println("Loaded Weather Records count: " + weatherDF.count());
            System.out.println("Loaded Disease Records count: " + diseaseDF.count());

            // 2. Water Quality Aggregations per District
            Dataset<Row> wqStats = waterQualityDF
                    .filter(col("ph").between(0.0, 14.0))
                    .groupBy("district")
                    .agg(
                            avg("ph").alias("spark_avg_ph"),
                            avg("turbidity").alias("spark_avg_turbidity"),
                            avg("tds").alias("spark_avg_tds"),
                            avg("dissolved_oxygen").alias("spark_avg_do"),
                            avg("water_temperature").alias("spark_avg_temp")
                    );
            System.out.println("--- Spark Water Quality District Averages ---");
            wqStats.show(10);

            // 3. Weather Statistics per District
            Dataset<Row> weatherStats = weatherDF
                    .groupBy("district")
                    .agg(
                            avg("temperature").alias("spark_avg_temp"),
                            avg("rainfall").alias("spark_avg_rainfall"),
                            avg("humidity").alias("spark_avg_humidity"),
                            max("rainfall").alias("spark_max_rainfall")
                    );
            System.out.println("--- Spark Weather District Averages ---");
            weatherStats.show(10);

            // 4. Monthly Disease Statistics
            Dataset<Row> monthlyDiseases = diseaseDF
                    .withColumn("total_cases", col("cholera_cases").plus(col("typhoid_cases")).plus(col("diarrhea_cases")).plus(col("hepatitis_cases")))
                    .withColumn("year_month", substring(col("date"), 1, 7))
                    .groupBy("year_month")
                    .agg(
                            sum("cholera_cases").alias("total_cholera"),
                            sum("typhoid_cases").alias("total_typhoid"),
                            sum("diarrhea_cases").alias("total_diarrhea"),
                            sum("hepatitis_cases").alias("total_hepatitis"),
                            sum("total_cases").alias("grand_total_cases")
                    )
                    .sort("year_month");
            System.out.println("--- Spark Monthly Disease Aggregations ---");
            monthlyDiseases.show(12);

            // 5. Join Datasets on District and Date for Comprehensive Big Data Risk Analysis
            Dataset<Row> joinedDF = waterQualityDF.alias("wq")
                    .join(weatherDF.alias("wth"),
                            col("wq.district").equalTo(col("wth.district"))
                                    .and(col("wq.date").equalTo(col("wth.date"))))
                    .join(diseaseDF.alias("dis"),
                            col("wq.district").equalTo(col("dis.district"))
                                    .and(col("wq.date").equalTo(col("dis.date"))));

            System.out.println("Total Joined Environmental & Epidemiological Records: " + joinedDF.count());

            // 6. Save Aggregated Output back to HDFS / Local Target
            String outputPath = hdfsBase + "output/district_aggregates";
            wqStats.write().mode("overwrite").parquet(outputPath);
            System.out.println("Successfully exported Parquet aggregates to: " + outputPath);

        } finally {
            spark.stop();
        }
    }
}
