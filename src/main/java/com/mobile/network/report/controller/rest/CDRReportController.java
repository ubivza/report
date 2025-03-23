package com.mobile.network.report.controller.rest;

import com.mobile.network.report.model.outer.CDRFileReportDto;
import com.mobile.network.report.model.outer.CDRReportFileStatusDto;
import com.mobile.network.report.service.api.CDRReportService;
import com.mobile.network.report.service.api.ReportStatusService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер отвечает за формирование cdr отчетов
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${api.prefix.public}/cdr")
public class CDRReportController {

    private final CDRReportService cdrReportService;
    private final ReportStatusService reportStatusService;

    /**
     * Неблокирующий пост запрос, возвращает uuid запроса сразу,
     * а потом формирует отчет, чтобы ендпоинт был доступен для других запросов
     * @param phoneNumber номер пользователя, отчет по которому хотим сформировать
     * @param start время начала выборки
     * @param end время конца выборки
     * @return дто с uuid запроса
     */
    @PostMapping("/{phoneNumber}")
    public CDRFileReportDto generateCDRCsvReport(@PathVariable
        @Valid @Pattern(regexp = "\\d{11}", message = "Phone number must be exactly 11 digits") String phoneNumber,
        @RequestParam("start") Instant start,
        @RequestParam("end") Instant end) {

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Start of the period cannot be after end of the period");
        }

        UUID requestId = UUID.randomUUID();

        cdrReportService.generateCDRReportAsync(phoneNumber, start, end, requestId);

        return CDRFileReportDto.builder()
            .requestId(requestId)
            .build();
    }

    /**
     * Ендпоинт предоставляет проверку статуса формирования отчета
     * @param requestId UUID ответ из ендпоинта на формирование
     * @return статус запроса
     */
    @GetMapping("/status/{requestId}")
    public CDRReportFileStatusDto getReportStatus(@PathVariable UUID requestId) {
        String status = reportStatusService.getStatus(requestId);
        return CDRReportFileStatusDto.builder()
            .status(status)
            .build();
    }
}
