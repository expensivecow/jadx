package android.support.v4.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class ContentLoadingProgressBar extends ProgressBar {
    private static final int MIN_DELAY = 500;
    private static final int MIN_SHOW_TIME = 500;
    private final Runnable mDelayedHide;
    private final Runnable mDelayedShow;
    boolean mDismissed;
    boolean mPostedHide;
    boolean mPostedShow;
    long mStartTime;

    public ContentLoadingProgressBar(Context context) {
        this(context, null);
    }

    public ContentLoadingProgressBar(Context context, AttributeSet attributeSet) {
        boolean z = false;
        super(context, attributeSet, z);
        this.mStartTime = -1;
        this.mPostedHide = z;
        this.mPostedShow = z;
        this.mDismissed = z;
        this.mDelayedHide = new Runnable() {
            public void run() {
                ContentLoadingProgressBar.this.mPostedHide = false;
                ContentLoadingProgressBar.this.mStartTime = -1;
                ContentLoadingProgressBar.this.setVisibility(8);
            }
        };
        this.mDelayedShow = new Runnable() {
            public void run() {
                boolean z = false;
                ContentLoadingProgressBar.this.mPostedShow = z;
                if (!ContentLoadingProgressBar.this.mDismissed) {
                    ContentLoadingProgressBar.this.mStartTime = System.currentTimeMillis();
                    ContentLoadingProgressBar.this.setVisibility(z);
                }
            }
        };
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        removeCallbacks();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks();
    }

    private void removeCallbacks() {
        removeCallbacks(this.mDelayedHide);
        removeCallbacks(this.mDelayedShow);
    }

    public void hide() {
        boolean z = true;
        this.mDismissed = z;
        removeCallbacks(this.mDelayedShow);
        long currentTimeMillis = System.currentTimeMillis() - this.mStartTime;
        long j = 500;
        if (currentTimeMillis >= j || this.mStartTime == -1) {
            setVisibility(8);
        } else if (!this.mPostedHide) {
            postDelayed(this.mDelayedHide, j - currentTimeMillis);
            this.mPostedHide = z;
        }
    }

    public void show() {
        this.mStartTime = -1;
        this.mDismissed = false;
        removeCallbacks(this.mDelayedHide);
        if (!this.mPostedShow) {
            postDelayed(this.mDelayedShow, 500);
            this.mPostedShow = true;
        }
    }
}
