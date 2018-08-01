package android.support.v7.widget;

class RtlSpacingHelper {
    public static final int UNDEFINED = Integer.MIN_VALUE;
    private int mEnd;
    private int mExplicitLeft;
    private int mExplicitRight;
    private boolean mIsRelative;
    private boolean mIsRtl;
    private int mLeft;
    private int mRight;
    private int mStart;

    RtlSpacingHelper() {
        boolean z = false;
        this.mLeft = z;
        this.mRight = z;
        int i = Integer.MIN_VALUE;
        this.mStart = i;
        this.mEnd = i;
        this.mExplicitLeft = z;
        this.mExplicitRight = z;
        this.mIsRtl = z;
        this.mIsRelative = z;
    }

    public int getLeft() {
        return this.mLeft;
    }

    public int getRight() {
        return this.mRight;
    }

    public int getStart() {
        return this.mIsRtl ? this.mRight : this.mLeft;
    }

    public int getEnd() {
        return this.mIsRtl ? this.mLeft : this.mRight;
    }

    public void setRelative(int i, int i2) {
        this.mStart = i;
        this.mEnd = i2;
        this.mIsRelative = true;
        int i3 = Integer.MIN_VALUE;
        if (this.mIsRtl) {
            if (i2 != i3) {
                this.mLeft = i2;
            }
            if (i != i3) {
                this.mRight = i;
                return;
            }
            return;
        }
        if (i != i3) {
            this.mLeft = i;
        }
        if (i2 != i3) {
            this.mRight = i2;
        }
    }

    public void setAbsolute(int i, int i2) {
        this.mIsRelative = false;
        int i3 = Integer.MIN_VALUE;
        if (i != i3) {
            this.mExplicitLeft = i;
            this.mLeft = i;
        }
        if (i2 != i3) {
            this.mExplicitRight = i2;
            this.mRight = i2;
        }
    }

    public void setDirection(boolean z) {
        if (z != this.mIsRtl) {
            this.mIsRtl = z;
            if (this.mIsRelative) {
                int i = Integer.MIN_VALUE;
                if (z) {
                    this.mLeft = this.mEnd != i ? this.mEnd : this.mExplicitLeft;
                    this.mRight = this.mStart != i ? this.mStart : this.mExplicitRight;
                } else {
                    this.mLeft = this.mStart != i ? this.mStart : this.mExplicitLeft;
                    this.mRight = this.mEnd != i ? this.mEnd : this.mExplicitRight;
                }
            } else {
                this.mLeft = this.mExplicitLeft;
                this.mRight = this.mExplicitRight;
            }
        }
    }
}
