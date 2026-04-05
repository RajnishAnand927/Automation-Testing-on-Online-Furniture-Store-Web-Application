package com.ofsms.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ProductsPage {
    private final WebDriver driver;
    private final By firstProduct = By.cssSelector("a[href*='single-product-detail.php?proid=']");
    private final By addToCartButton = By.cssSelector("input.btnAddAction, input[value='Add to Cart']");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
    }

    public void openFirstProduct() {
        driver.findElement(firstProduct).click();
    }

    public void addToCart() {
        driver.findElement(addToCartButton).click();
    }
}
