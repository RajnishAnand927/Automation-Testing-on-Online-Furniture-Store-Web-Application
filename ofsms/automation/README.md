# OFSMS Selenium + TestNG Automation

This folder contains a starter Selenium + TestNG framework for the Online Furniture Shop Project.

Location: `ofsms/automation`

Prerequisites

- Java 11+
- Maven
- Chrome installed (tests use ChromeDriver by default)
- Local webserver running the application (XAMPP)

Run tests

1. Start your webserver and make sure the app is available at the `baseUrl` configured in `testng.xml` or `src/test/resources/config.properties`.
2. From the `ofsms/automation` folder run:

```bash
mvn -q test
```

Useful overrides

```bash
# point the suite at a manually downloaded ChromeDriver
mvn test -Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe

# override the application URL
mvn test -DbaseUrl=http://127.0.0.1:8000/

# force headless mode locally
mvn test -Dheadless=true

# skip WebDriverManager cache clearing when not needed
mvn test -Dwdm.clearCache=false
```

Reports

- TestNG/Surefire reports are written to `target/surefire-reports`.
- Failure screenshots are written to `target/screenshots`.
- Allure result files are written to `target/allure-results`.

Notes

- The page object locators are generic placeholders and may need to be adjusted to match the actual HTML of your site (e.g., input names, button text).
- Update credentials in `LoginTest` before running login tests.
- The framework now prefers `-Dwebdriver.chrome.driver=...` when provided and falls back to WebDriverManager otherwise.
- By default the WebDriverManager driver and resolution caches are cleared before setup to avoid stale driver resolution issues.

Next steps you may want me to do:

- Add cross-browser support and CI pipeline (GitHub Actions)
- Add data-driven tests and reporting
- Flesh out selectors based on your app's HTML
