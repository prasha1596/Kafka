package com.prachi.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

//by giving key, we guarantee that same key goes to same partition always.
public class ProducerDemoKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Logger logger = LoggerFactory.getLogger(ProducerDemoKeys.class);
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        for(int i=0;i<10;i++){
            String key = "id_"+ i;
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>("first_topic",
                     key,"Hello World "+ i);

            logger.info("Key: ",key);

            kafkaProducer.send(producerRecord, (recordMetadata, e) -> {
                if(e==null){
                    logger.info("received metadata \n" + "Topic:"+recordMetadata.topic() + '\n'
                            +"Partition:"+recordMetadata.partition()+"\n"
                            +"Offset:"+recordMetadata.offset()+"\n"+"Timestamp:"+recordMetadata.timestamp());
                } else {
                    logger.error("Not able to send data. ");
                }
            }).get(); //block .send() to make it synchronous - don't do in prod
            kafkaProducer.flush();
        }
        kafkaProducer.close();
    }
}
