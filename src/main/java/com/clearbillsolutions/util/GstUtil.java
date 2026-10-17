package com.clearbillsolutions.util;

import java.util.regex.Pattern;

/**
 * Utility for GST validations and state matching.
 */
public class GstUtil {

    private static final Pattern GSTIN_PATTERN = Pattern.compile("^[0-9]{2}[A-Za-z]{5}[0-9]{4}[A-Za-z]{1}[1-9A-Za-z]{1}[Zz][0-9A-Za-z]{1}$");

    /**
     * Validates an optional GSTIN format (15 alphanumeric characters).
     * Returns true if blank/null (optional) or if matching standard GSTIN syntax.
     */
    public static boolean isValidGstin(String gstin) {
        if (gstin == null || gstin.trim().isEmpty()) {
            return true;
        }
        return GSTIN_PATTERN.matcher(gstin.trim()).matches();
    }

    /**
     * Determines whether transaction is intra-state (same state, CGST + SGST) or inter-state (IGST).
     */
    public static boolean isIntraState(String businessState, String customerState) {
        if (businessState == null || customerState == null) {
            return true; // Default to intra-state if either state is unspecified
        }
        return businessState.trim().equalsIgnoreCase(customerState.trim());
    }
}
