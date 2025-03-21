package com.mobile.network.report.model.inner;

import com.mobile.network.report.db.entity.CallType;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CDRRecordDto {
    private CallType callType;
    private String callerPhoneNumber;
    private String receiverPhoneNumber;
    private Instant callStartTime;
    private Instant callEndTime;
}
