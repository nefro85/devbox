package io.s7i.tool;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class Kafka {
    private final Loader loader;

    @Bean
    ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(loader.kafkaConfig());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }


    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerFactory(ConsumerFactory<String, String> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory() {
        var kafkaConfig = loader.kafkaConfig();
        var bs = kafkaConfig.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);

        log.info(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG + " -> {}", bs);

        return new DefaultKafkaConsumerFactory<>(kafkaConfig);
    }

}
