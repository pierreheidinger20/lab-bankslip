package com.lab.bankslip.domain.service.dto;

public record BankSlipGeneratorDataDto(
        String barcode,
        String digitableLine
) {
}