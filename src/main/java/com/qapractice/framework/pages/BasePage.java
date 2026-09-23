package com.qapractice.framework.pages;

import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.reporting.StepLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Parent of every page object / component. Holds the WebDriver and the reusable, wait-aware
 * actions (click, type, select, read text). Page classes contain locators and business actions only.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigManager.explicitWaitSeconds()));
    }

    // ------------------------------------------------------------------ waits

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    /** Polls {@code condition} until it is true or the explicit wait expires (typed to avoid lambda overload ambiguity). */
    protected void waitUntil(BooleanSupplier condition) {
        wait.until((ExpectedCondition<Boolean>) d -> condition.getAsBoolean());
    }

    /** Returns true if the element becomes visible within {@code seconds}; never throws on timeout. */
    protected boolean isDisplayed(By locator, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected boolean isDisplayed(By locator) {
        return isDisplayed(locator, ConfigManager.shortWaitSeconds());
    }

    // ------------------------------------------------------------------ actions

    protected void click(By locator, String elementName) {
        StepLogger.step("Click '" + elementName + "'");
        click(wait.until(ExpectedConditions.elementToBeClickable(locator)));
    }

    /** Normal click; falls back to a JavaScript click if another element intercepts it. */
    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Click intercepted - retrying with JavaScript click");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    protected void type(By locator, String text, String fieldName) {
        StepLogger.step("Type '" + text + "' into '" + fieldName + "'");
        typeInto(locator, text);
    }

    /** Same as {@link #type} but the value is not written to logs / report. */
    protected void typeSecret(By locator, String text, String fieldName) {
        StepLogger.step("Type ******** into '" + fieldName + "'");
        typeInto(locator, text);
    }

    private void typeInto(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected void selectByVisibleText(By locator, String visibleText, String fieldName) {
        StepLogger.step("Select '" + visibleText + "' in '" + fieldName + "'");
        new Select(waitForVisible(locator)).selectByVisibleText(visibleText);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }
}
