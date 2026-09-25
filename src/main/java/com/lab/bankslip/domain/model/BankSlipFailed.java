package com.lab.bankslip.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("bank_slips")
@Data 
public class BankSlipFailed {
    String payload;
    String messageError;
    LocalDateTime createdAt = LocalDateTime.now();
    String webhookUrl;
}
