package com.lab.bankslip.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.lab.bankslip.infrastructure.exception.BusinessException;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table("bank_slips")
@Data
public class BankSlip {

    @Id
    private Long id;
    private String customerId;
    private String barcode;
    private String digitableLine;
    private BigDecimal amount;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    private String base64file;
    private String webhookUrl;
    private String bankCode;
    private String beneficiaryName;
    private String beneficiaryDocument;
    private String agency;
    private String beneficiaryCode;
    private String documentNumber;
    private String payerName;
    private String payerDocument;
    private BigDecimal discount;
    private BigDecimal interest;
    private BigDecimal chargedAmount;
    private LocalDate issueDate;
    private boolean webhookNotified = false;

    private BankSlip() {
    }

    public static BankSlip create(
            String customerId,
            String payerName,
            String payerDocument,
            String beneficiaryName,
            String beneficiaryDocument,
            String documentNumber,
            BigDecimal amount,
            LocalDate dueDate,
            String webhookUrl) {

        validateCustomerId(customerId);

        BankSlip bankSlip = new BankSlip();

        bankSlip.customerId = customerId;
        bankSlip.payerName = payerName;
        bankSlip.payerDocument = payerDocument;
        bankSlip.beneficiaryName = beneficiaryName;
        bankSlip.beneficiaryDocument = beneficiaryDocument;
        bankSlip.documentNumber = documentNumber;
        bankSlip.amount = amount;
        bankSlip.dueDate = dueDate;
        bankSlip.webhookUrl = webhookUrl;
        bankSlip.createdAt = LocalDateTime.now();

        return bankSlip;
    }

    private static void validateCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new BusinessException("Customer ID is required");
        }
    }

}