package coccoc.pin.biometric;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.M)
public class BiometricPromptManager extends BiometricManager {

    private static BiometricPromptManager sInstance;

    public static BiometricPromptManager getInstance() {
        if (sInstance == null) {
            sInstance = new BiometricPromptManager();
        }
        return sInstance;
    }

    private BiometricPromptManager() {
        super();
    }

    @Override
    public void startBiometricAuth(FragmentActivity context,
        final BiometricCallback biometricCallback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        BiometricPrompt biometricPrompt = new BiometricPrompt(context, executor,
            new BiometricPrompt.AuthenticationCallback() {

                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    biometricCallback.onAuthenticationError(errorCode, errString);
                }

                @Override
                public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    biometricCallback.onAuthenticationSuccessful();
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    biometricCallback.onAuthenticationFailed();
                }

            });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Set the title to display.")
            .setSubtitle("Set the subtitle to display.")
            .setDescription("Set the description to display")
            .setNegativeButtonText("Negative Button")
            .build();
        biometricPrompt.authenticate(promptInfo);
    }

    @Override
    public void stopBiometricAuth() {
    }
}
