package android.support.graphics.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.animation.Interpolator;
import org.xmlpull.v1.XmlPullParser;

@RestrictTo({Scope.LIBRARY_GROUP})
public class PathInterpolatorCompat implements Interpolator {
    public static final double EPSILON = 1.0E-5d;
    public static final int MAX_NUM_POINTS = 3000;
    private static final float PRECISION = 0.002f;
    private float[] mX;
    private float[] mY;

    public PathInterpolatorCompat(Context context, AttributeSet attributeSet, XmlPullParser xmlPullParser) {
        this(context.getResources(), context.getTheme(), attributeSet, xmlPullParser);
    }

    public PathInterpolatorCompat(Resources resources, Theme theme, AttributeSet attributeSet, XmlPullParser xmlPullParser) {
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_PATH_INTERPOLATOR);
        parseInterpolatorFromTypeArray(obtainAttributes, xmlPullParser);
        obtainAttributes.recycle();
    }

    private void parseInterpolatorFromTypeArray(TypedArray typedArray, XmlPullParser xmlPullParser) {
        if (TypedArrayUtils.hasAttribute(xmlPullParser, "pathData")) {
            String namedString = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "pathData", 4);
            Path createPathFromPathData = PathParser.createPathFromPathData(namedString);
            if (createPathFromPathData == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("The path is null, which is created from ");
                stringBuilder.append(namedString);
                throw new InflateException(stringBuilder.toString());
            }
            initPath(createPathFromPathData);
        } else if (!TypedArrayUtils.hasAttribute(xmlPullParser, "controlX1")) {
            throw new InflateException("pathInterpolator requires the controlX1 attribute");
        } else if (TypedArrayUtils.hasAttribute(xmlPullParser, "controlY1")) {
            float f = 0.0f;
            float namedFloat = TypedArrayUtils.getNamedFloat(typedArray, xmlPullParser, "controlX1", 0, f);
            float namedFloat2 = TypedArrayUtils.getNamedFloat(typedArray, xmlPullParser, "controlY1", 1, f);
            boolean hasAttribute = TypedArrayUtils.hasAttribute(xmlPullParser, "controlX2");
            if (hasAttribute != TypedArrayUtils.hasAttribute(xmlPullParser, "controlY2")) {
                throw new InflateException("pathInterpolator requires both controlX2 and controlY2 for cubic Beziers.");
            } else if (hasAttribute) {
                initCubic(namedFloat, namedFloat2, TypedArrayUtils.getNamedFloat(typedArray, xmlPullParser, "controlX2", 2, f), TypedArrayUtils.getNamedFloat(typedArray, xmlPullParser, "controlY2", 3, f));
            } else {
                initQuad(namedFloat, namedFloat2);
            }
        } else {
            throw new InflateException("pathInterpolator requires the controlY1 attribute");
        }
    }

    private void initQuad(float f, float f2) {
        Path path = new Path();
        float f3 = 0.0f;
        path.moveTo(f3, f3);
        f3 = 1.0f;
        path.quadTo(f, f2, f3, f3);
        initPath(path);
    }

    private void initCubic(float f, float f2, float f3, float f4) {
        Path path = new Path();
        float f5 = 0.0f;
        path.moveTo(f5, f5);
        path.cubicTo(f, f2, f3, f4, 1.0f, 1.0f);
        initPath(path);
    }

    private void initPath(Path path) {
        int i = 0;
        PathMeasure pathMeasure = new PathMeasure(path, i);
        float length = pathMeasure.getLength();
        int i2 = 1;
        int min = Math.min(3000, ((int) (length / 0.002f)) + i2);
        StringBuilder stringBuilder;
        if (min <= 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("The Path has a invalid length ");
            stringBuilder.append(length);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        int i3;
        this.mX = new float[min];
        this.mY = new float[min];
        float[] fArr = new float[2];
        for (i3 = i; i3 < min; i3++) {
            pathMeasure.getPosTan((((float) i3) * length) / ((float) (min - 1)), fArr, null);
            this.mX[i3] = fArr[i];
            this.mY[i3] = fArr[i2];
        }
        double d = 1.0E-5d;
        if (((double) Math.abs(this.mX[i])) <= d && ((double) Math.abs(this.mY[i])) <= d) {
            int i4 = min - 1;
            float f = 1.0f;
            if (((double) Math.abs(this.mX[i4] - f)) <= d && ((double) Math.abs(this.mY[i4] - f)) <= d) {
                float f2 = 0.0f;
                int i5 = i;
                while (i < min) {
                    i3 = i5 + 1;
                    length = this.mX[i5];
                    if (length < f2) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("The Path cannot loop back on itself, x :");
                        stringBuilder.append(length);
                        throw new IllegalArgumentException(stringBuilder.toString());
                    }
                    this.mX[i] = length;
                    i++;
                    f2 = length;
                    i5 = i3;
                }
                if (pathMeasure.nextContour()) {
                    throw new IllegalArgumentException("The Path should be continuous, can't have 2+ contours");
                }
                return;
            }
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("The Path must start at (0,0) and end at (1,1) start: ");
        stringBuilder2.append(this.mX[i]);
        stringBuilder2.append(",");
        stringBuilder2.append(this.mY[i]);
        stringBuilder2.append(" end:");
        min -= i2;
        stringBuilder2.append(this.mX[min]);
        stringBuilder2.append(",");
        stringBuilder2.append(this.mY[min]);
        throw new IllegalArgumentException(stringBuilder2.toString());
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
}
