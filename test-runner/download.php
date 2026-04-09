<?php
/**
 * download.php — Secure Report Download Endpoint
 *
 * Serves PDF and TXT report files from the /reports directory.
 * Prevents path traversal by:
 *   1. Stripping all non-safe characters from jobId
 *   2. Using realpath() to verify the resolved file path stays
 *      inside the allowed reports directory
 *   3. Only serving files with the exact allowed extensions
 */

session_start();

// ── Auth guard ────────────────────────────────────────────────────────────────
if (empty($_SESSION['authenticated'])) {
    http_response_code(403);
    echo 'Unauthorized';
    exit;
}

define('REPORTS_DIR', realpath(__DIR__ . DIRECTORY_SEPARATOR . 'reports'));

// ── Allowed file types ────────────────────────────────────────────────────────
const ALLOWED_TYPES = [
    'pdf' => ['ext' => 'pdf', 'mime' => 'application/pdf'],
    'txt' => ['ext' => 'txt', 'mime' => 'text/plain'],
];

// ── Validate 'type' parameter ─────────────────────────────────────────────────
$type = $_GET['type'] ?? '';
if (!array_key_exists($type, ALLOWED_TYPES)) {
    http_response_code(400);
    echo 'Invalid file type';
    exit;
}

// ── Validate and sanitise 'jobId' parameter ───────────────────────────────────
// Allow only alphanumeric characters and underscores (matches run.php output).
$rawJobId = $_GET['jobId'] ?? '';
$jobId    = preg_replace('/[^a-z0-9_]/i', '', $rawJobId);

if (empty($jobId)) {
    http_response_code(400);
    echo 'Invalid jobId';
    exit;
}

// ── Build and verify file path ────────────────────────────────────────────────
$fileInfo    = ALLOWED_TYPES[$type];
$filename    = $jobId . '.' . $fileInfo['ext'];
$requestedPath = REPORTS_DIR . DIRECTORY_SEPARATOR . $filename;

// realpath() returns false if the file doesn't exist or can't be resolved.
$resolvedPath = realpath($requestedPath);

if ($resolvedPath === false) {
    http_response_code(404);
    echo 'Report not found';
    exit;
}

// ── Path traversal check ──────────────────────────────────────────────────────
// Ensure the resolved path starts with the reports directory.
// This blocks tricks like ../../etc/passwd even if realpath resolves them.
if (strpos($resolvedPath, REPORTS_DIR) !== 0) {
    http_response_code(403);
    echo 'Access denied';
    exit;
}

// ── Serve the file ────────────────────────────────────────────────────────────
$fileSize = filesize($resolvedPath);

header('Content-Type: '               . $fileInfo['mime']);
header('Content-Disposition: attachment; filename="' . $filename . '"');
header('Content-Length: '             . $fileSize);
header('Cache-Control: no-cache, must-revalidate');
header('Pragma: no-cache');

// Stream in chunks to handle large files without exhausting memory
$handle = fopen($resolvedPath, 'rb');
if ($handle === false) {
    http_response_code(500);
    echo 'Could not read file';
    exit;
}

while (!feof($handle)) {
    echo fread($handle, 8192);
    flush();
}
fclose($handle);
