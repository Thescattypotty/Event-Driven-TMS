package org.driventask.user.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaUserTopicConfiguration {
 
    public NewTopic userTopic(){
        return TopicBuilder.name("topic-user")
            .build();
    }
}
