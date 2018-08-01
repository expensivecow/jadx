package android.support.v4.graphics;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

public final class ColorUtils {
    private static final int MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10;
    private static final int MIN_ALPHA_SEARCH_PRECISION = 1;
    private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal();
    private static final double XYZ_EPSILON = 0.008856d;
    private static final double XYZ_KAPPA = 903.3d;
    private static final double XYZ_WHITE_REFERENCE_X = 95.047d;
    private static final double XYZ_WHITE_REFERENCE_Y = 100.0d;
    private static final double XYZ_WHITE_REFERENCE_Z = 108.883d;

    private static float constrain(float f, float f2, float f3) {
        return f < f2 ? f2 : f > f3 ? f3 : f;
    }

    private static int constrain(int i, int i2, int i3) {
        return i < i2 ? i2 : i > i3 ? i3 : i;
    }

    private ColorUtils() {
    }

    public static int compositeColors(@ColorInt int i, @ColorInt int i2) {
        int alpha = Color.alpha(i2);
        int alpha2 = Color.alpha(i);
        int compositeAlpha = compositeAlpha(alpha2, alpha);
        return Color.argb(compositeAlpha, compositeComponent(Color.red(i), alpha2, Color.red(i2), alpha, compositeAlpha), compositeComponent(Color.green(i), alpha2, Color.green(i2), alpha, compositeAlpha), compositeComponent(Color.blue(i), alpha2, Color.blue(i2), alpha, compositeAlpha));
    }

    private static int compositeAlpha(int i, int i2) {
        return 255 - (((255 - i2) * (255 - i)) / 255);
    }

    private static int compositeComponent(int i, int i2, int i3, int i4, int i5) {
        if (i5 == 0) {
            return 0;
        }
        int i6 = 255;
        return (((i * i6) * i2) + ((i3 * i4) * (255 - i2))) / (i5 * i6);
    }

    @FloatRange(from = 0.0d, to = 1.0d)
    public static double calculateLuminance(@ColorInt int i) {
        double[] tempDouble3Array = getTempDouble3Array();
        colorToXYZ(i, tempDouble3Array);
        return tempDouble3Array[1] / 100.0d;
    }

    public static double calculateContrast(@ColorInt int i, @ColorInt int i2) {
        int i3 = 255;
        if (Color.alpha(i2) != i3) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("background can not be translucent: #");
            stringBuilder.append(Integer.toHexString(i2));
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        if (Color.alpha(i) < i3) {
            i = compositeColors(i, i2);
        }
        double d = 0.05d;
        double calculateLuminance = calculateLuminance(i) + d;
        double calculateLuminance2 = calculateLuminance(i2) + d;
        return Math.max(calculateLuminance, calculateLuminance2) / Math.min(calculateLuminance, calculateLuminance2);
    }

    public static int calculateMinimumAlpha(@ColorInt int i, @ColorInt int i2, float f) {
        int i3 = 255;
        if (Color.alpha(i2) != i3) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("background can not be translucent: #");
            stringBuilder.append(Integer.toHexString(i2));
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        double d = (double) f;
        if (calculateContrast(setAlphaComponent(i, i3), i2) < d) {
            return -1;
        }
        int i4 = 0;
        int i5 = i4;
        while (i4 <= 10 && i3 - i5 > 1) {
            int i6 = (i5 + i3) / 2;
            if (calculateContrast(setAlphaComponent(i, i6), i2) < d) {
                i3 = i6;
                i4++;
            } else {
                i3 = i6;
                i4++;
            }
        }
        return i3;
    }

    public static void RGBToHSL(@IntRange(from = 0, to = 255) int i, @IntRange(from = 0, to = 255) int i2, @IntRange(from = 0, to = 255) int i3, @NonNull float[] fArr) {
        float f = 255.0f;
        float f2 = ((float) i) / f;
        float f3 = ((float) i2) / f;
        float f4 = ((float) i3) / f;
        f = Math.max(f2, Math.max(f3, f4));
        float min = Math.min(f2, Math.min(f3, f4));
        float f5 = f - min;
        float f6 = 2.0f;
        float f7 = (f + min) / f6;
        float f8 = 1.0f;
        float f9 = 0.0f;
        if (f == min) {
            f2 = f9;
            f3 = f2;
        } else {
            f2 = f == f2 ? ((f3 - f4) / f5) % 6.0f : f == f3 ? ((f4 - f2) / f5) + f6 : ((f2 - f3) / f5) + 4.0f;
            f3 = f5 / (f8 - Math.abs((f6 * f7) - f8));
        }
        f4 = 360.0f;
        f2 = (f2 * 60.0f) % f4;
        if (f2 < f9) {
            f2 += f4;
        }
        fArr[0] = constrain(f2, f9, f4);
        fArr[1] = constrain(f3, f9, f8);
        fArr[2] = constrain(f7, f9, f8);
    }

    public static void colorToHSL(@ColorInt int i, @NonNull float[] fArr) {
        RGBToHSL(Color.red(i), Color.green(i), Color.blue(i), fArr);
    }

    @ColorInt
    public static int HSLToColor(@NonNull float[] fArr) {
        int round;
        int round2;
        int round3;
        int i = 0;
        float f = fArr[i];
        float f2 = fArr[1];
        float f3 = fArr[2];
        float f4 = 2.0f;
        float f5 = 1.0f;
        float abs = (f5 - Math.abs((f4 * f3) - f5)) * f2;
        f3 -= 0.5f * abs;
        f5 = (f5 - Math.abs(((f / 60.0f) % f4) - f5)) * abs;
        f2 = 255.0f;
        switch (((int) f) / 60) {
            case 0:
                round = Math.round((abs + f3) * f2);
                round2 = Math.round((f5 + f3) * f2);
                round3 = Math.round(f2 * f3);
                break;
            case 1:
                round = Math.round((f5 + f3) * f2);
                round2 = Math.round((abs + f3) * f2);
                round3 = Math.round(f2 * f3);
                break;
            case 2:
                round = Math.round(f2 * f3);
                round2 = Math.round((abs + f3) * f2);
                round3 = Math.round(f2 * (f5 + f3));
                break;
            case 3:
                round = Math.round(f2 * f3);
                round2 = Math.round((f5 + f3) * f2);
                round3 = Math.round(f2 * (abs + f3));
                break;
            case 4:
                round = Math.round((f5 + f3) * f2);
                round2 = Math.round(f2 * f3);
                round3 = Math.round(f2 * (abs + f3));
                break;
            case 5:
            case 6:
                round = Math.round((abs + f3) * f2);
                round2 = Math.round(f2 * f3);
                round3 = Math.round(f2 * (f5 + f3));
                break;
            default:
                round3 = i;
                round = round3;
                round2 = round;
                break;
        }
        int i2 = 255;
        return Color.rgb(constrain(round, i, i2), constrain(round2, i, i2), constrain(round3, i, i2));
    }

    @ColorInt
    public static int setAlphaComponent(@ColorInt int i, @IntRange(from = 0, to = 255) int i2) {
        if (i2 >= 0 && i2 <= 255) {
            return (i & 16777215) | (i2 << 24);
        }
        throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }

    public static void colorToLAB(@ColorInt int i, @NonNull double[] dArr) {
        RGBToLAB(Color.red(i), Color.green(i), Color.blue(i), dArr);
    }

    public static void RGBToLAB(@IntRange(from = 0, to = 255) int i, @IntRange(from = 0, to = 255) int i2, @IntRange(from = 0, to = 255) int i3, @NonNull double[] dArr) {
        RGBToXYZ(i, i2, i3, dArr);
        XYZToLAB(dArr[0], dArr[1], dArr[2], dArr);
    }

    public static void colorToXYZ(@ColorInt int i, @NonNull double[] dArr) {
        RGBToXYZ(Color.red(i), Color.green(i), Color.blue(i), dArr);
    }

    public static void RGBToXYZ(@IntRange(from = 0, to = 255) int i, @IntRange(from = 0, to = 255) int i2, @IntRange(from = 0, to = 255) int i3, @NonNull double[] dArr) {
        double[] dArr2 = dArr;
        if (dArr2.length != 3) {
            throw new IllegalArgumentException("outXyz must have a length of 3.");
        }
        double d = 255.0d;
        double d2 = ((double) i) / d;
        double d3 = 0.04045d;
        double d4 = 2.4d;
        double d5 = 1.055d;
        double d6 = 0.055d;
        double d7 = 12.92d;
        if (d2 < d3) {
            d2 /= d7;
        } else {
            d2 = Math.pow((d2 + d6) / d5, d4);
        }
        double d8 = d2;
        d2 = ((double) i2) / d;
        if (d2 < d3) {
            d2 /= d7;
        } else {
            d2 = Math.pow((d2 + d6) / d5, d4);
        }
        double d9 = d2;
        d2 = ((double) i3) / d;
        if (d2 < d3) {
            d2 /= d7;
        } else {
            d2 = Math.pow((d2 + d6) / d5, d4);
        }
        double d10 = 100.0d;
        dArr2[0] = (((0.4124d * d8) + (0.3576d * d9)) + (0.1805d * d2)) * d10;
        dArr2[1] = (((0.2126d * d8) + (0.7152d * d9)) + (0.0722d * d2)) * d10;
        dArr2[2] = d10 * (((d8 * 0.0193d) + (d9 * 0.1192d)) + (d2 * 0.9505d));
    }

    public static void XYZToLAB(@FloatRange(from = 0.0d, to = 95.047d) double d, @FloatRange(from = 0.0d, to = 100.0d) double d2, @FloatRange(from = 0.0d, to = 108.883d) double d3, @NonNull double[] dArr) {
        if (dArr.length != 3) {
            throw new IllegalArgumentException("outLab must have a length of 3.");
        }
        d = pivotXyzComponent(d / 95.047d);
        d2 = pivotXyzComponent(d2 / 100.0d);
        d3 = pivotXyzComponent(d3 / 108.883d);
        dArr[0] = Math.max(0.0d, (116.0d * d2) - 16.0d);
        dArr[1] = 500.0d * (d - d2);
        dArr[2] = 200.0d * (d2 - d3);
    }

    public static void LABToXYZ(@FloatRange(from = 0.0d, to = 100.0d) double d, @FloatRange(from = -128.0d, to = 127.0d) double d2, @FloatRange(from = -128.0d, to = 127.0d) double d3, @NonNull double[] dArr) {
        double d4 = 16.0d;
        double d5 = 116.0d;
        double d6 = (d + d4) / d5;
        double d7 = (d2 / 500.0d) + d6;
        double d8 = d6 - (d3 / 200.0d);
        double d9 = 3.0d;
        double pow = Math.pow(d7, d9);
        double d10 = 0.008856d;
        double d11 = 903.3d;
        if (pow <= d10) {
            pow = ((d7 * d5) - d4) / d11;
        }
        double pow2 = d > 7.9996247999999985d ? Math.pow(d6, d9) : d / d11;
        d6 = Math.pow(d8, d9);
        if (d6 <= d10) {
            d6 = ((d5 * d8) - d4) / d11;
        }
        dArr[0] = pow * 95.047d;
        dArr[1] = pow2 * 100.0d;
        dArr[2] = d6 * 108.883d;
    }

    @ColorInt
    public static int XYZToColor(@FloatRange(from = 0.0d, to = 95.047d) double d, @FloatRange(from = 0.0d, to = 100.0d) double d2, @FloatRange(from = 0.0d, to = 108.883d) double d3) {
        double d4 = 100.0d;
        double d5 = (((3.2406d * d) + (-1.5372d * d2)) + (-0.4986d * d3)) / d4;
        double d6 = (((-0.9689d * d) + (1.8758d * d2)) + (0.0415d * d3)) / d4;
        double d7 = (((d * 0.0557d) + (d2 * -0.204d)) + (1.057d * d3)) / d4;
        double d8 = 0.0031308d;
        d4 = 12.92d;
        double d9 = 0.055d;
        double d10 = 0.4166666666666667d;
        double d11 = 1.055d;
        double pow = d5 > d8 ? (Math.pow(d5, d10) * d11) - d9 : d4 * d5;
        d5 = d6 > d8 ? (Math.pow(d6, d10) * d11) - d9 : d4 * d6;
        d11 = d7 > d8 ? (d11 * Math.pow(d7, d10)) - d9 : d4 * d7;
        d7 = 255.0d;
        int i = 255;
        int i2 = 0;
        return Color.rgb(constrain((int) Math.round(pow * d7), i2, i), constrain((int) Math.round(d5 * d7), i2, i), constrain((int) Math.round(d7 * d11), i2, i));
    }

    @ColorInt
    public static int LABToColor(@FloatRange(from = 0.0d, to = 100.0d) double d, @FloatRange(from = -128.0d, to = 127.0d) double d2, @FloatRange(from = -128.0d, to = 127.0d) double d3) {
        double[] tempDouble3Array = getTempDouble3Array();
        LABToXYZ(d, d2, d3, tempDouble3Array);
        return XYZToColor(tempDouble3Array[0], tempDouble3Array[1], tempDouble3Array[2]);
    }

    public static double distanceEuclidean(@NonNull double[] dArr, @NonNull double[] dArr2) {
        int i = 0;
        double d = 2.0d;
        int i2 = 1;
        i2 = 2;
        return Math.sqrt((Math.pow(dArr[i] - dArr2[i], d) + Math.pow(dArr[i2] - dArr2[i2], d)) + Math.pow(dArr[i2] - dArr2[i2], d));
    }

    private static double pivotXyzComponent(double d) {
        return d > 0.008856d ? Math.pow(d, 0.3333333333333333d) : ((903.3d * d) + 16.0d) / 116.0d;
    }

    @ColorInt
    public static int blendARGB(@ColorInt int i, @ColorInt int i2, @FloatRange(from = 0.0d, to = 1.0d) float f) {
        float f2 = 1.0f - f;
        return Color.argb((int) ((((float) Color.alpha(i)) * f2) + (((float) Color.alpha(i2)) * f)), (int) ((((float) Color.red(i)) * f2) + (((float) Color.red(i2)) * f)), (int) ((((float) Color.green(i)) * f2) + (((float) Color.green(i2)) * f)), (int) ((((float) Color.blue(i)) * f2) + (((float) Color.blue(i2)) * f)));
    }

    public static void blendHSL(@NonNull float[] fArr, @NonNull float[] fArr2, @FloatRange(from = 0.0d, to = 1.0d) float f, @NonNull float[] fArr3) {
        if (fArr3.length != 3) {
            throw new IllegalArgumentException("result must have a length of 3.");
        }
        float f2 = 1.0f - f;
        int i = 0;
        fArr3[i] = circularInterpolate(fArr[i], fArr2[i], f);
        i = 1;
        fArr3[i] = (fArr[i] * f2) + (fArr2[i] * f);
        i = 2;
        fArr3[i] = (fArr[i] * f2) + (fArr2[i] * f);
    }

    public static void blendLAB(@NonNull double[] dArr, @NonNull double[] dArr2, @FloatRange(from = 0.0d, to = 1.0d) double d, @NonNull double[] dArr3) {
        if (dArr3.length != 3) {
            throw new IllegalArgumentException("outResult must have a length of 3.");
        }
        double d2 = 1.0d - d;
        int i = 0;
        dArr3[i] = (dArr[i] * d2) + (dArr2[i] * d);
        i = 1;
        dArr3[i] = (dArr[i] * d2) + (dArr2[i] * d);
        i = 2;
        dArr3[i] = (dArr[i] * d2) + (dArr2[i] * d);
    }

    @VisibleForTesting
    static float circularInterpolate(float f, float f2, float f3) {
        float f4 = 360.0f;
        if (Math.abs(f2 - f) > 180.0f) {
            if (f2 > f) {
                f += f4;
            } else {
                f2 += f4;
            }
        }
        return (f + ((f2 - f) * f3)) % f4;
    }

    private static double[] getTempDouble3Array() {
        double[] dArr = (double[]) TEMP_ARRAY.get();
        if (dArr != null) {
            return dArr;
        }
        Object obj = new double[3];
        TEMP_ARRAY.set(obj);
        return obj;
    }
}
