package com.qapractice.framework.driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Holds one WebDriver per thread (ThreadLocal) so the framework is parallel-safe:
 * two tests running at the same time never share a browser session.
 */
public final class DriverManager {

    private static final Logger LOG = LogManager.getLogger(DriverManager.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void initDriver() {
        if (DRIVER.get() != null) {
            LOG.warn("A driver already exists for this thread - quitting it before creating a new one");
            quitDriver();
        }
        DRIVER.set(DriverFactory.createDriver());
        LOG.info("WebDriver session started on thread '{}'", Thread.currentThread().getName());
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No WebDriver for this thread. Did BaseTest.setUp() run?");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
                LOG.info("WebDriver session closed on thread '{}'", Thread.currentThread().getName());
            } catch (Exception e) {
                LOG.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                DRIVER.remove();   // prevents leaks in thread pools
            }
        }
    }
}
