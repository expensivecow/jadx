package android.support.v7.widget;

import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.graphics.drawable.DrawableWrapper;
import android.util.Log;
import java.lang.reflect.Field;

@RestrictTo({Scope.LIBRARY_GROUP})
public class DrawableUtils {
    public static final Rect INSETS_NONE = new Rect();
    private static final String TAG = "DrawableUtils";
    private static final String VECTOR_DRAWABLE_CLAZZ_NAME = "android.graphics.drawable.VectorDrawable";
    private static Class<?> sInsetsClazz;

    static {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v7.widget.DrawableUtils.<clinit>():void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.core.ProcessClass.lambda$0(ProcessClass.java:51)
	at java.lang.Iterable.forEach(Iterable.java:75)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
        /*
        r0 = new android.graphics.Rect;
        r0.<init>();
        INSETS_NONE = r0;
        r0 = android.os.Build.VERSION.SDK_INT;
        r1 = 18;
        if (r0 < r1) goto L_0x0015;
    L_0x000d:
        r0 = "android.graphics.Insets";	 Catch:{ ClassNotFoundException -> 0x0015 }
        r0 = java.lang.Class.forName(r0);	 Catch:{ ClassNotFoundException -> 0x0015 }
        sInsetsClazz = r0;	 Catch:{ ClassNotFoundException -> 0x0015 }
    L_0x0015:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.DrawableUtils.<clinit>():void");
    }

    private DrawableUtils() {
    }

    public static Rect getOpticalBounds(Drawable drawable) {
        if (sInsetsClazz != null) {
            try {
                drawable = DrawableCompat.unwrap(drawable);
                int i = 0;
                Object invoke = drawable.getClass().getMethod("getOpticalInsets", new Class[i]).invoke(drawable, new Object[i]);
                if (invoke != null) {
                    Rect rect = new Rect();
                    Field[] fields = sInsetsClazz.getFields();
                    int length = fields.length;
                    for (int i2 = i; i2 < length; i2++) {
                        int i3;
                        Field field = fields[i2];
                        String name = field.getName();
                        int i4 = -1;
                        int hashCode = name.hashCode();
                        if (hashCode != -1383228885) {
                            if (hashCode != 115029) {
                                if (hashCode != 3317767) {
                                    if (hashCode == 108511772 && name.equals("right")) {
                                        i3 = 2;
                                        switch (i3) {
                                            case 0:
                                                rect.left = field.getInt(invoke);
                                                break;
                                            case 1:
                                                rect.top = field.getInt(invoke);
                                                break;
                                            case 2:
                                                rect.right = field.getInt(invoke);
                                                break;
                                            case 3:
                                                rect.bottom = field.getInt(invoke);
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                } else if (name.equals("left")) {
                                    i3 = i;
                                    switch (i3) {
                                        case 0:
                                            rect.left = field.getInt(invoke);
                                            break;
                                        case 1:
                                            rect.top = field.getInt(invoke);
                                            break;
                                        case 2:
                                            rect.right = field.getInt(invoke);
                                            break;
                                        case 3:
                                            rect.bottom = field.getInt(invoke);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } else if (name.equals("top")) {
                                i3 = 1;
                                switch (i3) {
                                    case 0:
                                        rect.left = field.getInt(invoke);
                                        break;
                                    case 1:
                                        rect.top = field.getInt(invoke);
                                        break;
                                    case 2:
                                        rect.right = field.getInt(invoke);
                                        break;
                                    case 3:
                                        rect.bottom = field.getInt(invoke);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        } else if (name.equals("bottom")) {
                            i3 = 3;
                            switch (i3) {
                                case 0:
                                    rect.left = field.getInt(invoke);
                                    break;
                                case 1:
                                    rect.top = field.getInt(invoke);
                                    break;
                                case 2:
                                    rect.right = field.getInt(invoke);
                                    break;
                                case 3:
                                    rect.bottom = field.getInt(invoke);
                                    break;
                                default:
                                    break;
                            }
                        }
                        i3 = i4;
                        switch (i3) {
                            case 0:
                                rect.left = field.getInt(invoke);
                                break;
                            case 1:
                                rect.top = field.getInt(invoke);
                                break;
                            case 2:
                                rect.right = field.getInt(invoke);
                                break;
                            case 3:
                                rect.bottom = field.getInt(invoke);
                                break;
                            default:
                                break;
                        }
                    }
                    return rect;
                }
            } catch (Exception unused) {
                Log.e("DrawableUtils", "Couldn't obtain the optical insets. Ignoring.");
            }
        }
        return INSETS_NONE;
    }

    static void fixDrawable(@NonNull Drawable drawable) {
        if (VERSION.SDK_INT == 21 && "android.graphics.drawable.VectorDrawable".equals(drawable.getClass().getName())) {
            fixVectorDrawableTinting(drawable);
        }
    }

    public static boolean canSafelyMutateDrawable(@NonNull Drawable drawable) {
        int i = 15;
        boolean z = false;
        if (VERSION.SDK_INT < i && (drawable instanceof InsetDrawable)) {
            return z;
        }
        if (VERSION.SDK_INT < i && (drawable instanceof GradientDrawable)) {
            return z;
        }
        if (VERSION.SDK_INT < 17 && (drawable instanceof LayerDrawable)) {
            return z;
        }
        if (drawable instanceof DrawableContainer) {
            ConstantState constantState = drawable.getConstantState();
            if (constantState instanceof DrawableContainerState) {
                Drawable[] children = ((DrawableContainerState) constantState).getChildren();
                int length = children.length;
                for (i = z; i < length; i++) {
                    if (!canSafelyMutateDrawable(children[i])) {
                        return z;
                    }
                }
            }
        } else if (drawable instanceof DrawableWrapper) {
            return canSafelyMutateDrawable(((DrawableWrapper) drawable).getWrappedDrawable());
        } else {
            if (drawable instanceof android.support.v7.graphics.drawable.DrawableWrapper) {
                return canSafelyMutateDrawable(((android.support.v7.graphics.drawable.DrawableWrapper) drawable).getWrappedDrawable());
            }
            if (drawable instanceof ScaleDrawable) {
                return canSafelyMutateDrawable(((ScaleDrawable) drawable).getDrawable());
            }
        }
        return true;
    }

    private static void fixVectorDrawableTinting(Drawable drawable) {
        int[] state = drawable.getState();
        if (state == null || state.length == 0) {
            drawable.setState(ThemeUtils.CHECKED_STATE_SET);
        } else {
            drawable.setState(ThemeUtils.EMPTY_STATE_SET);
        }
        drawable.setState(state);
    }

    public static Mode parseTintMode(int i, Mode mode) {
        if (i == 3) {
            return Mode.SRC_OVER;
        }
        if (i == 5) {
            return Mode.SRC_IN;
        }
        if (i == 9) {
            return Mode.SRC_ATOP;
        }
        switch (i) {
            case 14:
                return Mode.MULTIPLY;
            case 15:
                return Mode.SCREEN;
            case 16:
                if (VERSION.SDK_INT >= 11) {
                    mode = Mode.valueOf("ADD");
                }
                return mode;
            default:
                return mode;
        }
    }
}
