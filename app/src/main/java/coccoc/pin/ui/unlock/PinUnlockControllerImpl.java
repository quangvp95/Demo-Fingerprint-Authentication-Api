package coccoc.pin.ui.unlock;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;

import coccoc.default_browser.FeatureUsageStat;
import coccoc.pin.base.PinBaseController;
import coccoc.pin.biometric.BiometricCallback;
import coccoc.pin.biometric.BiometricManager;
import coccoc.pin.model.Pin;

public class PinUnlockControllerImpl<V extends PinUnlockView> extends PinBaseController<V> implements PinUnlockController<V>, BiometricCallback {

    PinUnlockControllerImpl() {
        super();
    }

    @Override
    public void checkPin(Pin pin) {
        PinUnlockView view = getView();
        if (isMatch(pin)) {
            view.onUnlock();
            FeatureUsageStat.recordFeatureUsage(FeatureUsageStat.CoccocFeature.BROWSER_LOCK);
        } else {
            if (!exceedRetryLimit()) {
                view.onPinNotMatch();
            } else {
                runRetryTimer();
            }
        }
    }

    @Override
    public boolean isMatch(Pin inputPin) {
        return isMatchWithDatabase(inputPin);
    }

    @Override
    public void startBiometricAuth(Context context) {
        BiometricManager.getInstance().startBiometricAuth(context, this);
    }

    @Override
    public void stopBiometricAuth() {
        BiometricManager.getInstance().stopBiometricAuth();
    }

    @Override
    public void onAuthenticationFailed() {
        getView().onAuthWithFingerprintFailed();
    }

    @Override
    public void onAuthenticationCancelled() {
        // Do nothing
    }

    @Override
    public void onAuthenticationSuccessful() {
        getView().onUnlock();
        FeatureUsageStat.recordFeatureUsage(FeatureUsageStat.CoccocFeature.BROWSER_LOCK);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        // Do nothing
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (errorCode == BiometricPrompt.BIOMETRIC_ERROR_LOCKOUT) {
            runRetryTimer();
            getView().onAuthWithFingerprintLock();
        }
    }
}
