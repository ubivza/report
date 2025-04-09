package com.mobile.network.report.integration.rabbitmq.producer.api;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

//TODO вынести в общую апишку
@Getter
@Builder
@Jacksonized
public class CDRReportMessage {
    private List<CDRRecordMessage> cdrRecords;
}
