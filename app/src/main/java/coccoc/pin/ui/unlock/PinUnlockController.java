package coccoc.pin.ui.unlock;

import android.content.Context;

import coccoc.pin.base.PinController;
import coccoc.pin.model.Pin;

public interface PinUnlockController<V extends PinUnlockView> extends PinController<V> {
    void startBiometricAuth(Context context);
    void stopBiometricAuth();
    void checkPin(Pin pin);
}
