package com.mobile.network.report.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.network.report.controller.rest.CDRReportController;
import com.mobile.network.report.service.api.CDRReportService;
import com.mobile.network.report.service.api.ReportStatusService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CDRReportController.class)
class CDRReportControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CDRReportService cdrReportService;
    @MockitoBean
    private ReportStatusService reportStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    private static String phoneNumber;
    private static String invalidPhoneNumber;
    private static Instant start;
    private static Instant end;

    @BeforeAll
    static void setUp() {
        phoneNumber = "12345678901";
        invalidPhoneNumber = "12345";
        start = Instant.parse("2024-03-22T10:00:00Z");
        end = Instant.parse("2024-03-22T12:00:00Z");
    }

    @Test
    void testGenerateCDRCsvReport_Success() throws Exception {
        mockMvc.perform(post("/api/report/cdr/{phoneNumber}", phoneNumber)
                .param("start", start.toString())
                .param("end", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestId").exists())
            .andExpect(jsonPath("$.requestId").isString());

        verify(cdrReportService, times(1))
            .generateCDRReportAsync(eq(phoneNumber), eq(start), eq(end), any(UUID.class));
    }

    @Test
    void testGenerateCDRCsvReport_InvalidPhoneNumber() throws Exception {
        mockMvc.perform(post("/api/report/cdr/{phoneNumber}", invalidPhoneNumber)
                .param("start", start.toString())
                .param("end", end.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Phone number must be exactly 11 digits"));

        verify(cdrReportService, never()).generateCDRReportAsync(anyString(), any(), any(), any());
    }

    @Test
    void testGenerateCDRCsvReport_StartAfterEnd() throws Exception {
        mockMvc.perform(post("/api/report/cdr/{phoneNumber}", phoneNumber)
                .param("start", end.toString())
                .param("end", start.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Start of the period cannot be after end of the period"));

        verify(cdrReportService, never()).generateCDRReportAsync(anyString(), any(), any(), any());
    }
}