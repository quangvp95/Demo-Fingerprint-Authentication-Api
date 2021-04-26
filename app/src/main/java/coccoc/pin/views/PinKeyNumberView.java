package coccoc.pin.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.demofingerprintauth.R;

public class PinKeyNumberView extends TextView {
    private int mKeyValue;

    public PinKeyNumberView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.PinKeyNumberView,
                0, 0);

        try {
            mKeyValue = a.getInteger(R.styleable.PinKeyNumberView_value, 0);
        } finally {
            a.recycle();
        }

        setText(String.valueOf(mKeyValue));
    }

    public int getValue() {
        return mKeyValue;
    }
}
