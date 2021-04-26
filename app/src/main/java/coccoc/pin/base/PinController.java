package coccoc.pin.base;

import coccoc.pin.model.Pin;

public interface PinController<V extends PinView> {
    void attach(V view);
    void detach();
    boolean isMatch(Pin pin);
    void resumeRetryTimerIfPossible();
}
