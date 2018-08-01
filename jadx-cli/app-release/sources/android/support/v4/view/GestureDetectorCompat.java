package android.support.v4.view;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public final class GestureDetectorCompat {
    private final GestureDetectorCompatImpl mImpl;

    interface GestureDetectorCompatImpl {
        boolean isLongpressEnabled();

        boolean onTouchEvent(MotionEvent motionEvent);

        void setIsLongpressEnabled(boolean z);

        void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener);
    }

    static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl {
        private static final int DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
        private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
        private static final int LONG_PRESS = 2;
        private static final int SHOW_PRESS = 1;
        private static final int TAP = 3;
        private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
        private boolean mAlwaysInBiggerTapRegion;
        private boolean mAlwaysInTapRegion;
        MotionEvent mCurrentDownEvent;
        boolean mDeferConfirmSingleTap;
        OnDoubleTapListener mDoubleTapListener;
        private int mDoubleTapSlopSquare;
        private float mDownFocusX;
        private float mDownFocusY;
        private final Handler mHandler;
        private boolean mInLongPress;
        private boolean mIsDoubleTapping;
        private boolean mIsLongpressEnabled;
        private float mLastFocusX;
        private float mLastFocusY;
        final OnGestureListener mListener;
        private int mMaximumFlingVelocity;
        private int mMinimumFlingVelocity;
        private MotionEvent mPreviousUpEvent;
        boolean mStillDown;
        private int mTouchSlopSquare;
        private VelocityTracker mVelocityTracker;

        private class GestureHandler extends Handler {
            GestureHandler() {
            }

            GestureHandler(Handler handler) {
                super(handler.getLooper());
            }

            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:
                        GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        return;
                    case 2:
                        GestureDetectorCompatImplBase.this.dispatchLongPress();
                        return;
                    case 3:
                        if (GestureDetectorCompatImplBase.this.mDoubleTapListener == null) {
                            return;
                        }
                        if (GestureDetectorCompatImplBase.this.mStillDown) {
                            GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
                            return;
                        }
                        GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                        return;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown message ");
                        stringBuilder.append(message);
                        throw new RuntimeException(stringBuilder.toString());
                }
            }
        }

        public GestureDetectorCompatImplBase(Context context, OnGestureListener onGestureListener, Handler handler) {
            if (handler != null) {
                this.mHandler = new GestureHandler(handler);
            } else {
                this.mHandler = new GestureHandler();
            }
            this.mListener = onGestureListener;
            if (onGestureListener instanceof OnDoubleTapListener) {
                setOnDoubleTapListener((OnDoubleTapListener) onGestureListener);
            }
            init(context);
        }

        private void init(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            } else if (this.mListener == null) {
                throw new IllegalArgumentException("OnGestureListener must not be null");
            } else {
                this.mIsLongpressEnabled = true;
                ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
                int scaledTouchSlop = viewConfiguration.getScaledTouchSlop();
                int scaledDoubleTapSlop = viewConfiguration.getScaledDoubleTapSlop();
                this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
                this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
                this.mTouchSlopSquare = scaledTouchSlop * scaledTouchSlop;
                this.mDoubleTapSlopSquare = scaledDoubleTapSlop * scaledDoubleTapSlop;
            }
        }

        public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
            this.mDoubleTapListener = onDoubleTapListener;
        }

        public void setIsLongpressEnabled(boolean z) {
            this.mIsLongpressEnabled = z;
        }

        public boolean isLongpressEnabled() {
            return this.mIsLongpressEnabled;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int i;
            boolean onDoubleTapEvent;
            int action = motionEvent.getAction();
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            action &= 255;
            boolean z = true;
            boolean z2 = false;
            boolean z3 = action == 6 ? z : z2;
            int actionIndex = z3 ? motionEvent.getActionIndex() : -1;
            int pointerCount = motionEvent.getPointerCount();
            float f = 0.0f;
            float f2 = f;
            float f3 = f2;
            for (i = z2; i < pointerCount; i++) {
                if (actionIndex != i) {
                    f2 += motionEvent.getX(i);
                    f3 += motionEvent.getY(i);
                }
            }
            float f4 = (float) (z3 ? pointerCount - 1 : pointerCount);
            f2 /= f4;
            f3 /= f4;
            int i2 = 1000;
            actionIndex = 2;
            i = 3;
            int pointerId;
            switch (action) {
                case 0:
                    if (this.mDoubleTapListener != null) {
                        boolean hasMessages = this.mHandler.hasMessages(i);
                        if (hasMessages) {
                            this.mHandler.removeMessages(i);
                        }
                        if (this.mCurrentDownEvent == null || this.mPreviousUpEvent == null || !hasMessages || !isConsideredDoubleTap(this.mCurrentDownEvent, this.mPreviousUpEvent, motionEvent)) {
                            this.mHandler.sendEmptyMessageDelayed(i, (long) DOUBLE_TAP_TIMEOUT);
                        } else {
                            this.mIsDoubleTapping = z;
                            action = (this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | z2) | this.mDoubleTapListener.onDoubleTapEvent(motionEvent);
                            this.mLastFocusX = f2;
                            this.mDownFocusX = f2;
                            this.mLastFocusY = f3;
                            this.mDownFocusY = f3;
                            if (this.mCurrentDownEvent != null) {
                                this.mCurrentDownEvent.recycle();
                            }
                            this.mCurrentDownEvent = MotionEvent.obtain(motionEvent);
                            this.mAlwaysInTapRegion = z;
                            this.mAlwaysInBiggerTapRegion = z;
                            this.mStillDown = z;
                            this.mInLongPress = z2;
                            this.mDeferConfirmSingleTap = z2;
                            if (this.mIsLongpressEnabled) {
                                this.mHandler.removeMessages(actionIndex);
                                this.mHandler.sendEmptyMessageAtTime(actionIndex, (this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT)) + ((long) LONGPRESS_TIMEOUT));
                            }
                            this.mHandler.sendEmptyMessageAtTime(z, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT));
                            return action | this.mListener.onDown(motionEvent);
                        }
                    }
                    action = z2;
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                    if (this.mCurrentDownEvent != null) {
                        this.mCurrentDownEvent.recycle();
                    }
                    this.mCurrentDownEvent = MotionEvent.obtain(motionEvent);
                    this.mAlwaysInTapRegion = z;
                    this.mAlwaysInBiggerTapRegion = z;
                    this.mStillDown = z;
                    this.mInLongPress = z2;
                    this.mDeferConfirmSingleTap = z2;
                    if (this.mIsLongpressEnabled) {
                        this.mHandler.removeMessages(actionIndex);
                        this.mHandler.sendEmptyMessageAtTime(actionIndex, (this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT)) + ((long) LONGPRESS_TIMEOUT));
                    }
                    this.mHandler.sendEmptyMessageAtTime(z, this.mCurrentDownEvent.getDownTime() + ((long) TAP_TIMEOUT));
                    return action | this.mListener.onDown(motionEvent);
                case 1:
                    this.mStillDown = z2;
                    MotionEvent obtain = MotionEvent.obtain(motionEvent);
                    if (this.mIsDoubleTapping) {
                        onDoubleTapEvent = this.mDoubleTapListener.onDoubleTapEvent(motionEvent) | z2;
                    } else {
                        if (this.mInLongPress) {
                            this.mHandler.removeMessages(i);
                            this.mInLongPress = z2;
                        } else if (this.mAlwaysInTapRegion) {
                            z3 = this.mListener.onSingleTapUp(motionEvent);
                            if (this.mDeferConfirmSingleTap && this.mDoubleTapListener != null) {
                                this.mDoubleTapListener.onSingleTapConfirmed(motionEvent);
                            }
                            onDoubleTapEvent = z3;
                        } else {
                            VelocityTracker velocityTracker = this.mVelocityTracker;
                            pointerId = motionEvent.getPointerId(z2);
                            velocityTracker.computeCurrentVelocity(i2, (float) this.mMaximumFlingVelocity);
                            f4 = velocityTracker.getYVelocity(pointerId);
                            float xVelocity = velocityTracker.getXVelocity(pointerId);
                            if (Math.abs(f4) > ((float) this.mMinimumFlingVelocity) || Math.abs(xVelocity) > ((float) this.mMinimumFlingVelocity)) {
                                onDoubleTapEvent = this.mListener.onFling(this.mCurrentDownEvent, motionEvent, xVelocity, f4);
                            }
                        }
                        onDoubleTapEvent = z2;
                    }
                    if (this.mPreviousUpEvent != null) {
                        this.mPreviousUpEvent.recycle();
                    }
                    this.mPreviousUpEvent = obtain;
                    if (this.mVelocityTracker != null) {
                        this.mVelocityTracker.recycle();
                        this.mVelocityTracker = null;
                    }
                    this.mIsDoubleTapping = z2;
                    this.mDeferConfirmSingleTap = z2;
                    this.mHandler.removeMessages(z);
                    this.mHandler.removeMessages(actionIndex);
                    break;
                case 2:
                    if (this.mInLongPress) {
                        return z2;
                    }
                    float f5 = this.mLastFocusX - f2;
                    f4 = this.mLastFocusY - f3;
                    if (this.mIsDoubleTapping) {
                        return z2 | this.mDoubleTapListener.onDoubleTapEvent(motionEvent);
                    }
                    if (this.mAlwaysInTapRegion) {
                        pointerCount = (int) (f2 - this.mDownFocusX);
                        pointerId = (int) (f3 - this.mDownFocusY);
                        pointerCount = (pointerCount * pointerCount) + (pointerId * pointerId);
                        if (pointerCount > this.mTouchSlopSquare) {
                            onDoubleTapEvent = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, f5, f4);
                            this.mLastFocusX = f2;
                            this.mLastFocusY = f3;
                            this.mAlwaysInTapRegion = z2;
                            this.mHandler.removeMessages(i);
                            this.mHandler.removeMessages(z);
                            this.mHandler.removeMessages(actionIndex);
                        } else {
                            onDoubleTapEvent = z2;
                        }
                        if (pointerCount > this.mTouchSlopSquare) {
                            this.mAlwaysInBiggerTapRegion = z2;
                            break;
                        }
                    }
                    float f6 = 1.0f;
                    if (Math.abs(f5) < f6 && Math.abs(f4) < f6) {
                        return z2;
                    }
                    z2 = this.mListener.onScroll(this.mCurrentDownEvent, motionEvent, f5, f4);
                    this.mLastFocusX = f2;
                    this.mLastFocusY = f3;
                    return z2;
                    break;
                case 3:
                    cancel();
                    return z2;
                case 5:
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                    cancelTaps();
                    return z2;
                case 6:
                    this.mLastFocusX = f2;
                    this.mDownFocusX = f2;
                    this.mLastFocusY = f3;
                    this.mDownFocusY = f3;
                    this.mVelocityTracker.computeCurrentVelocity(i2, (float) this.mMaximumFlingVelocity);
                    action = motionEvent.getActionIndex();
                    i2 = motionEvent.getPointerId(action);
                    float xVelocity2 = this.mVelocityTracker.getXVelocity(i2);
                    f4 = this.mVelocityTracker.getYVelocity(i2);
                    for (actionIndex = z2; actionIndex < pointerCount; actionIndex++) {
                        if (actionIndex != action) {
                            i = motionEvent.getPointerId(actionIndex);
                            if ((this.mVelocityTracker.getXVelocity(i) * xVelocity2) + (this.mVelocityTracker.getYVelocity(i) * f4) < f) {
                                this.mVelocityTracker.clear();
                                return z2;
                            }
                        }
                    }
                    return z2;
                default:
                    return z2;
            }
            return onDoubleTapEvent;
        }

        private void cancel() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            boolean z = false;
            this.mIsDoubleTapping = z;
            this.mStillDown = z;
            this.mAlwaysInTapRegion = z;
            this.mAlwaysInBiggerTapRegion = z;
            this.mDeferConfirmSingleTap = z;
            if (this.mInLongPress) {
                this.mInLongPress = z;
            }
        }

        private void cancelTaps() {
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(2);
            this.mHandler.removeMessages(3);
            boolean z = false;
            this.mIsDoubleTapping = z;
            this.mAlwaysInTapRegion = z;
            this.mAlwaysInBiggerTapRegion = z;
            this.mDeferConfirmSingleTap = z;
            if (this.mInLongPress) {
                this.mInLongPress = z;
            }
        }

        private boolean isConsideredDoubleTap(MotionEvent motionEvent, MotionEvent motionEvent2, MotionEvent motionEvent3) {
            boolean z = false;
            if (!this.mAlwaysInBiggerTapRegion || motionEvent3.getEventTime() - motionEvent2.getEventTime() > ((long) DOUBLE_TAP_TIMEOUT)) {
                return z;
            }
            int x = ((int) motionEvent.getX()) - ((int) motionEvent3.getX());
            int y = ((int) motionEvent.getY()) - ((int) motionEvent3.getY());
            if ((x * x) + (y * y) < this.mDoubleTapSlopSquare) {
                z = true;
            }
            return z;
        }

        void dispatchLongPress() {
            this.mHandler.removeMessages(3);
            this.mDeferConfirmSingleTap = false;
            this.mInLongPress = true;
            this.mListener.onLongPress(this.mCurrentDownEvent);
        }
    }

    static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl {
        private final GestureDetector mDetector;

        public GestureDetectorCompatImplJellybeanMr2(Context context, OnGestureListener onGestureListener, Handler handler) {
            this.mDetector = new GestureDetector(context, onGestureListener, handler);
        }

        public boolean isLongpressEnabled() {
            return this.mDetector.isLongpressEnabled();
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.mDetector.onTouchEvent(motionEvent);
        }

        public void setIsLongpressEnabled(boolean z) {
            this.mDetector.setIsLongpressEnabled(z);
        }

        public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
            this.mDetector.setOnDoubleTapListener(onDoubleTapListener);
        }
    }

    public GestureDetectorCompat(Context context, OnGestureListener onGestureListener) {
        this(context, onGestureListener, null);
    }

    public GestureDetectorCompat(Context context, OnGestureListener onGestureListener, Handler handler) {
        if (VERSION.SDK_INT > 17) {
            this.mImpl = new GestureDetectorCompatImplJellybeanMr2(context, onGestureListener, handler);
        } else {
            this.mImpl = new GestureDetectorCompatImplBase(context, onGestureListener, handler);
        }
    }

    public boolean isLongpressEnabled() {
        return this.mImpl.isLongpressEnabled();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mImpl.onTouchEvent(motionEvent);
    }

    public void setIsLongpressEnabled(boolean z) {
        this.mImpl.setIsLongpressEnabled(z);
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mImpl.setOnDoubleTapListener(onDoubleTapListener);
    }
}
