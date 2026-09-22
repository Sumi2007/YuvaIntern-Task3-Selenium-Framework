package com.qapractice.tests;

import com.qapractice.framework.base.BaseTest;
import com.qapractice.framework.data.CartTestData;
import com.qapractice.framework.data.JsonDataReader;
import com.qapractice.framework.pages.ShopPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;
import java.util.List;

/**
 * Shopping-cart scenarios. Products are chosen by position (testdata/cart.json) and the expected
 * values are read from the product list itself, so the tests do not hard-code product names or prices.
 */
public class CartTests extends BaseTest {

    private static final CartTestData DATA = JsonDataReader.read("testdata/cart.json", CartTestData.class);

    @Test(groups = {"smoke", "cart"}, description = "TC-CART-01: Product added from the list appears in the cart")
    public void addedProductAppearsInCart() {
        ShopPage shop = loginAsDefaultUser();
        String title = shop.productTitle(0);

        shop.addProductToCart(0);

        Assert.assertEquals(lowerCase(shop.cart().itemTitles()), List.of(title.toLowerCase()),
                "Cart should contain exactly the product that was added");
    }

    @Test(groups = {"regression", "cart"}, description = "TC-CART-02: Cart total equals the sum of the added product prices")
    public void cartTotalEqualsSumOfProductPrices() {
        ShopPage shop = loginAsDefaultUser();
        Assert.assertTrue(shop.productCount() >= DATA.productIndexes().size(),
                "The shop must list at least " + DATA.productIndexes().size() + " products");

        BigDecimal expectedTotal = BigDecimal.ZERO;
        for (int index : DATA.productIndexes()) {
            expectedTotal = expectedTotal.add(shop.productPrice(index));
            shop.addProductToCart(index);
        }

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(shop.cart().itemCount(), DATA.productIndexes().size(), "Number of cart rows");
        soft.assertEquals(shop.cart().total(), expectedTotal.setScale(2), "Cart total");
        soft.assertAll();
    }

    @Test(groups = {"regression", "cart"}, description = "TC-CART-03: Removing a product updates the cart rows and total")
    public void removingProductUpdatesCartAndTotal() {
        ShopPage shop = loginAsDefaultUser();
        int first = DATA.productIndexes().get(0);
        int second = DATA.productIndexes().get(1);
        String firstTitle = shop.productTitle(first);
        BigDecimal secondPrice = shop.productPrice(second);

        shop.addProductToCart(first).addProductToCart(second);
        shop.cart().removeItem(firstTitle);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(shop.cart().itemCount(), 1, "Number of cart rows after removal");
        soft.assertEquals(shop.cart().total(), secondPrice.setScale(2), "Cart total after removal");
        soft.assertAll();
    }

    private static List<String> lowerCase(List<String> values) {
        return values.stream().map(String::toLowerCase).toList();
    }
}
