package coccoc.pin.ui.setting;

import coccoc.pin.base.PinController;
import coccoc.pin.model.Pin;

public interface PinSettingController<V extends PinSettingView> extends PinController<V> {
    /**
     * @param turnOffPin - If user is changing PIN status from enable to disable, set this value is true.
     */
    void init(boolean turnOffPin);
    void changePin(Pin pin);
}
