package coccoc.default_browser;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FeatureUsageStat {
    public static void recordFeatureUsage(int browserLock) {

    }

    @IntDef({CoccocFeature.NIGHT_MODE, CoccocFeature.SAVIOR, CoccocFeature.BROWSER_LOCK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CoccocFeature {
        int NIGHT_MODE = 0;
        int SAVIOR = 1;
        int BROWSER_LOCK = 2;
    }

}
