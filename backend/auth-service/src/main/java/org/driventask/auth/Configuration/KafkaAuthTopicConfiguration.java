package org.driventask.auth.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAuthTopicConfiguration {
    public NewTopic authTopic(){
        return TopicBuilder.name("auth-topic")
            .build();
    }
}
