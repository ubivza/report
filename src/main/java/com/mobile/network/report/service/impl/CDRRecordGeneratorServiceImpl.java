package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.entity.CDRRecord;
import com.mobile.network.report.db.entity.CallType;
import com.mobile.network.report.db.repository.CDRRecordRepository;
import com.mobile.network.report.mapper.CDRRecordMapper;
import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CDRRecordGeneratorService;
import com.mobile.network.report.service.api.CustomerService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Класс генератор CDR записей по условию постановки
 */
@Service
@RequiredArgsConstructor
public class CDRRecordGeneratorServiceImpl implements CDRRecordGeneratorService {

    private final CDRRecordRepository repository;
    private final CustomerService customerService;
    private final CDRRecordMapper mapper;

    private final Random random = new Random();

    /**
     * Метод генерации CDR записей по номерам существубщих пользователей
     * @param year год за который создаются записи
     * @return список записей для последующего сохранения
     */
    @Override
    public List<CDRRecordDto> generateCDRRecords(int year) {
        List<CDRRecordDto> generatedRecords = new ArrayList<>();

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
            long intervalSeconds = averageIntervalSeconds + random.nextInt(3600) - 1800;
            currentTime = currentTime.plusSeconds(intervalSeconds);

            if (currentTime.isAfter(endOfYear)) {
                break;
            }

            CDRRecordDto cdrRecord = generateSingleCDRRecord(customers, currentTime);
            generatedRecords.add(cdrRecord);
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
    private CDRRecordDto generateSingleCDRRecord(List<CustomerDto> customers, Instant callStartTime) {
        CustomerDto caller = customers.get(random.nextInt(customers.size()));
        CustomerDto receiver = customers.get(random.nextInt(customers.size()));

        while (receiver.equals(caller)) {
            receiver = customers.get(random.nextInt(customers.size()));
        }

        CallType callType = random.nextBoolean() ? CallType.INCOMING : CallType.OUTCOMING;

        long durationSeconds = random.nextInt(600 - 10 + 1) + 10;
        Instant callEndTime = callStartTime.plusSeconds(durationSeconds);

        return CDRRecordDto.builder()
            .callType(callType)
            .callerPhoneNumber(caller.getPhoneNumber())
            .receiverPhoneNumber(receiver.getPhoneNumber())
            .callStartTime(callStartTime)
            .callEndTime(callEndTime)
            .build();
    }
}
