package android.support.v4.widget;

import android.widget.ListView;

public class ListViewAutoScrollHelper extends AutoScrollHelper {
    private final ListView mTarget;

    public boolean canTargetScrollHorizontally(int i) {
        return false;
    }

    public ListViewAutoScrollHelper(ListView listView) {
        super(listView);
        this.mTarget = listView;
    }

    public void scrollTargetBy(int i, int i2) {
        ListViewCompat.scrollListBy(this.mTarget, i2);
    }

    public boolean canTargetScrollVertically(int i) {
        ListView listView = this.mTarget;
        int count = listView.getCount();
        boolean z = false;
        if (count == 0) {
            return z;
        }
        int childCount = listView.getChildCount();
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int i2 = firstVisiblePosition + childCount;
        boolean z2 = true;
        if (i > 0) {
            if (i2 >= count && listView.getChildAt(childCount - z2).getBottom() <= listView.getHeight()) {
                return z;
            }
        } else if (i < 0) {
            return (firstVisiblePosition > 0 || listView.getChildAt(z).getTop() < 0) ? z2 : z;
        } else {
            return z;
        }
    }
}
