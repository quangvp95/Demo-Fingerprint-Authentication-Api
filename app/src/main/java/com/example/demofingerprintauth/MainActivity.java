package com.example.demofingerprintauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.chromium.base.ContextUtils;

import coccoc.pin.ui.unlock.PinUnlockActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ContextUtils.mContext = getApplicationContext();

    }

    public void FingerprintManager(View view) {
        Intent mIntent = new Intent();
        // Check if we should show PIN unlock activity
        if (PinUnlockActivity.launch(this, mIntent)) {
        }

    }

    public void BiometricPrompt(View view) {
    }
}