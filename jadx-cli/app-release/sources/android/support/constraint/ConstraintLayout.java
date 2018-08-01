package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.constraint.solver.widgets.ConstraintAnchor.Strength;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.ArrayList;

public class ConstraintLayout extends ViewGroup {
    static final boolean ALLOWS_EMBEDDED = false;
    private static final boolean SIMPLE_LAYOUT = true;
    private static final String TAG = "ConstraintLayout";
    public static final String VERSION = "ConstraintLayout-1.0.0";
    SparseArray<View> mChildrenByIds = new SparseArray();
    private ConstraintSet mConstraintSet;
    private boolean mDirtyHierarchy;
    ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
    private int mMaxHeight;
    private int mMaxWidth;
    private int mMinHeight;
    private int mMinWidth;
    private int mOptimizationLevel;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList(100);

    public static class LayoutParams extends MarginLayoutParams {
        public static final int BASELINE = 5;
        public static final int BOTTOM = 4;
        public static final int CHAIN_PACKED = 2;
        public static final int CHAIN_SPREAD = 0;
        public static final int CHAIN_SPREAD_INSIDE = 1;
        public static final int END = 7;
        public static final int HORIZONTAL = 0;
        public static final int LEFT = 1;
        public static final int MATCH_CONSTRAINT = 0;
        public static final int MATCH_CONSTRAINT_SPREAD = 0;
        public static final int MATCH_CONSTRAINT_WRAP = 1;
        public static final int PARENT_ID = 0;
        public static final int RIGHT = 2;
        public static final int START = 6;
        public static final int TOP = 3;
        public static final int UNSET = -1;
        public static final int VERTICAL = 1;
        public int baselineToBaseline;
        public int bottomToBottom;
        public int bottomToTop;
        public String dimensionRatio;
        int dimensionRatioSide;
        float dimensionRatioValue;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
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
        public float guidePercent = -1.0f;
        public float horizontalBias;
        public int horizontalChainStyle;
        boolean horizontalDimensionFixed;
        public float horizontalWeight;
        boolean isGuideline;
        public int leftToLeft;
        public int leftToRight;
        public int matchConstraintDefaultHeight;
        public int matchConstraintDefaultWidth;
        public int matchConstraintMaxHeight;
        public int matchConstraintMaxWidth;
        public int matchConstraintMinHeight;
        public int matchConstraintMinWidth;
        boolean needsBaseline;
        public int orientation;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int resolvedLeftToRight;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        public int rightToLeft;
        public int rightToRight;
        public int startToEnd;
        public int startToStart;
        public int topToBottom;
        public int topToTop;
        public float verticalBias;
        public int verticalChainStyle;
        boolean verticalDimensionFixed;
        public float verticalWeight;
        ConstraintWidget widget;

        public LayoutParams(LayoutParams layoutParams) {
            super(layoutParams);
            int i = -1;
            this.guideBegin = i;
            this.guideEnd = i;
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
            this.goneLeftMargin = i;
            this.goneTopMargin = i;
            this.goneRightMargin = i;
            this.goneBottomMargin = i;
            this.goneStartMargin = i;
            this.goneEndMargin = i;
            float f = 0.5f;
            this.horizontalBias = f;
            this.verticalBias = f;
            this.dimensionRatio = null;
            float f2 = 0.0f;
            this.dimensionRatioValue = f2;
            boolean z = true;
            this.dimensionRatioSide = z;
            this.horizontalWeight = f2;
            this.verticalWeight = f2;
            boolean z2 = false;
            this.horizontalChainStyle = z2;
            this.verticalChainStyle = z2;
            this.matchConstraintDefaultWidth = z2;
            this.matchConstraintDefaultHeight = z2;
            this.matchConstraintMinWidth = z2;
            this.matchConstraintMinHeight = z2;
            this.matchConstraintMaxWidth = z2;
            this.matchConstraintMaxHeight = z2;
            this.editorAbsoluteX = i;
            this.editorAbsoluteY = i;
            this.orientation = i;
            this.horizontalDimensionFixed = z;
            this.verticalDimensionFixed = z;
            this.needsBaseline = z2;
            this.isGuideline = z2;
            this.resolvedLeftToLeft = i;
            this.resolvedLeftToRight = i;
            this.resolvedRightToLeft = i;
            this.resolvedRightToRight = i;
            this.resolveGoneLeftMargin = i;
            this.resolveGoneRightMargin = i;
            this.resolvedHorizontalBias = f;
            this.widget = new ConstraintWidget();
            this.guideBegin = layoutParams.guideBegin;
            this.guideEnd = layoutParams.guideEnd;
            this.guidePercent = layoutParams.guidePercent;
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
            this.goneLeftMargin = layoutParams.goneLeftMargin;
            this.goneTopMargin = layoutParams.goneTopMargin;
            this.goneRightMargin = layoutParams.goneRightMargin;
            this.goneBottomMargin = layoutParams.goneBottomMargin;
            this.goneStartMargin = layoutParams.goneStartMargin;
            this.goneEndMargin = layoutParams.goneEndMargin;
            this.horizontalBias = layoutParams.horizontalBias;
            this.verticalBias = layoutParams.verticalBias;
            this.dimensionRatio = layoutParams.dimensionRatio;
            this.dimensionRatioValue = layoutParams.dimensionRatioValue;
            this.dimensionRatioSide = layoutParams.dimensionRatioSide;
            this.horizontalWeight = layoutParams.horizontalWeight;
            this.verticalWeight = layoutParams.verticalWeight;
            this.horizontalChainStyle = layoutParams.horizontalChainStyle;
            this.verticalChainStyle = layoutParams.verticalChainStyle;
            this.matchConstraintDefaultWidth = layoutParams.matchConstraintDefaultWidth;
            this.matchConstraintDefaultHeight = layoutParams.matchConstraintDefaultHeight;
            this.matchConstraintMinWidth = layoutParams.matchConstraintMinWidth;
            this.matchConstraintMaxWidth = layoutParams.matchConstraintMaxWidth;
            this.matchConstraintMinHeight = layoutParams.matchConstraintMinHeight;
            this.matchConstraintMaxHeight = layoutParams.matchConstraintMaxHeight;
            this.editorAbsoluteX = layoutParams.editorAbsoluteX;
            this.editorAbsoluteY = layoutParams.editorAbsoluteY;
            this.orientation = layoutParams.orientation;
            this.horizontalDimensionFixed = layoutParams.horizontalDimensionFixed;
            this.verticalDimensionFixed = layoutParams.verticalDimensionFixed;
            this.needsBaseline = layoutParams.needsBaseline;
            this.isGuideline = layoutParams.isGuideline;
            this.resolvedLeftToLeft = layoutParams.resolvedLeftToLeft;
            this.resolvedLeftToRight = layoutParams.resolvedLeftToRight;
            this.resolvedRightToLeft = layoutParams.resolvedRightToLeft;
            this.resolvedRightToRight = layoutParams.resolvedRightToRight;
            this.resolveGoneLeftMargin = layoutParams.resolveGoneLeftMargin;
            this.resolveGoneRightMargin = layoutParams.resolveGoneRightMargin;
            this.resolvedHorizontalBias = layoutParams.resolvedHorizontalBias;
            this.widget = layoutParams.widget;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            int i = -1;
            this.guideBegin = i;
            this.guideEnd = i;
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
            this.goneLeftMargin = i;
            this.goneTopMargin = i;
            this.goneRightMargin = i;
            this.goneBottomMargin = i;
            this.goneStartMargin = i;
            this.goneEndMargin = i;
            float f = 0.5f;
            this.horizontalBias = f;
            this.verticalBias = f;
            this.dimensionRatio = null;
            float f2 = 0.0f;
            this.dimensionRatioValue = f2;
            boolean z = true;
            this.dimensionRatioSide = z;
            this.horizontalWeight = f2;
            this.verticalWeight = f2;
            boolean z2 = false;
            this.horizontalChainStyle = z2;
            this.verticalChainStyle = z2;
            this.matchConstraintDefaultWidth = z2;
            this.matchConstraintDefaultHeight = z2;
            this.matchConstraintMinWidth = z2;
            this.matchConstraintMinHeight = z2;
            this.matchConstraintMaxWidth = z2;
            this.matchConstraintMaxHeight = z2;
            this.editorAbsoluteX = i;
            this.editorAbsoluteY = i;
            this.orientation = i;
            this.horizontalDimensionFixed = z;
            this.verticalDimensionFixed = z;
            this.needsBaseline = z2;
            this.isGuideline = z2;
            this.resolvedLeftToLeft = i;
            this.resolvedLeftToRight = i;
            this.resolvedRightToLeft = i;
            this.resolvedRightToRight = i;
            this.resolveGoneLeftMargin = i;
            this.resolveGoneRightMargin = i;
            this.resolvedHorizontalBias = f;
            this.widget = new ConstraintWidget();
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            int i2 = z2;
            while (i2 < indexCount) {
                int index = obtainStyledAttributes.getIndex(i2);
                if (index == R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf) {
                    this.leftToLeft = obtainStyledAttributes.getResourceId(index, this.leftToLeft);
                    if (this.leftToLeft == i) {
                        this.leftToLeft = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf) {
                    this.leftToRight = obtainStyledAttributes.getResourceId(index, this.leftToRight);
                    if (this.leftToRight == i) {
                        this.leftToRight = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf) {
                    this.rightToLeft = obtainStyledAttributes.getResourceId(index, this.rightToLeft);
                    if (this.rightToLeft == i) {
                        this.rightToLeft = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf) {
                    this.rightToRight = obtainStyledAttributes.getResourceId(index, this.rightToRight);
                    if (this.rightToRight == i) {
                        this.rightToRight = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf) {
                    this.topToTop = obtainStyledAttributes.getResourceId(index, this.topToTop);
                    if (this.topToTop == i) {
                        this.topToTop = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf) {
                    this.topToBottom = obtainStyledAttributes.getResourceId(index, this.topToBottom);
                    if (this.topToBottom == i) {
                        this.topToBottom = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf) {
                    this.bottomToTop = obtainStyledAttributes.getResourceId(index, this.bottomToTop);
                    if (this.bottomToTop == i) {
                        this.bottomToTop = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf) {
                    this.bottomToBottom = obtainStyledAttributes.getResourceId(index, this.bottomToBottom);
                    if (this.bottomToBottom == i) {
                        this.bottomToBottom = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf) {
                    this.baselineToBaseline = obtainStyledAttributes.getResourceId(index, this.baselineToBaseline);
                    if (this.baselineToBaseline == i) {
                        this.baselineToBaseline = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX) {
                    this.editorAbsoluteX = obtainStyledAttributes.getDimensionPixelOffset(index, this.editorAbsoluteX);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY) {
                    this.editorAbsoluteY = obtainStyledAttributes.getDimensionPixelOffset(index, this.editorAbsoluteY);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin) {
                    this.guideBegin = obtainStyledAttributes.getDimensionPixelOffset(index, this.guideBegin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end) {
                    this.guideEnd = obtainStyledAttributes.getDimensionPixelOffset(index, this.guideEnd);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent) {
                    this.guidePercent = obtainStyledAttributes.getFloat(index, this.guidePercent);
                } else if (index == R.styleable.ConstraintLayout_Layout_android_orientation) {
                    this.orientation = obtainStyledAttributes.getInt(index, this.orientation);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf) {
                    this.startToEnd = obtainStyledAttributes.getResourceId(index, this.startToEnd);
                    if (this.startToEnd == i) {
                        this.startToEnd = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf) {
                    this.startToStart = obtainStyledAttributes.getResourceId(index, this.startToStart);
                    if (this.startToStart == i) {
                        this.startToStart = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf) {
                    this.endToStart = obtainStyledAttributes.getResourceId(index, this.endToStart);
                    if (this.endToStart == i) {
                        this.endToStart = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf) {
                    this.endToEnd = obtainStyledAttributes.getResourceId(index, this.endToEnd);
                    if (this.endToEnd == i) {
                        this.endToEnd = obtainStyledAttributes.getInt(index, i);
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft) {
                    this.goneLeftMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneLeftMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginTop) {
                    this.goneTopMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneTopMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginRight) {
                    this.goneRightMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneRightMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom) {
                    this.goneBottomMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneBottomMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginStart) {
                    this.goneStartMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneStartMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd) {
                    this.goneEndMargin = obtainStyledAttributes.getDimensionPixelSize(index, this.goneEndMargin);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias) {
                    this.horizontalBias = obtainStyledAttributes.getFloat(index, this.horizontalBias);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias) {
                    this.verticalBias = obtainStyledAttributes.getFloat(index, this.verticalBias);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio) {
                    this.dimensionRatio = obtainStyledAttributes.getString(index);
                    this.dimensionRatioValue = Float.NaN;
                    this.dimensionRatioSide = i;
                    if (this.dimensionRatio != null) {
                        index = this.dimensionRatio.length();
                        int indexOf = this.dimensionRatio.indexOf(44);
                        if (indexOf <= 0 || indexOf >= index - 1) {
                            indexOf = z2;
                        } else {
                            String substring = this.dimensionRatio.substring(z2, indexOf);
                            if (substring.equalsIgnoreCase("W")) {
                                this.dimensionRatioSide = z2;
                            } else if (substring.equalsIgnoreCase("H")) {
                                this.dimensionRatioSide = z;
                            }
                            indexOf++;
                        }
                        int indexOf2 = this.dimensionRatio.indexOf(58);
                        String substring2;
                        if (indexOf2 < 0 || indexOf2 >= index - 1) {
                            substring2 = this.dimensionRatio.substring(indexOf);
                            if (substring2.length() > 0) {
                                this.dimensionRatioValue = Float.parseFloat(substring2);
                            }
                        } else {
                            substring2 = this.dimensionRatio.substring(indexOf, indexOf2);
                            String substring3 = this.dimensionRatio.substring(indexOf2 + 1);
                            if (substring2.length() > 0 && substring3.length() > 0) {
                                try {
                                    float parseFloat = Float.parseFloat(substring2);
                                    float parseFloat2 = Float.parseFloat(substring3);
                                    if (parseFloat > f2 && parseFloat2 > f2) {
                                        if (this.dimensionRatioSide == z) {
                                            this.dimensionRatioValue = Math.abs(parseFloat2 / parseFloat);
                                        } else {
                                            this.dimensionRatioValue = Math.abs(parseFloat / parseFloat2);
                                        }
                                    }
                                } catch (NumberFormatException unused) {
                                    i2++;
                                }
                            }
                            i2++;
                        }
                    }
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight) {
                    this.horizontalWeight = obtainStyledAttributes.getFloat(index, f2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight) {
                    this.verticalWeight = obtainStyledAttributes.getFloat(index, f2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle) {
                    this.horizontalChainStyle = obtainStyledAttributes.getInt(index, z2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle) {
                    this.verticalChainStyle = obtainStyledAttributes.getInt(index, z2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default) {
                    this.matchConstraintDefaultWidth = obtainStyledAttributes.getInt(index, z2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default) {
                    this.matchConstraintDefaultHeight = obtainStyledAttributes.getInt(index, z2);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min) {
                    this.matchConstraintMinWidth = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMinWidth);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max) {
                    this.matchConstraintMaxWidth = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMaxWidth);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min) {
                    this.matchConstraintMinHeight = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMinHeight);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max) {
                    this.matchConstraintMaxHeight = obtainStyledAttributes.getDimensionPixelSize(index, this.matchConstraintMaxHeight);
                } else {
                    if (!(index == R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator || index == R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator || index == R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator || index == R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator)) {
                        index = R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator;
                    }
                    i2++;
                }
            }
            obtainStyledAttributes.recycle();
            validate();
        }

        public void validate() {
            boolean z = false;
            this.isGuideline = z;
            boolean z2 = true;
            this.horizontalDimensionFixed = z2;
            this.verticalDimensionFixed = z2;
            int i = -1;
            if (this.width == 0 || this.width == i) {
                this.horizontalDimensionFixed = z;
            }
            if (this.height == 0 || this.height == i) {
                this.verticalDimensionFixed = z;
            }
            if (this.guidePercent != -1.0f || this.guideBegin != i || this.guideEnd != i) {
                this.isGuideline = z2;
                this.horizontalDimensionFixed = z2;
                this.verticalDimensionFixed = z2;
                if (!(this.widget instanceof Guideline)) {
                    this.widget = new Guideline();
                }
                ((Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            i = -1;
            this.guideBegin = i;
            this.guideEnd = i;
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
            this.goneLeftMargin = i;
            this.goneTopMargin = i;
            this.goneRightMargin = i;
            this.goneBottomMargin = i;
            this.goneStartMargin = i;
            this.goneEndMargin = i;
            float f = 0.5f;
            this.horizontalBias = f;
            this.verticalBias = f;
            this.dimensionRatio = null;
            float f2 = 0.0f;
            this.dimensionRatioValue = f2;
            boolean z = true;
            this.dimensionRatioSide = z;
            this.horizontalWeight = f2;
            this.verticalWeight = f2;
            boolean z2 = false;
            this.horizontalChainStyle = z2;
            this.verticalChainStyle = z2;
            this.matchConstraintDefaultWidth = z2;
            this.matchConstraintDefaultHeight = z2;
            this.matchConstraintMinWidth = z2;
            this.matchConstraintMinHeight = z2;
            this.matchConstraintMaxWidth = z2;
            this.matchConstraintMaxHeight = z2;
            this.editorAbsoluteX = i;
            this.editorAbsoluteY = i;
            this.orientation = i;
            this.horizontalDimensionFixed = z;
            this.verticalDimensionFixed = z;
            this.needsBaseline = z2;
            this.isGuideline = z2;
            this.resolvedLeftToLeft = i;
            this.resolvedLeftToRight = i;
            this.resolvedRightToLeft = i;
            this.resolvedRightToRight = i;
            this.resolveGoneLeftMargin = i;
            this.resolveGoneRightMargin = i;
            this.resolvedHorizontalBias = f;
            this.widget = new ConstraintWidget();
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            int i = -1;
            this.guideBegin = i;
            this.guideEnd = i;
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
            this.goneLeftMargin = i;
            this.goneTopMargin = i;
            this.goneRightMargin = i;
            this.goneBottomMargin = i;
            this.goneStartMargin = i;
            this.goneEndMargin = i;
            float f = 0.5f;
            this.horizontalBias = f;
            this.verticalBias = f;
            this.dimensionRatio = null;
            float f2 = 0.0f;
            this.dimensionRatioValue = f2;
            boolean z = true;
            this.dimensionRatioSide = z;
            this.horizontalWeight = f2;
            this.verticalWeight = f2;
            boolean z2 = false;
            this.horizontalChainStyle = z2;
            this.verticalChainStyle = z2;
            this.matchConstraintDefaultWidth = z2;
            this.matchConstraintDefaultHeight = z2;
            this.matchConstraintMinWidth = z2;
            this.matchConstraintMinHeight = z2;
            this.matchConstraintMaxWidth = z2;
            this.matchConstraintMaxHeight = z2;
            this.editorAbsoluteX = i;
            this.editorAbsoluteY = i;
            this.orientation = i;
            this.horizontalDimensionFixed = z;
            this.verticalDimensionFixed = z;
            this.needsBaseline = z2;
            this.isGuideline = z2;
            this.resolvedLeftToLeft = i;
            this.resolvedLeftToRight = i;
            this.resolvedRightToLeft = i;
            this.resolvedRightToRight = i;
            this.resolveGoneLeftMargin = i;
            this.resolveGoneRightMargin = i;
            this.resolvedHorizontalBias = f;
            this.widget = new ConstraintWidget();
        }

        @TargetApi(17)
        public void resolveLayoutDirection(int i) {
            super.resolveLayoutDirection(i);
            i = -1;
            this.resolvedRightToLeft = i;
            this.resolvedRightToRight = i;
            this.resolvedLeftToLeft = i;
            this.resolvedLeftToRight = i;
            this.resolveGoneLeftMargin = i;
            this.resolveGoneRightMargin = i;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            int i2 = 1;
            if (i2 != getLayoutDirection()) {
                i2 = 0;
            }
            if (i2 != 0) {
                if (this.startToEnd != i) {
                    this.resolvedRightToLeft = this.startToEnd;
                } else if (this.startToStart != i) {
                    this.resolvedRightToRight = this.startToStart;
                }
                if (this.endToStart != i) {
                    this.resolvedLeftToRight = this.endToStart;
                }
                if (this.endToEnd != i) {
                    this.resolvedLeftToLeft = this.endToEnd;
                }
                if (this.goneStartMargin != i) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != i) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
            } else {
                if (this.startToEnd != i) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != i) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != i) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != i) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != i) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != i) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            }
            if (this.endToStart == i && this.endToEnd == i) {
                if (this.rightToLeft != i) {
                    this.resolvedRightToLeft = this.rightToLeft;
                } else if (this.rightToRight != i) {
                    this.resolvedRightToRight = this.rightToRight;
                }
            }
            if (this.startToStart != i || this.startToEnd != i) {
                return;
            }
            if (this.leftToLeft != i) {
                this.resolvedLeftToLeft = this.leftToLeft;
            } else if (this.leftToRight != i) {
                this.resolvedLeftToRight = this.leftToRight;
            }
        }
    }

    public ConstraintLayout(Context context) {
        super(context);
        int i = 0;
        this.mMinWidth = i;
        this.mMinHeight = i;
        i = Integer.MAX_VALUE;
        this.mMaxWidth = i;
        this.mMaxHeight = i;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        Object obj = null;
        this.mConstraintSet = obj;
        init(obj);
    }

    public ConstraintLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        int i = 0;
        this.mMinWidth = i;
        this.mMinHeight = i;
        i = Integer.MAX_VALUE;
        this.mMaxWidth = i;
        this.mMaxHeight = i;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        this.mConstraintSet = null;
        init(attributeSet);
    }

    public ConstraintLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        int i2 = 0;
        this.mMinWidth = i2;
        this.mMinHeight = i2;
        i2 = Integer.MAX_VALUE;
        this.mMaxWidth = i2;
        this.mMaxHeight = i2;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        this.mConstraintSet = null;
        init(attributeSet);
    }

    public void setId(int i) {
        this.mChildrenByIds.remove(getId());
        super.setId(i);
        this.mChildrenByIds.put(getId(), this);
    }

    private void init(AttributeSet attributeSet) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.ConstraintLayout_Layout);
            int indexCount = obtainStyledAttributes.getIndexCount();
            int i = 0;
            for (int i2 = i; i2 < indexCount; i2++) {
                int index = obtainStyledAttributes.getIndex(i2);
                if (index == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMinWidth);
                } else if (index == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMinHeight);
                } else if (index == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMaxWidth);
                } else if (index == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = obtainStyledAttributes.getDimensionPixelOffset(index, this.mMaxHeight);
                } else if (index == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = obtainStyledAttributes.getInt(index, this.mOptimizationLevel);
                } else if (index == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    index = obtainStyledAttributes.getResourceId(index, i);
                    this.mConstraintSet = new ConstraintSet();
                    this.mConstraintSet.load(getContext(), index);
                }
            }
            obtainStyledAttributes.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    public void addView(View view, int i, android.view.ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (VERSION.SDK_INT < 14) {
            onViewAdded(view);
        }
    }

    public void removeView(View view) {
        super.removeView(view);
        if (VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    public void onViewAdded(View view) {
        if (VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget viewWidget = getViewWidget(view);
        boolean z = true;
        if ((view instanceof Guideline) && !(viewWidget instanceof Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.widget = new Guideline();
            layoutParams.isGuideline = z;
            ((Guideline) layoutParams.widget).setOrientation(layoutParams.orientation);
            viewWidget = layoutParams.widget;
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = z;
    }

    public void onViewRemoved(View view) {
        if (VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        this.mLayoutWidget.remove(getViewWidget(view));
        this.mDirtyHierarchy = true;
    }

    public void setMinWidth(int i) {
        if (i != this.mMinWidth) {
            this.mMinWidth = i;
            requestLayout();
        }
    }

    public void setMinHeight(int i) {
        if (i != this.mMinHeight) {
            this.mMinHeight = i;
            requestLayout();
        }
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int i) {
        if (i != this.mMaxWidth) {
            this.mMaxWidth = i;
            requestLayout();
        }
    }

    public void setMaxHeight(int i) {
        if (i != this.mMaxHeight) {
            this.mMaxHeight = i;
            requestLayout();
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private void updateHierarchy() {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = i; i2 < childCount; i2++) {
            if (getChildAt(i2).isLayoutRequested()) {
                i = 1;
                break;
            }
        }
        if (i != 0) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    private void setChildrenConstraints() {
        if (this.mConstraintSet != null) {
            r0.mConstraintSet.applyToInternal(r0);
        }
        int childCount = getChildCount();
        r0.mLayoutWidget.removeAllChildren();
        for (int i = 0; i < childCount; i++) {
            Object obj;
            View childAt = getChildAt(i);
            ConstraintWidget viewWidget = getViewWidget(childAt);
            if (viewWidget != null) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                viewWidget.reset();
                viewWidget.setVisibility(childAt.getVisibility());
                viewWidget.setCompanionWidget(childAt);
                r0.mLayoutWidget.add(viewWidget);
                if (!(layoutParams.verticalDimensionFixed && layoutParams.horizontalDimensionFixed)) {
                    r0.mVariableDimensionsWidgets.add(viewWidget);
                }
                int i2 = -1;
                if (layoutParams.isGuideline) {
                    Guideline guideline = (Guideline) viewWidget;
                    if (layoutParams.guideBegin != i2) {
                        guideline.setGuideBegin(layoutParams.guideBegin);
                    }
                    if (layoutParams.guideEnd != i2) {
                        guideline.setGuideEnd(layoutParams.guideEnd);
                    }
                    if (layoutParams.guidePercent != -1.0f) {
                        guideline.setGuidePercent(layoutParams.guidePercent);
                    }
                } else if (!(layoutParams.resolvedLeftToLeft == i2 && layoutParams.resolvedLeftToRight == i2 && layoutParams.resolvedRightToLeft == i2 && layoutParams.resolvedRightToRight == i2 && layoutParams.topToTop == i2 && layoutParams.topToBottom == i2 && layoutParams.bottomToTop == i2 && layoutParams.bottomToBottom == i2 && layoutParams.baselineToBaseline == i2 && layoutParams.editorAbsoluteX == i2 && layoutParams.editorAbsoluteY == i2 && layoutParams.width != i2 && layoutParams.height != i2)) {
                    ConstraintWidget targetWidget;
                    float f;
                    int i3 = layoutParams.resolvedLeftToLeft;
                    int i4 = layoutParams.resolvedLeftToRight;
                    int i5 = layoutParams.resolvedRightToLeft;
                    int i6 = layoutParams.resolvedRightToRight;
                    int i7 = layoutParams.resolveGoneLeftMargin;
                    int i8 = layoutParams.resolveGoneRightMargin;
                    float f2 = layoutParams.resolvedHorizontalBias;
                    if (VERSION.SDK_INT < 17) {
                        i3 = layoutParams.leftToLeft;
                        i4 = layoutParams.leftToRight;
                        i5 = layoutParams.rightToLeft;
                        i6 = layoutParams.rightToRight;
                        i7 = layoutParams.goneLeftMargin;
                        i8 = layoutParams.goneRightMargin;
                        f2 = layoutParams.horizontalBias;
                        if (i3 == i2 && i4 == i2) {
                            if (layoutParams.startToStart != i2) {
                                i3 = layoutParams.startToStart;
                            } else if (layoutParams.startToEnd != i2) {
                                i4 = layoutParams.startToEnd;
                            }
                        }
                        if (i5 == i2 && i6 == i2) {
                            if (layoutParams.endToStart != i2) {
                                i5 = layoutParams.endToStart;
                            } else if (layoutParams.endToEnd != i2) {
                                i6 = layoutParams.endToEnd;
                            }
                        }
                    }
                    int i9 = i5;
                    int i10 = i6;
                    int i11 = i8;
                    float f3 = f2;
                    int i12 = i7;
                    if (i3 != i2) {
                        targetWidget = getTargetWidget(i3);
                        if (targetWidget != null) {
                            f = f3;
                            viewWidget.immediateConnect(Type.LEFT, targetWidget, Type.LEFT, layoutParams.leftMargin, i12);
                        } else {
                            f = f3;
                        }
                    } else {
                        f = f3;
                        if (i4 != i2) {
                            targetWidget = getTargetWidget(i4);
                            if (targetWidget != null) {
                                viewWidget.immediateConnect(Type.LEFT, targetWidget, Type.RIGHT, layoutParams.leftMargin, i12);
                            }
                        }
                    }
                    if (i9 != i2) {
                        targetWidget = getTargetWidget(i9);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.RIGHT, targetWidget, Type.LEFT, layoutParams.rightMargin, i11);
                        }
                    } else if (i10 != i2) {
                        targetWidget = getTargetWidget(i10);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.RIGHT, targetWidget, Type.RIGHT, layoutParams.rightMargin, i11);
                        }
                    }
                    if (layoutParams.topToTop != i2) {
                        targetWidget = getTargetWidget(layoutParams.topToTop);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.TOP, targetWidget, Type.TOP, layoutParams.topMargin, layoutParams.goneTopMargin);
                        }
                    } else if (layoutParams.topToBottom != i2) {
                        targetWidget = getTargetWidget(layoutParams.topToBottom);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.TOP, targetWidget, Type.BOTTOM, layoutParams.topMargin, layoutParams.goneTopMargin);
                        }
                    }
                    if (layoutParams.bottomToTop != i2) {
                        targetWidget = getTargetWidget(layoutParams.bottomToTop);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.BOTTOM, targetWidget, Type.TOP, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                        }
                    } else if (layoutParams.bottomToBottom != i2) {
                        targetWidget = getTargetWidget(layoutParams.bottomToBottom);
                        if (targetWidget != null) {
                            viewWidget.immediateConnect(Type.BOTTOM, targetWidget, Type.BOTTOM, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                        }
                    }
                    if (layoutParams.baselineToBaseline != i2) {
                        childAt = (View) r0.mChildrenByIds.get(layoutParams.baselineToBaseline);
                        ConstraintWidget targetWidget2 = getTargetWidget(layoutParams.baselineToBaseline);
                        if (!(targetWidget2 == null || childAt == null || !(childAt.getLayoutParams() instanceof LayoutParams))) {
                            LayoutParams layoutParams2 = (LayoutParams) childAt.getLayoutParams();
                            boolean z = true;
                            layoutParams.needsBaseline = z;
                            layoutParams2.needsBaseline = z;
                            viewWidget.getAnchor(Type.BASELINE).connect(targetWidget2.getAnchor(Type.BASELINE), 0, -1, Strength.STRONG, 0, true);
                            viewWidget.getAnchor(Type.TOP).reset();
                            viewWidget.getAnchor(Type.BOTTOM).reset();
                        }
                    }
                    float f4 = 0.0f;
                    float f5 = 0.5f;
                    if (f >= f4 && f != f5) {
                        viewWidget.setHorizontalBiasPercent(f);
                    }
                    if (layoutParams.verticalBias >= f4 && layoutParams.verticalBias != f5) {
                        viewWidget.setVerticalBiasPercent(layoutParams.verticalBias);
                    }
                    if (isInEditMode() && !(layoutParams.editorAbsoluteX == i2 && layoutParams.editorAbsoluteY == i2)) {
                        viewWidget.setOrigin(layoutParams.editorAbsoluteX, layoutParams.editorAbsoluteY);
                    }
                    if (layoutParams.horizontalDimensionFixed) {
                        viewWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                        viewWidget.setWidth(layoutParams.width);
                    } else if (layoutParams.width == i2) {
                        viewWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_PARENT);
                        viewWidget.getAnchor(Type.LEFT).mMargin = layoutParams.leftMargin;
                        viewWidget.getAnchor(Type.RIGHT).mMargin = layoutParams.rightMargin;
                    } else {
                        viewWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
                        viewWidget.setWidth(0);
                    }
                    if (layoutParams.verticalDimensionFixed) {
                        obj = null;
                        viewWidget.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                        viewWidget.setHeight(layoutParams.height);
                    } else if (layoutParams.height == i2) {
                        viewWidget.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_PARENT);
                        viewWidget.getAnchor(Type.TOP).mMargin = layoutParams.topMargin;
                        viewWidget.getAnchor(Type.BOTTOM).mMargin = layoutParams.bottomMargin;
                        obj = null;
                    } else {
                        viewWidget.setVerticalDimensionBehaviour(DimensionBehaviour.MATCH_CONSTRAINT);
                        viewWidget.setHeight(0);
                    }
                    if (layoutParams.dimensionRatio != null) {
                        viewWidget.setDimensionRatio(layoutParams.dimensionRatio);
                    }
                    viewWidget.setHorizontalWeight(layoutParams.horizontalWeight);
                    viewWidget.setVerticalWeight(layoutParams.verticalWeight);
                    viewWidget.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
                    viewWidget.setVerticalChainStyle(layoutParams.verticalChainStyle);
                    viewWidget.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth, layoutParams.matchConstraintMinWidth, layoutParams.matchConstraintMaxWidth);
                    viewWidget.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight, layoutParams.matchConstraintMinHeight, layoutParams.matchConstraintMaxHeight);
                }
            }
            obj = null;
        }
    }

    private final ConstraintWidget getTargetWidget(int i) {
        if (i == 0) {
            return this.mLayoutWidget;
        }
        View view = (View) this.mChildrenByIds.get(i);
        if (view == this) {
            return this.mLayoutWidget;
        }
        ConstraintWidget constraintWidget;
        if (view == null) {
            constraintWidget = null;
        } else {
            constraintWidget = ((LayoutParams) view.getLayoutParams()).widget;
        }
        return constraintWidget;
    }

    private final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        ConstraintWidget constraintWidget;
        if (view == null) {
            constraintWidget = null;
        } else {
            constraintWidget = ((LayoutParams) view.getLayoutParams()).widget;
        }
        return constraintWidget;
    }

    private void internalMeasureChildren(int i, int i2) {
        int i3 = i;
        int i4 = i2;
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int childCount = getChildCount();
        int i5 = 0;
        for (int i6 = i5; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                ConstraintWidget constraintWidget = layoutParams.widget;
                if (!layoutParams.isGuideline) {
                    int i7;
                    int i8 = layoutParams.width;
                    int i9 = layoutParams.height;
                    int i10 = -1;
                    int i11 = 1;
                    int i12 = (layoutParams.horizontalDimensionFixed || layoutParams.verticalDimensionFixed || ((!layoutParams.horizontalDimensionFixed && layoutParams.matchConstraintDefaultWidth == i11) || layoutParams.width == i10 || (!layoutParams.verticalDimensionFixed && (layoutParams.matchConstraintDefaultHeight == i11 || layoutParams.height == i10)))) ? i11 : i5;
                    if (i12 != 0) {
                        i12 = -2;
                        if (i8 == 0 || i8 == i10) {
                            i8 = getChildMeasureSpec(i3, paddingLeft, i12);
                            i7 = i11;
                        } else {
                            i8 = getChildMeasureSpec(i3, paddingLeft, i8);
                            i7 = i5;
                        }
                        if (i9 == 0 || i9 == i10) {
                            i9 = getChildMeasureSpec(i4, paddingTop, i12);
                        } else {
                            i9 = getChildMeasureSpec(i4, paddingTop, i9);
                            i11 = i5;
                        }
                        childAt.measure(i8, i9);
                        i8 = childAt.getMeasuredWidth();
                        i9 = childAt.getMeasuredHeight();
                    } else {
                        i11 = i5;
                        i7 = i11;
                    }
                    constraintWidget.setWidth(i8);
                    constraintWidget.setHeight(i9);
                    if (i7 != 0) {
                        constraintWidget.setWrapWidth(i8);
                    }
                    if (i11 != 0) {
                        constraintWidget.setWrapHeight(i9);
                    }
                    if (layoutParams.needsBaseline) {
                        int baseline = childAt.getBaseline();
                        if (baseline != i10) {
                            constraintWidget.setBaselineDistance(baseline);
                        }
                    }
                }
            }
        }
        ConstraintLayout constraintLayout = this;
    }

    protected void onMeasure(int i, int i2) {
        int i3;
        int i4 = i;
        int i5 = i2;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        this.mLayoutWidget.setX(paddingLeft);
        this.mLayoutWidget.setY(paddingTop);
        setSelfDimensionBehaviour(i, i2);
        int i6 = 0;
        if (this.mDirtyHierarchy) {
            r0.mDirtyHierarchy = i6;
            updateHierarchy();
        }
        internalMeasureChildren(i, i2);
        if (getChildCount() > 0) {
            solveLinearSystem();
        }
        int size = r0.mVariableDimensionsWidgets.size();
        paddingTop += getPaddingBottom();
        paddingLeft += getPaddingRight();
        if (size > 0) {
            int i7 = r0.mLayoutWidget.getHorizontalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT ? 1 : i6;
            int i8 = r0.mLayoutWidget.getVerticalDimensionBehaviour() == DimensionBehaviour.WRAP_CONTENT ? 1 : i6;
            int i9 = i6;
            i3 = i9;
            while (i6 < size) {
                int i10;
                ConstraintWidget constraintWidget = (ConstraintWidget) r0.mVariableDimensionsWidgets.get(i6);
                if (!(constraintWidget instanceof Guideline)) {
                    View view = (View) constraintWidget.getCompanionWidget();
                    if (!(view == null || view.getVisibility() == 8)) {
                        int childMeasureSpec;
                        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                        if (layoutParams.width == -2) {
                            childMeasureSpec = getChildMeasureSpec(i4, paddingLeft, layoutParams.width);
                        } else {
                            childMeasureSpec = MeasureSpec.makeMeasureSpec(constraintWidget.getWidth(), 1073741824);
                        }
                        i10 = size;
                        if (layoutParams.height == -2) {
                            size = getChildMeasureSpec(i5, paddingTop, layoutParams.height);
                        } else {
                            size = MeasureSpec.makeMeasureSpec(constraintWidget.getHeight(), 1073741824);
                        }
                        view.measure(childMeasureSpec, size);
                        size = view.getMeasuredWidth();
                        int measuredHeight = view.getMeasuredHeight();
                        if (size != constraintWidget.getWidth()) {
                            constraintWidget.setWidth(size);
                            if (i7 != 0 && constraintWidget.getRight() > r0.mLayoutWidget.getWidth()) {
                                r0.mLayoutWidget.setWidth(Math.max(r0.mMinWidth, constraintWidget.getRight() + constraintWidget.getAnchor(Type.RIGHT).getMargin()));
                            }
                            i9 = 1;
                        }
                        if (measuredHeight != constraintWidget.getHeight()) {
                            constraintWidget.setHeight(measuredHeight);
                            if (i8 != 0 && constraintWidget.getBottom() > r0.mLayoutWidget.getHeight()) {
                                r0.mLayoutWidget.setHeight(Math.max(r0.mMinHeight, constraintWidget.getBottom() + constraintWidget.getAnchor(Type.BOTTOM).getMargin()));
                            }
                            i9 = 1;
                        }
                        if (layoutParams.needsBaseline) {
                            size = view.getBaseline();
                            if (!(size == -1 || size == constraintWidget.getBaselineDistance())) {
                                constraintWidget.setBaselineDistance(size);
                                i9 = 1;
                            }
                        }
                        if (VERSION.SDK_INT >= 11) {
                            i3 = combineMeasuredStates(i3, view.getMeasuredState());
                        }
                        i6++;
                        size = i10;
                    }
                }
                i10 = size;
                i6++;
                size = i10;
            }
            if (i9 != 0) {
                solveLinearSystem();
            }
        } else {
            i3 = i6;
        }
        size = r0.mLayoutWidget.getWidth() + paddingLeft;
        paddingLeft = r0.mLayoutWidget.getHeight() + paddingTop;
        if (VERSION.SDK_INT >= 11) {
            i4 = resolveSizeAndState(size, i4, i3);
            i5 = resolveSizeAndState(paddingLeft, i5, i3 << 16);
            paddingLeft = 16777215;
            i4 = Math.min(r0.mMaxWidth, i4) & paddingLeft;
            i5 = Math.min(r0.mMaxHeight, i5) & paddingLeft;
            paddingTop = 16777216;
            if (r0.mLayoutWidget.isWidthMeasuredTooSmall()) {
                i4 |= paddingTop;
            }
            if (r0.mLayoutWidget.isHeightMeasuredTooSmall()) {
                i5 |= paddingTop;
            }
            setMeasuredDimension(i4, i5);
            return;
        }
        setMeasuredDimension(size, paddingLeft);
    }

    private void setSelfDimensionBehaviour(int i, int i2) {
        int mode = MeasureSpec.getMode(i);
        i = MeasureSpec.getSize(i);
        int mode2 = MeasureSpec.getMode(i2);
        i2 = MeasureSpec.getSize(i2);
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        DimensionBehaviour dimensionBehaviour = DimensionBehaviour.FIXED;
        DimensionBehaviour dimensionBehaviour2 = DimensionBehaviour.FIXED;
        getLayoutParams();
        int i3 = 1073741824;
        int i4 = Integer.MIN_VALUE;
        int i5 = 0;
        if (mode != i4) {
            if (mode == 0) {
                dimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
            } else if (mode == i3) {
                i = Math.min(this.mMaxWidth, i) - paddingLeft;
            }
            i = i5;
        } else {
            dimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
        }
        if (mode2 != i4) {
            if (mode2 == 0) {
                dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
            } else if (mode2 == i3) {
                i2 = Math.min(this.mMaxHeight, i2) - paddingTop;
            }
            i2 = i5;
        } else {
            dimensionBehaviour2 = DimensionBehaviour.WRAP_CONTENT;
        }
        this.mLayoutWidget.setMinWidth(i5);
        this.mLayoutWidget.setMinHeight(i5);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
        this.mLayoutWidget.setWidth(i);
        this.mLayoutWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
        this.mLayoutWidget.setHeight(i2);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    protected void solveLinearSystem() {
        this.mLayoutWidget.layout();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int childCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (childAt.getVisibility() != 8 || layoutParams.isGuideline || isInEditMode) {
                ConstraintWidget constraintWidget = layoutParams.widget;
                int drawX = constraintWidget.getDrawX();
                int drawY = constraintWidget.getDrawY();
                childAt.layout(drawX, drawY, constraintWidget.getWidth() + drawX, constraintWidget.getHeight() + drawY);
            }
        }
    }

    public void setOptimizationLevel(int i) {
        this.mLayoutWidget.setOptimizationLevel(i);
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateDefaultLayoutParams() {
        int i = -2;
        return new LayoutParams(i, i);
    }

    protected android.view.ViewGroup.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet constraintSet) {
        this.mConstraintSet = constraintSet;
    }

    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }
}
