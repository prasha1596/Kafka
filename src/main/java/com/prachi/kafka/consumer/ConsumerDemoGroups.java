package com.prachi.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoGroups {
    public static void main(String[] args) {
        /*
        * Each consumer in the consumer group reads from the same partition, i.e., if there are 3 partitions and 3 consumers,
        * each consumer will read from one-one dedicated partition.
        * If there are 3 partitions and 2 consumers, then one consumer will read from 2 partitions and 2nd consumer will read from 3rd partition.
        * We can check this by running multiple consumers.
        * Msg seen in logs- Attempt to heartbeat failed since group is rebalancing. Re-joining group.
        * */

        Logger logger = LoggerFactory.getLogger(ConsumerDemoGroups.class);

        //create consumer config
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        // when a producer takes a string serializes it to bytes and send to kafka, when kafka sends these bytes right back to consumer,
        // consumer has to take these bytes and create a string from it - this process is deserialization.
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "my-xth-app");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");//latest-read from only new messages; earliest-read all messages
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");

        //create Consumer
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        //Subscribe consumer to our topic(s)
        kafkaConsumer.subscribe(Collections.singleton("first_topic")); //For subscribing to multiple topics : Arrays.asList("T1","T2",...)

        //poll for new data (asking for new data)
        while(true){
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record: consumerRecords){
                logger.info("Key: "+record.key()+" Value: "+record.value());
                logger.info(" Partition:"+record.partition()+" Offset: "+record.offset());
            }
        }
    }
}