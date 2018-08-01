package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.NestedScrollView.OnScrollChangeListener;
import android.support.v7.appcompat.R;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.lang.ref.WeakReference;

class AlertController {
    ListAdapter mAdapter;
    private int mAlertDialogLayout;
    private final OnClickListener mButtonHandler;
    Button mButtonNegative;
    Message mButtonNegativeMessage;
    private CharSequence mButtonNegativeText;
    Button mButtonNeutral;
    Message mButtonNeutralMessage;
    private CharSequence mButtonNeutralText;
    private int mButtonPanelLayoutHint;
    private int mButtonPanelSideLayout;
    Button mButtonPositive;
    Message mButtonPositiveMessage;
    private CharSequence mButtonPositiveText;
    int mCheckedItem = -1;
    private final Context mContext;
    private View mCustomTitleView;
    final AppCompatDialog mDialog;
    Handler mHandler;
    private Drawable mIcon;
    private int mIconId;
    private ImageView mIconView;
    int mListItemLayout;
    int mListLayout;
    ListView mListView;
    private CharSequence mMessage;
    private TextView mMessageView;
    int mMultiChoiceItemLayout;
    NestedScrollView mScrollView;
    private boolean mShowTitle;
    int mSingleChoiceItemLayout;
    private CharSequence mTitle;
    private TextView mTitleView;
    private View mView;
    private int mViewLayoutResId;
    private int mViewSpacingBottom;
    private int mViewSpacingLeft;
    private int mViewSpacingRight;
    private boolean mViewSpacingSpecified;
    private int mViewSpacingTop;
    private final Window mWindow;

    public static class AlertParams {
        public ListAdapter mAdapter;
        public boolean mCancelable;
        public int mCheckedItem = -1;
        public boolean[] mCheckedItems;
        public final Context mContext;
        public Cursor mCursor;
        public View mCustomTitleView;
        public boolean mForceInverseBackground;
        public Drawable mIcon;
        public int mIconAttrId;
        public int mIconId;
        public final LayoutInflater mInflater;
        public String mIsCheckedColumn;
        public boolean mIsMultiChoice;
        public boolean mIsSingleChoice;
        public CharSequence[] mItems;
        public String mLabelColumn;
        public CharSequence mMessage;
        public DialogInterface.OnClickListener mNegativeButtonListener;
        public CharSequence mNegativeButtonText;
        public DialogInterface.OnClickListener mNeutralButtonListener;
        public CharSequence mNeutralButtonText;
        public OnCancelListener mOnCancelListener;
        public OnMultiChoiceClickListener mOnCheckboxClickListener;
        public DialogInterface.OnClickListener mOnClickListener;
        public OnDismissListener mOnDismissListener;
        public OnItemSelectedListener mOnItemSelectedListener;
        public OnKeyListener mOnKeyListener;
        public OnPrepareListViewListener mOnPrepareListViewListener;
        public DialogInterface.OnClickListener mPositiveButtonListener;
        public CharSequence mPositiveButtonText;
        public boolean mRecycleOnMeasure;
        public CharSequence mTitle;
        public View mView;
        public int mViewLayoutResId;
        public int mViewSpacingBottom;
        public int mViewSpacingLeft;
        public int mViewSpacingRight;
        public boolean mViewSpacingSpecified;
        public int mViewSpacingTop;

        /* renamed from: android.support.v7.app.AlertController$AlertParams$1 */
        class AnonymousClass1 extends ArrayAdapter<CharSequence> {
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass1(Context context, int i, int i2, CharSequence[] charSequenceArr, RecycleListView recycleListView) {
                this.val$listView = recycleListView;
                super(context, i, i2, charSequenceArr);
            }

            public View getView(int i, View view, ViewGroup viewGroup) {
                view = super.getView(i, view, viewGroup);
                if (AlertParams.this.mCheckedItems != null && AlertParams.this.mCheckedItems[i]) {
                    this.val$listView.setItemChecked(i, true);
                }
                return view;
            }
        }

        /* renamed from: android.support.v7.app.AlertController$AlertParams$2 */
        class AnonymousClass2 extends CursorAdapter {
            private final int mIsCheckedIndex;
            private final int mLabelIndex;
            final /* synthetic */ AlertController val$dialog;
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass2(Context context, Cursor cursor, boolean z, RecycleListView recycleListView, AlertController alertController) {
                this.val$listView = recycleListView;
                this.val$dialog = alertController;
                super(context, cursor, z);
                Cursor cursor2 = getCursor();
                this.mLabelIndex = cursor2.getColumnIndexOrThrow(AlertParams.this.mLabelColumn);
                this.mIsCheckedIndex = cursor2.getColumnIndexOrThrow(AlertParams.this.mIsCheckedColumn);
            }

            public void bindView(View view, Context context, Cursor cursor) {
                ((CheckedTextView) view.findViewById(16908308)).setText(cursor.getString(this.mLabelIndex));
                RecycleListView recycleListView = this.val$listView;
                int position = cursor.getPosition();
                boolean z = true;
                if (cursor.getInt(this.mIsCheckedIndex) != z) {
                    z = false;
                }
                recycleListView.setItemChecked(position, z);
            }

            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return AlertParams.this.mInflater.inflate(this.val$dialog.mMultiChoiceItemLayout, viewGroup, false);
            }
        }

        /* renamed from: android.support.v7.app.AlertController$AlertParams$3 */
        class AnonymousClass3 implements OnItemClickListener {
            final /* synthetic */ AlertController val$dialog;

            AnonymousClass3(AlertController alertController) {
                this.val$dialog = alertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                AlertParams.this.mOnClickListener.onClick(this.val$dialog.mDialog, i);
                if (!AlertParams.this.mIsSingleChoice) {
                    this.val$dialog.mDialog.dismiss();
                }
            }
        }

        /* renamed from: android.support.v7.app.AlertController$AlertParams$4 */
        class AnonymousClass4 implements OnItemClickListener {
            final /* synthetic */ AlertController val$dialog;
            final /* synthetic */ RecycleListView val$listView;

            AnonymousClass4(RecycleListView recycleListView, AlertController alertController) {
                this.val$listView = recycleListView;
                this.val$dialog = alertController;
            }

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (AlertParams.this.mCheckedItems != null) {
                    AlertParams.this.mCheckedItems[i] = this.val$listView.isItemChecked(i);
                }
                AlertParams.this.mOnCheckboxClickListener.onClick(this.val$dialog.mDialog, i, this.val$listView.isItemChecked(i));
            }
        }

        public interface OnPrepareListViewListener {
            void onPrepareListView(ListView listView);
        }

        public AlertParams(Context context) {
            boolean z = false;
            this.mIconId = z;
            this.mIconAttrId = z;
            this.mViewSpacingSpecified = z;
            z = true;
            this.mRecycleOnMeasure = z;
            this.mContext = context;
            this.mCancelable = z;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        }

        public void apply(AlertController alertController) {
            if (this.mCustomTitleView != null) {
                alertController.setCustomTitle(this.mCustomTitleView);
            } else {
                if (this.mTitle != null) {
                    alertController.setTitle(this.mTitle);
                }
                if (this.mIcon != null) {
                    alertController.setIcon(this.mIcon);
                }
                if (this.mIconId != 0) {
                    alertController.setIcon(this.mIconId);
                }
                if (this.mIconAttrId != 0) {
                    alertController.setIcon(alertController.getIconAttributeResId(this.mIconAttrId));
                }
            }
            if (this.mMessage != null) {
                alertController.setMessage(this.mMessage);
            }
            Message message = null;
            if (this.mPositiveButtonText != null) {
                alertController.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, message);
            }
            if (this.mNegativeButtonText != null) {
                alertController.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, message);
            }
            if (this.mNeutralButtonText != null) {
                alertController.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, message);
            }
            if (!(this.mItems == null && this.mCursor == null && this.mAdapter == null)) {
                createListView(alertController);
            }
            if (this.mView != null) {
                if (this.mViewSpacingSpecified) {
                    alertController.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
                    return;
                }
                alertController.setView(this.mView);
            } else if (this.mViewLayoutResId != 0) {
                alertController.setView(this.mViewLayoutResId);
            }
        }

        private void createListView(android.support.v7.app.AlertController r11) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r9_0 android.widget.ListAdapter) in PHI: PHI: (r9_6 android.widget.ListAdapter) = (r9_0 android.widget.ListAdapter), (r9_1 android.widget.ListAdapter), (r9_3 android.widget.ListAdapter), (r9_4 android.widget.ListAdapter), (r9_5 android.widget.ListAdapter) binds: {(r9_0 android.widget.ListAdapter)=B:4:0x0014, (r9_1 android.widget.ListAdapter)=B:5:0x0026, (r9_3 android.widget.ListAdapter)=B:13:0x0047, (r9_4 android.widget.ListAdapter)=B:16:0x0062, (r9_5 android.widget.ListAdapter)=B:17:0x0065}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:78)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
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
            r10 = this;
            r0 = r10.mInflater;
            r1 = r11.mListLayout;
            r2 = 0;
            r0 = r0.inflate(r1, r2);
            r0 = (android.support.v7.app.AlertController.RecycleListView) r0;
            r1 = r10.mIsMultiChoice;
            r8 = 1;
            if (r1 == 0) goto L_0x0035;
        L_0x0010:
            r1 = r10.mCursor;
            if (r1 != 0) goto L_0x0026;
        L_0x0014:
            r9 = new android.support.v7.app.AlertController$AlertParams$1;
            r3 = r10.mContext;
            r4 = r11.mMultiChoiceItemLayout;
            r5 = 16908308; // 0x1020014 float:2.3877285E-38 double:8.353814E-317;
            r6 = r10.mItems;
            r1 = r9;
            r2 = r10;
            r7 = r0;
            r1.<init>(r3, r4, r5, r6, r7);
            goto L_0x006e;
        L_0x0026:
            r9 = new android.support.v7.app.AlertController$AlertParams$2;
            r3 = r10.mContext;
            r4 = r10.mCursor;
            r5 = 0;
            r1 = r9;
            r2 = r10;
            r6 = r0;
            r7 = r11;
            r1.<init>(r3, r4, r5, r6, r7);
            goto L_0x006e;
        L_0x0035:
            r1 = r10.mIsSingleChoice;
            if (r1 == 0) goto L_0x003d;
        L_0x0039:
            r1 = r11.mSingleChoiceItemLayout;
        L_0x003b:
            r4 = r1;
            goto L_0x0040;
        L_0x003d:
            r1 = r11.mListItemLayout;
            goto L_0x003b;
        L_0x0040:
            r1 = r10.mCursor;
            r2 = 16908308; // 0x1020014 float:2.3877285E-38 double:8.353814E-317;
            if (r1 == 0) goto L_0x005e;
        L_0x0047:
            r1 = new android.widget.SimpleCursorAdapter;
            r3 = r10.mContext;
            r5 = r10.mCursor;
            r6 = new java.lang.String[r8];
            r7 = r10.mLabelColumn;
            r9 = 0;
            r6[r9] = r7;
            r7 = new int[r8];
            r7[r9] = r2;
            r2 = r1;
            r2.<init>(r3, r4, r5, r6, r7);
            r9 = r1;
            goto L_0x006e;
        L_0x005e:
            r1 = r10.mAdapter;
            if (r1 == 0) goto L_0x0065;
        L_0x0062:
            r9 = r10.mAdapter;
            goto L_0x006e;
        L_0x0065:
            r9 = new android.support.v7.app.AlertController$CheckedItemAdapter;
            r1 = r10.mContext;
            r3 = r10.mItems;
            r9.<init>(r1, r4, r2, r3);
        L_0x006e:
            r1 = r10.mOnPrepareListViewListener;
            if (r1 == 0) goto L_0x0077;
        L_0x0072:
            r1 = r10.mOnPrepareListViewListener;
            r1.onPrepareListView(r0);
        L_0x0077:
            r11.mAdapter = r9;
            r1 = r10.mCheckedItem;
            r11.mCheckedItem = r1;
            r1 = r10.mOnClickListener;
            if (r1 == 0) goto L_0x008a;
        L_0x0081:
            r1 = new android.support.v7.app.AlertController$AlertParams$3;
            r1.<init>(r11);
            r0.setOnItemClickListener(r1);
            goto L_0x0096;
        L_0x008a:
            r1 = r10.mOnCheckboxClickListener;
            if (r1 == 0) goto L_0x0096;
        L_0x008e:
            r1 = new android.support.v7.app.AlertController$AlertParams$4;
            r1.<init>(r0, r11);
            r0.setOnItemClickListener(r1);
        L_0x0096:
            r1 = r10.mOnItemSelectedListener;
            if (r1 == 0) goto L_0x009f;
        L_0x009a:
            r1 = r10.mOnItemSelectedListener;
            r0.setOnItemSelectedListener(r1);
        L_0x009f:
            r1 = r10.mIsSingleChoice;
            if (r1 == 0) goto L_0x00a7;
        L_0x00a3:
            r0.setChoiceMode(r8);
            goto L_0x00af;
        L_0x00a7:
            r1 = r10.mIsMultiChoice;
            if (r1 == 0) goto L_0x00af;
        L_0x00ab:
            r1 = 2;
            r0.setChoiceMode(r1);
        L_0x00af:
            r11.mListView = r0;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v7.app.AlertController.AlertParams.createListView(android.support.v7.app.AlertController):void");
        }
    }

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;
        private WeakReference<DialogInterface> mDialog;

        public ButtonHandler(DialogInterface dialogInterface) {
            this.mDialog = new WeakReference(dialogInterface);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i != 1) {
                switch (i) {
                    case -3:
                    case -2:
                    case -1:
                        ((DialogInterface.OnClickListener) message.obj).onClick((DialogInterface) this.mDialog.get(), message.what);
                        return;
                    default:
                        return;
                }
            }
            ((DialogInterface) message.obj).dismiss();
        }
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public CheckedItemAdapter(Context context, int i, int i2, CharSequence[] charSequenceArr) {
            super(context, i, i2, charSequenceArr);
        }
    }

    public static class RecycleListView extends ListView {
        private final int mPaddingBottomNoButtons;
        private final int mPaddingTopNoTitle;

        public RecycleListView(Context context) {
            this(context, null);
        }

        public RecycleListView(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RecycleListView);
            int i = -1;
            this.mPaddingBottomNoButtons = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.RecycleListView_paddingBottomNoButtons, i);
            this.mPaddingTopNoTitle = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.RecycleListView_paddingTopNoTitle, i);
        }

        public void setHasDecor(boolean z, boolean z2) {
            if (!z2 || !z) {
                setPadding(getPaddingLeft(), z ? getPaddingTop() : this.mPaddingTopNoTitle, getPaddingRight(), z2 ? getPaddingBottom() : this.mPaddingBottomNoButtons);
            }
        }
    }

    private static boolean shouldCenterSingleButton(Context context) {
        TypedValue typedValue = new TypedValue();
        boolean z = true;
        context.getTheme().resolveAttribute(R.attr.alertDialogCenterButtons, typedValue, z);
        return typedValue.data != 0 ? z : false;
    }

    public AlertController(Context context, AppCompatDialog appCompatDialog, Window window) {
        boolean z = false;
        this.mViewSpacingSpecified = z;
        this.mIconId = z;
        this.mButtonPanelLayoutHint = z;
        this.mButtonHandler = new OnClickListener() {
            public void onClick(View view) {
                Message obtain = (view != AlertController.this.mButtonPositive || AlertController.this.mButtonPositiveMessage == null) ? (view != AlertController.this.mButtonNegative || AlertController.this.mButtonNegativeMessage == null) ? (view != AlertController.this.mButtonNeutral || AlertController.this.mButtonNeutralMessage == null) ? null : Message.obtain(AlertController.this.mButtonNeutralMessage) : Message.obtain(AlertController.this.mButtonNegativeMessage) : Message.obtain(AlertController.this.mButtonPositiveMessage);
                if (obtain != null) {
                    obtain.sendToTarget();
                }
                AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialog).sendToTarget();
            }
        };
        this.mContext = context;
        this.mDialog = appCompatDialog;
        this.mWindow = window;
        this.mHandler = new ButtonHandler(appCompatDialog);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, z);
        this.mAlertDialogLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_android_layout, z);
        this.mButtonPanelSideLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, z);
        this.mListLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_listLayout, z);
        this.mMultiChoiceItemLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, z);
        this.mSingleChoiceItemLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, z);
        this.mListItemLayout = obtainStyledAttributes.getResourceId(R.styleable.AlertDialog_listItemLayout, z);
        z = true;
        this.mShowTitle = obtainStyledAttributes.getBoolean(R.styleable.AlertDialog_showTitle, z);
        obtainStyledAttributes.recycle();
        appCompatDialog.supportRequestWindowFeature(z);
    }

    static boolean canTextInput(View view) {
        boolean z = true;
        if (view.onCheckIsTextEditor()) {
            return z;
        }
        boolean z2 = false;
        if (!(view instanceof ViewGroup)) {
            return z2;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        while (childCount > 0) {
            childCount--;
            if (canTextInput(viewGroup.getChildAt(childCount))) {
                return z;
            }
        }
        return z2;
    }

    public void installContent() {
        this.mDialog.setContentView(selectContentView());
        setupView();
    }

    private int selectContentView() {
        if (this.mButtonPanelSideLayout == 0) {
            return this.mAlertDialogLayout;
        }
        if (this.mButtonPanelLayoutHint == 1) {
            return this.mButtonPanelSideLayout;
        }
        return this.mAlertDialogLayout;
    }

    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        if (this.mTitleView != null) {
            this.mTitleView.setText(charSequence);
        }
    }

    public void setCustomTitle(View view) {
        this.mCustomTitleView = view;
    }

    public void setMessage(CharSequence charSequence) {
        this.mMessage = charSequence;
        if (this.mMessageView != null) {
            this.mMessageView.setText(charSequence);
        }
    }

    public void setView(int i) {
        this.mView = null;
        this.mViewLayoutResId = i;
        this.mViewSpacingSpecified = false;
    }

    public void setView(View view) {
        this.mView = view;
        boolean z = false;
        this.mViewLayoutResId = z;
        this.mViewSpacingSpecified = z;
    }

    public void setView(View view, int i, int i2, int i3, int i4) {
        this.mView = view;
        this.mViewLayoutResId = 0;
        this.mViewSpacingSpecified = true;
        this.mViewSpacingLeft = i;
        this.mViewSpacingTop = i2;
        this.mViewSpacingRight = i3;
        this.mViewSpacingBottom = i4;
    }

    public void setButtonPanelLayoutHint(int i) {
        this.mButtonPanelLayoutHint = i;
    }

    public void setButton(int i, CharSequence charSequence, DialogInterface.OnClickListener onClickListener, Message message) {
        if (message == null && onClickListener != null) {
            message = this.mHandler.obtainMessage(i, onClickListener);
        }
        switch (i) {
            case -3:
                this.mButtonNeutralText = charSequence;
                this.mButtonNeutralMessage = message;
                return;
            case -2:
                this.mButtonNegativeText = charSequence;
                this.mButtonNegativeMessage = message;
                return;
            case -1:
                this.mButtonPositiveText = charSequence;
                this.mButtonPositiveMessage = message;
                return;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }

    public void setIcon(int i) {
        this.mIcon = null;
        this.mIconId = i;
        if (this.mIconView == null) {
            return;
        }
        if (i != 0) {
            this.mIconView.setVisibility(0);
            this.mIconView.setImageResource(this.mIconId);
            return;
        }
        this.mIconView.setVisibility(8);
    }

    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        int i = 0;
        this.mIconId = i;
        if (this.mIconView == null) {
            return;
        }
        if (drawable != null) {
            this.mIconView.setVisibility(i);
            this.mIconView.setImageDrawable(drawable);
            return;
        }
        this.mIconView.setVisibility(8);
    }

    public int getIconAttributeResId(int i) {
        TypedValue typedValue = new TypedValue();
        this.mContext.getTheme().resolveAttribute(i, typedValue, true);
        return typedValue.resourceId;
    }

    public ListView getListView() {
        return this.mListView;
    }

    public Button getButton(int i) {
        switch (i) {
            case -3:
                return this.mButtonNeutral;
            case -2:
                return this.mButtonNegative;
            case -1:
                return this.mButtonPositive;
            default:
                return null;
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(keyEvent);
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return this.mScrollView != null && this.mScrollView.executeKeyEvent(keyEvent);
    }

    @Nullable
    private ViewGroup resolvePanel(@Nullable View view, @Nullable View view2) {
        if (view == null) {
            if (view2 instanceof ViewStub) {
                view2 = ((ViewStub) view2).inflate();
            }
            return (ViewGroup) view2;
        }
        if (view2 != null) {
            ViewParent parent = view2.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view2);
            }
        }
        if (view instanceof ViewStub) {
            view = ((ViewStub) view).inflate();
        }
        return (ViewGroup) view;
    }

    private void setupView() {
        View findViewById = this.mWindow.findViewById(R.id.parentPanel);
        View findViewById2 = findViewById.findViewById(R.id.topPanel);
        View findViewById3 = findViewById.findViewById(R.id.contentPanel);
        View findViewById4 = findViewById.findViewById(R.id.buttonPanel);
        ViewGroup viewGroup = (ViewGroup) findViewById.findViewById(R.id.customPanel);
        setupCustomContent(viewGroup);
        View findViewById5 = viewGroup.findViewById(R.id.topPanel);
        View findViewById6 = viewGroup.findViewById(R.id.contentPanel);
        View findViewById7 = viewGroup.findViewById(R.id.buttonPanel);
        ViewGroup resolvePanel = resolvePanel(findViewById5, findViewById2);
        ViewGroup resolvePanel2 = resolvePanel(findViewById6, findViewById3);
        ViewGroup resolvePanel3 = resolvePanel(findViewById7, findViewById4);
        setupContent(resolvePanel2);
        setupButtons(resolvePanel3);
        setupTitle(resolvePanel);
        int i = 8;
        boolean z = true;
        int i2 = 0;
        boolean z2 = (viewGroup == null || viewGroup.getVisibility() == i) ? i2 : z;
        boolean z3 = (resolvePanel == null || resolvePanel.getVisibility() == i) ? i2 : z;
        boolean z4 = (resolvePanel3 == null || resolvePanel3.getVisibility() == i) ? i2 : z;
        if (!(z4 || resolvePanel2 == null)) {
            findViewById5 = resolvePanel2.findViewById(R.id.textSpacerNoButtons);
            if (findViewById5 != null) {
                findViewById5.setVisibility(i2);
            }
        }
        if (z3) {
            if (this.mScrollView != null) {
                this.mScrollView.setClipToPadding(z);
            }
            findViewById5 = null;
            if (!(this.mMessage == null && this.mListView == null)) {
                findViewById5 = resolvePanel.findViewById(R.id.titleDividerNoCustom);
            }
            if (findViewById5 != null) {
                findViewById5.setVisibility(i2);
            }
        } else if (resolvePanel2 != null) {
            findViewById2 = resolvePanel2.findViewById(R.id.textSpacerNoTitle);
            if (findViewById2 != null) {
                findViewById2.setVisibility(i2);
            }
        }
        if (this.mListView instanceof RecycleListView) {
            ((RecycleListView) this.mListView).setHasDecor(z3, z4);
        }
        if (!z2) {
            findViewById = this.mListView != null ? this.mListView : this.mScrollView;
            if (findViewById != null) {
                if (z4) {
                    i2 = 2;
                }
                setScrollIndicators(resolvePanel2, findViewById, z3 | i2, 3);
            }
        }
        ListView listView = this.mListView;
        if (listView != null && this.mAdapter != null) {
            listView.setAdapter(this.mAdapter);
            int i3 = this.mCheckedItem;
            if (i3 > -1) {
                listView.setItemChecked(i3, z);
                listView.setSelection(i3);
            }
        }
    }

    private void setScrollIndicators(ViewGroup viewGroup, View view, int i, int i2) {
        View findViewById = this.mWindow.findViewById(R.id.scrollIndicatorUp);
        View findViewById2 = this.mWindow.findViewById(R.id.scrollIndicatorDown);
        if (VERSION.SDK_INT >= 23) {
            ViewCompat.setScrollIndicators(view, i, i2);
            if (findViewById != null) {
                viewGroup.removeView(findViewById);
            }
            if (findViewById2 != null) {
                viewGroup.removeView(findViewById2);
                return;
            }
            return;
        }
        view = null;
        if (findViewById != null && (i & 1) == 0) {
            viewGroup.removeView(findViewById);
            findViewById = view;
        }
        if (findViewById2 == null || (i & 2) != 0) {
            view = findViewById2;
        } else {
            viewGroup.removeView(findViewById2);
        }
        if (findViewById != null || view != null) {
            if (this.mMessage != null) {
                this.mScrollView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    public void onScrollChange(NestedScrollView nestedScrollView, int i, int i2, int i3, int i4) {
                        AlertController.manageScrollIndicators(nestedScrollView, findViewById, view);
                    }
                });
                this.mScrollView.post(new Runnable() {
                    public void run() {
                        AlertController.manageScrollIndicators(AlertController.this.mScrollView, findViewById, view);
                    }
                });
            } else if (this.mListView != null) {
                this.mListView.setOnScrollListener(new OnScrollListener() {
                    public void onScrollStateChanged(AbsListView absListView, int i) {
                    }

                    public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                        AlertController.manageScrollIndicators(absListView, findViewById, view);
                    }
                });
                this.mListView.post(new Runnable() {
                    public void run() {
                        AlertController.manageScrollIndicators(AlertController.this.mListView, findViewById, view);
                    }
                });
            } else {
                if (findViewById != null) {
                    viewGroup.removeView(findViewById);
                }
                if (view != null) {
                    viewGroup.removeView(view);
                }
            }
        }
    }

    private void setupCustomContent(ViewGroup viewGroup) {
        int i;
        boolean z = false;
        View inflate = this.mView != null ? this.mView : this.mViewLayoutResId != 0 ? LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, viewGroup, z) : null;
        if (inflate != null) {
            z = true;
        }
        if (!(z && canTextInput(inflate))) {
            i = 131072;
            this.mWindow.setFlags(i, i);
        }
        if (z) {
            FrameLayout frameLayout = (FrameLayout) this.mWindow.findViewById(R.id.custom);
            i = -1;
            frameLayout.addView(inflate, new LayoutParams(i, i));
            if (this.mViewSpacingSpecified) {
                frameLayout.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
            }
            if (this.mListView != null) {
                ((LinearLayoutCompat.LayoutParams) viewGroup.getLayoutParams()).weight = 0.0f;
                return;
            }
            return;
        }
        viewGroup.setVisibility(8);
    }

    private void setupTitle(ViewGroup viewGroup) {
        int i = 8;
        if (this.mCustomTitleView != null) {
            viewGroup.addView(this.mCustomTitleView, 0, new LayoutParams(-1, -2));
            this.mWindow.findViewById(R.id.title_template).setVisibility(i);
            return;
        }
        this.mIconView = (ImageView) this.mWindow.findViewById(16908294);
        if ((TextUtils.isEmpty(this.mTitle) ^ 1) == 0 || !this.mShowTitle) {
            this.mWindow.findViewById(R.id.title_template).setVisibility(i);
            this.mIconView.setVisibility(i);
            viewGroup.setVisibility(i);
            return;
        }
        this.mTitleView = (TextView) this.mWindow.findViewById(R.id.alertTitle);
        this.mTitleView.setText(this.mTitle);
        if (this.mIconId != 0) {
            this.mIconView.setImageResource(this.mIconId);
        } else if (this.mIcon != null) {
            this.mIconView.setImageDrawable(this.mIcon);
        } else {
            this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
            this.mIconView.setVisibility(i);
        }
    }

    private void setupContent(ViewGroup viewGroup) {
        this.mScrollView = (NestedScrollView) this.mWindow.findViewById(R.id.scrollView);
        boolean z = false;
        this.mScrollView.setFocusable(z);
        this.mScrollView.setNestedScrollingEnabled(z);
        this.mMessageView = (TextView) viewGroup.findViewById(16908299);
        if (this.mMessageView != null) {
            if (this.mMessage != null) {
                this.mMessageView.setText(this.mMessage);
            } else {
                int i = 8;
                this.mMessageView.setVisibility(i);
                this.mScrollView.removeView(this.mMessageView);
                if (this.mListView != null) {
                    viewGroup = (ViewGroup) this.mScrollView.getParent();
                    int indexOfChild = viewGroup.indexOfChild(this.mScrollView);
                    viewGroup.removeViewAt(indexOfChild);
                    int i2 = -1;
                    viewGroup.addView(this.mListView, indexOfChild, new LayoutParams(i2, i2));
                } else {
                    viewGroup.setVisibility(i);
                }
            }
        }
    }

    static void manageScrollIndicators(View view, View view2, View view3) {
        int i = 4;
        int i2 = 0;
        if (view2 != null) {
            view2.setVisibility(view.canScrollVertically(-1) ? i2 : i);
        }
        if (view3 != null) {
            if (view.canScrollVertically(1)) {
                i = i2;
            }
            view3.setVisibility(i);
        }
    }

    private void setupButtons(ViewGroup viewGroup) {
        int i;
        this.mButtonPositive = (Button) viewGroup.findViewById(16908313);
        this.mButtonPositive.setOnClickListener(this.mButtonHandler);
        int i2 = 1;
        int i3 = 8;
        int i4 = 0;
        if (TextUtils.isEmpty(this.mButtonPositiveText)) {
            this.mButtonPositive.setVisibility(i3);
            i = i4;
        } else {
            this.mButtonPositive.setText(this.mButtonPositiveText);
            this.mButtonPositive.setVisibility(i4);
            i = i2;
        }
        this.mButtonNegative = (Button) viewGroup.findViewById(16908314);
        this.mButtonNegative.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNegativeText)) {
            this.mButtonNegative.setVisibility(i3);
        } else {
            this.mButtonNegative.setText(this.mButtonNegativeText);
            this.mButtonNegative.setVisibility(i4);
            i |= 2;
        }
        this.mButtonNeutral = (Button) viewGroup.findViewById(16908315);
        this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
        if (TextUtils.isEmpty(this.mButtonNeutralText)) {
            this.mButtonNeutral.setVisibility(i3);
        } else {
            this.mButtonNeutral.setText(this.mButtonNeutralText);
            this.mButtonNeutral.setVisibility(i4);
            i |= 4;
        }
        if (shouldCenterSingleButton(this.mContext)) {
            if (i == i2) {
                centerButton(this.mButtonPositive);
            } else if (i == 2) {
                centerButton(this.mButtonNegative);
            } else if (i == 4) {
                centerButton(this.mButtonNeutral);
            }
        }
        if (i == 0) {
            i2 = i4;
        }
        if (i2 == 0) {
            viewGroup.setVisibility(i3);
        }
    }

    private void centerButton(Button button) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        layoutParams.gravity = 1;
        layoutParams.weight = 0.5f;
        button.setLayoutParams(layoutParams);
    }
}
