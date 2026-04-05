package com.ofsms.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    private final WebDriver driver;
    private final By firstTopSellerLink = By.cssSelector(".seller-grid a[href*='single-product-detail.php?proid=']");
    private final By firstCategoryLink = By.cssSelector(".h_nav a[href*='category-details.php?catid=']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public void openFirstTopSellerProduct() {
        driver.findElement(firstTopSellerLink).click();
    }

    public void openFirstCategory() {
        driver.findElement(firstCategoryLink).click();
    }
}
