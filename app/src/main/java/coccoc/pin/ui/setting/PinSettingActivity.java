package coccoc.pin.ui.setting;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demofingerprintauth.R;

import coccoc.pin.PinConfig;
import coccoc.pin.PinManager;
import coccoc.pin.views.PinKeyboardView;
import coccoc.pin.views.PinPasscodeView;

public class PinSettingActivity extends AppCompatActivity implements PinSettingView, PinKeyboardView.OnKeyPressListener {

    public static final String EXTRAS_PIN_OFF = "extras_pin_setting_mode";

    private PinSettingController<PinSettingView> mPinController;
    private TextView mTvTitle;
    private PinPasscodeView mPasscodeView;
    private PinManager mPinManager = PinManager.getInstance();
    private Handler mHandler = new Handler();
    private PinKeyboardView mKeyboardView;
    private boolean isLock;
    private View mBtnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coccoc_pin_setting_activity);
        initController();
        initUI();
    }

    @Override
    protected void onDestroy() {
        mPinController.detach();
        super.onDestroy();
    }

    private void initController() {
        mPinController = new PinSettingControllerImpl<>();
        mPinController.attach(this);
    }

    private void initUI() {
        setTitle(R.string.coccoc_setting_title_pin);

        mTvTitle = findViewById(R.id.tvTitle);
        mPasscodeView = findViewById(R.id.pinView);

        mBtnCancel = findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(this::onCancel);

        mKeyboardView = findViewById(R.id.keyboardView);
        mKeyboardView.setKeyPressListener(this);

        boolean turningOff = getIntent().getBooleanExtra(EXTRAS_PIN_OFF, false);
        mPinController.init(turningOff);

        mPinController.resumeRetryTimerIfPossible();
    }

    private void onCancel(View view) {
        if (!isLock) {
            mPasscodeView.reset();
            finish();
        }
    }

    @Override
    public PinConfig.Type getPinType() {
        return PinConfig.Type.PIN_PASSCODE;
    }

    @Override
    public void onRetryLockStart() {
        this.isLock = true;
        mPasscodeView.reset();
        mPasscodeView.setEnable(false);
        mKeyboardView.setEnable(false);
        mBtnCancel.setEnabled(false);
    }

    @Override
    public void onRetryLockFinished() {
        this.isLock = false;
        mPasscodeView.reset();
        mPasscodeView.setEnable(true);
        mKeyboardView.setEnable(true);
        mBtnCancel.setEnabled(true);
        mTvTitle.setText(R.string.coccoc_setting_pin_enter_old_pin);
    }

    @Override
    public void onRetryLockUpdate(long timeRemainMs) {
        mTvTitle.setText(getString(R.string.coccoc_pin_msg_locked, timeRemainMs / 1000));
    }

    @Override
    public void enterSecondPin() {
        mPasscodeView.reset();
        mTvTitle.setText(R.string.coccoc_setting_pin_second_time);
    }

    @Override
    public void enterFirstPin() {
        mPasscodeView.reset();
        mTvTitle.setText(R.string.coccoc_setting_pin_first_time);
    }

    @Override
    public void enterOldPin() {
        mPasscodeView.reset();
        mTvTitle.setText(R.string.coccoc_setting_pin_enter_old_pin);
    }

    @Override
    public void onPinSaved() {
        if (!mPinManager.isPinFeatureOn()) {
            mPinManager.turnOnPin();
        }

        mPasscodeView.reset();
        finish();
    }

    @Override
    public void onPinNotMatch() {
        mPasscodeView.reset();
        mTvTitle.setText(R.string.coccoc_setting_pin_not_match);
    }

    @Override
    public void onPinFeatureOff() {
        finish();
    }

    @Override
    public void onKeyNumberPressed(int value) {
        if (mPasscodeView.isEmpty()) {
            mKeyboardView.setKeyDeleteEnable(true);
        }
        mPasscodeView.addDigit(value);
        if (mPasscodeView.isValid()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPinController.changePin(mPasscodeView.getPin());
                }
            }, 100);
        }
    }

    @Override
    public void onKeyDelPressed() {
        mPasscodeView.removeLastDigit();
        if (mPasscodeView.isEmpty()) {
            mKeyboardView.setKeyDeleteEnable(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (!isLock) {
            super.onBackPressed();
        }
    }
}
