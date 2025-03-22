package com.mobile.network.report.unit.controller.rest;

import com.mobile.network.report.controller.rest.UDRReportController;
import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.service.api.UDRReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UDRReportControllerTest {

    @Mock
    private UDRReportService service;

    @InjectMocks
    private UDRReportController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserUDRRecord_FullPeriod_Success() {
        String phoneNumber = "12345678901";
        boolean fullPeriod = true;
        UDRReportDto.CallDuration incoming = new UDRReportDto.CallDuration();
        incoming.setTotalTime(Duration.ofHours(1).plusMinutes(30));
        UDRReportDto.CallDuration outcoming = new UDRReportDto.CallDuration();
        outcoming.setTotalTime(Duration.ofHours(2).plusMinutes(15));
        UDRReportDto mockDto = UDRReportDto.builder()
            .msisdn(phoneNumber)
            .incomingCall(incoming)
            .outcomingCall(outcoming)
            .build();
        when(service.getUDRForCustomerFullPeriod(phoneNumber)).thenReturn(mockDto);

        UDRReportDto result = controller.getUserUDRRecord(phoneNumber, null, fullPeriod);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getMsisdn());
        assertEquals("01:30:00", result.getIncomingCall().getTotalTime());
        assertEquals("02:15:00", result.getOutcomingCall().getTotalTime());
        verify(service, times(1)).getUDRForCustomerFullPeriod(phoneNumber);
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetUserUDRRecord_ByMonth_Success() {
        String phoneNumber = "12345678901";
        YearMonth month = YearMonth.of(2025, 3);
        UDRReportDto.CallDuration incoming = new UDRReportDto.CallDuration();
        incoming.setTotalTime(Duration.ofMinutes(45));
        UDRReportDto.CallDuration outcoming = new UDRReportDto.CallDuration();
        outcoming.setTotalTime(Duration.ofHours(1));
        UDRReportDto mockDto = UDRReportDto.builder()
            .msisdn(phoneNumber)
            .incomingCall(incoming)
            .outcomingCall(outcoming)
            .build();
        when(service.getUDRForCustomerByMonth(phoneNumber, month)).thenReturn(mockDto);

        UDRReportDto result = controller.getUserUDRRecord(phoneNumber, month, false);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getMsisdn());
        assertEquals("00:45:00", result.getIncomingCall().getTotalTime());
        assertEquals("01:00:00", result.getOutcomingCall().getTotalTime());
        verify(service, times(1)).getUDRForCustomerByMonth(phoneNumber, month);
        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
    }

    @Test
    void testGetUserUDRRecord_MonthAndFullPeriod() {
        String phoneNumber = "12345678901";
        YearMonth month = YearMonth.of(2025, 3);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.getUserUDRRecord(phoneNumber, month, true);
        });
        assertEquals("Cannot generate report for both 'month' and 'fullPeriod'", ex.getMessage());
        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetUserUDRRecord_NoPeriodProvided() {
        String phoneNumber = "12345678901";

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.getUserUDRRecord(phoneNumber, null, false);
        });
        assertEquals("Report period not provided", ex.getMessage());
        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetAllUDRRecord_Success() {
        YearMonth month = YearMonth.of(2025, 3);
        UDRReportDto.CallDuration incoming1 = new UDRReportDto.CallDuration();
        incoming1.setTotalTime(Duration.ofHours(1));
        UDRReportDto.CallDuration outcoming1 = new UDRReportDto.CallDuration();
        outcoming1.setTotalTime(Duration.ofHours(2));
        UDRReportDto.CallDuration incoming2 = new UDRReportDto.CallDuration();
        incoming2.setTotalTime(Duration.ofMinutes(30));
        UDRReportDto.CallDuration outcoming2 = new UDRReportDto.CallDuration();
        outcoming2.setTotalTime(Duration.ofHours(1).plusMinutes(30));
        List<UDRReportDto> mockList = Arrays.asList(
            UDRReportDto.builder()
                .msisdn("12345678901")
                .incomingCall(incoming1)
                .outcomingCall(outcoming1)
                .build(),
            UDRReportDto.builder()
                .msisdn("98765432109")
                .incomingCall(incoming2)
                .outcomingCall(outcoming2)
                .build()
        );
        when(service.getUDRForAllCustomersByMonth(month)).thenReturn(mockList);

        List<UDRReportDto> result = controller.getAllUDRRecord(month);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12345678901", result.get(0).getMsisdn());
        assertEquals("01:00:00", result.get(0).getIncomingCall().getTotalTime());
        assertEquals("98765432109", result.get(1).getMsisdn());
        assertEquals("01:30:00", result.get(1).getOutcomingCall().getTotalTime());
        verify(service, times(1)).getUDRForAllCustomersByMonth(month);
    }

    @Test
    void testGetAllUDRRecord_NullMonth() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            controller.getAllUDRRecord(null);
        });
        assertEquals("Report period not provided", ex.getMessage());
        verify(service, never()).getUDRForAllCustomersByMonth(any());
    }
}