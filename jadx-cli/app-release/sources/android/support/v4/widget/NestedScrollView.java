package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import java.util.List;

public class NestedScrollView extends FrameLayout implements NestedScrollingParent, NestedScrollingChild2, ScrollingView {
    private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
    static final int ANIMATED_SCROLL_GAP = 250;
    private static final int INVALID_POINTER = -1;
    static final float MAX_SCROLL_FACTOR = 0.5f;
    private static final int[] SCROLLVIEW_STYLEABLE = new int[]{16843130};
    private static final String TAG = "NestedScrollView";
    private int mActivePointerId;
    private final NestedScrollingChildHelper mChildHelper;
    private View mChildToScrollTo;
    private EdgeEffect mEdgeGlowBottom;
    private EdgeEffect mEdgeGlowTop;
    private boolean mFillViewport;
    private boolean mIsBeingDragged;
    private boolean mIsLaidOut;
    private boolean mIsLayoutDirty;
    private int mLastMotionY;
    private long mLastScroll;
    private int mLastScrollerY;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private int mNestedYOffset;
    private OnScrollChangeListener mOnScrollChangeListener;
    private final NestedScrollingParentHelper mParentHelper;
    private SavedState mSavedState;
    private final int[] mScrollConsumed;
    private final int[] mScrollOffset;
    private OverScroller mScroller;
    private boolean mSmoothScrollingEnabled;
    private final Rect mTempRect;
    private int mTouchSlop;
    private VelocityTracker mVelocityTracker;
    private float mVerticalScrollFactor;

    public interface OnScrollChangeListener {
        void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4);
    }

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        public int scrollPosition;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        SavedState(Parcel parcel) {
            super(parcel);
            this.scrollPosition = parcel.readInt();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.scrollPosition);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("HorizontalScrollView.SavedState{");
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append(" scrollPosition=");
            stringBuilder.append(this.scrollPosition);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    static class AccessibilityDelegate extends AccessibilityDelegateCompat {
        AccessibilityDelegate() {
        }

        public boolean performAccessibilityAction(View view, int i, Bundle bundle) {
            boolean z = true;
            if (super.performAccessibilityAction(view, i, bundle)) {
                return z;
            }
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            boolean z2 = false;
            if (!nestedScrollView.isEnabled()) {
                return z2;
            }
            if (i == 4096) {
                i = Math.min(nestedScrollView.getScrollY() + ((nestedScrollView.getHeight() - nestedScrollView.getPaddingBottom()) - nestedScrollView.getPaddingTop()), nestedScrollView.getScrollRange());
                if (i == nestedScrollView.getScrollY()) {
                    return z2;
                }
                nestedScrollView.smoothScrollTo(z2, i);
                return z;
            } else if (i != 8192) {
                return z2;
            } else {
                i = Math.max(nestedScrollView.getScrollY() - ((nestedScrollView.getHeight() - nestedScrollView.getPaddingBottom()) - nestedScrollView.getPaddingTop()), z2);
                if (i == nestedScrollView.getScrollY()) {
                    return z2;
                }
                nestedScrollView.smoothScrollTo(z2, i);
                return z;
            }
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            accessibilityNodeInfoCompat.setClassName(ScrollView.class.getName());
            if (nestedScrollView.isEnabled()) {
                int scrollRange = nestedScrollView.getScrollRange();
                if (scrollRange > 0) {
                    accessibilityNodeInfoCompat.setScrollable(true);
                    if (nestedScrollView.getScrollY() > 0) {
                        accessibilityNodeInfoCompat.addAction(8192);
                    }
                    if (nestedScrollView.getScrollY() < scrollRange) {
                        accessibilityNodeInfoCompat.addAction(4096);
                    }
                }
            }
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            NestedScrollView nestedScrollView = (NestedScrollView) view;
            accessibilityEvent.setClassName(ScrollView.class.getName());
            accessibilityEvent.setScrollable(nestedScrollView.getScrollRange() > 0);
            accessibilityEvent.setScrollX(nestedScrollView.getScrollX());
            accessibilityEvent.setScrollY(nestedScrollView.getScrollY());
            AccessibilityRecordCompat.setMaxScrollX(accessibilityEvent, nestedScrollView.getScrollX());
            AccessibilityRecordCompat.setMaxScrollY(accessibilityEvent, nestedScrollView.getScrollRange());
        }
    }

    private static int clamp(int i, int i2, int i3) {
        return (i2 >= i3 || i < 0) ? 0 : i2 + i > i3 ? i3 - i2 : i;
    }

    public boolean onStartNestedScroll(View view, View view2, int i) {
        return (i & 2) != 0;
    }

    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public NestedScrollView(Context context) {
        this(context, null);
    }

    public NestedScrollView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mTempRect = new Rect();
        boolean z = true;
        this.mIsLayoutDirty = z;
        boolean z2 = false;
        this.mIsLaidOut = z2;
        this.mChildToScrollTo = null;
        this.mIsBeingDragged = z2;
        this.mSmoothScrollingEnabled = z;
        this.mActivePointerId = -1;
        int i2 = 2;
        this.mScrollOffset = new int[i2];
        this.mScrollConsumed = new int[i2];
        initScrollView();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, SCROLLVIEW_STYLEABLE, i, z2);
        setFillViewport(obtainStyledAttributes.getBoolean(z2, z2));
        obtainStyledAttributes.recycle();
        this.mParentHelper = new NestedScrollingParentHelper(this);
        this.mChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(z);
        ViewCompat.setAccessibilityDelegate(this, ACCESSIBILITY_DELEGATE);
    }

    public void setNestedScrollingEnabled(boolean z) {
        this.mChildHelper.setNestedScrollingEnabled(z);
    }

    public boolean isNestedScrollingEnabled() {
        return this.mChildHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int i) {
        return this.mChildHelper.startNestedScroll(i);
    }

    public boolean startNestedScroll(int i, int i2) {
        return this.mChildHelper.startNestedScroll(i, i2);
    }

    public void stopNestedScroll() {
        this.mChildHelper.stopNestedScroll();
    }

    public void stopNestedScroll(int i) {
        this.mChildHelper.stopNestedScroll(i);
    }

    public boolean hasNestedScrollingParent() {
        return this.mChildHelper.hasNestedScrollingParent();
    }

    public boolean hasNestedScrollingParent(int i) {
        return this.mChildHelper.hasNestedScrollingParent(i);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr) {
        return this.mChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5) {
        return this.mChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2) {
        return this.mChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return this.mChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        return this.mChildHelper.dispatchNestedFling(f, f2, z);
    }

    public boolean dispatchNestedPreFling(float f, float f2) {
        return this.mChildHelper.dispatchNestedPreFling(f, f2);
    }

    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mParentHelper.onNestedScrollAccepted(view, view2, i);
        startNestedScroll(2);
    }

    public void onStopNestedScroll(View view) {
        this.mParentHelper.onStopNestedScroll(view);
        stopNestedScroll();
    }

    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        int scrollY = getScrollY();
        scrollBy(0, i4);
        int scrollY2 = getScrollY() - scrollY;
        dispatchNestedScroll(0, scrollY2, 0, i4 - scrollY2, null);
    }

    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        dispatchNestedPreScroll(i, i2, iArr, null);
    }

    public boolean onNestedFling(View view, float f, float f2, boolean z) {
        if (z) {
            return false;
        }
        flingWithNestedDispatch((int) f2);
        return true;
    }

    public boolean onNestedPreFling(View view, float f, float f2) {
        return dispatchNestedPreFling(f, f2);
    }

    public int getNestedScrollAxes() {
        return this.mParentHelper.getNestedScrollAxes();
    }

    protected float getTopFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int scrollY = getScrollY();
        return scrollY < verticalFadingEdgeLength ? ((float) scrollY) / ((float) verticalFadingEdgeLength) : 1.0f;
    }

    protected float getBottomFadingEdgeStrength() {
        if (getChildCount() == 0) {
            return 0.0f;
        }
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int bottom = (getChildAt(0).getBottom() - getScrollY()) - (getHeight() - getPaddingBottom());
        return bottom < verticalFadingEdgeLength ? ((float) bottom) / ((float) verticalFadingEdgeLength) : 1.0f;
    }

    public int getMaxScrollAmount() {
        return (int) (0.5f * ((float) getHeight()));
    }

    private void initScrollView() {
        this.mScroller = new OverScroller(getContext());
        setFocusable(true);
        setDescendantFocusability(262144);
        setWillNotDraw(false);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    }

    public void addView(View view) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view);
    }

    public void addView(View view, int i) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, i);
    }

    public void addView(View view, LayoutParams layoutParams) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, layoutParams);
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        if (getChildCount() > 0) {
            throw new IllegalStateException("ScrollView can host only one direct child");
        }
        super.addView(view, i, layoutParams);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    private boolean canScroll() {
        boolean z = false;
        View childAt = getChildAt(z);
        if (childAt == null) {
            return z;
        }
        if (getHeight() < (childAt.getHeight() + getPaddingTop()) + getPaddingBottom()) {
            z = true;
        }
        return z;
    }

    public boolean isFillViewport() {
        return this.mFillViewport;
    }

    public void setFillViewport(boolean z) {
        if (z != this.mFillViewport) {
            this.mFillViewport = z;
            requestLayout();
        }
    }

    public boolean isSmoothScrollingEnabled() {
        return this.mSmoothScrollingEnabled;
    }

    public void setSmoothScrollingEnabled(boolean z) {
        this.mSmoothScrollingEnabled = z;
    }

    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mOnScrollChangeListener != null) {
            this.mOnScrollChangeListener.onScrollChange(this, i, i2, i3, i4);
        }
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mFillViewport && MeasureSpec.getMode(i2) != 0 && getChildCount() > 0) {
            View childAt = getChildAt(0);
            int measuredHeight = getMeasuredHeight();
            if (childAt.getMeasuredHeight() < measuredHeight) {
                childAt.measure(getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight(), ((FrameLayout.LayoutParams) childAt.getLayoutParams()).width), MeasureSpec.makeMeasureSpec((measuredHeight - getPaddingTop()) - getPaddingBottom(), 1073741824));
            }
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return super.dispatchKeyEvent(keyEvent) || executeKeyEvent(keyEvent);
    }

    public boolean executeKeyEvent(KeyEvent keyEvent) {
        this.mTempRect.setEmpty();
        boolean z = false;
        int i = 130;
        if (canScroll()) {
            if (keyEvent.getAction() == 0) {
                int keyCode = keyEvent.getKeyCode();
                int i2 = 33;
                if (keyCode != 62) {
                    switch (keyCode) {
                        case 19:
                            if (!keyEvent.isAltPressed()) {
                                z = arrowScroll(i2);
                                break;
                            }
                            z = fullScroll(i2);
                            break;
                        case 20:
                            if (!keyEvent.isAltPressed()) {
                                z = arrowScroll(i);
                                break;
                            }
                            z = fullScroll(i);
                            break;
                    }
                }
                if (keyEvent.isShiftPressed()) {
                    i = i2;
                }
                pageScroll(i);
            }
            return z;
        } else if (!isFocused() || keyEvent.getKeyCode() == 4) {
            return z;
        } else {
            View findFocus = findFocus();
            if (findFocus == this) {
                findFocus = null;
            }
            findFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
            if (!(findFocus == null || findFocus == this || !findFocus.requestFocus(i))) {
                z = true;
            }
            return z;
        }
    }

    private boolean inChild(int i, int i2) {
        boolean z = false;
        if (getChildCount() <= 0) {
            return z;
        }
        int scrollY = getScrollY();
        View childAt = getChildAt(z);
        if (i2 >= childAt.getTop() - scrollY && i2 < childAt.getBottom() - scrollY && i >= childAt.getLeft() && i < childAt.getRight()) {
            z = true;
        }
        return z;
    }

    private void initOrResetVelocityTracker() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        } else {
            this.mVelocityTracker.clear();
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
    }

    private void recycleVelocityTracker() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        if (z) {
            recycleVelocityTracker();
        }
        super.requestDisallowInterceptTouchEvent(z);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int i = 2;
        boolean z = true;
        if (action == i && this.mIsBeingDragged) {
            return z;
        }
        action &= 255;
        if (action != 6) {
            int i2 = -1;
            boolean z2 = false;
            switch (action) {
                case 0:
                    action = (int) motionEvent.getY();
                    if (!inChild((int) motionEvent.getX(), action)) {
                        this.mIsBeingDragged = z2;
                        recycleVelocityTracker();
                        break;
                    }
                    this.mLastMotionY = action;
                    this.mActivePointerId = motionEvent.getPointerId(z2);
                    initOrResetVelocityTracker();
                    this.mVelocityTracker.addMovement(motionEvent);
                    this.mScroller.computeScrollOffset();
                    this.mIsBeingDragged = this.mScroller.isFinished() ^ z;
                    startNestedScroll(i, z2);
                    break;
                case 1:
                case 3:
                    this.mIsBeingDragged = z2;
                    this.mActivePointerId = i2;
                    recycleVelocityTracker();
                    if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                        ViewCompat.postInvalidateOnAnimation(this);
                    }
                    stopNestedScroll(z2);
                    break;
                case 2:
                    action = this.mActivePointerId;
                    if (action != i2) {
                        int findPointerIndex = motionEvent.findPointerIndex(action);
                        if (findPointerIndex != i2) {
                            action = (int) motionEvent.getY(findPointerIndex);
                            if (Math.abs(action - this.mLastMotionY) > this.mTouchSlop && (i & getNestedScrollAxes()) == 0) {
                                this.mIsBeingDragged = z;
                                this.mLastMotionY = action;
                                initVelocityTrackerIfNotExists();
                                this.mVelocityTracker.addMovement(motionEvent);
                                this.mNestedYOffset = z2;
                                ViewParent parent = getParent();
                                if (parent != null) {
                                    parent.requestDisallowInterceptTouchEvent(z);
                                    break;
                                }
                            }
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Invalid pointerId=");
                        stringBuilder.append(action);
                        stringBuilder.append(" in onInterceptTouchEvent");
                        Log.e("NestedScrollView", stringBuilder.toString());
                        break;
                    }
                    break;
            }
        }
        onSecondaryPointerUp(motionEvent);
        return this.mIsBeingDragged;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        NestedScrollView nestedScrollView = this;
        MotionEvent motionEvent2 = motionEvent;
        initVelocityTrackerIfNotExists();
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        int actionMasked = motionEvent.getActionMasked();
        boolean z = false;
        if (actionMasked == 0) {
            nestedScrollView.mNestedYOffset = z;
        }
        float f = 0.0f;
        obtain.offsetLocation(f, (float) nestedScrollView.mNestedYOffset);
        int i = -1;
        boolean z2 = true;
        boolean isFinished;
        ViewParent parent;
        switch (actionMasked) {
            case 0:
                if (getChildCount() != 0) {
                    isFinished = nestedScrollView.mScroller.isFinished() ^ z2;
                    nestedScrollView.mIsBeingDragged = isFinished;
                    if (isFinished) {
                        parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(z2);
                        }
                    }
                    if (!nestedScrollView.mScroller.isFinished()) {
                        nestedScrollView.mScroller.abortAnimation();
                    }
                    nestedScrollView.mLastMotionY = (int) motionEvent.getY();
                    nestedScrollView.mActivePointerId = motionEvent2.getPointerId(z);
                    startNestedScroll(2, z);
                    break;
                }
                return z;
            case 1:
                VelocityTracker velocityTracker = nestedScrollView.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, (float) nestedScrollView.mMaximumVelocity);
                actionMasked = (int) velocityTracker.getYVelocity(nestedScrollView.mActivePointerId);
                if (Math.abs(actionMasked) > nestedScrollView.mMinimumVelocity) {
                    flingWithNestedDispatch(-actionMasked);
                } else if (nestedScrollView.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                nestedScrollView.mActivePointerId = i;
                endDrag();
                break;
            case 2:
                int findPointerIndex = motionEvent2.findPointerIndex(nestedScrollView.mActivePointerId);
                if (findPointerIndex != i) {
                    int y = (int) motionEvent2.getY(findPointerIndex);
                    int i2 = nestedScrollView.mLastMotionY - y;
                    if (dispatchNestedPreScroll(0, i2, nestedScrollView.mScrollConsumed, nestedScrollView.mScrollOffset, 0)) {
                        i2 -= nestedScrollView.mScrollConsumed[z2];
                        obtain.offsetLocation(f, (float) nestedScrollView.mScrollOffset[z2]);
                        nestedScrollView.mNestedYOffset += nestedScrollView.mScrollOffset[z2];
                    }
                    if (!nestedScrollView.mIsBeingDragged && Math.abs(i2) > nestedScrollView.mTouchSlop) {
                        parent = getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(z2);
                        }
                        nestedScrollView.mIsBeingDragged = z2;
                        if (i2 > 0) {
                            i2 -= nestedScrollView.mTouchSlop;
                        } else {
                            i2 += nestedScrollView.mTouchSlop;
                        }
                    }
                    int i3 = i2;
                    if (nestedScrollView.mIsBeingDragged) {
                        nestedScrollView.mLastMotionY = y - nestedScrollView.mScrollOffset[z2];
                        int scrollY = getScrollY();
                        i2 = getScrollRange();
                        isFinished = getOverScrollMode();
                        boolean z3 = (!isFinished || (isFinished == z2 && i2 > 0)) ? z2 : z;
                        int i4 = i2;
                        int i5 = i3;
                        int i6 = findPointerIndex;
                        if (overScrollByCompat(0, i3, 0, getScrollY(), 0, i2, 0, 0, true) && !hasNestedScrollingParent(z)) {
                            nestedScrollView.mVelocityTracker.clear();
                        }
                        int scrollY2 = getScrollY() - scrollY;
                        if (!dispatchNestedScroll(0, scrollY2, 0, i5 - scrollY2, nestedScrollView.mScrollOffset, 0)) {
                            if (z3) {
                                ensureGlows();
                                actionMasked = scrollY + i5;
                                if (actionMasked < 0) {
                                    EdgeEffectCompat.onPull(nestedScrollView.mEdgeGlowTop, ((float) i5) / ((float) getHeight()), motionEvent2.getX(i6) / ((float) getWidth()));
                                    if (!nestedScrollView.mEdgeGlowBottom.isFinished()) {
                                        nestedScrollView.mEdgeGlowBottom.onRelease();
                                    }
                                } else {
                                    scrollY2 = i6;
                                    if (actionMasked > i4) {
                                        EdgeEffectCompat.onPull(nestedScrollView.mEdgeGlowBottom, ((float) i5) / ((float) getHeight()), 1.0f - (motionEvent2.getX(scrollY2) / ((float) getWidth())));
                                        if (!nestedScrollView.mEdgeGlowTop.isFinished()) {
                                            nestedScrollView.mEdgeGlowTop.onRelease();
                                        }
                                    }
                                }
                                if (!(nestedScrollView.mEdgeGlowTop == null || (nestedScrollView.mEdgeGlowTop.isFinished() && nestedScrollView.mEdgeGlowBottom.isFinished()))) {
                                    ViewCompat.postInvalidateOnAnimation(this);
                                    break;
                                }
                            }
                        }
                        nestedScrollView.mLastMotionY -= nestedScrollView.mScrollOffset[z2];
                        obtain.offsetLocation(0.0f, (float) nestedScrollView.mScrollOffset[z2]);
                        nestedScrollView.mNestedYOffset += nestedScrollView.mScrollOffset[z2];
                        break;
                    }
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid pointerId=");
                stringBuilder.append(nestedScrollView.mActivePointerId);
                stringBuilder.append(" in onTouchEvent");
                Log.e("NestedScrollView", stringBuilder.toString());
                break;
                break;
            case 3:
                if (nestedScrollView.mIsBeingDragged && getChildCount() > 0 && nestedScrollView.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                nestedScrollView.mActivePointerId = i;
                endDrag();
                break;
            case 5:
                actionMasked = motionEvent.getActionIndex();
                nestedScrollView.mLastMotionY = (int) motionEvent2.getY(actionMasked);
                nestedScrollView.mActivePointerId = motionEvent2.getPointerId(actionMasked);
                break;
            case 6:
                onSecondaryPointerUp(motionEvent);
                nestedScrollView.mLastMotionY = (int) motionEvent2.getY(motionEvent2.findPointerIndex(nestedScrollView.mActivePointerId));
                break;
        }
        if (nestedScrollView.mVelocityTracker != null) {
            nestedScrollView.mVelocityTracker.addMovement(obtain);
        }
        obtain.recycle();
        return z2;
    }

    private void onSecondaryPointerUp(MotionEvent motionEvent) {
        int actionIndex = motionEvent.getActionIndex();
        if (motionEvent.getPointerId(actionIndex) == this.mActivePointerId) {
            actionIndex = actionIndex == 0 ? 1 : 0;
            this.mLastMotionY = (int) motionEvent.getY(actionIndex);
            this.mActivePointerId = motionEvent.getPointerId(actionIndex);
            if (this.mVelocityTracker != null) {
                this.mVelocityTracker.clear();
            }
        }
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        boolean z = false;
        if (!((motionEvent.getSource() & 2) == 0 || motionEvent.getAction() != 8 || this.mIsBeingDragged)) {
            float axisValue = motionEvent.getAxisValue(9);
            if (axisValue != 0.0f) {
                int verticalScrollFactorCompat = (int) (axisValue * getVerticalScrollFactorCompat());
                int scrollRange = getScrollRange();
                int scrollY = getScrollY();
                verticalScrollFactorCompat = scrollY - verticalScrollFactorCompat;
                if (verticalScrollFactorCompat < 0) {
                    verticalScrollFactorCompat = z;
                } else if (verticalScrollFactorCompat > scrollRange) {
                    verticalScrollFactorCompat = scrollRange;
                }
                if (verticalScrollFactorCompat != scrollY) {
                    super.scrollTo(getScrollX(), verticalScrollFactorCompat);
                    return true;
                }
            }
        }
        return z;
    }

    private float getVerticalScrollFactorCompat() {
        if (this.mVerticalScrollFactor == 0.0f) {
            TypedValue typedValue = new TypedValue();
            Context context = getContext();
            if (context.getTheme().resolveAttribute(16842829, typedValue, true)) {
                this.mVerticalScrollFactor = typedValue.getDimension(context.getResources().getDisplayMetrics());
            } else {
                throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
            }
        }
        return this.mVerticalScrollFactor;
    }

    protected void onOverScrolled(int i, int i2, boolean z, boolean z2) {
        super.scrollTo(i, i2);
    }

    boolean overScrollByCompat(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        NestedScrollView nestedScrollView = this;
        boolean overScrollMode = getOverScrollMode();
        boolean z2 = false;
        boolean z3 = true;
        int i9 = computeHorizontalScrollRange() > computeHorizontalScrollExtent() ? z3 : z2;
        int i10 = computeVerticalScrollRange() > computeVerticalScrollExtent() ? z3 : z2;
        boolean z4 = (!overScrollMode || (overScrollMode == z3 && i9 != 0)) ? z3 : z2;
        boolean z5 = (!overScrollMode || (overScrollMode == z3 && i10 != 0)) ? z3 : z2;
        int i11 = i3 + i;
        int i12 = !z4 ? z2 : i7;
        i9 = i4 + i2;
        i10 = !z5 ? z2 : i8;
        int i13 = -i12;
        i12 += i5;
        int i14 = -i10;
        i10 += i6;
        if (i11 > i12) {
            overScrollMode = z3;
            i13 = i12;
        } else if (i11 < i13) {
            overScrollMode = z3;
        } else {
            i13 = i11;
            overScrollMode = z2;
        }
        if (i9 > i10) {
            i14 = i10;
        } else if (i9 >= i14) {
            i14 = i9;
            z4 = z2;
            if (z4 && !hasNestedScrollingParent(z3)) {
                nestedScrollView.mScroller.springBack(i13, i14, 0, 0, 0, getScrollRange());
            }
            onOverScrolled(i13, i14, overScrollMode, z4);
            return (overScrollMode || z4) ? z3 : z2;
        }
        z4 = z3;
        nestedScrollView.mScroller.springBack(i13, i14, 0, 0, 0, getScrollRange());
        onOverScrolled(i13, i14, overScrollMode, z4);
        if (!overScrollMode) {
        }
    }

    int getScrollRange() {
        int i = 0;
        return getChildCount() > 0 ? Math.max(i, getChildAt(i).getHeight() - ((getHeight() - getPaddingBottom()) - getPaddingTop())) : i;
    }

    private View findFocusableViewInBounds(boolean z, int i, int i2) {
        List focusables = getFocusables(2);
        int size = focusables.size();
        int i3 = 0;
        int i4 = i3;
        View view = null;
        for (int i5 = i4; i5 < size; i5++) {
            View view2 = (View) focusables.get(i5);
            int top = view2.getTop();
            int bottom = view2.getBottom();
            int i6 = 1;
            if (i < bottom && top < i2) {
                int i7 = (i >= top || bottom >= i2) ? i3 : i6;
                if (view == null) {
                    view = view2;
                    i4 = i7;
                } else {
                    top = ((!z || top >= view.getTop()) && (z || bottom <= view.getBottom())) ? i3 : i6;
                    if (i4 != 0) {
                        if (i7 != 0) {
                            if (top == 0) {
                            }
                        }
                    } else if (i7 != 0) {
                        view = view2;
                        i4 = i6;
                    } else if (top == 0) {
                    }
                    view = view2;
                }
            }
        }
        return view;
    }

    public boolean pageScroll(int i) {
        int i2 = 0;
        int i3 = 1;
        int i4 = i == 130 ? i3 : i2;
        int height = getHeight();
        if (i4 != 0) {
            this.mTempRect.top = getScrollY() + height;
            i2 = getChildCount();
            if (i2 > 0) {
                View childAt = getChildAt(i2 - i3);
                if (this.mTempRect.top + height > childAt.getBottom()) {
                    this.mTempRect.top = childAt.getBottom() - height;
                }
            }
        } else {
            this.mTempRect.top = getScrollY() - height;
            if (this.mTempRect.top < 0) {
                this.mTempRect.top = i2;
            }
        }
        this.mTempRect.bottom = this.mTempRect.top + height;
        return scrollAndFocus(i, this.mTempRect.top, this.mTempRect.bottom);
    }

    public boolean fullScroll(int i) {
        int i2 = 0;
        int i3 = 1;
        int i4 = i == 130 ? i3 : i2;
        int height = getHeight();
        this.mTempRect.top = i2;
        this.mTempRect.bottom = height;
        if (i4 != 0) {
            i2 = getChildCount();
            if (i2 > 0) {
                this.mTempRect.bottom = getChildAt(i2 - i3).getBottom() + getPaddingBottom();
                this.mTempRect.top = this.mTempRect.bottom - height;
            }
        }
        return scrollAndFocus(i, this.mTempRect.top, this.mTempRect.bottom);
    }

    private boolean scrollAndFocus(int i, int i2, int i3) {
        int height = getHeight();
        int scrollY = getScrollY();
        height += scrollY;
        boolean z = false;
        boolean z2 = true;
        boolean z3 = i == 33 ? z2 : z;
        View findFocusableViewInBounds = findFocusableViewInBounds(z3, i2, i3);
        if (findFocusableViewInBounds == null) {
            findFocusableViewInBounds = this;
        }
        if (i2 < scrollY || i3 > height) {
            doScrollY(z3 ? i2 - scrollY : i3 - height);
            z = z2;
        }
        if (findFocusableViewInBounds != findFocus()) {
            findFocusableViewInBounds.requestFocus(i);
        }
        return z;
    }

    public boolean arrowScroll(int i) {
        View findFocus = findFocus();
        if (findFocus == this) {
            findFocus = null;
        }
        View findNextFocus = FocusFinder.getInstance().findNextFocus(this, findFocus, i);
        int maxScrollAmount = getMaxScrollAmount();
        if (findNextFocus == null || !isWithinDeltaOfScreen(findNextFocus, maxScrollAmount, getHeight())) {
            boolean z = false;
            int i2 = 130;
            if (i == 33 && getScrollY() < maxScrollAmount) {
                maxScrollAmount = getScrollY();
            } else if (i == i2 && getChildCount() > 0) {
                int bottom = getChildAt(z).getBottom() - ((getScrollY() + getHeight()) - getPaddingBottom());
                if (bottom < maxScrollAmount) {
                    maxScrollAmount = bottom;
                }
            }
            if (maxScrollAmount == 0) {
                return z;
            }
            if (i != i2) {
                maxScrollAmount = -maxScrollAmount;
            }
            doScrollY(maxScrollAmount);
        } else {
            findNextFocus.getDrawingRect(this.mTempRect);
            offsetDescendantRectToMyCoords(findNextFocus, this.mTempRect);
            doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
            findNextFocus.requestFocus(i);
        }
        if (findFocus != null && findFocus.isFocused() && isOffScreen(findFocus)) {
            i = getDescendantFocusability();
            setDescendantFocusability(131072);
            requestFocus();
            setDescendantFocusability(i);
        }
        return true;
    }

    private boolean isOffScreen(View view) {
        return isWithinDeltaOfScreen(view, 0, getHeight()) ^ 1;
    }

    private boolean isWithinDeltaOfScreen(View view, int i, int i2) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        return this.mTempRect.bottom + i >= getScrollY() && this.mTempRect.top - i <= getScrollY() + i2;
    }

    private void doScrollY(int i) {
        if (i != 0) {
            int i2 = 0;
            if (this.mSmoothScrollingEnabled) {
                smoothScrollBy(i2, i);
            } else {
                scrollBy(i2, i);
            }
        }
    }

    public final void smoothScrollBy(int i, int i2) {
        if (getChildCount() != 0) {
            if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250) {
                int i3 = 0;
                i = Math.max(i3, getChildAt(i3).getHeight() - ((getHeight() - getPaddingBottom()) - getPaddingTop()));
                int scrollY = getScrollY();
                this.mScroller.startScroll(getScrollX(), scrollY, i3, Math.max(i3, Math.min(i2 + scrollY, i)) - scrollY);
                ViewCompat.postInvalidateOnAnimation(this);
            } else {
                if (!this.mScroller.isFinished()) {
                    this.mScroller.abortAnimation();
                }
                scrollBy(i, i2);
            }
            this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
        }
    }

    public final void smoothScrollTo(int i, int i2) {
        smoothScrollBy(i - getScrollX(), i2 - getScrollY());
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeVerticalScrollRange() {
        int height = (getHeight() - getPaddingBottom()) - getPaddingTop();
        if (getChildCount() == 0) {
            return height;
        }
        int i = 0;
        int bottom = getChildAt(i).getBottom();
        int scrollY = getScrollY();
        i = Math.max(i, bottom - height);
        if (scrollY < 0) {
            bottom -= scrollY;
        } else if (scrollY > i) {
            bottom += scrollY - i;
        }
        return bottom;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeVerticalScrollOffset() {
        return Math.max(0, super.computeVerticalScrollOffset());
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeVerticalScrollExtent() {
        return super.computeVerticalScrollExtent();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollRange() {
        return super.computeHorizontalScrollRange();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollOffset() {
        return super.computeHorizontalScrollOffset();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int computeHorizontalScrollExtent() {
        return super.computeHorizontalScrollExtent();
    }

    protected void measureChild(View view, int i, int i2) {
        i = getChildMeasureSpec(i, getPaddingLeft() + getPaddingRight(), view.getLayoutParams().width);
        i2 = 0;
        view.measure(i, MeasureSpec.makeMeasureSpec(i2, i2));
    }

    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        view.measure(getChildMeasureSpec(i, (((getPaddingLeft() + getPaddingRight()) + marginLayoutParams.leftMargin) + marginLayoutParams.rightMargin) + i2, marginLayoutParams.width), MeasureSpec.makeMeasureSpec(marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, 0));
    }

    public void computeScroll() {
        int i = 1;
        if (this.mScroller.computeScrollOffset()) {
            r10.mScroller.getCurrX();
            int currY = r10.mScroller.getCurrY();
            int i2 = currY - r10.mLastScrollerY;
            if (dispatchNestedPreScroll(0, i2, r10.mScrollConsumed, null, 1)) {
                i2 -= r10.mScrollConsumed[i];
            }
            int i3 = i2;
            if (i3 != 0) {
                int scrollRange = getScrollRange();
                int scrollY = getScrollY();
                int i4 = 0;
                int i5 = scrollY;
                overScrollByCompat(i4, i3, getScrollX(), scrollY, 0, scrollRange, 0, 0, false);
                int scrollY2 = getScrollY() - i5;
                if (!dispatchNestedScroll(i4, scrollY2, 0, i3 - scrollY2, null, 1)) {
                    int overScrollMode = getOverScrollMode();
                    int i6 = (overScrollMode == 0 || (overScrollMode == i && scrollRange > 0)) ? i : 0;
                    if (i6 != 0) {
                        ensureGlows();
                        if (currY <= 0 && i5 > 0) {
                            r10.mEdgeGlowTop.onAbsorb((int) r10.mScroller.getCurrVelocity());
                        } else if (currY >= scrollRange && i5 < scrollRange) {
                            r10.mEdgeGlowBottom.onAbsorb((int) r10.mScroller.getCurrVelocity());
                        }
                    }
                }
            }
            r10.mLastScrollerY = currY;
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }
        if (hasNestedScrollingParent(i)) {
            stopNestedScroll(i);
        }
        r10.mLastScrollerY = 0;
    }

    private void scrollToChild(View view) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
        if (computeScrollDeltaToGetChildRectOnScreen != 0) {
            scrollBy(0, computeScrollDeltaToGetChildRectOnScreen);
        }
    }

    private boolean scrollToChildRect(Rect rect, boolean z) {
        int computeScrollDeltaToGetChildRectOnScreen = computeScrollDeltaToGetChildRectOnScreen(rect);
        boolean z2 = false;
        boolean z3 = computeScrollDeltaToGetChildRectOnScreen != 0 ? true : z2;
        if (z3) {
            if (z) {
                scrollBy(z2, computeScrollDeltaToGetChildRectOnScreen);
            } else {
                smoothScrollBy(z2, computeScrollDeltaToGetChildRectOnScreen);
            }
        }
        return z3;
    }

    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        int i = 0;
        if (getChildCount() == 0) {
            return i;
        }
        int height = getHeight();
        int scrollY = getScrollY();
        int i2 = scrollY + height;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        if (rect.top > 0) {
            scrollY += verticalFadingEdgeLength;
        }
        if (rect.bottom < getChildAt(i).getHeight()) {
            i2 -= verticalFadingEdgeLength;
        }
        if (rect.bottom > i2 && rect.top > scrollY) {
            int i3;
            if (rect.height() > height) {
                i3 = (rect.top - scrollY) + i;
            } else {
                i3 = (rect.bottom - i2) + i;
            }
            i = Math.min(i3, getChildAt(i).getBottom() - i2);
        } else if (rect.top < scrollY && rect.bottom < i2) {
            if (rect.height() > height) {
                i -= i2 - rect.bottom;
            } else {
                i -= scrollY - rect.top;
            }
            i = Math.max(i, -getScrollY());
        }
        return i;
    }

    public void requestChildFocus(View view, View view2) {
        if (this.mIsLayoutDirty) {
            this.mChildToScrollTo = view2;
        } else {
            scrollToChild(view2);
        }
        super.requestChildFocus(view, view2);
    }

    protected boolean onRequestFocusInDescendants(int i, Rect rect) {
        View findNextFocus;
        if (i == 2) {
            i = 130;
        } else if (i == 1) {
            i = 33;
        }
        if (rect == null) {
            findNextFocus = FocusFinder.getInstance().findNextFocus(this, null, i);
        } else {
            findNextFocus = FocusFinder.getInstance().findNextFocusFromRect(this, rect, i);
        }
        boolean z = false;
        if (findNextFocus == null || isOffScreen(findNextFocus)) {
            return z;
        }
        return findNextFocus.requestFocus(i, rect);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
        return scrollToChildRect(rect, z);
    }

    public void requestLayout() {
        this.mIsLayoutDirty = true;
        super.requestLayout();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        z = false;
        this.mIsLayoutDirty = z;
        if (this.mChildToScrollTo != null && isViewDescendantOf(this.mChildToScrollTo, this)) {
            scrollToChild(this.mChildToScrollTo);
        }
        Object obj = null;
        this.mChildToScrollTo = obj;
        if (!this.mIsLaidOut) {
            if (this.mSavedState != null) {
                scrollTo(getScrollX(), this.mSavedState.scrollPosition);
                this.mSavedState = obj;
            }
            i = Math.max(z, (getChildCount() > 0 ? getChildAt(z).getMeasuredHeight() : z) - (((i4 - i2) - getPaddingBottom()) - getPaddingTop()));
            if (getScrollY() > i) {
                scrollTo(getScrollX(), i);
            } else if (getScrollY() < 0) {
                scrollTo(getScrollX(), z);
            }
        }
        scrollTo(getScrollX(), getScrollY());
        this.mIsLaidOut = true;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mIsLaidOut = false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void onSizeChanged(int r1, int r2, int r3, int r4) {
        /*
        r0 = this;
        super.onSizeChanged(r1, r2, r3, r4);
        r1 = r0.findFocus();
        if (r1 == 0) goto L_0x0027;
    L_0x0009:
        if (r0 != r1) goto L_0x000c;
    L_0x000b:
        goto L_0x0027;
    L_0x000c:
        r2 = 0;
        r2 = r0.isWithinDeltaOfScreen(r1, r2, r4);
        if (r2 == 0) goto L_0x0026;
    L_0x0013:
        r2 = r0.mTempRect;
        r1.getDrawingRect(r2);
        r2 = r0.mTempRect;
        r0.offsetDescendantRectToMyCoords(r1, r2);
        r1 = r0.mTempRect;
        r1 = r0.computeScrollDeltaToGetChildRectOnScreen(r1);
        r0.doScrollY(r1);
    L_0x0026:
        return;
    L_0x0027:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.NestedScrollView.onSizeChanged(int, int, int, int):void");
    }

    private static boolean isViewDescendantOf(View view, View view2) {
        boolean z = true;
        if (view == view2) {
            return z;
        }
        ViewParent parent = view.getParent();
        if (!((parent instanceof ViewGroup) && isViewDescendantOf((View) parent, view2))) {
            z = false;
        }
        return z;
    }

    public void fling(int i) {
        if (getChildCount() > 0) {
            startNestedScroll(2, 1);
            this.mScroller.fling(getScrollX(), getScrollY(), 0, i, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
            this.mLastScrollerY = getScrollY();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void flingWithNestedDispatch(int i) {
        int scrollY = getScrollY();
        boolean z = (scrollY > 0 || i > 0) && (scrollY < getScrollRange() || i < 0);
        float f = (float) i;
        float f2 = 0.0f;
        if (!dispatchNestedPreFling(f2, f)) {
            dispatchNestedFling(f2, f, z);
            fling(i);
        }
    }

    private void endDrag() {
        boolean z = false;
        this.mIsBeingDragged = z;
        recycleVelocityTracker();
        stopNestedScroll(z);
        if (this.mEdgeGlowTop != null) {
            this.mEdgeGlowTop.onRelease();
            this.mEdgeGlowBottom.onRelease();
        }
    }

    public void scrollTo(int i, int i2) {
        if (getChildCount() > 0) {
            View childAt = getChildAt(0);
            i = clamp(i, (getWidth() - getPaddingRight()) - getPaddingLeft(), childAt.getWidth());
            i2 = clamp(i2, (getHeight() - getPaddingBottom()) - getPaddingTop(), childAt.getHeight());
            if (i != getScrollX() || i2 != getScrollY()) {
                super.scrollTo(i, i2);
            }
        }
    }

    private void ensureGlows() {
        if (getOverScrollMode() == 2) {
            EdgeEffect edgeEffect = null;
            this.mEdgeGlowTop = edgeEffect;
            this.mEdgeGlowBottom = edgeEffect;
        } else if (this.mEdgeGlowTop == null) {
            Context context = getContext();
            this.mEdgeGlowTop = new EdgeEffect(context);
            this.mEdgeGlowBottom = new EdgeEffect(context);
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mEdgeGlowTop != null) {
            int save;
            int width;
            int scrollY = getScrollY();
            if (!this.mEdgeGlowTop.isFinished()) {
                save = canvas.save();
                width = (getWidth() - getPaddingLeft()) - getPaddingRight();
                canvas.translate((float) getPaddingLeft(), (float) Math.min(0, scrollY));
                this.mEdgeGlowTop.setSize(width, getHeight());
                if (this.mEdgeGlowTop.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(save);
            }
            if (!this.mEdgeGlowBottom.isFinished()) {
                save = canvas.save();
                width = (getWidth() - getPaddingLeft()) - getPaddingRight();
                int height = getHeight();
                canvas.translate((float) ((-width) + getPaddingLeft()), (float) (Math.max(getScrollRange(), scrollY) + height));
                canvas.rotate(180.0f, (float) width, 0.0f);
                this.mEdgeGlowBottom.setSize(width, height);
                if (this.mEdgeGlowBottom.draw(canvas)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
                canvas.restoreToCount(save);
            }
        }
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            this.mSavedState = savedState;
            requestLayout();
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        savedState.scrollPosition = getScrollY();
        return savedState;
    }
}
