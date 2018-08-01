package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;
import java.io.PrintStream;
import java.util.Arrays;

public class ArrayLinkedVariables {
    private static final boolean DEBUG = false;
    private static final int NONE = -1;
    private int ROW_SIZE = 8;
    private SolverVariable candidate = null;
    int currentSize;
    private int[] mArrayIndices = new int[this.ROW_SIZE];
    private int[] mArrayNextIndices = new int[this.ROW_SIZE];
    private float[] mArrayValues = new float[this.ROW_SIZE];
    private final Cache mCache;
    private boolean mDidFillOnce;
    private int mHead;
    private int mLast;
    private final ArrayRow mRow;

    ArrayLinkedVariables(ArrayRow arrayRow, Cache cache) {
        boolean z = false;
        this.currentSize = z;
        int i = -1;
        this.mHead = i;
        this.mLast = i;
        this.mDidFillOnce = z;
        this.mRow = arrayRow;
        this.mCache = cache;
    }

    public final void put(SolverVariable solverVariable, float f) {
        if (f == 0.0f) {
            remove(solverVariable);
            return;
        }
        boolean z = false;
        int i = -1;
        boolean z2 = true;
        if (this.mHead == i) {
            this.mHead = z;
            this.mArrayValues[this.mHead] = f;
            this.mArrayIndices[this.mHead] = solverVariable.id;
            this.mArrayNextIndices[this.mHead] = i;
            this.currentSize += z2;
            if (!this.mDidFillOnce) {
                this.mLast += z2;
            }
            return;
        }
        int i2 = this.mHead;
        int i3 = z;
        int i4 = i;
        while (i2 != i && i3 < this.currentSize) {
            if (this.mArrayIndices[i2] == solverVariable.id) {
                this.mArrayValues[i2] = f;
                return;
            }
            if (this.mArrayIndices[i2] < solverVariable.id) {
                i4 = i2;
            }
            i2 = this.mArrayNextIndices[i2];
            i3++;
        }
        i2 = this.mLast + z2;
        if (this.mDidFillOnce) {
            if (this.mArrayIndices[this.mLast] == i) {
                i2 = this.mLast;
            } else {
                i2 = this.mArrayIndices.length;
            }
        }
        if (i2 >= this.mArrayIndices.length && this.currentSize < this.mArrayIndices.length) {
            for (i3 = z; i3 < this.mArrayIndices.length; i3++) {
                if (this.mArrayIndices[i3] == i) {
                    i2 = i3;
                    break;
                }
            }
        }
        if (i2 >= this.mArrayIndices.length) {
            i2 = this.mArrayIndices.length;
            this.ROW_SIZE *= 2;
            this.mDidFillOnce = z;
            this.mLast = i2 - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[i2] = solverVariable.id;
        this.mArrayValues[i2] = f;
        if (i4 != i) {
            this.mArrayNextIndices[i2] = this.mArrayNextIndices[i4];
            this.mArrayNextIndices[i4] = i2;
        } else {
            this.mArrayNextIndices[i2] = this.mHead;
            this.mHead = i2;
        }
        this.currentSize += z2;
        if (!this.mDidFillOnce) {
            this.mLast += z2;
        }
        if (this.currentSize >= this.mArrayIndices.length) {
            this.mDidFillOnce = z2;
        }
    }

    public final void add(SolverVariable solverVariable, float f) {
        float f2 = 0.0f;
        if (f != f2) {
            boolean z = false;
            int i = -1;
            boolean z2 = true;
            if (this.mHead == i) {
                this.mHead = z;
                this.mArrayValues[this.mHead] = f;
                this.mArrayIndices[this.mHead] = solverVariable.id;
                this.mArrayNextIndices[this.mHead] = i;
                this.currentSize += z2;
                if (!this.mDidFillOnce) {
                    this.mLast += z2;
                }
                return;
            }
            int i2 = this.mHead;
            int i3 = z;
            int i4 = i;
            while (i2 != i && i3 < this.currentSize) {
                int i5 = this.mArrayIndices[i2];
                if (i5 == solverVariable.id) {
                    float[] fArr = this.mArrayValues;
                    fArr[i2] = fArr[i2] + f;
                    if (this.mArrayValues[i2] == f2) {
                        if (i2 == this.mHead) {
                            this.mHead = this.mArrayNextIndices[i2];
                        } else {
                            this.mArrayNextIndices[i4] = this.mArrayNextIndices[i2];
                        }
                        this.mCache.mIndexedVariables[i5].removeClientEquation(this.mRow);
                        if (this.mDidFillOnce) {
                            this.mLast = i2;
                        }
                        this.currentSize -= z2;
                    }
                    return;
                }
                if (this.mArrayIndices[i2] < solverVariable.id) {
                    i4 = i2;
                }
                i2 = this.mArrayNextIndices[i2];
                i3++;
            }
            int i6 = this.mLast + z2;
            if (this.mDidFillOnce) {
                if (this.mArrayIndices[this.mLast] == i) {
                    i6 = this.mLast;
                } else {
                    i6 = this.mArrayIndices.length;
                }
            }
            if (i6 >= this.mArrayIndices.length && this.currentSize < this.mArrayIndices.length) {
                for (i2 = z; i2 < this.mArrayIndices.length; i2++) {
                    if (this.mArrayIndices[i2] == i) {
                        i6 = i2;
                        break;
                    }
                }
            }
            if (i6 >= this.mArrayIndices.length) {
                i6 = this.mArrayIndices.length;
                this.ROW_SIZE *= 2;
                this.mDidFillOnce = z;
                this.mLast = i6 - 1;
                this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
                this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
                this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
            }
            this.mArrayIndices[i6] = solverVariable.id;
            this.mArrayValues[i6] = f;
            if (i4 != i) {
                this.mArrayNextIndices[i6] = this.mArrayNextIndices[i4];
                this.mArrayNextIndices[i4] = i6;
            } else {
                this.mArrayNextIndices[i6] = this.mHead;
                this.mHead = i6;
            }
            this.currentSize += z2;
            if (!this.mDidFillOnce) {
                this.mLast += z2;
            }
            if (this.mLast >= this.mArrayIndices.length) {
                this.mDidFillOnce = z2;
                this.mLast = this.mArrayIndices.length - z2;
            }
        }
    }

    public final float remove(SolverVariable solverVariable) {
        if (this.candidate == solverVariable) {
            this.candidate = null;
        }
        float f = 0.0f;
        int i = -1;
        if (this.mHead == i) {
            return f;
        }
        int i2 = this.mHead;
        int i3 = 0;
        int i4 = i;
        while (i2 != i && i3 < this.currentSize) {
            int i5 = this.mArrayIndices[i2];
            if (i5 == solverVariable.id) {
                if (i2 == this.mHead) {
                    this.mHead = this.mArrayNextIndices[i2];
                } else {
                    this.mArrayNextIndices[i4] = this.mArrayNextIndices[i2];
                }
                this.mCache.mIndexedVariables[i5].removeClientEquation(this.mRow);
                this.currentSize--;
                this.mArrayIndices[i2] = i;
                if (this.mDidFillOnce) {
                    this.mLast = i2;
                }
                return this.mArrayValues[i2];
            }
            i3++;
            i4 = i2;
            i2 = this.mArrayNextIndices[i2];
        }
        return f;
    }

    public final void clear() {
        int i = -1;
        this.mHead = i;
        this.mLast = i;
        boolean z = false;
        this.mDidFillOnce = z;
        this.currentSize = z;
    }

    final boolean containsKey(SolverVariable solverVariable) {
        int i = -1;
        boolean z = false;
        if (this.mHead == i) {
            return z;
        }
        int i2 = this.mHead;
        int i3 = z;
        while (i2 != i && i3 < this.currentSize) {
            if (this.mArrayIndices[i2] == solverVariable.id) {
                return true;
            }
            i2 = this.mArrayNextIndices[i2];
            i3++;
        }
        return z;
    }

    boolean hasAtLeastOnePositiveVariable() {
        int i = this.mHead;
        boolean z = false;
        int i2 = z;
        while (i != -1 && i2 < this.currentSize) {
            if (this.mArrayValues[i] > 0.0f) {
                return true;
            }
            i = this.mArrayNextIndices[i];
            i2++;
        }
        return z;
    }

    void invert() {
        int i = this.mHead;
        int i2 = 0;
        while (i != -1 && i2 < this.currentSize) {
            float[] fArr = this.mArrayValues;
            fArr[i] = fArr[i] * -1.0f;
            i = this.mArrayNextIndices[i];
            i2++;
        }
    }

    void divideByAmount(float f) {
        int i = this.mHead;
        int i2 = 0;
        while (i != -1 && i2 < this.currentSize) {
            float[] fArr = this.mArrayValues;
            fArr[i] = fArr[i] / f;
            i = this.mArrayNextIndices[i];
            i2++;
        }
    }

    void updateClientEquations(ArrayRow arrayRow) {
        int i = this.mHead;
        int i2 = 0;
        while (i != -1 && i2 < this.currentSize) {
            this.mCache.mIndexedVariables[this.mArrayIndices[i]].addClientEquation(arrayRow);
            i = this.mArrayNextIndices[i];
            i2++;
        }
    }

    SolverVariable pickPivotCandidate() {
        int i = this.mHead;
        SolverVariable solverVariable = null;
        int i2 = 0;
        SolverVariable solverVariable2 = solverVariable;
        while (i != -1 && i2 < this.currentSize) {
            float f = this.mArrayValues[i];
            float f2 = 0.001f;
            float f3 = 0.0f;
            if (f < f3) {
                if (f > -0.001f) {
                    this.mArrayValues[i] = f3;
                }
                if (f == f3) {
                    SolverVariable solverVariable3 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                    if (solverVariable3.mType == Type.UNRESTRICTED) {
                        if (f < f3) {
                            return solverVariable3;
                        }
                        if (solverVariable == null) {
                            solverVariable = solverVariable3;
                        }
                    } else if (f < f3 && (solverVariable2 == null || solverVariable3.strength < solverVariable2.strength)) {
                        solverVariable2 = solverVariable3;
                    }
                }
                i = this.mArrayNextIndices[i];
                i2++;
            } else {
                if (f < f2) {
                    this.mArrayValues[i] = f3;
                }
                if (f == f3) {
                    SolverVariable solverVariable32 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                    if (solverVariable32.mType == Type.UNRESTRICTED) {
                        if (f < f3) {
                            return solverVariable32;
                        }
                        if (solverVariable == null) {
                            solverVariable = solverVariable32;
                        }
                    } else if (f < f3 && (solverVariable2 == null || solverVariable32.strength < solverVariable2.strength)) {
                        solverVariable2 = solverVariable32;
                    }
                }
                i = this.mArrayNextIndices[i];
                i2++;
            }
            f = f3;
            if (f == f3) {
                SolverVariable solverVariable322 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (solverVariable322.mType == Type.UNRESTRICTED) {
                    if (f < f3) {
                        return solverVariable322;
                    }
                    if (solverVariable == null) {
                        solverVariable = solverVariable322;
                    }
                } else if (f < f3 && (solverVariable2 == null || solverVariable322.strength < solverVariable2.strength)) {
                    solverVariable2 = solverVariable322;
                }
            }
            i = this.mArrayNextIndices[i];
            i2++;
        }
        return solverVariable != null ? solverVariable : solverVariable2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void updateFromRow(android.support.constraint.solver.ArrayRow r9, android.support.constraint.solver.ArrayRow r10) {
        /*
        r8 = this;
        r0 = r8.mHead;
        r1 = 0;
    L_0x0003:
        r2 = r1;
    L_0x0004:
        r3 = -1;
        if (r0 == r3) goto L_0x0059;
    L_0x0007:
        r4 = r8.currentSize;
        if (r2 >= r4) goto L_0x0059;
    L_0x000b:
        r4 = r8.mArrayIndices;
        r4 = r4[r0];
        r5 = r10.variable;
        r5 = r5.id;
        if (r4 != r5) goto L_0x0052;
    L_0x0015:
        r2 = r8.mArrayValues;
        r0 = r2[r0];
        r2 = r10.variable;
        r8.remove(r2);
        r2 = r10.variables;
        r4 = r2.mHead;
        r5 = r1;
    L_0x0023:
        if (r4 == r3) goto L_0x0042;
    L_0x0025:
        r6 = r2.currentSize;
        if (r5 >= r6) goto L_0x0042;
    L_0x0029:
        r6 = r8.mCache;
        r6 = r6.mIndexedVariables;
        r7 = r2.mArrayIndices;
        r7 = r7[r4];
        r6 = r6[r7];
        r7 = r2.mArrayValues;
        r7 = r7[r4];
        r7 = r7 * r0;
        r8.add(r6, r7);
        r6 = r2.mArrayNextIndices;
        r4 = r6[r4];
        r5 = r5 + 1;
        goto L_0x0023;
    L_0x0042:
        r2 = r9.constantValue;
        r3 = r10.constantValue;
        r3 = r3 * r0;
        r2 = r2 + r3;
        r9.constantValue = r2;
        r0 = r10.variable;
        r0.removeClientEquation(r9);
        r0 = r8.mHead;
        goto L_0x0003;
    L_0x0052:
        r3 = r8.mArrayNextIndices;
        r0 = r3[r0];
        r2 = r2 + 1;
        goto L_0x0004;
    L_0x0059:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.ArrayLinkedVariables.updateFromRow(android.support.constraint.solver.ArrayRow, android.support.constraint.solver.ArrayRow):void");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void updateFromSystem(android.support.constraint.solver.ArrayRow r10, android.support.constraint.solver.ArrayRow[] r11) {
        /*
        r9 = this;
        r0 = r9.mHead;
        r1 = 0;
    L_0x0003:
        r2 = r1;
    L_0x0004:
        r3 = -1;
        if (r0 == r3) goto L_0x0063;
    L_0x0007:
        r4 = r9.currentSize;
        if (r2 >= r4) goto L_0x0063;
    L_0x000b:
        r4 = r9.mCache;
        r4 = r4.mIndexedVariables;
        r5 = r9.mArrayIndices;
        r5 = r5[r0];
        r4 = r4[r5];
        r5 = r4.definitionId;
        if (r5 == r3) goto L_0x005c;
    L_0x0019:
        r2 = r9.mArrayValues;
        r0 = r2[r0];
        r9.remove(r4);
        r2 = r4.definitionId;
        r2 = r11[r2];
        r4 = r2.isSimpleDefinition;
        if (r4 != 0) goto L_0x004c;
    L_0x0028:
        r4 = r2.variables;
        r5 = r4.mHead;
        r6 = r1;
    L_0x002d:
        if (r5 == r3) goto L_0x004c;
    L_0x002f:
        r7 = r4.currentSize;
        if (r6 >= r7) goto L_0x004c;
    L_0x0033:
        r7 = r9.mCache;
        r7 = r7.mIndexedVariables;
        r8 = r4.mArrayIndices;
        r8 = r8[r5];
        r7 = r7[r8];
        r8 = r4.mArrayValues;
        r8 = r8[r5];
        r8 = r8 * r0;
        r9.add(r7, r8);
        r7 = r4.mArrayNextIndices;
        r5 = r7[r5];
        r6 = r6 + 1;
        goto L_0x002d;
    L_0x004c:
        r3 = r10.constantValue;
        r4 = r2.constantValue;
        r4 = r4 * r0;
        r3 = r3 + r4;
        r10.constantValue = r3;
        r0 = r2.variable;
        r0.removeClientEquation(r10);
        r0 = r9.mHead;
        goto L_0x0003;
    L_0x005c:
        r3 = r9.mArrayNextIndices;
        r0 = r3[r0];
        r2 = r2 + 1;
        goto L_0x0004;
    L_0x0063:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.ArrayLinkedVariables.updateFromSystem(android.support.constraint.solver.ArrayRow, android.support.constraint.solver.ArrayRow[]):void");
    }

    SolverVariable getPivotCandidate() {
        if (this.candidate != null) {
            return this.candidate;
        }
        int i = this.mHead;
        int i2 = 0;
        SolverVariable solverVariable = null;
        while (i != -1 && i2 < this.currentSize) {
            if (this.mArrayValues[i] < 0.0f) {
                SolverVariable solverVariable2 = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
                if (solverVariable == null || solverVariable.strength < solverVariable2.strength) {
                    solverVariable = solverVariable2;
                }
            }
            i = this.mArrayNextIndices[i];
            i2++;
        }
        return solverVariable;
    }

    final SolverVariable getVariable(int i) {
        int i2 = this.mHead;
        int i3 = 0;
        while (i2 != -1 && i3 < this.currentSize) {
            if (i3 == i) {
                return this.mCache.mIndexedVariables[this.mArrayIndices[i2]];
            }
            i2 = this.mArrayNextIndices[i2];
            i3++;
        }
        return null;
    }

    final float getVariableValue(int i) {
        int i2 = this.mHead;
        int i3 = 0;
        while (i2 != -1 && i3 < this.currentSize) {
            if (i3 == i) {
                return this.mArrayValues[i2];
            }
            i2 = this.mArrayNextIndices[i2];
            i3++;
        }
        return 0.0f;
    }

    public final float get(SolverVariable solverVariable) {
        int i = this.mHead;
        int i2 = 0;
        while (i != -1 && i2 < this.currentSize) {
            if (this.mArrayIndices[i] == solverVariable.id) {
                return this.mArrayValues[i];
            }
            i = this.mArrayNextIndices[i];
            i2++;
        }
        return 0.0f;
    }

    int sizeInBytes() {
        return (0 + (3 * (this.mArrayIndices.length * 4))) + 36;
    }

    public void display() {
        int i = this.currentSize;
        System.out.print("{ ");
        for (int i2 = 0; i2 < i; i2++) {
            SolverVariable variable = getVariable(i2);
            if (variable != null) {
                PrintStream printStream = System.out;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(variable);
                stringBuilder.append(" = ");
                stringBuilder.append(getVariableValue(i2));
                stringBuilder.append(" ");
                printStream.print(stringBuilder.toString());
            }
        }
        System.out.println(" }");
    }

    public String toString() {
        String str = "";
        int i = this.mHead;
        int i2 = 0;
        while (i != -1 && i2 < this.currentSize) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(" -> ");
            str = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.mArrayValues[i]);
            stringBuilder.append(" : ");
            str = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.mCache.mIndexedVariables[this.mArrayIndices[i]]);
            str = stringBuilder.toString();
            i = this.mArrayNextIndices[i];
            i2++;
        }
        return str;
    }
}
