package coccoc.pin.ui.unlock;

import androidx.fragment.app.FragmentActivity;

import coccoc.pin.base.PinController;
import coccoc.pin.model.Pin;

public interface PinUnlockController<V extends PinUnlockView> extends PinController<V> {
    void startBiometricAuth(FragmentActivity context);
    void stopBiometricAuth();
    void checkPin(Pin pin);
}
