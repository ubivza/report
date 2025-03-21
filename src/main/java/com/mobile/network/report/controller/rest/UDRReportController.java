package com.mobile.network.report.controller.rest;

import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.service.api.UDRReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер отвечает за работу с UDR отчетами
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${api.prefix.public}/udr")
public class UDRReportController {

    private final UDRReportService service;

    /**
     * Гет метод получения UDR отчета по номеру телефона пользователя и периоду
     * @param phoneNumber номер телефона пользователя
     * @param month год-месяц выборки записей
     * @param fullPeriod false - не учитывать параметр в запросе, true - выборка по номеру за весь тарифицируемый период
     * @return дто UDR отчета по постановке
     */
    @GetMapping("/{phoneNumber}")
    public UDRReportDto getUserUDRRecord(@PathVariable("phoneNumber")
        @Valid @Pattern(regexp = "\\d{11}", message = "Phone number must be exactly 11 digits") String phoneNumber,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month,
        @RequestParam(required = false, defaultValue = "false") boolean fullPeriod) {

        if (month != null && fullPeriod) {
            throw new IllegalArgumentException("Cannot generate report for both 'month' and 'fullPeriod'");
        }

        if (month == null && !fullPeriod) {
            throw new IllegalArgumentException("Report period not provided");
        }

        if (fullPeriod) {
            return service.getUDRForCustomerFullPeriod(phoneNumber);
        } else {
            return service.getUDRForCustomerByMonth(phoneNumber, month);
        }
    }

    /**
     * Гет метод возвращает UDR записи по всем нашим абонентам за запрошенный месяц.
     * @param month год-месяц выборки
     * @return список дто с UDR записями по номерам пользователей
     */
    @GetMapping("/all")
    public List<UDRReportDto> getAllUDRRecord(@RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth month) {

        if (month == null) {
            throw new IllegalArgumentException("Report period not provided");
        }

        return service.getUDRForAllCustomersByMonth(month);
    }
}
