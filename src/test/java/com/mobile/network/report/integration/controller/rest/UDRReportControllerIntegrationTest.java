package com.mobile.network.report.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.network.report.controller.rest.UDRReportController;
import com.mobile.network.report.model.outer.UDRReportDto;
import com.mobile.network.report.service.api.UDRReportService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.YearMonth;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UDRReportController.class)
class UDRReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UDRReportService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUserUDRRecord_FullPeriod_Success() throws Exception {
        String phoneNumber = "12345678901";
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

        mockMvc.perform(get("/api/report/udr/{phoneNumber}", phoneNumber)
                .param("fullPeriod", "true")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.msisdn").value(phoneNumber))
            .andExpect(jsonPath("$.incomingCall.totalTime").value("01:30:00"))
            .andExpect(jsonPath("$.outcomingCall.totalTime").value("02:15:00"));
        verify(service, times(1)).getUDRForCustomerFullPeriod(phoneNumber);
    }

    @Test
    void testGetUserUDRRecord_ByMonth_Success() throws Exception {
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

        mockMvc.perform(get("/api/report/udr/{phoneNumber}", phoneNumber)
                .param("month", "2025-03")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.msisdn").value(phoneNumber))
            .andExpect(jsonPath("$.incomingCall.totalTime").value("00:45:00"))
            .andExpect(jsonPath("$.outcomingCall.totalTime").value("01:00:00"));

        verify(service, times(1)).getUDRForCustomerByMonth(phoneNumber, month);
    }

    @Test
    void testGetUserUDRRecord_InvalidPhoneNumber() throws Exception {
        String invalidPhoneNumber = "12345";

        mockMvc.perform(get("/api/report/udr/{phoneNumber}", invalidPhoneNumber)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetUserUDRRecord_MonthAndFullPeriod() throws Exception {
        String phoneNumber = "12345678901";

        mockMvc.perform(get("/api/report/udr/{phoneNumber}", phoneNumber)
                .param("month", "2025-03")
                .param("fullPeriod", "true")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Cannot generate report for both 'month' and 'fullPeriod'"));

        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetUserUDRRecord_NoPeriodProvided() throws Exception {
        String phoneNumber = "12345678901";

        mockMvc.perform(get("/api/report/udr/{phoneNumber}", phoneNumber)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Report period not provided"));

        verify(service, never()).getUDRForCustomerFullPeriod(anyString());
        verify(service, never()).getUDRForCustomerByMonth(anyString(), any());
    }

    @Test
    void testGetAllUDRRecord_Success() throws Exception {
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

        mockMvc.perform(get("/api/report/udr/all")
                .param("month", "2025-03")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].msisdn").value("12345678901"))
            .andExpect(jsonPath("$[0].incomingCall.totalTime").value("01:00:00"))
            .andExpect(jsonPath("$[1].msisdn").value("98765432109"))
            .andExpect(jsonPath("$[1].outcomingCall.totalTime").value("01:30:00"));

        verify(service, times(1)).getUDRForAllCustomersByMonth(month);
    }

    @Test
    void testGetAllUDRRecord_NoMonthProvided() throws Exception {
        mockMvc.perform(get("/api/report/udr/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Required request parameter 'month' for method parameter type YearMonth is not present"));

        verify(service, never()).getUDRForAllCustomersByMonth(any());
    }
}
