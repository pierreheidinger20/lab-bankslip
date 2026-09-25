package com.lab.bankslip.domain.repository;

import java.util.Optional;

import com.lab.bankslip.domain.model.BankSlip;

public interface BankSlipRepository {

    BankSlip save(BankSlip bankSlip);

    Optional<BankSlip> findById(Long id);
}