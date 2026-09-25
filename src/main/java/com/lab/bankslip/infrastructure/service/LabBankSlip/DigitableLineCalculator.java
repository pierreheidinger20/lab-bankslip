package com.lab.bankslip.infrastructure.service.LabBankSlip;
import org.springframework.stereotype.Component;

@Component
public class DigitableLineCalculator {

    /**
     * Converts the simulated barcode into a simulated
     * digitable line.
     *
     * The generic structure documented by Iugu is:
     *
     * 47 or 48 digits
     * divided into 5 fields.
     *
     * For this laboratory implementation we use:
     *
     * Field 1:
     *   bank + currency + first 5 digits of free field + DV
     *
     * Field 2:
     *   next 10 digits of free field + DV
     *
     * Field 3:
     *   remaining 10 digits of free field + DV
     *
     * Field 4:
     *   general barcode DV
     *
     * Field 5:
     *   fixed value + amount
     */
    public String calculate(String barcode) {

        validateBarcode(barcode);

        String bankCode = barcode.substring(0, 3);
        String currency = barcode.substring(3, 4);

        String generalCheckDigit = barcode.substring(4, 5);

        String freeField = barcode.substring(5, 30);

        String fixedField = barcode.substring(30, 34);

        String amount = barcode.substring(34, 44);

        /*
         * Field 1
         *
         * 3 bank digits
         * 1 currency digit
         * 5 free-field digits
         * 1 field check digit
         */
        String field1Base =
                bankCode
                        + currency
                        + freeField.substring(0, 5);

        String field1 =
                field1Base
                        + calculateFieldCheckDigit(field1Base);

        /*
         * Field 2
         *
         * 10 digits + field check digit
         */
        String field2Base =
                freeField.substring(5, 15);

        String field2 =
                field2Base
                        + calculateFieldCheckDigit(field2Base);

        /*
         * Field 3
         *
         * 10 digits + field check digit
         */
        String field3Base =
                freeField.substring(15, 25);

        String field3 =
                field3Base
                        + calculateFieldCheckDigit(field3Base);

        /*
         * Field 4
         *
         * General barcode check digit.
         */
        String field4 = generalCheckDigit;

        /*
         * Field 5
         *
         * Fixed field + amount.
         *
         * 4 + 10 = 14 digits.
         */
        String field5 =
                fixedField
                        + amount;

        return field1
                + field2
                + field3
                + field4
                + field5;
    }

    private int calculateFieldCheckDigit(String value) {

        int sum = 0;
        int weight = 2;

        for (int i = value.length() - 1; i >= 0; i--) {

            int digit = Character.digit(
                    value.charAt(i),
                    10
            );

            if (digit < 0) {
                throw new IllegalArgumentException(
                        "Digitable line contains non-numeric characters"
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

        if (result == 10 || result == 11) {
            return 1;
        }

        return result;
    }

    private void validateBarcode(String barcode) {

        if (barcode == null || barcode.isBlank()) {
            throw new IllegalArgumentException(
                    "Barcode cannot be null or blank"
            );
        }

        if (barcode.length() != 44) {
            throw new IllegalArgumentException(
                    "Barcode must contain exactly 44 digits"
            );
        }

        if (!barcode.matches("\\d{44}")) {
            throw new IllegalArgumentException(
                    "Barcode must contain only numeric digits"
            );
        }
    }
}
