package android.support.v4.text;

import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.Method;
import java.util.Locale;

@RequiresApi(14)
class ICUCompatIcs {
    private static final String TAG = "ICUCompatIcs";
    private static Method sAddLikelySubtagsMethod;
    private static Method sGetScriptMethod;

    ICUCompatIcs() {
    }

    static {
        try {
            Class cls = Class.forName("libcore.icu.ICU");
            if (cls != null) {
                int i = 1;
                Class[] clsArr = new Class[i];
                int i2 = 0;
                clsArr[i2] = String.class;
                sGetScriptMethod = cls.getMethod("getScript", clsArr);
                Class[] clsArr2 = new Class[i];
                clsArr2[i2] = String.class;
                sAddLikelySubtagsMethod = cls.getMethod("addLikelySubtags", clsArr2);
            }
        } catch (Throwable e) {
            Method method = null;
            sGetScriptMethod = method;
            sAddLikelySubtagsMethod = method;
            Log.w("ICUCompatIcs", e);
        }
    }

    public static String maximizeAndGetScript(Locale locale) {
        String addLikelySubtags = addLikelySubtags(locale);
        return addLikelySubtags != null ? getScript(addLikelySubtags) : null;
    }

    private static String getScript(String str) {
        String str2 = null;
        try {
            if (sGetScriptMethod != null) {
                return (String) sGetScriptMethod.invoke(str2, new Object[]{str});
            }
        } catch (Throwable e) {
            Log.w("ICUCompatIcs", e);
        } catch (Throwable e2) {
            Log.w("ICUCompatIcs", e2);
        }
        return str2;
    }

    private static String addLikelySubtags(Locale locale) {
        String locale2 = locale.toString();
        try {
            if (sAddLikelySubtagsMethod != null) {
                return (String) sAddLikelySubtagsMethod.invoke(null, new Object[]{locale2});
            }
        } catch (Throwable e) {
            Log.w("ICUCompatIcs", e);
        } catch (Throwable e2) {
            Log.w("ICUCompatIcs", e2);
        }
        return locale2;
    }
}
