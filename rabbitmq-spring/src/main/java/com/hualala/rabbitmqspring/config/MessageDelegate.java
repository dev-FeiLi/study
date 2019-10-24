package com.hualala.rabbitmqspring.config;

import com.hualala.rabbitmqspring.entity.Order;

import java.io.File;
import java.util.Map;

public class MessageDelegate {

    public void handleMessage(byte[] messageBody) {
        System.out.println("默认方法, 消息内容： " + new String(messageBody));
    }

    public void consumeMessage(byte[] messageBody) {
        System.out.println("consumeMessage 方法 接收消息：" + new String(messageBody));
    }


    public void consumeMessage(String messageBody) {
        System.out.println("字符串方法，  消息内容：" + messageBody);
    }

    /**
     * 如果配置了Jackson2JsonMessageConverter消息转换器
     * 以json格式传输message, 此处接受消息参数则是用Map 类型
     * @param messageBody 消息队列里面的消息, json格式对应为Map
     */
    public void consumeMessage(Map messageBody){
        System.out.println("map方法, 消息内容：" + messageBody);
    }

    public void consumeMessage(Order order) {
        System.out.println("order 对象, 消息内容: id=" + order.getId()
                + ", name=" + order.getName() + ", content=" + order.getContent());
    }

    public void consumeMessage(File file) {
        System.out.println("文件对象方法, 消息内容: " + file.getName());
    }


    public void method1(String messageBody) {
        System.out.println("---method1 收到消息内容: " + messageBody);
    }

    public void method2(String messageBody) {
        System.out.println("---method2 收到消息内容: " + messageBody);
    }
}
