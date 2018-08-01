package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    static void applyDirectResolutionHorizontalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        int i3;
        int x;
        float f;
        float f2;
        ConstraintWidget constraintWidget2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        int i4 = i;
        float f3 = 0.0f;
        int i5 = 0;
        ConstraintWidget constraintWidget3 = constraintWidget;
        float f4 = f3;
        int i6 = i5;
        int i7 = i6;
        ConstraintWidget constraintWidget4 = null;
        while (true) {
            i2 = 8;
            i3 = 1;
            if (constraintWidget3 == null) {
                break;
            }
            if (constraintWidget3.getVisibility() != i2) {
                i3 = i5;
            }
            if (i3 == 0) {
                i6++;
                if (constraintWidget3.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    i7 = ((i7 + constraintWidget3.getWidth()) + (constraintWidget3.mLeft.mTarget != null ? constraintWidget3.mLeft.getMargin() : i5)) + (constraintWidget3.mRight.mTarget != null ? constraintWidget3.mRight.getMargin() : i5);
                } else {
                    f4 += constraintWidget3.mHorizontalWeight;
                }
            }
            constraintWidget4 = constraintWidget3.mRight.mTarget != null ? constraintWidget3.mRight.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mLeft.mTarget == null || !(constraintWidget4.mLeft.mTarget == null || constraintWidget4.mLeft.mTarget.mOwner == constraintWidget3))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget4 = constraintWidget3;
            constraintWidget3 = constraintWidget5;
        }
        if (constraintWidget4 != null) {
            x = constraintWidget4.mRight.mTarget != null ? constraintWidget4.mRight.mTarget.mOwner.getX() : i5;
            if (constraintWidget4.mRight.mTarget != null && constraintWidget4.mRight.mTarget.mOwner == constraintWidget2) {
                x = constraintWidgetContainer.getRight();
            }
        } else {
            x = i5;
        }
        float f5 = ((float) (x - i5)) - ((float) i7);
        float f6 = f5 / ((float) (i6 + i3));
        if (i4 == 0) {
            f = f6;
            f2 = f;
        } else {
            f = f3;
            f2 = f5 / ((float) i4);
        }
        constraintWidget4 = constraintWidget;
        while (constraintWidget4 != null) {
            i3 = constraintWidget4.mLeft.mTarget != null ? constraintWidget4.mLeft.getMargin() : i5;
            int margin = constraintWidget4.mRight.mTarget != null ? constraintWidget4.mRight.getMargin() : i5;
            float f7 = 0.5f;
            if (constraintWidget4.getVisibility() != i2) {
                float f8 = (float) i3;
                f += f8;
                linearSystem2.addEquality(constraintWidget4.mLeft.mSolverVariable, (int) (f + f7));
                if (constraintWidget4.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    f += (float) constraintWidget4.getWidth();
                } else if (f4 == f3) {
                    f += (f2 - f8) - ((float) margin);
                } else {
                    f += (((constraintWidget4.mHorizontalWeight * f5) / f4) - f8) - ((float) margin);
                }
                linearSystem2.addEquality(constraintWidget4.mRight.mSolverVariable, (int) (f7 + f));
                if (i4 == 0) {
                    f += f2;
                }
                f += (float) margin;
            } else {
                int i8 = (int) ((f - (f2 / 2.0f)) + f7);
                linearSystem2.addEquality(constraintWidget4.mLeft.mSolverVariable, i8);
                linearSystem2.addEquality(constraintWidget4.mRight.mSolverVariable, i8);
            }
            ConstraintWidget constraintWidget6 = constraintWidget4.mRight.mTarget != null ? constraintWidget4.mRight.mTarget.mOwner : null;
            if (!(constraintWidget6 == null || constraintWidget6.mLeft.mTarget == null || constraintWidget6.mLeft.mTarget.mOwner == constraintWidget4)) {
                constraintWidget6 = null;
            }
            constraintWidget4 = constraintWidget6 == constraintWidget2 ? null : constraintWidget6;
        }
    }

    static void applyDirectResolutionVerticalChain(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, ConstraintWidget constraintWidget) {
        int i2;
        int i3;
        int x;
        float f;
        float f2;
        ConstraintWidget constraintWidget2 = constraintWidgetContainer;
        LinearSystem linearSystem2 = linearSystem;
        int i4 = i;
        float f3 = 0.0f;
        int i5 = 0;
        ConstraintWidget constraintWidget3 = constraintWidget;
        float f4 = f3;
        int i6 = i5;
        int i7 = i6;
        ConstraintWidget constraintWidget4 = null;
        while (true) {
            i2 = 8;
            i3 = 1;
            if (constraintWidget3 == null) {
                break;
            }
            if (constraintWidget3.getVisibility() != i2) {
                i3 = i5;
            }
            if (i3 == 0) {
                i6++;
                if (constraintWidget3.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    i7 = ((i7 + constraintWidget3.getHeight()) + (constraintWidget3.mTop.mTarget != null ? constraintWidget3.mTop.getMargin() : i5)) + (constraintWidget3.mBottom.mTarget != null ? constraintWidget3.mBottom.getMargin() : i5);
                } else {
                    f4 += constraintWidget3.mVerticalWeight;
                }
            }
            constraintWidget4 = constraintWidget3.mBottom.mTarget != null ? constraintWidget3.mBottom.mTarget.mOwner : null;
            if (constraintWidget4 != null && (constraintWidget4.mTop.mTarget == null || !(constraintWidget4.mTop.mTarget == null || constraintWidget4.mTop.mTarget.mOwner == constraintWidget3))) {
                constraintWidget4 = null;
            }
            ConstraintWidget constraintWidget5 = constraintWidget4;
            constraintWidget4 = constraintWidget3;
            constraintWidget3 = constraintWidget5;
        }
        if (constraintWidget4 != null) {
            x = constraintWidget4.mBottom.mTarget != null ? constraintWidget4.mBottom.mTarget.mOwner.getX() : i5;
            if (constraintWidget4.mBottom.mTarget != null && constraintWidget4.mBottom.mTarget.mOwner == constraintWidget2) {
                x = constraintWidgetContainer.getBottom();
            }
        } else {
            x = i5;
        }
        float f5 = ((float) (x - i5)) - ((float) i7);
        float f6 = f5 / ((float) (i6 + i3));
        if (i4 == 0) {
            f = f6;
            f2 = f;
        } else {
            f = f3;
            f2 = f5 / ((float) i4);
        }
        constraintWidget4 = constraintWidget;
        while (constraintWidget4 != null) {
            i3 = constraintWidget4.mTop.mTarget != null ? constraintWidget4.mTop.getMargin() : i5;
            int margin = constraintWidget4.mBottom.mTarget != null ? constraintWidget4.mBottom.getMargin() : i5;
            float f7 = 0.5f;
            if (constraintWidget4.getVisibility() != i2) {
                float f8 = (float) i3;
                f += f8;
                linearSystem2.addEquality(constraintWidget4.mTop.mSolverVariable, (int) (f + f7));
                if (constraintWidget4.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_CONSTRAINT) {
                    f += (float) constraintWidget4.getHeight();
                } else if (f4 == f3) {
                    f += (f2 - f8) - ((float) margin);
                } else {
                    f += (((constraintWidget4.mVerticalWeight * f5) / f4) - f8) - ((float) margin);
                }
                linearSystem2.addEquality(constraintWidget4.mBottom.mSolverVariable, (int) (f7 + f));
                if (i4 == 0) {
                    f += f2;
                }
                f += (float) margin;
            } else {
                int i8 = (int) ((f - (f2 / 2.0f)) + f7);
                linearSystem2.addEquality(constraintWidget4.mTop.mSolverVariable, i8);
                linearSystem2.addEquality(constraintWidget4.mBottom.mSolverVariable, i8);
            }
            ConstraintWidget constraintWidget6 = constraintWidget4.mBottom.mTarget != null ? constraintWidget4.mBottom.mTarget.mOwner : null;
            if (!(constraintWidget6 == null || constraintWidget6.mTop.mTarget == null || constraintWidget6.mTop.mTarget.mOwner == constraintWidget4)) {
                constraintWidget6 = null;
            }
            constraintWidget4 = constraintWidget6 == constraintWidget2 ? null : constraintWidget6;
        }
    }

    static void checkMatchParent(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        int i;
        int i2 = 2;
        if (constraintWidgetContainer.mHorizontalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
            i = constraintWidget.mLeft.mMargin;
            int width = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
            constraintWidget.setHorizontalDimension(i, width);
            constraintWidget.mHorizontalResolution = i2;
        }
        if (constraintWidgetContainer.mVerticalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
            i = constraintWidget.mTop.mMargin;
            int height = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, i);
            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == 8) {
                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + i);
            }
            constraintWidget.setVerticalDimension(i, height);
            constraintWidget.mVerticalResolution = i2;
        }
    }

    static void checkHorizontalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        int i = 1;
        if (constraintWidget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mHorizontalResolution = i;
            return;
        }
        int i2 = 2;
        int margin;
        int width;
        if (constraintWidgetContainer.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT || constraintWidget.mHorizontalDimensionBehaviour != DimensionBehaviour.MATCH_PARENT) {
            float f = 0.5f;
            if (constraintWidget.mLeft.mTarget == null || constraintWidget.mRight.mTarget == null) {
                SolverVariable solverVariable;
                if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer) {
                    margin = constraintWidget.mLeft.getMargin();
                    width = constraintWidget.getWidth() + margin;
                    constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                    constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                    linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin);
                    linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
                    constraintWidget.mHorizontalResolution = i2;
                    constraintWidget.setHorizontalDimension(margin, width);
                } else if (constraintWidget.mRight.mTarget != null && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
                    constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                    constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                    margin = constraintWidgetContainer.getWidth() - constraintWidget.mRight.getMargin();
                    width = margin - constraintWidget.getWidth();
                    linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width);
                    linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin);
                    constraintWidget.mHorizontalResolution = i2;
                    constraintWidget.setHorizontalDimension(width, margin);
                } else if (constraintWidget.mLeft.mTarget != null && constraintWidget.mLeft.mTarget.mOwner.mHorizontalResolution == i2) {
                    solverVariable = constraintWidget.mLeft.mTarget.mSolverVariable;
                    constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                    constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                    margin = (int) ((solverVariable.computedValue + ((float) constraintWidget.mLeft.getMargin())) + f);
                    width = constraintWidget.getWidth() + margin;
                    linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin);
                    linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
                    constraintWidget.mHorizontalResolution = i2;
                    constraintWidget.setHorizontalDimension(margin, width);
                } else if (constraintWidget.mRight.mTarget == null || constraintWidget.mRight.mTarget.mOwner.mHorizontalResolution != i2) {
                    int i3 = 0;
                    width = constraintWidget.mLeft.mTarget != null ? i : i3;
                    int i4 = constraintWidget.mRight.mTarget != null ? i : i3;
                    if (width == 0 && i4 == 0) {
                        if (constraintWidget instanceof Guideline) {
                            Guideline guideline = (Guideline) constraintWidget;
                            if (guideline.getOrientation() == i) {
                                float relativeBegin;
                                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                                i4 = -1;
                                if (guideline.getRelativeBegin() != i4) {
                                    relativeBegin = (float) guideline.getRelativeBegin();
                                } else if (guideline.getRelativeEnd() != i4) {
                                    relativeBegin = (float) (constraintWidgetContainer.getWidth() - guideline.getRelativeEnd());
                                } else {
                                    relativeBegin = guideline.getRelativePercent() * ((float) constraintWidgetContainer.getWidth());
                                }
                                width = (int) (relativeBegin + f);
                                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width);
                                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
                                constraintWidget.mHorizontalResolution = i2;
                                constraintWidget.mVerticalResolution = i2;
                                constraintWidget.setHorizontalDimension(width, width);
                                constraintWidget.setVerticalDimension(i3, constraintWidgetContainer.getHeight());
                            }
                        } else {
                            constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                            constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                            margin = constraintWidget.getX();
                            width = constraintWidget.getWidth() + margin;
                            linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, margin);
                            linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, width);
                            constraintWidget.mHorizontalResolution = i2;
                        }
                    }
                } else {
                    solverVariable = constraintWidget.mRight.mTarget.mSolverVariable;
                    constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                    constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                    margin = (int) ((solverVariable.computedValue - ((float) constraintWidget.mRight.getMargin())) + f);
                    width = margin - constraintWidget.getWidth();
                    linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width);
                    linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin);
                    constraintWidget.mHorizontalResolution = i2;
                    constraintWidget.setHorizontalDimension(width, margin);
                }
                return;
            } else if (constraintWidget.mLeft.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mRight.mTarget.mOwner == constraintWidgetContainer) {
                width = constraintWidget.mLeft.getMargin();
                i = constraintWidget.mRight.getMargin();
                if (constraintWidgetContainer.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                    margin = constraintWidgetContainer.getWidth() - i;
                } else {
                    width += (int) ((((float) (((constraintWidgetContainer.getWidth() - width) - i) - constraintWidget.getWidth())) * constraintWidget.mHorizontalBiasPercent) + f);
                    margin = constraintWidget.getWidth() + width;
                }
                constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
                constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
                linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width);
                linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin);
                constraintWidget.mHorizontalResolution = i2;
                constraintWidget.setHorizontalDimension(width, margin);
                return;
            } else {
                constraintWidget.mHorizontalResolution = i;
                return;
            }
        }
        constraintWidget.mLeft.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mLeft);
        constraintWidget.mRight.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mRight);
        width = constraintWidget.mLeft.mMargin;
        margin = constraintWidgetContainer.getWidth() - constraintWidget.mRight.mMargin;
        linearSystem.addEquality(constraintWidget.mLeft.mSolverVariable, width);
        linearSystem.addEquality(constraintWidget.mRight.mSolverVariable, margin);
        constraintWidget.setHorizontalDimension(width, margin);
        constraintWidget.mHorizontalResolution = i2;
    }

    static void checkVerticalSimpleDependency(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, ConstraintWidget constraintWidget) {
        int i = 1;
        if (constraintWidget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            constraintWidget.mVerticalResolution = i;
            return;
        }
        int i2 = 8;
        int i3 = 2;
        int margin;
        int height;
        if (constraintWidgetContainer.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT || constraintWidget.mVerticalDimensionBehaviour != DimensionBehaviour.MATCH_PARENT) {
            float f = 0.5f;
            if (constraintWidget.mTop.mTarget == null || constraintWidget.mBottom.mTarget == null) {
                SolverVariable solverVariable;
                if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer) {
                    margin = constraintWidget.mTop.getMargin();
                    height = constraintWidget.getHeight() + margin;
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
                    if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                        constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                        linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin);
                    }
                    constraintWidget.mVerticalResolution = i3;
                    constraintWidget.setVerticalDimension(margin, height);
                } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    margin = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.getMargin();
                    height = margin - constraintWidget.getHeight();
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin);
                    if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                        constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                        linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height);
                    }
                    constraintWidget.mVerticalResolution = i3;
                    constraintWidget.setVerticalDimension(height, margin);
                } else if (constraintWidget.mTop.mTarget != null && constraintWidget.mTop.mTarget.mOwner.mVerticalResolution == i3) {
                    solverVariable = constraintWidget.mTop.mTarget.mSolverVariable;
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    margin = (int) ((solverVariable.computedValue + ((float) constraintWidget.mTop.getMargin())) + f);
                    height = constraintWidget.getHeight() + margin;
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
                    if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                        constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                        linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin);
                    }
                    constraintWidget.mVerticalResolution = i3;
                    constraintWidget.setVerticalDimension(margin, height);
                } else if (constraintWidget.mBottom.mTarget != null && constraintWidget.mBottom.mTarget.mOwner.mVerticalResolution == i3) {
                    solverVariable = constraintWidget.mBottom.mTarget.mSolverVariable;
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    margin = (int) ((solverVariable.computedValue - ((float) constraintWidget.mBottom.getMargin())) + f);
                    height = margin - constraintWidget.getHeight();
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin);
                    if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                        constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                        linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height);
                    }
                    constraintWidget.mVerticalResolution = i3;
                    constraintWidget.setVerticalDimension(height, margin);
                } else if (constraintWidget.mBaseline.mTarget == null || constraintWidget.mBaseline.mTarget.mOwner.mVerticalResolution != i3) {
                    int i4 = 0;
                    height = constraintWidget.mBaseline.mTarget != null ? i : i4;
                    int i5 = constraintWidget.mTop.mTarget != null ? i : i4;
                    if (constraintWidget.mBottom.mTarget == null) {
                        i = i4;
                    }
                    if (height == 0 && i5 == 0 && i == 0) {
                        if (constraintWidget instanceof Guideline) {
                            Guideline guideline = (Guideline) constraintWidget;
                            if (guideline.getOrientation() == 0) {
                                float relativeBegin;
                                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                                i2 = -1;
                                if (guideline.getRelativeBegin() != i2) {
                                    relativeBegin = (float) guideline.getRelativeBegin();
                                } else if (guideline.getRelativeEnd() != i2) {
                                    relativeBegin = (float) (constraintWidgetContainer.getHeight() - guideline.getRelativeEnd());
                                } else {
                                    relativeBegin = guideline.getRelativePercent() * ((float) constraintWidgetContainer.getHeight());
                                }
                                height = (int) (relativeBegin + f);
                                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height);
                                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
                                constraintWidget.mVerticalResolution = i3;
                                constraintWidget.mHorizontalResolution = i3;
                                constraintWidget.setVerticalDimension(height, height);
                                constraintWidget.setHorizontalDimension(i4, constraintWidgetContainer.getWidth());
                            }
                        } else {
                            constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                            constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                            margin = constraintWidget.getY();
                            height = constraintWidget.getHeight() + margin;
                            linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                            linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
                            if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                                constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                                linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, margin + constraintWidget.mBaselineDistance);
                            }
                            constraintWidget.mVerticalResolution = i3;
                        }
                    }
                } else {
                    solverVariable = constraintWidget.mBaseline.mTarget.mSolverVariable;
                    constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                    constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                    margin = (int) ((solverVariable.computedValue - ((float) constraintWidget.mBaselineDistance)) + f);
                    height = constraintWidget.getHeight() + margin;
                    linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, margin);
                    linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, height);
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + margin);
                    constraintWidget.mVerticalResolution = i3;
                    constraintWidget.setVerticalDimension(margin, height);
                }
                return;
            } else if (constraintWidget.mTop.mTarget.mOwner == constraintWidgetContainer && constraintWidget.mBottom.mTarget.mOwner == constraintWidgetContainer) {
                height = constraintWidget.mTop.getMargin();
                i = constraintWidget.mBottom.getMargin();
                if (constraintWidgetContainer.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                    margin = constraintWidget.getHeight() + height;
                } else {
                    height = (int) ((((float) height) + (((float) (((constraintWidgetContainer.getHeight() - height) - i) - constraintWidget.getHeight())) * constraintWidget.mVerticalBiasPercent)) + f);
                    margin = constraintWidget.getHeight() + height;
                }
                constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
                constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
                linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height);
                linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin);
                if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
                    constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
                    linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height);
                }
                constraintWidget.mVerticalResolution = i3;
                constraintWidget.setVerticalDimension(height, margin);
                return;
            } else {
                constraintWidget.mVerticalResolution = i;
                return;
            }
        }
        constraintWidget.mTop.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mTop);
        constraintWidget.mBottom.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBottom);
        height = constraintWidget.mTop.mMargin;
        margin = constraintWidgetContainer.getHeight() - constraintWidget.mBottom.mMargin;
        linearSystem.addEquality(constraintWidget.mTop.mSolverVariable, height);
        linearSystem.addEquality(constraintWidget.mBottom.mSolverVariable, margin);
        if (constraintWidget.mBaselineDistance > 0 || constraintWidget.getVisibility() == i2) {
            constraintWidget.mBaseline.mSolverVariable = linearSystem.createObjectVariable(constraintWidget.mBaseline);
            linearSystem.addEquality(constraintWidget.mBaseline.mSolverVariable, constraintWidget.mBaselineDistance + height);
        }
        constraintWidget.setVerticalDimension(height, margin);
        constraintWidget.mVerticalResolution = i3;
    }
}
