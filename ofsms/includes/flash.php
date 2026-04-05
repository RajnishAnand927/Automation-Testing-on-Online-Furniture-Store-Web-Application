<?php

if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

if (!function_exists('setFlash')) {
    function setFlash($type, $message)
    {
        $allowedTypes = ['success', 'error', 'warning', 'info'];
        $normalizedType = in_array($type, $allowedTypes, true) ? $type : 'info';

        if (!isset($_SESSION['flash_messages']) || !is_array($_SESSION['flash_messages'])) {
            $_SESSION['flash_messages'] = [];
        }

        $_SESSION['flash_messages'][] = [
            'type' => $normalizedType,
            'message' => $message,
        ];
    }
}

if (!function_exists('getFlash')) {
    function getFlash()
    {
        $messages = $_SESSION['flash_messages'] ?? [];
        unset($_SESSION['flash_messages']);

        return is_array($messages) ? $messages : [];
    }
}

if (!function_exists('redirectWithFlash')) {
    function redirectWithFlash($location, $type, $message)
    {
        setFlash($type, $message);
        header('Location: ' . $location);
        exit;
    }
}

if (!function_exists('renderFlashMessages')) {
    function renderFlashMessages()
    {
        $messages = getFlash();
        if (empty($messages)) {
            return '';
        }

        $html = '<style>
.flash-toast-container {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 9999;
    width: min(360px, calc(100% - 32px));
}
.flash-toast {
    position: relative;
    margin-bottom: 12px;
    padding: 14px 44px 14px 16px;
    border-radius: 12px;
    box-shadow: 0 12px 30px rgba(0, 0, 0, 0.16);
    color: #fff;
    font-size: 14px;
    line-height: 1.5;
    opacity: 0;
    transform: translateY(-10px);
    animation: flashToastIn .28s ease forwards;
}
.flash-toast-success { background: linear-gradient(135deg, #1c9f68, #158357); }
.flash-toast-error { background: linear-gradient(135deg, #d63c3c, #b82929); }
.flash-toast-warning { background: linear-gradient(135deg, #d99c22, #c47a05); }
.flash-toast-info { background: linear-gradient(135deg, #2476d8, #165bb1); }
.flash-toast-close {
    position: absolute;
    top: 10px;
    right: 12px;
    border: 0;
    background: transparent;
    color: inherit;
    font-size: 20px;
    line-height: 1;
    cursor: pointer;
    opacity: 0.85;
}
.flash-toast-close:hover { opacity: 1; }
.flash-inline-error {
    display: none;
    margin-top: 8px;
    color: #c62828;
    font-size: 13px;
    font-weight: 600;
}
.flash-inline-error.is-visible {
    display: block;
}
@keyframes flashToastIn {
    from {
        opacity: 0;
        transform: translateY(-10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
@keyframes flashToastOut {
    from {
        opacity: 1;
        transform: translateY(0);
    }
    to {
        opacity: 0;
        transform: translateY(-8px);
    }
}
@media (max-width: 767px) {
    .flash-toast-container {
        left: 16px;
        right: 16px;
        width: auto;
    }
}
</style>';

        $html .= '<div class="flash-toast-container" data-flash-container>';
        foreach ($messages as $message) {
            $type = htmlspecialchars($message['type'], ENT_QUOTES, 'UTF-8');
            $text = htmlspecialchars($message['message'], ENT_QUOTES, 'UTF-8');
            $html .= '<div class="flash-toast flash-toast-' . $type . '" data-flash-toast>';
            $html .= '<button type="button" class="flash-toast-close" aria-label="Close">&times;</button>';
            $html .= '<div class="flash-toast-message">' . $text . '</div>';
            $html .= '</div>';
        }
        $html .= '</div>';

        $html .= '<script>
(function () {
    function dismissToast(toast) {
        if (!toast || toast.dataset.dismissed === "true") {
            return;
        }
        toast.dataset.dismissed = "true";
        toast.style.animation = "flashToastOut .22s ease forwards";
        window.setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 220);
    }

    var toasts = document.querySelectorAll("[data-flash-toast]");
    for (var i = 0; i < toasts.length; i++) {
        (function (toast) {
            var closeButton = toast.querySelector(".flash-toast-close");
            if (closeButton) {
                closeButton.addEventListener("click", function () {
                    dismissToast(toast);
                });
            }
            window.setTimeout(function () {
                dismissToast(toast);
            }, 4200 + (i * 300));
        })(toasts[i]);
    }
})();
</script>';

        return $html;
    }
}
