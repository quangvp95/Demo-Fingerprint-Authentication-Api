package org.chromium.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.sql.ResultSet;

public class ContextUtils {
    public static Context mContext;

    public static SharedPreferences getAppSharedPreferences() {
        return mContext.getSharedPreferences("asdf", Context.MODE_PRIVATE);
    }

    public static Context getApplicationContext() {
        return mContext;
    }
}
