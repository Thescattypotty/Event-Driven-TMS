package org.driventask.task.KafkaService;

import org.driventask.task.Payload.Kafka.TaskCreation;
import org.driventask.task.Payload.Kafka.TaskUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTaskCreationEvent(TaskCreation taskCreation){
        Message<TaskCreation> message = MessageBuilder
            .withPayload(taskCreation)
            .setHeader(KafkaHeaders.TOPIC, "task-topic")
            .build();
        kafkaTemplate.send(message);
    }

    public void sendTaskUpdateEvent(TaskUpdate taskUpdate) {
        Message<TaskUpdate> message = MessageBuilder
            .withPayload(taskUpdate)
            .setHeader(KafkaHeaders.TOPIC, "task-topic")
            .build();
        kafkaTemplate.send(message);
    }
}
