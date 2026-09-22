package com.qapractice.framework.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.qapractice.framework.config.ConfigManager;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Creates the single ExtentReports instance (HTML report) for the whole run. */
public final class ExtentManager {

    private static ExtentReports extent;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            extent = create();
        }
        return extent;
    }

    private static ExtentReports create() {
        try {
            Path dir = Paths.get(ConfigManager.reportDir());
            Files.createDirectories(dir);
            String stamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            Path file = dir.resolve("TestReport_" + stamp + ".html");

            ExtentSparkReporter spark = new ExtentSparkReporter(file.toString());
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("QA Practice - Automation Report");
            spark.config().setReportName("E-commerce flow - Selenium/TestNG framework");

            ExtentReports reports = new ExtentReports();
            reports.attachReporter(spark);
            reports.setSystemInfo("Environment", ConfigManager.activeEnvironment());
            reports.setSystemInfo("Base URL", ConfigManager.baseUrl());
            reports.setSystemInfo("Browser", ConfigManager.browser());
            reports.setSystemInfo("Headless", String.valueOf(ConfigManager.isHeadless()));
            reports.setSystemInfo("OS", System.getProperty("os.name"));
            reports.setSystemInfo("Java", System.getProperty("java.version"));
            return reports;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create report directory", e);
        }
    }
}
