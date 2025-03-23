package com.mobile.network.report.unit.service.impl;

import com.mobile.network.report.db.entity.CallType;
import com.mobile.network.report.model.inner.CDRRecordDto;
import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.service.impl.UDRReportFormatterServiceImpl;
import com.mobile.network.report.utils.DateTimeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class UDRReportFormatterServiceTest {

    private UDRReportFormatterServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UDRReportFormatterServiceImpl();
    }

    @Test
    void testBuildUDRResponse_Success() {
        String phoneNumber = "12345678901";
        CDRRecordDto incomingRecord = createCDRRecordDto(
            CallType.INCOMING,
            "98765432109",
            phoneNumber,
            "2025-01-01T10:00:00Z",
            "2025-01-01T10:01:00Z");
        CDRRecordDto outcomingRecord = createCDRRecordDto(
            CallType.OUTCOMING,
            phoneNumber,
            "98765432109",
            "2025-01-01T11:00:00Z",
            "2025-01-01T11:02:00Z");

        List<CDRRecordDto> records = Arrays.asList(incomingRecord, outcomingRecord);

        try (MockedStatic<DateTimeUtils> utilities = Mockito.mockStatic(DateTimeUtils.class)) {
            utilities.when(() -> DateTimeUtils.calculateCallDuration(
                    incomingRecord.getCallStartTime(), incomingRecord.getCallEndTime()))
                .thenReturn(Duration.ofMinutes(1));
            utilities.when(() -> DateTimeUtils.calculateCallDuration(
                    outcomingRecord.getCallStartTime(), outcomingRecord.getCallEndTime()))
                .thenReturn(Duration.ofMinutes(2));

            UDRReportDto result = service.buildUDRResponse(phoneNumber, records);

            assertNotNull(result);
            assertEquals(phoneNumber, result.getMsisdn());
            assertEquals("00:01:00", result.getIncomingCall().getTotalTime());
            assertEquals("00:02:00", result.getOutcomingCall().getTotalTime());
        }
    }

    @Test
    void testBuildUDRResponse_EmptyRecords() {
        String phoneNumber = "12345678901";
        List<CDRRecordDto> records = Collections.emptyList();

        UDRReportDto result = service.buildUDRResponse(phoneNumber, records);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getMsisdn());
        assertEquals("00:00:00", result.getIncomingCall().getTotalTime());
        assertEquals("00:00:00", result.getOutcomingCall().getTotalTime());
    }

    @Test
    void testBuildUDRResponse_NullDuration() {
        String phoneNumber = "12345678901";
        CDRRecordDto record = createCDRRecordDto(
            CallType.INCOMING,
            "98765432109",
            phoneNumber,
            "2025-01-01T10:00:00Z",
            "2025-01-01T10:01:00Z");

        List<CDRRecordDto> records = List.of(record);

        try (MockedStatic<DateTimeUtils> utilities = Mockito.mockStatic(DateTimeUtils.class)) {
            utilities.when(() -> DateTimeUtils.calculateCallDuration(any(), any()))
                .thenReturn(null);

            UDRReportDto result = service.buildUDRResponse(phoneNumber, records);

            assertNotNull(result);
            assertEquals(phoneNumber, result.getMsisdn());
            assertEquals("00:00:00", result.getIncomingCall().getTotalTime());
            assertEquals("00:00:00", result.getOutcomingCall().getTotalTime());
        }
    }

    @Test
    void testBuildAllUDRResponse_Success() {
        List<CDRRecordDto> records = createCDRRecordsForAllResponse();

        try (MockedStatic<DateTimeUtils> utilities = Mockito.mockStatic(DateTimeUtils.class)) {
            utilities.when(() -> DateTimeUtils.calculateCallDuration(
                    records.get(0).getCallStartTime(), records.get(0).getCallEndTime()))
                .thenReturn(Duration.ofMinutes(1));
            utilities.when(() -> DateTimeUtils.calculateCallDuration(
                    records.get(1).getCallStartTime(), records.get(1).getCallEndTime()))
                .thenReturn(Duration.ofMinutes(2));

            List<UDRReportDto> result = service.buildAllUDRResponse(records);

            assertNotNull(result);
            assertEquals(2, result.size());

            UDRReportDto report1 = result.stream()
                .filter(dto -> dto.getMsisdn().equals("12345678901"))
                .findFirst()
                .orElse(null);
            assertNotNull(report1);
            assertEquals("00:01:00", report1.getIncomingCall().getTotalTime());
            assertEquals("00:00:00", report1.getOutcomingCall().getTotalTime());

            UDRReportDto report2 = result.stream()
                .filter(dto -> dto.getMsisdn().equals("98765432109"))
                .findFirst()
                .orElse(null);
            assertNotNull(report2);
            assertEquals("00:00:00", report2.getIncomingCall().getTotalTime());
            assertEquals("00:02:00", report2.getOutcomingCall().getTotalTime());
        }
    }

    @Test
    void testBuildAllUDRResponse_EmptyRecords() {
        List<CDRRecordDto> records = Collections.emptyList();

        List<UDRReportDto> result = service.buildAllUDRResponse(records);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testBuildAllUDRResponse_NullDuration() {
        CDRRecordDto record = createCDRRecordDto(
            CallType.INCOMING,
            "98765432109",
            "12345678901",
            "2025-01-01T10:00:00Z",
            "2025-01-01T10:01:00Z");

        List<CDRRecordDto> records = List.of(record);

        try (MockedStatic<DateTimeUtils> utilities = Mockito.mockStatic(DateTimeUtils.class)) {
            utilities.when(() -> DateTimeUtils.calculateCallDuration(any(), any()))
                .thenReturn(null);

            List<UDRReportDto> result = service.buildAllUDRResponse(records);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    private CDRRecordDto createCDRRecordDto(CallType callType, String callerPhoneNumber, String receiverPhoneNumber,
        String startTime, String endTime) {
        return CDRRecordDto.builder()
            .callType(callType)
            .callerPhoneNumber(callerPhoneNumber)
            .receiverPhoneNumber(receiverPhoneNumber)
            .callStartTime(Instant.parse(startTime))
            .callEndTime(Instant.parse(endTime))
            .build();
    }

    private List<CDRRecordDto> createCDRRecordsForAllResponse() {
        CDRRecordDto incomingRecord = createCDRRecordDto(
            CallType.INCOMING,
            "55555555555",
            "12345678901",
            "2025-01-01T10:00:00Z",
            "2025-01-01T10:01:00Z");
        CDRRecordDto outcomingRecord = createCDRRecordDto(
            CallType.OUTCOMING,
            "98765432109",
            "55555555555",
            "2025-01-01T11:00:00Z",
            "2025-01-01T11:02:00Z");
        return Arrays.asList(incomingRecord, outcomingRecord);
    }
}