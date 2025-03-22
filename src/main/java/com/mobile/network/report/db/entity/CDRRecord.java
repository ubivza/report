package com.mobile.network.report.db.entity;

import com.mobile.network.report.db.entity.converter.CallTypeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cdr_records", indexes = {
    @Index(name = "idx_caller_phone_number", columnList = "callerPhoneNumber"),
    @Index(name = "idx_receiver_phone_number", columnList = "receiverPhoneNumber"),
    @Index(name = "idx_call_start_time", columnList = "callStartTime")
})
public class CDRRecord extends BaseEntity {

    @Convert(converter = CallTypeConverter.class)
    private CallType callType;
    private String callerPhoneNumber;
    private String receiverPhoneNumber;
    private Instant callStartTime;
    private Instant callEndTime;
}
