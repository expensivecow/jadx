package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.PopupWindow.OnDismissListener;

abstract class MenuPopup implements ShowableListMenu, MenuPresenter, OnItemClickListener {
    private Rect mEpicenterBounds;

    public abstract void addMenu(MenuBuilder menuBuilder);

    protected boolean closeMenuOnSubMenuOpened() {
        return true;
    }

    public boolean collapseItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    public boolean expandItemActionView(MenuBuilder menuBuilder, MenuItemImpl menuItemImpl) {
        return false;
    }

    public int getId() {
        return 0;
    }

    public void initForMenu(@NonNull Context context, @Nullable MenuBuilder menuBuilder) {
    }

    public abstract void setAnchorView(View view);

    public abstract void setForceShowIcon(boolean z);

    public abstract void setGravity(int i);

    public abstract void setHorizontalOffset(int i);

    public abstract void setOnDismissListener(OnDismissListener onDismissListener);

    public abstract void setShowTitle(boolean z);

    public abstract void setVerticalOffset(int i);

    MenuPopup() {
    }

    public void setEpicenterBounds(Rect rect) {
        this.mEpicenterBounds = rect;
    }

    public Rect getEpicenterBounds() {
        return this.mEpicenterBounds;
    }

    public MenuView getMenuView(ViewGroup viewGroup) {
        throw new UnsupportedOperationException("MenuPopups manage their own views");
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ListAdapter listAdapter = (ListAdapter) adapterView.getAdapter();
        toMenuAdapter(listAdapter).mAdapterMenu.performItemAction((MenuItem) listAdapter.getItem(i), this, closeMenuOnSubMenuOpened() ? 0 : 4);
    }

    protected static int measureIndividualMenuWidth(ListAdapter listAdapter, ViewGroup viewGroup, Context context, int i) {
        int i2 = 0;
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(i2, i2);
        int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(i2, i2);
        int count = listAdapter.getCount();
        View view = null;
        ViewGroup viewGroup2 = viewGroup;
        int i3 = i2;
        int i4 = i3;
        View view2 = view;
        while (i2 < count) {
            int itemViewType = listAdapter.getItemViewType(i2);
            if (itemViewType != i3) {
                view2 = view;
                i3 = itemViewType;
            }
            if (viewGroup2 == null) {
                viewGroup2 = new FrameLayout(context);
            }
            view2 = listAdapter.getView(i2, view2, viewGroup2);
            view2.measure(makeMeasureSpec, makeMeasureSpec2);
            itemViewType = view2.getMeasuredWidth();
            if (itemViewType >= i) {
                return i;
            }
            if (itemViewType > i4) {
                i4 = itemViewType;
            }
            i2++;
        }
        return i4;
    }

    protected static MenuAdapter toMenuAdapter(ListAdapter listAdapter) {
        if (listAdapter instanceof HeaderViewListAdapter) {
            return (MenuAdapter) ((HeaderViewListAdapter) listAdapter).getWrappedAdapter();
        }
        return (MenuAdapter) listAdapter;
    }

    protected static boolean shouldPreserveIconSpacing(MenuBuilder menuBuilder) {
        int size = menuBuilder.size();
        boolean z = false;
        for (int i = z; i < size; i++) {
            MenuItem item = menuBuilder.getItem(i);
            if (item.isVisible() && item.getIcon() != null) {
                return true;
            }
        }
        return z;
    }
}
