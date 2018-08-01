package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

@RestrictTo({Scope.LIBRARY_GROUP})
public class AlertDialogLayout extends LinearLayoutCompat {
    public AlertDialogLayout(@Nullable Context context) {
        super(context);
    }

    public AlertDialogLayout(@Nullable Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected void onMeasure(int i, int i2) {
        if (!tryOnMeasure(i, i2)) {
            super.onMeasure(i, i2);
        }
    }

    private boolean tryOnMeasure(int i, int i2) {
        AlertDialogLayout alertDialogLayout = this;
        int i3 = i;
        int i4 = i2;
        int childCount = getChildCount();
        boolean z = false;
        View view = null;
        View view2 = view;
        View view3 = view2;
        int i5 = z;
        while (true) {
            int i6 = 8;
            if (i5 < childCount) {
                View childAt = getChildAt(i5);
                if (childAt.getVisibility() != i6) {
                    i6 = childAt.getId();
                    if (i6 == R.id.topPanel) {
                        view = childAt;
                    } else if (i6 == R.id.buttonPanel) {
                        view2 = childAt;
                    } else if ((i6 != R.id.contentPanel && i6 != R.id.customPanel) || view3 != null) {
                        return z;
                    } else {
                        view3 = childAt;
                    }
                }
                i5++;
            } else {
                int combineMeasuredStates;
                int resolveMinimumHeight;
                int measuredHeight;
                int i7;
                i5 = MeasureSpec.getMode(i2);
                int size = MeasureSpec.getSize(i2);
                int mode = MeasureSpec.getMode(i);
                int paddingTop = getPaddingTop() + getPaddingBottom();
                if (view != null) {
                    view.measure(i3, z);
                    paddingTop += view.getMeasuredHeight();
                    combineMeasuredStates = View.combineMeasuredStates(z, view.getMeasuredState());
                } else {
                    combineMeasuredStates = z;
                }
                if (view2 != null) {
                    view2.measure(i3, z);
                    resolveMinimumHeight = resolveMinimumHeight(view2);
                    measuredHeight = view2.getMeasuredHeight() - resolveMinimumHeight;
                    paddingTop += resolveMinimumHeight;
                    combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, view2.getMeasuredState());
                } else {
                    resolveMinimumHeight = z;
                    measuredHeight = resolveMinimumHeight;
                }
                if (view3 != null) {
                    if (i5 == 0) {
                        i7 = z;
                    } else {
                        i7 = MeasureSpec.makeMeasureSpec(Math.max(z, size - paddingTop), i5);
                    }
                    view3.measure(i3, i7);
                    i7 = view3.getMeasuredHeight();
                    paddingTop += i7;
                    combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, view3.getMeasuredState());
                } else {
                    i7 = 0;
                }
                size -= paddingTop;
                int i8 = 1073741824;
                if (view2 != null) {
                    paddingTop -= resolveMinimumHeight;
                    measuredHeight = Math.min(size, measuredHeight);
                    if (measuredHeight > 0) {
                        size -= measuredHeight;
                        resolveMinimumHeight += measuredHeight;
                    }
                    view2.measure(i3, MeasureSpec.makeMeasureSpec(resolveMinimumHeight, i8));
                    paddingTop += view2.getMeasuredHeight();
                    combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, view2.getMeasuredState());
                }
                if (view3 != null && size > 0) {
                    paddingTop -= i7;
                    view3.measure(i3, MeasureSpec.makeMeasureSpec(i7 + size, i5));
                    paddingTop += view3.getMeasuredHeight();
                    combineMeasuredStates = View.combineMeasuredStates(combineMeasuredStates, view3.getMeasuredState());
                }
                int i9 = 0;
                for (i5 = 0; i5 < childCount; i5++) {
                    view3 = getChildAt(i5);
                    if (view3.getVisibility() != i6) {
                        i9 = Math.max(i9, view3.getMeasuredWidth());
                    }
                }
                setMeasuredDimension(View.resolveSizeAndState(i9 + (getPaddingLeft() + getPaddingRight()), i3, combineMeasuredStates), View.resolveSizeAndState(paddingTop, i4, 0));
                if (mode != i8) {
                    forceUniformWidth(childCount, i4);
                }
                return true;
            }
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = childAt.getMeasuredHeight();
                    measureChildWithMargins(childAt, makeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    private static int resolveMinimumHeight(View view) {
        int minimumHeight = ViewCompat.getMinimumHeight(view);
        if (minimumHeight > 0) {
            return minimumHeight;
        }
        int i = 0;
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.getChildCount() == 1) {
                return resolveMinimumHeight(viewGroup.getChildAt(i));
            }
        }
        return i;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingTop;
        int i5;
        AlertDialogLayout alertDialogLayout = this;
        int paddingLeft = getPaddingLeft();
        int i6 = i3 - i;
        int paddingRight = i6 - getPaddingRight();
        int paddingRight2 = (i6 - paddingLeft) - getPaddingRight();
        i6 = getMeasuredHeight();
        int childCount = getChildCount();
        int gravity = getGravity();
        int i7 = gravity & 112;
        int i8 = gravity & 8388615;
        if (i7 == 16) {
            paddingTop = (((i4 - i2) - i6) / 2) + getPaddingTop();
        } else if (i7 != 80) {
            paddingTop = getPaddingTop();
        } else {
            paddingTop = ((getPaddingTop() + i4) - i2) - i6;
        }
        Drawable dividerDrawable = getDividerDrawable();
        i6 = 0;
        if (dividerDrawable == null) {
            i5 = i6;
        } else {
            i5 = dividerDrawable.getIntrinsicHeight();
        }
        for (int i9 = i6; i9 < childCount; i9++) {
            View childAt = getChildAt(i9);
            if (!(childAt == null || childAt.getVisibility() == 8)) {
                i7 = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                i6 = layoutParams.gravity;
                if (i6 < 0) {
                    i6 = i8;
                }
                i6 = GravityCompat.getAbsoluteGravity(i6, ViewCompat.getLayoutDirection(this)) & 7;
                if (i6 == 1) {
                    i6 = ((((paddingRight2 - i7) / 2) + paddingLeft) + layoutParams.leftMargin) - layoutParams.rightMargin;
                } else if (i6 != 5) {
                    i6 = layoutParams.leftMargin + paddingLeft;
                } else {
                    i6 = (paddingRight - i7) - layoutParams.rightMargin;
                }
                if (hasDividerBeforeChildAt(i9)) {
                    paddingTop += i5;
                }
                int i10 = paddingTop + layoutParams.topMargin;
                setChildFrame(childAt, i6, i10, i7, measuredHeight);
                paddingTop = i10 + (measuredHeight + layoutParams.bottomMargin);
            }
        }
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i3 + i, i4 + i2);
    }
}
