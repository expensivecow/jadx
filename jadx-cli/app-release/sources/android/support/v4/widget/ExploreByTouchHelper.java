package android.support.v4.widget;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewParentCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.FocusStrategy.BoundsAdapter;
import android.support.v4.widget.FocusStrategy.CollectionAdapter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityRecord;
import java.util.ArrayList;
import java.util.List;

public abstract class ExploreByTouchHelper extends AccessibilityDelegateCompat {
    private static final String DEFAULT_CLASS_NAME = "android.view.View";
    public static final int HOST_ID = -1;
    public static final int INVALID_ID = Integer.MIN_VALUE;
    private static final Rect INVALID_PARENT_BOUNDS;
    private static final BoundsAdapter<AccessibilityNodeInfoCompat> NODE_ADAPTER = new BoundsAdapter<AccessibilityNodeInfoCompat>() {
        public void obtainBounds(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat, Rect rect) {
            accessibilityNodeInfoCompat.getBoundsInParent(rect);
        }
    };
    private static final CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat> SPARSE_VALUES_ADAPTER = new CollectionAdapter<SparseArrayCompat<AccessibilityNodeInfoCompat>, AccessibilityNodeInfoCompat>() {
        public AccessibilityNodeInfoCompat get(SparseArrayCompat<AccessibilityNodeInfoCompat> sparseArrayCompat, int i) {
            return (AccessibilityNodeInfoCompat) sparseArrayCompat.valueAt(i);
        }

        public int size(SparseArrayCompat<AccessibilityNodeInfoCompat> sparseArrayCompat) {
            return sparseArrayCompat.size();
        }
    };
    private int mAccessibilityFocusedVirtualViewId;
    private final View mHost;
    private int mHoveredVirtualViewId;
    private int mKeyboardFocusedVirtualViewId;
    private final AccessibilityManager mManager;
    private MyNodeProvider mNodeProvider;
    private final int[] mTempGlobalRect = new int[2];
    private final Rect mTempParentRect = new Rect();
    private final Rect mTempScreenRect = new Rect();
    private final Rect mTempVisibleRect = new Rect();

    private class MyNodeProvider extends AccessibilityNodeProviderCompat {
        MyNodeProvider() {
        }

        public AccessibilityNodeInfoCompat createAccessibilityNodeInfo(int i) {
            return AccessibilityNodeInfoCompat.obtain(ExploreByTouchHelper.this.obtainAccessibilityNodeInfo(i));
        }

        public boolean performAction(int i, int i2, Bundle bundle) {
            return ExploreByTouchHelper.this.performAction(i, i2, bundle);
        }

        public AccessibilityNodeInfoCompat findFocus(int i) {
            i = i == 2 ? ExploreByTouchHelper.this.mAccessibilityFocusedVirtualViewId : ExploreByTouchHelper.this.mKeyboardFocusedVirtualViewId;
            if (i == Integer.MIN_VALUE) {
                return null;
            }
            return createAccessibilityNodeInfo(i);
        }
    }

    private static int keyToDirection(int i) {
        switch (i) {
            case 19:
                return 33;
            case 21:
                return 17;
            case 22:
                return 66;
            default:
                return 130;
        }
    }

    protected abstract int getVirtualViewAt(float f, float f2);

    protected abstract void getVisibleVirtualViews(List<Integer> list);

    protected abstract boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle);

    protected void onPopulateEventForHost(AccessibilityEvent accessibilityEvent) {
    }

    protected void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
    }

    protected void onPopulateNodeForHost(AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
    }

    protected abstract void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat);

    protected void onVirtualViewKeyboardFocusChanged(int i, boolean z) {
    }

    static {
        int i = Integer.MIN_VALUE;
        int i2 = Integer.MAX_VALUE;
        INVALID_PARENT_BOUNDS = new Rect(i2, i2, i, i);
    }

    public ExploreByTouchHelper(View view) {
        int i = Integer.MIN_VALUE;
        this.mAccessibilityFocusedVirtualViewId = i;
        this.mKeyboardFocusedVirtualViewId = i;
        this.mHoveredVirtualViewId = i;
        if (view == null) {
            throw new IllegalArgumentException("View may not be null");
        }
        this.mHost = view;
        this.mManager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
        boolean z = true;
        view.setFocusable(z);
        if (ViewCompat.getImportantForAccessibility(view) == 0) {
            ViewCompat.setImportantForAccessibility(view, z);
        }
    }

    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View view) {
        if (this.mNodeProvider == null) {
            this.mNodeProvider = new MyNodeProvider();
        }
        return this.mNodeProvider;
    }

    public final boolean dispatchHoverEvent(@NonNull MotionEvent motionEvent) {
        boolean z = false;
        if (!this.mManager.isEnabled() || !this.mManager.isTouchExplorationEnabled()) {
            return z;
        }
        int action = motionEvent.getAction();
        boolean z2 = true;
        int i = Integer.MIN_VALUE;
        if (action != 7) {
            switch (action) {
                case 9:
                    break;
                case 10:
                    if (this.mAccessibilityFocusedVirtualViewId == i) {
                        return z;
                    }
                    updateHoveredVirtualView(i);
                    return z2;
                default:
                    return z;
            }
        }
        int virtualViewAt = getVirtualViewAt(motionEvent.getX(), motionEvent.getY());
        updateHoveredVirtualView(virtualViewAt);
        if (virtualViewAt != i) {
            z = z2;
        }
        return z;
    }

    public final boolean dispatchKeyEvent(@NonNull KeyEvent keyEvent) {
        int i = 0;
        boolean z = true;
        if (keyEvent.getAction() == z) {
            return i;
        }
        int keyCode = keyEvent.getKeyCode();
        Rect rect = null;
        if (keyCode != 61) {
            if (keyCode != 66) {
                switch (keyCode) {
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                        if (!keyEvent.hasNoModifiers()) {
                            return i;
                        }
                        keyCode = keyToDirection(keyCode);
                        int repeatCount = keyEvent.getRepeatCount() + z;
                        boolean z2 = i;
                        while (i < repeatCount && moveFocus(keyCode, rect)) {
                            i++;
                            z2 = z;
                        }
                        return z2;
                    case 23:
                        break;
                    default:
                        return i;
                }
            }
            if (!keyEvent.hasNoModifiers() || keyEvent.getRepeatCount() != 0) {
                return i;
            }
            clickKeyboardFocusedVirtualView();
            return z;
        } else if (keyEvent.hasNoModifiers()) {
            return moveFocus(2, rect);
        } else {
            return keyEvent.hasModifiers(z) ? moveFocus(z, rect) : i;
        }
    }

    public final void onFocusChanged(boolean z, int i, @Nullable Rect rect) {
        if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
            clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
        }
        if (z) {
            moveFocus(i, rect);
        }
    }

    public final int getAccessibilityFocusedVirtualViewId() {
        return this.mAccessibilityFocusedVirtualViewId;
    }

    public final int getKeyboardFocusedVirtualViewId() {
        return this.mKeyboardFocusedVirtualViewId;
    }

    private void getBoundsInParent(int i, Rect rect) {
        obtainAccessibilityNodeInfo(i).getBoundsInParent(rect);
    }

    private boolean moveFocus(int i, @Nullable Rect rect) {
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat;
        Object obj;
        SparseArrayCompat allNodes = getAllNodes();
        int i2 = this.mKeyboardFocusedVirtualViewId;
        int i3 = Integer.MIN_VALUE;
        if (i2 == i3) {
            accessibilityNodeInfoCompat = null;
        } else {
            accessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat) allNodes.get(i2);
        }
        AccessibilityNodeInfoCompat accessibilityNodeInfoCompat2 = accessibilityNodeInfoCompat;
        if (i == 17 || i == 33 || i == 66 || i == 130) {
            Rect rect2 = new Rect();
            if (this.mKeyboardFocusedVirtualViewId != i3) {
                getBoundsInParent(this.mKeyboardFocusedVirtualViewId, rect2);
            } else if (rect != null) {
                rect2.set(rect);
            } else {
                guessPreviouslyFocusedRect(this.mHost, i, rect2);
            }
            obj = (AccessibilityNodeInfoCompat) FocusStrategy.findNextFocusInAbsoluteDirection(allNodes, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, accessibilityNodeInfoCompat2, rect2, i);
        } else {
            switch (i) {
                case 1:
                case 2:
                    boolean z = true;
                    obj = (AccessibilityNodeInfoCompat) FocusStrategy.findNextFocusInRelativeDirection(allNodes, SPARSE_VALUES_ADAPTER, NODE_ADAPTER, accessibilityNodeInfoCompat2, i, ViewCompat.getLayoutDirection(this.mHost) == z ? z : false, false);
                    break;
                default:
                    throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD, FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            }
        }
        if (obj != null) {
            i3 = allNodes.keyAt(allNodes.indexOfValue(obj));
        }
        return requestKeyboardFocusForVirtualView(i3);
    }

    private SparseArrayCompat<AccessibilityNodeInfoCompat> getAllNodes() {
        List arrayList = new ArrayList();
        getVisibleVirtualViews(arrayList);
        SparseArrayCompat<AccessibilityNodeInfoCompat> sparseArrayCompat = new SparseArrayCompat();
        for (int i = 0; i < arrayList.size(); i++) {
            sparseArrayCompat.put(i, createNodeForChild(i));
        }
        return sparseArrayCompat;
    }

    private static Rect guessPreviouslyFocusedRect(@NonNull View view, int i, @NonNull Rect rect) {
        int width = view.getWidth();
        int height = view.getHeight();
        int i2 = 0;
        if (i == 17) {
            rect.set(width, i2, width, height);
        } else if (i != 33) {
            int i3 = -1;
            if (i == 66) {
                rect.set(i3, i2, i3, height);
            } else if (i != 130) {
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
            } else {
                rect.set(i2, i3, width, i3);
            }
        } else {
            rect.set(i2, height, width, height);
        }
        return rect;
    }

    private boolean clickKeyboardFocusedVirtualView() {
        return this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE && onPerformActionForVirtualView(this.mKeyboardFocusedVirtualViewId, 16, null);
    }

    public final boolean sendEventForVirtualView(int i, int i2) {
        boolean z = false;
        if (i == Integer.MIN_VALUE || !this.mManager.isEnabled()) {
            return z;
        }
        ViewParent parent = this.mHost.getParent();
        if (parent == null) {
            return z;
        }
        return ViewParentCompat.requestSendAccessibilityEvent(parent, this.mHost, createEvent(i, i2));
    }

    public final void invalidateRoot() {
        invalidateVirtualView(-1, 1);
    }

    public final void invalidateVirtualView(int i) {
        invalidateVirtualView(i, 0);
    }

    public final void invalidateVirtualView(int i, int i2) {
        if (i != Integer.MIN_VALUE && this.mManager.isEnabled()) {
            ViewParent parent = this.mHost.getParent();
            if (parent != null) {
                AccessibilityEvent createEvent = createEvent(i, 2048);
                AccessibilityEventCompat.setContentChangeTypes(createEvent, i2);
                ViewParentCompat.requestSendAccessibilityEvent(parent, this.mHost, createEvent);
            }
        }
    }

    @Deprecated
    public int getFocusedVirtualView() {
        return getAccessibilityFocusedVirtualViewId();
    }

    private void updateHoveredVirtualView(int i) {
        if (this.mHoveredVirtualViewId != i) {
            int i2 = this.mHoveredVirtualViewId;
            this.mHoveredVirtualViewId = i;
            sendEventForVirtualView(i, 128);
            sendEventForVirtualView(i2, 256);
        }
    }

    private AccessibilityEvent createEvent(int i, int i2) {
        if (i != -1) {
            return createEventForChild(i, i2);
        }
        return createEventForHost(i2);
    }

    private AccessibilityEvent createEventForHost(int i) {
        AccessibilityEvent obtain = AccessibilityEvent.obtain(i);
        this.mHost.onInitializeAccessibilityEvent(obtain);
        return obtain;
    }

    public void onInitializeAccessibilityEvent(View view, AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(view, accessibilityEvent);
        onPopulateEventForHost(accessibilityEvent);
    }

    private AccessibilityEvent createEventForChild(int i, int i2) {
        AccessibilityRecord obtain = AccessibilityEvent.obtain(i2);
        AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo = obtainAccessibilityNodeInfo(i);
        obtain.getText().add(obtainAccessibilityNodeInfo.getText());
        obtain.setContentDescription(obtainAccessibilityNodeInfo.getContentDescription());
        obtain.setScrollable(obtainAccessibilityNodeInfo.isScrollable());
        obtain.setPassword(obtainAccessibilityNodeInfo.isPassword());
        obtain.setEnabled(obtainAccessibilityNodeInfo.isEnabled());
        obtain.setChecked(obtainAccessibilityNodeInfo.isChecked());
        onPopulateEventForVirtualView(i, obtain);
        if (obtain.getText().isEmpty() && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateEventForVirtualViewId()");
        }
        obtain.setClassName(obtainAccessibilityNodeInfo.getClassName());
        AccessibilityRecordCompat.setSource(obtain, this.mHost, i);
        obtain.setPackageName(this.mHost.getContext().getPackageName());
        return obtain;
    }

    @NonNull
    AccessibilityNodeInfoCompat obtainAccessibilityNodeInfo(int i) {
        if (i == -1) {
            return createNodeForHost();
        }
        return createNodeForChild(i);
    }

    @NonNull
    private AccessibilityNodeInfoCompat createNodeForHost() {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain(this.mHost);
        ViewCompat.onInitializeAccessibilityNodeInfo(this.mHost, obtain);
        ArrayList arrayList = new ArrayList();
        getVisibleVirtualViews(arrayList);
        if (obtain.getChildCount() <= 0 || arrayList.size() <= 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                obtain.addChild(this.mHost, ((Integer) arrayList.get(i)).intValue());
            }
            return obtain;
        }
        throw new RuntimeException("Views cannot have both real and virtual children");
    }

    public void onInitializeAccessibilityNodeInfo(View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfoCompat);
        onPopulateNodeForHost(accessibilityNodeInfoCompat);
    }

    @NonNull
    private AccessibilityNodeInfoCompat createNodeForChild(int i) {
        AccessibilityNodeInfoCompat obtain = AccessibilityNodeInfoCompat.obtain();
        int i2 = true;
        obtain.setEnabled(i2);
        obtain.setFocusable(i2);
        obtain.setClassName("android.view.View");
        obtain.setBoundsInParent(INVALID_PARENT_BOUNDS);
        obtain.setBoundsInScreen(INVALID_PARENT_BOUNDS);
        obtain.setParent(this.mHost);
        onPopulateNodeForVirtualView(i, obtain);
        if (obtain.getText() == null && obtain.getContentDescription() == null) {
            throw new RuntimeException("Callbacks must add text or a content description in populateNodeForVirtualViewId()");
        }
        obtain.getBoundsInParent(this.mTempParentRect);
        if (this.mTempParentRect.equals(INVALID_PARENT_BOUNDS)) {
            throw new RuntimeException("Callbacks must set parent bounds in populateNodeForVirtualViewId()");
        }
        int actions = obtain.getActions();
        if ((actions & 64) != 0) {
            throw new RuntimeException("Callbacks must not add ACTION_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        int i3 = 128;
        if ((actions & i3) != 0) {
            throw new RuntimeException("Callbacks must not add ACTION_CLEAR_ACCESSIBILITY_FOCUS in populateNodeForVirtualViewId()");
        }
        obtain.setPackageName(this.mHost.getContext().getPackageName());
        obtain.setSource(this.mHost, i);
        boolean z = false;
        if (this.mAccessibilityFocusedVirtualViewId == i) {
            obtain.setAccessibilityFocused(i2);
            obtain.addAction(i3);
        } else {
            obtain.setAccessibilityFocused(z);
            obtain.addAction(64);
        }
        boolean z2 = this.mKeyboardFocusedVirtualViewId == i ? i2 : z;
        if (z2) {
            obtain.addAction(2);
        } else if (obtain.isFocusable()) {
            obtain.addAction(i2);
        }
        obtain.setFocused(z2);
        this.mHost.getLocationOnScreen(this.mTempGlobalRect);
        obtain.getBoundsInScreen(this.mTempScreenRect);
        if (this.mTempScreenRect.equals(INVALID_PARENT_BOUNDS)) {
            obtain.getBoundsInParent(this.mTempScreenRect);
            actions = -1;
            if (obtain.mParentVirtualDescendantId != actions) {
                AccessibilityNodeInfoCompat obtain2 = AccessibilityNodeInfoCompat.obtain();
                for (i3 = obtain.mParentVirtualDescendantId; i3 != actions; i3 = obtain2.mParentVirtualDescendantId) {
                    obtain2.setParent(this.mHost, actions);
                    obtain2.setBoundsInParent(INVALID_PARENT_BOUNDS);
                    onPopulateNodeForVirtualView(i3, obtain2);
                    obtain2.getBoundsInParent(this.mTempParentRect);
                    this.mTempScreenRect.offset(this.mTempParentRect.left, this.mTempParentRect.top);
                }
                obtain2.recycle();
            }
            this.mTempScreenRect.offset(this.mTempGlobalRect[z] - this.mHost.getScrollX(), this.mTempGlobalRect[i2] - this.mHost.getScrollY());
        }
        if (this.mHost.getLocalVisibleRect(this.mTempVisibleRect)) {
            this.mTempVisibleRect.offset(this.mTempGlobalRect[z] - this.mHost.getScrollX(), this.mTempGlobalRect[i2] - this.mHost.getScrollY());
            if (this.mTempScreenRect.intersect(this.mTempVisibleRect)) {
                obtain.setBoundsInScreen(this.mTempScreenRect);
                if (isVisibleToUser(this.mTempScreenRect)) {
                    obtain.setVisibleToUser(i2);
                }
            }
        }
        return obtain;
    }

    boolean performAction(int i, int i2, Bundle bundle) {
        if (i != -1) {
            return performActionForChild(i, i2, bundle);
        }
        return performActionForHost(i2, bundle);
    }

    private boolean performActionForHost(int i, Bundle bundle) {
        return ViewCompat.performAccessibilityAction(this.mHost, i, bundle);
    }

    private boolean performActionForChild(int i, int i2, Bundle bundle) {
        if (i2 == 64) {
            return requestAccessibilityFocus(i);
        }
        if (i2 == 128) {
            return clearAccessibilityFocus(i);
        }
        switch (i2) {
            case 1:
                return requestKeyboardFocusForVirtualView(i);
            case 2:
                return clearKeyboardFocusForVirtualView(i);
            default:
                return onPerformActionForVirtualView(i, i2, bundle);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean isVisibleToUser(android.graphics.Rect r4) {
        /*
        r3 = this;
        r0 = 0;
        if (r4 == 0) goto L_0x0039;
    L_0x0003:
        r4 = r4.isEmpty();
        if (r4 == 0) goto L_0x000a;
    L_0x0009:
        goto L_0x0039;
    L_0x000a:
        r4 = r3.mHost;
        r4 = r4.getWindowVisibility();
        if (r4 == 0) goto L_0x0013;
    L_0x0012:
        return r0;
    L_0x0013:
        r4 = r3.mHost;
        r4 = r4.getParent();
    L_0x0019:
        r1 = r4 instanceof android.view.View;
        if (r1 == 0) goto L_0x0035;
    L_0x001d:
        r4 = (android.view.View) r4;
        r1 = r4.getAlpha();
        r2 = 0;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 <= 0) goto L_0x0034;
    L_0x0028:
        r1 = r4.getVisibility();
        if (r1 == 0) goto L_0x002f;
    L_0x002e:
        goto L_0x0034;
    L_0x002f:
        r4 = r4.getParent();
        goto L_0x0019;
    L_0x0034:
        return r0;
    L_0x0035:
        if (r4 == 0) goto L_0x0038;
    L_0x0037:
        r0 = 1;
    L_0x0038:
        return r0;
    L_0x0039:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.ExploreByTouchHelper.isVisibleToUser(android.graphics.Rect):boolean");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean requestAccessibilityFocus(int r3) {
        /*
        r2 = this;
        r0 = r2.mManager;
        r0 = r0.isEnabled();
        r1 = 0;
        if (r0 == 0) goto L_0x0031;
    L_0x0009:
        r0 = r2.mManager;
        r0 = r0.isTouchExplorationEnabled();
        if (r0 != 0) goto L_0x0012;
    L_0x0011:
        goto L_0x0031;
    L_0x0012:
        r0 = r2.mAccessibilityFocusedVirtualViewId;
        if (r0 == r3) goto L_0x0030;
    L_0x0016:
        r0 = r2.mAccessibilityFocusedVirtualViewId;
        r1 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        if (r0 == r1) goto L_0x0021;
    L_0x001c:
        r0 = r2.mAccessibilityFocusedVirtualViewId;
        r2.clearAccessibilityFocus(r0);
    L_0x0021:
        r2.mAccessibilityFocusedVirtualViewId = r3;
        r0 = r2.mHost;
        r0.invalidate();
        r0 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        r2.sendEventForVirtualView(r3, r0);
        r3 = 1;
        return r3;
    L_0x0030:
        return r1;
    L_0x0031:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.ExploreByTouchHelper.requestAccessibilityFocus(int):boolean");
    }

    private boolean clearAccessibilityFocus(int i) {
        if (this.mAccessibilityFocusedVirtualViewId != i) {
            return false;
        }
        this.mAccessibilityFocusedVirtualViewId = Integer.MIN_VALUE;
        this.mHost.invalidate();
        sendEventForVirtualView(i, 65536);
        return true;
    }

    public final boolean requestKeyboardFocusForVirtualView(int i) {
        boolean z = false;
        if ((!this.mHost.isFocused() && !this.mHost.requestFocus()) || this.mKeyboardFocusedVirtualViewId == i) {
            return z;
        }
        if (this.mKeyboardFocusedVirtualViewId != Integer.MIN_VALUE) {
            clearKeyboardFocusForVirtualView(this.mKeyboardFocusedVirtualViewId);
        }
        this.mKeyboardFocusedVirtualViewId = i;
        boolean z2 = true;
        onVirtualViewKeyboardFocusChanged(i, z2);
        sendEventForVirtualView(i, 8);
        return z2;
    }

    public final boolean clearKeyboardFocusForVirtualView(int i) {
        boolean z = false;
        if (this.mKeyboardFocusedVirtualViewId != i) {
            return z;
        }
        this.mKeyboardFocusedVirtualViewId = Integer.MIN_VALUE;
        onVirtualViewKeyboardFocusChanged(i, z);
        sendEventForVirtualView(i, 8);
        return true;
    }
}
