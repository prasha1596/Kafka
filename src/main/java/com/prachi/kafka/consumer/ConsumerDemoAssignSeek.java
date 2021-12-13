package com.prachi.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

public class ConsumerDemoAssignSeek {
    public static void main(String[] args) {
        /*
        * Remove group id
        * Remove consumer.subscribe*/

        Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);

        //create consumer config
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        // when a producer takes a string serializes it to bytes and send to kafka, when kafka sends these bytes right back to consumer,
        // consumer has to take these bytes and create a string from it - this process is deserialization.
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");//latest-read from only new messages; earliest-read all messages
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false");

        //create Consumer
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        //assign and seek are mostly used to replay data or fetch a specific message

        //assign
        TopicPartition partitionToReadFrom = new TopicPartition("first_topic", 0);
        long offsetToReadFrom = 15L;
        kafkaConsumer.assign(Arrays.asList(partitionToReadFrom));

        //seek
        kafkaConsumer.seek(partitionToReadFrom, offsetToReadFrom);

        int numOfMessagesToRead=5;
        int numOfMessagesReadSoFar=0;
        boolean keepOnReading = true;

        //poll for new data (asking for new data)
        while(keepOnReading){
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));

            for(ConsumerRecord<String,String> record: consumerRecords){
                numOfMessagesReadSoFar++;
                logger.info("Key: "+record.key()+" Value: "+record.value());
                logger.info(" Partition:"+record.partition()+" Offset: "+record.offset());
                if(numOfMessagesReadSoFar>numOfMessagesToRead){
                    keepOnReading=false; //to exit while loop
                    break; //to exit for loop
                }
            }
        }
    }
}