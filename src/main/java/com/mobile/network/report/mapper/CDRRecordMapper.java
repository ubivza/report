package com.mobile.network.report.mapper;

import com.mobile.network.report.db.entity.CDRRecord;
import com.mobile.network.report.model.inner.CDRRecordDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CDRRecordMapper {
    CDRRecordDto toDto(CDRRecord entity);
    CDRRecord toEntity(CDRRecordDto dto);
}
