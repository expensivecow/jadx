package android.support.v4.media;

import android.media.AudioAttributes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.Method;

@RequiresApi(21)
class AudioAttributesCompatApi21 {
    private static final String TAG = "AudioAttributesCompat";
    private static Method sAudioAttributesToLegacyStreamType;

    static final class Wrapper {
        private AudioAttributes mWrapped;

        private Wrapper(AudioAttributes audioAttributes) {
            this.mWrapped = audioAttributes;
        }

        public static Wrapper wrap(@NonNull AudioAttributes audioAttributes) {
            if (audioAttributes != null) {
                return new Wrapper(audioAttributes);
            }
            throw new IllegalArgumentException("AudioAttributesApi21.Wrapper cannot wrap null");
        }

        public AudioAttributes unwrap() {
            return this.mWrapped;
        }
    }

    AudioAttributesCompatApi21() {
    }

    public static int toLegacyStreamType(Wrapper wrapper) {
        AudioAttributes unwrap = wrapper.unwrap();
        try {
            int i = 0;
            int i2 = 1;
            if (sAudioAttributesToLegacyStreamType == null) {
                Class[] clsArr = new Class[i2];
                clsArr[i] = AudioAttributes.class;
                sAudioAttributesToLegacyStreamType = AudioAttributes.class.getMethod("toLegacyStreamType", clsArr);
            }
            Object[] objArr = new Object[i2];
            objArr[i] = unwrap;
            return ((Integer) sAudioAttributesToLegacyStreamType.invoke(null, objArr)).intValue();
        } catch (Throwable e) {
            Log.w("AudioAttributesCompat", "getLegacyStreamType() failed on API21+", e);
            return -1;
        }
    }
}
