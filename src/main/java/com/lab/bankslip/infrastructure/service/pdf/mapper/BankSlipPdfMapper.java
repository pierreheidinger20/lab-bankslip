package com.lab.bankslip.infrastructure.service.pdf.mapper;
import org.springframework.stereotype.Component;
import com.lab.bankslip.domain.model.BankSlip;
import com.lab.bankslip.infrastructure.service.pdf.dto.BankSlipPdfDto;

@Component
public class BankSlipPdfMapper {

    private static final String INSTITUTION_NAME =
            "ACME INSTITUIÇÃO DE PAGAMENTO S.A.";

    private static final String INSTITUTION_DOCUMENT =
            "12.345.678/0001-90";

    private static final String PAYMENT_PLACE =
            "Pagável em qualquer banco ou lotérica.";

    private static final String INSTRUCTIONS =
            "Não receber após o vencimento.";

    public BankSlipPdfDto toDto(BankSlip bankSlip) {

        BankSlipPdfDto dto = new BankSlipPdfDto();

        dto.setInstitutionName(INSTITUTION_NAME);
        dto.setInstitutionDocument(INSTITUTION_DOCUMENT);

        dto.setBankCode(bankSlip.getBankCode());

        dto.setPaymentPlace(PAYMENT_PLACE);

        /*
         * Para nosso laboratório usamos o ID
         * como Nosso Número.
         */
        dto.setOurNumber(
                String.valueOf(bankSlip.getId())
        );

        dto.setBeneficiaryName(
                bankSlip.getBeneficiaryName()
        );

        dto.setBeneficiaryDocument(
                bankSlip.getBeneficiaryDocument()
        );

        dto.setAgency(
                bankSlip.getAgency()
        );

        dto.setBeneficiaryCode(
                bankSlip.getBeneficiaryCode()
        );

        dto.setPayerName(
                bankSlip.getPayerName()
        );

        dto.setPayerDocument(
                bankSlip.getPayerDocument()
        );

        dto.setDocumentNumber(
                bankSlip.getDocumentNumber()
        );

        dto.setIssueDate(
                bankSlip.getIssueDate()
        );

        dto.setDueDate(
                bankSlip.getDueDate()
        );

        dto.setAmount(
                bankSlip.getAmount()
        );

        dto.setDiscount(
                bankSlip.getDiscount()
        );

        dto.setInterest(
                bankSlip.getInterest()
        );

        dto.setChargedAmount(
                bankSlip.getChargedAmount()
        );

        dto.setInstructions(INSTRUCTIONS);

        dto.setBarcode(
                bankSlip.getBarcode()
        );

        dto.setDigitableLine(
                bankSlip.getDigitableLine()
        );

        dto.setCustomerId(bankSlip.getCustomerId());

        return dto;
    }
}
