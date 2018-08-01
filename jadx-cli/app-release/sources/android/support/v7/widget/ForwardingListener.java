package android.support.v7.widget;

import android.os.SystemClock;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.view.menu.ShowableListMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewParent;

@RestrictTo({Scope.LIBRARY_GROUP})
public abstract class ForwardingListener implements OnTouchListener, OnAttachStateChangeListener {
    private int mActivePointerId;
    private Runnable mDisallowIntercept;
    private boolean mForwarding;
    private final int mLongPressTimeout;
    private final float mScaledTouchSlop;
    final View mSrc;
    private final int mTapTimeout = ViewConfiguration.getTapTimeout();
    private final int[] mTmpLocation;
    private Runnable mTriggerLongPress;

    private class DisallowIntercept implements Runnable {
        DisallowIntercept() {
        }

        public void run() {
            ViewParent parent = ForwardingListener.this.mSrc.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }

    private class TriggerLongPress implements Runnable {
        TriggerLongPress() {
        }

        public void run() {
            ForwardingListener.this.onLongPress();
        }
    }

    public abstract ShowableListMenu getPopup();

    public void onViewAttachedToWindow(View view) {
    }

    public ForwardingListener(View view) {
        int i = 2;
        this.mTmpLocation = new int[i];
        this.mSrc = view;
        view.setLongClickable(true);
        view.addOnAttachStateChangeListener(this);
        this.mScaledTouchSlop = (float) ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        this.mLongPressTimeout = (this.mTapTimeout + ViewConfiguration.getLongPressTimeout()) / i;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z;
        boolean z2 = this.mForwarding;
        boolean z3 = true;
        boolean z4 = false;
        if (z2) {
            z = (onTouchForwarded(motionEvent) || !onForwardingStopped()) ? z3 : z4;
        } else {
            z = (onTouchObserved(motionEvent) && onForwardingStarted()) ? z3 : z4;
            if (z) {
                long uptimeMillis = SystemClock.uptimeMillis();
                MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
                this.mSrc.onTouchEvent(obtain);
                obtain.recycle();
            }
        }
        this.mForwarding = z;
        return (z || z2) ? z3 : z4;
    }

    public void onViewDetachedFromWindow(View view) {
        this.mForwarding = false;
        this.mActivePointerId = -1;
        if (this.mDisallowIntercept != null) {
            this.mSrc.removeCallbacks(this.mDisallowIntercept);
        }
    }

    protected boolean onForwardingStarted() {
        ShowableListMenu popup = getPopup();
        if (!(popup == null || popup.isShowing())) {
            popup.show();
        }
        return true;
    }

    protected boolean onForwardingStopped() {
        ShowableListMenu popup = getPopup();
        if (popup != null && popup.isShowing()) {
            popup.dismiss();
        }
        return true;
    }

    private boolean onTouchObserved(MotionEvent motionEvent) {
        View view = this.mSrc;
        boolean z = false;
        if (!view.isEnabled()) {
            return z;
        }
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mActivePointerId = motionEvent.getPointerId(z);
                if (this.mDisallowIntercept == null) {
                    this.mDisallowIntercept = new DisallowIntercept();
                }
                view.postDelayed(this.mDisallowIntercept, (long) this.mTapTimeout);
                if (this.mTriggerLongPress == null) {
                    this.mTriggerLongPress = new TriggerLongPress();
                }
                view.postDelayed(this.mTriggerLongPress, (long) this.mLongPressTimeout);
                break;
            case 1:
            case 3:
                clearCallbacks();
                break;
            case 2:
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex >= 0 && !pointInView(view, motionEvent.getX(findPointerIndex), motionEvent.getY(findPointerIndex), this.mScaledTouchSlop)) {
                    clearCallbacks();
                    ViewParent parent = view.getParent();
                    boolean z2 = true;
                    parent.requestDisallowInterceptTouchEvent(z2);
                    return z2;
                }
        }
        return z;
    }

    private void clearCallbacks() {
        if (this.mTriggerLongPress != null) {
            this.mSrc.removeCallbacks(this.mTriggerLongPress);
        }
        if (this.mDisallowIntercept != null) {
            this.mSrc.removeCallbacks(this.mDisallowIntercept);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void onLongPress() {
        /*
        r11 = this;
        r11.clearCallbacks();
        r0 = r11.mSrc;
        r1 = r0.isEnabled();
        if (r1 == 0) goto L_0x0037;
    L_0x000b:
        r1 = r0.isLongClickable();
        if (r1 == 0) goto L_0x0012;
    L_0x0011:
        goto L_0x0037;
    L_0x0012:
        r1 = r11.onForwardingStarted();
        if (r1 != 0) goto L_0x0019;
    L_0x0018:
        return;
    L_0x0019:
        r1 = r0.getParent();
        r2 = 1;
        r1.requestDisallowInterceptTouchEvent(r2);
        r5 = android.os.SystemClock.uptimeMillis();
        r7 = 3;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r3 = r5;
        r1 = android.view.MotionEvent.obtain(r3, r5, r7, r8, r9, r10);
        r0.onTouchEvent(r1);
        r1.recycle();
        r11.mForwarding = r2;
        return;
    L_0x0037:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.ForwardingListener.onLongPress():void");
    }

    private boolean onTouchForwarded(MotionEvent motionEvent) {
        View view = this.mSrc;
        ShowableListMenu popup = getPopup();
        boolean z = false;
        if (popup == null || !popup.isShowing()) {
            return z;
        }
        DropDownListView dropDownListView = (DropDownListView) popup.getListView();
        if (dropDownListView == null || !dropDownListView.isShown()) {
            return z;
        }
        MotionEvent obtainNoHistory = MotionEvent.obtainNoHistory(motionEvent);
        toGlobalMotionEvent(view, obtainNoHistory);
        toLocalMotionEvent(dropDownListView, obtainNoHistory);
        boolean onForwardedEvent = dropDownListView.onForwardedEvent(obtainNoHistory, this.mActivePointerId);
        obtainNoHistory.recycle();
        boolean actionMasked = motionEvent.getActionMasked();
        boolean z2 = true;
        actionMasked = (actionMasked == z2 || actionMasked) ? z : z2;
        if (!(onForwardedEvent && actionMasked)) {
            z2 = z;
        }
        return z2;
    }

    private static boolean pointInView(View view, float f, float f2, float f3) {
        float f4 = -f3;
        return f >= f4 && f2 >= f4 && f < ((float) (view.getRight() - view.getLeft())) + f3 && f2 < ((float) (view.getBottom() - view.getTop())) + f3;
    }

    private boolean toLocalMotionEvent(View view, MotionEvent motionEvent) {
        int[] iArr = this.mTmpLocation;
        view.getLocationOnScreen(iArr);
        boolean z = true;
        motionEvent.offsetLocation((float) (-iArr[0]), (float) (-iArr[z]));
        return z;
    }

    private boolean toGlobalMotionEvent(View view, MotionEvent motionEvent) {
        int[] iArr = this.mTmpLocation;
        view.getLocationOnScreen(iArr);
        boolean z = true;
        motionEvent.offsetLocation((float) iArr[0], (float) iArr[z]);
        return z;
    }
}
