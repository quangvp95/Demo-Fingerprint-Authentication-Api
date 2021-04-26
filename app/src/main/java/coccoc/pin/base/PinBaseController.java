package coccoc.pin.base;

import android.os.CountDownTimer;

import coccoc.pin.PinConfig;
import coccoc.pin.PinManager;
import coccoc.pin.PinStorage;
import coccoc.pin.model.Pin;

public abstract class PinBaseController<V extends PinView> implements PinController<V> {
    private V mView;
    private CountDownTimer mRetryTimer;
    private PinManager mPinManager = PinManager.getInstance();
    protected PinStorage mPinStorage = PinStorage.getInstance();
    private int mNumRetry;

    protected PinBaseController() {
        this.mNumRetry = 0;
    }

    @Override
    public void attach(V view) {
        this.mView = view;
    }

    @Override
    public void detach() {
        if (mRetryTimer != null) {
            mRetryTimer.cancel();
            mRetryTimer = null;
        }
    }

    public V getView() {
        return mView;
    }

    @Override
    public void resumeRetryTimerIfPossible() {
        if (mPinManager.isRetryTimerFinished()) return;

        long remainTimer = mPinManager.getRetryTimer();
        startRetryTimer(remainTimer);
    }

    private void startRetryTimer(long timer) {
        getView().onRetryLockStart();
        mRetryTimer = new CountDownTimer(timer, 1000) {
            @Override
            public void onTick(long remainMilliseconds) {
                getView().onRetryLockUpdate(remainMilliseconds);
            }

            @Override
            public void onFinish() {
                getView().onRetryLockFinished();
            }
        };

        mRetryTimer.start();
    }

    protected boolean exceedRetryLimit() {
        return ++mNumRetry >= PinConfig.MAX_PIN_INPUT;
    }

    protected void runRetryTimer() {
        if (mPinManager.isRetryTimerFinished()) {
            mPinManager.saveLastRetryTimer(System.currentTimeMillis());
            startRetryTimer(PinConfig.PIN_RETRY_TIMER);
        }
        mNumRetry = 0;
    }

    protected boolean isMatchWithDatabase(Pin inputPin) {
        Pin pin = mPinStorage.getPin(getView().getPinType());
        return pin != null && pin.equals(inputPin);
    }
}
