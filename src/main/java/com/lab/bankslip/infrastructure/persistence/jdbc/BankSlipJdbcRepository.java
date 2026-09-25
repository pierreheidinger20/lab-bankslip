package com.lab.bankslip.infrastructure.persistence.jdbc;

import org.springframework.data.repository.CrudRepository;

import com.lab.bankslip.domain.model.BankSlip;

public interface BankSlipJdbcRepository
        extends CrudRepository<BankSlip, Long> {
}