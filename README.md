# datatransform
data transformation application to run the kafka consumer and producer local setup and spark streaming of data 

## Pre-requisite Steps
1. docker installation ` brew install docker `
2. docker compose install `brew install docker-compose`
3. Docker desktop install(since it is paid) or colima tool (command line based) `brew install colima` For more detail refer the [colima setup](https://opensource.com/article/22/9/docker-desktop-colima)


Now Run the docker compose file locally

```
docker-compose -f docker-compose.yml up -d

docker ps ## To get the container Id
```
Execute the below command to execute the kafka docker image
```
docker exec -it <containerId> /bin/sh
```
## command to check the available kafka topics, if not avaible then 2nd command needs to executed to create a topic
```
opt/bitnami/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
## kafka topic created with name `messages`, now if you execute the above command, you can check the messages topic 
```
opt/bitnami/kafka/bin/kafka-topics.sh --create --topic messages --bootstrap-server localhost:9092
```
## Command to consume/listen the kafka topic 
```
opt/bitnami/kafka/bin/kafka-console-consumer.sh --topic messages --bootstrap-server localhost:9092 --from-beginning
```
## Command to produce the kafka topic as string word
```
opt/bitnami/kafka/bin/kafka-console-producer.sh --topic messages --bootstrap-server localhost:9092
```

## Now execute master spark container and submit the jar the spark docker image to stream and receives the kafka topic data
```
docker exec -it <containerId> /bin/sh
```
```
./bin/spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.4.1 --class com.lbn.companion.dataprocess.WordCountingApp /usr/apps/dataprocess.jar --master spark://localhost:7077 --deploy-mode cluster
```
## now configure the casandra image as mentioned below
```
  cassandra:
    image: bitnami/cassandra:latest
    ports:
      - '9042:9042' # native protocol clients
    volumes:
      - './cassandra_data:/bitnami'
    environment:
      - CASSANDRA_USER=cassandra
      - CASSANDRA_PASS=cassandra
      - CASSANDRA_CQL_PORT_NUMBER=9042

```
### To test the casandra image 
```
 docker exec -it <casadra-Id> /bin/sh
```
To create login ot cqlsh command 
```
cqlsh -u cassandra -p cassandra

DESCRIBE keyspaces; # to check if keyspace exists or not?

CREATE KEYSPACE messages WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 } AND DURABLE_WRITES = false; # to create the keyspace

SELECT * FROM system_schema.keyspaces; # to check the keyspace

create table messages.test (count int PRIMARY KEY, value text);

DESC tables;
DESC messages.test; #keyspace_name.table_name 

```


## References
- [Spark Overview](https://spark.apache.org/docs/latest/index.html)
- [Spark Standalone Mode](https://spark.apache.org/docs/latest/spark-standalone.html)
- [Spark Streaming + Kafka Integration Guide](https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html)
- [Building a Data Pipeline with Kafka, Spark Streaming and Cassandra](https://www.baeldung.com/kafka-spark-data-pipeline)
- [Spark setup using Docker](https://hub.docker.com/r/bitnami/spark/)

## Structured Streaming
- [Structured Streaming Programming Guide](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html)
- [Structured Streaming + Kafka Integration Guide](https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html)
- [cassandra-docker](https://hub.docker.com/r/bitnami/cassandra/)
  
