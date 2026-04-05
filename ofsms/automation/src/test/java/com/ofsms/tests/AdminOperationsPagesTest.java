package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "regression"})
public class AdminOperationsPagesTest extends BaseTest {

    @Test
    public void adminCanOpenManageSubcategoryPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "manage-subcategory.php");
        slowMoPause();
        assertAdminPageLoads("manage-subcategory.php");
    }

    @Test
    public void adminCanOpenAdminProfilePage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "admin-profile.php");
        slowMoPause();
        assertAdminPageLoads("admin-profile.php");
    }

    @Test
    public void adminCanOpenAdminChangePasswordPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "change-password.php");
        slowMoPause();
        assertAdminPageLoads("change-password.php");
    }

    @Test
    public void adminCanOpenNewOrdersPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "new-order.php");
        slowMoPause();
        assertAdminPageLoads("new-order.php");
    }

    @Test
    public void adminCanOpenAllOrdersPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "all-order.php");
        slowMoPause();
        assertAdminPageLoads("all-order.php");
    }

    @Test
    public void adminCanOpenConfirmedOrdersPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "confirmed-order.php");
        slowMoPause();
        assertAdminPageLoads("confirmed-order.php");
    }

    @Test
    public void adminCanOpenCancelledOrdersPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "cancelled-order.php");
        slowMoPause();
        assertAdminPageLoads("cancelled-order.php");
    }

    @Test
    public void adminCanOpenRegisteredUsersPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "reg-users.php");
        slowMoPause();
        assertAdminPageLoads("reg-users.php");
    }

    private void assertAdminPageLoads(String pageName) {
        Assert.assertTrue(driver.getCurrentUrl().contains(pageName),
                "Expected admin page to load: " + pageName);
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().length() > 20,
                "Expected admin page content for: " + pageName);
    }
}
