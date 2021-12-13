package com.prachi.kafka.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ProducerDemoWithCallback.class);
        //create producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //by default kafka client will convert our data to bytes(0s,1s) when sending to kafka.
        // key and value serializer help kafka know what value we are sending to kafka and how this be serialized to bytes.
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //create producer
        //KafkaProducer<keyFormat, valueFormat>
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        //create a producer record
        ProducerRecord<String, String> record = new ProducerRecord<>("first_topic","Papa");

        //send data - asynchronous
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                //execute everytime a record is successfully sent or exception is thrown
                if(e==null){
                    logger.info("received metadata \n" + "Topic:"+recordMetadata.topic() +"\n"
                    +"Partition:"+recordMetadata.partition()+"\n"
                    +"Offset:"+recordMetadata.offset()+"\n"+"Timestamp:"+recordMetadata.timestamp());
                } else {
                    logger.error("Error while producing",e);
                }
            }
        });
        //flush
        producer.flush();
        //flush and close producer
        producer.close();
    }
}
