package android.support.constraint;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConstraintSet {
    private static final int ALPHA = 43;
    public static final int BASELINE = 5;
    private static final int BASELINE_TO_BASELINE = 1;
    public static final int BOTTOM = 4;
    private static final int BOTTOM_MARGIN = 2;
    private static final int BOTTOM_TO_BOTTOM = 3;
    private static final int BOTTOM_TO_TOP = 4;
    public static final int CHAIN_PACKED = 2;
    public static final int CHAIN_SPREAD = 0;
    public static final int CHAIN_SPREAD_INSIDE = 1;
    private static final boolean DEBUG = false;
    private static final int DIMENSION_RATIO = 5;
    private static final int EDITOR_ABSOLUTE_X = 6;
    private static final int EDITOR_ABSOLUTE_Y = 7;
    private static final int ELEVATION = 44;
    public static final int END = 7;
    private static final int END_MARGIN = 8;
    private static final int END_TO_END = 9;
    private static final int END_TO_START = 10;
    public static final int GONE = 8;
    private static final int GONE_BOTTOM_MARGIN = 11;
    private static final int GONE_END_MARGIN = 12;
    private static final int GONE_LEFT_MARGIN = 13;
    private static final int GONE_RIGHT_MARGIN = 14;
    private static final int GONE_START_MARGIN = 15;
    private static final int GONE_TOP_MARGIN = 16;
    private static final int GUIDE_BEGIN = 17;
    private static final int GUIDE_END = 18;
    private static final int GUIDE_PERCENT = 19;
    private static final int HEIGHT_DEFAULT = 55;
    private static final int HEIGHT_MAX = 57;
    private static final int HEIGHT_MIN = 59;
    public static final int HORIZONTAL = 0;
    private static final int HORIZONTAL_BIAS = 20;
    public static final int HORIZONTAL_GUIDELINE = 0;
    private static final int HORIZONTAL_STYLE = 41;
    private static final int HORIZONTAL_WEIGHT = 39;
    public static final int INVISIBLE = 4;
    private static final int LAYOUT_HEIGHT = 21;
    private static final int LAYOUT_VISIBILITY = 22;
    private static final int LAYOUT_WIDTH = 23;
    public static final int LEFT = 1;
    private static final int LEFT_MARGIN = 24;
    private static final int LEFT_TO_LEFT = 25;
    private static final int LEFT_TO_RIGHT = 26;
    public static final int MATCH_CONSTRAINT = 0;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    private static final int ORIENTATION = 27;
    public static final int PARENT_ID = 0;
    public static final int RIGHT = 2;
    private static final int RIGHT_MARGIN = 28;
    private static final int RIGHT_TO_LEFT = 29;
    private static final int RIGHT_TO_RIGHT = 30;
    private static final int ROTATION_X = 45;
    private static final int ROTATION_Y = 46;
    private static final int SCALE_X = 47;
    private static final int SCALE_Y = 48;
    public static final int START = 6;
    private static final int START_MARGIN = 31;
    private static final int START_TO_END = 32;
    private static final int START_TO_START = 33;
    private static final String TAG = "ConstraintSet";
    public static final int TOP = 3;
    private static final int TOP_MARGIN = 34;
    private static final int TOP_TO_BOTTOM = 35;
    private static final int TOP_TO_TOP = 36;
    private static final int TRANSFORM_PIVOT_X = 49;
    private static final int TRANSFORM_PIVOT_Y = 50;
    private static final int TRANSLATION_X = 51;
    private static final int TRANSLATION_Y = 52;
    private static final int TRANSLATION_Z = 53;
    public static final int UNSET = -1;
    private static final int UNUSED = 60;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_BIAS = 37;
    public static final int VERTICAL_GUIDELINE = 1;
    private static final int VERTICAL_STYLE = 42;
    private static final int VERTICAL_WEIGHT = 40;
    private static final int VIEW_ID = 38;
    private static final int[] VISIBILITY_FLAGS = new int[]{0, 4, 8};
    public static final int VISIBLE = 0;
    private static final int WIDTH_DEFAULT = 54;
    private static final int WIDTH_MAX = 56;
    private static final int WIDTH_MIN = 58;
    public static final int WRAP_CONTENT = -2;
    private static SparseIntArray mapToConstant = new SparseIntArray();
    private HashMap<Integer, Constraint> mConstraints = new HashMap();

    private static class Constraint {
        static final int UNSET = -1;
        public float alpha;
        public boolean applyElevation;
        public int baselineToBaseline;
        public int bottomMargin;
        public int bottomToBottom;
        public int bottomToTop;
        public String dimensionRatio;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public float elevation;
        public int endMargin;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public int heightDefault;
        public int heightMax;
        public int heightMin;
        public float horizontalBias;
        public int horizontalChainStyle;
        public float horizontalWeight;
        public int leftMargin;
        public int leftToLeft;
        public int leftToRight;
        public int mHeight;
        boolean mIsGuideline;
        int mViewId;
        public int mWidth;
        public int orientation;
        public int rightMargin;
        public int rightToLeft;
        public int rightToRight;
        public float rotationX;
        public float rotationY;
        public float scaleX;
        public float scaleY;
        public int startMargin;
        public int startToEnd;
        public int startToStart;
        public int topMargin;
        public int topToBottom;
        public int topToTop;
        public float transformPivotX;
        public float transformPivotY;
        public float translationX;
        public float translationY;
        public float translationZ;
        public float verticalBias;
        public int verticalChainStyle;
        public float verticalWeight;
        public int visibility;
        public int widthDefault;
        public int widthMax;
        public int widthMin;

        private Constraint() {
            boolean z = false;
            this.mIsGuideline = z;
            int i = -1;
            this.guideBegin = i;
            this.guideEnd = i;
            this.guidePercent = -1.0f;
            this.leftToLeft = i;
            this.leftToRight = i;
            this.rightToLeft = i;
            this.rightToRight = i;
            this.topToTop = i;
            this.topToBottom = i;
            this.bottomToTop = i;
            this.bottomToBottom = i;
            this.baselineToBaseline = i;
            this.startToEnd = i;
            this.startToStart = i;
            this.endToStart = i;
            this.endToEnd = i;
            float f = 0.5f;
            this.horizontalBias = f;
            this.verticalBias = f;
            this.dimensionRatio = null;
            this.editorAbsoluteX = i;
            this.editorAbsoluteY = i;
            this.orientation = i;
            this.leftMargin = i;
            this.rightMargin = i;
            this.topMargin = i;
            this.bottomMargin = i;
            this.endMargin = i;
            this.startMargin = i;
            this.visibility = z;
            this.goneLeftMargin = i;
            this.goneTopMargin = i;
            this.goneRightMargin = i;
            this.goneBottomMargin = i;
            this.goneEndMargin = i;
            this.goneStartMargin = i;
            f = 0.0f;
            this.verticalWeight = f;
            this.horizontalWeight = f;
            this.horizontalChainStyle = z;
            this.verticalChainStyle = z;
            float f2 = 1.0f;
            this.alpha = f2;
            this.applyElevation = z;
            this.elevation = f;
            this.rotationX = f;
            this.rotationY = f;
            this.scaleX = f2;
            this.scaleY = f2;
            this.transformPivotX = f;
            this.transformPivotY = f;
            this.translationX = f;
            this.translationY = f;
            this.translationZ = f;
            this.widthDefault = i;
            this.heightDefault = i;
            this.widthMax = i;
            this.heightMax = i;
            this.widthMin = i;
            this.heightMin = i;
        }

        public Constraint clone() {
            Constraint constraint = new Constraint();
            constraint.mIsGuideline = this.mIsGuideline;
            constraint.mWidth = this.mWidth;
            constraint.mHeight = this.mHeight;
            constraint.guideBegin = this.guideBegin;
            constraint.guideEnd = this.guideEnd;
            constraint.guidePercent = this.guidePercent;
            constraint.leftToLeft = this.leftToLeft;
            constraint.leftToRight = this.leftToRight;
            constraint.rightToLeft = this.rightToLeft;
            constraint.rightToRight = this.rightToRight;
            constraint.topToTop = this.topToTop;
            constraint.topToBottom = this.topToBottom;
            constraint.bottomToTop = this.bottomToTop;
            constraint.bottomToBottom = this.bottomToBottom;
            constraint.baselineToBaseline = this.baselineToBaseline;
            constraint.startToEnd = this.startToEnd;
            constraint.startToStart = this.startToStart;
            constraint.endToStart = this.endToStart;
            constraint.endToEnd = this.endToEnd;
            constraint.horizontalBias = this.horizontalBias;
            constraint.verticalBias = this.verticalBias;
            constraint.dimensionRatio = this.dimensionRatio;
            constraint.editorAbsoluteX = this.editorAbsoluteX;
            constraint.editorAbsoluteY = this.editorAbsoluteY;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.horizontalBias = this.horizontalBias;
            constraint.orientation = this.orientation;
            constraint.leftMargin = this.leftMargin;
            constraint.rightMargin = this.rightMargin;
            constraint.topMargin = this.topMargin;
            constraint.bottomMargin = this.bottomMargin;
            constraint.endMargin = this.endMargin;
            constraint.startMargin = this.startMargin;
            constraint.visibility = this.visibility;
            constraint.goneLeftMargin = this.goneLeftMargin;
            constraint.goneTopMargin = this.goneTopMargin;
            constraint.goneRightMargin = this.goneRightMargin;
            constraint.goneBottomMargin = this.goneBottomMargin;
            constraint.goneEndMargin = this.goneEndMargin;
            constraint.goneStartMargin = this.goneStartMargin;
            constraint.verticalWeight = this.verticalWeight;
            constraint.horizontalWeight = this.horizontalWeight;
            constraint.horizontalChainStyle = this.horizontalChainStyle;
            constraint.verticalChainStyle = this.verticalChainStyle;
            constraint.alpha = this.alpha;
            constraint.applyElevation = this.applyElevation;
            constraint.elevation = this.elevation;
            constraint.rotationX = this.rotationX;
            constraint.rotationY = this.rotationY;
            constraint.scaleX = this.scaleX;
            constraint.scaleY = this.scaleY;
            constraint.transformPivotX = this.transformPivotX;
            constraint.transformPivotY = this.transformPivotY;
            constraint.translationX = this.translationX;
            constraint.translationY = this.translationY;
            constraint.translationZ = this.translationZ;
            constraint.widthDefault = this.widthDefault;
            constraint.heightDefault = this.heightDefault;
            constraint.widthMax = this.widthMax;
            constraint.heightMax = this.heightMax;
            constraint.widthMin = this.widthMin;
            constraint.heightMin = this.heightMin;
            return constraint;
        }

        private void fillFrom(int i, LayoutParams layoutParams) {
            this.mViewId = i;
            this.leftToLeft = layoutParams.leftToLeft;
            this.leftToRight = layoutParams.leftToRight;
            this.rightToLeft = layoutParams.rightToLeft;
            this.rightToRight = layoutParams.rightToRight;
            this.topToTop = layoutParams.topToTop;
            this.topToBottom = layoutParams.topToBottom;
            this.bottomToTop = layoutParams.bottomToTop;
            this.bottomToBottom = layoutParams.bottomToBottom;
            this.baselineToBaseline = layoutParams.baselineToBaseline;
            this.startToEnd = layoutParams.startToEnd;
            this.startToStart = layoutParams.startToStart;
            this.endToStart = layoutParams.endToStart;
            this.endToEnd = layoutParams.endToEnd;
            this.horizontalBias = layoutParams.horizontalBias;
            this.verticalBias = layoutParams.verticalBias;
            this.dimensionRatio = layoutParams.dimensionRatio;
            this.editorAbsoluteX = layoutParams.editorAbsoluteX;
            this.editorAbsoluteY = layoutParams.editorAbsoluteY;
            this.orientation = layoutParams.orientation;
            this.guidePercent = layoutParams.guidePercent;
            this.guideBegin = layoutParams.guideBegin;
            this.guideEnd = layoutParams.guideEnd;
            this.mWidth = layoutParams.width;
            this.mHeight = layoutParams.height;
            this.leftMargin = layoutParams.leftMargin;
            this.rightMargin = layoutParams.rightMargin;
            this.topMargin = layoutParams.topMargin;
            this.bottomMargin = layoutParams.bottomMargin;
            this.verticalWeight = layoutParams.verticalWeight;
            this.horizontalWeight = layoutParams.horizontalWeight;
            this.verticalChainStyle = layoutParams.verticalChainStyle;
            this.horizontalChainStyle = layoutParams.horizontalChainStyle;
            this.widthDefault = layoutParams.matchConstraintDefaultWidth;
            this.heightDefault = layoutParams.matchConstraintDefaultHeight;
            this.widthMax = layoutParams.matchConstraintMaxWidth;
            this.heightMax = layoutParams.matchConstraintMaxHeight;
            this.widthMin = layoutParams.matchConstraintMinWidth;
            this.heightMin = layoutParams.matchConstraintMinHeight;
            if (VERSION.SDK_INT >= 17) {
                this.endMargin = layoutParams.getMarginEnd();
                this.startMargin = layoutParams.getMarginStart();
            }
        }

        public void applyTo(LayoutParams layoutParams) {
            layoutParams.leftToLeft = this.leftToLeft;
            layoutParams.leftToRight = this.leftToRight;
            layoutParams.rightToLeft = this.rightToLeft;
            layoutParams.rightToRight = this.rightToRight;
            layoutParams.topToTop = this.topToTop;
            layoutParams.topToBottom = this.topToBottom;
            layoutParams.bottomToTop = this.bottomToTop;
            layoutParams.bottomToBottom = this.bottomToBottom;
            layoutParams.baselineToBaseline = this.baselineToBaseline;
            layoutParams.startToEnd = this.startToEnd;
            layoutParams.startToStart = this.startToStart;
            layoutParams.endToStart = this.endToStart;
            layoutParams.endToEnd = this.endToEnd;
            layoutParams.leftMargin = this.leftMargin;
            layoutParams.rightMargin = this.rightMargin;
            layoutParams.topMargin = this.topMargin;
            layoutParams.bottomMargin = this.bottomMargin;
            layoutParams.goneStartMargin = this.goneStartMargin;
            layoutParams.goneEndMargin = this.goneEndMargin;
            layoutParams.horizontalBias = this.horizontalBias;
            layoutParams.verticalBias = this.verticalBias;
            layoutParams.dimensionRatio = this.dimensionRatio;
            layoutParams.editorAbsoluteX = this.editorAbsoluteX;
            layoutParams.editorAbsoluteY = this.editorAbsoluteY;
            layoutParams.verticalWeight = this.verticalWeight;
            layoutParams.horizontalWeight = this.horizontalWeight;
            layoutParams.verticalChainStyle = this.verticalChainStyle;
            layoutParams.horizontalChainStyle = this.horizontalChainStyle;
            layoutParams.matchConstraintDefaultWidth = this.widthDefault;
            layoutParams.matchConstraintDefaultHeight = this.heightDefault;
            layoutParams.matchConstraintMaxWidth = this.widthMax;
            layoutParams.matchConstraintMaxHeight = this.heightMax;
            layoutParams.matchConstraintMinWidth = this.widthMin;
            layoutParams.matchConstraintMinHeight = this.heightMin;
            layoutParams.orientation = this.orientation;
            layoutParams.guidePercent = this.guidePercent;
            layoutParams.guideBegin = this.guideBegin;
            layoutParams.guideEnd = this.guideEnd;
            layoutParams.width = this.mWidth;
            layoutParams.height = this.mHeight;
            if (VERSION.SDK_INT >= 17) {
                layoutParams.setMarginStart(this.startMargin);
                layoutParams.setMarginEnd(this.endMargin);
            }
            layoutParams.validate();
        }
    }

    private String sideToString(int i) {
        switch (i) {
            case 1:
                return "left";
            case 2:
                return "right";
            case 3:
                return "top";
            case 4:
                return "bottom";
            case 5:
                return "baseline";
            case 6:
                return "start";
            case 7:
                return "end";
            default:
                return "undefined";
        }
    }

    static {
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_toLeftOf, 25);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_toRightOf, 26);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_toLeftOf, 29);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_toRightOf, 30);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_toTopOf, 36);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_toBottomOf, 35);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_toTopOf, 4);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_toBottomOf, 3);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBaseline_toBaselineOf, 1);
        mapToConstant.append(R.styleable.ConstraintSet_layout_editor_absoluteX, 6);
        mapToConstant.append(R.styleable.ConstraintSet_layout_editor_absoluteY, 7);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_begin, 17);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_end, 18);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_percent, 19);
        mapToConstant.append(R.styleable.ConstraintSet_android_orientation, 27);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintStart_toEndOf, 32);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintStart_toStartOf, 33);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintEnd_toStartOf, 10);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintEnd_toEndOf, 9);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginLeft, 13);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginTop, 16);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginRight, 14);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginBottom, 11);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginStart, 15);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginEnd, 12);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_weight, 40);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_weight, 39);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_chainStyle, 41);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_chainStyle, 42);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_bias, 20);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_bias, 37);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintDimensionRatio, 5);
        int i = 60;
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_creator, i);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_creator, i);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_creator, i);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_creator, i);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBaseline_creator, i);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginLeft, 24);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginRight, 28);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginStart, 31);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginEnd, 8);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginTop, 34);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginBottom, 2);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_width, 23);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_height, 21);
        mapToConstant.append(R.styleable.ConstraintSet_android_visibility, 22);
        mapToConstant.append(R.styleable.ConstraintSet_android_alpha, 43);
        mapToConstant.append(R.styleable.ConstraintSet_android_elevation, 44);
        mapToConstant.append(R.styleable.ConstraintSet_android_rotationX, 45);
        mapToConstant.append(R.styleable.ConstraintSet_android_rotationY, 46);
        mapToConstant.append(R.styleable.ConstraintSet_android_scaleX, 47);
        mapToConstant.append(R.styleable.ConstraintSet_android_scaleY, 48);
        mapToConstant.append(R.styleable.ConstraintSet_android_transformPivotX, 49);
        mapToConstant.append(R.styleable.ConstraintSet_android_transformPivotY, 50);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationX, 51);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationY, 52);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationZ, 53);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_default, 54);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_default, 55);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_max, 56);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_max, 57);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_min, 58);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_min, 59);
        mapToConstant.append(R.styleable.ConstraintSet_android_id, 38);
    }

    public void clone(Context context, int i) {
        clone((ConstraintLayout) LayoutInflater.from(context).inflate(i, null));
    }

    public void clone(ConstraintSet constraintSet) {
        this.mConstraints.clear();
        for (Integer num : constraintSet.mConstraints.keySet()) {
            this.mConstraints.put(num, ((Constraint) constraintSet.mConstraints.get(num)).clone());
        }
    }

    public void clone(ConstraintLayout constraintLayout) {
        int childCount = constraintLayout.getChildCount();
        this.mConstraints.clear();
        for (int i = 0; i < childCount; i++) {
            View childAt = constraintLayout.getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            int id = childAt.getId();
            if (!this.mConstraints.containsKey(Integer.valueOf(id))) {
                this.mConstraints.put(Integer.valueOf(id), new Constraint());
            }
            Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(id));
            constraint.fillFrom(id, layoutParams);
            constraint.visibility = childAt.getVisibility();
            if (VERSION.SDK_INT >= 17) {
                constraint.alpha = childAt.getAlpha();
                constraint.rotationX = childAt.getRotationX();
                constraint.rotationY = childAt.getRotationY();
                constraint.scaleX = childAt.getScaleX();
                constraint.scaleY = childAt.getScaleY();
                constraint.transformPivotX = childAt.getPivotX();
                constraint.transformPivotY = childAt.getPivotY();
                constraint.translationX = childAt.getTranslationX();
                constraint.translationY = childAt.getTranslationY();
                if (VERSION.SDK_INT >= 21) {
                    constraint.translationZ = childAt.getTranslationZ();
                    if (constraint.applyElevation) {
                        constraint.elevation = childAt.getElevation();
                    }
                }
            }
        }
    }

    public void applyTo(ConstraintLayout constraintLayout) {
        applyToInternal(constraintLayout);
        constraintLayout.setConstraintSet(null);
    }

    void applyToInternal(ConstraintLayout constraintLayout) {
        View childAt;
        int childCount = constraintLayout.getChildCount();
        HashSet hashSet = new HashSet(this.mConstraints.keySet());
        for (int i = 0; i < childCount; i++) {
            childAt = constraintLayout.getChildAt(i);
            int id = childAt.getId();
            if (this.mConstraints.containsKey(Integer.valueOf(id))) {
                hashSet.remove(Integer.valueOf(id));
                Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(id));
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                constraint.applyTo(layoutParams);
                childAt.setLayoutParams(layoutParams);
                childAt.setVisibility(constraint.visibility);
                if (VERSION.SDK_INT >= 17) {
                    childAt.setAlpha(constraint.alpha);
                    childAt.setRotationX(constraint.rotationX);
                    childAt.setRotationY(constraint.rotationY);
                    childAt.setScaleX(constraint.scaleX);
                    childAt.setScaleY(constraint.scaleY);
                    childAt.setPivotX(constraint.transformPivotX);
                    childAt.setPivotY(constraint.transformPivotY);
                    childAt.setTranslationX(constraint.translationX);
                    childAt.setTranslationY(constraint.translationY);
                    if (VERSION.SDK_INT >= 21) {
                        childAt.setTranslationZ(constraint.translationZ);
                        if (constraint.applyElevation) {
                            childAt.setElevation(constraint.elevation);
                        }
                    }
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            Constraint constraint2 = (Constraint) this.mConstraints.get(num);
            if (constraint2.mIsGuideline) {
                childAt = new Guideline(constraintLayout.getContext());
                childAt.setId(num.intValue());
                ViewGroup.LayoutParams generateDefaultLayoutParams = constraintLayout.generateDefaultLayoutParams();
                constraint2.applyTo(generateDefaultLayoutParams);
                constraintLayout.addView(childAt, generateDefaultLayoutParams);
            }
        }
    }

    public void center(int i, int i2, int i3, int i4, int i5, int i6, int i7, float f) {
        ConstraintSet constraintSet = this;
        int i8 = i3;
        float f2 = f;
        int i9;
        if (i4 < 0) {
            throw new IllegalArgumentException("margin must be > 0");
        } else if (i7 < 0) {
            throw new IllegalArgumentException("margin must be > 0");
        } else if (f2 <= 0.0f || f2 > 1.0f) {
            throw new IllegalArgumentException("bias must be between 0 and 1 inclusive");
        } else if (i8 == 1 || i8 == 2) {
            i9 = i;
            connect(i9, 1, i2, i8, i4);
            connect(i9, 2, i5, i6, i7);
            ((Constraint) constraintSet.mConstraints.get(Integer.valueOf(i))).horizontalBias = f2;
        } else if (i8 == 6 || i8 == 7) {
            i9 = i;
            connect(i9, 6, i2, i8, i4);
            connect(i9, 7, i5, i6, i7);
            ((Constraint) constraintSet.mConstraints.get(Integer.valueOf(i))).horizontalBias = f2;
        } else {
            i9 = i;
            connect(i9, 3, i2, i8, i4);
            connect(i9, 4, i5, i6, i7);
            ((Constraint) constraintSet.mConstraints.get(Integer.valueOf(i))).verticalBias = f2;
        }
    }

    public void centerHorizontally(int i, int i2, int i3, int i4, int i5, int i6, int i7, float f) {
        connect(i, 1, i2, i3, i4);
        connect(i, 2, i5, i6, i7);
        ((Constraint) this.mConstraints.get(Integer.valueOf(i))).horizontalBias = f;
    }

    public void centerHorizontallyRtl(int i, int i2, int i3, int i4, int i5, int i6, int i7, float f) {
        connect(i, 6, i2, i3, i4);
        connect(i, 7, i5, i6, i7);
        ((Constraint) this.mConstraints.get(Integer.valueOf(i))).horizontalBias = f;
    }

    public void centerVertically(int i, int i2, int i3, int i4, int i5, int i6, int i7, float f) {
        connect(i, 3, i2, i3, i4);
        connect(i, 4, i5, i6, i7);
        ((Constraint) this.mConstraints.get(Integer.valueOf(i))).verticalBias = f;
    }

    public void createVerticalChain(int i, int i2, int i3, int i4, int[] iArr, float[] fArr, int i5) {
        ConstraintSet constraintSet = this;
        int[] iArr2 = iArr;
        float[] fArr2 = fArr;
        if (iArr2.length < 2) {
            throw new IllegalArgumentException("must have 2 or more widgets in a chain");
        } else if (fArr2 == null || fArr2.length == iArr2.length) {
            int i6 = 0;
            if (fArr2 != null) {
                get(iArr2[i6]).verticalWeight = fArr2[i6];
            }
            get(iArr2[i6]).verticalChainStyle = i5;
            connect(iArr2[i6], 3, i, i2, 0);
            int i7 = 1;
            for (int i8 = i7; i8 < iArr2.length; i8++) {
                i6 = iArr2[i8];
                int i9 = i8 - 1;
                int i10 = 0;
                connect(iArr2[i8], 3, iArr2[i9], 4, i10);
                connect(iArr2[i9], 4, iArr2[i8], 3, i10);
                if (fArr2 != null) {
                    get(iArr2[i8]).verticalWeight = fArr2[i8];
                }
            }
            connect(iArr2[iArr2.length - i7], 4, i3, i4, 0);
        } else {
            throw new IllegalArgumentException("must have 2 or more widgets in a chain");
        }
    }

    public void createHorizontalChain(int i, int i2, int i3, int i4, int[] iArr, float[] fArr, int i5) {
        createHorizontalChain(i, i2, i3, i4, iArr, fArr, i5, 1, 2);
    }

    public void createHorizontalChainRtl(int i, int i2, int i3, int i4, int[] iArr, float[] fArr, int i5) {
        createHorizontalChain(i, i2, i3, i4, iArr, fArr, i5, 6, 7);
    }

    private void createHorizontalChain(int i, int i2, int i3, int i4, int[] iArr, float[] fArr, int i5, int i6, int i7) {
        ConstraintSet constraintSet = this;
        int[] iArr2 = iArr;
        float[] fArr2 = fArr;
        if (iArr2.length < 2) {
            throw new IllegalArgumentException("must have 2 or more widgets in a chain");
        } else if (fArr2 == null || fArr2.length == iArr2.length) {
            int i8 = 0;
            if (fArr2 != null) {
                get(iArr2[i8]).verticalWeight = fArr2[i8];
            }
            get(iArr2[i8]).horizontalChainStyle = i5;
            connect(iArr2[i8], i6, i, i2, -1);
            int i9 = 1;
            for (int i10 = i9; i10 < iArr2.length; i10++) {
                i8 = iArr2[i10];
                int i11 = i10 - 1;
                int i12 = -1;
                connect(iArr2[i10], i6, iArr2[i11], i7, i12);
                connect(iArr2[i11], i7, iArr2[i10], i6, i12);
                if (fArr2 != null) {
                    get(iArr2[i10]).horizontalWeight = fArr2[i10];
                }
            }
            connect(iArr2[iArr2.length - i9], i7, i3, i4, -1);
        } else {
            throw new IllegalArgumentException("must have 2 or more widgets in a chain");
        }
    }

    public void connect(int i, int i2, int i3, int i4, int i5) {
        if (!this.mConstraints.containsKey(Integer.valueOf(i))) {
            this.mConstraints.put(Integer.valueOf(i), new Constraint());
        }
        Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(i));
        int i6 = 2;
        int i7 = 1;
        int i8 = 3;
        int i9 = 4;
        int i10 = 6;
        int i11 = 7;
        int i12 = -1;
        StringBuilder stringBuilder;
        switch (i2) {
            case 1:
                if (i4 == i7) {
                    constraint.leftToLeft = i3;
                    constraint.leftToRight = i12;
                } else if (i4 == i6) {
                    constraint.leftToRight = i3;
                    constraint.leftToLeft = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Left to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.leftMargin = i5;
                return;
            case 2:
                if (i4 == i7) {
                    constraint.rightToLeft = i3;
                    constraint.rightToRight = i12;
                } else if (i4 == i6) {
                    constraint.rightToRight = i3;
                    constraint.rightToLeft = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.rightMargin = i5;
                return;
            case 3:
                if (i4 == i8) {
                    constraint.topToTop = i3;
                    constraint.topToBottom = i12;
                    constraint.baselineToBaseline = i12;
                } else if (i4 == i9) {
                    constraint.topToBottom = i3;
                    constraint.topToTop = i12;
                    constraint.baselineToBaseline = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.topMargin = i5;
                return;
            case 4:
                if (i4 == i9) {
                    constraint.bottomToBottom = i3;
                    constraint.bottomToTop = i12;
                    constraint.baselineToBaseline = i12;
                } else if (i4 == i8) {
                    constraint.bottomToTop = i3;
                    constraint.bottomToBottom = i12;
                    constraint.baselineToBaseline = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.bottomMargin = i5;
                return;
            case 5:
                if (i4 == 5) {
                    constraint.baselineToBaseline = i3;
                    constraint.bottomToBottom = i12;
                    constraint.bottomToTop = i12;
                    constraint.topToTop = i12;
                    constraint.topToBottom = i12;
                    return;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("right to ");
                stringBuilder.append(sideToString(i4));
                stringBuilder.append(" undefined");
                throw new IllegalArgumentException(stringBuilder.toString());
            case 6:
                if (i4 == i10) {
                    constraint.startToStart = i3;
                    constraint.startToEnd = i12;
                } else if (i4 == i11) {
                    constraint.startToEnd = i3;
                    constraint.startToStart = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.startMargin = i5;
                return;
            case 7:
                if (i4 == i11) {
                    constraint.endToEnd = i3;
                    constraint.endToStart = i12;
                } else if (i4 == i10) {
                    constraint.endToStart = i3;
                    constraint.endToEnd = i12;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                constraint.endMargin = i5;
                return;
            default:
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(sideToString(i2));
                stringBuilder2.append(" to ");
                stringBuilder2.append(sideToString(i4));
                stringBuilder2.append(" unknown");
                throw new IllegalArgumentException(stringBuilder2.toString());
        }
    }

    public void connect(int i, int i2, int i3, int i4) {
        if (!this.mConstraints.containsKey(Integer.valueOf(i))) {
            this.mConstraints.put(Integer.valueOf(i), new Constraint());
        }
        Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(i));
        int i5 = 2;
        int i6 = 1;
        int i7 = 3;
        int i8 = 4;
        int i9 = 6;
        int i10 = 7;
        int i11 = -1;
        StringBuilder stringBuilder;
        switch (i2) {
            case 1:
                if (i4 == i6) {
                    constraint.leftToLeft = i3;
                    constraint.leftToRight = i11;
                    return;
                } else if (i4 == i5) {
                    constraint.leftToRight = i3;
                    constraint.leftToLeft = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("left to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            case 2:
                if (i4 == i6) {
                    constraint.rightToLeft = i3;
                    constraint.rightToRight = i11;
                    return;
                } else if (i4 == i5) {
                    constraint.rightToRight = i3;
                    constraint.rightToLeft = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            case 3:
                if (i4 == i7) {
                    constraint.topToTop = i3;
                    constraint.topToBottom = i11;
                    constraint.baselineToBaseline = i11;
                    return;
                } else if (i4 == i8) {
                    constraint.topToBottom = i3;
                    constraint.topToTop = i11;
                    constraint.baselineToBaseline = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            case 4:
                if (i4 == i8) {
                    constraint.bottomToBottom = i3;
                    constraint.bottomToTop = i11;
                    constraint.baselineToBaseline = i11;
                    return;
                } else if (i4 == i7) {
                    constraint.bottomToTop = i3;
                    constraint.bottomToBottom = i11;
                    constraint.baselineToBaseline = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            case 5:
                if (i4 == 5) {
                    constraint.baselineToBaseline = i3;
                    constraint.bottomToBottom = i11;
                    constraint.bottomToTop = i11;
                    constraint.topToTop = i11;
                    constraint.topToBottom = i11;
                    return;
                }
                stringBuilder = new StringBuilder();
                stringBuilder.append("right to ");
                stringBuilder.append(sideToString(i4));
                stringBuilder.append(" undefined");
                throw new IllegalArgumentException(stringBuilder.toString());
            case 6:
                if (i4 == i9) {
                    constraint.startToStart = i3;
                    constraint.startToEnd = i11;
                    return;
                } else if (i4 == i10) {
                    constraint.startToEnd = i3;
                    constraint.startToStart = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            case 7:
                if (i4 == i10) {
                    constraint.endToEnd = i3;
                    constraint.endToStart = i11;
                    return;
                } else if (i4 == i9) {
                    constraint.endToStart = i3;
                    constraint.endToEnd = i11;
                    return;
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("right to ");
                    stringBuilder.append(sideToString(i4));
                    stringBuilder.append(" undefined");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
            default:
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(sideToString(i2));
                stringBuilder2.append(" to ");
                stringBuilder2.append(sideToString(i4));
                stringBuilder2.append(" unknown");
                throw new IllegalArgumentException(stringBuilder2.toString());
        }
    }

    public void centerHorizontally(int i, int i2) {
        if (i2 == 0) {
            center(i, 0, 1, 0, 0, 2, 0, 0.5f);
            return;
        }
        center(i, i2, 2, 0, i2, 1, 0, 0.5f);
    }

    public void centerHorizontallyRtl(int i, int i2) {
        if (i2 == 0) {
            center(i, 0, 6, 0, 0, 7, 0, 0.5f);
            return;
        }
        center(i, i2, 7, 0, i2, 6, 0, 0.5f);
    }

    public void centerVertically(int i, int i2) {
        if (i2 == 0) {
            center(i, 0, 3, 0, 0, 4, 0, 0.5f);
            return;
        }
        center(i, i2, 4, 0, i2, 3, 0, 0.5f);
    }

    public void clear(int i) {
        this.mConstraints.remove(Integer.valueOf(i));
    }

    public void clear(int i, int i2) {
        if (this.mConstraints.containsKey(Integer.valueOf(i))) {
            Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(i));
            int i3 = -1;
            switch (i2) {
                case 1:
                    constraint.leftToRight = i3;
                    constraint.leftToLeft = i3;
                    constraint.leftMargin = i3;
                    constraint.goneLeftMargin = i3;
                    return;
                case 2:
                    constraint.rightToRight = i3;
                    constraint.rightToLeft = i3;
                    constraint.rightMargin = i3;
                    constraint.goneRightMargin = i3;
                    return;
                case 3:
                    constraint.topToBottom = i3;
                    constraint.topToTop = i3;
                    constraint.topMargin = i3;
                    constraint.goneTopMargin = i3;
                    return;
                case 4:
                    constraint.bottomToTop = i3;
                    constraint.bottomToBottom = i3;
                    constraint.bottomMargin = i3;
                    constraint.goneBottomMargin = i3;
                    return;
                case 5:
                    constraint.baselineToBaseline = i3;
                    return;
                case 6:
                    constraint.startToEnd = i3;
                    constraint.startToStart = i3;
                    constraint.startMargin = i3;
                    constraint.goneStartMargin = i3;
                    return;
                case 7:
                    constraint.endToStart = i3;
                    constraint.endToEnd = i3;
                    constraint.endMargin = i3;
                    constraint.goneEndMargin = i3;
                    return;
                default:
                    throw new IllegalArgumentException("unknown constraint");
            }
        }
    }

    public void setMargin(int i, int i2, int i3) {
        Constraint constraint = get(i);
        switch (i2) {
            case 1:
                constraint.leftMargin = i3;
                return;
            case 2:
                constraint.rightMargin = i3;
                return;
            case 3:
                constraint.topMargin = i3;
                return;
            case 4:
                constraint.bottomMargin = i3;
                return;
            case 5:
                throw new IllegalArgumentException("baseline does not support margins");
            case 6:
                constraint.startMargin = i3;
                return;
            case 7:
                constraint.endMargin = i3;
                return;
            default:
                throw new IllegalArgumentException("unknown constraint");
        }
    }

    public void setGoneMargin(int i, int i2, int i3) {
        Constraint constraint = get(i);
        switch (i2) {
            case 1:
                constraint.goneLeftMargin = i3;
                return;
            case 2:
                constraint.goneRightMargin = i3;
                return;
            case 3:
                constraint.goneTopMargin = i3;
                return;
            case 4:
                constraint.goneBottomMargin = i3;
                return;
            case 5:
                throw new IllegalArgumentException("baseline does not support margins");
            case 6:
                constraint.goneStartMargin = i3;
                return;
            case 7:
                constraint.goneEndMargin = i3;
                return;
            default:
                throw new IllegalArgumentException("unknown constraint");
        }
    }

    public void setHorizontalBias(int i, float f) {
        get(i).horizontalBias = f;
    }

    public void setVerticalBias(int i, float f) {
        get(i).verticalBias = f;
    }

    public void setDimensionRatio(int i, String str) {
        get(i).dimensionRatio = str;
    }

    public void setVisibility(int i, int i2) {
        get(i).visibility = i2;
    }

    public void setAlpha(int i, float f) {
        get(i).alpha = f;
    }

    public boolean getApplyElevation(int i) {
        return get(i).applyElevation;
    }

    public void setApplyElevation(int i, boolean z) {
        get(i).applyElevation = z;
    }

    public void setElevation(int i, float f) {
        get(i).elevation = f;
        get(i).applyElevation = true;
    }

    public void setRotationX(int i, float f) {
        get(i).rotationX = f;
    }

    public void setRotationY(int i, float f) {
        get(i).rotationY = f;
    }

    public void setScaleX(int i, float f) {
        get(i).scaleX = f;
    }

    public void setScaleY(int i, float f) {
        get(i).scaleY = f;
    }

    public void setTransformPivotX(int i, float f) {
        get(i).transformPivotX = f;
    }

    public void setTransformPivotY(int i, float f) {
        get(i).transformPivotY = f;
    }

    public void setTransformPivot(int i, float f, float f2) {
        Constraint constraint = get(i);
        constraint.transformPivotY = f2;
        constraint.transformPivotX = f;
    }

    public void setTranslationX(int i, float f) {
        get(i).translationX = f;
    }

    public void setTranslationY(int i, float f) {
        get(i).translationY = f;
    }

    public void setTranslation(int i, float f, float f2) {
        Constraint constraint = get(i);
        constraint.translationX = f;
        constraint.translationY = f2;
    }

    public void setTranslationZ(int i, float f) {
        get(i).translationZ = f;
    }

    public void constrainHeight(int i, int i2) {
        get(i).mHeight = i2;
    }

    public void constrainWidth(int i, int i2) {
        get(i).mWidth = i2;
    }

    public void constrainMaxHeight(int i, int i2) {
        get(i).heightMax = i2;
    }

    public void constrainMaxWidth(int i, int i2) {
        get(i).widthMax = i2;
    }

    public void constrainMinHeight(int i, int i2) {
        get(i).heightMin = i2;
    }

    public void constrainMinWidth(int i, int i2) {
        get(i).widthMin = i2;
    }

    public void constrainDefaultHeight(int i, int i2) {
        get(i).heightDefault = i2;
    }

    public void constrainDefaultWidth(int i, int i2) {
        get(i).widthDefault = i2;
    }

    public void setHorizontalWeight(int i, float f) {
        get(i).horizontalWeight = f;
    }

    public void setVerticalWeight(int i, float f) {
        get(i).verticalWeight = f;
    }

    public void setHorizontalChainStyle(int i, int i2) {
        get(i).horizontalChainStyle = i2;
    }

    public void setVerticalChainStyle(int i, int i2) {
        get(i).verticalChainStyle = i2;
    }

    public void addToHorizontalChain(int i, int i2, int i3) {
        int i4 = 2;
        int i5 = 1;
        connect(i, 1, i2, i2 == 0 ? i5 : i4, 0);
        connect(i, 2, i3, i3 == 0 ? i4 : i5, 0);
        if (i2 != 0) {
            connect(i2, 2, i, 1, 0);
        }
        if (i3 != 0) {
            connect(i3, 1, i, 2, 0);
        }
    }

    public void addToHorizontalChainRTL(int i, int i2, int i3) {
        int i4 = 7;
        int i5 = 6;
        connect(i, 6, i2, i2 == 0 ? i5 : i4, 0);
        connect(i, 7, i3, i3 == 0 ? i4 : i5, 0);
        if (i2 != 0) {
            connect(i2, 7, i, 6, 0);
        }
        if (i3 != 0) {
            connect(i3, 6, i, 7, 0);
        }
    }

    public void addToVerticalChain(int i, int i2, int i3) {
        int i4 = 4;
        int i5 = 3;
        connect(i, 3, i2, i2 == 0 ? i5 : i4, 0);
        connect(i, 4, i3, i3 == 0 ? i4 : i5, 0);
        if (i2 != 0) {
            connect(i2, 4, i, 3, 0);
        }
        if (i2 != 0) {
            connect(i3, 3, i, 4, 0);
        }
    }

    public void removeFromVerticalChain(int i) {
        if (this.mConstraints.containsKey(Integer.valueOf(i))) {
            Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(i));
            int i2 = constraint.topToBottom;
            int i3 = constraint.bottomToTop;
            int i4 = -1;
            if (!(i2 == i4 && i3 == i4)) {
                if (i2 != i4 && i3 != i4) {
                    int i5 = 0;
                    connect(i2, 4, i3, 3, i5);
                    connect(i3, 3, i2, 4, i5);
                } else if (!(i2 == i4 && i3 == i4)) {
                    if (constraint.bottomToBottom != i4) {
                        connect(i2, 4, constraint.bottomToBottom, 4, 0);
                    } else if (constraint.topToTop != i4) {
                        connect(i3, 3, constraint.topToTop, 3, 0);
                    }
                }
            }
        }
        clear(i, 3);
        clear(i, 4);
    }

    public void removeFromHorizontalChain(int i) {
        if (this.mConstraints.containsKey(Integer.valueOf(i))) {
            Constraint constraint = (Constraint) this.mConstraints.get(Integer.valueOf(i));
            int i2 = constraint.leftToRight;
            int i3 = constraint.rightToLeft;
            int i4 = -1;
            int i5;
            if (i2 == i4 && i3 == i4) {
                int i6 = constraint.startToEnd;
                i3 = constraint.endToStart;
                if (!(i6 == i4 && i3 == i4)) {
                    if (i6 != i4 && i3 != i4) {
                        i5 = 0;
                        connect(i6, 7, i3, 6, i5);
                        connect(i3, 6, i2, 7, i5);
                    } else if (!(i2 == i4 && i3 == i4)) {
                        if (constraint.rightToRight != i4) {
                            connect(i2, 7, constraint.rightToRight, 7, 0);
                        } else if (constraint.leftToLeft != i4) {
                            connect(i3, 6, constraint.leftToLeft, 6, 0);
                        }
                    }
                }
                clear(i, 6);
                clear(i, 7);
                return;
            }
            if (i2 != i4 && i3 != i4) {
                i5 = 0;
                connect(i2, 2, i3, 1, i5);
                connect(i3, 1, i2, 2, i5);
            } else if (!(i2 == i4 && i3 == i4)) {
                if (constraint.rightToRight != i4) {
                    connect(i2, 2, constraint.rightToRight, 2, 0);
                } else if (constraint.leftToLeft != i4) {
                    connect(i3, 1, constraint.leftToLeft, 1, 0);
                }
            }
            clear(i, 1);
            clear(i, 2);
        }
    }

    public void create(int i, int i2) {
        Constraint constraint = get(i);
        constraint.mIsGuideline = true;
        constraint.orientation = i2;
    }

    public void setGuidelineBegin(int i, int i2) {
        get(i).guideBegin = i2;
        get(i).guideEnd = -1;
        get(i).guidePercent = -1.0f;
    }

    public void setGuidelineEnd(int i, int i2) {
        get(i).guideEnd = i2;
        get(i).guideBegin = -1;
        get(i).guidePercent = -1.0f;
    }

    public void setGuidelinePercent(int i, float f) {
        get(i).guidePercent = f;
        int i2 = -1;
        get(i).guideEnd = i2;
        get(i).guideBegin = i2;
    }

    private Constraint get(int i) {
        if (!this.mConstraints.containsKey(Integer.valueOf(i))) {
            this.mConstraints.put(Integer.valueOf(i), new Constraint());
        }
        return (Constraint) this.mConstraints.get(Integer.valueOf(i));
    }

    public void load(Context context, int i) {
        XmlPullParser xml = context.getResources().getXml(i);
        try {
            boolean eventType = xml.getEventType();
            while (true) {
                boolean z = true;
                if (eventType != z) {
                    if (eventType) {
                        switch (eventType) {
                            case true:
                                String name = xml.getName();
                                Constraint fillFromAttributeList = fillFromAttributeList(context, Xml.asAttributeSet(xml));
                                if (name.equalsIgnoreCase("Guideline")) {
                                    fillFromAttributeList.mIsGuideline = z;
                                }
                                this.mConstraints.put(Integer.valueOf(fillFromAttributeList.mViewId), fillFromAttributeList);
                                break;
                        }
                    }
                    xml.getName();
                    eventType = xml.next();
                } else {
                    return;
                }
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static int lookupID(TypedArray typedArray, int i, int i2) {
        i2 = typedArray.getResourceId(i, i2);
        int i3 = -1;
        return i2 == i3 ? typedArray.getInt(i, i3) : i2;
    }

    private Constraint fillFromAttributeList(Context context, AttributeSet attributeSet) {
        Constraint constraint = new Constraint();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ConstraintSet);
        populateConstraint(constraint, obtainStyledAttributes);
        obtainStyledAttributes.recycle();
        return constraint;
    }

    private void populateConstraint(Constraint constraint, TypedArray typedArray) {
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            int i2 = mapToConstant.get(index);
            StringBuilder stringBuilder;
            if (i2 != 60) {
                switch (i2) {
                    case 1:
                        constraint.baselineToBaseline = lookupID(typedArray, index, constraint.baselineToBaseline);
                        break;
                    case 2:
                        constraint.bottomMargin = typedArray.getDimensionPixelSize(index, constraint.bottomMargin);
                        break;
                    case 3:
                        constraint.bottomToBottom = lookupID(typedArray, index, constraint.bottomToBottom);
                        break;
                    case 4:
                        constraint.bottomToTop = lookupID(typedArray, index, constraint.bottomToTop);
                        break;
                    case 5:
                        constraint.dimensionRatio = typedArray.getString(index);
                        break;
                    case 6:
                        constraint.editorAbsoluteX = typedArray.getDimensionPixelOffset(index, constraint.editorAbsoluteX);
                        break;
                    case 7:
                        constraint.editorAbsoluteY = typedArray.getDimensionPixelOffset(index, constraint.editorAbsoluteY);
                        break;
                    case 8:
                        constraint.endMargin = typedArray.getDimensionPixelSize(index, constraint.endMargin);
                        break;
                    case 9:
                        constraint.bottomToTop = lookupID(typedArray, index, constraint.endToEnd);
                        break;
                    case 10:
                        constraint.endToStart = lookupID(typedArray, index, constraint.endToStart);
                        break;
                    case 11:
                        constraint.goneBottomMargin = typedArray.getDimensionPixelSize(index, constraint.goneBottomMargin);
                        break;
                    case 12:
                        constraint.goneEndMargin = typedArray.getDimensionPixelSize(index, constraint.goneEndMargin);
                        break;
                    case 13:
                        constraint.goneLeftMargin = typedArray.getDimensionPixelSize(index, constraint.goneLeftMargin);
                        break;
                    case 14:
                        constraint.goneRightMargin = typedArray.getDimensionPixelSize(index, constraint.goneRightMargin);
                        break;
                    case 15:
                        constraint.goneStartMargin = typedArray.getDimensionPixelSize(index, constraint.goneStartMargin);
                        break;
                    case 16:
                        constraint.goneTopMargin = typedArray.getDimensionPixelSize(index, constraint.goneTopMargin);
                        break;
                    case 17:
                        constraint.guideBegin = typedArray.getDimensionPixelOffset(index, constraint.guideBegin);
                        break;
                    case 18:
                        constraint.guideEnd = typedArray.getDimensionPixelOffset(index, constraint.guideEnd);
                        break;
                    case 19:
                        constraint.guidePercent = typedArray.getFloat(index, constraint.guidePercent);
                        break;
                    case 20:
                        constraint.horizontalBias = typedArray.getFloat(index, constraint.horizontalBias);
                        break;
                    case 21:
                        constraint.mHeight = typedArray.getLayoutDimension(index, constraint.mHeight);
                        break;
                    case 22:
                        constraint.visibility = typedArray.getInt(index, constraint.visibility);
                        constraint.visibility = VISIBILITY_FLAGS[constraint.visibility];
                        break;
                    case 23:
                        constraint.mWidth = typedArray.getLayoutDimension(index, constraint.mWidth);
                        break;
                    case 24:
                        constraint.leftMargin = typedArray.getDimensionPixelSize(index, constraint.leftMargin);
                        break;
                    case 25:
                        constraint.leftToLeft = lookupID(typedArray, index, constraint.leftToLeft);
                        break;
                    case 26:
                        constraint.leftToRight = lookupID(typedArray, index, constraint.leftToRight);
                        break;
                    case 27:
                        constraint.orientation = typedArray.getInt(index, constraint.orientation);
                        break;
                    case 28:
                        constraint.rightMargin = typedArray.getDimensionPixelSize(index, constraint.rightMargin);
                        break;
                    case 29:
                        constraint.rightToLeft = lookupID(typedArray, index, constraint.rightToLeft);
                        break;
                    case 30:
                        constraint.rightToRight = lookupID(typedArray, index, constraint.rightToRight);
                        break;
                    case 31:
                        constraint.startMargin = typedArray.getDimensionPixelSize(index, constraint.startMargin);
                        break;
                    case 32:
                        constraint.startToEnd = lookupID(typedArray, index, constraint.startToEnd);
                        break;
                    case 33:
                        constraint.startToStart = lookupID(typedArray, index, constraint.startToStart);
                        break;
                    case 34:
                        constraint.topMargin = typedArray.getDimensionPixelSize(index, constraint.topMargin);
                        break;
                    case 35:
                        constraint.topToBottom = lookupID(typedArray, index, constraint.topToBottom);
                        break;
                    case 36:
                        constraint.topToTop = lookupID(typedArray, index, constraint.topToTop);
                        break;
                    case 37:
                        constraint.verticalBias = typedArray.getFloat(index, constraint.verticalBias);
                        break;
                    case 38:
                        constraint.mViewId = typedArray.getResourceId(index, constraint.mViewId);
                        break;
                    case 39:
                        constraint.horizontalWeight = typedArray.getFloat(index, constraint.horizontalWeight);
                        break;
                    case 40:
                        constraint.verticalWeight = typedArray.getFloat(index, constraint.verticalWeight);
                        break;
                    case 41:
                        constraint.horizontalChainStyle = typedArray.getInt(index, constraint.horizontalChainStyle);
                        break;
                    case 42:
                        constraint.verticalChainStyle = typedArray.getInt(index, constraint.verticalChainStyle);
                        break;
                    case 43:
                        constraint.alpha = typedArray.getFloat(index, constraint.alpha);
                        break;
                    case 44:
                        constraint.applyElevation = true;
                        constraint.elevation = typedArray.getFloat(index, constraint.elevation);
                        break;
                    case 45:
                        constraint.rotationX = typedArray.getFloat(index, constraint.rotationX);
                        break;
                    case 46:
                        constraint.rotationY = typedArray.getFloat(index, constraint.rotationY);
                        break;
                    case 47:
                        constraint.scaleX = typedArray.getFloat(index, constraint.scaleX);
                        break;
                    case 48:
                        constraint.scaleY = typedArray.getFloat(index, constraint.scaleY);
                        break;
                    case 49:
                        constraint.transformPivotX = typedArray.getFloat(index, constraint.transformPivotX);
                        break;
                    case 50:
                        constraint.transformPivotY = typedArray.getFloat(index, constraint.transformPivotY);
                        break;
                    case 51:
                        constraint.translationX = typedArray.getFloat(index, constraint.translationX);
                        break;
                    case 52:
                        constraint.translationY = typedArray.getFloat(index, constraint.translationY);
                        break;
                    case 53:
                        constraint.translationZ = typedArray.getFloat(index, constraint.translationZ);
                        break;
                    default:
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown attribute 0x");
                        stringBuilder.append(Integer.toHexString(index));
                        stringBuilder.append("   ");
                        stringBuilder.append(mapToConstant.get(index));
                        Log.w("ConstraintSet", stringBuilder.toString());
                        break;
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("unused attribute 0x");
            stringBuilder.append(Integer.toHexString(index));
            stringBuilder.append("   ");
            stringBuilder.append(mapToConstant.get(index));
            Log.w("ConstraintSet", stringBuilder.toString());
        }
    }
}
