package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.LruCache;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({Scope.LIBRARY_GROUP})
public final class AppCompatDrawableManager {
    private static final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY;
    private static final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED;
    private static final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL;
    private static final ColorFilterLruCache COLOR_FILTER_CACHE;
    private static final boolean DEBUG = false;
    private static final Mode DEFAULT_MODE = Mode.SRC_IN;
    private static AppCompatDrawableManager INSTANCE = null;
    private static final String PLATFORM_VD_CLAZZ = "android.graphics.drawable.VectorDrawable";
    private static final String SKIP_DRAWABLE_TAG = "appcompat_skip_skip";
    private static final String TAG = "AppCompatDrawableManager";
    private static final int[] TINT_CHECKABLE_BUTTON_LIST;
    private static final int[] TINT_COLOR_CONTROL_NORMAL;
    private static final int[] TINT_COLOR_CONTROL_STATE_LIST;
    private ArrayMap<String, InflateDelegate> mDelegates;
    private final Object mDrawableCacheLock = new Object();
    private final WeakHashMap<Context, LongSparseArray<WeakReference<ConstantState>>> mDrawableCaches = new WeakHashMap(0);
    private boolean mHasCheckedVectorDrawableSetup;
    private SparseArrayCompat<String> mKnownDrawableIdTags;
    private WeakHashMap<Context, SparseArrayCompat<ColorStateList>> mTintLists;
    private TypedValue mTypedValue;

    private interface InflateDelegate {
        Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme);
    }

    @RequiresApi(11)
    private static class AvdcInflateDelegate implements InflateDelegate {
        AvdcInflateDelegate() {
        }

        public Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) {
            try {
                return AnimatedVectorDrawableCompat.createFromXmlInner(context, context.getResources(), xmlPullParser, attributeSet, theme);
            } catch (Throwable e) {
                Log.e("AvdcInflateDelegate", "Exception while inflating <animated-vector>", e);
                return null;
            }
        }
    }

    private static class ColorFilterLruCache extends LruCache<Integer, PorterDuffColorFilter> {
        public ColorFilterLruCache(int i) {
            super(i);
        }

        PorterDuffColorFilter get(int i, Mode mode) {
            return (PorterDuffColorFilter) get(Integer.valueOf(generateCacheKey(i, mode)));
        }

        PorterDuffColorFilter put(int i, Mode mode, PorterDuffColorFilter porterDuffColorFilter) {
            return (PorterDuffColorFilter) put(Integer.valueOf(generateCacheKey(i, mode)), porterDuffColorFilter);
        }

        private static int generateCacheKey(int i, Mode mode) {
            int i2 = 31;
            return (i2 * (i + i2)) + mode.hashCode();
        }
    }

    private static class VdcInflateDelegate implements InflateDelegate {
        VdcInflateDelegate() {
        }

        public Drawable createFromXmlInner(@NonNull Context context, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) {
            try {
                return VectorDrawableCompat.createFromXmlInner(context.getResources(), xmlPullParser, attributeSet, theme);
            } catch (Throwable e) {
                Log.e("VdcInflateDelegate", "Exception while inflating <vector>", e);
                return null;
            }
        }
    }

    static {
        int i = 6;
        COLOR_FILTER_CACHE = new ColorFilterLruCache(i);
        int i2 = 3;
        int[] iArr = new int[i2];
        int i3 = 0;
        iArr[i3] = R.drawable.abc_textfield_search_default_mtrl_alpha;
        int i4 = 1;
        iArr[i4] = R.drawable.abc_textfield_default_mtrl_alpha;
        int i5 = 2;
        iArr[i5] = R.drawable.abc_ab_share_pack_mtrl_alpha;
        COLORFILTER_TINT_COLOR_CONTROL_NORMAL = iArr;
        int[] iArr2 = new int[7];
        iArr2[i3] = R.drawable.abc_ic_commit_search_api_mtrl_alpha;
        iArr2[i4] = R.drawable.abc_seekbar_tick_mark_material;
        iArr2[i5] = R.drawable.abc_ic_menu_share_mtrl_alpha;
        iArr2[i2] = R.drawable.abc_ic_menu_copy_mtrl_am_alpha;
        iArr2[4] = R.drawable.abc_ic_menu_cut_mtrl_alpha;
        iArr2[5] = R.drawable.abc_ic_menu_selectall_mtrl_alpha;
        iArr2[i] = R.drawable.abc_ic_menu_paste_mtrl_am_alpha;
        TINT_COLOR_CONTROL_NORMAL = iArr2;
        iArr2 = new int[10];
        iArr2[8] = R.drawable.abc_text_select_handle_middle_mtrl_light;
        iArr2[9] = R.drawable.abc_text_select_handle_right_mtrl_light;
        COLORFILTER_COLOR_CONTROL_ACTIVATED = iArr2;
        int[] iArr3 = new int[i2];
        iArr3[i3] = R.drawable.abc_popup_background_mtrl_mult;
        iArr3[i4] = R.drawable.abc_cab_background_internal_bg;
        iArr3[i5] = R.drawable.abc_menu_hardkey_panel_mtrl_mult;
        COLORFILTER_COLOR_BACKGROUND_MULTIPLY = iArr3;
        iArr3 = new int[i5];
        iArr3[i3] = R.drawable.abc_tab_indicator_material;
        iArr3[i4] = R.drawable.abc_textfield_search_material;
        TINT_COLOR_CONTROL_STATE_LIST = iArr3;
        iArr3 = new int[i5];
        iArr3[i3] = R.drawable.abc_btn_check_material;
        iArr3[i4] = R.drawable.abc_btn_radio_material;
        TINT_CHECKABLE_BUTTON_LIST = iArr3;
    }

    public static AppCompatDrawableManager get() {
        if (INSTANCE == null) {
            INSTANCE = new AppCompatDrawableManager();
            installDefaultInflateDelegates(INSTANCE);
        }
        return INSTANCE;
    }

    private static void installDefaultInflateDelegates(@NonNull AppCompatDrawableManager appCompatDrawableManager) {
        if (VERSION.SDK_INT < 24) {
            appCompatDrawableManager.addDelegate("vector", new VdcInflateDelegate());
            if (VERSION.SDK_INT >= 11) {
                appCompatDrawableManager.addDelegate("animated-vector", new AvdcInflateDelegate());
            }
        }
    }

    public Drawable getDrawable(@NonNull Context context, @DrawableRes int i) {
        return getDrawable(context, i, false);
    }

    Drawable getDrawable(@NonNull Context context, @DrawableRes int i, boolean z) {
        checkVectorDrawableSetup(context);
        Drawable loadDrawableFromDelegates = loadDrawableFromDelegates(context, i);
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = createDrawableIfNeeded(context, i);
        }
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = ContextCompat.getDrawable(context, i);
        }
        if (loadDrawableFromDelegates != null) {
            loadDrawableFromDelegates = tintDrawable(context, i, z, loadDrawableFromDelegates);
        }
        if (loadDrawableFromDelegates != null) {
            DrawableUtils.fixDrawable(loadDrawableFromDelegates);
        }
        return loadDrawableFromDelegates;
    }

    public void onConfigurationChanged(@NonNull Context context) {
        synchronized (this.mDrawableCacheLock) {
            LongSparseArray longSparseArray = (LongSparseArray) this.mDrawableCaches.get(context);
            if (longSparseArray != null) {
                longSparseArray.clear();
            }
        }
    }

    private static long createCacheKey(TypedValue typedValue) {
        return (((long) typedValue.assetCookie) << 32) | ((long) typedValue.data);
    }

    private Drawable createDrawableIfNeeded(@NonNull Context context, @DrawableRes int i) {
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        TypedValue typedValue = this.mTypedValue;
        context.getResources().getValue(i, typedValue, true);
        long createCacheKey = createCacheKey(typedValue);
        Drawable cachedDrawable = getCachedDrawable(context, createCacheKey);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        if (i == R.drawable.abc_cab_background_top_material) {
            cachedDrawable = new LayerDrawable(new Drawable[]{getDrawable(context, R.drawable.abc_cab_background_internal_bg), getDrawable(context, R.drawable.abc_cab_background_top_mtrl_alpha)});
        }
        if (cachedDrawable != null) {
            cachedDrawable.setChangingConfigurations(typedValue.changingConfigurations);
            addDrawableToCache(context, createCacheKey, cachedDrawable);
        }
        return cachedDrawable;
    }

    private Drawable tintDrawable(@NonNull Context context, @DrawableRes int i, boolean z, @NonNull Drawable drawable) {
        ColorStateList tintList = getTintList(context, i);
        if (tintList != null) {
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTintList(drawable, tintList);
            Mode tintMode = getTintMode(i);
            if (tintMode == null) {
                return drawable;
            }
            DrawableCompat.setTintMode(drawable, tintMode);
            return drawable;
        }
        int i2 = 16908301;
        int i3 = 16908303;
        int i4 = 16908288;
        LayerDrawable layerDrawable;
        if (i == R.drawable.abc_seekbar_track_material) {
            layerDrawable = (LayerDrawable) drawable;
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i4), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i3), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i2), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        } else if (i == R.drawable.abc_ratingbar_material || i == R.drawable.abc_ratingbar_indicator_material || i == R.drawable.abc_ratingbar_small_material) {
            layerDrawable = (LayerDrawable) drawable;
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i4), ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorControlNormal), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i3), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            setPorterDuffColorFilter(layerDrawable.findDrawableByLayerId(i2), ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated), DEFAULT_MODE);
            return drawable;
        } else if (tintDrawableUsingColorFilter(context, i, drawable) || !z) {
            return drawable;
        } else {
            return null;
        }
    }

    private Drawable loadDrawableFromDelegates(@NonNull Context context, @DrawableRes int i) {
        Drawable drawable = null;
        if (this.mDelegates == null || this.mDelegates.isEmpty()) {
            return drawable;
        }
        if (this.mKnownDrawableIdTags != null) {
            String str = (String) this.mKnownDrawableIdTags.get(i);
            if ("appcompat_skip_skip".equals(str) || (str != null && this.mDelegates.get(str) == null)) {
                return drawable;
            }
        }
        this.mKnownDrawableIdTags = new SparseArrayCompat();
        if (this.mTypedValue == null) {
            this.mTypedValue = new TypedValue();
        }
        TypedValue typedValue = this.mTypedValue;
        Resources resources = context.getResources();
        boolean z = true;
        resources.getValue(i, typedValue, z);
        long createCacheKey = createCacheKey(typedValue);
        Drawable cachedDrawable = getCachedDrawable(context, createCacheKey);
        if (cachedDrawable != null) {
            return cachedDrawable;
        }
        if (typedValue.string != null && typedValue.string.toString().endsWith(".xml")) {
            try {
                boolean next;
                boolean z2;
                XmlPullParser xml = resources.getXml(i);
                AttributeSet asAttributeSet = Xml.asAttributeSet(xml);
                while (true) {
                    next = xml.next();
                    z2 = true;
                    if (next == z2 || next == z) {
                        if (next == z2) {
                            throw new XmlPullParserException("No start tag found");
                        }
                        String name = xml.getName();
                        this.mKnownDrawableIdTags.append(i, name);
                        InflateDelegate inflateDelegate = (InflateDelegate) this.mDelegates.get(name);
                        if (inflateDelegate != null) {
                            cachedDrawable = inflateDelegate.createFromXmlInner(context, xml, asAttributeSet, context.getTheme());
                        }
                        if (cachedDrawable != null) {
                            cachedDrawable.setChangingConfigurations(typedValue.changingConfigurations);
                            addDrawableToCache(context, createCacheKey, cachedDrawable);
                        }
                    }
                }
                if (next == z2) {
                    String name2 = xml.getName();
                    this.mKnownDrawableIdTags.append(i, name2);
                    InflateDelegate inflateDelegate2 = (InflateDelegate) this.mDelegates.get(name2);
                    if (inflateDelegate2 != null) {
                        cachedDrawable = inflateDelegate2.createFromXmlInner(context, xml, asAttributeSet, context.getTheme());
                    }
                    if (cachedDrawable != null) {
                        cachedDrawable.setChangingConfigurations(typedValue.changingConfigurations);
                        addDrawableToCache(context, createCacheKey, cachedDrawable);
                    }
                } else {
                    throw new XmlPullParserException("No start tag found");
                }
            } catch (Throwable e) {
                Log.e("AppCompatDrawableManager", "Exception while inflating drawable", e);
            }
        }
        if (cachedDrawable == null) {
            this.mKnownDrawableIdTags.append(i, "appcompat_skip_skip");
        }
        return cachedDrawable;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.graphics.drawable.Drawable getCachedDrawable(@android.support.annotation.NonNull android.content.Context r5, long r6) {
        /*
        r4 = this;
        r0 = r4.mDrawableCacheLock;
        monitor-enter(r0);
        r1 = r4.mDrawableCaches;	 Catch:{ all -> 0x002f }
        r1 = r1.get(r5);	 Catch:{ all -> 0x002f }
        r1 = (android.support.v4.util.LongSparseArray) r1;	 Catch:{ all -> 0x002f }
        r2 = 0;
        if (r1 != 0) goto L_0x0010;
    L_0x000e:
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r2;
    L_0x0010:
        r3 = r1.get(r6);	 Catch:{ all -> 0x002f }
        r3 = (java.lang.ref.WeakReference) r3;	 Catch:{ all -> 0x002f }
        if (r3 == 0) goto L_0x002d;
    L_0x0018:
        r3 = r3.get();	 Catch:{ all -> 0x002f }
        r3 = (android.graphics.drawable.Drawable.ConstantState) r3;	 Catch:{ all -> 0x002f }
        if (r3 == 0) goto L_0x002a;
    L_0x0020:
        r5 = r5.getResources();	 Catch:{ all -> 0x002f }
        r5 = r3.newDrawable(r5);	 Catch:{ all -> 0x002f }
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r5;
    L_0x002a:
        r1.delete(r6);	 Catch:{ all -> 0x002f }
    L_0x002d:
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        return r2;
    L_0x002f:
        r5 = move-exception;
        monitor-exit(r0);	 Catch:{ all -> 0x002f }
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatDrawableManager.getCachedDrawable(android.content.Context, long):android.graphics.drawable.Drawable");
    }

    private boolean addDrawableToCache(@NonNull Context context, long j, @NonNull Drawable drawable) {
        ConstantState constantState = drawable.getConstantState();
        if (constantState == null) {
            return false;
        }
        synchronized (this.mDrawableCacheLock) {
            LongSparseArray longSparseArray = (LongSparseArray) this.mDrawableCaches.get(context);
            if (longSparseArray == null) {
                longSparseArray = new LongSparseArray();
                this.mDrawableCaches.put(context, longSparseArray);
            }
            longSparseArray.put(j, new WeakReference(constantState));
        }
        return true;
    }

    Drawable onDrawableLoadedFromResources(@NonNull Context context, @NonNull VectorEnabledTintResources vectorEnabledTintResources, @DrawableRes int i) {
        Drawable loadDrawableFromDelegates = loadDrawableFromDelegates(context, i);
        if (loadDrawableFromDelegates == null) {
            loadDrawableFromDelegates = vectorEnabledTintResources.superGetDrawable(i);
        }
        return loadDrawableFromDelegates != null ? tintDrawable(context, i, false, loadDrawableFromDelegates) : null;
    }

    static boolean tintDrawableUsingColorFilter(@NonNull Context context, @DrawableRes int i, @NonNull Drawable drawable) {
        int round;
        boolean z;
        Mode mode = DEFAULT_MODE;
        int i2 = 16842801;
        int i3 = -1;
        boolean z2 = false;
        boolean z3 = true;
        if (arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, i)) {
            i2 = R.attr.colorControlNormal;
        } else if (arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, i)) {
            i2 = R.attr.colorControlActivated;
        } else if (arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, i)) {
            mode = Mode.MULTIPLY;
        } else if (i == R.drawable.abc_list_divider_mtrl_alpha) {
            i2 = 16842800;
            round = Math.round(40.8f);
            z = z3;
            if (z) {
                return z2;
            }
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            drawable.setColorFilter(getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(context, i2), mode));
            if (round != i3) {
                drawable.setAlpha(round);
            }
            return z3;
        } else if (i != R.drawable.abc_dialog_material_background) {
            round = i3;
            z = z2;
            i2 = z;
            if (z) {
                return z2;
            }
            if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
                drawable = drawable.mutate();
            }
            drawable.setColorFilter(getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(context, i2), mode));
            if (round != i3) {
                drawable.setAlpha(round);
            }
            return z3;
        }
        round = i3;
        z = z3;
        if (z) {
            return z2;
        }
        if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
            drawable = drawable.mutate();
        }
        drawable.setColorFilter(getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(context, i2), mode));
        if (round != i3) {
            drawable.setAlpha(round);
        }
        return z3;
    }

    private void addDelegate(@NonNull String str, @NonNull InflateDelegate inflateDelegate) {
        if (this.mDelegates == null) {
            this.mDelegates = new ArrayMap();
        }
        this.mDelegates.put(str, inflateDelegate);
    }

    private void removeDelegate(@NonNull String str, @NonNull InflateDelegate inflateDelegate) {
        if (this.mDelegates != null && this.mDelegates.get(str) == inflateDelegate) {
            this.mDelegates.remove(str);
        }
    }

    private static boolean arrayContains(int[] iArr, int i) {
        boolean z = false;
        int length = iArr.length;
        for (int i2 = z; i2 < length; i2++) {
            if (iArr[i2] == i) {
                return true;
            }
        }
        return z;
    }

    static Mode getTintMode(int i) {
        return i == R.drawable.abc_switch_thumb_material ? Mode.MULTIPLY : null;
    }

    ColorStateList getTintList(@NonNull Context context, @DrawableRes int i) {
        ColorStateList tintListFromCache = getTintListFromCache(context, i);
        if (tintListFromCache == null) {
            if (i == R.drawable.abc_edit_text_material) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_edittext);
            } else if (i == R.drawable.abc_switch_track_mtrl_alpha) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_switch_track);
            } else if (i == R.drawable.abc_switch_thumb_material) {
                tintListFromCache = createSwitchThumbColorStateList(context);
            } else if (i == R.drawable.abc_btn_default_mtrl_shape) {
                tintListFromCache = createDefaultButtonColorStateList(context);
            } else if (i == R.drawable.abc_btn_borderless_material) {
                tintListFromCache = createBorderlessButtonColorStateList(context);
            } else if (i == R.drawable.abc_btn_colored_material) {
                tintListFromCache = createColoredButtonColorStateList(context);
            } else if (i == R.drawable.abc_spinner_mtrl_am_alpha || i == R.drawable.abc_spinner_textfield_background_material) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_spinner);
            } else if (arrayContains(TINT_COLOR_CONTROL_NORMAL, i)) {
                tintListFromCache = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorControlNormal);
            } else if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, i)) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_default);
            } else if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, i)) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_btn_checkable);
            } else if (i == R.drawable.abc_seekbar_thumb_material) {
                tintListFromCache = AppCompatResources.getColorStateList(context, R.color.abc_tint_seek_thumb);
            }
            if (tintListFromCache != null) {
                addTintListToCache(context, i, tintListFromCache);
            }
        }
        return tintListFromCache;
    }

    private ColorStateList getTintListFromCache(@NonNull Context context, @DrawableRes int i) {
        ColorStateList colorStateList = null;
        if (this.mTintLists == null) {
            return colorStateList;
        }
        SparseArrayCompat sparseArrayCompat = (SparseArrayCompat) this.mTintLists.get(context);
        if (sparseArrayCompat != null) {
            colorStateList = (ColorStateList) sparseArrayCompat.get(i);
        }
        return colorStateList;
    }

    private void addTintListToCache(@NonNull Context context, @DrawableRes int i, @NonNull ColorStateList colorStateList) {
        if (this.mTintLists == null) {
            this.mTintLists = new WeakHashMap();
        }
        SparseArrayCompat sparseArrayCompat = (SparseArrayCompat) this.mTintLists.get(context);
        if (sparseArrayCompat == null) {
            sparseArrayCompat = new SparseArrayCompat();
            this.mTintLists.put(context, sparseArrayCompat);
        }
        sparseArrayCompat.append(i, colorStateList);
    }

    private ColorStateList createDefaultButtonColorStateList(@NonNull Context context) {
        return createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorButtonNormal));
    }

    private ColorStateList createBorderlessButtonColorStateList(@NonNull Context context) {
        return createButtonColorStateList(context, 0);
    }

    private ColorStateList createColoredButtonColorStateList(@NonNull Context context) {
        return createButtonColorStateList(context, ThemeUtils.getThemeAttrColor(context, R.attr.colorAccent));
    }

    private ColorStateList createButtonColorStateList(@NonNull Context context, @ColorInt int i) {
        int i2 = 4;
        int[][] iArr = new int[i2][];
        int[] iArr2 = new int[i2];
        int themeAttrColor = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlHighlight);
        int disabledThemeAttrColor = ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorButtonNormal);
        int i3 = 0;
        iArr[i3] = ThemeUtils.DISABLED_STATE_SET;
        iArr2[i3] = disabledThemeAttrColor;
        int i4 = 1;
        iArr[i4] = ThemeUtils.PRESSED_STATE_SET;
        iArr2[i4] = ColorUtils.compositeColors(themeAttrColor, i);
        i4 = 2;
        iArr[i4] = ThemeUtils.FOCUSED_STATE_SET;
        iArr2[i4] = ColorUtils.compositeColors(themeAttrColor, i);
        themeAttrColor = 3;
        iArr[themeAttrColor] = ThemeUtils.EMPTY_STATE_SET;
        iArr2[themeAttrColor] = i;
        return new ColorStateList(iArr, iArr2);
    }

    private ColorStateList createSwitchThumbColorStateList(Context context) {
        int i = 3;
        int[][] iArr = new int[i][];
        int[] iArr2 = new int[i];
        ColorStateList themeAttrColorStateList = ThemeUtils.getThemeAttrColorStateList(context, R.attr.colorSwitchThumbNormal);
        int i2 = 2;
        int i3 = 1;
        int i4 = 0;
        if (themeAttrColorStateList == null || !themeAttrColorStateList.isStateful()) {
            iArr[i4] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[i4] = ThemeUtils.getDisabledThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
            iArr[i3] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[i3] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            iArr[i2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[i2] = ThemeUtils.getThemeAttrColor(context, R.attr.colorSwitchThumbNormal);
        } else {
            iArr[i4] = ThemeUtils.DISABLED_STATE_SET;
            iArr2[i4] = themeAttrColorStateList.getColorForState(iArr[i4], i4);
            iArr[i3] = ThemeUtils.CHECKED_STATE_SET;
            iArr2[i3] = ThemeUtils.getThemeAttrColor(context, R.attr.colorControlActivated);
            iArr[i2] = ThemeUtils.EMPTY_STATE_SET;
            iArr2[i2] = themeAttrColorStateList.getDefaultColor();
        }
        return new ColorStateList(iArr, iArr2);
    }

    static void tintDrawable(Drawable drawable, TintInfo tintInfo, int[] iArr) {
        if (!DrawableUtils.canSafelyMutateDrawable(drawable) || drawable.mutate() == drawable) {
            if (tintInfo.mHasTintList || tintInfo.mHasTintMode) {
                drawable.setColorFilter(createTintFilter(tintInfo.mHasTintList ? tintInfo.mTintList : null, tintInfo.mHasTintMode ? tintInfo.mTintMode : DEFAULT_MODE, iArr));
            } else {
                drawable.clearColorFilter();
            }
            if (VERSION.SDK_INT <= 23) {
                drawable.invalidateSelf();
            }
            return;
        }
        Log.d("AppCompatDrawableManager", "Mutated drawable is not the same instance as the input.");
    }

    private static PorterDuffColorFilter createTintFilter(ColorStateList colorStateList, Mode mode, int[] iArr) {
        return (colorStateList == null || mode == null) ? null : getPorterDuffColorFilter(colorStateList.getColorForState(iArr, 0), mode);
    }

    public static PorterDuffColorFilter getPorterDuffColorFilter(int i, Mode mode) {
        PorterDuffColorFilter porterDuffColorFilter = COLOR_FILTER_CACHE.get(i, mode);
        if (porterDuffColorFilter != null) {
            return porterDuffColorFilter;
        }
        porterDuffColorFilter = new PorterDuffColorFilter(i, mode);
        COLOR_FILTER_CACHE.put(i, mode, porterDuffColorFilter);
        return porterDuffColorFilter;
    }

    private static void setPorterDuffColorFilter(Drawable drawable, int i, Mode mode) {
        if (DrawableUtils.canSafelyMutateDrawable(drawable)) {
            drawable = drawable.mutate();
        }
        if (mode == null) {
            mode = DEFAULT_MODE;
        }
        drawable.setColorFilter(getPorterDuffColorFilter(i, mode));
    }

    private void checkVectorDrawableSetup(@NonNull Context context) {
        if (!this.mHasCheckedVectorDrawableSetup) {
            this.mHasCheckedVectorDrawableSetup = true;
            Drawable drawable = getDrawable(context, R.drawable.abc_vector_test);
            if (drawable == null || !isVectorDrawable(drawable)) {
                this.mHasCheckedVectorDrawableSetup = false;
                throw new IllegalStateException("This app has been built with an incorrect configuration. Please configure your build for VectorDrawableCompat.");
            }
        }
    }

    private static boolean isVectorDrawable(@NonNull Drawable drawable) {
        return (drawable instanceof VectorDrawableCompat) || "android.graphics.drawable.VectorDrawable".equals(drawable.getClass().getName());
    }
}
