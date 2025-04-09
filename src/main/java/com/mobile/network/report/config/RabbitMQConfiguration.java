package com.mobile.network.report.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfiguration {

    @Value("${rabbitmq.exchange-name.cdr.send.name}")
    private String exchangeName;
    @Value("${rabbitmq.queue.cdr.name}")
    private String queueName;
    @Value("${rabbitmq.routing-key.cdr.name}")
    private String cdrRoutingKey;
    private final ConnectionFactory connectionFactory;

    /**
     * Бины cdrQueue, directExchange, binding нужны для настроек нужного ексченжа, очереди и роутинг ключа
     * @return
     */
    @Bean
    public Queue cdrQueue() {
        return new Queue(queueName);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding binding(DirectExchange directExchange, Queue cdrQueue) {
        return BindingBuilder.bind(cdrQueue)
            .to(directExchange)
            .with(cdrRoutingKey);
    }

    /**
     * Тк сервис общается лишь через один "топик", устанавливаю его как дефолтный для темплейта в этом сервисе и роутинг ключ
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        template.setExchange(exchangeName);
        template.setRoutingKey(cdrRoutingKey);
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
