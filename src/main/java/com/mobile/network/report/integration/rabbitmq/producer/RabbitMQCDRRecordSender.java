package com.mobile.network.report.integration.rabbitmq.producer;

import com.mobile.network.report.integration.rabbitmq.producer.api.CDRReportMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQCDRRecordSender implements CDRRecordSender {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(CDRReportMessage message) {
        rabbitTemplate.convertAndSend(message);
    }
}
