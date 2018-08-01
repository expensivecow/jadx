package android.support.v4.widget;

import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.view.ViewCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

public abstract class AutoScrollHelper implements OnTouchListener {
    private static final int DEFAULT_ACTIVATION_DELAY = ViewConfiguration.getTapTimeout();
    private static final int DEFAULT_EDGE_TYPE = 1;
    private static final float DEFAULT_MAXIMUM_EDGE = Float.MAX_VALUE;
    private static final int DEFAULT_MAXIMUM_VELOCITY_DIPS = 1575;
    private static final int DEFAULT_MINIMUM_VELOCITY_DIPS = 315;
    private static final int DEFAULT_RAMP_DOWN_DURATION = 500;
    private static final int DEFAULT_RAMP_UP_DURATION = 500;
    private static final float DEFAULT_RELATIVE_EDGE = 0.2f;
    private static final float DEFAULT_RELATIVE_VELOCITY = 1.0f;
    public static final int EDGE_TYPE_INSIDE = 0;
    public static final int EDGE_TYPE_INSIDE_EXTEND = 1;
    public static final int EDGE_TYPE_OUTSIDE = 2;
    private static final int HORIZONTAL = 0;
    public static final float NO_MAX = Float.MAX_VALUE;
    public static final float NO_MIN = 0.0f;
    public static final float RELATIVE_UNSPECIFIED = 0.0f;
    private static final int VERTICAL = 1;
    private int mActivationDelay;
    private boolean mAlreadyDelayed;
    boolean mAnimating;
    private final Interpolator mEdgeInterpolator = new AccelerateInterpolator();
    private int mEdgeType;
    private boolean mEnabled;
    private boolean mExclusive;
    private float[] mMaximumEdges;
    private float[] mMaximumVelocity;
    private float[] mMinimumVelocity;
    boolean mNeedsCancel;
    boolean mNeedsReset;
    private float[] mRelativeEdges;
    private float[] mRelativeVelocity;
    private Runnable mRunnable;
    final ClampedScroller mScroller = new ClampedScroller();
    final View mTarget;

    private static class ClampedScroller {
        private long mDeltaTime = 0;
        private int mDeltaX;
        private int mDeltaY;
        private int mEffectiveRampDown;
        private int mRampDownDuration;
        private int mRampUpDuration;
        private long mStartTime = Long.MIN_VALUE;
        private long mStopTime = -1;
        private float mStopValue;
        private float mTargetVelocityX;
        private float mTargetVelocityY;

        private float interpolateValue(float f) {
            return ((-4.0f * f) * f) + (4.0f * f);
        }

        ClampedScroller() {
            int i = 0;
            this.mDeltaX = i;
            this.mDeltaY = i;
        }

        public void setRampUpDuration(int i) {
            this.mRampUpDuration = i;
        }

        public void setRampDownDuration(int i) {
            this.mRampDownDuration = i;
        }

        public void start() {
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mStopTime = -1;
            this.mDeltaTime = this.mStartTime;
            this.mStopValue = 0.5f;
            int i = 0;
            this.mDeltaX = i;
            this.mDeltaY = i;
        }

        public void requestStop() {
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            this.mEffectiveRampDown = AutoScrollHelper.constrain((int) (currentAnimationTimeMillis - this.mStartTime), 0, this.mRampDownDuration);
            this.mStopValue = getValueAt(currentAnimationTimeMillis);
            this.mStopTime = currentAnimationTimeMillis;
        }

        public boolean isFinished() {
            return this.mStopTime > 0 && AnimationUtils.currentAnimationTimeMillis() > this.mStopTime + ((long) this.mEffectiveRampDown);
        }

        private float getValueAt(long j) {
            float f = 0.0f;
            if (j < this.mStartTime) {
                return f;
            }
            float f2 = 1.0f;
            if (this.mStopTime < 0 || j < this.mStopTime) {
                return 0.5f * AutoScrollHelper.constrain(((float) (j - this.mStartTime)) / ((float) this.mRampUpDuration), f, f2);
            }
            return (f2 - this.mStopValue) + (this.mStopValue * AutoScrollHelper.constrain(((float) (j - this.mStopTime)) / ((float) this.mEffectiveRampDown), f, f2));
        }

        public void computeScrollDelta() {
            if (this.mDeltaTime == 0) {
                throw new RuntimeException("Cannot compute scroll delta before calling start()");
            }
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            float interpolateValue = interpolateValue(getValueAt(currentAnimationTimeMillis));
            long j = currentAnimationTimeMillis - this.mDeltaTime;
            this.mDeltaTime = currentAnimationTimeMillis;
            float f = ((float) j) * interpolateValue;
            this.mDeltaX = (int) (this.mTargetVelocityX * f);
            this.mDeltaY = (int) (f * this.mTargetVelocityY);
        }

        public void setTargetVelocity(float f, float f2) {
            this.mTargetVelocityX = f;
            this.mTargetVelocityY = f2;
        }

        public int getHorizontalDirection() {
            return (int) (this.mTargetVelocityX / Math.abs(this.mTargetVelocityX));
        }

        public int getVerticalDirection() {
            return (int) (this.mTargetVelocityY / Math.abs(this.mTargetVelocityY));
        }

        public int getDeltaX() {
            return this.mDeltaX;
        }

        public int getDeltaY() {
            return this.mDeltaY;
        }
    }

    private class ScrollAnimationRunnable implements Runnable {
        ScrollAnimationRunnable() {
        }

        public void run() {
            if (AutoScrollHelper.this.mAnimating) {
                boolean z = false;
                if (AutoScrollHelper.this.mNeedsReset) {
                    AutoScrollHelper.this.mNeedsReset = z;
                    AutoScrollHelper.this.mScroller.start();
                }
                ClampedScroller clampedScroller = AutoScrollHelper.this.mScroller;
                if (clampedScroller.isFinished() || !AutoScrollHelper.this.shouldAnimate()) {
                    AutoScrollHelper.this.mAnimating = z;
                    return;
                }
                if (AutoScrollHelper.this.mNeedsCancel) {
                    AutoScrollHelper.this.mNeedsCancel = z;
                    AutoScrollHelper.this.cancelTargetTouch();
                }
                clampedScroller.computeScrollDelta();
                AutoScrollHelper.this.scrollTargetBy(clampedScroller.getDeltaX(), clampedScroller.getDeltaY());
                ViewCompat.postOnAnimation(AutoScrollHelper.this.mTarget, this);
            }
        }
    }

    static float constrain(float f, float f2, float f3) {
        return f > f3 ? f3 : f < f2 ? f2 : f;
    }

    static int constrain(int i, int i2, int i3) {
        return i > i3 ? i3 : i < i2 ? i2 : i;
    }

    public abstract boolean canTargetScrollHorizontally(int i);

    public abstract boolean canTargetScrollVertically(int i);

    public abstract void scrollTargetBy(int i, int i2);

    public AutoScrollHelper(View view) {
        int i = 2;
        this.mRelativeEdges = new float[]{0.0f, 0.0f};
        this.mMaximumEdges = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        this.mRelativeVelocity = new float[]{0.0f, 0.0f};
        this.mMinimumVelocity = new float[]{0.0f, 0.0f};
        this.mMaximumVelocity = new float[]{Float.MAX_VALUE, Float.MAX_VALUE};
        this.mTarget = view;
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        float f = 0.5f;
        int i2 = (int) ((315.0f * displayMetrics.density) + f);
        f = (float) ((int) ((1575.0f * displayMetrics.density) + f));
        setMaximumVelocity(f, f);
        float f2 = (float) i2;
        setMinimumVelocity(f2, f2);
        setEdgeType(1);
        f2 = Float.MAX_VALUE;
        setMaximumEdges(f2, f2);
        f2 = 0.2f;
        setRelativeEdges(f2, f2);
        f2 = 1.0f;
        setRelativeVelocity(f2, f2);
        setActivationDelay(DEFAULT_ACTIVATION_DELAY);
        i2 = 500;
        setRampUpDuration(i2);
        setRampDownDuration(i2);
    }

    public AutoScrollHelper setEnabled(boolean z) {
        if (this.mEnabled && !z) {
            requestStop();
        }
        this.mEnabled = z;
        return this;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public AutoScrollHelper setExclusive(boolean z) {
        this.mExclusive = z;
        return this;
    }

    public boolean isExclusive() {
        return this.mExclusive;
    }

    public AutoScrollHelper setMaximumVelocity(float f, float f2) {
        float f3 = 1000.0f;
        this.mMaximumVelocity[0] = f / f3;
        this.mMaximumVelocity[1] = f2 / f3;
        return this;
    }

    public AutoScrollHelper setMinimumVelocity(float f, float f2) {
        float f3 = 1000.0f;
        this.mMinimumVelocity[0] = f / f3;
        this.mMinimumVelocity[1] = f2 / f3;
        return this;
    }

    public AutoScrollHelper setRelativeVelocity(float f, float f2) {
        float f3 = 1000.0f;
        this.mRelativeVelocity[0] = f / f3;
        this.mRelativeVelocity[1] = f2 / f3;
        return this;
    }

    public AutoScrollHelper setEdgeType(int i) {
        this.mEdgeType = i;
        return this;
    }

    public AutoScrollHelper setRelativeEdges(float f, float f2) {
        this.mRelativeEdges[0] = f;
        this.mRelativeEdges[1] = f2;
        return this;
    }

    public AutoScrollHelper setMaximumEdges(float f, float f2) {
        this.mMaximumEdges[0] = f;
        this.mMaximumEdges[1] = f2;
        return this;
    }

    public AutoScrollHelper setActivationDelay(int i) {
        this.mActivationDelay = i;
        return this;
    }

    public AutoScrollHelper setRampUpDuration(int i) {
        this.mScroller.setRampUpDuration(i);
        return this;
    }

    public AutoScrollHelper setRampDownDuration(int i) {
        this.mScroller.setRampDownDuration(i);
        return this;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        boolean z = false;
        if (!this.mEnabled) {
            return z;
        }
        boolean z2 = true;
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mNeedsCancel = z2;
                this.mAlreadyDelayed = z;
                break;
            case 1:
            case 3:
                requestStop();
                break;
            case 2:
                break;
        }
        this.mScroller.setTargetVelocity(computeTargetVelocity(z, motionEvent.getX(), (float) view.getWidth(), (float) this.mTarget.getWidth()), computeTargetVelocity(z2, motionEvent.getY(), (float) view.getHeight(), (float) this.mTarget.getHeight()));
        if (!this.mAnimating && shouldAnimate()) {
            startAnimating();
        }
        if (this.mExclusive && this.mAnimating) {
            z = z2;
        }
        return z;
    }

    boolean shouldAnimate() {
        ClampedScroller clampedScroller = this.mScroller;
        int verticalDirection = clampedScroller.getVerticalDirection();
        int horizontalDirection = clampedScroller.getHorizontalDirection();
        return (verticalDirection != 0 && canTargetScrollVertically(verticalDirection)) || (horizontalDirection != 0 && canTargetScrollHorizontally(horizontalDirection));
    }

    private void startAnimating() {
        if (this.mRunnable == null) {
            this.mRunnable = new ScrollAnimationRunnable();
        }
        boolean z = true;
        this.mAnimating = z;
        this.mNeedsReset = z;
        if (this.mAlreadyDelayed || this.mActivationDelay <= 0) {
            this.mRunnable.run();
        } else {
            ViewCompat.postOnAnimationDelayed(this.mTarget, this.mRunnable, (long) this.mActivationDelay);
        }
        this.mAlreadyDelayed = z;
    }

    private void requestStop() {
        if (this.mNeedsReset) {
            this.mAnimating = false;
        } else {
            this.mScroller.requestStop();
        }
    }

    private float computeTargetVelocity(int i, float f, float f2, float f3) {
        f = getEdgeValue(this.mRelativeEdges[i], f2, this.mMaximumEdges[i], f);
        f2 = 0.0f;
        if (f == f2) {
            return f2;
        }
        float f4 = this.mRelativeVelocity[i];
        float f5 = this.mMinimumVelocity[i];
        float f6 = this.mMaximumVelocity[i];
        f4 *= f3;
        if (f > f2) {
            return constrain(f * f4, f5, f6);
        }
        return -constrain((-f) * f4, f5, f6);
    }

    private float getEdgeValue(float f, float f2, float f3, float f4) {
        float f5 = 0.0f;
        f = constrain(f * f2, f5, f3);
        f = constrainEdgeValue(f2 - f4, f) - constrainEdgeValue(f4, f);
        if (f < f5) {
            f = -this.mEdgeInterpolator.getInterpolation(-f);
        } else if (f <= f5) {
            return f5;
        } else {
            f = this.mEdgeInterpolator.getInterpolation(f);
        }
        return constrain(f, -1.0f, 1.0f);
    }

    private float constrainEdgeValue(float f, float f2) {
        float f3 = 0.0f;
        if (f2 == f3) {
            return f3;
        }
        switch (this.mEdgeType) {
            case 0:
            case 1:
                if (f < f2) {
                    float f4 = 1.0f;
                    if (f >= f3) {
                        return f4 - (f / f2);
                    }
                    return (this.mAnimating && this.mEdgeType == 1) ? f4 : f3;
                }
                break;
            case 2:
                if (f < f3) {
                    return f / (-f2);
                }
                break;
        }
    }

    void cancelTargetTouch() {
        long uptimeMillis = SystemClock.uptimeMillis();
        MotionEvent obtain = MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0);
        this.mTarget.onTouchEvent(obtain);
        obtain.recycle();
    }
}
