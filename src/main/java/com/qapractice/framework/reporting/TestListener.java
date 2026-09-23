package com.qapractice.framework.reporting;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.driver.DriverManager;
import com.qapractice.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Bridges TestNG lifecycle events to the HTML report and log file.
 * Registered once in testng.xml - individual tests contain no reporting code.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        ExtentManager.getInstance();
        LOG.info("===== Test block started: {} =====", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = ExtentManager.getInstance().createTest(displayName(result), result.getMethod().getDescription());
        test.assignCategory(result.getMethod().getGroups());
        ExtentTestManager.set(test);
        LOG.info("TEST START  : {}", displayName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        currentTest(result).pass("Test passed");
        LOG.info("TEST PASSED : {}", displayName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = currentTest(result);
        test.fail(result.getThrowable());
        LOG.error("TEST FAILED : {} -> {}", displayName(result), String.valueOf(result.getThrowable()));

        if (ConfigManager.screenshotOnFailure() && DriverManager.hasDriver()) {
            try {
                String base64 = ScreenshotUtils.asBase64(DriverManager.getDriver());
                test.fail("Screenshot at the moment of failure",
                        MediaEntityBuilder.createScreenCaptureFromBase64String(base64, displayName(result)).build());
                ScreenshotUtils.saveToFile(DriverManager.getDriver(), result.getMethod().getMethodName());
            } catch (Exception e) {
                LOG.warn("Could not capture screenshot: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        currentTest(result).skip("Test skipped (or retried): " + result.getThrowable());
        LOG.warn("TEST SKIPPED: {}", displayName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.getInstance().flush();
        LOG.info("===== Test block finished: {} - passed={}, failed={}, skipped={} =====",
                context.getName(),
                context.getPassedTests().size(),
                context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    /** Method name, plus the first data-provider argument (a human-readable description) when present. */
    private static String displayName(ITestResult result) {
        Object[] params = result.getParameters();
        String name = result.getMethod().getMethodName();
        return params != null && params.length > 0 ? name + " [" + params[0] + "]" : name;
    }

    private static ExtentTest currentTest(ITestResult result) {
        ExtentTest test = ExtentTestManager.get();
        if (test == null) {   // e.g. skipped because a @BeforeMethod failed
            test = ExtentManager.getInstance().createTest(displayName(result));
            ExtentTestManager.set(test);
        }
        return test;
    }
}
