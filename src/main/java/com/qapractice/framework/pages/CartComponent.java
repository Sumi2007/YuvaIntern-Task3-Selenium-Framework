package com.qapractice.framework.pages;

import com.qapractice.framework.reporting.StepLogger;
import com.qapractice.framework.utils.PriceParser;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** The "SHOPPING CART" section. A reusable component (not a full page) composed into {@link ShopPage}. */
public class CartComponent extends BasePage {

    private static final By ROWS = By.cssSelector(".cart-items .cart-row");
    private static final By ROW_TITLE = By.cssSelector(".cart-item-title");
    private static final By ROW_PRICE = By.cssSelector(".cart-price");
    private static final By ROW_REMOVE_BUTTON = By.cssSelector(".btn-danger");
    private static final By TOTAL = By.cssSelector(".cart-total-price");
    private static final By CHECKOUT_BUTTON = By.cssSelector(".btn-purchase");

    public CartComponent(WebDriver driver) {
        super(driver);
    }

    public int itemCount() {
        return rows().size();
    }

    public List<String> itemTitles() {
        return rows().stream()
                .map(row -> row.findElement(ROW_TITLE).getText().trim())
                .toList();
    }

    public List<BigDecimal> itemPrices() {
        return rows().stream()
                .map(row -> PriceParser.parse(row.findElement(ROW_PRICE).getText()))
                .toList();
    }

    public BigDecimal total() {
        return PriceParser.parse(waitForVisible(TOTAL).getText());
    }

    public CartComponent removeItem(String title) {
        int before = itemCount();
        WebElement row = rows().stream()
                .filter(r -> r.findElement(ROW_TITLE).getText().trim().equalsIgnoreCase(title))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No cart row titled '" + title + "'"));
        StepLogger.step("Remove '" + title + "' from cart");
        click(row.findElement(ROW_REMOVE_BUTTON));
        waitUntil(() -> itemCount() == before - 1);
        return this;
    }

    public CheckoutPage proceedToCheckout() {
        click(CHECKOUT_BUTTON, "PROCEED TO CHECKOUT");
        return new CheckoutPage(driver).waitUntilDisplayed();
    }

    private List<WebElement> rows() {
        return driver.findElements(ROWS);
    }
}
