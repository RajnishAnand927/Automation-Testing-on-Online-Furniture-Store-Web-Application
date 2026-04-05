package com.ofsms.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;
    private final By emailInput = By.cssSelector("form[name='login'] input[name='email']");
    private final By passwordInput = By.cssSelector("form[name='login'] input[name='password']");
    private final By submitBtn = By
            .cssSelector("form[name='login'] input[name='login'], form[name='login'] button[name='login']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String email, String password) {
        driver.findElement(emailInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(submitBtn).click();
    }
}
