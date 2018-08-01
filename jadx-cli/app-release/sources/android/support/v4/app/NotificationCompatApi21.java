package android.support.v4.app;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatBase.Action;
import android.support.v4.app.NotificationCompatBase.UnreadConversation;
import android.support.v4.app.NotificationCompatBase.UnreadConversation.Factory;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;
import android.widget.RemoteViews;
import java.util.ArrayList;
import java.util.Iterator;

@RequiresApi(21)
class NotificationCompatApi21 {
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_MESSAGES = "messages";
    private static final String KEY_ON_READ = "on_read";
    private static final String KEY_ON_REPLY = "on_reply";
    private static final String KEY_PARTICIPANTS = "participants";
    private static final String KEY_REMOTE_INPUT = "remote_input";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TIMESTAMP = "timestamp";

    public static class Builder implements NotificationBuilderWithBuilderAccessor, NotificationBuilderWithActions {
        private android.app.Notification.Builder b;
        private RemoteViews mBigContentView;
        private RemoteViews mContentView;
        private Bundle mExtras;
        private int mGroupAlertBehavior;
        private RemoteViews mHeadsUpContentView;

        public Builder(Context context, Notification notification, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, RemoteViews remoteViews, int i, PendingIntent pendingIntent, PendingIntent pendingIntent2, Bitmap bitmap, int i2, int i3, boolean z, boolean z2, boolean z3, int i4, CharSequence charSequence4, boolean z4, String str, ArrayList<String> arrayList, Bundle bundle, int i5, int i6, Notification notification2, String str2, boolean z5, String str3, RemoteViews remoteViews2, RemoteViews remoteViews3, RemoteViews remoteViews4, int i7) {
            PendingIntent pendingIntent3;
            Builder builder = this;
            Notification notification3 = notification;
            Bundle bundle2 = bundle;
            boolean z6 = false;
            boolean z7 = true;
            android.app.Notification.Builder deleteIntent = new android.app.Notification.Builder(context).setWhen(notification3.when).setShowWhen(z2).setSmallIcon(notification3.icon, notification3.iconLevel).setContent(notification3.contentView).setTicker(notification3.tickerText, remoteViews).setSound(notification3.sound, notification3.audioStreamType).setVibrate(notification3.vibrate).setLights(notification3.ledARGB, notification3.ledOnMS, notification3.ledOffMS).setOngoing((notification3.flags & 2) != 0 ? z7 : z6).setOnlyAlertOnce((notification3.flags & 8) != 0 ? z7 : z6).setAutoCancel((notification3.flags & 16) != 0 ? z7 : z6).setDefaults(notification3.defaults).setContentTitle(charSequence).setContentText(charSequence2).setSubText(charSequence4).setContentInfo(charSequence3).setContentIntent(pendingIntent).setDeleteIntent(notification3.deleteIntent);
            if ((notification3.flags & 128) != 0) {
                pendingIntent3 = pendingIntent2;
                z6 = z7;
            } else {
                pendingIntent3 = pendingIntent2;
            }
            builder.b = deleteIntent.setFullScreenIntent(pendingIntent3, z6).setLargeIcon(bitmap).setNumber(i).setUsesChronometer(z3).setPriority(i4).setProgress(i2, i3, z).setLocalOnly(z4).setGroup(str2).setGroupSummary(z5).setSortKey(str3).setCategory(str).setColor(i5).setVisibility(i6).setPublicVersion(notification2);
            builder.mExtras = new Bundle();
            if (bundle2 != null) {
                builder.mExtras.putAll(bundle2);
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                builder.b.addPerson((String) it.next());
            }
            builder.mContentView = remoteViews2;
            builder.mBigContentView = remoteViews3;
            builder.mHeadsUpContentView = remoteViews4;
            builder.mGroupAlertBehavior = i7;
        }

        public void addAction(Action action) {
            NotificationCompatApi20.addAction(this.b, action);
        }

        public android.app.Notification.Builder getBuilder() {
            return this.b;
        }

        public Notification build() {
            this.b.setExtras(this.mExtras);
            Notification build = this.b.build();
            if (this.mContentView != null) {
                build.contentView = this.mContentView;
            }
            if (this.mBigContentView != null) {
                build.bigContentView = this.mBigContentView;
            }
            if (this.mHeadsUpContentView != null) {
                build.headsUpContentView = this.mHeadsUpContentView;
            }
            if (this.mGroupAlertBehavior != 0) {
                if (!(build.getGroup() == null || (build.flags & 512) == 0 || this.mGroupAlertBehavior != 2)) {
                    removeSoundAndVibration(build);
                }
                if (build.getGroup() != null && (build.flags & 512) == 0 && this.mGroupAlertBehavior == 1) {
                    removeSoundAndVibration(build);
                }
            }
            return build;
        }

        private void removeSoundAndVibration(Notification notification) {
            Uri uri = null;
            notification.sound = uri;
            notification.vibrate = uri;
            notification.defaults &= -2;
            notification.defaults &= -3;
        }
    }

    NotificationCompatApi21() {
    }

    static Bundle getBundleForUnreadConversation(UnreadConversation unreadConversation) {
        String str = null;
        if (unreadConversation == null) {
            return str;
        }
        Bundle bundle = new Bundle();
        int i = 0;
        if (unreadConversation.getParticipants() != null && unreadConversation.getParticipants().length > 1) {
            str = unreadConversation.getParticipants()[i];
        }
        Parcelable[] parcelableArr = new Parcelable[unreadConversation.getMessages().length];
        while (i < parcelableArr.length) {
            Bundle bundle2 = new Bundle();
            bundle2.putString("text", unreadConversation.getMessages()[i]);
            bundle2.putString("author", str);
            parcelableArr[i] = bundle2;
            i++;
        }
        bundle.putParcelableArray("messages", parcelableArr);
        RemoteInput remoteInput = unreadConversation.getRemoteInput();
        if (remoteInput != null) {
            bundle.putParcelable("remote_input", fromCompatRemoteInput(remoteInput));
        }
        bundle.putParcelable("on_reply", unreadConversation.getReplyPendingIntent());
        bundle.putParcelable("on_read", unreadConversation.getReadPendingIntent());
        bundle.putStringArray("participants", unreadConversation.getParticipants());
        bundle.putLong("timestamp", unreadConversation.getLatestTimestamp());
        return bundle;
    }

    static UnreadConversation getUnreadConversationFromBundle(Bundle bundle, Factory factory, RemoteInput.Factory factory2) {
        UnreadConversation unreadConversation = null;
        if (bundle == null) {
            return unreadConversation;
        }
        String[] strArr;
        Parcelable[] parcelableArray = bundle.getParcelableArray("messages");
        int i = 1;
        if (parcelableArray != null) {
            int i2 = 0;
            String[] strArr2 = new String[parcelableArray.length];
            for (int i3 = i2; i3 < strArr2.length; i3++) {
                if (!(parcelableArray[i3] instanceof Bundle)) {
                    break;
                }
                strArr2[i3] = ((Bundle) parcelableArray[i3]).getString("text");
                if (strArr2[i3] == null) {
                    break;
                }
            }
            i2 = i;
            if (i2 == 0) {
                return unreadConversation;
            }
            strArr = strArr2;
        } else {
            strArr = unreadConversation;
        }
        PendingIntent pendingIntent = (PendingIntent) bundle.getParcelable("on_read");
        PendingIntent pendingIntent2 = (PendingIntent) bundle.getParcelable("on_reply");
        android.app.RemoteInput remoteInput = (android.app.RemoteInput) bundle.getParcelable("remote_input");
        String[] stringArray = bundle.getStringArray("participants");
        if (stringArray == null || stringArray.length != i) {
            return unreadConversation;
        }
        if (remoteInput != null) {
            unreadConversation = toCompatRemoteInput(remoteInput, factory2);
        }
        return factory.build(strArr, unreadConversation, pendingIntent2, pendingIntent, stringArray, bundle.getLong("timestamp"));
    }

    private static android.app.RemoteInput fromCompatRemoteInput(RemoteInput remoteInput) {
        return new android.app.RemoteInput.Builder(remoteInput.getResultKey()).setLabel(remoteInput.getLabel()).setChoices(remoteInput.getChoices()).setAllowFreeFormInput(remoteInput.getAllowFreeFormInput()).addExtras(remoteInput.getExtras()).build();
    }

    private static RemoteInput toCompatRemoteInput(android.app.RemoteInput remoteInput, RemoteInput.Factory factory) {
        return factory.build(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), null);
    }
}
