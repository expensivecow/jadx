package android.support.v4.content;

import android.content.Context;
import android.os.Binder;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.AppOpsManagerCompat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PermissionChecker {
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_DENIED_APP_OP = -2;
    public static final int PERMISSION_GRANTED = 0;

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionResult {
    }

    private PermissionChecker() {
    }

    public static int checkPermission(@NonNull Context context, @NonNull String str, int i, int i2, String str2) {
        int i3 = -1;
        if (context.checkPermission(str, i, i2) == i3) {
            return i3;
        }
        str = AppOpsManagerCompat.permissionToOp(str);
        i = 0;
        if (str == null) {
            return i;
        }
        if (str2 == null) {
            String[] packagesForUid = context.getPackageManager().getPackagesForUid(i2);
            if (packagesForUid == null || packagesForUid.length <= 0) {
                return i3;
            }
            str2 = packagesForUid[i];
        }
        return AppOpsManagerCompat.noteProxyOp(context, str, str2) != 0 ? -2 : i;
    }

    public static int checkSelfPermission(@NonNull Context context, @NonNull String str) {
        return checkPermission(context, str, Process.myPid(), Process.myUid(), context.getPackageName());
    }

    public static int checkCallingPermission(@NonNull Context context, @NonNull String str, String str2) {
        if (Binder.getCallingPid() == Process.myPid()) {
            return -1;
        }
        return checkPermission(context, str, Binder.getCallingPid(), Binder.getCallingUid(), str2);
    }

    public static int checkCallingOrSelfPermission(@NonNull Context context, @NonNull String str) {
        return checkPermission(context, str, Binder.getCallingPid(), Binder.getCallingUid(), Binder.getCallingPid() == Process.myPid() ? context.getPackageName() : null);
    }
}
