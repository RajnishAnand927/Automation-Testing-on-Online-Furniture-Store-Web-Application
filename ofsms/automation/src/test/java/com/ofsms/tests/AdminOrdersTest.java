package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "orders", "regression"})
public class AdminOrdersTest extends BaseTest {

    @Test
    public void adminCanOpenOrderListPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "all-order.php");
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("Products Data Table") || pageText.contains("Order Number"),
                "Admin order list page should load");
        Assert.assertFalse(driver.findElements(By.cssSelector("table tbody tr")).isEmpty(),
                "Admin order list page should show at least one order row");
    }
}
