package coccoc.pin.biometric;

public interface BiometricCallback {

    void onAuthenticationFailed();

    void onAuthenticationCancelled();

    void onAuthenticationSuccessful();

    void onAuthenticationHelp(int helpCode, CharSequence helpString);

    void onAuthenticationError(int errorCode, CharSequence errString);
}
