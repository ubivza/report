package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.entity.CDRRecord;
import com.mobile.network.report.db.entity.CallType;
import com.mobile.network.report.db.repository.CDRRecordRepository;
import com.mobile.network.report.integration.rabbitmq.producer.CDRRecordSender;
import com.mobile.network.report.integration.rabbitmq.producer.api.CDRRecordMessage;
import com.mobile.network.report.integration.rabbitmq.producer.api.CDRReportMessage;
import com.mobile.network.report.mapper.CDRRecordMapper;
import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CDRRecordGeneratorService;
import com.mobile.network.report.service.api.CustomerService;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Класс генератор CDR записей по условию постановки
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CDRRecordGeneratorServiceImpl implements CDRRecordGeneratorService {

    private final CDRRecordRepository repository;
    private final CustomerService customerService;
    private final CDRRecordMapper mapper;
    private final CDRRecordSender cdrRecordSender;

    private final Random random = new Random();

    /**
     * Метод генерации CDR записей по номерам существубщих пользователей
     * @param year год за который создаются записи
     * @return список записей для последующего сохранения
     */
    //TODO rework sending to broker, and parallel generation
    @Override
    public List<CDRRecordDto> generateCDRRecords(int year) {
        AtomicInteger countMessages = new AtomicInteger();
        List<CDRRecordDto> generatedRecords = new ArrayList<>();
        List<CDRRecordDto> recordsToSend = new ArrayList<>();

        Instant startOfYear = Instant.parse(year + "-01-01T00:00:00Z");
        Instant endOfYear = Instant.parse(year + "-12-31T23:59:59Z");

        List<CustomerDto> customers = customerService.getAllCustomers();
        if (customers.size() < 10) {
            throw new IllegalStateException("Not enough customers");
        }

        int totalCalls = random.nextInt(500) + 100;
        long totalSecondsInYear = ChronoUnit.SECONDS.between(startOfYear, endOfYear);
        long averageIntervalSeconds = totalSecondsInYear / totalCalls;

        Instant currentTime = startOfYear;
        for (int i = 0; i < totalCalls; i++) {

            if (recordsToSend.size() == 10) {
                sendCDRRecord(recordsToSend);
                countMessages.incrementAndGet();
            }

            long intervalSeconds = averageIntervalSeconds + random.nextInt(3600) - 1800;
            currentTime = currentTime.plusSeconds(intervalSeconds);

            if (currentTime.isAfter(endOfYear)) {
                break;
            }

            List<CDRRecordDto> cdrRecords = generateCDRRecords(customers, currentTime);

            if (recordsToSend.size() == 9 && cdrRecords.size() == 2) {
                recordsToSend.add(cdrRecords.get(0));
                sendCDRRecord(recordsToSend);
                countMessages.incrementAndGet();
                recordsToSend.add(cdrRecords.get(1));
            } else {
                recordsToSend.addAll(cdrRecords);
            }

            generatedRecords.addAll(cdrRecords);
        }

        if (!recordsToSend.isEmpty()) {
            log.info("Records before last send {}", recordsToSend.size());
            sendCDRRecord(recordsToSend);
            log.info("Records at all {}", generatedRecords.size());
            log.info("Messages sent to broker {}", countMessages.incrementAndGet());
        }

        return generatedRecords;
    }

    @Override
    @Transactional
    public void saveRecords(List<CDRRecordDto> recordDtos) {
        List<CDRRecord> records = recordDtos.stream()
            .map(mapper::toEntity)
            .toList();
        repository.saveAll(records);
    }

    /**
     * Метод генерирует отдельно взятые CDR записи
     * @param customers список номеров пользователей
     * @param callStartTime сгенеренное в основном методе время начала звонка
     * @return единичную запись CDR с заполненными полями
     */
    private List<CDRRecordDto> generateCDRRecords(List<CustomerDto> customers, Instant callStartTime) {
        CustomerDto caller = customers.get(random.nextInt(customers.size()));
        CustomerDto receiver = customers.get(random.nextInt(customers.size()));

        while (receiver.equals(caller)) {
            receiver = customers.get(random.nextInt(customers.size()));
        }

        CallType callType = random.nextBoolean() ? CallType.INCOMING : CallType.OUTCOMING;

        long durationSeconds = random.nextInt(600 - 10 + 1) + 10;
        Instant callEndTime = callStartTime.plusSeconds(durationSeconds);

        ZonedDateTime callStartZoned = callStartTime.atZone(ZoneOffset.UTC);
        ZonedDateTime nextDayStart = callStartZoned.truncatedTo(ChronoUnit.DAYS).plusDays(1);

        if (callEndTime.isAfter(nextDayStart.toInstant()) && callStartTime.isBefore(nextDayStart.toInstant())) {
            CDRRecordDto cdrRecordDtoBeforeMidnight = CDRRecordDto.builder()
                .callType(callType)
                .callerPhoneNumber(caller.getPhoneNumber())
                .receiverPhoneNumber(receiver.getPhoneNumber())
                .callStartTime(callStartTime)
                .callEndTime(nextDayStart.toInstant())
                .build();

            CDRRecordDto cdrRecordDtoAfterMidnight = CDRRecordDto.builder()
                .callType(callType)
                .callerPhoneNumber(caller.getPhoneNumber())
                .receiverPhoneNumber(receiver.getPhoneNumber())
                .callStartTime(nextDayStart.toInstant())
                .callEndTime(callEndTime)
                .build();

            return List.of(cdrRecordDtoBeforeMidnight, cdrRecordDtoAfterMidnight);
        } else {
            return List.of(CDRRecordDto.builder()
                .callType(callType)
                .callerPhoneNumber(caller.getPhoneNumber())
                .receiverPhoneNumber(receiver.getPhoneNumber())
                .callStartTime(callStartTime)
                .callEndTime(callEndTime)
                .build());
        }
    }

    private void sendCDRRecord(List<CDRRecordDto> recordsToSend) {
        List<CDRRecordMessage> cdrRecordMessages = recordsToSend.stream()
            .map(mapper::toMessage)
            .toList();

        cdrRecordSender.send(CDRReportMessage.builder()
            .cdrRecords(cdrRecordMessages)
            .build());
        recordsToSend.clear();
    }
}
