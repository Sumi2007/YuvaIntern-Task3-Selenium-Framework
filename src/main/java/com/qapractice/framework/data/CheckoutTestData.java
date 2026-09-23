package com.qapractice.framework.data;

import java.util.List;

/** Mirrors testdata/checkout.json */
public record CheckoutTestData(String expectedConfirmationText,
                               List<ShippingDetails> validShipping,
                               ShippingDetails incompleteShipping) {
}
