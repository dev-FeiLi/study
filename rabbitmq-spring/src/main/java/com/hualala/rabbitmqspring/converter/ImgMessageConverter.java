package com.hualala.rabbitmqspring.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class ImgMessageConverter implements MessageConverter{

    @Override
    public Message toMessage(Object o, MessageProperties messageProperties) throws MessageConversionException {
        throw new MessageConversionException(" convert error !");
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        System.out.println("-------------------------ImageMessageConverter-------------------------");

        Object _extName = message.getMessageProperties().getHeaders().get("extName");
        String extName = _extName == null ? "png" : _extName.toString();

        byte[] body = message.getBody();
        String fileName = UUID.randomUUID().toString();
        String path = "E:/001_test/" + fileName + "." + extName;
        File file = new File(path);
        try {
            Files.copy(new ByteArrayInputStream(body), file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
