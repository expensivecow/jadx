package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;
import java.util.ArrayList;
import java.util.Arrays;

public class ConstraintWidgetContainer extends WidgetContainer {
    static boolean ALLOW_ROOT_GROUP = true;
    private static final int CHAIN_FIRST = 0;
    private static final int CHAIN_FIRST_VISIBLE = 2;
    private static final int CHAIN_LAST = 1;
    private static final int CHAIN_LAST_VISIBLE = 3;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_OPTIMIZE = false;
    private static final int FLAG_CHAIN_DANGLING = 1;
    private static final int FLAG_CHAIN_OPTIMIZE = 0;
    private static final int FLAG_RECOMPUTE_BOUNDS = 2;
    private static final int MAX_ITERATIONS = 8;
    public static final int OPTIMIZATION_ALL = 2;
    public static final int OPTIMIZATION_BASIC = 4;
    public static final int OPTIMIZATION_CHAIN = 8;
    public static final int OPTIMIZATION_NONE = 1;
    private static final boolean USE_SNAPSHOT = true;
    private static final boolean USE_THREAD = false;
    private boolean[] flags;
    protected LinearSystem mBackgroundSystem = null;
    private ConstraintWidget[] mChainEnds;
    private boolean mHeightMeasuredTooSmall;
    private ConstraintWidget[] mHorizontalChainsArray;
    private int mHorizontalChainsSize;
    private ConstraintWidget[] mMatchConstraintsChainedWidgets;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem = new LinearSystem();
    private ConstraintWidget[] mVerticalChainsArray;
    private int mVerticalChainsSize;
    private boolean mWidthMeasuredTooSmall;
    int mWrapHeight;
    int mWrapWidth;

    public String getType() {
        return "ConstraintLayout";
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public ConstraintWidgetContainer() {
        boolean z = false;
        this.mHorizontalChainsSize = z;
        this.mVerticalChainsSize = z;
        int i = 4;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[i];
        this.mVerticalChainsArray = new ConstraintWidget[i];
        this.mHorizontalChainsArray = new ConstraintWidget[i];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[i];
        this.mWidthMeasuredTooSmall = z;
        this.mHeightMeasuredTooSmall = z;
    }

    public ConstraintWidgetContainer(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        boolean z = false;
        this.mHorizontalChainsSize = z;
        this.mVerticalChainsSize = z;
        i2 = 4;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[i2];
        this.mVerticalChainsArray = new ConstraintWidget[i2];
        this.mHorizontalChainsArray = new ConstraintWidget[i2];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[i2];
        this.mWidthMeasuredTooSmall = z;
        this.mHeightMeasuredTooSmall = z;
    }

    public ConstraintWidgetContainer(int i, int i2) {
        super(i, i2);
        boolean z = false;
        this.mHorizontalChainsSize = z;
        this.mVerticalChainsSize = z;
        i2 = 4;
        this.mMatchConstraintsChainedWidgets = new ConstraintWidget[i2];
        this.mVerticalChainsArray = new ConstraintWidget[i2];
        this.mHorizontalChainsArray = new ConstraintWidget[i2];
        this.mOptimizationLevel = 2;
        this.flags = new boolean[3];
        this.mChainEnds = new ConstraintWidget[i2];
        this.mWidthMeasuredTooSmall = z;
        this.mHeightMeasuredTooSmall = z;
    }

    public void setOptimizationLevel(int i) {
        this.mOptimizationLevel = i;
    }

    public void reset() {
        this.mSystem.reset();
        int i = 0;
        this.mPaddingLeft = i;
        this.mPaddingRight = i;
        this.mPaddingTop = i;
        this.mPaddingBottom = i;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public static ConstraintWidgetContainer createContainer(ConstraintWidgetContainer constraintWidgetContainer, String str, ArrayList<ConstraintWidget> arrayList, int i) {
        Rectangle bounds = WidgetContainer.getBounds(arrayList);
        if (bounds.width == 0 || bounds.height == 0) {
            return null;
        }
        int min;
        if (i > 0) {
            min = Math.min(bounds.x, bounds.y);
            if (i > min) {
                i = min;
            }
            bounds.grow(i, i);
        }
        constraintWidgetContainer.setOrigin(bounds.x, bounds.y);
        constraintWidgetContainer.setDimension(bounds.width, bounds.height);
        constraintWidgetContainer.setDebugName(str);
        int i2 = 0;
        ConstraintWidget parent = ((ConstraintWidget) arrayList.get(i2)).getParent();
        min = arrayList.size();
        while (i2 < min) {
            ConstraintWidget constraintWidget = (ConstraintWidget) arrayList.get(i2);
            if (constraintWidget.getParent() == parent) {
                constraintWidgetContainer.add(constraintWidget);
                constraintWidget.setX(constraintWidget.getX() - bounds.x);
                constraintWidget.setY(constraintWidget.getY() - bounds.y);
            }
            i2++;
        }
        return constraintWidgetContainer;
    }

    public boolean addChildrenToSolver(LinearSystem linearSystem, int i) {
        boolean z;
        addToSolver(linearSystem, i);
        int size = this.mChildren.size();
        boolean z2 = true;
        int i2 = 0;
        if (this.mOptimizationLevel != 2 && this.mOptimizationLevel != 4) {
            z = z2;
        } else if (optimize(linearSystem)) {
            return i2;
        } else {
            z = i2;
        }
        while (i2 < size) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                DimensionBehaviour dimensionBehaviour = constraintWidget.mHorizontalDimensionBehaviour;
                DimensionBehaviour dimensionBehaviour2 = constraintWidget.mVerticalDimensionBehaviour;
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                if (dimensionBehaviour2 == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem, i);
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
                }
                if (dimensionBehaviour2 == DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
                }
            } else {
                if (z) {
                    Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                }
                constraintWidget.addToSolver(linearSystem, i);
            }
            i2++;
        }
        if (this.mHorizontalChainsSize > 0) {
            applyHorizontalChain(linearSystem);
        }
        if (this.mVerticalChainsSize > 0) {
            applyVerticalChain(linearSystem);
        }
        return z2;
    }

    private boolean optimize(LinearSystem linearSystem) {
        int i;
        boolean z;
        int size = this.mChildren.size();
        boolean z2 = false;
        int i2 = z2;
        while (true) {
            i = -1;
            z = true;
            if (i2 >= size) {
                break;
            }
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            constraintWidget.mHorizontalResolution = i;
            constraintWidget.mVerticalResolution = i;
            if (constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                constraintWidget.mHorizontalResolution = z;
                constraintWidget.mVerticalResolution = z;
            }
            i2++;
        }
        boolean z3 = z2;
        boolean z4 = z3;
        boolean z5 = z4;
        while (!z3) {
            int i3 = z2;
            boolean z6 = i3;
            boolean z7 = z6;
            while (i3 < size) {
                ConstraintWidget constraintWidget2 = (ConstraintWidget) this.mChildren.get(i3);
                if (constraintWidget2.mHorizontalResolution == i) {
                    if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                        constraintWidget2.mHorizontalResolution = z;
                    } else {
                        Optimizer.checkHorizontalSimpleDependency(this, linearSystem, constraintWidget2);
                    }
                }
                if (constraintWidget2.mVerticalResolution == i) {
                    if (this.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                        constraintWidget2.mVerticalResolution = z;
                    } else {
                        Optimizer.checkVerticalSimpleDependency(this, linearSystem, constraintWidget2);
                    }
                }
                if (constraintWidget2.mVerticalResolution == i) {
                    z6++;
                }
                if (constraintWidget2.mHorizontalResolution == i) {
                    z7++;
                }
                i3++;
            }
            if (!(z6 || z7) || (z4 == z6 && z5 == z7)) {
                z3 = z;
            }
            z4 = z6;
            z5 = z7;
        }
        int i4 = z2;
        i2 = i4;
        int i5 = i2;
        while (i4 < size) {
            ConstraintWidget constraintWidget3 = (ConstraintWidget) this.mChildren.get(i4);
            if (constraintWidget3.mHorizontalResolution == z || constraintWidget3.mHorizontalResolution == i) {
                i2++;
            }
            if (constraintWidget3.mVerticalResolution == z || constraintWidget3.mVerticalResolution == i) {
                i5++;
            }
            i4++;
        }
        return (i2 == 0 && i5 == 0) ? z : z2;
    }

    private void applyHorizontalChain(LinearSystem linearSystem) {
        ConstraintWidgetContainer constraintWidgetContainer = this;
        LinearSystem linearSystem2 = linearSystem;
        int i = 0;
        int i2 = i;
        while (i2 < constraintWidgetContainer.mHorizontalChainsSize) {
            int i3;
            LinearSystem linearSystem3;
            int i4;
            ConstraintWidget constraintWidget = constraintWidgetContainer.mHorizontalChainsArray[i2];
            int countMatchConstraintsChainedWidgets = constraintWidgetContainer.countMatchConstraintsChainedWidgets(linearSystem2, constraintWidgetContainer.mChainEnds, constraintWidgetContainer.mHorizontalChainsArray[i2], 0, constraintWidgetContainer.flags);
            int i5 = 2;
            ConstraintWidget constraintWidget2 = constraintWidgetContainer.mChainEnds[i5];
            if (constraintWidget2 != null) {
                i3 = 1;
                if (!constraintWidgetContainer.flags[i3]) {
                    int i6 = constraintWidget.mHorizontalChainStyle == 0 ? i3 : i;
                    int i7 = constraintWidget.mHorizontalChainStyle == i5 ? i3 : i;
                    int i8 = constraintWidgetContainer.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT ? i3 : i;
                    if ((constraintWidgetContainer.mOptimizationLevel != i5 && constraintWidgetContainer.mOptimizationLevel != 8) || !constraintWidgetContainer.flags[i] || !constraintWidget.mHorizontalChainFixedPosition || i7 != 0 || i8 != 0 || constraintWidget.mHorizontalChainStyle != 0) {
                        int i9 = 3;
                        ConstraintWidget constraintWidget3 = null;
                        ConstraintWidget constraintWidget4;
                        ConstraintWidget constraintWidget5;
                        SolverVariable solverVariable;
                        Object obj;
                        ConstraintWidget constraintWidget6;
                        if (countMatchConstraintsChainedWidgets != 0 && i7 == 0) {
                            float f = 0.0f;
                            constraintWidget4 = constraintWidget3;
                            while (constraintWidget2 != null) {
                                if (constraintWidget2.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                                    i8 = constraintWidget2.mLeft.getMargin();
                                    if (constraintWidget4 != null) {
                                        i8 += constraintWidget4.mRight.getMargin();
                                    }
                                    linearSystem2.addGreaterThan(constraintWidget2.mLeft.mSolverVariable, constraintWidget2.mLeft.mTarget.mSolverVariable, i8, constraintWidget2.mLeft.mTarget.mOwner.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT ? i5 : i9);
                                    i6 = constraintWidget2.mRight.getMargin();
                                    if (constraintWidget2.mRight.mTarget.mOwner.mLeft.mTarget != null && constraintWidget2.mRight.mTarget.mOwner.mLeft.mTarget.mOwner == constraintWidget2) {
                                        i6 += constraintWidget2.mRight.mTarget.mOwner.mLeft.getMargin();
                                    }
                                    linearSystem2.addLowerThan(constraintWidget2.mRight.mSolverVariable, constraintWidget2.mRight.mTarget.mSolverVariable, -i6, constraintWidget2.mRight.mTarget.mOwner.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT ? i5 : i9);
                                } else {
                                    f += constraintWidget2.mHorizontalWeight;
                                    if (constraintWidget2.mRight.mTarget != null) {
                                        i6 = constraintWidget2.mRight.getMargin();
                                        if (constraintWidget2 != constraintWidgetContainer.mChainEnds[i9]) {
                                            i6 += constraintWidget2.mRight.mTarget.mOwner.mLeft.getMargin();
                                        }
                                    } else {
                                        i6 = i;
                                    }
                                    linearSystem2.addGreaterThan(constraintWidget2.mRight.mSolverVariable, constraintWidget2.mLeft.mSolverVariable, i, i3);
                                    linearSystem2.addLowerThan(constraintWidget2.mRight.mSolverVariable, constraintWidget2.mRight.mTarget.mSolverVariable, -i6, i3);
                                }
                                constraintWidget4 = constraintWidget2;
                                constraintWidget2 = constraintWidget2.mHorizontalNextWidget;
                            }
                            int i10;
                            if (countMatchConstraintsChainedWidgets != i3) {
                                i10 = i;
                                while (true) {
                                    i6 = countMatchConstraintsChainedWidgets - 1;
                                    if (i10 >= i6) {
                                        break;
                                    }
                                    ConstraintWidget constraintWidget7 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i10];
                                    i10++;
                                    constraintWidget5 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i10];
                                    solverVariable = constraintWidget7.mLeft.mSolverVariable;
                                    SolverVariable solverVariable2 = constraintWidget7.mRight.mSolverVariable;
                                    SolverVariable solverVariable3 = constraintWidget5.mLeft.mSolverVariable;
                                    SolverVariable solverVariable4 = constraintWidget5.mRight.mSolverVariable;
                                    if (constraintWidget5 == constraintWidgetContainer.mChainEnds[i9]) {
                                        solverVariable4 = constraintWidgetContainer.mChainEnds[1].mRight.mSolverVariable;
                                    }
                                    i3 = constraintWidget7.mLeft.getMargin();
                                    if (!(constraintWidget7.mLeft.mTarget == null || constraintWidget7.mLeft.mTarget.mOwner.mRight.mTarget == null || constraintWidget7.mLeft.mTarget.mOwner.mRight.mTarget.mOwner != constraintWidget7)) {
                                        i3 += constraintWidget7.mLeft.mTarget.mOwner.mRight.getMargin();
                                    }
                                    int i11 = countMatchConstraintsChainedWidgets;
                                    linearSystem2.addGreaterThan(solverVariable, constraintWidget7.mLeft.mTarget.mSolverVariable, i3, 2);
                                    countMatchConstraintsChainedWidgets = constraintWidget7.mRight.getMargin();
                                    if (!(constraintWidget7.mRight.mTarget == null || constraintWidget7.mHorizontalNextWidget == null)) {
                                        countMatchConstraintsChainedWidgets += constraintWidget7.mHorizontalNextWidget.mLeft.mTarget != null ? constraintWidget7.mHorizontalNextWidget.mLeft.getMargin() : 0;
                                    }
                                    linearSystem2.addLowerThan(solverVariable2, constraintWidget7.mRight.mTarget.mSolverVariable, -countMatchConstraintsChainedWidgets, 2);
                                    if (i10 == i6) {
                                        countMatchConstraintsChainedWidgets = constraintWidget5.mLeft.getMargin();
                                        if (!(constraintWidget5.mLeft.mTarget == null || constraintWidget5.mLeft.mTarget.mOwner.mRight.mTarget == null || constraintWidget5.mLeft.mTarget.mOwner.mRight.mTarget.mOwner != constraintWidget5)) {
                                            countMatchConstraintsChainedWidgets += constraintWidget5.mLeft.mTarget.mOwner.mRight.getMargin();
                                        }
                                        linearSystem2.addGreaterThan(solverVariable3, constraintWidget5.mLeft.mTarget.mSolverVariable, countMatchConstraintsChainedWidgets, 2);
                                        ConstraintAnchor constraintAnchor = constraintWidget5.mRight;
                                        if (constraintWidget5 == constraintWidgetContainer.mChainEnds[3]) {
                                            constraintAnchor = constraintWidgetContainer.mChainEnds[1].mRight;
                                        }
                                        i6 = constraintAnchor.getMargin();
                                        if (!(constraintAnchor.mTarget == null || constraintAnchor.mTarget.mOwner.mLeft.mTarget == null || constraintAnchor.mTarget.mOwner.mLeft.mTarget.mOwner != constraintWidget5)) {
                                            i6 += constraintAnchor.mTarget.mOwner.mLeft.getMargin();
                                        }
                                        i3 = 2;
                                        linearSystem2.addLowerThan(solverVariable4, constraintAnchor.mTarget.mSolverVariable, -i6, i3);
                                    } else {
                                        i3 = 2;
                                    }
                                    if (constraintWidget.mMatchConstraintMaxWidth > 0) {
                                        linearSystem2.addLowerThan(solverVariable2, solverVariable, constraintWidget.mMatchConstraintMaxWidth, i3);
                                    }
                                    ArrayRow createRow = linearSystem.createRow();
                                    createRow.createRowEqualDimension(constraintWidget7.mHorizontalWeight, f, constraintWidget5.mHorizontalWeight, solverVariable, constraintWidget7.mLeft.getMargin(), solverVariable2, constraintWidget7.mRight.getMargin(), solverVariable3, constraintWidget5.mLeft.getMargin(), solverVariable4, constraintWidget5.mRight.getMargin());
                                    linearSystem2.addConstraint(createRow);
                                    countMatchConstraintsChainedWidgets = i11;
                                    obj = 2;
                                    Object obj2 = 1;
                                    i9 = 3;
                                    i = 0;
                                }
                            } else {
                                constraintWidget6 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i];
                                i10 = constraintWidget6.mLeft.getMargin();
                                if (constraintWidget6.mLeft.mTarget != null) {
                                    i10 += constraintWidget6.mLeft.mTarget.getMargin();
                                }
                                i6 = constraintWidget6.mRight.getMargin();
                                if (constraintWidget6.mRight.mTarget != null) {
                                    i6 += constraintWidget6.mRight.mTarget.getMargin();
                                }
                                SolverVariable solverVariable5 = constraintWidget.mRight.mTarget.mSolverVariable;
                                if (constraintWidget6 == constraintWidgetContainer.mChainEnds[i9]) {
                                    solverVariable5 = constraintWidgetContainer.mChainEnds[i3].mRight.mTarget.mSolverVariable;
                                }
                                if (constraintWidget6.mMatchConstraintDefaultWidth == i3) {
                                    linearSystem2.addGreaterThan(constraintWidget.mLeft.mSolverVariable, constraintWidget.mLeft.mTarget.mSolverVariable, i10, i3);
                                    linearSystem2.addLowerThan(constraintWidget.mRight.mSolverVariable, solverVariable5, -i6, i3);
                                    linearSystem2.addEquality(constraintWidget.mRight.mSolverVariable, constraintWidget.mLeft.mSolverVariable, constraintWidget.getWidth(), i5);
                                } else {
                                    linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, constraintWidget6.mLeft.mTarget.mSolverVariable, i10, i3);
                                    linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, solverVariable5, -i6, i3);
                                }
                            }
                        } else {
                            int margin;
                            SolverVariable solverVariable6;
                            constraintWidget6 = constraintWidget2;
                            ConstraintWidget constraintWidget8 = constraintWidget3;
                            ConstraintWidget constraintWidget9 = constraintWidget8;
                            Object obj3 = null;
                            while (constraintWidget6 != null) {
                                ConstraintWidget constraintWidget10;
                                ConstraintWidget constraintWidget11;
                                int i12;
                                ConstraintWidget constraintWidget12;
                                Object obj4;
                                constraintWidget5 = constraintWidget6.mHorizontalNextWidget;
                                if (constraintWidget5 == null) {
                                    constraintWidget10 = constraintWidgetContainer.mChainEnds[1];
                                    obj = 1;
                                } else {
                                    constraintWidget10 = constraintWidget8;
                                    obj = obj3;
                                }
                                ConstraintAnchor constraintAnchor2;
                                int margin2;
                                Object obj5;
                                if (i7 != 0) {
                                    constraintAnchor2 = constraintWidget6.mLeft;
                                    margin2 = constraintAnchor2.getMargin();
                                    if (constraintWidget9 != null) {
                                        margin2 += constraintWidget9.mRight.getMargin();
                                    }
                                    linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, margin2, constraintWidget2 != constraintWidget6 ? 3 : 1);
                                    if (constraintWidget6.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                                        ConstraintAnchor constraintAnchor3 = constraintWidget6.mRight;
                                        if (constraintWidget6.mMatchConstraintDefaultWidth == 1) {
                                            linearSystem2.addEquality(constraintAnchor3.mSolverVariable, constraintAnchor2.mSolverVariable, Math.max(constraintWidget6.mMatchConstraintMinWidth, constraintWidget6.getWidth()), 3);
                                        } else {
                                            i9 = 3;
                                            linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, constraintAnchor2.mMargin, i9);
                                            linearSystem2.addLowerThan(constraintAnchor3.mSolverVariable, constraintAnchor2.mSolverVariable, constraintWidget6.mMatchConstraintMinWidth, i9);
                                        }
                                    } else {
                                        obj5 = 3;
                                    }
                                } else {
                                    obj5 = 3;
                                    int i13 = 5;
                                    if (i6 != 0 || obj == null || constraintWidget9 == null) {
                                        if (i6 != 0 || obj != null || constraintWidget9 != null) {
                                            ConstraintWidget constraintWidget13;
                                            ConstraintAnchor constraintAnchor4 = constraintWidget6.mLeft;
                                            constraintAnchor2 = constraintWidget6.mRight;
                                            margin = constraintAnchor4.getMargin();
                                            margin2 = constraintAnchor2.getMargin();
                                            constraintWidget11 = constraintWidget6;
                                            i12 = i6;
                                            i6 = 1;
                                            linearSystem2.addGreaterThan(constraintAnchor4.mSolverVariable, constraintAnchor4.mTarget.mSolverVariable, margin, i6);
                                            int i14 = i2;
                                            linearSystem2.addLowerThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, -margin2, i6);
                                            SolverVariable solverVariable7 = constraintAnchor4.mTarget != null ? constraintAnchor4.mTarget.mSolverVariable : constraintWidget3;
                                            if (constraintWidget9 == null) {
                                                solverVariable7 = constraintWidget.mLeft.mTarget != null ? constraintWidget.mLeft.mTarget.mSolverVariable : constraintWidget3;
                                            }
                                            if (constraintWidget5 == null) {
                                                constraintWidget5 = constraintWidget10.mRight.mTarget != null ? constraintWidget10.mRight.mTarget.mOwner : constraintWidget3;
                                            }
                                            constraintWidget4 = constraintWidget5;
                                            if (constraintWidget4 != null) {
                                                SolverVariable solverVariable8 = constraintWidget4.mLeft.mSolverVariable;
                                                if (obj != null) {
                                                    solverVariable8 = constraintWidget10.mRight.mTarget != null ? constraintWidget10.mRight.mTarget.mSolverVariable : constraintWidget3;
                                                }
                                                if (!(solverVariable7 == null || solverVariable8 == null)) {
                                                    int i15 = margin2;
                                                    solverVariable = solverVariable7;
                                                    Object obj6 = 3;
                                                    constraintWidget6 = constraintWidget;
                                                    solverVariable6 = solverVariable8;
                                                    i3 = i14;
                                                    constraintWidget12 = constraintWidget10;
                                                    obj4 = null;
                                                    constraintWidget13 = constraintWidget4;
                                                    linearSystem3 = linearSystem2;
                                                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, solverVariable, margin, 0.5f, solverVariable6, constraintAnchor2.mSolverVariable, i15, 4);
                                                    constraintWidget5 = constraintWidget13;
                                                    if (obj == null) {
                                                        constraintWidget5 = constraintWidget3;
                                                    }
                                                    constraintWidget = constraintWidget6;
                                                    obj3 = obj;
                                                    linearSystem2 = linearSystem3;
                                                    i2 = i3;
                                                    constraintWidget6 = constraintWidget5;
                                                    constraintWidget8 = constraintWidget12;
                                                    constraintWidget9 = constraintWidget11;
                                                    i6 = i12;
                                                    constraintWidgetContainer = this;
                                                }
                                            }
                                            constraintWidget13 = constraintWidget4;
                                            constraintWidget6 = constraintWidget;
                                            constraintWidget12 = constraintWidget10;
                                            linearSystem3 = linearSystem2;
                                            i3 = i14;
                                            obj4 = null;
                                            constraintWidget5 = constraintWidget13;
                                            if (obj == null) {
                                                constraintWidget5 = constraintWidget3;
                                            }
                                            constraintWidget = constraintWidget6;
                                            obj3 = obj;
                                            linearSystem2 = linearSystem3;
                                            i2 = i3;
                                            constraintWidget6 = constraintWidget5;
                                            constraintWidget8 = constraintWidget12;
                                            constraintWidget9 = constraintWidget11;
                                            i6 = i12;
                                            constraintWidgetContainer = this;
                                        } else if (constraintWidget6.mLeft.mTarget == null) {
                                            linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, constraintWidget6.getDrawX());
                                        } else {
                                            linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, constraintWidget.mLeft.mTarget.mSolverVariable, constraintWidget6.mLeft.getMargin(), i13);
                                        }
                                    } else if (constraintWidget6.mRight.mTarget == null) {
                                        linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, constraintWidget6.getDrawRight());
                                    } else {
                                        linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, constraintWidget10.mRight.mTarget.mSolverVariable, -constraintWidget6.mRight.getMargin(), i13);
                                    }
                                }
                                constraintWidget11 = constraintWidget6;
                                i12 = i6;
                                constraintWidget6 = constraintWidget;
                                i3 = i2;
                                constraintWidget12 = constraintWidget10;
                                linearSystem3 = linearSystem2;
                                obj4 = null;
                                if (obj == null) {
                                    constraintWidget5 = constraintWidget3;
                                }
                                constraintWidget = constraintWidget6;
                                obj3 = obj;
                                linearSystem2 = linearSystem3;
                                i2 = i3;
                                constraintWidget6 = constraintWidget5;
                                constraintWidget8 = constraintWidget12;
                                constraintWidget9 = constraintWidget11;
                                i6 = i12;
                                constraintWidgetContainer = this;
                            }
                            constraintWidget6 = constraintWidget;
                            i3 = i2;
                            linearSystem3 = linearSystem2;
                            i4 = 0;
                            if (i7 != 0) {
                                ConstraintAnchor constraintAnchor5 = constraintWidget2.mLeft;
                                ConstraintAnchor constraintAnchor6 = constraintWidget8.mRight;
                                margin = constraintAnchor5.getMargin();
                                i = constraintAnchor6.getMargin();
                                solverVariable = constraintWidget6.mLeft.mTarget != null ? constraintWidget6.mLeft.mTarget.mSolverVariable : constraintWidget3;
                                solverVariable6 = constraintWidget8.mRight.mTarget != null ? constraintWidget8.mRight.mTarget.mSolverVariable : constraintWidget3;
                                if (!(solverVariable == null || solverVariable6 == null)) {
                                    linearSystem3.addLowerThan(constraintAnchor6.mSolverVariable, solverVariable6, -i, 1);
                                    linearSystem3.addCentering(constraintAnchor5.mSolverVariable, solverVariable, margin, constraintWidget6.mHorizontalBiasPercent, solverVariable6, constraintAnchor6.mSolverVariable, i, 4);
                                }
                            }
                            i2 = i3 + 1;
                            linearSystem2 = linearSystem3;
                            i = i4;
                            constraintWidgetContainer = this;
                        }
                    } else {
                        Optimizer.applyDirectResolutionHorizontalChain(constraintWidgetContainer, linearSystem2, countMatchConstraintsChainedWidgets, constraintWidget);
                    }
                } else {
                    countMatchConstraintsChainedWidgets = constraintWidget.getDrawX();
                    while (constraintWidget2 != null) {
                        linearSystem2.addEquality(constraintWidget2.mLeft.mSolverVariable, countMatchConstraintsChainedWidgets);
                        countMatchConstraintsChainedWidgets += (constraintWidget2.mLeft.getMargin() + constraintWidget2.getWidth()) + constraintWidget2.mRight.getMargin();
                        constraintWidget2 = constraintWidget2.mHorizontalNextWidget;
                    }
                }
            }
            i3 = i2;
            i4 = i;
            linearSystem3 = linearSystem2;
            i2 = i3 + 1;
            linearSystem2 = linearSystem3;
            i = i4;
            constraintWidgetContainer = this;
        }
    }

    private void applyVerticalChain(LinearSystem linearSystem) {
        ConstraintWidgetContainer constraintWidgetContainer = this;
        LinearSystem linearSystem2 = linearSystem;
        int i = 0;
        int i2 = i;
        while (i2 < constraintWidgetContainer.mVerticalChainsSize) {
            int i3;
            LinearSystem linearSystem3;
            int i4;
            ConstraintWidget constraintWidget = constraintWidgetContainer.mVerticalChainsArray[i2];
            int countMatchConstraintsChainedWidgets = constraintWidgetContainer.countMatchConstraintsChainedWidgets(linearSystem2, constraintWidgetContainer.mChainEnds, constraintWidgetContainer.mVerticalChainsArray[i2], 1, constraintWidgetContainer.flags);
            int i5 = 2;
            ConstraintWidget constraintWidget2 = constraintWidgetContainer.mChainEnds[i5];
            if (constraintWidget2 != null) {
                i3 = 1;
                if (!constraintWidgetContainer.flags[i3]) {
                    int i6 = constraintWidget.mVerticalChainStyle == 0 ? i3 : i;
                    int i7 = constraintWidget.mVerticalChainStyle == i5 ? i3 : i;
                    int i8 = constraintWidgetContainer.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT ? i3 : i;
                    if ((constraintWidgetContainer.mOptimizationLevel != i5 && constraintWidgetContainer.mOptimizationLevel != 8) || !constraintWidgetContainer.flags[i] || !constraintWidget.mVerticalChainFixedPosition || i7 != 0 || i8 != 0 || constraintWidget.mVerticalChainStyle != 0) {
                        int i9 = 3;
                        ConstraintWidget constraintWidget3 = null;
                        ConstraintWidget constraintWidget4;
                        ConstraintWidget constraintWidget5;
                        SolverVariable solverVariable;
                        SolverVariable solverVariable2;
                        Object obj;
                        ConstraintWidget constraintWidget6;
                        if (countMatchConstraintsChainedWidgets != 0 && i7 == 0) {
                            float f = 0.0f;
                            constraintWidget4 = constraintWidget3;
                            while (constraintWidget2 != null) {
                                if (constraintWidget2.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                                    i8 = constraintWidget2.mTop.getMargin();
                                    if (constraintWidget4 != null) {
                                        i8 += constraintWidget4.mBottom.getMargin();
                                    }
                                    linearSystem2.addGreaterThan(constraintWidget2.mTop.mSolverVariable, constraintWidget2.mTop.mTarget.mSolverVariable, i8, constraintWidget2.mTop.mTarget.mOwner.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT ? i5 : i9);
                                    i6 = constraintWidget2.mBottom.getMargin();
                                    if (constraintWidget2.mBottom.mTarget.mOwner.mTop.mTarget != null && constraintWidget2.mBottom.mTarget.mOwner.mTop.mTarget.mOwner == constraintWidget2) {
                                        i6 += constraintWidget2.mBottom.mTarget.mOwner.mTop.getMargin();
                                    }
                                    linearSystem2.addLowerThan(constraintWidget2.mBottom.mSolverVariable, constraintWidget2.mBottom.mTarget.mSolverVariable, -i6, constraintWidget2.mBottom.mTarget.mOwner.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT ? i5 : i9);
                                } else {
                                    f += constraintWidget2.mVerticalWeight;
                                    if (constraintWidget2.mBottom.mTarget != null) {
                                        i6 = constraintWidget2.mBottom.getMargin();
                                        if (constraintWidget2 != constraintWidgetContainer.mChainEnds[i9]) {
                                            i6 += constraintWidget2.mBottom.mTarget.mOwner.mTop.getMargin();
                                        }
                                    } else {
                                        i6 = i;
                                    }
                                    linearSystem2.addGreaterThan(constraintWidget2.mBottom.mSolverVariable, constraintWidget2.mTop.mSolverVariable, i, i3);
                                    linearSystem2.addLowerThan(constraintWidget2.mBottom.mSolverVariable, constraintWidget2.mBottom.mTarget.mSolverVariable, -i6, i3);
                                }
                                constraintWidget4 = constraintWidget2;
                                constraintWidget2 = constraintWidget2.mVerticalNextWidget;
                            }
                            int i10;
                            if (countMatchConstraintsChainedWidgets != i3) {
                                i10 = i;
                                while (true) {
                                    i6 = countMatchConstraintsChainedWidgets - 1;
                                    if (i10 >= i6) {
                                        break;
                                    }
                                    ConstraintWidget constraintWidget7 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i10];
                                    i10++;
                                    constraintWidget5 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i10];
                                    solverVariable = constraintWidget7.mTop.mSolverVariable;
                                    solverVariable2 = constraintWidget7.mBottom.mSolverVariable;
                                    SolverVariable solverVariable3 = constraintWidget5.mTop.mSolverVariable;
                                    SolverVariable solverVariable4 = constraintWidget5.mBottom.mSolverVariable;
                                    if (constraintWidget5 == constraintWidgetContainer.mChainEnds[i9]) {
                                        solverVariable4 = constraintWidgetContainer.mChainEnds[1].mBottom.mSolverVariable;
                                    }
                                    i3 = constraintWidget7.mTop.getMargin();
                                    if (!(constraintWidget7.mTop.mTarget == null || constraintWidget7.mTop.mTarget.mOwner.mBottom.mTarget == null || constraintWidget7.mTop.mTarget.mOwner.mBottom.mTarget.mOwner != constraintWidget7)) {
                                        i3 += constraintWidget7.mTop.mTarget.mOwner.mBottom.getMargin();
                                    }
                                    int i11 = countMatchConstraintsChainedWidgets;
                                    linearSystem2.addGreaterThan(solverVariable, constraintWidget7.mTop.mTarget.mSolverVariable, i3, 2);
                                    countMatchConstraintsChainedWidgets = constraintWidget7.mBottom.getMargin();
                                    if (!(constraintWidget7.mBottom.mTarget == null || constraintWidget7.mVerticalNextWidget == null)) {
                                        countMatchConstraintsChainedWidgets += constraintWidget7.mVerticalNextWidget.mTop.mTarget != null ? constraintWidget7.mVerticalNextWidget.mTop.getMargin() : 0;
                                    }
                                    linearSystem2.addLowerThan(solverVariable2, constraintWidget7.mBottom.mTarget.mSolverVariable, -countMatchConstraintsChainedWidgets, 2);
                                    if (i10 == i6) {
                                        countMatchConstraintsChainedWidgets = constraintWidget5.mTop.getMargin();
                                        if (!(constraintWidget5.mTop.mTarget == null || constraintWidget5.mTop.mTarget.mOwner.mBottom.mTarget == null || constraintWidget5.mTop.mTarget.mOwner.mBottom.mTarget.mOwner != constraintWidget5)) {
                                            countMatchConstraintsChainedWidgets += constraintWidget5.mTop.mTarget.mOwner.mBottom.getMargin();
                                        }
                                        linearSystem2.addGreaterThan(solverVariable3, constraintWidget5.mTop.mTarget.mSolverVariable, countMatchConstraintsChainedWidgets, 2);
                                        ConstraintAnchor constraintAnchor = constraintWidget5.mBottom;
                                        if (constraintWidget5 == constraintWidgetContainer.mChainEnds[3]) {
                                            constraintAnchor = constraintWidgetContainer.mChainEnds[1].mBottom;
                                        }
                                        i6 = constraintAnchor.getMargin();
                                        if (!(constraintAnchor.mTarget == null || constraintAnchor.mTarget.mOwner.mTop.mTarget == null || constraintAnchor.mTarget.mOwner.mTop.mTarget.mOwner != constraintWidget5)) {
                                            i6 += constraintAnchor.mTarget.mOwner.mTop.getMargin();
                                        }
                                        i3 = 2;
                                        linearSystem2.addLowerThan(solverVariable4, constraintAnchor.mTarget.mSolverVariable, -i6, i3);
                                    } else {
                                        i3 = 2;
                                    }
                                    if (constraintWidget.mMatchConstraintMaxHeight > 0) {
                                        linearSystem2.addLowerThan(solverVariable2, solverVariable, constraintWidget.mMatchConstraintMaxHeight, i3);
                                    }
                                    ArrayRow createRow = linearSystem.createRow();
                                    createRow.createRowEqualDimension(constraintWidget7.mVerticalWeight, f, constraintWidget5.mVerticalWeight, solverVariable, constraintWidget7.mTop.getMargin(), solverVariable2, constraintWidget7.mBottom.getMargin(), solverVariable3, constraintWidget5.mTop.getMargin(), solverVariable4, constraintWidget5.mBottom.getMargin());
                                    linearSystem2.addConstraint(createRow);
                                    countMatchConstraintsChainedWidgets = i11;
                                    obj = 2;
                                    Object obj2 = 1;
                                    i9 = 3;
                                    i = 0;
                                }
                            } else {
                                constraintWidget6 = constraintWidgetContainer.mMatchConstraintsChainedWidgets[i];
                                i10 = constraintWidget6.mTop.getMargin();
                                if (constraintWidget6.mTop.mTarget != null) {
                                    i10 += constraintWidget6.mTop.mTarget.getMargin();
                                }
                                i6 = constraintWidget6.mBottom.getMargin();
                                if (constraintWidget6.mBottom.mTarget != null) {
                                    i6 += constraintWidget6.mBottom.mTarget.getMargin();
                                }
                                SolverVariable solverVariable5 = constraintWidget.mBottom.mTarget.mSolverVariable;
                                if (constraintWidget6 == constraintWidgetContainer.mChainEnds[i9]) {
                                    solverVariable5 = constraintWidgetContainer.mChainEnds[i3].mBottom.mTarget.mSolverVariable;
                                }
                                if (constraintWidget6.mMatchConstraintDefaultHeight == i3) {
                                    linearSystem2.addGreaterThan(constraintWidget.mTop.mSolverVariable, constraintWidget.mTop.mTarget.mSolverVariable, i10, i3);
                                    linearSystem2.addLowerThan(constraintWidget.mBottom.mSolverVariable, solverVariable5, -i6, i3);
                                    linearSystem2.addEquality(constraintWidget.mBottom.mSolverVariable, constraintWidget.mTop.mSolverVariable, constraintWidget.getHeight(), i5);
                                } else {
                                    linearSystem2.addEquality(constraintWidget6.mTop.mSolverVariable, constraintWidget6.mTop.mTarget.mSolverVariable, i10, i3);
                                    linearSystem2.addEquality(constraintWidget6.mBottom.mSolverVariable, solverVariable5, -i6, i3);
                                }
                            }
                        } else {
                            int margin;
                            SolverVariable solverVariable6;
                            constraintWidget6 = constraintWidget2;
                            ConstraintWidget constraintWidget8 = constraintWidget3;
                            ConstraintWidget constraintWidget9 = constraintWidget8;
                            Object obj3 = null;
                            while (constraintWidget6 != null) {
                                ConstraintWidget constraintWidget10;
                                ConstraintWidget constraintWidget11;
                                int i12;
                                ConstraintWidget constraintWidget12;
                                Object obj4;
                                constraintWidget5 = constraintWidget6.mVerticalNextWidget;
                                if (constraintWidget5 == null) {
                                    constraintWidget10 = constraintWidgetContainer.mChainEnds[1];
                                    obj = 1;
                                } else {
                                    constraintWidget10 = constraintWidget8;
                                    obj = obj3;
                                }
                                ConstraintAnchor constraintAnchor2;
                                int margin2;
                                Object obj5;
                                if (i7 != 0) {
                                    SolverVariable solverVariable7;
                                    constraintAnchor2 = constraintWidget6.mTop;
                                    margin2 = constraintAnchor2.getMargin();
                                    if (constraintWidget9 != null) {
                                        margin2 += constraintWidget9.mBottom.getMargin();
                                    }
                                    i3 = constraintWidget2 != constraintWidget6 ? 3 : 1;
                                    if (constraintAnchor2.mTarget != null) {
                                        solverVariable2 = constraintAnchor2.mSolverVariable;
                                        solverVariable7 = constraintAnchor2.mTarget.mSolverVariable;
                                    } else if (constraintWidget6.mBaseline.mTarget != null) {
                                        solverVariable2 = constraintWidget6.mBaseline.mSolverVariable;
                                        solverVariable7 = constraintWidget6.mBaseline.mTarget.mSolverVariable;
                                        margin2 -= constraintAnchor2.getMargin();
                                    } else {
                                        solverVariable2 = constraintWidget3;
                                        solverVariable7 = solverVariable2;
                                    }
                                    if (!(solverVariable2 == null || solverVariable7 == null)) {
                                        linearSystem2.addGreaterThan(solverVariable2, solverVariable7, margin2, i3);
                                    }
                                    if (constraintWidget6.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                                        ConstraintAnchor constraintAnchor3 = constraintWidget6.mBottom;
                                        if (constraintWidget6.mMatchConstraintDefaultHeight == 1) {
                                            linearSystem2.addEquality(constraintAnchor3.mSolverVariable, constraintAnchor2.mSolverVariable, Math.max(constraintWidget6.mMatchConstraintMinHeight, constraintWidget6.getHeight()), 3);
                                        } else {
                                            i9 = 3;
                                            linearSystem2.addGreaterThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, constraintAnchor2.mMargin, i9);
                                            linearSystem2.addLowerThan(constraintAnchor3.mSolverVariable, constraintAnchor2.mSolverVariable, constraintWidget6.mMatchConstraintMinHeight, i9);
                                        }
                                    } else {
                                        obj5 = 3;
                                    }
                                } else {
                                    obj5 = 3;
                                    int i13 = 5;
                                    if (i6 != 0 || obj == null || constraintWidget9 == null) {
                                        if (i6 != 0 || obj != null || constraintWidget9 != null) {
                                            ConstraintWidget constraintWidget13;
                                            ConstraintAnchor constraintAnchor4 = constraintWidget6.mTop;
                                            constraintAnchor2 = constraintWidget6.mBottom;
                                            margin = constraintAnchor4.getMargin();
                                            margin2 = constraintAnchor2.getMargin();
                                            constraintWidget11 = constraintWidget6;
                                            i12 = i6;
                                            i6 = 1;
                                            linearSystem2.addGreaterThan(constraintAnchor4.mSolverVariable, constraintAnchor4.mTarget.mSolverVariable, margin, i6);
                                            int i14 = i2;
                                            linearSystem2.addLowerThan(constraintAnchor2.mSolverVariable, constraintAnchor2.mTarget.mSolverVariable, -margin2, i6);
                                            SolverVariable solverVariable8 = constraintAnchor4.mTarget != null ? constraintAnchor4.mTarget.mSolverVariable : constraintWidget3;
                                            if (constraintWidget9 == null) {
                                                solverVariable8 = constraintWidget.mTop.mTarget != null ? constraintWidget.mTop.mTarget.mSolverVariable : constraintWidget3;
                                            }
                                            if (constraintWidget5 == null) {
                                                constraintWidget5 = constraintWidget10.mBottom.mTarget != null ? constraintWidget10.mBottom.mTarget.mOwner : constraintWidget3;
                                            }
                                            constraintWidget4 = constraintWidget5;
                                            if (constraintWidget4 != null) {
                                                SolverVariable solverVariable9 = constraintWidget4.mTop.mSolverVariable;
                                                if (obj != null) {
                                                    solverVariable9 = constraintWidget10.mBottom.mTarget != null ? constraintWidget10.mBottom.mTarget.mSolverVariable : constraintWidget3;
                                                }
                                                if (!(solverVariable8 == null || solverVariable9 == null)) {
                                                    int i15 = margin2;
                                                    solverVariable = solverVariable8;
                                                    Object obj6 = 3;
                                                    constraintWidget6 = constraintWidget;
                                                    solverVariable6 = solverVariable9;
                                                    i3 = i14;
                                                    constraintWidget12 = constraintWidget10;
                                                    obj4 = null;
                                                    constraintWidget13 = constraintWidget4;
                                                    linearSystem3 = linearSystem2;
                                                    linearSystem2.addCentering(constraintAnchor4.mSolverVariable, solverVariable, margin, 0.5f, solverVariable6, constraintAnchor2.mSolverVariable, i15, 4);
                                                    constraintWidget5 = constraintWidget13;
                                                    if (obj == null) {
                                                        constraintWidget5 = constraintWidget3;
                                                    }
                                                    constraintWidget = constraintWidget6;
                                                    obj3 = obj;
                                                    linearSystem2 = linearSystem3;
                                                    i2 = i3;
                                                    constraintWidget6 = constraintWidget5;
                                                    constraintWidget8 = constraintWidget12;
                                                    constraintWidget9 = constraintWidget11;
                                                    i6 = i12;
                                                    constraintWidgetContainer = this;
                                                }
                                            }
                                            constraintWidget13 = constraintWidget4;
                                            constraintWidget6 = constraintWidget;
                                            constraintWidget12 = constraintWidget10;
                                            linearSystem3 = linearSystem2;
                                            i3 = i14;
                                            obj4 = null;
                                            constraintWidget5 = constraintWidget13;
                                            if (obj == null) {
                                                constraintWidget5 = constraintWidget3;
                                            }
                                            constraintWidget = constraintWidget6;
                                            obj3 = obj;
                                            linearSystem2 = linearSystem3;
                                            i2 = i3;
                                            constraintWidget6 = constraintWidget5;
                                            constraintWidget8 = constraintWidget12;
                                            constraintWidget9 = constraintWidget11;
                                            i6 = i12;
                                            constraintWidgetContainer = this;
                                        } else if (constraintWidget6.mTop.mTarget == null) {
                                            linearSystem2.addEquality(constraintWidget6.mTop.mSolverVariable, constraintWidget6.getDrawY());
                                        } else {
                                            linearSystem2.addEquality(constraintWidget6.mTop.mSolverVariable, constraintWidget.mTop.mTarget.mSolverVariable, constraintWidget6.mTop.getMargin(), i13);
                                        }
                                    } else if (constraintWidget6.mBottom.mTarget == null) {
                                        linearSystem2.addEquality(constraintWidget6.mBottom.mSolverVariable, constraintWidget6.getDrawBottom());
                                    } else {
                                        linearSystem2.addEquality(constraintWidget6.mBottom.mSolverVariable, constraintWidget10.mBottom.mTarget.mSolverVariable, -constraintWidget6.mBottom.getMargin(), i13);
                                    }
                                }
                                constraintWidget11 = constraintWidget6;
                                i12 = i6;
                                constraintWidget6 = constraintWidget;
                                i3 = i2;
                                constraintWidget12 = constraintWidget10;
                                linearSystem3 = linearSystem2;
                                obj4 = null;
                                if (obj == null) {
                                    constraintWidget5 = constraintWidget3;
                                }
                                constraintWidget = constraintWidget6;
                                obj3 = obj;
                                linearSystem2 = linearSystem3;
                                i2 = i3;
                                constraintWidget6 = constraintWidget5;
                                constraintWidget8 = constraintWidget12;
                                constraintWidget9 = constraintWidget11;
                                i6 = i12;
                                constraintWidgetContainer = this;
                            }
                            constraintWidget6 = constraintWidget;
                            i3 = i2;
                            linearSystem3 = linearSystem2;
                            i4 = 0;
                            if (i7 != 0) {
                                ConstraintAnchor constraintAnchor5 = constraintWidget2.mTop;
                                ConstraintAnchor constraintAnchor6 = constraintWidget8.mBottom;
                                margin = constraintAnchor5.getMargin();
                                i = constraintAnchor6.getMargin();
                                solverVariable = constraintWidget6.mTop.mTarget != null ? constraintWidget6.mTop.mTarget.mSolverVariable : constraintWidget3;
                                solverVariable6 = constraintWidget8.mBottom.mTarget != null ? constraintWidget8.mBottom.mTarget.mSolverVariable : constraintWidget3;
                                if (!(solverVariable == null || solverVariable6 == null)) {
                                    linearSystem3.addLowerThan(constraintAnchor6.mSolverVariable, solverVariable6, -i, 1);
                                    linearSystem3.addCentering(constraintAnchor5.mSolverVariable, solverVariable, margin, constraintWidget6.mVerticalBiasPercent, solverVariable6, constraintAnchor6.mSolverVariable, i, 4);
                                }
                            }
                            i2 = i3 + 1;
                            linearSystem2 = linearSystem3;
                            i = i4;
                            constraintWidgetContainer = this;
                        }
                    } else {
                        Optimizer.applyDirectResolutionVerticalChain(constraintWidgetContainer, linearSystem2, countMatchConstraintsChainedWidgets, constraintWidget);
                    }
                } else {
                    countMatchConstraintsChainedWidgets = constraintWidget.getDrawY();
                    while (constraintWidget2 != null) {
                        linearSystem2.addEquality(constraintWidget2.mTop.mSolverVariable, countMatchConstraintsChainedWidgets);
                        countMatchConstraintsChainedWidgets += (constraintWidget2.mTop.getMargin() + constraintWidget2.getHeight()) + constraintWidget2.mBottom.getMargin();
                        constraintWidget2 = constraintWidget2.mVerticalNextWidget;
                    }
                }
            }
            i3 = i2;
            i4 = i;
            linearSystem3 = linearSystem2;
            i2 = i3 + 1;
            linearSystem2 = linearSystem3;
            i = i4;
            constraintWidgetContainer = this;
        }
    }

    public void updateChildrenFromSolver(LinearSystem linearSystem, int i, boolean[] zArr) {
        int i2 = 0;
        int i3 = 2;
        zArr[i3] = i2;
        updateFromSolver(linearSystem, i);
        int size = this.mChildren.size();
        while (i2 < size) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            constraintWidget.updateFromSolver(linearSystem, i);
            boolean z = true;
            if (constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                zArr[i3] = z;
            }
            if (constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                zArr[i3] = z;
            }
            i2++;
        }
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.mPaddingLeft = i;
        this.mPaddingTop = i2;
        this.mPaddingRight = i3;
        this.mPaddingBottom = i4;
    }

    public void layout() {
        boolean z;
        int i;
        Exception e;
        int i2 = this.mX;
        int i3 = this.mY;
        boolean z2 = false;
        int max = Math.max(z2, getWidth());
        int max2 = Math.max(z2, getHeight());
        this.mWidthMeasuredTooSmall = z2;
        this.mHeightMeasuredTooSmall = z2;
        if (this.mParent != null) {
            if (r1.mSnapshot == null) {
                r1.mSnapshot = new Snapshot(r1);
            }
            r1.mSnapshot.updateFrom(r1);
            setX(r1.mPaddingLeft);
            setY(r1.mPaddingTop);
            resetAnchors();
            resetSolverVariables(r1.mSystem.getCache());
        } else {
            r1.mX = z2;
            r1.mY = z2;
        }
        DimensionBehaviour dimensionBehaviour = r1.mVerticalDimensionBehaviour;
        DimensionBehaviour dimensionBehaviour2 = r1.mHorizontalDimensionBehaviour;
        int i4 = 2;
        boolean z3 = true;
        if (r1.mOptimizationLevel == i4 && (r1.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT || r1.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT)) {
            findWrapSize(r1.mChildren, r1.flags);
            z = r1.flags[z2];
            if (max > 0 && max2 > 0 && (r1.mWrapWidth > max || r1.mWrapHeight > max2)) {
                z = z2;
            }
            if (z) {
                if (r1.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    r1.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
                    if (max <= 0 || max >= r1.mWrapWidth) {
                        setWidth(Math.max(r1.mMinWidth, r1.mWrapWidth));
                    } else {
                        r1.mWidthMeasuredTooSmall = z3;
                        setWidth(max);
                    }
                }
                if (r1.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
                    r1.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
                    if (max2 <= 0 || max2 >= r1.mWrapHeight) {
                        setHeight(Math.max(r1.mMinHeight, r1.mWrapHeight));
                    } else {
                        r1.mHeightMeasuredTooSmall = z3;
                        setHeight(max2);
                    }
                }
            }
        } else {
            z = z2;
        }
        resetChains();
        int size = r1.mChildren.size();
        for (i = z2; i < size; i++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) r1.mChildren.get(i);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer) constraintWidget).layout();
            }
        }
        i = z2;
        boolean z4 = z;
        z = z3;
        while (z) {
            int i5;
            ConstraintWidget constraintWidget2;
            int i6;
            Object obj;
            i += z3;
            int i7 = Integer.MAX_VALUE;
            try {
                r1.mSystem.reset();
                z2 = addChildrenToSolver(r1.mSystem, i7);
                if (z2) {
                    try {
                        r1.mSystem.minimize();
                    } catch (Exception e2) {
                        e = e2;
                        z = z2;
                    }
                }
            } catch (Exception e3) {
                e = e3;
                e.printStackTrace();
                z2 = z;
                if (!z2) {
                    updateFromSolver(r1.mSystem, i7);
                    i5 = 0;
                    while (i5 < size) {
                        constraintWidget2 = (ConstraintWidget) r1.mChildren.get(i5);
                        if (constraintWidget2.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getWidth() < constraintWidget2.getWrapWidth()) {
                            i4 = 2;
                            r1.flags[i4] = z3;
                            break;
                        }
                        Object obj2 = 2;
                        if (constraintWidget2.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getHeight() < constraintWidget2.getWrapHeight()) {
                            i6 = 2;
                            r1.flags[i6] = z3;
                            break;
                        }
                        i5++;
                        i4 = 2;
                    }
                } else {
                    updateChildrenFromSolver(r1.mSystem, i7, r1.flags);
                }
                i6 = i4;
                if (i < 8) {
                }
                z3 = z4;
                z2 = false;
                i6 = Math.max(r1.mMinWidth, getWidth());
                if (i6 > getWidth()) {
                    setWidth(i6);
                    r1.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
                    z2 = true;
                    z3 = true;
                }
                i6 = Math.max(r1.mMinHeight, getHeight());
                if (i6 > getHeight()) {
                    setHeight(i6);
                    r1.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
                    z2 = true;
                    z3 = true;
                }
                if (!z3) {
                    if (r1.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && max > 0 && getWidth() > max) {
                        r1.mWidthMeasuredTooSmall = true;
                        r1.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
                        setWidth(max);
                        z2 = true;
                        z3 = true;
                    }
                    if (r1.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && max2 > 0 && getHeight() > max2) {
                        z = true;
                        r1.mHeightMeasuredTooSmall = z;
                        r1.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
                        setHeight(max2);
                        z2 = z;
                        z4 = z2;
                        z3 = z;
                        i4 = 2;
                        z = z2;
                        obj = null;
                    }
                }
                z = true;
                z4 = z3;
                z3 = z;
                i4 = 2;
                z = z2;
                obj = null;
            }
            if (!z2) {
                updateChildrenFromSolver(r1.mSystem, i7, r1.flags);
            } else {
                updateFromSolver(r1.mSystem, i7);
                i5 = 0;
                while (i5 < size) {
                    constraintWidget2 = (ConstraintWidget) r1.mChildren.get(i5);
                    if (constraintWidget2.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getWidth() < constraintWidget2.getWrapWidth()) {
                        i4 = 2;
                        r1.flags[i4] = z3;
                        break;
                    }
                    Object obj22 = 2;
                    if (constraintWidget2.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget2.getHeight() < constraintWidget2.getWrapHeight()) {
                        i6 = 2;
                        r1.flags[i6] = z3;
                        break;
                    }
                    i5++;
                    i4 = 2;
                }
            }
            i6 = i4;
            if (i < 8 || !r1.flags[i6]) {
                z3 = z4;
                z2 = false;
            } else {
                i5 = 0;
                i4 = 0;
                i7 = 0;
                while (i5 < size) {
                    constraintWidget2 = (ConstraintWidget) r1.mChildren.get(i5);
                    i4 = Math.max(i4, constraintWidget2.mX + constraintWidget2.getWidth());
                    i7 = Math.max(i7, constraintWidget2.mY + constraintWidget2.getHeight());
                    i5++;
                    Object obj3 = 2;
                    Object obj4 = 1;
                }
                i5 = Math.max(r1.mMinWidth, i4);
                i6 = Math.max(r1.mMinHeight, i7);
                if (dimensionBehaviour2 != DimensionBehaviour.WRAP_CONTENT || getWidth() >= i5) {
                    z3 = z4;
                    z2 = false;
                } else {
                    setWidth(i5);
                    r1.mHorizontalDimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
                    z2 = true;
                    z3 = true;
                }
                if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && getHeight() < i6) {
                    setHeight(i6);
                    r1.mVerticalDimensionBehaviour = DimensionBehaviour.WRAP_CONTENT;
                    z2 = true;
                    z3 = true;
                }
            }
            i6 = Math.max(r1.mMinWidth, getWidth());
            if (i6 > getWidth()) {
                setWidth(i6);
                r1.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
                z2 = true;
                z3 = true;
            }
            i6 = Math.max(r1.mMinHeight, getHeight());
            if (i6 > getHeight()) {
                setHeight(i6);
                r1.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
                z2 = true;
                z3 = true;
            }
            if (z3) {
                if (r1.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && max > 0 && getWidth() > max) {
                    r1.mWidthMeasuredTooSmall = true;
                    r1.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
                    setWidth(max);
                    z2 = true;
                    z3 = true;
                }
                if (r1.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT && max2 > 0 && getHeight() > max2) {
                    z = true;
                    r1.mHeightMeasuredTooSmall = z;
                    r1.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
                    setHeight(max2);
                    z2 = z;
                    z4 = z2;
                    z3 = z;
                    i4 = 2;
                    z = z2;
                    obj = null;
                }
            }
            z = true;
            z4 = z3;
            z3 = z;
            i4 = 2;
            z = z2;
            obj = null;
        }
        if (r1.mParent != null) {
            i2 = Math.max(r1.mMinWidth, getWidth());
            i3 = Math.max(r1.mMinHeight, getHeight());
            r1.mSnapshot.applyTo(r1);
            setWidth((i2 + r1.mPaddingLeft) + r1.mPaddingRight);
            setHeight((i3 + r1.mPaddingTop) + r1.mPaddingBottom);
        } else {
            r1.mX = i2;
            r1.mY = i3;
        }
        if (z4) {
            r1.mHorizontalDimensionBehaviour = dimensionBehaviour2;
            r1.mVerticalDimensionBehaviour = dimensionBehaviour;
        }
        resetSolverVariables(r1.mSystem.getCache());
        if (r1 == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    static int setGroup(ConstraintAnchor constraintAnchor, int i) {
        int i2 = constraintAnchor.mGroup;
        if (constraintAnchor.mOwner.getParent() == null) {
            return i;
        }
        if (i2 <= i) {
            return i2;
        }
        constraintAnchor.mGroup = i;
        ConstraintAnchor opposite = constraintAnchor.getOpposite();
        ConstraintAnchor constraintAnchor2 = constraintAnchor.mTarget;
        if (opposite != null) {
            i = setGroup(opposite, i);
        }
        if (constraintAnchor2 != null) {
            i = setGroup(constraintAnchor2, i);
        }
        if (opposite != null) {
            i = setGroup(opposite, i);
        }
        constraintAnchor.mGroup = i;
        return i;
    }

    public int layoutFindGroupsSimple() {
        int size = this.mChildren.size();
        int i = 0;
        for (int i2 = i; i2 < size; i2++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            constraintWidget.mLeft.mGroup = i;
            constraintWidget.mRight.mGroup = i;
            int i3 = 1;
            constraintWidget.mTop.mGroup = i3;
            constraintWidget.mBottom.mGroup = i3;
            constraintWidget.mBaseline.mGroup = i3;
        }
        return 2;
    }

    public void findHorizontalWrapRecursive(ConstraintWidget constraintWidget, boolean[] zArr) {
        float f = 0.0f;
        boolean z = false;
        if (constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.mDimensionRatio > f) {
            zArr[z] = z;
            return;
        }
        int optimizerWrapWidth = constraintWidget.getOptimizerWrapWidth();
        if (constraintWidget.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mDimensionRatio <= f) {
            int z2;
            int margin;
            boolean z3 = true;
            constraintWidget.mHorizontalWrapVisited = z3;
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == z3) {
                    int i = -1;
                    if (guideline.getRelativeBegin() != i) {
                        optimizerWrapWidth = z2;
                        z2 = guideline.getRelativeBegin();
                    } else {
                        optimizerWrapWidth = guideline.getRelativeEnd() != i ? guideline.getRelativeEnd() : z2;
                    }
                } else {
                    z2 = optimizerWrapWidth;
                }
            } else if (!constraintWidget.mRight.isConnected() && !constraintWidget.mLeft.isConnected()) {
                z2 = optimizerWrapWidth + constraintWidget.getX();
            } else if (constraintWidget.mRight.mTarget == null || constraintWidget.mLeft.mTarget == null || (constraintWidget.mRight.mTarget != constraintWidget.mLeft.mTarget && (constraintWidget.mRight.mTarget.mOwner != constraintWidget.mLeft.mTarget.mOwner || constraintWidget.mRight.mTarget.mOwner == constraintWidget.mParent))) {
                ConstraintWidget constraintWidget2;
                ConstraintWidget constraintWidget3 = null;
                if (constraintWidget.mRight.mTarget != null) {
                    constraintWidget2 = constraintWidget.mRight.mTarget.mOwner;
                    margin = constraintWidget.mRight.getMargin() + optimizerWrapWidth;
                    if (!(constraintWidget2.isRoot() || constraintWidget2.mHorizontalWrapVisited)) {
                        findHorizontalWrapRecursive(constraintWidget2, zArr);
                    }
                } else {
                    margin = optimizerWrapWidth;
                    constraintWidget2 = constraintWidget3;
                }
                if (constraintWidget.mLeft.mTarget != null) {
                    constraintWidget3 = constraintWidget.mLeft.mTarget.mOwner;
                    optimizerWrapWidth += constraintWidget.mLeft.getMargin();
                    if (!(constraintWidget3.isRoot() || constraintWidget3.mHorizontalWrapVisited)) {
                        findHorizontalWrapRecursive(constraintWidget3, zArr);
                    }
                }
                if (!(constraintWidget.mRight.mTarget == null || constraintWidget2.isRoot())) {
                    if (constraintWidget.mRight.mTarget.mType == Type.RIGHT) {
                        margin += constraintWidget2.mDistToRight - constraintWidget2.getOptimizerWrapWidth();
                    } else if (constraintWidget.mRight.mTarget.getType() == Type.LEFT) {
                        margin += constraintWidget2.mDistToRight;
                    }
                    boolean z4 = (constraintWidget2.mRightHasCentered || !(constraintWidget2.mLeft.mTarget == null || constraintWidget2.mRight.mTarget == null || constraintWidget2.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT)) ? z3 : z2;
                    constraintWidget.mRightHasCentered = z4;
                    if (constraintWidget.mRightHasCentered && (constraintWidget2.mLeft.mTarget == null || constraintWidget2.mLeft.mTarget.mOwner != constraintWidget)) {
                        margin += margin - constraintWidget2.mDistToRight;
                    }
                }
                if (!(constraintWidget.mLeft.mTarget == null || constraintWidget3.isRoot())) {
                    if (constraintWidget.mLeft.mTarget.getType() == Type.LEFT) {
                        optimizerWrapWidth += constraintWidget3.mDistToLeft - constraintWidget3.getOptimizerWrapWidth();
                    } else if (constraintWidget.mLeft.mTarget.getType() == Type.RIGHT) {
                        optimizerWrapWidth += constraintWidget3.mDistToLeft;
                    }
                    if (!constraintWidget3.mLeftHasCentered && (constraintWidget3.mLeft.mTarget == null || constraintWidget3.mRight.mTarget == null || constraintWidget3.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z3 = z2;
                    }
                    constraintWidget.mLeftHasCentered = z3;
                    if (constraintWidget.mLeftHasCentered && (constraintWidget3.mRight.mTarget == null || constraintWidget3.mRight.mTarget.mOwner != constraintWidget)) {
                        z2 = optimizerWrapWidth + (optimizerWrapWidth - constraintWidget3.mDistToLeft);
                        if (constraintWidget.getVisibility() == 8) {
                            z2 -= constraintWidget.mWidth;
                            margin -= constraintWidget.mWidth;
                        }
                        constraintWidget.mDistToLeft = z2;
                        constraintWidget.mDistToRight = margin;
                        return;
                    }
                }
                z2 = optimizerWrapWidth;
                if (constraintWidget.getVisibility() == 8) {
                    z2 -= constraintWidget.mWidth;
                    margin -= constraintWidget.mWidth;
                }
                constraintWidget.mDistToLeft = z2;
                constraintWidget.mDistToRight = margin;
                return;
            } else {
                zArr[z2] = z2;
                return;
            }
            margin = optimizerWrapWidth;
            if (constraintWidget.getVisibility() == 8) {
                z2 -= constraintWidget.mWidth;
                margin -= constraintWidget.mWidth;
            }
            constraintWidget.mDistToLeft = z2;
            constraintWidget.mDistToRight = margin;
            return;
        }
        zArr[z2] = z2;
    }

    public void findVerticalWrapRecursive(ConstraintWidget constraintWidget, boolean[] zArr) {
        boolean z = false;
        if (constraintWidget.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.mDimensionRatio <= 0.0f) {
            int margin;
            int optimizerWrapHeight = constraintWidget.getOptimizerWrapHeight();
            boolean z2 = true;
            constraintWidget.mVerticalWrapVisited = z2;
            int i = 8;
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    int i2 = -1;
                    if (guideline.getRelativeBegin() != i2) {
                        optimizerWrapHeight = z;
                        z = guideline.getRelativeBegin();
                    } else {
                        optimizerWrapHeight = guideline.getRelativeEnd() != i2 ? guideline.getRelativeEnd() : z;
                    }
                } else {
                    z = optimizerWrapHeight;
                }
            } else if (constraintWidget.mBaseline.mTarget == null && constraintWidget.mTop.mTarget == null && constraintWidget.mBottom.mTarget == null) {
                z = optimizerWrapHeight + constraintWidget.getY();
            } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mTop.mTarget != null && (constraintWidget.mBottom.mTarget == constraintWidget.mTop.mTarget || (constraintWidget.mBottom.mTarget.mOwner == constraintWidget.mTop.mTarget.mOwner && constraintWidget.mBottom.mTarget.mOwner != constraintWidget.mParent))) {
                zArr[z] = z;
                return;
            } else if (constraintWidget.mBaseline.isConnected()) {
                ConstraintWidget owner = constraintWidget.mBaseline.mTarget.getOwner();
                if (!owner.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(owner, zArr);
                }
                int max = Math.max((owner.mDistToTop - owner.mHeight) + optimizerWrapHeight, optimizerWrapHeight);
                optimizerWrapHeight = Math.max((owner.mDistToBottom - owner.mHeight) + optimizerWrapHeight, optimizerWrapHeight);
                if (constraintWidget.getVisibility() == i) {
                    max -= constraintWidget.mHeight;
                    optimizerWrapHeight -= constraintWidget.mHeight;
                }
                constraintWidget.mDistToTop = max;
                constraintWidget.mDistToBottom = optimizerWrapHeight;
                return;
            } else {
                ConstraintWidget owner2;
                ConstraintWidget constraintWidget2 = null;
                if (constraintWidget.mTop.isConnected()) {
                    owner2 = constraintWidget.mTop.mTarget.getOwner();
                    margin = constraintWidget.mTop.getMargin() + optimizerWrapHeight;
                    if (!(owner2.isRoot() || owner2.mVerticalWrapVisited)) {
                        findVerticalWrapRecursive(owner2, zArr);
                    }
                } else {
                    margin = optimizerWrapHeight;
                    owner2 = constraintWidget2;
                }
                if (constraintWidget.mBottom.isConnected()) {
                    constraintWidget2 = constraintWidget.mBottom.mTarget.getOwner();
                    optimizerWrapHeight += constraintWidget.mBottom.getMargin();
                    if (!(constraintWidget2.isRoot() || constraintWidget2.mVerticalWrapVisited)) {
                        findVerticalWrapRecursive(constraintWidget2, zArr);
                    }
                }
                if (!(constraintWidget.mTop.mTarget == null || owner2.isRoot())) {
                    if (constraintWidget.mTop.mTarget.getType() == Type.TOP) {
                        margin += owner2.mDistToTop - owner2.getOptimizerWrapHeight();
                    } else if (constraintWidget.mTop.mTarget.getType() == Type.BOTTOM) {
                        margin += owner2.mDistToTop;
                    }
                    boolean z3 = (owner2.mTopHasCentered || !(owner2.mTop.mTarget == null || owner2.mTop.mTarget.mOwner == constraintWidget || owner2.mBottom.mTarget == null || owner2.mBottom.mTarget.mOwner == constraintWidget || owner2.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT)) ? z2 : z;
                    constraintWidget.mTopHasCentered = z3;
                    if (constraintWidget.mTopHasCentered && (owner2.mBottom.mTarget == null || owner2.mBottom.mTarget.mOwner != constraintWidget)) {
                        margin += margin - owner2.mDistToTop;
                    }
                }
                if (!(constraintWidget.mBottom.mTarget == null || constraintWidget2.isRoot())) {
                    if (constraintWidget.mBottom.mTarget.getType() == Type.BOTTOM) {
                        optimizerWrapHeight += constraintWidget2.mDistToBottom - constraintWidget2.getOptimizerWrapHeight();
                    } else if (constraintWidget.mBottom.mTarget.getType() == Type.TOP) {
                        optimizerWrapHeight += constraintWidget2.mDistToBottom;
                    }
                    if (!constraintWidget2.mBottomHasCentered && (constraintWidget2.mTop.mTarget == null || constraintWidget2.mTop.mTarget.mOwner == constraintWidget || constraintWidget2.mBottom.mTarget == null || constraintWidget2.mBottom.mTarget.mOwner == constraintWidget || constraintWidget2.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z2 = z;
                    }
                    constraintWidget.mBottomHasCentered = z2;
                    if (constraintWidget.mBottomHasCentered && (constraintWidget2.mTop.mTarget == null || constraintWidget2.mTop.mTarget.mOwner != constraintWidget)) {
                        optimizerWrapHeight += optimizerWrapHeight - constraintWidget2.mDistToBottom;
                    }
                }
                if (constraintWidget.getVisibility() == i) {
                    margin -= constraintWidget.mHeight;
                    optimizerWrapHeight -= constraintWidget.mHeight;
                }
                constraintWidget.mDistToTop = margin;
                constraintWidget.mDistToBottom = optimizerWrapHeight;
                return;
            }
            margin = z;
            if (constraintWidget.getVisibility() == i) {
                margin -= constraintWidget.mHeight;
                optimizerWrapHeight -= constraintWidget.mHeight;
            }
            constraintWidget.mDistToTop = margin;
            constraintWidget.mDistToBottom = optimizerWrapHeight;
            return;
        }
        zArr[z] = z;
    }

    public void findWrapSize(ArrayList<ConstraintWidget> arrayList, boolean[] zArr) {
        ConstraintWidgetContainer constraintWidgetContainer = this;
        ArrayList<ConstraintWidget> arrayList2 = arrayList;
        boolean[] zArr2 = zArr;
        int size = arrayList.size();
        int i = 0;
        zArr2[i] = true;
        int i2 = i;
        int i3 = i2;
        int i4 = i3;
        int i5 = i4;
        int i6 = i5;
        int i7 = i6;
        int i8 = i7;
        while (i2 < size) {
            ConstraintWidget constraintWidget = (ConstraintWidget) arrayList2.get(i2);
            if (!constraintWidget.isRoot()) {
                if (!constraintWidget.mHorizontalWrapVisited) {
                    findHorizontalWrapRecursive(constraintWidget, zArr2);
                }
                if (!constraintWidget.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(constraintWidget, zArr2);
                }
                if (zArr2[i]) {
                    int height = (constraintWidget.mDistToTop + constraintWidget.mDistToBottom) - constraintWidget.getHeight();
                    i = constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT ? (constraintWidget.getWidth() + constraintWidget.mLeft.mMargin) + constraintWidget.mRight.mMargin : (constraintWidget.mDistToLeft + constraintWidget.mDistToRight) - constraintWidget.getWidth();
                    int height2 = constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT ? (constraintWidget.getHeight() + constraintWidget.mTop.mMargin) + constraintWidget.mBottom.mMargin : height;
                    if (constraintWidget.getVisibility() == 8) {
                        i = 0;
                        height2 = 0;
                    }
                    i3 = Math.max(i3, constraintWidget.mDistToLeft);
                    i4 = Math.max(i4, constraintWidget.mDistToRight);
                    i7 = Math.max(i7, constraintWidget.mDistToBottom);
                    i6 = Math.max(i6, constraintWidget.mDistToTop);
                    i = Math.max(i5, i);
                    i8 = Math.max(i8, height2);
                    i5 = i;
                } else {
                    return;
                }
            }
            i2++;
            i = 0;
        }
        constraintWidgetContainer.mWrapWidth = Math.max(constraintWidgetContainer.mMinWidth, Math.max(Math.max(i3, i4), i5));
        constraintWidgetContainer.mWrapHeight = Math.max(constraintWidgetContainer.mMinHeight, Math.max(Math.max(i6, i7), i8));
        for (int i9 = 0; i9 < size; i9++) {
            ConstraintWidget constraintWidget2 = (ConstraintWidget) arrayList2.get(i9);
            boolean z = false;
            constraintWidget2.mHorizontalWrapVisited = z;
            constraintWidget2.mVerticalWrapVisited = z;
            constraintWidget2.mLeftHasCentered = z;
            constraintWidget2.mRightHasCentered = z;
            constraintWidget2.mTopHasCentered = z;
            constraintWidget2.mBottomHasCentered = z;
        }
    }

    public int layoutFindGroups() {
        int i;
        ConstraintWidget constraintWidget;
        ConstraintAnchor constraintAnchor;
        int i2;
        Type[] typeArr = new Type[5];
        int i3 = 0;
        typeArr[i3] = Type.LEFT;
        int i4 = 1;
        typeArr[i4] = Type.RIGHT;
        typeArr[2] = Type.TOP;
        typeArr[3] = Type.BASELINE;
        typeArr[4] = Type.BOTTOM;
        int size = this.mChildren.size();
        int i5 = i3;
        int i6 = i4;
        while (true) {
            i = Integer.MAX_VALUE;
            if (i5 >= size) {
                break;
            }
            constraintWidget = (ConstraintWidget) this.mChildren.get(i5);
            ConstraintAnchor constraintAnchor2 = constraintWidget.mLeft;
            if (constraintAnchor2.mTarget == null) {
                constraintAnchor2.mGroup = i;
            } else if (setGroup(constraintAnchor2, i6) == i6) {
                i6++;
            }
            constraintAnchor2 = constraintWidget.mTop;
            if (constraintAnchor2.mTarget == null) {
                constraintAnchor2.mGroup = i;
            } else if (setGroup(constraintAnchor2, i6) == i6) {
                i6++;
            }
            constraintAnchor2 = constraintWidget.mRight;
            if (constraintAnchor2.mTarget == null) {
                constraintAnchor2.mGroup = i;
            } else if (setGroup(constraintAnchor2, i6) == i6) {
                i6++;
            }
            constraintAnchor2 = constraintWidget.mBottom;
            if (constraintAnchor2.mTarget == null) {
                constraintAnchor2.mGroup = i;
            } else if (setGroup(constraintAnchor2, i6) == i6) {
                i6++;
            }
            constraintAnchor = constraintWidget.mBaseline;
            if (constraintAnchor.mTarget == null) {
                constraintAnchor.mGroup = i;
            } else if (setGroup(constraintAnchor, i6) == i6) {
                i6++;
            }
            i5++;
        }
        i5 = i4;
        while (i5 != 0) {
            i5 = i3;
            i6 = i5;
            while (i5 < size) {
                constraintWidget = (ConstraintWidget) this.mChildren.get(i5);
                i2 = i6;
                for (i6 = i3; i6 < typeArr.length; i6++) {
                    ConstraintAnchor constraintAnchor3 = null;
                    switch (typeArr[i6]) {
                        case LEFT:
                            constraintAnchor3 = constraintWidget.mLeft;
                            break;
                        case TOP:
                            constraintAnchor3 = constraintWidget.mTop;
                            break;
                        case RIGHT:
                            constraintAnchor3 = constraintWidget.mRight;
                            break;
                        case BOTTOM:
                            constraintAnchor3 = constraintWidget.mBottom;
                            break;
                        case BASELINE:
                            constraintAnchor3 = constraintWidget.mBaseline;
                            break;
                    }
                    ConstraintAnchor constraintAnchor4 = constraintAnchor3.mTarget;
                    if (constraintAnchor4 != null) {
                        if (!(constraintAnchor4.mOwner.getParent() == null || constraintAnchor4.mGroup == constraintAnchor3.mGroup)) {
                            i2 = constraintAnchor3.mGroup > constraintAnchor4.mGroup ? constraintAnchor4.mGroup : constraintAnchor3.mGroup;
                            constraintAnchor3.mGroup = i2;
                            constraintAnchor4.mGroup = i2;
                            i2 = i4;
                        }
                        constraintAnchor4 = constraintAnchor4.getOpposite();
                        if (!(constraintAnchor4 == null || constraintAnchor4.mGroup == constraintAnchor3.mGroup)) {
                            i2 = constraintAnchor3.mGroup > constraintAnchor4.mGroup ? constraintAnchor4.mGroup : constraintAnchor3.mGroup;
                            constraintAnchor3.mGroup = i2;
                            constraintAnchor4.mGroup = i2;
                            i2 = i4;
                        }
                    }
                }
                i5++;
                i6 = i2;
            }
            i5 = i6;
        }
        i6 = -1;
        int[] iArr = new int[((this.mChildren.size() * typeArr.length) + i4)];
        Arrays.fill(iArr, i6);
        i4 = i3;
        while (i3 < size) {
            int i7;
            ConstraintWidget constraintWidget2 = (ConstraintWidget) this.mChildren.get(i3);
            constraintAnchor = constraintWidget2.mLeft;
            if (constraintAnchor.mGroup != i) {
                i2 = constraintAnchor.mGroup;
                if (iArr[i2] == i6) {
                    i7 = i4 + 1;
                    iArr[i2] = i4;
                    i4 = i7;
                }
                constraintAnchor.mGroup = iArr[i2];
            }
            constraintAnchor = constraintWidget2.mTop;
            if (constraintAnchor.mGroup != i) {
                i2 = constraintAnchor.mGroup;
                if (iArr[i2] == i6) {
                    i7 = i4 + 1;
                    iArr[i2] = i4;
                    i4 = i7;
                }
                constraintAnchor.mGroup = iArr[i2];
            }
            constraintAnchor = constraintWidget2.mRight;
            if (constraintAnchor.mGroup != i) {
                i2 = constraintAnchor.mGroup;
                if (iArr[i2] == i6) {
                    i7 = i4 + 1;
                    iArr[i2] = i4;
                    i4 = i7;
                }
                constraintAnchor.mGroup = iArr[i2];
            }
            constraintAnchor = constraintWidget2.mBottom;
            if (constraintAnchor.mGroup != i) {
                i2 = constraintAnchor.mGroup;
                if (iArr[i2] == i6) {
                    i7 = i4 + 1;
                    iArr[i2] = i4;
                    i4 = i7;
                }
                constraintAnchor.mGroup = iArr[i2];
            }
            ConstraintAnchor constraintAnchor5 = constraintWidget2.mBaseline;
            if (constraintAnchor5.mGroup != i) {
                int i8 = constraintAnchor5.mGroup;
                if (iArr[i8] == i6) {
                    i2 = i4 + 1;
                    iArr[i8] = i4;
                    i4 = i2;
                }
                constraintAnchor5.mGroup = iArr[i8];
            }
            i3++;
        }
        return i4;
    }

    public void layoutWithGroup(int i) {
        int i2;
        int i3 = this.mX;
        int i4 = this.mY;
        int i5 = 0;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            this.mX = i5;
            this.mY = i5;
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = i5;
            this.mY = i5;
        }
        int size = this.mChildren.size();
        for (i2 = i5; i2 < size; i2++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i2);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer) constraintWidget).layout();
            }
        }
        this.mLeft.mGroup = i5;
        this.mRight.mGroup = i5;
        i2 = 1;
        this.mTop.mGroup = i2;
        this.mBottom.mGroup = i2;
        this.mSystem.reset();
        while (i5 < i) {
            try {
                addToSolver(this.mSystem, i5);
                this.mSystem.minimize();
                updateFromSolver(this.mSystem, i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateFromSolver(this.mSystem, -2);
            i5++;
        }
        if (this.mParent != null) {
            i = getWidth();
            i3 = getHeight();
            this.mSnapshot.applyTo(this);
            setWidth(i);
            setHeight(i3);
        } else {
            this.mX = i3;
            this.mY = i4;
        }
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = (ConstraintWidget) this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    private void resetChains() {
        int i = 0;
        this.mHorizontalChainsSize = i;
        this.mVerticalChainsSize = i;
    }

    void addChain(ConstraintWidget constraintWidget, int i) {
        if (i == 0) {
            while (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mRight.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mRight.mTarget == constraintWidget.mLeft && constraintWidget.mLeft.mTarget.mOwner != constraintWidget) {
                constraintWidget = constraintWidget.mLeft.mTarget.mOwner;
            }
            addHorizontalChain(constraintWidget);
        } else if (i == 1) {
            while (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mBottom.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mBottom.mTarget == constraintWidget.mTop && constraintWidget.mTop.mTarget.mOwner != constraintWidget) {
                constraintWidget = constraintWidget.mTop.mTarget.mOwner;
            }
            addVerticalChain(constraintWidget);
        }
    }

    private void addHorizontalChain(ConstraintWidget constraintWidget) {
        int i = 0;
        while (i < this.mHorizontalChainsSize) {
            if (this.mHorizontalChainsArray[i] != constraintWidget) {
                i++;
            } else {
                return;
            }
        }
        if (this.mHorizontalChainsSize + 1 >= this.mHorizontalChainsArray.length) {
            this.mHorizontalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mHorizontalChainsArray, this.mHorizontalChainsArray.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = constraintWidget;
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget constraintWidget) {
        int i = 0;
        while (i < this.mVerticalChainsSize) {
            if (this.mVerticalChainsArray[i] != constraintWidget) {
                i++;
            } else {
                return;
            }
        }
        if (this.mVerticalChainsSize + 1 >= this.mVerticalChainsArray.length) {
            this.mVerticalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mVerticalChainsArray, this.mVerticalChainsArray.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = constraintWidget;
        this.mVerticalChainsSize++;
    }

    private int countMatchConstraintsChainedWidgets(LinearSystem linearSystem, ConstraintWidget[] constraintWidgetArr, ConstraintWidget constraintWidget, int i, boolean[] zArr) {
        int i2;
        ConstraintWidget constraintWidget2 = this;
        LinearSystem linearSystem2 = linearSystem;
        ConstraintWidget constraintWidget3 = constraintWidget;
        boolean z = true;
        boolean z2 = false;
        zArr[z2] = z;
        zArr[z] = z2;
        ConstraintWidget constraintWidget4 = null;
        constraintWidgetArr[z2] = constraintWidget4;
        constraintWidgetArr[2] = constraintWidget4;
        constraintWidgetArr[z] = constraintWidget4;
        constraintWidgetArr[3] = constraintWidget4;
        float f = 0.0f;
        int i3 = 5;
        int i4 = 8;
        ConstraintWidget constraintWidget5;
        ConstraintWidget constraintWidget6;
        int i5;
        int i6;
        if (i == 0) {
            boolean z3 = (constraintWidget3.mLeft.mTarget == null || constraintWidget3.mLeft.mTarget.mOwner == constraintWidget2) ? z : z2;
            constraintWidget3.mHorizontalNextWidget = constraintWidget4;
            i2 = z2;
            ConstraintWidget constraintWidget7 = constraintWidget4;
            constraintWidget5 = constraintWidget.getVisibility() != i4 ? constraintWidget3 : constraintWidget4;
            ConstraintWidget constraintWidget8 = constraintWidget5;
            constraintWidget6 = constraintWidget3;
            while (constraintWidget6.mRight.mTarget != null) {
                constraintWidget6.mHorizontalNextWidget = constraintWidget4;
                if (constraintWidget6.getVisibility() != i4) {
                    if (constraintWidget8 == null) {
                        constraintWidget8 = constraintWidget6;
                    }
                    if (!(constraintWidget5 == null || constraintWidget5 == constraintWidget6)) {
                        constraintWidget5.mHorizontalNextWidget = constraintWidget6;
                    }
                    constraintWidget5 = constraintWidget6;
                } else {
                    linearSystem2.addEquality(constraintWidget6.mLeft.mSolverVariable, constraintWidget6.mLeft.mTarget.mSolverVariable, z2, i3);
                    linearSystem2.addEquality(constraintWidget6.mRight.mSolverVariable, constraintWidget6.mLeft.mSolverVariable, z2, i3);
                }
                if (constraintWidget6.getVisibility() != i4 && constraintWidget6.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                    if (constraintWidget6.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                        zArr[z2] = z2;
                    }
                    if (constraintWidget6.mDimensionRatio <= f) {
                        zArr[z2] = z2;
                        i5 = i2 + 1;
                        if (i5 >= constraintWidget2.mMatchConstraintsChainedWidgets.length) {
                            Object obj = 2;
                            constraintWidget2.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(constraintWidget2.mMatchConstraintsChainedWidgets, constraintWidget2.mMatchConstraintsChainedWidgets.length * 2);
                        }
                        constraintWidget2.mMatchConstraintsChainedWidgets[i2] = constraintWidget6;
                        i2 = i5;
                    }
                }
                if (constraintWidget6.mRight.mTarget.mOwner.mLeft.mTarget == null || constraintWidget6.mRight.mTarget.mOwner.mLeft.mTarget.mOwner != constraintWidget6 || constraintWidget6.mRight.mTarget.mOwner == constraintWidget6) {
                    break;
                }
                constraintWidget7 = constraintWidget6.mRight.mTarget.mOwner;
                constraintWidget6 = constraintWidget7;
                constraintWidget4 = null;
                f = 0.0f;
            }
            if (!(constraintWidget6.mRight.mTarget == null || constraintWidget6.mRight.mTarget.mOwner == constraintWidget2)) {
                z3 = z2;
            }
            if (constraintWidget3.mLeft.mTarget == null || constraintWidget7.mRight.mTarget == null) {
                i6 = 1;
                zArr[i6] = i6;
            } else {
                i6 = 1;
            }
            constraintWidget3.mHorizontalChainFixedPosition = z3;
            constraintWidget7.mHorizontalNextWidget = null;
            constraintWidgetArr[z2] = constraintWidget3;
            constraintWidgetArr[2] = constraintWidget8;
            constraintWidgetArr[i6] = constraintWidget7;
            constraintWidgetArr[3] = constraintWidget5;
        } else {
            z = (constraintWidget3.mTop.mTarget == null || constraintWidget3.mTop.mTarget.mOwner == constraintWidget2) ? true : z2;
            constraintWidget4 = null;
            constraintWidget3.mVerticalNextWidget = constraintWidget4;
            int i7 = z2;
            constraintWidget6 = constraintWidget4;
            constraintWidget5 = constraintWidget.getVisibility() != i4 ? constraintWidget3 : constraintWidget4;
            ConstraintWidget constraintWidget9 = constraintWidget5;
            ConstraintWidget constraintWidget10 = constraintWidget3;
            while (constraintWidget10.mBottom.mTarget != null) {
                constraintWidget10.mVerticalNextWidget = constraintWidget4;
                if (constraintWidget10.getVisibility() != i4) {
                    if (constraintWidget5 == null) {
                        constraintWidget5 = constraintWidget10;
                    }
                    if (!(constraintWidget9 == null || constraintWidget9 == constraintWidget10)) {
                        constraintWidget9.mVerticalNextWidget = constraintWidget10;
                    }
                    constraintWidget9 = constraintWidget10;
                } else {
                    linearSystem2.addEquality(constraintWidget10.mTop.mSolverVariable, constraintWidget10.mTop.mTarget.mSolverVariable, z2, i3);
                    linearSystem2.addEquality(constraintWidget10.mBottom.mSolverVariable, constraintWidget10.mTop.mSolverVariable, z2, i3);
                }
                if (constraintWidget10.getVisibility() == i4 || constraintWidget10.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    Object obj2 = null;
                } else {
                    if (constraintWidget10.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                        zArr[z2] = z2;
                    }
                    if (constraintWidget10.mDimensionRatio <= 0.0f) {
                        zArr[z2] = z2;
                        i5 = i7 + 1;
                        if (i5 >= constraintWidget2.mMatchConstraintsChainedWidgets.length) {
                            constraintWidget2.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(constraintWidget2.mMatchConstraintsChainedWidgets, constraintWidget2.mMatchConstraintsChainedWidgets.length * 2);
                        }
                        constraintWidget2.mMatchConstraintsChainedWidgets[i7] = constraintWidget10;
                        i7 = i5;
                    }
                }
                if (constraintWidget10.mBottom.mTarget.mOwner.mTop.mTarget == null || constraintWidget10.mBottom.mTarget.mOwner.mTop.mTarget.mOwner != constraintWidget10 || constraintWidget10.mBottom.mTarget.mOwner == constraintWidget10) {
                    break;
                }
                constraintWidget6 = constraintWidget10.mBottom.mTarget.mOwner;
                constraintWidget10 = constraintWidget6;
                constraintWidget4 = null;
                i3 = 5;
                i4 = 8;
            }
            i2 = i7;
            if (!(constraintWidget10.mBottom.mTarget == null || constraintWidget10.mBottom.mTarget.mOwner == constraintWidget2)) {
                z = z2;
            }
            if (constraintWidget3.mTop.mTarget == null || constraintWidget6.mBottom.mTarget == null) {
                i6 = 1;
                zArr[i6] = i6;
            } else {
                i6 = 1;
            }
            constraintWidget3.mVerticalChainFixedPosition = z;
            constraintWidget6.mVerticalNextWidget = null;
            constraintWidgetArr[z2] = constraintWidget3;
            constraintWidgetArr[2] = constraintWidget5;
            constraintWidgetArr[i6] = constraintWidget6;
            constraintWidgetArr[3] = constraintWidget9;
        }
        return i2;
    }
}
