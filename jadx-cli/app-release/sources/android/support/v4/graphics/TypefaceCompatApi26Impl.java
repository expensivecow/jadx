package android.support.v4.graphics;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.Typeface.Builder;
import android.graphics.fonts.FontVariationAxis;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import android.support.v4.content.res.FontResourcesParserCompat.FontFileResourceEntry;
import android.support.v4.provider.FontsContractCompat;
import android.support.v4.provider.FontsContractCompat.FontInfo;
import android.util.Log;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;

@RequiresApi(26)
@RestrictTo({Scope.LIBRARY_GROUP})
public class TypefaceCompatApi26Impl extends TypefaceCompatApi21Impl {
    private static final String ABORT_CREATION_METHOD = "abortCreation";
    private static final String ADD_FONT_FROM_ASSET_MANAGER_METHOD = "addFontFromAssetManager";
    private static final String ADD_FONT_FROM_BUFFER_METHOD = "addFontFromBuffer";
    private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
    private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
    private static final String FREEZE_METHOD = "freeze";
    private static final int RESOLVE_BY_FONT_TABLE = -1;
    private static final String TAG = "TypefaceCompatApi26Impl";
    private static final Method sAbortCreation;
    private static final Method sAddFontFromAssetManager;
    private static final Method sAddFontFromBuffer;
    private static final Method sCreateFromFamiliesWithDefault;
    private static final Class sFontFamily;
    private static final Constructor sFontFamilyCtor;
    private static final Method sFreeze;

    static {
        Class cls;
        Method method;
        Method method2;
        Method method3;
        Method method4;
        Method declaredMethod;
        Constructor constructor = null;
        try {
            cls = Class.forName("android.graphics.FontFamily");
            int i = 0;
            Constructor constructor2 = cls.getConstructor(new Class[i]);
            Class[] clsArr = new Class[8];
            clsArr[i] = AssetManager.class;
            boolean z = true;
            clsArr[z] = String.class;
            int i2 = 2;
            clsArr[i2] = Integer.TYPE;
            int i3 = 3;
            clsArr[i3] = Boolean.TYPE;
            int i4 = 4;
            clsArr[i4] = Integer.TYPE;
            int i5 = 5;
            clsArr[i5] = Integer.TYPE;
            clsArr[6] = Integer.TYPE;
            clsArr[7] = FontVariationAxis[].class;
            method = cls.getMethod("addFontFromAssetManager", clsArr);
            Class[] clsArr2 = new Class[i5];
            clsArr2[i] = ByteBuffer.class;
            clsArr2[z] = Integer.TYPE;
            clsArr2[i2] = FontVariationAxis[].class;
            clsArr2[i3] = Integer.TYPE;
            clsArr2[i4] = Integer.TYPE;
            method2 = cls.getMethod("addFontFromBuffer", clsArr2);
            method3 = cls.getMethod("freeze", new Class[i]);
            method4 = cls.getMethod("abortCreation", new Class[i]);
            Class[] clsArr3 = new Class[i3];
            clsArr3[i] = Array.newInstance(cls, z).getClass();
            clsArr3[z] = Integer.TYPE;
            clsArr3[i2] = Integer.TYPE;
            declaredMethod = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", clsArr3);
            declaredMethod.setAccessible(z);
            constructor = constructor2;
        } catch (Throwable e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unable to collect necessary methods for class ");
            stringBuilder.append(e.getClass().getName());
            Log.e("TypefaceCompatApi26Impl", stringBuilder.toString(), e);
            cls = constructor;
            declaredMethod = cls;
            method = declaredMethod;
            method2 = method;
            method3 = method2;
            method4 = method3;
        }
        sFontFamilyCtor = constructor;
        sFontFamily = cls;
        sAddFontFromAssetManager = method;
        sAddFontFromBuffer = method2;
        sFreeze = method3;
        sAbortCreation = method4;
        sCreateFromFamiliesWithDefault = declaredMethod;
    }

    private static boolean isFontFamilyPrivateAPIAvailable() {
        if (sAddFontFromAssetManager == null) {
            Log.w("TypefaceCompatApi26Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
        }
        return sAddFontFromAssetManager != null;
    }

    private static Object newFamily() {
        try {
            return sFontFamilyCtor.newInstance(new Object[0]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontFromAssetManager(Context context, Object obj, String str, int i, int i2, int i3) {
        try {
            Method method = sAddFontFromAssetManager;
            r1 = new Object[8];
            boolean z = false;
            r1[z] = context.getAssets();
            r1[1] = str;
            r1[2] = Integer.valueOf(z);
            r1[3] = Boolean.valueOf(z);
            r1[4] = Integer.valueOf(i);
            r1[5] = Integer.valueOf(i2);
            r1[6] = Integer.valueOf(i3);
            r1[7] = null;
            return ((Boolean) method.invoke(obj, r1)).booleanValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean addFontFromBuffer(Object obj, ByteBuffer byteBuffer, int i, int i2, int i3) {
        try {
            return ((Boolean) sAddFontFromBuffer.invoke(obj, new Object[]{byteBuffer, Integer.valueOf(i), null, Integer.valueOf(i2), Integer.valueOf(i3)})).booleanValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static Typeface createFromFamiliesWithDefault(Object obj) {
        try {
            int i = 1;
            Array.set(Array.newInstance(sFontFamily, i), 0, obj);
            r4 = new Object[3];
            int i2 = -1;
            r4[i] = Integer.valueOf(i2);
            r4[2] = Integer.valueOf(i2);
            return (Typeface) sCreateFromFamiliesWithDefault.invoke(null, r4);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean freeze(Object obj) {
        try {
            return ((Boolean) sFreeze.invoke(obj, new Object[0])).booleanValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean abortCreation(Object obj) {
        try {
            return ((Boolean) sAbortCreation.invoke(obj, new Object[0])).booleanValue();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontFamilyFilesResourceEntry fontFamilyFilesResourceEntry, Resources resources, int i) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromFontFamilyFilesResourceEntry(context, fontFamilyFilesResourceEntry, resources, i);
        }
        Object newFamily = newFamily();
        FontFileResourceEntry[] entries = fontFamilyFilesResourceEntry.getEntries();
        i = entries.length;
        int i2 = 0;
        while (true) {
            Typeface typeface = null;
            if (i2 < i) {
                FontFileResourceEntry fontFileResourceEntry = entries[i2];
                int weight = fontFileResourceEntry.getWeight();
                boolean isItalic = fontFileResourceEntry.isItalic();
                if (addFontFromAssetManager(context, newFamily, fontFileResourceEntry.getFileName(), 0, weight, isItalic)) {
                    i2++;
                } else {
                    abortCreation(newFamily);
                    return typeface;
                }
            } else if (freeze(newFamily)) {
                return createFromFamiliesWithDefault(newFamily);
            } else {
                return typeface;
            }
        }
    }

    public Typeface createFromFontInfo(Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fontInfoArr, int i) {
        ParcelFileDescriptor openFileDescriptor;
        Throwable th;
        Throwable th2;
        int i2 = 1;
        Typeface typeface = null;
        if (fontInfoArr.length < i2) {
            return typeface;
        }
        if (isFontFamilyPrivateAPIAvailable()) {
            Map prepareFontData = FontsContractCompat.prepareFontData(context, fontInfoArr, cancellationSignal);
            Object newFamily = newFamily();
            i = 0;
            int length = fontInfoArr.length;
            int i3 = i;
            while (i < length) {
                FontInfo fontInfo = fontInfoArr[i];
                ByteBuffer byteBuffer = (ByteBuffer) prepareFontData.get(fontInfo.getUri());
                if (byteBuffer != null) {
                    if (addFontFromBuffer(newFamily, byteBuffer, fontInfo.getTtcIndex(), fontInfo.getWeight(), fontInfo.isItalic())) {
                        i3 = i2;
                    } else {
                        abortCreation(newFamily);
                        return typeface;
                    }
                }
                i++;
            }
            if (i3 == 0) {
                abortCreation(newFamily);
                return typeface;
            } else if (freeze(newFamily)) {
                return createFromFamiliesWithDefault(newFamily);
            } else {
                return typeface;
            }
        }
        FontInfo findBestInfo = findBestInfo(fontInfoArr, i);
        try {
            openFileDescriptor = context.getContentResolver().openFileDescriptor(findBestInfo.getUri(), "r", cancellationSignal);
            try {
                Typeface build = new Builder(openFileDescriptor.getFileDescriptor()).setWeight(findBestInfo.getWeight()).setItalic(findBestInfo.isItalic()).build();
                if (openFileDescriptor != null) {
                    openFileDescriptor.close();
                }
                return build;
            } catch (Throwable th22) {
                Throwable th3 = th22;
                th22 = th;
                th = th3;
            }
        } catch (IOException unused) {
            return typeface;
        }
        throw th;
        if (openFileDescriptor != null) {
            if (th22 != null) {
                try {
                    openFileDescriptor.close();
                } catch (Throwable th4) {
                    th22.addSuppressed(th4);
                }
            } else {
                openFileDescriptor.close();
            }
        }
        throw th;
    }

    @Nullable
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int i, String str, int i2) {
        if (!isFontFamilyPrivateAPIAvailable()) {
            return super.createFromResourcesFontFile(context, resources, i, str, i2);
        }
        Object newFamily = newFamily();
        Typeface typeface = null;
        if (!addFontFromAssetManager(context, newFamily, str, 0, -1, -1)) {
            abortCreation(newFamily);
            return typeface;
        } else if (freeze(newFamily)) {
            return createFromFamiliesWithDefault(newFamily);
        } else {
            return typeface;
        }
    }
}
