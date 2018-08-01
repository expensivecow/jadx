package android.support.v4.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewParent;

public class NestedScrollingChildHelper {
    private boolean mIsNestedScrollingEnabled;
    private ViewParent mNestedScrollingParentNonTouch;
    private ViewParent mNestedScrollingParentTouch;
    private int[] mTempNestedScrollConsumed;
    private final View mView;

    public NestedScrollingChildHelper(@NonNull View view) {
        this.mView = view;
    }

    public void setNestedScrollingEnabled(boolean z) {
        if (this.mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(this.mView);
        }
        this.mIsNestedScrollingEnabled = z;
    }

    public boolean isNestedScrollingEnabled() {
        return this.mIsNestedScrollingEnabled;
    }

    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(0);
    }

    public boolean hasNestedScrollingParent(int i) {
        return getNestedScrollingParentForType(i) != null;
    }

    public boolean startNestedScroll(int i) {
        return startNestedScroll(i, 0);
    }

    public boolean startNestedScroll(int i, int i2) {
        boolean z = true;
        if (hasNestedScrollingParent(i2)) {
            return z;
        }
        if (isNestedScrollingEnabled()) {
            View view = this.mView;
            for (ViewParent parent = this.mView.getParent(); parent != null; parent = parent.getParent()) {
                if (ViewParentCompat.onStartNestedScroll(parent, view, this.mView, i, i2)) {
                    setNestedScrollingParentForType(i2, parent);
                    ViewParentCompat.onNestedScrollAccepted(parent, view, this.mView, i, i2);
                    return z;
                }
                if (parent instanceof View) {
                    view = (View) parent;
                }
            }
        }
        return false;
    }

    public void stopNestedScroll() {
        stopNestedScroll(0);
    }

    public void stopNestedScroll(int i) {
        ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(i);
        if (nestedScrollingParentForType != null) {
            ViewParentCompat.onStopNestedScroll(nestedScrollingParentForType, this.mView, i);
            setNestedScrollingParentForType(i, null);
        }
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, @Nullable int[] iArr) {
        return dispatchNestedScroll(i, i2, i3, i4, iArr, 0);
    }

    public boolean dispatchNestedScroll(int i, int i2, int i3, int i4, @Nullable int[] iArr, int i5) {
        int[] iArr2 = iArr;
        boolean z = false;
        if (isNestedScrollingEnabled()) {
            int i6 = i5;
            ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(i6);
            if (nestedScrollingParentForType == null) {
                return z;
            }
            boolean z2 = true;
            if (i != 0 || i2 != 0 || i3 != 0 || i4 != 0) {
                int i7;
                int i8;
                if (iArr2 != null) {
                    r0.mView.getLocationInWindow(iArr2);
                    i7 = iArr2[z];
                    i8 = iArr2[z2];
                } else {
                    i7 = z;
                    i8 = i7;
                }
                ViewParentCompat.onNestedScroll(nestedScrollingParentForType, r0.mView, i, i2, i3, i4, i6);
                if (iArr2 != null) {
                    r0.mView.getLocationInWindow(iArr2);
                    iArr2[z] = iArr2[z] - i7;
                    iArr2[z2] = iArr2[z2] - i8;
                }
                return z2;
            } else if (iArr2 != null) {
                iArr2[z] = z;
                iArr2[z2] = z;
            }
        }
        return z;
    }

    public boolean dispatchNestedPreScroll(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2) {
        return dispatchNestedPreScroll(i, i2, iArr, iArr2, 0);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, @Nullable int[] iArr, @Nullable int[] iArr2, int i3) {
        boolean z = false;
        if (isNestedScrollingEnabled()) {
            ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(i3);
            if (nestedScrollingParentForType == null) {
                return z;
            }
            boolean z2 = true;
            if (i != 0 || i2 != 0) {
                int i4;
                int i5;
                if (iArr2 != null) {
                    this.mView.getLocationInWindow(iArr2);
                    i4 = iArr2[z];
                    i5 = iArr2[z2];
                } else {
                    i4 = z;
                    i5 = i4;
                }
                if (iArr == null) {
                    if (this.mTempNestedScrollConsumed == null) {
                        this.mTempNestedScrollConsumed = new int[2];
                    }
                    iArr = this.mTempNestedScrollConsumed;
                }
                iArr[z] = z;
                iArr[z2] = z;
                ViewParentCompat.onNestedPreScroll(nestedScrollingParentForType, this.mView, i, i2, iArr, i3);
                if (iArr2 != null) {
                    this.mView.getLocationInWindow(iArr2);
                    iArr2[z] = iArr2[z] - i4;
                    iArr2[z2] = iArr2[z2] - i5;
                }
                if (iArr[z] == 0 && iArr[z2] == 0) {
                    z2 = z;
                }
                return z2;
            } else if (iArr2 != null) {
                iArr2[z] = z;
                iArr2[z2] = z;
            }
        }
        return z;
    }

    public boolean dispatchNestedFling(float f, float f2, boolean z) {
        boolean z2 = false;
        if (isNestedScrollingEnabled()) {
            ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(z2);
            if (nestedScrollingParentForType != null) {
                return ViewParentCompat.onNestedFling(nestedScrollingParentForType, this.mView, f, f2, z);
            }
        }
        return z2;
    }

    public boolean dispatchNestedPreFling(float f, float f2) {
        boolean z = false;
        if (isNestedScrollingEnabled()) {
            ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(z);
            if (nestedScrollingParentForType != null) {
                return ViewParentCompat.onNestedPreFling(nestedScrollingParentForType, this.mView, f, f2);
            }
        }
        return z;
    }

    public void onDetachedFromWindow() {
        ViewCompat.stopNestedScroll(this.mView);
    }

    public void onStopNestedScroll(@NonNull View view) {
        ViewCompat.stopNestedScroll(this.mView);
    }

    private ViewParent getNestedScrollingParentForType(int i) {
        switch (i) {
            case 0:
                return this.mNestedScrollingParentTouch;
            case 1:
                return this.mNestedScrollingParentNonTouch;
            default:
                return null;
        }
    }

    private void setNestedScrollingParentForType(int i, ViewParent viewParent) {
        switch (i) {
            case 0:
                this.mNestedScrollingParentTouch = viewParent;
                return;
            case 1:
                this.mNestedScrollingParentNonTouch = viewParent;
                return;
            default:
                return;
        }
    }
}
