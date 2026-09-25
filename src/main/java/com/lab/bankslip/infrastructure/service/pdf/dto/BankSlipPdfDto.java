package com.lab.bankslip.infrastructure.service.pdf.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data 
public class BankSlipPdfDto {
    private String institutionName;
    private String institutionDocument;
    private String bankCode;

    private String paymentPlace;
    private String ourNumber;

    private String beneficiaryName;
    private String beneficiaryDocument;
    private String agency;
    private String beneficiaryCode;

    private String payerName;
    private String payerDocument;
    private String documentNumber;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal interest;
    private BigDecimal chargedAmount;

    private String instructions;

    private String barcode;
    private String digitableLine;

    private String barcodeImage;

    private String customerId;
}
