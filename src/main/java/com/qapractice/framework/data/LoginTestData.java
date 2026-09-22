package com.qapractice.framework.data;

import java.util.List;

/** Mirrors testdata/login.json */
public record LoginTestData(List<Credentials> invalidUsers, String expectedErrorText) {
}
