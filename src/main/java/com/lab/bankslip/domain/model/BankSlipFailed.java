package com.lab.bankslip.domain.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Table("bank_slip_failures")
@Data 
public class BankSlipFailed {
    @Id
    private Long id;
    private String payload;
    private String messageError;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String webhookUrl;
}
