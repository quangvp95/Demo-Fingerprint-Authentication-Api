package coccoc.pin;

import org.chromium.base.ContextUtils;

import coccoc.pin.model.Pin;
import coccoc.pin.model.PinPasscode;

public class PinStorage {
    private static final String KEY_PIN = "key_pin";

    private static PinStorage sInstance;

    public static PinStorage getInstance() {
        if (sInstance == null) {
            sInstance = new PinStorage();
        }
        return sInstance;
    }

    private PinStorage() {
    }

    public Pin getPin(PinConfig.Type type) {
        String pinValue = ContextUtils.getAppSharedPreferences().getString(KEY_PIN, null);
        if (pinValue == null) return null;

        if (type == PinConfig.Type.PIN_PASSCODE) {
            int passcode = Integer.parseInt(pinValue);
            return new PinPasscode(passcode);
        }
        return null;
    }

    public void setPin(Pin pin) {
        ContextUtils.getAppSharedPreferences().edit().putString(KEY_PIN, pin.toPrefValue()).apply();
    }

    public boolean hasPin() {
        String pinValue = ContextUtils.getAppSharedPreferences().getString(KEY_PIN, null);
        return pinValue != null;
    }

    public void removePin() {
        ContextUtils.getAppSharedPreferences().edit().putString(KEY_PIN, null).apply();
    }
}
