package com.lab.bankslip.api;
import jakarta.validation.Valid;
import com.lab.bankslip.api.dto.BankSlipAcceptedResponseDto;
import com.lab.bankslip.api.dto.BankSlipRequestDto;
import com.lab.bankslip.application.command.EnqueueBankSlipCommand;
import com.lab.bankslip.application.command.Mediator;
import com.lab.bankslip.application.command.dto.BankSlipDto;
import com.lab.bankslip.application.command.dto.EnqueueBankSlipResultDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/bank-slips", headers = "X-API-Version=1")
@RequiredArgsConstructor
@Slf4j
public class BankSlipController {

    private final Mediator _mediator;

    @PostMapping
    public ResponseEntity<BankSlipAcceptedResponseDto> create( @Valid @RequestBody BankSlipRequestDto request) {
        log.info("Received request to create bank slip: {}", request);
        log.info("Validated request to create bank slip: {}", request);
        BankSlipDto bankSlipDto = BankSlipDto.builder()
                .customerId(request.customerId())
                .amount(request.amount())
                .dueDate(request.dueDate())
                .webhookUrl(request.webhookUrl())
                .payerName(request.payerName())
                .payerDocument(request.payerDocument())
                .beneficiaryName(request.beneficiaryName())
                .beneficiaryDocument(request.beneficiaryDocument())
                .documentNumber(request.documentNumber())
                .build();
        log.info("Created BankSlipDto: {}", bankSlipDto);
        EnqueueBankSlipResultDto result = _mediator.send(new EnqueueBankSlipCommand(bankSlipDto));
        log.info("EnqueueBankSlipResult received: {}", result);
        return ResponseEntity.accepted()
                .body(new BankSlipAcceptedResponseDto(result.requestId(), "accepted"));
    }

}
