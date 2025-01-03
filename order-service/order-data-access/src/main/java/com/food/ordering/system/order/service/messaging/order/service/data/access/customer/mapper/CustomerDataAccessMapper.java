package com.food.ordering.system.order.service.messaging.order.service.data.access.customer.mapper;

import com.food.ordering.system.order.service.messaging.order.service.data.access.customer.entity.CustomerEntity;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.valueobject.CustomerId;
import org.springframework.stereotype.Component;

@Component
public class CustomerDataAccessMapper {

    public Customer customerEntityToCostumer(CustomerEntity customerEntity){
        return new Customer(new CustomerId(customerEntity.getId()));
    }
}
