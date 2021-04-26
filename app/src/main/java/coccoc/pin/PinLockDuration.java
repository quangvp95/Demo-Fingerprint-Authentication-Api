package coccoc.pin;

import androidx.annotation.IntDef;

import org.chromium.base.ContextUtils;
import com.example.demofingerprintauth.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PinLockDuration {

    private static final long ONE_MINUTE = 60 * 1000;

    @IntDef({
            LockDuration.IMMEDIATELY, LockDuration.THIRDTY_SECONDS,
            LockDuration.ONE_MINUTE, LockDuration.TWO_MINUTES,
            LockDuration.FIVE_MINUTES
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface LockDuration {
        int IMMEDIATELY = 0;
        int THIRDTY_SECONDS = 1;
        int ONE_MINUTE = 2;
        int TWO_MINUTES = 3;
        int FIVE_MINUTES = 4;

        int NUM_ENTRY = 5;
    }

    private final static String[] LOCK_DURATION_STRINGS = {
            ContextUtils.getApplicationContext().getResources().getString(R.string.coccoc_pin_setting_duration_immediately),
            ContextUtils.getApplicationContext().getResources().getQuantityString(R.plurals.coccoc_pin_setting_duration_seconds, 30, 30),
            ContextUtils.getApplicationContext().getResources().getQuantityString(R.plurals.coccoc_pin_setting_duration_minutes, 1, 1),
            ContextUtils.getApplicationContext().getResources().getQuantityString(R.plurals.coccoc_pin_setting_duration_minutes, 2, 2),
            ContextUtils.getApplicationContext().getResources().getQuantityString(R.plurals.coccoc_pin_setting_duration_minutes, 5, 5),
    };

    public static String toString(@LockDuration int value) {
        return LOCK_DURATION_STRINGS[value];
    }

    public static @LockDuration int fromString(String value) {
        for (int i = 0; i < LockDuration.NUM_ENTRY; i++) {
            if (LOCK_DURATION_STRINGS[i].equals(value)) return i;
        }
        return LockDuration.IMMEDIATELY;
    }

    public static String getLockDurationSummary(@LockDuration int value) {
        if (value == LockDuration.IMMEDIATELY) {
            return ContextUtils.getApplicationContext().getString(R.string.coccoc_pin_setting_duration_summary_immediately);
        }
        return ContextUtils.getApplicationContext().getString(R.string.coccoc_pin_setting_duration_summary, toString(value));
    }

    public static long getLockDurationInMs(@LockDuration int value) {
        switch (value) {
            case LockDuration.IMMEDIATELY:
                return 0;
            case LockDuration.THIRDTY_SECONDS:
                return ONE_MINUTE / 2;
            case LockDuration.ONE_MINUTE:
                return ONE_MINUTE;
            case LockDuration.TWO_MINUTES:
                return 2 * ONE_MINUTE;
            case LockDuration.FIVE_MINUTES:
                return 5 * ONE_MINUTE;

        }
        return 0;
    }
}
