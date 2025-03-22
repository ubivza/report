package com.mobile.network.report.integration.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobile.network.report.controller.rest.CustomerController;
import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CustomerService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCustomers_Success() throws Exception {
        CustomerDto customer1 = CustomerDto.builder().phoneNumber("12345678901").build();
        CustomerDto customer2 = CustomerDto.builder().phoneNumber("98765432109").build();
        List<CustomerDto> mockCustomers = Arrays.asList(customer1, customer2);
        when(service.getAllCustomers()).thenReturn(mockCustomers);

        mockMvc.perform(get("/api/report/customers/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(mockCustomers)));

        verify(service, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomers_EmptyList() throws Exception {
        when(service.getAllCustomers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/report/customers/all")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

        verify(service, times(1)).getAllCustomers();
    }
}
