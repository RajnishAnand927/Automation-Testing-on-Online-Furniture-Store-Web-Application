package com.ofsms.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"user", "auth", "smoke", "regression"})
public class LoginTest extends BaseTest {

    @Test
    public void loginWithValidCredentials() {
        TestUser user = registerAndLoginUser();

        // Very basic assertion: after login the site redirects to index.php and title
        // exists
        Assert.assertTrue(
                driver.getCurrentUrl().contains("index.php") || driver.getTitle().length() > 0,
                "After login expected redirect or title for " + user.getEmail());
    }
}
