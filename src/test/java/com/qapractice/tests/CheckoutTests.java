package com.qapractice.tests;

import com.qapractice.framework.base.BaseTest;
import com.qapractice.framework.data.CheckoutTestData;
import com.qapractice.framework.data.JsonDataReader;
import com.qapractice.framework.data.ShippingDetails;
import com.qapractice.framework.pages.CheckoutPage;
import com.qapractice.framework.pages.ShopPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** End-to-end purchase flow: login -> add to cart -> checkout -> confirmation. Data: testdata/checkout.json */
public class CheckoutTests extends BaseTest {

    private static final CheckoutTestData DATA = JsonDataReader.read("testdata/checkout.json", CheckoutTestData.class);

    @DataProvider(name = "validShipping")
    public Object[][] validShipping() {
        return DATA.validShipping().stream()
                .map(s -> new Object[]{s.description(), s})
                .toArray(Object[][]::new);
    }

    @Test(groups = {"smoke", "checkout", "e2e"}, dataProvider = "validShipping",
            description = "TC-CHK-01: User can place an order with valid shipping details")
    public void userCanPlaceOrder(String description, ShippingDetails shipping) {
        ShopPage shop = loginAsDefaultUser();
        shop.addProductToCart(0);

        CheckoutPage checkout = shop.cart().proceedToCheckout()
                .fillShippingDetails(shipping)
                .submitOrder();

        Assert.assertTrue(checkout.isOrderConfirmed(), "An order confirmation message should be displayed");
        Assert.assertTrue(checkout.confirmationMessage().toLowerCase().contains(DATA.expectedConfirmationText().toLowerCase()),
                "Confirmation should mention '" + DATA.expectedConfirmationText() + "' but was: " + checkout.confirmationMessage());
    }

    @Test(groups = {"regression", "checkout"},
            description = "TC-CHK-02: Order is NOT placed when required shipping fields are missing")
    public void orderIsRejectedWhenRequiredFieldsAreMissing() {
        ShopPage shop = loginAsDefaultUser();
        shop.addProductToCart(0);

        CheckoutPage checkout = shop.cart().proceedToCheckout()
                .fillShippingDetails(DATA.incompleteShipping())
                .submitOrder();

        Assert.assertTrue(checkout.streetValidationMessage().contains("Please fill out this field"),
                "Street field should show required-field validation");}
}
