package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "guest", "regression"})
public class UserGuestPagesTest extends BaseTest {

    @Test
    public void guestCanOpenHomePageAndSeeBranding() {
        driver.get(baseUrl);
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("ofsms"),
                "Home page should open for guest users");
        Assert.assertTrue(pageText.contains("Furniture Store"),
                "Home page should show the storefront branding");
    }

    @Test
    public void guestCanSeeLoginLinkOnHomePage() {
        driver.get(baseUrl);
        slowMoPause();

        Assert.assertTrue(driver.findElement(By.linkText("LOGIN")).isDisplayed(),
                "Guest users should see the login link on the home page");
    }

    @Test
    public void guestCanSeeAdminLinkOnHomePage() {
        driver.get(baseUrl);
        slowMoPause();

        Assert.assertFalse(driver.findElements(By.cssSelector("a[href='admin/login.php']")).isEmpty(),
                "Guest users should see the admin link in the navigation");
    }

    @Test
    public void guestCanOpenSignupPage() {
        driver.get(baseUrl + "signup.php");
        slowMoPause();

        Assert.assertTrue(driver.getCurrentUrl().contains("signup.php"),
                "Signup page should open for guest users");
    }

    @Test
    public void signupPageShowsRegistrationSection() {
        driver.get(baseUrl + "signup.php");
        slowMoPause();

        Assert.assertFalse(driver.findElements(By.id("signup")).isEmpty(),
                "Signup page should show the registration form");
        Assert.assertFalse(driver.findElements(By.name("repeatpassword")).isEmpty(),
                "Signup page should include the repeat password field");
    }

    @Test
    public void signupPageShowsExistingUserSection() {
        driver.get(baseUrl + "signup.php");
        slowMoPause();

        Assert.assertFalse(driver.findElements(By.id("registration_form")).isEmpty(),
                "Signup page should also show the existing user login form");
        Assert.assertFalse(driver.findElements(By.cssSelector("input[name='login']")).isEmpty(),
                "Existing user section should include the sign in action");
    }

    @Test
    public void guestCanOpenForgotPasswordPage() {
        driver.get(baseUrl + "forgot-password.php");
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot-password.php"),
                "Forgot password page should open for guest users");
        Assert.assertTrue(pageText.contains("Forgot Password"),
                "Forgot password page should show its heading");
    }

    @Test
    public void guestCanOpenProductsPage() {
        driver.get(baseUrl + "products.php");
        slowMoPause();

        Assert.assertTrue(driver.getCurrentUrl().contains("products.php"),
                "Products page should open for guest users");
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().length() > 20,
                "Products page should render visible content");
    }
}
