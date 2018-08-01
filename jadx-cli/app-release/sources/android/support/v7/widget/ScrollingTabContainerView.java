package android.support.v7.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.appcompat.R;
import android.support.v7.view.ActionBarPolicy;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ScrollingTabContainerView extends HorizontalScrollView implements OnItemSelectedListener {
    private static final int FADE_DURATION = 200;
    private static final String TAG = "ScrollingTabContainerView";
    private static final Interpolator sAlphaInterpolator = new DecelerateInterpolator();
    private boolean mAllowCollapse;
    private int mContentHeight;
    int mMaxTabWidth;
    private int mSelectedTabIndex;
    int mStackedTabMaxWidth;
    private TabClickListener mTabClickListener;
    LinearLayoutCompat mTabLayout;
    Runnable mTabSelector;
    private Spinner mTabSpinner;
    protected final VisibilityAnimListener mVisAnimListener = new VisibilityAnimListener();
    protected ViewPropertyAnimator mVisibilityAnim;

    private class TabAdapter extends BaseAdapter {
        public long getItemId(int i) {
            return (long) i;
        }

        TabAdapter() {
        }

        public int getCount() {
            return ScrollingTabContainerView.this.mTabLayout.getChildCount();
        }

        public Object getItem(int i) {
            return ((TabView) ScrollingTabContainerView.this.mTabLayout.getChildAt(i)).getTab();
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                return ScrollingTabContainerView.this.createTabView((Tab) getItem(i), true);
            }
            ((TabView) view).bindTab((Tab) getItem(i));
            return view;
        }
    }

    private class TabClickListener implements OnClickListener {
        TabClickListener() {
        }

        public void onClick(View view) {
            ((TabView) view).getTab().select();
            int childCount = ScrollingTabContainerView.this.mTabLayout.getChildCount();
            boolean z = false;
            for (int i = z; i < childCount; i++) {
                View childAt = ScrollingTabContainerView.this.mTabLayout.getChildAt(i);
                childAt.setSelected(childAt == view ? true : z);
            }
        }
    }

    protected class VisibilityAnimListener extends AnimatorListenerAdapter {
        private boolean mCanceled = false;
        private int mFinalVisibility;

        protected VisibilityAnimListener() {
        }

        public VisibilityAnimListener withFinalVisibility(ViewPropertyAnimator viewPropertyAnimator, int i) {
            this.mFinalVisibility = i;
            ScrollingTabContainerView.this.mVisibilityAnim = viewPropertyAnimator;
            return this;
        }

        public void onAnimationStart(Animator animator) {
            boolean z = false;
            ScrollingTabContainerView.this.setVisibility(z);
            this.mCanceled = z;
        }

        public void onAnimationEnd(Animator animator) {
            if (!this.mCanceled) {
                ScrollingTabContainerView.this.mVisibilityAnim = null;
                ScrollingTabContainerView.this.setVisibility(this.mFinalVisibility);
            }
        }

        public void onAnimationCancel(Animator animator) {
            this.mCanceled = true;
        }
    }

    private class TabView extends LinearLayoutCompat {
        private final int[] BG_ATTRS;
        private View mCustomView;
        private ImageView mIconView;
        private Tab mTab;
        private TextView mTextView;

        public TabView(Context context, Tab tab, boolean z) {
            AttributeSet attributeSet = null;
            super(context, attributeSet, R.attr.actionBarTabStyle);
            int[] iArr = new int[1];
            int i = 0;
            iArr[i] = 16842964;
            this.BG_ATTRS = iArr;
            this.mTab = tab;
            TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, this.BG_ATTRS, R.attr.actionBarTabStyle, i);
            if (obtainStyledAttributes.hasValue(i)) {
                setBackgroundDrawable(obtainStyledAttributes.getDrawable(i));
            }
            obtainStyledAttributes.recycle();
            if (z) {
                setGravity(8388627);
            }
            update();
        }

        public void bindTab(Tab tab) {
            this.mTab = tab;
            update();
        }

        public void setSelected(boolean z) {
            Object obj = isSelected() != z ? 1 : null;
            super.setSelected(z);
            if (obj != null && z) {
                sendAccessibilityEvent(4);
            }
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(Tab.class.getName());
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(Tab.class.getName());
        }

        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (ScrollingTabContainerView.this.mMaxTabWidth > 0 && getMeasuredWidth() > ScrollingTabContainerView.this.mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(ScrollingTabContainerView.this.mMaxTabWidth, 1073741824), i2);
            }
        }

        public void update() {
            Tab tab = this.mTab;
            View customView = tab.getCustomView();
            int i = 8;
            CharSequence charSequence = null;
            if (customView != null) {
                TabView parent = customView.getParent();
                if (parent != this) {
                    if (parent != null) {
                        parent.removeView(customView);
                    }
                    addView(customView);
                }
                this.mCustomView = customView;
                if (this.mTextView != null) {
                    this.mTextView.setVisibility(i);
                }
                if (this.mIconView != null) {
                    this.mIconView.setVisibility(i);
                    this.mIconView.setImageDrawable(charSequence);
                    return;
                }
                return;
            }
            if (this.mCustomView != null) {
                removeView(this.mCustomView);
                this.mCustomView = charSequence;
            }
            Drawable icon = tab.getIcon();
            CharSequence text = tab.getText();
            int i2 = 16;
            int i3 = 0;
            int i4 = -2;
            if (icon != null) {
                if (this.mIconView == null) {
                    View appCompatImageView = new AppCompatImageView(getContext());
                    LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(i4, i4);
                    layoutParams.gravity = i2;
                    appCompatImageView.setLayoutParams(layoutParams);
                    addView(appCompatImageView, i3);
                    this.mIconView = appCompatImageView;
                }
                this.mIconView.setImageDrawable(icon);
                this.mIconView.setVisibility(i3);
            } else if (this.mIconView != null) {
                this.mIconView.setVisibility(i);
                this.mIconView.setImageDrawable(charSequence);
            }
            int isEmpty = TextUtils.isEmpty(text) ^ 1;
            if (isEmpty != 0) {
                if (this.mTextView == null) {
                    View appCompatTextView = new AppCompatTextView(getContext(), charSequence, R.attr.actionBarTabTextStyle);
                    appCompatTextView.setEllipsize(TruncateAt.END);
                    LayoutParams layoutParams2 = new LinearLayoutCompat.LayoutParams(i4, i4);
                    layoutParams2.gravity = i2;
                    appCompatTextView.setLayoutParams(layoutParams2);
                    addView(appCompatTextView);
                    this.mTextView = appCompatTextView;
                }
                this.mTextView.setText(text);
                this.mTextView.setVisibility(i3);
            } else if (this.mTextView != null) {
                this.mTextView.setVisibility(i);
                this.mTextView.setText(charSequence);
            }
            if (this.mIconView != null) {
                this.mIconView.setContentDescription(tab.getContentDescription());
            }
            if (isEmpty == 0) {
                charSequence = tab.getContentDescription();
            }
            TooltipCompat.setTooltipText(this, charSequence);
        }

        public Tab getTab() {
            return this.mTab;
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public ScrollingTabContainerView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(context);
        setContentHeight(actionBarPolicy.getTabContainerHeight());
        this.mStackedTabMaxWidth = actionBarPolicy.getStackedTabMaxWidth();
        this.mTabLayout = createTabLayout();
        addView(this.mTabLayout, new LayoutParams(-2, -1));
    }

    public void onMeasure(int i, int i2) {
        i2 = MeasureSpec.getMode(i);
        boolean z = false;
        boolean z2 = true;
        int i3 = 1073741824;
        boolean z3 = i2 == i3 ? z2 : z;
        setFillViewport(z3);
        boolean childCount = this.mTabLayout.getChildCount();
        if (childCount <= z2 || !(i2 == i3 || i2 == Integer.MIN_VALUE)) {
            this.mMaxTabWidth = -1;
        } else {
            boolean z4 = true;
            if (childCount > z4) {
                this.mMaxTabWidth = (int) (((float) MeasureSpec.getSize(i)) * 0.4f);
            } else {
                this.mMaxTabWidth = MeasureSpec.getSize(i) / z4;
            }
            this.mMaxTabWidth = Math.min(this.mMaxTabWidth, this.mStackedTabMaxWidth);
        }
        i2 = MeasureSpec.makeMeasureSpec(this.mContentHeight, i3);
        if (z3 || !this.mAllowCollapse) {
            z2 = z;
        }
        if (z2) {
            this.mTabLayout.measure(z, i2);
            if (this.mTabLayout.getMeasuredWidth() > MeasureSpec.getSize(i)) {
                performCollapse();
            } else {
                performExpand();
            }
        } else {
            performExpand();
        }
        int measuredWidth = getMeasuredWidth();
        super.onMeasure(i, i2);
        i = getMeasuredWidth();
        if (z3 && measuredWidth != i) {
            setTabSelected(this.mSelectedTabIndex);
        }
    }

    private boolean isCollapsed() {
        return this.mTabSpinner != null && this.mTabSpinner.getParent() == this;
    }

    public void setAllowCollapse(boolean z) {
        this.mAllowCollapse = z;
    }

    private void performCollapse() {
        if (!isCollapsed()) {
            if (this.mTabSpinner == null) {
                this.mTabSpinner = createSpinner();
            }
            removeView(this.mTabLayout);
            addView(this.mTabSpinner, new LayoutParams(-2, -1));
            if (this.mTabSpinner.getAdapter() == null) {
                this.mTabSpinner.setAdapter(new TabAdapter());
            }
            if (this.mTabSelector != null) {
                removeCallbacks(this.mTabSelector);
                this.mTabSelector = null;
            }
            this.mTabSpinner.setSelection(this.mSelectedTabIndex);
        }
    }

    private boolean performExpand() {
        boolean z = false;
        if (!isCollapsed()) {
            return z;
        }
        removeView(this.mTabSpinner);
        addView(this.mTabLayout, new LayoutParams(-2, -1));
        setTabSelected(this.mTabSpinner.getSelectedItemPosition());
        return z;
    }

    public void setTabSelected(int i) {
        this.mSelectedTabIndex = i;
        int childCount = this.mTabLayout.getChildCount();
        boolean z = false;
        int i2 = z;
        while (i2 < childCount) {
            View childAt = this.mTabLayout.getChildAt(i2);
            boolean z2 = i2 == i ? true : z;
            childAt.setSelected(z2);
            if (z2) {
                animateToTab(i);
            }
            i2++;
        }
        if (this.mTabSpinner != null && i >= 0) {
            this.mTabSpinner.setSelection(i);
        }
    }

    public void setContentHeight(int i) {
        this.mContentHeight = i;
        requestLayout();
    }

    private LinearLayoutCompat createTabLayout() {
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(getContext(), null, R.attr.actionBarTabBarStyle);
        linearLayoutCompat.setMeasureWithLargestChildEnabled(true);
        linearLayoutCompat.setGravity(17);
        linearLayoutCompat.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
        return linearLayoutCompat;
    }

    private Spinner createSpinner() {
        Spinner appCompatSpinner = new AppCompatSpinner(getContext(), null, R.attr.actionDropDownStyle);
        appCompatSpinner.setLayoutParams(new LinearLayoutCompat.LayoutParams(-2, -1));
        appCompatSpinner.setOnItemSelectedListener(this);
        return appCompatSpinner;
    }

    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ActionBarPolicy actionBarPolicy = ActionBarPolicy.get(getContext());
        setContentHeight(actionBarPolicy.getTabContainerHeight());
        this.mStackedTabMaxWidth = actionBarPolicy.getStackedTabMaxWidth();
    }

    public void animateToVisibility(int i) {
        if (this.mVisibilityAnim != null) {
            this.mVisibilityAnim.cancel();
        }
        long j = 200;
        float f = 0.0f;
        ViewPropertyAnimator alpha;
        if (i == 0) {
            if (getVisibility() != 0) {
                setAlpha(f);
            }
            alpha = animate().alpha(1.0f);
            alpha.setDuration(j);
            alpha.setInterpolator(sAlphaInterpolator);
            alpha.setListener(this.mVisAnimListener.withFinalVisibility(alpha, i));
            alpha.start();
            return;
        }
        alpha = animate().alpha(f);
        alpha.setDuration(j);
        alpha.setInterpolator(sAlphaInterpolator);
        alpha.setListener(this.mVisAnimListener.withFinalVisibility(alpha, i));
        alpha.start();
    }

    public void animateToTab(int i) {
        final View childAt = this.mTabLayout.getChildAt(i);
        if (this.mTabSelector != null) {
            removeCallbacks(this.mTabSelector);
        }
        this.mTabSelector = new Runnable() {
            public void run() {
                ScrollingTabContainerView.this.smoothScrollTo(childAt.getLeft() - ((ScrollingTabContainerView.this.getWidth() - childAt.getWidth()) / 2), 0);
                ScrollingTabContainerView.this.mTabSelector = null;
            }
        };
        post(this.mTabSelector);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mTabSelector != null) {
            post(this.mTabSelector);
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mTabSelector != null) {
            removeCallbacks(this.mTabSelector);
        }
    }

    TabView createTabView(Tab tab, boolean z) {
        TabView tabView = new TabView(getContext(), tab, z);
        if (z) {
            tabView.setBackgroundDrawable(null);
            tabView.setLayoutParams(new AbsListView.LayoutParams(-1, this.mContentHeight));
        } else {
            tabView.setFocusable(true);
            if (this.mTabClickListener == null) {
                this.mTabClickListener = new TabClickListener();
            }
            tabView.setOnClickListener(this.mTabClickListener);
        }
        return tabView;
    }

    public void addTab(Tab tab, boolean z) {
        boolean z2 = false;
        View createTabView = createTabView(tab, z2);
        this.mTabLayout.addView(createTabView, new LinearLayoutCompat.LayoutParams(z2, -1, 1.0f));
        if (this.mTabSpinner != null) {
            ((TabAdapter) this.mTabSpinner.getAdapter()).notifyDataSetChanged();
        }
        if (z) {
            createTabView.setSelected(true);
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void addTab(Tab tab, int i, boolean z) {
        boolean z2 = false;
        View createTabView = createTabView(tab, z2);
        this.mTabLayout.addView(createTabView, i, new LinearLayoutCompat.LayoutParams(z2, -1, 1.0f));
        if (this.mTabSpinner != null) {
            ((TabAdapter) this.mTabSpinner.getAdapter()).notifyDataSetChanged();
        }
        if (z) {
            createTabView.setSelected(true);
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void updateTab(int i) {
        ((TabView) this.mTabLayout.getChildAt(i)).update();
        if (this.mTabSpinner != null) {
            ((TabAdapter) this.mTabSpinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void removeTabAt(int i) {
        this.mTabLayout.removeViewAt(i);
        if (this.mTabSpinner != null) {
            ((TabAdapter) this.mTabSpinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void removeAllTabs() {
        this.mTabLayout.removeAllViews();
        if (this.mTabSpinner != null) {
            ((TabAdapter) this.mTabSpinner.getAdapter()).notifyDataSetChanged();
        }
        if (this.mAllowCollapse) {
            requestLayout();
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        ((TabView) view).getTab().select();
    }
}
