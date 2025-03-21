package com.mobile.network.report.service.api;

import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.outer.UDRReportDto;
import java.util.List;

public interface UDRReportFormatterService {
    UDRReportDto buildUDRResponse(String phoneNumber, List<CDRRecordDto> records);
    List<UDRReportDto> buildAllUDRResponse(List<CDRRecordDto> records);
}
