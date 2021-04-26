package org.chromium.base;

import android.app.Activity;

public class ApplicationStatus {

    /**
     * Interface to be implemented by listeners.
     */
    public interface ActivityStateListener {
        /**
         * Called when the activity's state changes.
         * @param activity The activity that had a state change.
         * @param newState New activity state.
         */
        void onActivityStateChange(Activity activity, int newState);
    }

    public static boolean hasVisibleActivities() {
        return false;
    }
}
