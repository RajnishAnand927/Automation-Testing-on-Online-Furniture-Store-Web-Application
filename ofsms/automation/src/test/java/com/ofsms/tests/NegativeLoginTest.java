package com.ofsms.tests;

import com.ofsms.pages.LoginPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

public class NegativeLoginTest extends BaseTest {

    @Test(groups = {"user", "auth", "regression"})
    public void userLoginFailsWithInvalidPassword() {
        TestUser user = registerUser();

        driver.get(baseUrl + "signup.php");
        slowMoPause();
        new LoginPage(driver).login(user.getEmail(), "WrongPass@123");
        waitForAlertAndAccept();

        Assert.assertTrue(driver.getCurrentUrl().contains("signup.php"),
                "User should remain on login page after invalid login");
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains("Invalid email or password."),
                "User should see an inline error message after invalid login");
    }

    @Test(groups = {"admin", "auth", "regression"})
    public void adminLoginFailsWithInvalidPassword() {
        driver.get(adminBaseUrl + "login.php");
        slowMoPause();
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys("WrongPass@123");
        driver.findElement(By.cssSelector("button[name='login']")).click();
        waitForAlertAndAccept();

        Assert.assertTrue(driver.getCurrentUrl().contains("login.php"),
                "Admin should remain on login page after invalid login");
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains("Invalid username or password."),
                "Admin should see an error message after invalid login");
    }
}
