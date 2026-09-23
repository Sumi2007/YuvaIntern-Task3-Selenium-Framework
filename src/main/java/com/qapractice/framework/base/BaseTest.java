package com.qapractice.framework.base;

import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.driver.DriverManager;
import com.qapractice.framework.pages.LoginPage;
import com.qapractice.framework.pages.ShopPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

/**
 * Parent of every test class.
 *
 * <ul>
 *   <li>{@link #setUp} starts a fresh, isolated browser session before EVERY test method.</li>
 *   <li>{@link #tearDown} always closes it (alwaysRun = true), even when the test failed.</li>
 *   <li>Screenshots / report entries are handled by {@code TestListener}, not here.</li>
 * </ul>
 * A new test class only needs {@code extends BaseTest}.
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(getClass());

    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method) {
        log.info("Setting up browser for test '{}'", method.getName());
        DriverManager.initDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        log.info("Tearing down after '{}' (status: {})", result.getMethod().getMethodName(), statusName(result.getStatus()));
        DriverManager.quitDriver();
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    // ------------------------------------------------------------------ reusable test steps

    protected LoginPage openLoginPage() {
        return new LoginPage(driver()).open();
    }

    /** Opens the site and logs in with the credentials from configuration. */
    protected ShopPage loginAsDefaultUser() {
        return openLoginPage().login(ConfigManager.userEmail(), ConfigManager.userPassword());
    }

    private static String statusName(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "PASS";
            case ITestResult.FAILURE -> "FAIL";
            case ITestResult.SKIP -> "SKIP";
            default -> "UNKNOWN(" + status + ")";
        };
    }
}
