package android.support.constraint.solver;

public class Cache {
    Pool<ArrayRow> arrayRowPool;
    SolverVariable[] mIndexedVariables = new SolverVariable[32];
    Pool<SolverVariable> solverVariablePool;

    public Cache() {
        int i = 256;
        this.arrayRowPool = new SimplePool(i);
        this.solverVariablePool = new SimplePool(i);
    }
}
