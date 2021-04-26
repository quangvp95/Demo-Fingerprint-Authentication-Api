package coccoc.pin.ui.setting;

import coccoc.pin.base.PinView;

public interface PinSettingView extends PinView {

    void enterSecondPin();

    void enterFirstPin();

    void enterOldPin();

    void onPinSaved();

    void onPinNotMatch();

    void onPinFeatureOff();
}
