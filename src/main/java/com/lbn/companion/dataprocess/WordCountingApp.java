package com.lbn.companion.dataprocess;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;


public class WordCountingApp {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {

        System.out.println("Start of application");
        SparkSession spark = SparkSession.builder()
                .appName("WordCountingApp")
                .getOrCreate();
        // Create DataSet representing the stream of input lines from kafka
        Dataset<String> lines = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "messages")
                .load()
                .selectExpr("CAST(value AS STRING)")
                .as(Encoders.STRING());
        // Generate running word count
        Dataset<Row> wordCounts = lines
                .flatMap((FlatMapFunction<String, String>)line -> Arrays.asList(line.split(" ")).iterator(),
                        Encoders.STRING())
                .groupBy("value").count();
        // Start running the query that prints the running counts to the console
        StreamingQuery query = wordCounts.writeStream()
                .outputMode("complete")
                .format("console")
                .start();
        query.awaitTermination();

        System.out.println("End of application");
    }
}
