package com.lab.bankslip.infrastructure.service.LabBankSlip;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import com.lab.bankslip.domain.model.BankSlip;

@Component
public class BarcodeCalculator {

    private static final String BANK_CODE = "001";
    private static final String CURRENCY_CODE = "9";
    private static final String FIXED_VALUE = "0000";

    /**
     * Generates a simulated bank slip barcode.
     *
     * Structure used by this laboratory project:
     *
     * 001 9 [general DV] [25-digit free field] 0000 [10-digit amount]
     *
     * This is a simulation based on the generic structure described
     * by Iugu. It is NOT a valid Banco do Brasil/FEBRABAN barcode.
     */
    public String calculate(BankSlip bankSlip) {

        String freeField = generateFreeField(bankSlip);

        String amount = formatAmount(bankSlip.getAmount());

        /*
         * Remove the general check digit from the final barcode.
         *
         * bank + currency + free field + fixed field + amount
         */
        String withoutCheckDigit =
                BANK_CODE
                        + CURRENCY_CODE
                        + freeField
                        + FIXED_VALUE
                        + amount;

        int checkDigit = calculateModulo11(withoutCheckDigit);

        return BANK_CODE
                + CURRENCY_CODE
                + checkDigit
                + freeField
                + FIXED_VALUE
                + amount;
    }

    /**
     * Generates the 25-digit field controlled by the issuer.
     *
     * For the laboratory we derive it deterministically from:
     *
     * customerId + document ID
     *
     * A real bank would define its own layout for these 25 digits.
     */
    private String generateFreeField(BankSlip bankSlip) {

        String customer = sanitize(bankSlip.getCustomerId());

        String customerPart = String.valueOf(Math.abs(customer.hashCode()));

        String documentPart = bankSlip.getId() == null
                ? "0"
                : String.valueOf(bankSlip.getId());

        String raw = customerPart + documentPart;

        if (raw.length() >= 25) {
            return raw.substring(0, 25);
        }

        return String.format("%-25s", raw)
                .replace(' ', '0');
    }

    /**
     * Converts:
     *
     * 1000.00 -> 0000100000
     * 125.50  -> 0000012550
     *
     * The amount occupies the last 10 digits.
     */
    private String formatAmount(BigDecimal amount) {

        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }

        long cents = amount
                .movePointRight(2)
                .longValueExact();

        if (cents < 0) {
            throw new IllegalArgumentException(
                    "Amount cannot be negative"
            );
        }

        if (cents > 9999999999L) {
            throw new IllegalArgumentException(
                    "Amount exceeds the maximum supported value"
            );
        }

        return String.format("%010d", cents);
    }

    /**
     * Modulo 11 used by this laboratory implementation.
     *
     * Weights:
     *
     * 2 3 4 5 6 7 8 9
     *
     * starting from the right.
     */
    private int calculateModulo11(String value) {

        int sum = 0;
        int weight = 2;

        for (int i = value.length() - 1; i >= 0; i--) {

            int digit = Character.digit(value.charAt(i), 10);

            if (digit < 0) {
                throw new IllegalArgumentException(
                        "Barcode contains non-numeric characters"
                );
            }

            sum += digit * weight;

            weight++;

            if (weight > 9) {
                weight = 2;
            }
        }

        int remainder = sum % 11;
        int result = 11 - remainder;

        /*
         * Laboratory convention.
         *
         * Real banks can have different rules for
         * exceptional results, so this must be replaced
         * when implementing a specific bank.
         */
        if (result == 10 || result == 11) {
            return 1;
        }

        return result;
    }

    private String sanitize(String value) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Customer ID cannot be null or blank"
            );
        }

        return value.trim();
    }
}