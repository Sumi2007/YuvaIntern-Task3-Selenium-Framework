package com.qapractice.framework.pages;

import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.reporting.StepLogger;
import com.qapractice.framework.utils.PriceParser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** Product list shown after login (products are loaded asynchronously). Owns a {@link CartComponent}. */
public class ShopPage extends BasePage {

    private static final By PRODUCT = By.cssSelector(".shop-item");
    private static final By PRODUCT_TITLE = By.cssSelector(".shop-item-title");
    private static final By PRODUCT_PRICE = By.cssSelector(".shop-item-price");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector(".shop-item-button");
    private static final By LOGOUT_BUTTON = By.id("logout");

    private final CartComponent cart;

    public ShopPage(WebDriver driver) {
        super(driver);
        this.cart = new CartComponent(driver);
    }

    /** Waits for the asynchronous "Loading products..." step to finish. */
    public ShopPage waitUntilLoaded() {
        waitUntil(() -> !products().isEmpty());
        return this;
    }

    public CartComponent cart() {
        return cart;
    }

    public int productCount() {
        return products().size();
    }

    public String productTitle(int index) {
        return products().get(index).findElement(PRODUCT_TITLE).getText().trim();
    }

    public BigDecimal productPrice(int index) {
        return PriceParser.parse(products().get(index).findElement(PRODUCT_PRICE).getText());
    }

    /** Clicks ADD TO CART for the product at {@code index} and waits until the cart shows one more row. */
    public ShopPage addProductToCart(int index) {
        int before = cart.itemCount();
        WebElement product = products().get(index);
        StepLogger.step("Add product #" + index + " ('" + productTitle(index) + "') to cart");
        click(product.findElement(ADD_TO_CART_BUTTON));
        waitUntil(() -> cart.itemCount() == before + 1);
        return this;
    }

    public boolean isLoggedIn() {
        return isDisplayed(LOGOUT_BUTTON, ConfigManager.shortWaitSeconds());
    }

    public LoginPage logout() {
        click(LOGOUT_BUTTON, "Logout");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.isLoginFormDisplayed();
        return loginPage;
    }

    private List<WebElement> products() {
        return driver.findElements(PRODUCT);
    }
}
