package android.support.v4.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.InsetDrawable;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.lang.reflect.Method;

@Deprecated
public class ActionBarDrawerToggle implements DrawerListener {
    private static final int ID_HOME = 16908332;
    private static final String TAG = "ActionBarDrawerToggle";
    private static final int[] THEME_ATTRS = new int[]{16843531};
    private static final float TOGGLE_DRAWABLE_OFFSET = 0.33333334f;
    final Activity mActivity;
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    private Drawable mDrawerImage;
    private final int mDrawerImageResource;
    private boolean mDrawerIndicatorEnabled;
    private final DrawerLayout mDrawerLayout;
    private boolean mHasCustomUpIndicator;
    private Drawable mHomeAsUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private SetIndicatorInfo mSetIndicatorInfo;
    private SlideDrawable mSlider;

    @Deprecated
    public interface Delegate {
        @Nullable
        Drawable getThemeUpIndicator();

        void setActionBarDescription(@StringRes int i);

        void setActionBarUpIndicator(Drawable drawable, @StringRes int i);
    }

    @Deprecated
    public interface DelegateProvider {
        @Nullable
        Delegate getDrawerToggleDelegate();
    }

    private static class SetIndicatorInfo {
        Method mSetHomeActionContentDescription;
        Method mSetHomeAsUpIndicator;
        ImageView mUpIndicatorView;

        SetIndicatorInfo(Activity activity) {
            int i = 0;
            int i2 = 1;
            try {
                Class[] clsArr = new Class[i2];
                clsArr[i] = Drawable.class;
                this.mSetHomeAsUpIndicator = ActionBar.class.getDeclaredMethod("setHomeAsUpIndicator", clsArr);
                clsArr = new Class[i2];
                clsArr[i] = Integer.TYPE;
                this.mSetHomeActionContentDescription = ActionBar.class.getDeclaredMethod("setHomeActionContentDescription", clsArr);
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
                            this.mUpIndicatorView = (ImageView) findViewById;
                        }
                    }
                }
            }
        }
    }

    private class SlideDrawable extends InsetDrawable implements Callback {
        private final boolean mHasMirroring;
        private float mOffset;
        private float mPosition;
        private final Rect mTmpRect;

        SlideDrawable(Drawable drawable) {
            boolean z = false;
            super(drawable, z);
            if (VERSION.SDK_INT > 18) {
                z = true;
            }
            this.mHasMirroring = z;
            this.mTmpRect = new Rect();
        }

        public void setPosition(float f) {
            this.mPosition = f;
            invalidateSelf();
        }

        public float getPosition() {
            return this.mPosition;
        }

        public void setOffset(float f) {
            this.mOffset = f;
            invalidateSelf();
        }

        public void draw(@NonNull Canvas canvas) {
            copyBounds(this.mTmpRect);
            canvas.save();
            int i = 1;
            int i2 = ViewCompat.getLayoutDirection(ActionBarDrawerToggle.this.mActivity.getWindow().getDecorView()) == i ? i : 0;
            if (i2 != 0) {
                i = -1;
            }
            float width = (float) this.mTmpRect.width();
            float f = (((-this.mOffset) * width) * this.mPosition) * ((float) i);
            float f2 = 0.0f;
            canvas.translate(f, f2);
            if (!(i2 == 0 || this.mHasMirroring)) {
                canvas.translate(width, f2);
                canvas.scale(-1.0f, 1.0f);
            }
            super.draw(canvas);
            canvas.restore();
        }
    }

    public void onDrawerStateChanged(int i) {
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, @DrawableRes int i, @StringRes int i2, @StringRes int i3) {
        this(activity, drawerLayout, assumeMaterial(activity) ^ 1, i, i2, i3);
    }

    private static boolean assumeMaterial(Context context) {
        int i = 21;
        return context.getApplicationInfo().targetSdkVersion >= i && VERSION.SDK_INT >= i;
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, boolean z, @DrawableRes int i, @StringRes int i2, @StringRes int i3) {
        this.mDrawerIndicatorEnabled = true;
        this.mActivity = activity;
        if (activity instanceof DelegateProvider) {
            this.mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else {
            this.mActivityImpl = null;
        }
        this.mDrawerLayout = drawerLayout;
        this.mDrawerImageResource = i;
        this.mOpenDrawerContentDescRes = i2;
        this.mCloseDrawerContentDescRes = i3;
        this.mHomeAsUpIndicator = getThemeUpIndicator();
        this.mDrawerImage = ContextCompat.getDrawable(activity, i);
        this.mSlider = new SlideDrawable(this.mDrawerImage);
        this.mSlider.setOffset(z ? 0.33333334f : 0.0f);
    }

    public void syncState() {
        int i = 8388611;
        if (this.mDrawerLayout.isDrawerOpen(i)) {
            this.mSlider.setPosition(1.0f);
        } else {
            this.mSlider.setPosition(0.0f);
        }
        if (this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(this.mSlider, this.mDrawerLayout.isDrawerOpen(i) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
        }
    }

    public void setHomeAsUpIndicator(Drawable drawable) {
        boolean z = false;
        if (drawable == null) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
            this.mHasCustomUpIndicator = z;
        } else {
            this.mHomeAsUpIndicator = drawable;
            this.mHasCustomUpIndicator = true;
        }
        if (!this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(this.mHomeAsUpIndicator, z);
        }
    }

    public void setHomeAsUpIndicator(int i) {
        setHomeAsUpIndicator(i != 0 ? ContextCompat.getDrawable(this.mActivity, i) : null);
    }

    public void setDrawerIndicatorEnabled(boolean z) {
        if (z != this.mDrawerIndicatorEnabled) {
            if (z) {
                setActionBarUpIndicator(this.mSlider, this.mDrawerLayout.isDrawerOpen(8388611) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
            } else {
                setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
            }
            this.mDrawerIndicatorEnabled = z;
        }
    }

    public boolean isDrawerIndicatorEnabled() {
        return this.mDrawerIndicatorEnabled;
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.mHasCustomUpIndicator) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
        }
        this.mDrawerImage = ContextCompat.getDrawable(this.mActivity, this.mDrawerImageResource);
        syncState();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == null || menuItem.getItemId() != 16908332 || !this.mDrawerIndicatorEnabled) {
            return false;
        }
        int i = 8388611;
        if (this.mDrawerLayout.isDrawerVisible(i)) {
            this.mDrawerLayout.closeDrawer(i);
        } else {
            this.mDrawerLayout.openDrawer(i);
        }
        return true;
    }

    public void onDrawerSlide(View view, float f) {
        float position = this.mSlider.getPosition();
        float f2 = 0.5f;
        float f3 = 2.0f;
        if (f > f2) {
            position = Math.max(position, Math.max(0.0f, f - f2) * f3);
        } else {
            position = Math.min(position, f * f3);
        }
        this.mSlider.setPosition(position);
    }

    public void onDrawerOpened(View view) {
        this.mSlider.setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }

    public void onDrawerClosed(View view) {
        this.mSlider.setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }

    private Drawable getThemeUpIndicator() {
        if (this.mActivityImpl != null) {
            return this.mActivityImpl.getThemeUpIndicator();
        }
        int i = 0;
        TypedArray obtainStyledAttributes;
        Drawable drawable;
        if (VERSION.SDK_INT >= 18) {
            Context themedContext;
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                themedContext = actionBar.getThemedContext();
            } else {
                themedContext = this.mActivity;
            }
            obtainStyledAttributes = themedContext.obtainStyledAttributes(null, THEME_ATTRS, 16843470, i);
            drawable = obtainStyledAttributes.getDrawable(i);
            obtainStyledAttributes.recycle();
            return drawable;
        }
        obtainStyledAttributes = this.mActivity.obtainStyledAttributes(THEME_ATTRS);
        drawable = obtainStyledAttributes.getDrawable(i);
        obtainStyledAttributes.recycle();
        return drawable;
    }

    private void setActionBarUpIndicator(Drawable drawable, int i) {
        if (this.mActivityImpl != null) {
            this.mActivityImpl.setActionBarUpIndicator(drawable, i);
            return;
        }
        ActionBar actionBar;
        if (VERSION.SDK_INT >= 18) {
            actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(i);
            }
        } else {
            if (this.mSetIndicatorInfo == null) {
                this.mSetIndicatorInfo = new SetIndicatorInfo(this.mActivity);
            }
            if (this.mSetIndicatorInfo.mSetHomeAsUpIndicator != null) {
                try {
                    actionBar = this.mActivity.getActionBar();
                    Method method = this.mSetIndicatorInfo.mSetHomeAsUpIndicator;
                    int i2 = 1;
                    Object[] objArr = new Object[i2];
                    int i3 = 0;
                    objArr[i3] = drawable;
                    method.invoke(actionBar, objArr);
                    Method method2 = this.mSetIndicatorInfo.mSetHomeActionContentDescription;
                    Object[] objArr2 = new Object[i2];
                    objArr2[i3] = Integer.valueOf(i);
                    method2.invoke(actionBar, objArr2);
                } catch (Throwable e) {
                    Log.w("ActionBarDrawerToggle", "Couldn't set home-as-up indicator via JB-MR2 API", e);
                }
            } else if (this.mSetIndicatorInfo.mUpIndicatorView != null) {
                this.mSetIndicatorInfo.mUpIndicatorView.setImageDrawable(drawable);
            } else {
                Log.w("ActionBarDrawerToggle", "Couldn't set home-as-up indicator");
            }
        }
    }

    private void setActionBarDescription(int i) {
        if (this.mActivityImpl != null) {
            this.mActivityImpl.setActionBarDescription(i);
            return;
        }
        ActionBar actionBar;
        if (VERSION.SDK_INT >= 18) {
            actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeActionContentDescription(i);
            }
        } else {
            if (this.mSetIndicatorInfo == null) {
                this.mSetIndicatorInfo = new SetIndicatorInfo(this.mActivity);
            }
            if (this.mSetIndicatorInfo.mSetHomeAsUpIndicator != null) {
                try {
                    actionBar = this.mActivity.getActionBar();
                    this.mSetIndicatorInfo.mSetHomeActionContentDescription.invoke(actionBar, new Object[]{Integer.valueOf(i)});
                    actionBar.setSubtitle(actionBar.getSubtitle());
                } catch (Throwable e) {
                    Log.w("ActionBarDrawerToggle", "Couldn't set content description via JB-MR2 API", e);
                }
            }
        }
    }
}
