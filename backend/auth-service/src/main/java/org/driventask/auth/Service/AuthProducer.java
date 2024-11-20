package org.driventask.auth.Service;

import org.driventask.auth.Payload.Kafka.UserLogedIn;
import org.driventask.auth.Payload.Kafka.UserLogedOut;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthProducer {
    private final KafkaTemplate<String , Object> kafkaTemplate;

    public void handleUserAuthenticated(UserLogedIn userLogedIn){
        Message<UserLogedIn> message = MessageBuilder
            .withPayload(userLogedIn)
            .setHeader(KafkaHeaders.TOPIC, "auth-topic")
            .build();
        kafkaTemplate.send(message);
    }
    
    public void handleUserLogout(UserLogedOut userLogedOut){
        Message<UserLogedOut> message = MessageBuilder
            .withPayload(userLogedOut)
            .setHeader(KafkaHeaders.TOPIC, "auth-topic")
            .build();
        kafkaTemplate.send(message);
    }
}
