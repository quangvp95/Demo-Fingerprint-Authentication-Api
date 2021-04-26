package coccoc.pin.ui.unlock;

import coccoc.pin.base.PinView;

public interface PinUnlockView extends PinView {
    void onPinNotMatch();
    void onUnlock();

    void onAuthWithFingerprintFailed();
    void onAuthWithFingerprintLock();
}
