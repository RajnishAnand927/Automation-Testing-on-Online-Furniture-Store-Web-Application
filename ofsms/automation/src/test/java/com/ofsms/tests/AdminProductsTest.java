package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "catalog", "regression"})
public class AdminProductsTest extends BaseTest {

    @Test
    public void adminCanOpenManageProductsPage() {
        loginAsAdmin();

        driver.get(adminBaseUrl + "manage-products.php");
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("Manage Furniture Products"),
                "Manage products page should show the products heading");
        Assert.assertFalse(driver.findElements(By.cssSelector("table tbody tr")).isEmpty(),
                "Manage products page should list at least one product");
    }
}
