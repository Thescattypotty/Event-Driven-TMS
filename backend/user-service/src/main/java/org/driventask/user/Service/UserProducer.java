package org.driventask.user.Service;

import org.driventask.user.Payload.Kafka.UserCreation;
import org.driventask.user.Payload.Kafka.UserUpdated;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void handleUserCreation(UserCreation userCreation){
        Message<UserCreation> message = MessageBuilder
            .withPayload(userCreation)
            .setHeader(KafkaHeaders.TOPIC, "user-topic")
            .build();
        System.out.println("Handler User Creartion is sent");
        kafkaTemplate.send(message);
    }
    public void handleUserUpdate(UserUpdated userUpdated){
        Message<UserUpdated> message = MessageBuilder
            .withPayload(userUpdated)
            .setHeader(KafkaHeaders.TOPIC, "user-topic")
            .build();
        System.out.println("Handler User Update is sent");
        kafkaTemplate.send(message);
    }
}
