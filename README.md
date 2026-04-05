# Automation Testing on Online Furniture Store Web Application

This repository contains a PHP-based e-commerce web application for an online furniture store together with a Selenium + TestNG automation framework for end-to-end testing.

## Project Structure

- `ofsms/`
  The main Online Furniture Store web application built with PHP, HTML, CSS, JavaScript, and MySQL.
- `ofsms/automation/`
  The automated test suite built with Java, Maven, Selenium WebDriver, and TestNG.
- `SQL File/ofsmsdb.sql`
  Database dump for the application.

## Main Features

- User signup, login, logout, forgot password, and profile management
- Product browsing, cart, checkout, order history, order tracking, invoice, and cancellation
- Admin login and management pages for categories, brands, subcategories, products, users, and orders
- Flash-message based user feedback instead of browser `alert()` popups
- Automated reporting with screenshots, text summary, PDF summary, Surefire, and Allure outputs

## Demo Credentials

### Admin Panel

- Username: `admin`
- Password: `Test@123`

### User Panel

- Username: `test@test.com`
- Password: `Test@123`

## Automation Coverage

The automation suite currently covers:

- User authentication flows
- Guest-accessible pages
- Logged-in user pages
- Cart and checkout
- Order history, view details, cancellation, tracking popup, and invoice popup
- Admin login and admin management pages
- Admin order status updates

The suite currently runs `56` passing tests in the main regression run.

## Prerequisites

Before running the app or tests, make sure you have:

- XAMPP with Apache and MySQL
- Java 11 or above
- Maven
- Google Chrome
- Matching ChromeDriver, for example: `C:\drivers\chromedriver.exe`

## Local Setup

1. Copy the project into your XAMPP `htdocs` folder.
2. Start Apache and MySQL from XAMPP.
3. Import the database from:
   `SQL File/ofsmsdb.sql`
4. Open the application in a browser:

```text
http://localhost/Online%20Furniture%20Shop%20Project/ofsms/
```

## Running the Automation Suite

Open PowerShell and move to the automation folder:

```powershell
cd "C:\xampp\htdocs\Online Furniture Shop Project\ofsms\automation"
```

Run the main suite:

```powershell
mvn test "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run the full regression suite:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-regression.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run the smoke suite:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-smoke.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run the user suite:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-user.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run the admin suite:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-admin.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run the auth suite:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-auth.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe"
```

Run with visible browser and slow motion for demos:

```powershell
mvn test "-Dsurefire.suiteXmlFiles=testng-user.xml" "-Dwebdriver.chrome.driver=C:\drivers\chromedriver.exe" -DslowMoMs=1500
```

## Test Reports

After execution, reports are generated in:

- `ofsms/automation/target/reports/test-summary.txt`
- `ofsms/automation/target/reports/test-summary.pdf`
- `ofsms/automation/target/screenshots`
- `ofsms/automation/target/surefire-reports`
- `ofsms/automation/target/allure-results`

## Notes

- A quick command reference is also available in:
  `ofsms/automation/RUN_TESTS.txt`
- The automation framework supports grouped suites so you do not need to run everything at once.
- The project has already been pushed to GitHub and is ready for future commits.

## Tech Stack

- PHP
- HTML / CSS / JavaScript
- MySQL
- Java
- Selenium WebDriver
- TestNG
- Maven
- Allure
- GitHub Actions
