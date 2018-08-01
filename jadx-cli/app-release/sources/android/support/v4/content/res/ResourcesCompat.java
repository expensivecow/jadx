package android.support.v4.content.res;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.FontRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.FontResourcesParserCompat.FamilyResourceEntry;
import android.support.v4.graphics.TypefaceCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

public final class ResourcesCompat {
    private static final String TAG = "ResourcesCompat";

    @Nullable
    public static Drawable getDrawable(@NonNull Resources resources, @DrawableRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return resources.getDrawable(i, theme);
        }
        return resources.getDrawable(i);
    }

    @Nullable
    public static Drawable getDrawableForDensity(@NonNull Resources resources, @DrawableRes int i, int i2, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 21) {
            return resources.getDrawableForDensity(i, i2, theme);
        }
        if (VERSION.SDK_INT >= 15) {
            return resources.getDrawableForDensity(i, i2);
        }
        return resources.getDrawable(i);
    }

    @ColorInt
    public static int getColor(@NonNull Resources resources, @ColorRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return resources.getColor(i, theme);
        }
        return resources.getColor(i);
    }

    @Nullable
    public static ColorStateList getColorStateList(@NonNull Resources resources, @ColorRes int i, @Nullable Theme theme) throws NotFoundException {
        if (VERSION.SDK_INT >= 23) {
            return resources.getColorStateList(i, theme);
        }
        return resources.getColorStateList(i);
    }

    @Nullable
    public static Typeface getFont(@NonNull Context context, @FontRes int i) throws NotFoundException {
        Object obj = null;
        if (context.isRestricted()) {
            return obj;
        }
        return loadFont(context, i, new TypedValue(), 0, obj);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static Typeface getFont(@NonNull Context context, @FontRes int i, TypedValue typedValue, int i2, @Nullable TextView textView) throws NotFoundException {
        if (context.isRestricted()) {
            return null;
        }
        return loadFont(context, i, typedValue, i2, textView);
    }

    private static Typeface loadFont(@NonNull Context context, int i, TypedValue typedValue, int i2, @Nullable TextView textView) {
        Resources resources = context.getResources();
        resources.getValue(i, typedValue, true);
        Typeface loadFont = loadFont(context, resources, typedValue, i, i2, textView);
        if (loadFont != null) {
            return loadFont;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Font resource ID #0x");
        stringBuilder.append(Integer.toHexString(i));
        throw new NotFoundException(stringBuilder.toString());
    }

    private static Typeface loadFont(@NonNull Context context, Resources resources, TypedValue typedValue, int i, int i2, @Nullable TextView textView) {
        StringBuilder stringBuilder;
        if (typedValue.string == null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Resource \"");
            stringBuilder2.append(resources.getResourceName(i));
            stringBuilder2.append("\" (");
            stringBuilder2.append(Integer.toHexString(i));
            stringBuilder2.append(") is not a Font: ");
            stringBuilder2.append(typedValue);
            throw new NotFoundException(stringBuilder2.toString());
        }
        String charSequence = typedValue.string.toString();
        Typeface typeface = null;
        if (!charSequence.startsWith("res/")) {
            return typeface;
        }
        Typeface findFromCache = TypefaceCompat.findFromCache(resources, i, i2);
        if (findFromCache != null) {
            return findFromCache;
        }
        try {
            if (!charSequence.toLowerCase().endsWith(".xml")) {
                return TypefaceCompat.createFromResourcesFontFile(context, resources, i, charSequence, i2);
            }
            FamilyResourceEntry parse = FontResourcesParserCompat.parse(resources.getXml(i), resources);
            if (parse != null) {
                return TypefaceCompat.createFromResourcesFamilyXml(context, parse, resources, i, i2, textView);
            }
            Log.e("ResourcesCompat", "Failed to find font-family tag");
            return typeface;
        } catch (Throwable e) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to parse xml resource ");
            stringBuilder.append(charSequence);
            Log.e("ResourcesCompat", stringBuilder.toString(), e);
            return typeface;
        } catch (Throwable e2) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed to read xml resource ");
            stringBuilder.append(charSequence);
            Log.e("ResourcesCompat", stringBuilder.toString(), e2);
            return typeface;
        }
    }

    private ResourcesCompat() {
    }
}
