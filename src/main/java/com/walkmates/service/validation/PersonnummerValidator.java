package com.walkmates.service.validation;

/**
 * Validates a Swedish personnummer using the Luhn algorithm (see REQUIREMENTS FR-1.4).
 *
 * <p>Ported almost unchanged from the BikeShare course system — it is domain-agnostic identity
 * validation and remains excellent boundary/spec-based testing material (format checks + the
 * checksum digit). Pure and deterministic: no dependencies, easy to unit test.</p>
 *
 * <p>Expected format: {@code YYMMDD-NNNN} (e.g. {@code 800101-8129}).</p>
 */
public class PersonnummerValidator {

    /**
     * Validates format and Luhn checksum.
     *
     * @param personnummer the candidate string in YYMMDD-NNNN form
     * @return true if the format is valid and the Luhn check digit matches
     */
    public boolean isValid(String personnummer) {
        if (personnummer == null) {
            return false;
        }
        if (!personnummer.matches("\\d{6}-\\d{4}")) {
            return false;
        }
        String digits = personnummer.replace("-", ""); // 10 digits
        return luhnChecksumValid(digits);
    }

    /**
     * Runs the Luhn algorithm over the 10 digits: double every second digit starting from the
     * left, sum the digits of any two-digit product, total, and verify the total is a multiple
     * of 10 (the check digit is the last position).
     *
     * @param digits exactly 10 numeric characters
     * @return true if the checksum is valid
     */
    private boolean luhnChecksumValid(String digits) {
        int sum = 0;
        for (int i = 0; i < digits.length(); i++) {
            int d = digits.charAt(i) - '0';
            // Positions 0,2,4,6,8 (1st,3rd,...) are doubled.
            if (i % 2 == 0) {
                d *= 2;
                if (d > 9) {
                    d -= 9; // same as summing the two digits
                }
            }
            sum += d;
        }
        return sum % 10 == 0;
    }
}
