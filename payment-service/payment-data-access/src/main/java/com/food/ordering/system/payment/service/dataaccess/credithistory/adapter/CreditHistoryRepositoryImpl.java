package com.food.ordering.system.payment.service.dataaccess.credithistory.adapter;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity;
import com.food.ordering.system.payment.service.dataaccess.credithistory.mapper.CreditHistoryDataAccessMapper;
import com.food.ordering.system.payment.service.dataaccess.credithistory.repository.CreditHistoryJpaRepository;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                       CreditHistoryDataAccessMapper creditHistoryDataAccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
    }


    @Override
    public CreditHistory save(CreditHistory creditHistory) {

        CreditHistoryEntity creditHistoryEntity = creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory);

        return creditHistoryDataAccessMapper.creditHistoryEntityToCreditHistory(creditHistoryJpaRepository.save(creditHistoryEntity));
    }

    @Override
    public Optional<List<CreditHistory>> findByCustomerId(CustomerId customerId) {

        return creditHistoryJpaRepository.findByCustomerId(customerId.getValue())
                .map(creditHistoryList -> creditHistoryList
                        .stream()
                        .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                        .collect(Collectors.toList())
                );
    }
}
