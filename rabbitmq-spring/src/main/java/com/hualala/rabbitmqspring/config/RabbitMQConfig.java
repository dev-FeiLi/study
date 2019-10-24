package com.hualala.rabbitmqspring.config;


import com.hualala.rabbitmqspring.converter.ImgMessageConverter;
import com.hualala.rabbitmqspring.converter.PDFMessageConverter;
import com.hualala.rabbitmqspring.converter.TextMessageConverter;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@ComponentScan({"com.hualala.rabbitmqspring.*"})
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("172.16.32.107");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    /**
     * 针对消费者配置
     * 1. 设置交换机类型
     * 2. 将队列绑定到交换机
     * FanoutExchange: 将消息分发到所有的绑定队列, 无routingKey的概念
     * HeaderExchange: 通过添加属性key-value匹配
     * DirectExchange: 按照routingKey分发到指定Queue
     * TopicExchange: 按pattern关键字匹配
     * @return
     */
    @Bean
    public TopicExchange exchange001() {
        return new TopicExchange("topic001", true, false);
    }
    @Bean
    public TopicExchange exchange002() {
        return new TopicExchange("topic002", true, false);
    }

    @Bean
    public Queue queue001(){
        return new Queue("queue001", true);
    }
    @Bean
    public Queue queue002(){
        return new Queue("queue002", true);
    }
    @Bean
    public Queue queue003(){
        return new Queue("queue003", true);
    }

    @Bean
    public Binding binding001(){
        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }
    @Bean
    public Binding binding002(){
        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    }
    @Bean
    public Binding binding003(){
        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    }

    @Bean
    public Queue queue_image(){
        return new Queue("image_queue", true);
    }
    @Bean
    public Queue queue_pdf(){
        return new Queue("pdf_queue", true);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    /**
     * 消息监听容器，监听消费消息的consumer
     * @param connectionFactory
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue001(), queue002(), queue003(), queue_image());
        container.setConcurrentConsumers(1);
        container.setMaxConcurrentConsumers(5);
        container.setDefaultRequeueRejected(false);
        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        container.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID().toString());
        /** 设置MessageListener有两种方式任选其一
         *  1. new MessageListener 监听器
         *  2. new MessageListenerAdapter 适配器
         */

        /** new MessageListener直接创建监听器, 重写onMessage方法 **/
//        container.setMessageListener(new ChannelAwareMessageListener() {
//            @Override
//            public void onMessage(Message message, Channel channel) throws Exception {
//                // 接收消息
//                String msg = new String(message.getBody());
//                System.out.println("----------------消费者: " + msg);
//            }
//        });

        /**
         * new MessageListenerAdapter消息监听适配器方式
         * 可以指定Delegate委托对象: 实际自定义的委托对象, 用户处理消息
         * 1. setDefaultListenerMethod(String methodName)来设置方法名为我们自己写的方法名
         * 2. setQueueOrTagToMethodName 队列标识与方法名称组合的集合: 可以一一进行队列和方法名称绑定,指定队列的消息会被绑定的方法接受处理
         * 可以添加一个自定义转换器: fromMessage方法里面写定规则, 从字节数组转换为String
         */
        // 1 setDefaultListenerMethod(String methodName)
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//        adapter.setMessageConverter(new TextMessageConverter());
//        container.setMessageListener(adapter);

        // 2 setQueueOrTagToMethodName(Map<K,V> map) 队列名称跟方法 一一进行匹配
        /*MessageListenerAdapter adapter1 = new MessageListenerAdapter(new MessageDelegate());
        adapter1.setMessageConverter(new TextMessageConverter());
        Map<String, String> map = new HashMap<>();
        map.put("queue001", "method1");
        map.put("queue002", "method2");
        adapter1.setQueueOrTagToMethodName(map);
        container.setMessageListener(adapter1);*/

        /** 1.1支持Json格式的转换器 **/
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        adapter.setMessageConverter(jackson2JsonMessageConverter);

//        container.setMessageListener(adapter);


        /** 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter 支持java对象转换 **/
//        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
//        adapter.setDefaultListenerMethod("consumeMessage");
//
//        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
//        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
//        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
//        adapter.setMessageConverter(jackson2JsonMessageConverter);

//        container.setMessageListener(adapter);


        /** 1.3 自定义其他类型Img, PDF等类型转换器  **/
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        // 全局转换器
        ContentTypeDelegatingMessageConverter converter = new ContentTypeDelegatingMessageConverter();
        TextMessageConverter textConverter = new TextMessageConverter();
        converter.addDelegate("text", textConverter);
        converter.addDelegate("html/text", textConverter);
        converter.addDelegate("xml/text", textConverter);
        converter.addDelegate("html/plain", textConverter);

        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        converter.addDelegate("json", jsonConverter);
        converter.addDelegate("application/json", jsonConverter);

        ImgMessageConverter imgConverter = new ImgMessageConverter();
        converter.addDelegate("img/png", imgConverter);
        converter.addDelegate("img", imgConverter);

        PDFMessageConverter pdfConverter = new PDFMessageConverter();
        converter.addDelegate("application/pdf", pdfConverter);

        adapter.setMessageConverter(converter);
        container.setMessageListener(adapter);
        return container;
    }
}
