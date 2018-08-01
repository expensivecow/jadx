package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.SolverVariable;
import java.util.ArrayList;
import java.util.HashSet;

public class ConstraintAnchor {
    private static final boolean ALLOW_BINARY = false;
    public static final int ANY_GROUP = Integer.MAX_VALUE;
    public static final int APPLY_GROUP_RESULTS = -2;
    public static final int AUTO_CONSTRAINT_CREATOR = 2;
    public static final int SCOUT_CREATOR = 1;
    private static final int UNSET_GONE_MARGIN = -1;
    public static final int USER_CREATOR = 0;
    public static final boolean USE_CENTER_ANCHOR = false;
    private int mConnectionCreator;
    private ConnectionType mConnectionType = ConnectionType.RELAXED;
    int mGoneMargin = -1;
    int mGroup;
    public int mMargin;
    final ConstraintWidget mOwner;
    SolverVariable mSolverVariable;
    private Strength mStrength = Strength.NONE;
    ConstraintAnchor mTarget;
    final Type mType;

    public enum ConnectionType {
        RELAXED,
        STRICT
    }

    public enum Strength {
        NONE,
        STRONG,
        WEAK
    }

    public enum Type {
        NONE,
        LEFT,
        TOP,
        RIGHT,
        BOTTOM,
        BASELINE,
        CENTER,
        CENTER_X,
        CENTER_Y
    }

    public ConstraintAnchor(ConstraintWidget constraintWidget, Type type) {
        int i = 0;
        this.mMargin = i;
        this.mConnectionCreator = i;
        this.mGroup = Integer.MAX_VALUE;
        this.mOwner = constraintWidget;
        this.mType = type;
    }

    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }

    public void resetSolverVariable(Cache cache) {
        if (this.mSolverVariable == null) {
            this.mSolverVariable = new SolverVariable(android.support.constraint.solver.SolverVariable.Type.UNRESTRICTED);
        } else {
            this.mSolverVariable.reset();
        }
    }

    public void setGroup(int i) {
        this.mGroup = i;
    }

    public int getGroup() {
        return this.mGroup;
    }

    public ConstraintWidget getOwner() {
        return this.mOwner;
    }

    public Type getType() {
        return this.mType;
    }

    public int getMargin() {
        int i = 8;
        if (this.mOwner.getVisibility() == i) {
            return 0;
        }
        if (this.mGoneMargin <= -1 || this.mTarget == null || this.mTarget.mOwner.getVisibility() != i) {
            return this.mMargin;
        }
        return this.mGoneMargin;
    }

    public Strength getStrength() {
        return this.mStrength;
    }

    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }

    public ConnectionType getConnectionType() {
        return this.mConnectionType;
    }

    public void setConnectionType(ConnectionType connectionType) {
        this.mConnectionType = connectionType;
    }

    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }

    public void setConnectionCreator(int i) {
        this.mConnectionCreator = i;
    }

    public void reset() {
        this.mTarget = null;
        int i = 0;
        this.mMargin = i;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = i;
        this.mConnectionType = ConnectionType.RELAXED;
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, Strength strength, int i2) {
        return connect(constraintAnchor, i, -1, strength, i2, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2, Strength strength, int i3, boolean z) {
        boolean z2 = true;
        boolean z3 = false;
        if (constraintAnchor == null) {
            this.mTarget = null;
            this.mMargin = z3;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return z2;
        } else if (!z && !isValidConnection(constraintAnchor)) {
            return z3;
        } else {
            this.mTarget = constraintAnchor;
            if (i > 0) {
                this.mMargin = i;
            } else {
                this.mMargin = z3;
            }
            this.mGoneMargin = i2;
            this.mStrength = strength;
            this.mConnectionCreator = i3;
            return z2;
        }
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i, int i2) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, i2, false);
    }

    public boolean connect(ConstraintAnchor constraintAnchor, int i) {
        return connect(constraintAnchor, i, -1, Strength.STRONG, 0, false);
    }

    public boolean isConnected() {
        return this.mTarget != null;
    }

    public boolean isValidConnection(ConstraintAnchor constraintAnchor) {
        boolean z = false;
        if (constraintAnchor == null) {
            return z;
        }
        Type type = constraintAnchor.getType();
        boolean z2 = true;
        if (type != this.mType) {
            boolean z3;
            switch (this.mType) {
                case CENTER:
                    if (!(type == Type.BASELINE || type == Type.CENTER_X || type == Type.CENTER_Y)) {
                        z = z2;
                    }
                    return z;
                case LEFT:
                case RIGHT:
                    z3 = (type == Type.LEFT || type == Type.RIGHT) ? z2 : z;
                    if (constraintAnchor.getOwner() instanceof Guideline) {
                        z3 = (z3 || type == Type.CENTER_X) ? z2 : z;
                    }
                    return z3;
                case TOP:
                case BOTTOM:
                    z3 = (type == Type.TOP || type == Type.BOTTOM) ? z2 : z;
                    if (constraintAnchor.getOwner() instanceof Guideline) {
                        z3 = (z3 || type == Type.CENTER_Y) ? z2 : z;
                    }
                    return z3;
                default:
                    return z;
            }
        } else if (this.mType == Type.CENTER) {
            return z;
        } else {
            return (this.mType != Type.BASELINE || (constraintAnchor.getOwner().hasBaseline() && getOwner().hasBaseline())) ? z2 : z;
        }
    }

    public boolean isSideAnchor() {
        switch (this.mType) {
            case LEFT:
            case RIGHT:
            case TOP:
            case BOTTOM:
                return true;
            default:
                return false;
        }
    }

    public boolean isSimilarDimensionConnection(ConstraintAnchor constraintAnchor) {
        Type type = constraintAnchor.getType();
        boolean z = true;
        if (type == this.mType) {
            return z;
        }
        boolean z2 = false;
        switch (this.mType) {
            case CENTER:
                if (type == Type.BASELINE) {
                    z = z2;
                }
                return z;
            case LEFT:
            case RIGHT:
            case CENTER_X:
                if (!(type == Type.LEFT || type == Type.RIGHT || type == Type.CENTER_X)) {
                    z = z2;
                }
                return z;
            case TOP:
            case BOTTOM:
            case CENTER_Y:
            case BASELINE:
                if (!(type == Type.TOP || type == Type.BOTTOM || type == Type.CENTER_Y || type == Type.BASELINE)) {
                    z = z2;
                }
                return z;
            default:
                return z2;
        }
    }

    public void setStrength(Strength strength) {
        if (isConnected()) {
            this.mStrength = strength;
        }
    }

    public void setMargin(int i) {
        if (isConnected()) {
            this.mMargin = i;
        }
    }

    public void setGoneMargin(int i) {
        if (isConnected()) {
            this.mGoneMargin = i;
        }
    }

    public boolean isVerticalAnchor() {
        int i = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[this.mType.ordinal()];
        if (i != 6) {
            switch (i) {
                case 1:
                case 2:
                case 3:
                    break;
                default:
                    return true;
            }
        }
        return false;
    }

    public String toString() {
        String stringBuilder;
        HashSet hashSet = new HashSet();
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.mOwner.getDebugName());
        stringBuilder2.append(":");
        stringBuilder2.append(this.mType.toString());
        if (this.mTarget != null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(" connected to ");
            stringBuilder3.append(this.mTarget.toString(hashSet));
            stringBuilder = stringBuilder3.toString();
        } else {
            stringBuilder = "";
        }
        stringBuilder2.append(stringBuilder);
        return stringBuilder2.toString();
    }

    private String toString(HashSet<ConstraintAnchor> hashSet) {
        if (!hashSet.add(this)) {
            return "<-";
        }
        String stringBuilder;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(this.mOwner.getDebugName());
        stringBuilder2.append(":");
        stringBuilder2.append(this.mType.toString());
        if (this.mTarget != null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(" connected to ");
            stringBuilder3.append(this.mTarget.toString(hashSet));
            stringBuilder = stringBuilder3.toString();
        } else {
            stringBuilder = "";
        }
        stringBuilder2.append(stringBuilder);
        return stringBuilder2.toString();
    }

    public int getSnapPriorityLevel() {
        int i = 1;
        int i2 = 0;
        switch (this.mType) {
            case CENTER:
                return 3;
            case LEFT:
                return i;
            case RIGHT:
                return i;
            case TOP:
                return i2;
            case BOTTOM:
                return i2;
            case CENTER_X:
                return i2;
            case CENTER_Y:
                return i;
            case BASELINE:
                return 2;
            default:
                return i2;
        }
    }

    public int getPriorityLevel() {
        int i = 0;
        int i2 = 2;
        switch (this.mType) {
            case CENTER:
                return i2;
            case LEFT:
                return i2;
            case RIGHT:
                return i2;
            case TOP:
                return i2;
            case BOTTOM:
                return i2;
            case CENTER_X:
                return i;
            case CENTER_Y:
                return i;
            case BASELINE:
                return 1;
            default:
                return i;
        }
    }

    public boolean isSnapCompatibleWith(ConstraintAnchor constraintAnchor) {
        boolean z = false;
        if (this.mType == Type.CENTER) {
            return z;
        }
        boolean z2 = true;
        if (this.mType == constraintAnchor.getType()) {
            return z2;
        }
        int i = 6;
        int i2 = 7;
        int i3;
        switch (this.mType) {
            case LEFT:
                i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return (i3 == 3 || i3 == i) ? z2 : z;
            case RIGHT:
                i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return (i3 == 2 || i3 == i) ? z2 : z;
            case TOP:
                i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return (i3 == 5 || i3 == i2) ? z2 : z;
            case BOTTOM:
                i3 = AnonymousClass1.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[constraintAnchor.getType().ordinal()];
                return (i3 == 4 || i3 == i2) ? z2 : z;
            case CENTER_X:
                switch (constraintAnchor.getType()) {
                    case LEFT:
                        return z2;
                    case RIGHT:
                        return z2;
                    default:
                        return z;
                }
            case CENTER_Y:
                switch (constraintAnchor.getType()) {
                    case TOP:
                        return z2;
                    case BOTTOM:
                        return z2;
                    default:
                        return z;
                }
            default:
                return z;
        }
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget, ConstraintAnchor constraintAnchor) {
        return isConnectionAllowed(constraintWidget);
    }

    public boolean isConnectionAllowed(ConstraintWidget constraintWidget) {
        boolean z = false;
        if (isConnectionToMe(constraintWidget, new HashSet())) {
            return z;
        }
        ConstraintWidget parent = getOwner().getParent();
        boolean z2 = true;
        return (parent == constraintWidget || constraintWidget.getParent() == parent) ? z2 : z;
    }

    private boolean isConnectionToMe(ConstraintWidget constraintWidget, HashSet<ConstraintWidget> hashSet) {
        boolean z = false;
        if (hashSet.contains(constraintWidget)) {
            return z;
        }
        hashSet.add(constraintWidget);
        boolean z2 = true;
        if (constraintWidget == getOwner()) {
            return z2;
        }
        ArrayList anchors = constraintWidget.getAnchors();
        int size = anchors.size();
        for (int i = z; i < size; i++) {
            ConstraintAnchor constraintAnchor = (ConstraintAnchor) anchors.get(i);
            if (constraintAnchor.isSimilarDimensionConnection(this) && constraintAnchor.isConnected() && isConnectionToMe(constraintAnchor.getTarget().getOwner(), hashSet)) {
                return z2;
            }
        }
        return z;
    }

    public final ConstraintAnchor getOpposite() {
        switch (this.mType) {
            case LEFT:
                return this.mOwner.mRight;
            case RIGHT:
                return this.mOwner.mLeft;
            case TOP:
                return this.mOwner.mBottom;
            case BOTTOM:
                return this.mOwner.mTop;
            default:
                return null;
        }
    }
}
