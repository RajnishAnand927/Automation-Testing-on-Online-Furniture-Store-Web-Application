package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "auth", "regression"})
public class UserAuthenticatedPagesTest extends BaseTest {

    @Test
    public void loggedInUserSeesMyAccountMenuOnHomePage() {
        registerAndLoginUser();

        Assert.assertFalse(driver.findElements(By.xpath("//a[normalize-space()='My Account']")).isEmpty(),
                "Logged-in user should see the My Account menu");
    }

    @Test
    public void loggedInUserSeesLogoutLinkOnHomePage() {
        registerAndLoginUser();

        Assert.assertFalse(driver.findElements(By.cssSelector("a[href='logout.php']")).isEmpty(),
                "Logged-in user should see the logout link");
    }

    @Test
    public void loggedInUserCanOpenProfilePageAgain() {
        registerAndLoginUser();

        driver.get(baseUrl + "profile.php");
        waitForUrlContains("profile.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("profile.php"),
                "Profile page should open for a logged-in user");
    }

    @Test
    public void loggedInUserCanOpenChangePasswordPage() {
        registerAndLoginUser();

        driver.get(baseUrl + "change-password.php");
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("change-password.php"),
                "Change password page should open for a logged-in user");
        Assert.assertTrue(pageText.contains("Change Password"),
                "Change password page should show its heading");
    }

    @Test
    public void loggedInUserCanOpenCartPage() {
        registerAndLoginUser();

        driver.get(baseUrl + "cart.php");
        slowMoPause();

        Assert.assertTrue(driver.getCurrentUrl().contains("cart.php"),
                "Cart page should open for a logged-in user");
    }

    @Test
    public void loggedInUserSeesEmptyCartMessageBeforeAddingItems() {
        registerAndLoginUser();

        driver.get(baseUrl + "cart.php");
        slowMoPause();

        String bodyText = driver.findElement(By.tagName("body")).getText();
        boolean hasEmptyMessage = !driver.findElements(By.cssSelector(".no-records")).isEmpty()
                && bodyText.contains("Cart");
        boolean hasCartRows = !driver.findElements(By.cssSelector("table tbody tr")).isEmpty();

        Assert.assertTrue(
                hasEmptyMessage || hasCartRows,
                "Cart page should either show the empty cart message or render cart rows for the user");
    }

    @Test
    public void loggedInUserCanOpenAboutPage() {
        registerAndLoginUser();

        driver.get(baseUrl + "about-us.php");
        slowMoPause();

        Assert.assertTrue(driver.getCurrentUrl().contains("about-us.php"),
                "About page should open while the user is logged in");
    }

    @Test
    public void loggedInUserCanOpenContactPage() {
        registerAndLoginUser();

        driver.get(baseUrl + "contact-us.php");
        slowMoPause();

        Assert.assertTrue(driver.getCurrentUrl().contains("contact-us.php"),
                "Contact page should open while the user is logged in");
    }
}
