package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "auth", "regression"})
public class ProfileTest extends BaseTest {

    @Test
    public void loggedInUserCanUpdateProfile() {
        TestUser user = registerAndLoginUser();
        String updatedName = user.getFullName() + " Updated";
        String updatedMobile = "8888888888";

        driver.get(baseUrl + "profile.php");
        slowMoPause();
        driver.findElement(By.name("fname")).clear();
        driver.findElement(By.name("fname")).sendKeys(updatedName);
        slowMoPause();
        driver.findElement(By.name("mobno")).clear();
        driver.findElement(By.name("mobno")).sendKeys(updatedMobile);
        slowMoPause();
        driver.findElement(By.name("submit")).click();
        slowMoPause();
        String pageTextAfterSave = driver.findElement(By.tagName("body")).getText();
        Assert.assertTrue(pageTextAfterSave.contains("Profile updated successfully."),
                "Profile page should show a success flash message after saving");
        slowMoPause();

        driver.get(baseUrl + "profile.php");
        slowMoPause();

        Assert.assertEquals(driver.findElement(By.name("fname")).getAttribute("value"), updatedName,
                "Updated full name should persist on the profile page");
        Assert.assertEquals(driver.findElement(By.name("mobno")).getAttribute("value"), updatedMobile,
                "Updated mobile number should persist on the profile page");
        Assert.assertEquals(driver.findElement(By.name("email")).getAttribute("value"), user.getEmail(),
                "Email should remain bound to the registered user");
    }
}
