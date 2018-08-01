package android.support.graphics.drawable;

import android.animation.TypeEvaluator;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ArgbEvaluator implements TypeEvaluator {
    private static final ArgbEvaluator sInstance = new ArgbEvaluator();

    public static ArgbEvaluator getInstance() {
        return sInstance;
    }

    public Object evaluate(float f, Object obj, Object obj2) {
        int intValue = ((Integer) obj).intValue();
        float f2 = 255.0f;
        float f3 = ((float) ((intValue >> 24) & 255)) / f2;
        float f4 = ((float) ((intValue >> 16) & 255)) / f2;
        float f5 = ((float) ((intValue >> 8) & 255)) / f2;
        float f6 = ((float) (intValue & 255)) / f2;
        int intValue2 = ((Integer) obj2).intValue();
        float f7 = ((float) ((intValue2 >> 24) & 255)) / f2;
        float f8 = ((float) ((intValue2 >> 16) & 255)) / f2;
        float f9 = ((float) ((intValue2 >> 8) & 255)) / f2;
        double d = 2.2d;
        f4 = (float) Math.pow((double) f4, d);
        f5 = (float) Math.pow((double) f5, d);
        f6 = (float) Math.pow((double) f6, d);
        f5 += (((float) Math.pow((double) f9, d)) - f5) * f;
        f6 += f * (((float) Math.pow((double) (((float) (intValue2 & 255)) / f2), d)) - f6);
        f3 = (f3 + ((f7 - f3) * f)) * f2;
        double d2 = 0.45454545454545453d;
        return Integer.valueOf((((Math.round(((float) Math.pow((double) (f4 + ((((float) Math.pow((double) f8, d)) - f4) * f)), d2)) * f2) << 16) | (Math.round(f3) << 24)) | (Math.round(((float) Math.pow((double) f5, d2)) * f2) << 8)) | Math.round(((float) Math.pow((double) f6, d2)) * f2));
    }
}
