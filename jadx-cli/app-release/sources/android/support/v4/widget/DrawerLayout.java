package android.support.v4.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class DrawerLayout extends ViewGroup {
    private static final boolean ALLOW_EDGE_LOCK = false;
    static final boolean CAN_HIDE_DESCENDANTS;
    private static final boolean CHILDREN_DISALLOW_INTERCEPT = true;
    private static final int DEFAULT_SCRIM_COLOR = -1728053248;
    private static final int DRAWER_ELEVATION = 10;
    static final int[] LAYOUT_ATTRS;
    public static final int LOCK_MODE_LOCKED_CLOSED = 1;
    public static final int LOCK_MODE_LOCKED_OPEN = 2;
    public static final int LOCK_MODE_UNDEFINED = 3;
    public static final int LOCK_MODE_UNLOCKED = 0;
    private static final int MIN_DRAWER_MARGIN = 64;
    private static final int MIN_FLING_VELOCITY = 400;
    private static final int PEEK_DELAY = 160;
    private static final boolean SET_DRAWER_SHADOW_FROM_ELEVATION;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_SETTLING = 2;
    private static final String TAG = "DrawerLayout";
    private static final int[] THEME_ATTRS;
    private static final float TOUCH_SLOP_SENSITIVITY = 1.0f;
    private final ChildAccessibilityDelegate mChildAccessibilityDelegate;
    private boolean mChildrenCanceledTouch;
    private boolean mDisallowInterceptRequested;
    private boolean mDrawStatusBarBackground;
    private float mDrawerElevation;
    private int mDrawerState;
    private boolean mFirstLayout;
    private boolean mInLayout;
    private float mInitialMotionX;
    private float mInitialMotionY;
    private Object mLastInsets;
    private final ViewDragCallback mLeftCallback;
    private final ViewDragHelper mLeftDragger;
    @Nullable
    private DrawerListener mListener;
    private List<DrawerListener> mListeners;
    private int mLockModeEnd;
    private int mLockModeLeft;
    private int mLockModeRight;
    private int mLockModeStart;
    private int mMinDrawerMargin;
    private final ArrayList<View> mNonDrawerViews;
    private final ViewDragCallback mRightCallback;
    private final ViewDragHelper mRightDragger;
    private int mScrimColor;
    private float mScrimOpacity;
    private Paint mScrimPaint;
    private Drawable mShadowEnd;
    private Drawable mShadowLeft;
    private Drawable mShadowLeftResolved;
    private Drawable mShadowRight;
    private Drawable mShadowRightResolved;
    private Drawable mShadowStart;
    private Drawable mStatusBarBackground;
    private CharSequence mTitleLeft;
    private CharSequence mTitleRight;

    public interface DrawerListener {
        void onDrawerClosed(View view);

        void onDrawerOpened(View view);

        void onDrawerSlide(View view, float f);

        void onDrawerStateChanged(int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface EdgeGravity {
    }

    public static class LayoutParams extends MarginLayoutParams {
        private static final int FLAG_IS_CLOSING = 4;
        private static final int FLAG_IS_OPENED = 1;
        private static final int FLAG_IS_OPENING = 2;
        public int gravity;
        boolean isPeeking;
        float onScreen;
        int openState;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            int i = 0;
            this.gravity = i;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, DrawerLayout.LAYOUT_ATTRS);
            this.gravity = obtainStyledAttributes.getInt(i, i);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = 0;
        }

        public LayoutParams(int i, int i2, int i3) {
            this(i, i2);
            this.gravity = i3;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = 0;
            this.gravity = layoutParams.gravity;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = 0;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = 0;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface LockMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface State {
    }

    class AccessibilityDelegate extends AccessibilityDelegateCompat {
        private final Rect mTmpRect = new Rect();

        AccessibilityDelegate() {
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            if (DrawerLayout.CAN_HIDE_DESCENDANTS) {
                super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            } else {
                AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(accessibilityNodeInfoCompat);
                super.onInitializeAccessibilityNodeInfo(view, obtain);
                accessibilityNodeInfoCompat.setSource(view);
                ViewParent parentForAccessibility = ViewCompat.getParentForAccessibility(view);
                if (parentForAccessibility instanceof View) {
                    accessibilityNodeInfoCompat.setParent((View) parentForAccessibility);
                }
                copyNodeInfoNoChildren(accessibilityNodeInfoCompat, obtain);
                obtain.recycle();
                addChildrenForAccessibility(accessibilityNodeInfoCompat, (ViewGroup) view);
            }
            accessibilityNodeInfoCompat.setClassName(DrawerLayout.class.getName());
            boolean z = false;
            accessibilityNodeInfoCompat.setFocusable(z);
            accessibilityNodeInfoCompat.setFocused(z);
            accessibilityNodeInfoCompat.removeAction(AccessibilityActionCompat.ACTION_FOCUS);
            accessibilityNodeInfoCompat.removeAction(AccessibilityActionCompat.ACTION_CLEAR_FOCUS);
        }

        public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            super.onInitializeAccessibilityEvent(view, accessibilityEvent);
            accessibilityEvent.setClassName(DrawerLayout.class.getName());
        }

        public boolean dispatchPopulateAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
            if (accessibilityEvent.getEventType() != 32) {
                return super.dispatchPopulateAccessibilityEvent(view, accessibilityEvent);
            }
            List text = accessibilityEvent.getText();
            View findVisibleDrawer = DrawerLayout.this.findVisibleDrawer();
            if (findVisibleDrawer != null) {
                CharSequence drawerTitle = DrawerLayout.this.getDrawerTitle(DrawerLayout.this.getDrawerViewAbsoluteGravity(findVisibleDrawer));
                if (drawerTitle != null) {
                    text.add(drawerTitle);
                }
            }
            return true;
        }

        public boolean onRequestSendAccessibilityEvent(ViewGroup viewGroup, View view, AccessibilityEvent accessibilityEvent) {
            if (DrawerLayout.CAN_HIDE_DESCENDANTS || DrawerLayout.includeChildForAccessibility(view)) {
                return super.onRequestSendAccessibilityEvent(viewGroup, view, accessibilityEvent);
            }
            return false;
        }

        private void addChildrenForAccessibility(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, ViewGroup viewGroup) {
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (DrawerLayout.includeChildForAccessibility(childAt)) {
                    accessibilityNodeInfoCompat.addChild(childAt);
                }
            }
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
        }
    }

    static final class ChildAccessibilityDelegate extends AccessibilityDelegateCompat {
        ChildAccessibilityDelegate() {
        }

        public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
            if (!DrawerLayout.includeChildForAccessibility(view)) {
                accessibilityNodeInfoCompat.setParent(null);
            }
        }
    }

    protected static class SavedState extends AbsSavedState {
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
        int lockModeEnd;
        int lockModeLeft;
        int lockModeRight;
        int lockModeStart;
        int openDrawerGravity = 0;

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.openDrawerGravity = parcel.readInt();
            this.lockModeLeft = parcel.readInt();
            this.lockModeRight = parcel.readInt();
            this.lockModeStart = parcel.readInt();
            this.lockModeEnd = parcel.readInt();
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.openDrawerGravity);
            parcel.writeInt(this.lockModeLeft);
            parcel.writeInt(this.lockModeRight);
            parcel.writeInt(this.lockModeStart);
            parcel.writeInt(this.lockModeEnd);
        }
    }

    public static abstract class SimpleDrawerListener implements DrawerListener {
        public void onDrawerClosed(View view) {
        }

        public void onDrawerOpened(View view) {
        }

        public void onDrawerSlide(View view, float f) {
        }

        public void onDrawerStateChanged(int i) {
        }
    }

    private class ViewDragCallback extends Callback {
        private final int mAbsGravity;
        private ViewDragHelper mDragger;
        private final Runnable mPeekRunnable = new Runnable() {
            public void run() {
                ViewDragCallback.this.peekDrawer();
            }
        };

        public boolean onEdgeLock(int i) {
            return false;
        }

        ViewDragCallback(int i) {
            this.mAbsGravity = i;
        }

        public void setDragger(ViewDragHelper viewDragHelper) {
            this.mDragger = viewDragHelper;
        }

        public void removeCallbacks() {
            DrawerLayout.this.removeCallbacks(this.mPeekRunnable);
        }

        public boolean tryCaptureView(View view, int i) {
            return DrawerLayout.this.isDrawerView(view) && DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, this.mAbsGravity) && DrawerLayout.this.getDrawerLockMode(view) == 0;
        }

        public void onViewDragStateChanged(int i) {
            DrawerLayout.this.updateDrawerState(this.mAbsGravity, i, this.mDragger.getCapturedView());
        }

        public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
            float f;
            i2 = view.getWidth();
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                f = ((float) (i + i2)) / ((float) i2);
            } else {
                f = ((float) (DrawerLayout.this.getWidth() - i)) / ((float) i2);
            }
            DrawerLayout.this.setDrawerViewOffset(view, f);
            view.setVisibility(f == 0.0f ? 4 : 0);
            DrawerLayout.this.invalidate();
        }

        public void onViewCaptured(View view, int i) {
            ((LayoutParams) view.getLayoutParams()).isPeeking = false;
            closeOtherDrawer();
        }

        private void closeOtherDrawer() {
            int i = 3;
            if (this.mAbsGravity == i) {
                i = 5;
            }
            View findDrawerWithGravity = DrawerLayout.this.findDrawerWithGravity(i);
            if (findDrawerWithGravity != null) {
                DrawerLayout.this.closeDrawer(findDrawerWithGravity);
            }
        }

        public void onViewReleased(View view, float f, float f2) {
            int i;
            f2 = DrawerLayout.this.getDrawerViewOffset(view);
            int width = view.getWidth();
            float f3 = 0.5f;
            float f4 = 0.0f;
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                i = (f > f4 || (f == f4 && f2 > f3)) ? 0 : -width;
            } else {
                int width2 = DrawerLayout.this.getWidth();
                if (f < f4 || (f == f4 && f2 > f3)) {
                    width2 -= width;
                }
                i = width2;
            }
            this.mDragger.settleCapturedViewAt(i, view.getTop());
            DrawerLayout.this.invalidate();
        }

        public void onEdgeTouched(int i, int i2) {
            DrawerLayout.this.postDelayed(this.mPeekRunnable, 160);
        }

        void peekDrawer() {
            View findDrawerWithGravity;
            int edgeSize = this.mDragger.getEdgeSize();
            int i = 0;
            boolean z = true;
            int i2 = 3;
            boolean z2 = this.mAbsGravity == i2 ? z : i;
            if (z2) {
                findDrawerWithGravity = DrawerLayout.this.findDrawerWithGravity(i2);
                if (findDrawerWithGravity != null) {
                    i = -findDrawerWithGravity.getWidth();
                }
                i += edgeSize;
            } else {
                findDrawerWithGravity = DrawerLayout.this.findDrawerWithGravity(5);
                i = DrawerLayout.this.getWidth() - edgeSize;
            }
            if (findDrawerWithGravity == null) {
                return;
            }
            if (((z2 && findDrawerWithGravity.getLeft() < i) || (!z2 && findDrawerWithGravity.getLeft() > i)) && DrawerLayout.this.getDrawerLockMode(findDrawerWithGravity) == 0) {
                LayoutParams layoutParams = (LayoutParams) findDrawerWithGravity.getLayoutParams();
                this.mDragger.smoothSlideViewTo(findDrawerWithGravity, i, findDrawerWithGravity.getTop());
                layoutParams.isPeeking = z;
                DrawerLayout.this.invalidate();
                closeOtherDrawer();
                DrawerLayout.this.cancelChildViewTouch();
            }
        }

        public void onEdgeDragStarted(int i, int i2) {
            View findDrawerWithGravity;
            int i3 = 1;
            if ((i & i3) == i3) {
                findDrawerWithGravity = DrawerLayout.this.findDrawerWithGravity(3);
            } else {
                findDrawerWithGravity = DrawerLayout.this.findDrawerWithGravity(5);
            }
            if (findDrawerWithGravity != null && DrawerLayout.this.getDrawerLockMode(findDrawerWithGravity) == 0) {
                this.mDragger.captureChildView(findDrawerWithGravity, i2);
            }
        }

        public int getViewHorizontalDragRange(View view) {
            return DrawerLayout.this.isDrawerView(view) ? view.getWidth() : 0;
        }

        public int clampViewPositionHorizontal(View view, int i, int i2) {
            if (DrawerLayout.this.checkDrawerViewAbsoluteGravity(view, 3)) {
                return Math.max(-view.getWidth(), Math.min(i, 0));
            }
            i2 = DrawerLayout.this.getWidth();
            return Math.max(i2 - view.getWidth(), Math.min(i, i2));
        }

        public int clampViewPositionVertical(View view, int i, int i2) {
            return view.getTop();
        }
    }

    static {
        boolean z = true;
        int[] iArr = new int[z];
        boolean z2 = false;
        iArr[z2] = 16843828;
        THEME_ATTRS = iArr;
        iArr = new int[z];
        iArr[z2] = 16842931;
        LAYOUT_ATTRS = iArr;
        CAN_HIDE_DESCENDANTS = VERSION.SDK_INT >= 19 ? z : z2;
        if (VERSION.SDK_INT < 21) {
            z = z2;
        }
        SET_DRAWER_SHADOW_FROM_ELEVATION = z;
    }

    public DrawerLayout(Context context) {
        this(context, null);
    }

    public DrawerLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DrawerLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChildAccessibilityDelegate = new ChildAccessibilityDelegate();
        this.mScrimColor = -1728053248;
        this.mScrimPaint = new Paint();
        boolean z = true;
        this.mFirstLayout = z;
        i = 3;
        this.mLockModeLeft = i;
        this.mLockModeRight = i;
        this.mLockModeStart = i;
        this.mLockModeEnd = i;
        Drawable drawable = null;
        this.mShadowStart = drawable;
        this.mShadowEnd = drawable;
        this.mShadowLeft = drawable;
        this.mShadowRight = drawable;
        setDescendantFocusability(262144);
        float f = getResources().getDisplayMetrics().density;
        this.mMinDrawerMargin = (int) ((64.0f * f) + 0.5f);
        float f2 = 400.0f * f;
        this.mLeftCallback = new ViewDragCallback(i);
        this.mRightCallback = new ViewDragCallback(5);
        float f3 = 1.0f;
        this.mLeftDragger = ViewDragHelper.create(this, f3, this.mLeftCallback);
        this.mLeftDragger.setEdgeTrackingEnabled(z);
        this.mLeftDragger.setMinVelocity(f2);
        this.mLeftCallback.setDragger(this.mLeftDragger);
        this.mRightDragger = ViewDragHelper.create(this, f3, this.mRightCallback);
        this.mRightDragger.setEdgeTrackingEnabled(2);
        this.mRightDragger.setMinVelocity(f2);
        this.mRightCallback.setDragger(this.mRightDragger);
        setFocusableInTouchMode(z);
        ViewCompat.setImportantForAccessibility(this, z);
        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegate());
        z = false;
        ViewGroupCompat.setMotionEventSplittingEnabled(this, z);
        if (ViewCompat.getFitsSystemWindows(this)) {
            if (VERSION.SDK_INT >= 21) {
                setOnApplyWindowInsetsListener(new OnApplyWindowInsetsListener() {
                    @TargetApi(21)
                    public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                        ((DrawerLayout) view).setChildInsets(windowInsets, windowInsets.getSystemWindowInsetTop() > 0);
                        return windowInsets.consumeSystemWindowInsets();
                    }
                });
                setSystemUiVisibility(1280);
                TypedArray obtainStyledAttributes = context.obtainStyledAttributes(THEME_ATTRS);
                try {
                    this.mStatusBarBackground = obtainStyledAttributes.getDrawable(z);
                } finally {
                    obtainStyledAttributes.recycle();
                }
            } else {
                this.mStatusBarBackground = drawable;
            }
        }
        this.mDrawerElevation = 10.0f * f;
        this.mNonDrawerViews = new ArrayList();
    }

    public void setDrawerElevation(float f) {
        this.mDrawerElevation = f;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (isDrawerView(childAt)) {
                ViewCompat.setElevation(childAt, this.mDrawerElevation);
            }
        }
    }

    public float getDrawerElevation() {
        return SET_DRAWER_SHADOW_FROM_ELEVATION ? this.mDrawerElevation : 0.0f;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setChildInsets(Object obj, boolean z) {
        this.mLastInsets = obj;
        this.mDrawStatusBarBackground = z;
        boolean z2 = !z && getBackground() == null;
        setWillNotDraw(z2);
        requestLayout();
    }

    public void setDrawerShadow(Drawable drawable, int i) {
        if (!SET_DRAWER_SHADOW_FROM_ELEVATION) {
            int i2 = 8388611;
            if ((i & i2) == i2) {
                this.mShadowStart = drawable;
            } else {
                i2 = 8388613;
                if ((i & i2) == i2) {
                    this.mShadowEnd = drawable;
                } else if ((i & 3) == 3) {
                    this.mShadowLeft = drawable;
                } else {
                    i2 = 5;
                    if ((i & i2) == i2) {
                        this.mShadowRight = drawable;
                    } else {
                        return;
                    }
                }
            }
            resolveShadowDrawables();
            invalidate();
        }
    }

    public void setDrawerShadow(@DrawableRes int i, int i2) {
        setDrawerShadow(ContextCompat.getDrawable(getContext(), i), i2);
    }

    public void setScrimColor(@ColorInt int i) {
        this.mScrimColor = i;
        invalidate();
    }

    @Deprecated
    public void setDrawerListener(DrawerListener drawerListener) {
        if (this.mListener != null) {
            removeDrawerListener(this.mListener);
        }
        if (drawerListener != null) {
            addDrawerListener(drawerListener);
        }
        this.mListener = drawerListener;
    }

    public void addDrawerListener(@NonNull DrawerListener drawerListener) {
        if (drawerListener != null) {
            if (this.mListeners == null) {
                this.mListeners = new ArrayList();
            }
            this.mListeners.add(drawerListener);
        }
    }

    public void removeDrawerListener(@NonNull DrawerListener drawerListener) {
        if (drawerListener != null && this.mListeners != null) {
            this.mListeners.remove(drawerListener);
        }
    }

    public void setDrawerLockMode(int i) {
        setDrawerLockMode(i, 3);
        setDrawerLockMode(i, 5);
    }

    public void setDrawerLockMode(int i, int i2) {
        int absoluteGravity = GravityCompat.getAbsoluteGravity(i2, ViewCompat.getLayoutDirection(this));
        int i3 = 3;
        if (i2 == i3) {
            this.mLockModeLeft = i;
        } else if (i2 == 5) {
            this.mLockModeRight = i;
        } else if (i2 == 8388611) {
            this.mLockModeStart = i;
        } else if (i2 == 8388613) {
            this.mLockModeEnd = i;
        }
        if (i != 0) {
            (absoluteGravity == i3 ? this.mLeftDragger : this.mRightDragger).cancel();
        }
        View findDrawerWithGravity;
        switch (i) {
            case 1:
                findDrawerWithGravity = findDrawerWithGravity(absoluteGravity);
                if (findDrawerWithGravity != null) {
                    closeDrawer(findDrawerWithGravity);
                    return;
                }
                return;
            case 2:
                findDrawerWithGravity = findDrawerWithGravity(absoluteGravity);
                if (findDrawerWithGravity != null) {
                    openDrawer(findDrawerWithGravity);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void setDrawerLockMode(int i, View view) {
        if (isDrawerView(view)) {
            setDrawerLockMode(i, ((LayoutParams) view.getLayoutParams()).gravity);
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not a ");
        stringBuilder.append("drawer with appropriate layout_gravity");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public int getDrawerLockMode(int i) {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        int i2 = 3;
        if (i != i2) {
            if (i != 5) {
                if (i != 8388611) {
                    if (i == 8388613) {
                        if (this.mLockModeEnd != i2) {
                            return this.mLockModeEnd;
                        }
                        if (layoutDirection == 0) {
                            i = this.mLockModeRight;
                        } else {
                            i = this.mLockModeLeft;
                        }
                        if (i != i2) {
                            return i;
                        }
                    }
                } else if (this.mLockModeStart != i2) {
                    return this.mLockModeStart;
                } else {
                    if (layoutDirection == 0) {
                        i = this.mLockModeLeft;
                    } else {
                        i = this.mLockModeRight;
                    }
                    if (i != i2) {
                        return i;
                    }
                }
            } else if (this.mLockModeRight != i2) {
                return this.mLockModeRight;
            } else {
                if (layoutDirection == 0) {
                    i = this.mLockModeEnd;
                } else {
                    i = this.mLockModeStart;
                }
                if (i != i2) {
                    return i;
                }
            }
        } else if (this.mLockModeLeft != i2) {
            return this.mLockModeLeft;
        } else {
            i = layoutDirection == 0 ? this.mLockModeStart : this.mLockModeEnd;
            if (i != i2) {
                return i;
            }
        }
        return 0;
    }

    public int getDrawerLockMode(View view) {
        if (isDrawerView(view)) {
            return getDrawerLockMode(((LayoutParams) view.getLayoutParams()).gravity);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not a drawer");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void setDrawerTitle(int i, CharSequence charSequence) {
        i = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this));
        if (i == 3) {
            this.mTitleLeft = charSequence;
        } else if (i == 5) {
            this.mTitleRight = charSequence;
        }
    }

    @Nullable
    public CharSequence getDrawerTitle(int i) {
        i = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this));
        if (i == 3) {
            return this.mTitleLeft;
        }
        return i == 5 ? this.mTitleRight : null;
    }

    void updateDrawerState(int i, int i2, View view) {
        i = this.mLeftDragger.getViewDragState();
        int viewDragState = this.mRightDragger.getViewDragState();
        int i3 = 2;
        int i4 = 1;
        if (i == i4 || viewDragState == i4) {
            i3 = i4;
        } else if (!(i == i3 || viewDragState == i3)) {
            i3 = 0;
        }
        if (view != null && i2 == 0) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.onScreen == 0.0f) {
                dispatchOnDrawerClosed(view);
            } else if (layoutParams.onScreen == 1.0f) {
                dispatchOnDrawerOpened(view);
            }
        }
        if (i3 != this.mDrawerState) {
            this.mDrawerState = i3;
            if (this.mListeners != null) {
                for (i = this.mListeners.size() - i4; i >= 0; i--) {
                    ((DrawerListener) this.mListeners.get(i)).onDrawerStateChanged(i3);
                }
            }
        }
    }

    void dispatchOnDrawerClosed(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i = 1;
        if ((layoutParams.openState & i) == i) {
            boolean z = false;
            layoutParams.openState = z;
            if (this.mListeners != null) {
                for (int size = this.mListeners.size() - i; size >= 0; size--) {
                    ((DrawerListener) this.mListeners.get(size)).onDrawerClosed(view);
                }
            }
            updateChildrenImportantForAccessibility(view, z);
            if (hasWindowFocus()) {
                view = getRootView();
                if (view != null) {
                    view.sendAccessibilityEvent(32);
                }
            }
        }
    }

    void dispatchOnDrawerOpened(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        boolean z = true;
        if ((layoutParams.openState & z) == 0) {
            layoutParams.openState = z;
            if (this.mListeners != null) {
                for (int size = this.mListeners.size() - z; size >= 0; size--) {
                    ((DrawerListener) this.mListeners.get(size)).onDrawerOpened(view);
                }
            }
            updateChildrenImportantForAccessibility(view, z);
            if (hasWindowFocus()) {
                sendAccessibilityEvent(32);
            }
        }
    }

    private void updateChildrenImportantForAccessibility(View view, boolean z) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if ((z || isDrawerView(childAt)) && !(z && childAt == view)) {
                ViewCompat.setImportantForAccessibility(childAt, 4);
            } else {
                ViewCompat.setImportantForAccessibility(childAt, 1);
            }
        }
    }

    void dispatchOnDrawerSlide(View view, float f) {
        if (this.mListeners != null) {
            for (int size = this.mListeners.size() - 1; size >= 0; size--) {
                ((DrawerListener) this.mListeners.get(size)).onDrawerSlide(view, f);
            }
        }
    }

    void setDrawerViewOffset(View view, float f) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (f != layoutParams.onScreen) {
            layoutParams.onScreen = f;
            dispatchOnDrawerSlide(view, f);
        }
    }

    float getDrawerViewOffset(View view) {
        return ((LayoutParams) view.getLayoutParams()).onScreen;
    }

    int getDrawerViewAbsoluteGravity(View view) {
        return GravityCompat.getAbsoluteGravity(((LayoutParams) view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(this));
    }

    boolean checkDrawerViewAbsoluteGravity(View view, int i) {
        return (getDrawerViewAbsoluteGravity(view) & i) == i;
    }

    View findOpenDrawer() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int i2 = 1;
            if ((((LayoutParams) childAt.getLayoutParams()).openState & i2) == i2) {
                return childAt;
            }
        }
        return null;
    }

    void moveDrawerToOffset(View view, float f) {
        float width = (float) view.getWidth();
        int i = (int) (width * f);
        i -= (int) (getDrawerViewOffset(view) * width);
        if (!checkDrawerViewAbsoluteGravity(view, 3)) {
            i = -i;
        }
        view.offsetLeftAndRight(i);
        setDrawerViewOffset(view, f);
    }

    View findDrawerWithGravity(int i) {
        i = GravityCompat.getAbsoluteGravity(i, ViewCompat.getLayoutDirection(this)) & 7;
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if ((getDrawerViewAbsoluteGravity(childAt) & 7) == i) {
                return childAt;
            }
        }
        return null;
    }

    static String gravityToString(int i) {
        if ((i & 3) == 3) {
            return "LEFT";
        }
        return (i & 5) == 5 ? "RIGHT" : Integer.toHexString(i);
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        DrawerLayout drawerLayout = this;
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int i4 = 300;
        int i5 = 1073741824;
        if (!(mode == i5 && mode2 == i5)) {
            if (isInEditMode()) {
                i3 = Integer.MIN_VALUE;
                if (mode != i3 && mode == 0) {
                    size = i4;
                }
                if (mode2 != i3 && mode2 == 0) {
                    size2 = i4;
                }
            } else {
                throw new IllegalArgumentException("DrawerLayout must be measured with MeasureSpec.EXACTLY.");
            }
        }
        setMeasuredDimension(size, size2);
        i4 = 0;
        mode = (drawerLayout.mLastInsets == null || !ViewCompat.getFitsSystemWindows(this)) ? i4 : 1;
        i3 = ViewCompat.getLayoutDirection(this);
        int childCount = getChildCount();
        int i6 = i4;
        int i7 = i6;
        int i8 = i7;
        while (i6 < childCount) {
            int absoluteGravity;
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int i9 = 3;
                if (mode != 0) {
                    absoluteGravity = GravityCompat.getAbsoluteGravity(layoutParams.gravity, i3);
                    i5 = 21;
                    WindowInsets windowInsets;
                    if (ViewCompat.getFitsSystemWindows(childAt)) {
                        if (VERSION.SDK_INT >= i5) {
                            windowInsets = (WindowInsets) drawerLayout.mLastInsets;
                            if (absoluteGravity == i9) {
                                windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), i4, windowInsets.getSystemWindowInsetBottom());
                            } else if (absoluteGravity == 5) {
                                windowInsets = windowInsets.replaceSystemWindowInsets(i4, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
                            }
                            childAt.dispatchApplyWindowInsets(windowInsets);
                        }
                    } else if (VERSION.SDK_INT >= i5) {
                        windowInsets = (WindowInsets) drawerLayout.mLastInsets;
                        if (absoluteGravity == 3) {
                            windowInsets = windowInsets.replaceSystemWindowInsets(windowInsets.getSystemWindowInsetLeft(), windowInsets.getSystemWindowInsetTop(), i4, windowInsets.getSystemWindowInsetBottom());
                        } else if (absoluteGravity == 5) {
                            windowInsets = windowInsets.replaceSystemWindowInsets(i4, windowInsets.getSystemWindowInsetTop(), windowInsets.getSystemWindowInsetRight(), windowInsets.getSystemWindowInsetBottom());
                        }
                        layoutParams.leftMargin = windowInsets.getSystemWindowInsetLeft();
                        layoutParams.topMargin = windowInsets.getSystemWindowInsetTop();
                        layoutParams.rightMargin = windowInsets.getSystemWindowInsetRight();
                        layoutParams.bottomMargin = windowInsets.getSystemWindowInsetBottom();
                    }
                }
                if (isContentView(childAt)) {
                    i5 = 1073741824;
                    childAt.measure(MeasureSpec.makeMeasureSpec((size - layoutParams.leftMargin) - layoutParams.rightMargin, i5), MeasureSpec.makeMeasureSpec((size2 - layoutParams.topMargin) - layoutParams.bottomMargin, i5));
                } else {
                    Object obj = 1073741824;
                    if (isDrawerView(childAt)) {
                        if (SET_DRAWER_SHADOW_FROM_ELEVATION && ViewCompat.getElevation(childAt) != drawerLayout.mDrawerElevation) {
                            ViewCompat.setElevation(childAt, drawerLayout.mDrawerElevation);
                        }
                        mode2 = getDrawerViewAbsoluteGravity(childAt) & 7;
                        i9 = mode2 == 3 ? 1 : i4;
                        if ((i9 == 0 || i7 == 0) && (i9 != 0 || i8 == 0)) {
                            if (i9 != 0) {
                                i7 = 1;
                            } else {
                                i8 = 1;
                            }
                            childAt.measure(getChildMeasureSpec(i, (drawerLayout.mMinDrawerMargin + layoutParams.leftMargin) + layoutParams.rightMargin, layoutParams.width), getChildMeasureSpec(i2, layoutParams.topMargin + layoutParams.bottomMargin, layoutParams.height));
                            i6++;
                            i4 = 0;
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Child drawer has absolute gravity ");
                            stringBuilder.append(gravityToString(mode2));
                            stringBuilder.append(" but this ");
                            stringBuilder.append("DrawerLayout");
                            stringBuilder.append(" already has a ");
                            stringBuilder.append("drawer view along that edge");
                            throw new IllegalStateException(stringBuilder.toString());
                        }
                    }
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Child ");
                    stringBuilder2.append(childAt);
                    stringBuilder2.append(" at index ");
                    stringBuilder2.append(i6);
                    stringBuilder2.append(" does not have a valid layout_gravity - must be Gravity.LEFT, ");
                    stringBuilder2.append("Gravity.RIGHT or Gravity.NO_GRAVITY");
                    throw new IllegalStateException(stringBuilder2.toString());
                }
            }
            absoluteGravity = i;
            int i10 = i2;
            i6++;
            i4 = 0;
        }
    }

    private void resolveShadowDrawables() {
        if (!SET_DRAWER_SHADOW_FROM_ELEVATION) {
            this.mShadowLeftResolved = resolveLeftShadow();
            this.mShadowRightResolved = resolveRightShadow();
        }
    }

    private Drawable resolveLeftShadow() {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == 0) {
            if (this.mShadowStart != null) {
                mirror(this.mShadowStart, layoutDirection);
                return this.mShadowStart;
            }
        } else if (this.mShadowEnd != null) {
            mirror(this.mShadowEnd, layoutDirection);
            return this.mShadowEnd;
        }
        return this.mShadowLeft;
    }

    private Drawable resolveRightShadow() {
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        if (layoutDirection == 0) {
            if (this.mShadowEnd != null) {
                mirror(this.mShadowEnd, layoutDirection);
                return this.mShadowEnd;
            }
        } else if (this.mShadowStart != null) {
            mirror(this.mShadowStart, layoutDirection);
            return this.mShadowStart;
        }
        return this.mShadowRight;
    }

    private boolean mirror(Drawable drawable, int i) {
        if (drawable == null || !DrawableCompat.isAutoMirrored(drawable)) {
            return false;
        }
        DrawableCompat.setLayoutDirection(drawable, i);
        return true;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        boolean z2 = true;
        this.mInLayout = z2;
        int i5 = i3 - i;
        int childCount = getChildCount();
        int i6 = 0;
        while (i6 < childCount) {
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (isContentView(childAt)) {
                    childAt.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + childAt.getMeasuredWidth(), layoutParams.topMargin + childAt.getMeasuredHeight());
                } else {
                    int i7;
                    float f;
                    int measuredWidth = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (checkDrawerViewAbsoluteGravity(childAt, 3)) {
                        float f2 = (float) measuredWidth;
                        i7 = (-measuredWidth) + ((int) (layoutParams.onScreen * f2));
                        f = ((float) (measuredWidth + i7)) / f2;
                    } else {
                        float f3 = (float) measuredWidth;
                        int i8 = i5 - ((int) (layoutParams.onScreen * f3));
                        f = ((float) (i5 - i8)) / f3;
                        i7 = i8;
                    }
                    boolean z3 = f != layoutParams.onScreen ? z2 : false;
                    int i9 = layoutParams.gravity & 112;
                    int i10;
                    if (i9 == 16) {
                        i10 = i4 - i2;
                        i9 = (i10 - measuredHeight) / 2;
                        if (i9 < layoutParams.topMargin) {
                            i9 = layoutParams.topMargin;
                        } else if (i9 + measuredHeight > i10 - layoutParams.bottomMargin) {
                            i9 = (i10 - layoutParams.bottomMargin) - measuredHeight;
                        }
                        childAt.layout(i7, i9, measuredWidth + i7, measuredHeight + i9);
                    } else if (i9 != 80) {
                        childAt.layout(i7, layoutParams.topMargin, measuredWidth + i7, layoutParams.topMargin + measuredHeight);
                    } else {
                        i10 = i4 - i2;
                        childAt.layout(i7, (i10 - layoutParams.bottomMargin) - childAt.getMeasuredHeight(), measuredWidth + i7, i10 - layoutParams.bottomMargin);
                    }
                    if (z3) {
                        setDrawerViewOffset(childAt, f);
                    }
                    int i11 = layoutParams.onScreen > 0.0f ? 0 : 4;
                    if (childAt.getVisibility() != i11) {
                        childAt.setVisibility(i11);
                    }
                }
            }
            i6++;
            z2 = true;
        }
        boolean z4 = false;
        r0.mInLayout = z4;
        r0.mFirstLayout = z4;
    }

    public void requestLayout() {
        if (!this.mInLayout) {
            super.requestLayout();
        }
    }

    public void computeScroll() {
        int childCount = getChildCount();
        float f = 0.0f;
        for (int i = 0; i < childCount; i++) {
            f = Math.max(f, ((LayoutParams) getChildAt(i).getLayoutParams()).onScreen);
        }
        this.mScrimOpacity = f;
        boolean z = true;
        boolean continueSettling = this.mLeftDragger.continueSettling(z);
        z = this.mRightDragger.continueSettling(z);
        if (continueSettling || z) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private static boolean hasOpaqueBackground(View view) {
        Drawable background = view.getBackground();
        boolean z = false;
        if (background == null) {
            return z;
        }
        if (background.getOpacity() == -1) {
            z = true;
        }
        return z;
    }

    public void setStatusBarBackground(Drawable drawable) {
        this.mStatusBarBackground = drawable;
        invalidate();
    }

    public Drawable getStatusBarBackgroundDrawable() {
        return this.mStatusBarBackground;
    }

    public void setStatusBarBackground(int i) {
        this.mStatusBarBackground = i != 0 ? ContextCompat.getDrawable(getContext(), i) : null;
        invalidate();
    }

    public void setStatusBarBackgroundColor(@ColorInt int i) {
        this.mStatusBarBackground = new ColorDrawable(i);
        invalidate();
    }

    public void onRtlPropertiesChanged(int i) {
        resolveShadowDrawables();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawStatusBarBackground && this.mStatusBarBackground != null) {
            int i = 0;
            int systemWindowInsetTop = (VERSION.SDK_INT < 21 || this.mLastInsets == null) ? i : ((WindowInsets) this.mLastInsets).getSystemWindowInsetTop();
            if (systemWindowInsetTop > 0) {
                this.mStatusBarBackground.setBounds(i, i, getWidth(), systemWindowInsetTop);
                this.mStatusBarBackground.draw(canvas);
            }
        }
    }

    protected boolean drawChild(Canvas canvas, View view, long j) {
        int i;
        int i2;
        Canvas canvas2 = canvas;
        View view2 = view;
        int height = getHeight();
        boolean isContentView = isContentView(view2);
        int width = getWidth();
        int save = canvas2.save();
        int i3 = 3;
        int i4 = 0;
        if (isContentView) {
            int childCount = getChildCount();
            i = width;
            width = i4;
            i2 = width;
            while (width < childCount) {
                View childAt = getChildAt(width);
                if (childAt != view2 && childAt.getVisibility() == 0 && hasOpaqueBackground(childAt) && isDrawerView(childAt) && childAt.getHeight() >= height) {
                    int right;
                    if (checkDrawerViewAbsoluteGravity(childAt, i3)) {
                        right = childAt.getRight();
                        if (right > i2) {
                            i2 = right;
                        }
                    } else {
                        right = childAt.getLeft();
                        if (right < i) {
                            i = right;
                        }
                    }
                }
                width++;
            }
            canvas2.clipRect(i2, i4, i, getHeight());
        } else {
            i = width;
            i2 = i4;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas2.restoreToCount(save);
        float f = 0.0f;
        if (r0.mScrimOpacity <= f || !isContentView) {
            float f2 = 255.0f;
            float f3 = 1.0f;
            if (r0.mShadowLeftResolved != null && checkDrawerViewAbsoluteGravity(view2, i3)) {
                height = r0.mShadowLeftResolved.getIntrinsicWidth();
                i3 = view.getRight();
                f = Math.max(f, Math.min(((float) i3) / ((float) r0.mLeftDragger.getEdgeSize()), f3));
                r0.mShadowLeftResolved.setBounds(i3, view.getTop(), height + i3, view.getBottom());
                r0.mShadowLeftResolved.setAlpha((int) (f2 * f));
                r0.mShadowLeftResolved.draw(canvas2);
            } else if (r0.mShadowRightResolved != null && checkDrawerViewAbsoluteGravity(view2, 5)) {
                height = r0.mShadowRightResolved.getIntrinsicWidth();
                i3 = view.getLeft();
                f = Math.max(f, Math.min(((float) (getWidth() - i3)) / ((float) r0.mRightDragger.getEdgeSize()), f3));
                r0.mShadowRightResolved.setBounds(i3 - height, view.getTop(), i3, view.getBottom());
                r0.mShadowRightResolved.setAlpha((int) (f2 * f));
                r0.mShadowRightResolved.draw(canvas2);
            }
        } else {
            r0.mScrimPaint.setColor((((int) (((float) ((r0.mScrimColor & -16777216) >>> 24)) * r0.mScrimOpacity)) << 24) | (r0.mScrimColor & 16777215));
            canvas2.drawRect((float) i2, 0.0f, (float) i, (float) getHeight(), r0.mScrimPaint);
        }
        return drawChild;
    }

    boolean isContentView(View view) {
        return ((LayoutParams) view.getLayoutParams()).gravity == 0;
    }

    boolean isDrawerView(View view) {
        int absoluteGravity = GravityCompat.getAbsoluteGravity(((LayoutParams) view.getLayoutParams()).gravity, ViewCompat.getLayoutDirection(view));
        boolean z = true;
        return ((absoluteGravity & 3) == 0 && (absoluteGravity & 5) == 0) ? false : z;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean z;
        int shouldInterceptTouchEvent = this.mLeftDragger.shouldInterceptTouchEvent(motionEvent) | this.mRightDragger.shouldInterceptTouchEvent(motionEvent);
        boolean z2 = true;
        boolean z3 = false;
        switch (motionEvent.getActionMasked()) {
            case 0:
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (this.mScrimOpacity > 0.0f) {
                    View findTopChildUnder = this.mLeftDragger.findTopChildUnder((int) x, (int) y);
                    if (findTopChildUnder != null && isContentView(findTopChildUnder)) {
                        z = z2;
                        this.mDisallowInterceptRequested = z3;
                        this.mChildrenCanceledTouch = z3;
                        break;
                    }
                }
                z = z3;
                this.mDisallowInterceptRequested = z3;
                this.mChildrenCanceledTouch = z3;
            case 1:
            case 3:
                closeDrawers(z2);
                this.mDisallowInterceptRequested = z3;
                this.mChildrenCanceledTouch = z3;
                break;
            case 2:
                if (this.mLeftDragger.checkTouchSlop(3)) {
                    this.mLeftCallback.removeCallbacks();
                    this.mRightCallback.removeCallbacks();
                    break;
                }
                break;
        }
        z = z3;
        return (shouldInterceptTouchEvent != 0 || z || hasPeekingDrawer() || this.mChildrenCanceledTouch) ? z2 : z3;
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mLeftDragger.processTouchEvent(motionEvent);
        this.mRightDragger.processTouchEvent(motionEvent);
        int action = motionEvent.getAction() & 255;
        boolean z = true;
        boolean z2 = false;
        if (action != 3) {
            float x;
            float y;
            switch (action) {
                case 0:
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    this.mInitialMotionX = x;
                    this.mInitialMotionY = y;
                    this.mDisallowInterceptRequested = z2;
                    this.mChildrenCanceledTouch = z2;
                    break;
                case 1:
                    boolean z3;
                    x = motionEvent.getX();
                    y = motionEvent.getY();
                    View findTopChildUnder = this.mLeftDragger.findTopChildUnder((int) x, (int) y);
                    if (findTopChildUnder != null && isContentView(findTopChildUnder)) {
                        x -= this.mInitialMotionX;
                        y -= this.mInitialMotionY;
                        int touchSlop = this.mLeftDragger.getTouchSlop();
                        if ((x * x) + (y * y) < ((float) (touchSlop * touchSlop))) {
                            View findOpenDrawer = findOpenDrawer();
                            if (!(findOpenDrawer == null || getDrawerLockMode(findOpenDrawer) == 2)) {
                                z3 = z2;
                                closeDrawers(z3);
                                this.mDisallowInterceptRequested = z2;
                                break;
                            }
                        }
                    }
                    z3 = z;
                    closeDrawers(z3);
                    this.mDisallowInterceptRequested = z2;
            }
        } else {
            closeDrawers(z);
            this.mDisallowInterceptRequested = z2;
            this.mChildrenCanceledTouch = z2;
        }
        return z;
    }

    public void requestDisallowInterceptTouchEvent(boolean z) {
        super.requestDisallowInterceptTouchEvent(z);
        this.mDisallowInterceptRequested = z;
        if (z) {
            closeDrawers(true);
        }
    }

    public void closeDrawers() {
        closeDrawers(false);
    }

    void closeDrawers(boolean z) {
        int childCount = getChildCount();
        boolean z2 = false;
        int i = z2;
        int i2 = i;
        while (i < childCount) {
            View childAt = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (isDrawerView(childAt) && (!z || layoutParams.isPeeking)) {
                int width = childAt.getWidth();
                if (checkDrawerViewAbsoluteGravity(childAt, 3)) {
                    i2 |= this.mLeftDragger.smoothSlideViewTo(childAt, -width, childAt.getTop());
                } else {
                    i2 |= this.mRightDragger.smoothSlideViewTo(childAt, getWidth(), childAt.getTop());
                }
                layoutParams.isPeeking = z2;
            }
            i++;
        }
        this.mLeftCallback.removeCallbacks();
        this.mRightCallback.removeCallbacks();
        if (i2 != 0) {
            invalidate();
        }
    }

    public void openDrawer(View view) {
        openDrawer(view, true);
    }

    public void openDrawer(View view, boolean z) {
        if (isDrawerView(view)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            float f = 1.0f;
            if (this.mFirstLayout) {
                layoutParams.onScreen = f;
                z = true;
                layoutParams.openState = z;
                updateChildrenImportantForAccessibility(view, z);
            } else {
                int i = 0;
                if (z) {
                    layoutParams.openState |= 2;
                    if (checkDrawerViewAbsoluteGravity(view, 3)) {
                        this.mLeftDragger.smoothSlideViewTo(view, i, view.getTop());
                    } else {
                        this.mRightDragger.smoothSlideViewTo(view, getWidth() - view.getWidth(), view.getTop());
                    }
                } else {
                    moveDrawerToOffset(view, f);
                    updateDrawerState(layoutParams.gravity, i, view);
                    view.setVisibility(i);
                }
            }
            invalidate();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not a sliding drawer");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void openDrawer(int i) {
        openDrawer(i, true);
    }

    public void openDrawer(int i, boolean z) {
        View findDrawerWithGravity = findDrawerWithGravity(i);
        if (findDrawerWithGravity == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No drawer view found with gravity ");
            stringBuilder.append(gravityToString(i));
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        openDrawer(findDrawerWithGravity, z);
    }

    public void closeDrawer(View view) {
        closeDrawer(view, true);
    }

    public void closeDrawer(View view, boolean z) {
        if (isDrawerView(view)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            int i = 0;
            float f = 0.0f;
            if (this.mFirstLayout) {
                layoutParams.onScreen = f;
                layoutParams.openState = i;
            } else {
                int i2 = 4;
                if (z) {
                    layoutParams.openState |= i2;
                    if (checkDrawerViewAbsoluteGravity(view, 3)) {
                        this.mLeftDragger.smoothSlideViewTo(view, -view.getWidth(), view.getTop());
                    } else {
                        this.mRightDragger.smoothSlideViewTo(view, getWidth(), view.getTop());
                    }
                } else {
                    moveDrawerToOffset(view, f);
                    updateDrawerState(layoutParams.gravity, i, view);
                    view.setVisibility(i2);
                }
            }
            invalidate();
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("View ");
        stringBuilder.append(view);
        stringBuilder.append(" is not a sliding drawer");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public void closeDrawer(int i) {
        closeDrawer(i, true);
    }

    public void closeDrawer(int i, boolean z) {
        View findDrawerWithGravity = findDrawerWithGravity(i);
        if (findDrawerWithGravity == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No drawer view found with gravity ");
            stringBuilder.append(gravityToString(i));
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        closeDrawer(findDrawerWithGravity, z);
    }

    public boolean isDrawerOpen(View view) {
        if (isDrawerView(view)) {
            boolean z = true;
            return (((LayoutParams) view.getLayoutParams()).openState & z) == z ? z : false;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("View ");
            stringBuilder.append(view);
            stringBuilder.append(" is not a drawer");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public boolean isDrawerOpen(int i) {
        View findDrawerWithGravity = findDrawerWithGravity(i);
        return findDrawerWithGravity != null ? isDrawerOpen(findDrawerWithGravity) : false;
    }

    public boolean isDrawerVisible(View view) {
        if (isDrawerView(view)) {
            return ((LayoutParams) view.getLayoutParams()).onScreen > 0.0f;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("View ");
            stringBuilder.append(view);
            stringBuilder.append(" is not a drawer");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public boolean isDrawerVisible(int i) {
        View findDrawerWithGravity = findDrawerWithGravity(i);
        return findDrawerWithGravity != null ? isDrawerVisible(findDrawerWithGravity) : false;
    }

    private boolean hasPeekingDrawer() {
        int childCount = getChildCount();
        boolean z = false;
        for (int i = z; i < childCount; i++) {
            if (((LayoutParams) getChildAt(i).getLayoutParams()).isPeeking) {
                return true;
            }
        }
        return z;
    }

    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        int i = -1;
        return new LayoutParams(i, i);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        return layoutParams instanceof MarginLayoutParams ? new LayoutParams((MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return (layoutParams instanceof LayoutParams) && super.checkLayoutParams(layoutParams);
    }

    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (getDescendantFocusability() != 393216) {
            int childCount = getChildCount();
            int i3 = 0;
            int i4 = i3;
            int i5 = i4;
            while (i4 < childCount) {
                View childAt = getChildAt(i4);
                if (!isDrawerView(childAt)) {
                    this.mNonDrawerViews.add(childAt);
                } else if (isDrawerOpen(childAt)) {
                    childAt.addFocusables(arrayList, i, i2);
                    i5 = 1;
                }
                i4++;
            }
            if (i5 == 0) {
                childCount = this.mNonDrawerViews.size();
                while (i3 < childCount) {
                    View view = (View) this.mNonDrawerViews.get(i3);
                    if (view.getVisibility() == 0) {
                        view.addFocusables(arrayList, i, i2);
                    }
                    i3++;
                }
            }
            this.mNonDrawerViews.clear();
        }
    }

    private boolean hasVisibleDrawer() {
        return findVisibleDrawer() != null;
    }

    View findVisibleDrawer() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (isDrawerView(childAt) && isDrawerVisible(childAt)) {
                return childAt;
            }
        }
        return null;
    }

    void cancelChildViewTouch() {
        if (!this.mChildrenCanceledTouch) {
            long uptimeMillis = SystemClock.uptimeMillis();
            MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).dispatchTouchEvent(obtain);
            }
            obtain.recycle();
            this.mChildrenCanceledTouch = true;
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || !hasVisibleDrawer()) {
            return super.onKeyDown(i, keyEvent);
        }
        keyEvent.startTracking();
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyUp(i, keyEvent);
        }
        View findVisibleDrawer = findVisibleDrawer();
        if (findVisibleDrawer != null && getDrawerLockMode(findVisibleDrawer) == 0) {
            closeDrawers();
        }
        return findVisibleDrawer != null;
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            if (savedState.openDrawerGravity != 0) {
                View findDrawerWithGravity = findDrawerWithGravity(savedState.openDrawerGravity);
                if (findDrawerWithGravity != null) {
                    openDrawer(findDrawerWithGravity);
                }
            }
            int i = 3;
            if (savedState.lockModeLeft != i) {
                setDrawerLockMode(savedState.lockModeLeft, i);
            }
            if (savedState.lockModeRight != i) {
                setDrawerLockMode(savedState.lockModeRight, 5);
            }
            if (savedState.lockModeStart != i) {
                setDrawerLockMode(savedState.lockModeStart, 8388611);
            }
            if (savedState.lockModeEnd != i) {
                setDrawerLockMode(savedState.lockModeEnd, 8388613);
            }
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = i; i2 < childCount; i2++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i2).getLayoutParams();
            int i3 = 1;
            int i4 = layoutParams.openState == i3 ? i3 : i;
            if (layoutParams.openState != 2) {
                i3 = i;
            }
            if (i4 != 0 || i3 != 0) {
                savedState.openDrawerGravity = layoutParams.gravity;
                break;
            }
        }
        savedState.lockModeLeft = this.mLockModeLeft;
        savedState.lockModeRight = this.mLockModeRight;
        savedState.lockModeStart = this.mLockModeStart;
        savedState.lockModeEnd = this.mLockModeEnd;
        return savedState;
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (findOpenDrawer() != null || isDrawerView(view)) {
            ViewCompat.setImportantForAccessibility(view, 4);
        } else {
            ViewCompat.setImportantForAccessibility(view, 1);
        }
        if (!CAN_HIDE_DESCENDANTS) {
            ViewCompat.setAccessibilityDelegate(view, this.mChildAccessibilityDelegate);
        }
    }

    static boolean includeChildForAccessibility(View view) {
        return (ViewCompat.getImportantForAccessibility(view) == 4 || ViewCompat.getImportantForAccessibility(view) == 2) ? false : true;
    }
}
