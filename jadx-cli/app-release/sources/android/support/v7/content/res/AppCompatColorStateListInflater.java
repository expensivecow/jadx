package android.support.v7.content.res;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.util.StateSet;
import android.util.Xml;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

final class AppCompatColorStateListInflater {
    private static final int DEFAULT_COLOR = -65536;

    private AppCompatColorStateListInflater() {
    }

    @NonNull
    public static ColorStateList createFromXml(@NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @Nullable Theme theme) throws XmlPullParserException, IOException {
        int next;
        int i;
        AttributeSet asAttributeSet = Xml.asAttributeSet(xmlPullParser);
        while (true) {
            next = xmlPullParser.next();
            i = 2;
            if (next == i || next == 1) {
                if (next != i) {
                    return createFromXmlInner(resources, xmlPullParser, asAttributeSet, theme);
                }
                throw new XmlPullParserException("No start tag found");
            }
        }
        if (next != i) {
            return createFromXmlInner(resources, xmlPullParser, asAttributeSet, theme);
        }
        throw new XmlPullParserException("No start tag found");
    }

    @NonNull
    private static ColorStateList createFromXmlInner(@NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) throws XmlPullParserException, IOException {
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            return inflate(resources, xmlPullParser, attributeSet, theme);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(xmlPullParser.getPositionDescription());
        stringBuilder.append(": invalid color state list tag ");
        stringBuilder.append(name);
        throw new XmlPullParserException(stringBuilder.toString());
    }

    private static ColorStateList inflate(@NonNull Resources resources, @NonNull XmlPullParser xmlPullParser, @NonNull AttributeSet attributeSet, @Nullable Theme theme) throws XmlPullParserException, IOException {
        AttributeSet attributeSet2 = attributeSet;
        int i = 1;
        int depth = xmlPullParser.getDepth() + i;
        Object obj = new int[20][];
        boolean z = false;
        Object obj2 = new int[obj.length];
        int i2 = z;
        while (true) {
            int next = xmlPullParser.next();
            if (next == i) {
                break;
            }
            int depth2 = xmlPullParser.getDepth();
            if (depth2 < depth && next == 3) {
                break;
            }
            if (next == 2 && depth2 <= depth && xmlPullParser.getName().equals("item")) {
                Object obj3;
                TypedArray obtainAttributes = obtainAttributes(resources, theme, attributeSet2, R.styleable.ColorStateListItem);
                int color = obtainAttributes.getColor(R.styleable.ColorStateListItem_android_color, -65281);
                float f = 1.0f;
                if (obtainAttributes.hasValue(R.styleable.ColorStateListItem_android_alpha)) {
                    f = obtainAttributes.getFloat(R.styleable.ColorStateListItem_android_alpha, f);
                } else if (obtainAttributes.hasValue(R.styleable.ColorStateListItem_alpha)) {
                    f = obtainAttributes.getFloat(R.styleable.ColorStateListItem_alpha, f);
                }
                obtainAttributes.recycle();
                next = attributeSet.getAttributeCount();
                int[] iArr = new int[next];
                int i3 = z;
                int i4 = i3;
                while (i3 < next) {
                    int attributeNameResource = attributeSet2.getAttributeNameResource(i3);
                    if (!(attributeNameResource == 16843173 || attributeNameResource == 16843551 || attributeNameResource == R.attr.alpha)) {
                        i = i4 + 1;
                        if (!attributeSet2.getAttributeBooleanValue(i3, z)) {
                            attributeNameResource = -attributeNameResource;
                        }
                        iArr[i4] = attributeNameResource;
                        i4 = i;
                    }
                    i3++;
                    obj3 = 1;
                }
                obj3 = StateSet.trimStateSet(iArr, i4);
                next = modulateColorAlpha(color, f);
                if (i2 != 0) {
                    color = obj3.length;
                }
                obj2 = GrowingArrayUtils.append((int[]) obj2, i2, next);
                obj = (int[][]) GrowingArrayUtils.append((Object[]) obj, i2, obj3);
                i2++;
            } else {
                Resources resources2 = resources;
                Theme theme2 = theme;
            }
            i = 1;
        }
        Object obj4 = new int[i2];
        Object obj5 = new int[i2][];
        System.arraycopy(obj2, z, obj4, z, i2);
        System.arraycopy(obj, z, obj5, z, i2);
        return new ColorStateList(obj5, obj4);
    }

    private static TypedArray obtainAttributes(Resources resources, Theme theme, AttributeSet attributeSet, int[] iArr) {
        if (theme == null) {
            return resources.obtainAttributes(attributeSet, iArr);
        }
        int i = 0;
        return theme.obtainStyledAttributes(attributeSet, iArr, i, i);
    }

    private static int modulateColorAlpha(int i, float f) {
        return ColorUtils.setAlphaComponent(i, Math.round(((float) Color.alpha(i)) * f));
    }
}
