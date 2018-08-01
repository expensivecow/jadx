package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;

public class ContentFrameLayout extends FrameLayout {
    private OnAttachListener mAttachListener;
    private final Rect mDecorPadding;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;

    public interface OnAttachListener {
        void onAttachedFromWindow();

        void onDetachedFromWindow();
    }

    public ContentFrameLayout(Context context) {
        this(context, null);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDecorPadding = new Rect();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void dispatchFitSystemWindows(Rect rect) {
        fitSystemWindows(rect);
    }

    public void setAttachListener(OnAttachListener onAttachListener) {
        this.mAttachListener = onAttachListener;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setDecorPadding(int i, int i2, int i3, int i4) {
        this.mDecorPadding.set(i, i2, i3, i4);
        if (ViewCompat.isLaidOut(this)) {
            requestLayout();
        }
    }

    protected void onMeasure(int i, int i2) {
        int dimension;
        TypedValue typedValue;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int i3 = 1;
        int i4 = 0;
        int i5 = displayMetrics.widthPixels < displayMetrics.heightPixels ? i3 : i4;
        int mode = MeasureSpec.getMode(i);
        int mode2 = MeasureSpec.getMode(i2);
        int i6 = 6;
        int i7 = 5;
        int i8 = Integer.MIN_VALUE;
        int i9 = 1073741824;
        if (mode == i8) {
            TypedValue typedValue2 = i5 != 0 ? this.mFixedWidthMinor : this.mFixedWidthMajor;
            if (!(typedValue2 == null || typedValue2.type == 0)) {
                dimension = typedValue2.type == i7 ? (int) typedValue2.getDimension(displayMetrics) : typedValue2.type == i6 ? (int) typedValue2.getFraction((float) displayMetrics.widthPixels, (float) displayMetrics.widthPixels) : i4;
                if (dimension > 0) {
                    dimension = MeasureSpec.makeMeasureSpec(Math.min(dimension - (this.mDecorPadding.left + this.mDecorPadding.right), MeasureSpec.getSize(i)), i9);
                    i = i3;
                    if (mode2 == i8) {
                        TypedValue typedValue3 = i5 != 0 ? this.mFixedHeightMajor : this.mFixedHeightMinor;
                        if (!(typedValue3 == null || typedValue3.type == 0)) {
                            mode2 = typedValue3.type == i7 ? (int) typedValue3.getDimension(displayMetrics) : typedValue3.type == i6 ? (int) typedValue3.getFraction((float) displayMetrics.heightPixels, (float) displayMetrics.heightPixels) : i4;
                            if (mode2 > 0) {
                                i2 = MeasureSpec.makeMeasureSpec(Math.min(mode2 - (this.mDecorPadding.top + this.mDecorPadding.bottom), MeasureSpec.getSize(i2)), i9);
                            }
                        }
                    }
                    super.onMeasure(dimension, i2);
                    mode2 = getMeasuredWidth();
                    dimension = MeasureSpec.makeMeasureSpec(mode2, i9);
                    if (i == 0 && mode == i8) {
                        typedValue = i5 == 0 ? this.mMinWidthMinor : this.mMinWidthMajor;
                        if (!(typedValue == null || typedValue.type == 0)) {
                            i = typedValue.type != i7 ? (int) typedValue.getDimension(displayMetrics) : typedValue.type == i6 ? (int) typedValue.getFraction((float) displayMetrics.widthPixels, (float) displayMetrics.widthPixels) : i4;
                            if (i > 0) {
                                i -= this.mDecorPadding.left + this.mDecorPadding.right;
                            }
                            if (mode2 < i) {
                                dimension = MeasureSpec.makeMeasureSpec(i, i9);
                                if (i3 == 0) {
                                    super.onMeasure(dimension, i2);
                                }
                            }
                        }
                    }
                    i3 = i4;
                    if (i3 == 0) {
                        super.onMeasure(dimension, i2);
                    }
                }
            }
        }
        dimension = i;
        i = i4;
        if (mode2 == i8) {
            TypedValue typedValue32 = i5 != 0 ? this.mFixedHeightMajor : this.mFixedHeightMinor;
            if (!(typedValue32 == null || typedValue32.type == 0)) {
                mode2 = typedValue32.type == i7 ? (int) typedValue32.getDimension(displayMetrics) : typedValue32.type == i6 ? (int) typedValue32.getFraction((float) displayMetrics.heightPixels, (float) displayMetrics.heightPixels) : i4;
                if (mode2 > 0) {
                    i2 = MeasureSpec.makeMeasureSpec(Math.min(mode2 - (this.mDecorPadding.top + this.mDecorPadding.bottom), MeasureSpec.getSize(i2)), i9);
                }
            }
        }
        super.onMeasure(dimension, i2);
        mode2 = getMeasuredWidth();
        dimension = MeasureSpec.makeMeasureSpec(mode2, i9);
        if (i5 == 0) {
        }
        if (typedValue.type != i7) {
        }
        if (i > 0) {
            i -= this.mDecorPadding.left + this.mDecorPadding.right;
        }
        if (mode2 < i) {
            dimension = MeasureSpec.makeMeasureSpec(i, i9);
            if (i3 == 0) {
                super.onMeasure(dimension, i2);
            }
        }
        i3 = i4;
        if (i3 == 0) {
            super.onMeasure(dimension, i2);
        }
    }

    public TypedValue getMinWidthMajor() {
        if (this.mMinWidthMajor == null) {
            this.mMinWidthMajor = new TypedValue();
        }
        return this.mMinWidthMajor;
    }

    public TypedValue getMinWidthMinor() {
        if (this.mMinWidthMinor == null) {
            this.mMinWidthMinor = new TypedValue();
        }
        return this.mMinWidthMinor;
    }

    public TypedValue getFixedWidthMajor() {
        if (this.mFixedWidthMajor == null) {
            this.mFixedWidthMajor = new TypedValue();
        }
        return this.mFixedWidthMajor;
    }

    public TypedValue getFixedWidthMinor() {
        if (this.mFixedWidthMinor == null) {
            this.mFixedWidthMinor = new TypedValue();
        }
        return this.mFixedWidthMinor;
    }

    public TypedValue getFixedHeightMajor() {
        if (this.mFixedHeightMajor == null) {
            this.mFixedHeightMajor = new TypedValue();
        }
        return this.mFixedHeightMajor;
    }

    public TypedValue getFixedHeightMinor() {
        if (this.mFixedHeightMinor == null) {
            this.mFixedHeightMinor = new TypedValue();
        }
        return this.mFixedHeightMinor;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onAttachedFromWindow();
        }
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttachListener != null) {
            this.mAttachListener.onDetachedFromWindow();
        }
    }
}
