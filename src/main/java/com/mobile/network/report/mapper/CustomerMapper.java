package com.mobile.network.report.mapper;

import com.mobile.network.report.db.entity.Customer;
import com.mobile.network.report.model.inner.CustomerDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDto toDto(Customer entity);
    Customer toEntity(CustomerDto dto);
}
