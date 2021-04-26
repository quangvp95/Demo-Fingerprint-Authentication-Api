package org.chromium.base;

import androidx.annotation.IntDef;

@IntDef({ActivityState.CREATED, ActivityState.STARTED, ActivityState.RESUMED, ActivityState.PAUSED,
    ActivityState.STOPPED, ActivityState.DESTROYED})
public @interface ActivityState {
    int CREATED = 1;

    /**
     * Represents Activity#onStart().
     */
    int STARTED = 2;

    /**
     * Represents Activity#onResume().
     */
    int RESUMED = 3;

    /**
     * Represents Activity#onPause().
     */
    int PAUSED = 4;

    /**
     * Represents Activity#onStop().
     */
    int STOPPED = 5;

    /**
     * Represents Activity#onDestroy().  This is also used when the state of an Activity is unknown.
     */
    int DESTROYED = 6;
}
