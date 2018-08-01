package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuBuilder.ItemInvoker;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewDebug.ExportedProperty;
import android.view.accessibility.AccessibilityEvent;

public class ActionMenuView extends LinearLayoutCompat implements ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    @RestrictTo({Scope.LIBRARY_GROUP})
    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    private static class ActionMenuPresenterCallback implements Callback {
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            return false;
        }

        ActionMenuPresenterCallback() {
        }
    }

    public static class LayoutParams extends android.support.v7.widget.LinearLayoutCompat.LayoutParams {
        @ExportedProperty
        public int cellsUsed;
        @ExportedProperty
        public boolean expandable;
        boolean expanded;
        @ExportedProperty
        public int extraPixels;
        @ExportedProperty
        public boolean isOverflowButton;
        @ExportedProperty
        public boolean preventEdgeOffset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((android.view.ViewGroup.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }
    }

    private class MenuBuilderCallback implements MenuBuilder.Callback {
        MenuBuilderCallback() {
        }

        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }

        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public int getWindowAnimations() {
        return 0;
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        boolean z = false;
        setBaselineAligned(z);
        float f = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * f);
        this.mGeneratedItemPadding = (int) (4.0f * f);
        this.mPopupContext = context;
        this.mPopupTheme = z;
    }

    public void setPopupTheme(@StyleRes int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = getContext();
            } else {
                this.mPopupContext = new ContextThemeWrapper(getContext(), i);
            }
        }
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
        this.mPresenter.setMenuView(this);
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    protected void onMeasure(int i, int i2) {
        boolean z = this.mFormatItems;
        boolean z2 = true;
        boolean z3 = false;
        this.mFormatItems = MeasureSpec.getMode(i) == 1073741824 ? z2 : z3;
        if (z != this.mFormatItems) {
            this.mFormatItemsWidth = z3;
        }
        int size = MeasureSpec.getSize(i);
        if (!(!this.mFormatItems || this.mMenu == null || size == this.mFormatItemsWidth)) {
            this.mFormatItemsWidth = size;
            this.mMenu.onItemsChanged(z2);
        }
        size = getChildCount();
        if (!this.mFormatItems || size <= 0) {
            for (int i3 = z3; i3 < size; i3++) {
                LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
                layoutParams.rightMargin = z3;
                layoutParams.leftMargin = z3;
            }
            super.onMeasure(i, i2);
            return;
        }
        onMeasureExactFormat(i, i2);
    }

    private void onMeasureExactFormat(int i, int i2) {
        int mode = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i);
        int size2 = MeasureSpec.getSize(i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int childMeasureSpec = getChildMeasureSpec(i2, paddingTop, -2);
        size -= paddingLeft;
        paddingLeft = size / this.mMinCellSize;
        int i3 = size % this.mMinCellSize;
        int i4 = 0;
        if (paddingLeft == 0) {
            setMeasuredDimension(size, i4);
            return;
        }
        int i5;
        int i6;
        boolean z;
        long j;
        int i7;
        int i8;
        int i9;
        boolean z2;
        int i10 = r0.mMinCellSize + (i3 / paddingLeft);
        i3 = getChildCount();
        int i11 = paddingLeft;
        paddingLeft = i4;
        int i12 = paddingLeft;
        int i13 = i12;
        int i14 = i13;
        int i15 = i14;
        int i16 = i15;
        long j2 = 0;
        while (paddingLeft < i3) {
            View childAt = getChildAt(paddingLeft);
            i5 = size2;
            if (childAt.getVisibility() == 8) {
                i6 = size;
            } else {
                int i17;
                boolean z3;
                boolean z4 = childAt instanceof ActionMenuItemView;
                i14++;
                if (z4) {
                    i17 = i14;
                    i6 = size;
                    z3 = false;
                    childAt.setPadding(r0.mGeneratedItemPadding, z3, r0.mGeneratedItemPadding, z3);
                } else {
                    i6 = size;
                    i17 = i14;
                    z3 = false;
                }
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                layoutParams.expanded = z3;
                layoutParams.extraPixels = z3;
                layoutParams.cellsUsed = z3;
                layoutParams.expandable = z3;
                layoutParams.leftMargin = z3;
                layoutParams.rightMargin = z3;
                z3 = z4 && ((ActionMenuItemView) childAt).hasText();
                layoutParams.preventEdgeOffset = z3;
                size = measureChildForCells(childAt, i10, layoutParams.isOverflowButton ? 1 : i11, childMeasureSpec, paddingTop);
                size2 = Math.max(i15, size);
                if (layoutParams.expandable) {
                    i16++;
                }
                if (layoutParams.isOverflowButton) {
                    i13 = 1;
                }
                i11 -= size;
                i12 = Math.max(i12, childAt.getMeasuredHeight());
                i4 = 1;
                if (size == i4) {
                    i14 = i17;
                    i15 = size2;
                    j2 |= (long) (i4 << paddingLeft);
                } else {
                    i14 = i17;
                    i15 = size2;
                }
            }
            paddingLeft++;
            size2 = i5;
            size = i6;
            Object obj = null;
        }
        i6 = size;
        i5 = size2;
        Object obj2 = (i13 == 0 || i14 != 2) ? null : 1;
        boolean z5 = false;
        while (i16 > 0 && i11 > 0) {
            Object obj3;
            int i18 = Integer.MAX_VALUE;
            paddingTop = 0;
            i4 = 0;
            long j3 = 0;
            while (paddingTop < i3) {
                int i19;
                LayoutParams layoutParams2 = (LayoutParams) getChildAt(paddingTop).getLayoutParams();
                z = z5;
                if (!layoutParams2.expandable) {
                    i19 = paddingTop;
                } else if (layoutParams2.cellsUsed < i18) {
                    i19 = paddingTop;
                    i18 = layoutParams2.cellsUsed;
                    j3 = (long) (1 << paddingTop);
                    i4 = 1;
                } else {
                    i19 = paddingTop;
                    if (layoutParams2.cellsUsed == i18) {
                        i4++;
                        j3 |= (long) (1 << i19);
                    }
                }
                paddingTop = i19 + 1;
                z5 = z;
                obj3 = 2;
            }
            z = z5;
            j = j2 | j3;
            if (i4 > i11) {
                i7 = childMeasureSpec;
                i8 = i3;
                i9 = i12;
                break;
            }
            i18++;
            j2 = j;
            size = 0;
            while (size < i3) {
                View childAt2 = getChildAt(size);
                LayoutParams layoutParams3 = (LayoutParams) childAt2.getLayoutParams();
                i9 = i12;
                i7 = childMeasureSpec;
                i8 = i3;
                long j4 = (long) (1 << size);
                if ((j3 & j4) != 0) {
                    boolean z6;
                    if (obj2 == null || !layoutParams3.preventEdgeOffset) {
                        z6 = true;
                    } else {
                        z6 = true;
                        if (i11 == z6) {
                            i12 = 0;
                            childAt2.setPadding(r0.mGeneratedItemPadding + i10, i12, r0.mGeneratedItemPadding, i12);
                        }
                    }
                    layoutParams3.cellsUsed += z6;
                    layoutParams3.expanded = z6;
                    i11--;
                } else if (layoutParams3.cellsUsed == i18) {
                    j2 |= j4;
                }
                size++;
                i12 = i9;
                childMeasureSpec = i7;
                i3 = i8;
            }
            obj3 = 2;
            z5 = true;
        }
        z = z5;
        i7 = childMeasureSpec;
        i8 = i3;
        i9 = i12;
        j = j2;
        if (i13 == 0) {
            size = 1;
            if (i14 == size) {
                size2 = size;
                if (i11 > 0 || j == 0 || (i11 >= i14 - r2 && size2 == 0 && i15 <= r2)) {
                    size = i8;
                    size2 = 0;
                    z2 = z;
                } else {
                    float bitCount = (float) Long.bitCount(j);
                    if (size2 == 0) {
                        float f = 0.5f;
                        if ((j & 1) != 0) {
                            size2 = 0;
                            if (!((LayoutParams) getChildAt(size2).getLayoutParams()).preventEdgeOffset) {
                                bitCount -= f;
                            }
                        } else {
                            size2 = 0;
                        }
                        i3 = i8 - 1;
                        if (!((j & ((long) (1 << i3))) == 0 || ((LayoutParams) getChildAt(i3).getLayoutParams()).preventEdgeOffset)) {
                            bitCount -= f;
                        }
                    } else {
                        size2 = 0;
                    }
                    i4 = bitCount > 0.0f ? (int) (((float) (i11 * i10)) / bitCount) : size2;
                    z2 = z;
                    size = i8;
                    for (childMeasureSpec = size2; childMeasureSpec < size; childMeasureSpec++) {
                        Object obj4;
                        Object obj5;
                        if ((j & ((long) (1 << childMeasureSpec))) == 0) {
                            obj4 = 1;
                            obj5 = 2;
                        } else {
                            View childAt3 = getChildAt(childMeasureSpec);
                            LayoutParams layoutParams4 = (LayoutParams) childAt3.getLayoutParams();
                            if (childAt3 instanceof ActionMenuItemView) {
                                layoutParams4.extraPixels = i4;
                                layoutParams4.expanded = true;
                                if (childMeasureSpec != 0 || layoutParams4.preventEdgeOffset) {
                                    obj5 = 2;
                                } else {
                                    layoutParams4.leftMargin = (-i4) / 2;
                                }
                                obj4 = 1;
                                z2 = true;
                            } else {
                                i11 = 2;
                                if (layoutParams4.isOverflowButton) {
                                    layoutParams4.extraPixels = i4;
                                    boolean z7 = true;
                                    layoutParams4.expanded = z7;
                                    layoutParams4.rightMargin = (-i4) / i11;
                                    z2 = z7;
                                } else {
                                    obj4 = 1;
                                    if (childMeasureSpec != 0) {
                                        layoutParams4.leftMargin = i4 / 2;
                                    }
                                    if (childMeasureSpec != size - 1) {
                                        layoutParams4.rightMargin = i4 / 2;
                                    }
                                }
                            }
                        }
                    }
                }
                paddingLeft = 1073741824;
                if (z2) {
                    while (size2 < size) {
                        View childAt4 = getChildAt(size2);
                        LayoutParams layoutParams5 = (LayoutParams) childAt4.getLayoutParams();
                        if (layoutParams5.expanded) {
                            i3 = i7;
                            childAt4.measure(MeasureSpec.makeMeasureSpec((layoutParams5.cellsUsed * i10) + layoutParams5.extraPixels, paddingLeft), i3);
                        } else {
                            i3 = i7;
                        }
                        size2++;
                        i7 = i3;
                    }
                }
                if (mode == paddingLeft) {
                    size = i6;
                    mode = i9;
                } else {
                    mode = i5;
                    size = i6;
                }
                setMeasuredDimension(size, mode);
            }
        }
        size = 1;
        size2 = 0;
        if (i11 > 0) {
        }
        size = i8;
        size2 = 0;
        z2 = z;
        paddingLeft = 1073741824;
        if (z2) {
            while (size2 < size) {
                View childAt42 = getChildAt(size2);
                LayoutParams layoutParams52 = (LayoutParams) childAt42.getLayoutParams();
                if (layoutParams52.expanded) {
                    i3 = i7;
                    childAt42.measure(MeasureSpec.makeMeasureSpec((layoutParams52.cellsUsed * i10) + layoutParams52.extraPixels, paddingLeft), i3);
                } else {
                    i3 = i7;
                }
                size2++;
                i7 = i3;
            }
        }
        if (mode == paddingLeft) {
            mode = i5;
            size = i6;
        } else {
            size = i6;
            mode = i9;
        }
        setMeasuredDimension(size, mode);
    }

    static int measureChildForCells(View view, int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        i3 = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i3) - i4, MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z = false;
        boolean z2 = true;
        boolean z3 = (actionMenuItemView == null || !actionMenuItemView.hasText()) ? z : z2;
        int i5 = 2;
        if (i2 <= 0 || (z3 && i2 < i5)) {
            i5 = z;
        } else {
            view.measure(MeasureSpec.makeMeasureSpec(i2 * i, Integer.MIN_VALUE), i3);
            i2 = view.getMeasuredWidth();
            int i6 = i2 / i;
            if (i2 % i != 0) {
                i6++;
            }
            if (!z3 || i6 >= i5) {
                i5 = i6;
            }
        }
        if (!layoutParams.isOverflowButton && z3) {
            z = z2;
        }
        layoutParams.expandable = z;
        layoutParams.cellsUsed = i5;
        view.measure(MeasureSpec.makeMeasureSpec(i * i5, 1073741824), i3);
        return i5;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mFormatItems) {
            int i5;
            int i6;
            int i7;
            int childCount = getChildCount();
            int i8 = (i4 - i2) / 2;
            int dividerWidth = getDividerWidth();
            int i9 = i3 - i;
            int paddingRight = (i9 - getPaddingRight()) - getPaddingLeft();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int i10 = paddingRight;
            paddingRight = 0;
            int i11 = 0;
            int i12 = 0;
            while (true) {
                i5 = 8;
                i6 = 1;
                if (paddingRight >= childCount) {
                    break;
                }
                View childAt = getChildAt(paddingRight);
                if (childAt.getVisibility() != i5) {
                    LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                    if (layoutParams.isOverflowButton) {
                        int paddingLeft;
                        i11 = childAt.getMeasuredWidth();
                        if (hasSupportDividerBeforeChildAt(paddingRight)) {
                            i11 += dividerWidth;
                        }
                        int measuredHeight = childAt.getMeasuredHeight();
                        if (isLayoutRtl) {
                            paddingLeft = getPaddingLeft() + layoutParams.leftMargin;
                            i5 = paddingLeft + i11;
                        } else {
                            i5 = (getWidth() - getPaddingRight()) - layoutParams.rightMargin;
                            paddingLeft = i5 - i11;
                        }
                        i7 = i8 - (measuredHeight / 2);
                        childAt.layout(paddingLeft, i7, i5, measuredHeight + i7);
                        i10 -= i11;
                        i11 = i6;
                    } else {
                        i10 -= (childAt.getMeasuredWidth() + layoutParams.leftMargin) + layoutParams.rightMargin;
                        boolean hasSupportDividerBeforeChildAt = hasSupportDividerBeforeChildAt(paddingRight);
                        i12++;
                    }
                }
                paddingRight++;
            }
            if (childCount == i6 && i11 == 0) {
                View childAt2 = getChildAt(0);
                dividerWidth = childAt2.getMeasuredWidth();
                paddingRight = childAt2.getMeasuredHeight();
                i9 = (i9 / 2) - (dividerWidth / 2);
                i8 -= paddingRight / 2;
                childAt2.layout(i9, i8, dividerWidth + i9, paddingRight + i8);
                return;
            }
            i12 -= i11 ^ 1;
            if (i12 > 0) {
                i7 = i10 / i12;
                dividerWidth = 0;
            } else {
                dividerWidth = 0;
                i7 = 0;
            }
            i9 = Math.max(dividerWidth, i7);
            View childAt3;
            LayoutParams layoutParams2;
            if (isLayoutRtl) {
                paddingRight = getWidth() - getPaddingRight();
                while (dividerWidth < childCount) {
                    childAt3 = getChildAt(dividerWidth);
                    layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                    if (!(childAt3.getVisibility() == i5 || layoutParams2.isOverflowButton)) {
                        paddingRight -= layoutParams2.rightMargin;
                        i11 = childAt3.getMeasuredWidth();
                        i12 = childAt3.getMeasuredHeight();
                        i10 = i8 - (i12 / 2);
                        childAt3.layout(paddingRight - i11, i10, paddingRight, i12 + i10);
                        paddingRight -= (i11 + layoutParams2.leftMargin) + i9;
                    }
                    dividerWidth++;
                }
            } else {
                paddingRight = getPaddingLeft();
                while (dividerWidth < childCount) {
                    childAt3 = getChildAt(dividerWidth);
                    layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                    if (!(childAt3.getVisibility() == i5 || layoutParams2.isOverflowButton)) {
                        paddingRight += layoutParams2.leftMargin;
                        i11 = childAt3.getMeasuredWidth();
                        i12 = childAt3.getMeasuredHeight();
                        i10 = i8 - (i12 / 2);
                        childAt3.layout(paddingRight, i10, paddingRight + i11, i12 + i10);
                        paddingRight += (i11 + layoutParams2.rightMargin) + i9;
                    }
                    dividerWidth++;
                }
            }
            return;
        }
        super.onLayout(z, i, i2, i3, i4);
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    public void setOverflowIcon(@Nullable Drawable drawable) {
        getMenu();
        this.mPresenter.setOverflowIcon(drawable);
    }

    @Nullable
    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    protected LayoutParams generateDefaultLayoutParams() {
        int i = -2;
        LayoutParams layoutParams = new LayoutParams(i, i);
        layoutParams.gravity = 16;
        return layoutParams;
    }

    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams layoutParams2 = layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : new LayoutParams(layoutParams);
        if (layoutParams2.gravity <= 0) {
            layoutParams2.gravity = 16;
        }
        return layoutParams2;
    }

    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams generateDefaultLayoutParams = generateDefaultLayoutParams();
        generateDefaultLayoutParams.isOverflowButton = true;
        return generateDefaultLayoutParams;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean invokeItem(MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction(menuItemImpl, 0);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback());
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setMenuCallbacks(Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    protected boolean hasSupportDividerBeforeChildAt(int i) {
        boolean z = false;
        if (i == 0) {
            return z;
        }
        View childAt = getChildAt(i - 1);
        View childAt2 = getChildAt(i);
        if (i < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            z |= ((ActionMenuChildView) childAt).needsDividerAfter();
        }
        if (i > 0 && (childAt2 instanceof ActionMenuChildView)) {
            z |= ((ActionMenuChildView) childAt2).needsDividerBefore();
        }
        return z;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setExpandedActionViewsExclusive(boolean z) {
        this.mPresenter.setExpandedActionViewsExclusive(z);
    }
}
