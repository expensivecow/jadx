package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.support.v4.provider.FontsContractCompat.FontInfo;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

@RequiresApi(24)
@RestrictTo({Scope.LIBRARY_GROUP})
class TypefaceCompatApi24Impl extends TypefaceCompatBaseImpl {
    private static final String ADD_FONT_WEIGHT_STYLE_METHOD = "addFontWeightStyle";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String TAG = "TypefaceCompatApi24Impl";
    private static final Method sAddFontWeightStyle;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;

    TypefaceCompatApi24Impl() {
    }

    static {
        Class cls;
        Method method;
        Method method2;
        Constructor constructor = null;
        try {
            cls = Class.forName("android.graphics.FontFamily");
            int i = 0;
            Constructor constructor2 = cls.getConstructor(new Class[i]);
            r5 = new Class[5];
            int i2 = 1;
            r5[i2] = Integer.TYPE;
            r5[2] = List.class;
            r5[3] = Integer.TYPE;
            r5[4] = Boolean.TYPE;
            method = cls.getMethod("addFontWeightStyle", r5);
            Object newInstance = Array.newInstance(cls, i2);
            Class[] clsArr = new Class[i2];
            clsArr[i] = newInstance.getClass();
            method2 = Typeface.class.getMethod("createFromFamiliesWithDefault", clsArr);
            constructor = constructor2;
        } catch (Throwable e) {
            Log.e("TypefaceCompatApi24Impl", e.getClass().getName(), e);
            cls = constructor;
            method2 = cls;
            method = method2;
        }
        sFontFamilyCtor = constructor;
        sFontFamily = cls;
        sAddFontWeightStyle = method;
        sCreateFromFamiliesWithDefault = method2;
    }

    public static boolean isUsable() {
        if (sAddFontWeightStyle == null) {
            Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return sAddFontWeightStyle != null;
    }

    private static Object newFamily() {
        try {
            return sFontFamilyCtor.newInstance(new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontWeightStyle(Object obj, ByteBuffer byteBuffer, int i, int i2, boolean z) {
        try {
            return ((Boolean) sAddFontWeightStyle.invoke(obj, new Object[]{byteBuffer, Integer.valueOf(i), null, Integer.valueOf(i2), Boolean.valueOf(z)})).booleanValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Typeface createFromFamiliesWithDefault(Object obj) {
        try {
            int i = 1;
            Object newInstance = Array.newInstance(sFontFamily, i);
            int i2 = 0;
            Array.set(newInstance, i2, obj);
            Object[] objArr = new Object[i];
            objArr[i2] = newInstance;
            return (Typeface) sCreateFromFamiliesWithDefault.invoke(null, objArr);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Typeface createFromFontInfo(Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fontInfoArr, int i) {
        Object newFamily = newFamily();
        SimpleArrayMap simpleArrayMap = new SimpleArrayMap();
        for (FontInfo fontInfo : fontInfoArr) {
            Uri uri = fontInfo.getUri();
            ByteBuffer byteBuffer = (ByteBuffer) simpleArrayMap.get(uri);
            if (byteBuffer == null) {
                byteBuffer = TypefaceCompatUtil.mmap(context, cancellationSignal, uri);
                simpleArrayMap.put(uri, byteBuffer);
            }
            if (!addFontWeightStyle(newFamily, byteBuffer, fontInfo.getTtcIndex(), fontInfo.getWeight(), fontInfo.isItalic())) {
                return null;
            }
        }
        return createFromFamiliesWithDefault(newFamily);
    }

    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, Resources resources, int i) {
        Object newFamily = newFamily();
        FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        int i2 = 0;
        int length = entries.length;
        for (int i3 = i2; i3 < length; i3++) {
            FontFileResourceEntry fontFileResourceEntry = entries[i3];
            if (!addFontWeightStyle(newFamily, TypefaceCompatUtil.copyToDirectBuffer(context, resources, fontFileResourceEntry.getResourceId()), i2, fontFileResourceEntry.getWeight(), fontFileResourceEntry.isItalic())) {
                return null;
            }
        }
        return createFromFamiliesWithDefault(newFamily);
    }
}
