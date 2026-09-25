package com.lab.bankslip.infrastructure.persistence.jdbc;

import com.lab.bankslip.domain.model.BankSlipFailed;
import org.springframework.data.repository.CrudRepository;

public interface BankSlipFailedJdbcRepository extends CrudRepository<BankSlipFailed, Long> {
    
}
