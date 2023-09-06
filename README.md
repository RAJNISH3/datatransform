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
./bin/spark-submit --class com.lbn.companion.dataprocess.WordCountingApp /usr/apps/dataprocess.jar --master spark://localhost:7077 --deploy-mode cluster
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
  
