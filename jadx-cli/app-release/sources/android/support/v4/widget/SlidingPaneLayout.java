package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SlidingPaneLayout extends ViewGroup {
    private static final int DEFAULT_FADE_COLOR = -858993460;
    private static final int DEFAULT_OVERHANG_SIZE = 32;
    static final SlidingPanelLayoutImpl IMPL;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final String TAG = "SlidingPaneLayout";
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private PanelSlideListener mPanelSlideListener;
    private int mParallaxBy;
    private float mParallaxOffset;
    final ArrayList<DisableLayerRunnable> mPostedRunnables;
    boolean mPreservedOpenState;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    float mSlideOffset;
    int mSlideRange;
    View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;

    private class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View view) {
            this.mChildView = view;
        }

        public void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                this.mChildView.setLayerType(0, null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int[] ATTRS = new int[]{16843137};
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;

        public LayoutParams() {
            int i = -1;
            super(i, i);
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.weight = 0.0f;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.weight = 0.0f;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.weight = 0.0f;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.weight = 0.0f;
            this.weight = layoutParams.weight;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            float f = 0.0f;
            this.weight = f;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, ATTRS);
            this.weight = obtainStyledAttributes.getFloat(0, f);
            obtainStyledAttributes.recycle();
        }
    }

    public interface PanelSlideListener {
        void onPanelClosed(View view);

        void onPanelOpened(View view);

        void onPanelSlide(View view, float f);
    }

    interface SlidingPanelLayoutImpl {
        void invalidateChildRegion(SlidingPaneLayout slidingPaneLayout, View view);
    }

    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        AccessibilityDelegate() {
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
            super.onInitializeAccessibilityNodeInfo(view, obtain);
            copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
            obtain.recycle();
            accessibilityNodeInfoCompat.setClassName(SlidingPaneLayout.class.getName());
            accessibilityNodeInfoCompat.setSource(view);
            ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(view);
            if (parentForAccessibility instanceof View) {
                accessibilityNodeInfoCompat.setParent((View) parentForAccessibility);
            }
            int childCount = SlidingPaneLayout.this.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = SlidingPaneLayout.this.getChildAt(i);
                if (!filter(childAt) && childAt.getVisibility() == 0) {
                    ViewCompat.setImportantForAccessibility(childAt, 1);
                    accessibilityNodeInfoCompat.addChild(childAt);
                }
            }
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(SlidingPaneLayout.class.getName());
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            return !filter(view) ? super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent) : false;
        }

        public boolean filter(View view) {
            return SlidingPaneLayout.this.isDimmed(view);
        }

        private void copyNodeInfoNoChildren(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2) {
            Rect rect = this.mTmpRect;
            accessibilityNodeInfoCompat2.getBoundsInParent(rect);
            accessibilityNodeInfoCompat.setBoundsInParent(rect);
            accessibilityNodeInfoCompat2.getBoundsInScreen(rect);
            accessibilityNodeInfoCompat.setBoundsInScreen(rect);
            accessibilityNodeInfoCompat.setVisibleToUser(accessibilityNodeInfoCompat2.isVisibleToUser());
            accessibilityNodeInfoCompat.setPackageName(accessibilityNodeInfoCompat2.getPackageName());
            accessibilityNodeInfoCompat.setClassName(accessibilityNodeInfoCompat2.getClassName());
            accessibilityNodeInfoCompat.setContentDescription(accessibilityNodeInfoCompat2.getContentDescription());
            accessibilityNodeInfoCompat.setEnabled(accessibilityNodeInfoCompat2.isEnabled());
            accessibilityNodeInfoCompat.setClickable(accessibilityNodeInfoCompat2.isClickable());
            accessibilityNodeInfoCompat.setFocusable(accessibilityNodeInfoCompat2.isFocusable());
            accessibilityNodeInfoCompat.setFocused(accessibilityNodeInfoCompat2.isFocused());
            accessibilityNodeInfoCompat.setAccessibilityFocused(accessibilityNodeInfoCompat2.isAccessibilityFocused());
            accessibilityNodeInfoCompat.setSelected(accessibilityNodeInfoCompat2.isSelected());
            accessibilityNodeInfoCompat.setLongClickable(accessibilityNodeInfoCompat2.isLongClickable());
            accessibilityNodeInfoCompat.addAction(accessibilityNodeInfoCompat2.getActions());
            accessibilityNodeInfoCompat.setMovementGranularities(accessibilityNodeInfoCompat2.getMovementGranularities());
        }
    }

    private class DragHelperCallback extends Callback {
        DragHelperCallback() {
        }

        public boolean tryCaptureView(View view, int i) {
            if (SlidingPaneLayout.this.mIsUnableToDrag) {
                return false;
            }
            return ((LayoutParams) view.getLayoutParams()).slideable;
        }

        public void onViewDragStateChanged(int i) {
            if (SlidingPaneLayout.this.mDragHelper.getViewDragState() != 0) {
                return;
            }
            if (SlidingPaneLayout.this.mSlideOffset == 0.0f) {
                SlidingPaneLayout.this.updateObscuredViewsVisibility(SlidingPaneLayout.this.mSlideableView);
                SlidingPaneLayout.this.dispatchOnPanelClosed(SlidingPaneLayout.this.mSlideableView);
                SlidingPaneLayout.this.mPreservedOpenState = false;
                return;
            }
            SlidingPaneLayout.this.dispatchOnPanelOpened(SlidingPaneLayout.this.mSlideableView);
            SlidingPaneLayout.this.mPreservedOpenState = true;
        }

        public void onViewCaptured(View view, int i) {
            SlidingPaneLayout.this.setAllChildrenVisible();
        }

        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            SlidingPaneLayout.this.onPanelDragged(i);
            SlidingPaneLayout.this.invalidate();
        }

        public void onViewReleased(View view, float f, float f2) {
            int width;
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            float f3 = 0.5f;
            float f4 = 0.0f;
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                int paddingRight = SlidingPaneLayout.this.getPaddingRight() + layoutParams.rightMargin;
                if (f < f4 || (f == f4 && SlidingPaneLayout.this.mSlideOffset > f3)) {
                    paddingRight += SlidingPaneLayout.this.mSlideRange;
                }
                width = (SlidingPaneLayout.this.getWidth() - paddingRight) - SlidingPaneLayout.this.mSlideableView.getWidth();
            } else {
                width = layoutParams.leftMargin + SlidingPaneLayout.this.getPaddingLeft();
                if (f > f4 || (f == f4 && SlidingPaneLayout.this.mSlideOffset > f3)) {
                    width += SlidingPaneLayout.this.mSlideRange;
                }
            }
            SlidingPaneLayout.this.mDragHelper.settleCapturedViewAt(width, view.getTop());
            SlidingPaneLayout.this.invalidate();
        }

        public int getViewHorizontalDragRange(View view) {
            return SlidingPaneLayout.this.mSlideRange;
        }

        public int clampViewPositionHorizontal(View view, int i, int i2) {
            LayoutParams layoutParams = (LayoutParams) SlidingPaneLayout.this.mSlideableView.getLayoutParams();
            if (SlidingPaneLayout.this.isLayoutRtlSupport()) {
                i2 = SlidingPaneLayout.this.getWidth() - ((SlidingPaneLayout.this.getPaddingRight() + layoutParams.rightMargin) + SlidingPaneLayout.this.mSlideableView.getWidth());
                return Math.max(Math.min(i, i2), i2 - SlidingPaneLayout.this.mSlideRange);
            }
            i2 = SlidingPaneLayout.this.getPaddingLeft() + layoutParams.leftMargin;
            return Math.min(Math.max(i, i2), SlidingPaneLayout.this.mSlideRange + i2);
        }

        public int clampViewPositionVertical(View view, int i, int i2) {
            return view.getTop();
        }

        public void onEdgeDragStarted(int i, int i2) {
            SlidingPaneLayout.this.mDragHelper.captureChildView(SlidingPaneLayout.this.mSlideableView, i2);
        }
    }

    static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, null);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean isOpen;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.isOpen = parcel.readInt() != 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.isOpen);
        }
    }

    public static class SimplePanelSlideListener implements PanelSlideListener {
        public void onPanelClosed(View view) {
        }

        public void onPanelOpened(View view) {
        }

        public void onPanelSlide(View view, float f) {
        }
    }

    static class SlidingPanelLayoutImplBase implements SlidingPanelLayoutImpl {
        SlidingPanelLayoutImplBase() {
        }

        public void invalidateChildRegion(SlidingPaneLayout slidingPaneLayout, View view) {
            ViewCompat.postInvalidateOnAnimation(slidingPaneLayout, view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        }
    }

    @RequiresApi(16)
    static class SlidingPanelLayoutImplJB extends SlidingPanelLayoutImplBase {
        private Method mGetDisplayList;
        private Field mRecreateDisplayList;

        SlidingPanelLayoutImplJB() {
            try {
                this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", (Class[]) null);
            } catch (Throwable e) {
                Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", e);
            }
            try {
                this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                this.mRecreateDisplayList.setAccessible(true);
            } catch (Throwable e2) {
                Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e2);
            }
        }

        public void invalidateChildRegion(SlidingPaneLayout slidingPaneLayout, View view) {
            if (this.mGetDisplayList == null || this.mRecreateDisplayList == null) {
                view.invalidate();
                return;
            }
            try {
                this.mRecreateDisplayList.setBoolean(view, true);
                this.mGetDisplayList.invoke(view, (Object[]) null);
            } catch (Throwable e) {
                Log.e("SlidingPaneLayout", "Error refreshing display list state", e);
            }
            super.invalidateChildRegion(slidingPaneLayout, view);
        }
    }

    @RequiresApi(17)
    static class SlidingPanelLayoutImplJBMR1 extends SlidingPanelLayoutImplBase {
        SlidingPanelLayoutImplJBMR1() {
        }

        public void invalidateChildRegion(SlidingPaneLayout slidingPaneLayout, View view) {
            ViewCompat.setLayerPaint(view, ((LayoutParams) view.getLayoutParams()).dimPaint);
        }
    }

    static {
        if (VERSION.SDK_INT >= 17) {
            IMPL = new SlidingPanelLayoutImplJBMR1();
        } else if (VERSION.SDK_INT >= 16) {
            IMPL = new SlidingPanelLayoutImplJB();
        } else {
            IMPL = new SlidingPanelLayoutImplBase();
        }
    }

    public SlidingPaneLayout(Context context) {
        this(context, null);
    }

    public SlidingPaneLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SlidingPaneLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSliderFadeColor = -858993460;
        boolean z = true;
        this.mFirstLayout = z;
        this.mTmpRect = new Rect();
        this.mPostedRunnables = new ArrayList();
        float f = context.getResources().getDisplayMetrics().density;
        float f2 = 0.5f;
        this.mOverhangSize = (int) ((32.0f * f) + f2);
        setWillNotDraw(false);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        ViewCompat.setImportantForAccessibility(this, z);
        this.mDragHelper = ViewDragHelper.create(this, f2, new DragHelperCallback());
        this.mDragHelper.setMinVelocity(400.0f * f);
    }

    public void setParallaxDistance(int i) {
        this.mParallaxBy = i;
        requestLayout();
    }

    public int getParallaxDistance() {
        return this.mParallaxBy;
    }

    public void setSliderFadeColor(@ColorInt int i) {
        this.mSliderFadeColor = i;
    }

    @ColorInt
    public int getSliderFadeColor() {
        return this.mSliderFadeColor;
    }

    public void setCoveredFadeColor(@ColorInt int i) {
        this.mCoveredFadeColor = i;
    }

    @ColorInt
    public int getCoveredFadeColor() {
        return this.mCoveredFadeColor;
    }

    public void setPanelSlideListener(PanelSlideListener panelSlideListener) {
        this.mPanelSlideListener = panelSlideListener;
    }

    void dispatchOnPanelSlide(View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelSlide(view, this.mSlideOffset);
        }
    }

    void dispatchOnPanelOpened(View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelOpened(view);
        }
        sendAccessibilityEvent(32);
    }

    void dispatchOnPanelClosed(View view) {
        if (this.mPanelSlideListener != null) {
            this.mPanelSlideListener.onPanelClosed(view);
        }
        sendAccessibilityEvent(32);
    }

    void updateObscuredViewsVisibility(View view) {
        int i;
        int i2;
        int i3;
        int i4;
        View view2 = view;
        boolean isLayoutRtlSupport = isLayoutRtlSupport();
        int width = isLayoutRtlSupport ? getWidth() - getPaddingRight() : getPaddingLeft();
        int paddingLeft = isLayoutRtlSupport ? getPaddingLeft() : getWidth() - getPaddingRight();
        int paddingTop = getPaddingTop();
        int height = getHeight() - getPaddingBottom();
        if (view2 == null || !viewIsOpaque(view)) {
            i = 0;
            i2 = 0;
            i3 = 0;
            i4 = 0;
        } else {
            i = view.getLeft();
            i2 = view.getRight();
            i3 = view.getTop();
            i4 = view.getBottom();
        }
        int childCount = getChildCount();
        int i5 = 0;
        while (i5 < childCount) {
            View childAt = getChildAt(i5);
            if (childAt != view2) {
                boolean z;
                if (childAt.getVisibility() == 8) {
                    z = isLayoutRtlSupport;
                } else {
                    int i6;
                    int max = Math.max(isLayoutRtlSupport ? paddingLeft : width, childAt.getLeft());
                    int max2 = Math.max(paddingTop, childAt.getTop());
                    if (isLayoutRtlSupport) {
                        z = isLayoutRtlSupport;
                        i6 = width;
                    } else {
                        z = isLayoutRtlSupport;
                        i6 = paddingLeft;
                    }
                    max = (max < i || max2 < i3 || Math.min(i6, childAt.getRight()) > i2 || Math.min(height, childAt.getBottom()) > i4) ? 0 : 4;
                    childAt.setVisibility(max);
                }
                i5++;
                isLayoutRtlSupport = z;
                view2 = view;
            } else {
                return;
            }
        }
        SlidingPaneLayout slidingPaneLayout = this;
    }

    void setAllChildrenVisible() {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = i; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() == 4) {
                childAt.setVisibility(i);
            }
        }
    }

    private static boolean viewIsOpaque(View view) {
        boolean z = true;
        if (view.isOpaque()) {
            return z;
        }
        boolean z2 = false;
        if (VERSION.SDK_INT >= 18) {
            return z2;
        }
        Drawable background = view.getBackground();
        if (background == null) {
            return z2;
        }
        if (background.getOpacity() != -1) {
            z = z2;
        }
        return z;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        int size = this.mPostedRunnables.size();
        for (int i = 0; i < size; i++) {
            ((DisableLayerRunnable) this.mPostedRunnables.get(i)).run();
        }
        this.mPostedRunnables.clear();
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        boolean z;
        int i4;
        Object obj;
        Object obj2;
        SlidingPaneLayout slidingPaneLayout = this;
        int mode = MeasureSpec.getMode(i);
        int size = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size2 = MeasureSpec.getSize(i2);
        int i5 = 300;
        int i6 = Integer.MIN_VALUE;
        int i7 = 1073741824;
        if (mode != i7) {
            if (!isInEditMode()) {
                throw new IllegalStateException("Width must have an exact value or MATCH_PARENT");
            } else if (mode != i6 && mode == 0) {
                size = i5;
            }
        } else if (mode2 == 0) {
            if (!isInEditMode()) {
                throw new IllegalStateException("Height must not be UNSPECIFIED");
            } else if (mode2 == 0) {
                size2 = i5;
                mode2 = i6;
            }
        }
        boolean z2 = false;
        if (mode2 != i6) {
            if (mode2 != i7) {
                size2 = z2;
            } else {
                size2 = (size2 - getPaddingTop()) - getPaddingBottom();
            }
            i5 = size2;
        } else {
            i5 = (size2 - getPaddingTop()) - getPaddingBottom();
            size2 = z2;
        }
        int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
        int childCount = getChildCount();
        if (childCount > 2) {
            Log.e("SlidingPaneLayout", "onMeasure: More than two child views are not supported.");
        }
        slidingPaneLayout.mSlideableView = null;
        boolean z3 = z2;
        int i8 = size2;
        int i9 = paddingLeft;
        float f = 0.0f;
        size2 = z3;
        while (true) {
            i3 = 8;
            z = true;
            if (size2 >= childCount) {
                break;
            }
            View childAt = getChildAt(size2);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (childAt.getVisibility() == i3) {
                layoutParams.dimWhenOffset = z2;
            } else {
                if (layoutParams.weight > 0.0f) {
                    f += layoutParams.weight;
                    if (layoutParams.width == 0) {
                    }
                }
                i4 = layoutParams.leftMargin + layoutParams.rightMargin;
                if (layoutParams.width == -2) {
                    mode = MeasureSpec.makeMeasureSpec(paddingLeft - i4, Integer.MIN_VALUE);
                    Object obj3 = 1073741824;
                } else if (layoutParams.width == -1) {
                    mode = MeasureSpec.makeMeasureSpec(paddingLeft - i4, 1073741824);
                } else {
                    mode = MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824);
                }
                if (layoutParams.height == -2) {
                    i3 = MeasureSpec.makeMeasureSpec(i5, Integer.MIN_VALUE);
                } else if (layoutParams.height == -1) {
                    i3 = MeasureSpec.makeMeasureSpec(i5, 1073741824);
                } else {
                    i3 = MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
                }
                childAt.measure(mode, i3);
                mode = childAt.getMeasuredWidth();
                i4 = childAt.getMeasuredHeight();
                if (mode2 == Integer.MIN_VALUE && i4 > i8) {
                    i8 = Math.min(i4, i5);
                }
                i9 -= mode;
                z2 = i9 < 0 ? z : false;
                layoutParams.slideable = z2;
                z2 |= z3;
                if (layoutParams.slideable) {
                    slidingPaneLayout.mSlideableView = childAt;
                }
                z3 = z2;
            }
            size2++;
            z2 = false;
            obj = Integer.MIN_VALUE;
            obj2 = 1073741824;
        }
        if (z3 || f > 0.0f) {
            mode = paddingLeft - slidingPaneLayout.mOverhangSize;
            mode2 = 0;
            while (mode2 < childCount) {
                int i10;
                Object obj4;
                View childAt2 = getChildAt(mode2);
                if (childAt2.getVisibility() != i3) {
                    LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                    if (childAt2.getVisibility() != i3) {
                        boolean z4 = (layoutParams2.width != 0 || layoutParams2.weight <= 0.0f) ? false : z;
                        if (z4) {
                            i4 = 0;
                        } else {
                            i4 = childAt2.getMeasuredWidth();
                        }
                        if (!z3 || childAt2 == slidingPaneLayout.mSlideableView) {
                            if (layoutParams2.weight > 0.0f) {
                                int makeMeasureSpec;
                                if (layoutParams2.width != 0) {
                                    makeMeasureSpec = MeasureSpec.makeMeasureSpec(childAt2.getMeasuredHeight(), 1073741824);
                                } else if (layoutParams2.height == -2) {
                                    i3 = MeasureSpec.makeMeasureSpec(i5, Integer.MIN_VALUE);
                                    obj2 = 1073741824;
                                    if (z3) {
                                        i10 = mode;
                                        childAt2.measure(MeasureSpec.makeMeasureSpec(i4 + ((int) ((layoutParams2.weight * ((float) Math.max(0, i9))) / f)), 1073741824), i3);
                                        mode2++;
                                        mode = i10;
                                        i3 = 8;
                                    } else {
                                        i6 = paddingLeft - (layoutParams2.leftMargin + layoutParams2.rightMargin);
                                        i10 = mode;
                                        mode = MeasureSpec.makeMeasureSpec(i6, 1073741824);
                                        if (i4 != i6) {
                                            childAt2.measure(mode, i3);
                                        }
                                        obj4 = null;
                                        obj = 1073741824;
                                        mode2++;
                                        mode = i10;
                                        i3 = 8;
                                    }
                                } else if (layoutParams2.height == -1) {
                                    makeMeasureSpec = MeasureSpec.makeMeasureSpec(i5, 1073741824);
                                } else {
                                    makeMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams2.height, 1073741824);
                                }
                                i3 = makeMeasureSpec;
                                if (z3) {
                                    i10 = mode;
                                    childAt2.measure(MeasureSpec.makeMeasureSpec(i4 + ((int) ((layoutParams2.weight * ((float) Math.max(0, i9))) / f)), 1073741824), i3);
                                    mode2++;
                                    mode = i10;
                                    i3 = 8;
                                } else {
                                    i6 = paddingLeft - (layoutParams2.leftMargin + layoutParams2.rightMargin);
                                    i10 = mode;
                                    mode = MeasureSpec.makeMeasureSpec(i6, 1073741824);
                                    if (i4 != i6) {
                                        childAt2.measure(mode, i3);
                                    }
                                    obj4 = null;
                                    obj = 1073741824;
                                    mode2++;
                                    mode = i10;
                                    i3 = 8;
                                }
                            }
                        } else if (layoutParams2.width < 0 && (i4 > mode || layoutParams2.weight > 0.0f)) {
                            if (!z4) {
                                i7 = 1073741824;
                                i6 = MeasureSpec.makeMeasureSpec(childAt2.getMeasuredHeight(), i7);
                            } else if (layoutParams2.height == -2) {
                                i6 = MeasureSpec.makeMeasureSpec(i5, Integer.MIN_VALUE);
                                i7 = 1073741824;
                            } else if (layoutParams2.height == -1) {
                                i7 = 1073741824;
                                i6 = MeasureSpec.makeMeasureSpec(i5, i7);
                            } else {
                                i7 = 1073741824;
                                i6 = MeasureSpec.makeMeasureSpec(layoutParams2.height, i7);
                            }
                            childAt2.measure(MeasureSpec.makeMeasureSpec(mode, i7), i6);
                        }
                    }
                }
                i10 = mode;
                obj4 = null;
                obj = 1073741824;
                mode2++;
                mode = i10;
                i3 = 8;
            }
        }
        setMeasuredDimension(size, (i8 + getPaddingTop()) + getPaddingBottom());
        slidingPaneLayout.mCanSlide = z3;
        if (slidingPaneLayout.mDragHelper.getViewDragState() != 0 && !z3) {
            slidingPaneLayout.mDragHelper.abort();
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        SlidingPaneLayout slidingPaneLayout = this;
        boolean isLayoutRtlSupport = isLayoutRtlSupport();
        int i5 = 1;
        if (isLayoutRtlSupport) {
            slidingPaneLayout.mDragHelper.setEdgeTrackingEnabled(2);
        } else {
            slidingPaneLayout.mDragHelper.setEdgeTrackingEnabled(i5);
        }
        int i6 = i3 - i;
        int paddingRight = isLayoutRtlSupport ? getPaddingRight() : getPaddingLeft();
        int paddingLeft = isLayoutRtlSupport ? getPaddingLeft() : getPaddingRight();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        if (slidingPaneLayout.mFirstLayout) {
            float f = (slidingPaneLayout.mCanSlide && slidingPaneLayout.mPreservedOpenState) ? 1.0f : 0.0f;
            slidingPaneLayout.mSlideOffset = f;
        }
        int i7 = paddingRight;
        int i8 = i7;
        paddingRight = 0;
        while (paddingRight < childCount) {
            View childAt = getChildAt(paddingRight);
            Object obj;
            if (childAt.getVisibility() == 8) {
                obj = 1065353216;
            } else {
                int min;
                int i9;
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int measuredWidth = childAt.getMeasuredWidth();
                if (layoutParams.slideable) {
                    int i10 = i6 - paddingLeft;
                    min = (Math.min(i7, i10 - slidingPaneLayout.mOverhangSize) - i8) - (layoutParams.leftMargin + layoutParams.rightMargin);
                    slidingPaneLayout.mSlideRange = min;
                    i5 = isLayoutRtlSupport ? layoutParams.rightMargin : layoutParams.leftMargin;
                    layoutParams.dimWhenOffset = ((i8 + i5) + min) + (measuredWidth / 2) > i10;
                    i10 = (int) (((float) min) * slidingPaneLayout.mSlideOffset);
                    i5 = (i5 + i10) + i8;
                    slidingPaneLayout.mSlideOffset = ((float) i10) / ((float) slidingPaneLayout.mSlideRange);
                    obj = 1065353216;
                } else if (!slidingPaneLayout.mCanSlide || slidingPaneLayout.mParallaxBy == 0) {
                    obj = 1065353216;
                    i5 = i7;
                } else {
                    min = (int) ((1.0f - slidingPaneLayout.mSlideOffset) * ((float) slidingPaneLayout.mParallaxBy));
                    i5 = i7;
                    if (isLayoutRtlSupport) {
                        min = i5 - min;
                        i9 = min + measuredWidth;
                    } else {
                        i9 = (i6 - i5) + min;
                        min = i9 - measuredWidth;
                    }
                    childAt.layout(min, paddingTop, i9, childAt.getMeasuredHeight() + paddingTop);
                    i7 += childAt.getWidth();
                    i8 = i5;
                }
                min = 0;
                if (isLayoutRtlSupport) {
                    min = i5 - min;
                    i9 = min + measuredWidth;
                } else {
                    i9 = (i6 - i5) + min;
                    min = i9 - measuredWidth;
                }
                childAt.layout(min, paddingTop, i9, childAt.getMeasuredHeight() + paddingTop);
                i7 += childAt.getWidth();
                i8 = i5;
            }
            paddingRight++;
            Object obj2 = 1;
        }
        if (slidingPaneLayout.mFirstLayout) {
            if (slidingPaneLayout.mCanSlide) {
                if (slidingPaneLayout.mParallaxBy != 0) {
                    parallaxOtherViews(slidingPaneLayout.mSlideOffset);
                }
                if (((LayoutParams) slidingPaneLayout.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    dimChildView(slidingPaneLayout.mSlideableView, slidingPaneLayout.mSlideOffset, slidingPaneLayout.mSliderFadeColor);
                }
            } else {
                for (int i11 = 0; i11 < childCount; i11++) {
                    dimChildView(getChildAt(i11), 0.0f, slidingPaneLayout.mSliderFadeColor);
                }
            }
            updateObscuredViewsVisibility(slidingPaneLayout.mSlideableView);
        }
        slidingPaneLayout.mFirstLayout = false;
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3) {
            this.mFirstLayout = true;
        }
    }

    public void requestChildFocus(View view, View view2) {
        super.requestChildFocus(view, view2);
        if (!isInTouchMode() && !this.mCanSlide) {
            this.mPreservedOpenState = view == this.mSlideableView;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean actionMasked = motionEvent.getActionMasked();
        boolean z = true;
        if (!(this.mCanSlide || actionMasked || getChildCount() <= z)) {
            View childAt = getChildAt(z);
            if (childAt != null) {
                this.mPreservedOpenState = this.mDragHelper.isViewUnder(childAt, (int) motionEvent.getX(), (int) motionEvent.getY()) ^ z;
            }
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && actionMasked)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(motionEvent);
        }
        boolean z2 = false;
        if (actionMasked || actionMasked == z) {
            this.mDragHelper.cancel();
            return z2;
        }
        float x;
        float y;
        if (!actionMasked) {
            this.mIsUnableToDrag = z2;
            x = motionEvent.getX();
            y = motionEvent.getY();
            this.mInitialMotionX = x;
            this.mInitialMotionY = y;
            if (this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y) && isDimmed(this.mSlideableView)) {
                actionMasked = z;
                if (!(this.mDragHelper.shouldInterceptTouchEvent(motionEvent) || r0)) {
                    z = z2;
                }
                return z;
            }
        } else if (actionMasked) {
            x = motionEvent.getX();
            y = motionEvent.getY();
            x = Math.abs(x - this.mInitialMotionX);
            y = Math.abs(y - this.mInitialMotionY);
            if (x > ((float) this.mDragHelper.getTouchSlop()) && y > x) {
                this.mDragHelper.cancel();
                this.mIsUnableToDrag = z;
                return z2;
            }
        }
        actionMasked = z2;
        z = z2;
        return z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!this.mCanSlide) {
            return super.onTouchEvent(motionEvent);
        }
        this.mDragHelper.processTouchEvent(motionEvent);
        boolean z = true;
        float x;
        float y;
        switch (motionEvent.getActionMasked()) {
            case 0:
                x = motionEvent.getX();
                y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                break;
            case 1:
                if (isDimmed(this.mSlideableView)) {
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    float f = x - this.mInitialMotionX;
                    float f2 = y - this.mInitialMotionY;
                    int touchSlop = this.mDragHelper.getTouchSlop();
                    if ((f * f) + (f2 * f2) < ((float) (touchSlop * touchSlop)) && this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y)) {
                        closePane(this.mSlideableView, 0);
                        break;
                    }
                }
                break;
        }
        return z;
    }

    private boolean closePane(View view, int i) {
        boolean z = false;
        if (!this.mFirstLayout && !smoothSlideTo(0.0f, i)) {
            return z;
        }
        this.mPreservedOpenState = z;
        return true;
    }

    private boolean openPane(View view, int i) {
        if (!this.mFirstLayout && !smoothSlideTo(1.0f, i)) {
            return false;
        }
        boolean z = true;
        this.mPreservedOpenState = z;
        return z;
    }

    @Deprecated
    public void smoothSlideOpen() {
        openPane();
    }

    public boolean openPane() {
        return openPane(this.mSlideableView, 0);
    }

    @Deprecated
    public void smoothSlideClosed() {
        closePane();
    }

    public boolean closePane() {
        return closePane(this.mSlideableView, 0);
    }

    public boolean isOpen() {
        return !this.mCanSlide || this.mSlideOffset == 1.0f;
    }

    @Deprecated
    public boolean canSlide() {
        return this.mCanSlide;
    }

    public boolean isSlideable() {
        return this.mCanSlide;
    }

    void onPanelDragged(int i) {
        if (this.mSlideableView == null) {
            this.mSlideOffset = 0.0f;
            return;
        }
        boolean isLayoutRtlSupport = isLayoutRtlSupport();
        LayoutParams layoutParams = (LayoutParams) this.mSlideableView.getLayoutParams();
        int width = this.mSlideableView.getWidth();
        if (isLayoutRtlSupport) {
            i = (getWidth() - i) - width;
        }
        this.mSlideOffset = ((float) (i - ((isLayoutRtlSupport ? getPaddingRight() : getPaddingLeft()) + (isLayoutRtlSupport ? layoutParams.rightMargin : layoutParams.leftMargin)))) / ((float) this.mSlideRange);
        if (this.mParallaxBy != 0) {
            parallaxOtherViews(this.mSlideOffset);
        }
        if (layoutParams.dimWhenOffset) {
            dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
        }
        dispatchOnPanelSlide(this.mSlideableView);
    }

    private void dimChildView(View view, float f, int i) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (f > 0.0f && i != 0) {
            int i2 = (((int) (((float) ((-16777216 & i) >>> 24)) * f)) << 24) | (i & 16777215);
            if (layoutParams.dimPaint == null) {
                layoutParams.dimPaint = new Paint();
            }
            layoutParams.dimPaint.setColorFilter(new PorterDuffColorFilter(i2, Mode.SRC_OVER));
            i = 2;
            if (view.getLayerType() != i) {
                view.setLayerType(i, layoutParams.dimPaint);
            }
            invalidateChildRegion(view);
        } else if (view.getLayerType() != 0) {
            if (layoutParams.dimPaint != null) {
                layoutParams.dimPaint.setColorFilter(null);
            }
            Runnable disableLayerRunnable = new DisableLayerRunnable(view);
            this.mPostedRunnables.add(disableLayerRunnable);
            ViewCompat.postOnAnimation(this, disableLayerRunnable);
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int save = canvas.save(2);
        if (!(!this.mCanSlide || layoutParams.slideable || this.mSlideableView == null)) {
            canvas.getClipBounds(this.mTmpRect);
            if (isLayoutRtlSupport()) {
                this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
            } else {
                this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
            }
            canvas.clipRect(this.mTmpRect);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restoreToCount(save);
        return drawChild;
    }

    void invalidateChildRegion(View view) {
        IMPL.invalidateChildRegion(this, view);
    }

    boolean smoothSlideTo(float f, int i) {
        boolean z = false;
        if (!this.mCanSlide) {
            return z;
        }
        int width;
        LayoutParams layoutParams = (LayoutParams) this.mSlideableView.getLayoutParams();
        if (isLayoutRtlSupport()) {
            width = (int) (((float) getWidth()) - ((((float) (getPaddingRight() + layoutParams.rightMargin)) + (f * ((float) this.mSlideRange))) + ((float) this.mSlideableView.getWidth())));
        } else {
            width = (int) (((float) (getPaddingLeft() + layoutParams.leftMargin)) + (f * ((float) this.mSlideRange)));
        }
        if (!this.mDragHelper.smoothSlideViewTo(this.mSlideableView, width, this.mSlideableView.getTop())) {
            return z;
        }
        setAllChildrenVisible();
        ViewCompat.postInvalidateOnAnimation(this);
        return true;
    }

    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (this.mCanSlide) {
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                this.mDragHelper.abort();
            }
        }
    }

    @Deprecated
    public void setShadowDrawable(Drawable drawable) {
        setShadowDrawableLeft(drawable);
    }

    public void setShadowDrawableLeft(Drawable drawable) {
        this.mShadowDrawableLeft = drawable;
    }

    public void setShadowDrawableRight(Drawable drawable) {
        this.mShadowDrawableRight = drawable;
    }

    @Deprecated
    public void setShadowResource(@DrawableRes int i) {
        setShadowDrawable(getResources().getDrawable(i));
    }

    public void setShadowResourceLeft(int i) {
        setShadowDrawableLeft(ContextCompat.getDrawable(getContext(), i));
    }

    public void setShadowResourceRight(int i) {
        setShadowDrawableRight(ContextCompat.getDrawable(getContext(), i));
    }

    public void draw(Canvas canvas) {
        Drawable drawable;
        super.draw(canvas);
        if (isLayoutRtlSupport()) {
            drawable = this.mShadowDrawableRight;
        } else {
            drawable = this.mShadowDrawableLeft;
        }
        int i = 1;
        View childAt = getChildCount() > i ? getChildAt(i) : null;
        if (childAt != null && drawable != null) {
            int right;
            i = childAt.getTop();
            int bottom = childAt.getBottom();
            int intrinsicWidth = drawable.getIntrinsicWidth();
            if (isLayoutRtlSupport()) {
                right = childAt.getRight();
                intrinsicWidth += right;
            } else {
                right = childAt.getLeft();
                int i2 = right - intrinsicWidth;
                intrinsicWidth = right;
                right = i2;
            }
            drawable.setBounds(right, i, intrinsicWidth, bottom);
            drawable.draw(canvas);
        }
    }

    private void parallaxOtherViews(float f) {
        int i;
        int childCount;
        boolean isLayoutRtlSupport = isLayoutRtlSupport();
        LayoutParams layoutParams = (LayoutParams) this.mSlideableView.getLayoutParams();
        int i2 = 0;
        if (layoutParams.dimWhenOffset) {
            if ((isLayoutRtlSupport ? layoutParams.rightMargin : layoutParams.leftMargin) <= 0) {
                i = 1;
                childCount = getChildCount();
                while (i2 < childCount) {
                    View childAt = getChildAt(i2);
                    if (childAt != this.mSlideableView) {
                        float f2 = 1.0f;
                        int i3 = (int) ((f2 - this.mParallaxOffset) * ((float) this.mParallaxBy));
                        this.mParallaxOffset = f;
                        i3 -= (int) ((f2 - f) * ((float) this.mParallaxBy));
                        if (isLayoutRtlSupport) {
                            i3 = -i3;
                        }
                        childAt.offsetLeftAndRight(i3);
                        if (i != 0) {
                            dimChildView(childAt, isLayoutRtlSupport ? this.mParallaxOffset - f2 : f2 - this.mParallaxOffset, this.mCoveredFadeColor);
                        }
                    }
                    i2++;
                }
            }
        }
        i = i2;
        childCount = getChildCount();
        while (i2 < childCount) {
            View childAt2 = getChildAt(i2);
            if (childAt2 != this.mSlideableView) {
                float f22 = 1.0f;
                int i32 = (int) ((f22 - this.mParallaxOffset) * ((float) this.mParallaxBy));
                this.mParallaxOffset = f;
                i32 -= (int) ((f22 - f) * ((float) this.mParallaxBy));
                if (isLayoutRtlSupport) {
                    i32 = -i32;
                }
                childAt2.offsetLeftAndRight(i32);
                if (i != 0) {
                    dimChildView(childAt2, isLayoutRtlSupport ? this.mParallaxOffset - f22 : f22 - this.mParallaxOffset, this.mCoveredFadeColor);
                }
            }
            i2++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected boolean canScroll(android.view.View r14, boolean r15, int r16, int r17, int r18) {
        /*
        r13 = this;
        r0 = r14;
        r1 = r0 instanceof android.view.ViewGroup;
        r2 = 1;
        if (r1 == 0) goto L_0x0053;
    L_0x0006:
        r1 = r0;
        r1 = (android.view.ViewGroup) r1;
        r3 = r0.getScrollX();
        r4 = r0.getScrollY();
        r5 = r1.getChildCount();
        r5 = r5 - r2;
    L_0x0016:
        if (r5 < 0) goto L_0x0053;
    L_0x0018:
        r7 = r1.getChildAt(r5);
        r6 = r17 + r3;
        r8 = r7.getLeft();
        if (r6 < r8) goto L_0x0050;
    L_0x0024:
        r8 = r7.getRight();
        if (r6 >= r8) goto L_0x0050;
    L_0x002a:
        r8 = r18 + r4;
        r9 = r7.getTop();
        if (r8 < r9) goto L_0x0050;
    L_0x0032:
        r9 = r7.getBottom();
        if (r8 >= r9) goto L_0x0050;
    L_0x0038:
        r9 = 1;
        r10 = r7.getLeft();
        r10 = r6 - r10;
        r6 = r7.getTop();
        r11 = r8 - r6;
        r6 = r13;
        r8 = r9;
        r9 = r16;
        r6 = r6.canScroll(r7, r8, r9, r10, r11);
        if (r6 == 0) goto L_0x0050;
    L_0x004f:
        return r2;
    L_0x0050:
        r5 = r5 + -1;
        goto L_0x0016;
    L_0x0053:
        if (r15 == 0) goto L_0x0068;
    L_0x0055:
        r1 = r13.isLayoutRtlSupport();
        if (r1 == 0) goto L_0x005e;
    L_0x005b:
        r1 = r16;
        goto L_0x0061;
    L_0x005e:
        r1 = r16;
        r1 = -r1;
    L_0x0061:
        r0 = r0.canScrollHorizontally(r1);
        if (r0 == 0) goto L_0x0068;
    L_0x0067:
        goto L_0x0069;
    L_0x0068:
        r2 = 0;
    L_0x0069:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SlidingPaneLayout.canScroll(android.view.View, boolean, int, int, int):boolean");
    }

    boolean isDimmed(View view) {
        boolean z = false;
        if (view == null) {
            return z;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (this.mCanSlide && layoutParams.dimWhenOffset && this.mSlideOffset > 0.0f) {
            z = true;
        }
        return z;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        savedState.isOpen = isSlideable() ? isOpen() : this.mPreservedOpenState;
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (savedState.isOpen) {
                openPane();
            } else {
                closePane();
            }
            this.mPreservedOpenState = savedState.isOpen;
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    boolean isLayoutRtlSupport() {
        boolean z = true;
        return ViewCompat.getLayoutDirection(this) == z ? z : false;
    }
}
