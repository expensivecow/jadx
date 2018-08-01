package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private boolean mBaselineAligned;
    private int mBaselineAlignedChildIndex;
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;
    private int mGravity;
    private int[] mMaxAscent;
    private int[] mMaxDescent;
    private int mOrientation;
    private int mShowDividers;
    private int mTotalLength;
    private boolean mUseLargestChild;
    private float mWeightSum;

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity;
        public float weight;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            int i = -1;
            this.gravity = i;
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayoutCompat_Layout);
            this.weight = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0f);
            this.gravity = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, i);
            obtainStyledAttributes.recycle();
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    int getLocationOffset(View view) {
        return 0;
    }

    int getNextLocationOffset(View view) {
        return 0;
    }

    int measureNullChild(int i) {
        return 0;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public LinearLayoutCompat(Context context) {
        this(context, null);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayoutCompat(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        boolean z = true;
        this.mBaselineAligned = z;
        int i2 = -1;
        this.mBaselineAlignedChildIndex = i2;
        boolean z2 = false;
        this.mBaselineChildTop = z2;
        this.mGravity = 8388659;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.LinearLayoutCompat, i, z2);
        int i3 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_orientation, i2);
        if (i3 >= 0) {
            setOrientation(i3);
        }
        i3 = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_gravity, i2);
        if (i3 >= 0) {
            setGravity(i3);
        }
        boolean z3 = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, z);
        if (!z3) {
            setBaselineAligned(z3);
        }
        this.mWeightSum = obtainStyledAttributes.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0f);
        this.mBaselineAlignedChildIndex = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, i2);
        this.mUseLargestChild = obtainStyledAttributes.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, z2);
        setDividerDrawable(obtainStyledAttributes.getDrawable(R.styleable.LinearLayoutCompat_divider));
        this.mShowDividers = obtainStyledAttributes.getInt(R.styleable.LinearLayoutCompat_showDividers, z2);
        this.mDividerPadding = obtainStyledAttributes.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, z2);
        obtainStyledAttributes.recycle();
    }

    public void setShowDividers(int i) {
        if (i != this.mShowDividers) {
            requestLayout();
        }
        this.mShowDividers = i;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public void setDividerDrawable(Drawable drawable) {
        if (drawable != this.mDivider) {
            this.mDivider = drawable;
            boolean z = false;
            if (drawable != null) {
                this.mDividerWidth = drawable.getIntrinsicWidth();
                this.mDividerHeight = drawable.getIntrinsicHeight();
            } else {
                this.mDividerWidth = z;
                this.mDividerHeight = z;
            }
            if (drawable == null) {
                z = true;
            }
            setWillNotDraw(z);
            requestLayout();
        }
    }

    public void setDividerPadding(int i) {
        this.mDividerPadding = i;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    protected void onDraw(Canvas canvas) {
        if (this.mDivider != null) {
            if (this.mOrientation == 1) {
                drawDividersVertical(canvas);
            } else {
                drawDividersHorizontal(canvas);
            }
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int virtualChildCount = getVirtualChildCount();
        int i = 0;
        while (i < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
            i++;
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 == null) {
                virtualChildCount = (getHeight() - getPaddingBottom()) - this.mDividerHeight;
            } else {
                virtualChildCount = virtualChildAt2.getBottom() + ((LayoutParams) virtualChildAt2.getLayoutParams()).bottomMargin;
            }
            drawHorizontalDivider(canvas, virtualChildCount);
        }
    }

    void drawDividersHorizontal(Canvas canvas) {
        int virtualChildCount = getVirtualChildCount();
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i = 0;
        while (i < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(i);
            if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || !hasDividerBeforeChildAt(i))) {
                int right;
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (isLayoutRtl) {
                    right = virtualChildAt.getRight() + layoutParams.rightMargin;
                } else {
                    right = (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth;
                }
                drawVerticalDivider(canvas, right);
            }
            i++;
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View virtualChildAt2 = getVirtualChildAt(virtualChildCount - 1);
            if (virtualChildAt2 != null) {
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                if (isLayoutRtl) {
                    virtualChildCount = (virtualChildAt2.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth;
                } else {
                    virtualChildCount = virtualChildAt2.getRight() + layoutParams2.rightMargin;
                }
            } else if (isLayoutRtl) {
                virtualChildCount = getPaddingLeft();
            } else {
                virtualChildCount = (getWidth() - getPaddingRight()) - this.mDividerWidth;
            }
            drawVerticalDivider(canvas, virtualChildCount);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    public int getBaseline() {
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() <= this.mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View childAt = getChildAt(this.mBaselineAlignedChildIndex);
        int baseline = childAt.getBaseline();
        int i = -1;
        if (baseline != i) {
            i = this.mBaselineChildTop;
            if (this.mOrientation == 1) {
                int i2 = this.mGravity & 112;
                if (i2 != 48) {
                    if (i2 == 16) {
                        i += ((((getBottom() - getTop()) - getPaddingTop()) - getPaddingBottom()) - this.mTotalLength) / 2;
                    } else if (i2 == 80) {
                        i = ((getBottom() - getTop()) - getPaddingBottom()) - this.mTotalLength;
                    }
                }
            }
            return (i + ((LayoutParams) childAt.getLayoutParams()).topMargin) + baseline;
        } else if (this.mBaselineAlignedChildIndex == 0) {
            return i;
        } else {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    public void setBaselineAlignedChildIndex(int i) {
        if (i < 0 || i >= getChildCount()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("base aligned child index out of range (0, ");
            stringBuilder.append(getChildCount());
            stringBuilder.append(")");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        this.mBaselineAlignedChildIndex = i;
    }

    View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    protected void onMeasure(int i, int i2) {
        if (this.mOrientation == 1) {
            measureVertical(i, i2);
        } else {
            measureHorizontal(i, i2);
        }
    }

    protected boolean hasDividerBeforeChildAt(int i) {
        boolean z = false;
        boolean z2 = true;
        if (i == 0) {
            if ((this.mShowDividers & z2) != 0) {
                z = z2;
            }
            return z;
        } else if (i == getChildCount()) {
            if ((this.mShowDividers & 4) != 0) {
                z = z2;
            }
            return z;
        } else if ((this.mShowDividers & 2) == 0) {
            return z;
        } else {
            for (i -= z2; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != 8) {
                    z = z2;
                    break;
                }
            }
            return z;
        }
    }

    void measureVertical(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7 = i;
        int i8 = i2;
        int i9 = 0;
        this.mTotalLength = i9;
        int virtualChildCount = getVirtualChildCount();
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int i10 = this.mBaselineAlignedChildIndex;
        boolean z = this.mUseLargestChild;
        float f = 0.0f;
        int i11 = 1;
        int i12 = i9;
        int i13 = i12;
        int i14 = i13;
        int i15 = i14;
        int i16 = i15;
        int i17 = i16;
        float f2 = f;
        int i18 = i11;
        int i19 = Integer.MIN_VALUE;
        while (i14 < virtualChildCount) {
            Object obj;
            View virtualChildAt = getVirtualChildAt(i14);
            if (virtualChildAt == null) {
                r7.mTotalLength += measureNullChild(i14);
                i3 = i9;
                i4 = virtualChildCount;
                i5 = mode2;
            } else {
                int i20 = i12;
                if (virtualChildAt.getVisibility() == 8) {
                    i14 += getChildrenSkipCount(virtualChildAt, i14);
                    i3 = i9;
                    i4 = virtualChildCount;
                    i5 = mode2;
                    i12 = i20;
                } else {
                    int i21;
                    int i22;
                    View view;
                    LayoutParams layoutParams;
                    int i23;
                    int i24;
                    if (hasDividerBeforeChildAt(i14)) {
                        r7.mTotalLength += r7.mDividerHeight;
                    }
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                    float f3 = f2 + layoutParams2.weight;
                    if (mode2 == 1073741824 && layoutParams2.height == 0 && layoutParams2.weight > f) {
                        i6 = r7.mTotalLength;
                        i21 = i19;
                        r7.mTotalLength = Math.max(i6, (layoutParams2.topMargin + i6) + layoutParams2.bottomMargin);
                        i22 = i13;
                        view = virtualChildAt;
                        layoutParams = layoutParams2;
                        i23 = i9;
                        i4 = virtualChildCount;
                        i5 = mode2;
                        i15 = i11;
                        i24 = i17;
                        mode2 = i20;
                        obj = Integer.MIN_VALUE;
                        virtualChildCount = i14;
                    } else {
                        i21 = i19;
                        if (layoutParams2.height != 0 || layoutParams2.weight <= f) {
                            i19 = Integer.MIN_VALUE;
                        } else {
                            layoutParams2.height = -2;
                            i19 = 0;
                        }
                        i5 = mode2;
                        mode2 = i20;
                        int i25 = i19;
                        int i26 = i21;
                        i4 = virtualChildCount;
                        virtualChildCount = i13;
                        i13 = i7;
                        view = virtualChildAt;
                        i22 = virtualChildCount;
                        i24 = i17;
                        Object obj2 = 1073741824;
                        virtualChildCount = i14;
                        i14 = i8;
                        layoutParams = layoutParams2;
                        i23 = i9;
                        i9 = Integer.MIN_VALUE;
                        measureChildBeforeLayout(virtualChildAt, i14, i13, 0, i14, f3 == f ? r7.mTotalLength : 0);
                        i6 = i25;
                        if (i6 != i9) {
                            layoutParams.height = i6;
                        }
                        i6 = view.getMeasuredHeight();
                        i12 = r7.mTotalLength;
                        r7.mTotalLength = Math.max(i12, (((i12 + i6) + layoutParams.topMargin) + layoutParams.bottomMargin) + getNextLocationOffset(view));
                        i21 = z ? Math.max(i6, i26) : i26;
                    }
                    if (i10 >= 0 && i10 == virtualChildCount + 1) {
                        r7.mBaselineChildTop = r7.mTotalLength;
                    }
                    if (virtualChildCount >= i10 || layoutParams.weight <= f) {
                        if (mode != 1073741824) {
                            i12 = -1;
                            if (layoutParams.width == i12) {
                                i6 = i11;
                                i16 = i6;
                                i19 = layoutParams.leftMargin + layoutParams.rightMargin;
                                i13 = view.getMeasuredWidth() + i19;
                                i3 = Math.max(mode2, i13);
                                i14 = View.combineMeasuredStates(i23, view.getMeasuredState());
                                i12 = (i18 == 0 && layoutParams.width == i12) ? i11 : 0;
                                if (layoutParams.weight <= f) {
                                    if (i6 == 0) {
                                        i19 = i13;
                                    }
                                    i13 = Math.max(i22, i19);
                                } else {
                                    i8 = i22;
                                    if (i6 != 0) {
                                        i13 = i19;
                                    }
                                    i17 = Math.max(i24, i13);
                                    i13 = i8;
                                    i24 = i17;
                                }
                                i18 = i12;
                                i12 = i3;
                                i3 = i14;
                                i19 = i21;
                                i17 = i24;
                                i14 = getChildrenSkipCount(view, virtualChildCount) + virtualChildCount;
                                f2 = f3;
                                i14++;
                                i9 = i3;
                                mode2 = i5;
                                virtualChildCount = i4;
                                i7 = i;
                                i8 = i2;
                            }
                        } else {
                            i12 = -1;
                        }
                        i6 = 0;
                        i19 = layoutParams.leftMargin + layoutParams.rightMargin;
                        i13 = view.getMeasuredWidth() + i19;
                        i3 = Math.max(mode2, i13);
                        i14 = View.combineMeasuredStates(i23, view.getMeasuredState());
                        if (i18 == 0) {
                        }
                        if (layoutParams.weight <= f) {
                            i8 = i22;
                            if (i6 != 0) {
                                i13 = i19;
                            }
                            i17 = Math.max(i24, i13);
                            i13 = i8;
                            i24 = i17;
                        } else {
                            if (i6 == 0) {
                                i19 = i13;
                            }
                            i13 = Math.max(i22, i19);
                        }
                        i18 = i12;
                        i12 = i3;
                        i3 = i14;
                        i19 = i21;
                        i17 = i24;
                        i14 = getChildrenSkipCount(view, virtualChildCount) + virtualChildCount;
                        f2 = f3;
                        i14++;
                        i9 = i3;
                        mode2 = i5;
                        virtualChildCount = i4;
                        i7 = i;
                        i8 = i2;
                    } else {
                        throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                    }
                }
            }
            obj = Integer.MIN_VALUE;
            i14++;
            i9 = i3;
            mode2 = i5;
            virtualChildCount = i4;
            i7 = i;
            i8 = i2;
        }
        int i27 = i19;
        i8 = i13;
        i3 = i9;
        i4 = virtualChildCount;
        i5 = mode2;
        i19 = i17;
        i9 = Integer.MIN_VALUE;
        mode2 = i12;
        Object obj3 = -1;
        if (r7.mTotalLength > 0) {
            i13 = i4;
            if (hasDividerBeforeChildAt(i13)) {
                r7.mTotalLength += r7.mDividerHeight;
            }
        } else {
            i13 = i4;
        }
        if (z) {
            i14 = i5;
            if (i14 == i9 || i14 == 0) {
                r7.mTotalLength = 0;
                i7 = 0;
                while (i7 < i13) {
                    View virtualChildAt2 = getVirtualChildAt(i7);
                    if (virtualChildAt2 == null) {
                        r7.mTotalLength += measureNullChild(i7);
                    } else if (virtualChildAt2.getVisibility() == 8) {
                        i7 += getChildrenSkipCount(virtualChildAt2, i7);
                    } else {
                        LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                        i10 = r7.mTotalLength;
                        r7.mTotalLength = Math.max(i10, (((i10 + i27) + layoutParams3.topMargin) + layoutParams3.bottomMargin) + getNextLocationOffset(virtualChildAt2));
                    }
                    i7++;
                    obj3 = -1;
                }
            }
        } else {
            i14 = i5;
        }
        r7.mTotalLength += getPaddingTop() + getPaddingBottom();
        i7 = i2;
        i12 = View.resolveSizeAndState(Math.max(r7.mTotalLength, getSuggestedMinimumHeight()), i7, 0);
        i9 = (16777215 & i12) - r7.mTotalLength;
        if (i15 != 0 || (i9 != 0 && f2 > f)) {
            if (r7.mWeightSum > f) {
                f2 = r7.mWeightSum;
            }
            i27 = 0;
            r7.mTotalLength = i27;
            float f4 = f2;
            i6 = i27;
            int i28 = i9;
            i9 = i3;
            i3 = i28;
            while (i6 < i13) {
                float f5;
                View virtualChildAt3 = getVirtualChildAt(i6);
                if (virtualChildAt3.getVisibility() == 8) {
                    f5 = f4;
                    i8 = i;
                } else {
                    int i29;
                    int i30;
                    LayoutParams layoutParams4 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f6 = layoutParams4.weight;
                    if (f6 > f) {
                        i29 = (int) ((((float) i3) * f6) / f4);
                        i30 = i3 - i29;
                        f5 = f4 - f6;
                        i3 = getChildMeasureSpec(i, ((getPaddingLeft() + getPaddingRight()) + layoutParams4.leftMargin) + layoutParams4.rightMargin, layoutParams4.width);
                        if (layoutParams4.height == 0) {
                            i27 = 1073741824;
                            if (i14 == i27) {
                                if (i29 <= 0) {
                                    i29 = 0;
                                }
                                virtualChildAt3.measure(i3, MeasureSpec.makeMeasureSpec(i29, i27));
                                i9 = View.combineMeasuredStates(i9, virtualChildAt3.getMeasuredState() & -256);
                            }
                        } else {
                            i27 = 1073741824;
                        }
                        i29 = virtualChildAt3.getMeasuredHeight() + i29;
                        if (i29 < 0) {
                            i29 = 0;
                        }
                        virtualChildAt3.measure(i3, MeasureSpec.makeMeasureSpec(i29, i27));
                        i9 = View.combineMeasuredStates(i9, virtualChildAt3.getMeasuredState() & -256);
                    } else {
                        f6 = f4;
                        i8 = i;
                        i30 = i3;
                        f5 = f6;
                    }
                    i3 = layoutParams4.leftMargin + layoutParams4.rightMargin;
                    i27 = virtualChildAt3.getMeasuredWidth() + i3;
                    mode2 = Math.max(mode2, i27);
                    int i31;
                    if (mode != 1073741824) {
                        i31 = i3;
                        i3 = -1;
                        if (layoutParams4.width == i3) {
                            i29 = i11;
                            if (i29 != 0) {
                                i27 = i31;
                            }
                            i19 = Math.max(i19, i27);
                            i27 = (i18 == 0 && layoutParams4.width == i3) ? i11 : 0;
                            i29 = r7.mTotalLength;
                            r7.mTotalLength = Math.max(i29, (((i29 + virtualChildAt3.getMeasuredHeight()) + layoutParams4.topMargin) + layoutParams4.bottomMargin) + getNextLocationOffset(virtualChildAt3));
                            i18 = i27;
                            i3 = i30;
                        }
                    } else {
                        i31 = i3;
                        i3 = -1;
                    }
                    i29 = 0;
                    if (i29 != 0) {
                        i27 = i31;
                    }
                    i19 = Math.max(i19, i27);
                    if (i18 == 0) {
                    }
                    i29 = r7.mTotalLength;
                    r7.mTotalLength = Math.max(i29, (((i29 + virtualChildAt3.getMeasuredHeight()) + layoutParams4.topMargin) + layoutParams4.bottomMargin) + getNextLocationOffset(virtualChildAt3));
                    i18 = i27;
                    i3 = i30;
                }
                i6++;
                f4 = f5;
                Object obj4 = null;
            }
            i8 = i;
            r7.mTotalLength += getPaddingTop() + getPaddingBottom();
            i6 = i19;
            i3 = i9;
        } else {
            i6 = Math.max(i19, i8);
            if (z && i14 != 1073741824) {
                for (i19 = 0; i19 < i13; i19++) {
                    View virtualChildAt4 = getVirtualChildAt(i19);
                    if (!(virtualChildAt4 == null || virtualChildAt4.getVisibility() == 8 || ((LayoutParams) virtualChildAt4.getLayoutParams()).weight <= f)) {
                        i9 = 1073741824;
                        virtualChildAt4.measure(MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredWidth(), i9), MeasureSpec.makeMeasureSpec(i27, i9));
                    }
                }
            }
            i8 = i;
        }
        if (i18 == 0 && mode != 1073741824) {
            mode2 = i6;
        }
        setMeasuredDimension(View.resolveSizeAndState(Math.max(mode2 + (getPaddingLeft() + getPaddingRight()), getSuggestedMinimumWidth()), i8, i3), i12);
        if (i16 != 0) {
            forceUniformWidth(i13, i7);
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, makeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    void measureHorizontal(int i, int i2) {
        int[] iArr;
        int i3;
        boolean z;
        boolean z2;
        int i4;
        Object obj;
        int measuredHeight;
        int combineMeasuredStates;
        int i5;
        View virtualChildAt;
        LayoutParams layoutParams;
        int i6;
        int i7 = i;
        int i8 = i2;
        int i9 = 0;
        this.mTotalLength = i9;
        int virtualChildCount = getVirtualChildCount();
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int i10 = 4;
        if (this.mMaxAscent == null || r7.mMaxDescent == null) {
            r7.mMaxAscent = new int[i10];
            r7.mMaxDescent = new int[i10];
        }
        int[] iArr2 = r7.mMaxAscent;
        int[] iArr3 = r7.mMaxDescent;
        int i11 = 3;
        int i12 = -1;
        iArr2[i11] = i12;
        int i13 = 2;
        iArr2[i13] = i12;
        int i14 = 1;
        iArr2[i14] = i12;
        iArr2[i9] = i12;
        iArr3[i11] = i12;
        iArr3[i13] = i12;
        iArr3[i14] = i12;
        iArr3[i9] = i12;
        boolean z3 = r7.mBaselineAligned;
        boolean z4 = r7.mUseLargestChild;
        int i15 = 1073741824;
        int i16 = mode == i15 ? i14 : i9;
        float f = 0.0f;
        int i17 = i9;
        int i18 = i17;
        int i19 = i18;
        int i20 = i19;
        int i21 = i20;
        int i22 = i21;
        int i23 = i22;
        int i24 = i14;
        float f2 = f;
        i10 = Integer.MIN_VALUE;
        while (true) {
            iArr = iArr3;
            i3 = 8;
            if (i17 >= virtualChildCount) {
                break;
            }
            Object obj2;
            Object obj3;
            View virtualChildAt2 = getVirtualChildAt(i17);
            if (virtualChildAt2 == null) {
                r7.mTotalLength += measureNullChild(i17);
            } else if (virtualChildAt2.getVisibility() == i3) {
                i17 += getChildrenSkipCount(virtualChildAt2, i17);
            } else {
                int i25;
                LayoutParams layoutParams2;
                View view;
                if (hasDividerBeforeChildAt(i17)) {
                    r7.mTotalLength += r7.mDividerWidth;
                }
                LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                f2 += layoutParams3.weight;
                if (mode == i15 && layoutParams3.width == 0 && layoutParams3.weight > f) {
                    if (i16 != 0) {
                        r7.mTotalLength += layoutParams3.leftMargin + layoutParams3.rightMargin;
                    } else {
                        i3 = r7.mTotalLength;
                        r7.mTotalLength = Math.max(i3, (layoutParams3.leftMargin + i3) + layoutParams3.rightMargin);
                    }
                    if (z3) {
                        i3 = 0;
                        i15 = MeasureSpec.makeMeasureSpec(i3, i3);
                        virtualChildAt2.measure(i15, i15);
                        i25 = i17;
                        z = z4;
                        z2 = z3;
                        layoutParams2 = layoutParams3;
                        i4 = mode;
                        obj = -2;
                        view = virtualChildAt2;
                    } else {
                        i25 = i17;
                        z = z4;
                        z2 = z3;
                        layoutParams2 = layoutParams3;
                        i4 = mode;
                        i19 = i14;
                        i17 = 1073741824;
                        obj = -2;
                        view = virtualChildAt2;
                        if (mode2 == i17 && layoutParams2.height == -1) {
                            i3 = i14;
                            i23 = i3;
                        } else {
                            i3 = 0;
                        }
                        i15 = layoutParams2.topMargin + layoutParams2.bottomMargin;
                        measuredHeight = view.getMeasuredHeight() + i15;
                        combineMeasuredStates = View.combineMeasuredStates(i22, view.getMeasuredState());
                        if (z2) {
                            i12 = view.getBaseline();
                            if (i12 != -1) {
                                i5 = ((((layoutParams2.gravity < 0 ? r7.mGravity : layoutParams2.gravity) & 112) >> 4) & -2) >> 1;
                                iArr2[i5] = Math.max(iArr2[i5], i12);
                                iArr[i5] = Math.max(iArr[i5], measuredHeight - i12);
                            }
                        }
                        i12 = Math.max(i18, measuredHeight);
                        i5 = (i24 == 0 && layoutParams2.height == -1) ? i14 : 0;
                        if (layoutParams2.weight <= f) {
                            if (i3 == 0) {
                                i15 = measuredHeight;
                            }
                            i7 = Math.max(i21, i15);
                        } else {
                            i7 = i21;
                            if (i3 != 0) {
                                measuredHeight = i15;
                            }
                            i20 = Math.max(i20, measuredHeight);
                        }
                        i9 = i25;
                        i3 = getChildrenSkipCount(view, i9) + i9;
                        i22 = combineMeasuredStates;
                        i18 = i12;
                        i24 = i5;
                        i21 = i7;
                        i15 = i17;
                        i17 = i3 + 1;
                        iArr3 = iArr;
                        z4 = z;
                        z3 = z2;
                        mode = i4;
                        obj2 = -1;
                        i7 = i;
                        obj3 = null;
                    }
                } else {
                    if (layoutParams3.width != 0 || layoutParams3.weight <= f) {
                        obj3 = -2;
                        i15 = Integer.MIN_VALUE;
                    } else {
                        layoutParams3.width = -2;
                        i15 = 0;
                    }
                    i25 = i17;
                    obj3 = Integer.MIN_VALUE;
                    i9 = i15;
                    z = z4;
                    z2 = z3;
                    layoutParams2 = layoutParams3;
                    i4 = mode;
                    Object obj4 = -1;
                    view = virtualChildAt2;
                    obj = -2;
                    measureChildBeforeLayout(virtualChildAt2, i25, i7, f2 == f ? r7.mTotalLength : 0, i8, 0);
                    if (i9 != Integer.MIN_VALUE) {
                        layoutParams2.width = i9;
                    }
                    i17 = view.getMeasuredWidth();
                    if (i16 != 0) {
                        r7.mTotalLength += ((layoutParams2.leftMargin + i17) + layoutParams2.rightMargin) + getNextLocationOffset(view);
                    } else {
                        i3 = r7.mTotalLength;
                        r7.mTotalLength = Math.max(i3, (((i3 + i17) + layoutParams2.leftMargin) + layoutParams2.rightMargin) + getNextLocationOffset(view));
                    }
                    if (z) {
                        i10 = Math.max(i17, i10);
                    }
                }
                i17 = 1073741824;
                if (mode2 == i17) {
                }
                i3 = 0;
                i15 = layoutParams2.topMargin + layoutParams2.bottomMargin;
                measuredHeight = view.getMeasuredHeight() + i15;
                combineMeasuredStates = View.combineMeasuredStates(i22, view.getMeasuredState());
                if (z2) {
                    i12 = view.getBaseline();
                    if (i12 != -1) {
                        i5 = ((((layoutParams2.gravity < 0 ? r7.mGravity : layoutParams2.gravity) & 112) >> 4) & -2) >> 1;
                        iArr2[i5] = Math.max(iArr2[i5], i12);
                        iArr[i5] = Math.max(iArr[i5], measuredHeight - i12);
                    }
                }
                i12 = Math.max(i18, measuredHeight);
                if (i24 == 0) {
                }
                if (layoutParams2.weight <= f) {
                    i7 = i21;
                    if (i3 != 0) {
                        measuredHeight = i15;
                    }
                    i20 = Math.max(i20, measuredHeight);
                } else {
                    if (i3 == 0) {
                        i15 = measuredHeight;
                    }
                    i7 = Math.max(i21, i15);
                }
                i9 = i25;
                i3 = getChildrenSkipCount(view, i9) + i9;
                i22 = combineMeasuredStates;
                i18 = i12;
                i24 = i5;
                i21 = i7;
                i15 = i17;
                i17 = i3 + 1;
                iArr3 = iArr;
                z4 = z;
                z3 = z2;
                mode = i4;
                obj2 = -1;
                i7 = i;
                obj3 = null;
            }
            i3 = i17;
            i17 = i15;
            z = z4;
            z2 = z3;
            i4 = mode;
            i15 = i17;
            i17 = i3 + 1;
            iArr3 = iArr;
            z4 = z;
            z3 = z2;
            mode = i4;
            obj2 = -1;
            i7 = i;
            obj3 = null;
        }
        i17 = i15;
        z = z4;
        z2 = z3;
        i4 = mode;
        i12 = i18;
        i15 = i20;
        i7 = i21;
        i9 = i22;
        obj = -2;
        if (r7.mTotalLength > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            r7.mTotalLength += r7.mDividerWidth;
        }
        combineMeasuredStates = -1;
        if (!(iArr2[i14] == combineMeasuredStates && iArr2[0] == combineMeasuredStates && iArr2[i13] == combineMeasuredStates && iArr2[i11] == combineMeasuredStates)) {
            combineMeasuredStates = 0;
            i12 = Math.max(i12, Math.max(iArr2[i11], Math.max(iArr2[combineMeasuredStates], Math.max(iArr2[i14], iArr2[i13]))) + Math.max(iArr[i11], Math.max(iArr[combineMeasuredStates], Math.max(iArr[i14], iArr[i13]))));
        }
        if (z) {
            i17 = i4;
            if (i17 == Integer.MIN_VALUE || i17 == 0) {
                r7.mTotalLength = 0;
                measuredHeight = 0;
                while (measuredHeight < virtualChildCount) {
                    int i26;
                    virtualChildAt = getVirtualChildAt(measuredHeight);
                    if (virtualChildAt == null) {
                        r7.mTotalLength += measureNullChild(measuredHeight);
                    } else if (virtualChildAt.getVisibility() == i3) {
                        measuredHeight += getChildrenSkipCount(virtualChildAt, measuredHeight);
                    } else {
                        layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                        if (i16 != 0) {
                            r7.mTotalLength += ((layoutParams.leftMargin + i10) + layoutParams.rightMargin) + getNextLocationOffset(virtualChildAt);
                        } else {
                            i3 = r7.mTotalLength;
                            i26 = measuredHeight;
                            r7.mTotalLength = Math.max(i3, (((i3 + i10) + layoutParams.leftMargin) + layoutParams.rightMargin) + getNextLocationOffset(virtualChildAt));
                            measuredHeight = i26 + 1;
                            i3 = 8;
                        }
                    }
                    i26 = measuredHeight;
                    measuredHeight = i26 + 1;
                    i3 = 8;
                }
            }
        } else {
            i17 = i4;
        }
        r7.mTotalLength += getPaddingLeft() + getPaddingRight();
        i3 = View.resolveSizeAndState(Math.max(r7.mTotalLength, getSuggestedMinimumWidth()), i, 0);
        combineMeasuredStates = (16777215 & i3) - r7.mTotalLength;
        if (i19 != 0 || (combineMeasuredStates != 0 && f2 > f)) {
            float f3 = r7.mWeightSum > f ? r7.mWeightSum : f2;
            i7 = -1;
            iArr2[i11] = i7;
            iArr2[i13] = i7;
            iArr2[i14] = i7;
            mode = 0;
            iArr2[mode] = i7;
            iArr[i11] = i7;
            iArr[i13] = i7;
            iArr[i14] = i7;
            iArr[mode] = i7;
            r7.mTotalLength = mode;
            mode = i15;
            i15 = 0;
            i7 = -1;
            while (i15 < virtualChildCount) {
                View virtualChildAt3 = getVirtualChildAt(i15);
                Object obj5;
                if (virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8) {
                    i6 = virtualChildCount;
                    obj5 = 4;
                } else {
                    int i27;
                    layoutParams = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f4 = layoutParams.weight;
                    if (f4 > f) {
                        i6 = virtualChildCount;
                        virtualChildCount = (int) ((((float) combineMeasuredStates) * f4) / f3);
                        f3 -= f4;
                        i27 = combineMeasuredStates - virtualChildCount;
                        measuredHeight = getChildMeasureSpec(i8, ((getPaddingTop() + getPaddingBottom()) + layoutParams.topMargin) + layoutParams.bottomMargin, layoutParams.height);
                        if (layoutParams.width == 0) {
                            combineMeasuredStates = 1073741824;
                            if (i17 == combineMeasuredStates) {
                                if (virtualChildCount <= 0) {
                                    virtualChildCount = 0;
                                }
                                virtualChildAt3.measure(MeasureSpec.makeMeasureSpec(virtualChildCount, combineMeasuredStates), measuredHeight);
                                i9 = View.combineMeasuredStates(i9, virtualChildAt3.getMeasuredState() & -16777216);
                            }
                        } else {
                            combineMeasuredStates = 1073741824;
                        }
                        virtualChildCount = virtualChildAt3.getMeasuredWidth() + virtualChildCount;
                        if (virtualChildCount < 0) {
                            virtualChildCount = 0;
                        }
                        virtualChildAt3.measure(MeasureSpec.makeMeasureSpec(virtualChildCount, combineMeasuredStates), measuredHeight);
                        i9 = View.combineMeasuredStates(i9, virtualChildAt3.getMeasuredState() & -16777216);
                    } else {
                        i6 = virtualChildCount;
                        i27 = combineMeasuredStates;
                    }
                    if (i16 != 0) {
                        r7.mTotalLength += ((virtualChildAt3.getMeasuredWidth() + layoutParams.leftMargin) + layoutParams.rightMargin) + getNextLocationOffset(virtualChildAt3);
                    } else {
                        measuredHeight = r7.mTotalLength;
                        r7.mTotalLength = Math.max(measuredHeight, (((virtualChildAt3.getMeasuredWidth() + measuredHeight) + layoutParams.leftMargin) + layoutParams.rightMargin) + getNextLocationOffset(virtualChildAt3));
                    }
                    measuredHeight = (mode2 == 1073741824 || layoutParams.height != -1) ? 0 : i14;
                    combineMeasuredStates = layoutParams.topMargin + layoutParams.bottomMargin;
                    virtualChildCount = virtualChildAt3.getMeasuredHeight() + combineMeasuredStates;
                    i7 = Math.max(i7, virtualChildCount);
                    if (measuredHeight == 0) {
                        combineMeasuredStates = virtualChildCount;
                    }
                    measuredHeight = Math.max(mode, combineMeasuredStates);
                    if (i24 != 0) {
                        mode = -1;
                        if (layoutParams.height == mode) {
                            combineMeasuredStates = i14;
                            if (z2) {
                                i10 = virtualChildAt3.getBaseline();
                                if (i10 != mode) {
                                    obj5 = 4;
                                    i5 = ((((layoutParams.gravity < 0 ? r7.mGravity : layoutParams.gravity) & 112) >> 4) & -2) >> 1;
                                    iArr2[i5] = Math.max(iArr2[i5], i10);
                                    iArr[i5] = Math.max(iArr[i5], virtualChildCount - i10);
                                    mode = measuredHeight;
                                    i24 = combineMeasuredStates;
                                    combineMeasuredStates = i27;
                                }
                            }
                            obj5 = 4;
                            mode = measuredHeight;
                            i24 = combineMeasuredStates;
                            combineMeasuredStates = i27;
                        }
                    } else {
                        mode = -1;
                    }
                    combineMeasuredStates = 0;
                    if (z2) {
                        i10 = virtualChildAt3.getBaseline();
                        if (i10 != mode) {
                            obj5 = 4;
                            i5 = ((((layoutParams.gravity < 0 ? r7.mGravity : layoutParams.gravity) & 112) >> 4) & -2) >> 1;
                            iArr2[i5] = Math.max(iArr2[i5], i10);
                            iArr[i5] = Math.max(iArr[i5], virtualChildCount - i10);
                            mode = measuredHeight;
                            i24 = combineMeasuredStates;
                            combineMeasuredStates = i27;
                        }
                    }
                    obj5 = 4;
                    mode = measuredHeight;
                    i24 = combineMeasuredStates;
                    combineMeasuredStates = i27;
                }
                i15++;
                virtualChildCount = i6;
                measuredHeight = i;
            }
            i6 = virtualChildCount;
            r7.mTotalLength += getPaddingLeft() + getPaddingRight();
            i15 = -1;
            if (iArr2[i14] == i15 && iArr2[0] == i15 && iArr2[i13] == i15 && iArr2[i11] == i15) {
                i12 = i7;
            } else {
                i15 = 0;
                i12 = Math.max(i7, Math.max(iArr2[i11], Math.max(iArr2[i15], Math.max(iArr2[i14], iArr2[i13]))) + Math.max(iArr[i11], Math.max(iArr[i15], Math.max(iArr[i14], iArr[i13]))));
            }
        } else {
            i15 = Math.max(i15, i7);
            if (z && i17 != 1073741824) {
                for (i17 = 0; i17 < virtualChildCount; i17++) {
                    virtualChildAt = getVirtualChildAt(i17);
                    if (!(virtualChildAt == null || virtualChildAt.getVisibility() == 8 || ((LayoutParams) virtualChildAt.getLayoutParams()).weight <= f)) {
                        i7 = 1073741824;
                        virtualChildAt.measure(MeasureSpec.makeMeasureSpec(i10, i7), MeasureSpec.makeMeasureSpec(virtualChildAt.getMeasuredHeight(), i7));
                    }
                }
            }
            mode = i15;
            i6 = virtualChildCount;
        }
        if (i24 != 0 || mode2 == 1073741824) {
            mode = i12;
        }
        setMeasuredDimension(i3 | (-16777216 & i9), View.resolveSizeAndState(Math.max(mode + (getPaddingTop() + getPaddingBottom()), getSuggestedMinimumHeight()), i8, i9 << 16));
        if (i23 != 0) {
            forceUniformHeight(i6, i);
        }
    }

    private void forceUniformHeight(int i, int i2) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, makeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation == 1) {
            layoutVertical(i, i2, i3, i4);
        } else {
            layoutHorizontal(i, i2, i3, i4);
        }
    }

    void layoutVertical(int i, int i2, int i3, int i4) {
        int paddingTop;
        int paddingLeft = getPaddingLeft();
        int i5 = i3 - i;
        int paddingRight = i5 - getPaddingRight();
        int paddingRight2 = (i5 - paddingLeft) - getPaddingRight();
        int virtualChildCount = getVirtualChildCount();
        i5 = this.mGravity & 112;
        int i6 = this.mGravity & 8388615;
        if (i5 == 16) {
            paddingTop = (((i4 - i2) - r6.mTotalLength) / 2) + getPaddingTop();
        } else if (i5 != 80) {
            paddingTop = getPaddingTop();
        } else {
            paddingTop = ((getPaddingTop() + i4) - i2) - r6.mTotalLength;
        }
        int i7 = 0;
        while (i7 < virtualChildCount) {
            int i8;
            View virtualChildAt = getVirtualChildAt(i7);
            int i9 = 1;
            if (virtualChildAt == null) {
                paddingTop += measureNullChild(i7);
            } else if (virtualChildAt.getVisibility() != 8) {
                int measuredWidth = virtualChildAt.getMeasuredWidth();
                int measuredHeight = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                i8 = layoutParams.gravity;
                if (i8 < 0) {
                    i8 = i6;
                }
                i8 = GravityCompat.getAbsoluteGravity(i8, ViewCompat.getLayoutDirection(this)) & 7;
                if (i8 == i9) {
                    i8 = ((((paddingRight2 - measuredWidth) / 2) + paddingLeft) + layoutParams.leftMargin) - layoutParams.rightMargin;
                } else if (i8 != 5) {
                    i8 = layoutParams.leftMargin + paddingLeft;
                } else {
                    i8 = (paddingRight - measuredWidth) - layoutParams.rightMargin;
                }
                i5 = i8;
                if (hasDividerBeforeChildAt(i7)) {
                    paddingTop += r6.mDividerHeight;
                }
                int i10 = paddingTop + layoutParams.topMargin;
                LayoutParams layoutParams2 = layoutParams;
                setChildFrame(virtualChildAt, i5, i10 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                i7 += getChildrenSkipCount(virtualChildAt, i7);
                paddingTop = i10 + ((measuredHeight + layoutParams2.bottomMargin) + getNextLocationOffset(virtualChildAt));
                i8 = 1;
                i7 += i8;
            }
            i8 = i9;
            i7 += i8;
        }
    }

    void layoutHorizontal(int i, int i2, int i3, int i4) {
        int paddingLeft;
        int i5;
        int i6;
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int paddingTop = getPaddingTop();
        int i7 = i4 - i2;
        int paddingBottom = i7 - getPaddingBottom();
        int paddingBottom2 = (i7 - paddingTop) - getPaddingBottom();
        int virtualChildCount = getVirtualChildCount();
        i7 = this.mGravity & 8388615;
        int i8 = this.mGravity & 112;
        boolean z = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        i7 = GravityCompat.getAbsoluteGravity(i7, ViewCompat.getLayoutDirection(this));
        int i9 = 2;
        int i10 = 1;
        if (i7 == i10) {
            paddingLeft = (((i3 - i) - r6.mTotalLength) / i9) + getPaddingLeft();
        } else if (i7 != 5) {
            paddingLeft = getPaddingLeft();
        } else {
            paddingLeft = ((getPaddingLeft() + i3) - i) - r6.mTotalLength;
        }
        int i11 = 0;
        if (isLayoutRtl) {
            i5 = virtualChildCount - 1;
            i6 = -1;
        } else {
            i5 = i11;
            i6 = i10;
        }
        i7 = i11;
        while (i7 < virtualChildCount) {
            int i12;
            int i13;
            int i14;
            int i15;
            Object obj;
            Object obj2;
            int i16 = i5 + (i6 * i7);
            View virtualChildAt = getVirtualChildAt(i16);
            int i17;
            if (virtualChildAt == null) {
                paddingLeft += measureNullChild(i16);
                i12 = i10;
                i13 = paddingTop;
                i14 = virtualChildCount;
                i15 = i8;
            } else if (virtualChildAt.getVisibility() != 8) {
                int i18;
                View view;
                LayoutParams layoutParams;
                View view2;
                i9 = virtualChildAt.getMeasuredWidth();
                i10 = virtualChildAt.getMeasuredHeight();
                LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                if (z) {
                    i18 = i7;
                    i14 = virtualChildCount;
                    if (layoutParams2.height != -1) {
                        i7 = virtualChildAt.getBaseline();
                        virtualChildCount = layoutParams2.gravity;
                        if (virtualChildCount < 0) {
                            virtualChildCount = i8;
                        }
                        virtualChildCount &= 112;
                        i15 = i8;
                        Object obj3;
                        if (virtualChildCount != 16) {
                            obj3 = -1;
                            i12 = 1;
                            i7 = ((((paddingBottom2 - i10) / 2) + paddingTop) + layoutParams2.topMargin) - layoutParams2.bottomMargin;
                        } else if (virtualChildCount != 48) {
                            if (virtualChildCount != 80) {
                                i7 = paddingTop;
                                obj3 = -1;
                            } else {
                                virtualChildCount = (paddingBottom - i10) - layoutParams2.bottomMargin;
                                if (i7 != -1) {
                                    virtualChildCount -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - i7);
                                }
                                i7 = virtualChildCount;
                            }
                            i12 = 1;
                        } else {
                            virtualChildCount = layoutParams2.topMargin + paddingTop;
                            if (i7 != -1) {
                                i12 = 1;
                                virtualChildCount += iArr[i12] - i7;
                            } else {
                                i12 = 1;
                            }
                            i7 = virtualChildCount;
                        }
                        if (hasDividerBeforeChildAt(i16)) {
                            paddingLeft += r6.mDividerWidth;
                        }
                        virtualChildCount = layoutParams2.leftMargin + paddingLeft;
                        view = virtualChildAt;
                        i8 = i16;
                        i16 = virtualChildCount + getLocationOffset(virtualChildAt);
                        i17 = i18;
                        i13 = paddingTop;
                        obj = -1;
                        layoutParams = layoutParams2;
                        setChildFrame(virtualChildAt, i16, i7, i9, i10);
                        view2 = view;
                        i7 = i17 + getChildrenSkipCount(view2, i8);
                        paddingLeft = virtualChildCount + ((i9 + layoutParams.rightMargin) + getNextLocationOffset(view2));
                        i7++;
                        i10 = i12;
                        virtualChildCount = i14;
                        i8 = i15;
                        paddingTop = i13;
                        obj2 = 2;
                    }
                } else {
                    i18 = i7;
                    i14 = virtualChildCount;
                }
                i7 = -1;
                virtualChildCount = layoutParams2.gravity;
                if (virtualChildCount < 0) {
                    virtualChildCount = i8;
                }
                virtualChildCount &= 112;
                i15 = i8;
                if (virtualChildCount != 16) {
                    obj3 = -1;
                    i12 = 1;
                    i7 = ((((paddingBottom2 - i10) / 2) + paddingTop) + layoutParams2.topMargin) - layoutParams2.bottomMargin;
                } else if (virtualChildCount != 48) {
                    if (virtualChildCount != 80) {
                        i7 = paddingTop;
                        obj3 = -1;
                    } else {
                        virtualChildCount = (paddingBottom - i10) - layoutParams2.bottomMargin;
                        if (i7 != -1) {
                            virtualChildCount -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - i7);
                        }
                        i7 = virtualChildCount;
                    }
                    i12 = 1;
                } else {
                    virtualChildCount = layoutParams2.topMargin + paddingTop;
                    if (i7 != -1) {
                        i12 = 1;
                        virtualChildCount += iArr[i12] - i7;
                    } else {
                        i12 = 1;
                    }
                    i7 = virtualChildCount;
                }
                if (hasDividerBeforeChildAt(i16)) {
                    paddingLeft += r6.mDividerWidth;
                }
                virtualChildCount = layoutParams2.leftMargin + paddingLeft;
                view = virtualChildAt;
                i8 = i16;
                i16 = virtualChildCount + getLocationOffset(virtualChildAt);
                i17 = i18;
                i13 = paddingTop;
                obj = -1;
                layoutParams = layoutParams2;
                setChildFrame(virtualChildAt, i16, i7, i9, i10);
                view2 = view;
                i7 = i17 + getChildrenSkipCount(view2, i8);
                paddingLeft = virtualChildCount + ((i9 + layoutParams.rightMargin) + getNextLocationOffset(view2));
                i7++;
                i10 = i12;
                virtualChildCount = i14;
                i8 = i15;
                paddingTop = i13;
                obj2 = 2;
            } else {
                i17 = i7;
                i13 = paddingTop;
                i14 = virtualChildCount;
                i15 = i8;
                i12 = 1;
            }
            obj = -1;
            i7++;
            i10 = i12;
            virtualChildCount = i14;
            i8 = i15;
            paddingTop = i13;
            obj2 = 2;
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i3 + i, i4 + i2);
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    public int getGravity() {
        return this.mGravity;
    }

    public void setHorizontalGravity(int i) {
        int i2 = 8388615;
        i &= i2;
        if ((i2 & this.mGravity) != i) {
            this.mGravity = i | (this.mGravity & -8388616);
            requestLayout();
        }
    }

    public void setVerticalGravity(int i) {
        i &= 112;
        if ((this.mGravity & 112) != i) {
            this.mGravity = i | (this.mGravity & -113);
            requestLayout();
        }
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        int i = -2;
        if (this.mOrientation == 0) {
            return new LayoutParams(i, i);
        }
        return this.mOrientation == 1 ? new LayoutParams(-1, i) : null;
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityEvent(accessibilityEvent);
            accessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        if (VERSION.SDK_INT >= 14) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
        }
    }
}
