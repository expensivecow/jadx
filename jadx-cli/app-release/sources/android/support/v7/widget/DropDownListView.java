package android.support.v7.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.appcompat.R;
import android.view.MotionEvent;
import android.view.View;

class DropDownListView extends ListViewCompat {
    private ViewPropertyAnimatorCompat mClickAnimation;
    private boolean mDrawsInPressedState;
    private boolean mHijackFocus;
    private boolean mListSelectionHidden;
    private ListViewAutoScrollHelper mScrollHelper;

    public DropDownListView(Context context, boolean z) {
        super(context, null, R.attr.dropDownListViewStyle);
        this.mHijackFocus = z;
        setCacheColorHint(0);
    }

    public boolean onForwardedEvent(MotionEvent motionEvent, int i) {
        boolean z;
        boolean z2;
        boolean actionMasked = motionEvent.getActionMasked();
        boolean z3 = false;
        boolean z4 = true;
        switch (actionMasked) {
            case true:
                z = z3;
                break;
            case true:
                z = z4;
                break;
            case true:
                z2 = z3;
                z = z2;
                break;
            default:
                z2 = z3;
                z = z4;
                break;
        }
        i = motionEvent.findPointerIndex(i);
        if (i >= 0) {
            int x = (int) motionEvent.getX(i);
            i = (int) motionEvent.getY(i);
            int pointToPosition = pointToPosition(x, i);
            if (pointToPosition == -1) {
                z2 = z4;
                if (!z || z2) {
                    clearPressedItem();
                }
                if (!z) {
                    if (this.mScrollHelper == null) {
                        this.mScrollHelper = new ListViewAutoScrollHelper(this);
                    }
                    this.mScrollHelper.setEnabled(z4);
                    this.mScrollHelper.onTouch(this, motionEvent);
                } else if (this.mScrollHelper != null) {
                    this.mScrollHelper.setEnabled(z3);
                }
                return z;
            }
            View childAt = getChildAt(pointToPosition - getFirstVisiblePosition());
            setPressedItem(childAt, pointToPosition, (float) x, (float) i);
            if (actionMasked == z4) {
                clickPressedItem(childAt, pointToPosition);
            }
            z2 = z3;
            z = z4;
            clearPressedItem();
            if (!z) {
                if (this.mScrollHelper == null) {
                    this.mScrollHelper = new ListViewAutoScrollHelper(this);
                }
                this.mScrollHelper.setEnabled(z4);
                this.mScrollHelper.onTouch(this, motionEvent);
            } else if (this.mScrollHelper != null) {
                this.mScrollHelper.setEnabled(z3);
            }
            return z;
        }
        z2 = z3;
        z = z2;
        clearPressedItem();
        if (!z) {
            if (this.mScrollHelper == null) {
                this.mScrollHelper = new ListViewAutoScrollHelper(this);
            }
            this.mScrollHelper.setEnabled(z4);
            this.mScrollHelper.onTouch(this, motionEvent);
        } else if (this.mScrollHelper != null) {
            this.mScrollHelper.setEnabled(z3);
        }
        return z;
    }

    private void clickPressedItem(View view, int i) {
        performItemClick(view, i, getItemIdAtPosition(i));
    }

    void setListSelectionHidden(boolean z) {
        this.mListSelectionHidden = z;
    }

    private void clearPressedItem() {
        boolean z = false;
        this.mDrawsInPressedState = z;
        setPressed(z);
        drawableStateChanged();
        View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
        if (childAt != null) {
            childAt.setPressed(z);
        }
        if (this.mClickAnimation != null) {
            this.mClickAnimation.cancel();
            this.mClickAnimation = null;
        }
    }

    private void setPressedItem(View view, int i, float f, float f2) {
        boolean z = true;
        this.mDrawsInPressedState = z;
        int i2 = 21;
        if (VERSION.SDK_INT >= i2) {
            drawableHotspotChanged(f, f2);
        }
        if (!isPressed()) {
            setPressed(z);
        }
        layoutChildren();
        boolean z2 = false;
        if (this.mMotionPosition != -1) {
            View childAt = getChildAt(this.mMotionPosition - getFirstVisiblePosition());
            if (!(childAt == null || childAt == view || !childAt.isPressed())) {
                childAt.setPressed(z2);
            }
        }
        this.mMotionPosition = i;
        float left = f - ((float) view.getLeft());
        float top = f2 - ((float) view.getTop());
        if (VERSION.SDK_INT >= i2) {
            view.drawableHotspotChanged(left, top);
        }
        if (!view.isPressed()) {
            view.setPressed(z);
        }
        positionSelectorLikeTouchCompat(i, view, f, f2);
        setSelectorEnabled(z2);
        refreshDrawableState();
    }

    protected boolean touchModeDrawsInPressedStateCompat() {
        return this.mDrawsInPressedState || super.touchModeDrawsInPressedStateCompat();
    }

    public boolean isInTouchMode() {
        return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
    }

    public boolean hasWindowFocus() {
        return this.mHijackFocus || super.hasWindowFocus();
    }

    public boolean isFocused() {
        return this.mHijackFocus || super.isFocused();
    }

    public boolean hasFocus() {
        return this.mHijackFocus || super.hasFocus();
    }
}
