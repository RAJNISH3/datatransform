package com.lbn.companion.dataprocess;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;

public class WordCountingApp {

    public static void main(String[] args) throws TimeoutException, StreamingQueryException {

        System.out.println("Start of application");
        SparkConf sparkConf = new SparkConf();
        sparkConf.setMaster("local[2]");
        sparkConf.setAppName("WordCountingApp");
        sparkConf.set("spark.cassandra.connection.host", "cassandra");
        sparkConf.set("spark.cassandra.connection.port", "9042");
        sparkConf.set("spark.cassandra.connection.localDC", "datacenter1");

        SparkSession spark = SparkSession.builder()
                .config(sparkConf)
                .appName("WordCountingApp")
                .getOrCreate();
        // Create DataSet representing the stream of input lines from kafka
        Dataset<String> lines = spark.readStream()
                .format("kafka")
                .option("failOnDataLoss", false)
                .option("kafka.bootstrap.servers", "kafka:9092")
                .option("subscribe", "messages")
                .load()
                .selectExpr("CAST(value AS STRING)")
                .as(Encoders.STRING());
        // Generate running word count
        Dataset<Row> wordCounts = lines
                .flatMap((FlatMapFunction<String, String>) line -> Arrays.asList(line.split(" "))
                                .iterator(),
                        Encoders.STRING())
                .groupBy("value").count();


        StreamingQuery query = wordCounts.writeStream()
                .outputMode(OutputMode.Complete())
                .option("confirm.truncate", true)
                .option("checkpointLocation", "checkpoint")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", "messages")
                .option("table", "words")
                .start();



        //    wordCounts.write()
        //            .format("org.apache.spark.sql.cassandra")
        //            .options(new HashMap<>() {{
        //                put("keyspace", "messages");
        //                put("table", "wordcount");
        //            }})
        //            .mode(SaveMode.Append)
        //            .save();

        // Start running the query that prints the running counts to the console
//        StreamingQuery query = wordCounts.writeStream()
//                .outputMode("complete")
//                .format("console")
//                .start();

        query.awaitTermination();
        System.out.println("End of application");
    }
}
