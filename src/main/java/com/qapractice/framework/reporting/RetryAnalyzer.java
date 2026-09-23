package com.qapractice.framework.reporting;

import com.qapractice.framework.config.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to {@code retry.count} times (default 0 locally, 1 in the 'ci' environment). */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        int max = ConfigManager.retryCount();
        if (attempt < max) {
            attempt++;
            LOG.warn("Retrying '{}' (attempt {} of {})", result.getName(), attempt, max);
            return true;
        }
        return false;
    }
}
