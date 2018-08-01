package android.support.v4.app;

import android.app.Notification;
import android.app.Notification.BigPictureStyle;
import android.app.Notification.BigTextStyle;
import android.app.Notification.InboxStyle;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase.Action;
import android.support.v4.app.NotificationCompatBase.Action.Factory;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;
import android.util.Log;
import android.util.SparseArray;
import android.widget.RemoteViews;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiresApi(16)
class NotificationCompatJellybean {
    static final String EXTRA_ALLOW_GENERATED_REPLIES = "android.support.allowGeneratedReplies";
    static final String EXTRA_DATA_ONLY_REMOTE_INPUTS = "android.support.dataRemoteInputs";
    private static final String KEY_ACTION_INTENT = "actionIntent";
    private static final String KEY_DATA_ONLY_REMOTE_INPUTS = "dataOnlyRemoteInputs";
    private static final String KEY_EXTRAS = "extras";
    private static final String KEY_ICON = "icon";
    private static final String KEY_REMOTE_INPUTS = "remoteInputs";
    private static final String KEY_TITLE = "title";
    public static final String TAG = "NotificationCompat";
    private static Class<?> sActionClass;
    private static Field sActionIconField;
    private static Field sActionIntentField;
    private static Field sActionTitleField;
    private static boolean sActionsAccessFailed;
    private static Field sActionsField;
    private static final Object sActionsLock = new Object();
    private static Field sExtrasField;
    private static boolean sExtrasFieldAccessFailed;
    private static final Object sExtrasLock = new Object();

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private android.app.Notification.Builder b;
        private List<Bundle> mActionExtrasList = new ArrayList();
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private final Bundle mExtras;

        public Builder(Context context, Notification notification, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, RemoteViews remoteViews, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, Bitmap bitmap, int i2, int i3, boolean z, boolean z2, int i4, CharSequence charSequence4, boolean z3, Bundle bundle, String str, boolean z4, String str2, RemoteViews remoteViews2, RemoteViews remoteViews3) {
            PendingIntent pendingIntent3;
            Notification notification2 = notification;
            Bundle bundle2 = bundle;
            String str3 = str;
            String str4 = str2;
            boolean z5 = false;
            boolean z6 = true;
            android.app.Notification.Builder deleteIntent = new android.app.Notification.Builder(context).setWhen(notification2.when).setSmallIcon(notification2.icon, notification2.iconLevel).setContent(notification2.contentView).setTicker(notification2.tickerText, remoteViews).setSound(notification2.sound, notification2.audioStreamType).setVibrate(notification2.vibrate).setLights(notification2.ledARGB, notification2.ledOnMS, notification2.ledOffMS).setOngoing((notification2.flags & 2) != 0 ? z6 : z5).setOnlyAlertOnce((notification2.flags & 8) != 0 ? z6 : z5).setAutoCancel((notification2.flags & 16) != 0 ? z6 : z5).setDefaults(notification2.defaults).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence4).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification2.deleteIntent);
            if ((notification2.flags & 128) != 0) {
                pendingIntent3 = pendingIntent2;
                z5 = z6;
            } else {
                pendingIntent3 = pendingIntent2;
            }
            r0.b = deleteIntent.setFullScreenIntent(pendingIntent3, z5).setLargeIcon(bitmap).setNumber(i).setUsesChronometer(z2).setPriority(i4).setProgress(i2, i3, z);
            r0.mExtras = new Bundle();
            if (bundle2 != null) {
                r0.mExtras.putAll(bundle2);
            }
            if (z3) {
                r0.mExtras.putBoolean("android.support.localOnly", z6);
            }
            if (str3 != null) {
                r0.mExtras.putString("android.support.groupKey", str3);
                if (z4) {
                    r0.mExtras.putBoolean("android.support.isGroupSummary", z6);
                } else {
                    r0.mExtras.putBoolean("android.support.useSideChannel", z6);
                }
            }
            if (str4 != null) {
                r0.mExtras.putString("android.support.sortKey", str4);
            }
            r0.mContentView = remoteViews2;
            r0.mBigContentView = remoteViews3;
        }

        public void addAction(Action action) {
            this.mActionExtrasList.add(NotificationCompatJellybean.writeActionAndGetExtras(this.b, action));
        }

        public android.app.Notification.Builder getBuilder() {
            return this.b;
        }

        public Notification build() {
            Notification build = this.b.build();
            Bundle extras = NotificationCompatJellybean.getExtras(build);
            Bundle bundle = new Bundle(this.mExtras);
            for (String str : this.mExtras.keySet()) {
                if (extras.containsKey(str)) {
                    bundle.remove(str);
                }
            }
            extras.putAll(bundle);
            SparseArray buildActionExtrasMap = NotificationCompatJellybean.buildActionExtrasMap(this.mActionExtrasList);
            if (buildActionExtrasMap != null) {
                NotificationCompatJellybean.getExtras(build).putSparseParcelableArray("android.support.actionExtras", buildActionExtrasMap);
            }
            if (this.mContentView != null) {
                build.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                build.bigContentView = this.mBigContentView;
            }
            return build;
        }
    }

    NotificationCompatJellybean() {
    }

    public static void addBigTextStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, CharSequence charSequence3) {
        BigTextStyle bigText = new BigTextStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence).bigText(charSequence3);
        if (z) {
            bigText.setSummaryText(charSequence2);
        }
    }

    public static void addBigPictureStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, Bitmap bitmap, Bitmap bitmap2, boolean z2) {
        BigPictureStyle bigPicture = new BigPictureStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence).bigPicture(bitmap);
        if (z2) {
            bigPicture.bigLargeIcon(bitmap2);
        }
        if (z) {
            bigPicture.setSummaryText(charSequence2);
        }
    }

    public static void addInboxStyle(NotificationBuilderWithBuilderAccessor notificationBuilderWithBuilderAccessor, CharSequence charSequence, boolean z, CharSequence charSequence2, ArrayList<CharSequence> arrayList) {
        InboxStyle bigContentTitle = new InboxStyle(notificationBuilderWithBuilderAccessor.getBuilder()).setBigContentTitle(charSequence);
        if (z) {
            bigContentTitle.setSummaryText(charSequence2);
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            bigContentTitle.addLine((CharSequence) it.next());
        }
    }

    public static SparseArray<Bundle> buildActionExtrasMap(List<Bundle> list) {
        int size = list.size();
        SparseArray<Bundle> sparseArray = null;
        for (int i = 0; i < size; i++) {
            Bundle bundle = (Bundle) list.get(i);
            if (bundle != null) {
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                }
                sparseArray.put(i, bundle);
            }
        }
        return sparseArray;
    }

    public static Bundle getExtras(Notification notification) {
        synchronized (sExtrasLock) {
            Bundle bundle = null;
            if (sExtrasFieldAccessFailed) {
                return bundle;
            }
            boolean z = true;
            try {
                if (sExtrasField == null) {
                    Field declaredField = Notification.class.getDeclaredField("extras");
                    if (Bundle.class.isAssignableFrom(declaredField.getType())) {
                        declaredField.setAccessible(z);
                        sExtrasField = declaredField;
                    } else {
                        Log.e("NotificationCompat", "Notification.extras field is not of type Bundle");
                        sExtrasFieldAccessFailed = z;
                        return bundle;
                    }
                }
                Bundle bundle2 = (Bundle) sExtrasField.get(notification);
                if (bundle2 == null) {
                    bundle2 = new Bundle();
                    sExtrasField.set(notification, bundle2);
                }
                return bundle2;
            } catch (Throwable e) {
                Log.e("NotificationCompat", "Unable to access notification extras", e);
                sExtrasFieldAccessFailed = z;
                return bundle;
            } catch (Throwable e2) {
                Log.e("NotificationCompat", "Unable to access notification extras", e2);
                sExtrasFieldAccessFailed = z;
                return bundle;
            }
        }
    }

    public static Action readAction(Factory factory, RemoteInput.Factory factory2, int i, CharSequence charSequence, PendingIntent pendingIntent, Bundle bundle) {
        boolean z;
        RemoteInput[] remoteInputArr;
        RemoteInput[] remoteInputArr2;
        RemoteInput[] remoteInputArr3 = null;
        if (bundle != null) {
            remoteInputArr3 = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, "android.support.remoteInputs"), factory2);
            RemoteInput[] fromBundleArray = RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, "android.support.dataRemoteInputs"), factory2);
            z = bundle.getBoolean("android.support.allowGeneratedReplies");
            remoteInputArr = fromBundleArray;
            remoteInputArr2 = remoteInputArr3;
        } else {
            z = false;
            remoteInputArr2 = remoteInputArr3;
            remoteInputArr = remoteInputArr2;
        }
        return factory.build(i, charSequence, pendingIntent, bundle, remoteInputArr2, remoteInputArr, z);
    }

    public static Bundle writeActionAndGetExtras(android.app.Notification.Builder builder, Action action) {
        builder.addAction(action.getIcon(), action.getTitle(), action.getActionIntent());
        Bundle bundle = new Bundle(action.getExtras());
        if (action.getRemoteInputs() != null) {
            bundle.putParcelableArray("android.support.remoteInputs", RemoteInputCompatJellybean.toBundleArray(action.getRemoteInputs()));
        }
        if (action.getDataOnlyRemoteInputs() != null) {
            bundle.putParcelableArray("android.support.dataRemoteInputs", RemoteInputCompatJellybean.toBundleArray(action.getDataOnlyRemoteInputs()));
        }
        bundle.putBoolean("android.support.allowGeneratedReplies", action.getAllowGeneratedReplies());
        return bundle;
    }

    public static int getActionCount(Notification notification) {
        int length;
        synchronized (sActionsLock) {
            Object[] actionObjectsLocked = getActionObjectsLocked(notification);
            length = actionObjectsLocked != null ? actionObjectsLocked.length : 0;
        }
        return length;
    }

    public static Action getAction(Notification notification, int i, Factory factory, RemoteInput.Factory factory2) {
        Action action;
        synchronized (sActionsLock) {
            action = null;
            try {
                Object[] actionObjectsLocked = getActionObjectsLocked(notification);
                if (actionObjectsLocked != null) {
                    Bundle bundle;
                    Action readAction;
                    Object obj = actionObjectsLocked[i];
                    Bundle extras = getExtras(notification);
                    if (extras != null) {
                        SparseArray sparseParcelableArray = extras.getSparseParcelableArray("android.support.actionExtras");
                        if (sparseParcelableArray != null) {
                            bundle = (Bundle) sparseParcelableArray.get(i);
                            readAction = readAction(factory, factory2, sActionIconField.getInt(obj), (CharSequence) sActionTitleField.get(obj), (PendingIntent) sActionIntentField.get(obj), bundle);
                            return readAction;
                        }
                    }
                    bundle = action;
                    readAction = readAction(factory, factory2, sActionIconField.getInt(obj), (CharSequence) sActionTitleField.get(obj), (PendingIntent) sActionIntentField.get(obj), bundle);
                    return readAction;
                }
            } catch (Throwable e) {
                Log.e("NotificationCompat", "Unable to access notification actions", e);
                sActionsAccessFailed = true;
            }
        }
        return action;
    }

    private static Object[] getActionObjectsLocked(Notification notification) {
        synchronized (sActionsLock) {
            Object[] objArr = null;
            if (ensureActionReflectionReadyLocked()) {
                try {
                    Object[] objArr2 = (Object[]) sActionsField.get(notification);
                    return objArr2;
                } catch (Throwable e) {
                    Log.e("NotificationCompat", "Unable to access notification actions", e);
                    sActionsAccessFailed = true;
                    return objArr;
                }
            }
            return objArr;
        }
    }

    private static boolean ensureActionReflectionReadyLocked() {
        if (sActionsAccessFailed) {
            return false;
        }
        boolean z = true;
        try {
            if (sActionsField == null) {
                sActionClass = Class.forName("android.app.Notification$Action");
                sActionIconField = sActionClass.getDeclaredField("icon");
                sActionTitleField = sActionClass.getDeclaredField("title");
                sActionIntentField = sActionClass.getDeclaredField("actionIntent");
                sActionsField = Notification.class.getDeclaredField("actions");
                sActionsField.setAccessible(z);
            }
        } catch (Throwable e) {
            Log.e("NotificationCompat", "Unable to access notification actions", e);
            sActionsAccessFailed = z;
        } catch (Throwable e2) {
            Log.e("NotificationCompat", "Unable to access notification actions", e2);
            sActionsAccessFailed = z;
        }
        return z ^ sActionsAccessFailed;
    }

    public static Action[] getActionsFromParcelableArrayList(ArrayList<Parcelable> arrayList, Factory factory, RemoteInput.Factory factory2) {
        if (arrayList == null) {
            return null;
        }
        Action[] newArray = factory.newArray(arrayList.size());
        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = getActionFromBundle((Bundle) arrayList.get(i), factory, factory2);
        }
        return newArray;
    }

    private static Action getActionFromBundle(Bundle bundle, Factory factory, RemoteInput.Factory factory2) {
        Bundle bundle2 = bundle.getBundle("extras");
        boolean z = false;
        if (bundle2 != null) {
            z = bundle2.getBoolean("android.support.allowGeneratedReplies", z);
        }
        return factory.build(bundle.getInt("icon"), bundle.getCharSequence("title"), (PendingIntent) bundle.getParcelable("actionIntent"), bundle.getBundle("extras"), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, "remoteInputs"), factory2), RemoteInputCompatJellybean.fromBundleArray(BundleUtil.getBundleArrayFromBundle(bundle, "dataOnlyRemoteInputs"), factory2), z);
    }

    public static ArrayList<Parcelable> getParcelableArrayListForActions(Action[] actionArr) {
        if (actionArr == null) {
            return null;
        }
        ArrayList<Parcelable> arrayList = new ArrayList(actionArr.length);
        for (Action bundleForAction : actionArr) {
            arrayList.add(getBundleForAction(bundleForAction));
        }
        return arrayList;
    }

    private static Bundle getBundleForAction(Action action) {
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putInt("icon", action.getIcon());
        bundle2.putCharSequence("title", action.getTitle());
        bundle2.putParcelable("actionIntent", action.getActionIntent());
        if (action.getExtras() != null) {
            bundle = new Bundle(action.getExtras());
        } else {
            bundle = new Bundle();
        }
        bundle.putBoolean("android.support.allowGeneratedReplies", action.getAllowGeneratedReplies());
        bundle2.putBundle("extras", bundle);
        bundle2.putParcelableArray("remoteInputs", RemoteInputCompatJellybean.toBundleArray(action.getRemoteInputs()));
        return bundle2;
    }
}
