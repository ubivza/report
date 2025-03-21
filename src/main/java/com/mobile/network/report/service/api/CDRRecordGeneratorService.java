package com.mobile.network.report.service.api;

import com.mobile.network.report.model.inner.CDRRecordDto;
import java.util.List;

public interface CDRRecordGeneratorService {
    List<CDRRecordDto> generateCDRRecords(int year);
    void saveRecords(List<CDRRecordDto> recordDtos);
}
