package io.s7i.tool;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageConsumer {

    @KafkaListener(topics = "${app.topic}", groupId = "${app.group}")
    public void listen(ConsumerRecord<?, ?> record) {
        var value = record.value();

        System.out.println("Received message: " + value);
    }

}