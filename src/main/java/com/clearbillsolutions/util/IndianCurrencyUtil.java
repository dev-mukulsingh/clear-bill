package com.clearbillsolutions.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility class for formatting Indian currency (₹) with Lakh/Crore grouping
 * and converting monetary amounts to English words (Rupees and Paise).
 */
public class IndianCurrencyUtil {

    private static final String[] UNITS = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    /**
     * Formats a BigDecimal into Indian Rupee notation (e.g., ₹1,25,000.00).
     */
    public static String format(BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }

        boolean isNegative = amount.signum() < 0;
        BigDecimal abs = amount.abs().setScale(2, RoundingMode.HALF_UP);
        String s = abs.toPlainString();
        String[] parts = s.split("\\.");
        String intPart = parts[0];
        String decPart = parts.length > 1 ? parts[1] : "00";

        if (intPart.length() <= 3) {
            return (isNegative ? "-₹" : "₹") + intPart + "." + decPart;
        }

        String last3 = intPart.substring(intPart.length() - 3);
        String rest = intPart.substring(0, intPart.length() - 3);

        StringBuilder sb = new StringBuilder();
        int len = rest.length();
        for (int i = len; i > 0; i -= 2) {
            int start = Math.max(0, i - 2);
            String chunk = rest.substring(start, i);
            if (sb.length() > 0) {
                sb.insert(0, ",");
            }
            sb.insert(0, chunk);
        }
        sb.append(",").append(last3).append(".").append(decPart);
        return (isNegative ? "-₹" : "₹") + sb.toString();
    }

    /**
     * Converts a monetary amount to Indian English words.
     * Example: 125000.50 -> "Rupees One Lakh Twenty-Five Thousand and Fifty Paise Only"
     */
    public static String toWords(BigDecimal amount) {
        if (amount == null) {
            return "Rupees Zero Only";
        }

        BigDecimal abs = amount.abs().setScale(2, RoundingMode.HALF_UP);
        long rupees = abs.toBigInteger().longValue();
        int paise = abs.remainder(BigDecimal.ONE).multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue();

        StringBuilder result = new StringBuilder();
        if (rupees > 0) {
            result.append("Rupees ").append(convertNumber(rupees));
        }

        if (paise > 0) {
            if (rupees > 0) {
                result.append(" and ");
            } else {
                result.append("Rupees Zero and ");
            }
            result.append(convertLessThanThousand(paise)).append(" Paise");
        }

        if (result.length() == 0) {
            result.append("Rupees Zero");
        }

        result.append(" Only");
        return result.toString();
    }

    private static String convertLessThanThousand(int number) {
        StringBuilder sb = new StringBuilder();
        if (number >= 100) {
            sb.append(UNITS[number / 100]).append(" Hundred");
            number %= 100;
            if (number > 0) {
                sb.append(" ");
            }
        }
        if (number >= 20) {
            sb.append(TENS[number / 10]);
            int unit = number % 10;
            if (unit > 0) {
                sb.append("-").append(UNITS[unit]);
            }
        } else if (number > 0) {
            sb.append(UNITS[number]);
        }
        return sb.toString().trim();
    }

    private static String convertNumber(long number) {
        if (number == 0) {
            return "Zero";
        }

        StringBuilder sb = new StringBuilder();

        long crore = number / 10000000L;
        number %= 10000000L;

        long lakh = number / 100000L;
        number %= 100000L;

        long thousand = number / 1000L;
        number %= 1000L;

        long hundred = number / 100L;
        number %= 100L;

        long rest = number;

        if (crore > 0) {
            sb.append(convertNumber(crore)).append(" Crore ");
        }
        if (lakh > 0) {
            sb.append(convertLessThanThousand((int) lakh)).append(" Lakh ");
        }
        if (thousand > 0) {
            sb.append(convertLessThanThousand((int) thousand)).append(" Thousand ");
        }
        if (hundred > 0) {
            sb.append(convertLessThanThousand((int) hundred)).append(" Hundred ");
        }
        if (rest > 0) {
            sb.append(convertLessThanThousand((int) rest)).append(" ");
        }

        return sb.toString().trim();
    }
}
