package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "orders", "regression"})
public class AdminOrderStatusTest extends BaseTest {

    @Test
    public void adminCanUpdateOrderStatusToConfirmed() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        String orderNumber = placeOrderAndReturnOrderNumber(user);

        loginAsAdmin();
        driver.get(adminBaseUrl + "view-order-detail.php?viewid=" + orderNumber);
        slowMoPause();
        driver.findElement(By.cssSelector("button[data-target='#myModal']")).click();
        slowMoPause();
        driver.findElement(By.name("remark")).sendKeys("Confirmed by automation");
        driver.findElement(By.name("status")).sendKeys("Confirmed");
        slowMoPause();
        driver.findElement(By.cssSelector("#myModal button[name='submit'], #myModal .btn.btn-primary")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "order-detail.php?orderid=" + orderNumber);
        slowMoPause();
        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("Confirmed"),
                "Updated order status should be visible on the user order detail page");
    }

    private String placeOrderAndReturnOrderNumber(TestUser user) throws InterruptedException {
        CheckoutTest checkoutFlow = new CheckoutTest();
        checkoutFlow.driver = this.driver;
        checkoutFlow.baseUrl = this.baseUrl;
        checkoutFlow.adminBaseUrl = this.adminBaseUrl;
        checkoutFlow.slowMoMs = this.slowMoMs;
        checkoutFlow.addAnyProductToCart();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        driver.findElement(By.cssSelector("input[name='paytype'][value='Cash on Delivery']")).click();
        driver.findElement(By.name("fname")).sendKeys(user.getFullName());
        driver.findElement(By.name("cnumber")).sendKeys(user.getMobileNumber());
        driver.findElement(By.name("flatbldgnumber")).sendKeys("42B");
        driver.findElement(By.name("streename")).sendKeys("Main Road");
        driver.findElement(By.name("area")).sendKeys("South");
        driver.findElement(By.name("landmark")).sendKeys("Near Park");
        driver.findElement(By.name("city")).sendKeys("Kolkata");
        driver.findElement(By.name("zipcode")).sendKeys("700020");
        driver.findElement(By.name("state")).sendKeys("West Bengal");
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        String pageText = driver.findElement(By.tagName("body")).getText();
        int orderMarker = pageText.indexOf("Order #");
        int orderEnd = pageText.indexOf("Order Date", orderMarker);
        return pageText.substring(orderMarker + 7, orderEnd).trim();
    }
}
