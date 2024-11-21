package org.driventask.project.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProjectTopicConfiguration{
    
    public NewTopic projectTopic(){
        return TopicBuilder
            .name("project-topic")
            .build();
    } 


}