package android.support.v4.view.animation;

import android.view.animation.Interpolator;

abstract class LookupTableInterpolator implements Interpolator {
    private final float mStepSize = (1.0f / ((float) (this.mValues.length - 1)));
    private final float[] mValues;

    public LookupTableInterpolator(float[] fArr) {
        this.mValues = fArr;
    }

    public float getInterpolation(float f) {
        float f2 = 1.0f;
        if (f >= f2) {
            return f2;
        }
        f2 = 0.0f;
        if (f <= f2) {
            return f2;
        }
        int min = Math.min((int) (((float) (this.mValues.length - 1)) * f), this.mValues.length - 2);
        return this.mValues[min] + (((f - (((float) min) * this.mStepSize)) / this.mStepSize) * (this.mValues[min + 1] - this.mValues[min]));
    }
}
