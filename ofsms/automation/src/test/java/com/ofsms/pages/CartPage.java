package com.ofsms.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage {
    private final WebDriver driver;
    private final By cartRows = By.cssSelector("#shopping-cart table tbody tr");
    private final By emptyMessage = By.cssSelector(".no-records");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean hasItems() {
        return driver.findElements(emptyMessage).isEmpty() && driver.findElements(cartRows).size() > 1;
    }
}
