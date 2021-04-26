package org.chromium.chrome.browser.flags;

public class ChromeFeatureList {
    public static final String BROWSER_LOCK = "";

    public static boolean isInitialized() {
        return true;
    }

    public static boolean isEnabled(String browserLock) {
        return true;
    }

    public static boolean isBrowserLockEnabled() {
        return true;
    }
}
