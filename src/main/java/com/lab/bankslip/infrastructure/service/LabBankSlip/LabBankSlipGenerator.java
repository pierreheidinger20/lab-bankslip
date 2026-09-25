package com.lab.bankslip.infrastructure.service.LabBankSlip;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.lab.bankslip.domain.service.BankSlipGenerator;
import com.lab.bankslip.domain.service.dto.BankSlipGeneratorDataDto;
import com.lab.bankslip.domain.model.BankSlip;

@Service
@RequiredArgsConstructor
public class LabBankSlipGenerator implements BankSlipGenerator {

    private final BarcodeCalculator barcodeCalculator;
    private final DigitableLineCalculator digitableLineCalculator;

    @Override
    public BankSlipGeneratorDataDto generate(BankSlip bankSlip) {
        String barcode = barcodeCalculator.calculate(bankSlip);
        String digitableLine = digitableLineCalculator.calculate(barcode);
        return new BankSlipGeneratorDataDto(
                barcode,
                digitableLine);
    }
}
