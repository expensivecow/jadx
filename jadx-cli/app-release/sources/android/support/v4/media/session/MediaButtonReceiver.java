package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.BroadcastReceiver.PendingResult;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserCompat.ConnectionCallback;
import android.util.Log;
import android.view.KeyEvent;
import java.util.List;

public class MediaButtonReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaButtonReceiver";

    private static class MediaButtonConnectionCallback extends ConnectionCallback {
        private final Context mContext;
        private final Intent mIntent;
        private MediaBrowserCompat mMediaBrowser;
        private final PendingResult mPendingResult;

        MediaButtonConnectionCallback(Context context, Intent intent, PendingResult pendingResult) {
            this.mContext = context;
            this.mIntent = intent;
            this.mPendingResult = pendingResult;
        }

        void setMediaBrowser(MediaBrowserCompat mediaBrowserCompat) {
            this.mMediaBrowser = mediaBrowserCompat;
        }

        public void onConnected() {
            try {
                new MediaControllerCompat(this.mContext, this.mMediaBrowser.getSessionToken()).dispatchMediaButtonEvent((KeyEvent) this.mIntent.getParcelableExtra("android.intent.extra.KEY_EVENT"));
            } catch (Throwable e) {
                Log.e("MediaButtonReceiver", "Failed to create a media controller", e);
            }
            finish();
        }

        public void onConnectionSuspended() {
            finish();
        }

        public void onConnectionFailed() {
            finish();
        }

        private void finish() {
            this.mMediaBrowser.disconnect();
            this.mPendingResult.finish();
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null && "android.intent.action.MEDIA_BUTTON".equals(intent.getAction()) && intent.hasExtra("android.intent.extra.KEY_EVENT")) {
            ComponentName serviceComponentByAction = getServiceComponentByAction(context, "android.intent.action.MEDIA_BUTTON");
            if (serviceComponentByAction != null) {
                intent.setComponent(serviceComponentByAction);
                startForegroundService(context, intent);
                return;
            }
            serviceComponentByAction = getServiceComponentByAction(context, "android.media.browse.MediaBrowserService");
            if (serviceComponentByAction != null) {
                PendingResult goAsync = goAsync();
                context = context.getApplicationContext();
                ConnectionCallback mediaButtonConnectionCallback = new MediaButtonConnectionCallback(context, intent, goAsync);
                MediaBrowserCompat mediaBrowserCompat = new MediaBrowserCompat(context, serviceComponentByAction, mediaButtonConnectionCallback, null);
                mediaButtonConnectionCallback.setMediaBrowser(mediaBrowserCompat);
                mediaBrowserCompat.connect();
                return;
            }
            throw new IllegalStateException("Could not find any Service that handles android.intent.action.MEDIA_BUTTON or implements a media browser service.");
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ignore unsupported intent: ");
        stringBuilder.append(intent);
        Log.d("MediaButtonReceiver", stringBuilder.toString());
    }

    public static KeyEvent handleIntent(MediaSessionCompat mediaSessionCompat, Intent intent) {
        if (mediaSessionCompat == null || intent == null || !"android.intent.action.MEDIA_BUTTON".equals(intent.getAction()) || !intent.hasExtra("android.intent.extra.KEY_EVENT")) {
            return null;
        }
        KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
        mediaSessionCompat.getController().dispatchMediaButtonEvent(keyEvent);
        return keyEvent;
    }

    public static PendingIntent buildMediaButtonPendingIntent(Context context, long j) {
        ComponentName mediaButtonReceiverComponent = getMediaButtonReceiverComponent(context);
        if (mediaButtonReceiverComponent != null) {
            return buildMediaButtonPendingIntent(context, mediaButtonReceiverComponent, j);
        }
        Log.w("MediaButtonReceiver", "A unique media button receiver could not be found in the given context, so couldn't build a pending intent.");
        return null;
    }

    public static PendingIntent buildMediaButtonPendingIntent(Context context, ComponentName componentName, long j) {
        PendingIntent pendingIntent = null;
        if (componentName == null) {
            Log.w("MediaButtonReceiver", "The component name of media button receiver should be provided.");
            return pendingIntent;
        }
        int toKeyCode = PlaybackStateCompat.toKeyCode(j);
        if (toKeyCode == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Cannot build a media button pending intent with the given action: ");
            stringBuilder.append(j);
            Log.w("MediaButtonReceiver", stringBuilder.toString());
            return pendingIntent;
        }
        Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.setComponent(componentName);
        int i = 0;
        intent.putExtra("android.intent.extra.KEY_EVENT", new KeyEvent(i, toKeyCode));
        return PendingIntent.getBroadcast(context, toKeyCode, intent, i);
    }

    static ComponentName getMediaButtonReceiverComponent(Context context) {
        Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
        intent.setPackage(context.getPackageName());
        int i = 0;
        List queryBroadcastReceivers = context.getPackageManager().queryBroadcastReceivers(intent, i);
        int i2 = 1;
        if (queryBroadcastReceivers.size() == i2) {
            ResolveInfo resolveInfo = (ResolveInfo) queryBroadcastReceivers.get(i);
            return new ComponentName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
        }
        if (queryBroadcastReceivers.size() > i2) {
            Log.w("MediaButtonReceiver", "More than one BroadcastReceiver that handles android.intent.action.MEDIA_BUTTON was found, returning null.");
        }
        return null;
    }

    private static void startForegroundService(Context context, Intent intent) {
        if (VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    private static ComponentName getServiceComponentByAction(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(str);
        intent.setPackage(context.getPackageName());
        int i = 0;
        List queryIntentServices = packageManager.queryIntentServices(intent, i);
        if (queryIntentServices.size() == 1) {
            ResolveInfo resolveInfo = (ResolveInfo) queryIntentServices.get(i);
            return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
        } else if (queryIntentServices.isEmpty()) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected 1 service that handles ");
            stringBuilder.append(str);
            stringBuilder.append(", found ");
            stringBuilder.append(queryIntentServices.size());
            throw new IllegalStateException(stringBuilder.toString());
        }
    }
}
