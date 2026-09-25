package com.lab.bankslip.domain.service;
import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.service.dto.BankSlipGeneratorDataDto;

public interface BankSlipGenerator {
    BankSlipGeneratorDataDto generate(BankSlip bankSlip);
}
