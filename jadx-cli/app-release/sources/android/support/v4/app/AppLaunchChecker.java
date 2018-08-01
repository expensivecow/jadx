package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat.EditorCompat;

public class AppLaunchChecker {
    private static final String KEY_STARTED_FROM_LAUNCHER = "startedFromLauncher";
    private static final String SHARED_PREFS_NAME = "android.support.AppLaunchChecker";

    public static boolean hasStartedFromLauncher(Context context) {
        boolean z = false;
        return context.getSharedPreferences("android.support.AppLaunchChecker", z).getBoolean("startedFromLauncher", z);
    }

    public static void onActivityCreate(Activity activity) {
        boolean z = false;
        SharedPreferences sharedPreferences = activity.getSharedPreferences("android.support.AppLaunchChecker", z);
        if (!sharedPreferences.getBoolean("startedFromLauncher", z)) {
            Intent intent = activity.getIntent();
            if (intent != null && "android.intent.action.MAIN".equals(intent.getAction()) && (intent.hasCategory("android.intent.category.LAUNCHER") || intent.hasCategory("android.intent.category.LEANBACK_LAUNCHER"))) {
                EditorCompat.getInstance().apply(sharedPreferences.edit().putBoolean("startedFromLauncher", true));
            }
        }
    }
}
