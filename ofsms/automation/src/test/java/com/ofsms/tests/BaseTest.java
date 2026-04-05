package com.ofsms.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.WebDriverManagerException;
import com.ofsms.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.TimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTest {
    private static final Properties CONFIG = loadConfig();
    private static final Path WDM_CACHE_DIR = Path.of("target", "wdm");
    private static final Logger CDP_VERSION_LOGGER = Logger.getLogger("org.openqa.selenium.devtools.CdpVersionFinder");

    protected WebDriver driver;
    protected String baseUrl;
    protected String adminBaseUrl;
    protected long slowMoMs;

    static {
        // Selenium can safely fall back to the nearest supported CDP version for our test flows.
        CDP_VERSION_LOGGER.setLevel(Level.SEVERE);
    }

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        configureChromeDriver();
        driver = new ChromeDriver(buildChromeOptions());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        driver.manage().window().maximize();
        baseUrl = resolveBaseUrl();
        adminBaseUrl = resolveAdminBaseUrl();
        slowMoMs = resolveSlowMoMs();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected TestUser registerUser() {
        String unique = String.valueOf(System.currentTimeMillis());
        String shortUnique = unique.substring(Math.max(0, unique.length() - 6));
        String email = "autotest" + unique + "@example.com";
        String password = "Test@12345";
        String fullName = "Auto Test " + shortUnique;
        String mobileNumber = "9" + String.format("%09d", Integer.parseInt(shortUnique));

        driver.get(baseUrl + "signup.php");
        slowMoPause();
        driver.findElement(By.name("fname")).sendKeys(fullName);
        slowMoPause();
        driver.findElement(By.name("email")).sendKeys(email);
        slowMoPause();
        driver.findElement(By.name("mobno")).sendKeys(mobileNumber);
        slowMoPause();
        driver.findElement(By.name("password")).sendKeys(password);
        slowMoPause();
        driver.findElement(By.name("repeatpassword")).sendKeys(password);
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        acceptAlertIfPresent();
        slowMoPause();

        return new TestUser(fullName, email, password, mobileNumber);
    }

    protected TestUser registerAndLoginUser() {
        TestUser user = registerUser();
        driver.get(baseUrl + "signup.php");
        slowMoPause();
        new LoginPage(driver).login(user.getEmail(), user.getPassword());
        slowMoPause();
        return user;
    }

    protected void acceptAlertIfPresent() {
        try {
            driver.switchTo().alert().accept();
            slowMoPause();
        } catch (NoAlertPresentException ignored) {
            // Some flows redirect immediately without keeping an alert open.
        }
    }

    protected void waitForAlertAndAccept() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent())
                    .accept();
            slowMoPause();
        } catch (TimeoutException ignored) {
            // Flash-message flows redirect without using blocking browser alerts.
        }
    }

    protected void waitForUrlContains(String urlPart) {
        new WebDriverWait(driver, Duration.ofSeconds(8))
                .until(ExpectedConditions.urlContains(urlPart));
        slowMoPause();
    }

    protected void loginAsAdmin() {
        String username = firstNonBlank(
                System.getProperty("admin.username"),
                System.getenv("ADMIN_USERNAME"),
                "admin");
        String password = firstNonBlank(
                System.getProperty("admin.password"),
                System.getenv("ADMIN_PASSWORD"),
                "Test@123");

        driver.get(adminBaseUrl + "login.php");
        slowMoPause();
        driver.findElement(By.name("username")).clear();
        driver.findElement(By.name("username")).sendKeys(username);
        slowMoPause();
        driver.findElement(By.name("password")).clear();
        driver.findElement(By.name("password")).sendKeys(password);
        slowMoPause();
        driver.findElement(By.cssSelector("button[name='login']")).click();
        try {
            waitForUrlContains("dashboard.php");
        } catch (TimeoutException e) {
            driver.get(adminBaseUrl + "dashboard.php");
            slowMoPause();
        }
    }

    protected void slowMoPause() {
        slowMoPause(slowMoMs);
    }

    protected void slowMoPause(long millis) {
        if (millis <= 0) {
            return;
        }
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during slow-motion demo pause", e);
        }
    }

    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = BaseTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to load automation config.properties", e);
        }
        return properties;
    }

    private void configureChromeDriver() {
        String explicitDriverPath = firstNonBlank(
                System.getProperty("webdriver.chrome.driver"),
                System.getenv("WEBDRIVER_CHROME_DRIVER"));

        if (explicitDriverPath != null) {
            Path driverPath = Path.of(explicitDriverPath);
            if (!Files.isRegularFile(driverPath)) {
                throw new IllegalArgumentException("ChromeDriver not found at: " + driverPath.toAbsolutePath());
            }
            System.setProperty("webdriver.chrome.driver", driverPath.toAbsolutePath().toString());
            return;
        }

        createCacheDirectory();

        WebDriverManager manager = WebDriverManager.chromedriver()
                .cachePath(WDM_CACHE_DIR.toString())
                .resolutionCachePath(WDM_CACHE_DIR.toString());
        if (isCacheClearEnabled()) {
            manager.clearDriverCache().clearResolutionCache();
        }
        try {
            manager.setup();
        } catch (WebDriverManagerException e) {
            throw new IllegalStateException(
                    "Unable to resolve ChromeDriver automatically. "
                            + "Provide -Dwebdriver.chrome.driver=<full-path-to-chromedriver> "
                            + "or run with network access so WebDriverManager can download a matching driver.",
                    e);
        }
    }

    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        if (isHeadlessEnabled()) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
        }

        return options;
    }

    private String resolveBaseUrl() {
        return firstNonBlank(
                System.getProperty("baseUrl"),
                System.getenv("BASE_URL"),
                CONFIG.getProperty("baseUrl"),
                "http://localhost/Online%20Furniture%20Shop%20Project/ofsms/");
    }

    private String resolveAdminBaseUrl() {
        return firstNonBlank(
                System.getProperty("adminBaseUrl"),
                System.getenv("ADMIN_BASE_URL"),
                baseUrl + "admin/");
    }

    private boolean isCacheClearEnabled() {
        return Boolean.parseBoolean(firstNonBlank(
                System.getProperty("wdm.clearCache"),
                System.getenv("WDM_CLEAR_CACHE"),
                "true"));
    }

    private boolean isHeadlessEnabled() {
        String explicitHeadless = firstNonBlank(
                System.getProperty("headless"),
                System.getenv("HEADLESS"));
        if (explicitHeadless != null) {
            return Boolean.parseBoolean(explicitHeadless);
        }
        return Boolean.parseBoolean(System.getenv().getOrDefault("CI", "false"))
                || Boolean.parseBoolean(System.getenv().getOrDefault("GITHUB_ACTIONS", "false"));
    }

    private long resolveSlowMoMs() {
        String configuredDelay = firstNonBlank(
                System.getProperty("slowMoMs"),
                System.getenv("SLOW_MO_MS"),
                "0");
        return Long.parseLong(configuredDelay);
    }

    private void createCacheDirectory() {
        try {
            Files.createDirectories(WDM_CACHE_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create WebDriverManager cache directory", e);
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }

    protected static class TestUser {
        private final String fullName;
        private final String email;
        private final String password;
        private final String mobileNumber;

        protected TestUser(String fullName, String email, String password, String mobileNumber) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.mobileNumber = mobileNumber;
        }

        protected String getFullName() {
            return fullName;
        }

        protected String getEmail() {
            return email;
        }

        protected String getPassword() {
            return password;
        }

        protected String getMobileNumber() {
            return mobileNumber;
        }
    }
}
