package org.driventask.project.KafkaService;

import org.driventask.project.Payload.Kafka.ProjectCreation;
import org.driventask.project.Payload.Kafka.ProjectUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProjectCreationEvent(ProjectCreation projectCreation){
        Message<ProjectCreation> message = MessageBuilder
            .withPayload(projectCreation)
            .setHeader(KafkaHeaders.TOPIC, "project-topic")
            .build();
        kafkaTemplate.send(message);
    } 

    public void sendProjectUpdateEvent(ProjectUpdate projectUpdate){
        Message<ProjectUpdate> message = MessageBuilder
            .withPayload(projectUpdate)
            .setHeader(KafkaHeaders.TOPIC, "project-topic")
            .build();
        kafkaTemplate.send(message);
    } 
}
