package coccoc.pin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demofingerprintauth.R;

import java.util.ArrayList;
import java.util.List;

import coccoc.pin.PinConfig;
import coccoc.pin.model.Pin;
import coccoc.pin.model.PinPasscode;

public class PinPasscodeView extends LinearLayout {

    private List<View> mDigitViews = new ArrayList<>(PinConfig.MAX_PIN_DIGIT);
    private StringBuilder mPinBuilder = new StringBuilder();

    public PinPasscodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout();
        reset();
    }

    private void inflateLayout() {
        setOrientation(HORIZONTAL);
        for (int i = 0; i < PinConfig.MAX_PIN_DIGIT; i++) {
            View dot = LayoutInflater.from(getContext()).inflate(R.layout.coccoc_pin_dot, this, false);
            addView(dot);
            mDigitViews.add(dot);
        }
    }

    public void reset() {
        mPinBuilder.setLength(0);
        for (int i = 0; i < mDigitViews.size(); i++) {
            mDigitViews.get(i).setBackgroundResource(R.drawable.bg_pin_dot_inactive);
        }
    }

    public Pin getPin() {
        if (isValid()) {
            int pinValue = Integer.parseInt(mPinBuilder.toString());
            return new PinPasscode(pinValue);
        }
        return null;
    }

    public void addDigit(int value) {
        int length = mPinBuilder.length();
        if (length < PinConfig.MAX_PIN_DIGIT) {
            mDigitViews.get(length).setBackgroundResource(R.drawable.bg_pin_dot_active);
            mPinBuilder.append(value);
        }
    }

    public void removeLastDigit() {
        int length = mPinBuilder.length();
        if (length > 0) {
            mDigitViews.get(length - 1).setBackgroundResource(R.drawable.bg_pin_dot_inactive);
            mPinBuilder.deleteCharAt(length - 1);
        }
    }

    public boolean isValid() {
        return mPinBuilder.length() == PinConfig.MAX_PIN_DIGIT;
    }

    public boolean isEmpty() {
        return mPinBuilder.length() <= 0;
    }

    public void setEnable(boolean enable) {
        setAlpha(enable ? 1.0f : 0.5f);
    }

}
