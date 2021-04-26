package org.chromium.base;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class IntentUtils {

    public static boolean safeStartActivity(Context context, Intent intent) {
        return safeStartActivity(context, intent, null);
    }

    /**
     * Catches any failures to start an Activity.
     * @param context Context to use when starting the Activity.
     * @param intent  Intent to fire.
     * @param bundle  Bundle of launch options.
     * @return Whether or not Android accepted the Intent.
     */
    public static boolean safeStartActivity(
        Context context, Intent intent, @Nullable Bundle bundle) {
        try {
            context.startActivity(intent, bundle);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public static PendingIntent safeGetParcelableExtra(Intent intent,
        String extraCompleteUnlockIntent) {
        return null;
    }
}
