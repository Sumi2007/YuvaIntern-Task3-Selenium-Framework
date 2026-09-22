package com.qapractice.framework.reporting;

import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * One call writes a step to BOTH the log file and the HTML report.
 * Page objects call this from their action methods, so every report shows a readable step list
 * without the test author writing any logging code.
 */
public final class StepLogger {

    private static final Logger LOG = LogManager.getLogger("STEP");

    private StepLogger() {
    }

    public static void step(String message) {
        LOG.info(message);
        ExtentTest test = ExtentTestManager.get();
        if (test != null) {
            test.info(message);
        }
    }
}
