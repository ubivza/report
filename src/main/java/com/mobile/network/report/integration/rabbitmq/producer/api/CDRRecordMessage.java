package com.mobile.network.report.integration.rabbitmq.producer.api;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

//TODO вынести в общую апи
@Getter
@Builder
@Jacksonized
public class CDRRecordMessage {
    private String callType;
    private String callerPhoneNumber;
    private String receiverPhoneNumber;
    private Instant callStartTime;
    private Instant callEndTime;
}
