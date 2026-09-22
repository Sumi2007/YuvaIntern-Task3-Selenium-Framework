package com.qapractice.framework.reporting;

import com.aventstack.extentreports.ExtentTest;

/**
 * Thread-local holder so each parallel test writes to its own section of the report.
 */
public final class ExtentTestManager {

    private static final ThreadLocal<ExtentTest> CURRENT = new ThreadLocal<>();

    private ExtentTestManager() {
    }

    public static void set(ExtentTest test) {
        CURRENT.set(test);
    }

    /**
     * May return null when called outside a running test.
     */
    public static ExtentTest get() {
        return CURRENT.get();
    }
}