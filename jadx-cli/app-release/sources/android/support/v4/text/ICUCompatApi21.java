package android.support.v4.text;

import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Locale;

@RequiresApi(21)
class ICUCompatApi21 {
    private static final String TAG = "ICUCompatApi21";
    private static Method sAddLikelySubtagsMethod;

    ICUCompatApi21() {
    }

    static {
        try {
            sAddLikelySubtagsMethod = Class.forName("libcore.icu.ICU").getMethod("addLikelySubtags", new Class[]{Locale.class});
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public static String maximizeAndGetScript(Locale locale) {
        try {
            return ((Locale) sAddLikelySubtagsMethod.invoke(null, new Object[]{locale})).getScript();
        } catch (Throwable e) {
            Log.w("ICUCompatApi21", e);
            return locale.getScript();
        } catch (Throwable e2) {
            Log.w("ICUCompatApi21", e2);
            return locale.getScript();
        }
    }
}