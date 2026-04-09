# TestOps Control Panel — Setup Guide

A PHP-based Test Execution Dashboard for running Java + Maven + TestNG automation tests.

## Project Structure

```
test-runner/
├── index.php        ← Dashboard UI + Login screen
├── run.php          ← Async test execution initiator
├── status.php       ← Job status & log polling API
├── download.php     ← Secure report file downloader
├── .htaccess        ← Apache security rules
├── logs/
│   ├── .htaccess    ← Block direct log access
│   ├── <jobId>.txt          ← Console output log
│   ├── <jobId>.status       ← Job state (running/completed/failed)
│   └── <jobId>_runner.bat   ← Generated launcher (auto-cleaned)
└── reports/
    ├── .htaccess    ← Block direct report access
    ├── <jobId>.pdf  ← Copied from Maven target/reports/
    └── <jobId>.txt  ← Copied from Maven target/reports/
```

## Prerequisites

- **XAMPP** (Apache + PHP 7.4+) running on Windows
- **Java JDK 8+** installed and in PATH
- **Apache Maven** installed and `mvn` in PATH (or set full path in `run.php`)
- **ChromeDriver** at `C:\drivers\chromedriver.exe` (configurable)
- **TestNG** configured in your Maven project
- A **TestNG Listener** that generates `target/reports/<reportName>.pdf` and `.txt`

## Installation

1. Copy the `test-runner/` folder to: `C:\xampp\htdocs\test-runner\`

2. Open `run.php` and update these constants:
   ```php
   define('PROJECT_DIR',   'C:\\your-automation-project');
   define('CHROME_DRIVER', 'C:\\drivers\\chromedriver.exe');
   define('MAVEN_HOME',    'mvn');  // or full path like 'C:\\apache-maven\\bin\\mvn'
   ```

3. Update `ADMIN_USER` and `ADMIN_PASS` in `index.php`:
   ```php
   define('ADMIN_USER', 'admin');
   define('ADMIN_PASS', 'YourSecurePassword');
   ```

4. Add your TestNG suite XML files to:
   `<PROJECT_DIR>\src\test\resources\suites\`
   - smoke.xml
   - regression.xml
   - admin.xml
   - user.xml
   - validation.xml
   - ui.xml

5. Ensure your TestNG Listener generates reports to:
   `<PROJECT_DIR>\target\reports\<reportName>.pdf`
   `<PROJECT_DIR>\target\reports\<reportName>.txt`

6. Enable `mod_headers` in XAMPP's `httpd.conf` (for security headers):
   Uncomment: `LoadModule headers_module modules/mod_headers.so`

7. Visit: `http://localhost/test-runner/`

## Default Credentials
- Username: `admin`
- Password:  `Test@1234`

**Change these before deploying!**

## Maven Command Generated

```
mvn test -f "C:\project\pom.xml"
         -Dsurefire.suiteXmlFiles="src\test\resources\suites\smoke.xml"
         -Dwebdriver.chrome.driver="C:\drivers\chromedriver.exe"
         -DreportName="smoke_20240101_120000"
```

## Security Notes

- Test type is **whitelisted** — user input never reaches the shell command
- All file downloads go through `download.php` with path traversal protection
- Direct access to `logs/` and `reports/` is blocked by `.htaccess`
- PHP sessions use `httponly` and `SameSite=Strict` cookies
- Job IDs are validated with strict regex before any file operations

## TestNG Listener (Java) — Expected Interface

Your listener should read the `reportName` system property:
```java
String reportName = System.getProperty("reportName", "report");
// Generate: target/reports/<reportName>.pdf
// Generate: target/reports/<reportName>.txt
```

## Polling Architecture

```
Browser → executeTest() → POST /run.php
                              ↓
                    Validates + generates jobId
                    Writes .bat launcher file
                    popen("start /B cmd /c <bat>")  ← non-blocking
                    Returns { jobId }
                              ↓
Browser ← startPolling() → GET /status.php?jobId=...  (every 2s)
                              ↓
                    Reads .status file → running/completed/failed
                    Reads .txt log file
                    Checks reports/ for PDF/TXT
                    Parses TestNG summary from log
                    Returns JSON
```
