package com.ofsms.tests;

import com.ofsms.pages.HomePage;
import com.ofsms.pages.ProductsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

@Test(groups = {"user", "orders", "regression"})
public class UserOrderActionsTest extends BaseTest {

    @Test
    public void userCanNavigateHomeFromCartBreadcrumb() {
        registerAndLoginUser();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        driver.findElement(By.cssSelector(".breadcrumb a[href='index.php']")).click();
        waitForUrlContains("index.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("index.php"),
                "Home breadcrumb on cart page should return the user to the home page");
    }

    @Test
    public void userCanNavigateHomeFromMyOrdersBreadcrumb() {
        registerAndLoginUser();

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        driver.findElement(By.cssSelector(".breadcrumb a[href='index.php']")).click();
        waitForUrlContains("index.php");

        Assert.assertTrue(driver.getCurrentUrl().contains("index.php"),
                "Home breadcrumb on my orders page should return the user to the home page");
    }

    @Test
    public void userCanOpenViewDetailsFromMyOrders() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        String orderNumber = placeOrderAndReturnOrderNumber(user);

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        driver.findElement(By.cssSelector("a[href='order-detail.php?orderid=" + orderNumber + "']")).click();
        waitForUrlContains("order-detail.php");

        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(driver.getCurrentUrl().contains("orderid=" + orderNumber),
                "View Details should open the matching order detail page");
        Assert.assertTrue(pageText.contains("#" + orderNumber + " Order Details"),
                "Order detail page should show the selected order number");
        Assert.assertTrue(pageText.contains("Product Name"),
                "Order detail page should show the ordered product table");
    }

    @Test
    public void userCanCancelOrderFromOrderDetails() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        String orderNumber = placeOrderAndReturnOrderNumber(user);

        driver.get(baseUrl + "order-detail.php?orderid=" + orderNumber);
        slowMoPause();
        cancelOrderInPopup("Cancelled by automation for verification");

        driver.get(baseUrl + "order-detail.php?orderid=" + orderNumber);
        slowMoPause();
        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("Cancelled"),
                "Order detail page should show Cancelled after user cancellation");
    }

    @Test
    public void userCanTrackOrderInPopupWindow() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        String orderNumber = placeOrderAndReturnOrderNumber(user);

        driver.get(baseUrl + "order-detail.php?orderid=" + orderNumber);
        slowMoPause();
        cancelOrderInPopup("Cancelled before tracking verification");

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        String originalWindow = driver.getWindowHandle();
        driver.findElement(By.cssSelector("a[title='Track order'][onclick*='" + orderNumber + "']")).click();

        switchToNewWindow(originalWindow);
        String popupText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(popupText.contains("Tracking History #" + orderNumber),
                "Track Order popup should open with the correct order number");
        Assert.assertTrue(popupText.contains("Cancelled"),
                "Track Order popup should show the latest cancelled status");
        Assert.assertTrue(popupText.contains("by user"),
                "Track Order popup should indicate the cancellation came from the user side");

        driver.close();
        driver.switchTo().window(originalWindow);
    }

    @Test
    public void userCanOpenInvoiceInPopupWindow() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        String orderNumber = placeOrderAndReturnOrderNumber(user);

        driver.get(baseUrl + "order-detail.php?orderid=" + orderNumber);
        slowMoPause();
        String originalWindow = driver.getWindowHandle();
        driver.findElement(By.cssSelector("a[title='Invoice']")).click();

        switchToNewWindow(originalWindow);
        String popupText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(popupText.contains(orderNumber),
                "Invoice popup should include the selected order number");
        Assert.assertTrue(popupText.contains("Order Date"),
                "Invoice popup should show the order date");
        Assert.assertTrue(popupText.contains("Product Name"),
                "Invoice popup should list the ordered product details");

        driver.close();
        driver.switchTo().window(originalWindow);
    }

    private String placeOrderAndReturnOrderNumber(TestUser user) throws InterruptedException {
        addAnyProductToCart();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        driver.findElement(By.cssSelector("input[name='paytype'][value='Cash on Delivery']")).click();
        driver.findElement(By.name("fname")).sendKeys(user.getFullName());
        driver.findElement(By.name("cnumber")).sendKeys(user.getMobileNumber());
        driver.findElement(By.name("flatbldgnumber")).sendKeys("17A");
        driver.findElement(By.name("streename")).sendKeys("Lake Road");
        driver.findElement(By.name("area")).sendKeys("South Kolkata");
        driver.findElement(By.name("landmark")).sendKeys("Near City Centre");
        driver.findElement(By.name("city")).sendKeys("Kolkata");
        driver.findElement(By.name("zipcode")).sendKeys("700029");
        driver.findElement(By.name("state")).sendKeys("West Bengal");
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        WebElement orderHeading = driver.findElement(By.cssSelector(".cart-item-info h3"));
        String orderText = orderHeading.getText();
        int orderMarker = orderText.indexOf("Order #");
        int orderDateMarker = orderText.indexOf("Order Date");
        if (orderMarker < 0 || orderDateMarker < 0) {
            throw new IllegalStateException("Unable to extract order number from My Orders page: " + orderText);
        }
        return orderText.substring(orderMarker + 7, orderDateMarker).trim();
    }

    private void addAnyProductToCart() throws InterruptedException {
        driver.get(baseUrl);
        slowMoPause();
        HomePage home = new HomePage(driver);

        try {
            home.openFirstTopSellerProduct();
        } catch (Exception e) {
            home.openFirstCategory();
            ProductsPage products = new ProductsPage(driver);
            products.openFirstProduct();
        }

        ProductsPage products = new ProductsPage(driver);
        slowMoPause(Math.max(slowMoMs, 800));
        products.addToCart();
        slowMoPause();
    }

    private void cancelOrderInPopup(String cancellationReason) {
        String originalWindow = driver.getWindowHandle();
        driver.findElement(By.cssSelector("a[title='Cancel this order']")).click();
        switchToNewWindow(originalWindow);

        driver.findElement(By.name("restremark")).sendKeys(cancellationReason);
        slowMoPause();
        driver.findElement(By.cssSelector("button[name='submit']")).click();
        slowMoPause();

        if (!driver.getCurrentUrl().contains("order-detail.php")) {
            waitForUrlContains("order-detail.php");
        }
        driver.close();
        driver.switchTo().window(originalWindow);
    }

    private void switchToNewWindow(String originalWindow) {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String handle : windowHandles) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                slowMoPause();
                return;
            }
        }
        throw new IllegalStateException("Expected a popup window to open, but no new window handle was found.");
    }
}
