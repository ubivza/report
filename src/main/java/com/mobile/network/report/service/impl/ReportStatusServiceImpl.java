package com.mobile.network.report.service.impl;

import com.mobile.network.report.service.api.ReportStatusService;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Класс-контейнер статусов запросов на генерацию csv CDR отчетов
 */
@Service
public class ReportStatusServiceImpl implements ReportStatusService {
    private final Map<UUID, String> reportStatus = new ConcurrentHashMap<>();

    @Override
    public void setStatus(UUID requestId, String status) {
        reportStatus.put(requestId, status);
    }

    @Override
    public String getStatus(UUID requestId) {
        return reportStatus.getOrDefault(requestId, "There is no report with this UUID");
    }
}
