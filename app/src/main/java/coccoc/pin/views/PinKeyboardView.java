package coccoc.pin.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.demofingerprintauth.R;

public class PinKeyboardView extends FrameLayout {

    public interface OnKeyPressListener {
        void onKeyNumberPressed(int value);

        void onKeyDelPressed();
    }

    private OnKeyPressListener mKeyPressListener;
    private boolean mEnabled;
    private View mDelKey;

    public PinKeyboardView(@NonNull Context context) {
        super(context);
        inflateLayout(context);
    }

    public PinKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflateLayout(context);
    }

    public PinKeyboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflateLayout(context);
    }

    private void inflateLayout(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.coccoc_view_pin_keyboard, this, false);
        addView(v);

        v.findViewById(R.id.key0).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key1).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key2).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key3).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key4).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key5).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key6).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key7).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key8).setOnClickListener(this::onKeyPress);
        v.findViewById(R.id.key9).setOnClickListener(this::onKeyPress);

        mDelKey = v.findViewById(R.id.keyDel);
        mDelKey.setOnClickListener(this::onKeyPress);

        setEnable(true);
    }

    public void setKeyPressListener(OnKeyPressListener mKeyPressListener) {
        this.mKeyPressListener = mKeyPressListener;
    }

    public void onKeyPress(View v) {
        if (!mEnabled) return;

        if (v instanceof PinKeyNumberView) {
            PinKeyNumberView numberKey = (PinKeyNumberView) v;
            if (mKeyPressListener != null) {
                mKeyPressListener.onKeyNumberPressed(numberKey.getValue());
            }
        } else if (v instanceof PinKeyDelView) {
            if (mKeyPressListener != null) {
                mKeyPressListener.onKeyDelPressed();
            }
        }
    }

    public void setEnable(boolean enable) {
        this.mEnabled = enable;
        setAlpha(enable ? 1.0f : 0.5f);
        setKeyDeleteEnable(false);
    }

    public void setKeyDeleteEnable(boolean enable) {
        mDelKey.setAlpha(enable ? 1.0f : 0.5f);
    }
}
