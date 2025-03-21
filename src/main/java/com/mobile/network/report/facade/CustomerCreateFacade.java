package com.mobile.network.report.facade;

import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Фасад создания и сохранения списка пользователей
 */
@Component
@RequiredArgsConstructor
public class CustomerCreateFacade {

    private final CustomerService service;

    public void generateAndSaveCustomers() {
        List<CustomerDto> customerDtos = service.generateCustomers();
        service.saveCustomers(customerDtos);
    }
}
