package android.support.v7.widget;

import android.app.PendingIntent;
import android.app.SearchableInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.ClassLoaderCreator;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.appcompat.R;
import android.support.v7.view.CollapsibleActionView;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewConfiguration;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

public class SearchView extends LinearLayoutCompat implements CollapsibleActionView {
    static final boolean DBG = false;
    static final AutoCompleteTextViewReflector HIDDEN_METHOD_INVOKER = new AutoCompleteTextViewReflector();
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    static final String LOG_TAG = "SearchView";
    private Bundle mAppSearchData;
    private boolean mClearingFocus;
    final ImageView mCloseButton;
    private final ImageView mCollapsedIcon;
    private int mCollapsedImeOptions;
    private final CharSequence mDefaultQueryHint;
    private final View mDropDownAnchor;
    private boolean mExpandedInActionView;
    final ImageView mGoButton;
    private boolean mIconified;
    private boolean mIconifiedByDefault;
    private int mMaxWidth;
    private CharSequence mOldQueryText;
    private final OnClickListener mOnClickListener;
    private OnCloseListener mOnCloseListener;
    private final OnEditorActionListener mOnEditorActionListener;
    private final OnItemClickListener mOnItemClickListener;
    private final OnItemSelectedListener mOnItemSelectedListener;
    private OnQueryTextListener mOnQueryChangeListener;
    OnFocusChangeListener mOnQueryTextFocusChangeListener;
    private OnClickListener mOnSearchClickListener;
    private OnSuggestionListener mOnSuggestionListener;
    private final WeakHashMap<String, ConstantState> mOutsideDrawablesCache;
    private CharSequence mQueryHint;
    private boolean mQueryRefinement;
    private Runnable mReleaseCursorRunnable;
    final ImageView mSearchButton;
    private final View mSearchEditFrame;
    private final Drawable mSearchHintIcon;
    private final View mSearchPlate;
    final SearchAutoComplete mSearchSrcTextView;
    private Rect mSearchSrcTextViewBounds;
    private Rect mSearchSrtTextViewBoundsExpanded;
    SearchableInfo mSearchable;
    private final View mSubmitArea;
    private boolean mSubmitButtonEnabled;
    private final int mSuggestionCommitIconResId;
    private final int mSuggestionRowLayout;
    CursorAdapter mSuggestionsAdapter;
    private int[] mTemp;
    private int[] mTemp2;
    OnKeyListener mTextKeyListener;
    private TextWatcher mTextWatcher;
    private UpdatableTouchDelegate mTouchDelegate;
    private final Runnable mUpdateDrawableStateRunnable;
    private CharSequence mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    final ImageView mVoiceButton;
    private boolean mVoiceButtonEnabled;
    private final Intent mVoiceWebSearchIntent;

    private static class AutoCompleteTextViewReflector {
        private Method doAfterTextChanged;
        private Method doBeforeTextChanged;
        private Method ensureImeVisible;
        private Method showSoftInputUnchecked;

        AutoCompleteTextViewReflector() {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.<init>():void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.lambda$0(DepthTraversal.java:13)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
            /*
            r6 = this;
            r6.<init>();
            r0 = 0;
            r1 = 1;
            r2 = android.widget.AutoCompleteTextView.class;	 Catch:{ NoSuchMethodException -> 0x0016 }
            r3 = "doBeforeTextChanged";	 Catch:{ NoSuchMethodException -> 0x0016 }
            r4 = new java.lang.Class[r0];	 Catch:{ NoSuchMethodException -> 0x0016 }
            r2 = r2.getDeclaredMethod(r3, r4);	 Catch:{ NoSuchMethodException -> 0x0016 }
            r6.doBeforeTextChanged = r2;	 Catch:{ NoSuchMethodException -> 0x0016 }
            r2 = r6.doBeforeTextChanged;	 Catch:{ NoSuchMethodException -> 0x0016 }
            r2.setAccessible(r1);	 Catch:{ NoSuchMethodException -> 0x0016 }
        L_0x0016:
            r2 = android.widget.AutoCompleteTextView.class;	 Catch:{ NoSuchMethodException -> 0x0027 }
            r3 = "doAfterTextChanged";	 Catch:{ NoSuchMethodException -> 0x0027 }
            r4 = new java.lang.Class[r0];	 Catch:{ NoSuchMethodException -> 0x0027 }
            r2 = r2.getDeclaredMethod(r3, r4);	 Catch:{ NoSuchMethodException -> 0x0027 }
            r6.doAfterTextChanged = r2;	 Catch:{ NoSuchMethodException -> 0x0027 }
            r2 = r6.doAfterTextChanged;	 Catch:{ NoSuchMethodException -> 0x0027 }
            r2.setAccessible(r1);	 Catch:{ NoSuchMethodException -> 0x0027 }
        L_0x0027:
            r2 = android.widget.AutoCompleteTextView.class;	 Catch:{ NoSuchMethodException -> 0x003c }
            r3 = "ensureImeVisible";	 Catch:{ NoSuchMethodException -> 0x003c }
            r4 = new java.lang.Class[r1];	 Catch:{ NoSuchMethodException -> 0x003c }
            r5 = java.lang.Boolean.TYPE;	 Catch:{ NoSuchMethodException -> 0x003c }
            r4[r0] = r5;	 Catch:{ NoSuchMethodException -> 0x003c }
            r0 = r2.getMethod(r3, r4);	 Catch:{ NoSuchMethodException -> 0x003c }
            r6.ensureImeVisible = r0;	 Catch:{ NoSuchMethodException -> 0x003c }
            r0 = r6.ensureImeVisible;	 Catch:{ NoSuchMethodException -> 0x003c }
            r0.setAccessible(r1);	 Catch:{ NoSuchMethodException -> 0x003c }
        L_0x003c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.<init>():void");
        }

        void doBeforeTextChanged(android.widget.AutoCompleteTextView r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.doBeforeTextChanged(android.widget.AutoCompleteTextView):void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.lambda$0(DepthTraversal.java:13)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
            /*
            r2 = this;
            r0 = r2.doBeforeTextChanged;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = r2.doBeforeTextChanged;	 Catch:{ Exception -> 0x000c }
            r1 = 0;	 Catch:{ Exception -> 0x000c }
            r1 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x000c }
            r0.invoke(r3, r1);	 Catch:{ Exception -> 0x000c }
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.doBeforeTextChanged(android.widget.AutoCompleteTextView):void");
        }

        void doAfterTextChanged(android.widget.AutoCompleteTextView r3) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.doAfterTextChanged(android.widget.AutoCompleteTextView):void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.lambda$0(DepthTraversal.java:13)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
            /*
            r2 = this;
            r0 = r2.doAfterTextChanged;
            if (r0 == 0) goto L_0x000c;
        L_0x0004:
            r0 = r2.doAfterTextChanged;	 Catch:{ Exception -> 0x000c }
            r1 = 0;	 Catch:{ Exception -> 0x000c }
            r1 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x000c }
            r0.invoke(r3, r1);	 Catch:{ Exception -> 0x000c }
        L_0x000c:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.doAfterTextChanged(android.widget.AutoCompleteTextView):void");
        }

        void ensureImeVisible(android.widget.AutoCompleteTextView r4, boolean r5) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.ensureImeVisible(android.widget.AutoCompleteTextView, boolean):void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.dex.visitors.DepthTraversal.lambda$0(DepthTraversal.java:13)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:13)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
            /*
            r3 = this;
            r0 = r3.ensureImeVisible;
            if (r0 == 0) goto L_0x0013;
        L_0x0004:
            r0 = r3.ensureImeVisible;	 Catch:{ Exception -> 0x0013 }
            r1 = 1;	 Catch:{ Exception -> 0x0013 }
            r1 = new java.lang.Object[r1];	 Catch:{ Exception -> 0x0013 }
            r2 = 0;	 Catch:{ Exception -> 0x0013 }
            r5 = java.lang.Boolean.valueOf(r5);	 Catch:{ Exception -> 0x0013 }
            r1[r2] = r5;	 Catch:{ Exception -> 0x0013 }
            r0.invoke(r4, r1);	 Catch:{ Exception -> 0x0013 }
        L_0x0013:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.AutoCompleteTextViewReflector.ensureImeVisible(android.widget.AutoCompleteTextView, boolean):void");
        }
    }

    public interface OnCloseListener {
        boolean onClose();
    }

    public interface OnQueryTextListener {
        boolean onQueryTextChange(String str);

        boolean onQueryTextSubmit(String str);
    }

    public interface OnSuggestionListener {
        boolean onSuggestionClick(int i);

        boolean onSuggestionSelect(int i);
    }

    private static class UpdatableTouchDelegate extends TouchDelegate {
        private final Rect mActualBounds = new Rect();
        private boolean mDelegateTargeted;
        private final View mDelegateView;
        private final int mSlop;
        private final Rect mSlopBounds = new Rect();
        private final Rect mTargetBounds = new Rect();

        public UpdatableTouchDelegate(Rect rect, Rect rect2, View view) {
            super(rect, view);
            this.mSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
            setBounds(rect, rect2);
            this.mDelegateView = view;
        }

        public void setBounds(Rect rect, Rect rect2) {
            this.mTargetBounds.set(rect);
            this.mSlopBounds.set(rect);
            this.mSlopBounds.inset(-this.mSlop, -this.mSlop);
            this.mActualBounds.set(rect2);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean onTouchEvent(android.view.MotionEvent r7) {
            /*
            r6 = this;
            r0 = r7.getX();
            r0 = (int) r0;
            r1 = r7.getY();
            r1 = (int) r1;
            r2 = r7.getAction();
            r3 = 1;
            r4 = 0;
            switch(r2) {
                case 0: goto L_0x0027;
                case 1: goto L_0x0019;
                case 2: goto L_0x0019;
                case 3: goto L_0x0014;
                default: goto L_0x0013;
            };
        L_0x0013:
            goto L_0x0033;
        L_0x0014:
            r2 = r6.mDelegateTargeted;
            r6.mDelegateTargeted = r4;
            goto L_0x0034;
        L_0x0019:
            r2 = r6.mDelegateTargeted;
            if (r2 == 0) goto L_0x0034;
        L_0x001d:
            r5 = r6.mSlopBounds;
            r5 = r5.contains(r0, r1);
            if (r5 != 0) goto L_0x0034;
        L_0x0025:
            r3 = r4;
            goto L_0x0034;
        L_0x0027:
            r2 = r6.mTargetBounds;
            r2 = r2.contains(r0, r1);
            if (r2 == 0) goto L_0x0033;
        L_0x002f:
            r6.mDelegateTargeted = r3;
            r2 = r3;
            goto L_0x0034;
        L_0x0033:
            r2 = r4;
        L_0x0034:
            if (r2 == 0) goto L_0x006b;
        L_0x0036:
            if (r3 == 0) goto L_0x0056;
        L_0x0038:
            r2 = r6.mActualBounds;
            r2 = r2.contains(r0, r1);
            if (r2 != 0) goto L_0x0056;
        L_0x0040:
            r0 = r6.mDelegateView;
            r0 = r0.getWidth();
            r0 = r0 / 2;
            r0 = (float) r0;
            r1 = r6.mDelegateView;
            r1 = r1.getHeight();
            r1 = r1 / 2;
            r1 = (float) r1;
            r7.setLocation(r0, r1);
            goto L_0x0065;
        L_0x0056:
            r2 = r6.mActualBounds;
            r2 = r2.left;
            r0 = r0 - r2;
            r0 = (float) r0;
            r2 = r6.mActualBounds;
            r2 = r2.top;
            r1 = r1 - r2;
            r1 = (float) r1;
            r7.setLocation(r0, r1);
        L_0x0065:
            r0 = r6.mDelegateView;
            r4 = r0.dispatchTouchEvent(r7);
        L_0x006b:
            return r4;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.SearchView.UpdatableTouchDelegate.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }

    static class SavedState extends AbsSavedState {
        public static final Creator<SavedState> CREATOR = new ClassLoaderCreator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, null);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean isIconified;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.isIconified = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeValue(Boolean.valueOf(this.isIconified));
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SearchView.SavedState{");
            stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
            stringBuilder.append(" isIconified=");
            stringBuilder.append(this.isIconified);
            stringBuilder.append("}");
            return stringBuilder.toString();
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class SearchAutoComplete extends AppCompatAutoCompleteTextView {
        private boolean mHasPendingShowSoftInputRequest;
        final Runnable mRunShowSoftInputIfNecessary;
        private SearchView mSearchView;
        private int mThreshold;

        public void performCompletion() {
        }

        protected void replaceText(CharSequence charSequence) {
        }

        public SearchAutoComplete(Context context) {
            this(context, null);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet) {
            this(context, attributeSet, R.attr.autoCompleteTextViewStyle);
        }

        public SearchAutoComplete(Context context, AttributeSet attributeSet, int i) {
            super(context, attributeSet, i);
            this.mRunShowSoftInputIfNecessary = new Runnable() {
                public void run() {
                    SearchAutoComplete.this.showSoftInputIfNecessary();
                }
            };
            this.mThreshold = getThreshold();
        }

        protected void onFinishInflate() {
            super.onFinishInflate();
            int i = 1;
            setMinWidth((int) TypedValue.applyDimension(i, (float) getSearchViewTextMinWidthDp(), getResources().getDisplayMetrics()));
        }

        void setSearchView(SearchView searchView) {
            this.mSearchView = searchView;
        }

        public void setThreshold(int i) {
            super.setThreshold(i);
            this.mThreshold = i;
        }

        private boolean isEmpty() {
            return TextUtils.getTrimmedLength(getText()) == 0;
        }

        public void onWindowFocusChanged(boolean z) {
            super.onWindowFocusChanged(z);
            if (z && this.mSearchView.hasFocus() && getVisibility() == 0) {
                z = true;
                this.mHasPendingShowSoftInputRequest = z;
                if (SearchView.isLandscapeMode(getContext())) {
                    SearchView.HIDDEN_METHOD_INVOKER.ensureImeVisible(this, z);
                }
            }
        }

        protected void onFocusChanged(boolean z, int i, Rect rect) {
            super.onFocusChanged(z, i, rect);
            this.mSearchView.onTextFocusChanged();
        }

        public boolean enoughToFilter() {
            return this.mThreshold <= 0 || super.enoughToFilter();
        }

        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            if (i == 4) {
                boolean z = true;
                if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                    DispatcherState keyDispatcherState = getKeyDispatcherState();
                    if (keyDispatcherState != null) {
                        keyDispatcherState.startTracking(keyEvent, this);
                    }
                    return z;
                } else if (keyEvent.getAction() == z) {
                    DispatcherState keyDispatcherState2 = getKeyDispatcherState();
                    if (keyDispatcherState2 != null) {
                        keyDispatcherState2.handleUpEvent(keyEvent);
                    }
                    if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                        this.mSearchView.clearFocus();
                        setImeVisibility(false);
                        return z;
                    }
                }
            }
            return super.onKeyPreIme(i, keyEvent);
        }

        private int getSearchViewTextMinWidthDp() {
            Configuration configuration = getResources().getConfiguration();
            int i = configuration.screenWidthDp;
            int i2 = configuration.screenHeightDp;
            if (i < 960 || i2 < 720 || configuration.orientation != 2) {
                return (i >= 600 || (i >= 640 && i2 >= 480)) ? 192 : 160;
            } else {
                return 256;
            }
        }

        public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
            InputConnection onCreateInputConnection = super.onCreateInputConnection(editorInfo);
            if (this.mHasPendingShowSoftInputRequest) {
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                post(this.mRunShowSoftInputIfNecessary);
            }
            return onCreateInputConnection;
        }

        private void showSoftInputIfNecessary() {
            if (this.mHasPendingShowSoftInputRequest) {
                boolean z = false;
                ((InputMethodManager) getContext().getSystemService("input_method")).showSoftInput(this, z);
                this.mHasPendingShowSoftInputRequest = z;
            }
        }

        private void setImeVisibility(boolean z) {
            InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
            boolean z2 = false;
            if (!z) {
                this.mHasPendingShowSoftInputRequest = z2;
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), z2);
            } else if (inputMethodManager.isActive(this)) {
                this.mHasPendingShowSoftInputRequest = z2;
                removeCallbacks(this.mRunShowSoftInputIfNecessary);
                inputMethodManager.showSoftInput(this, z2);
            } else {
                this.mHasPendingShowSoftInputRequest = true;
            }
        }
    }

    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.searchViewStyle);
    }

    public SearchView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mSearchSrcTextViewBounds = new Rect();
        this.mSearchSrtTextViewBoundsExpanded = new Rect();
        int i2 = 2;
        this.mTemp = new int[i2];
        this.mTemp2 = new int[i2];
        this.mUpdateDrawableStateRunnable = new Runnable() {
            public void run() {
                SearchView.this.updateFocusedState();
            }
        };
        this.mReleaseCursorRunnable = new Runnable() {
            public void run() {
                if (SearchView.this.mSuggestionsAdapter != null && (SearchView.this.mSuggestionsAdapter instanceof SuggestionsAdapter)) {
                    SearchView.this.mSuggestionsAdapter.changeCursor(null);
                }
            }
        };
        this.mOutsideDrawablesCache = new WeakHashMap();
        this.mOnClickListener = new OnClickListener() {
            public void onClick(View view) {
                if (view == SearchView.this.mSearchButton) {
                    SearchView.this.onSearchClicked();
                } else if (view == SearchView.this.mCloseButton) {
                    SearchView.this.onCloseClicked();
                } else if (view == SearchView.this.mGoButton) {
                    SearchView.this.onSubmitQuery();
                } else if (view == SearchView.this.mVoiceButton) {
                    SearchView.this.onVoiceClicked();
                } else if (view == SearchView.this.mSearchSrcTextView) {
                    SearchView.this.forceSuggestionQuery();
                }
            }
        };
        this.mTextKeyListener = new OnKeyListener() {
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                boolean z = false;
                if (SearchView.this.mSearchable == null) {
                    return z;
                }
                if (SearchView.this.mSearchSrcTextView.isPopupShowing() && SearchView.this.mSearchSrcTextView.getListSelection() != -1) {
                    return SearchView.this.onSuggestionsKey(view, i, keyEvent);
                }
                if (!SearchView.this.mSearchSrcTextView.isEmpty() && keyEvent.hasNoModifiers()) {
                    boolean z2 = true;
                    if (keyEvent.getAction() == z2 && i == 66) {
                        view.cancelLongPress();
                        SearchView.this.launchQuerySearch(z, null, SearchView.this.mSearchSrcTextView.getText().toString());
                        return z2;
                    }
                }
                return z;
            }
        };
        this.mOnEditorActionListener = new OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                SearchView.this.onSubmitQuery();
                return true;
            }
        };
        this.mOnItemClickListener = new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                SearchView.this.onItemClicked(i, 0, null);
            }
        };
        this.mOnItemSelectedListener = new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                SearchView.this.onItemSelected(i);
            }
        };
        this.mTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                SearchView.this.onTextChanged(charSequence);
            }
        };
        int i3 = 0;
        TintTypedArray obtainStyledAttributes = TintTypedArray.obtainStyledAttributes(context, attributeSet, R.styleable.SearchView, i, i3);
        boolean z = true;
        LayoutInflater.from(context).inflate(obtainStyledAttributes.getResourceId(R.styleable.SearchView_layout, R.layout.abc_search_view), this, z);
        this.mSearchSrcTextView = (SearchAutoComplete) findViewById(R.id.search_src_text);
        this.mSearchSrcTextView.setSearchView(this);
        this.mSearchEditFrame = findViewById(R.id.search_edit_frame);
        this.mSearchPlate = findViewById(R.id.search_plate);
        this.mSubmitArea = findViewById(R.id.submit_area);
        this.mSearchButton = (ImageView) findViewById(R.id.search_button);
        this.mGoButton = (ImageView) findViewById(R.id.search_go_btn);
        this.mCloseButton = (ImageView) findViewById(R.id.search_close_btn);
        this.mVoiceButton = (ImageView) findViewById(R.id.search_voice_btn);
        this.mCollapsedIcon = (ImageView) findViewById(R.id.search_mag_icon);
        ViewCompat.setBackground(this.mSearchPlate, obtainStyledAttributes.getDrawable(R.styleable.SearchView_queryBackground));
        ViewCompat.setBackground(this.mSubmitArea, obtainStyledAttributes.getDrawable(R.styleable.SearchView_submitBackground));
        this.mSearchButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchIcon));
        this.mGoButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_goIcon));
        this.mCloseButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_closeIcon));
        this.mVoiceButton.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_voiceIcon));
        this.mCollapsedIcon.setImageDrawable(obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchIcon));
        this.mSearchHintIcon = obtainStyledAttributes.getDrawable(R.styleable.SearchView_searchHintIcon);
        TooltipCompat.setTooltipText(this.mSearchButton, getResources().getString(R.string.abc_searchview_description_search));
        this.mSuggestionRowLayout = obtainStyledAttributes.getResourceId(R.styleable.SearchView_suggestionRowLayout, R.layout.abc_search_dropdown_item_icons_2line);
        this.mSuggestionCommitIconResId = obtainStyledAttributes.getResourceId(R.styleable.SearchView_commitIcon, i3);
        this.mSearchButton.setOnClickListener(this.mOnClickListener);
        this.mCloseButton.setOnClickListener(this.mOnClickListener);
        this.mGoButton.setOnClickListener(this.mOnClickListener);
        this.mVoiceButton.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.setOnClickListener(this.mOnClickListener);
        this.mSearchSrcTextView.addTextChangedListener(this.mTextWatcher);
        this.mSearchSrcTextView.setOnEditorActionListener(this.mOnEditorActionListener);
        this.mSearchSrcTextView.setOnItemClickListener(this.mOnItemClickListener);
        this.mSearchSrcTextView.setOnItemSelectedListener(this.mOnItemSelectedListener);
        this.mSearchSrcTextView.setOnKeyListener(this.mTextKeyListener);
        this.mSearchSrcTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (SearchView.this.mOnQueryTextFocusChangeListener != null) {
                    SearchView.this.mOnQueryTextFocusChangeListener.onFocusChange(SearchView.this, z);
                }
            }
        });
        setIconifiedByDefault(obtainStyledAttributes.getBoolean(R.styleable.SearchView_iconifiedByDefault, z));
        i = -1;
        int dimensionPixelSize = obtainStyledAttributes.getDimensionPixelSize(R.styleable.SearchView_android_maxWidth, i);
        if (dimensionPixelSize != i) {
            setMaxWidth(dimensionPixelSize);
        }
        this.mDefaultQueryHint = obtainStyledAttributes.getText(R.styleable.SearchView_defaultQueryHint);
        this.mQueryHint = obtainStyledAttributes.getText(R.styleable.SearchView_queryHint);
        dimensionPixelSize = obtainStyledAttributes.getInt(R.styleable.SearchView_android_imeOptions, i);
        if (dimensionPixelSize != i) {
            setImeOptions(dimensionPixelSize);
        }
        dimensionPixelSize = obtainStyledAttributes.getInt(R.styleable.SearchView_android_inputType, i);
        if (dimensionPixelSize != i) {
            setInputType(dimensionPixelSize);
        }
        setFocusable(obtainStyledAttributes.getBoolean(R.styleable.SearchView_android_focusable, z));
        obtainStyledAttributes.recycle();
        this.mVoiceWebSearchIntent = new Intent("android.speech.action.WEB_SEARCH");
        int i4 = 268435456;
        this.mVoiceWebSearchIntent.addFlags(i4);
        this.mVoiceWebSearchIntent.putExtra("android.speech.extra.LANGUAGE_MODEL", "web_search");
        this.mVoiceAppSearchIntent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        this.mVoiceAppSearchIntent.addFlags(i4);
        this.mDropDownAnchor = findViewById(this.mSearchSrcTextView.getDropDownAnchor());
        if (this.mDropDownAnchor != null) {
            this.mDropDownAnchor.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    SearchView.this.adjustDropDownSizeAndPosition();
                }
            });
        }
        updateViewsVisibility(this.mIconifiedByDefault);
        updateQueryHint();
    }

    int getSuggestionRowLayout() {
        return this.mSuggestionRowLayout;
    }

    int getSuggestionCommitIconResId() {
        return this.mSuggestionCommitIconResId;
    }

    public void setSearchableInfo(SearchableInfo searchableInfo) {
        this.mSearchable = searchableInfo;
        if (this.mSearchable != null) {
            updateSearchAutoComplete();
            updateQueryHint();
        }
        this.mVoiceButtonEnabled = hasVoiceSearch();
        if (this.mVoiceButtonEnabled) {
            this.mSearchSrcTextView.setPrivateImeOptions("nm");
        }
        updateViewsVisibility(isIconified());
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void setAppSearchData(Bundle bundle) {
        this.mAppSearchData = bundle;
    }

    public void setImeOptions(int i) {
        this.mSearchSrcTextView.setImeOptions(i);
    }

    public int getImeOptions() {
        return this.mSearchSrcTextView.getImeOptions();
    }

    public void setInputType(int i) {
        this.mSearchSrcTextView.setInputType(i);
    }

    public int getInputType() {
        return this.mSearchSrcTextView.getInputType();
    }

    public boolean requestFocus(int i, Rect rect) {
        boolean z = false;
        if (this.mClearingFocus || !isFocusable()) {
            return z;
        }
        if (isIconified()) {
            return super.requestFocus(i, rect);
        }
        boolean requestFocus = this.mSearchSrcTextView.requestFocus(i, rect);
        if (requestFocus) {
            updateViewsVisibility(z);
        }
        return requestFocus;
    }

    public void clearFocus() {
        this.mClearingFocus = true;
        super.clearFocus();
        this.mSearchSrcTextView.clearFocus();
        boolean z = false;
        this.mSearchSrcTextView.setImeVisibility(z);
        this.mClearingFocus = z;
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        this.mOnQueryChangeListener = onQueryTextListener;
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
    }

    public void setOnQueryTextFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.mOnQueryTextFocusChangeListener = onFocusChangeListener;
    }

    public void setOnSuggestionListener(OnSuggestionListener onSuggestionListener) {
        this.mOnSuggestionListener = onSuggestionListener;
    }

    public void setOnSearchClickListener(OnClickListener onClickListener) {
        this.mOnSearchClickListener = onClickListener;
    }

    public CharSequence getQuery() {
        return this.mSearchSrcTextView.getText();
    }

    public void setQuery(CharSequence charSequence, boolean z) {
        this.mSearchSrcTextView.setText(charSequence);
        if (charSequence != null) {
            this.mSearchSrcTextView.setSelection(this.mSearchSrcTextView.length());
            this.mUserQuery = charSequence;
        }
        if (z && !TextUtils.isEmpty(charSequence)) {
            onSubmitQuery();
        }
    }

    public void setQueryHint(@Nullable CharSequence charSequence) {
        this.mQueryHint = charSequence;
        updateQueryHint();
    }

    @Nullable
    public CharSequence getQueryHint() {
        if (this.mQueryHint != null) {
            return this.mQueryHint;
        }
        if (this.mSearchable == null || this.mSearchable.getHintId() == 0) {
            return this.mDefaultQueryHint;
        }
        return getContext().getText(this.mSearchable.getHintId());
    }

    public void setIconifiedByDefault(boolean z) {
        if (this.mIconifiedByDefault != z) {
            this.mIconifiedByDefault = z;
            updateViewsVisibility(z);
            updateQueryHint();
        }
    }

    public boolean isIconfiedByDefault() {
        return this.mIconifiedByDefault;
    }

    public void setIconified(boolean z) {
        if (z) {
            onCloseClicked();
        } else {
            onSearchClicked();
        }
    }

    public boolean isIconified() {
        return this.mIconified;
    }

    public void setSubmitButtonEnabled(boolean z) {
        this.mSubmitButtonEnabled = z;
        updateViewsVisibility(isIconified());
    }

    public boolean isSubmitButtonEnabled() {
        return this.mSubmitButtonEnabled;
    }

    public void setQueryRefinementEnabled(boolean z) {
        this.mQueryRefinement = z;
        if (this.mSuggestionsAdapter instanceof SuggestionsAdapter) {
            ((SuggestionsAdapter) this.mSuggestionsAdapter).setQueryRefinement(z ? 2 : 1);
        }
    }

    public boolean isQueryRefinementEnabled() {
        return this.mQueryRefinement;
    }

    public void setSuggestionsAdapter(CursorAdapter cursorAdapter) {
        this.mSuggestionsAdapter = cursorAdapter;
        this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
    }

    public CursorAdapter getSuggestionsAdapter() {
        return this.mSuggestionsAdapter;
    }

    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    protected void onMeasure(int i, int i2) {
        if (isIconified()) {
            super.onMeasure(i, i2);
            return;
        }
        int mode = MeasureSpec.getMode(i);
        i = MeasureSpec.getSize(i);
        int i3 = Integer.MIN_VALUE;
        int i4 = 1073741824;
        if (mode == i3) {
            i = this.mMaxWidth > 0 ? Math.min(this.mMaxWidth, i) : Math.min(getPreferredWidth(), i);
        } else if (mode == 0) {
            i = this.mMaxWidth > 0 ? this.mMaxWidth : getPreferredWidth();
        } else if (mode == i4 && this.mMaxWidth > 0) {
            i = Math.min(this.mMaxWidth, i);
        }
        mode = MeasureSpec.getMode(i2);
        i2 = MeasureSpec.getSize(i2);
        if (mode == i3) {
            i2 = Math.min(getPreferredHeight(), i2);
        } else if (mode == 0) {
            i2 = getPreferredHeight();
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(i, i4), MeasureSpec.makeMeasureSpec(i2, i4));
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (z) {
            getChildBoundsWithinSearchView(this.mSearchSrcTextView, this.mSearchSrcTextViewBounds);
            this.mSearchSrtTextViewBoundsExpanded.set(this.mSearchSrcTextViewBounds.left, 0, this.mSearchSrcTextViewBounds.right, i4 - i2);
            if (this.mTouchDelegate == null) {
                this.mTouchDelegate = new UpdatableTouchDelegate(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds, this.mSearchSrcTextView);
                setTouchDelegate(this.mTouchDelegate);
                return;
            }
            this.mTouchDelegate.setBounds(this.mSearchSrtTextViewBoundsExpanded, this.mSearchSrcTextViewBounds);
        }
    }

    private void getChildBoundsWithinSearchView(View view, Rect rect) {
        view.getLocationInWindow(this.mTemp);
        getLocationInWindow(this.mTemp2);
        int i = 1;
        int i2 = this.mTemp[i] - this.mTemp2[i];
        int i3 = 0;
        i = this.mTemp[i3] - this.mTemp2[i3];
        rect.set(i, i2, view.getWidth() + i, view.getHeight() + i2);
    }

    private int getPreferredWidth() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_width);
    }

    private int getPreferredHeight() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.abc_search_view_preferred_height);
    }

    private void updateViewsVisibility(boolean z) {
        this.mIconified = z;
        int i = 8;
        boolean z2 = false;
        boolean z3 = true;
        boolean isEmpty = TextUtils.isEmpty(this.mSearchSrcTextView.getText()) ^ z3;
        this.mSearchButton.setVisibility(z ? z2 : i);
        updateSubmitButton(isEmpty);
        this.mSearchEditFrame.setVisibility(z ? i : z2);
        if (!(this.mCollapsedIcon.getDrawable() == null || this.mIconifiedByDefault)) {
            i = z2;
        }
        this.mCollapsedIcon.setVisibility(i);
        updateCloseButton();
        if (!isEmpty) {
            z2 = z3;
        }
        updateVoiceButton(z2);
        updateSubmitArea();
    }

    private boolean hasVoiceSearch() {
        boolean z = false;
        if (this.mSearchable != null && this.mSearchable.getVoiceSearchEnabled()) {
            Intent intent = null;
            if (this.mSearchable.getVoiceSearchLaunchWebSearch()) {
                intent = this.mVoiceWebSearchIntent;
            } else if (this.mSearchable.getVoiceSearchLaunchRecognizer()) {
                intent = this.mVoiceAppSearchIntent;
            }
            if (intent != null) {
                if (getContext().getPackageManager().resolveActivity(intent, 65536) != null) {
                    z = true;
                }
                return z;
            }
        }
        return z;
    }

    private boolean isSubmitAreaEnabled() {
        return (this.mSubmitButtonEnabled || this.mVoiceButtonEnabled) && !isIconified();
    }

    private void updateSubmitButton(boolean z) {
        int i = (this.mSubmitButtonEnabled && isSubmitAreaEnabled() && hasFocus() && (z || !this.mVoiceButtonEnabled)) ? 0 : 8;
        this.mGoButton.setVisibility(i);
    }

    private void updateSubmitArea() {
        int i = (isSubmitAreaEnabled() && (this.mGoButton.getVisibility() == 0 || this.mVoiceButton.getVisibility() == 0)) ? 0 : 8;
        this.mSubmitArea.setVisibility(i);
    }

    private void updateCloseButton() {
        int i = 1;
        int isEmpty = TextUtils.isEmpty(this.mSearchSrcTextView.getText()) ^ i;
        int i2 = 0;
        if (isEmpty == 0 && (!this.mIconifiedByDefault || this.mExpandedInActionView)) {
            i = i2;
        }
        ImageView imageView = this.mCloseButton;
        if (i == 0) {
            i2 = 8;
        }
        imageView.setVisibility(i2);
        Drawable drawable = this.mCloseButton.getDrawable();
        if (drawable != null) {
            drawable.setState(isEmpty != 0 ? ENABLED_STATE_SET : EMPTY_STATE_SET);
        }
    }

    private void postUpdateFocusedState() {
        post(this.mUpdateDrawableStateRunnable);
    }

    void updateFocusedState() {
        int[] iArr = this.mSearchSrcTextView.hasFocus() ? FOCUSED_STATE_SET : EMPTY_STATE_SET;
        Drawable background = this.mSearchPlate.getBackground();
        if (background != null) {
            background.setState(iArr);
        }
        background = this.mSubmitArea.getBackground();
        if (background != null) {
            background.setState(iArr);
        }
        invalidate();
    }

    protected void onDetachedFromWindow() {
        removeCallbacks(this.mUpdateDrawableStateRunnable);
        post(this.mReleaseCursorRunnable);
        super.onDetachedFromWindow();
    }

    void onQueryRefine(CharSequence charSequence) {
        setQuery(charSequence);
    }

    boolean onSuggestionsKey(View view, int i, KeyEvent keyEvent) {
        boolean z = false;
        if (this.mSearchable != null && this.mSuggestionsAdapter != null && keyEvent.getAction() == 0 && keyEvent.hasNoModifiers()) {
            if (i == 66 || i == 84 || i == 61) {
                return onItemClicked(this.mSearchSrcTextView.getListSelection(), z, null);
            }
            int i2 = 21;
            if (i == i2 || i == 22) {
                if (i == i2) {
                    i2 = z;
                } else {
                    i2 = this.mSearchSrcTextView.length();
                }
                this.mSearchSrcTextView.setSelection(i2);
                this.mSearchSrcTextView.setListSelection(z);
                this.mSearchSrcTextView.clearListSelection();
                boolean z2 = true;
                HIDDEN_METHOD_INVOKER.ensureImeVisible(this.mSearchSrcTextView, z2);
                return z2;
            } else if (i != 19 || this.mSearchSrcTextView.getListSelection() == 0) {
                return z;
            }
        }
        return z;
    }

    private CharSequence getDecoratedHint(CharSequence charSequence) {
        if (!this.mIconifiedByDefault || this.mSearchHintIcon == null) {
            return charSequence;
        }
        int textSize = (int) (((double) this.mSearchSrcTextView.getTextSize()) * 1.25d);
        int i = 0;
        this.mSearchHintIcon.setBounds(i, i, textSize, textSize);
        CharSequence spannableStringBuilder = new SpannableStringBuilder("   ");
        spannableStringBuilder.setSpan(new ImageSpan(this.mSearchHintIcon), 1, 2, 33);
        spannableStringBuilder.append(charSequence);
        return spannableStringBuilder;
    }

    private void updateQueryHint() {
        CharSequence queryHint = getQueryHint();
        SearchAutoComplete searchAutoComplete = this.mSearchSrcTextView;
        if (queryHint == null) {
            queryHint = "";
        }
        searchAutoComplete.setHint(getDecoratedHint(queryHint));
    }

    private void updateSearchAutoComplete() {
        this.mSearchSrcTextView.setThreshold(this.mSearchable.getSuggestThreshold());
        this.mSearchSrcTextView.setImeOptions(this.mSearchable.getImeOptions());
        int inputType = this.mSearchable.getInputType();
        int i = 1;
        if ((inputType & 15) == i) {
            inputType &= -65537;
            if (this.mSearchable.getSuggestAuthority() != null) {
                inputType = (inputType | 65536) | 524288;
            }
        }
        this.mSearchSrcTextView.setInputType(inputType);
        if (this.mSuggestionsAdapter != null) {
            this.mSuggestionsAdapter.changeCursor(null);
        }
        if (this.mSearchable.getSuggestAuthority() != null) {
            this.mSuggestionsAdapter = new SuggestionsAdapter(getContext(), this, this.mSearchable, this.mOutsideDrawablesCache);
            this.mSearchSrcTextView.setAdapter(this.mSuggestionsAdapter);
            SuggestionsAdapter suggestionsAdapter = (SuggestionsAdapter) this.mSuggestionsAdapter;
            if (this.mQueryRefinement) {
                i = 2;
            }
            suggestionsAdapter.setQueryRefinement(i);
        }
    }

    private void updateVoiceButton(boolean z) {
        int i;
        int i2 = 8;
        if (this.mVoiceButtonEnabled && !isIconified() && z) {
            i = 0;
            this.mGoButton.setVisibility(i2);
        } else {
            i = i2;
        }
        this.mVoiceButton.setVisibility(i);
    }

    void onTextChanged(CharSequence charSequence) {
        CharSequence text = this.mSearchSrcTextView.getText();
        this.mUserQuery = text;
        boolean z = true;
        boolean isEmpty = TextUtils.isEmpty(text) ^ z;
        updateSubmitButton(isEmpty);
        if (isEmpty) {
            z = false;
        }
        updateVoiceButton(z);
        updateCloseButton();
        updateSubmitArea();
        if (!(this.mOnQueryChangeListener == null || TextUtils.equals(charSequence, this.mOldQueryText))) {
            this.mOnQueryChangeListener.onQueryTextChange(charSequence.toString());
        }
        this.mOldQueryText = charSequence.toString();
    }

    void onSubmitQuery() {
        CharSequence text = this.mSearchSrcTextView.getText();
        if (text != null && TextUtils.getTrimmedLength(text) > 0) {
            if (this.mOnQueryChangeListener == null || !this.mOnQueryChangeListener.onQueryTextSubmit(text.toString())) {
                boolean z = false;
                if (this.mSearchable != null) {
                    launchQuerySearch(z, null, text.toString());
                }
                this.mSearchSrcTextView.setImeVisibility(z);
                dismissSuggestions();
            }
        }
    }

    private void dismissSuggestions() {
        this.mSearchSrcTextView.dismissDropDown();
    }

    void onCloseClicked() {
        boolean z = true;
        if (!TextUtils.isEmpty(this.mSearchSrcTextView.getText())) {
            this.mSearchSrcTextView.setText("");
            this.mSearchSrcTextView.requestFocus();
            this.mSearchSrcTextView.setImeVisibility(z);
        } else if (!this.mIconifiedByDefault) {
        } else {
            if (this.mOnCloseListener == null || !this.mOnCloseListener.onClose()) {
                clearFocus();
                updateViewsVisibility(z);
            }
        }
    }

    void onSearchClicked() {
        updateViewsVisibility(false);
        this.mSearchSrcTextView.requestFocus();
        this.mSearchSrcTextView.setImeVisibility(true);
        if (this.mOnSearchClickListener != null) {
            this.mOnSearchClickListener.onClick(this);
        }
    }

    void onVoiceClicked() {
        if (this.mSearchable != null) {
            SearchableInfo searchableInfo = this.mSearchable;
            try {
                if (searchableInfo.getVoiceSearchLaunchWebSearch()) {
                    getContext().startActivity(createVoiceWebSearchIntent(this.mVoiceWebSearchIntent, searchableInfo));
                } else if (searchableInfo.getVoiceSearchLaunchRecognizer()) {
                    getContext().startActivity(createVoiceAppSearchIntent(this.mVoiceAppSearchIntent, searchableInfo));
                }
            } catch (ActivityNotFoundException unused) {
                Log.w("SearchView", "Could not find voice search activity");
            }
        }
    }

    void onTextFocusChanged() {
        updateViewsVisibility(isIconified());
        postUpdateFocusedState();
        if (this.mSearchSrcTextView.hasFocus()) {
            forceSuggestionQuery();
        }
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        postUpdateFocusedState();
    }

    public void onActionViewCollapsed() {
        boolean z = false;
        setQuery("", z);
        clearFocus();
        updateViewsVisibility(true);
        this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions);
        this.mExpandedInActionView = z;
    }

    public void onActionViewExpanded() {
        if (!this.mExpandedInActionView) {
            this.mExpandedInActionView = true;
            this.mCollapsedImeOptions = this.mSearchSrcTextView.getImeOptions();
            this.mSearchSrcTextView.setImeOptions(this.mCollapsedImeOptions | 33554432);
            this.mSearchSrcTextView.setText("");
            setIconified(false);
        }
    }

    protected Parcelable onSaveInstanceState() {
        Parcelable savedState = new SavedState(super.onSaveInstanceState());
        savedState.isIconified = isIconified();
        return savedState;
    }

    protected void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            super.onRestoreInstanceState(savedState.getSuperState());
            updateViewsVisibility(savedState.isIconified);
            requestLayout();
            return;
        }
        super.onRestoreInstanceState(parcelable);
    }

    void adjustDropDownSizeAndPosition() {
        if (this.mDropDownAnchor.getWidth() > 1) {
            int i;
            Resources resources = getContext().getResources();
            int paddingLeft = this.mSearchPlate.getPaddingLeft();
            Rect rect = new Rect();
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int dimensionPixelSize = this.mIconifiedByDefault ? resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_icon_width) + resources.getDimensionPixelSize(R.dimen.abc_dropdownitem_text_padding_left) : 0;
            this.mSearchSrcTextView.getDropDownBackground().getPadding(rect);
            if (isLayoutRtl) {
                i = -rect.left;
            } else {
                i = paddingLeft - (rect.left + dimensionPixelSize);
            }
            this.mSearchSrcTextView.setDropDownHorizontalOffset(i);
            this.mSearchSrcTextView.setDropDownWidth((((this.mDropDownAnchor.getWidth() + rect.left) + rect.right) + dimensionPixelSize) - paddingLeft);
        }
    }

    boolean onItemClicked(int i, int i2, String str) {
        boolean z = false;
        if (this.mOnSuggestionListener != null && this.mOnSuggestionListener.onSuggestionClick(i)) {
            return z;
        }
        launchSuggestion(i, z, null);
        this.mSearchSrcTextView.setImeVisibility(z);
        dismissSuggestions();
        return true;
    }

    boolean onItemSelected(int i) {
        if (this.mOnSuggestionListener != null && this.mOnSuggestionListener.onSuggestionSelect(i)) {
            return false;
        }
        rewriteQueryFromSuggestion(i);
        return true;
    }

    private void rewriteQueryFromSuggestion(int i) {
        CharSequence text = this.mSearchSrcTextView.getText();
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor != null) {
            if (cursor.moveToPosition(i)) {
                CharSequence convertToString = this.mSuggestionsAdapter.convertToString(cursor);
                if (convertToString != null) {
                    setQuery(convertToString);
                } else {
                    setQuery(text);
                }
            } else {
                setQuery(text);
            }
        }
    }

    private boolean launchSuggestion(int i, int i2, String str) {
        Cursor cursor = this.mSuggestionsAdapter.getCursor();
        if (cursor == null || !cursor.moveToPosition(i)) {
            return false;
        }
        launchIntent(createIntentFromSuggestion(cursor, i2, str));
        return true;
    }

    private void launchIntent(Intent intent) {
        if (intent != null) {
            try {
                getContext().startActivity(intent);
            } catch (Throwable e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Failed launch activity: ");
                stringBuilder.append(intent);
                Log.e("SearchView", stringBuilder.toString(), e);
            }
        }
    }

    private void setQuery(CharSequence charSequence) {
        this.mSearchSrcTextView.setText(charSequence);
        this.mSearchSrcTextView.setSelection(TextUtils.isEmpty(charSequence) ? 0 : charSequence.length());
    }

    void launchQuerySearch(int i, String str, String str2) {
        getContext().startActivity(createIntent("android.intent.action.SEARCH", null, null, str2, i, str));
    }

    private Intent createIntent(String str, Uri uri, String str2, String str3, int i, String str4) {
        Intent intent = new Intent(str);
        intent.addFlags(268435456);
        if (uri != null) {
            intent.setData(uri);
        }
        intent.putExtra("user_query", this.mUserQuery);
        if (str3 != null) {
            intent.putExtra("query", str3);
        }
        if (str2 != null) {
            intent.putExtra("intent_extra_data_key", str2);
        }
        if (this.mAppSearchData != null) {
            intent.putExtra("app_data", this.mAppSearchData);
        }
        if (i != 0) {
            intent.putExtra("action_key", i);
            intent.putExtra("action_msg", str4);
        }
        intent.setComponent(this.mSearchable.getSearchActivity());
        return intent;
    }

    private Intent createVoiceWebSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        String str;
        Intent intent2 = new Intent(intent);
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        String str2 = "calling_package";
        if (searchActivity == null) {
            str = null;
        } else {
            str = searchActivity.flattenToShortString();
        }
        intent2.putExtra(str2, str);
        return intent2;
    }

    private Intent createVoiceAppSearchIntent(Intent intent, SearchableInfo searchableInfo) {
        ComponentName searchActivity = searchableInfo.getSearchActivity();
        Intent intent2 = new Intent("android.intent.action.SEARCH");
        intent2.setComponent(searchActivity);
        Parcelable activity = PendingIntent.getActivity(getContext(), 0, intent2, 1073741824);
        Bundle bundle = new Bundle();
        if (this.mAppSearchData != null) {
            bundle.putParcelable("app_data", this.mAppSearchData);
        }
        Intent intent3 = new Intent(intent);
        String str = "free_form";
        int i = 1;
        Resources resources = getResources();
        if (searchableInfo.getVoiceLanguageModeId() != 0) {
            str = resources.getString(searchableInfo.getVoiceLanguageModeId());
        }
        String str2 = null;
        String string = searchableInfo.getVoicePromptTextId() != 0 ? resources.getString(searchableInfo.getVoicePromptTextId()) : str2;
        String string2 = searchableInfo.getVoiceLanguageId() != 0 ? resources.getString(searchableInfo.getVoiceLanguageId()) : str2;
        if (searchableInfo.getVoiceMaxResults() != 0) {
            i = searchableInfo.getVoiceMaxResults();
        }
        intent3.putExtra("android.speech.extra.LANGUAGE_MODEL", str);
        intent3.putExtra("android.speech.extra.PROMPT", string);
        intent3.putExtra("android.speech.extra.LANGUAGE", string2);
        intent3.putExtra("android.speech.extra.MAX_RESULTS", i);
        str = "calling_package";
        if (searchActivity != null) {
            str2 = searchActivity.flattenToShortString();
        }
        intent3.putExtra(str, str2);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT", activity);
        intent3.putExtra("android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE", bundle);
        return intent3;
    }

    private Intent createIntentFromSuggestion(Cursor cursor, int i, String str) {
        Intent intent = null;
        try {
            Uri uri;
            String columnString = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_action");
            if (columnString == null) {
                columnString = this.mSearchable.getSuggestIntentAction();
            }
            if (columnString == null) {
                columnString = "android.intent.action.SEARCH";
            }
            String str2 = columnString;
            columnString = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data");
            if (columnString == null) {
                columnString = this.mSearchable.getSuggestIntentData();
            }
            if (columnString != null) {
                String columnString2 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_data_id");
                if (columnString2 != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(columnString);
                    stringBuilder.append("/");
                    stringBuilder.append(Uri.encode(columnString2));
                    columnString = stringBuilder.toString();
                }
            }
            if (columnString == null) {
                uri = intent;
            } else {
                uri = Uri.parse(columnString);
            }
            String columnString3 = SuggestionsAdapter.getColumnString(cursor, "suggest_intent_query");
            return createIntent(str2, uri, SuggestionsAdapter.getColumnString(cursor, "suggest_intent_extra_data"), columnString3, i, str);
        } catch (Throwable e) {
            int position;
            try {
                position = cursor.getPosition();
            } catch (RuntimeException unused) {
                position = -1;
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Search suggestions cursor at row ");
            stringBuilder2.append(position);
            stringBuilder2.append(" returned exception.");
            Log.w("SearchView", stringBuilder2.toString(), e);
            return intent;
        }
    }

    void forceSuggestionQuery() {
        HIDDEN_METHOD_INVOKER.doBeforeTextChanged(this.mSearchSrcTextView);
        HIDDEN_METHOD_INVOKER.doAfterTextChanged(this.mSearchSrcTextView);
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }
}
