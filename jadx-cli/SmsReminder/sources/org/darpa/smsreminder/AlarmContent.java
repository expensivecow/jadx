package org.darpa.smsreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.telephony.SmsMessage;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.AccessibilityDelegate;
import android.view.View.DragShadowBuilder;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnGenericMotionListener;
import android.view.View.OnHoverListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class AlarmContent {
    private static AlarmContent instance = null;
    private Context alarm_context;
    private int alarm_number = 0;
    private final String file_name = "sms_alarm.dat";
    private List<AlarmItem> items = new ArrayList();
    private View my_view;

    public class AlarmItem {
        public int alarm_number;
        private boolean is_reminder = false;
        public String message;
        public String phone_number;
        private Calendar time;

        public AlarmItem(String phone_number, Calendar time, String message, int alarm_number) {
            System.out.println("Alarm will go off in " + Long.toString((time.getTimeInMillis() - System.currentTimeMillis()) / 1000) + " seconds");
            this.phone_number = phone_number;
            this.time = time;
            this.message = message;
            this.alarm_number = alarm_number;
        }

        public AlarmItem(String phone_number, long time_millis, String message, int alarm_number) {
            this.phone_number = phone_number;
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time_millis);
            this.time = cal;
            this.message = message;
            this.alarm_number = alarm_number;
        }

        public int getAlarmNumber() {
            return this.alarm_number;
        }

        public String time_string() {
            return DateFormat.getDateTimeInstance().format(this.time.getTime());
        }

        public long time_millis() {
            return this.time.getTimeInMillis();
        }

        public String toString() {
            return this.phone_number + "\n\t" + time_string() + "\n\t" + this.message;
        }

        public boolean isReminder() {
            return this.is_reminder;
        }

        public void setReminder(boolean b) {
            this.is_reminder = b;
        }

        public boolean matchesPhoneNumber(String phone_number) {
            return AlarmContent.this.comparePhoneNumbers(this.phone_number, phone_number, Math.min(this.phone_number.length(), this.phone_number.length()));
        }
    }

    public static AlarmContent getInstance() {
        if (instance == null) {
            instance = new AlarmContent();
        }
        return instance;
    }

    private AlarmContent() {
        try {
            Scanner reader = new Scanner(new InputStreamReader(this.alarm_context.openFileInput("sms_alarm.dat")));
            System.out.println("Opened " + "sms_alarm.dat" + " for reading");
            while (reader.hasNext()) {
                System.out.println("Reading next alarm!");
                String phone_num = reader.next();
                Long time_in_millis = Long.valueOf(reader.nextLong());
                int alarm_num = reader.nextInt();
                boolean reminder = reader.nextBoolean();
                AlarmItem new_alarm = new AlarmItem(phone_num, time_in_millis.longValue(), reader.nextLine(), alarm_num);
                new_alarm.setReminder(reminder);
                this.items.add(new_alarm);
            }
            System.out.println("Done reading from file.");
            reader.close();
        } catch (Exception e) {
            System.out.println("Error: Failed to read from file.");
        }
        System.out.println("Read " + Integer.toString(this.items.size()) + " alarms from file.");
    }

    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        this.my_view.addFocusables(views, direction, focusableMode);
    }

    public void addFocusables(ArrayList<View> views, int direction) {
        this.my_view.addFocusables(views, direction);
    }

    public void addOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        this.my_view.addOnAttachStateChangeListener(listener);
    }

    public void addOnLayoutChangeListener(OnLayoutChangeListener listener) {
        this.my_view.addOnLayoutChangeListener(listener);
    }

    public void addTouchables(ArrayList<View> views) {
        this.my_view.addTouchables(views);
    }

    public ViewPropertyAnimator animate() {
        return this.my_view.animate();
    }

    public void bringToFront() {
        this.my_view.bringToFront();
    }

    public void buildDrawingCache() {
        this.my_view.buildDrawingCache();
    }

    public void buildDrawingCache(boolean autoScale) {
        this.my_view.buildDrawingCache(autoScale);
    }

    public void buildLayer() {
        this.my_view.buildLayer();
    }

    public boolean callOnClick() {
        return this.my_view.callOnClick();
    }

    public boolean canScrollHorizontally(int direction) {
        return this.my_view.canScrollHorizontally(direction);
    }

    public boolean canScrollVertically(int direction) {
        return this.my_view.canScrollVertically(direction);
    }

    public void cancelLongPress() {
        this.my_view.cancelLongPress();
    }

    public boolean checkInputConnectionProxy(View view) {
        return this.my_view.checkInputConnectionProxy(view);
    }

    public void clearAnimation() {
        this.my_view.clearAnimation();
    }

    public void clearFocus() {
        this.my_view.clearFocus();
    }

    public void computeScroll() {
        this.my_view.computeScroll();
    }

    public AccessibilityNodeInfo createAccessibilityNodeInfo() {
        return this.my_view.createAccessibilityNodeInfo();
    }

    public void createContextMenu(ContextMenu menu) {
        this.my_view.createContextMenu(menu);
    }

    public void destroyDrawingCache() {
        this.my_view.destroyDrawingCache();
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        this.my_view.dispatchConfigurationChanged(newConfig);
    }

    public void dispatchDisplayHint(int hint) {
        this.my_view.dispatchDisplayHint(hint);
    }

    public boolean dispatchDragEvent(DragEvent event) {
        return this.my_view.dispatchDragEvent(event);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        return this.my_view.dispatchGenericMotionEvent(event);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return this.my_view.dispatchKeyEvent(event);
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return this.my_view.dispatchKeyEventPreIme(event);
    }

    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        return this.my_view.dispatchKeyShortcutEvent(event);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return this.my_view.dispatchPopulateAccessibilityEvent(event);
    }

    public void dispatchSystemUiVisibilityChanged(int visibility) {
        this.my_view.dispatchSystemUiVisibilityChanged(visibility);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        return this.my_view.dispatchTouchEvent(event);
    }

    public boolean dispatchTrackballEvent(MotionEvent event) {
        return this.my_view.dispatchTrackballEvent(event);
    }

    public boolean dispatchUnhandledMove(View focused, int direction) {
        return this.my_view.dispatchUnhandledMove(focused, direction);
    }

    public void dispatchWindowFocusChanged(boolean hasFocus) {
        this.my_view.dispatchWindowFocusChanged(hasFocus);
    }

    public void dispatchWindowVisibilityChanged(int visibility) {
        this.my_view.dispatchWindowVisibilityChanged(visibility);
    }

    public void draw(Canvas canvas) {
        this.my_view.draw(canvas);
    }

    public boolean equals(Object o) {
        return this.my_view.equals(o);
    }

    public View findFocus() {
        return this.my_view.findFocus();
    }

    public final View findViewById(int id) {
        return this.my_view.findViewById(id);
    }

    public final View findViewWithTag(Object tag) {
        return this.my_view.findViewWithTag(tag);
    }

    public void findViewsWithText(ArrayList<View> outViews, CharSequence searched, int flags) {
        this.my_view.findViewsWithText(outViews, searched, flags);
    }

    public boolean fitsSystemWindows() {
        return this.my_view.fitsSystemWindows();
    }

    public View focusSearch(int direction) {
        return this.my_view.focusSearch(direction);
    }

    public void forceLayout() {
        this.my_view.forceLayout();
    }

    public float getAlpha() {
        return this.my_view.getAlpha();
    }

    public Animation getAnimation() {
        return this.my_view.getAnimation();
    }

    public IBinder getApplicationWindowToken() {
        return this.my_view.getApplicationWindowToken();
    }

    public Drawable getBackground() {
        return this.my_view.getBackground();
    }

    public int getBaseline() {
        return this.my_view.getBaseline();
    }

    public final int getBottom() {
        return this.my_view.getBottom();
    }

    public CharSequence getContentDescription() {
        return this.my_view.getContentDescription();
    }

    public final Context getContext() {
        return this.my_view.getContext();
    }

    public final int[] getDrawableState() {
        return this.my_view.getDrawableState();
    }

    public Bitmap getDrawingCache() {
        return this.my_view.getDrawingCache();
    }

    public Bitmap getDrawingCache(boolean autoScale) {
        return this.my_view.getDrawingCache(autoScale);
    }

    public int getDrawingCacheBackgroundColor() {
        return this.my_view.getDrawingCacheBackgroundColor();
    }

    public int getDrawingCacheQuality() {
        return this.my_view.getDrawingCacheQuality();
    }

    public void getDrawingRect(Rect outRect) {
        this.my_view.getDrawingRect(outRect);
    }

    public long getDrawingTime() {
        return this.my_view.getDrawingTime();
    }

    public boolean getFilterTouchesWhenObscured() {
        return this.my_view.getFilterTouchesWhenObscured();
    }

    public ArrayList<View> getFocusables(int direction) {
        return this.my_view.getFocusables(direction);
    }

    public void getFocusedRect(Rect r) {
        this.my_view.getFocusedRect(r);
    }

    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        return this.my_view.getGlobalVisibleRect(r, globalOffset);
    }

    public final boolean getGlobalVisibleRect(Rect r) {
        return this.my_view.getGlobalVisibleRect(r);
    }

    public Handler getHandler() {
        return this.my_view.getHandler();
    }

    public final int getHeight() {
        return this.my_view.getHeight();
    }

    public void getHitRect(Rect outRect) {
        this.my_view.getHitRect(outRect);
    }

    public int getHorizontalFadingEdgeLength() {
        return this.my_view.getHorizontalFadingEdgeLength();
    }

    public int getId() {
        return this.my_view.getId();
    }

    public boolean getKeepScreenOn() {
        return this.my_view.getKeepScreenOn();
    }

    public DispatcherState getKeyDispatcherState() {
        return this.my_view.getKeyDispatcherState();
    }

    public int getLayerType() {
        return this.my_view.getLayerType();
    }

    public LayoutParams getLayoutParams() {
        return this.my_view.getLayoutParams();
    }

    public final int getLeft() {
        return this.my_view.getLeft();
    }

    public final boolean getLocalVisibleRect(Rect r) {
        return this.my_view.getLocalVisibleRect(r);
    }

    public void getLocationInWindow(int[] location) {
        this.my_view.getLocationInWindow(location);
    }

    public void getLocationOnScreen(int[] location) {
        this.my_view.getLocationOnScreen(location);
    }

    public Matrix getMatrix() {
        return this.my_view.getMatrix();
    }

    public final int getMeasuredHeight() {
        return this.my_view.getMeasuredHeight();
    }

    public final int getMeasuredHeightAndState() {
        return this.my_view.getMeasuredHeightAndState();
    }

    public final int getMeasuredState() {
        return this.my_view.getMeasuredState();
    }

    public final int getMeasuredWidth() {
        return this.my_view.getMeasuredWidth();
    }

    public final int getMeasuredWidthAndState() {
        return this.my_view.getMeasuredWidthAndState();
    }

    public int getNextFocusDownId() {
        return this.my_view.getNextFocusDownId();
    }

    public int getNextFocusForwardId() {
        return this.my_view.getNextFocusForwardId();
    }

    public int getNextFocusLeftId() {
        return this.my_view.getNextFocusLeftId();
    }

    public int getNextFocusRightId() {
        return this.my_view.getNextFocusRightId();
    }

    public int getNextFocusUpId() {
        return this.my_view.getNextFocusUpId();
    }

    public OnFocusChangeListener getOnFocusChangeListener() {
        return this.my_view.getOnFocusChangeListener();
    }

    public int getOverScrollMode() {
        return this.my_view.getOverScrollMode();
    }

    public int getPaddingBottom() {
        return this.my_view.getPaddingBottom();
    }

    public int getPaddingLeft() {
        return this.my_view.getPaddingLeft();
    }

    public int getPaddingRight() {
        return this.my_view.getPaddingRight();
    }

    public int getPaddingTop() {
        return this.my_view.getPaddingTop();
    }

    public final ViewParent getParent() {
        return this.my_view.getParent();
    }

    public float getPivotX() {
        return this.my_view.getPivotX();
    }

    public float getPivotY() {
        return this.my_view.getPivotY();
    }

    public Resources getResources() {
        return this.my_view.getResources();
    }

    public final int getRight() {
        return this.my_view.getRight();
    }

    public View getRootView() {
        return this.my_view.getRootView();
    }

    public float getRotation() {
        return this.my_view.getRotation();
    }

    public float getRotationX() {
        return this.my_view.getRotationX();
    }

    public float getRotationY() {
        return this.my_view.getRotationY();
    }

    public float getScaleX() {
        return this.my_view.getScaleX();
    }

    public float getScaleY() {
        return this.my_view.getScaleY();
    }

    public int getScrollBarStyle() {
        return this.my_view.getScrollBarStyle();
    }

    public final int getScrollX() {
        return this.my_view.getScrollX();
    }

    public final int getScrollY() {
        return this.my_view.getScrollY();
    }

    public int getSolidColor() {
        return this.my_view.getSolidColor();
    }

    public int getSystemUiVisibility() {
        return this.my_view.getSystemUiVisibility();
    }

    public Object getTag() {
        return this.my_view.getTag();
    }

    public Object getTag(int key) {
        return this.my_view.getTag(key);
    }

    public final int getTop() {
        return this.my_view.getTop();
    }

    public TouchDelegate getTouchDelegate() {
        return this.my_view.getTouchDelegate();
    }

    public ArrayList<View> getTouchables() {
        return this.my_view.getTouchables();
    }

    public float getTranslationX() {
        return this.my_view.getTranslationX();
    }

    public float getTranslationY() {
        return this.my_view.getTranslationY();
    }

    public int getVerticalFadingEdgeLength() {
        return this.my_view.getVerticalFadingEdgeLength();
    }

    public int getVerticalScrollbarPosition() {
        return this.my_view.getVerticalScrollbarPosition();
    }

    public int getVerticalScrollbarWidth() {
        return this.my_view.getVerticalScrollbarWidth();
    }

    public ViewTreeObserver getViewTreeObserver() {
        return this.my_view.getViewTreeObserver();
    }

    public int getVisibility() {
        return this.my_view.getVisibility();
    }

    public final int getWidth() {
        return this.my_view.getWidth();
    }

    public IBinder getWindowToken() {
        return this.my_view.getWindowToken();
    }

    public int getWindowVisibility() {
        return this.my_view.getWindowVisibility();
    }

    public void getWindowVisibleDisplayFrame(Rect outRect) {
        this.my_view.getWindowVisibleDisplayFrame(outRect);
    }

    public float getX() {
        return this.my_view.getX();
    }

    public float getY() {
        return this.my_view.getY();
    }

    public boolean hasFocus() {
        return this.my_view.hasFocus();
    }

    public boolean hasFocusable() {
        return this.my_view.hasFocusable();
    }

    public boolean hasOnClickListeners() {
        return this.my_view.hasOnClickListeners();
    }

    public boolean hasWindowFocus() {
        return this.my_view.hasWindowFocus();
    }

    public int hashCode() {
        return this.my_view.hashCode();
    }

    public void invalidate() {
        this.my_view.invalidate();
    }

    public void invalidate(int l, int t, int r, int b) {
        this.my_view.invalidate(l, t, r, b);
    }

    public void invalidate(Rect dirty) {
        this.my_view.invalidate(dirty);
    }

    public void invalidateDrawable(Drawable drawable) {
        this.my_view.invalidateDrawable(drawable);
    }

    public boolean isActivated() {
        return this.my_view.isActivated();
    }

    public boolean isClickable() {
        return this.my_view.isClickable();
    }

    public boolean isDirty() {
        return this.my_view.isDirty();
    }

    public boolean isDrawingCacheEnabled() {
        return this.my_view.isDrawingCacheEnabled();
    }

    public boolean isDuplicateParentStateEnabled() {
        return this.my_view.isDuplicateParentStateEnabled();
    }

    public boolean isEnabled() {
        return this.my_view.isEnabled();
    }

    public final boolean isFocusable() {
        return this.my_view.isFocusable();
    }

    public final boolean isFocusableInTouchMode() {
        return this.my_view.isFocusableInTouchMode();
    }

    public boolean isFocused() {
        return this.my_view.isFocused();
    }

    public boolean isHapticFeedbackEnabled() {
        return this.my_view.isHapticFeedbackEnabled();
    }

    public boolean isHardwareAccelerated() {
        return this.my_view.isHardwareAccelerated();
    }

    public boolean isHorizontalFadingEdgeEnabled() {
        return this.my_view.isHorizontalFadingEdgeEnabled();
    }

    public boolean isHorizontalScrollBarEnabled() {
        return this.my_view.isHorizontalScrollBarEnabled();
    }

    public boolean isHovered() {
        return this.my_view.isHovered();
    }

    public boolean isInEditMode() {
        return this.my_view.isInEditMode();
    }

    public boolean isInTouchMode() {
        return this.my_view.isInTouchMode();
    }

    public boolean isLayoutRequested() {
        return this.my_view.isLayoutRequested();
    }

    public boolean isLongClickable() {
        return this.my_view.isLongClickable();
    }

    public boolean isOpaque() {
        return this.my_view.isOpaque();
    }

    public boolean isPressed() {
        return this.my_view.isPressed();
    }

    public boolean isSaveEnabled() {
        return this.my_view.isSaveEnabled();
    }

    public boolean isSaveFromParentEnabled() {
        return this.my_view.isSaveFromParentEnabled();
    }

    public boolean isScrollbarFadingEnabled() {
        return this.my_view.isScrollbarFadingEnabled();
    }

    public boolean isSelected() {
        return this.my_view.isSelected();
    }

    public boolean isShown() {
        return this.my_view.isShown();
    }

    public boolean isSoundEffectsEnabled() {
        return this.my_view.isSoundEffectsEnabled();
    }

    public boolean isVerticalFadingEdgeEnabled() {
        return this.my_view.isVerticalFadingEdgeEnabled();
    }

    public boolean isVerticalScrollBarEnabled() {
        return this.my_view.isVerticalScrollBarEnabled();
    }

    public void jumpDrawablesToCurrentState() {
        this.my_view.jumpDrawablesToCurrentState();
    }

    public void layout(int l, int t, int r, int b) {
        this.my_view.layout(l, t, r, b);
    }

    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        this.my_view.measure(widthMeasureSpec, heightMeasureSpec);
    }

    public void offsetLeftAndRight(int offset) {
        this.my_view.offsetLeftAndRight(offset);
    }

    public void offsetTopAndBottom(int offset) {
        this.my_view.offsetTopAndBottom(offset);
    }

    public boolean onCheckIsTextEditor() {
        return this.my_view.onCheckIsTextEditor();
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return this.my_view.onCreateInputConnection(outAttrs);
    }

    public boolean onDragEvent(DragEvent event) {
        return this.my_view.onDragEvent(event);
    }

    public boolean onFilterTouchEventForSecurity(MotionEvent event) {
        return this.my_view.onFilterTouchEventForSecurity(event);
    }

    public void onFinishTemporaryDetach() {
        this.my_view.onFinishTemporaryDetach();
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return this.my_view.onGenericMotionEvent(event);
    }

    public void onHoverChanged(boolean hovered) {
        this.my_view.onHoverChanged(hovered);
    }

    public boolean onHoverEvent(MotionEvent event) {
        return this.my_view.onHoverEvent(event);
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        this.my_view.onInitializeAccessibilityEvent(event);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        this.my_view.onInitializeAccessibilityNodeInfo(info);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.my_view.onKeyDown(keyCode, event);
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return this.my_view.onKeyLongPress(keyCode, event);
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return this.my_view.onKeyMultiple(keyCode, repeatCount, event);
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        return this.my_view.onKeyPreIme(keyCode, event);
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return this.my_view.onKeyShortcut(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.my_view.onKeyUp(keyCode, event);
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        this.my_view.onPopulateAccessibilityEvent(event);
    }

    public void onStartTemporaryDetach() {
        this.my_view.onStartTemporaryDetach();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return this.my_view.onTouchEvent(event);
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return this.my_view.onTrackballEvent(event);
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        this.my_view.onWindowFocusChanged(hasWindowFocus);
    }

    public boolean performClick() {
        return this.my_view.performClick();
    }

    public boolean performHapticFeedback(int feedbackConstant, int flags) {
        return this.my_view.performHapticFeedback(feedbackConstant, flags);
    }

    public boolean performHapticFeedback(int feedbackConstant) {
        return this.my_view.performHapticFeedback(feedbackConstant);
    }

    public boolean performLongClick() {
        return this.my_view.performLongClick();
    }

    public void playSoundEffect(int soundConstant) {
        this.my_view.playSoundEffect(soundConstant);
    }

    public boolean post(Runnable action) {
        return this.my_view.post(action);
    }

    public boolean postDelayed(Runnable action, long delayMillis) {
        return this.my_view.postDelayed(action, delayMillis);
    }

    public void postInvalidate() {
        this.my_view.postInvalidate();
    }

    public void postInvalidate(int left, int top, int right, int bottom) {
        this.my_view.postInvalidate(left, top, right, bottom);
    }

    public void postInvalidateDelayed(long delayMilliseconds, int left, int top, int right, int bottom) {
        this.my_view.postInvalidateDelayed(delayMilliseconds, left, top, right, bottom);
    }

    public void postInvalidateDelayed(long delayMilliseconds) {
        this.my_view.postInvalidateDelayed(delayMilliseconds);
    }

    public void refreshDrawableState() {
        this.my_view.refreshDrawableState();
    }

    public boolean removeCallbacks(Runnable action) {
        return this.my_view.removeCallbacks(action);
    }

    public void removeOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
        this.my_view.removeOnAttachStateChangeListener(listener);
    }

    public void removeOnLayoutChangeListener(OnLayoutChangeListener listener) {
        this.my_view.removeOnLayoutChangeListener(listener);
    }

    public final boolean requestFocus() {
        return this.my_view.requestFocus();
    }

    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return this.my_view.requestFocus(direction, previouslyFocusedRect);
    }

    public final boolean requestFocus(int direction) {
        return this.my_view.requestFocus(direction);
    }

    public final boolean requestFocusFromTouch() {
        return this.my_view.requestFocusFromTouch();
    }

    public void requestLayout() {
        this.my_view.requestLayout();
    }

    public boolean requestRectangleOnScreen(Rect rectangle, boolean immediate) {
        return this.my_view.requestRectangleOnScreen(rectangle, immediate);
    }

    public boolean requestRectangleOnScreen(Rect rectangle) {
        return this.my_view.requestRectangleOnScreen(rectangle);
    }

    public void restoreHierarchyState(SparseArray<Parcelable> container) {
        this.my_view.restoreHierarchyState(container);
    }

    public void saveHierarchyState(SparseArray<Parcelable> container) {
        this.my_view.saveHierarchyState(container);
    }

    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        this.my_view.scheduleDrawable(who, what, when);
    }

    public void scrollBy(int x, int y) {
        this.my_view.scrollBy(x, y);
    }

    public void scrollTo(int x, int y) {
        this.my_view.scrollTo(x, y);
    }

    public void sendAccessibilityEvent(int eventType) {
        this.my_view.sendAccessibilityEvent(eventType);
    }

    public void sendAccessibilityEventUnchecked(AccessibilityEvent event) {
        this.my_view.sendAccessibilityEventUnchecked(event);
    }

    public void setAccessibilityDelegate(AccessibilityDelegate delegate) {
        this.my_view.setAccessibilityDelegate(delegate);
    }

    public void setActivated(boolean activated) {
        this.my_view.setActivated(activated);
    }

    public void setAlpha(float alpha) {
        this.my_view.setAlpha(alpha);
    }

    public void setAnimation(Animation animation) {
        this.my_view.setAnimation(animation);
    }

    public void setBackgroundColor(int color) {
        this.my_view.setBackgroundColor(color);
    }

    public void setBackgroundDrawable(Drawable background) {
        this.my_view.setBackgroundDrawable(background);
    }

    public void setBackgroundResource(int resid) {
        this.my_view.setBackgroundResource(resid);
    }

    public final void setBottom(int bottom) {
        this.my_view.setBottom(bottom);
    }

    public void setCameraDistance(float distance) {
        this.my_view.setCameraDistance(distance);
    }

    public void setClickable(boolean clickable) {
        this.my_view.setClickable(clickable);
    }

    public void setContentDescription(CharSequence contentDescription) {
        this.my_view.setContentDescription(contentDescription);
    }

    public void setDrawingCacheBackgroundColor(int color) {
        this.my_view.setDrawingCacheBackgroundColor(color);
    }

    public void setDrawingCacheEnabled(boolean enabled) {
        this.my_view.setDrawingCacheEnabled(enabled);
    }

    public void setDrawingCacheQuality(int quality) {
        this.my_view.setDrawingCacheQuality(quality);
    }

    public void setDuplicateParentStateEnabled(boolean enabled) {
        this.my_view.setDuplicateParentStateEnabled(enabled);
    }

    public void setEnabled(boolean enabled) {
        this.my_view.setEnabled(enabled);
    }

    public void setFadingEdgeLength(int length) {
        this.my_view.setFadingEdgeLength(length);
    }

    public void setFilterTouchesWhenObscured(boolean enabled) {
        this.my_view.setFilterTouchesWhenObscured(enabled);
    }

    public void setFitsSystemWindows(boolean fitSystemWindows) {
        this.my_view.setFitsSystemWindows(fitSystemWindows);
    }

    public void setFocusable(boolean focusable) {
        this.my_view.setFocusable(focusable);
    }

    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        this.my_view.setFocusableInTouchMode(focusableInTouchMode);
    }

    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) {
        this.my_view.setHapticFeedbackEnabled(hapticFeedbackEnabled);
    }

    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        this.my_view.setHorizontalFadingEdgeEnabled(horizontalFadingEdgeEnabled);
    }

    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        this.my_view.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }

    public void setHovered(boolean hovered) {
        this.my_view.setHovered(hovered);
    }

    public void setId(int id) {
        this.my_view.setId(id);
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.my_view.setKeepScreenOn(keepScreenOn);
    }

    public void setLayerType(int layerType, Paint paint) {
        this.my_view.setLayerType(layerType, paint);
    }

    public void setLayoutParams(LayoutParams params) {
        this.my_view.setLayoutParams(params);
    }

    public final void setLeft(int left) {
        this.my_view.setLeft(left);
    }

    public void setLongClickable(boolean longClickable) {
        this.my_view.setLongClickable(longClickable);
    }

    public void setMinimumHeight(int minHeight) {
        this.my_view.setMinimumHeight(minHeight);
    }

    public void setMinimumWidth(int minWidth) {
        this.my_view.setMinimumWidth(minWidth);
    }

    public void setNextFocusDownId(int nextFocusDownId) {
        this.my_view.setNextFocusDownId(nextFocusDownId);
    }

    public void setNextFocusForwardId(int nextFocusForwardId) {
        this.my_view.setNextFocusForwardId(nextFocusForwardId);
    }

    public void setNextFocusLeftId(int nextFocusLeftId) {
        this.my_view.setNextFocusLeftId(nextFocusLeftId);
    }

    public void setNextFocusRightId(int nextFocusRightId) {
        this.my_view.setNextFocusRightId(nextFocusRightId);
    }

    public void setNextFocusUpId(int nextFocusUpId) {
        this.my_view.setNextFocusUpId(nextFocusUpId);
    }

    public void setOnClickListener(OnClickListener l) {
        this.my_view.setOnClickListener(l);
    }

    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {
        this.my_view.setOnCreateContextMenuListener(l);
    }

    public void setOnDragListener(OnDragListener l) {
        this.my_view.setOnDragListener(l);
    }

    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.my_view.setOnFocusChangeListener(l);
    }

    public void setOnGenericMotionListener(OnGenericMotionListener l) {
        this.my_view.setOnGenericMotionListener(l);
    }

    public void setOnHoverListener(OnHoverListener l) {
        this.my_view.setOnHoverListener(l);
    }

    public void setOnKeyListener(OnKeyListener l) {
        this.my_view.setOnKeyListener(l);
    }

    public void setOnLongClickListener(OnLongClickListener l) {
        this.my_view.setOnLongClickListener(l);
    }

    public void setOnSystemUiVisibilityChangeListener(OnSystemUiVisibilityChangeListener l) {
        this.my_view.setOnSystemUiVisibilityChangeListener(l);
    }

    public void setOnTouchListener(OnTouchListener l) {
        this.my_view.setOnTouchListener(l);
    }

    public void setOverScrollMode(int overScrollMode) {
        this.my_view.setOverScrollMode(overScrollMode);
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.my_view.setPadding(left, top, right, bottom);
    }

    public void setPivotX(float pivotX) {
        this.my_view.setPivotX(pivotX);
    }

    public void setPivotY(float pivotY) {
        this.my_view.setPivotY(pivotY);
    }

    public void setPressed(boolean pressed) {
        this.my_view.setPressed(pressed);
    }

    public final void setRight(int right) {
        this.my_view.setRight(right);
    }

    public void setRotation(float rotation) {
        this.my_view.setRotation(rotation);
    }

    public void setRotationX(float rotationX) {
        this.my_view.setRotationX(rotationX);
    }

    public void setRotationY(float rotationY) {
        this.my_view.setRotationY(rotationY);
    }

    public void setSaveEnabled(boolean enabled) {
        this.my_view.setSaveEnabled(enabled);
    }

    public void setSaveFromParentEnabled(boolean enabled) {
        this.my_view.setSaveFromParentEnabled(enabled);
    }

    public void setScaleX(float scaleX) {
        this.my_view.setScaleX(scaleX);
    }

    public void setScaleY(float scaleY) {
        this.my_view.setScaleY(scaleY);
    }

    public void setScrollBarStyle(int style) {
        this.my_view.setScrollBarStyle(style);
    }

    public void setScrollContainer(boolean isScrollContainer) {
        this.my_view.setScrollContainer(isScrollContainer);
    }

    public void setScrollX(int value) {
        this.my_view.setScrollX(value);
    }

    public void setScrollY(int value) {
        this.my_view.setScrollY(value);
    }

    public void setScrollbarFadingEnabled(boolean fadeScrollbars) {
        this.my_view.setScrollbarFadingEnabled(fadeScrollbars);
    }

    public void setSelected(boolean selected) {
        this.my_view.setSelected(selected);
    }

    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        this.my_view.setSoundEffectsEnabled(soundEffectsEnabled);
    }

    public void setSystemUiVisibility(int visibility) {
        this.my_view.setSystemUiVisibility(visibility);
    }

    public void setTag(int key, Object tag) {
        this.my_view.setTag(key, tag);
    }

    public void setTag(Object tag) {
        this.my_view.setTag(tag);
    }

    public final void setTop(int top) {
        this.my_view.setTop(top);
    }

    public void setTouchDelegate(TouchDelegate delegate) {
        this.my_view.setTouchDelegate(delegate);
    }

    public void setTranslationX(float translationX) {
        this.my_view.setTranslationX(translationX);
    }

    public void setTranslationY(float translationY) {
        this.my_view.setTranslationY(translationY);
    }

    public void setVerticalFadingEdgeEnabled(boolean verticalFadingEdgeEnabled) {
        this.my_view.setVerticalFadingEdgeEnabled(verticalFadingEdgeEnabled);
    }

    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        this.my_view.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }

    public void setVerticalScrollbarPosition(int position) {
        this.my_view.setVerticalScrollbarPosition(position);
    }

    public void setVisibility(int visibility) {
        this.my_view.setVisibility(visibility);
    }

    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        this.my_view.setWillNotCacheDrawing(willNotCacheDrawing);
    }

    public void setWillNotDraw(boolean willNotDraw) {
        this.my_view.setWillNotDraw(willNotDraw);
    }

    public void setX(float x) {
        this.my_view.setX(x);
    }

    public void setY(float y) {
        this.my_view.setY(y);
    }

    public boolean showContextMenu() {
        return this.my_view.showContextMenu();
    }

    public ActionMode startActionMode(Callback callback) {
        return this.my_view.startActionMode(callback);
    }

    public void startAnimation(Animation animation) {
        this.my_view.startAnimation(animation);
    }

    public final boolean startDrag(ClipData data, DragShadowBuilder shadowBuilder, Object myLocalState, int flags) {
        return this.my_view.startDrag(data, shadowBuilder, myLocalState, flags);
    }

    public String toString() {
        return this.my_view.toString();
    }

    public void unscheduleDrawable(Drawable who, Runnable what) {
        this.my_view.unscheduleDrawable(who, what);
    }

    public void unscheduleDrawable(Drawable who) {
        this.my_view.unscheduleDrawable(who);
    }

    public boolean willNotCacheDrawing() {
        return this.my_view.willNotCacheDrawing();
    }

    public boolean willNotDraw() {
        return this.my_view.willNotDraw();
    }

    public void setAlarmContext(Context context) {
        this.alarm_context = context;
    }

    private PendingIntent createPendingIntent(AlarmItem new_alarm) {
        Intent intent = new Intent(this.alarm_context, SmsAlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("phone_number", new_alarm.phone_number);
        bundle.putCharSequence("message", new_alarm.message);
        bundle.putInt("alarm_id", new_alarm.alarm_number);
        intent.putExtras(bundle);
        return PendingIntent.getService(this.alarm_context, new_alarm.alarm_number, intent, 0);
    }

    private void updateAlarmList() {
        this.items = new ArrayList();
        try {
            Scanner reader = new Scanner(new InputStreamReader(this.alarm_context.openFileInput("sms_alarm.dat")));
            System.out.println("Opened " + "sms_alarm.dat" + " for reading");
            while (reader.hasNext()) {
                System.out.println("Reading next alarm!");
                String phone_num = reader.next();
                Long time_in_millis = Long.valueOf(reader.nextLong());
                int alarm_num = reader.nextInt();
                boolean reminder = reader.nextBoolean();
                AlarmItem new_alarm = new AlarmItem(phone_num, time_in_millis.longValue(), reader.nextLine(), alarm_num);
                new_alarm.setReminder(reminder);
                this.items.add(new_alarm);
            }
            System.out.println("Done reading from file.");
            reader.close();
        } catch (Exception e) {
            System.out.println("Error: Failed to read from file.");
        }
        System.out.println("Read " + Integer.toString(this.items.size()) + " alarms from file.");
    }

    public void addAlarm(String phone_number, Calendar time, String message) {
        int i = 0;
        updateAlarmList();
        AlarmItem new_alarm = new AlarmItem(phone_number, time, message, this.alarm_number);
        this.items.add(new_alarm);
        Intent intent = new Intent(this.alarm_context, SmsAlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("phone_number", new_alarm.phone_number);
        bundle.putCharSequence("message", new_alarm.message);
        bundle.putInt("alarm_id", new_alarm.alarm_number);
        intent.putExtras(bundle);
        ((AlarmManager) this.alarm_context.getSystemService("alarm")).set(i, time.getTimeInMillis(), PendingIntent.getService(this.alarm_context, new_alarm.alarm_number, intent, i));
        Toast.makeText(this.alarm_context, "alarm set!", i).show();
        this.alarm_number++;
        System.out.println("Number of alarms: " + Integer.toString(this.items.size()));
        saveAlarms();
    }

    public boolean isExpired(SmsMessage msg) {
        boolean z = false;
        try {
            if (msg.getMessageBody().length() <= 10 || Integer.decode(msg.getMessageBody()).intValue() != 0) {
                return z;
            }
            return true;
        } catch (Exception e) {
            return z;
        }
    }

    private boolean comparePhoneNumbers(String p1, String p2, int len) {
        return p1.substring(p1.length() - len).equals(p2.substring(p2.length() - len));
    }

    public void addAlarm(AlarmItem new_alarm) {
        int i = 0;
        updateAlarmList();
        int i2 = this.alarm_number;
        this.alarm_number = i2 + 1;
        new_alarm.alarm_number = i2;
        System.out.println("new alarm is a reminder = " + Boolean.toString(new_alarm.isReminder()));
        this.items.add(new_alarm);
        Intent intent = new Intent(this.alarm_context, SmsAlarmReceiver.class);
        Bundle bundle = new Bundle();
        bundle.putCharSequence("phone_number", new_alarm.phone_number);
        bundle.putCharSequence("message", new_alarm.message);
        bundle.putInt("alarm_id", new_alarm.alarm_number);
        intent.putExtras(bundle);
        ((AlarmManager) this.alarm_context.getSystemService("alarm")).set(i, new_alarm.time_millis(), PendingIntent.getService(this.alarm_context, new_alarm.alarm_number, intent, i));
        System.out.println("Number of alarms: " + Integer.toString(this.items.size()));
        saveAlarms();
    }

    public int getAlarm_number() {
        return this.alarm_number;
    }

    public void setAlarm_number(int alarm_number) {
        this.alarm_number = alarm_number;
    }

    public Context getAlarm_context() {
        return this.alarm_context;
    }

    public void setAlarm_context(Context alarm_context) {
        this.alarm_context = alarm_context;
    }

    public List<AlarmItem> getItems() {
        return this.items;
    }

    public void setItems(List<AlarmItem> items) {
        this.items = items;
    }

    public String getFile_name() {
        return "sms_alarm.dat";
    }

    public static void setInstance(AlarmContent instance) {
        instance = instance;
    }

    private void saveAlarms() {
        try {
            PrintWriter file_writer = new PrintWriter(this.alarm_context.openFileOutput("sms_alarm.dat", 0));
            System.out.println("Opened " + "sms_alarm.dat" + " for writing");
            System.out.println("Writing " + Integer.toString(this.items.size()) + " alarms to file");
            for (AlarmItem alarm_item : this.items) {
                StringBuffer sb = new StringBuffer();
                sb.append(alarm_item.phone_number);
                sb.append(' ');
                sb.append(Long.toString(alarm_item.time_millis()));
                sb.append(' ');
                sb.append(Integer.toString(alarm_item.alarm_number));
                sb.append(' ');
                sb.append(Boolean.toString(alarm_item.isReminder()));
                sb.append(' ');
                sb.append(alarm_item.message);
                System.out.println("Writing: " + sb.toString());
                file_writer.println(sb.toString());
            }
            file_writer.close();
            System.out.println("Done writing");
        } catch (Exception e) {
            Toast.makeText(this.alarm_context, "Error: Could not write to file", 1).show();
        }
    }

    public void loadAlarms() {
        this.items = new ArrayList();
        try {
            Scanner reader = new Scanner(new InputStreamReader(this.alarm_context.openFileInput("sms_alarm.dat")));
            System.out.println("Opened " + "sms_alarm.dat" + " for reading");
            while (reader.hasNext()) {
                System.out.println("Reading next alarm!");
                String phone_num = reader.next();
                Long time_in_millis = Long.valueOf(reader.nextLong());
                int alarm_num = reader.nextInt();
                boolean reminder = reader.nextBoolean();
                AlarmItem new_alarm = new AlarmItem(phone_num, time_in_millis.longValue(), reader.nextLine(), alarm_num);
                new_alarm.setReminder(reminder);
                this.items.add(new_alarm);
            }
            System.out.println("Done reading from file.");
            reader.close();
        } catch (Exception e) {
            System.out.println("Error: Failed to read from file.");
        }
        System.out.println("Read " + Integer.toString(this.items.size()) + " alarms from file.");
    }

    public List<AlarmItem> getAlarms() {
        updateAlarmList();
        return this.items;
    }

    public AlarmItem getAlarmFromPosition(int position) {
        return (AlarmItem) this.items.get(position);
    }

    private void cancelAlarm(AlarmItem alarm_item) {
        ((AlarmManager) this.alarm_context.getSystemService("alarm")).cancel(createPendingIntent(alarm_item));
    }

    public void removeAlarmByPosition(int position) {
        updateAlarmList();
        cancelAlarm((AlarmItem) this.items.get(position));
        this.items.remove(position);
        saveAlarms();
    }

    public void removeAlarmById(int id) {
        updateAlarmList();
        for (int i = 0; i < this.items.size(); i++) {
            if (((AlarmItem) this.items.get(i)).alarm_number == id) {
                cancelAlarm((AlarmItem) this.items.get(i));
                this.items.remove(i);
                saveAlarms();
                return;
            }
        }
    }

    public AlarmItem getAlarmById(int id) {
        updateAlarmList();
        for (AlarmItem alarm_item : this.items) {
            System.out.println("Looking for id=" + Integer.toString(id) + ", found " + Integer.toString(alarm_item.alarm_number));
            if (alarm_item.alarm_number == id) {
                return alarm_item;
            }
        }
        return null;
    }

    private boolean needsToBeDeleted(AlarmItem ai, SmsMessage msg) {
        return (ai.matchesPhoneNumber(msg.getOriginatingAddress()) && ai.isReminder()) || isExpired(msg);
    }

    public void removeAllAlamrs() {
        for (int i = 0; i < this.items.size(); i++) {
            removeAlarmByPosition(i);
        }
    }

    public void deleteRemindersFor(SmsMessage msg) {
        updateAlarmList();
        int i = 0;
        while (i < this.items.size()) {
            if (needsToBeDeleted((AlarmItem) this.items.get(i), msg)) {
                cancelAlarm((AlarmItem) this.items.get(i));
                this.items.remove(i);
                i--;
            }
            i++;
        }
        saveAlarms();
    }
}
