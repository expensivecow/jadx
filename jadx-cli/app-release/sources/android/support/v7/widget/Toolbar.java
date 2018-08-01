package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MarginLayoutParamsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.CollapsibleActionView;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.support.v7.view.menu.SubMenuBuilder;
import android.text.Layout;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class Toolbar extends ViewGroup {
    private static final String TAG = "Toolbar";
    private Callback mActionMenuPresenterCallback;
    int mButtonGravity;
    ImageButton mCollapseButtonView;
    private CharSequence mCollapseDescription;
    private Drawable mCollapseIcon;
    private boolean mCollapsible;
    private int mContentInsetEndWithActions;
    private int mContentInsetStartWithNavigation;
    private RtlSpacingHelper mContentInsets;
    private boolean mEatingHover;
    private boolean mEatingTouch;
    View mExpandedActionView;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private int mGravity;
    private final ArrayList<View> mHiddenViews;
    private ImageView mLogoView;
    private int mMaxButtonHeight;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private ActionMenuView mMenuView;
    private final android.support.v7.widget.ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener;
    private ImageButton mNavButtonView;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private ActionMenuPresenter mOuterActionMenuPresenter;
    private Context mPopupContext;
    private int mPopupTheme;
    private final Runnable mShowOverflowMenuRunnable;
    private CharSequence mSubtitleText;
    private int mSubtitleTextAppearance;
    private int mSubtitleTextColor;
    private TextView mSubtitleTextView;
    private final int[] mTempMargins;
    private final ArrayList<View> mTempViews;
    private int mTitleMarginBottom;
    private int mTitleMarginEnd;
    private int mTitleMarginStart;
    private int mTitleMarginTop;
    private CharSequence mTitleText;
    private int mTitleTextAppearance;
    private int mTitleTextColor;
    private TextView mTitleTextView;
    private ToolbarWidgetWrapper mWrapper;

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    private class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuItemImpl mCurrentExpandedItem;
        MenuBuilder mMenu;

        public boolean flagActionItems() {
            return false;
        }

        public int getId() {
            return 0;
        }

        public MenuView getMenuView(ViewGroup viewGroup) {
            return null;
        }

        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        public void onRestoreInstanceState(Parcelable parcelable) {
        }

        public Parcelable onSaveInstanceState() {
            return null;
        }

        public boolean onSubMenuSelected(SubMenuBuilder subMenuBuilder) {
            return false;
        }

        public void setCallback(Callback callback) {
        }

        ExpandedActionViewMenuPresenter() {
        }

        public void initForMenu(Context context, MenuBuilder menuBuilder) {
            if (!(this.mMenu == null || this.mCurrentExpandedItem == null)) {
                this.mMenu.collapseItemActionView(this.mCurrentExpandedItem);
            }
            this.mMenu = menuBuilder;
        }

        public void updateMenuView(boolean z) {
            if (this.mCurrentExpandedItem != null) {
                int i = 0;
                if (this.mMenu != null) {
                    int size = this.mMenu.size();
                    for (int i2 = i; i2 < size; i2++) {
                        if (this.mMenu.getItem(i2) == this.mCurrentExpandedItem) {
                            i = 1;
                            break;
                        }
                    }
                }
                if (i == 0) {
                    collapseItemActionView(this.mMenu, this.mCurrentExpandedItem);
                }
            }
        }

        public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            Toolbar.this.ensureCollapseButtonView();
            if (Toolbar.this.mCollapseButtonView.getParent() != Toolbar.this) {
                Toolbar.this.addView(Toolbar.this.mCollapseButtonView);
            }
            Toolbar.this.mExpandedActionView = menuItemImpl.getActionView();
            this.mCurrentExpandedItem = menuItemImpl;
            if (Toolbar.this.mExpandedActionView.getParent() != Toolbar.this) {
                android.view.ViewGroup.LayoutParams generateDefaultLayoutParams = Toolbar.this.generateDefaultLayoutParams();
                generateDefaultLayoutParams.gravity = 8388611 | (Toolbar.this.mButtonGravity & 112);
                generateDefaultLayoutParams.mViewType = 2;
                Toolbar.this.mExpandedActionView.setLayoutParams(generateDefaultLayoutParams);
                Toolbar.this.addView(Toolbar.this.mExpandedActionView);
            }
            Toolbar.this.removeChildrenForExpandedActionView();
            Toolbar.this.requestLayout();
            boolean z = true;
            menuItemImpl.setActionViewExpanded(z);
            if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) Toolbar.this.mExpandedActionView).onActionViewExpanded();
            }
            return z;
        }

        public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
            if (Toolbar.this.mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) Toolbar.this.mExpandedActionView).onActionViewCollapsed();
            }
            Toolbar.this.removeView(Toolbar.this.mExpandedActionView);
            Toolbar.this.removeView(Toolbar.this.mCollapseButtonView);
            Object obj = null;
            Toolbar.this.mExpandedActionView = obj;
            Toolbar.this.addChildrenForExpandedActionView();
            this.mCurrentExpandedItem = obj;
            Toolbar.this.requestLayout();
            menuItemImpl.setActionViewExpanded(false);
            return true;
        }
    }

    public static class LayoutParams extends android.support.v7.app.ActionBar.LayoutParams {
        static final int CUSTOM = 0;
        static final int EXPANDED = 2;
        static final int SYSTEM = 1;
        int mViewType;

        public LayoutParams(@NonNull Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mViewType = 0;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.mViewType = 0;
            this.gravity = 8388627;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2);
            this.mViewType = 0;
            this.gravity = i3;
        }

        public LayoutParams(int i) {
            this(-2, -1, i);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((android.support.v7.app.ActionBar.LayoutParams) layoutParams);
            this.mViewType = 0;
            this.mViewType = layoutParams.mViewType;
        }

        public LayoutParams(android.support.v7.app.ActionBar.LayoutParams layoutParams) {
            super(layoutParams);
            this.mViewType = 0;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super((android.view.ViewGroup.LayoutParams) marginLayoutParams);
            this.mViewType = 0;
            copyMarginsFromCompat(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mViewType = 0;
        }

        void copyMarginsFromCompat(MarginLayoutParams marginLayoutParams) {
            this.leftMargin = marginLayoutParams.leftMargin;
            this.topMargin = marginLayoutParams.topMargin;
            this.rightMargin = marginLayoutParams.rightMargin;
            this.bottomMargin = marginLayoutParams.bottomMargin;
        }
    }

    public static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int expandedMenuItemId;
        boolean isOverflowOpen;

        public SavedState(Parcel parcel) {
            this(parcel, null);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.expandedMenuItemId = parcel.readInt();
            this.isOverflowOpen = parcel.readInt() != 0;
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.expandedMenuItemId);
            parcel.writeInt(this.isOverflowOpen);
        }
    }

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.toolbarStyle);
    }

    public Toolbar(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mGravity = 8388627;
        this.mTempViews = new ArrayList();
        this.mHiddenViews = new ArrayList();
        this.mTempMargins = new int[2];
        this.mMenuViewItemClickListener = new android.support.v7.widget.ActionMenuView.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem menuItem) {
                return Toolbar.this.mOnMenuItemClickListener != null ? Toolbar.this.mOnMenuItemClickListener.onMenuItemClick(menuItem) : false;
            }
        };
        this.mShowOverflowMenuRunnable = new Runnable() {
            public void run() {
                Toolbar.this.showOverflowMenu();
            }
        };
        int i2 = 0;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(getContext(), attributeSet, R.styleable.Toolbar, i, i2);
        this.mTitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_titleTextAppearance, i2);
        this.mSubtitleTextAppearance = obtainStyledAttributes.getResourceId(R.styleable.Toolbar_subtitleTextAppearance, i2);
        this.mGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_android_gravity, this.mGravity);
        this.mButtonGravity = obtainStyledAttributes.getInteger(R.styleable.Toolbar_buttonGravity, 48);
        int dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMargin, i2);
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_titleMargins)) {
            dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMargins, dimensionPixelOffset);
        }
        this.mTitleMarginBottom = dimensionPixelOffset;
        this.mTitleMarginTop = dimensionPixelOffset;
        this.mTitleMarginEnd = dimensionPixelOffset;
        this.mTitleMarginStart = dimensionPixelOffset;
        i = -1;
        dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginStart, i);
        if (dimensionPixelOffset >= 0) {
            this.mTitleMarginStart = dimensionPixelOffset;
        }
        dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginEnd, i);
        if (dimensionPixelOffset >= 0) {
            this.mTitleMarginEnd = dimensionPixelOffset;
        }
        dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginTop, i);
        if (dimensionPixelOffset >= 0) {
            this.mTitleMarginTop = dimensionPixelOffset;
        }
        dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_titleMarginBottom, i);
        if (dimensionPixelOffset >= 0) {
            this.mTitleMarginBottom = dimensionPixelOffset;
        }
        this.mMaxButtonHeight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_maxButtonHeight, i);
        int i3 = Integer.MIN_VALUE;
        dimensionPixelOffset = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStart, i3);
        int dimensionPixelOffset2 = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEnd, i3);
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetLeft, i2);
        int dimensionPixelSize2 = obtainStyledAttributes.getDimensionPixelSize(R.styleable.Toolbar_contentInsetRight, i2);
        ensureContentInsets();
        this.mContentInsets.setAbsolute(dimensionPixelSize, dimensionPixelSize2);
        if (!(dimensionPixelOffset == i3 && dimensionPixelOffset2 == i3)) {
            this.mContentInsets.setRelative(dimensionPixelOffset, dimensionPixelOffset2);
        }
        this.mContentInsetStartWithNavigation = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetStartWithNavigation, i3);
        this.mContentInsetEndWithActions = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.Toolbar_contentInsetEndWithActions, i3);
        this.mCollapseIcon = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_collapseIcon);
        this.mCollapseDescription = obtainStyledAttributes.getText(R.styleable.Toolbar_collapseContentDescription);
        CharSequence text = obtainStyledAttributes.getText(R.styleable.Toolbar_title);
        if (!TextUtils.isEmpty(text)) {
            setTitle(text);
        }
        text = obtainStyledAttributes.getText(R.styleable.Toolbar_subtitle);
        if (!TextUtils.isEmpty(text)) {
            setSubtitle(text);
        }
        this.mPopupContext = getContext();
        setPopupTheme(obtainStyledAttributes.getResourceId(R.styleable.Toolbar_popupTheme, i2));
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_navigationIcon);
        if (drawable != null) {
            setNavigationIcon(drawable);
        }
        text = obtainStyledAttributes.getText(R.styleable.Toolbar_navigationContentDescription);
        if (!TextUtils.isEmpty(text)) {
            setNavigationContentDescription(text);
        }
        drawable = obtainStyledAttributes.getDrawable(R.styleable.Toolbar_logo);
        if (drawable != null) {
            setLogo(drawable);
        }
        text = obtainStyledAttributes.getText(R.styleable.Toolbar_logoDescription);
        if (!TextUtils.isEmpty(text)) {
            setLogoDescription(text);
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_titleTextColor)) {
            setTitleTextColor(obtainStyledAttributes.getColor(R.styleable.Toolbar_titleTextColor, i));
        }
        if (obtainStyledAttributes.hasValue(R.styleable.Toolbar_subtitleTextColor)) {
            setSubtitleTextColor(obtainStyledAttributes.getColor(R.styleable.Toolbar_subtitleTextColor, i));
        }
        obtainStyledAttributes.recycle();
    }

    public void setPopupTheme(@StyleRes int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), i);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    public void setTitleMargin(int i, int i2, int i3, int i4) {
        this.mTitleMarginStart = i;
        this.mTitleMarginTop = i2;
        this.mTitleMarginEnd = i3;
        this.mTitleMarginBottom = i4;
        requestLayout();
    }

    public int getTitleMarginStart() {
        return this.mTitleMarginStart;
    }

    public void setTitleMarginStart(int i) {
        this.mTitleMarginStart = i;
        requestLayout();
    }

    public int getTitleMarginTop() {
        return this.mTitleMarginTop;
    }

    public void setTitleMarginTop(int i) {
        this.mTitleMarginTop = i;
        requestLayout();
    }

    public int getTitleMarginEnd() {
        return this.mTitleMarginEnd;
    }

    public void setTitleMarginEnd(int i) {
        this.mTitleMarginEnd = i;
        requestLayout();
    }

    public int getTitleMarginBottom() {
        return this.mTitleMarginBottom;
    }

    public void setTitleMarginBottom(int i) {
        this.mTitleMarginBottom = i;
        requestLayout();
    }

    public void onRtlPropertiesChanged(int i) {
        if (VERSION.SDK_INT >= 17) {
            super.onRtlPropertiesChanged(i);
        }
        ensureContentInsets();
        RtlSpacingHelper rtlSpacingHelper = this.mContentInsets;
        boolean z = true;
        if (i != z) {
            z = false;
        }
        rtlSpacingHelper.setDirection(z);
    }

    public void setLogo(@DrawableRes int i) {
        setLogo(AppCompatResources.getDrawable(getContext(), i));
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean canShowOverflowMenu() {
        return getVisibility() == 0 && this.mMenuView != null && this.mMenuView.isOverflowReserved();
    }

    public boolean isOverflowMenuShowing() {
        return this.mMenuView != null && this.mMenuView.isOverflowMenuShowing();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean isOverflowMenuShowPending() {
        return this.mMenuView != null && this.mMenuView.isOverflowMenuShowPending();
    }

    public boolean showOverflowMenu() {
        return this.mMenuView != null && this.mMenuView.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mMenuView != null && this.mMenuView.hideOverflowMenu();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setMenu(MenuBuilder menuBuilder, ActionMenuPresenter actionMenuPresenter) {
        if (menuBuilder != null || this.mMenuView != null) {
            ensureMenuView();
            MenuBuilder peekMenu = this.mMenuView.peekMenu();
            if (peekMenu != menuBuilder) {
                if (peekMenu != null) {
                    peekMenu.removeMenuPresenter(this.mOuterActionMenuPresenter);
                    peekMenu.removeMenuPresenter(this.mExpandedMenuPresenter);
                }
                if (this.mExpandedMenuPresenter == null) {
                    this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
                }
                boolean z = true;
                actionMenuPresenter.setExpandedActionViewsExclusive(z);
                if (menuBuilder != null) {
                    menuBuilder.addMenuPresenter(actionMenuPresenter, this.mPopupContext);
                    menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
                } else {
                    MenuBuilder menuBuilder2 = null;
                    actionMenuPresenter.initForMenu(this.mPopupContext, menuBuilder2);
                    this.mExpandedMenuPresenter.initForMenu(this.mPopupContext, menuBuilder2);
                    actionMenuPresenter.updateMenuView(z);
                    this.mExpandedMenuPresenter.updateMenuView(z);
                }
                this.mMenuView.setPopupTheme(this.mPopupTheme);
                this.mMenuView.setPresenter(actionMenuPresenter);
                this.mOuterActionMenuPresenter = actionMenuPresenter;
            }
        }
    }

    public void dismissPopupMenus() {
        if (this.mMenuView != null) {
            this.mMenuView.dismissPopupMenus();
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean isTitleTruncated() {
        boolean z = false;
        if (this.mTitleTextView == null) {
            return z;
        }
        Layout layout = this.mTitleTextView.getLayout();
        if (layout == null) {
            return z;
        }
        int lineCount = layout.getLineCount();
        for (int i = z; i < lineCount; i++) {
            if (layout.getEllipsisCount(i) > 0) {
                return true;
            }
        }
        return z;
    }

    public void setLogo(Drawable drawable) {
        if (drawable != null) {
            ensureLogoView();
            if (!isChildOrHidden(this.mLogoView)) {
                addSystemView(this.mLogoView, true);
            }
        } else if (this.mLogoView != null && isChildOrHidden(this.mLogoView)) {
            removeView(this.mLogoView);
            this.mHiddenViews.remove(this.mLogoView);
        }
        if (this.mLogoView != null) {
            this.mLogoView.setImageDrawable(drawable);
        }
    }

    public Drawable getLogo() {
        return this.mLogoView != null ? this.mLogoView.getDrawable() : null;
    }

    public void setLogoDescription(@StringRes int i) {
        setLogoDescription(getContext().getText(i));
    }

    public void setLogoDescription(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            ensureLogoView();
        }
        if (this.mLogoView != null) {
            this.mLogoView.setContentDescription(charSequence);
        }
    }

    public CharSequence getLogoDescription() {
        return this.mLogoView != null ? this.mLogoView.getContentDescription() : null;
    }

    private void ensureLogoView() {
        if (this.mLogoView == null) {
            this.mLogoView = new AppCompatImageView(getContext());
        }
    }

    public boolean hasExpandedActionView() {
        return (this.mExpandedMenuPresenter == null || this.mExpandedMenuPresenter.mCurrentExpandedItem == null) ? false : true;
    }

    public void collapseActionView() {
        MenuItemImpl menuItemImpl = this.mExpandedMenuPresenter == null ? null : this.mExpandedMenuPresenter.mCurrentExpandedItem;
        if (menuItemImpl != null) {
            menuItemImpl.collapseActionView();
        }
    }

    public CharSequence getTitle() {
        return this.mTitleText;
    }

    public void setTitle(@StringRes int i) {
        setTitle(getContext().getText(i));
    }

    public void setTitle(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            if (this.mTitleTextView == null) {
                Context context = getContext();
                this.mTitleTextView = new AppCompatTextView(context);
                this.mTitleTextView.setSingleLine();
                this.mTitleTextView.setEllipsize(TruncateAt.END);
                if (this.mTitleTextAppearance != 0) {
                    this.mTitleTextView.setTextAppearance(context, this.mTitleTextAppearance);
                }
                if (this.mTitleTextColor != 0) {
                    this.mTitleTextView.setTextColor(this.mTitleTextColor);
                }
            }
            if (!isChildOrHidden(this.mTitleTextView)) {
                addSystemView(this.mTitleTextView, true);
            }
        } else if (this.mTitleTextView != null && isChildOrHidden(this.mTitleTextView)) {
            removeView(this.mTitleTextView);
            this.mHiddenViews.remove(this.mTitleTextView);
        }
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setText(charSequence);
        }
        this.mTitleText = charSequence;
    }

    public CharSequence getSubtitle() {
        return this.mSubtitleText;
    }

    public void setSubtitle(@StringRes int i) {
        setSubtitle(getContext().getText(i));
    }

    public void setSubtitle(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            if (this.mSubtitleTextView == null) {
                Context context = getContext();
                this.mSubtitleTextView = new AppCompatTextView(context);
                this.mSubtitleTextView.setSingleLine();
                this.mSubtitleTextView.setEllipsize(TruncateAt.END);
                if (this.mSubtitleTextAppearance != 0) {
                    this.mSubtitleTextView.setTextAppearance(context, this.mSubtitleTextAppearance);
                }
                if (this.mSubtitleTextColor != 0) {
                    this.mSubtitleTextView.setTextColor(this.mSubtitleTextColor);
                }
            }
            if (!isChildOrHidden(this.mSubtitleTextView)) {
                addSystemView(this.mSubtitleTextView, true);
            }
        } else if (this.mSubtitleTextView != null && isChildOrHidden(this.mSubtitleTextView)) {
            removeView(this.mSubtitleTextView);
            this.mHiddenViews.remove(this.mSubtitleTextView);
        }
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setText(charSequence);
        }
        this.mSubtitleText = charSequence;
    }

    public void setTitleTextAppearance(Context context, @StyleRes int i) {
        this.mTitleTextAppearance = i;
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextAppearance(context, i);
        }
    }

    public void setSubtitleTextAppearance(Context context, @StyleRes int i) {
        this.mSubtitleTextAppearance = i;
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setTextAppearance(context, i);
        }
    }

    public void setTitleTextColor(@ColorInt int i) {
        this.mTitleTextColor = i;
        if (this.mTitleTextView != null) {
            this.mTitleTextView.setTextColor(i);
        }
    }

    public void setSubtitleTextColor(@ColorInt int i) {
        this.mSubtitleTextColor = i;
        if (this.mSubtitleTextView != null) {
            this.mSubtitleTextView.setTextColor(i);
        }
    }

    @Nullable
    public CharSequence getNavigationContentDescription() {
        return this.mNavButtonView != null ? this.mNavButtonView.getContentDescription() : null;
    }

    public void setNavigationContentDescription(@StringRes int i) {
        setNavigationContentDescription(i != 0 ? getContext().getText(i) : null);
    }

    public void setNavigationContentDescription(@Nullable CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence)) {
            ensureNavButtonView();
        }
        if (this.mNavButtonView != null) {
            this.mNavButtonView.setContentDescription(charSequence);
        }
    }

    public void setNavigationIcon(@DrawableRes int i) {
        setNavigationIcon(AppCompatResources.getDrawable(getContext(), i));
    }

    public void setNavigationIcon(@Nullable Drawable drawable) {
        if (drawable != null) {
            ensureNavButtonView();
            if (!isChildOrHidden(this.mNavButtonView)) {
                addSystemView(this.mNavButtonView, true);
            }
        } else if (this.mNavButtonView != null && isChildOrHidden(this.mNavButtonView)) {
            removeView(this.mNavButtonView);
            this.mHiddenViews.remove(this.mNavButtonView);
        }
        if (this.mNavButtonView != null) {
            this.mNavButtonView.setImageDrawable(drawable);
        }
    }

    @Nullable
    public Drawable getNavigationIcon() {
        return this.mNavButtonView != null ? this.mNavButtonView.getDrawable() : null;
    }

    public void setNavigationOnClickListener(OnClickListener onClickListener) {
        ensureNavButtonView();
        this.mNavButtonView.setOnClickListener(onClickListener);
    }

    public Menu getMenu() {
        ensureMenu();
        return this.mMenuView.getMenu();
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        ensureMenu();
        this.mMenuView.setOverflowIcon(drawable);
    }

    @Nullable
    public Drawable getOverflowIcon() {
        ensureMenu();
        return this.mMenuView.getOverflowIcon();
    }

    private void ensureMenu() {
        ensureMenuView();
        if (this.mMenuView.peekMenu() == null) {
            MenuBuilder menuBuilder = (MenuBuilder) this.mMenuView.getMenu();
            if (this.mExpandedMenuPresenter == null) {
                this.mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            this.mMenuView.setExpandedActionViewsExclusive(true);
            menuBuilder.addMenuPresenter(this.mExpandedMenuPresenter, this.mPopupContext);
        }
    }

    private void ensureMenuView() {
        if (this.mMenuView == null) {
            this.mMenuView = new ActionMenuView(getContext());
            this.mMenuView.setPopupTheme(this.mPopupTheme);
            this.mMenuView.setOnMenuItemClickListener(this.mMenuViewItemClickListener);
            this.mMenuView.setMenuCallbacks(this.mActionMenuPresenterCallback, this.mMenuBuilderCallback);
            android.view.ViewGroup.LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388613 | (this.mButtonGravity & 112);
            this.mMenuView.setLayoutParams(generateDefaultLayoutParams);
            addSystemView(this.mMenuView, false);
        }
    }

    private MenuInflater getMenuInflater() {
        return new SupportMenuInflater(getContext());
    }

    public void inflateMenu(@MenuRes int i) {
        getMenuInflater().inflate(i, getMenu());
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setContentInsetsRelative(int i, int i2) {
        ensureContentInsets();
        this.mContentInsets.setRelative(i, i2);
    }

    public int getContentInsetStart() {
        return this.mContentInsets != null ? this.mContentInsets.getStart() : 0;
    }

    public int getContentInsetEnd() {
        return this.mContentInsets != null ? this.mContentInsets.getEnd() : 0;
    }

    public void setContentInsetsAbsolute(int i, int i2) {
        ensureContentInsets();
        this.mContentInsets.setAbsolute(i, i2);
    }

    public int getContentInsetLeft() {
        return this.mContentInsets != null ? this.mContentInsets.getLeft() : 0;
    }

    public int getContentInsetRight() {
        return this.mContentInsets != null ? this.mContentInsets.getRight() : 0;
    }

    public int getContentInsetStartWithNavigation() {
        if (this.mContentInsetStartWithNavigation != Integer.MIN_VALUE) {
            return this.mContentInsetStartWithNavigation;
        }
        return getContentInsetStart();
    }

    public void setContentInsetStartWithNavigation(int i) {
        if (i < 0) {
            i = Integer.MIN_VALUE;
        }
        if (i != this.mContentInsetStartWithNavigation) {
            this.mContentInsetStartWithNavigation = i;
            if (getNavigationIcon() != null) {
                requestLayout();
            }
        }
    }

    public int getContentInsetEndWithActions() {
        if (this.mContentInsetEndWithActions != Integer.MIN_VALUE) {
            return this.mContentInsetEndWithActions;
        }
        return getContentInsetEnd();
    }

    public void setContentInsetEndWithActions(int i) {
        if (i < 0) {
            i = Integer.MIN_VALUE;
        }
        if (i != this.mContentInsetEndWithActions) {
            this.mContentInsetEndWithActions = i;
            if (getNavigationIcon() != null) {
                requestLayout();
            }
        }
    }

    public int getCurrentContentInsetStart() {
        if (getNavigationIcon() != null) {
            return Math.max(getContentInsetStart(), Math.max(this.mContentInsetStartWithNavigation, 0));
        }
        return getContentInsetStart();
    }

    public int getCurrentContentInsetEnd() {
        int i;
        int i2 = 0;
        if (this.mMenuView != null) {
            MenuBuilder peekMenu = this.mMenuView.peekMenu();
            if (peekMenu != null && peekMenu.hasVisibleItems()) {
                i = 1;
                if (i == 0) {
                    return Math.max(getContentInsetEnd(), Math.max(this.mContentInsetEndWithActions, i2));
                }
                return getContentInsetEnd();
            }
        }
        i = i2;
        if (i == 0) {
            return getContentInsetEnd();
        }
        return Math.max(getContentInsetEnd(), Math.max(this.mContentInsetEndWithActions, i2));
    }

    public int getCurrentContentInsetLeft() {
        if (ViewCompat.getLayoutDirection(this) == 1) {
            return getCurrentContentInsetEnd();
        }
        return getCurrentContentInsetStart();
    }

    public int getCurrentContentInsetRight() {
        if (ViewCompat.getLayoutDirection(this) == 1) {
            return getCurrentContentInsetStart();
        }
        return getCurrentContentInsetEnd();
    }

    private void ensureNavButtonView() {
        if (this.mNavButtonView == null) {
            this.mNavButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
            android.view.ViewGroup.LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388611 | (this.mButtonGravity & 112);
            this.mNavButtonView.setLayoutParams(generateDefaultLayoutParams);
        }
    }

    void ensureCollapseButtonView() {
        if (this.mCollapseButtonView == null) {
            this.mCollapseButtonView = new AppCompatImageButton(getContext(), null, R.attr.toolbarNavigationButtonStyle);
            this.mCollapseButtonView.setImageDrawable(this.mCollapseIcon);
            this.mCollapseButtonView.setContentDescription(this.mCollapseDescription);
            android.view.ViewGroup.LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
            generateDefaultLayoutParams.gravity = 8388611 | (this.mButtonGravity & 112);
            generateDefaultLayoutParams.mViewType = 2;
            this.mCollapseButtonView.setLayoutParams(generateDefaultLayoutParams);
            this.mCollapseButtonView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    Toolbar.this.collapseActionView();
                }
            });
        }
    }

    private void addSystemView(View view, boolean z) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = generateDefaultLayoutParams();
        } else if (checkLayoutParams(layoutParams)) {
            layoutParams = (LayoutParams) layoutParams;
        } else {
            layoutParams = generateLayoutParams(layoutParams);
        }
        layoutParams.mViewType = 1;
        if (!z || this.mExpandedActionView == null) {
            addView(view, layoutParams);
            return;
        }
        view.setLayoutParams(layoutParams);
        this.mHiddenViews.add(view);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        if (!(this.mExpandedMenuPresenter == null || this.mExpandedMenuPresenter.mCurrentExpandedItem == null)) {
            savedState.expandedMenuItemId = this.mExpandedMenuPresenter.mCurrentExpandedItem.getItemId();
        }
        savedState.isOverflowOpen = isOverflowMenuShowing();
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            Menu peekMenu = this.mMenuView != null ? this.mMenuView.peekMenu() : null;
            if (!(savedState.expandedMenuItemId == 0 || this.mExpandedMenuPresenter == null || peekMenu == null)) {
                MenuItem findItem = peekMenu.findItem(savedState.expandedMenuItemId);
                if (findItem != null) {
                    findItem.expandActionView();
                }
            }
            if (savedState.isOverflowOpen) {
                postShowOverflowMenu();
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    private void postShowOverflowMenu() {
        removeCallbacks(this.mShowOverflowMenuRunnable);
        post(this.mShowOverflowMenuRunnable);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mShowOverflowMenuRunnable);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean actionMasked = motionEvent.getActionMasked();
        boolean z = false;
        if (!actionMasked) {
            this.mEatingTouch = z;
        }
        boolean z2 = true;
        if (!this.mEatingTouch) {
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (!(actionMasked || onTouchEvent)) {
                this.mEatingTouch = z2;
            }
        }
        if (actionMasked == z2 || actionMasked) {
            this.mEatingTouch = z;
        }
        return z2;
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        boolean z = false;
        int i = 9;
        if (actionMasked == i) {
            this.mEatingHover = z;
        }
        boolean z2 = true;
        if (!this.mEatingHover) {
            boolean onHoverEvent = super.onHoverEvent(motionEvent);
            if (actionMasked == i && !onHoverEvent) {
                this.mEatingHover = z2;
            }
        }
        if (actionMasked == 10 || actionMasked == 3) {
            this.mEatingHover = z;
        }
        return z2;
    }

    private void measureChildConstrained(View view, int i, int i2, int i3, int i4, int i5) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        i = getChildMeasureSpec(i, (((getPaddingLeft() + getPaddingRight()) + marginLayoutParams.leftMargin) + marginLayoutParams.rightMargin) + i2, marginLayoutParams.width);
        i2 = getChildMeasureSpec(i3, (((getPaddingTop() + getPaddingBottom()) + marginLayoutParams.topMargin) + marginLayoutParams.bottomMargin) + i4, marginLayoutParams.height);
        i3 = MeasureSpec.getMode(i2);
        i4 = 1073741824;
        if (i3 != i4 && i5 >= 0) {
            if (i3 != 0) {
                i5 = Math.min(MeasureSpec.getSize(i2), i5);
            }
            i2 = MeasureSpec.makeMeasureSpec(i5, i4);
        }
        view.measure(i, i2);
    }

    private int measureChildCollapseMargins(View view, int i, int i2, int i3, int i4, int[] iArr) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        int i5 = 0;
        int i6 = marginLayoutParams.leftMargin - iArr[i5];
        int i7 = 1;
        int i8 = marginLayoutParams.rightMargin - iArr[i7];
        int max = Math.max(i5, i6) + Math.max(i5, i8);
        iArr[i5] = Math.max(i5, -i6);
        iArr[i7] = Math.max(i5, -i8);
        view.measure(getChildMeasureSpec(i, ((getPaddingLeft() + getPaddingRight()) + max) + i2, marginLayoutParams.width), getChildMeasureSpec(i3, (((getPaddingTop() + getPaddingBottom()) + marginLayoutParams.topMargin) + marginLayoutParams.bottomMargin) + i4, marginLayoutParams.height));
        return view.getMeasuredWidth() + max;
    }

    private boolean shouldCollapse() {
        boolean z = false;
        if (!this.mCollapsible) {
            return z;
        }
        int childCount = getChildCount();
        for (int i = z; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (shouldLayout(childAt) && childAt.getMeasuredWidth() > 0 && childAt.getMeasuredHeight() > 0) {
                return z;
            }
        }
        return true;
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int measuredWidth;
        int max;
        int combineMeasuredStates;
        int measuredHeight;
        int combineMeasuredStates2;
        int[] iArr = this.mTempMargins;
        int i6 = 1;
        int i7 = 0;
        if (ViewUtils.isLayoutRtl(this)) {
            i3 = i6;
            i4 = i7;
        } else {
            i4 = i6;
            i3 = i7;
        }
        if (shouldLayout(r7.mNavButtonView)) {
            i5 = i;
            measureChildConstrained(r7.mNavButtonView, i5, 0, i2, 0, r7.mMaxButtonHeight);
            measuredWidth = r7.mNavButtonView.getMeasuredWidth() + getHorizontalMargins(r7.mNavButtonView);
            max = Math.max(i7, r7.mNavButtonView.getMeasuredHeight() + getVerticalMargins(r7.mNavButtonView));
            combineMeasuredStates = View.combineMeasuredStates(i7, r7.mNavButtonView.getMeasuredState());
        } else {
            measuredWidth = i7;
            max = measuredWidth;
            combineMeasuredStates = max;
        }
        if (shouldLayout(r7.mCollapseButtonView)) {
            i5 = i;
            measureChildConstrained(r7.mCollapseButtonView, i5, 0, i2, 0, r7.mMaxButtonHeight);
            measuredWidth = r7.mCollapseButtonView.getMeasuredWidth() + getHorizontalMargins(r7.mCollapseButtonView);
            max = Math.max(max, r7.mCollapseButtonView.getMeasuredHeight() + getVerticalMargins(r7.mCollapseButtonView));
            combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, r7.mCollapseButtonView.getMeasuredState());
        }
        i6 = getCurrentContentInsetStart();
        int max2 = i7 + Math.max(i6, measuredWidth);
        iArr[i3] = Math.max(i7, i6 - measuredWidth);
        if (shouldLayout(r7.mMenuView)) {
            measureChildConstrained(r7.mMenuView, i, max2, i2, 0, r7.mMaxButtonHeight);
            measuredWidth = r7.mMenuView.getMeasuredWidth() + getHorizontalMargins(r7.mMenuView);
            max = Math.max(max, r7.mMenuView.getMeasuredHeight() + getVerticalMargins(r7.mMenuView));
            combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, r7.mMenuView.getMeasuredState());
        } else {
            measuredWidth = i7;
        }
        i6 = getCurrentContentInsetEnd();
        i3 = max2 + Math.max(i6, measuredWidth);
        iArr[i4] = Math.max(i7, i6 - measuredWidth);
        if (shouldLayout(r7.mExpandedActionView)) {
            i3 += measureChildCollapseMargins(r7.mExpandedActionView, i, i3, i2, 0, iArr);
            max = Math.max(max, r7.mExpandedActionView.getMeasuredHeight() + getVerticalMargins(r7.mExpandedActionView));
            combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, r7.mExpandedActionView.getMeasuredState());
        }
        if (shouldLayout(r7.mLogoView)) {
            i3 += measureChildCollapseMargins(r7.mLogoView, i, i3, i2, 0, iArr);
            max = Math.max(max, r7.mLogoView.getMeasuredHeight() + getVerticalMargins(r7.mLogoView));
            combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, r7.mLogoView.getMeasuredState());
        }
        i4 = getChildCount();
        max2 = max;
        max = i3;
        for (i3 = i7; i3 < i4; i3++) {
            View childAt = getChildAt(i3);
            if (((LayoutParams) childAt.getLayoutParams()).mViewType == 0 && shouldLayout(childAt)) {
                max += measureChildCollapseMargins(childAt, i, max, i2, 0, iArr);
                max2 = Math.max(max2, childAt.getMeasuredHeight() + getVerticalMargins(childAt));
                combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, childAt.getMeasuredState());
            }
        }
        i3 = r7.mTitleMarginTop + r7.mTitleMarginBottom;
        i4 = r7.mTitleMarginStart + r7.mTitleMarginEnd;
        if (shouldLayout(r7.mTitleTextView)) {
            measureChildCollapseMargins(r7.mTitleTextView, i, max + i4, i2, i3, iArr);
            measuredWidth = r7.mTitleTextView.getMeasuredWidth() + getHorizontalMargins(r7.mTitleTextView);
            measuredHeight = r7.mTitleTextView.getMeasuredHeight() + getVerticalMargins(r7.mTitleTextView);
            combineMeasuredStates2 = View.combineMeasuredStates(combineMeasuredStates, r7.mTitleTextView.getMeasuredState());
            combineMeasuredStates = measuredWidth;
        } else {
            measuredHeight = i7;
            combineMeasuredStates2 = combineMeasuredStates;
            combineMeasuredStates = measuredHeight;
        }
        if (shouldLayout(r7.mSubtitleTextView)) {
            int i8 = measuredHeight + i3;
            i3 = combineMeasuredStates2;
            combineMeasuredStates = Math.max(combineMeasuredStates, measureChildCollapseMargins(r7.mSubtitleTextView, i, max + i4, i2, i8, iArr));
            measuredHeight += r7.mSubtitleTextView.getMeasuredHeight() + getVerticalMargins(r7.mSubtitleTextView);
            combineMeasuredStates2 = View.combineMeasuredStates(i3, r7.mSubtitleTextView.getMeasuredState());
        } else {
            i3 = combineMeasuredStates2;
        }
        max += combineMeasuredStates;
        measuredWidth = Math.max(max2, measuredHeight) + (getPaddingTop() + getPaddingBottom());
        int i9 = i;
        i6 = View.resolveSizeAndState(Math.max(max + (getPaddingLeft() + getPaddingRight()), getSuggestedMinimumWidth()), i9, -16777216 & combineMeasuredStates2);
        measuredWidth = View.resolveSizeAndState(Math.max(measuredWidth, getSuggestedMinimumHeight()), i2, combineMeasuredStates2 << 16);
        if (shouldCollapse()) {
            measuredWidth = i7;
        }
        setMeasuredDimension(i6, measuredWidth);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int layoutChildRight;
        int currentContentInsetLeft;
        int currentContentInsetRight;
        boolean shouldLayout;
        boolean shouldLayout2;
        LayoutParams layoutParams;
        LayoutParams layoutParams2;
        int i6;
        Object obj;
        int i7;
        int i8;
        LayoutParams layoutParams3;
        Toolbar toolbar = this;
        int i9 = 1;
        int i10 = 0;
        int i11 = ViewCompat.getLayoutDirection(this) == i9 ? i9 : i10;
        int width = getWidth();
        int height = getHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int i12 = width - paddingRight;
        int[] iArr = toolbar.mTempMargins;
        iArr[i9] = i10;
        iArr[i10] = i10;
        int minimumHeight = ViewCompat.getMinimumHeight(this);
        minimumHeight = minimumHeight >= 0 ? Math.min(minimumHeight, i4 - i2) : i10;
        if (!shouldLayout(toolbar.mNavButtonView)) {
            i5 = paddingLeft;
        } else if (i11 != 0) {
            int i13;
            int i14;
            layoutChildRight = layoutChildRight(toolbar.mNavButtonView, i12, iArr, minimumHeight);
            i5 = paddingLeft;
            if (shouldLayout(toolbar.mCollapseButtonView)) {
                if (i11 != 0) {
                    layoutChildRight = layoutChildRight(toolbar.mCollapseButtonView, layoutChildRight, iArr, minimumHeight);
                } else {
                    i5 = layoutChildLeft(toolbar.mCollapseButtonView, i5, iArr, minimumHeight);
                }
            }
            if (shouldLayout(toolbar.mMenuView)) {
                if (i11 != 0) {
                    i5 = layoutChildLeft(toolbar.mMenuView, i5, iArr, minimumHeight);
                } else {
                    layoutChildRight = layoutChildRight(toolbar.mMenuView, layoutChildRight, iArr, minimumHeight);
                }
            }
            currentContentInsetLeft = getCurrentContentInsetLeft();
            currentContentInsetRight = getCurrentContentInsetRight();
            iArr[i10] = Math.max(i10, currentContentInsetLeft - i5);
            iArr[1] = Math.max(i10, currentContentInsetRight - (i12 - layoutChildRight));
            i9 = Math.max(i5, currentContentInsetLeft);
            i12 = Math.min(layoutChildRight, i12 - currentContentInsetRight);
            if (shouldLayout(toolbar.mExpandedActionView)) {
                if (i11 != 0) {
                    i12 = layoutChildRight(toolbar.mExpandedActionView, i12, iArr, minimumHeight);
                } else {
                    i9 = layoutChildLeft(toolbar.mExpandedActionView, i9, iArr, minimumHeight);
                }
            }
            if (shouldLayout(toolbar.mLogoView)) {
                if (i11 != 0) {
                    i12 = layoutChildRight(toolbar.mLogoView, i12, iArr, minimumHeight);
                } else {
                    i9 = layoutChildLeft(toolbar.mLogoView, i9, iArr, minimumHeight);
                }
            }
            shouldLayout = shouldLayout(toolbar.mTitleTextView);
            shouldLayout2 = shouldLayout(toolbar.mSubtitleTextView);
            if (shouldLayout) {
                i13 = paddingRight;
                i10 = 0;
            } else {
                LayoutParams layoutParams4 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
                i13 = paddingRight;
                i10 = ((layoutParams4.topMargin + toolbar.mTitleTextView.getMeasuredHeight()) + layoutParams4.bottomMargin) + 0;
            }
            if (shouldLayout2) {
                i14 = width;
            } else {
                layoutParams = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
                i14 = width;
                i10 += (layoutParams.topMargin + toolbar.mSubtitleTextView.getMeasuredHeight()) + layoutParams.bottomMargin;
            }
            if (!shouldLayout || shouldLayout2) {
                layoutParams2 = (LayoutParams) (shouldLayout ? toolbar.mTitleTextView : toolbar.mSubtitleTextView).getLayoutParams();
                layoutParams = (LayoutParams) (shouldLayout2 ? toolbar.mSubtitleTextView : toolbar.mTitleTextView).getLayoutParams();
                if ((shouldLayout || toolbar.mTitleTextView.getMeasuredWidth() <= 0) && (!shouldLayout2 || toolbar.mSubtitleTextView.getMeasuredWidth() <= 0)) {
                    i6 = paddingLeft;
                    obj = null;
                } else {
                    i6 = paddingLeft;
                    obj = 1;
                }
                paddingLeft = toolbar.mGravity & 112;
                i7 = minimumHeight;
                if (paddingLeft != 48) {
                    i8 = i9;
                    paddingTop = (getPaddingTop() + layoutParams2.topMargin) + toolbar.mTitleMarginTop;
                } else if (paddingLeft != 80) {
                    paddingLeft = (((height - paddingTop) - paddingBottom) - i10) / 2;
                    i8 = i9;
                    if (paddingLeft < layoutParams2.topMargin + toolbar.mTitleMarginTop) {
                        paddingLeft = layoutParams2.topMargin + toolbar.mTitleMarginTop;
                    } else {
                        height = (((height - paddingBottom) - i10) - paddingLeft) - paddingTop;
                        if (height < layoutParams2.bottomMargin + toolbar.mTitleMarginBottom) {
                            paddingLeft = Math.max(0, paddingLeft - ((layoutParams.bottomMargin + toolbar.mTitleMarginBottom) - height));
                        }
                    }
                    paddingTop += paddingLeft;
                } else {
                    i8 = i9;
                    paddingTop = (((height - paddingBottom) - layoutParams.bottomMargin) - toolbar.mTitleMarginBottom) - i10;
                }
                if (i11 == 0) {
                    if (obj != null) {
                        i10 = toolbar.mTitleMarginStart;
                        i11 = 1;
                    } else {
                        i11 = 1;
                        i10 = 0;
                    }
                    i10 -= iArr[i11];
                    i9 = 0;
                    i12 -= Math.max(i9, i10);
                    iArr[i11] = Math.max(i9, -i10);
                    if (shouldLayout) {
                        layoutParams3 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
                        i9 = i12 - toolbar.mTitleTextView.getMeasuredWidth();
                        i10 = toolbar.mTitleTextView.getMeasuredHeight() + paddingTop;
                        toolbar.mTitleTextView.layout(i9, paddingTop, i12, i10);
                        i9 -= toolbar.mTitleMarginEnd;
                        paddingTop = i10 + layoutParams3.bottomMargin;
                    } else {
                        i9 = i12;
                    }
                    if (shouldLayout2) {
                        layoutParams3 = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
                        paddingTop += layoutParams3.topMargin;
                        toolbar.mSubtitleTextView.layout(i12 - toolbar.mSubtitleTextView.getMeasuredWidth(), paddingTop, i12, toolbar.mSubtitleTextView.getMeasuredHeight() + paddingTop);
                        i10 = i12 - toolbar.mTitleMarginEnd;
                        i11 = layoutParams3.bottomMargin;
                    } else {
                        i10 = i12;
                    }
                    if (obj != null) {
                        i12 = Math.min(i9, i10);
                    }
                    i9 = i8;
                } else {
                    int i15;
                    if (obj != null) {
                        i15 = toolbar.mTitleMarginStart;
                        paddingRight = 0;
                    } else {
                        paddingRight = 0;
                        i15 = 0;
                    }
                    i11 = i15 - iArr[paddingRight];
                    i9 = i8 + Math.max(paddingRight, i11);
                    iArr[paddingRight] = Math.max(paddingRight, -i11);
                    if (shouldLayout) {
                        layoutParams3 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
                        i10 = toolbar.mTitleTextView.getMeasuredWidth() + i9;
                        width = toolbar.mTitleTextView.getMeasuredHeight() + paddingTop;
                        toolbar.mTitleTextView.layout(i9, paddingTop, i10, width);
                        i10 += toolbar.mTitleMarginEnd;
                        paddingTop = width + layoutParams3.bottomMargin;
                    } else {
                        i10 = i9;
                    }
                    if (shouldLayout2) {
                        layoutParams3 = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
                        paddingTop += layoutParams3.topMargin;
                        width = toolbar.mSubtitleTextView.getMeasuredWidth() + i9;
                        toolbar.mSubtitleTextView.layout(i9, paddingTop, width, toolbar.mSubtitleTextView.getMeasuredHeight() + paddingTop);
                        width += toolbar.mTitleMarginEnd;
                        i11 = layoutParams3.bottomMargin;
                    } else {
                        width = i9;
                    }
                    if (obj != null) {
                        i9 = Math.max(i10, width);
                    }
                    addCustomViewsWithGravity(toolbar.mTempViews, 3);
                    i11 = toolbar.mTempViews.size();
                    i10 = i9;
                    for (i9 = paddingRight; i9 < i11; i9++) {
                        i10 = layoutChildLeft((View) toolbar.mTempViews.get(i9), i10, iArr, i7);
                    }
                    minimumHeight = i7;
                    addCustomViewsWithGravity(toolbar.mTempViews, 5);
                    i11 = toolbar.mTempViews.size();
                    for (i9 = paddingRight; i9 < i11; i9++) {
                        i12 = layoutChildRight((View) toolbar.mTempViews.get(i9), i12, iArr, minimumHeight);
                    }
                    addCustomViewsWithGravity(toolbar.mTempViews, 1);
                    i11 = getViewListMeasuredWidth(toolbar.mTempViews, iArr);
                    i9 = (i6 + (((i14 - i6) - i13) / 2)) - (i11 / 2);
                    i11 += i9;
                    if (i9 >= i10) {
                        i10 = i11 > i12 ? i9 - (i11 - i12) : i9;
                    }
                    i11 = toolbar.mTempViews.size();
                    while (paddingRight < i11) {
                        i10 = layoutChildLeft((View) toolbar.mTempViews.get(paddingRight), i10, iArr, minimumHeight);
                        paddingRight++;
                    }
                    toolbar.mTempViews.clear();
                    return;
                }
            }
            i6 = paddingLeft;
            i7 = minimumHeight;
            paddingRight = 0;
            addCustomViewsWithGravity(toolbar.mTempViews, 3);
            i11 = toolbar.mTempViews.size();
            i10 = i9;
            for (i9 = paddingRight; i9 < i11; i9++) {
                i10 = layoutChildLeft((View) toolbar.mTempViews.get(i9), i10, iArr, i7);
            }
            minimumHeight = i7;
            addCustomViewsWithGravity(toolbar.mTempViews, 5);
            i11 = toolbar.mTempViews.size();
            for (i9 = paddingRight; i9 < i11; i9++) {
                i12 = layoutChildRight((View) toolbar.mTempViews.get(i9), i12, iArr, minimumHeight);
            }
            addCustomViewsWithGravity(toolbar.mTempViews, 1);
            i11 = getViewListMeasuredWidth(toolbar.mTempViews, iArr);
            i9 = (i6 + (((i14 - i6) - i13) / 2)) - (i11 / 2);
            i11 += i9;
            if (i9 >= i10) {
                i10 = i11 > i12 ? i9 - (i11 - i12) : i9;
            }
            i11 = toolbar.mTempViews.size();
            while (paddingRight < i11) {
                i10 = layoutChildLeft((View) toolbar.mTempViews.get(paddingRight), i10, iArr, minimumHeight);
                paddingRight++;
            }
            toolbar.mTempViews.clear();
            return;
        } else {
            i5 = layoutChildLeft(toolbar.mNavButtonView, paddingLeft, iArr, minimumHeight);
        }
        layoutChildRight = i12;
        if (shouldLayout(toolbar.mCollapseButtonView)) {
            if (i11 != 0) {
                layoutChildRight = layoutChildRight(toolbar.mCollapseButtonView, layoutChildRight, iArr, minimumHeight);
            } else {
                i5 = layoutChildLeft(toolbar.mCollapseButtonView, i5, iArr, minimumHeight);
            }
        }
        if (shouldLayout(toolbar.mMenuView)) {
            if (i11 != 0) {
                i5 = layoutChildLeft(toolbar.mMenuView, i5, iArr, minimumHeight);
            } else {
                layoutChildRight = layoutChildRight(toolbar.mMenuView, layoutChildRight, iArr, minimumHeight);
            }
        }
        currentContentInsetLeft = getCurrentContentInsetLeft();
        currentContentInsetRight = getCurrentContentInsetRight();
        iArr[i10] = Math.max(i10, currentContentInsetLeft - i5);
        iArr[1] = Math.max(i10, currentContentInsetRight - (i12 - layoutChildRight));
        i9 = Math.max(i5, currentContentInsetLeft);
        i12 = Math.min(layoutChildRight, i12 - currentContentInsetRight);
        if (shouldLayout(toolbar.mExpandedActionView)) {
            if (i11 != 0) {
                i12 = layoutChildRight(toolbar.mExpandedActionView, i12, iArr, minimumHeight);
            } else {
                i9 = layoutChildLeft(toolbar.mExpandedActionView, i9, iArr, minimumHeight);
            }
        }
        if (shouldLayout(toolbar.mLogoView)) {
            if (i11 != 0) {
                i12 = layoutChildRight(toolbar.mLogoView, i12, iArr, minimumHeight);
            } else {
                i9 = layoutChildLeft(toolbar.mLogoView, i9, iArr, minimumHeight);
            }
        }
        shouldLayout = shouldLayout(toolbar.mTitleTextView);
        shouldLayout2 = shouldLayout(toolbar.mSubtitleTextView);
        if (shouldLayout) {
            i13 = paddingRight;
            i10 = 0;
        } else {
            LayoutParams layoutParams42 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
            i13 = paddingRight;
            i10 = ((layoutParams42.topMargin + toolbar.mTitleTextView.getMeasuredHeight()) + layoutParams42.bottomMargin) + 0;
        }
        if (shouldLayout2) {
            i14 = width;
        } else {
            layoutParams = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
            i14 = width;
            i10 += (layoutParams.topMargin + toolbar.mSubtitleTextView.getMeasuredHeight()) + layoutParams.bottomMargin;
        }
        if (shouldLayout) {
        }
        if (shouldLayout) {
        }
        if (shouldLayout2) {
        }
        layoutParams2 = (LayoutParams) (shouldLayout ? toolbar.mTitleTextView : toolbar.mSubtitleTextView).getLayoutParams();
        layoutParams = (LayoutParams) (shouldLayout2 ? toolbar.mSubtitleTextView : toolbar.mTitleTextView).getLayoutParams();
        if (shouldLayout) {
        }
        i6 = paddingLeft;
        obj = null;
        paddingLeft = toolbar.mGravity & 112;
        i7 = minimumHeight;
        if (paddingLeft != 48) {
            i8 = i9;
            paddingTop = (getPaddingTop() + layoutParams2.topMargin) + toolbar.mTitleMarginTop;
        } else if (paddingLeft != 80) {
            paddingLeft = (((height - paddingTop) - paddingBottom) - i10) / 2;
            i8 = i9;
            if (paddingLeft < layoutParams2.topMargin + toolbar.mTitleMarginTop) {
                paddingLeft = layoutParams2.topMargin + toolbar.mTitleMarginTop;
            } else {
                height = (((height - paddingBottom) - i10) - paddingLeft) - paddingTop;
                if (height < layoutParams2.bottomMargin + toolbar.mTitleMarginBottom) {
                    paddingLeft = Math.max(0, paddingLeft - ((layoutParams.bottomMargin + toolbar.mTitleMarginBottom) - height));
                }
            }
            paddingTop += paddingLeft;
        } else {
            i8 = i9;
            paddingTop = (((height - paddingBottom) - layoutParams.bottomMargin) - toolbar.mTitleMarginBottom) - i10;
        }
        if (i11 == 0) {
            int i152;
            if (obj != null) {
                i152 = toolbar.mTitleMarginStart;
                paddingRight = 0;
            } else {
                paddingRight = 0;
                i152 = 0;
            }
            i11 = i152 - iArr[paddingRight];
            i9 = i8 + Math.max(paddingRight, i11);
            iArr[paddingRight] = Math.max(paddingRight, -i11);
            if (shouldLayout) {
                layoutParams3 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
                i10 = toolbar.mTitleTextView.getMeasuredWidth() + i9;
                width = toolbar.mTitleTextView.getMeasuredHeight() + paddingTop;
                toolbar.mTitleTextView.layout(i9, paddingTop, i10, width);
                i10 += toolbar.mTitleMarginEnd;
                paddingTop = width + layoutParams3.bottomMargin;
            } else {
                i10 = i9;
            }
            if (shouldLayout2) {
                layoutParams3 = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
                paddingTop += layoutParams3.topMargin;
                width = toolbar.mSubtitleTextView.getMeasuredWidth() + i9;
                toolbar.mSubtitleTextView.layout(i9, paddingTop, width, toolbar.mSubtitleTextView.getMeasuredHeight() + paddingTop);
                width += toolbar.mTitleMarginEnd;
                i11 = layoutParams3.bottomMargin;
            } else {
                width = i9;
            }
            if (obj != null) {
                i9 = Math.max(i10, width);
            }
            addCustomViewsWithGravity(toolbar.mTempViews, 3);
            i11 = toolbar.mTempViews.size();
            i10 = i9;
            for (i9 = paddingRight; i9 < i11; i9++) {
                i10 = layoutChildLeft((View) toolbar.mTempViews.get(i9), i10, iArr, i7);
            }
            minimumHeight = i7;
            addCustomViewsWithGravity(toolbar.mTempViews, 5);
            i11 = toolbar.mTempViews.size();
            for (i9 = paddingRight; i9 < i11; i9++) {
                i12 = layoutChildRight((View) toolbar.mTempViews.get(i9), i12, iArr, minimumHeight);
            }
            addCustomViewsWithGravity(toolbar.mTempViews, 1);
            i11 = getViewListMeasuredWidth(toolbar.mTempViews, iArr);
            i9 = (i6 + (((i14 - i6) - i13) / 2)) - (i11 / 2);
            i11 += i9;
            if (i9 >= i10) {
                i10 = i11 > i12 ? i9 - (i11 - i12) : i9;
            }
            i11 = toolbar.mTempViews.size();
            while (paddingRight < i11) {
                i10 = layoutChildLeft((View) toolbar.mTempViews.get(paddingRight), i10, iArr, minimumHeight);
                paddingRight++;
            }
            toolbar.mTempViews.clear();
            return;
        }
        if (obj != null) {
            i10 = toolbar.mTitleMarginStart;
            i11 = 1;
        } else {
            i11 = 1;
            i10 = 0;
        }
        i10 -= iArr[i11];
        i9 = 0;
        i12 -= Math.max(i9, i10);
        iArr[i11] = Math.max(i9, -i10);
        if (shouldLayout) {
            layoutParams3 = (LayoutParams) toolbar.mTitleTextView.getLayoutParams();
            i9 = i12 - toolbar.mTitleTextView.getMeasuredWidth();
            i10 = toolbar.mTitleTextView.getMeasuredHeight() + paddingTop;
            toolbar.mTitleTextView.layout(i9, paddingTop, i12, i10);
            i9 -= toolbar.mTitleMarginEnd;
            paddingTop = i10 + layoutParams3.bottomMargin;
        } else {
            i9 = i12;
        }
        if (shouldLayout2) {
            layoutParams3 = (LayoutParams) toolbar.mSubtitleTextView.getLayoutParams();
            paddingTop += layoutParams3.topMargin;
            toolbar.mSubtitleTextView.layout(i12 - toolbar.mSubtitleTextView.getMeasuredWidth(), paddingTop, i12, toolbar.mSubtitleTextView.getMeasuredHeight() + paddingTop);
            i10 = i12 - toolbar.mTitleMarginEnd;
            i11 = layoutParams3.bottomMargin;
        } else {
            i10 = i12;
        }
        if (obj != null) {
            i12 = Math.min(i9, i10);
        }
        i9 = i8;
    }

    private int getViewListMeasuredWidth(List<View> list, int[] iArr) {
        int i = 0;
        int i2 = iArr[i];
        int i3 = iArr[1];
        int size = list.size();
        int i4 = i3;
        i3 = i;
        int i5 = i3;
        while (i3 < size) {
            View view = (View) list.get(i3);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int i6 = layoutParams.leftMargin - i2;
            i2 = layoutParams.rightMargin - i4;
            i4 = Math.max(i, i6);
            int max = Math.max(i, i2);
            i6 = Math.max(i, -i6);
            i5 += (i4 + view.getMeasuredWidth()) + max;
            i3++;
            i4 = Math.max(i, -i2);
            i2 = i6;
        }
        return i5;
    }

    private int layoutChildLeft(View view, int i, int[] iArr, int i2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i3 = 0;
        int i4 = layoutParams.leftMargin - iArr[i3];
        i += Math.max(i3, i4);
        iArr[i3] = Math.max(i3, -i4);
        int childTop = getChildTop(view, i2);
        i2 = view.getMeasuredWidth();
        view.layout(i, childTop, i + i2, view.getMeasuredHeight() + childTop);
        return i + (i2 + layoutParams.rightMargin);
    }

    private int layoutChildRight(View view, int i, int[] iArr, int i2) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i3 = 1;
        int i4 = layoutParams.rightMargin - iArr[i3];
        int i5 = 0;
        i -= Math.max(i5, i4);
        iArr[i3] = Math.max(i5, -i4);
        int childTop = getChildTop(view, i2);
        i2 = view.getMeasuredWidth();
        view.layout(i - i2, childTop, i, view.getMeasuredHeight() + childTop);
        return i - (i2 + layoutParams.leftMargin);
    }

    private int getChildTop(View view, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int measuredHeight = view.getMeasuredHeight();
        int i2 = 0;
        i = i > 0 ? (measuredHeight - i) / 2 : i2;
        int childVerticalGravity = getChildVerticalGravity(layoutParams.gravity);
        if (childVerticalGravity == 48) {
            return getPaddingTop() - i;
        }
        if (childVerticalGravity == 80) {
            return (((getHeight() - getPaddingBottom()) - measuredHeight) - layoutParams.bottomMargin) - i;
        }
        i = getPaddingTop();
        childVerticalGravity = getPaddingBottom();
        int height = getHeight();
        int i3 = (((height - i) - childVerticalGravity) - measuredHeight) / 2;
        if (i3 < layoutParams.topMargin) {
            i3 = layoutParams.topMargin;
        } else {
            height = (((height - childVerticalGravity) - measuredHeight) - i3) - i;
            if (height < layoutParams.bottomMargin) {
                i3 = Math.max(i2, i3 - (layoutParams.bottomMargin - height));
            }
        }
        return i + i3;
    }

    private int getChildVerticalGravity(int i) {
        i &= 112;
        return (i == 16 || i == 48 || i == 80) ? i : this.mGravity & 112;
    }

    private void addCustomViewsWithGravity(List<View> list, int i) {
        int i2 = 0;
        int i3 = 1;
        int i4 = ViewCompat.getLayoutDirection(this) == i3 ? i3 : i2;
        int childCount = getChildCount();
        i = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this));
        list.clear();
        View childAt;
        if (i4 != 0) {
            for (childCount -= i3; childCount >= 0; childCount--) {
                childAt = getChildAt(childCount);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.mViewType == 0 && shouldLayout(childAt) && getChildHorizontalGravity(layoutParams.gravity) == i) {
                    list.add(childAt);
                }
            }
            return;
        }
        while (i2 < childCount) {
            childAt = getChildAt(i2);
            LayoutParams layoutParams2 = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams2.mViewType == 0 && shouldLayout(childAt) && getChildHorizontalGravity(layoutParams2.gravity) == i) {
                list.add(childAt);
            }
            i2++;
        }
    }

    private int getChildHorizontalGravity(int i) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        i = GravityCompat.getAbsoluteGravity(i, layoutDirection) & 7;
        int i2 = 1;
        if (i != i2) {
            int i3 = 3;
            if (i != i3) {
                int i4 = 5;
                if (i != i4) {
                    if (layoutDirection == i2) {
                        i3 = i4;
                    }
                    return i3;
                }
            }
        }
        return i;
    }

    private boolean shouldLayout(View view) {
        return (view == null || view.getParent() != this || view.getVisibility() == 8) ? false : true;
    }

    private int getHorizontalMargins(View view) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        return MarginLayoutParamsCompat.getMarginStart(marginLayoutParams) + MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams);
    }

    private int getVerticalMargins(View view) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        return marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof android.support.v7.app.ActionBar.LayoutParams) {
            return new LayoutParams((android.support.v7.app.ActionBar.LayoutParams) layoutParams);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        int i = -2;
        return new LayoutParams(i, i);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return super.checkLayoutParams(layoutParams) && (layoutParams instanceof LayoutParams);
    }

    private static boolean isCustomView(View view) {
        return ((LayoutParams) view.getLayoutParams()).mViewType == 0;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public DecorToolbar getWrapper() {
        if (this.mWrapper == null) {
            this.mWrapper = new ToolbarWidgetWrapper(this, true);
        }
        return this.mWrapper;
    }

    void removeChildrenForExpandedActionView() {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (!(((LayoutParams) childAt.getLayoutParams()).mViewType == 2 || childAt == this.mMenuView)) {
                removeViewAt(childCount);
                this.mHiddenViews.add(childAt);
            }
        }
    }

    void addChildrenForExpandedActionView() {
        for (int size = this.mHiddenViews.size() - 1; size >= 0; size--) {
            addView((View) this.mHiddenViews.get(size));
        }
        this.mHiddenViews.clear();
    }

    private boolean isChildOrHidden(View view) {
        return view.getParent() == this || this.mHiddenViews.contains(view);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setCollapsible(boolean z) {
        this.mCollapsible = z;
        requestLayout();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setMenuCallbacks(Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
        if (this.mMenuView != null) {
            this.mMenuView.setMenuCallbacks(callback, callback2);
        }
    }

    private void ensureContentInsets() {
        if (this.mContentInsets == null) {
            this.mContentInsets = new RtlSpacingHelper();
        }
    }

    ActionMenuPresenter getOuterActionMenuPresenter() {
        return this.mOuterActionMenuPresenter;
    }

    Context getPopupContext() {
        return this.mPopupContext;
    }
}
