package coccoc.pin.biometric;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.annotation.IntDef;
import androidx.fragment.app.FragmentActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@TargetApi(Build.VERSION_CODES.M)
public abstract class BiometricManager {

    @IntDef({BiometricFeature.BIOMETRIC_PROMPT, BiometricFeature.FINGERPRINT_MANAGER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BiometricFeature {
        int BIOMETRIC_PROMPT = 0;
        int FINGERPRINT_MANAGER = 1;
    }

    public static @BiometricFeature int sType;

    protected BiometricManager() {
    }

    public static BiometricManager getInstance() {
        switch (sType) {
            case BiometricFeature.BIOMETRIC_PROMPT:
                return BiometricPromptManager.getInstance();
            case BiometricFeature.FINGERPRINT_MANAGER:
                return FingerprintManagerCompatManager.getInstance();
        }
        return BiometricPromptManager.getInstance();
    }

    public abstract void startBiometricAuth(FragmentActivity context, final BiometricCallback biometricCallback);

    public abstract void stopBiometricAuth();
}
