<?php
/**
 * run.php — Test Execution Trigger
 *
 * Validates the requested test type, generates a unique job ID,
 * builds the Maven command, and launches it asynchronously so the
 * HTTP response returns immediately while the test continues running.
 *
 * Log file   → /logs/<jobId>.txt
 * Status file→ /logs/<jobId>.status
 */

session_start();
header('Content-Type: application/json');

// ── Auth guard ────────────────────────────────────────────────────────────────
if (empty($_SESSION['authenticated'])) {
    http_response_code(403);
    echo json_encode(['error' => 'Unauthorized']);
    exit;
}

// ── SECURITY: Whitelist of allowed test types ─────────────────────────────────
// Raw user input NEVER touches the shell command. Only values from this map
// are embedded in any command string.
const SUITE_MAP = [
    'smoke'      => 'testng-smoke.xml',
    'regression' => 'testng-regression.xml',
    'admin'      => 'testng-admin.xml',
    'user'       => 'testng-user.xml',
    'validation' => 'testng-auth.xml',
    'ui'         => 'testng-ui.xml',
];

// ── Environment paths — adjust to match your XAMPP / Windows setup ────────────
define('MAVEN_PROJECT_DIR', 'C:\xampp\htdocs\Online Furniture Shop Project\ofsms\automation');   // Maven project root
define('CHROMEDRIVER_PATH', 'C:\\drivers\\chromedriver.exe'); // ChromeDriver path
define('LOGS_DIR',    __DIR__ . DIRECTORY_SEPARATOR . 'logs');
define('REPORTS_DIR', __DIR__ . DIRECTORY_SEPARATOR . 'reports');

// ── Validate input ────────────────────────────────────────────────────────────
$testType = trim($_POST['testType'] ?? '');

if (!array_key_exists($testType, SUITE_MAP)) {
    http_response_code(400);
    echo json_encode(['error' => 'Invalid test type: ' . htmlspecialchars($testType)]);
    exit;
}

$suiteFile = SUITE_MAP[$testType]; // Safe: from whitelist

// ── Generate job ID ───────────────────────────────────────────────────────────
// Format: <testType>_YYYYMMDD_HHMMSS
$jobId = $testType . '_' . date('Ymd_His');

// ── Ensure directories exist ──────────────────────────────────────────────────
foreach ([LOGS_DIR, REPORTS_DIR] as $dir) {
    if (!is_dir($dir)) {
        mkdir($dir, 0755, true);
    }
}

$logFile    = LOGS_DIR . DIRECTORY_SEPARATOR . $jobId . '.txt';
$statusFile = LOGS_DIR . DIRECTORY_SEPARATOR . $jobId . '.status';

// ── Write initial status ──────────────────────────────────────────────────────
file_put_contents($statusFile, 'running');
file_put_contents($logFile,    '[' . date('H:i:s') . "] TestOps Control Center\n");
file_put_contents($logFile,    '[' . date('H:i:s') . "] Job ID   : $jobId\n", FILE_APPEND);
file_put_contents($logFile,    '[' . date('H:i:s') . "] Suite    : $suiteFile\n", FILE_APPEND);
file_put_contents($logFile,    '[' . date('H:i:s') . "] Started  : " . date('Y-m-d H:i:s') . "\n", FILE_APPEND);
file_put_contents($logFile,    '[' . date('H:i:s') . "] Launching Maven...\n\n", FILE_APPEND);

// ── Build Maven command (all values are from constants / whitelist) ────────────
$mvnCmd = sprintf(
    'cd /d "%s" && mvn test -Dsurefire.suiteXmlFiles="%s" -Dwebdriver.chrome.driver="%s" -DreportName="%s" >> "%s" 2>&1',
    MAVEN_PROJECT_DIR,
    $suiteFile,
    CHROMEDRIVER_PATH,
    $jobId,
    $logFile
);

// After Maven finishes, write the appropriate status file.
$writeOk   = 'echo completed> "' . $statusFile . '"';
$writeFail = 'echo failed> "'    . $statusFile . '"';

// ── Async launch via popen ────────────────────────────────────────────────────
// start /B launches the process detached; pclose immediately releases PHP.
// The cmd /c wrapper runs our pipeline and writes the status when done.
$fullCmd = 'start /B cmd /c "(' . $mvnCmd . ') && (' . $writeOk . ') || (' . $writeFail . ')"';

$handle = popen($fullCmd, 'r');
if ($handle === false) {
    file_put_contents($statusFile, 'failed');
    file_put_contents($logFile, '[ERROR] Failed to launch Maven process.\n', FILE_APPEND);
    echo json_encode(['error' => 'Could not start test process']);
    exit;
}
pclose($handle);

// ── Respond to frontend with job info ─────────────────────────────────────────
echo json_encode([
    'jobId'     => $jobId,
    'testType'  => $testType,
    'suiteFile' => $suiteFile,
    'startedAt' => date('Y-m-d H:i:s'),
]);
