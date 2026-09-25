package com.lab.bankslip.infrastructure.persistence.jdbc;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;

import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.repository.BankSlipRepository;

@Repository
@RequiredArgsConstructor
public class BankSlipPersistenceAdapter
        implements BankSlipRepository {

    private final BankSlipJdbcRepository repository;

    @Override
    public BankSlip save(BankSlip bankSlip) {
        return repository.save(bankSlip);
    }

    @Override
    public Optional<BankSlip> findById(Long id) {
        return repository.findById(id);
    }
}
