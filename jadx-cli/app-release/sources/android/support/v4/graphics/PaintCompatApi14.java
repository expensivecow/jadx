package android.support.v4.graphics;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

class PaintCompatApi14 {
    private static final String EM_STRING = "m";
    private static final String TOFU_STRING = "󟿽";
    private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal();

    PaintCompatApi14() {
    }

    static boolean hasGlyph(@NonNull Paint paint, @NonNull String str) {
        boolean length = str.length();
        boolean z = true;
        boolean z2 = false;
        if (length == z && Character.isWhitespace(str.charAt(z2))) {
            return z;
        }
        float measureText = paint.measureText("󟿽");
        float measureText2 = paint.measureText("m");
        float measureText3 = paint.measureText(str);
        float f = 0.0f;
        if (measureText3 == f) {
            return z2;
        }
        if (str.codePointCount(z2, str.length()) > z) {
            if (measureText3 > 2.0f * measureText2) {
                return z2;
            }
            boolean z3 = z2;
            while (z3 < length) {
                boolean charCount = Character.charCount(str.codePointAt(z3)) + z3;
                f += paint.measureText(str, z3, charCount);
                z3 = charCount;
            }
            if (measureText3 >= f) {
                return z2;
            }
        }
        if (measureText3 != measureText) {
            return z;
        }
        Pair obtainEmptyRects = obtainEmptyRects();
        paint.getTextBounds("󟿽", z2, "󟿽".length(), (Rect) obtainEmptyRects.first);
        paint.getTextBounds(str, z2, length, (Rect) obtainEmptyRects.second);
        return ((Rect) obtainEmptyRects.first).equals(obtainEmptyRects.second) ^ z;
    }

    private static Pair<Rect, Rect> obtainEmptyRects() {
        Pair<Rect, Rect> pair = (Pair) sRectThreadLocal.get();
        if (pair == null) {
            pair = new Pair(new Rect(), new Rect());
            sRectThreadLocal.set(pair);
            return pair;
        }
        ((Rect) pair.first).setEmpty();
        ((Rect) pair.second).setEmpty();
        return pair;
    }
}
