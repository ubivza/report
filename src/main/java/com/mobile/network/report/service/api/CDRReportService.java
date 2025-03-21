package com.mobile.network.report.service.api;

import java.time.Instant;
import java.util.UUID;

public interface CDRReportService {
    void generateCDRReportAsync(String phoneNumber, Instant start, Instant end, UUID requestId);
}
