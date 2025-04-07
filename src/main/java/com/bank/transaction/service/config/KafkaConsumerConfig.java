//package com.bank.transaction.service.config;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import com.bank.transaction.service.events.Event;
//
//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//	
//	private final String bootstrapAddress = "localhost:9092";
//
//    @Bean
//    public ConsumerFactory<String, Event<?>> consumerFactory() {
//        Map<String, String> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapAddress);
//        props.put(JsonSerializer.TYPE_MAPPINGS,"op.banco:op.banco.events.Event");
////        props.put(JsonSerializer.TYPE_MAPPINGS,"event:spring.boot.webflu.ms.cliente.app.event.Event");
//        
//        final JsonDeserializer<Event<?>> jsonDeserializer = new JsonDeserializer<>();
//        return new DefaultKafkaConsumerFactory(props,new StringDeserializer(),jsonDeserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Event<?>>
//    kafkaListenerContainerFactory() {
//
//        ConcurrentKafkaListenerContainerFactory<String, Event<?>> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
//
//}
