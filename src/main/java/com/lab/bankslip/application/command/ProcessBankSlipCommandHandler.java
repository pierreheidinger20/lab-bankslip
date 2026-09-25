package com.lab.bankslip.application.command;

import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.model.BankSlipFailed;
import com.lab.bankslip.domain.repository.BankSlipFailedRepository;
import com.lab.bankslip.domain.repository.BankSlipRepository;
import com.lab.bankslip.domain.service.BankSlipGenerator;
import com.lab.bankslip.domain.service.BankSlipPdfGenerator;
import com.lab.bankslip.domain.service.BankSlipWebhook;
import com.lab.bankslip.domain.service.dto.BankSlipGeneratorDataDto;
import com.lab.bankslip.infrastructure.exception.BusinessException;
import com.lab.bankslip.application.command.dto.SaveBankSlipDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessBankSlipCommandHandler implements CommandHandler<ProcessBankSlipCommand, BankSlip> {

    private final BankSlipRepository _bankSlipRepository;
    private final BankSlipGenerator _bankSlipGenerator;
    private final BankSlipPdfGenerator _bankSlipPdfGenerator;
    private final BankSlipWebhook _bankSlipWebhook;
    private final BankSlipFailedRepository _bankSlipFailedRepository;

    @Override
    @Retryable(retryFor = Exception.class, maxAttempts = 3)
    public BankSlip handle(ProcessBankSlipCommand command) {
        try {
            log.info("starting to process ProcessBankSlipCommand");
            SaveBankSlipDto saveBankSlipDto = command.saveBankSlipDto();
            BankSlip bankSlip = saveBankSlipFromDto(saveBankSlipDto);
            log.info("generating barcode,digitableLine and pdf");
            BankSlipGeneratorDataDto bankSlipData = _bankSlipGenerator.generate(bankSlip);
            bankSlip.setBarcode(bankSlipData.barcode());
            bankSlip.setDigitableLine(bankSlipData.digitableLine());
            String pdfBase64 = _bankSlipPdfGenerator.generateBase64(bankSlip);
            bankSlip.setBase64file(pdfBase64);
            sendBankSlipWebhook(bankSlip);
            _bankSlipRepository.save(bankSlip);
            return bankSlip;
        } catch (BusinessException e) {
            processFailedBankSlip(command.saveBankSlipDto(), e.getMessage());
            log.error("Failed to process BankSlip command: {}", command, e);
            throw e;
        } catch (Exception e) {
            processFailedBankSlip(command.saveBankSlipDto(), "Error occurred while processing BankSlip");
            log.error("Failed to process BankSlip command: {}", command, e);
            throw e;
        }
        // Here you can you use another specific exception handling if needed
    }

    private BankSlip saveBankSlipFromDto(SaveBankSlipDto dto) {
        log.info("saving BankSlip from DTO");
        BankSlip bankSlip = BankSlip.create(
                dto.getCustomerId(),
                dto.getPayerName(),
                dto.getPayerDocument(),
                dto.getBeneficiaryName(),
                dto.getBeneficiaryDocument(),
                dto.getDocumentNumber(),
                dto.getAmount(),
                dto.getDueDate(),
                dto.getWebhookUrl());
        log.info("Created BankSlip: {}", bankSlip);
        _bankSlipRepository.save(bankSlip);
        log.info("BankSlip saved");
        return bankSlip;
    }

    private void sendBankSlipWebhook(BankSlip bankSlip) {
        try {
            _bankSlipWebhook.notify(bankSlip);
            bankSlip.setWebhookNotified(Boolean.TRUE);
        } catch (Exception e) {
            log.error("Failed to send BankSlip webhook for BankSlip: {}", bankSlip, e);
        }
    }

    private BankSlipFailed saveFailedBankSlip(SaveBankSlipDto saveBankSlipDto, String messageError) {
        BankSlipFailed bankSlipFailed = new BankSlipFailed();
        bankSlipFailed.setPayload(saveBankSlipDto.toString());
        bankSlipFailed.setMessageError(messageError);
        log.info("Created BankSlipFailed: {}", bankSlipFailed);
        _bankSlipFailedRepository.save(bankSlipFailed);
        return bankSlipFailed;
    }

    private void processFailedBankSlip(SaveBankSlipDto saveBankSlipDto, String messageError) {
        BankSlipFailed bankSlipFailed = saveFailedBankSlip(saveBankSlipDto, messageError);
        _bankSlipWebhook.notifyFailure(bankSlipFailed, messageError);
    }
}
