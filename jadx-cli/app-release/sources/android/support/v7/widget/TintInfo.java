package android.support.v7.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;

class TintInfo {
    public boolean mHasTintList;
    public boolean mHasTintMode;
    public ColorStateList mTintList;
    public Mode mTintMode;

    TintInfo() {
    }

    void clear() {
        Object obj = null;
        this.mTintList = obj;
        boolean z = false;
        this.mHasTintList = z;
        this.mTintMode = obj;
        this.mHasTintMode = z;
    }
}
