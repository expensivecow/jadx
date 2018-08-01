package android.support.v4.app;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.support.annotation.GuardedBy;
import android.support.v4.app.INotificationSideChannel.Stub;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class NotificationManagerCompat {
    public static final String ACTION_BIND_SIDE_CHANNEL = "android.support.BIND_NOTIFICATION_SIDE_CHANNEL";
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    public static final String EXTRA_USE_SIDE_CHANNEL = "android.support.useSideChannel";
    public static final int IMPORTANCE_DEFAULT = 3;
    public static final int IMPORTANCE_HIGH = 4;
    public static final int IMPORTANCE_LOW = 2;
    public static final int IMPORTANCE_MAX = 5;
    public static final int IMPORTANCE_MIN = 1;
    public static final int IMPORTANCE_NONE = 0;
    public static final int IMPORTANCE_UNSPECIFIED = -1000;
    static final int MAX_SIDE_CHANNEL_SDK_VERSION = 19;
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final String SETTING_ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final int SIDE_CHANNEL_RETRY_BASE_INTERVAL_MS = 1000;
    private static final int SIDE_CHANNEL_RETRY_MAX_COUNT = 6;
    private static final String TAG = "NotifManCompat";
    @GuardedBy("sEnabledNotificationListenersLock")
    private static Set<String> sEnabledNotificationListenerPackages = new HashSet();
    @GuardedBy("sEnabledNotificationListenersLock")
    private static String sEnabledNotificationListeners;
    private static final Object sEnabledNotificationListenersLock = new Object();
    private static final Object sLock = new Object();
    @GuardedBy("sLock")
    private static SideChannelManager sSideChannelManager;
    private final Context mContext;
    private final NotificationManager mNotificationManager = ((NotificationManager) this.mContext.getSystemService("notification"));

    private static class ServiceConnectedEvent {
        final ComponentName componentName;
        final IBinder iBinder;

        ServiceConnectedEvent(ComponentName componentName, IBinder iBinder) {
            this.componentName = componentName;
            this.iBinder = iBinder;
        }
    }

    private static class SideChannelManager implements Callback, ServiceConnection {
        private static final int MSG_QUEUE_TASK = 0;
        private static final int MSG_RETRY_LISTENER_QUEUE = 3;
        private static final int MSG_SERVICE_CONNECTED = 1;
        private static final int MSG_SERVICE_DISCONNECTED = 2;
        private Set<String> mCachedEnabledPackages = new HashSet();
        private final Context mContext;
        private final Handler mHandler;
        private final HandlerThread mHandlerThread;
        private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();

        private static class ListenerRecord {
            public boolean bound;
            public final ComponentName componentName;
            public int retryCount;
            public INotificationSideChannel service;
            public LinkedList<Task> taskQueue = new LinkedList();

            public ListenerRecord(ComponentName componentName) {
                boolean z = false;
                this.bound = z;
                this.retryCount = z;
                this.componentName = componentName;
            }
        }

        public SideChannelManager(Context context) {
            this.mContext = context;
            this.mHandlerThread = new HandlerThread("NotificationManagerCompat");
            this.mHandlerThread.start();
            this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
        }

        public void queueTask(Task task) {
            this.mHandler.obtainMessage(0, task).sendToTarget();
        }

        public boolean handleMessage(Message message) {
            boolean z = true;
            switch (message.what) {
                case 0:
                    handleQueueTask((Task) message.obj);
                    return z;
                case 1:
                    ServiceConnectedEvent serviceConnectedEvent = (ServiceConnectedEvent) message.obj;
                    handleServiceConnected(serviceConnectedEvent.componentName, serviceConnectedEvent.iBinder);
                    return z;
                case 2:
                    handleServiceDisconnected((ComponentName) message.obj);
                    return z;
                case 3:
                    handleRetryListenerQueue((ComponentName) message.obj);
                    return z;
                default:
                    return false;
            }
        }

        private void handleQueueTask(Task task) {
            updateListenerMap();
            for (ListenerRecord listenerRecord : this.mRecordMap.values()) {
                listenerRecord.taskQueue.add(task);
                processListenerQueue(listenerRecord);
            }
        }

        private void handleServiceConnected(ComponentName componentName, IBinder iBinder) {
            ListenerRecord listenerRecord = (ListenerRecord) this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                listenerRecord.service = Stub.asInterface(iBinder);
                listenerRecord.retryCount = 0;
                processListenerQueue(listenerRecord);
            }
        }

        private void handleServiceDisconnected(ComponentName componentName) {
            ListenerRecord listenerRecord = (ListenerRecord) this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                ensureServiceUnbound(listenerRecord);
            }
        }

        private void handleRetryListenerQueue(ComponentName componentName) {
            ListenerRecord listenerRecord = (ListenerRecord) this.mRecordMap.get(componentName);
            if (listenerRecord != null) {
                processListenerQueue(listenerRecord);
            }
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            if (Log.isLoggable("NotifManCompat", 3)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Connected to service ");
                stringBuilder.append(componentName);
                Log.d("NotifManCompat", stringBuilder.toString());
            }
            this.mHandler.obtainMessage(1, new ServiceConnectedEvent(componentName, iBinder)).sendToTarget();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            if (Log.isLoggable("NotifManCompat", 3)) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Disconnected from service ");
                stringBuilder.append(componentName);
                Log.d("NotifManCompat", stringBuilder.toString());
            }
            this.mHandler.obtainMessage(2, componentName).sendToTarget();
        }

        private void updateListenerMap() {
            Set enabledListenerPackages = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
            if (!enabledListenerPackages.equals(this.mCachedEnabledPackages)) {
                StringBuilder stringBuilder;
                int i;
                this.mCachedEnabledPackages = enabledListenerPackages;
                List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(new Intent().setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 0);
                Set hashSet = new HashSet();
                for (ResolveInfo resolveInfo : queryIntentServices) {
                    if (enabledListenerPackages.contains(resolveInfo.serviceInfo.packageName)) {
                        ComponentName componentName = new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
                        if (resolveInfo.serviceInfo.permission != null) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Permission present on component ");
                            stringBuilder.append(componentName);
                            stringBuilder.append(", not adding listener record.");
                            Log.w("NotifManCompat", stringBuilder.toString());
                        } else {
                            hashSet.add(componentName);
                        }
                    }
                }
                Iterator it = hashSet.iterator();
                while (true) {
                    i = 3;
                    if (!it.hasNext()) {
                        break;
                    }
                    ComponentName componentName2 = (ComponentName) it.next();
                    if (!this.mRecordMap.containsKey(componentName2)) {
                        if (Log.isLoggable("NotifManCompat", i)) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Adding listener record for ");
                            stringBuilder2.append(componentName2);
                            Log.d("NotifManCompat", stringBuilder2.toString());
                        }
                        this.mRecordMap.put(componentName2, new ListenerRecord(componentName2));
                    }
                }
                it = this.mRecordMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry entry = (Entry) it.next();
                    if (!hashSet.contains(entry.getKey())) {
                        if (Log.isLoggable("NotifManCompat", i)) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Removing listener record for ");
                            stringBuilder.append(entry.getKey());
                            Log.d("NotifManCompat", stringBuilder.toString());
                        }
                        ensureServiceUnbound((ListenerRecord) entry.getValue());
                        it.remove();
                    }
                }
            }
        }

        private boolean ensureServiceBound(ListenerRecord listenerRecord) {
            if (listenerRecord.bound) {
                return true;
            }
            listenerRecord.bound = this.mContext.bindService(new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL").setComponent(listenerRecord.componentName), this, 33);
            if (listenerRecord.bound) {
                listenerRecord.retryCount = 0;
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unable to bind to listener ");
                stringBuilder.append(listenerRecord.componentName);
                Log.w("NotifManCompat", stringBuilder.toString());
                this.mContext.unbindService(this);
            }
            return listenerRecord.bound;
        }

        private void ensureServiceUnbound(ListenerRecord listenerRecord) {
            if (listenerRecord.bound) {
                this.mContext.unbindService(this);
                listenerRecord.bound = false;
            }
            listenerRecord.service = null;
        }

        private void scheduleListenerRetry(ListenerRecord listenerRecord) {
            int i = 3;
            if (!this.mHandler.hasMessages(i, listenerRecord.componentName)) {
                int i2 = 1;
                listenerRecord.retryCount += i2;
                if (listenerRecord.retryCount > 6) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Giving up on delivering ");
                    stringBuilder.append(listenerRecord.taskQueue.size());
                    stringBuilder.append(" tasks to ");
                    stringBuilder.append(listenerRecord.componentName);
                    stringBuilder.append(" after ");
                    stringBuilder.append(listenerRecord.retryCount);
                    stringBuilder.append(" retries");
                    Log.w("NotifManCompat", stringBuilder.toString());
                    listenerRecord.taskQueue.clear();
                    return;
                }
                int i3 = 1000 * (i2 << (listenerRecord.retryCount - i2));
                if (Log.isLoggable("NotifManCompat", i)) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Scheduling retry for ");
                    stringBuilder2.append(i3);
                    stringBuilder2.append(" ms");
                    Log.d("NotifManCompat", stringBuilder2.toString());
                }
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i, listenerRecord.componentName), (long) i3);
            }
        }

        private void processListenerQueue(ListenerRecord listenerRecord) {
            StringBuilder stringBuilder;
            int i = 3;
            if (Log.isLoggable("NotifManCompat", i)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Processing component ");
                stringBuilder.append(listenerRecord.componentName);
                stringBuilder.append(", ");
                stringBuilder.append(listenerRecord.taskQueue.size());
                stringBuilder.append(" queued tasks");
                Log.d("NotifManCompat", stringBuilder.toString());
            }
            if (!listenerRecord.taskQueue.isEmpty()) {
                if (!ensureServiceBound(listenerRecord) || listenerRecord.service == null) {
                    scheduleListenerRetry(listenerRecord);
                    return;
                }
                while (true) {
                    Task task = (Task) listenerRecord.taskQueue.peek();
                    if (task == null) {
                        break;
                    }
                    try {
                        if (Log.isLoggable("NotifManCompat", i)) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Sending task ");
                            stringBuilder2.append(task);
                            Log.d("NotifManCompat", stringBuilder2.toString());
                        }
                        task.send(listenerRecord.service);
                        listenerRecord.taskQueue.remove();
                    } catch (DeadObjectException unused) {
                        if (Log.isLoggable("NotifManCompat", i)) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("Remote service has died: ");
                            stringBuilder3.append(listenerRecord.componentName);
                            Log.d("NotifManCompat", stringBuilder3.toString());
                        }
                    } catch (Throwable e) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("RemoteException communicating with ");
                        stringBuilder.append(listenerRecord.componentName);
                        Log.w("NotifManCompat", stringBuilder.toString(), e);
                    }
                }
                if (!listenerRecord.taskQueue.isEmpty()) {
                    scheduleListenerRetry(listenerRecord);
                }
            }
        }
    }

    private interface Task {
        void send(INotificationSideChannel iNotificationSideChannel) throws RemoteException;
    }

    private static class CancelTask implements Task {
        final boolean all;
        final int id;
        final String packageName;
        final String tag;

        CancelTask(String str) {
            this.packageName = str;
            this.id = 0;
            this.tag = null;
            this.all = true;
        }

        CancelTask(String str, int i, String str2) {
            this.packageName = str;
            this.id = i;
            this.tag = str2;
            this.all = false;
        }

        public void send(INotificationSideChannel iNotificationSideChannel) throws RemoteException {
            if (this.all) {
                iNotificationSideChannel.cancelAll(this.packageName);
            } else {
                iNotificationSideChannel.cancel(this.packageName, this.id, this.tag);
            }
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("CancelTask[");
            stringBuilder.append("packageName:");
            stringBuilder.append(this.packageName);
            stringBuilder.append(", id:");
            stringBuilder.append(this.id);
            stringBuilder.append(", tag:");
            stringBuilder.append(this.tag);
            stringBuilder.append(", all:");
            stringBuilder.append(this.all);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    private static class NotifyTask implements Task {
        final int id;
        final Notification notif;
        final String packageName;
        final String tag;

        NotifyTask(String str, int i, String str2, Notification notification) {
            this.packageName = str;
            this.id = i;
            this.tag = str2;
            this.notif = notification;
        }

        public void send(INotificationSideChannel iNotificationSideChannel) throws RemoteException {
            iNotificationSideChannel.notify(this.packageName, this.id, this.tag, this.notif);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder("NotifyTask[");
            stringBuilder.append("packageName:");
            stringBuilder.append(this.packageName);
            stringBuilder.append(", id:");
            stringBuilder.append(this.id);
            stringBuilder.append(", tag:");
            stringBuilder.append(this.tag);
            stringBuilder.append("]");
            return stringBuilder.toString();
        }
    }

    public static NotificationManagerCompat from(Context context) {
        return new NotificationManagerCompat(context);
    }

    private NotificationManagerCompat(Context context) {
        this.mContext = context;
    }

    public void cancel(int i) {
        cancel(null, i);
    }

    public void cancel(String str, int i) {
        this.mNotificationManager.cancel(str, i);
        if (VERSION.SDK_INT <= 19) {
            pushSideChannelQueue(new CancelTask(this.mContext.getPackageName(), i, str));
        }
    }

    public void cancelAll() {
        this.mNotificationManager.cancelAll();
        if (VERSION.SDK_INT <= 19) {
            pushSideChannelQueue(new CancelTask(this.mContext.getPackageName()));
        }
    }

    public void notify(int i, Notification notification) {
        notify(null, i, notification);
    }

    public void notify(String str, int i, Notification notification) {
        if (useSideChannelForNotification(notification)) {
            pushSideChannelQueue(new NotifyTask(this.mContext.getPackageName(), i, str, notification));
            this.mNotificationManager.cancel(str, i);
            return;
        }
        this.mNotificationManager.notify(str, i, notification);
    }

    public boolean areNotificationsEnabled() {
        if (VERSION.SDK_INT >= 24) {
            return this.mNotificationManager.areNotificationsEnabled();
        }
        boolean z = true;
        if (VERSION.SDK_INT < 19) {
            return z;
        }
        AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        ApplicationInfo applicationInfo = this.mContext.getApplicationInfo();
        String packageName = this.mContext.getApplicationContext().getPackageName();
        int i = applicationInfo.uid;
        try {
            Class cls = Class.forName(AppOpsManager.class.getName());
            int i2 = 3;
            Class[] clsArr = new Class[i2];
            boolean z2 = false;
            clsArr[z2] = Integer.TYPE;
            clsArr[z] = Integer.TYPE;
            int i3 = 2;
            clsArr[i3] = String.class;
            Method method = cls.getMethod("checkOpNoThrow", clsArr);
            Object[] objArr = new Object[i2];
            objArr[z2] = Integer.valueOf(((Integer) cls.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class)).intValue());
            objArr[z] = Integer.valueOf(i);
            objArr[i3] = packageName;
            if (((Integer) method.invoke(appOpsManager, objArr)).intValue() != 0) {
                z = z2;
            }
            return z;
        } catch (ClassNotFoundException unused) {
            return z;
        }
    }

    public int getImportance() {
        return VERSION.SDK_INT >= 24 ? this.mNotificationManager.getImportance() : -1000;
    }

    public static Set<String> getEnabledListenerPackages(Context context) {
        String string = Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
        synchronized (sEnabledNotificationListenersLock) {
            if (string != null) {
                if (!string.equals(sEnabledNotificationListeners)) {
                    String[] split = string.split(":");
                    Set hashSet = new HashSet(split.length);
                    for (String unflattenFromString : split) {
                        ComponentName unflattenFromString2 = ComponentName.unflattenFromString(unflattenFromString);
                        if (unflattenFromString2 != null) {
                            hashSet.add(unflattenFromString2.getPackageName());
                        }
                    }
                    sEnabledNotificationListenerPackages = hashSet;
                    sEnabledNotificationListeners = string;
                }
            }
        }
        return sEnabledNotificationListenerPackages;
    }

    private static boolean useSideChannelForNotification(Notification notification) {
        Bundle extras = NotificationCompat.getExtras(notification);
        return extras != null && extras.getBoolean("android.support.useSideChannel");
    }

    private void pushSideChannelQueue(Task task) {
        synchronized (sLock) {
            if (sSideChannelManager == null) {
                sSideChannelManager = new SideChannelManager(this.mContext.getApplicationContext());
            }
            sSideChannelManager.queueTask(task);
        }
    }
}
