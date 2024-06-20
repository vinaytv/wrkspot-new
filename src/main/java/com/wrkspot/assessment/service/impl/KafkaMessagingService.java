package com.wrkspot.assessment.service.impl;

import com.wrkspot.assessment.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessagingService implements MessagingService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessagingService.class);

    @Value("${spring.kafka.topic}")
    private String topic = "NEW_CUSTOMER";

    @Autowired
    private  KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String key, Object message) {
        logger.info("sending message {}  to topic {} ", message, topic);
        try {
            kafkaTemplate.send(topic, key, message);

        } catch (Exception e) {
            logger.info("error while sending message {}  to topic {} ", message, topic);
        }
    }
}
