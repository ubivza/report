package com.mobile.network.report.controller.rest;

import com.mobile.network.report.model.inner.CustomerDto;
import com.mobile.network.report.service.api.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер отвечает за пользователей
 */
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("${api.prefix.public}/customers")
public class CustomerController {

    private final CustomerService service;

    /**
     * Ендпоинт возвращает список номеров пользователей
     * @return список дто с номером пользователя
     */
    @GetMapping("/all")
    public List<CustomerDto> getCustomers() {

        return service.getAllCustomers();
    }
}
