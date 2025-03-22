package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.repository.CDRRecordRepository;
import com.mobile.network.report.exception.NotFoundException;
import com.mobile.network.report.mapper.CDRRecordMapper;
import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.service.api.UDRReportFormatterService;
import com.mobile.network.report.service.api.UDRReportService;
import com.mobile.network.report.utils.DateTimeUtils;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Класс отвечает за работу с UDR отчетами, формирование делегирует UDRReportFormatterService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UDRReportServiceImpl implements UDRReportService {

    private final CDRRecordRepository repository;
    private final UDRReportFormatterService udrReportFormatterService;
    private final CDRRecordMapper mapper;

    /**
     * Метод формирования UDR отчетов по пользователю за весь период тарификации
     * @param phoneNumber номер телефона пользователя
     * @return отчет
     */
    @Override
    public UDRReportDto getUDRForCustomerFullPeriod(String phoneNumber) {
        List<CDRRecordDto> calls = repository.findAllByCallerPhoneNumberOrReceiverPhoneNumber(phoneNumber).stream()
            .map(mapper::toDto)
            .toList();

        if (calls.isEmpty()) {
            throw new NotFoundException("Records with phone number " + phoneNumber + " not found");
        }

        return udrReportFormatterService.buildUDRResponse(phoneNumber, calls);
    }

    /**
     * Метод формирования отчета по пользователю за определенный период
     * @param phoneNumber номер телефона пользователя
     * @param yearMonth год-месяц выбороки
     * @return сформированный отчет
     */
    @Override
    public UDRReportDto getUDRForCustomerByMonth(String phoneNumber, YearMonth yearMonth) {
        Instant start = DateTimeUtils.getStartOfMonth(yearMonth);
        Instant end = DateTimeUtils.getEndOfMonth(yearMonth);

        log.info("Start period from: {}", start);
        log.info("End period on: {}", end);

        List<CDRRecordDto> callsAsCaller = repository.findAllByCallerPhoneNumberAndCallStartTimeBetween(phoneNumber, start, end).stream()
            .map(mapper::toDto)
            .toList();
        List<CDRRecordDto> callsAsReceiver = repository.findAllByReceiverPhoneNumberAndCallStartTimeBetween(phoneNumber, start, end).stream()
            .map(mapper::toDto)
            .toList();

        if (callsAsCaller.isEmpty() && callsAsReceiver.isEmpty()) {
            throw new NotFoundException("Records with phone number " + phoneNumber + " not found for provided period");
        }

        List<CDRRecordDto> summaryRecordsList = Stream.concat(callsAsCaller.stream(), callsAsReceiver.stream()).toList();

        return udrReportFormatterService.buildUDRResponse(phoneNumber, summaryRecordsList);
    }

    /**
     * Метод отвечает за формирование отчетов по всем пользователям за запрошенный период
     * @param yearMonth год-месяц запрошенного периода
     * @return список UDR отчетов по всем пользователям
     */
    @Override
    public List<UDRReportDto> getUDRForAllCustomersByMonth(YearMonth yearMonth) {
        Instant start = DateTimeUtils.getStartOfMonth(yearMonth);
        Instant end = DateTimeUtils.getEndOfMonth(yearMonth);

        log.info("Start period from: {}", start);
        log.info("End period on: {}", end);

        List<CDRRecordDto> calls = repository.findAllByCallStartTimeBetween(start, end).stream()
            .map(mapper::toDto)
            .toList();

        if (calls.isEmpty()) {
            throw new NotFoundException("Records for the period not found");
        }

        return udrReportFormatterService.buildAllUDRResponse(calls);
    }
}
