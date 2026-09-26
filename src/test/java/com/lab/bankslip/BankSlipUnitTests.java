package com.lab.bankslip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.lab.bankslip.application.command.ProcessBankSlipCommand;
import com.lab.bankslip.application.command.ProcessBankSlipCommandHandler;
import com.lab.bankslip.application.command.dto.SaveBankSlipDto;
import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.domain.model.BankSlipFailed;
import com.lab.bankslip.domain.repository.BankSlipFailedRepository;
import com.lab.bankslip.domain.repository.BankSlipRepository;
import com.lab.bankslip.domain.service.BankSlipGenerator;
import com.lab.bankslip.domain.service.BankSlipPdfGenerator;
import com.lab.bankslip.domain.service.BankSlipWebhook;
import com.lab.bankslip.domain.service.dto.BankSlipGeneratorDataDto;
import com.lab.bankslip.infrastructure.exception.BusinessException;

class BankSlipUnitTests {

    @Test
    void createCopiesRequestFieldsAndInitializesState() {
        LocalDate dueDate = LocalDate.of(2026, 10, 15);

        BankSlip bankSlip = BankSlip.create(
                "customer-123",
                "Payer Name",
                "payer-doc",
                "Beneficiary Name",
                "beneficiary-doc",
                "doc-456",
                new BigDecimal("125.50"),
                dueDate,
                "https://example.com/webhook");

        assertEquals("customer-123", bankSlip.getCustomerId());
        assertEquals("Payer Name", bankSlip.getPayerName());
        assertEquals("payer-doc", bankSlip.getPayerDocument());
        assertEquals("Beneficiary Name", bankSlip.getBeneficiaryName());
        assertEquals("beneficiary-doc", bankSlip.getBeneficiaryDocument());
        assertEquals("doc-456", bankSlip.getDocumentNumber());
        assertEquals(new BigDecimal("125.50"), bankSlip.getAmount());
        assertEquals(dueDate, bankSlip.getDueDate());
        assertEquals("https://example.com/webhook", bankSlip.getWebhookUrl());
        assertNotNull(bankSlip.getCreatedAt());
        assertFalse(bankSlip.isWebhookNotified());
    }

    @Test
    void createRejectsBlankCustomerId() {
        assertThrows(BusinessException.class, () -> BankSlip.create(
                "  ",
                "Payer Name",
                "payer-doc",
                "Beneficiary Name",
                "beneficiary-doc",
                "doc-456",
                new BigDecimal("125.50"),
                LocalDate.of(2026, 10, 15),
                null));
    }

    @Test
    void handlerGeneratesAndNotifiesBankSlip() {
        InMemoryBankSlipRepository bankSlipRepository = new InMemoryBankSlipRepository();
        AtomicReference<BankSlip> notifiedBankSlip = new AtomicReference<>();
        BankSlipGenerator generator = bankSlip -> new BankSlipGeneratorDataDto("barcode", "digitable-line");
        BankSlipPdfGenerator pdfGenerator = bankSlip -> "pdf-base64";
        BankSlipWebhook webhook = new BankSlipWebhook() {
            @Override
            public void notify(BankSlip bankSlip) {
                notifiedBankSlip.set(bankSlip);
            }

            @Override
            public void notifyFailure(BankSlipFailed bankSlipFailed, String error) {
                throw new AssertionError("Failure callback should not be called on success");
            }
        };
        BankSlipFailedRepository failedRepository = bankSlipFailed -> {
        };
        ProcessBankSlipCommandHandler handler = new ProcessBankSlipCommandHandler(
                bankSlipRepository,
                generator,
                pdfGenerator,
                webhook,
                failedRepository);

        BankSlip result = handler.handle(new ProcessBankSlipCommand(sampleRequest()));

        assertEquals(42L, result.getId());
        assertEquals("barcode", result.getBarcode());
        assertEquals("digitable-line", result.getDigitableLine());
        assertEquals("pdf-base64", result.getBase64file());
        assertTrue(result.isWebhookNotified());
        assertSame(result, notifiedBankSlip.get());
        assertSame(result, bankSlipRepository.savedBankSlip);
    }

    private static SaveBankSlipDto sampleRequest() {
        SaveBankSlipDto request = new SaveBankSlipDto();
        request.setCustomerId("customer-123");
        request.setPayerName("Payer Name");
        request.setPayerDocument("payer-doc");
        request.setBeneficiaryName("Beneficiary Name");
        request.setBeneficiaryDocument("beneficiary-doc");
        request.setDocumentNumber("doc-456");
        request.setAmount(new BigDecimal("125.50"));
        request.setDueDate(LocalDate.of(2026, 10, 15));
        request.setWebhookUrl("https://example.com/webhook");
        return request;
    }

    private static final class InMemoryBankSlipRepository implements BankSlipRepository {
        private BankSlip savedBankSlip;

        @Override
        public BankSlip save(BankSlip bankSlip) {
            if (bankSlip.getId() == null) {
                bankSlip.setId(42L);
            }
            savedBankSlip = bankSlip;
            return bankSlip;
        }

        @Override
        public Optional<BankSlip> findById(Long id) {
            return Optional.ofNullable(savedBankSlip)
                    .filter(bankSlip -> bankSlip.getId().equals(id));
        }
    }
}