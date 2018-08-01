package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;

public class ArrayRow {
    private static final boolean DEBUG = false;
    float constantValue = 0.0f;
    boolean isSimpleDefinition;
    boolean used;
    SolverVariable variable = null;
    final ArrayLinkedVariables variables;

    public ArrayRow(Cache cache) {
        boolean z = false;
        this.used = z;
        this.isSimpleDefinition = z;
        this.variables = new ArrayLinkedVariables(this, cache);
    }

    void updateClientEquations() {
        this.variables.updateClientEquations(this);
    }

    boolean hasAtLeastOnePositiveVariable() {
        return this.variables.hasAtLeastOnePositiveVariable();
    }

    boolean hasKeyVariable() {
        return this.variable != null && (this.variable.mType == Type.UNRESTRICTED || this.constantValue >= 0.0f);
    }

    public String toString() {
        return toReadableString();
    }

    String toReadableString() {
        StringBuilder stringBuilder;
        int i;
        String str = "";
        if (this.variable == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("0");
            str = stringBuilder.toString();
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.variable);
            str = stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(" = ");
        str = stringBuilder.toString();
        float f = 0.0f;
        int i2 = 0;
        int i3 = 1;
        if (this.constantValue != f) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.constantValue);
            str = stringBuilder.toString();
            i = i3;
        } else {
            i = i2;
        }
        int i4 = this.variables.currentSize;
        while (i2 < i4) {
            SolverVariable variable = this.variables.getVariable(i2);
            if (variable != null) {
                float variableValue = this.variables.getVariableValue(i2);
                String solverVariable = variable.toString();
                float f2 = -1.0f;
                if (i == 0) {
                    if (variableValue < f) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str);
                        stringBuilder.append("- ");
                        str = stringBuilder.toString();
                        variableValue *= f2;
                    }
                } else if (variableValue > f) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(" + ");
                    str = stringBuilder.toString();
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(" - ");
                    str = stringBuilder.toString();
                    variableValue *= f2;
                }
                if (variableValue == 1.0f) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(solverVariable);
                    str = stringBuilder.toString();
                } else {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(str);
                    stringBuilder.append(variableValue);
                    stringBuilder.append(" ");
                    stringBuilder.append(solverVariable);
                    str = stringBuilder.toString();
                }
                i = i3;
            }
            i2++;
        }
        if (i != 0) {
            return str;
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append("0.0");
        return stringBuilder.toString();
    }

    public void reset() {
        this.variable = null;
        this.variables.clear();
        this.constantValue = 0.0f;
        this.isSimpleDefinition = false;
    }

    boolean hasVariable(SolverVariable solverVariable) {
        return this.variables.containsKey(solverVariable);
    }

    ArrayRow createRowDefinition(SolverVariable solverVariable, int i) {
        this.variable = solverVariable;
        float f = (float) i;
        solverVariable.computedValue = f;
        this.constantValue = f;
        this.isSimpleDefinition = true;
        return this;
    }

    public ArrayRow createRowEquals(SolverVariable solverVariable, int i) {
        if (i < 0) {
            this.constantValue = (float) (-1 * i);
            this.variables.put(solverVariable, 1.0f);
        } else {
            this.constantValue = (float) i;
            this.variables.put(solverVariable, -1.0f);
        }
        return this;
    }

    public ArrayRow createRowEquals(SolverVariable solverVariable, SolverVariable solverVariable2, int i) {
        Object obj = null;
        if (i != 0) {
            if (i < 0) {
                i *= -1;
                obj = 1;
            }
            this.constantValue = (float) i;
        }
        float f = 1.0f;
        float f2 = -1.0f;
        if (obj == null) {
            this.variables.put(solverVariable, f2);
            this.variables.put(solverVariable2, f);
        } else {
            this.variables.put(solverVariable, f);
            this.variables.put(solverVariable2, f2);
        }
        return this;
    }

    ArrayRow addSingleError(SolverVariable solverVariable, int i) {
        this.variables.put(solverVariable, (float) i);
        return this;
    }

    public ArrayRow createRowGreaterThan(SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, int i) {
        Object obj = null;
        if (i != 0) {
            if (i < 0) {
                i *= -1;
                obj = 1;
            }
            this.constantValue = (float) i;
        }
        float f = 1.0f;
        float f2 = -1.0f;
        if (obj == null) {
            this.variables.put(solverVariable, f2);
            this.variables.put(solverVariable2, f);
            this.variables.put(solverVariable3, f);
        } else {
            this.variables.put(solverVariable, f);
            this.variables.put(solverVariable2, f2);
            this.variables.put(solverVariable3, f2);
        }
        return this;
    }

    public ArrayRow createRowLowerThan(SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, int i) {
        Object obj = null;
        if (i != 0) {
            if (i < 0) {
                i *= -1;
                obj = 1;
            }
            this.constantValue = (float) i;
        }
        float f = 1.0f;
        float f2 = -1.0f;
        if (obj == null) {
            this.variables.put(solverVariable, f2);
            this.variables.put(solverVariable2, f);
            this.variables.put(solverVariable3, f2);
        } else {
            this.variables.put(solverVariable, f);
            this.variables.put(solverVariable2, f2);
            this.variables.put(solverVariable3, f);
        }
        return this;
    }

    public ArrayRow createRowEqualDimension(float f, float f2, float f3, SolverVariable solverVariable, int i, SolverVariable solverVariable2, int i2, SolverVariable solverVariable3, int i3, SolverVariable solverVariable4, int i4) {
        float f4 = -1.0f;
        float f5 = 1.0f;
        if (f2 == 0.0f || f == f3) {
            this.constantValue = (float) ((((-i) - i2) + i3) + i4);
            this.variables.put(solverVariable, f5);
            this.variables.put(solverVariable2, f4);
            this.variables.put(solverVariable4, f5);
            this.variables.put(solverVariable3, f4);
        } else {
            f = (f / f2) / (f3 / f2);
            this.constantValue = (((float) ((-i) - i2)) + (((float) i3) * f)) + (((float) i4) * f);
            this.variables.put(solverVariable, f5);
            this.variables.put(solverVariable2, f4);
            this.variables.put(solverVariable4, f);
            this.variables.put(solverVariable3, -f);
        }
        return this;
    }

    ArrayRow createRowCentering(SolverVariable solverVariable, SolverVariable solverVariable2, int i, float f, SolverVariable solverVariable3, SolverVariable solverVariable4, int i2) {
        float f2 = 1.0f;
        if (solverVariable2 == solverVariable3) {
            this.variables.put(solverVariable, f2);
            this.variables.put(solverVariable4, f2);
            this.variables.put(solverVariable2, -2.0f);
            return this;
        }
        float f3 = -1.0f;
        if (f == 0.5f) {
            this.variables.put(solverVariable, f2);
            this.variables.put(solverVariable2, f3);
            this.variables.put(solverVariable3, f3);
            this.variables.put(solverVariable4, f2);
            if (i > 0 || i2 > 0) {
                this.constantValue = (float) ((-i) + i2);
            }
        } else if (f <= 0.0f) {
            this.variables.put(solverVariable, f3);
            this.variables.put(solverVariable2, f2);
            this.constantValue = (float) i;
        } else if (f >= f2) {
            this.variables.put(solverVariable3, f3);
            this.variables.put(solverVariable4, f2);
            this.constantValue = (float) i2;
        } else {
            float f4 = f2 - f;
            this.variables.put(solverVariable, f2 * f4);
            this.variables.put(solverVariable2, f3 * f4);
            this.variables.put(solverVariable3, f3 * f);
            this.variables.put(solverVariable4, f2 * f);
            if (i > 0 || i2 > 0) {
                this.constantValue = (((float) (-i)) * f4) + (((float) i2) * f);
            }
        }
        return this;
    }

    public ArrayRow addError(SolverVariable solverVariable, SolverVariable solverVariable2) {
        this.variables.put(solverVariable, 1.0f);
        this.variables.put(solverVariable2, -1.0f);
        return this;
    }

    ArrayRow createRowDimensionPercent(SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, float f) {
        this.variables.put(solverVariable, -1.0f);
        this.variables.put(solverVariable2, 1.0f - f);
        this.variables.put(solverVariable3, f);
        return this;
    }

    public ArrayRow createRowDimensionRatio(SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, SolverVariable solverVariable4, float f) {
        this.variables.put(solverVariable, -1.0f);
        this.variables.put(solverVariable2, 1.0f);
        this.variables.put(solverVariable3, f);
        this.variables.put(solverVariable4, -f);
        return this;
    }

    int sizeInBytes() {
        int i = 4;
        return (((this.variable != null ? i : 0) + i) + i) + this.variables.sizeInBytes();
    }

    boolean updateRowWithEquation(ArrayRow arrayRow) {
        this.variables.updateFromRow(this, arrayRow);
        return true;
    }

    void ensurePositiveConstant() {
        if (this.constantValue < 0.0f) {
            this.constantValue *= -1.0f;
            this.variables.invert();
        }
    }

    void pickRowVariable() {
        SolverVariable pickPivotCandidate = this.variables.pickPivotCandidate();
        if (pickPivotCandidate != null) {
            pivot(pickPivotCandidate);
        }
        if (this.variables.currentSize == 0) {
            this.isSimpleDefinition = true;
        }
    }

    void pivot(SolverVariable solverVariable) {
        float f = -1.0f;
        if (this.variable != null) {
            this.variables.put(this.variable, f);
            this.variable = null;
        }
        float remove = this.variables.remove(solverVariable) * f;
        this.variable = solverVariable;
        if (remove != 1.0f) {
            this.constantValue /= remove;
            this.variables.divideByAmount(remove);
        }
    }
}
