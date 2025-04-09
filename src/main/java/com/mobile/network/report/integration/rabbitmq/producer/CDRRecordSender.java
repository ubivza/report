package com.mobile.network.report.integration.rabbitmq.producer;

import com.mobile.network.report.integration.rabbitmq.producer.api.CDRReportMessage;

public interface CDRRecordSender {
    void send(CDRReportMessage message);
}
