package com.mobile.network.report.service.api;

import java.util.UUID;

public interface ReportStatusService {
    void setStatus(UUID requestId, String status);
    String getStatus(UUID requestId);
}
