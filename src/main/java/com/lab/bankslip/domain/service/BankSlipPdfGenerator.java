package com.lab.bankslip.domain.service;

import com.lab.bankslip.domain.model.BankSlip;

public interface BankSlipPdfGenerator {
    String generateBase64(BankSlip bankSlip);
}
