package com.mobile.network.report.service.impl;

import com.mobile.network.report.db.entity.Customer;
import com.mobile.network.report.db.repository.CustomerRepository;
import com.mobile.network.report.mapper.CustomerMapper;
import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CustomerService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Класс отвечает за работу с пользователями
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public List<CustomerDto> generateCustomers() {
        return Arrays.asList(
            CustomerDto.builder().phoneNumber("98145678901").build(),
            CustomerDto.builder().phoneNumber("98145678902").build(),
            CustomerDto.builder().phoneNumber("98145678903").build(),
            CustomerDto.builder().phoneNumber("98145678904").build(),
            CustomerDto.builder().phoneNumber("98145678905").build(),
            CustomerDto.builder().phoneNumber("98145678906").build(),
            CustomerDto.builder().phoneNumber("98145678907").build(),
            CustomerDto.builder().phoneNumber("98145678908").build(),
            CustomerDto.builder().phoneNumber("98145678909").build(),
            CustomerDto.builder().phoneNumber("98145678910").build()
        );
    }

    @Override
    @Transactional
    public void saveCustomers(List<CustomerDto> customers) {
        List<Customer> entities = customers.stream()
            .map(mapper::toEntity)
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        return repository.findAll().stream()
            .map(mapper::toDto)
            .toList();
    }
}
