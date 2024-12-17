package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
        Optional<Customer> findCustomer(UUID uuid);
}
