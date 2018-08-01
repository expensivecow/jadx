package android.support.v7.widget;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.appcompat.R;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

@RestrictTo({Scope.LIBRARY_GROUP})
class TooltipPopup {
    private static final String TAG = "TooltipPopup";
    private final View mContentView;
    private final Context mContext;
    private final LayoutParams mLayoutParams = new LayoutParams();
    private final TextView mMessageView;
    private final int[] mTmpAnchorPos;
    private final int[] mTmpAppPos;
    private final Rect mTmpDisplayFrame = new Rect();

    TooltipPopup(Context context) {
        int i = 2;
        this.mTmpAnchorPos = new int[i];
        this.mTmpAppPos = new int[i];
        this.mContext = context;
        this.mContentView = LayoutInflater.from(this.mContext).inflate(R.layout.tooltip, null);
        this.mMessageView = (TextView) this.mContentView.findViewById(R.id.message);
        this.mLayoutParams.setTitle(getClass().getSimpleName());
        this.mLayoutParams.packageName = this.mContext.getPackageName();
        this.mLayoutParams.type = 1002;
        i = -2;
        this.mLayoutParams.width = i;
        this.mLayoutParams.height = i;
        this.mLayoutParams.format = -3;
        this.mLayoutParams.windowAnimations = R.style.Animation_AppCompat_Tooltip;
        this.mLayoutParams.flags = 24;
    }

    void show(View view, int i, int i2, boolean z, CharSequence charSequence) {
        if (isShowing()) {
            hide();
        }
        this.mMessageView.setText(charSequence);
        computePosition(view, i, i2, z, this.mLayoutParams);
        ((WindowManager) this.mContext.getSystemService("window")).addView(this.mContentView, this.mLayoutParams);
    }

    void hide() {
        if (isShowing()) {
            ((WindowManager) this.mContext.getSystemService("window")).removeView(this.mContentView);
        }
    }

    boolean isShowing() {
        return this.mContentView.getParent() != null;
    }

    private void computePosition(View view, int i, int i2, boolean z, LayoutParams layoutParams) {
        int i3;
        int dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_threshold);
        if (view.getWidth() < dimensionPixelOffset) {
            i = view.getWidth() / 2;
        }
        int i4 = 0;
        if (view.getHeight() >= dimensionPixelOffset) {
            dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(R.dimen.tooltip_precise_anchor_extra_offset);
            i3 = i2 + dimensionPixelOffset;
            i2 -= dimensionPixelOffset;
        } else {
            i3 = view.getHeight();
            i2 = i4;
        }
        layoutParams.gravity = 49;
        dimensionPixelOffset = this.mContext.getResources().getDimensionPixelOffset(z ? R.dimen.tooltip_y_offset_touch : R.dimen.tooltip_y_offset_non_touch);
        View appRootView = getAppRootView(view);
        if (appRootView == null) {
            Log.e("TooltipPopup", "Cannot find app view");
            return;
        }
        appRootView.getWindowVisibleDisplayFrame(this.mTmpDisplayFrame);
        if (this.mTmpDisplayFrame.left < 0 && this.mTmpDisplayFrame.top < 0) {
            Resources resources = this.mContext.getResources();
            int identifier = resources.getIdentifier("status_bar_height", "dimen", "android");
            identifier = identifier != 0 ? resources.getDimensionPixelSize(identifier) : i4;
            DisplayMetrics displayMetrics = resources.getDisplayMetrics();
            this.mTmpDisplayFrame.set(i4, identifier, displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        appRootView.getLocationOnScreen(this.mTmpAppPos);
        view.getLocationOnScreen(this.mTmpAnchorPos);
        int[] iArr = this.mTmpAnchorPos;
        iArr[i4] = iArr[i4] - this.mTmpAppPos[i4];
        iArr = this.mTmpAnchorPos;
        int i5 = 1;
        iArr[i5] = iArr[i5] - this.mTmpAppPos[i5];
        layoutParams.x = (this.mTmpAnchorPos[i4] + i) - (this.mTmpDisplayFrame.width() / 2);
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i4, i4);
        this.mContentView.measure(makeMeasureSpec, makeMeasureSpec);
        makeMeasureSpec = this.mContentView.getMeasuredHeight();
        i = ((this.mTmpAnchorPos[i5] + i2) - dimensionPixelOffset) - makeMeasureSpec;
        i2 = (this.mTmpAnchorPos[i5] + i3) + dimensionPixelOffset;
        if (z) {
            if (i >= 0) {
                layoutParams.y = i;
            } else {
                layoutParams.y = i2;
            }
        } else if (makeMeasureSpec + i2 <= this.mTmpDisplayFrame.height()) {
            layoutParams.y = i2;
        } else {
            layoutParams.y = i;
        }
    }

    private static View getAppRootView(View view) {
        for (Context context = view.getContext(); context instanceof ContextWrapper; context = ((ContextWrapper) context).getBaseContext()) {
            if (context instanceof Activity) {
                return ((Activity) context).getWindow().getDecorView();
            }
        }
        return view.getRootView();
    }
}
