package coccoc.pin.model;

import androidx.annotation.Nullable;

public class PinPasscode extends Pin {
    private int mPasscode;

    public PinPasscode(int passcode) {
        this.mPasscode = passcode;
    }

    public int getPasscode() {
        return mPasscode;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof PinPasscode) {
            PinPasscode p = (PinPasscode) obj;
            return p.mPasscode == mPasscode;
        }
        return super.equals(obj);
    }

    @Override
    public boolean isEmpty() {
        return mPasscode < 0;
    }

    @Override
    public String toPrefValue() {
        return String.valueOf(mPasscode);
    }
}
