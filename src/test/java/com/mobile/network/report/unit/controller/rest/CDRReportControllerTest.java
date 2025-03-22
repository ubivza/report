package com.mobile.network.report.unit.controller.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.mobile.network.report.controller.rest.CDRReportController;
import com.mobile.network.report.model.outer.CDRFileReportDto;
import com.mobile.network.report.service.api.CDRReportService;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CDRReportControllerTest {

    @Mock
    private CDRReportService cdrReportService;

    @InjectMocks
    private CDRReportController cdrReportController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateCDRCsvReport_Success() {
        String phoneNumber = "12345678901";
        Instant start = Instant.parse("2025-03-22T10:00:00Z");
        Instant end = Instant.parse("2025-03-22T12:00:00Z");

        CDRFileReportDto result = cdrReportController.generateCDRCsvReport(phoneNumber, start, end);

        assertNotNull(result);
        assertNotNull(result.getRequestId());
        assertTrue(result.getRequestId() instanceof UUID);

        verify(cdrReportService, times(1))
            .generateCDRReportAsync(eq(phoneNumber), eq(start), eq(end), any(UUID.class));
    }

    @Test
    void testGenerateCDRCsvReport_StartAfterEnd() {
        String phoneNumber = "12345678901";
        Instant start = Instant.parse("2025-03-22T12:00:00Z");
        Instant end = Instant.parse("2025-03-22T10:00:00Z");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cdrReportController.generateCDRCsvReport(phoneNumber, start, end);
        });

        assertEquals("Start of the period cannot be after end of the period", exception.getMessage());

        verify(cdrReportService, never()).generateCDRReportAsync(anyString(), any(), any(), any());
    }

    @Test
    void testGenerateCDRCsvReport_NullStart() {
        String phoneNumber = "12345678901";
        Instant start = null;
        Instant end = Instant.parse("2025-03-22T12:00:00Z");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            cdrReportController.generateCDRCsvReport(phoneNumber, start, end);
        });

        assertEquals("Start of the period cannot be after end of the period", exception.getMessage());

        verify(cdrReportService, never()).generateCDRReportAsync(anyString(), any(), any(), any());
    }
}