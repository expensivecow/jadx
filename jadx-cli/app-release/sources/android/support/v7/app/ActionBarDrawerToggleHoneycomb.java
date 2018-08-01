package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.lang.reflect.Method;

@RequiresApi(11)
class ActionBarDrawerToggleHoneycomb {
    private static final String TAG = "ActionBarDrawerToggleHoneycomb";
    private static final int[] THEME_ATTRS = new int[]{16843531};

    static class SetIndicatorInfo {
        public Method setHomeActionContentDescription;
        public Method setHomeAsUpIndicator;
        public ImageView upIndicatorView;

        SetIndicatorInfo(Activity activity) {
            int i = 0;
            int i2 = 1;
            try {
                Class[] clsArr = new Class[i2];
                clsArr[i] = Drawable.class;
                this.setHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", clsArr);
                clsArr = new Class[i2];
                clsArr[i] = Integer.TYPE;
                this.setHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", clsArr);
            } catch (NoSuchMethodException unused) {
                int i3 = 16908332;
                View findViewById = activity.findViewById(i3);
                if (findViewById != null) {
                    ViewGroup viewGroup = (ViewGroup) findViewById.getParent();
                    if (viewGroup.getChildCount() == 2) {
                        View childAt = viewGroup.getChildAt(i);
                        findViewById = viewGroup.getChildAt(i2);
                        if (childAt.getId() != i3) {
                            findViewById = childAt;
                        }
                        if (findViewById instanceof ImageView) {
                            this.upIndicatorView = (ImageView) findViewById;
                        }
                    }
                }
            }
        }
    }

    ActionBarDrawerToggleHoneycomb() {
    }

    public static SetIndicatorInfo setActionBarUpIndicator(SetIndicatorInfo setIndicatorInfo, Activity activity, Drawable drawable, int i) {
        setIndicatorInfo = new SetIndicatorInfo(activity);
        if (setIndicatorInfo.setHomeAsUpIndicator != null) {
            try {
                ActionBar actionBar = activity.getActionBar();
                Method method = setIndicatorInfo.setHomeAsUpIndicator;
                int i2 = 1;
                Object[] objArr = new Object[i2];
                int i3 = 0;
                objArr[i3] = drawable;
                method.invoke(actionBar, objArr);
                Method method2 = setIndicatorInfo.setHomeActionContentDescription;
                Object[] objArr2 = new Object[i2];
                objArr2[i3] = Integer.valueOf(i);
                method2.invoke(actionBar, objArr2);
            } catch (Throwable e) {
                Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator via JB-MR2 API", e);
            }
        } else if (setIndicatorInfo.upIndicatorView != null) {
            setIndicatorInfo.upIndicatorView.setImageDrawable(drawable);
        } else {
            Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set home-as-up indicator");
        }
        return setIndicatorInfo;
    }

    public static SetIndicatorInfo setActionBarDescription(SetIndicatorInfo setIndicatorInfo, Activity activity, int i) {
        if (setIndicatorInfo == null) {
            setIndicatorInfo = new SetIndicatorInfo(activity);
        }
        if (setIndicatorInfo.setHomeAsUpIndicator != null) {
            try {
                ActionBar actionBar = activity.getActionBar();
                setIndicatorInfo.setHomeActionContentDescription.invoke(actionBar, new Object[]{Integer.valueOf(i)});
                if (VERSION.SDK_INT <= 19) {
                    actionBar.setSubtitle(actionBar.getSubtitle());
                }
            } catch (Throwable e) {
                Log.w("ActionBarDrawerToggleHoneycomb", "Couldn't set content description via JB-MR2 API", e);
            }
        }
        return setIndicatorInfo;
    }

    public static Drawable getThemeUpIndicator(Activity activity) {
        TypedArray obtainStyledAttributes = activity.obtainStyledAttributes(THEME_ATTRS);
        Drawable drawable = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        return drawable;
    }
}
