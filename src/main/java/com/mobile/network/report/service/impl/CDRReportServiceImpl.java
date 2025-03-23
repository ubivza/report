package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.entity.CDRRecord;
import com.mobile.network.report.db.repository.CDRRecordRepository;
import com.mobile.network.report.exception.NotFoundException;
import com.mobile.network.report.model.outer.CDRReportFileStatus;
import com.mobile.network.report.service.api.CDRReportService;
import com.mobile.network.report.service.api.ReportStatusService;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CDRReportServiceImpl implements CDRReportService {

    private final CDRRecordRepository repository;
    private final ReportStatusService reportStatusService;

    private static final String CSV_HEADER = "id,call_type,caller_phone_number,receiver_phone_number,call_start_time,call_end_time";
    @Value("${app.reports-dir}")
    private String reportsDir;

    /**
     * Метод асинхронно формирующий отчеты (каждый в своем потоке)
     * @param phoneNumber номер пользователя, отчет по которому хотим сформировать
     * @param start время начала выборки
     * @param end время конца выборки
     * @param requestId uuid запроса на формирование отчета
     */
    @Async
    @Override
    public void generateCDRReportAsync(String phoneNumber, Instant start, Instant end, UUID requestId) {
        try {
            reportStatusService.setStatus(requestId, CDRReportFileStatus.IN_PROGRESS);
            List<CDRRecord> recordsAsCaller = repository.findAllByCallerPhoneNumberAndCallStartTimeBetween(phoneNumber, start, end);
            List<CDRRecord> recordsAsReceiver = repository.findAllByReceiverPhoneNumberAndCallStartTimeBetween(phoneNumber, start, end);

            if (recordsAsCaller.isEmpty() && recordsAsReceiver.isEmpty()) {
                throw new NotFoundException("Records with phone number " + phoneNumber + " not found for provided period");
            }

            List<CDRRecord> summaryRecordsList = Stream.concat(recordsAsCaller.stream(), recordsAsReceiver.stream()).toList();

            String fileName = String.format("%s_%s.csv", phoneNumber, requestId);
            File reportDir = new File(reportsDir);

            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            File reportFile = new File(reportDir, fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                writer.write(CSV_HEADER + System.lineSeparator());
                for (CDRRecord record : summaryRecordsList) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s" + System.lineSeparator(),
                        record.getId(),
                        record.getCallType(),
                        record.getCallerPhoneNumber(),
                        record.getReceiverPhoneNumber(),
                        record.getCallStartTime(),
                        record.getCallEndTime()));
                }
            }
            reportStatusService.setStatus(requestId, CDRReportFileStatus.SUCCESS);
        } catch (IOException e) {
            log.info("Failed to generate cdr report file {}", e.getMessage());
            reportStatusService.setStatus(requestId, CDRReportFileStatus.CANNOT_GENERATE_FILE);
        } catch (NotFoundException e){
            log.info("Not found {}", e.getMessage());
            reportStatusService.setStatus(requestId, "Records with phone number " + phoneNumber + " not found for provided period");
        } catch (Exception e) {
            log.info("Unexpected error during CDR report generation: {}", e.getMessage());
            reportStatusService.setStatus(requestId, CDRReportFileStatus.UNEXPECTED_FAIL);
        }
    }
}
