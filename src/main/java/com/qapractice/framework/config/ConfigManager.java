package com.qapractice.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration access point.
 *
 * <p>Resolution order for every key (first non-blank value wins):
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dbrowser=firefox}</li>
 *   <li>OS environment variable, e.g. {@code QA_BROWSER=firefox}
 *       (prefix {@code QA_}, upper-case, dots replaced by underscores)</li>
 *   <li>Environment file {@code config/<env>.properties} (env = {@code -Denv=ci} or {@code QA_ENV=ci}, default {@code qa})</li>
 *   <li>{@code config/default.properties}</li>
 * </ol>
 * The prefix on environment variables avoids clashes with variables that already exist on many
 * machines (for example {@code BROWSER} on Linux desktops).
 */
public final class ConfigManager {

    private static final Logger LOG = LogManager.getLogger(ConfigManager.class);
    private static final String DEFAULT_ENV = "qa";
    private static final Properties FILE_PROPS = new Properties();
    private static final String ACTIVE_ENV;

    static {
        String env = System.getProperty("env");
        if (isBlank(env)) {
            env = System.getenv("QA_ENV");
        }
        ACTIVE_ENV = isBlank(env) ? DEFAULT_ENV : env.trim().toLowerCase();

        loadClasspathFile("config/default.properties", true);
        loadClasspathFile("config/" + ACTIVE_ENV + ".properties", false);
        LOG.info("Configuration loaded. Active environment = '{}'", ACTIVE_ENV);
    }

    private ConfigManager() {
    }

    // ------------------------------------------------------------------ generic accessors

    public static String get(String key) {
        String value = resolve(key);
        if (value == null) {
            throw new IllegalStateException("Missing configuration key '" + key
                    + "'. Define it in config/default.properties, config/" + ACTIVE_ENV
                    + ".properties, as -D" + key + " or as env var " + toEnvName(key));
        }
        return value;
    }

    public static String get(String key, String fallback) {
        String value = resolve(key);
        return value == null ? fallback : value;
    }

    public static int getInt(String key) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Config key '" + key + "' must be an integer but was '" + value + "'", e);
        }
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    // ------------------------------------------------------------------ typed convenience accessors

    public static String activeEnvironment()    { return ACTIVE_ENV; }
    public static String baseUrl()              { return stripTrailingSlash(get("app.base.url")); }
    public static String loginPath()            { return get("app.login.path"); }
    public static String userEmail()            { return get("app.user.email"); }
    public static String userPassword()         { return get("app.user.password"); }
    public static String browser()              { return get("browser").toLowerCase(); }
    public static boolean isHeadless()          { return getBoolean("headless"); }
    public static boolean maximize()            { return getBoolean("maximize"); }
    public static String gridUrl()              { return get("grid.url", ""); }
    public static int explicitWaitSeconds()     { return getInt("explicit.wait.seconds"); }
    public static int shortWaitSeconds()        { return getInt("short.wait.seconds"); }
    public static int pageLoadTimeoutSeconds()  { return getInt("page.load.timeout.seconds"); }
    public static String reportDir()            { return get("report.dir"); }
    public static String screenshotDir()        { return get("screenshot.dir"); }
    public static boolean screenshotOnFailure() { return getBoolean("screenshot.on.failure"); }
    public static int retryCount()              { return getInt("retry.count"); }

    // ------------------------------------------------------------------ internals

    private static String resolve(String key) {
        String value = System.getProperty(key);
        if (!isBlank(value)) {
            return value.trim();
        }
        value = System.getenv(toEnvName(key));
        if (!isBlank(value)) {
            return value.trim();
        }
        value = FILE_PROPS.getProperty(key);
        return value == null ? null : value.trim();
    }

    private static String toEnvName(String key) {
        return "QA_" + key.toUpperCase().replace('.', '_');
    }

    private static void loadClasspathFile(String path, boolean mandatory) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                if (mandatory) {
                    throw new IllegalStateException("Mandatory config file not found on classpath: " + path);
                }
                LOG.warn("Optional config file not found: {} (continuing with defaults)", path);
                return;
            }
            FILE_PROPS.load(in);   // later files overwrite earlier ones
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config file " + path, e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String stripTrailingSlash(String s) {
        return s.endsWith("/") ? s.substring(0, s.length() - 1) : s;
    }
}
