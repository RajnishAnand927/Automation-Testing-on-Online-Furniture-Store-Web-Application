<?php
/**
 * Test Execution Dashboard — index.php
 * Main entry point: handles login session and renders the dashboard UI.
 */
session_start();

// ── Simple hardcoded admin credentials ──────────────────────────────────────
// In production, replace with a database lookup + password_hash/password_verify.
define('ADMIN_USER', 'admin');
define('ADMIN_PASS', 'Test@123');   // ← change before deployment

$loginError = '';

// Handle login form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action']) && $_POST['action'] === 'login') {
    $u = trim($_POST['username'] ?? '');
    $p = trim($_POST['password'] ?? '');

    if ($u === ADMIN_USER && $p === ADMIN_PASS) {
        $_SESSION['authenticated'] = true;
        $_SESSION['user'] = $u;
        header('Location: index.php');
        exit;
    } else {
        $loginError = 'Invalid credentials. Please try again.';
    }
}

// Handle logout
if (isset($_GET['logout'])) {
    session_destroy();
    header('Location: index.php');
    exit;
}

$isLoggedIn = !empty($_SESSION['authenticated']);
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Automation Testing Control Center</title>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link href="https://fonts.googleapis.com/css2?family=Share+Tech+Mono&family=Exo+2:wght@300;400;600;800&display=swap" rel="stylesheet">
<style>
/* ═══════════════════════════════════════════════════════════════
   CSS VARIABLES & RESET
═══════════════════════════════════════════════════════════════ */
:root {
    --bg:        #050a0f;
    --bg2:       #0a1520;
    --bg3:       #0e1e2e;
    --panel:     #0d1b2a;
    --border:    #1a3a5c;
    --accent:    #00d4ff;
    --accent2:   #00ff9d;
    --danger:    #ff3e6c;
    --warn:      #ffb800;
    --text:      #c8e6f7;
    --text-dim:  #5a8aaa;
    --mono:      'Share Tech Mono', monospace;
    --sans:      'Exo 2', sans-serif;
}
*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
html, body { height: 100%; background: var(--bg); color: var(--text); font-family: var(--sans); overflow-x: hidden; }

/* Scanline overlay */
body::before {
    content: ''; position: fixed; inset: 0; z-index: 9999;
    background: repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,.07) 2px, rgba(0,0,0,.07) 4px);
    pointer-events: none;
}
/* Grid background */
body::after {
    content: ''; position: fixed; inset: 0; z-index: 0;
    background-image: linear-gradient(rgba(0,212,255,.03) 1px, transparent 1px), linear-gradient(90deg, rgba(0,212,255,.03) 1px, transparent 1px);
    background-size: 40px 40px; pointer-events: none;
}

/* ═══════════════════════════════════════════════════════════════
   LOGIN SCREEN
═══════════════════════════════════════════════════════════════ */
.login-wrap { position: relative; z-index: 1; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 2rem; }
.login-logo { font-family: var(--mono); font-size: clamp(1rem,3vw,1.4rem); color: var(--accent); letter-spacing: .2em; text-align: center; animation: flicker 4s infinite; }
.login-logo span { color: var(--accent2); }
@keyframes flicker { 0%,100%{opacity:1} 92%{opacity:1} 93%{opacity:.6} 94%{opacity:1} 96%{opacity:.8} 97%{opacity:1} }
.login-box { background: var(--panel); border: 1px solid var(--border); border-top: 2px solid var(--accent); padding: 2.5rem 3rem; width: min(420px,92vw); position: relative; }
.login-box::before { content: 'SECURE ACCESS'; position: absolute; top: -1px; right: 1.5rem; background: var(--accent); color: var(--bg); font-family: var(--mono); font-size: .65rem; letter-spacing: .15em; padding: .1rem .6rem; }
.login-box h2 { font-size: 1.3rem; font-weight: 800; letter-spacing: .1em; margin-bottom: 1.8rem; color: #fff; }
.field { margin-bottom: 1.2rem; }
.field label { display: block; font-family: var(--mono); font-size: .7rem; color: var(--text-dim); letter-spacing: .12em; margin-bottom: .4rem; text-transform: uppercase; }
.field input { width: 100%; background: var(--bg2); border: 1px solid var(--border); border-radius: 2px; color: var(--accent); font-family: var(--mono); font-size: .95rem; padding: .65rem .85rem; outline: none; transition: border-color .2s; }
.field input:focus { border-color: var(--accent); }
.btn-login { width: 100%; margin-top: .6rem; padding: .8rem; background: linear-gradient(135deg, var(--accent), #0088aa); border: none; color: var(--bg); font-family: var(--sans); font-weight: 800; font-size: .95rem; letter-spacing: .12em; text-transform: uppercase; cursor: pointer; transition: filter .2s, transform .1s; }
.btn-login:hover { filter: brightness(1.15); }
.btn-login:active { transform: scale(.98); }
.error-msg { background: rgba(255,62,108,.12); border: 1px solid var(--danger); color: var(--danger); font-family: var(--mono); font-size: .8rem; padding: .6rem .85rem; margin-bottom: 1rem; }

/* ═══════════════════════════════════════════════════════════════
   DASHBOARD LAYOUT
═══════════════════════════════════════════════════════════════ */
.dash-wrap { position: relative; z-index: 1; min-height: 100vh; display: grid; grid-template-rows: auto 1fr; }
header { display: flex; align-items: center; justify-content: space-between; padding: .9rem 2rem; background: var(--panel); border-bottom: 1px solid var(--border); position: sticky; top: 0; z-index: 100; }
.hdr-brand { display: flex; align-items: center; gap: 1rem; }
.hdr-icon { width: 38px; height: 38px; background: var(--accent); display: flex; align-items: center; justify-content: center; font-size: 1.2rem; color: var(--bg); font-weight: 900; }
.hdr-title { font-family: var(--mono); font-size: 1.05rem; color: var(--accent); letter-spacing: .18em; }
.hdr-title small { display: block; font-size: .62rem; color: var(--text-dim); letter-spacing: .1em; }
.hdr-right { display: flex; align-items: center; gap: 1.5rem; }
.hdr-clock { font-family: var(--mono); font-size: .8rem; color: var(--text-dim); }
.hdr-user { display: flex; align-items: center; gap: .6rem; font-size: .82rem; color: var(--accent2); }
.hdr-user .dot { width: 8px; height: 8px; background: var(--accent2); border-radius: 50%; animation: pulse 2s infinite; }
@keyframes pulse { 0%,100%{box-shadow:0 0 0 0 rgba(0,255,157,.4)} 50%{box-shadow:0 0 0 6px rgba(0,255,157,0)} }
.btn-logout { background: transparent; border: 1px solid var(--border); color: var(--text-dim); font-family: var(--mono); font-size: .7rem; letter-spacing: .1em; padding: .35rem .8rem; cursor: pointer; text-decoration: none; transition: border-color .2s, color .2s; }
.btn-logout:hover { border-color: var(--danger); color: var(--danger); }

/* Main grid */
main { padding: 2rem; display: grid; grid-template-columns: 340px 1fr; gap: 1.5rem; align-content: start; }

/* Card */
.card { background: var(--panel); border: 1px solid var(--border); position: relative; overflow: hidden; }
.card::after { content: ''; position: absolute; top: 0; right: 0; width: 40px; height: 40px; background: linear-gradient(225deg, rgba(0,212,255,.06), transparent); pointer-events: none; }
.card-hdr { display: flex; align-items: center; gap: .7rem; padding: .8rem 1.2rem; border-bottom: 1px solid var(--border); background: rgba(0,212,255,.02); }
.card-hdr-icon { font-size: 1rem; color: var(--accent); }
.card-hdr h3 { font-family: var(--mono); font-size: .78rem; letter-spacing: .15em; color: var(--text-dim); text-transform: uppercase; flex: 1; }
.card-body { padding: 1.2rem; }

/* Control panel */
.control-panel { display: flex; flex-direction: column; gap: 1.5rem; }

/* Test selector */
.test-type-label { font-family: var(--mono); font-size: .68rem; color: var(--text-dim); letter-spacing: .12em; text-transform: uppercase; margin-bottom: .5rem; display: block; }
.test-select { width: 100%; background: var(--bg2); border: 1px solid var(--border); color: var(--accent); font-family: var(--mono); font-size: .9rem; padding: .65rem .85rem; outline: none; appearance: none; background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='8'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%2300d4ff' stroke-width='1.5' fill='none'/%3E%3C/svg%3E"); background-repeat: no-repeat; background-position: right 1rem center; cursor: pointer; transition: border-color .2s; }
.test-select:focus { border-color: var(--accent); }
.test-select option { background: var(--bg2); }

/* Execute button */
.btn-execute { width: 100%; margin-top: 1rem; padding: 1rem; background: transparent; border: 1px solid var(--accent2); color: var(--accent2); font-family: var(--sans); font-weight: 800; font-size: 1rem; letter-spacing: .18em; text-transform: uppercase; cursor: pointer; position: relative; overflow: hidden; transition: color .25s; }
.btn-execute::before { content: ''; position: absolute; inset: 0; background: var(--accent2); transform: translateX(-100%); transition: transform .3s ease; z-index: 0; }
.btn-execute:hover::before { transform: translateX(0); }
.btn-execute:hover { color: var(--bg); }
.btn-execute:disabled { opacity: .4; cursor: not-allowed; }
.btn-execute:disabled::before { display: none; }
.btn-execute span { position: relative; z-index: 1; }

/* Status badge */
.status-badge { display: inline-flex; align-items: center; gap: .5rem; font-family: var(--mono); font-size: .78rem; letter-spacing: .1em; padding: .35rem .75rem; border-radius: 2px; }
.status-badge.idle     { background: rgba(90,138,170,.12); color: var(--text-dim);  border: 1px solid var(--border); }
.status-badge.running  { background: rgba(255,184,0,.1);   color: var(--warn);      border: 1px solid var(--warn); }
.status-badge.completed{ background: rgba(0,255,157,.1);   color: var(--accent2);   border: 1px solid var(--accent2); }
.status-badge.failed   { background: rgba(255,62,108,.1);  color: var(--danger);    border: 1px solid var(--danger); }
.status-dot { width: 7px; height: 7px; border-radius: 50%; background: currentColor; }
.status-badge.running .status-dot { animation: blink .8s infinite; }
@keyframes blink { 0%,100%{opacity:1} 50%{opacity:.2} }

/* Job KV rows */
.job-info { margin-top: 1rem; }
.kv-row { display: flex; justify-content: space-between; align-items: baseline; padding: .45rem 0; border-bottom: 1px solid rgba(26,58,92,.5); }
.kv-row:last-child { border-bottom: none; }
.kv-key { font-family: var(--mono); color: var(--text-dim); font-size: .7rem; letter-spacing: .08em; text-transform: uppercase; }
.kv-val { font-family: var(--mono); color: var(--accent); font-size: .78rem; word-break: break-all; text-align: right; max-width: 60%; }

/* Summary tiles */
.summary-grid { display: grid; grid-template-columns: repeat(3,1fr); gap: .7rem; }
.summary-tile { background: var(--bg2); border: 1px solid var(--border); padding: .8rem .6rem; text-align: center; }
.summary-tile .s-num { font-family: var(--mono); font-size: 1.8rem; font-weight: 700; line-height: 1; }
.summary-tile .s-label { font-family: var(--mono); font-size: .6rem; letter-spacing: .12em; text-transform: uppercase; margin-top: .3rem; color: var(--text-dim); }
.tile-pass .s-num { color: var(--accent2); }
.tile-fail .s-num { color: var(--danger); }
.tile-skip .s-num { color: var(--warn); }

/* Download buttons */
.download-row { display: flex; gap: .7rem; }
.btn-dl { flex: 1; display: flex; align-items: center; justify-content: center; gap: .4rem; padding: .65rem .5rem; background: var(--bg2); border: 1px solid var(--border); color: var(--text); font-family: var(--mono); font-size: .75rem; letter-spacing: .1em; text-decoration: none; cursor: pointer; transition: border-color .2s, color .2s; }
.btn-dl:hover { border-color: var(--accent); color: var(--accent); }

/* Log viewer */
.log-panel { 
    display: flex; 
    flex-direction: column; 
    position: sticky; 
    top: 5rem; 
    height: calc(100vh - 7rem); 
}
.log-panel .card { flex: 1; display: flex; flex-direction: column; }
.log-toolbar { display: flex; align-items: center; gap: .8rem; padding: .5rem 1.2rem; border-bottom: 1px solid var(--border); background: rgba(0,0,0,.2); }
.log-toolbar span { font-family: var(--mono); font-size: .68rem; color: var(--text-dim); letter-spacing: .1em; }
.auto-scroll-toggle { margin-left: auto; display: flex; align-items: center; gap: .5rem; font-family: var(--mono); font-size: .68rem; color: var(--text-dim); cursor: pointer; user-select: none; }
.toggle-switch { width: 30px; height: 16px; background: var(--border); border-radius: 8px; position: relative; transition: background .2s; }
.toggle-switch.on { background: var(--accent); }
.toggle-switch::after { content: ''; position: absolute; top: 2px; left: 2px; width: 12px; height: 12px; background: #fff; border-radius: 50%; transition: left .2s; }
.toggle-switch.on::after { left: 16px; }
#log-output { flex: 1; background: #020c14; color: #7ec8e3; font-family: var(--mono); font-size: .8rem; line-height: 1.7; padding: 1rem 1.4rem; overflow-y: auto; white-space: pre-wrap; word-break: break-all; min-height: 500px; scroll-behavior: smooth;}
#log-output::-webkit-scrollbar { width: 6px; }
#log-output::-webkit-scrollbar-track { background: var(--bg); }
#log-output::-webkit-scrollbar-thumb { background: var(--border); border-radius: 3px; }
.log-info  { color: #7ec8e3; }
.log-warn  { color: var(--warn); }
.log-error { color: var(--danger); }
.log-pass  { color: var(--accent2); }
.log-time  { color: var(--text-dim); }

/* Progress bar */
.progress-bar-wrap { height: 3px; background: var(--bg2); overflow: hidden; }
.progress-bar { height: 100%; background: linear-gradient(90deg, var(--accent), var(--accent2)); width: 0%; transition: width .5s; }
.progress-bar.indeterminate { width: 40%; animation: indeterminate 1.4s infinite ease-in-out; }
@keyframes indeterminate { 0%{transform:translateX(-150%)} 100%{transform:translateX(350%)} }

@media (max-width: 900px) {
    main { grid-template-columns: 1fr; }
    .log-panel { grid-column: 1; }
}
</style>
</head>
<body>

<?php if (!$isLoggedIn): ?>
<!-- ═══════════════════════ LOGIN ═══════════════════════ -->
<div class="login-wrap">
    <div class="login-logo">
        <div style="font-size:2.5rem;margin-bottom:.4rem;font-family:monospace">⬡</div>
        OFS <span>AUTOMATION TESTING</span>
        <div style="font-size:.65rem;margin-top:.3rem;color:var(--text-dim)">AUTOMATION EXECUTION PLATFORM v2.0</div>
    </div>
    <div class="login-box">
        <h2>Administrator Login</h2>
        <?php if ($loginError): ?>
            <div class="error-msg">⚠ <?= htmlspecialchars($loginError) ?></div>
        <?php endif; ?>
        <form method="post">
            <input type="hidden" name="action" value="login">
            <div class="field">
                <label>Username</label>
                <input type="text" name="username" autocomplete="username" autofocus>
            </div>
            <div class="field">
                <label>Password</label>
                <input type="password" name="password" autocomplete="current-password">
            </div>
            <button type="submit" class="btn-login">Authenticate →</button>
        </form>
    </div>
</div>

<?php else: ?>
<!-- ═══════════════════════ DASHBOARD ═══════════════════════ -->
<div class="dash-wrap">

    <header>
        <div class="hdr-brand">
            <div class="hdr-icon">⬡</div>
            <div class="hdr-title">AUTOMATION TESTING<small>AUTOMATION EXECUTION DASHBOARD</small></div>
        </div>
        <div class="hdr-right">
            <div class="hdr-clock" id="clock">--:--:--</div>
            <div class="hdr-user"><div class="dot"></div><?= htmlspecialchars($_SESSION['user']) ?></div>
            <a href="?logout" class="btn-logout">LOGOUT</a>
        </div>
    </header>

    <main>
        <!-- LEFT: Control panel -->
        <div class="control-panel">

            <div class="card">
                <div class="card-hdr"><span class="card-hdr-icon">⚙</span><h3>Test Configuration</h3></div>
                <div class="card-body">
                    <label class="test-type-label">Select Test Suite</label>
                    <select id="test-type" class="test-select">
                        <option value="smoke">🔥 Smoke</option>
                        <option value="regression">🔄 Regression</option>
                        <option value="admin">🛡 Admin</option>
                        <option value="user">👤 User</option>
                        <option value="validation">✅ Validation</option>
                        <option value="ui">🖥 UI</option>
                    </select>
                    <button id="btn-execute" class="btn-execute" onclick="startExecution()">
                        <span>▶ Execute Tests</span>
                    </button>
                </div>
            </div>

            <div class="card">
                <div class="card-hdr"><span class="card-hdr-icon">◈</span><h3>Execution Status</h3></div>
                <div class="card-body">
                    <div id="status-badge" class="status-badge idle">
                        <div class="status-dot"></div>
                        <span id="status-text">IDLE</span>
                    </div>
                    <div class="job-info">
                        <div class="kv-row"><span class="kv-key">Job ID</span><span class="kv-val" id="kv-jobid">—</span></div>
                        <div class="kv-row"><span class="kv-key">Suite</span><span class="kv-val" id="kv-suite">—</span></div>
                        <div class="kv-row"><span class="kv-key">Started</span><span class="kv-val" id="kv-started">—</span></div>
                        <div class="kv-row"><span class="kv-key">Duration</span><span class="kv-val" id="kv-duration">—</span></div>
                    </div>
                </div>
            </div>

            <div class="card" id="summary-card" style="display:none">
                <div class="card-hdr"><span class="card-hdr-icon">📊</span><h3>Test Summary</h3></div>
                <div class="card-body">
                    <div class="summary-grid">
                        <div class="summary-tile tile-pass"><div class="s-num" id="s-pass">0</div><div class="s-label">Passed</div></div>
                        <div class="summary-tile tile-fail"><div class="s-num" id="s-fail">0</div><div class="s-label">Failed</div></div>
                        <div class="summary-tile tile-skip"><div class="s-num" id="s-skip">0</div><div class="s-label">Skipped</div></div>
                    </div>
                </div>
            </div>

            <div class="card" id="download-card" style="display:none">
                <div class="card-hdr"><span class="card-hdr-icon">⬇</span><h3>Reports</h3></div>
                <div class="card-body">
                    <div class="download-row">
                        <a id="dl-pdf" href="#" class="btn-dl" target="_blank">📄 PDF Report</a>
                        <a id="dl-txt" href="#" class="btn-dl" target="_blank">📝 TXT Report</a>
                    </div>
                </div>
            </div>

        </div>

        <!-- RIGHT: Log viewer -->
        <div class="log-panel">
            <div class="card" style="flex:1;display:flex;flex-direction:column">
                <div class="card-hdr">
                    <span class="card-hdr-icon">▣</span>
                    <h3>Live Execution Log</h3>
                    <span id="log-lines" style="font-family:var(--mono);font-size:.65rem;color:var(--text-dim);margin-left:auto">0 lines</span>
                </div>
                <div class="progress-bar-wrap"><div class="progress-bar" id="progress-bar"></div></div>
                <div class="log-toolbar">
                    <span id="log-ts">Waiting for execution…</span>
                    <label class="auto-scroll-toggle" onclick="toggleAutoScroll()">
                        <span>AUTO-SCROLL</span>
                        <div class="toggle-switch on" id="auto-scroll-toggle"></div>
                    </label>
                </div>
                <pre id="log-output">

  ████████╗███████╗███████╗████████╗ ██████╗ ██████╗ ███████╗
     ██╔══╝██╔════╝██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗██╔════╝
     ██║   █████╗  ███████╗   ██║   ██║   ██║██████╔╝███████╗
     ██║   ██╔══╝  ╚════██║   ██║   ██║   ██║██╔═══╝ ╚════██║
     ██║   ███████╗███████║   ██║   ╚██████╔╝██║     ███████║
     ╚═╝   ╚══════╝╚══════╝   ╚═╝    ╚═════╝ ╚═╝     ╚══════╝

         Select a test suite and click Execute to begin.

</pre>
            </div>
        </div>

    </main>
</div>
<?php endif; ?>

<script>
/* ════════════════════════════════════════════════════════
   DASHBOARD JAVASCRIPT
════════════════════════════════════════════════════════ */

// Live clock
function updateClock() {
    document.getElementById('clock').textContent = new Date().toTimeString().slice(0,8);
}
setInterval(updateClock, 1000); updateClock();

// State
let currentJobId  = null;
let pollInterval  = null;
let autoScroll    = true;
let startTime     = null;
let durationTimer = null;
let lastLogLen    = 0;

function toggleAutoScroll() {
    autoScroll = !autoScroll;
    document.getElementById('auto-scroll-toggle').classList.toggle('on', autoScroll);
}

// ── Start execution ────────────────────────────────────
function startExecution() {
    const testType = document.getElementById('test-type').value;
    document.getElementById('btn-execute').disabled = true;
    resetUI();
    setStatus('running');

    fetch('run.php', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'testType=' + encodeURIComponent(testType)
    })
    .then(r => r.json())
    .then(data => {
        if (data.error) {
            appendLog('ERROR: ' + data.error, 'log-error');
            setStatus('failed');
            document.getElementById('btn-execute').disabled = false;
            return;
        }
        currentJobId = data.jobId;
        startTime    = Date.now();
        document.getElementById('kv-jobid').textContent   = data.jobId;
        document.getElementById('kv-suite').textContent   = testType.toUpperCase();
        document.getElementById('kv-started').textContent = new Date().toLocaleTimeString();
        durationTimer = setInterval(updateDuration, 1000);
        document.getElementById('progress-bar').classList.add('indeterminate');
        pollInterval = setInterval(() => pollStatus(), 2000);
    })
    .catch(err => {
        appendLog('Network error: ' + err.message, 'log-error');
        setStatus('failed');
        document.getElementById('btn-execute').disabled = false;
    });
}

// ── Poll status every 2 s ──────────────────────────────
function pollStatus() {
    if (!currentJobId) return;
    fetch('status.php?jobId=' + encodeURIComponent(currentJobId))
    .then(r => r.json())
    .then(data => {
        if (data.log) renderLog(data.log);

        if (data.status === 'completed' || data.status === 'failed') {
            clearInterval(pollInterval);  pollInterval  = null;
            clearInterval(durationTimer); durationTimer = null;

            const pb = document.getElementById('progress-bar');
            pb.classList.remove('indeterminate');
            pb.style.width = data.status === 'completed' ? '100%' : '60%';

            setStatus(data.status);
            document.getElementById('btn-execute').disabled = false;

            // Summary
            if (data.summary) {
                document.getElementById('s-pass').textContent = data.summary.passed  ?? 0;
                document.getElementById('s-fail').textContent = data.summary.failed  ?? 0;
                document.getElementById('s-skip').textContent = data.summary.skipped ?? 0;
                document.getElementById('summary-card').style.display = '';
            }

            // Downloads
            if (data.pdf || data.txt) {
                if (data.pdf) document.getElementById('dl-pdf').href =
                    'download.php?jobId=' + encodeURIComponent(currentJobId) + '&type=pdf';
                if (data.txt) document.getElementById('dl-txt').href =
                    'download.php?jobId=' + encodeURIComponent(currentJobId) + '&type=txt';
                document.getElementById('download-card').style.display = '';
            }
        }
    })
    .catch(err => console.warn('Poll error:', err));
}

// ── Render / colorise log ──────────────────────────────
function renderLog(logText) {
    const el = document.getElementById('log-output');
    if (logText.length === lastLogLen) return;
    lastLogLen = logText.length;

    const lines = logText.split('\n');
    el.innerHTML = lines.map(line => {
        const e = line.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
        if (/error|FAILED|Exception|BUILD FAILURE/i.test(line)) return `<span class="log-error">${e}</span>`;
        if (/WARN|WARNING/i.test(line))  return `<span class="log-warn">${e}</span>`;
        if (/PASSED|BUILD SUCCESS|Tests run.*Failures: 0/i.test(line)) return `<span class="log-pass">${e}</span>`;
        if (/^\[INFO\]|\d{2}:\d{2}:\d{2}/.test(line)) return `<span class="log-time">${e}</span>`;
        return `<span class="log-info">${e}</span>`;
    }).join('\n');

    document.getElementById('log-lines').textContent = lines.length + ' lines';
    document.getElementById('log-ts').textContent = 'Last update: ' + new Date().toLocaleTimeString();
    if (autoScroll) {
        setTimeout(() => { 
    el.scrollTo({
        top: el.scrollHeight,
        behavior: 'smooth'
    }); 
}, 10);
    }
}

function appendLog(text, cls) {
    const el = document.getElementById('log-output');
    const span = document.createElement('span');
    span.className = cls || 'log-info';
    span.textContent = text + '\n';
    el.appendChild(span);
    if (autoScroll) el.scrollTop = el.scrollHeight;
}

function setStatus(s) {
    const badge = document.getElementById('status-badge');
    badge.className = 'status-badge ' + s;
    document.getElementById('status-text').textContent = s.toUpperCase();
}

function updateDuration() {
    if (!startTime) return;
    const sec = Math.floor((Date.now() - startTime) / 1000);
    const m = String(Math.floor(sec/60)).padStart(2,'0');
    const s = String(sec%60).padStart(2,'0');
    document.getElementById('kv-duration').textContent = m + ':' + s;
}

function resetUI() {
    lastLogLen = 0;
    document.getElementById('log-output').textContent = '';
    document.getElementById('log-lines').textContent = '0 lines';
    document.getElementById('log-ts').textContent = 'Starting execution…';
    ['kv-jobid','kv-suite','kv-started','kv-duration'].forEach(id => {
        document.getElementById(id).textContent = '—';
    });
    document.getElementById('summary-card').style.display = 'none';
    document.getElementById('download-card').style.display = 'none';
    document.getElementById('progress-bar').classList.remove('indeterminate');
    document.getElementById('progress-bar').style.width = '0%';
    if (pollInterval)  { clearInterval(pollInterval);  pollInterval  = null; }
    if (durationTimer) { clearInterval(durationTimer); durationTimer = null; }
    startTime = null;
}
</script>
</body>
</html>
