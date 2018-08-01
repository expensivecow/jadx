package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.util.Log;

public final class NavUtils {
    public static final String PARENT_ACTIVITY = "android.support.PARENT_ACTIVITY";
    private static final String TAG = "NavUtils";

    public static boolean shouldUpRecreateTask(Activity activity, Intent intent) {
        if (VERSION.SDK_INT >= 16) {
            return activity.shouldUpRecreateTask(intent);
        }
        String action = activity.getIntent().getAction();
        boolean z = (action == null || action.equals("android.intent.action.MAIN")) ? false : true;
        return z;
    }

    public static void navigateUpFromSameTask(Activity activity) {
        Intent parentActivityIntent = getParentActivityIntent(activity);
        if (parentActivityIntent == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Activity ");
            stringBuilder.append(activity.getClass().getSimpleName());
            stringBuilder.append(" does not have a parent activity name specified.");
            stringBuilder.append(" (Did you forget to add the android.support.PARENT_ACTIVITY <meta-data> ");
            stringBuilder.append(" element in your manifest?)");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        navigateUpTo(activity, parentActivityIntent);
    }

    public static void navigateUpTo(Activity activity, Intent intent) {
        if (VERSION.SDK_INT >= 16) {
            activity.navigateUpTo(intent);
            return;
        }
        intent.addFlags(67108864);
        activity.startActivity(intent);
        activity.finish();
    }

    public static Intent getParentActivityIntent(Activity activity) {
        if (VERSION.SDK_INT >= 16) {
            Intent parentActivityIntent = activity.getParentActivityIntent();
            if (parentActivityIntent != null) {
                return parentActivityIntent;
            }
        }
        String parentActivityName = getParentActivityName(activity);
        Intent intent = null;
        if (parentActivityName == null) {
            return intent;
        }
        ComponentName componentName = new ComponentName(activity, parentActivityName);
        try {
            Intent makeMainActivity;
            if (getParentActivityName(activity, componentName) == null) {
                makeMainActivity = Intent.makeMainActivity(componentName);
            } else {
                makeMainActivity = new Intent().setComponent(componentName);
            }
            return makeMainActivity;
        } catch (NameNotFoundException unused) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("getParentActivityIntent: bad parentActivityName '");
            stringBuilder.append(parentActivityName);
            stringBuilder.append("' in manifest");
            Log.e("NavUtils", stringBuilder.toString());
            return intent;
        }
    }

    public static Intent getParentActivityIntent(Context context, Class<?> cls) throws NameNotFoundException {
        String parentActivityName = getParentActivityName(context, new ComponentName(context, cls));
        if (parentActivityName == null) {
            return null;
        }
        Intent makeMainActivity;
        ComponentName componentName = new ComponentName(context, parentActivityName);
        if (getParentActivityName(context, componentName) == null) {
            makeMainActivity = Intent.makeMainActivity(componentName);
        } else {
            makeMainActivity = new Intent().setComponent(componentName);
        }
        return makeMainActivity;
    }

    public static Intent getParentActivityIntent(Context context, ComponentName componentName) throws NameNotFoundException {
        String parentActivityName = getParentActivityName(context, componentName);
        if (parentActivityName == null) {
            return null;
        }
        Intent makeMainActivity;
        ComponentName componentName2 = new ComponentName(componentName.getPackageName(), parentActivityName);
        if (getParentActivityName(context, componentName2) == null) {
            makeMainActivity = Intent.makeMainActivity(componentName2);
        } else {
            makeMainActivity = new Intent().setComponent(componentName2);
        }
        return makeMainActivity;
    }

    @Nullable
    public static String getParentActivityName(Activity activity) {
        try {
            return getParentActivityName(activity, activity.getComponentName());
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    public static String getParentActivityName(Context context, ComponentName componentName) throws NameNotFoundException {
        ActivityInfo activityInfo = context.getPackageManager().getActivityInfo(componentName, 128);
        if (VERSION.SDK_INT >= 16) {
            String str = activityInfo.parentActivityName;
            if (str != null) {
                return str;
            }
        }
        String str2 = null;
        if (activityInfo.metaData == null) {
            return str2;
        }
        String string = activityInfo.metaData.getString("android.support.PARENT_ACTIVITY");
        if (string == null) {
            return str2;
        }
        if (string.charAt(0) == '.') {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(context.getPackageName());
            stringBuilder.append(string);
            string = stringBuilder.toString();
        }
        return string;
    }

    private NavUtils() {
    }
}
