package com.mobile.network.report.db.entity;

import com.mobile.network.report.db.entity.converter.CallTypeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cdr_records")
public class CDRRecord extends BaseEntity {

    @Convert(converter = CallTypeConverter.class)
    private CallType callType;
    private String callerPhoneNumber;
    private String receiverPhoneNumber;
    private Instant callStartTime;
    private Instant callEndTime;
}
