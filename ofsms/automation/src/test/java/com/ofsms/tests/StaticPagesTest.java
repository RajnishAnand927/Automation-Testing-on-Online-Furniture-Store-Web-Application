package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "guest", "regression"})
public class StaticPagesTest extends BaseTest {

    @Test
    public void contactPageShowsBusinessDetails() {
        driver.get(baseUrl + "contact-us.php");
        slowMoPause();

        Assert.assertTrue(driver.getTitle().contains("Contact"), "Contact page title should contain Contact");
        Assert.assertTrue(driver.getCurrentUrl().contains("contact-us.php"),
                "Contact page should remain on contact-us.php");
        Assert.assertFalse(driver.findElements(By.xpath("//*[contains(text(),'Email')]")).isEmpty(),
                "Contact page should render email details");
        Assert.assertFalse(driver.findElements(By.xpath("//*[contains(text(),'Mobile Number') or contains(text(),'Telephone')]")).isEmpty(),
                "Contact page should render phone details");
    }

    @Test
    public void aboutPageShowsDescription() {
        driver.get(baseUrl + "about-us.php");
        slowMoPause();

        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().contains("About Us"),
                "About page should contain About Us content");
        Assert.assertFalse(driver.findElements(By.cssSelector(".contact-left p")).isEmpty(),
                "About page should show the about-us description");
    }
}
