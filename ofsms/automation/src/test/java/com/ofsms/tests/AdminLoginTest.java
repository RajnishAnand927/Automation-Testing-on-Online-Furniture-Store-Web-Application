package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "auth", "smoke", "regression"})
public class AdminLoginTest extends BaseTest {

    @Test
    public void adminCanLoginAndSeeDashboard() {
        loginAsAdmin();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard.php"),
                "Admin should land on the dashboard after login");
        Assert.assertTrue(pageText.contains("Total Orders"),
                "Dashboard should show order summary cards");
    }
}
