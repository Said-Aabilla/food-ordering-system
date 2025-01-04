package com.food.ordering.system.order.service.data.access.customer.repository;

import com.food.ordering.system.order.service.data.access.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID>  {
    Optional<CustomerEntity> findCustomerEntitiesById(UUID id);
}
