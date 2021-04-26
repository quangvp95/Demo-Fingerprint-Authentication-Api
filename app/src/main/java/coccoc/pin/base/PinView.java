package coccoc.pin.base;

import coccoc.pin.PinConfig;

public interface PinView {
    PinConfig.Type getPinType();
    void onRetryLockStart();
    void onRetryLockFinished();
    void onRetryLockUpdate(long timeRemainMs);
}
