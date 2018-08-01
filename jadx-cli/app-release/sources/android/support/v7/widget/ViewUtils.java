package android.support.v7.widget;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ViewUtils {
    private static final String TAG = "ViewUtils";
    private static Method sComputeFitSystemWindowsMethod;

    static {
        if (VERSION.SDK_INT >= 18) {
            try {
                Class[] clsArr = new Class[2];
                clsArr[0] = Rect.class;
                boolean z = true;
                clsArr[z] = Rect.class;
                sComputeFitSystemWindowsMethod = View.class.getDeclaredMethod("computeFitSystemWindows", clsArr);
                if (!sComputeFitSystemWindowsMethod.isAccessible()) {
                    sComputeFitSystemWindowsMethod.setAccessible(z);
                }
            } catch (NoSuchMethodException unused) {
                Log.d("ViewUtils", "Could not find method computeFitSystemWindows. Oh well.");
            }
        }
    }

    private ViewUtils() {
    }

    public static boolean isLayoutRtl(View view) {
        boolean z = true;
        return ViewCompat.getLayoutDirection(view) == z ? z : false;
    }

    public static void computeFitSystemWindows(View view, Rect rect, Rect rect2) {
        if (sComputeFitSystemWindowsMethod != null) {
            try {
                sComputeFitSystemWindowsMethod.invoke(view, new Object[]{rect, rect2});
            } catch (Throwable e) {
                Log.d("ViewUtils", "Could not invoke computeFitSystemWindows", e);
            }
        }
    }

    public static void makeOptionalFitsSystemWindows(View view) {
        if (VERSION.SDK_INT >= 16) {
            try {
                int i = 0;
                Method method = view.getClass().getMethod("makeOptionalFitsSystemWindows", new Class[i]);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                method.invoke(view, new Object[i]);
            } catch (NoSuchMethodException unused) {
                Log.d("ViewUtils", "Could not find method makeOptionalFitsSystemWindows. Oh well...");
            } catch (Throwable e) {
                Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", e);
            } catch (Throwable e2) {
                Log.d("ViewUtils", "Could not invoke makeOptionalFitsSystemWindows", e2);
            }
        }
    }
}
