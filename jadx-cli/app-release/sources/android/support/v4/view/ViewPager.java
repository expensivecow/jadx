package android.support.v4.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.Scroller;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPager extends ViewGroup {
    private static final int CLOSE_ENOUGH = 2;
    private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ItemInfo itemInfo, ItemInfo itemInfo2) {
            return itemInfo.position - itemInfo2.position;
        }
    };
    private static final boolean DEBUG = false;
    private static final int DEFAULT_GUTTER_SIZE = 16;
    private static final int DEFAULT_OFFSCREEN_PAGES = 1;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private static final int INVALID_POINTER = -1;
    static final int[] LAYOUT_ATTRS = new int[]{16842931};
    private static final int MAX_SETTLE_DURATION = 600;
    private static final int MIN_DISTANCE_FOR_FLING = 25;
    private static final int MIN_FLING_VELOCITY = 400;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_SETTLING = 2;
    private static final String TAG = "ViewPager";
    private static final boolean USE_CACHE = false;
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float f) {
            float f2 = 1.0f;
            f -= f2;
            return ((((f * f) * f) * f) * f) + f2;
        }
    };
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
    private int mActivePointerId;
    PagerAdapter mAdapter;
    private List<OnAdapterChangeListener> mAdapterChangeListeners;
    private int mBottomPageBounds;
    private boolean mCalledSuper;
    private int mChildHeightMeasureSpec;
    private int mChildWidthMeasureSpec;
    private int mCloseEnough;
    int mCurItem;
    private int mDecorChildCount;
    private int mDefaultGutterSize;
    private int mDrawingOrder;
    private ArrayList<View> mDrawingOrderedChildren;
    private final Runnable mEndScrollRunnable;
    private int mExpectedAdapterCount;
    private long mFakeDragBeginTime;
    private boolean mFakeDragging;
    private boolean mFirstLayout;
    private float mFirstOffset;
    private int mFlingDistance;
    private int mGutterSize;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private OnPageChangeListener mInternalPageChangeListener;
    private boolean mIsBeingDragged;
    private boolean mIsScrollStarted;
    private boolean mIsUnableToDrag;
    private final ArrayList<ItemInfo> mItems = new ArrayList();
    private float mLastMotionX;
    private float mLastMotionY;
    private float mLastOffset;
    private EdgeEffect mLeftEdge;
    private Drawable mMarginDrawable;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private boolean mNeedCalculatePageOffsets;
    private PagerObserver mObserver;
    private int mOffscreenPageLimit;
    private OnPageChangeListener mOnPageChangeListener;
    private List<OnPageChangeListener> mOnPageChangeListeners;
    private int mPageMargin;
    private PageTransformer mPageTransformer;
    private int mPageTransformerLayerType;
    private boolean mPopulatePending;
    private Parcelable mRestoredAdapterState;
    private ClassLoader mRestoredClassLoader;
    private int mRestoredCurItem;
    private EdgeEffect mRightEdge;
    private int mScrollState;
    private Scroller mScroller;
    private boolean mScrollingCacheEnabled;
    private final ItemInfo mTempItem = new ItemInfo();
    private final Rect mTempRect = new Rect();
    private int mTopPageBounds;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;

    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface DecorView {
    }

    static class ItemInfo {
        Object object;
        float offset;
        int position;
        boolean scrolling;
        float widthFactor;

        ItemInfo() {
        }
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        int childIndex;
        public int gravity;
        public boolean isDecor;
        boolean needsMeasure;
        int position;
        float widthFactor = 0.0f;

        public LayoutParams() {
            int i = -1;
            super(i, i);
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ViewPager.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInteger(0, 48);
            obtainStyledAttributes.recycle();
        }
    }

    public interface OnAdapterChangeListener {
        void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter, @Nullable PagerAdapter pagerAdapter2);
    }

    public interface OnPageChangeListener {
        void onPageScrollStateChanged(int i);

        void onPageScrolled(int i, float f, int i2);

        void onPageSelected(int i);
    }

    public interface PageTransformer {
        void transformPage(View view, float f);
    }

    private class PagerObserver extends DataSetObserver {
        PagerObserver() {
        }

        public void onChanged() {
            ViewPager.this.dataSetChanged();
        }

        public void onInvalidated() {
            ViewPager.this.dataSetChanged();
        }
    }

    static class ViewPositionComparator implements Comparator<View> {
        ViewPositionComparator() {
        }

        public int compare(View view, View view2) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            LayoutParams layoutParams2 = (LayoutParams) view2.getLayoutParams();
            if (layoutParams.isDecor == layoutParams2.isDecor) {
                return layoutParams.position - layoutParams2.position;
            }
            return layoutParams.isDecor ? 1 : -1;
        }
    }

    class MyAccessibilityDelegate extends AccessibilityDelegateCompat {
        MyAccessibilityDelegate() {
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(ViewPager.class.getName());
            accessibilityEvent.setScrollable(canScroll());
            if (accessibilityEvent.getEventType() == 4096 && ViewPager.this.mAdapter != null) {
                accessibilityEvent.setItemCount(ViewPager.this.mAdapter.getCount());
                accessibilityEvent.setFromIndex(ViewPager.this.mCurItem);
                accessibilityEvent.setToIndex(ViewPager.this.mCurItem);
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            accessibilityNodeInfoCompat.setClassName(ViewPager.class.getName());
            accessibilityNodeInfoCompat.setScrollable(canScroll());
            if (ViewPager.this.canScrollHorizontally(1)) {
                accessibilityNodeInfoCompat.addAction(4096);
            }
            if (ViewPager.this.canScrollHorizontally(-1)) {
                accessibilityNodeInfoCompat.addAction(8192);
            }
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            boolean performAccessibilityAction = super.performAccessibilityAction(view, i, bundle);
            boolean z = true;
            if (performAccessibilityAction) {
                return z;
            }
            boolean z2 = false;
            if (i != 4096) {
                if (i != 8192 || !ViewPager.this.canScrollHorizontally(-1)) {
                    return z2;
                }
                ViewPager.this.setCurrentItem(ViewPager.this.mCurItem - z);
                return z;
            } else if (!ViewPager.this.canScrollHorizontally(z)) {
                return z2;
            } else {
                ViewPager.this.setCurrentItem(ViewPager.this.mCurItem + z);
                return z;
            }
        }

        private boolean canScroll() {
            boolean z = true;
            return (ViewPager.this.mAdapter == null || ViewPager.this.mAdapter.getCount() <= z) ? false : z;
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
        Parcelable adapterState;
        ClassLoader loader;
        int position;

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.position);
            parcel.writeParcelable(this.adapterState, i);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("FragmentPager.SavedState{");
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append(" position=");
            stringBuilder.append(this.position);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            if (classLoader == null) {
                classLoader = getClass().getClassLoader();
            }
            this.position = parcel.readInt();
            this.adapterState = parcel.readParcelable(classLoader);
            this.loader = classLoader;
        }
    }

    public static class SimpleOnPageChangeListener implements OnPageChangeListener {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int i2) {
        }

        public void onPageSelected(int i) {
        }
    }

    public ViewPager(Context context) {
        super(context);
        int i = -1;
        this.mRestoredCurItem = i;
        Object obj = null;
        this.mRestoredAdapterState = obj;
        this.mRestoredClassLoader = obj;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        boolean z = true;
        this.mOffscreenPageLimit = z;
        this.mActivePointerId = i;
        this.mFirstLayout = z;
        boolean z2 = false;
        this.mNeedCalculatePageOffsets = z2;
        this.mEndScrollRunnable = new Runnable() {
            public void run() {
                ViewPager.this.setScrollState(0);
                ViewPager.this.populate();
            }
        };
        this.mScrollState = z2;
        initViewPager();
    }

    public ViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int i = -1;
        this.mRestoredCurItem = i;
        Object obj = null;
        this.mRestoredAdapterState = obj;
        this.mRestoredClassLoader = obj;
        this.mFirstOffset = -3.4028235E38f;
        this.mLastOffset = Float.MAX_VALUE;
        boolean z = true;
        this.mOffscreenPageLimit = z;
        this.mActivePointerId = i;
        this.mFirstLayout = z;
        boolean z2 = false;
        this.mNeedCalculatePageOffsets = z2;
        this.mEndScrollRunnable = /* anonymous class already generated */;
        this.mScrollState = z2;
        initViewPager();
    }

    void initViewPager() {
        setWillNotDraw(false);
        setDescendantFocusability(262144);
        boolean z = true;
        setFocusable(z);
        Context context = getContext();
        this.mScroller = new Scroller(context, sInterpolator);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        float f = context.getResources().getDisplayMetrics().density;
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMinimumVelocity = (int) (400.0f * f);
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mLeftEdge = new EdgeEffect(context);
        this.mRightEdge = new EdgeEffect(context);
        this.mFlingDistance = (int) (25.0f * f);
        this.mCloseEnough = (int) (2.0f * f);
        this.mDefaultGutterSize = (int) (16.0f * f);
        ViewCompat.setAccessibilityDelegate(this, new MyAccessibilityDelegate());
        if (ViewCompat.getImportantForAccessibility(this) == 0) {
            ViewCompat.setImportantForAccessibility(this, z);
        }
        ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener() {
            private final Rect mTempRect = new Rect();

            public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                WindowInsetsCompat onApplyWindowInsets = ViewCompat.onApplyWindowInsets(view, windowInsetsCompat);
                if (onApplyWindowInsets.isConsumed()) {
                    return onApplyWindowInsets;
                }
                Rect rect = this.mTempRect;
                rect.left = onApplyWindowInsets.getSystemWindowInsetLeft();
                rect.top = onApplyWindowInsets.getSystemWindowInsetTop();
                rect.right = onApplyWindowInsets.getSystemWindowInsetRight();
                rect.bottom = onApplyWindowInsets.getSystemWindowInsetBottom();
                int childCount = ViewPager.this.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    WindowInsetsCompat dispatchApplyWindowInsets = ViewCompat.dispatchApplyWindowInsets(ViewPager.this.getChildAt(i), onApplyWindowInsets);
                    rect.left = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetLeft(), rect.left);
                    rect.top = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetTop(), rect.top);
                    rect.right = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetRight(), rect.right);
                    rect.bottom = Math.min(dispatchApplyWindowInsets.getSystemWindowInsetBottom(), rect.bottom);
                }
                return onApplyWindowInsets.replaceSystemWindowInsets(rect.left, rect.top, rect.right, rect.bottom);
            }
        });
    }

    protected void onDetachedFromWindow() {
        removeCallbacks(this.mEndScrollRunnable);
        if (!(this.mScroller == null || this.mScroller.isFinished())) {
            this.mScroller.abortAnimation();
        }
        super.onDetachedFromWindow();
    }

    void setScrollState(int i) {
        if (this.mScrollState != i) {
            this.mScrollState = i;
            if (this.mPageTransformer != null) {
                enableLayers(i != 0);
            }
            dispatchOnScrollStateChanged(i);
        }
    }

    public void setAdapter(PagerAdapter pagerAdapter) {
        ClassLoader classLoader = null;
        int i = 0;
        if (this.mAdapter != null) {
            this.mAdapter.setViewPagerObserver(classLoader);
            this.mAdapter.startUpdate((ViewGroup) this);
            for (int i2 = i; i2 < this.mItems.size(); i2++) {
                ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
                this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
            }
            this.mAdapter.finishUpdate((ViewGroup) this);
            this.mItems.clear();
            removeNonDecorViews();
            this.mCurItem = i;
            scrollTo(i, i);
        }
        PagerAdapter pagerAdapter2 = this.mAdapter;
        this.mAdapter = pagerAdapter;
        this.mExpectedAdapterCount = i;
        if (this.mAdapter != null) {
            if (this.mObserver == null) {
                this.mObserver = new PagerObserver();
            }
            this.mAdapter.setViewPagerObserver(this.mObserver);
            this.mPopulatePending = i;
            boolean z = this.mFirstLayout;
            boolean z2 = true;
            this.mFirstLayout = z2;
            this.mExpectedAdapterCount = this.mAdapter.getCount();
            if (this.mRestoredCurItem >= 0) {
                this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
                setCurrentItemInternal(this.mRestoredCurItem, i, z2);
                this.mRestoredCurItem = -1;
                this.mRestoredAdapterState = classLoader;
                this.mRestoredClassLoader = classLoader;
            } else if (z) {
                requestLayout();
            } else {
                populate();
            }
        }
        if (this.mAdapterChangeListeners != null && !this.mAdapterChangeListeners.isEmpty()) {
            int size = this.mAdapterChangeListeners.size();
            while (i < size) {
                ((OnAdapterChangeListener) this.mAdapterChangeListeners.get(i)).onAdapterChanged(this, pagerAdapter2, pagerAdapter);
                i++;
            }
        }
    }

    private void removeNonDecorViews() {
        int i = 0;
        while (i < getChildCount()) {
            if (!((LayoutParams) getChildAt(i).getLayoutParams()).isDecor) {
                removeViewAt(i);
                i--;
            }
            i++;
        }
    }

    public PagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void addOnAdapterChangeListener(@NonNull OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners == null) {
            this.mAdapterChangeListeners = new ArrayList();
        }
        this.mAdapterChangeListeners.add(onAdapterChangeListener);
    }

    public void removeOnAdapterChangeListener(@NonNull OnAdapterChangeListener onAdapterChangeListener) {
        if (this.mAdapterChangeListeners != null) {
            this.mAdapterChangeListeners.remove(onAdapterChangeListener);
        }
    }

    private int getClientWidth() {
        return (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
    }

    public void setCurrentItem(int i) {
        boolean z = false;
        this.mPopulatePending = z;
        setCurrentItemInternal(i, this.mFirstLayout ^ 1, z);
    }

    public void setCurrentItem(int i, boolean z) {
        boolean z2 = false;
        this.mPopulatePending = z2;
        setCurrentItemInternal(i, z, z2);
    }

    public int getCurrentItem() {
        return this.mCurItem;
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2) {
        setCurrentItemInternal(i, z, z2, 0);
    }

    void setCurrentItemInternal(int i, boolean z, boolean z2, int i2) {
        boolean z3 = false;
        if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
            setScrollingCacheEnabled(z3);
        } else if (z2 || this.mCurItem != i || this.mItems.size() == 0) {
            z2 = true;
            if (i < 0) {
                i = z3;
            } else if (i >= this.mAdapter.getCount()) {
                i = this.mAdapter.getCount() - z2;
            }
            int i3 = this.mOffscreenPageLimit;
            if (i > this.mCurItem + i3 || i < this.mCurItem - i3) {
                for (i3 = z3; i3 < this.mItems.size(); i3++) {
                    ((ItemInfo) this.mItems.get(i3)).scrolling = z2;
                }
            }
            if (this.mCurItem == i) {
                z2 = z3;
            }
            if (this.mFirstLayout) {
                this.mCurItem = i;
                if (z2) {
                    dispatchOnPageSelected(i);
                }
                requestLayout();
            } else {
                populate(i);
                scrollToItem(i, z, i2, z2);
            }
        } else {
            setScrollingCacheEnabled(z3);
        }
    }

    private void scrollToItem(int i, boolean z, int i2, boolean z2) {
        ItemInfo infoForPosition = infoForPosition(i);
        boolean z3 = false;
        int clientWidth = infoForPosition != null ? (int) (((float) getClientWidth()) * Math.max(this.mFirstOffset, Math.min(infoForPosition.offset, this.mLastOffset))) : z3;
        if (z) {
            smoothScrollTo(clientWidth, z3, i2);
            if (z2) {
                dispatchOnPageSelected(i);
                return;
            }
            return;
        }
        if (z2) {
            dispatchOnPageSelected(i);
        }
        completeScroll(z3);
        scrollTo(clientWidth, z3);
        pageScrolled(clientWidth);
    }

    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners == null) {
            this.mOnPageChangeListeners = new ArrayList();
        }
        this.mOnPageChangeListeners.add(onPageChangeListener);
    }

    public void removeOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        if (this.mOnPageChangeListeners != null) {
            this.mOnPageChangeListeners.remove(onPageChangeListener);
        }
    }

    public void clearOnPageChangeListeners() {
        if (this.mOnPageChangeListeners != null) {
            this.mOnPageChangeListeners.clear();
        }
    }

    public void setPageTransformer(boolean z, PageTransformer pageTransformer) {
        setPageTransformer(z, pageTransformer, 2);
    }

    public void setPageTransformer(boolean z, PageTransformer pageTransformer, int i) {
        boolean z2 = false;
        int i2 = 1;
        boolean z3 = pageTransformer != null ? i2 : z2;
        boolean z4 = z3 != (this.mPageTransformer != null ? i2 : z2) ? i2 : z2;
        this.mPageTransformer = pageTransformer;
        setChildrenDrawingOrderEnabled(z3);
        if (z3) {
            if (z) {
                i2 = 2;
            }
            this.mDrawingOrder = i2;
            this.mPageTransformerLayerType = i;
        } else {
            this.mDrawingOrder = z2;
        }
        if (z4) {
            populate();
        }
    }

    protected int getChildDrawingOrder(int i, int i2) {
        if (this.mDrawingOrder == 2) {
            i2 = (i - 1) - i2;
        }
        return ((LayoutParams) ((View) this.mDrawingOrderedChildren.get(i2)).getLayoutParams()).childIndex;
    }

    OnPageChangeListener setInternalPageChangeListener(OnPageChangeListener onPageChangeListener) {
        OnPageChangeListener onPageChangeListener2 = this.mInternalPageChangeListener;
        this.mInternalPageChangeListener = onPageChangeListener;
        return onPageChangeListener2;
    }

    public int getOffscreenPageLimit() {
        return this.mOffscreenPageLimit;
    }

    public void setOffscreenPageLimit(int i) {
        int i2 = 1;
        if (i < i2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Requested offscreen page limit ");
            stringBuilder.append(i);
            stringBuilder.append(" too small; defaulting to ");
            stringBuilder.append(i2);
            Log.w("ViewPager", stringBuilder.toString());
            i = i2;
        }
        if (i != this.mOffscreenPageLimit) {
            this.mOffscreenPageLimit = i;
            populate();
        }
    }

    public void setPageMargin(int i) {
        int i2 = this.mPageMargin;
        this.mPageMargin = i;
        int width = getWidth();
        recomputeScrollPosition(width, width, i, i2);
        requestLayout();
    }

    public int getPageMargin() {
        return this.mPageMargin;
    }

    public void setPageMarginDrawable(Drawable drawable) {
        this.mMarginDrawable = drawable;
        if (drawable != null) {
            refreshDrawableState();
        }
        setWillNotDraw(drawable == null);
        invalidate();
    }

    public void setPageMarginDrawable(@DrawableRes int i) {
        setPageMarginDrawable(ContextCompat.getDrawable(getContext(), i));
    }

    protected boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.mMarginDrawable;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mMarginDrawable;
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
    }

    float distanceInfluenceForSnapDuration(float f) {
        return (float) Math.sin((double) ((f - 0.5f) * 0.47123894f));
    }

    void smoothScrollTo(int i, int i2) {
        smoothScrollTo(i, i2, 0);
    }

    void smoothScrollTo(int i, int i2, int i3) {
        boolean z = false;
        if (getChildCount() == 0) {
            setScrollingCacheEnabled(z);
            return;
        }
        int currX;
        boolean z2 = true;
        boolean z3 = (this.mScroller == null || this.mScroller.isFinished()) ? z : z2;
        if (z3) {
            currX = this.mIsScrollStarted ? this.mScroller.getCurrX() : this.mScroller.getStartX();
            this.mScroller.abortAnimation();
            setScrollingCacheEnabled(z);
        } else {
            currX = getScrollX();
        }
        int i4 = currX;
        int scrollY = getScrollY();
        int i5 = i - i4;
        int i6 = i2 - scrollY;
        if (i5 == 0 && i6 == 0) {
            completeScroll(z);
            populate();
            setScrollState(z);
            return;
        }
        setScrollingCacheEnabled(z2);
        setScrollState(2);
        i = getClientWidth();
        i2 = i / 2;
        float f = 1.0f;
        float f2 = (float) i;
        float f3 = (float) i2;
        f3 += distanceInfluenceForSnapDuration(Math.min(f, (((float) Math.abs(i5)) * f) / f2)) * f3;
        i3 = Math.abs(i3);
        if (i3 > 0) {
            i = 4 * Math.round(1000.0f * Math.abs(f3 / ((float) i3)));
        } else {
            i = (int) (((((float) Math.abs(i5)) / ((f2 * this.mAdapter.getPageWidth(this.mCurItem)) + ((float) this.mPageMargin))) + f) * 100.0f);
        }
        int min = Math.min(i, 600);
        this.mIsScrollStarted = z;
        this.mScroller.startScroll(i4, scrollY, i5, i6, min);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    ItemInfo addNewItem(int i, int i2) {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.position = i;
        itemInfo.object = this.mAdapter.instantiateItem((ViewGroup) this, i);
        itemInfo.widthFactor = this.mAdapter.getPageWidth(i);
        if (i2 < 0 || i2 >= this.mItems.size()) {
            this.mItems.add(itemInfo);
        } else {
            this.mItems.add(i2, itemInfo);
        }
        return itemInfo;
    }

    void dataSetChanged() {
        int count = this.mAdapter.getCount();
        this.mExpectedAdapterCount = count;
        boolean z = true;
        boolean z2 = false;
        boolean z3 = (this.mItems.size() >= (this.mOffscreenPageLimit * 2) + z || this.mItems.size() >= count) ? z2 : z;
        boolean z4 = z3;
        int i = this.mCurItem;
        int i2 = z2;
        boolean z5 = i2;
        while (i2 < this.mItems.size()) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
            int itemPosition = this.mAdapter.getItemPosition(itemInfo.object);
            if (itemPosition != -1) {
                if (itemPosition == -2) {
                    this.mItems.remove(i2);
                    i2--;
                    if (!z5) {
                        this.mAdapter.startUpdate((ViewGroup) this);
                        z5 = z;
                    }
                    this.mAdapter.destroyItem((ViewGroup) this, itemInfo.position, itemInfo.object);
                    if (this.mCurItem == itemInfo.position) {
                        i = Math.max(z2, Math.min(this.mCurItem, count - 1));
                    }
                } else if (itemInfo.position != itemPosition) {
                    if (itemInfo.position == this.mCurItem) {
                        i = itemPosition;
                    }
                    itemInfo.position = itemPosition;
                }
                z4 = z;
            }
            i2 += z;
        }
        if (z5) {
            this.mAdapter.finishUpdate((ViewGroup) this);
        }
        Collections.sort(this.mItems, COMPARATOR);
        if (z4) {
            count = getChildCount();
            for (i2 = z2; i2 < count; i2++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i2).getLayoutParams();
                if (!layoutParams.isDecor) {
                    layoutParams.widthFactor = 0.0f;
                }
            }
            setCurrentItemInternal(i, z2, z);
            requestLayout();
        }
    }

    void populate() {
        populate(this.mCurItem);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void populate(int r19) {
        /*
        r18 = this;
        r0 = r18;
        r1 = r19;
        r2 = r0.mCurItem;
        if (r2 == r1) goto L_0x0011;
    L_0x0008:
        r2 = r0.mCurItem;
        r2 = r0.infoForPosition(r2);
        r0.mCurItem = r1;
        goto L_0x0012;
    L_0x0011:
        r2 = 0;
    L_0x0012:
        r1 = r0.mAdapter;
        if (r1 != 0) goto L_0x001a;
    L_0x0016:
        r18.sortChildDrawingOrder();
        return;
    L_0x001a:
        r1 = r0.mPopulatePending;
        if (r1 == 0) goto L_0x0022;
    L_0x001e:
        r18.sortChildDrawingOrder();
        return;
    L_0x0022:
        r1 = r18.getWindowToken();
        if (r1 != 0) goto L_0x0029;
    L_0x0028:
        return;
    L_0x0029:
        r1 = r0.mAdapter;
        r1.startUpdate(r0);
        r1 = r0.mOffscreenPageLimit;
        r4 = r0.mCurItem;
        r4 = r4 - r1;
        r5 = 0;
        r4 = java.lang.Math.max(r5, r4);
        r6 = r0.mAdapter;
        r6 = r6.getCount();
        r7 = r6 + -1;
        r8 = r0.mCurItem;
        r8 = r8 + r1;
        r1 = java.lang.Math.min(r7, r8);
        r7 = r0.mExpectedAdapterCount;
        if (r6 == r7) goto L_0x00a3;
    L_0x004b:
        r1 = r18.getResources();	 Catch:{ NotFoundException -> 0x0058 }
        r2 = r18.getId();	 Catch:{ NotFoundException -> 0x0058 }
        r1 = r1.getResourceName(r2);	 Catch:{ NotFoundException -> 0x0058 }
        goto L_0x0060;
    L_0x0058:
        r1 = r18.getId();
        r1 = java.lang.Integer.toHexString(r1);
    L_0x0060:
        r2 = new java.lang.IllegalStateException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "The application's PagerAdapter changed the adapter's contents without calling PagerAdapter#notifyDataSetChanged! Expected adapter item count: ";
        r3.append(r4);
        r4 = r0.mExpectedAdapterCount;
        r3.append(r4);
        r4 = ", found: ";
        r3.append(r4);
        r3.append(r6);
        r4 = " Pager id: ";
        r3.append(r4);
        r3.append(r1);
        r1 = " Pager class: ";
        r3.append(r1);
        r1 = r18.getClass();
        r3.append(r1);
        r1 = " Problematic adapter: ";
        r3.append(r1);
        r1 = r0.mAdapter;
        r1 = r1.getClass();
        r3.append(r1);
        r1 = r3.toString();
        r2.<init>(r1);
        throw r2;
    L_0x00a3:
        r7 = r5;
    L_0x00a4:
        r8 = r0.mItems;
        r8 = r8.size();
        if (r7 >= r8) goto L_0x00c4;
    L_0x00ac:
        r8 = r0.mItems;
        r8 = r8.get(r7);
        r8 = (android.support.v4.view.ViewPager.ItemInfo) r8;
        r9 = r8.position;
        r10 = r0.mCurItem;
        if (r9 < r10) goto L_0x00c1;
    L_0x00ba:
        r9 = r8.position;
        r10 = r0.mCurItem;
        if (r9 != r10) goto L_0x00c4;
    L_0x00c0:
        goto L_0x00c5;
    L_0x00c1:
        r7 = r7 + 1;
        goto L_0x00a4;
    L_0x00c4:
        r8 = 0;
    L_0x00c5:
        if (r8 != 0) goto L_0x00cf;
    L_0x00c7:
        if (r6 <= 0) goto L_0x00cf;
    L_0x00c9:
        r8 = r0.mCurItem;
        r8 = r0.addNewItem(r8, r7);
    L_0x00cf:
        r9 = 0;
        if (r8 == 0) goto L_0x01f2;
    L_0x00d2:
        r10 = r7 + -1;
        if (r10 < 0) goto L_0x00df;
    L_0x00d6:
        r11 = r0.mItems;
        r11 = r11.get(r10);
        r11 = (android.support.v4.view.ViewPager.ItemInfo) r11;
        goto L_0x00e0;
    L_0x00df:
        r11 = 0;
    L_0x00e0:
        r12 = r18.getClientWidth();
        r13 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r12 > 0) goto L_0x00ea;
    L_0x00e8:
        r3 = r9;
        goto L_0x00f7;
    L_0x00ea:
        r14 = r8.widthFactor;
        r14 = r13 - r14;
        r15 = r18.getPaddingLeft();
        r15 = (float) r15;
        r3 = (float) r12;
        r15 = r15 / r3;
        r3 = r14 + r15;
    L_0x00f7:
        r14 = r0.mCurItem;
        r14 = r14 + -1;
        r15 = r7;
        r7 = r9;
    L_0x00fd:
        if (r14 < 0) goto L_0x015d;
    L_0x00ff:
        r16 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1));
        if (r16 < 0) goto L_0x012b;
    L_0x0103:
        if (r14 >= r4) goto L_0x012b;
    L_0x0105:
        if (r11 != 0) goto L_0x0108;
    L_0x0107:
        goto L_0x015d;
    L_0x0108:
        r5 = r11.position;
        if (r14 != r5) goto L_0x0159;
    L_0x010c:
        r5 = r11.scrolling;
        if (r5 != 0) goto L_0x0159;
    L_0x0110:
        r5 = r0.mItems;
        r5.remove(r10);
        r5 = r0.mAdapter;
        r11 = r11.object;
        r5.destroyItem(r0, r14, r11);
        r10 = r10 + -1;
        r15 = r15 + -1;
        if (r10 < 0) goto L_0x0157;
    L_0x0122:
        r5 = r0.mItems;
        r5 = r5.get(r10);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x0158;
    L_0x012b:
        if (r11 == 0) goto L_0x0141;
    L_0x012d:
        r5 = r11.position;
        if (r14 != r5) goto L_0x0141;
    L_0x0131:
        r5 = r11.widthFactor;
        r7 = r7 + r5;
        r10 = r10 + -1;
        if (r10 < 0) goto L_0x0157;
    L_0x0138:
        r5 = r0.mItems;
        r5 = r5.get(r10);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x0158;
    L_0x0141:
        r5 = r10 + 1;
        r5 = r0.addNewItem(r14, r5);
        r5 = r5.widthFactor;
        r7 = r7 + r5;
        r15 = r15 + 1;
        if (r10 < 0) goto L_0x0157;
    L_0x014e:
        r5 = r0.mItems;
        r5 = r5.get(r10);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x0158;
    L_0x0157:
        r5 = 0;
    L_0x0158:
        r11 = r5;
    L_0x0159:
        r14 = r14 + -1;
        r5 = 0;
        goto L_0x00fd;
    L_0x015d:
        r3 = r8.widthFactor;
        r4 = r15 + 1;
        r5 = (r3 > r13 ? 1 : (r3 == r13 ? 0 : -1));
        if (r5 >= 0) goto L_0x01ef;
    L_0x0165:
        r5 = r0.mItems;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x0176;
    L_0x016d:
        r5 = r0.mItems;
        r5 = r5.get(r4);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x0177;
    L_0x0176:
        r5 = 0;
    L_0x0177:
        if (r12 > 0) goto L_0x017b;
    L_0x0179:
        r7 = r9;
        goto L_0x0183;
    L_0x017b:
        r7 = r18.getPaddingRight();
        r7 = (float) r7;
        r10 = (float) r12;
        r7 = r7 / r10;
        r7 = r7 + r13;
    L_0x0183:
        r10 = r0.mCurItem;
    L_0x0185:
        r10 = r10 + 1;
        if (r10 >= r6) goto L_0x01ef;
    L_0x0189:
        r11 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
        if (r11 < 0) goto L_0x01b9;
    L_0x018d:
        if (r10 <= r1) goto L_0x01b9;
    L_0x018f:
        if (r5 != 0) goto L_0x0192;
    L_0x0191:
        goto L_0x01ef;
    L_0x0192:
        r11 = r5.position;
        if (r10 != r11) goto L_0x01ee;
    L_0x0196:
        r11 = r5.scrolling;
        if (r11 != 0) goto L_0x01ee;
    L_0x019a:
        r11 = r0.mItems;
        r11.remove(r4);
        r11 = r0.mAdapter;
        r5 = r5.object;
        r11.destroyItem(r0, r10, r5);
        r5 = r0.mItems;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x01b7;
    L_0x01ae:
        r5 = r0.mItems;
        r5 = r5.get(r4);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x01ee;
    L_0x01b7:
        r5 = 0;
        goto L_0x01ee;
    L_0x01b9:
        if (r5 == 0) goto L_0x01d5;
    L_0x01bb:
        r11 = r5.position;
        if (r10 != r11) goto L_0x01d5;
    L_0x01bf:
        r5 = r5.widthFactor;
        r3 = r3 + r5;
        r4 = r4 + 1;
        r5 = r0.mItems;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x01b7;
    L_0x01cc:
        r5 = r0.mItems;
        r5 = r5.get(r4);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
        goto L_0x01ee;
    L_0x01d5:
        r5 = r0.addNewItem(r10, r4);
        r4 = r4 + 1;
        r5 = r5.widthFactor;
        r3 = r3 + r5;
        r5 = r0.mItems;
        r5 = r5.size();
        if (r4 >= r5) goto L_0x01b7;
    L_0x01e6:
        r5 = r0.mItems;
        r5 = r5.get(r4);
        r5 = (android.support.v4.view.ViewPager.ItemInfo) r5;
    L_0x01ee:
        goto L_0x0185;
    L_0x01ef:
        r0.calculatePageOffsets(r8, r15, r2);
    L_0x01f2:
        r1 = r0.mAdapter;
        r2 = r0.mCurItem;
        if (r8 == 0) goto L_0x01fb;
    L_0x01f8:
        r3 = r8.object;
        goto L_0x01fc;
    L_0x01fb:
        r3 = 0;
    L_0x01fc:
        r1.setPrimaryItem(r0, r2, r3);
        r1 = r0.mAdapter;
        r1.finishUpdate(r0);
        r1 = r18.getChildCount();
        r2 = 0;
    L_0x0209:
        if (r2 >= r1) goto L_0x0232;
    L_0x020b:
        r3 = r0.getChildAt(r2);
        r4 = r3.getLayoutParams();
        r4 = (android.support.v4.view.ViewPager.LayoutParams) r4;
        r4.childIndex = r2;
        r5 = r4.isDecor;
        if (r5 != 0) goto L_0x022f;
    L_0x021b:
        r5 = r4.widthFactor;
        r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
        if (r5 != 0) goto L_0x022f;
    L_0x0221:
        r3 = r0.infoForChild(r3);
        if (r3 == 0) goto L_0x022f;
    L_0x0227:
        r5 = r3.widthFactor;
        r4.widthFactor = r5;
        r3 = r3.position;
        r4.position = r3;
    L_0x022f:
        r2 = r2 + 1;
        goto L_0x0209;
    L_0x0232:
        r18.sortChildDrawingOrder();
        r1 = r18.hasFocus();
        if (r1 == 0) goto L_0x0271;
    L_0x023b:
        r1 = r18.findFocus();
        if (r1 == 0) goto L_0x0246;
    L_0x0241:
        r3 = r0.infoForAnyChild(r1);
        goto L_0x0247;
    L_0x0246:
        r3 = 0;
    L_0x0247:
        if (r3 == 0) goto L_0x024f;
    L_0x0249:
        r1 = r3.position;
        r2 = r0.mCurItem;
        if (r1 == r2) goto L_0x0271;
    L_0x024f:
        r1 = 0;
    L_0x0250:
        r2 = r18.getChildCount();
        if (r1 >= r2) goto L_0x0271;
    L_0x0256:
        r2 = r0.getChildAt(r1);
        r3 = r0.infoForChild(r2);
        if (r3 == 0) goto L_0x026e;
    L_0x0260:
        r3 = r3.position;
        r4 = r0.mCurItem;
        if (r3 != r4) goto L_0x026e;
    L_0x0266:
        r3 = 2;
        r2 = r2.requestFocus(r3);
        if (r2 == 0) goto L_0x026e;
    L_0x026d:
        goto L_0x0271;
    L_0x026e:
        r1 = r1 + 1;
        goto L_0x0250;
    L_0x0271:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.ViewPager.populate(int):void");
    }

    private void sortChildDrawingOrder() {
        if (this.mDrawingOrder != 0) {
            if (this.mDrawingOrderedChildren == null) {
                this.mDrawingOrderedChildren = new ArrayList();
            } else {
                this.mDrawingOrderedChildren.clear();
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                this.mDrawingOrderedChildren.add(getChildAt(i));
            }
            Collections.sort(this.mDrawingOrderedChildren, sPositionComparator);
        }
    }

    private void calculatePageOffsets(ItemInfo itemInfo, int i, ItemInfo itemInfo2) {
        int i2;
        int size;
        int count = this.mAdapter.getCount();
        int clientWidth = getClientWidth();
        float f = clientWidth > 0 ? ((float) this.mPageMargin) / ((float) clientWidth) : 0.0f;
        boolean z = false;
        if (itemInfo2 != null) {
            int i3 = itemInfo2.position;
            Object obj;
            ItemInfo itemInfo3;
            if (i3 < itemInfo.position) {
                float f2 = (itemInfo2.offset + itemInfo2.widthFactor) + f;
                i3++;
                i2 = z;
                while (i3 <= itemInfo.position && i2 < this.mItems.size()) {
                    obj = this.mItems.get(i2);
                    while (true) {
                        itemInfo3 = (ItemInfo) obj;
                        if (i3 <= itemInfo3.position || i2 >= this.mItems.size() - 1) {
                            while (i3 < itemInfo3.position) {
                                f2 += this.mAdapter.getPageWidth(i3) + f;
                                i3++;
                            }
                        } else {
                            i2++;
                            obj = this.mItems.get(i2);
                        }
                    }
                    while (i3 < itemInfo3.position) {
                        f2 += this.mAdapter.getPageWidth(i3) + f;
                        i3++;
                    }
                    itemInfo3.offset = f2;
                    f2 += itemInfo3.widthFactor + f;
                    i3++;
                }
            } else if (i3 > itemInfo.position) {
                size = this.mItems.size() - 1;
                float f3 = itemInfo2.offset;
                while (true) {
                    i3--;
                    if (i3 < itemInfo.position || size < 0) {
                        break;
                    }
                    obj = this.mItems.get(size);
                    while (true) {
                        itemInfo3 = (ItemInfo) obj;
                        if (i3 >= itemInfo3.position || size <= 0) {
                            while (i3 > itemInfo3.position) {
                                f3 -= this.mAdapter.getPageWidth(i3) + f;
                                i3--;
                            }
                        } else {
                            size--;
                            obj = this.mItems.get(size);
                        }
                    }
                    while (i3 > itemInfo3.position) {
                        f3 -= this.mAdapter.getPageWidth(i3) + f;
                        i3--;
                    }
                    f3 -= itemInfo3.widthFactor + f;
                    itemInfo3.offset = f3;
                }
            }
        }
        i2 = this.mItems.size();
        float f4 = itemInfo.offset;
        size = itemInfo.position - 1;
        this.mFirstOffset = itemInfo.position == 0 ? itemInfo.offset : -3.4028235E38f;
        count--;
        float f5 = 1.0f;
        this.mLastOffset = itemInfo.position == count ? (itemInfo.offset + itemInfo.widthFactor) - f5 : Float.MAX_VALUE;
        int i4 = i - 1;
        while (i4 >= 0) {
            ItemInfo itemInfo4 = (ItemInfo) this.mItems.get(i4);
            while (size > itemInfo4.position) {
                f4 -= this.mAdapter.getPageWidth(size) + f;
                size--;
            }
            f4 -= itemInfo4.widthFactor + f;
            itemInfo4.offset = f4;
            if (itemInfo4.position == 0) {
                this.mFirstOffset = f4;
            }
            i4--;
            size--;
        }
        f4 = (itemInfo.offset + itemInfo.widthFactor) + f;
        int i5 = itemInfo.position + 1;
        i++;
        while (i < i2) {
            ItemInfo itemInfo5 = (ItemInfo) this.mItems.get(i);
            while (i5 < itemInfo5.position) {
                f4 += this.mAdapter.getPageWidth(i5) + f;
                i5++;
            }
            if (itemInfo5.position == count) {
                this.mLastOffset = (itemInfo5.widthFactor + f4) - f5;
            }
            itemInfo5.offset = f4;
            f4 += itemInfo5.widthFactor + f;
            i++;
            i5++;
        }
        this.mNeedCalculatePageOffsets = z;
    }

    public Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        savedState.position = this.mCurItem;
        if (this.mAdapter != null) {
            savedState.adapterState = this.mAdapter.saveState();
        }
        return savedState;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (this.mAdapter != null) {
                this.mAdapter.restoreState(savedState.adapterState, savedState.loader);
                setCurrentItemInternal(savedState.position, false, true);
            } else {
                this.mRestoredCurItem = savedState.position;
                this.mRestoredAdapterState = savedState.adapterState;
                this.mRestoredClassLoader = savedState.loader;
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
        if (!checkLayoutParams(layoutParams)) {
            layoutParams = generateLayoutParams(layoutParams);
        }
        LayoutParams layoutParams2 = (LayoutParams) layoutParams;
        layoutParams2.isDecor |= isDecorView(view);
        if (!this.mInLayout) {
            super.addView(view, i, layoutParams);
        } else if (layoutParams2 == null || !layoutParams2.isDecor) {
            layoutParams2.needsMeasure = true;
            addViewInLayout(view, i, layoutParams);
        } else {
            throw new IllegalStateException("Cannot add pager decor view during layout");
        }
    }

    private static boolean isDecorView(@NonNull View view) {
        return view.getClass().getAnnotation(DecorView.class) != null;
    }

    public void removeView(View view) {
        if (this.mInLayout) {
            removeViewInLayout(view);
        } else {
            super.removeView(view);
        }
    }

    ItemInfo infoForChild(View view) {
        for (int i = 0; i < this.mItems.size(); i++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i);
            if (this.mAdapter.isViewFromObject(view, itemInfo.object)) {
                return itemInfo;
            }
        }
        return null;
    }

    ItemInfo infoForAnyChild(View view) {
        while (true) {
            ViewPager parent = view.getParent();
            if (parent == this) {
                return infoForChild(view);
            }
            if (parent != null && (parent instanceof View)) {
                view = parent;
            }
        }
        return null;
    }

    ItemInfo infoForPosition(int i) {
        for (int i2 = 0; i2 < this.mItems.size(); i2++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
            if (itemInfo.position == i) {
                return itemInfo;
            }
        }
        return null;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        boolean z;
        int i4;
        int i5;
        boolean z2 = false;
        setMeasuredDimension(getDefaultSize(z2, i), getDefaultSize(z2, i2));
        int measuredWidth = getMeasuredWidth();
        this.mGutterSize = Math.min(measuredWidth / 10, this.mDefaultGutterSize);
        measuredWidth = (measuredWidth - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        int childCount = getChildCount();
        int i6 = measuredHeight;
        measuredHeight = measuredWidth;
        measuredWidth = z2;
        while (true) {
            i3 = 8;
            z = true;
            i4 = 1073741824;
            if (measuredWidth >= childCount) {
                break;
            }
            View childAt = getChildAt(measuredWidth);
            if (childAt.getVisibility() != i3) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams != null && layoutParams.isDecor) {
                    int i7;
                    int i8;
                    int i9 = layoutParams.gravity & 7;
                    int i10 = layoutParams.gravity & 112;
                    i10 = (i10 == 48 || i10 == 80) ? z : z2;
                    if (!(i9 == 3 || i9 == 5)) {
                        z = z2;
                    }
                    i9 = Integer.MIN_VALUE;
                    if (i10 != 0) {
                        i7 = i9;
                        i9 = i4;
                    } else {
                        i7 = z ? i4 : i9;
                    }
                    int i11 = -1;
                    int i12 = -2;
                    if (layoutParams.width != i12) {
                        i8 = layoutParams.width != i11 ? layoutParams.width : measuredHeight;
                        i9 = i4;
                    } else {
                        i8 = measuredHeight;
                    }
                    if (layoutParams.height != i12) {
                        i5 = layoutParams.height != i11 ? layoutParams.height : i6;
                    } else {
                        i5 = i6;
                        i4 = i7;
                    }
                    childAt.measure(MeasureSpec.makeMeasureSpec(i8, i9), MeasureSpec.makeMeasureSpec(i5, i4));
                    if (i10 != 0) {
                        i6 -= childAt.getMeasuredHeight();
                    } else if (z) {
                        measuredHeight -= childAt.getMeasuredWidth();
                    }
                }
            }
            measuredWidth++;
            z2 = false;
        }
        r0.mChildWidthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, i4);
        r0.mChildHeightMeasureSpec = MeasureSpec.makeMeasureSpec(i6, i4);
        r0.mInLayout = z;
        populate();
        i5 = 0;
        r0.mInLayout = i5;
        measuredWidth = getChildCount();
        while (i5 < measuredWidth) {
            View childAt2 = getChildAt(i5);
            if (childAt2.getVisibility() != i3) {
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (layoutParams2 == null || !layoutParams2.isDecor) {
                    childAt2.measure(MeasureSpec.makeMeasureSpec((int) (((float) measuredHeight) * layoutParams2.widthFactor), i4), r0.mChildHeightMeasureSpec);
                }
            }
            i5++;
        }
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3) {
            recomputeScrollPosition(i, i3, this.mPageMargin, this.mPageMargin);
        }
    }

    private void recomputeScrollPosition(int i, int i2, int i3, int i4) {
        if (i2 <= 0 || this.mItems.isEmpty()) {
            ItemInfo infoForPosition = infoForPosition(this.mCurItem);
            i = (int) ((infoForPosition != null ? Math.min(infoForPosition.offset, this.mLastOffset) : 0.0f) * ((float) ((i - getPaddingLeft()) - getPaddingRight())));
            if (i != getScrollX()) {
                completeScroll(false);
                scrollTo(i, getScrollY());
            }
        } else if (this.mScroller.isFinished()) {
            scrollTo((int) ((((float) getScrollX()) / ((float) (((i2 - getPaddingLeft()) - getPaddingRight()) + i4))) * ((float) (((i - getPaddingLeft()) - getPaddingRight()) + i3))), getScrollY());
        } else {
            this.mScroller.setFinalX(getCurrentItem() * getClientWidth());
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        boolean z2;
        ViewPager viewPager = this;
        int childCount = getChildCount();
        int i6 = i3 - i;
        int i7 = i4 - i2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        int scrollX = getScrollX();
        int i8 = paddingBottom;
        int i9 = 0;
        paddingBottom = paddingTop;
        paddingTop = paddingLeft;
        paddingLeft = 0;
        while (true) {
            i5 = 8;
            if (paddingLeft >= childCount) {
                break;
            }
            View childAt = getChildAt(paddingLeft);
            if (childAt.getVisibility() != i5) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int i10 = layoutParams.gravity & 7;
                    i5 = layoutParams.gravity & 112;
                    if (i10 == 1) {
                        i10 = Math.max((i6 - childAt.getMeasuredWidth()) / 2, paddingTop);
                    } else if (i10 == 3) {
                        i10 = paddingTop;
                        paddingTop = childAt.getMeasuredWidth() + paddingTop;
                    } else if (i10 != 5) {
                        i10 = paddingTop;
                    } else {
                        i10 = (i6 - paddingRight) - childAt.getMeasuredWidth();
                        paddingRight += childAt.getMeasuredWidth();
                    }
                    if (i5 == 16) {
                        i5 = Math.max((i7 - childAt.getMeasuredHeight()) / 2, paddingBottom);
                    } else if (i5 == 48) {
                        i5 = paddingBottom;
                        paddingBottom = childAt.getMeasuredHeight() + paddingBottom;
                    } else if (i5 != 80) {
                        i5 = paddingBottom;
                    } else {
                        i5 = (i7 - i8) - childAt.getMeasuredHeight();
                        i8 += childAt.getMeasuredHeight();
                    }
                    i10 += scrollX;
                    childAt.layout(i10, i5, childAt.getMeasuredWidth() + i10, i5 + childAt.getMeasuredHeight());
                    i9++;
                }
            }
            paddingLeft++;
        }
        i6 = (i6 - paddingTop) - paddingRight;
        for (paddingLeft = 0; paddingLeft < childCount; paddingLeft++) {
            View childAt2 = getChildAt(paddingLeft);
            if (childAt2.getVisibility() != i5) {
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                if (!layoutParams2.isDecor) {
                    ItemInfo infoForChild = infoForChild(childAt2);
                    if (infoForChild != null) {
                        float f = (float) i6;
                        int i11 = ((int) (infoForChild.offset * f)) + paddingTop;
                        if (layoutParams2.needsMeasure) {
                            layoutParams2.needsMeasure = false;
                            scrollX = (int) (f * layoutParams2.widthFactor);
                            int i12 = 1073741824;
                            childAt2.measure(MeasureSpec.makeMeasureSpec(scrollX, i12), MeasureSpec.makeMeasureSpec((i7 - paddingBottom) - i8, i12));
                        }
                        childAt2.layout(i11, paddingBottom, childAt2.getMeasuredWidth() + i11, childAt2.getMeasuredHeight() + paddingBottom);
                    }
                }
            }
        }
        viewPager.mTopPageBounds = paddingBottom;
        viewPager.mBottomPageBounds = i7 - i8;
        viewPager.mDecorChildCount = i9;
        if (viewPager.mFirstLayout) {
            z2 = false;
            scrollToItem(viewPager.mCurItem, z2, z2, z2);
        } else {
            z2 = false;
        }
        viewPager.mFirstLayout = z2;
    }

    public void computeScroll() {
        boolean z = true;
        this.mIsScrollStarted = z;
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            completeScroll(z);
            return;
        }
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        int currX = this.mScroller.getCurrX();
        int currY = this.mScroller.getCurrY();
        if (!(scrollX == currX && scrollY == currY)) {
            scrollTo(currX, currY);
            if (!pageScrolled(currX)) {
                this.mScroller.abortAnimation();
                scrollTo(0, currY);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    private boolean pageScrolled(int i) {
        boolean z = false;
        if (this.mItems.size() != 0) {
            ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
            int clientWidth = getClientWidth();
            int i2 = this.mPageMargin + clientWidth;
            float f = (float) clientWidth;
            float f2 = ((float) this.mPageMargin) / f;
            int i3 = infoForCurrentScrollPosition.position;
            float f3 = ((((float) i) / f) - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + f2);
            int i4 = (int) (((float) i2) * f3);
            this.mCalledSuper = z;
            onPageScrolled(i3, f3, i4);
            if (this.mCalledSuper) {
                return true;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        } else if (this.mFirstLayout) {
            return z;
        } else {
            this.mCalledSuper = z;
            onPageScrolled(z, 0.0f, z);
            if (this.mCalledSuper) {
                return z;
            }
            throw new IllegalStateException("onPageScrolled did not call superclass implementation");
        }
    }

    @CallSuper
    protected void onPageScrolled(int i, float f, int i2) {
        int i3 = 0;
        boolean z = true;
        if (this.mDecorChildCount > 0) {
            int scrollX = getScrollX();
            int paddingLeft = getPaddingLeft();
            int paddingRight = getPaddingRight();
            int width = getWidth();
            int childCount = getChildCount();
            int i4 = paddingRight;
            paddingRight = paddingLeft;
            for (paddingLeft = i3; paddingLeft < childCount; paddingLeft++) {
                View childAt = getChildAt(paddingLeft);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isDecor) {
                    int width2;
                    boolean z2 = layoutParams.gravity & 7;
                    if (z2 != z) {
                        if (z2) {
                            width2 = childAt.getWidth() + paddingRight;
                        } else if (!z2) {
                            width2 = paddingRight;
                        } else {
                            width2 = (width - i4) - childAt.getMeasuredWidth();
                            i4 += childAt.getMeasuredWidth();
                        }
                        paddingRight = (paddingRight + scrollX) - childAt.getLeft();
                        if (paddingRight != 0) {
                            childAt.offsetLeftAndRight(paddingRight);
                        }
                        paddingRight = width2;
                    } else {
                        width2 = Math.max((width - childAt.getMeasuredWidth()) / 2, paddingRight);
                    }
                    int i5 = width2;
                    width2 = paddingRight;
                    paddingRight = i5;
                    paddingRight = (paddingRight + scrollX) - childAt.getLeft();
                    if (paddingRight != 0) {
                        childAt.offsetLeftAndRight(paddingRight);
                    }
                    paddingRight = width2;
                }
            }
        }
        dispatchOnPageScrolled(i, f, i2);
        if (this.mPageTransformer != null) {
            i = getScrollX();
            int childCount2 = getChildCount();
            while (i3 < childCount2) {
                View childAt2 = getChildAt(i3);
                if (!((LayoutParams) childAt2.getLayoutParams()).isDecor) {
                    this.mPageTransformer.transformPage(childAt2, ((float) (childAt2.getLeft() - i)) / ((float) getClientWidth()));
                }
                i3++;
            }
        }
        this.mCalledSuper = z;
    }

    private void dispatchOnPageScrolled(int i, float f, int i2) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrolled(i, f, i2);
        }
        if (this.mOnPageChangeListeners != null) {
            int size = this.mOnPageChangeListeners.size();
            for (int i3 = 0; i3 < size; i3++) {
                OnPageChangeListener onPageChangeListener = (OnPageChangeListener) this.mOnPageChangeListeners.get(i3);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrolled(i, f, i2);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrolled(i, f, i2);
        }
    }

    private void dispatchOnPageSelected(int i) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageSelected(i);
        }
        if (this.mOnPageChangeListeners != null) {
            int size = this.mOnPageChangeListeners.size();
            for (int i2 = 0; i2 < size; i2++) {
                OnPageChangeListener onPageChangeListener = (OnPageChangeListener) this.mOnPageChangeListeners.get(i2);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageSelected(i);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageSelected(i);
        }
    }

    private void dispatchOnScrollStateChanged(int i) {
        if (this.mOnPageChangeListener != null) {
            this.mOnPageChangeListener.onPageScrollStateChanged(i);
        }
        if (this.mOnPageChangeListeners != null) {
            int size = this.mOnPageChangeListeners.size();
            for (int i2 = 0; i2 < size; i2++) {
                OnPageChangeListener onPageChangeListener = (OnPageChangeListener) this.mOnPageChangeListeners.get(i2);
                if (onPageChangeListener != null) {
                    onPageChangeListener.onPageScrollStateChanged(i);
                }
            }
        }
        if (this.mInternalPageChangeListener != null) {
            this.mInternalPageChangeListener.onPageScrollStateChanged(i);
        }
    }

    private void completeScroll(boolean z) {
        int scrollX;
        int i = 1;
        boolean z2 = false;
        int i2 = this.mScrollState == 2 ? i : z2;
        if (i2 != 0) {
            setScrollingCacheEnabled(z2);
            if ((this.mScroller.isFinished() ^ i) != 0) {
                this.mScroller.abortAnimation();
                scrollX = getScrollX();
                int scrollY = getScrollY();
                int currX = this.mScroller.getCurrX();
                int currY = this.mScroller.getCurrY();
                if (!(scrollX == currX && scrollY == currY)) {
                    scrollTo(currX, currY);
                    if (currX != scrollX) {
                        pageScrolled(currX);
                    }
                }
            }
        }
        this.mPopulatePending = z2;
        scrollX = i2;
        for (i2 = z2; i2 < this.mItems.size(); i2++) {
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(i2);
            if (itemInfo.scrolling) {
                itemInfo.scrolling = z2;
                scrollX = i;
            }
        }
        if (scrollX == 0) {
            return;
        }
        if (z) {
            ViewCompat.postOnAnimation(this, this.mEndScrollRunnable);
        } else {
            this.mEndScrollRunnable.run();
        }
    }

    private boolean isGutterDrag(float f, float f2) {
        float f3 = 0.0f;
        return (f < ((float) this.mGutterSize) && f2 > f3) || (f > ((float) (getWidth() - this.mGutterSize)) && f2 < f3);
    }

    private void enableLayers(boolean z) {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = i; i2 < childCount; i2++) {
            getChildAt(i2).setLayerType(z ? this.mPageTransformerLayerType : i, null);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        View view = this;
        MotionEvent motionEvent2 = motionEvent;
        boolean action = motionEvent.getAction() & 255;
        boolean z = false;
        if (!action) {
            boolean z2 = true;
            if (action != z2) {
                if (action) {
                    if (view.mIsBeingDragged) {
                        return z2;
                    }
                    if (view.mIsUnableToDrag) {
                        return z;
                    }
                }
                boolean z3 = true;
                if (!action) {
                    float x = motionEvent.getX();
                    view.mInitialMotionX = x;
                    view.mLastMotionX = x;
                    x = motionEvent.getY();
                    view.mInitialMotionY = x;
                    view.mLastMotionY = x;
                    view.mActivePointerId = motionEvent2.getPointerId(z);
                    view.mIsUnableToDrag = z;
                    view.mIsScrollStarted = z2;
                    view.mScroller.computeScrollOffset();
                    if (view.mScrollState != z3 || Math.abs(view.mScroller.getFinalX() - view.mScroller.getCurrX()) <= view.mCloseEnough) {
                        completeScroll(z);
                        view.mIsBeingDragged = z;
                    } else {
                        view.mScroller.abortAnimation();
                        view.mPopulatePending = z;
                        populate();
                        view.mIsBeingDragged = z2;
                        requestParentDisallowInterceptTouchEvent(z2);
                        setScrollState(z2);
                    }
                } else if (action == z3) {
                    int i = view.mActivePointerId;
                    if (i != -1) {
                        i = motionEvent2.findPointerIndex(i);
                        float x2 = motionEvent2.getX(i);
                        float f = x2 - view.mLastMotionX;
                        float abs = Math.abs(f);
                        float y = motionEvent2.getY(i);
                        float abs2 = Math.abs(y - view.mInitialMotionY);
                        float f2 = 0.0f;
                        if (f == f2 || isGutterDrag(view.mLastMotionX, f) || !canScroll(view, false, (int) f, (int) x2, (int) y)) {
                            if (abs > ((float) view.mTouchSlop) && abs * 0.5f > abs2) {
                                view.mIsBeingDragged = z2;
                                requestParentDisallowInterceptTouchEvent(z2);
                                setScrollState(z2);
                                view.mLastMotionX = f > f2 ? view.mInitialMotionX + ((float) view.mTouchSlop) : view.mInitialMotionX - ((float) view.mTouchSlop);
                                view.mLastMotionY = y;
                                setScrollingCacheEnabled(z2);
                            } else if (abs2 > ((float) view.mTouchSlop)) {
                                view.mIsUnableToDrag = z2;
                            }
                            if (view.mIsBeingDragged && performDrag(x2)) {
                                ViewCompat.postInvalidateOnAnimation(this);
                            }
                        } else {
                            view.mLastMotionX = x2;
                            view.mLastMotionY = y;
                            view.mIsUnableToDrag = z2;
                            return z;
                        }
                    }
                } else if (action) {
                    onSecondaryPointerUp(motionEvent);
                }
                if (view.mVelocityTracker == null) {
                    view.mVelocityTracker = VelocityTracker.obtain();
                }
                view.mVelocityTracker.addMovement(motionEvent2);
                return view.mIsBeingDragged;
            }
        }
        resetTouch();
        return z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z = true;
        if (this.mFakeDragging) {
            return z;
        }
        boolean z2 = false;
        if ((motionEvent.getAction() == 0 && motionEvent.getEdgeFlags() != 0) || this.mAdapter == null || this.mAdapter.getCount() == 0) {
            return z2;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        float x;
        int xVelocity;
        switch (motionEvent.getAction() & 255) {
            case 0:
                this.mScroller.abortAnimation();
                this.mPopulatePending = z2;
                populate();
                x = motionEvent.getX();
                this.mInitialMotionX = x;
                this.mLastMotionX = x;
                x = motionEvent.getY();
                this.mInitialMotionY = x;
                this.mLastMotionY = x;
                this.mActivePointerId = motionEvent.getPointerId(z2);
                break;
            case 1:
                if (this.mIsBeingDragged) {
                    VelocityTracker velocityTracker = this.mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                    xVelocity = (int) velocityTracker.getXVelocity(this.mActivePointerId);
                    this.mPopulatePending = z;
                    int clientWidth = getClientWidth();
                    int scrollX = getScrollX();
                    ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
                    float f = (float) clientWidth;
                    setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((((float) scrollX) / f) - infoForCurrentScrollPosition.offset) / (infoForCurrentScrollPosition.widthFactor + (((float) this.mPageMargin) / f)), xVelocity, (int) (motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)) - this.mInitialMotionX)), z, z, xVelocity);
                    z2 = resetTouch();
                    break;
                }
                break;
            case 2:
                if (!this.mIsBeingDragged) {
                    xVelocity = motionEvent.findPointerIndex(this.mActivePointerId);
                    if (xVelocity == -1) {
                        z2 = resetTouch();
                        break;
                    }
                    float x2 = motionEvent.getX(xVelocity);
                    float abs = Math.abs(x2 - this.mLastMotionX);
                    x = motionEvent.getY(xVelocity);
                    float abs2 = Math.abs(x - this.mLastMotionY);
                    if (abs > ((float) this.mTouchSlop) && abs > abs2) {
                        this.mIsBeingDragged = z;
                        requestParentDisallowInterceptTouchEvent(z);
                        this.mLastMotionX = x2 - this.mInitialMotionX > 0.0f ? this.mInitialMotionX + ((float) this.mTouchSlop) : this.mInitialMotionX - ((float) this.mTouchSlop);
                        this.mLastMotionY = x;
                        setScrollState(z);
                        setScrollingCacheEnabled(z);
                        ViewParent parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(z);
                        }
                    }
                }
                if (this.mIsBeingDragged) {
                    z2 |= performDrag(motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId)));
                    break;
                }
                break;
            case 3:
                if (this.mIsBeingDragged) {
                    scrollToItem(this.mCurItem, z, z2, z2);
                    z2 = resetTouch();
                    break;
                }
                break;
            case 5:
                xVelocity = motionEvent.getActionIndex();
                this.mLastMotionX = motionEvent.getX(xVelocity);
                this.mActivePointerId = motionEvent.getPointerId(xVelocity);
                break;
            case 6:
                onSecondaryPointerUp(motionEvent);
                this.mLastMotionX = motionEvent.getX(motionEvent.findPointerIndex(this.mActivePointerId));
                break;
        }
        if (z2) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
        return z;
    }

    private boolean resetTouch() {
        this.mActivePointerId = -1;
        endDrag();
        this.mLeftEdge.onRelease();
        this.mRightEdge.onRelease();
        return this.mLeftEdge.isFinished() || this.mRightEdge.isFinished();
    }

    private void requestParentDisallowInterceptTouchEvent(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(z);
        }
    }

    private boolean performDrag(float f) {
        boolean z;
        boolean z2;
        float f2 = this.mLastMotionX - f;
        this.mLastMotionX = f;
        f = ((float) getScrollX()) + f2;
        f2 = (float) getClientWidth();
        float f3 = this.mFirstOffset * f2;
        float f4 = this.mLastOffset * f2;
        boolean z3 = false;
        ItemInfo itemInfo = (ItemInfo) this.mItems.get(z3);
        boolean z4 = true;
        ItemInfo itemInfo2 = (ItemInfo) this.mItems.get(this.mItems.size() - z4);
        if (itemInfo.position != 0) {
            f3 = itemInfo.offset * f2;
            z = z3;
        } else {
            z = z4;
        }
        if (itemInfo2.position != this.mAdapter.getCount() - z4) {
            f4 = itemInfo2.offset * f2;
            z2 = z3;
        } else {
            z2 = z4;
        }
        if (f < f3) {
            if (z) {
                this.mLeftEdge.onPull(Math.abs(f3 - f) / f2);
                z3 = z4;
            }
            f = f3;
        } else if (f > f4) {
            if (z2) {
                this.mRightEdge.onPull(Math.abs(f - f4) / f2);
                z3 = z4;
            }
            f = f4;
        }
        int i = (int) f;
        this.mLastMotionX += f - ((float) i);
        scrollTo(i, getScrollY());
        pageScrolled(i);
        return z3;
    }

    private ItemInfo infoForCurrentScrollPosition() {
        int clientWidth = getClientWidth();
        float f = 0.0f;
        float scrollX = clientWidth > 0 ? ((float) getScrollX()) / ((float) clientWidth) : f;
        float f2 = clientWidth > 0 ? ((float) this.mPageMargin) / ((float) clientWidth) : f;
        int i = 0;
        int i2 = 1;
        float f3 = f;
        float f4 = f3;
        int i3 = i;
        int i4 = -1;
        ItemInfo itemInfo = null;
        int i5 = i2;
        while (i3 < this.mItems.size()) {
            ItemInfo itemInfo2 = (ItemInfo) this.mItems.get(i3);
            if (i5 == 0) {
                i4 += i2;
                if (itemInfo2.position != i4) {
                    itemInfo2 = this.mTempItem;
                    itemInfo2.offset = (f3 + f4) + f2;
                    itemInfo2.position = i4;
                    itemInfo2.widthFactor = this.mAdapter.getPageWidth(itemInfo2.position);
                    i3--;
                }
            }
            f3 = itemInfo2.offset;
            float f5 = (itemInfo2.widthFactor + f3) + f2;
            if (i5 == 0 && scrollX < f3) {
                return itemInfo;
            }
            if (scrollX < f5 || i3 == this.mItems.size() - i2) {
                return itemInfo2;
            }
            i4 = itemInfo2.position;
            f4 = itemInfo2.widthFactor;
            i3++;
            i5 = i;
            itemInfo = itemInfo2;
        }
        return itemInfo;
    }

    private int determineTargetPage(int i, float f, int i2, int i3) {
        if (Math.abs(i3) <= this.mFlingDistance || Math.abs(i2) <= this.mMinimumVelocity) {
            i += (int) (f + (i >= this.mCurItem ? 0.4f : 0.6f));
        } else if (i2 <= 0) {
            i++;
        }
        if (this.mItems.size() <= 0) {
            return i;
        }
        return Math.max(((ItemInfo) this.mItems.get(0)).position, Math.min(i, ((ItemInfo) this.mItems.get(this.mItems.size() - 1)).position));
    }

    public void draw(Canvas canvas) {
        int i;
        int width;
        super.draw(canvas);
        int overScrollMode = getOverScrollMode();
        int i2 = 0;
        if (overScrollMode != 0) {
            i = 1;
            if (overScrollMode != i || this.mAdapter == null || this.mAdapter.getCount() <= i) {
                this.mLeftEdge.finish();
                this.mRightEdge.finish();
                if (i2 != 0) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }
        }
        if (!this.mLeftEdge.isFinished()) {
            overScrollMode = canvas.save();
            i = (getHeight() - getPaddingTop()) - getPaddingBottom();
            width = getWidth();
            canvas.rotate(270.0f);
            canvas.translate((float) ((-i) + getPaddingTop()), this.mFirstOffset * ((float) width));
            this.mLeftEdge.setSize(i, width);
            i2 |= this.mLeftEdge.draw(canvas);
            canvas.restoreToCount(overScrollMode);
        }
        if (!this.mRightEdge.isFinished()) {
            overScrollMode = canvas.save();
            i = getWidth();
            width = (getHeight() - getPaddingTop()) - getPaddingBottom();
            canvas.rotate(90.0f);
            canvas.translate((float) (-getPaddingTop()), (-(this.mLastOffset + 1.0f)) * ((float) i));
            this.mRightEdge.setSize(width, i);
            i2 |= this.mRightEdge.draw(canvas);
            canvas.restoreToCount(overScrollMode);
        }
        if (i2 != 0) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPageMargin > 0 && r0.mMarginDrawable != null && r0.mItems.size() > 0 && r0.mAdapter != null) {
            int scrollX = getScrollX();
            int width = getWidth();
            float f = (float) width;
            float f2 = ((float) r0.mPageMargin) / f;
            int i = 0;
            ItemInfo itemInfo = (ItemInfo) r0.mItems.get(i);
            float f3 = itemInfo.offset;
            int size = r0.mItems.size();
            int i2 = itemInfo.position;
            int i3 = ((ItemInfo) r0.mItems.get(size - 1)).position;
            while (i2 < i3) {
                float f4;
                float f5;
                while (i2 > itemInfo.position && i < size) {
                    i++;
                    itemInfo = (ItemInfo) r0.mItems.get(i);
                }
                if (i2 == itemInfo.position) {
                    f4 = (itemInfo.offset + itemInfo.widthFactor) * f;
                    f3 = (itemInfo.offset + itemInfo.widthFactor) + f2;
                } else {
                    float pageWidth = r0.mAdapter.getPageWidth(i2);
                    f4 = (f3 + pageWidth) * f;
                    f3 += pageWidth + f2;
                }
                if (((float) r0.mPageMargin) + f4 > ((float) scrollX)) {
                    f5 = f2;
                    r0.mMarginDrawable.setBounds(Math.round(f4), r0.mTopPageBounds, Math.round(((float) r0.mPageMargin) + f4), r0.mBottomPageBounds);
                    r0.mMarginDrawable.draw(canvas);
                } else {
                    Canvas canvas2 = canvas;
                    f5 = f2;
                }
                if (f4 <= ((float) (scrollX + width))) {
                    i2++;
                    f2 = f5;
                } else {
                    return;
                }
            }
        }
    }

    public boolean beginFakeDrag() {
        if (this.mIsBeingDragged) {
            return false;
        }
        boolean z = true;
        this.mFakeDragging = z;
        setScrollState(z);
        float f = 0.0f;
        this.mLastMotionX = f;
        this.mInitialMotionX = f;
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 0, 0.0f, 0.0f, 0);
        this.mVelocityTracker.addMovement(obtain);
        obtain.recycle();
        this.mFakeDragBeginTime = uptimeMillis;
        return z;
    }

    public void endFakeDrag() {
        if (this.mFakeDragging) {
            if (this.mAdapter != null) {
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) this.mMaximumVelocity);
                int xVelocity = (int) velocityTracker.getXVelocity(this.mActivePointerId);
                boolean z = true;
                this.mPopulatePending = z;
                int clientWidth = getClientWidth();
                int scrollX = getScrollX();
                ItemInfo infoForCurrentScrollPosition = infoForCurrentScrollPosition();
                setCurrentItemInternal(determineTargetPage(infoForCurrentScrollPosition.position, ((((float) scrollX) / ((float) clientWidth)) - infoForCurrentScrollPosition.offset) / infoForCurrentScrollPosition.widthFactor, xVelocity, (int) (this.mLastMotionX - this.mInitialMotionX)), z, z, xVelocity);
            }
            endDrag();
            this.mFakeDragging = false;
            return;
        }
        throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
    }

    public void fakeDragBy(float f) {
        if (!this.mFakeDragging) {
            throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
        } else if (this.mAdapter != null) {
            this.mLastMotionX += f;
            float scrollX = ((float) getScrollX()) - f;
            f = (float) getClientWidth();
            float f2 = this.mFirstOffset * f;
            float f3 = this.mLastOffset * f;
            ItemInfo itemInfo = (ItemInfo) this.mItems.get(0);
            ItemInfo itemInfo2 = (ItemInfo) this.mItems.get(this.mItems.size() - 1);
            if (itemInfo.position != 0) {
                f2 = itemInfo.offset * f;
            }
            if (itemInfo2.position != this.mAdapter.getCount() - 1) {
                f3 = itemInfo2.offset * f;
            }
            if (scrollX < f2) {
                scrollX = f2;
            } else if (scrollX > f3) {
                scrollX = f3;
            }
            int i = (int) scrollX;
            this.mLastMotionX += scrollX - ((float) i);
            scrollTo(i, getScrollY());
            pageScrolled(i);
            MotionEvent obtain = MotionEvent.obtain(this.mFakeDragBeginTime, SystemClock.uptimeMillis(), 2, this.mLastMotionX, 0.0f, 0);
            this.mVelocityTracker.addMovement(obtain);
            obtain.recycle();
        }
    }

    public boolean isFakeDragging() {
        return this.mFakeDragging;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            this.mLastMotionX = motionEvent.getX(actionIndex);
            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    private void endDrag() {
        boolean z = false;
        this.mIsBeingDragged = z;
        this.mIsUnableToDrag = z;
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private void setScrollingCacheEnabled(boolean z) {
        if (this.mScrollingCacheEnabled != z) {
            this.mScrollingCacheEnabled = z;
        }
    }

    public boolean canScrollHorizontally(int i) {
        boolean z = false;
        if (this.mAdapter == null) {
            return z;
        }
        int clientWidth = getClientWidth();
        int scrollX = getScrollX();
        boolean z2 = true;
        if (i < 0) {
            if (scrollX > ((int) (((float) clientWidth) * this.mFirstOffset))) {
                z = z2;
            }
            return z;
        } else if (i <= 0) {
            return z;
        } else {
            if (scrollX < ((int) (((float) clientWidth) * this.mLastOffset))) {
                z = z2;
            }
            return z;
        }
    }

    protected boolean canScroll(View view, boolean z, int i, int i2, int i3) {
        View view2 = view;
        boolean z2 = true;
        if (view2 instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view2;
            int scrollX = view2.getScrollX();
            int scrollY = view2.getScrollY();
            for (int childCount = viewGroup.getChildCount() - z2; childCount >= 0; childCount--) {
                View childAt = viewGroup.getChildAt(childCount);
                int i4 = i2 + scrollX;
                if (i4 >= childAt.getLeft() && i4 < childAt.getRight()) {
                    int i5 = i3 + scrollY;
                    if (i5 >= childAt.getTop() && i5 < childAt.getBottom()) {
                        if (canScroll(childAt, true, i, i4 - childAt.getLeft(), i5 - childAt.getTop())) {
                            return z2;
                        }
                    }
                }
            }
        }
        if (!(z && view2.canScrollHorizontally(-i))) {
            z2 = false;
        }
        return z2;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || executeKeyEvent(keyEvent);
    }

    public boolean executeKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            int i = 2;
            if (keyCode != 61) {
                switch (keyCode) {
                    case 21:
                        if (keyEvent.hasModifiers(i)) {
                            return pageLeft();
                        }
                        return arrowScroll(17);
                    case 22:
                        if (keyEvent.hasModifiers(i)) {
                            return pageRight();
                        }
                        return arrowScroll(66);
                }
            } else if (keyEvent.hasNoModifiers()) {
                return arrowScroll(i);
            } else {
                keyCode = 1;
                if (keyEvent.hasModifiers(keyCode)) {
                    return arrowScroll(keyCode);
                }
            }
        }
        return false;
    }

    public boolean arrowScroll(int i) {
        View findFocus = findFocus();
        boolean z = true;
        boolean z2 = false;
        View view = null;
        if (findFocus != this) {
            if (findFocus != null) {
                boolean z3;
                for (ViewPager parent = findFocus.getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
                    if (parent == this) {
                        z3 = z;
                        break;
                    }
                }
                z3 = z2;
                if (!z3) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(findFocus.getClass().getSimpleName());
                    for (ViewParent parent2 = findFocus.getParent(); parent2 instanceof ViewGroup; parent2 = parent2.getParent()) {
                        stringBuilder.append(" => ");
                        stringBuilder.append(parent2.getClass().getSimpleName());
                    }
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("arrowScroll tried to find focus based on non-child current focused view ");
                    stringBuilder2.append(stringBuilder.toString());
                    Log.e("ViewPager", stringBuilder2.toString());
                }
            }
            view = findFocus;
        }
        findFocus = FocusFinder.getInstance().findNextFocus(this, view, i);
        int i2 = 66;
        int i3 = 17;
        if (findFocus != null && findFocus != view) {
            boolean requestFocus;
            int i4;
            int i5;
            if (i == i3) {
                i4 = getChildRectInPagerCoordinates(this.mTempRect, findFocus).left;
                i5 = getChildRectInPagerCoordinates(this.mTempRect, view).left;
                if (view == null || i4 < i5) {
                    requestFocus = findFocus.requestFocus();
                } else {
                    requestFocus = pageLeft();
                }
            } else if (i == i2) {
                i4 = getChildRectInPagerCoordinates(this.mTempRect, findFocus).left;
                i5 = getChildRectInPagerCoordinates(this.mTempRect, view).left;
                if (view == null || i4 > i5) {
                    requestFocus = findFocus.requestFocus();
                } else {
                    requestFocus = pageRight();
                }
            }
            z2 = requestFocus;
        } else if (i == i3 || i == z) {
            z2 = pageLeft();
        } else if (i == i2 || i == 2) {
            z2 = pageRight();
        }
        if (z2) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
        }
        return z2;
    }

    private Rect getChildRectInPagerCoordinates(Rect rect, View view) {
        if (rect == null) {
            rect = new Rect();
        }
        if (view == null) {
            int i = 0;
            rect.set(i, i, i, i);
            return rect;
        }
        rect.left = view.getLeft();
        rect.right = view.getRight();
        rect.top = view.getTop();
        rect.bottom = view.getBottom();
        ViewPager parent = view.getParent();
        while ((parent instanceof ViewGroup) && parent != this) {
            ViewGroup viewGroup = parent;
            rect.left += viewGroup.getLeft();
            rect.right += viewGroup.getRight();
            rect.top += viewGroup.getTop();
            rect.bottom += viewGroup.getBottom();
            parent = viewGroup.getParent();
        }
        return rect;
    }

    boolean pageLeft() {
        if (this.mCurItem <= 0) {
            return false;
        }
        boolean z = true;
        setCurrentItem(this.mCurItem - z, z);
        return z;
    }

    boolean pageRight() {
        if (this.mAdapter != null) {
            boolean z = true;
            if (this.mCurItem < this.mAdapter.getCount() - z) {
                setCurrentItem(this.mCurItem + z, z);
                return z;
            }
        }
        return false;
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        int size = arrayList.size();
        int descendantFocusability = getDescendantFocusability();
        if (descendantFocusability != 393216) {
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt.getVisibility() == 0) {
                    ItemInfo infoForChild = infoForChild(childAt);
                    if (infoForChild != null && infoForChild.position == this.mCurItem) {
                        childAt.addFocusables(arrayList, i, i2);
                    }
                }
            }
        }
        if ((descendantFocusability != 262144 || size == arrayList.size()) && isFocusable()) {
            i = 1;
            if (!(((i2 & i) == i && isInTouchMode() && !isFocusableInTouchMode()) || arrayList == null)) {
                arrayList.add(this);
            }
        }
    }

    public void addTouchables(ArrayList<View> arrayList) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem) {
                    childAt.addTouchables(arrayList);
                }
            }
        }
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        int i2;
        int childCount = getChildCount();
        int i3 = -1;
        boolean z = false;
        boolean z2 = true;
        if ((i & 2) != 0) {
            i3 = childCount;
            childCount = z;
            i2 = z2;
        } else {
            childCount--;
            i2 = i3;
        }
        while (childCount != i3) {
            View childAt = getChildAt(childCount);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem && childAt.requestFocus(i, rect)) {
                    return z2;
                }
            }
            childCount += i2;
        }
        return z;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return super.dispatchPopulateAccessibilityEvent(accessibilityEvent);
        }
        int childCount = getChildCount();
        boolean z = false;
        for (int i = z; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt.getVisibility() == 0) {
                ItemInfo infoForChild = infoForChild(childAt);
                if (infoForChild != null && infoForChild.position == this.mCurItem && childAt.dispatchPopulateAccessibilityEvent(accessibilityEvent)) {
                    return true;
                }
            }
        }
        return z;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return generateDefaultLayoutParams();
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }
}
