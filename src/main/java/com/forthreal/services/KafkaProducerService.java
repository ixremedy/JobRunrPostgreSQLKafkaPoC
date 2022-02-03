package com.forthreal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {
    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void produceMessage(String message)
    {
        kafkaTemplate.send("incomingRule", "message", message);
    }
}
