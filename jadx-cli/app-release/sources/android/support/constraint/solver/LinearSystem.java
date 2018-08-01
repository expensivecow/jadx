package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

public class LinearSystem {
    private static final boolean DEBUG = false;
    private static int POOL_SIZE = 1000;
    private int TABLE_SIZE = 32;
    private boolean[] mAlreadyTestedCandidates;
    final Cache mCache;
    private Goal mGoal = new Goal();
    private int mMaxColumns = this.TABLE_SIZE;
    private int mMaxRows;
    int mNumColumns;
    private int mNumRows;
    private SolverVariable[] mPoolVariables;
    private int mPoolVariablesCount;
    private ArrayRow[] mRows;
    private HashMap<String, SolverVariable> mVariables;
    int mVariablesID;
    private ArrayRow[] tempClientsCopy;

    public LinearSystem() {
        int i = 0;
        this.mVariablesID = i;
        HashMap hashMap = null;
        this.mVariables = hashMap;
        this.mRows = hashMap;
        this.mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
        this.mNumColumns = 1;
        this.mNumRows = i;
        this.mMaxRows = this.TABLE_SIZE;
        this.mPoolVariables = new SolverVariable[POOL_SIZE];
        this.mPoolVariablesCount = i;
        this.tempClientsCopy = new ArrayRow[this.TABLE_SIZE];
        this.mRows = new ArrayRow[this.TABLE_SIZE];
        releaseRows();
        this.mCache = new Cache();
    }

    private void increaseTableSize() {
        this.TABLE_SIZE *= 2;
        this.mRows = (ArrayRow[]) Arrays.copyOf(this.mRows, this.TABLE_SIZE);
        this.mCache.mIndexedVariables = (SolverVariable[]) Arrays.copyOf(this.mCache.mIndexedVariables, this.TABLE_SIZE);
        this.mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
        this.mMaxColumns = this.TABLE_SIZE;
        this.mMaxRows = this.TABLE_SIZE;
        this.mGoal.variables.clear();
    }

    private void releaseRows() {
        for (int i = 0; i < this.mRows.length; i++) {
            Object obj = this.mRows[i];
            if (obj != null) {
                this.mCache.arrayRowPool.release(obj);
            }
            this.mRows[i] = null;
        }
    }

    public void reset() {
        int i;
        boolean z = false;
        for (i = z; i < this.mCache.mIndexedVariables.length; i++) {
            SolverVariable solverVariable = this.mCache.mIndexedVariables[i];
            if (solverVariable != null) {
                solverVariable.reset();
            }
        }
        this.mCache.solverVariablePool.releaseAll(this.mPoolVariables, this.mPoolVariablesCount);
        this.mPoolVariablesCount = z;
        Arrays.fill(this.mCache.mIndexedVariables, null);
        if (this.mVariables != null) {
            this.mVariables.clear();
        }
        this.mVariablesID = z;
        this.mGoal.variables.clear();
        this.mNumColumns = 1;
        for (i = z; i < this.mNumRows; i++) {
            this.mRows[i].used = z;
        }
        releaseRows();
        this.mNumRows = z;
    }

    public SolverVariable createObjectVariable(Object obj) {
        SolverVariable solverVariable = null;
        if (obj == null) {
            return solverVariable;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        if (obj instanceof ConstraintAnchor) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) obj;
            solverVariable = constraintAnchor.getSolverVariable();
            if (solverVariable == null) {
                constraintAnchor.resetSolverVariable(this.mCache);
                solverVariable = constraintAnchor.getSolverVariable();
            }
            int i = -1;
            if (solverVariable.id == i || solverVariable.id > this.mVariablesID || this.mCache.mIndexedVariables[solverVariable.id] == null) {
                if (solverVariable.id != i) {
                    solverVariable.reset();
                }
                this.mVariablesID++;
                this.mNumColumns++;
                solverVariable.id = this.mVariablesID;
                solverVariable.mType = Type.UNRESTRICTED;
                this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
            }
        }
        return solverVariable;
    }

    public ArrayRow createRow() {
        ArrayRow arrayRow = (ArrayRow) this.mCache.arrayRowPool.acquire();
        if (arrayRow == null) {
            return new ArrayRow(this.mCache);
        }
        arrayRow.reset();
        return arrayRow;
    }

    public SolverVariable createSlackVariable() {
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(Type.SLACK);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    private void addError(ArrayRow arrayRow) {
        arrayRow.addError(createErrorVariable(), createErrorVariable());
    }

    private void addSingleError(ArrayRow arrayRow, int i) {
        arrayRow.addSingleError(createErrorVariable(), i);
    }

    private SolverVariable createVariable(String str, Type type) {
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(type);
        acquireSolverVariable.setName(str);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        if (this.mVariables == null) {
            this.mVariables = new HashMap();
        }
        this.mVariables.put(str, acquireSolverVariable);
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    public SolverVariable createErrorVariable() {
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            increaseTableSize();
        }
        SolverVariable acquireSolverVariable = acquireSolverVariable(Type.ERROR);
        this.mVariablesID++;
        this.mNumColumns++;
        acquireSolverVariable.id = this.mVariablesID;
        this.mCache.mIndexedVariables[this.mVariablesID] = acquireSolverVariable;
        return acquireSolverVariable;
    }

    private SolverVariable acquireSolverVariable(Type type) {
        SolverVariable solverVariable = (SolverVariable) this.mCache.solverVariablePool.acquire();
        if (solverVariable == null) {
            solverVariable = new SolverVariable(type);
        } else {
            solverVariable.reset();
            solverVariable.setType(type);
        }
        if (this.mPoolVariablesCount >= POOL_SIZE) {
            POOL_SIZE *= 2;
            this.mPoolVariables = (SolverVariable[]) Arrays.copyOf(this.mPoolVariables, POOL_SIZE);
        }
        SolverVariable[] solverVariableArr = this.mPoolVariables;
        int i = this.mPoolVariablesCount;
        this.mPoolVariablesCount = i + 1;
        solverVariableArr[i] = solverVariable;
        return solverVariable;
    }

    Goal getGoal() {
        return this.mGoal;
    }

    ArrayRow getRow(int i) {
        return this.mRows[i];
    }

    float getValueFor(String str) {
        SolverVariable variable = getVariable(str, Type.UNRESTRICTED);
        if (variable == null) {
            return 0.0f;
        }
        return variable.computedValue;
    }

    public int getObjectVariableValue(Object obj) {
        SolverVariable solverVariable = ((ConstraintAnchor) obj).getSolverVariable();
        return solverVariable != null ? (int) (solverVariable.computedValue + 0.5f) : 0;
    }

    SolverVariable getVariable(String str, Type type) {
        if (this.mVariables == null) {
            this.mVariables = new HashMap();
        }
        SolverVariable solverVariable = (SolverVariable) this.mVariables.get(str);
        return solverVariable == null ? createVariable(str, type) : solverVariable;
    }

    void rebuildGoalFromErrors() {
        this.mGoal.updateFromSystem(this);
    }

    public void minimize() throws Exception {
        minimizeGoal(this.mGoal);
    }

    void minimizeGoal(Goal goal) throws Exception {
        goal.updateFromSystem(this);
        enforceBFS(goal);
        optimize(goal);
        computeValues();
    }

    private void updateRowFromVariables(ArrayRow arrayRow) {
        if (this.mNumRows > 0) {
            arrayRow.variables.updateFromSystem(arrayRow, this.mRows);
            if (arrayRow.variables.currentSize == 0) {
                arrayRow.isSimpleDefinition = true;
            }
        }
    }

    public void addConstraint(ArrayRow arrayRow) {
        if (arrayRow != null) {
            if (this.mNumRows + 1 >= this.mMaxRows || this.mNumColumns + 1 >= this.mMaxColumns) {
                increaseTableSize();
            }
            if (!arrayRow.isSimpleDefinition) {
                updateRowFromVariables(arrayRow);
                arrayRow.ensurePositiveConstant();
                arrayRow.pickRowVariable();
                if (!arrayRow.hasKeyVariable()) {
                    return;
                }
            }
            if (this.mRows[this.mNumRows] != null) {
                this.mCache.arrayRowPool.release(this.mRows[this.mNumRows]);
            }
            if (!arrayRow.isSimpleDefinition) {
                arrayRow.updateClientEquations();
            }
            this.mRows[this.mNumRows] = arrayRow;
            arrayRow.variable.definitionId = this.mNumRows;
            this.mNumRows++;
            int i = arrayRow.variable.mClientEquationsCount;
            if (i > 0) {
                while (this.tempClientsCopy.length < i) {
                    this.tempClientsCopy = new ArrayRow[(this.tempClientsCopy.length * 2)];
                }
                ArrayRow[] arrayRowArr = this.tempClientsCopy;
                int i2 = 0;
                for (int i3 = i2; i3 < i; i3++) {
                    arrayRowArr[i3] = arrayRow.variable.mClientEquations[i3];
                }
                while (i2 < i) {
                    ArrayRow arrayRow2 = arrayRowArr[i2];
                    if (arrayRow2 != arrayRow) {
                        arrayRow2.variables.updateFromRow(arrayRow2, arrayRow);
                        arrayRow2.updateClientEquations();
                    }
                    i2++;
                }
            }
        }
    }

    private int optimize(Goal goal) {
        boolean z = false;
        for (int i = z; i < this.mNumColumns; i++) {
            this.mAlreadyTestedCandidates[i] = z;
        }
        boolean z2 = z;
        int i2 = z2;
        int i3 = i2;
        while (!z2) {
            i2++;
            SolverVariable pivotCandidate = goal.getPivotCandidate();
            boolean z3 = true;
            if (pivotCandidate != null) {
                if (this.mAlreadyTestedCandidates[pivotCandidate.id]) {
                    pivotCandidate = null;
                } else {
                    this.mAlreadyTestedCandidates[pivotCandidate.id] = z3;
                    i3++;
                    if (i3 >= this.mNumColumns) {
                        z2 = z3;
                    }
                }
            }
            if (pivotCandidate != null) {
                int i4 = -1;
                float f = Float.MAX_VALUE;
                int i5 = i4;
                for (int i6 = z; i6 < this.mNumRows; i6++) {
                    ArrayRow arrayRow = this.mRows[i6];
                    if (arrayRow.variable.mType != Type.UNRESTRICTED && arrayRow.hasVariable(pivotCandidate)) {
                        float f2 = arrayRow.variables.get(pivotCandidate);
                        if (f2 < 0.0f) {
                            float f3 = (-arrayRow.constantValue) / f2;
                            if (f3 < f) {
                                i5 = i6;
                                f = f3;
                            }
                        }
                    }
                }
                if (i5 > i4) {
                    ArrayRow arrayRow2 = this.mRows[i5];
                    arrayRow2.variable.definitionId = i4;
                    arrayRow2.pivot(pivotCandidate);
                    arrayRow2.variable.definitionId = i5;
                    for (int i7 = z; i7 < this.mNumRows; i7++) {
                        this.mRows[i7].updateRowWithEquation(arrayRow2);
                    }
                    goal.updateFromSystem(this);
                    try {
                        enforceBFS(goal);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            z2 = z3;
        }
        return i2;
    }

    private int enforceBFS(Goal goal) throws Exception {
        float f;
        Object obj;
        int i;
        int i2;
        LinearSystem linearSystem = this;
        int i3 = 0;
        while (true) {
            f = 0.0f;
            if (i3 >= linearSystem.mNumRows) {
                obj = null;
                break;
            } else if (linearSystem.mRows[i3].variable.mType != Type.UNRESTRICTED && linearSystem.mRows[i3].constantValue < f) {
                obj = 1;
                break;
            } else {
                i3++;
            }
        }
        if (obj != null) {
            obj = null;
            i = 0;
            while (obj == null) {
                i++;
                int i4 = -1;
                float f2 = Float.MAX_VALUE;
                int i5 = i4;
                int i6 = i5;
                int i7 = 0;
                for (int i8 = 0; i8 < linearSystem.mNumRows; i8++) {
                    ArrayRow arrayRow = linearSystem.mRows[i8];
                    if (arrayRow.variable.mType != Type.UNRESTRICTED && arrayRow.constantValue < f) {
                        int i9 = i7;
                        float f3 = f2;
                        int i10 = i6;
                        i6 = i5;
                        for (i5 = 1; i5 < linearSystem.mNumColumns; i5++) {
                            SolverVariable solverVariable = linearSystem.mCache.mIndexedVariables[i5];
                            float f4 = arrayRow.variables.get(solverVariable);
                            if (f4 > f) {
                                i2 = i9;
                                float f5 = f3;
                                i7 = i10;
                                i10 = i6;
                                i6 = 0;
                                while (i6 < 6) {
                                    float f6 = solverVariable.strengthVector[i6] / f4;
                                    if ((f6 < f5 && i6 == i2) || i6 > i2) {
                                        f5 = f6;
                                        i10 = i8;
                                        i7 = i5;
                                        i2 = i6;
                                    }
                                    i6++;
                                }
                                i6 = i10;
                                i10 = i7;
                                f3 = f5;
                                i9 = i2;
                            }
                        }
                        i5 = i6;
                        i6 = i10;
                        f2 = f3;
                        i7 = i9;
                    }
                }
                if (i5 != i4) {
                    ArrayRow arrayRow2 = linearSystem.mRows[i5];
                    arrayRow2.variable.definitionId = i4;
                    arrayRow2.pivot(linearSystem.mCache.mIndexedVariables[i6]);
                    arrayRow2.variable.definitionId = i5;
                    for (int i11 = 0; i11 < linearSystem.mNumRows; i11++) {
                        linearSystem.mRows[i11].updateRowWithEquation(arrayRow2);
                    }
                    goal.updateFromSystem(linearSystem);
                } else {
                    Goal goal2 = goal;
                    obj = 1;
                }
            }
        } else {
            i = 0;
        }
        i2 = 0;
        while (i2 < linearSystem.mNumRows && (linearSystem.mRows[i2].variable.mType == Type.UNRESTRICTED || linearSystem.mRows[i2].constantValue >= f)) {
            i2++;
        }
        return i;
    }

    private void computeValues() {
        for (int i = 0; i < this.mNumRows; i++) {
            ArrayRow arrayRow = this.mRows[i];
            arrayRow.variable.computedValue = arrayRow.constantValue;
        }
    }

    private void displayRows() {
        displaySolverVariables();
        String str = "";
        for (int i = 0; i < this.mNumRows; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.mRows[i]);
            str = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("\n");
            str = stringBuilder.toString();
        }
        if (this.mGoal.variables.size() != 0) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(this.mGoal);
            stringBuilder2.append("\n");
            str = stringBuilder2.toString();
        }
        System.out.println(str);
    }

    void displayReadableRows() {
        displaySolverVariables();
        String str = "";
        for (int i = 0; i < this.mNumRows; i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(this.mRows[i].toReadableString());
            str = stringBuilder.toString();
            stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("\n");
            str = stringBuilder.toString();
        }
        if (this.mGoal != null) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(this.mGoal);
            stringBuilder2.append("\n");
            str = stringBuilder2.toString();
        }
        System.out.println(str);
    }

    public void displayVariablesReadableRows() {
        displaySolverVariables();
        String str = "";
        for (int i = 0; i < this.mNumRows; i++) {
            if (this.mRows[i].variable.mType == Type.UNRESTRICTED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(this.mRows[i].toReadableString());
                str = stringBuilder.toString();
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append("\n");
                str = stringBuilder.toString();
            }
        }
        if (this.mGoal.variables.size() != 0) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str);
            stringBuilder2.append(this.mGoal);
            stringBuilder2.append("\n");
            str = stringBuilder2.toString();
        }
        System.out.println(str);
    }

    public int getMemoryUsed() {
        int i = 0;
        int i2 = i;
        while (i < this.mNumRows) {
            if (this.mRows[i] != null) {
                i2 += this.mRows[i].sizeInBytes();
            }
            i++;
        }
        return i2;
    }

    public int getNumEquations() {
        return this.mNumRows;
    }

    public int getNumVariables() {
        return this.mVariablesID;
    }

    void displaySystemInformations() {
        int i = 0;
        int i2 = i;
        int i3 = i2;
        while (i2 < this.TABLE_SIZE) {
            if (this.mRows[i2] != null) {
                i3 += this.mRows[i2].sizeInBytes();
            }
            i2++;
        }
        i2 = i;
        int i4 = i2;
        while (i2 < this.mNumRows) {
            if (this.mRows[i2] != null) {
                i4 += this.mRows[i2].sizeInBytes();
            }
            i2++;
        }
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Linear System -> Table size: ");
        stringBuilder.append(this.TABLE_SIZE);
        stringBuilder.append(" (");
        stringBuilder.append(getDisplaySize(this.TABLE_SIZE * this.TABLE_SIZE));
        stringBuilder.append(") -- row sizes: ");
        stringBuilder.append(getDisplaySize(i3));
        stringBuilder.append(", actual size: ");
        stringBuilder.append(getDisplaySize(i4));
        stringBuilder.append(" rows: ");
        stringBuilder.append(this.mNumRows);
        stringBuilder.append("/");
        stringBuilder.append(this.mMaxRows);
        stringBuilder.append(" cols: ");
        stringBuilder.append(this.mNumColumns);
        stringBuilder.append("/");
        stringBuilder.append(this.mMaxColumns);
        stringBuilder.append(" ");
        stringBuilder.append(i);
        stringBuilder.append(" occupied cells, ");
        stringBuilder.append(getDisplaySize(i));
        printStream.println(stringBuilder.toString());
    }

    private void displaySolverVariables() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Display Rows (");
        stringBuilder.append(this.mNumRows);
        stringBuilder.append("x");
        stringBuilder.append(this.mNumColumns);
        stringBuilder.append(") :\n\t | C | ");
        String stringBuilder2 = stringBuilder.toString();
        for (int i = 1; i <= this.mNumColumns; i++) {
            Object obj = this.mCache.mIndexedVariables[i];
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(stringBuilder2);
            stringBuilder3.append(obj);
            stringBuilder2 = stringBuilder3.toString();
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append(stringBuilder2);
            stringBuilder4.append(" | ");
            stringBuilder2 = stringBuilder4.toString();
        }
        StringBuilder stringBuilder5 = new StringBuilder();
        stringBuilder5.append(stringBuilder2);
        stringBuilder5.append("\n");
        System.out.println(stringBuilder5.toString());
    }

    private String getDisplaySize(int i) {
        i *= 4;
        int i2 = i / 1024;
        int i3 = i2 / 1024;
        StringBuilder stringBuilder;
        if (i3 > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i3);
            stringBuilder.append(" Mb");
            return stringBuilder.toString();
        } else if (i2 > 0) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(i2);
            stringBuilder.append(" Kb");
            return stringBuilder.toString();
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("");
            stringBuilder2.append(i);
            stringBuilder2.append(" bytes");
            return stringBuilder2.toString();
        }
    }

    public Cache getCache() {
        return this.mCache;
    }

    public void addGreaterThan(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = i2;
        createRow.createRowGreaterThan(solverVariable, solverVariable2, createSlackVariable, i);
        addConstraint(createRow);
    }

    public void addLowerThan(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        SolverVariable createSlackVariable = createSlackVariable();
        createSlackVariable.strength = i2;
        createRow.createRowLowerThan(solverVariable, solverVariable2, createSlackVariable, i);
        addConstraint(createRow);
    }

    public void addCentering(SolverVariable solverVariable, SolverVariable solverVariable2, int i, float f, SolverVariable solverVariable3, SolverVariable solverVariable4, int i2, int i3) {
        int i4 = i3;
        ArrayRow createRow = createRow();
        createRow.createRowCentering(solverVariable, solverVariable2, i, f, solverVariable3, solverVariable4, i2);
        SolverVariable createErrorVariable = createErrorVariable();
        SolverVariable createErrorVariable2 = createErrorVariable();
        createErrorVariable.strength = i4;
        createErrorVariable2.strength = i4;
        createRow.addError(createErrorVariable, createErrorVariable2);
        addConstraint(createRow);
    }

    public ArrayRow addEquality(SolverVariable solverVariable, SolverVariable solverVariable2, int i, int i2) {
        ArrayRow createRow = createRow();
        createRow.createRowEquals(solverVariable, solverVariable2, i);
        solverVariable = createErrorVariable();
        solverVariable2 = createErrorVariable();
        solverVariable.strength = i2;
        solverVariable2.strength = i2;
        createRow.addError(solverVariable, solverVariable2);
        addConstraint(createRow);
        return createRow;
    }

    public void addEquality(SolverVariable solverVariable, int i) {
        int i2 = solverVariable.definitionId;
        ArrayRow arrayRow;
        if (solverVariable.definitionId != -1) {
            arrayRow = this.mRows[i2];
            if (arrayRow.isSimpleDefinition) {
                arrayRow.constantValue = (float) i;
                return;
            }
            arrayRow = createRow();
            arrayRow.createRowEquals(solverVariable, i);
            addConstraint(arrayRow);
            return;
        }
        arrayRow = createRow();
        arrayRow.createRowDefinition(solverVariable, i);
        addConstraint(arrayRow);
    }

    public static ArrayRow createRowEquals(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowEquals(solverVariable, solverVariable2, i);
        if (z) {
            linearSystem.addSingleError(createRow, 1);
        }
        return createRow;
    }

    public static ArrayRow createRowDimensionPercent(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, SolverVariable solverVariable3, float f, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        if (z) {
            linearSystem.addError(createRow);
        }
        return createRow.createRowDimensionPercent(solverVariable, solverVariable2, solverVariable3, f);
    }

    public static ArrayRow createRowGreaterThan(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        SolverVariable createSlackVariable = linearSystem.createSlackVariable();
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowGreaterThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (z) {
            linearSystem.addSingleError(createRow, (int) (-1.0f * createRow.variables.get(createSlackVariable)));
        }
        return createRow;
    }

    public static ArrayRow createRowLowerThan(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, boolean z) {
        SolverVariable createSlackVariable = linearSystem.createSlackVariable();
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowLowerThan(solverVariable, solverVariable2, createSlackVariable, i);
        if (z) {
            linearSystem.addSingleError(createRow, (int) (-1.0f * createRow.variables.get(createSlackVariable)));
        }
        return createRow;
    }

    public static ArrayRow createRowCentering(LinearSystem linearSystem, SolverVariable solverVariable, SolverVariable solverVariable2, int i, float f, SolverVariable solverVariable3, SolverVariable solverVariable4, int i2, boolean z) {
        ArrayRow createRow = linearSystem.createRow();
        createRow.createRowCentering(solverVariable, solverVariable2, i, f, solverVariable3, solverVariable4, i2);
        if (z) {
            SolverVariable createErrorVariable = linearSystem.createErrorVariable();
            SolverVariable createErrorVariable2 = linearSystem.createErrorVariable();
            int i3 = 4;
            createErrorVariable.strength = i3;
            createErrorVariable2.strength = i3;
            createRow.addError(createErrorVariable, createErrorVariable2);
        }
        return createRow;
    }
}
