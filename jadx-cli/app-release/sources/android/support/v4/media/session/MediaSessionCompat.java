package android.support.v4.media.session;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataEditor;
import android.media.Rating;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.RemoteControlClient.OnMetadataUpdateListener;
import android.media.RemoteControlClient.OnPlaybackPositionUpdateListener;
import android.net.Uri;
import android.os.BadParcelableException;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.app.BundleCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.MediaMetadataCompat.Builder;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.IMediaSession.Stub;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaSessionCompat {
    static final String ACTION_ARGUMENT_CAPTIONING_ENABLED = "android.support.v4.media.session.action.ARGUMENT_CAPTIONING_ENABLED";
    static final String ACTION_ARGUMENT_EXTRAS = "android.support.v4.media.session.action.ARGUMENT_EXTRAS";
    static final String ACTION_ARGUMENT_MEDIA_ID = "android.support.v4.media.session.action.ARGUMENT_MEDIA_ID";
    static final String ACTION_ARGUMENT_QUERY = "android.support.v4.media.session.action.ARGUMENT_QUERY";
    static final String ACTION_ARGUMENT_RATING = "android.support.v4.media.session.action.ARGUMENT_RATING";
    static final String ACTION_ARGUMENT_REPEAT_MODE = "android.support.v4.media.session.action.ARGUMENT_REPEAT_MODE";
    static final String ACTION_ARGUMENT_SHUFFLE_MODE = "android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE";
    static final String ACTION_ARGUMENT_SHUFFLE_MODE_ENABLED = "android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE_ENABLED";
    static final String ACTION_ARGUMENT_URI = "android.support.v4.media.session.action.ARGUMENT_URI";
    public static final String ACTION_FLAG_AS_INAPPROPRIATE = "android.support.v4.media.session.action.FLAG_AS_INAPPROPRIATE";
    public static final String ACTION_FOLLOW = "android.support.v4.media.session.action.FOLLOW";
    static final String ACTION_PLAY_FROM_URI = "android.support.v4.media.session.action.PLAY_FROM_URI";
    static final String ACTION_PREPARE = "android.support.v4.media.session.action.PREPARE";
    static final String ACTION_PREPARE_FROM_MEDIA_ID = "android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID";
    static final String ACTION_PREPARE_FROM_SEARCH = "android.support.v4.media.session.action.PREPARE_FROM_SEARCH";
    static final String ACTION_PREPARE_FROM_URI = "android.support.v4.media.session.action.PREPARE_FROM_URI";
    static final String ACTION_SET_CAPTIONING_ENABLED = "android.support.v4.media.session.action.SET_CAPTIONING_ENABLED";
    static final String ACTION_SET_RATING = "android.support.v4.media.session.action.SET_RATING";
    static final String ACTION_SET_REPEAT_MODE = "android.support.v4.media.session.action.SET_REPEAT_MODE";
    static final String ACTION_SET_SHUFFLE_MODE = "android.support.v4.media.session.action.SET_SHUFFLE_MODE";
    static final String ACTION_SET_SHUFFLE_MODE_ENABLED = "android.support.v4.media.session.action.SET_SHUFFLE_MODE_ENABLED";
    public static final String ACTION_SKIP_AD = "android.support.v4.media.session.action.SKIP_AD";
    public static final String ACTION_UNFOLLOW = "android.support.v4.media.session.action.UNFOLLOW";
    public static final String ARGUMENT_MEDIA_ATTRIBUTE = "android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE";
    public static final String ARGUMENT_MEDIA_ATTRIBUTE_VALUE = "android.support.v4.media.session.ARGUMENT_MEDIA_ATTRIBUTE_VALUE";
    static final String EXTRA_BINDER = "android.support.v4.media.session.EXTRA_BINDER";
    public static final int FLAG_HANDLES_MEDIA_BUTTONS = 1;
    public static final int FLAG_HANDLES_QUEUE_COMMANDS = 4;
    public static final int FLAG_HANDLES_TRANSPORT_CONTROLS = 2;
    private static final int MAX_BITMAP_SIZE_IN_DP = 320;
    public static final int MEDIA_ATTRIBUTE_ALBUM = 1;
    public static final int MEDIA_ATTRIBUTE_ARTIST = 0;
    public static final int MEDIA_ATTRIBUTE_PLAYLIST = 2;
    static final String TAG = "MediaSessionCompat";
    static int sMaxBitmapSize;
    private final ArrayList<OnActiveChangeListener> mActiveListeners;
    private final MediaControllerCompat mController;
    private final MediaSessionImpl mImpl;

    public static abstract class Callback {
        private CallbackHandler mCallbackHandler;
        final Object mCallbackObj;
        private boolean mMediaPlayPauseKeyPending;
        private WeakReference<MediaSessionImpl> mSessionImpl;

        private class CallbackHandler extends Handler {
            private static final int MSG_MEDIA_PLAY_PAUSE_KEY_DOUBLE_TAP_TIMEOUT = 1;

            CallbackHandler(Looper looper) {
                super(looper);
            }

            public void handleMessage(Message message) {
                if (message.what == 1) {
                    Callback.this.handleMediaPlayPauseKeySingleTapIfPending();
                }
            }
        }

        @RequiresApi(21)
        private class StubApi21 implements Callback {
            StubApi21() {
            }

            public void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) {
                try {
                    QueueItem queueItem = null;
                    MediaSessionImplApi21 mediaSessionImplApi21;
                    if (str.equals("android.support.v4.media.session.command.GET_EXTRA_BINDER")) {
                        mediaSessionImplApi21 = (MediaSessionImplApi21) Callback.this.mSessionImpl.get();
                        if (mediaSessionImplApi21 != null) {
                            IBinder asBinder;
                            bundle = new Bundle();
                            IMediaSession extraBinder = mediaSessionImplApi21.getSessionToken().getExtraBinder();
                            String str2 = "android.support.v4.media.session.EXTRA_BINDER";
                            if (extraBinder != null) {
                                asBinder = extraBinder.asBinder();
                            }
                            BundleCompat.putBinder(bundle, str2, asBinder);
                            resultReceiver.send(0, bundle);
                        }
                    } else if (str.equals("android.support.v4.media.session.command.ADD_QUEUE_ITEM")) {
                        bundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
                        Callback.this.onAddQueueItem((MediaDescriptionCompat) bundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"));
                    } else if (str.equals("android.support.v4.media.session.command.ADD_QUEUE_ITEM_AT")) {
                        bundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
                        Callback.this.onAddQueueItem((MediaDescriptionCompat) bundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"), bundle.getInt("android.support.v4.media.session.command.ARGUMENT_INDEX"));
                    } else if (str.equals("android.support.v4.media.session.command.REMOVE_QUEUE_ITEM")) {
                        bundle.setClassLoader(MediaDescriptionCompat.class.getClassLoader());
                        Callback.this.onRemoveQueueItem((MediaDescriptionCompat) bundle.getParcelable("android.support.v4.media.session.command.ARGUMENT_MEDIA_DESCRIPTION"));
                    } else if (str.equals("android.support.v4.media.session.command.REMOVE_QUEUE_ITEM_AT")) {
                        mediaSessionImplApi21 = (MediaSessionImplApi21) Callback.this.mSessionImpl.get();
                        if (mediaSessionImplApi21 != null && mediaSessionImplApi21.mQueue != null) {
                            int i = bundle.getInt("android.support.v4.media.session.command.ARGUMENT_INDEX", -1);
                            if (i >= 0 && i < mediaSessionImplApi21.mQueue.size()) {
                                queueItem = (QueueItem) mediaSessionImplApi21.mQueue.get(i);
                            }
                            if (queueItem != null) {
                                Callback.this.onRemoveQueueItem(queueItem.getDescription());
                            }
                        }
                    } else {
                        Callback.this.onCommand(str, bundle, resultReceiver);
                    }
                } catch (BadParcelableException unused) {
                    Log.e("MediaSessionCompat", "Could not unparcel the extra data.");
                }
            }

            public boolean onMediaButtonEvent(Intent intent) {
                return Callback.this.onMediaButtonEvent(intent);
            }

            public void onPlay() {
                Callback.this.onPlay();
            }

            public void onPlayFromMediaId(String str, Bundle bundle) {
                Callback.this.onPlayFromMediaId(str, bundle);
            }

            public void onPlayFromSearch(String str, Bundle bundle) {
                Callback.this.onPlayFromSearch(str, bundle);
            }

            public void onSkipToQueueItem(long j) {
                Callback.this.onSkipToQueueItem(j);
            }

            public void onPause() {
                Callback.this.onPause();
            }

            public void onSkipToNext() {
                Callback.this.onSkipToNext();
            }

            public void onSkipToPrevious() {
                Callback.this.onSkipToPrevious();
            }

            public void onFastForward() {
                Callback.this.onFastForward();
            }

            public void onRewind() {
                Callback.this.onRewind();
            }

            public void onStop() {
                Callback.this.onStop();
            }

            public void onSeekTo(long j) {
                Callback.this.onSeekTo(j);
            }

            public void onSetRating(Object obj) {
                Callback.this.onSetRating(RatingCompat.fromRating(obj));
            }

            public void onSetRating(Object obj, Bundle bundle) {
                Callback.this.onSetRating(RatingCompat.fromRating(obj), bundle);
            }

            public void onCustomAction(String str, Bundle bundle) {
                if (str.equals("android.support.v4.media.session.action.PLAY_FROM_URI")) {
                    Callback.this.onPlayFromUri((Uri) bundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI"), (Bundle) bundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_EXTRAS"));
                } else if (str.equals("android.support.v4.media.session.action.PREPARE")) {
                    Callback.this.onPrepare();
                } else if (str.equals("android.support.v4.media.session.action.PREPARE_FROM_MEDIA_ID")) {
                    Callback.this.onPrepareFromMediaId(bundle.getString("android.support.v4.media.session.action.ARGUMENT_MEDIA_ID"), bundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS"));
                } else if (str.equals("android.support.v4.media.session.action.PREPARE_FROM_SEARCH")) {
                    Callback.this.onPrepareFromSearch(bundle.getString("android.support.v4.media.session.action.ARGUMENT_QUERY"), bundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS"));
                } else if (str.equals("android.support.v4.media.session.action.PREPARE_FROM_URI")) {
                    Callback.this.onPrepareFromUri((Uri) bundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_URI"), bundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS"));
                } else if (str.equals("android.support.v4.media.session.action.SET_CAPTIONING_ENABLED")) {
                    Callback.this.onSetCaptioningEnabled(bundle.getBoolean("android.support.v4.media.session.action.ARGUMENT_CAPTIONING_ENABLED"));
                } else if (str.equals("android.support.v4.media.session.action.SET_REPEAT_MODE")) {
                    Callback.this.onSetRepeatMode(bundle.getInt("android.support.v4.media.session.action.ARGUMENT_REPEAT_MODE"));
                } else if (str.equals("android.support.v4.media.session.action.SET_SHUFFLE_MODE_ENABLED")) {
                    Callback.this.onSetShuffleModeEnabled(bundle.getBoolean("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE_ENABLED"));
                } else if (str.equals("android.support.v4.media.session.action.SET_SHUFFLE_MODE")) {
                    Callback.this.onSetShuffleMode(bundle.getInt("android.support.v4.media.session.action.ARGUMENT_SHUFFLE_MODE"));
                } else if (str.equals("android.support.v4.media.session.action.SET_RATING")) {
                    bundle.setClassLoader(RatingCompat.class.getClassLoader());
                    Callback.this.onSetRating((RatingCompat) bundle.getParcelable("android.support.v4.media.session.action.ARGUMENT_RATING"), bundle.getBundle("android.support.v4.media.session.action.ARGUMENT_EXTRAS"));
                } else {
                    Callback.this.onCustomAction(str, bundle);
                }
            }
        }

        @RequiresApi(23)
        private class StubApi23 extends StubApi21 implements android.support.v4.media.session.MediaSessionCompatApi23.Callback {
            StubApi23() {
                super();
            }

            public void onPlayFromUri(Uri uri, Bundle bundle) {
                Callback.this.onPlayFromUri(uri, bundle);
            }
        }

        @RequiresApi(24)
        private class StubApi24 extends StubApi23 implements android.support.v4.media.session.MediaSessionCompatApi24.Callback {
            StubApi24() {
                super();
            }

            public void onPrepare() {
                Callback.this.onPrepare();
            }

            public void onPrepareFromMediaId(String str, Bundle bundle) {
                Callback.this.onPrepareFromMediaId(str, bundle);
            }

            public void onPrepareFromSearch(String str, Bundle bundle) {
                Callback.this.onPrepareFromSearch(str, bundle);
            }

            public void onPrepareFromUri(Uri uri, Bundle bundle) {
                Callback.this.onPrepareFromUri(uri, bundle);
            }
        }

        public void onAddQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
        }

        public void onAddQueueItem(MediaDescriptionCompat mediaDescriptionCompat, int i) {
        }

        public void onCommand(String str, Bundle bundle, ResultReceiver resultReceiver) {
        }

        public void onCustomAction(String str, Bundle bundle) {
        }

        public void onFastForward() {
        }

        public void onPause() {
        }

        public void onPlay() {
        }

        public void onPlayFromMediaId(String str, Bundle bundle) {
        }

        public void onPlayFromSearch(String str, Bundle bundle) {
        }

        public void onPlayFromUri(Uri uri, Bundle bundle) {
        }

        public void onPrepare() {
        }

        public void onPrepareFromMediaId(String str, Bundle bundle) {
        }

        public void onPrepareFromSearch(String str, Bundle bundle) {
        }

        public void onPrepareFromUri(Uri uri, Bundle bundle) {
        }

        public void onRemoveQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
        }

        @Deprecated
        public void onRemoveQueueItemAt(int i) {
        }

        public void onRewind() {
        }

        public void onSeekTo(long j) {
        }

        public void onSetCaptioningEnabled(boolean z) {
        }

        public void onSetRating(RatingCompat ratingCompat) {
        }

        public void onSetRating(RatingCompat ratingCompat, Bundle bundle) {
        }

        public void onSetRepeatMode(int i) {
        }

        public void onSetShuffleMode(int i) {
        }

        @Deprecated
        public void onSetShuffleModeEnabled(boolean z) {
        }

        public void onSkipToNext() {
        }

        public void onSkipToPrevious() {
        }

        public void onSkipToQueueItem(long j) {
        }

        public void onStop() {
        }

        public Callback() {
            CallbackHandler callbackHandler = null;
            this.mCallbackHandler = callbackHandler;
            if (VERSION.SDK_INT >= 24) {
                this.mCallbackObj = MediaSessionCompatApi24.createCallback(new StubApi24());
            } else if (VERSION.SDK_INT >= 23) {
                this.mCallbackObj = MediaSessionCompatApi23.createCallback(new StubApi23());
            } else if (VERSION.SDK_INT >= 21) {
                this.mCallbackObj = MediaSessionCompatApi21.createCallback(new StubApi21());
            } else {
                this.mCallbackObj = callbackHandler;
            }
        }

        private void setSessionImpl(MediaSessionImpl mediaSessionImpl, Handler handler) {
            this.mSessionImpl = new WeakReference(mediaSessionImpl);
            if (this.mCallbackHandler != null) {
                this.mCallbackHandler.removeCallbacksAndMessages(null);
            }
            this.mCallbackHandler = new CallbackHandler(handler.getLooper());
        }

        public boolean onMediaButtonEvent(Intent intent) {
            MediaSessionImpl mediaSessionImpl = (MediaSessionImpl) this.mSessionImpl.get();
            boolean z = false;
            if (mediaSessionImpl == null || this.mCallbackHandler == null) {
                return z;
            }
            KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra("android.intent.extra.KEY_EVENT");
            if (keyEvent == null || keyEvent.getAction() != 0) {
                return z;
            }
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == 79 || keyCode == 85) {
                boolean z2 = true;
                if (keyEvent.getRepeatCount() > 0) {
                    handleMediaPlayPauseKeySingleTapIfPending();
                } else if (this.mMediaPlayPauseKeyPending) {
                    long j;
                    this.mCallbackHandler.removeMessages(z2);
                    this.mMediaPlayPauseKeyPending = z;
                    PlaybackStateCompat playbackState = mediaSessionImpl.getPlaybackState();
                    long j2 = 0;
                    if (playbackState == null) {
                        j = j2;
                    } else {
                        j = playbackState.getActions();
                    }
                    if ((j & 32) != j2) {
                        onSkipToNext();
                    }
                } else {
                    this.mMediaPlayPauseKeyPending = z2;
                    this.mCallbackHandler.sendEmptyMessageDelayed(z2, (long) ViewConfiguration.getDoubleTapTimeout());
                }
                return z2;
            }
            handleMediaPlayPauseKeySingleTapIfPending();
            return z;
        }

        private void handleMediaPlayPauseKeySingleTapIfPending() {
            if (this.mMediaPlayPauseKeyPending) {
                boolean z = false;
                this.mMediaPlayPauseKeyPending = z;
                boolean z2 = true;
                this.mCallbackHandler.removeMessages(z2);
                MediaSessionImpl mediaSessionImpl = (MediaSessionImpl) this.mSessionImpl.get();
                if (mediaSessionImpl != null) {
                    long j;
                    PlaybackStateCompat playbackState = mediaSessionImpl.getPlaybackState();
                    long j2 = 0;
                    if (playbackState == null) {
                        j = j2;
                    } else {
                        j = playbackState.getActions();
                    }
                    int i = (playbackState == null || playbackState.getState() != 3) ? z : z2;
                    int i2 = (j & 516) != j2 ? z2 : z;
                    if ((j & 514) != j2) {
                        z = z2;
                    }
                    if (i != 0 && z) {
                        onPause();
                    } else if (i == 0 && i2 != 0) {
                        onPlay();
                    }
                }
            }
        }
    }

    interface MediaSessionImpl {
        String getCallingPackage();

        Object getMediaSession();

        PlaybackStateCompat getPlaybackState();

        Object getRemoteControlClient();

        Token getSessionToken();

        boolean isActive();

        void release();

        void sendSessionEvent(String str, Bundle bundle);

        void setActive(boolean z);

        void setCallback(Callback callback, Handler handler);

        void setCaptioningEnabled(boolean z);

        void setExtras(Bundle bundle);

        void setFlags(int i);

        void setMediaButtonReceiver(PendingIntent pendingIntent);

        void setMetadata(MediaMetadataCompat mediaMetadataCompat);

        void setPlaybackState(PlaybackStateCompat playbackStateCompat);

        void setPlaybackToLocal(int i);

        void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat);

        void setQueue(List<QueueItem> list);

        void setQueueTitle(CharSequence charSequence);

        void setRatingType(int i);

        void setRepeatMode(int i);

        void setSessionActivity(PendingIntent pendingIntent);

        void setShuffleMode(int i);

        void setShuffleModeEnabled(boolean z);
    }

    public interface OnActiveChangeListener {
        void onActiveChanged();
    }

    public static final class QueueItem implements Parcelable {
        public static final Creator<QueueItem> CREATOR = new Creator<QueueItem>() {
            public QueueItem createFromParcel(Parcel parcel) {
                return new QueueItem(parcel);
            }

            public QueueItem[] newArray(int i) {
                return new QueueItem[i];
            }
        };
        public static final int UNKNOWN_ID = -1;
        private final MediaDescriptionCompat mDescription;
        private final long mId;
        private Object mItem;

        public int describeContents() {
            return 0;
        }

        public QueueItem(MediaDescriptionCompat mediaDescriptionCompat, long j) {
            this(null, mediaDescriptionCompat, j);
        }

        private QueueItem(Object obj, MediaDescriptionCompat mediaDescriptionCompat, long j) {
            if (mediaDescriptionCompat == null) {
                throw new IllegalArgumentException("Description cannot be null.");
            } else if (j == -1) {
                throw new IllegalArgumentException("Id cannot be QueueItem.UNKNOWN_ID");
            } else {
                this.mDescription = mediaDescriptionCompat;
                this.mId = j;
                this.mItem = obj;
            }
        }

        QueueItem(Parcel parcel) {
            this.mDescription = (MediaDescriptionCompat) MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
            this.mId = parcel.readLong();
        }

        public MediaDescriptionCompat getDescription() {
            return this.mDescription;
        }

        public long getQueueId() {
            return this.mId;
        }

        public void writeToParcel(Parcel parcel, int i) {
            this.mDescription.writeToParcel(parcel, i);
            parcel.writeLong(this.mId);
        }

        public Object getQueueItem() {
            if (this.mItem != null || VERSION.SDK_INT < 21) {
                return this.mItem;
            }
            this.mItem = QueueItem.createItem(this.mDescription.getMediaDescription(), this.mId);
            return this.mItem;
        }

        public static QueueItem fromQueueItem(Object obj) {
            return (obj == null || VERSION.SDK_INT < 21) ? null : new QueueItem(obj, MediaDescriptionCompat.fromMediaDescription(QueueItem.getDescription(obj)), QueueItem.getQueueId(obj));
        }

        public static List<QueueItem> fromQueueItemList(List<?> list) {
            if (list == null || VERSION.SDK_INT < 21) {
                return null;
            }
            List<QueueItem> arrayList = new ArrayList();
            for (Object fromQueueItem : list) {
                arrayList.add(fromQueueItem(fromQueueItem));
            }
            return arrayList;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("MediaSession.QueueItem {Description=");
            stringBuilder.append(this.mDescription);
            stringBuilder.append(", Id=");
            stringBuilder.append(this.mId);
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }
    }

    static final class ResultReceiverWrapper implements Parcelable {
        public static final Creator<ResultReceiverWrapper> CREATOR = new Creator<ResultReceiverWrapper>() {
            public ResultReceiverWrapper createFromParcel(Parcel parcel) {
                return new ResultReceiverWrapper(parcel);
            }

            public ResultReceiverWrapper[] newArray(int i) {
                return new ResultReceiverWrapper[i];
            }
        };
        private ResultReceiver mResultReceiver;

        public int describeContents() {
            return 0;
        }

        public ResultReceiverWrapper(ResultReceiver resultReceiver) {
            this.mResultReceiver = resultReceiver;
        }

        ResultReceiverWrapper(Parcel parcel) {
            this.mResultReceiver = (ResultReceiver) ResultReceiver.CREATOR.createFromParcel(parcel);
        }

        public void writeToParcel(Parcel parcel, int i) {
            this.mResultReceiver.writeToParcel(parcel, i);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SessionFlags {
    }

    public static final class Token implements Parcelable {
        public static final Creator<Token> CREATOR = new Creator<Token>() {
            public Token createFromParcel(Parcel parcel) {
                Object readParcelable;
                if (VERSION.SDK_INT >= 21) {
                    readParcelable = parcel.readParcelable(null);
                } else {
                    readParcelable = parcel.readStrongBinder();
                }
                return new Token(readParcelable);
            }

            public Token[] newArray(int i) {
                return new Token[i];
            }
        };
        private final IMediaSession mExtraBinder;
        private final Object mInner;

        public int describeContents() {
            return 0;
        }

        Token(Object obj) {
            this(obj, null);
        }

        Token(Object obj, IMediaSession iMediaSession) {
            this.mInner = obj;
            this.mExtraBinder = iMediaSession;
        }

        public static Token fromToken(Object obj) {
            return fromToken(obj, null);
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public static Token fromToken(Object obj, IMediaSession iMediaSession) {
            return (obj == null || VERSION.SDK_INT < 21) ? null : new Token(MediaSessionCompatApi21.verifyToken(obj), iMediaSession);
        }

        public void writeToParcel(Parcel parcel, int i) {
            if (VERSION.SDK_INT >= 21) {
                parcel.writeParcelable((Parcelable) this.mInner, i);
            } else {
                parcel.writeStrongBinder((IBinder) this.mInner);
            }
        }

        public int hashCode() {
            if (this.mInner == null) {
                return 0;
            }
            return this.mInner.hashCode();
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return z;
            }
            boolean z2 = false;
            if (!(obj instanceof Token)) {
                return z2;
            }
            Token token = (Token) obj;
            if (this.mInner == null) {
                if (token.mInner != null) {
                    z = z2;
                }
                return z;
            } else if (token.mInner == null) {
                return z2;
            } else {
                return this.mInner.equals(token.mInner);
            }
        }

        public Object getToken() {
            return this.mInner;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public IMediaSession getExtraBinder() {
            return this.mExtraBinder;
        }
    }

    @RequiresApi(21)
    static class MediaSessionImplApi21 implements MediaSessionImpl {
        boolean mCaptioningEnabled;
        private boolean mDestroyed = false;
        private final RemoteCallbackList<IMediaControllerCallback> mExtraControllerCallbacks = new RemoteCallbackList();
        private MediaMetadataCompat mMetadata;
        private PlaybackStateCompat mPlaybackState;
        private List<QueueItem> mQueue;
        int mRatingType;
        int mRepeatMode;
        private final Object mSessionObj;
        int mShuffleMode;
        boolean mShuffleModeEnabled;
        private final Token mToken;

        class ExtraSession extends Stub {
            public List<QueueItem> getQueue() {
                return null;
            }

            ExtraSession() {
            }

            public void sendCommand(String str, Bundle bundle, ResultReceiverWrapper resultReceiverWrapper) {
                throw new AssertionError();
            }

            public boolean sendMediaButton(KeyEvent keyEvent) {
                throw new AssertionError();
            }

            public void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) {
                if (!MediaSessionImplApi21.this.mDestroyed) {
                    MediaSessionImplApi21.this.mExtraControllerCallbacks.register(iMediaControllerCallback);
                }
            }

            public void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) {
                MediaSessionImplApi21.this.mExtraControllerCallbacks.unregister(iMediaControllerCallback);
            }

            public String getPackageName() {
                throw new AssertionError();
            }

            public String getTag() {
                throw new AssertionError();
            }

            public PendingIntent getLaunchPendingIntent() {
                throw new AssertionError();
            }

            public long getFlags() {
                throw new AssertionError();
            }

            public ParcelableVolumeInfo getVolumeAttributes() {
                throw new AssertionError();
            }

            public void adjustVolume(int i, int i2, String str) {
                throw new AssertionError();
            }

            public void setVolumeTo(int i, int i2, String str) {
                throw new AssertionError();
            }

            public void prepare() throws RemoteException {
                throw new AssertionError();
            }

            public void prepareFromMediaId(String str, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void prepareFromSearch(String str, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void play() throws RemoteException {
                throw new AssertionError();
            }

            public void playFromMediaId(String str, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void playFromSearch(String str, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void playFromUri(Uri uri, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void skipToQueueItem(long j) {
                throw new AssertionError();
            }

            public void pause() throws RemoteException {
                throw new AssertionError();
            }

            public void stop() throws RemoteException {
                throw new AssertionError();
            }

            public void next() throws RemoteException {
                throw new AssertionError();
            }

            public void previous() throws RemoteException {
                throw new AssertionError();
            }

            public void fastForward() throws RemoteException {
                throw new AssertionError();
            }

            public void rewind() throws RemoteException {
                throw new AssertionError();
            }

            public void seekTo(long j) throws RemoteException {
                throw new AssertionError();
            }

            public void rate(RatingCompat ratingCompat) throws RemoteException {
                throw new AssertionError();
            }

            public void rateWithExtras(RatingCompat ratingCompat, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public void setCaptioningEnabled(boolean z) throws RemoteException {
                throw new AssertionError();
            }

            public void setRepeatMode(int i) throws RemoteException {
                throw new AssertionError();
            }

            public void setShuffleModeEnabledDeprecated(boolean z) throws RemoteException {
                throw new AssertionError();
            }

            public void setShuffleMode(int i) throws RemoteException {
                throw new AssertionError();
            }

            public void sendCustomAction(String str, Bundle bundle) throws RemoteException {
                throw new AssertionError();
            }

            public MediaMetadataCompat getMetadata() {
                throw new AssertionError();
            }

            public PlaybackStateCompat getPlaybackState() {
                return MediaSessionCompat.getStateWithUpdatedPosition(MediaSessionImplApi21.this.mPlaybackState, MediaSessionImplApi21.this.mMetadata);
            }

            public void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
                throw new AssertionError();
            }

            public void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int i) {
                throw new AssertionError();
            }

            public void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
                throw new AssertionError();
            }

            public void removeQueueItemAt(int i) {
                throw new AssertionError();
            }

            public CharSequence getQueueTitle() {
                throw new AssertionError();
            }

            public Bundle getExtras() {
                throw new AssertionError();
            }

            public int getRatingType() {
                return MediaSessionImplApi21.this.mRatingType;
            }

            public boolean isCaptioningEnabled() {
                return MediaSessionImplApi21.this.mCaptioningEnabled;
            }

            public int getRepeatMode() {
                return MediaSessionImplApi21.this.mRepeatMode;
            }

            public boolean isShuffleModeEnabledDeprecated() {
                return MediaSessionImplApi21.this.mShuffleModeEnabled;
            }

            public int getShuffleMode() {
                return MediaSessionImplApi21.this.mShuffleMode;
            }

            public boolean isTransportControlEnabled() {
                throw new AssertionError();
            }
        }

        public Object getRemoteControlClient() {
            return null;
        }

        public MediaSessionImplApi21(Context context, String str) {
            this.mSessionObj = MediaSessionCompatApi21.createSession(context, str);
            this.mToken = new Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj), new ExtraSession());
        }

        public MediaSessionImplApi21(Object obj) {
            this.mSessionObj = MediaSessionCompatApi21.verifySession(obj);
            this.mToken = new Token(MediaSessionCompatApi21.getSessionToken(this.mSessionObj), new ExtraSession());
        }

        public void setCallback(Callback callback, Handler handler) {
            MediaSessionCompatApi21.setCallback(this.mSessionObj, callback == null ? null : callback.mCallbackObj, handler);
            if (callback != null) {
                callback.setSessionImpl(this, handler);
            }
        }

        public void setFlags(int i) {
            MediaSessionCompatApi21.setFlags(this.mSessionObj, i);
        }

        public void setPlaybackToLocal(int i) {
            MediaSessionCompatApi21.setPlaybackToLocal(this.mSessionObj, i);
        }

        public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
            MediaSessionCompatApi21.setPlaybackToRemote(this.mSessionObj, volumeProviderCompat.getVolumeProvider());
        }

        public void setActive(boolean z) {
            MediaSessionCompatApi21.setActive(this.mSessionObj, z);
        }

        public boolean isActive() {
            return MediaSessionCompatApi21.isActive(this.mSessionObj);
        }

        public void sendSessionEvent(String str, Bundle bundle) {
            if (VERSION.SDK_INT < 23) {
                int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
                while (beginBroadcast >= 0) {
                    try {
                        ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onEvent(str, bundle);
                    } catch (RemoteException unused) {
                        beginBroadcast--;
                    }
                }
                this.mExtraControllerCallbacks.finishBroadcast();
            }
            MediaSessionCompatApi21.sendSessionEvent(this.mSessionObj, str, bundle);
        }

        public void release() {
            this.mDestroyed = true;
            MediaSessionCompatApi21.release(this.mSessionObj);
        }

        public Token getSessionToken() {
            return this.mToken;
        }

        public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
            Object obj;
            this.mPlaybackState = playbackStateCompat;
            int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onPlaybackStateChanged(playbackStateCompat);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mExtraControllerCallbacks.finishBroadcast();
            Object obj2 = this.mSessionObj;
            if (playbackStateCompat == null) {
                obj = null;
            } else {
                obj = playbackStateCompat.getPlaybackState();
            }
            MediaSessionCompatApi21.setPlaybackState(obj2, obj);
        }

        public PlaybackStateCompat getPlaybackState() {
            return this.mPlaybackState;
        }

        public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
            Object obj;
            this.mMetadata = mediaMetadataCompat;
            Object obj2 = this.mSessionObj;
            if (mediaMetadataCompat == null) {
                obj = null;
            } else {
                obj = mediaMetadataCompat.getMediaMetadata();
            }
            MediaSessionCompatApi21.setMetadata(obj2, obj);
        }

        public void setSessionActivity(PendingIntent pendingIntent) {
            MediaSessionCompatApi21.setSessionActivity(this.mSessionObj, pendingIntent);
        }

        public void setMediaButtonReceiver(PendingIntent pendingIntent) {
            MediaSessionCompatApi21.setMediaButtonReceiver(this.mSessionObj, pendingIntent);
        }

        public void setQueue(List<QueueItem> list) {
            List arrayList;
            this.mQueue = list;
            if (list != null) {
                arrayList = new ArrayList();
                for (QueueItem queueItem : list) {
                    arrayList.add(queueItem.getQueueItem());
                }
            } else {
                arrayList = null;
            }
            MediaSessionCompatApi21.setQueue(this.mSessionObj, arrayList);
        }

        public void setQueueTitle(CharSequence charSequence) {
            MediaSessionCompatApi21.setQueueTitle(this.mSessionObj, charSequence);
        }

        public void setRatingType(int i) {
            if (VERSION.SDK_INT < 22) {
                this.mRatingType = i;
            } else {
                MediaSessionCompatApi22.setRatingType(this.mSessionObj, i);
            }
        }

        public void setCaptioningEnabled(boolean z) {
            if (this.mCaptioningEnabled != z) {
                this.mCaptioningEnabled = z;
                int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
                while (beginBroadcast >= 0) {
                    try {
                        ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onCaptioningEnabledChanged(z);
                    } catch (RemoteException unused) {
                        beginBroadcast--;
                    }
                }
                this.mExtraControllerCallbacks.finishBroadcast();
            }
        }

        public void setRepeatMode(int i) {
            if (this.mRepeatMode != i) {
                this.mRepeatMode = i;
                int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
                while (beginBroadcast >= 0) {
                    try {
                        ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onRepeatModeChanged(i);
                    } catch (RemoteException unused) {
                        beginBroadcast--;
                    }
                }
                this.mExtraControllerCallbacks.finishBroadcast();
            }
        }

        public void setShuffleModeEnabled(boolean z) {
            if (this.mShuffleModeEnabled != z) {
                this.mShuffleModeEnabled = z;
                int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
                while (beginBroadcast >= 0) {
                    try {
                        ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onShuffleModeChangedDeprecated(z);
                    } catch (RemoteException unused) {
                        beginBroadcast--;
                    }
                }
                this.mExtraControllerCallbacks.finishBroadcast();
            }
        }

        public void setShuffleMode(int i) {
            if (this.mShuffleMode != i) {
                this.mShuffleMode = i;
                int beginBroadcast = this.mExtraControllerCallbacks.beginBroadcast() - 1;
                while (beginBroadcast >= 0) {
                    try {
                        ((IMediaControllerCallback) this.mExtraControllerCallbacks.getBroadcastItem(beginBroadcast)).onShuffleModeChanged(i);
                    } catch (RemoteException unused) {
                        beginBroadcast--;
                    }
                }
                this.mExtraControllerCallbacks.finishBroadcast();
            }
        }

        public void setExtras(Bundle bundle) {
            MediaSessionCompatApi21.setExtras(this.mSessionObj, bundle);
        }

        public Object getMediaSession() {
            return this.mSessionObj;
        }

        public String getCallingPackage() {
            if (VERSION.SDK_INT < 24) {
                return null;
            }
            return MediaSessionCompatApi24.getCallingPackage(this.mSessionObj);
        }
    }

    static class MediaSessionImplBase implements MediaSessionImpl {
        static final int RCC_PLAYSTATE_NONE = 0;
        final AudioManager mAudioManager;
        volatile Callback mCallback;
        boolean mCaptioningEnabled;
        private final Context mContext;
        final RemoteCallbackList<IMediaControllerCallback> mControllerCallbacks = new RemoteCallbackList();
        boolean mDestroyed;
        Bundle mExtras;
        int mFlags;
        private MessageHandler mHandler;
        boolean mIsActive;
        private boolean mIsMbrRegistered;
        private boolean mIsRccRegistered;
        int mLocalStream;
        final Object mLock = new Object();
        private final ComponentName mMediaButtonReceiverComponentName;
        private final PendingIntent mMediaButtonReceiverIntent;
        MediaMetadataCompat mMetadata;
        final String mPackageName;
        List<QueueItem> mQueue;
        CharSequence mQueueTitle;
        int mRatingType;
        final RemoteControlClient mRcc;
        int mRepeatMode;
        PendingIntent mSessionActivity;
        int mShuffleMode;
        boolean mShuffleModeEnabled;
        PlaybackStateCompat mState;
        private final MediaSessionStub mStub;
        final String mTag;
        private final Token mToken;
        private android.support.v4.media.VolumeProviderCompat.Callback mVolumeCallback;
        VolumeProviderCompat mVolumeProvider;
        int mVolumeType;

        private static final class Command {
            public final String command;
            public final Bundle extras;
            public final ResultReceiver stub;

            public Command(String str, Bundle bundle, ResultReceiver resultReceiver) {
                this.command = str;
                this.extras = bundle;
                this.stub = resultReceiver;
            }
        }

        class MessageHandler extends Handler {
            private static final int KEYCODE_MEDIA_PAUSE = 127;
            private static final int KEYCODE_MEDIA_PLAY = 126;
            private static final int MSG_ADD_QUEUE_ITEM = 25;
            private static final int MSG_ADD_QUEUE_ITEM_AT = 26;
            private static final int MSG_ADJUST_VOLUME = 2;
            private static final int MSG_COMMAND = 1;
            private static final int MSG_CUSTOM_ACTION = 20;
            private static final int MSG_FAST_FORWARD = 16;
            private static final int MSG_MEDIA_BUTTON = 21;
            private static final int MSG_NEXT = 14;
            private static final int MSG_PAUSE = 12;
            private static final int MSG_PLAY = 7;
            private static final int MSG_PLAY_MEDIA_ID = 8;
            private static final int MSG_PLAY_SEARCH = 9;
            private static final int MSG_PLAY_URI = 10;
            private static final int MSG_PREPARE = 3;
            private static final int MSG_PREPARE_MEDIA_ID = 4;
            private static final int MSG_PREPARE_SEARCH = 5;
            private static final int MSG_PREPARE_URI = 6;
            private static final int MSG_PREVIOUS = 15;
            private static final int MSG_RATE = 19;
            private static final int MSG_RATE_EXTRA = 31;
            private static final int MSG_REMOVE_QUEUE_ITEM = 27;
            private static final int MSG_REMOVE_QUEUE_ITEM_AT = 28;
            private static final int MSG_REWIND = 17;
            private static final int MSG_SEEK_TO = 18;
            private static final int MSG_SET_CAPTIONING_ENABLED = 29;
            private static final int MSG_SET_REPEAT_MODE = 23;
            private static final int MSG_SET_SHUFFLE_MODE = 30;
            private static final int MSG_SET_SHUFFLE_MODE_ENABLED = 24;
            private static final int MSG_SET_VOLUME = 22;
            private static final int MSG_SKIP_TO_ITEM = 11;
            private static final int MSG_STOP = 13;

            public MessageHandler(Looper looper) {
                super(looper);
            }

            public void post(int i, Object obj, Bundle bundle) {
                Message obtainMessage = obtainMessage(i, obj);
                obtainMessage.setData(bundle);
                obtainMessage.sendToTarget();
            }

            public void post(int i, Object obj) {
                obtainMessage(i, obj).sendToTarget();
            }

            public void post(int i) {
                post(i, null);
            }

            public void post(int i, Object obj, int i2) {
                obtainMessage(i, i2, 0, obj).sendToTarget();
            }

            public void handleMessage(Message message) {
                Callback callback = MediaSessionImplBase.this.mCallback;
                if (callback != null) {
                    int i = 0;
                    switch (message.what) {
                        case 1:
                            Command command = (Command) message.obj;
                            callback.onCommand(command.command, command.extras, command.stub);
                            break;
                        case 2:
                            MediaSessionImplBase.this.adjustVolume(message.arg1, i);
                            break;
                        case 3:
                            callback.onPrepare();
                            break;
                        case 4:
                            callback.onPrepareFromMediaId((String) message.obj, message.getData());
                            break;
                        case 5:
                            callback.onPrepareFromSearch((String) message.obj, message.getData());
                            break;
                        case 6:
                            callback.onPrepareFromUri((Uri) message.obj, message.getData());
                            break;
                        case 7:
                            callback.onPlay();
                            break;
                        case 8:
                            callback.onPlayFromMediaId((String) message.obj, message.getData());
                            break;
                        case 9:
                            callback.onPlayFromSearch((String) message.obj, message.getData());
                            break;
                        case 10:
                            callback.onPlayFromUri((Uri) message.obj, message.getData());
                            break;
                        case 11:
                            callback.onSkipToQueueItem(((Long) message.obj).longValue());
                            break;
                        case 12:
                            callback.onPause();
                            break;
                        case 13:
                            callback.onStop();
                            break;
                        case 14:
                            callback.onSkipToNext();
                            break;
                        case 15:
                            callback.onSkipToPrevious();
                            break;
                        case 16:
                            callback.onFastForward();
                            break;
                        case 17:
                            callback.onRewind();
                            break;
                        case 18:
                            callback.onSeekTo(((Long) message.obj).longValue());
                            break;
                        case 19:
                            callback.onSetRating((RatingCompat) message.obj);
                            break;
                        case 20:
                            callback.onCustomAction((String) message.obj, message.getData());
                            break;
                        case 21:
                            KeyEvent keyEvent = (KeyEvent) message.obj;
                            Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                            intent.putExtra("android.intent.extra.KEY_EVENT", keyEvent);
                            if (!callback.onMediaButtonEvent(intent)) {
                                onMediaButtonEvent(keyEvent, callback);
                                break;
                            }
                            break;
                        case 22:
                            MediaSessionImplBase.this.setVolumeTo(message.arg1, i);
                            break;
                        case 23:
                            callback.onSetRepeatMode(message.arg1);
                            break;
                        case 24:
                            callback.onSetShuffleModeEnabled(((Boolean) message.obj).booleanValue());
                            break;
                        case 25:
                            callback.onAddQueueItem((MediaDescriptionCompat) message.obj);
                            break;
                        case 26:
                            callback.onAddQueueItem((MediaDescriptionCompat) message.obj, message.arg1);
                            break;
                        case 27:
                            callback.onRemoveQueueItem((MediaDescriptionCompat) message.obj);
                            break;
                        case 28:
                            if (MediaSessionImplBase.this.mQueue != null) {
                                QueueItem queueItem = (message.arg1 < 0 || message.arg1 >= MediaSessionImplBase.this.mQueue.size()) ? null : (QueueItem) MediaSessionImplBase.this.mQueue.get(message.arg1);
                                if (queueItem != null) {
                                    callback.onRemoveQueueItem(queueItem.getDescription());
                                    break;
                                }
                            }
                            break;
                        case 29:
                            callback.onSetCaptioningEnabled(((Boolean) message.obj).booleanValue());
                            break;
                        case 30:
                            callback.onSetShuffleMode(message.arg1);
                            break;
                        case 31:
                            callback.onSetRating((RatingCompat) message.obj, message.getData());
                            break;
                    }
                }
            }

            private void onMediaButtonEvent(KeyEvent keyEvent, Callback callback) {
                if (keyEvent != null && keyEvent.getAction() == 0) {
                    long j = 0;
                    long actions = MediaSessionImplBase.this.mState == null ? j : MediaSessionImplBase.this.mState.getActions();
                    int keyCode = keyEvent.getKeyCode();
                    if (keyCode != 79) {
                        switch (keyCode) {
                            case 85:
                                break;
                            case 86:
                                if ((actions & 1) != j) {
                                    callback.onStop();
                                    break;
                                }
                                break;
                            case 87:
                                if ((actions & 32) != j) {
                                    callback.onSkipToNext();
                                    break;
                                }
                                break;
                            case 88:
                                if ((actions & 16) != j) {
                                    callback.onSkipToPrevious();
                                    break;
                                }
                                break;
                            case 89:
                                if ((actions & 8) != j) {
                                    callback.onRewind();
                                    break;
                                }
                                break;
                            case 90:
                                if ((actions & 64) != j) {
                                    callback.onFastForward();
                                    break;
                                }
                                break;
                            default:
                                switch (keyCode) {
                                    case 126:
                                        if ((actions & 4) != j) {
                                            callback.onPlay();
                                            break;
                                        }
                                        break;
                                    case 127:
                                        if ((actions & 2) != j) {
                                            callback.onPause();
                                            break;
                                        }
                                        break;
                                }
                                break;
                        }
                    }
                    Log.w("MediaSessionCompat", "KEYCODE_MEDIA_PLAY_PAUSE and KEYCODE_HEADSETHOOK are handled already");
                }
            }
        }

        class MediaSessionStub extends Stub {
            MediaSessionStub() {
            }

            public void sendCommand(String str, Bundle bundle, ResultReceiverWrapper resultReceiverWrapper) {
                MediaSessionImplBase.this.postToHandler(1, new Command(str, bundle, resultReceiverWrapper.mResultReceiver));
            }

            public boolean sendMediaButton(KeyEvent keyEvent) {
                boolean z = true;
                if ((MediaSessionImplBase.this.mFlags & z) == 0) {
                    z = false;
                }
                if (z) {
                    MediaSessionImplBase.this.postToHandler(21, (Object) keyEvent);
                }
                return z;
            }

            public void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) {
                if (MediaSessionImplBase.this.mDestroyed) {
                    try {
                        iMediaControllerCallback.onSessionDestroyed();
                    } catch (Exception unused) {
                    }
                } else {
                    MediaSessionImplBase.this.mControllerCallbacks.register(iMediaControllerCallback);
                }
            }

            public void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) {
                MediaSessionImplBase.this.mControllerCallbacks.unregister(iMediaControllerCallback);
            }

            public String getPackageName() {
                return MediaSessionImplBase.this.mPackageName;
            }

            public String getTag() {
                return MediaSessionImplBase.this.mTag;
            }

            public PendingIntent getLaunchPendingIntent() {
                PendingIntent pendingIntent;
                synchronized (MediaSessionImplBase.this.mLock) {
                    pendingIntent = MediaSessionImplBase.this.mSessionActivity;
                }
                return pendingIntent;
            }

            public long getFlags() {
                long j;
                synchronized (MediaSessionImplBase.this.mLock) {
                    j = (long) MediaSessionImplBase.this.mFlags;
                }
                return j;
            }

            public ParcelableVolumeInfo getVolumeAttributes() {
                int i;
                int i2;
                int maxVolume;
                int currentVolume;
                int i3;
                synchronized (MediaSessionImplBase.this.mLock) {
                    i = MediaSessionImplBase.this.mVolumeType;
                    i2 = MediaSessionImplBase.this.mLocalStream;
                    VolumeProviderCompat volumeProviderCompat = MediaSessionImplBase.this.mVolumeProvider;
                    int i4 = 2;
                    if (i == i4) {
                        i4 = volumeProviderCompat.getVolumeControl();
                        maxVolume = volumeProviderCompat.getMaxVolume();
                        currentVolume = volumeProviderCompat.getCurrentVolume();
                        i3 = maxVolume;
                    } else {
                        i3 = MediaSessionImplBase.this.mAudioManager.getStreamMaxVolume(i2);
                        currentVolume = MediaSessionImplBase.this.mAudioManager.getStreamVolume(i2);
                    }
                    maxVolume = i4;
                }
                return new ParcelableVolumeInfo(i, i2, maxVolume, i3, currentVolume);
            }

            public void adjustVolume(int i, int i2, String str) {
                MediaSessionImplBase.this.adjustVolume(i, i2);
            }

            public void setVolumeTo(int i, int i2, String str) {
                MediaSessionImplBase.this.setVolumeTo(i, i2);
            }

            public void prepare() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(3);
            }

            public void prepareFromMediaId(String str, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(4, (Object) str, bundle);
            }

            public void prepareFromSearch(String str, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(5, (Object) str, bundle);
            }

            public void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(6, (Object) uri, bundle);
            }

            public void play() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(7);
            }

            public void playFromMediaId(String str, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(8, (Object) str, bundle);
            }

            public void playFromSearch(String str, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(9, (Object) str, bundle);
            }

            public void playFromUri(Uri uri, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(10, (Object) uri, bundle);
            }

            public void skipToQueueItem(long j) {
                MediaSessionImplBase.this.postToHandler(11, Long.valueOf(j));
            }

            public void pause() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(12);
            }

            public void stop() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(13);
            }

            public void next() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(14);
            }

            public void previous() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(15);
            }

            public void fastForward() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(16);
            }

            public void rewind() throws RemoteException {
                MediaSessionImplBase.this.postToHandler(17);
            }

            public void seekTo(long j) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(18, Long.valueOf(j));
            }

            public void rate(RatingCompat ratingCompat) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(19, (Object) ratingCompat);
            }

            public void rateWithExtras(RatingCompat ratingCompat, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(31, (Object) ratingCompat, bundle);
            }

            public void setCaptioningEnabled(boolean z) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(29, Boolean.valueOf(z));
            }

            public void setRepeatMode(int i) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(23, i);
            }

            public void setShuffleModeEnabledDeprecated(boolean z) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(24, Boolean.valueOf(z));
            }

            public void setShuffleMode(int i) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(30, i);
            }

            public void sendCustomAction(String str, Bundle bundle) throws RemoteException {
                MediaSessionImplBase.this.postToHandler(20, (Object) str, bundle);
            }

            public MediaMetadataCompat getMetadata() {
                return MediaSessionImplBase.this.mMetadata;
            }

            public PlaybackStateCompat getPlaybackState() {
                PlaybackStateCompat playbackStateCompat;
                MediaMetadataCompat mediaMetadataCompat;
                synchronized (MediaSessionImplBase.this.mLock) {
                    playbackStateCompat = MediaSessionImplBase.this.mState;
                    mediaMetadataCompat = MediaSessionImplBase.this.mMetadata;
                }
                return MediaSessionCompat.getStateWithUpdatedPosition(playbackStateCompat, mediaMetadataCompat);
            }

            public List<QueueItem> getQueue() {
                List<QueueItem> list;
                synchronized (MediaSessionImplBase.this.mLock) {
                    list = MediaSessionImplBase.this.mQueue;
                }
                return list;
            }

            public void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
                MediaSessionImplBase.this.postToHandler(25, (Object) mediaDescriptionCompat);
            }

            public void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int i) {
                MediaSessionImplBase.this.postToHandler(26, (Object) mediaDescriptionCompat, i);
            }

            public void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) {
                MediaSessionImplBase.this.postToHandler(27, (Object) mediaDescriptionCompat);
            }

            public void removeQueueItemAt(int i) {
                MediaSessionImplBase.this.postToHandler(28, i);
            }

            public CharSequence getQueueTitle() {
                return MediaSessionImplBase.this.mQueueTitle;
            }

            public Bundle getExtras() {
                Bundle bundle;
                synchronized (MediaSessionImplBase.this.mLock) {
                    bundle = MediaSessionImplBase.this.mExtras;
                }
                return bundle;
            }

            public int getRatingType() {
                return MediaSessionImplBase.this.mRatingType;
            }

            public boolean isCaptioningEnabled() {
                return MediaSessionImplBase.this.mCaptioningEnabled;
            }

            public int getRepeatMode() {
                return MediaSessionImplBase.this.mRepeatMode;
            }

            public boolean isShuffleModeEnabledDeprecated() {
                return MediaSessionImplBase.this.mShuffleModeEnabled;
            }

            public int getShuffleMode() {
                return MediaSessionImplBase.this.mShuffleMode;
            }

            public boolean isTransportControlEnabled() {
                return (MediaSessionImplBase.this.mFlags & 2) != 0;
            }
        }

        public String getCallingPackage() {
            return null;
        }

        public Object getMediaSession() {
            return null;
        }

        int getRccStateFromState(int i) {
            switch (i) {
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
                case 5:
                    return 5;
                case 6:
                case 8:
                    return 8;
                case 7:
                    return 9;
                case 9:
                    return 7;
                case 10:
                case 11:
                    return 6;
                default:
                    return -1;
            }
        }

        int getRccTransportControlFlagsFromActions(long j) {
            long j2 = 0;
            int i = (j & 1) != j2 ? 32 : 0;
            if ((j & 2) != j2) {
                i |= 16;
            }
            if ((j & 4) != j2) {
                i |= 4;
            }
            if ((j & 8) != j2) {
                i |= 2;
            }
            if ((j & 16) != j2) {
                i |= 1;
            }
            if ((j & 32) != j2) {
                i |= 128;
            }
            if ((j & 64) != j2) {
                i |= 64;
            }
            return (j & 512) != j2 ? i | 8 : i;
        }

        public Object getRemoteControlClient() {
            return null;
        }

        public void setMediaButtonReceiver(PendingIntent pendingIntent) {
        }

        public MediaSessionImplBase(Context context, String str, ComponentName componentName, PendingIntent pendingIntent) {
            boolean z = false;
            this.mDestroyed = z;
            this.mIsActive = z;
            this.mIsMbrRegistered = z;
            this.mIsRccRegistered = z;
            this.mVolumeCallback = new android.support.v4.media.VolumeProviderCompat.Callback() {
                public void onVolumeChanged(VolumeProviderCompat volumeProviderCompat) {
                    if (MediaSessionImplBase.this.mVolumeProvider == volumeProviderCompat) {
                        MediaSessionImplBase.this.sendVolumeInfoChanged(new ParcelableVolumeInfo(MediaSessionImplBase.this.mVolumeType, MediaSessionImplBase.this.mLocalStream, volumeProviderCompat.getVolumeControl(), volumeProviderCompat.getMaxVolume(), volumeProviderCompat.getCurrentVolume()));
                    }
                }
            };
            if (componentName == null) {
                throw new IllegalArgumentException("MediaButtonReceiver component may not be null.");
            }
            this.mContext = context;
            this.mPackageName = context.getPackageName();
            this.mAudioManager = (AudioManager) context.getSystemService("audio");
            this.mTag = str;
            this.mMediaButtonReceiverComponentName = componentName;
            this.mMediaButtonReceiverIntent = pendingIntent;
            this.mStub = new MediaSessionStub();
            this.mToken = new Token(this.mStub);
            this.mRatingType = z;
            this.mVolumeType = 1;
            this.mLocalStream = 3;
            this.mRcc = new RemoteControlClient(pendingIntent);
        }

        public void setCallback(Callback callback, Handler handler) {
            this.mCallback = callback;
            if (callback != null) {
                if (handler == null) {
                    handler = new Handler();
                }
                synchronized (this.mLock) {
                    if (this.mHandler != null) {
                        this.mHandler.removeCallbacksAndMessages(null);
                    }
                    this.mHandler = new MessageHandler(handler.getLooper());
                    this.mCallback.setSessionImpl(this, handler);
                }
            }
        }

        void postToHandler(int i) {
            postToHandler(i, null);
        }

        void postToHandler(int i, int i2) {
            postToHandler(i, null, i2);
        }

        void postToHandler(int i, Object obj) {
            postToHandler(i, obj, null);
        }

        void postToHandler(int i, Object obj, int i2) {
            synchronized (this.mLock) {
                if (this.mHandler != null) {
                    this.mHandler.post(i, obj, i2);
                }
            }
        }

        void postToHandler(int i, Object obj, Bundle bundle) {
            synchronized (this.mLock) {
                if (this.mHandler != null) {
                    this.mHandler.post(i, obj, bundle);
                }
            }
        }

        public void setFlags(int i) {
            synchronized (this.mLock) {
                this.mFlags = i;
            }
            update();
        }

        public void setPlaybackToLocal(int i) {
            if (this.mVolumeProvider != null) {
                this.mVolumeProvider.setCallback(null);
            }
            this.mVolumeType = 1;
            sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, 2, this.mAudioManager.getStreamMaxVolume(this.mLocalStream), this.mAudioManager.getStreamVolume(this.mLocalStream)));
        }

        public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
            if (volumeProviderCompat == null) {
                throw new IllegalArgumentException("volumeProvider may not be null");
            }
            if (this.mVolumeProvider != null) {
                this.mVolumeProvider.setCallback(null);
            }
            this.mVolumeType = 2;
            this.mVolumeProvider = volumeProviderCompat;
            sendVolumeInfoChanged(new ParcelableVolumeInfo(this.mVolumeType, this.mLocalStream, this.mVolumeProvider.getVolumeControl(), this.mVolumeProvider.getMaxVolume(), this.mVolumeProvider.getCurrentVolume()));
            volumeProviderCompat.setCallback(this.mVolumeCallback);
        }

        public void setActive(boolean z) {
            if (z != this.mIsActive) {
                this.mIsActive = z;
                if (update()) {
                    setMetadata(this.mMetadata);
                    setPlaybackState(this.mState);
                }
            }
        }

        public boolean isActive() {
            return this.mIsActive;
        }

        public void sendSessionEvent(String str, Bundle bundle) {
            sendEvent(str, bundle);
        }

        public void release() {
            this.mIsActive = false;
            this.mDestroyed = true;
            update();
            sendSessionDestroyed();
        }

        public Token getSessionToken() {
            return this.mToken;
        }

        public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
            synchronized (this.mLock) {
                this.mState = playbackStateCompat;
            }
            sendState(playbackStateCompat);
            if (this.mIsActive) {
                if (playbackStateCompat == null) {
                    int i = 0;
                    this.mRcc.setPlaybackState(i);
                    this.mRcc.setTransportControlFlags(i);
                } else {
                    setRccState(playbackStateCompat);
                    this.mRcc.setTransportControlFlags(getRccTransportControlFlagsFromActions(playbackStateCompat.getActions()));
                }
            }
        }

        public PlaybackStateCompat getPlaybackState() {
            PlaybackStateCompat playbackStateCompat;
            synchronized (this.mLock) {
                playbackStateCompat = this.mState;
            }
            return playbackStateCompat;
        }

        void setRccState(PlaybackStateCompat playbackStateCompat) {
            this.mRcc.setPlaybackState(getRccStateFromState(playbackStateCompat.getState()));
        }

        public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
            if (mediaMetadataCompat != null) {
                mediaMetadataCompat = new Builder(mediaMetadataCompat, MediaSessionCompat.sMaxBitmapSize).build();
            }
            synchronized (this.mLock) {
                this.mMetadata = mediaMetadataCompat;
            }
            sendMetadata(mediaMetadataCompat);
            if (this.mIsActive) {
                Bundle bundle;
                if (mediaMetadataCompat == null) {
                    bundle = null;
                } else {
                    bundle = mediaMetadataCompat.getBundle();
                }
                buildRccMetadata(bundle).apply();
            }
        }

        MetadataEditor buildRccMetadata(Bundle bundle) {
            boolean z = true;
            MetadataEditor editMetadata = this.mRcc.editMetadata(z);
            if (bundle == null) {
                return editMetadata;
            }
            int i = 100;
            boolean z2 = false;
            Bitmap bitmap;
            if (bundle.containsKey("android.media.metadata.ART")) {
                bitmap = (Bitmap) bundle.getParcelable("android.media.metadata.ART");
                if (bitmap != null) {
                    bitmap = bitmap.copy(bitmap.getConfig(), z2);
                }
                editMetadata.putBitmap(i, bitmap);
            } else if (bundle.containsKey("android.media.metadata.ALBUM_ART")) {
                bitmap = (Bitmap) bundle.getParcelable("android.media.metadata.ALBUM_ART");
                if (bitmap != null) {
                    bitmap = bitmap.copy(bitmap.getConfig(), z2);
                }
                editMetadata.putBitmap(i, bitmap);
            }
            if (bundle.containsKey("android.media.metadata.ALBUM")) {
                editMetadata.putString(z, bundle.getString("android.media.metadata.ALBUM"));
            }
            if (bundle.containsKey("android.media.metadata.ALBUM_ARTIST")) {
                editMetadata.putString(13, bundle.getString("android.media.metadata.ALBUM_ARTIST"));
            }
            if (bundle.containsKey("android.media.metadata.ARTIST")) {
                editMetadata.putString(2, bundle.getString("android.media.metadata.ARTIST"));
            }
            if (bundle.containsKey("android.media.metadata.AUTHOR")) {
                editMetadata.putString(3, bundle.getString("android.media.metadata.AUTHOR"));
            }
            if (bundle.containsKey("android.media.metadata.COMPILATION")) {
                editMetadata.putString(15, bundle.getString("android.media.metadata.COMPILATION"));
            }
            if (bundle.containsKey("android.media.metadata.COMPOSER")) {
                editMetadata.putString(4, bundle.getString("android.media.metadata.COMPOSER"));
            }
            if (bundle.containsKey("android.media.metadata.DATE")) {
                editMetadata.putString(5, bundle.getString("android.media.metadata.DATE"));
            }
            if (bundle.containsKey("android.media.metadata.DISC_NUMBER")) {
                editMetadata.putLong(14, bundle.getLong("android.media.metadata.DISC_NUMBER"));
            }
            if (bundle.containsKey("android.media.metadata.DURATION")) {
                editMetadata.putLong(9, bundle.getLong("android.media.metadata.DURATION"));
            }
            if (bundle.containsKey("android.media.metadata.GENRE")) {
                editMetadata.putString(6, bundle.getString("android.media.metadata.GENRE"));
            }
            if (bundle.containsKey("android.media.metadata.TITLE")) {
                editMetadata.putString(7, bundle.getString("android.media.metadata.TITLE"));
            }
            if (bundle.containsKey("android.media.metadata.TRACK_NUMBER")) {
                editMetadata.putLong(z2, bundle.getLong("android.media.metadata.TRACK_NUMBER"));
            }
            if (bundle.containsKey("android.media.metadata.WRITER")) {
                editMetadata.putString(11, bundle.getString("android.media.metadata.WRITER"));
            }
            return editMetadata;
        }

        public void setSessionActivity(PendingIntent pendingIntent) {
            synchronized (this.mLock) {
                this.mSessionActivity = pendingIntent;
            }
        }

        public void setQueue(List<QueueItem> list) {
            this.mQueue = list;
            sendQueue(list);
        }

        public void setQueueTitle(CharSequence charSequence) {
            this.mQueueTitle = charSequence;
            sendQueueTitle(charSequence);
        }

        public void setRatingType(int i) {
            this.mRatingType = i;
        }

        public void setCaptioningEnabled(boolean z) {
            if (this.mCaptioningEnabled != z) {
                this.mCaptioningEnabled = z;
                sendCaptioningEnabled(z);
            }
        }

        public void setRepeatMode(int i) {
            if (this.mRepeatMode != i) {
                this.mRepeatMode = i;
                sendRepeatMode(i);
            }
        }

        public void setShuffleModeEnabled(boolean z) {
            if (this.mShuffleModeEnabled != z) {
                this.mShuffleModeEnabled = z;
                sendShuffleModeEnabled(z);
            }
        }

        public void setShuffleMode(int i) {
            if (this.mShuffleMode != i) {
                this.mShuffleMode = i;
                sendShuffleMode(i);
            }
        }

        public void setExtras(Bundle bundle) {
            this.mExtras = bundle;
            sendExtras(bundle);
        }

        boolean update() {
            boolean z = true;
            boolean z2 = false;
            if (this.mIsActive) {
                if (!this.mIsMbrRegistered && (this.mFlags & z) != 0) {
                    registerMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
                    this.mIsMbrRegistered = z;
                } else if (this.mIsMbrRegistered && (this.mFlags & z) == 0) {
                    unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
                    this.mIsMbrRegistered = z2;
                }
                if (!this.mIsRccRegistered && (this.mFlags & 2) != 0) {
                    this.mAudioManager.registerRemoteControlClient(this.mRcc);
                    this.mIsRccRegistered = z;
                    return z;
                } else if (this.mIsRccRegistered && (this.mFlags & 2) == 0) {
                    this.mRcc.setPlaybackState(z2);
                    this.mAudioManager.unregisterRemoteControlClient(this.mRcc);
                    this.mIsRccRegistered = z2;
                }
            } else {
                if (this.mIsMbrRegistered) {
                    unregisterMediaButtonEventReceiver(this.mMediaButtonReceiverIntent, this.mMediaButtonReceiverComponentName);
                    this.mIsMbrRegistered = z2;
                }
                if (this.mIsRccRegistered) {
                    this.mRcc.setPlaybackState(z2);
                    this.mAudioManager.unregisterRemoteControlClient(this.mRcc);
                    this.mIsRccRegistered = z2;
                }
            }
            return z2;
        }

        void registerMediaButtonEventReceiver(PendingIntent pendingIntent, ComponentName componentName) {
            this.mAudioManager.registerMediaButtonEventReceiver(componentName);
        }

        void unregisterMediaButtonEventReceiver(PendingIntent pendingIntent, ComponentName componentName) {
            this.mAudioManager.unregisterMediaButtonEventReceiver(componentName);
        }

        void adjustVolume(int i, int i2) {
            if (this.mVolumeType != 2) {
                this.mAudioManager.adjustStreamVolume(this.mLocalStream, i, i2);
            } else if (this.mVolumeProvider != null) {
                this.mVolumeProvider.onAdjustVolume(i);
            }
        }

        void setVolumeTo(int i, int i2) {
            if (this.mVolumeType != 2) {
                this.mAudioManager.setStreamVolume(this.mLocalStream, i, i2);
            } else if (this.mVolumeProvider != null) {
                this.mVolumeProvider.onSetVolumeTo(i);
            }
        }

        void sendVolumeInfoChanged(ParcelableVolumeInfo parcelableVolumeInfo) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onVolumeInfoChanged(parcelableVolumeInfo);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendSessionDestroyed() {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onSessionDestroyed();
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
            this.mControllerCallbacks.kill();
        }

        private void sendEvent(String str, Bundle bundle) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onEvent(str, bundle);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendState(PlaybackStateCompat playbackStateCompat) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onPlaybackStateChanged(playbackStateCompat);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendMetadata(MediaMetadataCompat mediaMetadataCompat) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onMetadataChanged(mediaMetadataCompat);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendQueue(List<QueueItem> list) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onQueueChanged(list);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendQueueTitle(CharSequence charSequence) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onQueueTitleChanged(charSequence);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendCaptioningEnabled(boolean z) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onCaptioningEnabledChanged(z);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendRepeatMode(int i) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onRepeatModeChanged(i);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendShuffleModeEnabled(boolean z) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onShuffleModeChangedDeprecated(z);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendShuffleMode(int i) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onShuffleModeChanged(i);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }

        private void sendExtras(Bundle bundle) {
            int beginBroadcast = this.mControllerCallbacks.beginBroadcast() - 1;
            while (beginBroadcast >= 0) {
                try {
                    ((IMediaControllerCallback) this.mControllerCallbacks.getBroadcastItem(beginBroadcast)).onExtrasChanged(bundle);
                } catch (RemoteException unused) {
                    beginBroadcast--;
                }
            }
            this.mControllerCallbacks.finishBroadcast();
        }
    }

    @RequiresApi(18)
    static class MediaSessionImplApi18 extends MediaSessionImplBase {
        private static boolean sIsMbrPendingIntentSupported = true;

        MediaSessionImplApi18(Context context, String str, ComponentName componentName, PendingIntent pendingIntent) {
            super(context, str, componentName, pendingIntent);
        }

        public void setCallback(Callback callback, Handler handler) {
            super.setCallback(callback, handler);
            if (callback == null) {
                this.mRcc.setPlaybackPositionUpdateListener(null);
                return;
            }
            this.mRcc.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
                public void onPlaybackPositionUpdate(long j) {
                    MediaSessionImplApi18.this.postToHandler(18, (Object) Long.valueOf(j));
                }
            });
        }

        void setRccState(PlaybackStateCompat playbackStateCompat) {
            long position = playbackStateCompat.getPosition();
            float playbackSpeed = playbackStateCompat.getPlaybackSpeed();
            long lastPositionUpdateTime = playbackStateCompat.getLastPositionUpdateTime();
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (playbackStateCompat.getState() == 3) {
                long j = 0;
                if (position > j) {
                    if (lastPositionUpdateTime > j) {
                        j = elapsedRealtime - lastPositionUpdateTime;
                        if (playbackSpeed > 0.0f && playbackSpeed != 1.0f) {
                            j = (long) (((float) j) * playbackSpeed);
                        }
                    }
                    position += j;
                }
            }
            this.mRcc.setPlaybackState(getRccStateFromState(playbackStateCompat.getState()), position, playbackSpeed);
        }

        int getRccTransportControlFlagsFromActions(long j) {
            int rccTransportControlFlagsFromActions = super.getRccTransportControlFlagsFromActions(j);
            return (j & 256) != 0 ? rccTransportControlFlagsFromActions | 256 : rccTransportControlFlagsFromActions;
        }

        void registerMediaButtonEventReceiver(PendingIntent pendingIntent, ComponentName componentName) {
            if (sIsMbrPendingIntentSupported) {
                try {
                    this.mAudioManager.registerMediaButtonEventReceiver(pendingIntent);
                } catch (NullPointerException unused) {
                    Log.w("MediaSessionCompat", "Unable to register media button event receiver with PendingIntent, falling back to ComponentName.");
                    sIsMbrPendingIntentSupported = false;
                }
            }
            if (!sIsMbrPendingIntentSupported) {
                super.registerMediaButtonEventReceiver(pendingIntent, componentName);
            }
        }

        void unregisterMediaButtonEventReceiver(PendingIntent pendingIntent, ComponentName componentName) {
            if (sIsMbrPendingIntentSupported) {
                this.mAudioManager.unregisterMediaButtonEventReceiver(pendingIntent);
            } else {
                super.unregisterMediaButtonEventReceiver(pendingIntent, componentName);
            }
        }
    }

    @RequiresApi(19)
    static class MediaSessionImplApi19 extends MediaSessionImplApi18 {
        MediaSessionImplApi19(Context context, String str, ComponentName componentName, PendingIntent pendingIntent) {
            super(context, str, componentName, pendingIntent);
        }

        public void setCallback(Callback callback, Handler handler) {
            super.setCallback(callback, handler);
            if (callback == null) {
                this.mRcc.setMetadataUpdateListener(null);
                return;
            }
            this.mRcc.setMetadataUpdateListener(new OnMetadataUpdateListener() {
                public void onMetadataUpdate(int i, Object obj) {
                    if (i == 268435457 && (obj instanceof Rating)) {
                        MediaSessionImplApi19.this.postToHandler(19, (Object) RatingCompat.fromRating(obj));
                    }
                }
            });
        }

        int getRccTransportControlFlagsFromActions(long j) {
            int rccTransportControlFlagsFromActions = super.getRccTransportControlFlagsFromActions(j);
            return (j & 128) != 0 ? rccTransportControlFlagsFromActions | 512 : rccTransportControlFlagsFromActions;
        }

        MetadataEditor buildRccMetadata(Bundle bundle) {
            MediaMetadataEditor buildRccMetadata = super.buildRccMetadata(bundle);
            long j = 0;
            int i = (((this.mState == null ? j : this.mState.getActions()) & 128) > j ? 1 : (((this.mState == null ? j : this.mState.getActions()) & 128) == j ? 0 : -1));
            int i2 = 268435457;
            if (i != 0) {
                buildRccMetadata.addEditableKey(i2);
            }
            if (bundle == null) {
                return buildRccMetadata;
            }
            if (bundle.containsKey("android.media.metadata.YEAR")) {
                buildRccMetadata.putLong(8, bundle.getLong("android.media.metadata.YEAR"));
            }
            if (bundle.containsKey("android.media.metadata.RATING")) {
                buildRccMetadata.putObject(101, bundle.getParcelable("android.media.metadata.RATING"));
            }
            if (bundle.containsKey("android.media.metadata.USER_RATING")) {
                buildRccMetadata.putObject(i2, bundle.getParcelable("android.media.metadata.USER_RATING"));
            }
            return buildRccMetadata;
        }
    }

    public MediaSessionCompat(Context context, String str) {
        Object obj = null;
        this(context, str, obj, obj);
    }

    public MediaSessionCompat(Context context, String str, ComponentName componentName, PendingIntent pendingIntent) {
        this.mActiveListeners = new ArrayList();
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        } else if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("tag must not be null or empty");
        } else {
            if (componentName == null) {
                componentName = MediaButtonReceiver.getMediaButtonReceiverComponent(context);
                if (componentName == null) {
                    Log.w("MediaSessionCompat", "Couldn't find a unique registered media button receiver in the given context.");
                }
            }
            if (componentName != null && pendingIntent == null) {
                Intent intent = new Intent("android.intent.action.MEDIA_BUTTON");
                intent.setComponent(componentName);
                int i = 0;
                pendingIntent = PendingIntent.getBroadcast(context, i, intent, i);
            }
            if (VERSION.SDK_INT >= 21) {
                this.mImpl = new MediaSessionImplApi21(context, str);
                setCallback(new Callback() {
                });
                this.mImpl.setMediaButtonReceiver(pendingIntent);
            } else if (VERSION.SDK_INT >= 19) {
                this.mImpl = new MediaSessionImplApi19(context, str, componentName, pendingIntent);
            } else if (VERSION.SDK_INT >= 18) {
                this.mImpl = new MediaSessionImplApi18(context, str, componentName, pendingIntent);
            } else {
                this.mImpl = new MediaSessionImplBase(context, str, componentName, pendingIntent);
            }
            this.mController = new MediaControllerCompat(context, this);
            if (sMaxBitmapSize == 0) {
                sMaxBitmapSize = (int) TypedValue.applyDimension(1, 320.0f, context.getResources().getDisplayMetrics());
            }
        }
    }

    private MediaSessionCompat(Context context, MediaSessionImpl mediaSessionImpl) {
        this.mActiveListeners = new ArrayList();
        this.mImpl = mediaSessionImpl;
        if (VERSION.SDK_INT >= 21 && !MediaSessionCompatApi21.hasCallback(mediaSessionImpl.getMediaSession())) {
            setCallback(new Callback() {
            });
        }
        this.mController = new MediaControllerCompat(context, this);
    }

    public void setCallback(Callback callback) {
        setCallback(callback, null);
    }

    public void setCallback(Callback callback, Handler handler) {
        MediaSessionImpl mediaSessionImpl = this.mImpl;
        if (handler == null) {
            handler = new Handler();
        }
        mediaSessionImpl.setCallback(callback, handler);
    }

    public void setSessionActivity(PendingIntent pendingIntent) {
        this.mImpl.setSessionActivity(pendingIntent);
    }

    public void setMediaButtonReceiver(PendingIntent pendingIntent) {
        this.mImpl.setMediaButtonReceiver(pendingIntent);
    }

    public void setFlags(int i) {
        this.mImpl.setFlags(i);
    }

    public void setPlaybackToLocal(int i) {
        this.mImpl.setPlaybackToLocal(i);
    }

    public void setPlaybackToRemote(VolumeProviderCompat volumeProviderCompat) {
        if (volumeProviderCompat == null) {
            throw new IllegalArgumentException("volumeProvider may not be null!");
        }
        this.mImpl.setPlaybackToRemote(volumeProviderCompat);
    }

    public void setActive(boolean z) {
        this.mImpl.setActive(z);
        Iterator it = this.mActiveListeners.iterator();
        while (it.hasNext()) {
            ((OnActiveChangeListener) it.next()).onActiveChanged();
        }
    }

    public boolean isActive() {
        return this.mImpl.isActive();
    }

    public void sendSessionEvent(String str, Bundle bundle) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("event cannot be null or empty");
        }
        this.mImpl.sendSessionEvent(str, bundle);
    }

    public void release() {
        this.mImpl.release();
    }

    public Token getSessionToken() {
        return this.mImpl.getSessionToken();
    }

    public MediaControllerCompat getController() {
        return this.mController;
    }

    public void setPlaybackState(PlaybackStateCompat playbackStateCompat) {
        this.mImpl.setPlaybackState(playbackStateCompat);
    }

    public void setMetadata(MediaMetadataCompat mediaMetadataCompat) {
        this.mImpl.setMetadata(mediaMetadataCompat);
    }

    public void setQueue(List<QueueItem> list) {
        this.mImpl.setQueue(list);
    }

    public void setQueueTitle(CharSequence charSequence) {
        this.mImpl.setQueueTitle(charSequence);
    }

    public void setRatingType(int i) {
        this.mImpl.setRatingType(i);
    }

    public void setCaptioningEnabled(boolean z) {
        this.mImpl.setCaptioningEnabled(z);
    }

    public void setRepeatMode(int i) {
        this.mImpl.setRepeatMode(i);
    }

    @Deprecated
    public void setShuffleModeEnabled(boolean z) {
        this.mImpl.setShuffleModeEnabled(z);
    }

    public void setShuffleMode(int i) {
        this.mImpl.setShuffleMode(i);
    }

    public void setExtras(Bundle bundle) {
        this.mImpl.setExtras(bundle);
    }

    public Object getMediaSession() {
        return this.mImpl.getMediaSession();
    }

    public Object getRemoteControlClient() {
        return this.mImpl.getRemoteControlClient();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public String getCallingPackage() {
        return this.mImpl.getCallingPackage();
    }

    public void addOnActiveChangeListener(OnActiveChangeListener onActiveChangeListener) {
        if (onActiveChangeListener == null) {
            throw new IllegalArgumentException("Listener may not be null");
        }
        this.mActiveListeners.add(onActiveChangeListener);
    }

    public void removeOnActiveChangeListener(OnActiveChangeListener onActiveChangeListener) {
        if (onActiveChangeListener == null) {
            throw new IllegalArgumentException("Listener may not be null");
        }
        this.mActiveListeners.remove(onActiveChangeListener);
    }

    public static MediaSessionCompat fromMediaSession(Context context, Object obj) {
        return (context == null || obj == null || VERSION.SDK_INT < 21) ? null : new MediaSessionCompat(context, new MediaSessionImplApi21(obj));
    }

    private static PlaybackStateCompat getStateWithUpdatedPosition(PlaybackStateCompat playbackStateCompat, MediaMetadataCompat mediaMetadataCompat) {
        if (playbackStateCompat != null) {
            long j = -1;
            if (playbackStateCompat.getPosition() != j) {
                if (playbackStateCompat.getState() == 3 || playbackStateCompat.getState() == 4 || playbackStateCompat.getState() == 5) {
                    long lastPositionUpdateTime = playbackStateCompat.getLastPositionUpdateTime();
                    long j2 = 0;
                    if (lastPositionUpdateTime > j2) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        long playbackSpeed = ((long) (playbackStateCompat.getPlaybackSpeed() * ((float) (elapsedRealtime - lastPositionUpdateTime)))) + playbackStateCompat.getPosition();
                        if (mediaMetadataCompat != null && mediaMetadataCompat.containsKey("android.media.metadata.DURATION")) {
                            j = mediaMetadataCompat.getLong("android.media.metadata.DURATION");
                        }
                        long j3 = (j < j2 || playbackSpeed <= j) ? playbackSpeed < j2 ? j2 : playbackSpeed : j;
                        return new PlaybackStateCompat.Builder(playbackStateCompat).setState(playbackStateCompat.getState(), j3, playbackStateCompat.getPlaybackSpeed(), elapsedRealtime).build();
                    }
                }
                return playbackStateCompat;
            }
        }
        return playbackStateCompat;
    }
}
