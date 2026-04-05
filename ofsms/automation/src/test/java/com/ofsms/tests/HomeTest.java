package com.ofsms.tests;

import com.ofsms.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "guest", "smoke", "regression"})
public class HomeTest extends BaseTest {

    @Test
    public void openHomeAndCheckTitle() {
        driver.get(baseUrl);
        slowMoPause();
        HomePage home = new HomePage(driver);
        String title = home.getTitle();
        // basic sanity check - adjust according to your installed app
        Assert.assertTrue(title.length() > 0, "Page title should not be empty");
    }
}
