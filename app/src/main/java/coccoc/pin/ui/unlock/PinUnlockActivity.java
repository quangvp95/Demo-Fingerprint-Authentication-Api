package coccoc.pin.ui.unlock;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.chromium.base.IntentUtils;
import com.example.demofingerprintauth.R;
import org.chromium.chrome.browser.SynchronousInitializationActivity;
import org.chromium.chrome.browser.flags.ChromeFeatureList;

import coccoc.pin.ForegroundDetector;
import coccoc.pin.PinConfig;
import coccoc.pin.PinManager;
import coccoc.pin.views.PinKeyboardView;
import coccoc.pin.views.PinPasscodeView;

public class PinUnlockActivity extends SynchronousInitializationActivity implements PinUnlockView, PinKeyboardView.OnKeyPressListener {

    public static final int REQ_PIN_UNLOCK = 500;
    private static final String EXTRA_COMPLETE_UNLOCK_INTENT = "extra_complete_unlock_intent";

    private PinManager mPinManager = PinManager.getInstance();
    private PinUnlockController<PinUnlockView> mPinController;
    private PinPasscodeView mPasscodeView;
    private PinKeyboardView mKeyboardView;
    private TextView mTvTitle;
    private TextView mTvMessage;
    private Handler mHandler = new Handler();

    public static boolean launch(Context caller, Intent fromIntent) {
        if (!checkIfFirstUnlockIsNecessary()) return false;

        Intent unlockIntent = new Intent(caller, PinUnlockActivity.class);
        addPendingIntent(caller, unlockIntent, fromIntent);

        if (!(caller instanceof Activity)) unlockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        IntentUtils.safeStartActivity(caller, unlockIntent);
        return true;
    }

    private static boolean checkIfFirstUnlockIsNecessary() {
        PinManager pinManager = PinManager.getInstance();
        return pinManager.isPinFeatureOn() && pinManager.shouldBeLocked() && pinManager.isOpenFromLauncher();
    }

    private static void addPendingIntent(Context context, Intent pinIntent, Intent intentToLaunchAfterUnlock) {
        PendingIntent pendingIntent;
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT;

        pendingIntent = PendingIntent.getActivity(context, REQ_PIN_UNLOCK, intentToLaunchAfterUnlock, pendingIntentFlags);
        pinIntent.putExtra(PinUnlockActivity.EXTRA_COMPLETE_UNLOCK_INTENT, pendingIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coccoc_pin_activity);

        // If browser lock is disabled in about://flags, reset PIN config, unlock and go to next scene.
        boolean enableLock = ChromeFeatureList.isInitialized() && ChromeFeatureList.isEnabled(
                ChromeFeatureList.BROWSER_LOCK);
        if (!enableLock) {
            mPinManager.resetPinPrefs();
            onUnlock();
            return;
        }

        initController();
        initUI();
    }

    @Override
    protected void onDestroy() {
        stopFingerprintAuthIfPossible();
        if (mPinController != null) {
            mPinController.detach();
        }
        super.onDestroy();
    }

    private void initUI() {
        mTvTitle = findViewById(R.id.tvTitle);
        mTvMessage = findViewById(R.id.tvMessage);
        mPasscodeView = findViewById(R.id.pinView);
        mKeyboardView = findViewById(R.id.keyboardView);
        mKeyboardView.setKeyPressListener(this);

        boolean isFingerprintOn = mPinManager.isPinFingerprintOn();
        mTvTitle.setText(isFingerprintOn ? R.string.coccoc_pin_title_enter_pin_with_fingerprint : R.string.coccoc_pin_title_enter_pin);
        mTvMessage.setText(R.string.coccoc_pin_msg_enter_pin);

        startFingerprintAuthIfPossible();
        mPinController.resumeRetryTimerIfPossible();
    }

    private void sendUnlockCompletePendingIntent() {
        PendingIntent pendingIntent = IntentUtils.safeGetParcelableExtra(getIntent(), EXTRA_COMPLETE_UNLOCK_INTENT);

        if (pendingIntent == null) return;

        try {
            // Use the PendingIntent to send the intent that originally launched Chrome. The intent
            // will go back to the ChromeLauncherActivity, which will route it accordingly.
            pendingIntent.send(Activity.RESULT_OK, null, null);
        } catch (PendingIntent.CanceledException ignored) {
        }
    }

    private void initController() {
        mPinController = new PinUnlockControllerImpl<>();
        mPinController.attach(this);
    }

    private void startFingerprintAuthIfPossible() {
        View ivFingerprint = findViewById(R.id.ivFingerprint);
        boolean isFingerprintOn = mPinManager.isPinFingerprintOn();

        if (!isFingerprintOn) {
            ivFingerprint.setVisibility(View.GONE);
        } else {
            ivFingerprint.setOnClickListener(
                v -> mPinController.startBiometricAuth(PinUnlockActivity.this));
            mPinController.startBiometricAuth(this);
        }
    }

    private void stopFingerprintAuthIfPossible() {
        boolean isFingerprintOn = mPinManager.isPinFingerprintOn();
        if (isFingerprintOn) {
            mPinController.stopBiometricAuth();
        }
    }

    @Override
    public void onPinNotMatch() {
        mTvTitle.setText(R.string.coccoc_pin_title_wrong_pin);
        mTvMessage.setText(R.string.coccoc_pin_msg_wrong_pin);
        mPasscodeView.reset();
    }

    @Override
    public void onUnlock() {
        ForegroundDetector.getInstance().setShouldLock(false);
        PinManager pinManager = PinManager.getInstance();
        if (pinManager.isOpenFromLauncher()) {
//            pinManager.setOpenFromLauncher(false);
            sendUnlockCompletePendingIntent();
        }

        finish();
    }

    @Override
    public void onRetryLockStart() {

        mTvTitle.setText(R.string.coccoc_pin_title_locked);
        mTvMessage.setText(R.string.coccoc_pin_msg_locked);

        mPasscodeView.reset();
        mPasscodeView.setEnable(false);
        mKeyboardView.setEnable(false);

        stopFingerprintAuthIfPossible();
    }

    @Override
    public void onRetryLockFinished() {
        boolean isFingerprintOn = mPinManager.isPinFingerprintOn();
        mTvTitle.setText(isFingerprintOn ? R.string.coccoc_pin_title_enter_pin_with_fingerprint : R.string.coccoc_pin_title_enter_pin);
        mTvMessage.setText(R.string.coccoc_pin_msg_enter_pin);

        mPasscodeView.reset();
        mPasscodeView.setEnable(true);
        mKeyboardView.setEnable(true);

        startFingerprintAuthIfPossible();
    }

    @Override
    public void onRetryLockUpdate(long ms) {
        mTvMessage.setText(getString(R.string.coccoc_pin_msg_locked, ms / 1000));
    }

    @Override
    public void onAuthWithFingerprintFailed() {
        mTvTitle.setText(R.string.coccoc_pin_title_wrong_fingerprint);
        mTvMessage.setText(R.string.coccoc_pin_msg_wrong_pin);
        mPasscodeView.reset();
    }

    @Override
    public void onAuthWithFingerprintLock() {
        mTvTitle.setText(R.string.coccoc_pin_title_locked);
        mTvMessage.setText(R.string.coccoc_pin_msg_locked);
        mPasscodeView.reset();
    }

    @Override
    public PinConfig.Type getPinType() {
        return PinConfig.Type.PIN_PASSCODE;
    }

    @Override
    public void onKeyNumberPressed(int value) {
        if (mPasscodeView.isEmpty()) {
            mKeyboardView.setKeyDeleteEnable(true);
        }

        mPasscodeView.addDigit(value);
        if (mPasscodeView.isValid()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPinController.checkPin(mPasscodeView.getPin());
                }
            }, 100);
        }
    }

    @Override
    public void onKeyDelPressed() {
        mPasscodeView.removeLastDigit();
        if (mPasscodeView.isEmpty()) {
            mKeyboardView.setKeyDeleteEnable(false);
        }
    }

    @Override
    public void onBackPressed() {
        // Remove current activity stack
        finishAffinity();
    }
}
