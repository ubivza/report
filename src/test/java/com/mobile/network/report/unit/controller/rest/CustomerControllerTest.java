package com.mobile.network.report.unit.controller.rest;

import com.mobile.network.report.controller.rest.CustomerController;
import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CustomerService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService service;

    @InjectMocks
    private CustomerController customerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCustomers_Success() {
        CustomerDto customer1 = CustomerDto.builder().phoneNumber("12345678901").build();
        CustomerDto customer2 = CustomerDto.builder().phoneNumber("98765432109").build();
        List<CustomerDto> mockCustomers = Arrays.asList(customer1, customer2);
        when(service.getAllCustomers()).thenReturn(mockCustomers);

        List<CustomerDto> result = customerController.getCustomers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12345678901", result.get(0).getPhoneNumber());
        assertEquals("98765432109", result.get(1).getPhoneNumber());

        verify(service, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomers_EmptyList() {
        when(service.getAllCustomers()).thenReturn(Collections.emptyList());

        List<CustomerDto> result = customerController.getCustomers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(service, times(1)).getAllCustomers();
    }

    @Test
    void testGetCustomers_ServiceThrowsException() {
        when(service.getAllCustomers()).thenThrow(new RuntimeException("Service error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerController.getCustomers();
        });
        assertEquals("Service error", exception.getMessage());

        verify(service, times(1)).getAllCustomers();
    }
}