package com.hualala.rabbitmqspring.converter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * MessageConverter  消息转换器
 * 我们在进行发送消息的时候, 正常情况下消息体为二进制的数据方式进行传输, 如果希望内部帮我们进行转换,或指定自定义的转换器,
 * 就需要用到MessageConverter
 *
 * 自定义常用转换器：MessageConverter, 一般都需要实现这个接口
 * 重写下面两个方法
 * toMessage: java对象转换为Message
 * fromMessage: Message对象转换为java对象
 *
 * Json转换器: Jackson2JsonMessageConverter: 可以进行java对象的转换功能
 * DefaultJackson2JavaTypeMapper映射器: 可以进行Java对象的映射关系
 * 自定义二进制转换器: 比如图片类型、PDF、PPT、流媒体
 *
 *
 *
 */
public class TextMessageConverter implements MessageConverter {
    /**
     * Convert a Java Object to a Message
     * @param object
     * @param messageProperties
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
        return new Message(object.toString().getBytes(), messageProperties);
    }

    /**
     *
     * Convert from a Message to a Java Object。
     * before consume message, we can do something with the message
     * @param message
     * @return
     * @throws MessageConversionException
     */
    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        String contentType = message.getMessageProperties().getContentType();
        if (null != contentType && contentType.contains("text")) {
            // 若是纯文本的消息, 返回一个String.
            // 我们定义的MessageDelegate.consumeMessage(String messageBody)方法就会接受到此放回值
            return new String(message.getBody());
        }
        return message.getBody();
    }
}
