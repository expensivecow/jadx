package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.TypedValue;

class ThemeUtils {
    static final int[] ACTIVATED_STATE_SET;
    static final int[] CHECKED_STATE_SET;
    static final int[] DISABLED_STATE_SET;
    static final int[] EMPTY_STATE_SET;
    static final int[] FOCUSED_STATE_SET;
    static final int[] NOT_PRESSED_OR_FOCUSED_STATE_SET = new int[]{-16842919, -16842908};
    static final int[] PRESSED_STATE_SET;
    static final int[] SELECTED_STATE_SET;
    private static final int[] TEMP_ARRAY;
    private static final ThreadLocal<TypedValue> TL_TYPED_VALUE = new ThreadLocal();

    ThemeUtils() {
    }

    static {
        int i = 1;
        int[] iArr = new int[i];
        int i2 = 0;
        iArr[i2] = -16842910;
        DISABLED_STATE_SET = iArr;
        iArr = new int[i];
        iArr[i2] = 16842908;
        FOCUSED_STATE_SET = iArr;
        iArr = new int[i];
        iArr[i2] = 16843518;
        ACTIVATED_STATE_SET = iArr;
        iArr = new int[i];
        iArr[i2] = 16842919;
        PRESSED_STATE_SET = iArr;
        iArr = new int[i];
        iArr[i2] = 16842912;
        CHECKED_STATE_SET = iArr;
        iArr = new int[i];
        iArr[i2] = 16842913;
        SELECTED_STATE_SET = iArr;
        int i3 = 2;
        EMPTY_STATE_SET = new int[i2];
        TEMP_ARRAY = new int[i];
    }

    public static ColorStateList createDisabledStateList(int i, int i2) {
        int i3 = 2;
        int[][] iArr = new int[i3][];
        int[] iArr2 = new int[i3];
        int i4 = 0;
        iArr[i4] = DISABLED_STATE_SET;
        iArr2[i4] = i2;
        int i5 = 1;
        iArr[i5] = EMPTY_STATE_SET;
        iArr2[i5] = i;
        return new ColorStateList(iArr, iArr2);
    }

    public static int getThemeAttrColor(Context context, int i) {
        int i2 = 0;
        TEMP_ARRAY[i2] = i;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, null, TEMP_ARRAY);
        try {
            i = obtainStyledAttributes.getColor(i2, i2);
            return i;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static ColorStateList getThemeAttrColorStateList(Context context, int i) {
        int i2 = 0;
        TEMP_ARRAY[i2] = i;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, null, TEMP_ARRAY);
        try {
            ColorStateList colorStateList = obtainStyledAttributes.getColorStateList(i2);
            return colorStateList;
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public static int getDisabledThemeAttrColor(Context context, int i) {
        ColorStateList themeAttrColorStateList = getThemeAttrColorStateList(context, i);
        if (themeAttrColorStateList != null && themeAttrColorStateList.isStateful()) {
            return themeAttrColorStateList.getColorForState(DISABLED_STATE_SET, themeAttrColorStateList.getDefaultColor());
        }
        TypedValue typedValue = getTypedValue();
        context.getTheme().resolveAttribute(16842803, typedValue, true);
        return getThemeAttrColor(context, i, typedValue.getFloat());
    }

    private static TypedValue getTypedValue() {
        TypedValue typedValue = (TypedValue) TL_TYPED_VALUE.get();
        if (typedValue != null) {
            return typedValue;
        }
        typedValue = new TypedValue();
        TL_TYPED_VALUE.set(typedValue);
        return typedValue;
    }

    static int getThemeAttrColor(Context context, int i, float f) {
        int themeAttrColor = getThemeAttrColor(context, i);
        return ColorUtils.setAlphaComponent(themeAttrColor, Math.round(((float) Color.alpha(themeAttrColor)) * f));
    }
}
