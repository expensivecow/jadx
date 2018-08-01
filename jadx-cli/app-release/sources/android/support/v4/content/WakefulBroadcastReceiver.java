package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.util.SparseArray;

@Deprecated
public abstract class WakefulBroadcastReceiver extends BroadcastReceiver {
    private static final String EXTRA_WAKE_LOCK_ID = "android.support.content.wakelockid";
    private static int mNextId = 1;
    private static final SparseArray<WakeLock> sActiveWakeLocks = new SparseArray();

    public static ComponentName startWakefulService(Context context, Intent intent) {
        synchronized (sActiveWakeLocks) {
            int i = mNextId;
            int i2 = 1;
            mNextId += i2;
            if (mNextId <= 0) {
                mNextId = i2;
            }
            intent.putExtra("android.support.content.wakelockid", i);
            ComponentName startService = context.startService(intent);
            if (startService == null) {
                ComponentName componentName = null;
                return componentName;
            }
            PowerManager powerManager = (PowerManager) context.getSystemService("power");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("wake:");
            stringBuilder.append(startService.flattenToShortString());
            WakeLock newWakeLock = powerManager.newWakeLock(i2, stringBuilder.toString());
            newWakeLock.setReferenceCounted(false);
            newWakeLock.acquire(60000);
            sActiveWakeLocks.put(i, newWakeLock);
            return startService;
        }
    }

    public static boolean completeWakefulIntent(Intent intent) {
        boolean z = false;
        int intExtra = intent.getIntExtra("android.support.content.wakelockid", z);
        if (intExtra == 0) {
            return z;
        }
        synchronized (sActiveWakeLocks) {
            WakeLock wakeLock = (WakeLock) sActiveWakeLocks.get(intExtra);
            boolean z2 = true;
            if (wakeLock != null) {
                wakeLock.release();
                sActiveWakeLocks.remove(intExtra);
                return z2;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No active wake lock id #");
            stringBuilder.append(intExtra);
            Log.w("WakefulBroadcastReceiv.", stringBuilder.toString());
            return z2;
        }
    }
}
