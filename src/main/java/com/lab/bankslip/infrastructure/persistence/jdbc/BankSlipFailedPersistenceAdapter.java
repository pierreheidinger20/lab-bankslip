package com.lab.bankslip.infrastructure.persistence.jdbc;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import com.lab.bankslip.domain.model.BankSlipFailed;
import com.lab.bankslip.domain.repository.BankSlipFailedRepository;

@Repository
@RequiredArgsConstructor
public class BankSlipFailedPersistenceAdapter
        implements BankSlipFailedRepository {

    private final BankSlipFailedJdbcRepository repository;

    @Override
    public void save(BankSlipFailed bankSlipFailed) {
        repository.save(bankSlipFailed);
    }

}
