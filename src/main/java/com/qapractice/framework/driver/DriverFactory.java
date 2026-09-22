package com.qapractice.framework.driver;

import com.qapractice.framework.config.ConfigManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds configured WebDriver instances (local or remote/Grid). Contains NO test logic.
 * Driver binaries are resolved automatically by Selenium Manager (bundled since Selenium 4.6).
 * To support a new browser add one case to each switch and one options method.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigManager.browser();
        boolean headless = ConfigManager.isHeadless();
        String grid = ConfigManager.gridUrl();

        WebDriver driver = grid.isBlank()
                ? createLocal(browser, headless)
                : createRemote(grid, browser, headless);

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigManager.pageLoadTimeoutSeconds()));
        if (ConfigManager.maximize() && !headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    private static WebDriver createLocal(String browser, boolean headless) {
        return switch (browser) {
            case "chrome"  -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge"    -> new EdgeDriver(edgeOptions(headless));
            default -> throw unsupported(browser);
        };
    }

    private static WebDriver createRemote(String gridUrl, String browser, boolean headless) {
        Capabilities capabilities = switch (browser) {
            case "chrome"  -> chromeOptions(headless);
            case "firefox" -> firefoxOptions(headless);
            case "edge"    -> edgeOptions(headless);
            default -> throw unsupported(browser);
        };
        try {
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid grid.url: " + gridUrl, e);
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--disable-infobars");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);          // no "save password" bubble
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080",
                    "--no-sandbox", "--disable-dev-shm-usage");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless", "--width=1920", "--height=1080");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-notifications");
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }

    private static IllegalArgumentException unsupported(String browser) {
        return new IllegalArgumentException("Unsupported browser '" + browser
                + "'. Supported values: chrome, firefox, edge");
    }
}
