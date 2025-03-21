package com.mobile.network.report.service.api;

import com.mobile.network.report.model.inner.CustomerDto;
import java.util.List;

public interface CustomerService {
    List<CustomerDto> generateCustomers();
    void saveCustomers(List<CustomerDto> customers);
    List<CustomerDto> getAllCustomers();
}
