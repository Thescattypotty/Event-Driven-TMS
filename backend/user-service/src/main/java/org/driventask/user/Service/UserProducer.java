package org.driventask.user.Service;

import org.driventask.user.Payload.Response.UserChangingListener;
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
    private final KafkaTemplate<String, UserChangingListener> kafkaTemplate;

    public void sendEmailToUser(UserChangingListener userChangingPasword){
        Message<UserChangingListener> message = MessageBuilder
            .withPayload(userChangingPasword)
            .setHeader(KafkaHeaders.TOPIC, "user-topic")
            .build();

        kafkaTemplate.send(message);
    }
}
