package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor.Type;
import java.util.ArrayList;

public class Guideline extends ConstraintWidget {
    public static final int HORIZONTAL = 0;
    public static final int RELATIVE_BEGIN = 1;
    public static final int RELATIVE_END = 2;
    public static final int RELATIVE_PERCENT = 0;
    public static final int RELATIVE_UNKNWON = -1;
    public static final int VERTICAL = 1;
    private ConstraintAnchor mAnchor;
    private Rectangle mHead;
    private int mHeadSize;
    private boolean mIsPositionRelaxed;
    private int mMinimumPosition;
    private int mOrientation;
    protected int mRelativeBegin;
    protected int mRelativeEnd;
    protected float mRelativePercent = -1.0f;

    public String getType() {
        return "Guideline";
    }

    public Guideline() {
        int i = -1;
        this.mRelativeBegin = i;
        this.mRelativeEnd = i;
        this.mAnchor = this.mTop;
        boolean z = false;
        this.mOrientation = z;
        this.mIsPositionRelaxed = z;
        this.mMinimumPosition = z;
        this.mHead = new Rectangle();
        this.mHeadSize = 8;
        this.mAnchors.clear();
        this.mAnchors.add(this.mAnchor);
    }

    public int getRelativeBehaviour() {
        if (this.mRelativePercent != -1.0f) {
            return 0;
        }
        int i = -1;
        if (this.mRelativeBegin != i) {
            return 1;
        }
        return this.mRelativeEnd != i ? 2 : i;
    }

    public Rectangle getHead() {
        int i = 2;
        this.mHead.setBounds(getDrawX() - this.mHeadSize, getDrawY() - (this.mHeadSize * i), this.mHeadSize * i, this.mHeadSize * i);
        if (getOrientation() == 0) {
            this.mHead.setBounds(getDrawX() - (this.mHeadSize * i), getDrawY() - this.mHeadSize, this.mHeadSize * i, i * this.mHeadSize);
        }
        return this.mHead;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            this.mAnchors.clear();
            if (this.mOrientation == 1) {
                this.mAnchor = this.mLeft;
            } else {
                this.mAnchor = this.mTop;
            }
            this.mAnchors.add(this.mAnchor);
        }
    }

    public ConstraintAnchor getAnchor() {
        return this.mAnchor;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setMinimumPosition(int i) {
        this.mMinimumPosition = i;
    }

    public void setPositionRelaxed(boolean z) {
        if (this.mIsPositionRelaxed != z) {
            this.mIsPositionRelaxed = z;
        }
    }

    public ConstraintAnchor getAnchor(Type type) {
        switch (type) {
            case LEFT:
            case RIGHT:
                if (this.mOrientation == 1) {
                    return this.mAnchor;
                }
                break;
            case TOP:
            case BOTTOM:
                if (this.mOrientation == 0) {
                    return this.mAnchor;
                }
                break;
        }
        return null;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setGuidePercent(int i) {
        setGuidePercent(((float) i) / 100.0f);
    }

    public void setGuidePercent(float f) {
        if (f > -1.0f) {
            this.mRelativePercent = f;
            int i = -1;
            this.mRelativeBegin = i;
            this.mRelativeEnd = i;
        }
    }

    public void setGuideBegin(int i) {
        int i2 = -1;
        if (i > i2) {
            this.mRelativePercent = -1.0f;
            this.mRelativeBegin = i;
            this.mRelativeEnd = i2;
        }
    }

    public void setGuideEnd(int i) {
        int i2 = -1;
        if (i > i2) {
            this.mRelativePercent = -1.0f;
            this.mRelativeBegin = i2;
            this.mRelativeEnd = i;
        }
    }

    public float getRelativePercent() {
        return this.mRelativePercent;
    }

    public int getRelativeBegin() {
        return this.mRelativeBegin;
    }

    public int getRelativeEnd() {
        return this.mRelativeEnd;
    }

    public void addToSolver(LinearSystem linearSystem, int i) {
        ConstraintWidgetContainer constraintWidgetContainer = (ConstraintWidgetContainer) getParent();
        if (constraintWidgetContainer != null) {
            Object anchor = constraintWidgetContainer.getAnchor(Type.LEFT);
            Object anchor2 = constraintWidgetContainer.getAnchor(Type.RIGHT);
            if (this.mOrientation == 0) {
                anchor = constraintWidgetContainer.getAnchor(Type.TOP);
                anchor2 = constraintWidgetContainer.getAnchor(Type.BOTTOM);
            }
            boolean z = false;
            int i2 = -1;
            if (this.mRelativeBegin != i2) {
                linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, linearSystem.createObjectVariable(this.mAnchor), linearSystem.createObjectVariable(anchor), this.mRelativeBegin, z));
            } else if (this.mRelativeEnd != i2) {
                linearSystem.addConstraint(LinearSystem.createRowEquals(linearSystem, linearSystem.createObjectVariable(this.mAnchor), linearSystem.createObjectVariable(anchor2), -this.mRelativeEnd, z));
            } else if (this.mRelativePercent != -1.0f) {
                linearSystem.addConstraint(LinearSystem.createRowDimensionPercent(linearSystem, linearSystem.createObjectVariable(this.mAnchor), linearSystem.createObjectVariable(anchor), linearSystem.createObjectVariable(anchor2), this.mRelativePercent, this.mIsPositionRelaxed));
            }
        }
    }

    public void updateFromSolver(LinearSystem linearSystem, int i) {
        if (getParent() != null) {
            int objectVariableValue = linearSystem.getObjectVariableValue(this.mAnchor);
            int i2 = 0;
            if (this.mOrientation == 1) {
                setX(objectVariableValue);
                setY(i2);
                setHeight(getParent().getHeight());
                setWidth(i2);
            } else {
                setX(i2);
                setY(objectVariableValue);
                setWidth(getParent().getWidth());
                setHeight(i2);
            }
        }
    }

    public void setDrawOrigin(int i, int i2) {
        float f = -1.0f;
        int i3 = -1;
        if (this.mOrientation == 1) {
            i -= this.mOffsetX;
            if (this.mRelativeBegin != i3) {
                setGuideBegin(i);
                return;
            } else if (this.mRelativeEnd != i3) {
                setGuideEnd(getParent().getWidth() - i);
                return;
            } else if (this.mRelativePercent != f) {
                setGuidePercent(((float) i) / ((float) getParent().getWidth()));
                return;
            } else {
                return;
            }
        }
        i2 -= this.mOffsetY;
        if (this.mRelativeBegin != i3) {
            setGuideBegin(i2);
        } else if (this.mRelativeEnd != i3) {
            setGuideEnd(getParent().getHeight() - i2);
        } else if (this.mRelativePercent != f) {
            setGuidePercent(((float) i2) / ((float) getParent().getHeight()));
        }
    }

    void inferRelativePercentPosition() {
        float x = ((float) getX()) / ((float) getParent().getWidth());
        if (this.mOrientation == 0) {
            x = ((float) getY()) / ((float) getParent().getHeight());
        }
        setGuidePercent(x);
    }

    void inferRelativeBeginPosition() {
        int x = getX();
        if (this.mOrientation == 0) {
            x = getY();
        }
        setGuideBegin(x);
    }

    void inferRelativeEndPosition() {
        int width = getParent().getWidth() - getX();
        if (this.mOrientation == 0) {
            width = getParent().getHeight() - getY();
        }
        setGuideEnd(width);
    }

    public void cyclePosition() {
        int i = -1;
        if (this.mRelativeBegin != i) {
            inferRelativePercentPosition();
        } else if (this.mRelativePercent != -1.0f) {
            inferRelativeEndPosition();
        } else if (this.mRelativeEnd != i) {
            inferRelativeBeginPosition();
        }
    }
}
