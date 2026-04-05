package com.ofsms.tests;

import com.ofsms.pages.CartPage;
import com.ofsms.pages.HomePage;
import com.ofsms.pages.ProductsPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "checkout", "smoke", "regression"})
public class CheckoutTest extends BaseTest {

    @Test
    public void loggedInUserCanPlaceOrderWithCod() throws InterruptedException {
        TestUser user = registerAndLoginUser();
        addAnyProductToCart();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.hasItems(), "Cart should contain the selected product before checkout");

        driver.findElement(By.cssSelector("input[name='paytype'][value='Cash on Delivery']")).click();
        slowMoPause();
        driver.findElement(By.name("fname")).sendKeys(user.getFullName());
        slowMoPause();
        driver.findElement(By.name("cnumber")).sendKeys(user.getMobileNumber());
        slowMoPause();
        driver.findElement(By.name("flatbldgnumber")).sendKeys("221B");
        slowMoPause();
        driver.findElement(By.name("streename")).sendKeys("Baker Street");
        slowMoPause();
        driver.findElement(By.name("area")).sendKeys("Central");
        slowMoPause();
        driver.findElement(By.name("landmark")).sendKeys("Near City Mall");
        slowMoPause();
        driver.findElement(By.name("city")).sendKeys("Kolkata");
        slowMoPause();
        driver.findElement(By.name("zipcode")).sendKeys("700001");
        slowMoPause();
        driver.findElement(By.name("state")).sendKeys("West Bengal");
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        waitForAlertAndAccept();

        driver.get(baseUrl + "my-order.php");
        slowMoPause();
        String pageText = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageText.contains("Your Order Detail"), "Order history page should open after placing an order");
        Assert.assertTrue(pageText.contains("Order #"), "New user should have at least one order after checkout");
    }

    protected void addAnyProductToCart() throws InterruptedException {
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
}
