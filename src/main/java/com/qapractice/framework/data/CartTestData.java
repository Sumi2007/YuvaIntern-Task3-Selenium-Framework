package com.qapractice.framework.data;

import java.util.List;

/** Mirrors testdata/cart.json. Product positions (0-based) are used so tests do not depend on product names. */
public record CartTestData(List<Integer> productIndexes) {
}
