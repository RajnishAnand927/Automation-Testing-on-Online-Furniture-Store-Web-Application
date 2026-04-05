package com.ofsms.tests;

import com.ofsms.pages.CartPage;
import com.ofsms.pages.HomePage;
import com.ofsms.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "cart", "smoke", "regression"})
public class AddToCartTest extends BaseTest {

    @Test
    public void addProductToCart() throws InterruptedException {
        registerAndLoginUser();
        addAnyProductToCart();

        driver.get(baseUrl + "cart.php");
        slowMoPause();
        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.hasItems(), "Cart should have at least one item after adding product");
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
    }
}
