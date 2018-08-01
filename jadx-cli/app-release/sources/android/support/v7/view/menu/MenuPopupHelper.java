package android.support.v7.view.menu;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuPresenter.Callback;
import android.view.View;
import android.widget.PopupWindow.OnDismissListener;

@RestrictTo({Scope.LIBRARY_GROUP})
public class MenuPopupHelper implements MenuHelper {
    private static final int TOUCH_EPICENTER_SIZE_DP = 48;
    private View mAnchorView;
    private final Context mContext;
    private int mDropDownGravity;
    private boolean mForceShowIcon;
    private final OnDismissListener mInternalOnDismissListener;
    private final MenuBuilder mMenu;
    private OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private MenuPopup mPopup;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private Callback mPresenterCallback;

    public MenuPopupHelper(@NonNull Context context, @NonNull MenuBuilder menuBuilder) {
        Context context2 = context;
        MenuBuilder menuBuilder2 = menuBuilder;
        this(context2, menuBuilder2, null, false, R.attr.popupMenuStyle, 0);
    }

    public MenuPopupHelper(@NonNull Context context, @NonNull MenuBuilder menuBuilder, @NonNull View view) {
        Context context2 = context;
        MenuBuilder menuBuilder2 = menuBuilder;
        View view2 = view;
        this(context2, menuBuilder2, view2, false, R.attr.popupMenuStyle, 0);
    }

    public MenuPopupHelper(@NonNull Context context, @NonNull MenuBuilder menuBuilder, @NonNull View view, boolean z, @AttrRes int i) {
        this(context, menuBuilder, view, z, i, 0);
    }

    public MenuPopupHelper(@NonNull Context context, @NonNull MenuBuilder menuBuilder, @NonNull View view, boolean z, @AttrRes int i, @StyleRes int i2) {
        this.mDropDownGravity = 8388611;
        this.mInternalOnDismissListener = new OnDismissListener() {
            public void onDismiss() {
                MenuPopupHelper.this.onDismiss();
            }
        };
        this.mContext = context;
        this.mMenu = menuBuilder;
        this.mAnchorView = view;
        this.mOverflowOnly = z;
        this.mPopupStyleAttr = i;
        this.mPopupStyleRes = i2;
    }

    public void setOnDismissListener(@Nullable OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    public void setAnchorView(@NonNull View view) {
        this.mAnchorView = view;
    }

    public void setForceShowIcon(boolean z) {
        this.mForceShowIcon = z;
        if (this.mPopup != null) {
            this.mPopup.setForceShowIcon(z);
        }
    }

    public void setGravity(int i) {
        this.mDropDownGravity = i;
    }

    public int getGravity() {
        return this.mDropDownGravity;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public void show(int i, int i2) {
        if (!tryShow(i, i2)) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    @NonNull
    public MenuPopup getPopup() {
        if (this.mPopup == null) {
            this.mPopup = createPopup();
        }
        return this.mPopup;
    }

    public boolean tryShow() {
        boolean z = true;
        if (isShowing()) {
            return z;
        }
        boolean z2 = false;
        if (this.mAnchorView == null) {
            return z2;
        }
        showPopup(z2, z2, z2, z2);
        return z;
    }

    public boolean tryShow(int i, int i2) {
        boolean z = true;
        if (isShowing()) {
            return z;
        }
        if (this.mAnchorView == null) {
            return false;
        }
        showPopup(i, i2, z, z);
        return z;
    }

    @android.support.annotation.NonNull
    private android.support.v7.view.menu.MenuPopup createPopup() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r0_9 android.support.v7.view.menu.MenuPopup) in PHI: PHI: (r0_11 android.support.v7.view.menu.MenuPopup) = (r0_9 android.support.v7.view.menu.MenuPopup), (r0_10 android.support.v7.view.menu.MenuPopup) binds: {(r0_9 android.support.v7.view.menu.MenuPopup)=B:9:0x003b, (r0_10 android.support.v7.view.menu.MenuPopup)=B:10:0x004c}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:78)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.core.ProcessClass.lambda$0(ProcessClass.java:51)
	at java.lang.Iterable.forEach(Iterable.java:75)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
        /*
        r14 = this;
        r0 = r14.mContext;
        r1 = "window";
        r0 = r0.getSystemService(r1);
        r0 = (android.view.WindowManager) r0;
        r0 = r0.getDefaultDisplay();
        r1 = new android.graphics.Point;
        r1.<init>();
        r2 = android.os.Build.VERSION.SDK_INT;
        r3 = 17;
        if (r2 < r3) goto L_0x001d;
    L_0x0019:
        r0.getRealSize(r1);
        goto L_0x0020;
    L_0x001d:
        r0.getSize(r1);
    L_0x0020:
        r0 = r1.x;
        r1 = r1.y;
        r0 = java.lang.Math.min(r0, r1);
        r1 = r14.mContext;
        r1 = r1.getResources();
        r2 = android.support.v7.appcompat.R.dimen.abc_cascading_menus_min_smallest_width;
        r1 = r1.getDimensionPixelSize(r2);
        if (r0 < r1) goto L_0x0038;
    L_0x0036:
        r0 = 1;
        goto L_0x0039;
    L_0x0038:
        r0 = 0;
    L_0x0039:
        if (r0 == 0) goto L_0x004c;
    L_0x003b:
        r0 = new android.support.v7.view.menu.CascadingMenuPopup;
        r2 = r14.mContext;
        r3 = r14.mAnchorView;
        r4 = r14.mPopupStyleAttr;
        r5 = r14.mPopupStyleRes;
        r6 = r14.mOverflowOnly;
        r1 = r0;
        r1.<init>(r2, r3, r4, r5, r6);
        goto L_0x005e;
    L_0x004c:
        r0 = new android.support.v7.view.menu.StandardMenuPopup;
        r8 = r14.mContext;
        r9 = r14.mMenu;
        r10 = r14.mAnchorView;
        r11 = r14.mPopupStyleAttr;
        r12 = r14.mPopupStyleRes;
        r13 = r14.mOverflowOnly;
        r7 = r0;
        r7.<init>(r8, r9, r10, r11, r12, r13);
    L_0x005e:
        r1 = r14.mMenu;
        r0.addMenu(r1);
        r1 = r14.mInternalOnDismissListener;
        r0.setOnDismissListener(r1);
        r1 = r14.mAnchorView;
        r0.setAnchorView(r1);
        r1 = r14.mPresenterCallback;
        r0.setCallback(r1);
        r1 = r14.mForceShowIcon;
        r0.setForceShowIcon(r1);
        r1 = r14.mDropDownGravity;
        r0.setGravity(r1);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.view.menu.MenuPopupHelper.createPopup():android.support.v7.view.menu.MenuPopup");
    }

    private void showPopup(int i, int i2, boolean z, boolean z2) {
        MenuPopup popup = getPopup();
        popup.setShowTitle(z2);
        if (z) {
            if ((GravityCompat.getAbsoluteGravity(this.mDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView)) & 7) == 5) {
                i += this.mAnchorView.getWidth();
            }
            popup.setHorizontalOffset(i);
            popup.setVerticalOffset(i2);
            int i3 = (int) ((48.0f * this.mContext.getResources().getDisplayMetrics().density) / 2.0f);
            popup.setEpicenterBounds(new Rect(i - i3, i2 - i3, i + i3, i2 + i3));
        }
        popup.show();
    }

    public void dismiss() {
        if (isShowing()) {
            this.mPopup.dismiss();
        }
    }

    protected void onDismiss() {
        this.mPopup = null;
        if (this.mOnDismissListener != null) {
            this.mOnDismissListener.onDismiss();
        }
    }

    public boolean isShowing() {
        return this.mPopup != null && this.mPopup.isShowing();
    }

    public void setPresenterCallback(@Nullable Callback callback) {
        this.mPresenterCallback = callback;
        if (this.mPopup != null) {
            this.mPopup.setCallback(callback);
        }
    }
}
