package com.qapractice.tests;

import com.qapractice.framework.base.BaseTest;
import com.qapractice.framework.config.ConfigManager;
import com.qapractice.framework.data.Credentials;
import com.qapractice.framework.data.JsonDataReader;
import com.qapractice.framework.data.LoginTestData;
import com.qapractice.framework.pages.LoginPage;
import com.qapractice.framework.pages.ShopPage;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Authentication scenarios. Data: testdata/login.json + configured credentials. */
public class LoginTests extends BaseTest {

    private static final LoginTestData DATA = JsonDataReader.read("testdata/login.json", LoginTestData.class);

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        // first column = human-readable description (shown in the report by TestListener)
        return DATA.invalidUsers().stream()
                .map(c -> new Object[]{c.description(), c})
                .toArray(Object[][]::new);
    }

    @Test(groups = {"smoke", "login"}, description = "TC-LOGIN-01: Registered user can log in and sees the product list")
    public void validUserCanLogIn() {
        ShopPage shop = openLoginPage().login(ConfigManager.userEmail(), ConfigManager.userPassword());

        Assert.assertTrue(shop.isLoggedIn(), "Logout button should be visible after a successful login");
        Assert.assertTrue(shop.productCount() > 0, "Product list should not be empty after login");
    }

    @Test(groups = {"regression", "login"}, dataProvider = "invalidCredentials",
            description = "TC-LOGIN-02: Invalid credentials are rejected with an error message")
    public void invalidCredentialsAreRejected(String description, Credentials credentials) {
        LoginPage loginPage = openLoginPage().attemptLogin(credentials.email(), credentials.password());

        Assert.assertTrue(loginPage.isMessageDisplayed(), "An error message should be displayed");
        Assert.assertTrue(loginPage.getMessageText().toLowerCase().contains(DATA.expectedErrorText().toLowerCase()),
                "Error message should contain '" + DATA.expectedErrorText() + "' but was: " + loginPage.getMessageText());
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "User must remain on the login form");
    }

    @Test(groups = {"smoke", "login"}, description = "TC-LOGIN-03: Logged-in user can log out")
    public void loggedInUserCanLogOut() {
        LoginPage loginPage = loginAsDefaultUser().logout();

        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login form should be shown again after logout");
    }
}
