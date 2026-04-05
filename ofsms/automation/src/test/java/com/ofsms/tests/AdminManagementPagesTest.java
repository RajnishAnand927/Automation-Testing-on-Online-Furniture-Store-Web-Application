package com.ofsms.tests;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = {"admin", "catalog", "regression"})
public class AdminManagementPagesTest extends BaseTest {

    @Test
    public void adminCanOpenDashboardPage() {
        loginAsAdmin();
        assertAdminPageLoads("dashboard.php");
    }

    @Test
    public void adminCanOpenAddCategoryPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "add-category.php");
        slowMoPause();
        assertAdminPageLoads("add-category.php");
    }

    @Test
    public void adminCanOpenAddBrandPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "add-brand.php");
        slowMoPause();
        assertAdminPageLoads("add-brand.php");
    }

    @Test
    public void adminCanOpenAddSubcategoryPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "add-subcategory.php");
        slowMoPause();
        assertAdminPageLoads("add-subcategory.php");
    }

    @Test
    public void adminCanOpenAddProductsPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "add-products.php");
        slowMoPause();
        assertAdminPageLoads("add-products.php");
    }

    @Test
    public void adminCanOpenManageCategoryPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "manage-category.php");
        slowMoPause();
        assertAdminPageLoads("manage-category.php");
    }

    @Test
    public void adminCanOpenManageBrandPage() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "manage-brand.php");
        slowMoPause();
        assertAdminPageLoads("manage-brand.php");
    }

    @Test
    public void adminCanOpenManageProductsPageAgain() {
        loginAsAdmin();
        driver.get(adminBaseUrl + "manage-products.php");
        slowMoPause();
        assertAdminPageLoads("manage-products.php");
    }

    private void assertAdminPageLoads(String pageName) {
        Assert.assertTrue(driver.getCurrentUrl().contains(pageName),
                "Expected admin page to load: " + pageName);
        Assert.assertTrue(driver.findElement(By.tagName("body")).getText().length() > 20,
                "Expected admin page content for: " + pageName);
    }
}
