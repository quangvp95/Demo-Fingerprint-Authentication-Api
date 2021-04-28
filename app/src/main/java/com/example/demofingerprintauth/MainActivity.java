package com.example.demofingerprintauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.chromium.base.ContextUtils;

import coccoc.pin.biometric.BiometricManager;
import coccoc.pin.ui.unlock.PinUnlockActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextUtils.mContext = getApplicationContext();

    }

    public void useFingerprintManager(View view) {
        BiometricManager.sType = BiometricManager.BiometricFeature.FINGERPRINT_MANAGER;

        Intent mIntent = new Intent(this, MainActivity.class);
        // Check if we should show PIN unlock activity
        PinUnlockActivity.launch(this, mIntent);

    }

    public void useBiometricPrompt(View view) {
        BiometricManager.sType = BiometricManager.BiometricFeature.BIOMETRIC_PROMPT;

        Intent mIntent = new Intent(this, MainActivity.class);
        // Check if we should show PIN unlock activity
        PinUnlockActivity.launch(this, mIntent);
    }
}