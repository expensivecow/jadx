package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.Cache;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType;
import android.support.constraint.solver.widgets.ConstraintAnchor.Strength;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import java.util.ArrayList;

public class ConstraintWidget {
    private static final boolean AUTOTAG_CENTER = false;
    public static final int CHAIN_PACKED = 2;
    public static final int CHAIN_SPREAD = 0;
    public static final int CHAIN_SPREAD_INSIDE = 1;
    public static float DEFAULT_BIAS = 0.5f;
    protected static final int DIRECT = 2;
    public static final int GONE = 8;
    public static final int HORIZONTAL = 0;
    public static final int INVISIBLE = 4;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    protected static final int SOLVER = 1;
    public static final int UNKNOWN = -1;
    public static final int VERTICAL = 1;
    public static final int VISIBLE = 0;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline;
    int mBaselineDistance;
    ConstraintAnchor mBottom;
    boolean mBottomHasCentered;
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX;
    ConstraintAnchor mCenterY;
    private Object mCompanionWidget;
    private int mContainerItemSkip;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    int mDistToBottom;
    int mDistToLeft;
    int mDistToRight;
    int mDistToTop;
    private int mDrawHeight;
    private int mDrawWidth;
    private int mDrawX;
    private int mDrawY;
    int mHeight;
    float mHorizontalBiasPercent;
    boolean mHorizontalChainFixedPosition;
    int mHorizontalChainStyle;
    DimensionBehaviour mHorizontalDimensionBehaviour;
    ConstraintWidget mHorizontalNextWidget;
    public int mHorizontalResolution;
    float mHorizontalWeight;
    boolean mHorizontalWrapVisited;
    ConstraintAnchor mLeft;
    boolean mLeftHasCentered;
    int mMatchConstraintDefaultHeight;
    int mMatchConstraintDefaultWidth;
    int mMatchConstraintMaxHeight;
    int mMatchConstraintMaxWidth;
    int mMatchConstraintMinHeight;
    int mMatchConstraintMinWidth;
    protected int mMinHeight;
    protected int mMinWidth;
    protected int mOffsetX;
    protected int mOffsetY;
    ConstraintWidget mParent;
    ConstraintAnchor mRight;
    boolean mRightHasCentered;
    private int mSolverBottom;
    private int mSolverLeft;
    private int mSolverRight;
    private int mSolverTop;
    ConstraintAnchor mTop;
    boolean mTopHasCentered;
    private String mType;
    float mVerticalBiasPercent;
    boolean mVerticalChainFixedPosition;
    int mVerticalChainStyle;
    DimensionBehaviour mVerticalDimensionBehaviour;
    ConstraintWidget mVerticalNextWidget;
    public int mVerticalResolution;
    float mVerticalWeight;
    boolean mVerticalWrapVisited;
    private int mVisibility;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;
    protected int mX;
    protected int mY;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public void connectedTo(ConstraintWidget constraintWidget) {
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        String str = null;
        this.mParent = str;
        boolean z = false;
        this.mWidth = z;
        this.mHeight = z;
        float f = 0.0f;
        this.mDimensionRatio = f;
        int i = -1;
        this.mDimensionRatioSide = i;
        this.mX = z;
        this.mY = z;
        this.mDrawX = z;
        this.mDrawY = z;
        this.mDrawWidth = z;
        this.mDrawHeight = z;
        this.mOffsetX = z;
        this.mOffsetY = z;
        this.mBaselineDistance = z;
        this.mMinWidth = z;
        this.mMinHeight = z;
        this.mWrapWidth = z;
        this.mWrapHeight = z;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mCompanionWidget = str;
        this.mContainerItemSkip = z;
        this.mVisibility = z;
        this.mDebugName = str;
        this.mType = str;
        this.mHorizontalWrapVisited = z;
        this.mVerticalWrapVisited = z;
        this.mHorizontalChainStyle = z;
        this.mVerticalChainStyle = z;
        this.mHorizontalChainFixedPosition = z;
        this.mVerticalChainFixedPosition = z;
        this.mHorizontalWeight = f;
        this.mVerticalWeight = f;
        this.mHorizontalResolution = i;
        this.mVerticalResolution = i;
    }

    public ConstraintWidget() {
        int i = -1;
        this.mHorizontalResolution = i;
        this.mVerticalResolution = i;
        int i2 = 0;
        this.mMatchConstraintDefaultWidth = i2;
        this.mMatchConstraintDefaultHeight = i2;
        this.mMatchConstraintMinWidth = i2;
        this.mMatchConstraintMaxWidth = i2;
        this.mMatchConstraintMinHeight = i2;
        this.mMatchConstraintMaxHeight = i2;
        this.mLeft = new ConstraintAnchor(this, Type.LEFT);
        this.mTop = new ConstraintAnchor(this, Type.TOP);
        this.mRight = new ConstraintAnchor(this, Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, Type.CENTER);
        this.mAnchors = new ArrayList();
        ConstraintWidget constraintWidget = null;
        this.mParent = constraintWidget;
        this.mWidth = i2;
        this.mHeight = i2;
        float f = 0.0f;
        this.mDimensionRatio = f;
        this.mDimensionRatioSide = i;
        this.mSolverLeft = i2;
        this.mSolverTop = i2;
        this.mSolverRight = i2;
        this.mSolverBottom = i2;
        this.mX = i2;
        this.mY = i2;
        this.mDrawX = i2;
        this.mDrawY = i2;
        this.mDrawWidth = i2;
        this.mDrawHeight = i2;
        this.mOffsetX = i2;
        this.mOffsetY = i2;
        this.mBaselineDistance = i2;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = i2;
        this.mVisibility = i2;
        this.mDebugName = constraintWidget;
        this.mType = constraintWidget;
        this.mHorizontalChainStyle = i2;
        this.mVerticalChainStyle = i2;
        this.mHorizontalWeight = f;
        this.mVerticalWeight = f;
        this.mHorizontalNextWidget = constraintWidget;
        this.mVerticalNextWidget = constraintWidget;
        addAnchors();
    }

    public ConstraintWidget(int i, int i2, int i3, int i4) {
        int i5 = -1;
        this.mHorizontalResolution = i5;
        this.mVerticalResolution = i5;
        int i6 = 0;
        this.mMatchConstraintDefaultWidth = i6;
        this.mMatchConstraintDefaultHeight = i6;
        this.mMatchConstraintMinWidth = i6;
        this.mMatchConstraintMaxWidth = i6;
        this.mMatchConstraintMinHeight = i6;
        this.mMatchConstraintMaxHeight = i6;
        this.mLeft = new ConstraintAnchor(this, Type.LEFT);
        this.mTop = new ConstraintAnchor(this, Type.TOP);
        this.mRight = new ConstraintAnchor(this, Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, Type.CENTER);
        this.mAnchors = new ArrayList();
        ConstraintWidget constraintWidget = null;
        this.mParent = constraintWidget;
        this.mWidth = i6;
        this.mHeight = i6;
        float f = 0.0f;
        this.mDimensionRatio = f;
        this.mDimensionRatioSide = i5;
        this.mSolverLeft = i6;
        this.mSolverTop = i6;
        this.mSolverRight = i6;
        this.mSolverBottom = i6;
        this.mX = i6;
        this.mY = i6;
        this.mDrawX = i6;
        this.mDrawY = i6;
        this.mDrawWidth = i6;
        this.mDrawHeight = i6;
        this.mOffsetX = i6;
        this.mOffsetY = i6;
        this.mBaselineDistance = i6;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mContainerItemSkip = i6;
        this.mVisibility = i6;
        this.mDebugName = constraintWidget;
        this.mType = constraintWidget;
        this.mHorizontalChainStyle = i6;
        this.mVerticalChainStyle = i6;
        this.mHorizontalWeight = f;
        this.mVerticalWeight = f;
        this.mHorizontalNextWidget = constraintWidget;
        this.mVerticalNextWidget = constraintWidget;
        this.mX = i;
        this.mY = i2;
        this.mWidth = i3;
        this.mHeight = i4;
        addAnchors();
        forceUpdateDrawPosition();
    }

    public ConstraintWidget(int i, int i2) {
        int i3 = 0;
        this(i3, i3, i, i2);
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    public void resetGroups() {
        int size = this.mAnchors.size();
        for (int i = 0; i < size; i++) {
            ((ConstraintAnchor) this.mAnchors.get(i)).mGroup = Integer.MAX_VALUE;
        }
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mBaseline);
    }

    public boolean isRoot() {
        return this.mParent == null;
    }

    public boolean isRootContainer() {
        return (this instanceof ConstraintWidgetContainer) && (this.mParent == null || !(this.mParent instanceof ConstraintWidgetContainer));
    }

    public boolean isInsideConstraintLayout() {
        ConstraintWidget parent = getParent();
        boolean z = false;
        if (parent == null) {
            return z;
        }
        while (parent != null) {
            if (parent instanceof ConstraintWidgetContainer) {
                return true;
            }
            parent = parent.getParent();
        }
        return z;
    }

    public boolean hasAncestor(ConstraintWidget constraintWidget) {
        ConstraintWidget parent = getParent();
        boolean z = true;
        if (parent == constraintWidget) {
            return z;
        }
        boolean z2 = false;
        if (parent == constraintWidget.getParent()) {
            return z2;
        }
        while (parent != null) {
            if (parent == constraintWidget || parent == constraintWidget.getParent()) {
                return z;
            }
            parent = parent.getParent();
        }
        return z2;
    }

    public WidgetContainer getRootWidgetContainer() {
        ConstraintWidget constraintWidget = this;
        while (constraintWidget.getParent() != null) {
            constraintWidget = constraintWidget.getParent();
        }
        return constraintWidget instanceof WidgetContainer ? (WidgetContainer) constraintWidget : null;
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    public void setParent(ConstraintWidget constraintWidget) {
        this.mParent = constraintWidget;
    }

    public String getType() {
        return this.mType;
    }

    public void setType(String str) {
        this.mType = str;
    }

    public void setVisibility(int i) {
        this.mVisibility = i;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public void setDebugName(String str) {
        this.mDebugName = str;
    }

    public void setDebugSolverName(LinearSystem linearSystem, String str) {
        this.mDebugName = str;
        SolverVariable createObjectVariable = linearSystem.createObjectVariable(this.mLeft);
        SolverVariable createObjectVariable2 = linearSystem.createObjectVariable(this.mTop);
        SolverVariable createObjectVariable3 = linearSystem.createObjectVariable(this.mRight);
        SolverVariable createObjectVariable4 = linearSystem.createObjectVariable(this.mBottom);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(".left");
        createObjectVariable.setName(stringBuilder.toString());
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(".top");
        createObjectVariable2.setName(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(".right");
        createObjectVariable3.setName(stringBuilder2.toString());
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(".bottom");
        createObjectVariable4.setName(stringBuilder2.toString());
        if (this.mBaselineDistance > 0) {
            SolverVariable createObjectVariable5 = linearSystem.createObjectVariable(this.mBaseline);
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(".baseline");
            createObjectVariable5.setName(stringBuilder2.toString());
        }
    }

    public String toString() {
        StringBuilder stringBuilder;
        String stringBuilder2;
        StringBuilder stringBuilder3 = new StringBuilder();
        if (this.mType != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("type: ");
            stringBuilder.append(this.mType);
            stringBuilder.append(" ");
            stringBuilder2 = stringBuilder.toString();
        } else {
            stringBuilder2 = "";
        }
        stringBuilder3.append(stringBuilder2);
        if (this.mDebugName != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("id: ");
            stringBuilder.append(this.mDebugName);
            stringBuilder.append(" ");
            stringBuilder2 = stringBuilder.toString();
        } else {
            stringBuilder2 = "";
        }
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append("(");
        stringBuilder3.append(this.mX);
        stringBuilder3.append(", ");
        stringBuilder3.append(this.mY);
        stringBuilder3.append(") - (");
        stringBuilder3.append(this.mWidth);
        stringBuilder3.append(" x ");
        stringBuilder3.append(this.mHeight);
        stringBuilder3.append(")");
        stringBuilder3.append(" wrap: (");
        stringBuilder3.append(this.mWrapWidth);
        stringBuilder3.append(" x ");
        stringBuilder3.append(this.mWrapHeight);
        stringBuilder3.append(")");
        return stringBuilder3.toString();
    }

    int getInternalDrawX() {
        return this.mDrawX;
    }

    int getInternalDrawY() {
        return this.mDrawY;
    }

    public int getInternalDrawRight() {
        return this.mDrawX + this.mDrawWidth;
    }

    public int getInternalDrawBottom() {
        return this.mDrawY + this.mDrawHeight;
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getOptimizerWrapWidth() {
        int i = this.mWidth;
        if (this.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i;
        }
        if (this.mMatchConstraintDefaultWidth == 1) {
            i = Math.max(this.mMatchConstraintMinWidth, i);
        } else if (this.mMatchConstraintMinWidth > 0) {
            i = this.mMatchConstraintMinWidth;
            this.mWidth = i;
        } else {
            i = 0;
        }
        return (this.mMatchConstraintMaxWidth <= 0 || this.mMatchConstraintMaxWidth >= i) ? i : this.mMatchConstraintMaxWidth;
    }

    public int getOptimizerWrapHeight() {
        int i = this.mHeight;
        if (this.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i;
        }
        if (this.mMatchConstraintDefaultHeight == 1) {
            i = Math.max(this.mMatchConstraintMinHeight, i);
        } else if (this.mMatchConstraintMinHeight > 0) {
            i = this.mMatchConstraintMinHeight;
            this.mHeight = i;
        } else {
            i = 0;
        }
        return (this.mMatchConstraintMaxHeight <= 0 || this.mMatchConstraintMaxHeight >= i) ? i : this.mMatchConstraintMaxHeight;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    public int getDrawWidth() {
        return this.mDrawWidth;
    }

    public int getDrawHeight() {
        return this.mDrawHeight;
    }

    public int getDrawBottom() {
        return getDrawY() + this.mDrawHeight;
    }

    public int getDrawRight() {
        return getDrawX() + this.mDrawWidth;
    }

    protected int getRootX() {
        return this.mX + this.mOffsetX;
    }

    protected int getRootY() {
        return this.mY + this.mOffsetY;
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public int getLeft() {
        return getX();
    }

    public int getTop() {
        return getY();
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }

    public float getVerticalBiasPercent() {
        return this.mVerticalBiasPercent;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setX(int i) {
        this.mX = i;
    }

    public void setY(int i) {
        this.mY = i;
    }

    public void setOrigin(int i, int i2) {
        this.mX = i;
        this.mY = i2;
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void setGoneMargin(Type type, int i) {
        switch (type) {
            case LEFT:
                this.mLeft.mGoneMargin = i;
                return;
            case TOP:
                this.mTop.mGoneMargin = i;
                return;
            case RIGHT:
                this.mRight.mGoneMargin = i;
                return;
            case BOTTOM:
                this.mBottom.mGoneMargin = i;
                return;
            default:
                return;
        }
    }

    public void updateDrawPosition() {
        int i = this.mX;
        int i2 = this.mY;
        int i3 = this.mX + this.mWidth;
        int i4 = this.mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public void forceUpdateDrawPosition() {
        int i = this.mX;
        int i2 = this.mY;
        int i3 = this.mX + this.mWidth;
        int i4 = this.mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public void setDrawOrigin(int i, int i2) {
        this.mDrawX = i - this.mOffsetX;
        this.mDrawY = i2 - this.mOffsetY;
        this.mX = this.mDrawX;
        this.mY = this.mDrawY;
    }

    public void setDrawX(int i) {
        this.mDrawX = i - this.mOffsetX;
        this.mX = this.mDrawX;
    }

    public void setDrawY(int i) {
        this.mDrawY = i - this.mOffsetY;
        this.mY = this.mDrawY;
    }

    public void setDrawWidth(int i) {
        this.mDrawWidth = i;
    }

    public void setDrawHeight(int i) {
        this.mDrawHeight = i;
    }

    public void setWidth(int i) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHeight(int i) {
        this.mHeight = i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setHorizontalMatchStyle(int i, int i2, int i3) {
        this.mMatchConstraintDefaultWidth = i;
        this.mMatchConstraintMinWidth = i2;
        this.mMatchConstraintMaxWidth = i3;
    }

    public void setVerticalMatchStyle(int i, int i2, int i3) {
        this.mMatchConstraintDefaultHeight = i;
        this.mMatchConstraintMinHeight = i2;
        this.mMatchConstraintMaxHeight = i3;
    }

    public void setDimensionRatio(String str) {
        float f = 0.0f;
        if (str == null || str.length() == 0) {
            this.mDimensionRatio = f;
            return;
        }
        float parseFloat;
        int i = -1;
        int length = str.length();
        int indexOf = str.indexOf(44);
        int i2 = 0;
        int i3 = 1;
        if (indexOf > 0 && indexOf < length - 1) {
            String substring = str.substring(i2, indexOf);
            if (substring.equalsIgnoreCase("W")) {
                i = i2;
            } else if (substring.equalsIgnoreCase("H")) {
                i = i3;
            }
            i2 = indexOf + 1;
        }
        indexOf = str.indexOf(58);
        if (indexOf < 0 || indexOf >= length - i3) {
            str = str.substring(i2);
            if (str.length() > 0) {
                parseFloat = Float.parseFloat(str);
            }
        } else {
            String substring2 = str.substring(i2, indexOf);
            str = str.substring(indexOf + i3);
            if (substring2.length() > 0 && str.length() > 0) {
                try {
                    float parseFloat2 = Float.parseFloat(substring2);
                    parseFloat = Float.parseFloat(str);
                    if (parseFloat2 > f && parseFloat > f) {
                        parseFloat = i == i3 ? Math.abs(parseFloat / parseFloat2) : Math.abs(parseFloat2 / parseFloat);
                    }
                } catch (NumberFormatException unused) {
                    parseFloat = f;
                }
            }
        }
        if (parseFloat > f) {
            this.mDimensionRatio = parseFloat;
            this.mDimensionRatioSide = i;
        }
    }

    public void setDimensionRatio(float f, int i) {
        this.mDimensionRatio = f;
        this.mDimensionRatioSide = i;
    }

    public float getDimensionRatio() {
        return this.mDimensionRatio;
    }

    public int getDimensionRatioSide() {
        return this.mDimensionRatioSide;
    }

    public void setHorizontalBiasPercent(float f) {
        this.mHorizontalBiasPercent = f;
    }

    public void setVerticalBiasPercent(float f) {
        this.mVerticalBiasPercent = f;
    }

    public void setMinWidth(int i) {
        if (i < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = i;
        }
    }

    public void setMinHeight(int i) {
        if (i < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = i;
        }
    }

    public void setWrapWidth(int i) {
        this.mWrapWidth = i;
    }

    public void setWrapHeight(int i) {
        this.mWrapHeight = i;
    }

    public void setDimension(int i, int i2) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
        this.mHeight = i2;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setFrame(int i, int i2, int i3, int i4) {
        i3 -= i;
        i4 -= i2;
        this.mX = i;
        this.mY = i2;
        if (this.mVisibility == 8) {
            i = 0;
            this.mWidth = i;
            this.mHeight = i;
            return;
        }
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.FIXED && i3 < this.mWidth) {
            i3 = this.mWidth;
        }
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.FIXED && i4 < this.mHeight) {
            i4 = this.mHeight;
        }
        this.mWidth = i3;
        this.mHeight = i4;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHorizontalDimension(int i, int i2) {
        this.mX = i;
        this.mWidth = i2 - i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setVerticalDimension(int i, int i2) {
        this.mY = i;
        this.mHeight = i2 - i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setBaselineDistance(int i) {
        this.mBaselineDistance = i;
    }

    public void setCompanionWidget(Object obj) {
        this.mCompanionWidget = obj;
    }

    public void setContainerItemSkip(int i) {
        if (i >= 0) {
            this.mContainerItemSkip = i;
        } else {
            this.mContainerItemSkip = 0;
        }
    }

    public int getContainerItemSkip() {
        return this.mContainerItemSkip;
    }

    public void setHorizontalWeight(float f) {
        this.mHorizontalWeight = f;
    }

    public void setVerticalWeight(float f) {
        this.mVerticalWeight = f;
    }

    public void setHorizontalChainStyle(int i) {
        this.mHorizontalChainStyle = i;
    }

    public int getHorizontalChainStyle() {
        return this.mHorizontalChainStyle;
    }

    public void setVerticalChainStyle(int i) {
        this.mVerticalChainStyle = i;
    }

    public int getVerticalChainStyle() {
        return this.mVerticalChainStyle;
    }

    public void immediateConnect(Type type, ConstraintWidget constraintWidget, Type type2, int i, int i2) {
        getAnchor(type).connect(constraintWidget.getAnchor(type2), i, i2, Strength.STRONG, 0, true);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2) {
        connect(constraintAnchor, constraintAnchor2, i, Strength.STRONG, i2);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i) {
        connect(constraintAnchor, constraintAnchor2, i, Strength.STRONG, 0);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, Strength strength, int i2) {
        if (constraintAnchor.getOwner() == this) {
            connect(constraintAnchor.getType(), constraintAnchor2.getOwner(), constraintAnchor2.getType(), i, strength, i2);
        }
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i) {
        connect(type, constraintWidget, type2, i, Strength.STRONG);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2) {
        Type type3 = type;
        ConstraintWidget constraintWidget2 = constraintWidget;
        Type type4 = type2;
        connect(type3, constraintWidget2, type4, 0, Strength.STRONG);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i, Strength strength) {
        connect(type, constraintWidget, type2, i, strength, 0);
    }

    public void connect(Type type, ConstraintWidget constraintWidget, Type type2, int i, Strength strength, int i2) {
        ConstraintWidget constraintWidget2 = this;
        Type type3 = type;
        ConstraintWidget constraintWidget3 = constraintWidget;
        Type type4 = type2;
        int i3 = i2;
        int i4 = 0;
        ConstraintAnchor anchor;
        ConstraintAnchor anchor2;
        ConstraintAnchor anchor3;
        if (type3 == Type.CENTER) {
            int i5;
            ConstraintWidget constraintWidget4;
            Strength strength2;
            int i6;
            Type type5;
            if (type4 == Type.CENTER) {
                int i7;
                anchor = getAnchor(Type.LEFT);
                anchor2 = getAnchor(Type.RIGHT);
                ConstraintAnchor anchor4 = getAnchor(Type.TOP);
                ConstraintAnchor anchor5 = getAnchor(Type.BOTTOM);
                int i8 = 1;
                if ((anchor == null || !anchor.isConnected()) && (anchor2 == null || !anchor2.isConnected())) {
                    i5 = 0;
                    constraintWidget4 = constraintWidget3;
                    strength2 = strength;
                    i6 = i3;
                    connect(Type.LEFT, constraintWidget4, Type.LEFT, i5, strength2, i6);
                    connect(Type.RIGHT, constraintWidget4, Type.RIGHT, i5, strength2, i6);
                    i7 = i8;
                } else {
                    i7 = i4;
                }
                if ((anchor4 == null || !anchor4.isConnected()) && (anchor5 == null || !anchor5.isConnected())) {
                    i5 = 0;
                    constraintWidget4 = constraintWidget3;
                    strength2 = strength;
                    i6 = i3;
                    connect(Type.TOP, constraintWidget4, Type.TOP, i5, strength2, i6);
                    connect(Type.BOTTOM, constraintWidget4, Type.BOTTOM, i5, strength2, i6);
                } else {
                    i8 = i4;
                }
                if (i7 != 0 && i8 != 0) {
                    getAnchor(Type.CENTER).connect(constraintWidget3.getAnchor(Type.CENTER), i4, i3);
                } else if (i7 != 0) {
                    getAnchor(Type.CENTER_X).connect(constraintWidget3.getAnchor(Type.CENTER_X), i4, i3);
                } else if (i8 != 0) {
                    getAnchor(Type.CENTER_Y).connect(constraintWidget3.getAnchor(Type.CENTER_Y), i4, i3);
                }
            } else if (type4 == Type.LEFT || type4 == Type.RIGHT) {
                i5 = 0;
                constraintWidget4 = constraintWidget3;
                type5 = type4;
                strength2 = strength;
                i6 = i3;
                connect(Type.LEFT, constraintWidget4, type5, i5, strength2, i6);
                connect(Type.RIGHT, constraintWidget4, type5, i5, strength2, i6);
                getAnchor(Type.CENTER).connect(constraintWidget.getAnchor(type2), i4, i3);
            } else if (type4 == Type.TOP || type4 == Type.BOTTOM) {
                i5 = 0;
                constraintWidget4 = constraintWidget3;
                type5 = type4;
                strength2 = strength;
                i6 = i3;
                connect(Type.TOP, constraintWidget4, type5, i5, strength2, i6);
                connect(Type.BOTTOM, constraintWidget4, type5, i5, strength2, i6);
                getAnchor(Type.CENTER).connect(constraintWidget.getAnchor(type2), i4, i3);
            }
        } else if (type3 == Type.CENTER_X && (type4 == Type.LEFT || type4 == Type.RIGHT)) {
            anchor = getAnchor(Type.LEFT);
            anchor2 = constraintWidget.getAnchor(type2);
            anchor3 = getAnchor(Type.RIGHT);
            anchor.connect(anchor2, i4, i3);
            anchor3.connect(anchor2, i4, i3);
            getAnchor(Type.CENTER_X).connect(anchor2, i4, i3);
        } else if (type3 == Type.CENTER_Y && (type4 == Type.TOP || type4 == Type.BOTTOM)) {
            anchor = constraintWidget.getAnchor(type2);
            getAnchor(Type.TOP).connect(anchor, i4, i3);
            getAnchor(Type.BOTTOM).connect(anchor, i4, i3);
            getAnchor(Type.CENTER_Y).connect(anchor, i4, i3);
        } else if (type3 == Type.CENTER_X && type4 == Type.CENTER_X) {
            getAnchor(Type.LEFT).connect(constraintWidget3.getAnchor(Type.LEFT), i4, i3);
            getAnchor(Type.RIGHT).connect(constraintWidget3.getAnchor(Type.RIGHT), i4, i3);
            getAnchor(Type.CENTER_X).connect(constraintWidget.getAnchor(type2), i4, i3);
        } else if (type3 == Type.CENTER_Y && type4 == Type.CENTER_Y) {
            getAnchor(Type.TOP).connect(constraintWidget3.getAnchor(Type.TOP), i4, i3);
            getAnchor(Type.BOTTOM).connect(constraintWidget3.getAnchor(Type.BOTTOM), i4, i3);
            getAnchor(Type.CENTER_Y).connect(constraintWidget.getAnchor(type2), i4, i3);
        } else {
            anchor2 = getAnchor(type);
            anchor3 = constraintWidget.getAnchor(type2);
            if (anchor2.isValidConnection(anchor3)) {
                ConstraintAnchor anchor6;
                if (type3 == Type.BASELINE) {
                    anchor = getAnchor(Type.TOP);
                    anchor6 = getAnchor(Type.BOTTOM);
                    if (anchor != null) {
                        anchor.reset();
                    }
                    if (anchor6 != null) {
                        anchor6.reset();
                    }
                } else {
                    if (type3 == Type.TOP || type3 == Type.BOTTOM) {
                        anchor6 = getAnchor(Type.BASELINE);
                        if (anchor6 != null) {
                            anchor6.reset();
                        }
                        anchor6 = getAnchor(Type.CENTER);
                        if (anchor6.getTarget() != anchor3) {
                            anchor6.reset();
                        }
                        anchor = getAnchor(type).getOpposite();
                        anchor6 = getAnchor(Type.CENTER_Y);
                        if (anchor6.isConnected()) {
                            anchor.reset();
                            anchor6.reset();
                        }
                    } else if (type3 == Type.LEFT || type3 == Type.RIGHT) {
                        anchor6 = getAnchor(Type.CENTER);
                        if (anchor6.getTarget() != anchor3) {
                            anchor6.reset();
                        }
                        anchor = getAnchor(type).getOpposite();
                        anchor6 = getAnchor(Type.CENTER_X);
                        if (anchor6.isConnected()) {
                            anchor.reset();
                            anchor6.reset();
                        }
                    }
                    i4 = i;
                }
                anchor2.connect(anchor3, i4, strength, i3);
                anchor3.getOwner().connectedTo(anchor2.getOwner());
            }
        }
    }

    public void resetAllConstraints() {
        resetAnchors();
        setVerticalBiasPercent(DEFAULT_BIAS);
        setHorizontalBiasPercent(DEFAULT_BIAS);
        if (!(this instanceof ConstraintWidgetContainer)) {
            if (getHorizontalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                if (getWidth() == getWrapWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                } else if (getWidth() > getMinWidth()) {
                    setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
            }
            if (getVerticalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
                if (getHeight() == getWrapHeight()) {
                    setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
                } else if (getHeight() > getMinHeight()) {
                    setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
            }
        }
    }

    public void resetAnchor(ConstraintAnchor constraintAnchor) {
        if (getParent() == null || !(getParent() instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            ConstraintAnchor anchor = getAnchor(Type.LEFT);
            ConstraintAnchor anchor2 = getAnchor(Type.RIGHT);
            ConstraintAnchor anchor3 = getAnchor(Type.TOP);
            ConstraintAnchor anchor4 = getAnchor(Type.BOTTOM);
            ConstraintAnchor anchor5 = getAnchor(Type.CENTER);
            ConstraintAnchor anchor6 = getAnchor(Type.CENTER_X);
            ConstraintAnchor anchor7 = getAnchor(Type.CENTER_Y);
            float f = 0.5f;
            if (constraintAnchor == anchor5) {
                if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                    anchor.reset();
                    anchor2.reset();
                }
                if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
                    anchor3.reset();
                    anchor4.reset();
                }
                this.mHorizontalBiasPercent = f;
                this.mVerticalBiasPercent = f;
            } else if (constraintAnchor == anchor6) {
                if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget().getOwner() == anchor2.getTarget().getOwner()) {
                    anchor.reset();
                    anchor2.reset();
                }
                this.mHorizontalBiasPercent = f;
            } else if (constraintAnchor == anchor7) {
                if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget().getOwner() == anchor4.getTarget().getOwner()) {
                    anchor3.reset();
                    anchor4.reset();
                }
                this.mVerticalBiasPercent = f;
            } else if (constraintAnchor == anchor || constraintAnchor == anchor2) {
                if (anchor.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                    anchor5.reset();
                }
            } else if ((constraintAnchor == anchor3 || constraintAnchor == anchor4) && anchor3.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
                anchor5.reset();
            }
            constraintAnchor.reset();
        }
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int size = this.mAnchors.size();
            for (int i = 0; i < size; i++) {
                ((ConstraintAnchor) this.mAnchors.get(i)).reset();
            }
        }
    }

    public void resetAnchors(int i) {
        ConstraintWidget parent = getParent();
        if (parent == null || !(parent instanceof ConstraintWidgetContainer) || !((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            int size = this.mAnchors.size();
            for (int i2 = 0; i2 < size; i2++) {
                ConstraintAnchor constraintAnchor = (ConstraintAnchor) this.mAnchors.get(i2);
                if (i == constraintAnchor.getConnectionCreator()) {
                    if (constraintAnchor.isVerticalAnchor()) {
                        setVerticalBiasPercent(DEFAULT_BIAS);
                    } else {
                        setHorizontalBiasPercent(DEFAULT_BIAS);
                    }
                    constraintAnchor.reset();
                }
            }
        }
    }

    public void disconnectWidget(ConstraintWidget constraintWidget) {
        ArrayList anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget) {
                constraintAnchor.reset();
            }
        }
    }

    public void disconnectUnlockedWidget(ConstraintWidget constraintWidget) {
        ArrayList anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget && constraintAnchor.getConnectionCreator() == 2) {
                constraintAnchor.reset();
            }
        }
    }

    public ConstraintAnchor getAnchor(Type type) {
        switch (type) {
            case LEFT:
                return this.mLeft;
            case TOP:
                return this.mTop;
            case RIGHT:
                return this.mRight;
            case BOTTOM:
                return this.mBottom;
            case BASELINE:
                return this.mBaseline;
            case CENTER_X:
                return this.mCenterX;
            case CENTER_Y:
                return this.mCenterY;
            case CENTER:
                return this.mCenter;
            default:
                return null;
        }
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mHorizontalDimensionBehaviour;
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mVerticalDimensionBehaviour;
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mHorizontalDimensionBehaviour = dimensionBehaviour;
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mVerticalDimensionBehaviour = dimensionBehaviour;
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    public boolean isInHorizontalChain() {
        return (this.mLeft.mTarget != null && this.mLeft.mTarget.mTarget == this.mLeft) || (this.mRight.mTarget != null && this.mRight.mTarget.mTarget == this.mRight);
    }

    public ConstraintWidget getHorizontalChainControlWidget() {
        ConstraintWidget constraintWidget = null;
        if (!isInHorizontalChain()) {
            return constraintWidget;
        }
        ConstraintWidget constraintWidget2 = this;
        ConstraintWidget constraintWidget3 = constraintWidget;
        while (constraintWidget3 == null && constraintWidget2 != null) {
            ConstraintWidget constraintWidget4;
            ConstraintAnchor anchor = constraintWidget2.getAnchor(Type.LEFT);
            if (anchor == null) {
                anchor = constraintWidget;
            } else {
                anchor = anchor.getTarget();
            }
            if (anchor == null) {
                constraintWidget4 = constraintWidget;
            } else {
                constraintWidget4 = anchor.getOwner();
            }
            if (constraintWidget4 == getParent()) {
                return constraintWidget2;
            }
            ConstraintAnchor constraintAnchor;
            if (constraintWidget4 == null) {
                constraintAnchor = constraintWidget;
            } else {
                constraintAnchor = constraintWidget4.getAnchor(Type.RIGHT).getTarget();
            }
            if (constraintAnchor == null || constraintAnchor.getOwner() == constraintWidget2) {
                constraintWidget2 = constraintWidget4;
            } else {
                constraintWidget3 = constraintWidget2;
            }
        }
        return constraintWidget3;
    }

    public boolean isInVerticalChain() {
        return (this.mTop.mTarget != null && this.mTop.mTarget.mTarget == this.mTop) || (this.mBottom.mTarget != null && this.mBottom.mTarget.mTarget == this.mBottom);
    }

    public ConstraintWidget getVerticalChainControlWidget() {
        ConstraintWidget constraintWidget = null;
        if (!isInVerticalChain()) {
            return constraintWidget;
        }
        ConstraintWidget constraintWidget2 = this;
        ConstraintWidget constraintWidget3 = constraintWidget;
        while (constraintWidget3 == null && constraintWidget2 != null) {
            ConstraintWidget constraintWidget4;
            ConstraintAnchor anchor = constraintWidget2.getAnchor(Type.TOP);
            if (anchor == null) {
                anchor = constraintWidget;
            } else {
                anchor = anchor.getTarget();
            }
            if (anchor == null) {
                constraintWidget4 = constraintWidget;
            } else {
                constraintWidget4 = anchor.getOwner();
            }
            if (constraintWidget4 == getParent()) {
                return constraintWidget2;
            }
            ConstraintAnchor constraintAnchor;
            if (constraintWidget4 == null) {
                constraintAnchor = constraintWidget;
            } else {
                constraintAnchor = constraintWidget4.getAnchor(Type.BOTTOM).getTarget();
            }
            if (constraintAnchor == null || constraintAnchor.getOwner() == constraintWidget2) {
                constraintWidget2 = constraintWidget4;
            } else {
                constraintWidget3 = constraintWidget2;
            }
        }
        return constraintWidget3;
    }

    public void addToSolver(LinearSystem linearSystem) {
        addToSolver(linearSystem, Integer.MAX_VALUE);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addToSolver(android.support.constraint.solver.LinearSystem r45, int r46) {
        /*
        r44 = this;
        r15 = r44;
        r14 = r45;
        r13 = r46;
        r0 = 0;
        r12 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r13 == r12) goto L_0x0015;
    L_0x000c:
        r1 = r15.mLeft;
        r1 = r1.mGroup;
        if (r1 != r13) goto L_0x0013;
    L_0x0012:
        goto L_0x0015;
    L_0x0013:
        r11 = r0;
        goto L_0x001c;
    L_0x0015:
        r1 = r15.mLeft;
        r1 = r14.createObjectVariable(r1);
        r11 = r1;
    L_0x001c:
        if (r13 == r12) goto L_0x0027;
    L_0x001e:
        r1 = r15.mRight;
        r1 = r1.mGroup;
        if (r1 != r13) goto L_0x0025;
    L_0x0024:
        goto L_0x0027;
    L_0x0025:
        r10 = r0;
        goto L_0x002e;
    L_0x0027:
        r1 = r15.mRight;
        r1 = r14.createObjectVariable(r1);
        r10 = r1;
    L_0x002e:
        if (r13 == r12) goto L_0x0039;
    L_0x0030:
        r1 = r15.mTop;
        r1 = r1.mGroup;
        if (r1 != r13) goto L_0x0037;
    L_0x0036:
        goto L_0x0039;
    L_0x0037:
        r9 = r0;
        goto L_0x0040;
    L_0x0039:
        r1 = r15.mTop;
        r1 = r14.createObjectVariable(r1);
        r9 = r1;
    L_0x0040:
        if (r13 == r12) goto L_0x004b;
    L_0x0042:
        r1 = r15.mBottom;
        r1 = r1.mGroup;
        if (r1 != r13) goto L_0x0049;
    L_0x0048:
        goto L_0x004b;
    L_0x0049:
        r8 = r0;
        goto L_0x0052;
    L_0x004b:
        r1 = r15.mBottom;
        r1 = r14.createObjectVariable(r1);
        r8 = r1;
    L_0x0052:
        if (r13 == r12) goto L_0x005d;
    L_0x0054:
        r1 = r15.mBaseline;
        r1 = r1.mGroup;
        if (r1 != r13) goto L_0x005b;
    L_0x005a:
        goto L_0x005d;
    L_0x005b:
        r7 = r0;
        goto L_0x0064;
    L_0x005d:
        r0 = r15.mBaseline;
        r0 = r14.createObjectVariable(r0);
        goto L_0x005b;
    L_0x0064:
        r0 = r15.mParent;
        r6 = 0;
        r5 = 1;
        if (r0 == 0) goto L_0x01d7;
    L_0x006a:
        r0 = r15.mLeft;
        r0 = r0.mTarget;
        if (r0 == 0) goto L_0x007a;
    L_0x0070:
        r0 = r15.mLeft;
        r0 = r0.mTarget;
        r0 = r0.mTarget;
        r1 = r15.mLeft;
        if (r0 == r1) goto L_0x008a;
    L_0x007a:
        r0 = r15.mRight;
        r0 = r0.mTarget;
        if (r0 == 0) goto L_0x0093;
    L_0x0080:
        r0 = r15.mRight;
        r0 = r0.mTarget;
        r0 = r0.mTarget;
        r1 = r15.mRight;
        if (r0 != r1) goto L_0x0093;
    L_0x008a:
        r0 = r15.mParent;
        r0 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r0;
        r0.addChain(r15, r6);
        r0 = r5;
        goto L_0x0094;
    L_0x0093:
        r0 = r6;
    L_0x0094:
        r1 = r15.mTop;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x00a4;
    L_0x009a:
        r1 = r15.mTop;
        r1 = r1.mTarget;
        r1 = r1.mTarget;
        r2 = r15.mTop;
        if (r1 == r2) goto L_0x00b4;
    L_0x00a4:
        r1 = r15.mBottom;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x00bd;
    L_0x00aa:
        r1 = r15.mBottom;
        r1 = r1.mTarget;
        r1 = r1.mTarget;
        r2 = r15.mBottom;
        if (r1 != r2) goto L_0x00bd;
    L_0x00b4:
        r1 = r15.mParent;
        r1 = (android.support.constraint.solver.widgets.ConstraintWidgetContainer) r1;
        r1.addChain(r15, r5);
        r1 = r5;
        goto L_0x00be;
    L_0x00bd:
        r1 = r6;
    L_0x00be:
        r2 = r15.mParent;
        r2 = r2.getHorizontalDimensionBehaviour();
        r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (r2 != r3) goto L_0x0148;
    L_0x00c8:
        if (r0 != 0) goto L_0x0148;
    L_0x00ca:
        r2 = r15.mLeft;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x00f3;
    L_0x00d0:
        r2 = r15.mLeft;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 == r3) goto L_0x00db;
    L_0x00da:
        goto L_0x00f3;
    L_0x00db:
        r2 = r15.mLeft;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x0109;
    L_0x00e1:
        r2 = r15.mLeft;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 != r3) goto L_0x0109;
    L_0x00eb:
        r2 = r15.mLeft;
        r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT;
        r2.setConnectionType(r3);
        goto L_0x0109;
    L_0x00f3:
        r2 = r15.mParent;
        r2 = r2.mLeft;
        r2 = r14.createObjectVariable(r2);
        r3 = r45.createRow();
        r4 = r45.createSlackVariable();
        r3.createRowGreaterThan(r11, r2, r4, r6);
        r14.addConstraint(r3);
    L_0x0109:
        r2 = r15.mRight;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x0132;
    L_0x010f:
        r2 = r15.mRight;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 == r3) goto L_0x011a;
    L_0x0119:
        goto L_0x0132;
    L_0x011a:
        r2 = r15.mRight;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x0148;
    L_0x0120:
        r2 = r15.mRight;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 != r3) goto L_0x0148;
    L_0x012a:
        r2 = r15.mRight;
        r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT;
        r2.setConnectionType(r3);
        goto L_0x0148;
    L_0x0132:
        r2 = r15.mParent;
        r2 = r2.mRight;
        r2 = r14.createObjectVariable(r2);
        r3 = r45.createRow();
        r4 = r45.createSlackVariable();
        r3.createRowGreaterThan(r2, r10, r4, r6);
        r14.addConstraint(r3);
    L_0x0148:
        r2 = r15.mParent;
        r2 = r2.getVerticalDimensionBehaviour();
        r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (r2 != r3) goto L_0x01d2;
    L_0x0152:
        if (r1 != 0) goto L_0x01d2;
    L_0x0154:
        r2 = r15.mTop;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x017d;
    L_0x015a:
        r2 = r15.mTop;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 == r3) goto L_0x0165;
    L_0x0164:
        goto L_0x017d;
    L_0x0165:
        r2 = r15.mTop;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x0193;
    L_0x016b:
        r2 = r15.mTop;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 != r3) goto L_0x0193;
    L_0x0175:
        r2 = r15.mTop;
        r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT;
        r2.setConnectionType(r3);
        goto L_0x0193;
    L_0x017d:
        r2 = r15.mParent;
        r2 = r2.mTop;
        r2 = r14.createObjectVariable(r2);
        r3 = r45.createRow();
        r4 = r45.createSlackVariable();
        r3.createRowGreaterThan(r9, r2, r4, r6);
        r14.addConstraint(r3);
    L_0x0193:
        r2 = r15.mBottom;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x01bc;
    L_0x0199:
        r2 = r15.mBottom;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 == r3) goto L_0x01a4;
    L_0x01a3:
        goto L_0x01bc;
    L_0x01a4:
        r2 = r15.mBottom;
        r2 = r2.mTarget;
        if (r2 == 0) goto L_0x01d2;
    L_0x01aa:
        r2 = r15.mBottom;
        r2 = r2.mTarget;
        r2 = r2.mOwner;
        r3 = r15.mParent;
        if (r2 != r3) goto L_0x01d2;
    L_0x01b4:
        r2 = r15.mBottom;
        r3 = android.support.constraint.solver.widgets.ConstraintAnchor.ConnectionType.STRICT;
        r2.setConnectionType(r3);
        goto L_0x01d2;
    L_0x01bc:
        r2 = r15.mParent;
        r2 = r2.mBottom;
        r2 = r14.createObjectVariable(r2);
        r3 = r45.createRow();
        r4 = r45.createSlackVariable();
        r3.createRowGreaterThan(r2, r8, r4, r6);
        r14.addConstraint(r3);
    L_0x01d2:
        r16 = r0;
        r17 = r1;
        goto L_0x01db;
    L_0x01d7:
        r16 = r6;
        r17 = r16;
    L_0x01db:
        r0 = r15.mWidth;
        r1 = r15.mMinWidth;
        if (r0 >= r1) goto L_0x01e3;
    L_0x01e1:
        r0 = r15.mMinWidth;
    L_0x01e3:
        r1 = r15.mHeight;
        r2 = r15.mMinHeight;
        if (r1 >= r2) goto L_0x01eb;
    L_0x01e9:
        r1 = r15.mMinHeight;
    L_0x01eb:
        r2 = r15.mHorizontalDimensionBehaviour;
        r3 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        if (r2 == r3) goto L_0x01f3;
    L_0x01f1:
        r2 = r5;
        goto L_0x01f4;
    L_0x01f3:
        r2 = r6;
    L_0x01f4:
        r3 = r15.mVerticalDimensionBehaviour;
        r4 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        if (r3 == r4) goto L_0x01fc;
    L_0x01fa:
        r3 = r5;
        goto L_0x01fd;
    L_0x01fc:
        r3 = r6;
    L_0x01fd:
        if (r2 != 0) goto L_0x0214;
    L_0x01ff:
        r4 = r15.mLeft;
        if (r4 == 0) goto L_0x0214;
    L_0x0203:
        r4 = r15.mRight;
        if (r4 == 0) goto L_0x0214;
    L_0x0207:
        r4 = r15.mLeft;
        r4 = r4.mTarget;
        if (r4 == 0) goto L_0x0213;
    L_0x020d:
        r4 = r15.mRight;
        r4 = r4.mTarget;
        if (r4 != 0) goto L_0x0214;
    L_0x0213:
        r2 = r5;
    L_0x0214:
        if (r3 != 0) goto L_0x023f;
    L_0x0216:
        r4 = r15.mTop;
        if (r4 == 0) goto L_0x023f;
    L_0x021a:
        r4 = r15.mBottom;
        if (r4 == 0) goto L_0x023f;
    L_0x021e:
        r4 = r15.mTop;
        r4 = r4.mTarget;
        if (r4 == 0) goto L_0x022a;
    L_0x0224:
        r4 = r15.mBottom;
        r4 = r4.mTarget;
        if (r4 != 0) goto L_0x023f;
    L_0x022a:
        r4 = r15.mBaselineDistance;
        if (r4 == 0) goto L_0x023e;
    L_0x022e:
        r4 = r15.mBaseline;
        if (r4 == 0) goto L_0x023f;
    L_0x0232:
        r4 = r15.mTop;
        r4 = r4.mTarget;
        if (r4 == 0) goto L_0x023e;
    L_0x0238:
        r4 = r15.mBaseline;
        r4 = r4.mTarget;
        if (r4 != 0) goto L_0x023f;
    L_0x023e:
        r3 = r5;
    L_0x023f:
        r4 = r15.mDimensionRatioSide;
        r5 = r15.mDimensionRatio;
        r6 = r15.mDimensionRatio;
        r20 = 0;
        r6 = (r6 > r20 ? 1 : (r6 == r20 ? 0 : -1));
        r21 = r8;
        r8 = -1;
        if (r6 <= 0) goto L_0x02c9;
    L_0x024e:
        r6 = r15.mVisibility;
        r12 = 8;
        if (r6 == r12) goto L_0x02c9;
    L_0x0254:
        r6 = r15.mHorizontalDimensionBehaviour;
        r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        r20 = 1065353216; // 0x3f800000 float:1.0 double:5.263544247E-315;
        if (r6 != r12) goto L_0x0295;
    L_0x025c:
        r6 = r15.mVerticalDimensionBehaviour;
        r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        if (r6 != r12) goto L_0x0295;
    L_0x0262:
        if (r2 == 0) goto L_0x026f;
    L_0x0264:
        if (r3 != 0) goto L_0x026f;
    L_0x0266:
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r25 = r5;
        r12 = 0;
        goto L_0x0292;
    L_0x026f:
        if (r2 != 0) goto L_0x028a;
    L_0x0271:
        if (r3 == 0) goto L_0x028a;
    L_0x0273:
        r4 = r15.mDimensionRatioSide;
        if (r4 != r8) goto L_0x0281;
    L_0x0277:
        r20 = r20 / r5;
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r25 = r20;
        goto L_0x0288;
    L_0x0281:
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r25 = r5;
    L_0x0288:
        r12 = 1;
        goto L_0x0292;
    L_0x028a:
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r12 = r4;
        r25 = r5;
    L_0x0292:
        r20 = 1;
        goto L_0x02d3;
    L_0x0295:
        r6 = r15.mHorizontalDimensionBehaviour;
        r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        if (r6 != r12) goto L_0x02ac;
    L_0x029b:
        r0 = r15.mHeight;
        r0 = (float) r0;
        r0 = r0 * r5;
        r0 = (int) r0;
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r25 = r5;
        r3 = 1;
        r12 = 0;
        r20 = 0;
        goto L_0x02d4;
    L_0x02ac:
        r6 = r15.mVerticalDimensionBehaviour;
        r12 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        if (r6 != r12) goto L_0x02c9;
    L_0x02b2:
        r1 = r15.mDimensionRatioSide;
        if (r1 != r8) goto L_0x02b8;
    L_0x02b6:
        r5 = r20 / r5;
    L_0x02b8:
        r1 = r15.mWidth;
        r1 = (float) r1;
        r1 = r1 * r5;
        r1 = (int) r1;
        r23 = r0;
        r6 = r1;
        r3 = r2;
        r25 = r5;
        r12 = 1;
        r20 = 0;
        r24 = 1;
        goto L_0x02d4;
    L_0x02c9:
        r23 = r0;
        r6 = r1;
        r24 = r3;
        r12 = r4;
        r25 = r5;
        r20 = 0;
    L_0x02d3:
        r3 = r2;
    L_0x02d4:
        if (r20 == 0) goto L_0x02dd;
    L_0x02d6:
        if (r12 == 0) goto L_0x02da;
    L_0x02d8:
        if (r12 != r8) goto L_0x02dd;
    L_0x02da:
        r26 = 1;
        goto L_0x02df;
    L_0x02dd:
        r26 = 0;
    L_0x02df:
        r0 = r15.mHorizontalDimensionBehaviour;
        r1 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (r0 != r1) goto L_0x02eb;
    L_0x02e5:
        r0 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer;
        if (r0 == 0) goto L_0x02eb;
    L_0x02e9:
        r2 = 1;
        goto L_0x02ec;
    L_0x02eb:
        r2 = 0;
    L_0x02ec:
        r0 = r15.mHorizontalResolution;
        r5 = 2;
        r4 = 3;
        if (r0 == r5) goto L_0x03d7;
    L_0x02f2:
        r1 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r13 == r1) goto L_0x0303;
    L_0x02f7:
        r0 = r15.mLeft;
        r0 = r0.mGroup;
        if (r0 != r13) goto L_0x03d7;
    L_0x02fd:
        r0 = r15.mRight;
        r0 = r0.mGroup;
        if (r0 != r13) goto L_0x03d7;
    L_0x0303:
        if (r26 == 0) goto L_0x038d;
    L_0x0305:
        r0 = r15.mLeft;
        r0 = r0.mTarget;
        if (r0 == 0) goto L_0x038d;
    L_0x030b:
        r0 = r15.mRight;
        r0 = r0.mTarget;
        if (r0 == 0) goto L_0x038d;
    L_0x0311:
        r0 = r15.mLeft;
        r2 = r14.createObjectVariable(r0);
        r0 = r15.mRight;
        r3 = r14.createObjectVariable(r0);
        r0 = r15.mLeft;
        r0 = r0.getTarget();
        r0 = r14.createObjectVariable(r0);
        r1 = r15.mRight;
        r1 = r1.getTarget();
        r1 = r14.createObjectVariable(r1);
        r5 = r15.mLeft;
        r5 = r5.getMargin();
        r14.addGreaterThan(r2, r0, r5, r4);
        r5 = r15.mRight;
        r5 = r5.getMargin();
        r5 = r5 * r8;
        r14.addLowerThan(r3, r1, r5, r4);
        if (r16 != 0) goto L_0x0378;
    L_0x0346:
        r5 = r15.mLeft;
        r5 = r5.getMargin();
        r4 = r15.mHorizontalBiasPercent;
        r8 = r15.mRight;
        r8 = r8.getMargin();
        r16 = 4;
        r22 = r0;
        r0 = r14;
        r23 = r1;
        r27 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1 = r2;
        r2 = r22;
        r22 = r3;
        r3 = r5;
        r5 = 3;
        r5 = r23;
        r28 = r6;
        r18 = 0;
        r6 = r22;
        r29 = r7;
        r7 = r8;
        r30 = r21;
        r8 = r16;
        r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8);
        goto L_0x0383;
    L_0x0378:
        r28 = r6;
        r29 = r7;
        r30 = r21;
        r18 = 0;
        r27 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0383:
        r32 = r9;
        r33 = r10;
        r34 = r11;
        r35 = r12;
        goto L_0x03e7;
    L_0x038d:
        r27 = r1;
        r28 = r6;
        r29 = r7;
        r30 = r21;
        r18 = 0;
        r4 = r15.mLeft;
        r5 = r15.mRight;
        r6 = r15.mX;
        r0 = r15.mX;
        r7 = r0 + r23;
        r8 = r15.mMinWidth;
        r1 = r15.mHorizontalBiasPercent;
        r0 = r15.mMatchConstraintDefaultWidth;
        r13 = r15.mMatchConstraintMinWidth;
        r31 = r13;
        r13 = r15.mMatchConstraintMaxWidth;
        r19 = r0;
        r0 = r15;
        r21 = r1;
        r1 = r14;
        r22 = r8;
        r8 = r23;
        r32 = r9;
        r9 = r22;
        r33 = r10;
        r10 = r21;
        r34 = r11;
        r11 = r26;
        r35 = r12;
        r12 = r16;
        r21 = r13;
        r16 = r31;
        r13 = r19;
        r14 = r16;
        r15 = r21;
        r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15);
        r15 = r44;
        goto L_0x03e7;
    L_0x03d7:
        r28 = r6;
        r29 = r7;
        r32 = r9;
        r33 = r10;
        r34 = r11;
        r35 = r12;
        r30 = r21;
        r18 = 0;
    L_0x03e7:
        r0 = r15.mVerticalResolution;
        r1 = 2;
        if (r0 != r1) goto L_0x03ed;
    L_0x03ec:
        return;
    L_0x03ed:
        r0 = r15.mVerticalDimensionBehaviour;
        r1 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (r0 != r1) goto L_0x03f9;
    L_0x03f3:
        r0 = r15 instanceof android.support.constraint.solver.widgets.ConstraintWidgetContainer;
        if (r0 == 0) goto L_0x03f9;
    L_0x03f7:
        r2 = 1;
        goto L_0x03fb;
    L_0x03f9:
        r2 = r18;
    L_0x03fb:
        if (r20 == 0) goto L_0x0409;
    L_0x03fd:
        r14 = r35;
        r13 = 1;
        if (r14 == r13) goto L_0x0406;
    L_0x0402:
        r0 = -1;
        if (r14 != r0) goto L_0x040d;
    L_0x0405:
        goto L_0x0407;
    L_0x0406:
        r0 = -1;
    L_0x0407:
        r11 = r13;
        goto L_0x040f;
    L_0x0409:
        r14 = r35;
        r0 = -1;
        r13 = 1;
    L_0x040d:
        r11 = r18;
    L_0x040f:
        r1 = r15.mBaselineDistance;
        if (r1 <= 0) goto L_0x0514;
    L_0x0413:
        r1 = r15.mBottom;
        r12 = 5;
        r9 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r10 = r46;
        if (r10 == r9) goto L_0x042f;
    L_0x041d:
        r3 = r15.mBottom;
        r3 = r3.mGroup;
        if (r3 != r10) goto L_0x042a;
    L_0x0423:
        r3 = r15.mBaseline;
        r3 = r3.mGroup;
        if (r3 != r10) goto L_0x042a;
    L_0x0429:
        goto L_0x042f;
    L_0x042a:
        r7 = r32;
        r8 = r45;
        goto L_0x043c;
    L_0x042f:
        r3 = r44.getBaselineDistance();
        r4 = r29;
        r7 = r32;
        r8 = r45;
        r8.addEquality(r4, r7, r3, r12);
    L_0x043c:
        r3 = r15.mBaseline;
        r3 = r3.mTarget;
        if (r3 == 0) goto L_0x044a;
    L_0x0442:
        r1 = r15.mBaselineDistance;
        r3 = r15.mBaseline;
        r16 = r1;
        r5 = r3;
        goto L_0x044d;
    L_0x044a:
        r5 = r1;
        r16 = r28;
    L_0x044d:
        if (r10 == r9) goto L_0x0462;
    L_0x044f:
        r1 = r15.mTop;
        r1 = r1.mGroup;
        if (r1 != r10) goto L_0x045a;
    L_0x0455:
        r1 = r5.mGroup;
        if (r1 != r10) goto L_0x045a;
    L_0x0459:
        goto L_0x0462;
    L_0x045a:
        r15 = r8;
        r39 = r14;
        r13 = r30;
        r14 = r7;
        goto L_0x0534;
    L_0x0462:
        if (r11 == 0) goto L_0x04cd;
    L_0x0464:
        r1 = r15.mTop;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x04cd;
    L_0x046a:
        r1 = r15.mBottom;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x04cd;
    L_0x0470:
        r1 = r15.mTop;
        r1 = r8.createObjectVariable(r1);
        r2 = r15.mBottom;
        r6 = r8.createObjectVariable(r2);
        r2 = r15.mTop;
        r2 = r2.getTarget();
        r2 = r8.createObjectVariable(r2);
        r3 = r15.mBottom;
        r3 = r3.getTarget();
        r5 = r8.createObjectVariable(r3);
        r3 = r15.mTop;
        r3 = r3.getMargin();
        r11 = 3;
        r8.addGreaterThan(r1, r2, r3, r11);
        r3 = r15.mBottom;
        r3 = r3.getMargin();
        r0 = r0 * r3;
        r8.addLowerThan(r6, r5, r0, r11);
        if (r17 != 0) goto L_0x04c1;
    L_0x04a6:
        r0 = r15.mTop;
        r3 = r0.getMargin();
        r4 = r15.mVerticalBiasPercent;
        r0 = r15.mBottom;
        r12 = r0.getMargin();
        r16 = 4;
        r0 = r8;
        r36 = r7;
        r7 = r12;
        r12 = r8;
        r8 = r16;
        r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8);
        goto L_0x04c4;
    L_0x04c1:
        r36 = r7;
        r12 = r8;
    L_0x04c4:
        r15 = r12;
        r39 = r14;
        r13 = r30;
        r14 = r36;
        goto L_0x0534;
    L_0x04cd:
        r36 = r7;
        r7 = 3;
        r4 = r15.mTop;
        r6 = r15.mY;
        r0 = r15.mY;
        r18 = r0 + r16;
        r3 = r15.mMinHeight;
        r1 = r15.mVerticalBiasPercent;
        r0 = r15.mMatchConstraintDefaultHeight;
        r37 = r14;
        r14 = r15.mMatchConstraintMinHeight;
        r38 = r14;
        r14 = r15.mMatchConstraintMaxHeight;
        r19 = r0;
        r0 = r15;
        r21 = r1;
        r1 = r8;
        r22 = r3;
        r3 = r24;
        r7 = r18;
        r8 = r16;
        r9 = r22;
        r10 = r21;
        r12 = r17;
        r13 = r19;
        r16 = r14;
        r39 = r37;
        r14 = r38;
        r15 = r16;
        r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15);
        r8 = r28;
        r13 = r30;
        r14 = r36;
        r0 = 5;
        r15 = r45;
        r15.addEquality(r13, r14, r8, r0);
        goto L_0x0534;
    L_0x0514:
        r39 = r14;
        r8 = r28;
        r13 = r30;
        r14 = r32;
        r10 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r12 = r46;
        r15 = r45;
        if (r12 == r10) goto L_0x053a;
    L_0x0525:
        r9 = r44;
        r1 = r9.mTop;
        r1 = r1.mGroup;
        if (r1 != r12) goto L_0x0534;
    L_0x052d:
        r1 = r9.mBottom;
        r1 = r1.mGroup;
        if (r1 != r12) goto L_0x0534;
    L_0x0533:
        goto L_0x053c;
    L_0x0534:
        r42 = r13;
        r43 = r14;
        goto L_0x05cf;
    L_0x053a:
        r9 = r44;
    L_0x053c:
        if (r11 == 0) goto L_0x0595;
    L_0x053e:
        r1 = r9.mTop;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x0595;
    L_0x0544:
        r1 = r9.mBottom;
        r1 = r1.mTarget;
        if (r1 == 0) goto L_0x0595;
    L_0x054a:
        r1 = r9.mTop;
        r1 = r15.createObjectVariable(r1);
        r2 = r9.mBottom;
        r6 = r15.createObjectVariable(r2);
        r2 = r9.mTop;
        r2 = r2.getTarget();
        r2 = r15.createObjectVariable(r2);
        r3 = r9.mBottom;
        r3 = r3.getTarget();
        r5 = r15.createObjectVariable(r3);
        r3 = r9.mTop;
        r3 = r3.getMargin();
        r11 = 3;
        r15.addGreaterThan(r1, r2, r3, r11);
        r3 = r9.mBottom;
        r3 = r3.getMargin();
        r8 = r0 * r3;
        r15.addLowerThan(r6, r5, r8, r11);
        if (r17 != 0) goto L_0x0534;
    L_0x0581:
        r0 = r9.mTop;
        r3 = r0.getMargin();
        r4 = r9.mVerticalBiasPercent;
        r0 = r9.mBottom;
        r7 = r0.getMargin();
        r8 = 4;
        r0 = r15;
        r0.addCentering(r1, r2, r3, r4, r5, r6, r7, r8);
        goto L_0x0534;
    L_0x0595:
        r7 = 3;
        r4 = r9.mTop;
        r5 = r9.mBottom;
        r6 = r9.mY;
        r0 = r9.mY;
        r16 = r0 + r8;
        r3 = r9.mMinHeight;
        r1 = r9.mVerticalBiasPercent;
        r0 = r9.mMatchConstraintDefaultHeight;
        r40 = r14;
        r14 = r9.mMatchConstraintMinHeight;
        r41 = r14;
        r14 = r9.mMatchConstraintMaxHeight;
        r18 = r0;
        r0 = r9;
        r19 = r1;
        r1 = r15;
        r21 = r3;
        r3 = r24;
        r7 = r16;
        r9 = r21;
        r10 = r19;
        r12 = r17;
        r42 = r13;
        r13 = r18;
        r16 = r14;
        r43 = r40;
        r14 = r41;
        r15 = r16;
        r0.applyConstraints(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15);
    L_0x05cf:
        if (r20 == 0) goto L_0x065f;
    L_0x05d1:
        r0 = r45.createRow();
        r1 = r46;
        r2 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r1 == r2) goto L_0x05eb;
    L_0x05dc:
        r8 = r44;
        r2 = r8.mLeft;
        r2 = r2.mGroup;
        if (r2 != r1) goto L_0x0661;
    L_0x05e4:
        r2 = r8.mRight;
        r2 = r2.mGroup;
        if (r2 != r1) goto L_0x0661;
    L_0x05ea:
        goto L_0x05ed;
    L_0x05eb:
        r8 = r44;
    L_0x05ed:
        r4 = r39;
        if (r4 != 0) goto L_0x0606;
    L_0x05f1:
        r2 = r0;
        r3 = r33;
        r4 = r34;
        r5 = r42;
        r6 = r43;
        r7 = r25;
        r0 = r2.createRowDimensionRatio(r3, r4, r5, r6, r7);
        r1 = r45;
        r1.addConstraint(r0);
        goto L_0x0661;
    L_0x0606:
        r1 = r45;
        r2 = 1;
        if (r4 != r2) goto L_0x061e;
    L_0x060b:
        r2 = r0;
        r3 = r42;
        r4 = r43;
        r5 = r33;
        r6 = r34;
        r7 = r25;
        r0 = r2.createRowDimensionRatio(r3, r4, r5, r6, r7);
        r1.addConstraint(r0);
        goto L_0x0661;
    L_0x061e:
        r2 = r8.mMatchConstraintMinWidth;
        if (r2 <= 0) goto L_0x062d;
    L_0x0622:
        r2 = r8.mMatchConstraintMinWidth;
        r3 = r33;
        r4 = r34;
        r5 = 3;
        r1.addGreaterThan(r3, r4, r2, r5);
        goto L_0x0632;
    L_0x062d:
        r3 = r33;
        r4 = r34;
        r5 = 3;
    L_0x0632:
        r2 = r8.mMatchConstraintMinHeight;
        if (r2 <= 0) goto L_0x0640;
    L_0x0636:
        r2 = r8.mMatchConstraintMinHeight;
        r7 = r42;
        r6 = r43;
        r1.addGreaterThan(r7, r6, r2, r5);
        goto L_0x0644;
    L_0x0640:
        r7 = r42;
        r6 = r43;
    L_0x0644:
        r9 = 4;
        r2 = r0;
        r5 = r7;
        r7 = r25;
        r2.createRowDimensionRatio(r3, r4, r5, r6, r7);
        r2 = r45.createErrorVariable();
        r3 = r45.createErrorVariable();
        r2.strength = r9;
        r3.strength = r9;
        r0.addError(r2, r3);
        r1.addConstraint(r0);
        goto L_0x0661;
    L_0x065f:
        r8 = r44;
    L_0x0661:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidget.addToSolver(android.support.constraint.solver.LinearSystem, int):void");
    }

    private void applyConstraints(LinearSystem linearSystem, boolean z, boolean z2, ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2, int i3, int i4, float f, boolean z3, boolean z4, int i5, int i6, int i7) {
        boolean z5;
        LinearSystem linearSystem2 = linearSystem;
        int i8 = i;
        int i9 = i2;
        int i10 = i4;
        int i11 = i6;
        int i12 = i7;
        SolverVariable createObjectVariable = linearSystem2.createObjectVariable(constraintAnchor);
        SolverVariable createObjectVariable2 = linearSystem2.createObjectVariable(constraintAnchor2);
        SolverVariable createObjectVariable3 = linearSystem2.createObjectVariable(constraintAnchor.getTarget());
        SolverVariable createObjectVariable4 = linearSystem2.createObjectVariable(constraintAnchor2.getTarget());
        int margin = constraintAnchor.getMargin();
        i12 = constraintAnchor2.getMargin();
        if (this.mVisibility == 8) {
            z5 = true;
            i11 = 0;
        } else {
            z5 = z2;
            i11 = i3;
        }
        if (createObjectVariable3 == null && createObjectVariable4 == null) {
            linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, i8));
            if (!z3) {
                if (z) {
                    linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i10, true));
                    return;
                } else if (z5) {
                    linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i11, false));
                    return;
                } else {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, i9));
                    return;
                }
            }
            return;
        }
        boolean z6 = false;
        if (createObjectVariable3 == null || createObjectVariable4 != null) {
            i9 = -1;
            if (createObjectVariable3 == null && createObjectVariable4 != null) {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, i9 * i12));
                if (z) {
                    linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i10, true));
                    return;
                } else if (!z3) {
                    if (z5) {
                        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i11));
                        return;
                    } else {
                        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, i8));
                        return;
                    }
                } else {
                    return;
                }
            } else if (z5) {
                if (z) {
                    linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i10, true));
                } else {
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i11));
                }
                if (constraintAnchor.getStrength() != constraintAnchor2.getStrength()) {
                    SolverVariable createSlackVariable;
                    ArrayRow createRow;
                    if (constraintAnchor.getStrength() == Strength.STRONG) {
                        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
                        createSlackVariable = linearSystem.createSlackVariable();
                        createRow = linearSystem.createRow();
                        createRow.createRowLowerThan(createObjectVariable2, createObjectVariable4, createSlackVariable, i9 * i12);
                        linearSystem2.addConstraint(createRow);
                        return;
                    }
                    createSlackVariable = linearSystem.createSlackVariable();
                    createRow = linearSystem.createRow();
                    createRow.createRowGreaterThan(createObjectVariable, createObjectVariable3, createSlackVariable, margin);
                    linearSystem2.addConstraint(createRow);
                    linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, i9 * i12));
                    return;
                } else if (createObjectVariable3 == createObjectVariable4) {
                    linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem2, createObjectVariable, createObjectVariable3, 0, 0.5f, createObjectVariable4, createObjectVariable2, 0, true));
                    return;
                } else if (!z4) {
                    linearSystem2.addConstraint(LinearSystem.createRowGreaterThan(linearSystem2, createObjectVariable, createObjectVariable3, margin, constraintAnchor.getConnectionType() != ConnectionType.STRICT ? true : z6));
                    linearSystem2.addConstraint(LinearSystem.createRowLowerThan(linearSystem2, createObjectVariable2, createObjectVariable4, i9 * i12, constraintAnchor2.getConnectionType() != ConnectionType.STRICT ? true : z6));
                    linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem2, createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, i12, false));
                    return;
                } else {
                    return;
                }
            } else {
                i8 = 3;
                if (z3) {
                    linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, i8);
                    linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, i9 * i12, i8);
                    linearSystem2.addConstraint(LinearSystem.createRowCentering(linearSystem2, createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, i12, true));
                    return;
                } else if (!z4) {
                    int i13 = 2;
                    int i14;
                    int i15;
                    if (i5 == 1) {
                        i10 = i6;
                        if (i10 <= i11) {
                            i10 = i11;
                        }
                        i14 = i12;
                        i15 = i7;
                        if (i15 > 0) {
                            if (i15 < i10) {
                                i10 = i15;
                            } else {
                                linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable, i15, i8);
                            }
                        }
                        linearSystem2.addEquality(createObjectVariable2, createObjectVariable, i10, i8);
                        linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, i13);
                        linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, -i14, i13);
                        linearSystem2.addCentering(createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, i14, 4);
                        return;
                    }
                    i14 = i12;
                    i15 = i7;
                    if (i6 == 0 && i15 == 0) {
                        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
                        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable4, i9 * i14));
                        return;
                    }
                    if (i15 > 0) {
                        linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable, i15, i8);
                    }
                    linearSystem2.addGreaterThan(createObjectVariable, createObjectVariable3, margin, i13);
                    linearSystem2.addLowerThan(createObjectVariable2, createObjectVariable4, -i14, i13);
                    linearSystem2.addCentering(createObjectVariable, createObjectVariable3, margin, f, createObjectVariable4, createObjectVariable2, i14, 4);
                    return;
                } else {
                    return;
                }
            }
        }
        linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable, createObjectVariable3, margin));
        if (z) {
            linearSystem2.addConstraint(LinearSystem.createRowEquals(linearSystem2, createObjectVariable2, createObjectVariable, i10, true));
        } else if (!z3) {
            if (z5) {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, createObjectVariable, i11));
            } else {
                linearSystem2.addConstraint(linearSystem.createRow().createRowEquals(createObjectVariable2, i9));
            }
        }
    }

    public void updateFromSolver(LinearSystem linearSystem, int i) {
        if (i == Integer.MAX_VALUE) {
            setFrame(linearSystem.getObjectVariableValue(this.mLeft), linearSystem.getObjectVariableValue(this.mTop), linearSystem.getObjectVariableValue(this.mRight), linearSystem.getObjectVariableValue(this.mBottom));
        } else if (i == -2) {
            setFrame(this.mSolverLeft, this.mSolverTop, this.mSolverRight, this.mSolverBottom);
        } else {
            if (this.mLeft.mGroup == i) {
                this.mSolverLeft = linearSystem.getObjectVariableValue(this.mLeft);
            }
            if (this.mTop.mGroup == i) {
                this.mSolverTop = linearSystem.getObjectVariableValue(this.mTop);
            }
            if (this.mRight.mGroup == i) {
                this.mSolverRight = linearSystem.getObjectVariableValue(this.mRight);
            }
            if (this.mBottom.mGroup == i) {
                this.mSolverBottom = linearSystem.getObjectVariableValue(this.mBottom);
            }
        }
    }

    public void updateFromSolver(LinearSystem linearSystem) {
        updateFromSolver(linearSystem, Integer.MAX_VALUE);
    }
}
