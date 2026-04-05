package com.ofsms.tests;

import com.ofsms.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForgotPasswordTest extends BaseTest {

    @Test(groups = {"user", "auth", "regression"})
    public void userCanResetPasswordWithRegisteredEmailAndMobile() {
        TestUser user = registerUser();
        String newPassword = "NewPass@123";

        driver.get(baseUrl + "forgot-password.php");
        slowMoPause();
        driver.findElement(By.name("email")).sendKeys(user.getEmail());
        driver.findElement(By.name("mobile")).sendKeys(user.getMobileNumber());
        driver.findElement(By.name("newpassword")).sendKeys(newPassword);
        driver.findElement(By.name("confirmpassword")).sendKeys(newPassword);
        driver.findElement(By.name("submit")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "signup.php");
        new LoginPage(driver).login(user.getEmail(), newPassword);
        waitForUrlContains("index.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("index.php"),
                "User should be able to log in with the reset password");
    }

    @Test(groups = {"admin", "auth", "regression"})
    public void adminCanResetPasswordWithRegisteredEmailAndMobile() {
        String newPassword = "Admin@456";
        boolean loginSucceeded = false;

        driver.get(adminBaseUrl + "forgot-password.php");
        slowMoPause();
        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.name("mobile")).sendKeys("7572908503");
        driver.findElement(By.name("newpassword")).sendKeys(newPassword);
        driver.findElement(By.name("confirmpassword")).sendKeys(newPassword);
        ((JavascriptExecutor) driver).executeScript(
                "var form=document.forms[0];"
                        + "var hidden=document.createElement('input');"
                        + "hidden.type='hidden';"
                        + "hidden.name='submit';"
                        + "hidden.value='Reset';"
                        + "form.appendChild(hidden);"
                        + "HTMLFormElement.prototype.submit.call(form);");
        waitForAlertAndAccept();

        driver.get(adminBaseUrl + "login.php");
        slowMoPause();
        driver.findElement(By.name("username")).sendKeys("admin");
        driver.findElement(By.name("password")).sendKeys(newPassword);
        driver.findElement(By.cssSelector("button[name='login']")).click();
        try {
            waitForUrlContains("dashboard.php");
            loginSucceeded = true;
        } finally {
            driver.get(adminBaseUrl + "forgot-password.php");
            slowMoPause();
            driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
            driver.findElement(By.name("mobile")).sendKeys("7572908503");
            driver.findElement(By.name("newpassword")).sendKeys("Test@123");
            driver.findElement(By.name("confirmpassword")).sendKeys("Test@123");
            ((JavascriptExecutor) driver).executeScript(
                    "var form=document.forms[0];"
                            + "var hidden=document.createElement('input');"
                            + "hidden.type='hidden';"
                            + "hidden.name='submit';"
                            + "hidden.value='Reset';"
                            + "form.appendChild(hidden);"
                            + "HTMLFormElement.prototype.submit.call(form);");
            waitForAlertAndAccept();
        }

        Assert.assertTrue(loginSucceeded,
                "Admin should be able to log in after resetting the password");
    }
}
