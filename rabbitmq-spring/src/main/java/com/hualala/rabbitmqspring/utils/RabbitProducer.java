package com.hualala.rabbitmqspring.utils;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RabbitProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void send(Object obj, Map<String, Object> properties) throws Exception{
        MessageHeaders headers = new MessageHeaders(properties);
        Message message = MessageBuilder.createMessage(obj, headers);

        rabbitTemplate.convertAndSend("exchange-1", "springboot.hello", message);
    }
}
