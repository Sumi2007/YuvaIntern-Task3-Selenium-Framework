package com.qapractice.framework.pages;

import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.reporting.StepLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Login form of https://qa-practice.razvanvancea.ro/auth_ecommerce.html */
public class LoginPage extends BasePage {

    // ---- Locators (single place to fix if the UI changes) ----
    private static final By EMAIL_INPUT = By.id("email");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By SUBMIT_BUTTON = By.id("submitLoginBtn");
    private static final By MESSAGE = By.id("message");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        String url = ConfigManager.baseUrl() + ConfigManager.loginPath();
        StepLogger.step("Open login page: " + url);
        driver.get(url);
        waitForVisible(EMAIL_INPUT);
        return this;
    }

    /** Submits credentials and stays on this page object (use for negative scenarios). */
    public LoginPage attemptLogin(String email, String password) {
        type(EMAIL_INPUT, email, "Email");
        typeSecret(PASSWORD_INPUT, password, "Password");
        click(SUBMIT_BUTTON, "Submit (login)");
        return this;
    }

    /** Logs in successfully and returns the shop page once products are loaded. */
    public ShopPage login(String email, String password) {
        attemptLogin(email, password);
        return new ShopPage(driver).waitUntilLoaded();
    }

    public boolean isLoginFormDisplayed() {
        return isDisplayed(EMAIL_INPUT);
    }

    public boolean isMessageDisplayed() {
        return isDisplayed(MESSAGE);
    }

    public String getMessageText() {
        return getText(MESSAGE);
    }
}
