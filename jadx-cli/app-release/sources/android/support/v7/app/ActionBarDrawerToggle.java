package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class ActionBarDrawerToggle implements DrawerListener {
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    boolean mDrawerIndicatorEnabled;
    private final DrawerLayout mDrawerLayout;
    private boolean mDrawerSlideAnimationEnabled;
    private boolean mHasCustomUpIndicator;
    private Drawable mHomeAsUpIndicator;
    private final int mOpenDrawerContentDescRes;
    private DrawerArrowDrawable mSlider;
    OnClickListener mToolbarNavigationClickListener;
    private boolean mWarnedForDisplayHomeAsUp;

    public interface Delegate {
        Context getActionBarThemedContext();

        Drawable getThemeUpIndicator();

        boolean isNavigationVisible();

        void setActionBarDescription(@StringRes int i);

        void setActionBarUpIndicator(Drawable drawable, @StringRes int i);
    }

    public interface DelegateProvider {
        @Nullable
        Delegate getDrawerToggleDelegate();
    }

    static class DummyDelegate implements Delegate {
        final Activity mActivity;

        public Drawable getThemeUpIndicator() {
            return null;
        }

        public boolean isNavigationVisible() {
            return true;
        }

        public void setActionBarDescription(@StringRes int i) {
        }

        public void setActionBarUpIndicator(Drawable drawable, @StringRes int i) {
        }

        DummyDelegate(Activity activity) {
            this.mActivity = activity;
        }

        public Context getActionBarThemedContext() {
            return this.mActivity;
        }
    }

    @RequiresApi(11)
    private static class HoneycombDelegate implements Delegate {
        final Activity mActivity;
        SetIndicatorInfo mSetIndicatorInfo;

        HoneycombDelegate(Activity activity) {
            this.mActivity = activity;
        }

        public Drawable getThemeUpIndicator() {
            return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(this.mActivity);
        }

        public Context getActionBarThemedContext() {
            return this.mActivity;
        }

        public boolean isNavigationVisible() {
            ActionBar actionBar = this.mActivity.getActionBar();
            return (actionBar == null || (actionBar.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable drawable, int i) {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, drawable, i);
                actionBar.setDisplayShowHomeEnabled(false);
            }
        }

        public void setActionBarDescription(int i) {
            this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, i);
        }
    }

    @RequiresApi(18)
    private static class JellybeanMr2Delegate implements Delegate {
        final Activity mActivity;

        JellybeanMr2Delegate(Activity activity) {
            this.mActivity = activity;
        }

        public Drawable getThemeUpIndicator() {
            Context actionBarThemedContext = getActionBarThemedContext();
            int[] iArr = new int[1];
            int i = 0;
            iArr[i] = 16843531;
            TypedArray obtainStyledAttributes = actionBarThemedContext.obtainStyledAttributes(null, iArr, 16843470, i);
            Drawable drawable = obtainStyledAttributes.getDrawable(i);
            obtainStyledAttributes.recycle();
            return drawable;
        }

        public Context getActionBarThemedContext() {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                return actionBar.getThemedContext();
            }
            return this.mActivity;
        }

        public boolean isNavigationVisible() {
            ActionBar actionBar = this.mActivity.getActionBar();
            return (actionBar == null || (actionBar.getDisplayOptions() & 4) == 0) ? false : true;
        }

        public void setActionBarUpIndicator(Drawable drawable, int i) {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeAsUpIndicator(drawable);
                actionBar.setHomeActionContentDescription(i);
            }
        }

        public void setActionBarDescription(int i) {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                actionBar.setHomeActionContentDescription(i);
            }
        }
    }

    static class ToolbarCompatDelegate implements Delegate {
        final CharSequence mDefaultContentDescription;
        final Drawable mDefaultUpIndicator;
        final Toolbar mToolbar;

        public boolean isNavigationVisible() {
            return true;
        }

        ToolbarCompatDelegate(Toolbar toolbar) {
            this.mToolbar = toolbar;
            this.mDefaultUpIndicator = toolbar.getNavigationIcon();
            this.mDefaultContentDescription = toolbar.getNavigationContentDescription();
        }

        public void setActionBarUpIndicator(Drawable drawable, @StringRes int i) {
            this.mToolbar.setNavigationIcon(drawable);
            setActionBarDescription(i);
        }

        public void setActionBarDescription(@StringRes int i) {
            if (i == 0) {
                this.mToolbar.setNavigationContentDescription(this.mDefaultContentDescription);
            } else {
                this.mToolbar.setNavigationContentDescription(i);
            }
        }

        public Drawable getThemeUpIndicator() {
            return this.mDefaultUpIndicator;
        }

        public Context getActionBarThemedContext() {
            return this.mToolbar.getContext();
        }
    }

    @RequiresApi(14)
    private static class IcsDelegate extends HoneycombDelegate {
        IcsDelegate(Activity activity) {
            super(activity);
        }

        public Context getActionBarThemedContext() {
            ActionBar actionBar = this.mActivity.getActionBar();
            if (actionBar != null) {
                return actionBar.getThemedContext();
            }
            return this.mActivity;
        }
    }

    public void onDrawerStateChanged(int i) {
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, @StringRes int i, @StringRes int i2) {
        this(activity, null, drawerLayout, null, i, i2);
    }

    public ActionBarDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, @StringRes int i, @StringRes int i2) {
        this(activity, toolbar, drawerLayout, null, i, i2);
    }

    ActionBarDrawerToggle(Activity activity, Toolbar toolbar, DrawerLayout drawerLayout, DrawerArrowDrawable drawerArrowDrawable, @StringRes int i, @StringRes int i2) {
        boolean z = true;
        this.mDrawerSlideAnimationEnabled = z;
        this.mDrawerIndicatorEnabled = z;
        this.mWarnedForDisplayHomeAsUp = false;
        if (toolbar != null) {
            this.mActivityImpl = new ToolbarCompatDelegate(toolbar);
            toolbar.setNavigationOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (ActionBarDrawerToggle.this.mDrawerIndicatorEnabled) {
                        ActionBarDrawerToggle.this.toggle();
                    } else if (ActionBarDrawerToggle.this.mToolbarNavigationClickListener != null) {
                        ActionBarDrawerToggle.this.mToolbarNavigationClickListener.onClick(view);
                    }
                }
            });
        } else if (activity instanceof DelegateProvider) {
            this.mActivityImpl = ((DelegateProvider) activity).getDrawerToggleDelegate();
        } else if (VERSION.SDK_INT >= 18) {
            this.mActivityImpl = new JellybeanMr2Delegate(activity);
        } else if (VERSION.SDK_INT >= 14) {
            this.mActivityImpl = new IcsDelegate(activity);
        } else if (VERSION.SDK_INT >= 11) {
            this.mActivityImpl = new HoneycombDelegate(activity);
        } else {
            this.mActivityImpl = new DummyDelegate(activity);
        }
        this.mDrawerLayout = drawerLayout;
        this.mOpenDrawerContentDescRes = i;
        this.mCloseDrawerContentDescRes = i2;
        if (drawerArrowDrawable == null) {
            this.mSlider = new DrawerArrowDrawable(this.mActivityImpl.getActionBarThemedContext());
        } else {
            this.mSlider = drawerArrowDrawable;
        }
        this.mHomeAsUpIndicator = getThemeUpIndicator();
    }

    public void syncState() {
        int i = 8388611;
        if (this.mDrawerLayout.isDrawerOpen(i)) {
            setPosition(1.0f);
        } else {
            setPosition(0.0f);
        }
        if (this.mDrawerIndicatorEnabled) {
            setActionBarUpIndicator(this.mSlider, this.mDrawerLayout.isDrawerOpen(i) ? this.mCloseDrawerContentDescRes : this.mOpenDrawerContentDescRes);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (!this.mHasCustomUpIndicator) {
            this.mHomeAsUpIndicator = getThemeUpIndicator();
        }
        syncState();
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem == null || menuItem.getItemId() != 16908332 || !this.mDrawerIndicatorEnabled) {
            return false;
        }
        toggle();
        return true;
    }

    void toggle() {
        int i = 8388611;
        int drawerLockMode = this.mDrawerLayout.getDrawerLockMode(i);
        if (this.mDrawerLayout.isDrawerVisible(i) && drawerLockMode != 2) {
            this.mDrawerLayout.closeDrawer(i);
        } else if (drawerLockMode != 1) {
            this.mDrawerLayout.openDrawer(i);
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
        setHomeAsUpIndicator(i != 0 ? this.mDrawerLayout.getResources().getDrawable(i) : null);
    }

    public boolean isDrawerIndicatorEnabled() {
        return this.mDrawerIndicatorEnabled;
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

    @NonNull
    public DrawerArrowDrawable getDrawerArrowDrawable() {
        return this.mSlider;
    }

    public void setDrawerArrowDrawable(@NonNull DrawerArrowDrawable drawerArrowDrawable) {
        this.mSlider = drawerArrowDrawable;
        syncState();
    }

    public void setDrawerSlideAnimationEnabled(boolean z) {
        this.mDrawerSlideAnimationEnabled = z;
        if (!z) {
            setPosition(0.0f);
        }
    }

    public boolean isDrawerSlideAnimationEnabled() {
        return this.mDrawerSlideAnimationEnabled;
    }

    public void onDrawerSlide(View view, float f) {
        float f2 = 0.0f;
        if (this.mDrawerSlideAnimationEnabled) {
            setPosition(Math.min(1.0f, Math.max(f2, f)));
        } else {
            setPosition(f2);
        }
    }

    public void onDrawerOpened(View view) {
        setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }

    public void onDrawerClosed(View view) {
        setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }

    public OnClickListener getToolbarNavigationClickListener() {
        return this.mToolbarNavigationClickListener;
    }

    public void setToolbarNavigationClickListener(OnClickListener onClickListener) {
        this.mToolbarNavigationClickListener = onClickListener;
    }

    void setActionBarUpIndicator(Drawable drawable, int i) {
        if (!(this.mWarnedForDisplayHomeAsUp || this.mActivityImpl.isNavigationVisible())) {
            Log.w("ActionBarDrawerToggle", "DrawerToggle may not show up because NavigationIcon is not visible. You may need to call actionbar.setDisplayHomeAsUpEnabled(true);");
            this.mWarnedForDisplayHomeAsUp = true;
        }
        this.mActivityImpl.setActionBarUpIndicator(drawable, i);
    }

    void setActionBarDescription(int i) {
        this.mActivityImpl.setActionBarDescription(i);
    }

    Drawable getThemeUpIndicator() {
        return this.mActivityImpl.getThemeUpIndicator();
    }

    private void setPosition(float f) {
        if (f == 1.0f) {
            this.mSlider.setVerticalMirror(true);
        } else if (f == 0.0f) {
            this.mSlider.setVerticalMirror(false);
        }
        this.mSlider.setProgress(f);
    }
}
