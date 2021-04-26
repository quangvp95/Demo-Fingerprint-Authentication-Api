package coccoc.pin.ui.setting;

import coccoc.base.tracking.TrackingUtils;
import coccoc.pin.PinManager;
import coccoc.pin.base.PinBaseController;
import coccoc.pin.model.Pin;

public class PinSettingControllerImpl<V extends PinSettingView> extends PinBaseController<V> implements PinSettingController<V> {

    private static final int STEP_INIT = 0;
    private static final int STEP_ENTER_OLD_PIN = STEP_INIT + 1;
    private static final int STEP_ENTER_FIRST_PIN = STEP_INIT + 2;
    private static final int STEP_ENTER_SECOND_PIN = STEP_INIT + 3;
    private static final int STEP_FINISHED = STEP_INIT + 4;

    private Pin mFirstPin;
    private int mChangeStep;
    private boolean mTurnOffPin;

    PinSettingControllerImpl() {
        super();
    }

    @Override
    public void init(boolean turnOffPin) {
        this.mTurnOffPin = turnOffPin;

        if (!mPinStorage.hasPin()) mChangeStep = STEP_ENTER_FIRST_PIN;
        else mChangeStep = STEP_ENTER_OLD_PIN;

        goNextStep();
    }

    @Override
    public boolean isMatch(Pin inputPin) {
        if (inputPin == null) return false;

        if (mFirstPin != null && mChangeStep == STEP_ENTER_SECOND_PIN) {
            // Compare with first pin
            return mFirstPin.equals(inputPin);
        } else {
            // Compare with old pin
            return isMatchWithDatabase(inputPin);
        }
    }

    @Override
    public void changePin(Pin inputPin) {
        boolean matched = true;

        switch (mChangeStep) {
            case STEP_ENTER_OLD_PIN:
                matched = isMatch(inputPin);
                if (matched) {
                    mChangeStep++;
                } else {
                    boolean hasExceed = exceedRetryLimit();
                    if (hasExceed) {
                        runRetryTimer();
                    }
                }
                break;
            case STEP_ENTER_FIRST_PIN:
                mFirstPin = inputPin;
                mChangeStep++;
                break;
            case STEP_ENTER_SECOND_PIN:
                matched = isMatch(inputPin);
                if (matched) {
                    mChangeStep++;
                    mFirstPin = null;
                }
                break;
        }

        if (mChangeStep >= STEP_FINISHED) {
            TrackingUtils.logTrackingEvent(TrackingUtils.TRACK_PIN_USE);
            mPinStorage.setPin(inputPin);
        }

        if (matched) {
            // If user is changing PIN status to disable, just turn off PIN feature in database
            // and notify to View. Otherwise, go to next step
            if (mTurnOffPin) {
                PinManager.getInstance().turnOffPin();
                getView().onPinFeatureOff();
            } else {
                goNextStep();
            }
        } else {
            getView().onPinNotMatch();
        }
    }

    private void goNextStep() {
        PinSettingView view = getView();
        switch (mChangeStep) {
            case STEP_ENTER_OLD_PIN:
                view.enterOldPin();
                break;
            case STEP_ENTER_FIRST_PIN:
                view.enterFirstPin();
                break;
            case STEP_ENTER_SECOND_PIN:
                view.enterSecondPin();
                break;
            case STEP_FINISHED:
                view.onPinSaved();
                break;
        }
    }
}
