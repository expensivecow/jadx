package android.support.v4.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

final class SwipeProgressBar {
    private static final int ANIMATION_DURATION_MS = 2000;
    private static final int COLOR1 = -1291845632;
    private static final int COLOR2 = Integer.MIN_VALUE;
    private static final int COLOR3 = 1291845632;
    private static final int COLOR4 = 436207616;
    private static final int FINISH_ANIMATION_DURATION_MS = 1000;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private Rect mBounds = new Rect();
    private final RectF mClipRect = new RectF();
    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColor4;
    private long mFinishTime;
    private final Paint mPaint = new Paint();
    private View mParent;
    private boolean mRunning;
    private long mStartTime;
    private float mTriggerPercentage;

    SwipeProgressBar(View view) {
        this.mParent = view;
        this.mColor1 = -1291845632;
        this.mColor2 = Integer.MIN_VALUE;
        this.mColor3 = 1291845632;
        this.mColor4 = 436207616;
    }

    void setColorScheme(int i, int i2, int i3, int i4) {
        this.mColor1 = i;
        this.mColor2 = i2;
        this.mColor3 = i3;
        this.mColor4 = i4;
    }

    void setTriggerPercentage(float f) {
        this.mTriggerPercentage = f;
        this.mStartTime = 0;
        ViewCompat.postInvalidateOnAnimation(this.mParent, this.mBounds.left, this.mBounds.top, this.mBounds.right, this.mBounds.bottom);
    }

    void start() {
        if (!this.mRunning) {
            this.mTriggerPercentage = 0.0f;
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mRunning = true;
            this.mParent.postInvalidate();
        }
    }

    void stop() {
        if (this.mRunning) {
            this.mTriggerPercentage = 0.0f;
            this.mFinishTime = AnimationUtils.currentAnimationTimeMillis();
            this.mRunning = false;
            this.mParent.postInvalidate();
        }
    }

    boolean isRunning() {
        return this.mRunning || this.mFinishTime > 0;
    }

    void draw(Canvas canvas) {
        Canvas canvas2 = canvas;
        int width = this.mBounds.width();
        int height = this.mBounds.height();
        int i = width / 2;
        int i2 = height / 2;
        int save = canvas.save();
        canvas2.clipRect(this.mBounds);
        long j = 0;
        float f = 0.0f;
        if (this.mRunning || r6.mFinishTime > j) {
            Canvas canvas3;
            long currentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis();
            long j2 = 2000;
            long j3 = (currentAnimationTimeMillis - r6.mStartTime) / j2;
            float f2 = ((float) ((currentAnimationTimeMillis - r6.mStartTime) % j2)) / 20.0f;
            float f3 = 100.0f;
            Object obj;
            if (r6.mRunning) {
                obj = null;
            } else {
                long j4 = 1000;
                if (currentAnimationTimeMillis - r6.mFinishTime >= j4) {
                    r6.mFinishTime = 0;
                    return;
                }
                float f4 = (float) i;
                float interpolation = INTERPOLATOR.getInterpolation((((float) ((currentAnimationTimeMillis - r6.mFinishTime) % j4)) / 10.0f) / f3) * f4;
                r6.mClipRect.set(f4 - interpolation, f, f4 + interpolation, (float) height);
                height = 0;
                canvas2.saveLayerAlpha(r6.mClipRect, height, height);
                obj = 1;
            }
            float f5 = 75.0f;
            float f6 = 50.0f;
            float f7 = 25.0f;
            if (j3 == 0) {
                canvas2.drawColor(r6.mColor1);
            } else if (f2 >= f && f2 < f7) {
                canvas2.drawColor(r6.mColor4);
            } else if (f2 >= f7 && f2 < f6) {
                canvas2.drawColor(r6.mColor1);
            } else if (f2 < f6 || f2 >= f5) {
                canvas2.drawColor(r6.mColor3);
            } else {
                canvas2.drawColor(r6.mColor2);
            }
            float f8 = 2.0f;
            if (f2 >= f && f2 <= f7) {
                canvas3 = canvas2;
                drawCircle(canvas3, (float) i, (float) i2, r6.mColor1, ((f2 + f7) * f8) / f3);
            }
            if (f2 >= f && f2 <= f6) {
                canvas3 = canvas2;
                drawCircle(canvas3, (float) i, (float) i2, r6.mColor2, (f2 * f8) / f3);
            }
            if (f2 >= f7 && f2 <= f5) {
                canvas3 = canvas2;
                drawCircle(canvas3, (float) i, (float) i2, r6.mColor3, ((f2 - f7) * f8) / f3);
            }
            if (f2 >= f6 && f2 <= f3) {
                canvas3 = canvas2;
                drawCircle(canvas3, (float) i, (float) i2, r6.mColor4, ((f2 - f6) * f8) / f3);
            }
            if (f2 >= f5 && f2 <= f3) {
                canvas3 = canvas2;
                drawCircle(canvas3, (float) i, (float) i2, r6.mColor1, ((f2 - f5) * f8) / f3);
            }
            if (r6.mTriggerPercentage > f && obj != null) {
                canvas2.restoreToCount(save);
                width = canvas.save();
                canvas2.clipRect(r6.mBounds);
                drawTrigger(canvas2, i, i2);
                save = width;
            }
            ViewCompat.postInvalidateOnAnimation(r6.mParent, r6.mBounds.left, r6.mBounds.top, r6.mBounds.right, r6.mBounds.bottom);
        } else if (r6.mTriggerPercentage > f && ((double) r6.mTriggerPercentage) <= 1.0d) {
            drawTrigger(canvas2, i, i2);
        }
        canvas2.restoreToCount(save);
    }

    private void drawTrigger(Canvas canvas, int i, int i2) {
        this.mPaint.setColor(this.mColor1);
        float f = (float) i;
        canvas.drawCircle(f, (float) i2, this.mTriggerPercentage * f, this.mPaint);
    }

    private void drawCircle(Canvas canvas, float f, float f2, int i, float f3) {
        this.mPaint.setColor(i);
        canvas.save();
        canvas.translate(f, f2);
        f2 = INTERPOLATOR.getInterpolation(f3);
        canvas.scale(f2, f2);
        float f4 = 0.0f;
        canvas.drawCircle(f4, f4, f, this.mPaint);
        canvas.restore();
    }

    void setBounds(int i, int i2, int i3, int i4) {
        this.mBounds.left = i;
        this.mBounds.top = i2;
        this.mBounds.right = i3;
        this.mBounds.bottom = i4;
    }
}
