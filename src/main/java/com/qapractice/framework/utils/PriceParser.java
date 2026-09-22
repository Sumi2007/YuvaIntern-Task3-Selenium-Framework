package com.qapractice.framework.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Converts UI price strings such as "$12.99" or "$1,299.00" into BigDecimal (scale 2). */
public final class PriceParser {

    private PriceParser() {
    }

    public static BigDecimal parse(String text) {
        String cleaned = text == null ? "" : text.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException("No numeric price found in text: '" + text + "'");
        }
        return new BigDecimal(cleaned).setScale(2, RoundingMode.HALF_UP);
    }
}
