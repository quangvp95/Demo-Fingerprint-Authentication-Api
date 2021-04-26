package coccoc.pin;

import android.app.Activity;
import android.content.Intent;

import org.chromium.base.ActivityState;
import org.chromium.base.ApplicationStatus;
import org.chromium.chrome.browser.ChromeBaseAppCompatActivity;
import org.chromium.chrome.browser.flags.ChromeFeatureList;

import coccoc.pin.ui.unlock.PinUnlockActivity;

public class ForegroundDetector implements ApplicationStatus.ActivityStateListener {

    private static ForegroundDetector sInstance;

    private PinManager pinManager = PinManager.getInstance();

    /**
     * Init with false value as we will handle the first lock when starting app in
     * PinUnlockActivity.launch(). ForegroundDetector only does its work when the app is running.
     */
    private boolean mShouldLock;
    private boolean mIsShowingLockActivity;

    private ForegroundDetector() {

    }

    public static ForegroundDetector getInstance() {
        if (sInstance == null) {
            sInstance = new ForegroundDetector();
        }
        return sInstance;
    }

    @Override
    public void onActivityStateChange(Activity activity, int newState) {
        if (activity == null) return;
        if (!ChromeFeatureList.isBrowserLockEnabled() || !pinManager.isPinFeatureOn()) return;

        if (activity instanceof PinUnlockActivity) {
            mIsShowingLockActivity = newState == ActivityState.CREATED
                    || newState == ActivityState.STARTED || newState == ActivityState.RESUMED;
        }

        // Check when to lock the app
        if (!mShouldLock) {
            boolean appNotVisible = !ApplicationStatus.hasVisibleActivities();
            boolean appInBackground = appNotVisible && !activity.isChangingConfigurations();
            if (appInBackground) {
                mShouldLock = true; // app enters background, we will lock the app when it shows up
                pinManager.saveLastBackgroundTime(System.currentTimeMillis());
                return;
            }
        }

        // Check when to show lock screen
        if (mShouldLock) {
            boolean isActivityShowing = newState == ActivityState.CREATED
                    || newState == ActivityState.STARTED || newState == ActivityState.RESUMED;
            boolean isSecureActivity = activity instanceof ChromeBaseAppCompatActivity;
            boolean isShowingSecureActivity = isSecureActivity && isActivityShowing;
            if (!mIsShowingLockActivity && isShowingSecureActivity && pinManager.shouldBeLocked()) {
                Intent intent = new Intent(activity, PinUnlockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
            }
        }
    }

    public void setShouldLock(boolean shouldLock) {
        mShouldLock = shouldLock;
    }
}