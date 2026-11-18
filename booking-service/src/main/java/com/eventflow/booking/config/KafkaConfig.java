package com.eventflow.booking.config;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic bookingCreatedTopic() {
        return TopicBuilder.name("booking-created").partitions(3).replicas(1).build();
    }
}
