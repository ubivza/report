package com.mobile.network.report.service.api;

import com.mobile.network.report.model.outer.UDRReportDto;
import java.time.YearMonth;
import java.util.List;

public interface UDRReportService {
    UDRReportDto getUDRForCustomerFullPeriod(String phoneNumber);
    UDRReportDto getUDRForCustomerByMonth(String phoneNumber, YearMonth yearMonth);
    List<UDRReportDto> getUDRForAllCustomersByMonth(YearMonth yearMonth);
}
