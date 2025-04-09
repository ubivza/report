package com.mobile.network.report.mapper;

import com.mobile.network.report.db.entity.CDRRecord;
import com.mobile.network.report.integration.rabbitmq.producer.api.CDRRecordMessage;
import com.mobile.network.report.model.inner.CDRRecordDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CDRRecordMapper {
    CDRRecord toEntity(CDRRecordDto dto);
    @Mapping(target = "callType", expression = "java( dto.getCallType().getType() )")
    CDRRecordMessage toMessage(CDRRecordDto dto);
}
