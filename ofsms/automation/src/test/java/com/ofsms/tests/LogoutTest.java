package com.ofsms.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

public class LogoutTest extends BaseTest {

    @Test(groups = {"user", "auth", "regression"})
    public void loggedInUserCanLogout() {
        registerAndLoginUser();

        driver.get(baseUrl + "logout.php");
        waitForUrlContains("signup.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("signup.php"),
                "User logout should redirect to signup/login page");
    }

    @Test(groups = {"admin", "auth", "regression"})
    public void adminCanLogout() {
        loginAsAdmin();

        driver.get(adminBaseUrl + "logout.php");
        waitForUrlContains("login.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("login.php"),
                "Admin logout should redirect to admin login page");
    }
}
