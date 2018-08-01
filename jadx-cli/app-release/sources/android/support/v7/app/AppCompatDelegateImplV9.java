package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.StandaloneActionMode;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.Callback;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.ContentFrameLayout.OnAttachListener;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.FitWindowsViewGroup;
import android.support.v7.widget.FitWindowsViewGroup.OnFitSystemWindowsListener;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.support.v7.widget.ViewStubCompat;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;

@RequiresApi(14)
class AppCompatDelegateImplV9 extends AppCompatDelegateImplBase implements Callback, Factory2 {
    private static final boolean IS_PRE_LOLLIPOP = (VERSION.SDK_INT < 21);
    private ActionMenuPresenterCallback mActionMenuPresenterCallback;
    ActionMode mActionMode;
    PopupWindow mActionModePopup;
    ActionBarContextView mActionModeView;
    private AppCompatViewInflater mAppCompatViewInflater;
    private boolean mClosingActionMenu;
    private DecorContentParent mDecorContentParent;
    private boolean mEnableDefaultActionBarUp;
    ViewPropertyAnimatorCompat mFadeAnim = null;
    private boolean mFeatureIndeterminateProgress;
    private boolean mFeatureProgress;
    int mInvalidatePanelMenuFeatures;
    boolean mInvalidatePanelMenuPosted;
    private final Runnable mInvalidatePanelMenuRunnable = new Runnable() {
        public void run() {
            boolean z = false;
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 1) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(z);
            }
            if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 4096) != 0) {
                AppCompatDelegateImplV9.this.doInvalidatePanelMenu(108);
            }
            AppCompatDelegateImplV9.this.mInvalidatePanelMenuPosted = z;
            AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures = z;
        }
    };
    private boolean mLongPressBackDown;
    private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
    private PanelFeatureState[] mPanels;
    private PanelFeatureState mPreparedPanel;
    Runnable mShowActionModePopup;
    private View mStatusGuard;
    private ViewGroup mSubDecor;
    private boolean mSubDecorInstalled;
    private Rect mTempRect1;
    private Rect mTempRect2;
    private TextView mTitleView;

    protected static final class PanelFeatureState {
        int background;
        View createdPanelView;
        ViewGroup decorView;
        int featureId;
        Bundle frozenActionViewState;
        Bundle frozenMenuState;
        int gravity;
        boolean isHandled;
        boolean isOpen;
        boolean isPrepared;
        ListMenuPresenter listMenuPresenter;
        Context listPresenterContext;
        MenuBuilder menu;
        public boolean qwertyMode;
        boolean refreshDecorView = false;
        boolean refreshMenuContent;
        View shownPanelView;
        boolean wasLastOpen;
        int windowAnimations;
        int x;
        int y;

        private static class SavedState implements Parcelable {
            public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
                public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                    return SavedState.readFromParcel(parcel, classLoader);
                }

                public SavedState createFromParcel(Parcel parcel) {
                    return SavedState.readFromParcel(parcel, null);
                }

                public SavedState[] newArray(int i) {
                    return new SavedState[i];
                }
            };
            int featureId;
            boolean isOpen;
            Bundle menuState;

            public int describeContents() {
                return 0;
            }

            SavedState() {
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.featureId);
                parcel.writeInt(this.isOpen);
                if (this.isOpen) {
                    parcel.writeBundle(this.menuState);
                }
            }

            static SavedState readFromParcel(Parcel parcel, ClassLoader classLoader) {
                SavedState savedState = new SavedState();
                savedState.featureId = parcel.readInt();
                boolean z = true;
                if (parcel.readInt() != z) {
                    z = false;
                }
                savedState.isOpen = z;
                if (savedState.isOpen) {
                    savedState.menuState = parcel.readBundle(classLoader);
                }
                return savedState;
            }
        }

        PanelFeatureState(int i) {
            this.featureId = i;
        }

        public boolean hasPanelItems() {
            boolean z = false;
            if (this.shownPanelView == null) {
                return z;
            }
            boolean z2 = true;
            if (this.createdPanelView != null) {
                return z2;
            }
            if (this.listMenuPresenter.getAdapter().getCount() > 0) {
                z = z2;
            }
            return z;
        }

        public void clearMenuPresenters() {
            if (this.menu != null) {
                this.menu.removeMenuPresenter(this.listMenuPresenter);
            }
            this.listMenuPresenter = null;
        }

        void setStyle(Context context) {
            TypedValue typedValue = new TypedValue();
            Theme newTheme = context.getResources().newTheme();
            newTheme.setTo(context.getTheme());
            boolean z = true;
            newTheme.resolveAttribute(R.attr.actionBarPopupTheme, typedValue, z);
            if (typedValue.resourceId != 0) {
                newTheme.applyStyle(typedValue.resourceId, z);
            }
            newTheme.resolveAttribute(R.attr.panelMenuListTheme, typedValue, z);
            if (typedValue.resourceId != 0) {
                newTheme.applyStyle(typedValue.resourceId, z);
            } else {
                newTheme.applyStyle(R.style.Theme_AppCompat_CompactMenu, z);
            }
            int i = 0;
            Context contextThemeWrapper = new ContextThemeWrapper(context, i);
            contextThemeWrapper.getTheme().setTo(newTheme);
            this.listPresenterContext = contextThemeWrapper;
            TypedArray obtainStyledAttributes = contextThemeWrapper.obtainStyledAttributes(R.styleable.AppCompatTheme);
            this.background = obtainStyledAttributes.getResourceId(R.styleable.AppCompatTheme_panelBackground, i);
            this.windowAnimations = obtainStyledAttributes.getResourceId(R.styleable.AppCompatTheme_android_windowAnimationStyle, i);
            obtainStyledAttributes.recycle();
        }

        void setMenu(MenuBuilder menuBuilder) {
            if (menuBuilder != this.menu) {
                if (this.menu != null) {
                    this.menu.removeMenuPresenter(this.listMenuPresenter);
                }
                this.menu = menuBuilder;
                if (!(menuBuilder == null || this.listMenuPresenter == null)) {
                    menuBuilder.addMenuPresenter(this.listMenuPresenter);
                }
            }
        }

        MenuView getListMenuView(MenuPresenter.Callback callback) {
            if (this.menu == null) {
                return null;
            }
            if (this.listMenuPresenter == null) {
                this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, R.layout.abc_list_menu_item_layout);
                this.listMenuPresenter.setCallback(callback);
                this.menu.addMenuPresenter(this.listMenuPresenter);
            }
            return this.listMenuPresenter.getMenuView(this.decorView);
        }

        Parcelable onSaveInstanceState() {
            Parcelable savedState = new SavedState();
            savedState.featureId = this.featureId;
            savedState.isOpen = this.isOpen;
            if (this.menu != null) {
                savedState.menuState = new Bundle();
                this.menu.savePresenterStates(savedState.menuState);
            }
            return savedState;
        }

        void onRestoreInstanceState(Parcelable parcelable) {
            SavedState savedState = (SavedState) parcelable;
            this.featureId = savedState.featureId;
            this.wasLastOpen = savedState.isOpen;
            this.frozenMenuState = savedState.menuState;
            View view = null;
            this.shownPanelView = view;
            this.decorView = view;
        }

        void applyFrozenState() {
            if (this.menu != null && this.frozenMenuState != null) {
                this.menu.restorePresenterStates(this.frozenMenuState);
                this.frozenMenuState = null;
            }
        }
    }

    private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        ActionMenuPresenterCallback() {
        }

        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            Window.Callback windowCallback = AppCompatDelegateImplV9.this.getWindowCallback();
            if (windowCallback != null) {
                windowCallback.onMenuOpened(108, menuBuilder);
            }
            return true;
        }

        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            AppCompatDelegateImplV9.this.checkCloseActionMenu(menuBuilder);
        }
    }

    class ActionModeCallbackWrapperV9 implements ActionMode.Callback {
        private ActionMode.Callback mWrapped;

        public ActionModeCallbackWrapperV9(ActionMode.Callback callback) {
            this.mWrapped = callback;
        }

        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onCreateActionMode(actionMode, menu);
        }

        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return this.mWrapped.onPrepareActionMode(actionMode, menu);
        }

        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            return this.mWrapped.onActionItemClicked(actionMode, menuItem);
        }

        public void onDestroyActionMode(ActionMode actionMode) {
            this.mWrapped.onDestroyActionMode(actionMode);
            if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                AppCompatDelegateImplV9.this.mWindow.getDecorView().removeCallbacks(AppCompatDelegateImplV9.this.mShowActionModePopup);
            }
            if (AppCompatDelegateImplV9.this.mActionModeView != null) {
                AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
                AppCompatDelegateImplV9.this.mFadeAnim = ViewCompat.animate(AppCompatDelegateImplV9.this.mActionModeView).alpha(0.0f);
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                    public void onAnimationEnd(View view) {
                        AppCompatDelegateImplV9.this.mActionModeView.setVisibility(8);
                        if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                            AppCompatDelegateImplV9.this.mActionModePopup.dismiss();
                        } else if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                            ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
                        }
                        AppCompatDelegateImplV9.this.mActionModeView.removeAllViews();
                        Object obj = null;
                        AppCompatDelegateImplV9.this.mFadeAnim.setListener(obj);
                        AppCompatDelegateImplV9.this.mFadeAnim = obj;
                    }
                });
            }
            if (AppCompatDelegateImplV9.this.mAppCompatCallback != null) {
                AppCompatDelegateImplV9.this.mAppCompatCallback.onSupportActionModeFinished(AppCompatDelegateImplV9.this.mActionMode);
            }
            AppCompatDelegateImplV9.this.mActionMode = null;
        }
    }

    private class ListMenuDecorView extends ContentFrameLayout {
        public ListMenuDecorView(Context context) {
            super(context);
        }

        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            return AppCompatDelegateImplV9.this.dispatchKeyEvent(keyEvent) || super.dispatchKeyEvent(keyEvent);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0 || !isOutOfBounds((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            AppCompatDelegateImplV9.this.closePanel(0);
            return true;
        }

        public void setBackgroundResource(int i) {
            setBackgroundDrawable(AppCompatResources.getDrawable(getContext(), i));
        }

        private boolean isOutOfBounds(int i, int i2) {
            int i3 = -5;
            return i < i3 || i2 < i3 || i > getWidth() + 5 || i2 > getHeight() + 5;
        }
    }

    private final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
        PanelMenuPresenterCallback() {
        }

        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
            Menu menuBuilder2;
            Menu rootMenu = menuBuilder2.getRootMenu();
            boolean z2 = true;
            boolean z3 = rootMenu != menuBuilder2 ? z2 : false;
            AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
            if (z3) {
                menuBuilder2 = rootMenu;
            }
            PanelFeatureState findMenuPanel = appCompatDelegateImplV9.findMenuPanel(menuBuilder2);
            if (findMenuPanel == null) {
                return;
            }
            if (z3) {
                AppCompatDelegateImplV9.this.callOnPanelClosed(findMenuPanel.featureId, findMenuPanel, rootMenu);
                AppCompatDelegateImplV9.this.closePanel(findMenuPanel, z2);
                return;
            }
            AppCompatDelegateImplV9.this.closePanel(findMenuPanel, z);
        }

        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            if (menuBuilder == null && AppCompatDelegateImplV9.this.mHasActionBar) {
                Window.Callback windowCallback = AppCompatDelegateImplV9.this.getWindowCallback();
                if (!(windowCallback == null || AppCompatDelegateImplV9.this.isDestroyed())) {
                    windowCallback.onMenuOpened(108, menuBuilder);
                }
            }
            return true;
        }
    }

    void onSubDecorInstalled(ViewGroup viewGroup) {
    }

    AppCompatDelegateImplV9(Context context, Window window, AppCompatCallback appCompatCallback) {
        super(context, window, appCompatCallback);
    }

    public void onCreate(Bundle bundle) {
        if ((this.mOriginalWindowCallback instanceof Activity) && NavUtils.getParentActivityName((Activity) this.mOriginalWindowCallback) != null) {
            ActionBar peekSupportActionBar = peekSupportActionBar();
            boolean z = true;
            if (peekSupportActionBar == null) {
                this.mEnableDefaultActionBarUp = z;
            } else {
                peekSupportActionBar.setDefaultDisplayHomeAsUpEnabled(z);
            }
        }
    }

    public void onPostCreate(Bundle bundle) {
        ensureSubDecor();
    }

    public void initWindowDecorActionBar() {
        ensureSubDecor();
        if (this.mHasActionBar && this.mActionBar == null) {
            if (this.mOriginalWindowCallback instanceof Activity) {
                this.mActionBar = new WindowDecorActionBar((Activity) this.mOriginalWindowCallback, this.mOverlayActionBar);
            } else if (this.mOriginalWindowCallback instanceof Dialog) {
                this.mActionBar = new WindowDecorActionBar((Dialog) this.mOriginalWindowCallback);
            }
            if (this.mActionBar != null) {
                this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
            }
        }
    }

    public void setSupportActionBar(Toolbar toolbar) {
        if (this.mOriginalWindowCallback instanceof Activity) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar instanceof WindowDecorActionBar) {
                throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead.");
            }
            Object obj = null;
            this.mMenuInflater = obj;
            if (supportActionBar != null) {
                supportActionBar.onDestroy();
            }
            if (toolbar != null) {
                supportActionBar = new ToolbarActionBar(toolbar, ((Activity) this.mOriginalWindowCallback).getTitle(), this.mAppCompatWindowCallback);
                this.mActionBar = supportActionBar;
                this.mWindow.setCallback(supportActionBar.getWrappedWindowCallback());
            } else {
                this.mActionBar = obj;
                this.mWindow.setCallback(this.mAppCompatWindowCallback);
            }
            invalidateOptionsMenu();
        }
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int i) {
        ensureSubDecor();
        return this.mWindow.findViewById(i);
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mHasActionBar && this.mSubDecorInstalled) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.onConfigurationChanged(configuration);
            }
        }
        AppCompatDrawableManager.get().onConfigurationChanged(this.mContext);
        applyDayNight();
    }

    public void onStop() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(false);
        }
    }

    public void onPostResume() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setShowHideAnimationEnabled(true);
        }
    }

    public void setContentView(View view) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void setContentView(int i) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        LayoutInflater.from(this.mContext).inflate(i, viewGroup);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void setContentView(View view, LayoutParams layoutParams) {
        ensureSubDecor();
        ViewGroup viewGroup = (ViewGroup) this.mSubDecor.findViewById(16908290);
        viewGroup.removeAllViews();
        viewGroup.addView(view, layoutParams);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void addContentView(View view, LayoutParams layoutParams) {
        ensureSubDecor();
        ((ViewGroup) this.mSubDecor.findViewById(16908290)).addView(view, layoutParams);
        this.mOriginalWindowCallback.onContentChanged();
    }

    public void onDestroy() {
        if (this.mInvalidatePanelMenuPosted) {
            this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
        }
        super.onDestroy();
        if (this.mActionBar != null) {
            this.mActionBar.onDestroy();
        }
    }

    private void ensureSubDecor() {
        if (!this.mSubDecorInstalled) {
            this.mSubDecor = createSubDecor();
            CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                onTitleChanged(title);
            }
            applyFixedSizeWindow();
            onSubDecorInstalled(this.mSubDecor);
            this.mSubDecorInstalled = true;
            boolean z = false;
            PanelFeatureState panelState = getPanelState(z, z);
            if (!isDestroyed()) {
                if (panelState == null || panelState.menu == null) {
                    invalidatePanelMenu(108);
                }
            }
        }
    }

    private ViewGroup createSubDecor() {
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            View view;
            boolean z = false;
            boolean z2 = true;
            if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowNoTitle, z)) {
                requestWindowFeature(z2);
            } else if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionBar, z)) {
                requestWindowFeature(108);
            }
            int i = 109;
            if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionBarOverlay, z)) {
                requestWindowFeature(i);
            }
            if (obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_windowActionModeOverlay, z)) {
                requestWindowFeature(10);
            }
            this.mIsFloating = obtainStyledAttributes.getBoolean(R.styleable.AppCompatTheme_android_windowIsFloating, z);
            obtainStyledAttributes.recycle();
            this.mWindow.getDecorView();
            LayoutInflater from = LayoutInflater.from(this.mContext);
            View view2 = null;
            if (this.mWindowNoTitle) {
                if (this.mOverlayActionMode) {
                    view = (ViewGroup) from.inflate(R.layout.abc_screen_simple_overlay_action_mode, view2);
                } else {
                    view = (ViewGroup) from.inflate(R.layout.abc_screen_simple, view2);
                }
                if (VERSION.SDK_INT >= 21) {
                    ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
                        public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                            int systemWindowInsetTop = windowInsetsCompat.getSystemWindowInsetTop();
                            int updateStatusGuard = AppCompatDelegateImplV9.this.updateStatusGuard(systemWindowInsetTop);
                            if (systemWindowInsetTop != updateStatusGuard) {
                                windowInsetsCompat = windowInsetsCompat.replaceSystemWindowInsets(windowInsetsCompat.getSystemWindowInsetLeft(), updateStatusGuard, windowInsetsCompat.getSystemWindowInsetRight(), windowInsetsCompat.getSystemWindowInsetBottom());
                            }
                            return ViewCompat.onApplyWindowInsets(view, windowInsetsCompat);
                        }
                    });
                } else {
                    ((FitWindowsViewGroup) view).setOnFitSystemWindowsListener(new OnFitSystemWindowsListener() {
                        public void onFitSystemWindows(Rect rect) {
                            rect.top = AppCompatDelegateImplV9.this.updateStatusGuard(rect.top);
                        }
                    });
                }
            } else if (this.mIsFloating) {
                view = (ViewGroup) from.inflate(R.layout.abc_dialog_title_material, view2);
                this.mOverlayActionBar = z;
                this.mHasActionBar = z;
            } else if (this.mHasActionBar) {
                Context contextThemeWrapper;
                TypedValue typedValue = new TypedValue();
                this.mContext.getTheme().resolveAttribute(R.attr.actionBarTheme, typedValue, z2);
                if (typedValue.resourceId != 0) {
                    contextThemeWrapper = new ContextThemeWrapper(this.mContext, typedValue.resourceId);
                } else {
                    contextThemeWrapper = this.mContext;
                }
                view = (ViewGroup) LayoutInflater.from(contextThemeWrapper).inflate(R.layout.abc_screen_toolbar, view2);
                this.mDecorContentParent = (DecorContentParent) view.findViewById(R.id.decor_content_parent);
                this.mDecorContentParent.setWindowCallback(getWindowCallback());
                if (this.mOverlayActionBar) {
                    this.mDecorContentParent.initFeature(i);
                }
                if (this.mFeatureProgress) {
                    this.mDecorContentParent.initFeature(2);
                }
                if (this.mFeatureIndeterminateProgress) {
                    this.mDecorContentParent.initFeature(5);
                }
            } else {
                view = view2;
            }
            if (view == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("AppCompat does not support the current theme features: { windowActionBar: ");
                stringBuilder.append(this.mHasActionBar);
                stringBuilder.append(", windowActionBarOverlay: ");
                stringBuilder.append(this.mOverlayActionBar);
                stringBuilder.append(", android:windowIsFloating: ");
                stringBuilder.append(this.mIsFloating);
                stringBuilder.append(", windowActionModeOverlay: ");
                stringBuilder.append(this.mOverlayActionMode);
                stringBuilder.append(", windowNoTitle: ");
                stringBuilder.append(this.mWindowNoTitle);
                stringBuilder.append(" }");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            if (this.mDecorContentParent == null) {
                this.mTitleView = (TextView) view.findViewById(R.id.title);
            }
            ViewUtils.makeOptionalFitsSystemWindows(view);
            ContentFrameLayout contentFrameLayout = (ContentFrameLayout) view.findViewById(R.id.action_bar_activity_content);
            i = 16908290;
            ViewGroup viewGroup = (ViewGroup) this.mWindow.findViewById(i);
            if (viewGroup != null) {
                while (viewGroup.getChildCount() > 0) {
                    View childAt = viewGroup.getChildAt(z);
                    viewGroup.removeViewAt(z);
                    contentFrameLayout.addView(childAt);
                }
                viewGroup.setId(-1);
                contentFrameLayout.setId(i);
                if (viewGroup instanceof FrameLayout) {
                    ((FrameLayout) viewGroup).setForeground(view2);
                }
            }
            this.mWindow.setContentView(view);
            contentFrameLayout.setAttachListener(new OnAttachListener() {
                public void onAttachedFromWindow() {
                }

                public void onDetachedFromWindow() {
                    AppCompatDelegateImplV9.this.dismissPopups();
                }
            });
            return view;
        }
        obtainStyledAttributes.recycle();
        throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
    }

    private void applyFixedSizeWindow() {
        ContentFrameLayout contentFrameLayout = (ContentFrameLayout) this.mSubDecor.findViewById(16908290);
        View decorView = this.mWindow.getDecorView();
        contentFrameLayout.setDecorPadding(decorView.getPaddingLeft(), decorView.getPaddingTop(), decorView.getPaddingRight(), decorView.getPaddingBottom());
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
        obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowMinWidthMajor, contentFrameLayout.getMinWidthMajor());
        obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowMinWidthMinor, contentFrameLayout.getMinWidthMinor());
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMajor)) {
            obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowFixedWidthMajor, contentFrameLayout.getFixedWidthMajor());
        }
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMinor)) {
            obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowFixedWidthMinor, contentFrameLayout.getFixedWidthMinor());
        }
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMajor)) {
            obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowFixedHeightMajor, contentFrameLayout.getFixedHeightMajor());
        }
        if (obtainStyledAttributes.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMinor)) {
            obtainStyledAttributes.getValue(R.styleable.AppCompatTheme_windowFixedHeightMinor, contentFrameLayout.getFixedHeightMinor());
        }
        obtainStyledAttributes.recycle();
        contentFrameLayout.requestLayout();
    }

    public boolean requestWindowFeature(int i) {
        boolean sanitizeWindowFeatureId = sanitizeWindowFeatureId(i);
        boolean z = false;
        if (this.mWindowNoTitle && sanitizeWindowFeatureId) {
            return z;
        }
        boolean z2 = true;
        if (this.mHasActionBar && sanitizeWindowFeatureId == z2) {
            this.mHasActionBar = z;
        }
        switch (sanitizeWindowFeatureId) {
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mWindowNoTitle = z2;
                return z2;
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureProgress = z2;
                return z2;
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mFeatureIndeterminateProgress = z2;
                return z2;
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionMode = z2;
                return z2;
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mHasActionBar = z2;
                return z2;
            case true:
                throwFeatureRequestIfSubDecorInstalled();
                this.mOverlayActionBar = z2;
                return z2;
            default:
                return this.mWindow.requestFeature(sanitizeWindowFeatureId);
        }
    }

    public boolean hasWindowFeature(int i) {
        switch (sanitizeWindowFeatureId(i)) {
            case 1:
                return this.mWindowNoTitle;
            case 2:
                return this.mFeatureProgress;
            case 5:
                return this.mFeatureIndeterminateProgress;
            case 10:
                return this.mOverlayActionMode;
            case 108:
                return this.mHasActionBar;
            case 109:
                return this.mOverlayActionBar;
            default:
                return false;
        }
    }

    void onTitleChanged(CharSequence charSequence) {
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.setWindowTitle(charSequence);
        } else if (peekSupportActionBar() != null) {
            peekSupportActionBar().setWindowTitle(charSequence);
        } else if (this.mTitleView != null) {
            this.mTitleView.setText(charSequence);
        }
    }

    void onPanelClosed(int i, Menu menu) {
        boolean z = false;
        if (i == 108) {
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.dispatchMenuVisibilityChanged(z);
            }
        } else if (i == 0) {
            PanelFeatureState panelState = getPanelState(i, true);
            if (panelState.isOpen) {
                closePanel(panelState, z);
            }
        }
    }

    boolean onMenuOpened(int i, Menu menu) {
        if (i != 108) {
            return false;
        }
        ActionBar supportActionBar = getSupportActionBar();
        boolean z = true;
        if (supportActionBar != null) {
            supportActionBar.dispatchMenuVisibilityChanged(z);
        }
        return z;
    }

    public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
        Window.Callback windowCallback = getWindowCallback();
        if (!(windowCallback == null || isDestroyed())) {
            PanelFeatureState findMenuPanel = findMenuPanel(menuBuilder.getRootMenu());
            if (findMenuPanel != null) {
                return windowCallback.onMenuItemSelected(findMenuPanel.featureId, menuItem);
            }
        }
        return false;
    }

    public void onMenuModeChange(MenuBuilder menuBuilder) {
        reopenMenu(menuBuilder, true);
    }

    public ActionMode startSupportActionMode(@NonNull ActionMode.Callback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("ActionMode callback can not be null.");
        }
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        ActionMode.Callback actionModeCallbackWrapperV9 = new ActionModeCallbackWrapperV9(callback);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            this.mActionMode = supportActionBar.startActionMode(actionModeCallbackWrapperV9);
            if (!(this.mActionMode == null || this.mAppCompatCallback == null)) {
                this.mAppCompatCallback.onSupportActionModeStarted(this.mActionMode);
            }
        }
        if (this.mActionMode == null) {
            this.mActionMode = startSupportActionModeFromWindow(actionModeCallbackWrapperV9);
        }
        return this.mActionMode;
    }

    public void invalidateOptionsMenu() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar == null || !supportActionBar.invalidateOptionsMenu()) {
            invalidatePanelMenu(0);
        }
    }

    ActionMode startSupportActionModeFromWindow(@NonNull ActionMode.Callback callback) {
        endOnGoingFadeAnimation();
        if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
        if (!(callback instanceof ActionModeCallbackWrapperV9)) {
            callback = new ActionModeCallbackWrapperV9(callback);
        }
        ActionMode actionMode = null;
        if (!(this.mAppCompatCallback == null || isDestroyed())) {
            ActionMode onWindowStartingSupportActionMode;
            try {
                onWindowStartingSupportActionMode = this.mAppCompatCallback.onWindowStartingSupportActionMode(callback);
            } catch (AbstractMethodError unused) {
                onWindowStartingSupportActionMode = actionMode;
            }
            if (onWindowStartingSupportActionMode != null) {
                this.mActionMode = onWindowStartingSupportActionMode;
            } else {
                Context contextThemeWrapper;
                boolean z = false;
                boolean z2 = true;
                if (this.mActionModeView == null) {
                    if (this.mIsFloating) {
                        TypedValue typedValue = new TypedValue();
                        Theme theme = this.mContext.getTheme();
                        theme.resolveAttribute(R.attr.actionBarTheme, typedValue, z2);
                        if (typedValue.resourceId != 0) {
                            Theme newTheme = this.mContext.getResources().newTheme();
                            newTheme.setTo(theme);
                            newTheme.applyStyle(typedValue.resourceId, z2);
                            contextThemeWrapper = new ContextThemeWrapper(this.mContext, (int) z);
                            contextThemeWrapper.getTheme().setTo(newTheme);
                        } else {
                            contextThemeWrapper = this.mContext;
                        }
                        this.mActionModeView = new ActionBarContextView(contextThemeWrapper);
                        this.mActionModePopup = new PopupWindow(contextThemeWrapper, actionMode, R.attr.actionModePopupWindowStyle);
                        PopupWindowCompat.setWindowLayoutType(this.mActionModePopup, 2);
                        this.mActionModePopup.setContentView(this.mActionModeView);
                        this.mActionModePopup.setWidth(-1);
                        contextThemeWrapper.getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, z2);
                        this.mActionModeView.setContentHeight(TypedValue.complexToDimensionPixelSize(typedValue.data, contextThemeWrapper.getResources().getDisplayMetrics()));
                        this.mActionModePopup.setHeight(-2);
                        this.mShowActionModePopup = new Runnable() {
                            public void run() {
                                int i = 0;
                                AppCompatDelegateImplV9.this.mActionModePopup.showAtLocation(AppCompatDelegateImplV9.this.mActionModeView, 55, i, i);
                                AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
                                float f = 1.0f;
                                if (AppCompatDelegateImplV9.this.shouldAnimateActionModeView()) {
                                    AppCompatDelegateImplV9.this.mActionModeView.setAlpha(0.0f);
                                    AppCompatDelegateImplV9.this.mFadeAnim = ViewCompat.animate(AppCompatDelegateImplV9.this.mActionModeView).alpha(f);
                                    AppCompatDelegateImplV9.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                                        public void onAnimationStart(View view) {
                                            AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
                                        }

                                        public void onAnimationEnd(View view) {
                                            AppCompatDelegateImplV9.this.mActionModeView.setAlpha(1.0f);
                                            Object obj = null;
                                            AppCompatDelegateImplV9.this.mFadeAnim.setListener(obj);
                                            AppCompatDelegateImplV9.this.mFadeAnim = obj;
                                        }
                                    });
                                    return;
                                }
                                AppCompatDelegateImplV9.this.mActionModeView.setAlpha(f);
                                AppCompatDelegateImplV9.this.mActionModeView.setVisibility(i);
                            }
                        };
                    } else {
                        ViewStubCompat viewStubCompat = (ViewStubCompat) this.mSubDecor.findViewById(R.id.action_mode_bar_stub);
                        if (viewStubCompat != null) {
                            viewStubCompat.setLayoutInflater(LayoutInflater.from(getActionBarThemedContext()));
                            this.mActionModeView = (ActionBarContextView) viewStubCompat.inflate();
                        }
                    }
                }
                if (this.mActionModeView != null) {
                    endOnGoingFadeAnimation();
                    this.mActionModeView.killMode();
                    contextThemeWrapper = this.mActionModeView.getContext();
                    ActionBarContextView actionBarContextView = this.mActionModeView;
                    if (this.mActionModePopup != null) {
                        z2 = z;
                    }
                    onWindowStartingSupportActionMode = new StandaloneActionMode(contextThemeWrapper, actionBarContextView, callback, z2);
                    if (callback.onCreateActionMode(onWindowStartingSupportActionMode, onWindowStartingSupportActionMode.getMenu())) {
                        onWindowStartingSupportActionMode.invalidate();
                        this.mActionModeView.initForMode(onWindowStartingSupportActionMode);
                        this.mActionMode = onWindowStartingSupportActionMode;
                        float f = 1.0f;
                        if (shouldAnimateActionModeView()) {
                            this.mActionModeView.setAlpha(0.0f);
                            this.mFadeAnim = ViewCompat.animate(this.mActionModeView).alpha(f);
                            this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
                                public void onAnimationStart(View view) {
                                    AppCompatDelegateImplV9.this.mActionModeView.setVisibility(0);
                                    AppCompatDelegateImplV9.this.mActionModeView.sendAccessibilityEvent(32);
                                    if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                                        ViewCompat.requestApplyInsets((View) AppCompatDelegateImplV9.this.mActionModeView.getParent());
                                    }
                                }

                                public void onAnimationEnd(View view) {
                                    AppCompatDelegateImplV9.this.mActionModeView.setAlpha(1.0f);
                                    Object obj = null;
                                    AppCompatDelegateImplV9.this.mFadeAnim.setListener(obj);
                                    AppCompatDelegateImplV9.this.mFadeAnim = obj;
                                }
                            });
                        } else {
                            this.mActionModeView.setAlpha(f);
                            this.mActionModeView.setVisibility(z);
                            this.mActionModeView.sendAccessibilityEvent(32);
                            if (this.mActionModeView.getParent() instanceof View) {
                                ViewCompat.requestApplyInsets((View) this.mActionModeView.getParent());
                            }
                        }
                        if (this.mActionModePopup != null) {
                            this.mWindow.getDecorView().post(this.mShowActionModePopup);
                        }
                    } else {
                        this.mActionMode = actionMode;
                    }
                }
            }
            if (!(this.mActionMode == null || this.mAppCompatCallback == null)) {
                this.mAppCompatCallback.onSupportActionModeStarted(this.mActionMode);
            }
            return this.mActionMode;
        }
    }

    final boolean shouldAnimateActionModeView() {
        return this.mSubDecorInstalled && this.mSubDecor != null && ViewCompat.isLaidOut(this.mSubDecor);
    }

    void endOnGoingFadeAnimation() {
        if (this.mFadeAnim != null) {
            this.mFadeAnim.cancel();
        }
    }

    boolean onBackPressed() {
        boolean z = true;
        if (this.mActionMode != null) {
            this.mActionMode.finish();
            return z;
        }
        ActionBar supportActionBar = getSupportActionBar();
        return (supportActionBar == null || !supportActionBar.collapseActionView()) ? false : z;
    }

    boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        ActionBar supportActionBar = getSupportActionBar();
        boolean z = true;
        if (supportActionBar != null && supportActionBar.onKeyShortcut(i, keyEvent)) {
            return z;
        }
        if (this.mPreparedPanel == null || !performPanelShortcut(this.mPreparedPanel, keyEvent.getKeyCode(), keyEvent, z)) {
            boolean z2 = false;
            if (this.mPreparedPanel == null) {
                PanelFeatureState panelState = getPanelState(z2, z);
                preparePanel(panelState, keyEvent);
                boolean performPanelShortcut = performPanelShortcut(panelState, keyEvent.getKeyCode(), keyEvent, z);
                panelState.isPrepared = z2;
                if (performPanelShortcut) {
                    return z;
                }
            }
            return z2;
        }
        if (this.mPreparedPanel != null) {
            this.mPreparedPanel.isHandled = z;
        }
        return z;
    }

    boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean z = true;
        if (keyEvent.getKeyCode() == 82 && this.mOriginalWindowCallback.dispatchKeyEvent(keyEvent)) {
            return z;
        }
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() != 0) {
            z = false;
        }
        return z ? onKeyDown(keyCode, keyEvent) : onKeyUp(keyCode, keyEvent);
    }

    boolean onKeyUp(int i, KeyEvent keyEvent) {
        boolean z = true;
        boolean z2 = false;
        if (i == 4) {
            boolean z3 = this.mLongPressBackDown;
            this.mLongPressBackDown = z2;
            PanelFeatureState panelState = getPanelState(z2, z2);
            if (panelState != null && panelState.isOpen) {
                if (!z3) {
                    closePanel(panelState, z);
                }
                return z;
            } else if (onBackPressed()) {
                return z;
            }
        } else if (i == 82) {
            onKeyUpPanel(z2, keyEvent);
            return z;
        }
        return z2;
    }

    boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean z = true;
        boolean z2 = false;
        if (i == 4) {
            if ((keyEvent.getFlags() & 128) == 0) {
                z = z2;
            }
            this.mLongPressBackDown = z;
        } else if (i == 82) {
            onKeyDownPanel(z2, keyEvent);
            return z;
        }
        if (VERSION.SDK_INT < 11) {
            onKeyShortcut(i, keyEvent);
        }
        return z2;
    }

    public View createView(View view, String str, @NonNull Context context, @NonNull AttributeSet attributeSet) {
        if (this.mAppCompatViewInflater == null) {
            this.mAppCompatViewInflater = new AppCompatViewInflater();
        }
        boolean z = false;
        if (IS_PRE_LOLLIPOP) {
            boolean z2 = true;
            if (!(attributeSet instanceof XmlPullParser)) {
                z = shouldInheritContext((ViewParent) view);
            } else if (((XmlPullParser) attributeSet).getDepth() > z2) {
                z = z2;
            }
        }
        return this.mAppCompatViewInflater.createView(view, str, context, attributeSet, z, IS_PRE_LOLLIPOP, true, VectorEnabledTintResources.shouldBeUsed());
    }

    private boolean shouldInheritContext(ViewParent viewParent) {
        boolean z = false;
        if (viewParent == null) {
            return z;
        }
        ViewParent decorView = this.mWindow.getDecorView();
        while (viewParent != null) {
            if (viewParent == decorView || !(viewParent instanceof View) || ViewCompat.isAttachedToWindow((View) viewParent)) {
                return z;
            }
            viewParent = viewParent.getParent();
        }
        return true;
    }

    public void installViewFactory() {
        LayoutInflater from = LayoutInflater.from(this.mContext);
        if (from.getFactory() == null) {
            LayoutInflaterCompat.setFactory2(from, this);
        } else if (!(from.getFactory2() instanceof AppCompatDelegateImplV9)) {
            Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's");
        }
    }

    public final View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        View callActivityOnCreateView = callActivityOnCreateView(view, str, context, attributeSet);
        if (callActivityOnCreateView != null) {
            return callActivityOnCreateView;
        }
        return createView(view, str, context, attributeSet);
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView(null, str, context, attributeSet);
    }

    View callActivityOnCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        if (this.mOriginalWindowCallback instanceof Factory) {
            view = ((Factory) this.mOriginalWindowCallback).onCreateView(str, context, attributeSet);
            if (view != null) {
                return view;
            }
        }
        return null;
    }

    private void openPanel(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        if (!panelFeatureState.isOpen && !isDestroyed()) {
            boolean z = false;
            boolean z2 = true;
            if (panelFeatureState.featureId == 0) {
                Context context = this.mContext;
                boolean z3 = (context.getResources().getConfiguration().screenLayout & 15) == 4 ? z2 : z;
                boolean z4 = context.getApplicationInfo().targetSdkVersion >= 11 ? z2 : z;
                if (z3 && z4) {
                    return;
                }
            }
            Window.Callback windowCallback = getWindowCallback();
            if (windowCallback == null || windowCallback.onMenuOpened(panelFeatureState.featureId, panelFeatureState.menu)) {
                WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
                if (windowManager != null && preparePanel(panelFeatureState, keyEvent)) {
                    int i;
                    LayoutParams layoutParams;
                    int i2 = -1;
                    int i3 = -2;
                    LayoutParams layoutParams2;
                    if (panelFeatureState.decorView == null || panelFeatureState.refreshDecorView) {
                        if (panelFeatureState.decorView == null) {
                            if (!initializePanelDecor(panelFeatureState) || panelFeatureState.decorView == null) {
                                return;
                            }
                        } else if (panelFeatureState.refreshDecorView && panelFeatureState.decorView.getChildCount() > 0) {
                            panelFeatureState.decorView.removeAllViews();
                        }
                        if (initializePanelContent(panelFeatureState) && panelFeatureState.hasPanelItems()) {
                            layoutParams2 = panelFeatureState.shownPanelView.getLayoutParams();
                            if (layoutParams2 == null) {
                                layoutParams2 = new LayoutParams(i3, i3);
                            }
                            panelFeatureState.decorView.setBackgroundResource(panelFeatureState.background);
                            ViewParent parent = panelFeatureState.shownPanelView.getParent();
                            if (parent != null && (parent instanceof ViewGroup)) {
                                ((ViewGroup) parent).removeView(panelFeatureState.shownPanelView);
                            }
                            panelFeatureState.decorView.addView(panelFeatureState.shownPanelView, layoutParams2);
                            if (!panelFeatureState.shownPanelView.hasFocus()) {
                                panelFeatureState.shownPanelView.requestFocus();
                            }
                        } else {
                            return;
                        }
                    } else if (panelFeatureState.createdPanelView != null) {
                        layoutParams2 = panelFeatureState.createdPanelView.getLayoutParams();
                        if (layoutParams2 != null && layoutParams2.width == i2) {
                            i = i2;
                            panelFeatureState.isHandled = z;
                            layoutParams = new WindowManager.LayoutParams(i, -2, panelFeatureState.x, panelFeatureState.y, 1002, 8519680, -3);
                            layoutParams.gravity = panelFeatureState.gravity;
                            layoutParams.windowAnimations = panelFeatureState.windowAnimations;
                            windowManager.addView(panelFeatureState.decorView, layoutParams);
                            panelFeatureState.isOpen = z2;
                            return;
                        }
                    }
                    i = i3;
                    panelFeatureState.isHandled = z;
                    layoutParams = new WindowManager.LayoutParams(i, -2, panelFeatureState.x, panelFeatureState.y, 1002, 8519680, -3);
                    layoutParams.gravity = panelFeatureState.gravity;
                    layoutParams.windowAnimations = panelFeatureState.windowAnimations;
                    windowManager.addView(panelFeatureState.decorView, layoutParams);
                    panelFeatureState.isOpen = z2;
                    return;
                }
                return;
            }
            closePanel(panelFeatureState, z2);
        }
    }

    private boolean initializePanelDecor(PanelFeatureState panelFeatureState) {
        panelFeatureState.setStyle(getActionBarThemedContext());
        panelFeatureState.decorView = new ListMenuDecorView(panelFeatureState.listPresenterContext);
        panelFeatureState.gravity = 81;
        return true;
    }

    private void reopenMenu(MenuBuilder menuBuilder, boolean z) {
        boolean z2 = true;
        boolean z3 = false;
        if (this.mDecorContentParent == null || !this.mDecorContentParent.canShowOverflowMenu() || (ViewConfiguration.get(this.mContext).hasPermanentMenuKey() && !this.mDecorContentParent.isOverflowMenuShowPending())) {
            PanelFeatureState panelState = getPanelState(z3, z2);
            panelState.refreshDecorView = z2;
            closePanel(panelState, z3);
            openPanel(panelState, null);
            return;
        }
        Window.Callback windowCallback = getWindowCallback();
        int i = 108;
        if (this.mDecorContentParent.isOverflowMenuShowing() && z) {
            this.mDecorContentParent.hideOverflowMenu();
            if (!isDestroyed()) {
                windowCallback.onPanelClosed(i, getPanelState(z3, z2).menu);
            }
        } else if (!(windowCallback == null || isDestroyed())) {
            if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & z2) != 0) {
                this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
                this.mInvalidatePanelMenuRunnable.run();
            }
            PanelFeatureState panelState2 = getPanelState(z3, z2);
            if (!(panelState2.menu == null || panelState2.refreshMenuContent || !windowCallback.onPreparePanel(z3, panelState2.createdPanelView, panelState2.menu))) {
                windowCallback.onMenuOpened(i, panelState2.menu);
                this.mDecorContentParent.showOverflowMenu();
            }
        }
    }

    private boolean initializePanelMenu(PanelFeatureState panelFeatureState) {
        Context context = this.mContext;
        boolean z = true;
        if ((panelFeatureState.featureId == 0 || panelFeatureState.featureId == 108) && this.mDecorContentParent != null) {
            TypedValue typedValue = new TypedValue();
            Theme theme = context.getTheme();
            theme.resolveAttribute(R.attr.actionBarTheme, typedValue, z);
            Theme theme2 = null;
            if (typedValue.resourceId != 0) {
                theme2 = context.getResources().newTheme();
                theme2.setTo(theme);
                theme2.applyStyle(typedValue.resourceId, z);
                theme2.resolveAttribute(R.attr.actionBarWidgetTheme, typedValue, z);
            } else {
                theme.resolveAttribute(R.attr.actionBarWidgetTheme, typedValue, z);
            }
            if (typedValue.resourceId != 0) {
                if (theme2 == null) {
                    theme2 = context.getResources().newTheme();
                    theme2.setTo(theme);
                }
                theme2.applyStyle(typedValue.resourceId, z);
            }
            if (theme2 != null) {
                Context contextThemeWrapper = new ContextThemeWrapper(context, 0);
                contextThemeWrapper.getTheme().setTo(theme2);
                context = contextThemeWrapper;
            }
        }
        MenuBuilder menuBuilder = new MenuBuilder(context);
        menuBuilder.setCallback(this);
        panelFeatureState.setMenu(menuBuilder);
        return z;
    }

    private boolean initializePanelContent(PanelFeatureState panelFeatureState) {
        boolean z = true;
        if (panelFeatureState.createdPanelView != null) {
            panelFeatureState.shownPanelView = panelFeatureState.createdPanelView;
            return z;
        }
        boolean z2 = false;
        if (panelFeatureState.menu == null) {
            return z2;
        }
        if (this.mPanelMenuPresenterCallback == null) {
            this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback();
        }
        panelFeatureState.shownPanelView = (View) panelFeatureState.getListMenuView(this.mPanelMenuPresenterCallback);
        if (panelFeatureState.shownPanelView == null) {
            z = z2;
        }
        return z;
    }

    private boolean preparePanel(PanelFeatureState panelFeatureState, KeyEvent keyEvent) {
        boolean z = false;
        if (isDestroyed()) {
            return z;
        }
        boolean z2 = true;
        if (panelFeatureState.isPrepared) {
            return z2;
        }
        if (!(this.mPreparedPanel == null || this.mPreparedPanel == panelFeatureState)) {
            closePanel(this.mPreparedPanel, z);
        }
        Window.Callback windowCallback = getWindowCallback();
        if (windowCallback != null) {
            panelFeatureState.createdPanelView = windowCallback.onCreatePanelView(panelFeatureState.featureId);
        }
        boolean z3 = (panelFeatureState.featureId == 0 || panelFeatureState.featureId == 108) ? z2 : z;
        if (z3 && this.mDecorContentParent != null) {
            this.mDecorContentParent.setMenuPrepared();
        }
        if (panelFeatureState.createdPanelView == null && !(z3 && (peekSupportActionBar() instanceof ToolbarActionBar))) {
            Menu menu = null;
            if (panelFeatureState.menu == null || panelFeatureState.refreshMenuContent) {
                if (panelFeatureState.menu == null && (!initializePanelMenu(panelFeatureState) || panelFeatureState.menu == null)) {
                    return z;
                }
                if (z3 && this.mDecorContentParent != null) {
                    if (this.mActionMenuPresenterCallback == null) {
                        this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback();
                    }
                    this.mDecorContentParent.setMenu(panelFeatureState.menu, this.mActionMenuPresenterCallback);
                }
                panelFeatureState.menu.stopDispatchingItemsChanged();
                if (windowCallback.onCreatePanelMenu(panelFeatureState.featureId, panelFeatureState.menu)) {
                    panelFeatureState.refreshMenuContent = z;
                } else {
                    panelFeatureState.setMenu(menu);
                    if (z3 && this.mDecorContentParent != null) {
                        this.mDecorContentParent.setMenu(menu, this.mActionMenuPresenterCallback);
                    }
                    return z;
                }
            }
            panelFeatureState.menu.stopDispatchingItemsChanged();
            if (panelFeatureState.frozenActionViewState != null) {
                panelFeatureState.menu.restoreActionViewStates(panelFeatureState.frozenActionViewState);
                panelFeatureState.frozenActionViewState = menu;
            }
            if (windowCallback.onPreparePanel(z, panelFeatureState.createdPanelView, panelFeatureState.menu)) {
                panelFeatureState.qwertyMode = KeyCharacterMap.load(keyEvent != null ? keyEvent.getDeviceId() : -1).getKeyboardType() != z2 ? z2 : z;
                panelFeatureState.menu.setQwertyMode(panelFeatureState.qwertyMode);
                panelFeatureState.menu.startDispatchingItemsChanged();
            } else {
                if (z3 && this.mDecorContentParent != null) {
                    this.mDecorContentParent.setMenu(menu, this.mActionMenuPresenterCallback);
                }
                panelFeatureState.menu.startDispatchingItemsChanged();
                return z;
            }
        }
        panelFeatureState.isPrepared = z2;
        panelFeatureState.isHandled = z;
        this.mPreparedPanel = panelFeatureState;
        return z2;
    }

    void checkCloseActionMenu(MenuBuilder menuBuilder) {
        if (!this.mClosingActionMenu) {
            this.mClosingActionMenu = true;
            this.mDecorContentParent.dismissPopups();
            Window.Callback windowCallback = getWindowCallback();
            if (!(windowCallback == null || isDestroyed())) {
                windowCallback.onPanelClosed(108, menuBuilder);
            }
            this.mClosingActionMenu = false;
        }
    }

    void closePanel(int i) {
        boolean z = true;
        closePanel(getPanelState(i, z), z);
    }

    void closePanel(PanelFeatureState panelFeatureState, boolean z) {
        if (z && panelFeatureState.featureId == 0 && this.mDecorContentParent != null && this.mDecorContentParent.isOverflowMenuShowing()) {
            checkCloseActionMenu(panelFeatureState.menu);
            return;
        }
        WindowManager windowManager = (WindowManager) this.mContext.getSystemService("window");
        PanelFeatureState panelFeatureState2 = null;
        if (!(windowManager == null || !panelFeatureState.isOpen || panelFeatureState.decorView == null)) {
            windowManager.removeView(panelFeatureState.decorView);
            if (z) {
                callOnPanelClosed(panelFeatureState.featureId, panelFeatureState, panelFeatureState2);
            }
        }
        z = false;
        panelFeatureState.isPrepared = z;
        panelFeatureState.isHandled = z;
        panelFeatureState.isOpen = z;
        panelFeatureState.shownPanelView = panelFeatureState2;
        panelFeatureState.refreshDecorView = true;
        if (this.mPreparedPanel == panelFeatureState) {
            this.mPreparedPanel = panelFeatureState2;
        }
    }

    private boolean onKeyDownPanel(int i, KeyEvent keyEvent) {
        if (keyEvent.getRepeatCount() == 0) {
            PanelFeatureState panelState = getPanelState(i, true);
            if (!panelState.isOpen) {
                return preparePanel(panelState, keyEvent);
            }
        }
        return false;
    }

    private boolean onKeyUpPanel(int i, KeyEvent keyEvent) {
        boolean z = false;
        if (this.mActionMode != null) {
            return z;
        }
        boolean hideOverflowMenu;
        boolean z2 = true;
        PanelFeatureState panelState = getPanelState(i, z2);
        if (i == 0 && this.mDecorContentParent != null && this.mDecorContentParent.canShowOverflowMenu() && !ViewConfiguration.get(this.mContext).hasPermanentMenuKey()) {
            if (this.mDecorContentParent.isOverflowMenuShowing()) {
                hideOverflowMenu = this.mDecorContentParent.hideOverflowMenu();
            } else if (!isDestroyed() && preparePanel(panelState, keyEvent)) {
                hideOverflowMenu = this.mDecorContentParent.showOverflowMenu();
            }
            if (hideOverflowMenu) {
                AudioManager audioManager = (AudioManager) this.mContext.getSystemService("audio");
                if (audioManager != null) {
                    audioManager.playSoundEffect(z);
                } else {
                    Log.w("AppCompatDelegate", "Couldn't get audio manager");
                }
            }
            return hideOverflowMenu;
        } else if (panelState.isOpen || panelState.isHandled) {
            hideOverflowMenu = panelState.isOpen;
            closePanel(panelState, z2);
            if (hideOverflowMenu) {
                AudioManager audioManager2 = (AudioManager) this.mContext.getSystemService("audio");
                if (audioManager2 != null) {
                    audioManager2.playSoundEffect(z);
                } else {
                    Log.w("AppCompatDelegate", "Couldn't get audio manager");
                }
            }
            return hideOverflowMenu;
        } else if (panelState.isPrepared) {
            if (panelState.refreshMenuContent) {
                panelState.isPrepared = z;
                hideOverflowMenu = preparePanel(panelState, keyEvent);
            } else {
                hideOverflowMenu = z2;
            }
            if (hideOverflowMenu) {
                openPanel(panelState, keyEvent);
                hideOverflowMenu = z2;
                if (hideOverflowMenu) {
                    AudioManager audioManager22 = (AudioManager) this.mContext.getSystemService("audio");
                    if (audioManager22 != null) {
                        audioManager22.playSoundEffect(z);
                    } else {
                        Log.w("AppCompatDelegate", "Couldn't get audio manager");
                    }
                }
                return hideOverflowMenu;
            }
        }
        hideOverflowMenu = z;
        if (hideOverflowMenu) {
            AudioManager audioManager222 = (AudioManager) this.mContext.getSystemService("audio");
            if (audioManager222 != null) {
                audioManager222.playSoundEffect(z);
            } else {
                Log.w("AppCompatDelegate", "Couldn't get audio manager");
            }
        }
        return hideOverflowMenu;
    }

    void callOnPanelClosed(int i, PanelFeatureState panelFeatureState, Menu menu) {
        if (menu == null) {
            if (panelFeatureState == null && i >= 0 && i < this.mPanels.length) {
                panelFeatureState = this.mPanels[i];
            }
            if (panelFeatureState != null) {
                menu = panelFeatureState.menu;
            }
        }
        if ((panelFeatureState == null || panelFeatureState.isOpen) && !isDestroyed()) {
            this.mOriginalWindowCallback.onPanelClosed(i, menu);
        }
    }

    PanelFeatureState findMenuPanel(Menu menu) {
        PanelFeatureState[] panelFeatureStateArr = this.mPanels;
        int i = 0;
        int length = panelFeatureStateArr != null ? panelFeatureStateArr.length : i;
        while (i < length) {
            PanelFeatureState panelFeatureState = panelFeatureStateArr[i];
            if (panelFeatureState != null && panelFeatureState.menu == menu) {
                return panelFeatureState;
            }
            i++;
        }
        return null;
    }

    protected PanelFeatureState getPanelState(int i, boolean z) {
        Object obj = this.mPanels;
        if (obj == null || obj.length <= i) {
            Object obj2 = new PanelFeatureState[(i + 1)];
            if (obj != null) {
                int i2 = 0;
                System.arraycopy(obj, i2, obj2, i2, obj.length);
            }
            this.mPanels = obj2;
            obj = obj2;
        }
        PanelFeatureState panelFeatureState = obj[i];
        if (panelFeatureState != null) {
            return panelFeatureState;
        }
        panelFeatureState = new PanelFeatureState(i);
        obj[i] = panelFeatureState;
        return panelFeatureState;
    }

    private boolean performPanelShortcut(PanelFeatureState panelFeatureState, int i, KeyEvent keyEvent, int i2) {
        boolean z = false;
        if (keyEvent.isSystem()) {
            return z;
        }
        if ((panelFeatureState.isPrepared || preparePanel(panelFeatureState, keyEvent)) && panelFeatureState.menu != null) {
            z = panelFeatureState.menu.performShortcut(i, keyEvent, i2);
        }
        if (z) {
            boolean z2 = true;
            if ((i2 & 1) == 0 && this.mDecorContentParent == null) {
                closePanel(panelFeatureState, z2);
            }
        }
        return z;
    }

    private void invalidatePanelMenu(int i) {
        boolean z = true;
        this.mInvalidatePanelMenuFeatures = (z << i) | this.mInvalidatePanelMenuFeatures;
        if (!this.mInvalidatePanelMenuPosted) {
            ViewCompat.postOnAnimation(this.mWindow.getDecorView(), this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuPosted = z;
        }
    }

    void doInvalidatePanelMenu(int i) {
        boolean z = true;
        PanelFeatureState panelState = getPanelState(i, z);
        if (panelState.menu != null) {
            Bundle bundle = new Bundle();
            panelState.menu.saveActionViewStates(bundle);
            if (bundle.size() > 0) {
                panelState.frozenActionViewState = bundle;
            }
            panelState.menu.stopDispatchingItemsChanged();
            panelState.menu.clear();
        }
        panelState.refreshMenuContent = z;
        panelState.refreshDecorView = z;
        if ((i == 108 || i == 0) && this.mDecorContentParent != null) {
            boolean z2 = false;
            PanelFeatureState panelState2 = getPanelState(z2, z2);
            if (panelState2 != null) {
                panelState2.isPrepared = z2;
                preparePanel(panelState2, null);
            }
        }
    }

    int updateStatusGuard(int i) {
        int i2;
        int i3 = 0;
        if (this.mActionModeView == null || !(this.mActionModeView.getLayoutParams() instanceof MarginLayoutParams)) {
            i2 = i3;
        } else {
            int i4;
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.mActionModeView.getLayoutParams();
            i2 = 1;
            if (this.mActionModeView.isShown()) {
                if (this.mTempRect1 == null) {
                    this.mTempRect1 = new Rect();
                    this.mTempRect2 = new Rect();
                }
                Rect rect = this.mTempRect1;
                Rect rect2 = this.mTempRect2;
                rect.set(i3, i, i3, i3);
                ViewUtils.computeFitSystemWindows(this.mSubDecor, rect, rect2);
                if (marginLayoutParams.topMargin != (rect2.top == 0 ? i : i3)) {
                    marginLayoutParams.topMargin = i;
                    if (this.mStatusGuard == null) {
                        this.mStatusGuard = new View(this.mContext);
                        this.mStatusGuard.setBackgroundColor(this.mContext.getResources().getColor(R.color.abc_input_method_navigation_guard));
                        int i5 = -1;
                        this.mSubDecor.addView(this.mStatusGuard, i5, new LayoutParams(i5, i));
                    } else {
                        LayoutParams layoutParams = this.mStatusGuard.getLayoutParams();
                        if (layoutParams.height != i) {
                            layoutParams.height = i;
                            this.mStatusGuard.setLayoutParams(layoutParams);
                        }
                    }
                    i4 = i2;
                } else {
                    i4 = i3;
                }
                if (this.mStatusGuard == null) {
                    i2 = i3;
                }
                if (!(this.mOverlayActionMode || r3 == 0)) {
                    i = i3;
                }
            } else if (marginLayoutParams.topMargin != 0) {
                marginLayoutParams.topMargin = i3;
                i4 = i2;
                i2 = i3;
            } else {
                i4 = i3;
                i2 = i4;
            }
            if (i4 != 0) {
                this.mActionModeView.setLayoutParams(marginLayoutParams);
            }
        }
        if (this.mStatusGuard != null) {
            View view = this.mStatusGuard;
            if (i2 == 0) {
                i3 = 8;
            }
            view.setVisibility(i3);
        }
        return i;
    }

    private void throwFeatureRequestIfSubDecorInstalled() {
        if (this.mSubDecorInstalled) {
            throw new AndroidRuntimeException("Window feature must be requested before adding content");
        }
    }

    private int sanitizeWindowFeatureId(int i) {
        if (i == 8) {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
            return 108;
        } else if (i != 9) {
            return i;
        } else {
            Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
            return 109;
        }
    }

    ViewGroup getSubDecor() {
        return this.mSubDecor;
    }

    void dismissPopups() {
        if (this.mDecorContentParent != null) {
            this.mDecorContentParent.dismissPopups();
        }
        if (this.mActionModePopup != null) {
            this.mWindow.getDecorView().removeCallbacks(this.mShowActionModePopup);
            if (this.mActionModePopup.isShowing()) {
                try {
                    this.mActionModePopup.dismiss();
                } catch (IllegalArgumentException unused) {
                    this.mActionModePopup = null;
                }
            }
        }
        endOnGoingFadeAnimation();
        boolean z = false;
        PanelFeatureState panelState = getPanelState(z, z);
        if (panelState != null && panelState.menu != null) {
            panelState.menu.close();
        }
    }
}
