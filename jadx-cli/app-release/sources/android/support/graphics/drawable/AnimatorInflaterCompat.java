package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build.VERSION;
import android.support.annotation.AnimatorRes;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.support.v4.graphics.PathParser.PathDataNode;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({Scope.LIBRARY_GROUP})
public class AnimatorInflaterCompat {
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int MAX_NUM_POINTS = 100;
    private static final String TAG = "AnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 3;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_PATH = 2;
    private static final int VALUE_TYPE_UNDEFINED = 4;

    private static class PathDataEvaluator implements TypeEvaluator<PathDataNode[]> {
        private PathDataNode[] mNodeArray;

        private PathDataEvaluator() {
        }

        PathDataEvaluator(PathDataNode[] pathDataNodeArr) {
            this.mNodeArray = pathDataNodeArr;
        }

        public PathDataNode[] evaluate(float f, PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
            if (PathParser.canMorph(pathDataNodeArr, pathDataNodeArr2)) {
                if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, pathDataNodeArr)) {
                    this.mNodeArray = PathParser.deepCopyNodes(pathDataNodeArr);
                }
                for (int i = 0; i < pathDataNodeArr.length; i++) {
                    this.mNodeArray[i].interpolatePathDataNode(pathDataNodeArr[i], pathDataNodeArr2[i], f);
                }
                return this.mNodeArray;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }

    private static boolean isColorType(int i) {
        return i >= 28 && i <= 31;
    }

    public static Animator loadAnimator(Context context, @AnimatorRes int i) throws NotFoundException {
        if (VERSION.SDK_INT >= 24) {
            return AnimatorInflater.loadAnimator(context, i);
        }
        return loadAnimator(context, context.getResources(), context.getTheme(), i);
    }

    public static Animator loadAnimator(Context context, Resources resources, Theme theme, @AnimatorRes int i) throws NotFoundException {
        return loadAnimator(context, resources, theme, i, 1.0f);
    }

    public static Animator loadAnimator(Context context, Resources resources, Theme theme, @AnimatorRes int i, float f) throws NotFoundException {
        Throwable e;
        StringBuilder stringBuilder;
        NotFoundException notFoundException;
        XmlResourceParser xmlResourceParser = null;
        try {
            XmlResourceParser animation = resources.getAnimation(i);
            try {
                Animator createAnimatorFromXml = createAnimatorFromXml(context, resources, theme, animation, f);
                if (animation != null) {
                    animation.close();
                }
                return createAnimatorFromXml;
            } catch (XmlPullParserException e2) {
                e = e2;
                xmlResourceParser = animation;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Can't load animation resource ID #0x");
                stringBuilder.append(Integer.toHexString(i));
                notFoundException = new NotFoundException(stringBuilder.toString());
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (IOException e3) {
                e = e3;
                xmlResourceParser = animation;
                stringBuilder = new StringBuilder();
                stringBuilder.append("Can't load animation resource ID #0x");
                stringBuilder.append(Integer.toHexString(i));
                notFoundException = new NotFoundException(stringBuilder.toString());
                notFoundException.initCause(e);
                throw notFoundException;
            } catch (Throwable th) {
                e = th;
                xmlResourceParser = animation;
                if (xmlResourceParser != null) {
                    xmlResourceParser.close();
                }
                throw e;
            }
        } catch (XmlPullParserException e4) {
            e = e4;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Can't load animation resource ID #0x");
            stringBuilder.append(Integer.toHexString(i));
            notFoundException = new NotFoundException(stringBuilder.toString());
            notFoundException.initCause(e);
            throw notFoundException;
        } catch (IOException e5) {
            e = e5;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Can't load animation resource ID #0x");
            stringBuilder.append(Integer.toHexString(i));
            notFoundException = new NotFoundException(stringBuilder.toString());
            notFoundException.initCause(e);
            throw notFoundException;
        } catch (Throwable th2) {
            e = th2;
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw e;
        }
    }

    private static PropertyValuesHolder getPVH(TypedArray typedArray, int i, int i2, int i3, String str) {
        TypedValue peekValue = typedArray.peekValue(i2);
        int i4 = 1;
        int i5 = 0;
        int i6 = peekValue != null ? i4 : i5;
        int i7 = i6 != 0 ? peekValue.type : i5;
        TypedValue peekValue2 = typedArray.peekValue(i3);
        int i8 = peekValue2 != null ? i4 : i5;
        int i9 = i8 != 0 ? peekValue2.type : i5;
        int i10 = 3;
        if (i == 4) {
            i = ((i6 == 0 || !isColorType(i7)) && (i8 == 0 || !isColorType(i9))) ? i5 : i10;
        }
        int i11 = i == 0 ? i4 : i5;
        int i12 = 2;
        PropertyValuesHolder propertyValuesHolder = null;
        PropertyValuesHolder ofObject;
        if (i == i12) {
            String string = typedArray.getString(i2);
            String string2 = typedArray.getString(i3);
            PathDataNode[] createNodesFromPathData = PathParser.createNodesFromPathData(string);
            PathDataNode[] createNodesFromPathData2 = PathParser.createNodesFromPathData(string2);
            if (createNodesFromPathData == null && createNodesFromPathData2 == null) {
                return propertyValuesHolder;
            }
            if (createNodesFromPathData != null) {
                TypeEvaluator pathDataEvaluator = new PathDataEvaluator();
                Object[] objArr;
                if (createNodesFromPathData2 == null) {
                    objArr = new Object[i4];
                    objArr[i5] = createNodesFromPathData;
                    ofObject = PropertyValuesHolder.ofObject(str, pathDataEvaluator, objArr);
                } else if (PathParser.canMorph(createNodesFromPathData, createNodesFromPathData2)) {
                    objArr = new Object[i12];
                    objArr[i5] = createNodesFromPathData;
                    objArr[i4] = createNodesFromPathData2;
                    ofObject = PropertyValuesHolder.ofObject(str, pathDataEvaluator, objArr);
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(" Can't morph from ");
                    stringBuilder.append(string);
                    stringBuilder.append(" to ");
                    stringBuilder.append(string2);
                    throw new InflateException(stringBuilder.toString());
                }
                return ofObject;
            } else if (createNodesFromPathData2 == null) {
                return propertyValuesHolder;
            } else {
                TypeEvaluator pathDataEvaluator2 = new PathDataEvaluator();
                Object[] objArr2 = new Object[i4];
                objArr2[i5] = createNodesFromPathData2;
                return PropertyValuesHolder.ofObject(str, pathDataEvaluator2, objArr2);
            }
        }
        TypeEvaluator instance = i == i10 ? ArgbEvaluator.getInstance() : propertyValuesHolder;
        i10 = 5;
        float f = 0.0f;
        int dimension;
        if (i11 != 0) {
            float dimension2;
            if (i6 != 0) {
                float dimension3;
                if (i7 == i10) {
                    dimension3 = typedArray.getDimension(i2, f);
                } else {
                    dimension3 = typedArray.getFloat(i2, f);
                }
                if (i8 != 0) {
                    if (i9 == i10) {
                        dimension2 = typedArray.getDimension(i3, f);
                    } else {
                        dimension2 = typedArray.getFloat(i3, f);
                    }
                    float[] fArr = new float[i12];
                    fArr[i5] = dimension3;
                    fArr[i4] = dimension2;
                    ofObject = PropertyValuesHolder.ofFloat(str, fArr);
                } else {
                    float[] fArr2 = new float[i4];
                    fArr2[i5] = dimension3;
                    ofObject = PropertyValuesHolder.ofFloat(str, fArr2);
                }
            } else {
                if (i9 == i10) {
                    dimension2 = typedArray.getDimension(i3, f);
                } else {
                    dimension2 = typedArray.getFloat(i3, f);
                }
                float[] fArr3 = new float[i4];
                fArr3[i5] = dimension2;
                ofObject = PropertyValuesHolder.ofFloat(str, fArr3);
            }
            propertyValuesHolder = ofObject;
        } else if (i6 != 0) {
            if (i7 == i10) {
                i2 = (int) typedArray.getDimension(i2, f);
            } else if (isColorType(i7)) {
                i2 = typedArray.getColor(i2, i5);
            } else {
                i2 = typedArray.getInt(i2, i5);
            }
            if (i8 != 0) {
                if (i9 == i10) {
                    dimension = (int) typedArray.getDimension(i3, f);
                } else if (isColorType(i9)) {
                    dimension = typedArray.getColor(i3, i5);
                } else {
                    dimension = typedArray.getInt(i3, i5);
                }
                int[] iArr = new int[i12];
                iArr[i5] = i2;
                iArr[i4] = dimension;
                propertyValuesHolder = PropertyValuesHolder.ofInt(str, iArr);
            } else {
                int[] iArr2 = new int[i4];
                iArr2[i5] = i2;
                propertyValuesHolder = PropertyValuesHolder.ofInt(str, iArr2);
            }
        } else if (i8 != 0) {
            if (i9 == i10) {
                dimension = (int) typedArray.getDimension(i3, f);
            } else if (isColorType(i9)) {
                dimension = typedArray.getColor(i3, i5);
            } else {
                dimension = typedArray.getInt(i3, i5);
            }
            int[] iArr3 = new int[i4];
            iArr3[i5] = dimension;
            propertyValuesHolder = PropertyValuesHolder.ofInt(str, iArr3);
        }
        if (propertyValuesHolder == null || instance == null) {
            return propertyValuesHolder;
        }
        propertyValuesHolder.setEvaluator(instance);
        return propertyValuesHolder;
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator valueAnimator, TypedArray typedArray, TypedArray typedArray2, float f, XmlPullParser xmlPullParser) {
        int i = 1;
        long namedInt = (long) TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "duration", i, 300);
        int i2 = 0;
        long namedInt2 = (long) TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "startOffset", 2, i2);
        int i3 = 4;
        int namedInt3 = TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "valueType", 7, i3);
        if (TypedArrayUtils.hasAttribute(xmlPullParser, "valueFrom") && TypedArrayUtils.hasAttribute(xmlPullParser, "valueTo")) {
            int i4 = 6;
            int i5 = 5;
            if (namedInt3 == i3) {
                namedInt3 = inferValueTypeFromValues(typedArray, i5, i4);
            }
            PropertyValuesHolder pvh = getPVH(typedArray, namedInt3, i5, i4, "");
            if (pvh != null) {
                PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[i];
                propertyValuesHolderArr[i2] = pvh;
                valueAnimator.setValues(propertyValuesHolderArr);
            }
        }
        valueAnimator.setDuration(namedInt);
        valueAnimator.setStartDelay(namedInt2);
        valueAnimator.setRepeatCount(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatCount", 3, i2));
        valueAnimator.setRepeatMode(TypedArrayUtils.getNamedInt(typedArray, xmlPullParser, "repeatMode", i3, i));
        if (typedArray2 != null) {
            setupObjectAnimator(valueAnimator, typedArray2, namedInt3, f, xmlPullParser);
        }
    }

    private static void setupObjectAnimator(ValueAnimator valueAnimator, TypedArray typedArray, int i, float f, XmlPullParser xmlPullParser) {
        ObjectAnimator objectAnimator = (ObjectAnimator) valueAnimator;
        String namedString = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "pathData", 1);
        if (namedString != null) {
            int i2 = 2;
            String namedString2 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyXName", i2);
            String namedString3 = TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyYName", 3);
            if (i != i2) {
                Object obj = 4;
            }
            if (namedString2 == null && namedString3 == null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(typedArray.getPositionDescription());
                stringBuilder.append(" propertyXName or propertyYName is needed for PathData");
                throw new InflateException(stringBuilder.toString());
            }
            setupPathMotion(PathParser.createPathFromPathData(namedString), objectAnimator, 0.5f * f, namedString2, namedString3);
            return;
        }
        objectAnimator.setPropertyName(TypedArrayUtils.getNamedString(typedArray, xmlPullParser, "propertyName", 0));
    }

    private static void setupPathMotion(Path path, ObjectAnimator objectAnimator, float f, String str, String str2) {
        float[] fArr;
        Path path2 = path;
        ObjectAnimator objectAnimator2 = objectAnimator;
        String str3 = str;
        String str4 = str2;
        int i = 0;
        PathMeasure pathMeasure = new PathMeasure(path2, i);
        ArrayList arrayList = new ArrayList();
        float f2 = 0.0f;
        arrayList.add(Float.valueOf(f2));
        float f3 = f2;
        do {
            f3 += pathMeasure.getLength();
            arrayList.add(Float.valueOf(f3));
        } while (pathMeasure.nextContour());
        pathMeasure = new PathMeasure(path2, i);
        int i2 = 1;
        int min = Math.min(100, ((int) (f3 / f)) + i2);
        float[] fArr2 = new float[min];
        float[] fArr3 = new float[min];
        float[] fArr4 = new float[2];
        f3 /= (float) (min - 1);
        int i3 = i;
        float f4 = f2;
        int i4 = i3;
        while (true) {
            fArr = null;
            if (i4 >= min) {
                break;
            }
            pathMeasure.getPosTan(f4, fArr4, fArr);
            pathMeasure.getPosTan(f4, fArr4, fArr);
            fArr2[i4] = fArr4[i];
            fArr3[i4] = fArr4[i2];
            f4 += f3;
            int i5 = i3 + 1;
            if (i5 < arrayList.size() && f4 > ((Float) arrayList.get(i5)).floatValue()) {
                f4 -= ((Float) arrayList.get(i5)).floatValue();
                pathMeasure.nextContour();
                i3 = i5;
            }
            i4++;
            i = 0;
        }
        PropertyValuesHolder ofFloat = str3 != null ? PropertyValuesHolder.ofFloat(str3, fArr2) : fArr;
        if (str4 != null) {
            fArr = PropertyValuesHolder.ofFloat(str4, fArr3);
        }
        if (ofFloat == null) {
            PropertyValuesHolder[] propertyValuesHolderArr = new PropertyValuesHolder[i2];
            propertyValuesHolderArr[0] = fArr;
            objectAnimator2.setValues(propertyValuesHolderArr);
            return;
        }
        i = 0;
        if (fArr == null) {
            PropertyValuesHolder[] propertyValuesHolderArr2 = new PropertyValuesHolder[i2];
            propertyValuesHolderArr2[i] = ofFloat;
            objectAnimator2.setValues(propertyValuesHolderArr2);
            return;
        }
        objectAnimator2.setValues(new PropertyValuesHolder[]{ofFloat, fArr});
    }

    private static Animator createAnimatorFromXml(Context context, Resources resources, Theme theme, XmlPullParser xmlPullParser, float f) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0, f);
    }

    private static Animator createAnimatorFromXml(Context context, Resources resources, Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet, AnimatorSet animatorSet, int i, float f) throws XmlPullParserException, IOException {
        Resources resources2 = resources;
        Theme theme2 = theme;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        AnimatorSet animatorSet2 = animatorSet;
        int depth = xmlPullParser.getDepth();
        Animator animator = null;
        ArrayList arrayList = animator;
        while (true) {
            int next = xmlPullParser.next();
            int i2 = 0;
            if (next == 3 && xmlPullParser.getDepth() <= depth) {
                break;
            }
            int i3 = 1;
            if (next == i3) {
                break;
            } else if (next == 2) {
                Context context2;
                String name = xmlPullParser.getName();
                if (name.equals("objectAnimator")) {
                    animator = loadObjectAnimator(context, resources2, theme2, attributeSet, f, xmlPullParser2);
                } else if (name.equals("animator")) {
                    animator = loadAnimator(context, resources2, theme2, attributeSet, null, f, xmlPullParser2);
                } else {
                    if (name.equals("set")) {
                        Animator animatorSet3 = new AnimatorSet();
                        AttributeSet attributeSet2 = attributeSet;
                        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources2, theme2, attributeSet2, AndroidResources.STYLEABLE_ANIMATOR_SET);
                        int namedInt = TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser2, "ordering", i2, i2);
                        TypedArray typedArray = obtainAttributes;
                        createAnimatorFromXml(context, resources2, theme2, xmlPullParser2, attributeSet2, (AnimatorSet) animatorSet3, namedInt, f);
                        typedArray.recycle();
                        context2 = context;
                        animator = animatorSet3;
                        i2 = 0;
                    } else if (name.equals("propertyValuesHolder")) {
                        PropertyValuesHolder[] loadValues = loadValues(context, resources2, theme2, xmlPullParser2, Xml.asAttributeSet(xmlPullParser));
                        if (!(loadValues == null || animator == null || !(animator instanceof ValueAnimator))) {
                            ((ValueAnimator) animator).setValues(loadValues);
                        }
                        i2 = i3;
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown animator name: ");
                        stringBuilder.append(xmlPullParser.getName());
                        throw new RuntimeException(stringBuilder.toString());
                    }
                    if (animatorSet2 != null && i2 == 0) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(animator);
                    }
                }
                context2 = context;
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(animator);
            }
        }
        if (!(animatorSet2 == null || arrayList == null)) {
            Animator[] animatorArr = new Animator[arrayList.size()];
            Iterator it = arrayList.iterator();
            int i4 = 0;
            while (it.hasNext()) {
                int i5 = i4 + 1;
                animatorArr[i4] = (Animator) it.next();
                i4 = i5;
            }
            if (i == 0) {
                animatorSet2.playTogether(animatorArr);
            } else {
                animatorSet2.playSequentially(animatorArr);
            }
        }
        return animator;
    }

    private static PropertyValuesHolder[] loadValues(Context context, Resources resources, Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int eventType;
        int i;
        XmlPullParser xmlPullParser2 = xmlPullParser;
        PropertyValuesHolder[] propertyValuesHolderArr = null;
        ArrayList arrayList = propertyValuesHolderArr;
        while (true) {
            eventType = xmlPullParser.getEventType();
            i = 0;
            int i2 = 3;
            if (eventType == i2) {
                break;
            }
            int i3 = 1;
            if (eventType == i3) {
                break;
            }
            int i4 = 2;
            if (eventType != i4) {
                xmlPullParser.next();
            } else {
                Resources resources2;
                Theme theme2;
                if (xmlPullParser.getName().equals("propertyValuesHolder")) {
                    resources2 = resources;
                    theme2 = theme;
                    TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources2, theme2, attributeSet, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
                    String namedString = TypedArrayUtils.getNamedString(obtainAttributes, xmlPullParser2, "propertyName", i2);
                    int namedInt = TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser2, "valueType", i4, 4);
                    int i5 = namedInt;
                    Object loadPvh = loadPvh(context, resources2, theme2, xmlPullParser2, namedString, namedInt);
                    if (loadPvh == null) {
                        loadPvh = getPVH(obtainAttributes, i5, i, i3, namedString);
                    }
                    if (loadPvh != null) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(loadPvh);
                    }
                    obtainAttributes.recycle();
                } else {
                    resources2 = resources;
                    theme2 = theme;
                    AttributeSet attributeSet2 = attributeSet;
                }
                xmlPullParser.next();
            }
        }
        if (arrayList != null) {
            eventType = arrayList.size();
            propertyValuesHolderArr = new PropertyValuesHolder[eventType];
            while (i < eventType) {
                propertyValuesHolderArr[i] = (PropertyValuesHolder) arrayList.get(i);
                i++;
            }
        }
        return propertyValuesHolderArr;
    }

    private static int inferValueTypeOfKeyframe(Resources resources, Theme theme, AttributeSet attributeSet, XmlPullParser xmlPullParser) {
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_KEYFRAME);
        int i = 0;
        TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, "value", i);
        if ((peekNamedValue != null ? 1 : i) != 0 && isColorType(peekNamedValue.type)) {
            i = 3;
        }
        obtainAttributes.recycle();
        return i;
    }

    private static int inferValueTypeFromValues(TypedArray typedArray, int i, int i2) {
        TypedValue peekValue = typedArray.peekValue(i);
        int i3 = 1;
        int i4 = 0;
        int i5 = peekValue != null ? i3 : i4;
        i = i5 != 0 ? peekValue.type : i4;
        TypedValue peekValue2 = typedArray.peekValue(i2);
        if (peekValue2 == null) {
            i3 = i4;
        }
        return ((i5 == 0 || !isColorType(i)) && (i3 == 0 || !isColorType(i3 != 0 ? peekValue2.type : i4))) ? i4 : 3;
    }

    private static void dumpKeyframes(Object[] objArr, String str) {
        if (objArr != null && objArr.length != 0) {
            Log.d("AnimatorInflater", str);
            int length = objArr.length;
            for (int i = 0; i < length; i++) {
                Keyframe keyframe = (Keyframe) objArr[i];
                String str2 = "AnimatorInflater";
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Keyframe ");
                stringBuilder.append(i);
                stringBuilder.append(": fraction ");
                stringBuilder.append(keyframe.getFraction() < 0.0f ? "null" : Float.valueOf(keyframe.getFraction()));
                stringBuilder.append(", ");
                stringBuilder.append(", value : ");
                stringBuilder.append(keyframe.hasValue() ? keyframe.getValue() : "null");
                Log.d(str2, stringBuilder.toString());
            }
        }
    }

    private static PropertyValuesHolder loadPvh(Context context, Resources resources, Theme theme, XmlPullParser xmlPullParser, String str, int i) throws XmlPullParserException, IOException {
        PropertyValuesHolder propertyValuesHolder = null;
        int i2 = i;
        ArrayList arrayList = propertyValuesHolder;
        while (true) {
            int next = xmlPullParser.next();
            int i3 = 3;
            if (next == i3 || next == 1) {
                if (arrayList != null) {
                    int size = arrayList.size();
                    if (size > 0) {
                        int i4 = 0;
                        Keyframe keyframe = (Keyframe) arrayList.get(i4);
                        Keyframe keyframe2 = (Keyframe) arrayList.get(size - 1);
                        float fraction = keyframe2.getFraction();
                        float f = 1.0f;
                        float f2 = 0.0f;
                        if (fraction < f) {
                            if (fraction < f2) {
                                keyframe2.setFraction(f);
                            } else {
                                arrayList.add(arrayList.size(), createNewKeyframe(keyframe2, f));
                                size++;
                            }
                        }
                        float fraction2 = keyframe.getFraction();
                        if (fraction2 != f2) {
                            if (fraction2 < f2) {
                                keyframe.setFraction(f2);
                            } else {
                                arrayList.add(i4, createNewKeyframe(keyframe, f2));
                                size++;
                            }
                        }
                        Keyframe[] keyframeArr = new Keyframe[size];
                        arrayList.toArray(keyframeArr);
                        while (i4 < size) {
                            keyframe2 = keyframeArr[i4];
                            if (keyframe2.getFraction() < f2) {
                                if (i4 == 0) {
                                    keyframe2.setFraction(f2);
                                } else {
                                    i = size - 1;
                                    if (i4 == i) {
                                        keyframe2.setFraction(f);
                                    } else {
                                        int i5 = i4 + 1;
                                        int i6 = i4;
                                        while (i5 < i && keyframeArr[i5].getFraction() < f2) {
                                            i6 = i5;
                                            i5++;
                                        }
                                        distributeKeyframes(keyframeArr, keyframeArr[i6 + 1].getFraction() - keyframeArr[i4 - 1].getFraction(), i4, i6);
                                    }
                                }
                            }
                            i4++;
                        }
                        propertyValuesHolder = PropertyValuesHolder.ofKeyframe(str, keyframeArr);
                        if (i2 == i3) {
                            propertyValuesHolder.setEvaluator(ArgbEvaluator.getInstance());
                        }
                    }
                }
            } else if (xmlPullParser.getName().equals("keyframe")) {
                if (i2 == 4) {
                    i2 = inferValueTypeOfKeyframe(resources, theme, Xml.asAttributeSet(xmlPullParser), xmlPullParser);
                }
                Keyframe loadKeyframe = loadKeyframe(context, resources, theme, Xml.asAttributeSet(xmlPullParser), i2, xmlPullParser);
                if (loadKeyframe != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(loadKeyframe);
                }
                xmlPullParser.next();
            }
        }
        if (arrayList != null) {
            int size2 = arrayList.size();
            if (size2 > 0) {
                int i42 = 0;
                Keyframe keyframe3 = (Keyframe) arrayList.get(i42);
                Keyframe keyframe22 = (Keyframe) arrayList.get(size2 - 1);
                float fraction3 = keyframe22.getFraction();
                float f3 = 1.0f;
                float f22 = 0.0f;
                if (fraction3 < f3) {
                    if (fraction3 < f22) {
                        keyframe22.setFraction(f3);
                    } else {
                        arrayList.add(arrayList.size(), createNewKeyframe(keyframe22, f3));
                        size2++;
                    }
                }
                float fraction22 = keyframe3.getFraction();
                if (fraction22 != f22) {
                    if (fraction22 < f22) {
                        keyframe3.setFraction(f22);
                    } else {
                        arrayList.add(i42, createNewKeyframe(keyframe3, f22));
                        size2++;
                    }
                }
                Keyframe[] keyframeArr2 = new Keyframe[size2];
                arrayList.toArray(keyframeArr2);
                while (i42 < size2) {
                    keyframe22 = keyframeArr2[i42];
                    if (keyframe22.getFraction() < f22) {
                        if (i42 == 0) {
                            keyframe22.setFraction(f22);
                        } else {
                            i = size2 - 1;
                            if (i42 == i) {
                                keyframe22.setFraction(f3);
                            } else {
                                int i52 = i42 + 1;
                                int i62 = i42;
                                while (i52 < i && keyframeArr2[i52].getFraction() < f22) {
                                    i62 = i52;
                                    i52++;
                                }
                                distributeKeyframes(keyframeArr2, keyframeArr2[i62 + 1].getFraction() - keyframeArr2[i42 - 1].getFraction(), i42, i62);
                            }
                        }
                    }
                    i42++;
                }
                propertyValuesHolder = PropertyValuesHolder.ofKeyframe(str, keyframeArr2);
                if (i2 == i3) {
                    propertyValuesHolder.setEvaluator(ArgbEvaluator.getInstance());
                }
            }
        }
        return propertyValuesHolder;
    }

    private static Keyframe createNewKeyframe(Keyframe keyframe, float f) {
        if (keyframe.getType() == Float.TYPE) {
            return Keyframe.ofFloat(f);
        }
        if (keyframe.getType() == Integer.TYPE) {
            return Keyframe.ofInt(f);
        }
        return Keyframe.ofObject(f);
    }

    private static void distributeKeyframes(Keyframe[] keyframeArr, float f, int i, int i2) {
        f /= (float) ((i2 - i) + 2);
        while (i <= i2) {
            keyframeArr[i].setFraction(keyframeArr[i - 1].getFraction() + f);
            i++;
        }
    }

    private static Keyframe loadKeyframe(Context context, Resources resources, Theme theme, AttributeSet attributeSet, int i, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        Keyframe ofFloat;
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_KEYFRAME);
        int i2 = 3;
        float namedFloat = TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, "fraction", i2, -1.0f);
        int i3 = 0;
        TypedValue peekNamedValue = TypedArrayUtils.peekNamedValue(obtainAttributes, xmlPullParser, "value", i3);
        int i4 = 1;
        int i5 = peekNamedValue != null ? i4 : i3;
        if (i == 4) {
            i = (i5 == 0 || !isColorType(peekNamedValue.type)) ? i3 : i2;
        }
        if (i5 != 0) {
            if (i != i2) {
                switch (i) {
                    case 0:
                        ofFloat = Keyframe.ofFloat(namedFloat, TypedArrayUtils.getNamedFloat(obtainAttributes, xmlPullParser, "value", i3, 0.0f));
                        break;
                    case 1:
                        break;
                    default:
                        ofFloat = null;
                        break;
                }
            }
            ofFloat = Keyframe.ofInt(namedFloat, TypedArrayUtils.getNamedInt(obtainAttributes, xmlPullParser, "value", i3, i3));
        } else if (i == 0) {
            ofFloat = Keyframe.ofFloat(namedFloat);
        } else {
            ofFloat = Keyframe.ofInt(namedFloat);
        }
        i2 = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", i4, i3);
        if (i2 > 0) {
            ofFloat.setInterpolator(AnimationUtilsCompat.loadInterpolator(context, i2));
        }
        obtainAttributes.recycle();
        return ofFloat;
    }

    private static ObjectAnimator loadObjectAnimator(Context context, Resources resources, Theme theme, AttributeSet attributeSet, float f, XmlPullParser xmlPullParser) throws NotFoundException {
        ValueAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(context, resources, theme, attributeSet, objectAnimator, f, xmlPullParser);
        return objectAnimator;
    }

    private static ValueAnimator loadAnimator(Context context, Resources resources, Theme theme, AttributeSet attributeSet, ValueAnimator valueAnimator, float f, XmlPullParser xmlPullParser) throws NotFoundException {
        TypedArray obtainAttributes = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_ANIMATOR);
        TypedArray obtainAttributes2 = TypedArrayUtils.obtainAttributes(resources, theme, attributeSet, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        parseAnimatorFromTypeArray(valueAnimator, obtainAttributes, obtainAttributes2, f, xmlPullParser);
        int i = 0;
        int namedResourceId = TypedArrayUtils.getNamedResourceId(obtainAttributes, xmlPullParser, "interpolator", i, i);
        if (namedResourceId > 0) {
            valueAnimator.setInterpolator(AnimationUtilsCompat.loadInterpolator(context, namedResourceId));
        }
        obtainAttributes.recycle();
        if (obtainAttributes2 != null) {
            obtainAttributes2.recycle();
        }
        return valueAnimator;
    }
}
