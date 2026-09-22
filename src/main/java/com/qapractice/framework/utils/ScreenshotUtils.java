package com.qapractice.framework.utils;

import com.qapractice.framework.config.ConfigManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Screenshot helpers used by the reporting listener. */
public final class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    public static String asBase64(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
    }

    /** Saves a PNG under {@code screenshot.dir} and returns its path. */
    public static Path saveToFile(WebDriver driver, String name) throws IOException {
        Path dir = Paths.get(ConfigManager.screenshotDir());
        Files.createDirectories(dir);
        String safeName = name.replaceAll("[^a-zA-Z0-9-_]", "_");
        String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        Path target = dir.resolve(safeName + "_" + stamp + ".png");
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(target, bytes);
        return target;
    }
}
