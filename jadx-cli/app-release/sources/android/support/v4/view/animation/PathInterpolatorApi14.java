package android.support.v4.view.animation;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.animation.Interpolator;

class PathInterpolatorApi14 implements Interpolator {
    private static final float PRECISION = 0.002f;
    private final float[] mX;
    private final float[] mY;

    PathInterpolatorApi14(Path path) {
        boolean z = false;
        PathMeasure pathMeasure = new PathMeasure(path, z);
        float length = pathMeasure.getLength();
        int i = 1;
        int i2 = ((int) (length / 0.002f)) + i;
        this.mX = new float[i2];
        this.mY = new float[i2];
        float[] fArr = new float[2];
        for (int i3 = z; i3 < i2; i3++) {
            pathMeasure.getPosTan((((float) i3) * length) / ((float) (i2 - 1)), fArr, null);
            this.mX[i3] = fArr[z];
            this.mY[i3] = fArr[i];
        }
    }

    PathInterpolatorApi14(float f, float f2) {
        this(createQuad(f, f2));
    }

    PathInterpolatorApi14(float f, float f2, float f3, float f4) {
        this(createCubic(f, f2, f3, f4));
    }

    public float getInterpolation(float f) {
        float f2 = 0.0f;
        if (f <= f2) {
            return f2;
        }
        float f3 = 1.0f;
        if (f >= f3) {
            return f3;
        }
        int i = 0;
        int i2 = 1;
        int length = this.mX.length - i2;
        while (length - i > i2) {
            int i3 = (i + length) / 2;
            if (f < this.mX[i3]) {
                length = i3;
            } else {
                i = i3;
            }
        }
        float f4 = this.mX[length] - this.mX[i];
        if (f4 == f2) {
            return this.mY[i];
        }
        f = (f - this.mX[i]) / f4;
        f2 = this.mY[i];
        return f2 + (f * (this.mY[length] - f2));
    }

    private static Path createQuad(float f, float f2) {
        Path path = new Path();
        float f3 = 0.0f;
        path.moveTo(f3, f3);
        f3 = 1.0f;
        path.quadTo(f, f2, f3, f3);
        return path;
    }

    private static Path createCubic(float f, float f2, float f3, float f4) {
        Path path = new Path();
        float f5 = 0.0f;
        path.moveTo(f5, f5);
        path.cubicTo(f, f2, f3, f4, 1.0f, 1.0f);
        return path;
    }
}
