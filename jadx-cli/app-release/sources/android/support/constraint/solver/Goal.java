package android.support.constraint.solver;

import android.support.constraint.solver.SolverVariable.Type;
import java.util.ArrayList;

public class Goal {
    ArrayList<SolverVariable> variables = new ArrayList();

    SolverVariable getPivotCandidate() {
        int size = this.variables.size();
        int i = 0;
        SolverVariable solverVariable = null;
        int i2 = i;
        SolverVariable solverVariable2 = solverVariable;
        while (i < size) {
            SolverVariable solverVariable3 = (SolverVariable) this.variables.get(i);
            int i3 = 5;
            while (i3 >= 0) {
                float f = solverVariable3.strengthVector[i3];
                float f2 = 0.0f;
                if (solverVariable2 == null && f < f2 && i3 >= r4) {
                    solverVariable2 = solverVariable3;
                    i2 = i3;
                }
                if (f > f2 && i3 > i2) {
                    solverVariable2 = solverVariable;
                    i2 = i3;
                }
                i3--;
            }
            i++;
        }
        return solverVariable2;
    }

    private void initFromSystemErrors(LinearSystem linearSystem) {
        this.variables.clear();
        for (int i = 1; i < linearSystem.mNumColumns; i++) {
            SolverVariable solverVariable = linearSystem.mCache.mIndexedVariables[i];
            for (int i2 = 0; i2 < 6; i2++) {
                solverVariable.strengthVector[i2] = 0.0f;
            }
            solverVariable.strengthVector[solverVariable.strength] = 1.0f;
            if (solverVariable.mType == Type.ERROR) {
                this.variables.add(solverVariable);
            }
        }
    }

    void updateFromSystem(LinearSystem linearSystem) {
        initFromSystemErrors(linearSystem);
        int size = this.variables.size();
        int i = 0;
        for (int i2 = i; i2 < size; i2++) {
            SolverVariable solverVariable = (SolverVariable) this.variables.get(i2);
            if (solverVariable.definitionId != -1) {
                ArrayLinkedVariables arrayLinkedVariables = linearSystem.getRow(solverVariable.definitionId).variables;
                int i3 = arrayLinkedVariables.currentSize;
                for (int i4 = i; i4 < i3; i4++) {
                    SolverVariable variable = arrayLinkedVariables.getVariable(i4);
                    if (variable != null) {
                        float variableValue = arrayLinkedVariables.getVariableValue(i4);
                        for (int i5 = i; i5 < 6; i5++) {
                            float[] fArr = variable.strengthVector;
                            fArr[i5] = fArr[i5] + (solverVariable.strengthVector[i5] * variableValue);
                        }
                        if (!this.variables.contains(variable)) {
                            this.variables.add(variable);
                        }
                    }
                }
                solverVariable.clearStrengths();
            }
        }
    }

    public String toString() {
        String str = "Goal: ";
        int size = this.variables.size();
        for (int i = 0; i < size; i++) {
            SolverVariable solverVariable = (SolverVariable) this.variables.get(i);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append(solverVariable.strengthsToString());
            str = stringBuilder.toString();
        }
        return str;
    }
}
