package coccoc.pin.biometric;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintManagerCompatManager extends BiometricManager {
    private static final String KEY_NAME = UUID.randomUUID().toString();

    private Cipher mCipher;
    private KeyStore mKeyStore;

    private CancellationSignal mCancellationSignal;

    private static FingerprintManagerCompatManager sInstance;

    public static FingerprintManagerCompatManager getInstance() {
        if (sInstance == null) {
            sInstance = new FingerprintManagerCompatManager();
        }
        return sInstance;
    }

    private FingerprintManagerCompatManager() {
        super();
    }

    @Override
    public void startBiometricAuth(FragmentActivity context, final BiometricCallback biometricCallback) {
        generateKey();

        if (initCipher()) {

            mCancellationSignal = new CancellationSignal();
            // TODO(cc_android): Deprecated FingerprintManagerCompat. New androidx_core lib uses
            // standard dialog popup for biometrics. We need to update Browser Lock UI to use this
            // popup. Refer to https://medium.com/mindorks/fingerprint-authentication-using-biometricprompt-compat-1466365b4795
            // and iOS implementation.
            FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(mCipher);
            FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
            fingerprintManagerCompat.authenticate(cryptoObject, 0, mCancellationSignal,
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        biometricCallback.onAuthenticationError(errMsgId, errString);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        biometricCallback.onAuthenticationHelp(helpMsgId, helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        biometricCallback.onAuthenticationSuccessful();
                    }


                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        biometricCallback.onAuthenticationFailed();
                    }
                }, null);
        }
    }

    @Override
    public void stopBiometricAuth() {
        if (mCancellationSignal != null && !mCancellationSignal.isCanceled()) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }


    private void generateKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new
                KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
            | NoSuchAlgorithmException
            | NoSuchProviderException
            | InvalidAlgorithmParameterException
            | CertificateException
            | IOException exc) {
            exc.printStackTrace();
        }
    }


    private boolean initCipher() {
        try {
            mCipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME,
                null);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
