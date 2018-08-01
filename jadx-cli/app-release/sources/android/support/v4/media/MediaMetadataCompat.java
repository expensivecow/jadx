package android.support.v4.media;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

public final class MediaMetadataCompat implements Parcelable {
    public static final Creator<MediaMetadataCompat> CREATOR = new Creator<MediaMetadataCompat>() {
        public MediaMetadataCompat createFromParcel(Parcel parcel) {
            return new MediaMetadataCompat(parcel);
        }

        public MediaMetadataCompat[] newArray(int i) {
            return new MediaMetadataCompat[i];
        }
    };
    static final ArrayMap<String, Integer> METADATA_KEYS_TYPE = new ArrayMap();
    public static final String METADATA_KEY_ADVERTISEMENT = "android.media.metadata.ADVERTISEMENT";
    public static final String METADATA_KEY_ALBUM = "android.media.metadata.ALBUM";
    public static final String METADATA_KEY_ALBUM_ART = "android.media.metadata.ALBUM_ART";
    public static final String METADATA_KEY_ALBUM_ARTIST = "android.media.metadata.ALBUM_ARTIST";
    public static final String METADATA_KEY_ALBUM_ART_URI = "android.media.metadata.ALBUM_ART_URI";
    public static final String METADATA_KEY_ART = "android.media.metadata.ART";
    public static final String METADATA_KEY_ARTIST = "android.media.metadata.ARTIST";
    public static final String METADATA_KEY_ART_URI = "android.media.metadata.ART_URI";
    public static final String METADATA_KEY_AUTHOR = "android.media.metadata.AUTHOR";
    public static final String METADATA_KEY_BT_FOLDER_TYPE = "android.media.metadata.BT_FOLDER_TYPE";
    public static final String METADATA_KEY_COMPILATION = "android.media.metadata.COMPILATION";
    public static final String METADATA_KEY_COMPOSER = "android.media.metadata.COMPOSER";
    public static final String METADATA_KEY_DATE = "android.media.metadata.DATE";
    public static final String METADATA_KEY_DISC_NUMBER = "android.media.metadata.DISC_NUMBER";
    public static final String METADATA_KEY_DISPLAY_DESCRIPTION = "android.media.metadata.DISPLAY_DESCRIPTION";
    public static final String METADATA_KEY_DISPLAY_ICON = "android.media.metadata.DISPLAY_ICON";
    public static final String METADATA_KEY_DISPLAY_ICON_URI = "android.media.metadata.DISPLAY_ICON_URI";
    public static final String METADATA_KEY_DISPLAY_SUBTITLE = "android.media.metadata.DISPLAY_SUBTITLE";
    public static final String METADATA_KEY_DISPLAY_TITLE = "android.media.metadata.DISPLAY_TITLE";
    public static final String METADATA_KEY_DOWNLOAD_STATUS = "android.media.metadata.DOWNLOAD_STATUS";
    public static final String METADATA_KEY_DURATION = "android.media.metadata.DURATION";
    public static final String METADATA_KEY_GENRE = "android.media.metadata.GENRE";
    public static final String METADATA_KEY_MEDIA_ID = "android.media.metadata.MEDIA_ID";
    public static final String METADATA_KEY_MEDIA_URI = "android.media.metadata.MEDIA_URI";
    public static final String METADATA_KEY_NUM_TRACKS = "android.media.metadata.NUM_TRACKS";
    public static final String METADATA_KEY_RATING = "android.media.metadata.RATING";
    public static final String METADATA_KEY_TITLE = "android.media.metadata.TITLE";
    public static final String METADATA_KEY_TRACK_NUMBER = "android.media.metadata.TRACK_NUMBER";
    public static final String METADATA_KEY_USER_RATING = "android.media.metadata.USER_RATING";
    public static final String METADATA_KEY_WRITER = "android.media.metadata.WRITER";
    public static final String METADATA_KEY_YEAR = "android.media.metadata.YEAR";
    static final int METADATA_TYPE_BITMAP = 2;
    static final int METADATA_TYPE_LONG = 0;
    static final int METADATA_TYPE_RATING = 3;
    static final int METADATA_TYPE_TEXT = 1;
    private static final String[] PREFERRED_BITMAP_ORDER;
    private static final String[] PREFERRED_DESCRIPTION_ORDER;
    private static final String[] PREFERRED_URI_ORDER;
    private static final String TAG = "MediaMetadata";
    final Bundle mBundle;
    private MediaDescriptionCompat mDescription;
    private Object mMetadataObj;

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BitmapKey {
    }

    public static final class Builder {
        private final Bundle mBundle;

        public Builder() {
            this.mBundle = new Bundle();
        }

        public Builder(MediaMetadataCompat mediaMetadataCompat) {
            this.mBundle = new Bundle(mediaMetadataCompat.mBundle);
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public Builder(MediaMetadataCompat mediaMetadataCompat, int i) {
            this(mediaMetadataCompat);
            for (String str : this.mBundle.keySet()) {
                Object obj = this.mBundle.get(str);
                if (obj instanceof Bitmap) {
                    Bitmap bitmap = (Bitmap) obj;
                    if (bitmap.getHeight() > i || bitmap.getWidth() > i) {
                        putBitmap(str, scaleBitmap(bitmap, i));
                    }
                }
            }
        }

        public Builder putText(String str, CharSequence charSequence) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 1) {
                this.mBundle.putCharSequence(str, charSequence);
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The ");
            stringBuilder.append(str);
            stringBuilder.append(" key cannot be used to put a CharSequence");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder putString(String str, String str2) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 1) {
                this.mBundle.putCharSequence(str, str2);
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The ");
            stringBuilder.append(str);
            stringBuilder.append(" key cannot be used to put a String");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder putLong(String str, long j) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 0) {
                this.mBundle.putLong(str, j);
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The ");
            stringBuilder.append(str);
            stringBuilder.append(" key cannot be used to put a long");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder putRating(String str, RatingCompat ratingCompat) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 3) {
                if (VERSION.SDK_INT >= 19) {
                    this.mBundle.putParcelable(str, (Parcelable) ratingCompat.getRating());
                } else {
                    this.mBundle.putParcelable(str, ratingCompat);
                }
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The ");
            stringBuilder.append(str);
            stringBuilder.append(" key cannot be used to put a Rating");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public Builder putBitmap(String str, Bitmap bitmap) {
            if (!MediaMetadataCompat.METADATA_KEYS_TYPE.containsKey(str) || ((Integer) MediaMetadataCompat.METADATA_KEYS_TYPE.get(str)).intValue() == 2) {
                this.mBundle.putParcelable(str, bitmap);
                return this;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The ");
            stringBuilder.append(str);
            stringBuilder.append(" key cannot be used to put a Bitmap");
            throw new IllegalArgumentException(stringBuilder.toString());
        }

        public MediaMetadataCompat build() {
            return new MediaMetadataCompat(this.mBundle);
        }

        private Bitmap scaleBitmap(Bitmap bitmap, int i) {
            float f = (float) i;
            f = Math.min(f / ((float) bitmap.getWidth()), f / ((float) bitmap.getHeight()));
            return Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) * f), (int) (((float) bitmap.getHeight()) * f), true);
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LongKey {
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RatingKey {
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextKey {
    }

    public int describeContents() {
        return 0;
    }

    static {
        int i = 1;
        METADATA_KEYS_TYPE.put("android.media.metadata.TITLE", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.ARTIST", Integer.valueOf(i));
        int i2 = 0;
        METADATA_KEYS_TYPE.put("android.media.metadata.DURATION", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.AUTHOR", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.WRITER", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.COMPOSER", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.COMPILATION", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.DATE", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.YEAR", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.GENRE", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.TRACK_NUMBER", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.NUM_TRACKS", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISC_NUMBER", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ARTIST", Integer.valueOf(i));
        int i3 = 2;
        METADATA_KEYS_TYPE.put("android.media.metadata.ART", Integer.valueOf(i3));
        METADATA_KEYS_TYPE.put("android.media.metadata.ART_URI", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART", Integer.valueOf(i3));
        METADATA_KEYS_TYPE.put("android.media.metadata.ALBUM_ART_URI", Integer.valueOf(i));
        int i4 = 3;
        METADATA_KEYS_TYPE.put("android.media.metadata.USER_RATING", Integer.valueOf(i4));
        METADATA_KEYS_TYPE.put("android.media.metadata.RATING", Integer.valueOf(i4));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_TITLE", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_SUBTITLE", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_DESCRIPTION", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON", Integer.valueOf(i3));
        METADATA_KEYS_TYPE.put("android.media.metadata.DISPLAY_ICON_URI", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_ID", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.BT_FOLDER_TYPE", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.MEDIA_URI", Integer.valueOf(i));
        METADATA_KEYS_TYPE.put("android.media.metadata.ADVERTISEMENT", Integer.valueOf(i2));
        METADATA_KEYS_TYPE.put("android.media.metadata.DOWNLOAD_STATUS", Integer.valueOf(i2));
        String[] strArr = new String[7];
        strArr[i2] = "android.media.metadata.TITLE";
        strArr[i] = "android.media.metadata.ARTIST";
        strArr[i3] = "android.media.metadata.ALBUM";
        strArr[i4] = "android.media.metadata.ALBUM_ARTIST";
        strArr[4] = "android.media.metadata.WRITER";
        strArr[5] = "android.media.metadata.AUTHOR";
        strArr[6] = "android.media.metadata.COMPOSER";
        PREFERRED_DESCRIPTION_ORDER = strArr;
        strArr = new String[i4];
        strArr[i2] = "android.media.metadata.DISPLAY_ICON";
        strArr[i] = "android.media.metadata.ART";
        strArr[i3] = "android.media.metadata.ALBUM_ART";
        PREFERRED_BITMAP_ORDER = strArr;
        strArr = new String[i4];
        strArr[i2] = "android.media.metadata.DISPLAY_ICON_URI";
        strArr[i] = "android.media.metadata.ART_URI";
        strArr[i3] = "android.media.metadata.ALBUM_ART_URI";
        PREFERRED_URI_ORDER = strArr;
    }

    MediaMetadataCompat(Bundle bundle) {
        this.mBundle = new Bundle(bundle);
    }

    MediaMetadataCompat(Parcel parcel) {
        this.mBundle = parcel.readBundle();
    }

    public boolean containsKey(String str) {
        return this.mBundle.containsKey(str);
    }

    public CharSequence getText(String str) {
        return this.mBundle.getCharSequence(str);
    }

    public String getString(String str) {
        CharSequence charSequence = this.mBundle.getCharSequence(str);
        return charSequence != null ? charSequence.toString() : null;
    }

    public long getLong(String str) {
        return this.mBundle.getLong(str, 0);
    }

    public RatingCompat getRating(String str) {
        try {
            if (VERSION.SDK_INT >= 19) {
                return RatingCompat.fromRating(this.mBundle.getParcelable(str));
            }
            return (RatingCompat) this.mBundle.getParcelable(str);
        } catch (Throwable e) {
            Log.w("MediaMetadata", "Failed to retrieve a key as Rating.", e);
            return null;
        }
    }

    public Bitmap getBitmap(String str) {
        try {
            return (Bitmap) this.mBundle.getParcelable(str);
        } catch (Throwable e) {
            Log.w("MediaMetadata", "Failed to retrieve a key as Bitmap.", e);
            return null;
        }
    }

    public MediaDescriptionCompat getDescription() {
        if (this.mDescription != null) {
            return this.mDescription;
        }
        int i;
        Uri uri;
        Bitmap bitmap;
        Object string;
        Uri parse;
        String string2 = getString("android.media.metadata.MEDIA_ID");
        CharSequence[] charSequenceArr = new CharSequence[3];
        CharSequence text = getText("android.media.metadata.DISPLAY_TITLE");
        int i2 = 2;
        int i3 = 1;
        int i4 = 0;
        if (TextUtils.isEmpty(text)) {
            i = i4;
            int i5 = i;
            while (i < charSequenceArr.length && i5 < PREFERRED_DESCRIPTION_ORDER.length) {
                int i6 = i5 + 1;
                CharSequence text2 = getText(PREFERRED_DESCRIPTION_ORDER[i5]);
                if (!TextUtils.isEmpty(text2)) {
                    int i7 = i + 1;
                    charSequenceArr[i] = text2;
                    i = i7;
                }
                i5 = i6;
            }
        } else {
            charSequenceArr[i4] = text;
            charSequenceArr[i3] = getText("android.media.metadata.DISPLAY_SUBTITLE");
            charSequenceArr[i2] = getText("android.media.metadata.DISPLAY_DESCRIPTION");
        }
        i = i4;
        while (true) {
            uri = null;
            if (i >= PREFERRED_BITMAP_ORDER.length) {
                bitmap = uri;
                break;
            }
            bitmap = getBitmap(PREFERRED_BITMAP_ORDER[i]);
            if (bitmap != null) {
                break;
            }
            i++;
        }
        for (i = i4; i < PREFERRED_URI_ORDER.length; i++) {
            string = getString(PREFERRED_URI_ORDER[i]);
            if (!TextUtils.isEmpty(string)) {
                parse = Uri.parse(string);
                break;
            }
        }
        parse = uri;
        string = getString("android.media.metadata.MEDIA_URI");
        if (!TextUtils.isEmpty(string)) {
            uri = Uri.parse(string);
        }
        android.support.v4.media.MediaDescriptionCompat.Builder builder = new android.support.v4.media.MediaDescriptionCompat.Builder();
        builder.setMediaId(string2);
        builder.setTitle(charSequenceArr[i4]);
        builder.setSubtitle(charSequenceArr[i3]);
        builder.setDescription(charSequenceArr[i2]);
        builder.setIconBitmap(bitmap);
        builder.setIconUri(parse);
        builder.setMediaUri(uri);
        Bundle bundle = new Bundle();
        if (this.mBundle.containsKey("android.media.metadata.BT_FOLDER_TYPE")) {
            bundle.putLong("android.media.extra.BT_FOLDER_TYPE", getLong("android.media.metadata.BT_FOLDER_TYPE"));
        }
        if (this.mBundle.containsKey("android.media.metadata.DOWNLOAD_STATUS")) {
            bundle.putLong("android.media.extra.DOWNLOAD_STATUS", getLong("android.media.metadata.DOWNLOAD_STATUS"));
        }
        if (!bundle.isEmpty()) {
            builder.setExtras(bundle);
        }
        this.mDescription = builder.build();
        return this.mDescription;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mBundle);
    }

    public int size() {
        return this.mBundle.size();
    }

    public Set<String> keySet() {
        return this.mBundle.keySet();
    }

    public Bundle getBundle() {
        return this.mBundle;
    }

    public static MediaMetadataCompat fromMediaMetadata(Object obj) {
        if (obj == null || VERSION.SDK_INT < 21) {
            return null;
        }
        Parcel obtain = Parcel.obtain();
        int i = 0;
        MediaMetadataCompatApi21.writeToParcel(obj, obtain, i);
        obtain.setDataPosition(i);
        MediaMetadataCompat mediaMetadataCompat = (MediaMetadataCompat) CREATOR.createFromParcel(obtain);
        obtain.recycle();
        mediaMetadataCompat.mMetadataObj = obj;
        return mediaMetadataCompat;
    }

    public Object getMediaMetadata() {
        if (this.mMetadataObj == null && VERSION.SDK_INT >= 21) {
            Parcel obtain = Parcel.obtain();
            int i = 0;
            writeToParcel(obtain, i);
            obtain.setDataPosition(i);
            this.mMetadataObj = MediaMetadataCompatApi21.createFromParcel(obtain);
            obtain.recycle();
        }
        return this.mMetadataObj;
    }
}
