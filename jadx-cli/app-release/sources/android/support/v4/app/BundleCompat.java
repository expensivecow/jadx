package android.support.v4.app;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import java.lang.reflect.Method;

public final class BundleCompat {

    static class BundleCompatBaseImpl {
        private static final String TAG = "BundleCompatBaseImpl";
        private static Method sGetIBinderMethod;
        private static boolean sGetIBinderMethodFetched;
        private static Method sPutIBinderMethod;
        private static boolean sPutIBinderMethodFetched;

        BundleCompatBaseImpl() {
        }

        public static IBinder getBinder(Bundle bundle, String str) {
            int i = 0;
            boolean z = true;
            if (!sGetIBinderMethodFetched) {
                try {
                    Class[] clsArr = new Class[z];
                    clsArr[i] = String.class;
                    sGetIBinderMethod = Bundle.class.getMethod("getIBinder", clsArr);
                    sGetIBinderMethod.setAccessible(z);
                } catch (Throwable e) {
                    Log.i("BundleCompatBaseImpl", "Failed to retrieve getIBinder method", e);
                }
                sGetIBinderMethodFetched = z;
            }
            Object obj = null;
            if (sGetIBinderMethod != null) {
                try {
                    Method method = sGetIBinderMethod;
                    Object[] objArr = new Object[z];
                    objArr[i] = str;
                    return (IBinder) method.invoke(bundle, objArr);
                } catch (Throwable e2) {
                    Log.i("BundleCompatBaseImpl", "Failed to invoke getIBinder via reflection", e2);
                    sGetIBinderMethod = obj;
                }
            }
            return obj;
        }

        public static void putBinder(Bundle bundle, String str, IBinder iBinder) {
            int i = 0;
            int i2 = 2;
            boolean z = true;
            if (!sPutIBinderMethodFetched) {
                try {
                    Class[] clsArr = new Class[i2];
                    clsArr[i] = String.class;
                    clsArr[z] = IBinder.class;
                    sPutIBinderMethod = Bundle.class.getMethod("putIBinder", clsArr);
                    sPutIBinderMethod.setAccessible(z);
                } catch (Throwable e) {
                    Log.i("BundleCompatBaseImpl", "Failed to retrieve putIBinder method", e);
                }
                sPutIBinderMethodFetched = z;
            }
            if (sPutIBinderMethod != null) {
                try {
                    Method method = sPutIBinderMethod;
                    Object[] objArr = new Object[i2];
                    objArr[i] = str;
                    objArr[z] = iBinder;
                    method.invoke(bundle, objArr);
                } catch (Throwable e2) {
                    Log.i("BundleCompatBaseImpl", "Failed to invoke putIBinder via reflection", e2);
                    sPutIBinderMethod = null;
                }
            }
        }
    }

    private BundleCompat() {
    }

    public static IBinder getBinder(Bundle bundle, String str) {
        if (VERSION.SDK_INT >= 18) {
            return bundle.getBinder(str);
        }
        return BundleCompatBaseImpl.getBinder(bundle, str);
    }

    public static void putBinder(Bundle bundle, String str, IBinder iBinder) {
        if (VERSION.SDK_INT >= 18) {
            bundle.putBinder(str, iBinder);
        } else {
            BundleCompatBaseImpl.putBinder(bundle, str, iBinder);
        }
    }
}
