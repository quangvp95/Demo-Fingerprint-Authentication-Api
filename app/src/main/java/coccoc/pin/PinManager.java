package coccoc.pin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import org.chromium.base.ContextUtils;
import com.example.demofingerprintauth.R;
import org.chromium.chrome.browser.settings.SettingsLauncherImpl;
import org.chromium.components.browser_ui.settings.SettingsLauncher;

import java.util.ArrayList;
import java.util.List;

import coccoc.base.tracking.TrackingUtils;
import coccoc.pin.biometric.BiometricUtils;
import coccoc.pin.ui.setting.PinSettingActivity;
import coccoc.preferences.LockPreferences;

public class PinManager {

    private static final String PREF_PIN_FEATURE = "pref_pin_feature";
    private static final String PREF_PIN_LAST_RETRY = "pref_pin_last_lock";
    private static final String PREF_PIN_LAST_BACKGROUND = "pref_pin_last_background";
    private static final String PREF_PIN_FINGERPRINT = "pref_pin_fingerprint";
    private static final String PREF_PIN_LOCK_DURATION = "pref_pin_lock_duration";
    private static final String PREF_PIN_FEATURE_INTRO_SHOWN = "pref_pin_intro_shown";

    private static PinManager sInstance;
    private boolean mOpenFromLauncher;
    private List<OnPinChangedListener> pinListeners = new ArrayList<>();

    private PinManager() {
        mOpenFromLauncher = true;
    }

    public static PinManager getInstance() {
        if (sInstance == null) {
            sInstance = new PinManager();
        }
        return sInstance;
    }

    public void registerPinListener(OnPinChangedListener listener) {
        if (listener != null) {
            pinListeners.add(listener);
        }
    }

    public void unregisterPinListener(OnPinChangedListener listener) {
        if (pinListeners != null) {
            pinListeners.remove(listener);
        }
    }

    private void notifyPinChanged(boolean pinEnabled) {
        for (OnPinChangedListener listener : pinListeners) {
            if (listener != null) {
                listener.onPinChanged(pinEnabled);
            }
        }
    }

    public void handleBottomMenuClick(Context context) {
        if (isPinFeatureOn()) {
            showSettingsPage(context);
        } else {
            if (ContextUtils.getAppSharedPreferences().getBoolean(PREF_PIN_FEATURE_INTRO_SHOWN, false)) {
                showSettingsPage(context);
            } else {
                ContextUtils.getAppSharedPreferences().edit().putBoolean(PREF_PIN_FEATURE_INTRO_SHOWN, true).apply();
                showPinIntroDialog(context);
            }
        }
    }

    private void showPinIntroDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.browser_lock_dialog_title);
        builder.setCancelable(true);
        View content = LayoutInflater.from(context).inflate(R.layout.pin_intro_dialog, null);
        builder.setView(content);
        AlertDialog dialog = builder.create();
        content.findViewById(R.id.negative).setOnClickListener(view -> dialog.dismiss());
        content.findViewById(R.id.positive).setOnClickListener(view -> {
            Intent pinIntent = new Intent(context, PinSettingActivity.class);
            pinIntent.putExtra(PinSettingActivity.EXTRAS_PIN_OFF, false);
            context.startActivity(pinIntent);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showSettingsPage(Context context) {
        SettingsLauncher settingsLauncher = new SettingsLauncherImpl();
        settingsLauncher.launchSettingsActivity(context, LockPreferences.class);
    }

    public boolean isOpenFromLauncher() {
        return mOpenFromLauncher;
    }

    public void setOpenFromLauncher(boolean fromLauncher) {
        this.mOpenFromLauncher = fromLauncher;
    }

    public void setPinFeatureOn(boolean isOn) {
        ContextUtils.getAppSharedPreferences().edit().putBoolean(PREF_PIN_FEATURE, isOn).apply();
    }

    public boolean isPinFeatureOn() {
        return true/*ContextUtils.getAppSharedPreferences().getBoolean(PREF_PIN_FEATURE, false)*/;
    }

    public boolean isPinFingerprintOn() {
        return /*ContextUtils.getAppSharedPreferences().getBoolean(PREF_PIN_FINGERPRINT, false)
                &&*/ BiometricUtils.isFingerprintAvailable(ContextUtils.getApplicationContext());
    }

    public void enablePinFingerprint(boolean enabled) {
        ContextUtils.getAppSharedPreferences().edit().putBoolean(PREF_PIN_FINGERPRINT, enabled).apply();
    }

    public void setLockDuration(@PinLockDuration.LockDuration int duration) {
        ContextUtils.getAppSharedPreferences().edit().putInt(PREF_PIN_LOCK_DURATION, duration).apply();
    }

    public @PinLockDuration.LockDuration int getLockDuration() {
        return ContextUtils.getAppSharedPreferences().getInt(PREF_PIN_LOCK_DURATION, PinLockDuration.LockDuration.IMMEDIATELY);
    }

    public void turnOffPin() {
        // If turn off PIN feature, then remove PIN from database. New PIN will be setup when user turn on PIN feature.
        PinStorage.getInstance().removePin();

        ContextUtils.getAppSharedPreferences().edit().putBoolean(PREF_PIN_FEATURE, false).apply();
        notifyPinChanged(false);

        // Log event
        TrackingUtils.logTrackingEvent(TrackingUtils.TRACK_PIN_DISABLE);
    }

    public void turnOnPin() {
        ContextUtils.getAppSharedPreferences().edit().putBoolean(PREF_PIN_FEATURE, true).apply();
        notifyPinChanged(true);

        // Log event
        TrackingUtils.logTrackingEvent(TrackingUtils.TRACK_PIN_ENABLE);
    }

    public void saveLastRetryTimer(long timeMs) {
        ContextUtils.getAppSharedPreferences().edit().putLong(PREF_PIN_LAST_RETRY, timeMs).apply();
    }

    public boolean isRetryTimerFinished() {
        long lastLockTime = ContextUtils.getAppSharedPreferences().getLong(PREF_PIN_LAST_RETRY, 0);
        long nowMs = System.currentTimeMillis();
        return nowMs - lastLockTime > PinConfig.PIN_RETRY_TIMER;
    }

    public long getRetryTimer() {
        long lastLockTime = ContextUtils.getAppSharedPreferences().getLong(PREF_PIN_LAST_RETRY, 0);
        long nowMs = System.currentTimeMillis();

        return PinConfig.PIN_RETRY_TIMER - (nowMs - lastLockTime);
    }

    public void saveLastBackgroundTime(long timeMs) {
        ContextUtils.getAppSharedPreferences().edit().putLong(PREF_PIN_LAST_BACKGROUND, timeMs).apply();
    }

    public boolean shouldBeLocked() {
        long lastBackgroundTime = ContextUtils.getAppSharedPreferences().getLong(PREF_PIN_LAST_BACKGROUND, 0);
        long nowMs = System.currentTimeMillis();
        boolean hasPin = PinStorage.getInstance().hasPin();

        return true/*hasPin && ((nowMs - lastBackgroundTime) > PinLockDuration.getLockDurationInMs(getLockDuration()))*/;
    }

    public void resetPinPrefs() {
        // turn off PIN feature
        turnOffPin();

        // Reset last background time
        saveLastBackgroundTime(0);

        // turn off fingerprint
        enablePinFingerprint(false);

        // set lock duration is 0
        setLockDuration(PinLockDuration.LockDuration.IMMEDIATELY);

        // clear Pin in database
        PinStorage.getInstance().removePin();
    }
}
