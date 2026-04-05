package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "orders", "regression"})
public class OrderHistoryTest extends BaseTest {

    @Test
    public void loggedInUserCanOpenOrderHistoryPage() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        addAnyProductToCart();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        driver.findElement(By.cssSelector("input[name='paytype'][value='Cash on Delivery']")).click();
        slowMoPause();
        driver.findElement(By.name("fname")).sendKeys(user.getFullName());
        slowMoPause();
        driver.findElement(By.name("cnumber")).sendKeys(user.getMobileNumber());
        slowMoPause();
        driver.findElement(By.name("flatbldgnumber")).sendKeys("12A");
        slowMoPause();
        driver.findElement(By.name("streename")).sendKeys("Park Street");
        slowMoPause();
        driver.findElement(By.name("area")).sendKeys("Central");
        slowMoPause();
        driver.findElement(By.name("landmark")).sendKeys("Near Metro");
        slowMoPause();
        driver.findElement(By.name("city")).sendKeys("Kolkata");
        slowMoPause();
        driver.findElement(By.name("zipcode")).sendKeys("700016");
        slowMoPause();
        driver.findElement(By.name("state")).sendKeys("West Bengal");
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "my-order.php");
        slowMoPause();

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("my-order.php"),
                "Logged-in user should remain on the order history page");
        Assert.assertTrue(pageText.contains("Your Order Detail"),
                "Order history page should show the order detail heading");
        Assert.assertTrue(pageText.contains("Order #"),
                "Order history page should list at least one order after checkout");
    }

    private void addAnyProductToCart() throws InterruptedException {
        CheckoutTest checkoutFlow = new CheckoutTest();
        checkoutFlow.driver = this.driver;
        checkoutFlow.baseUrl = this.baseUrl;
        checkoutFlow.adminBaseUrl = this.adminBaseUrl;
        checkoutFlow.slowMoMs = this.slowMoMs;
        checkoutFlow.addAnyProductToCart();
    }
}
