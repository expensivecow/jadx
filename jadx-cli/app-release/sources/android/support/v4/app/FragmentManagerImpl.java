package android.support.v4.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.util.ArraySet;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* compiled from: FragmentManager */
final class FragmentManagerImpl extends FragmentManager implements Factory2 {
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField;
    SparseArray<Fragment> mActive;
    final ArrayList<Fragment> mAdded = new ArrayList();
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState;
    boolean mDestroyed;
    Runnable mExecCommit;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    private final CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks = new CopyOnWriteArrayList();
    boolean mNeedMenuInvalidate;
    int mNextFragmentIndex;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<OpGenerator> mPendingActions;
    ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    Fragment mPrimaryNav;
    FragmentManagerNonConfig mSavedNonConfig;
    SparseArray<Parcelable> mStateArray;
    Bundle mStateBundle;
    boolean mStateSaved;
    ArrayList<Fragment> mTmpAddedFragments;
    ArrayList<Boolean> mTmpIsPop;
    ArrayList<BackStackRecord> mTmpRecords;

    /* compiled from: FragmentManager */
    private static class AnimationListenerWrapper implements AnimationListener {
        private final AnimationListener mWrapped;

        /* synthetic */ AnimationListenerWrapper(AnimationListener animationListener, AnonymousClass1 anonymousClass1) {
            this(animationListener);
        }

        private AnimationListenerWrapper(AnimationListener animationListener) {
            this.mWrapped = animationListener;
        }

        @CallSuper
        public void onAnimationStart(Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationStart(animation);
            }
        }

        @CallSuper
        public void onAnimationEnd(Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationEnd(animation);
            }
        }

        @CallSuper
        public void onAnimationRepeat(Animation animation) {
            if (this.mWrapped != null) {
                this.mWrapped.onAnimationRepeat(animation);
            }
        }
    }

    /* compiled from: FragmentManager */
    private static class AnimationOrAnimator {
        public final Animation animation;
        public final Animator animator;

        /* synthetic */ AnimationOrAnimator(Animator animator, AnonymousClass1 anonymousClass1) {
            this(animator);
        }

        /* synthetic */ AnimationOrAnimator(Animation animation, AnonymousClass1 anonymousClass1) {
            this(animation);
        }

        private AnimationOrAnimator(Animation animation) {
            this.animation = animation;
            this.animator = null;
            if (animation == null) {
                throw new IllegalStateException("Animation cannot be null");
            }
        }

        private AnimationOrAnimator(Animator animator) {
            this.animation = null;
            this.animator = animator;
            if (animator == null) {
                throw new IllegalStateException("Animator cannot be null");
            }
        }
    }

    /* compiled from: FragmentManager */
    private static class AnimatorOnHWLayerIfNeededListener extends AnimatorListenerAdapter {
        View mView;

        AnimatorOnHWLayerIfNeededListener(View view) {
            this.mView = view;
        }

        public void onAnimationStart(Animator animator) {
            this.mView.setLayerType(2, null);
        }

        public void onAnimationEnd(Animator animator) {
            this.mView.setLayerType(0, null);
            animator.removeListener(this);
        }
    }

    /* compiled from: FragmentManager */
    static class FragmentTag {
        public static final int[] Fragment = new int[]{16842755, 16842960, 16842961};
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;

        FragmentTag() {
        }

        static {
            int i = 3;
        }
    }

    /* compiled from: FragmentManager */
    interface OpGenerator {
        boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2);
    }

    /* compiled from: FragmentManager */
    private static class AnimateOnHWLayerIfNeededListener extends AnimationListenerWrapper {
        View mView;

        AnimateOnHWLayerIfNeededListener(View view, AnimationListener animationListener) {
            super(animationListener, null);
            this.mView = view;
        }

        @CallSuper
        public void onAnimationEnd(Animation animation) {
            if (ViewCompat.isAttachedToWindow(this.mView) || VERSION.SDK_INT >= 24) {
                this.mView.post(new Runnable() {
                    public void run() {
                        AnimateOnHWLayerIfNeededListener.this.mView.setLayerType(0, null);
                    }
                });
            } else {
                this.mView.setLayerType(0, null);
            }
            super.onAnimationEnd(animation);
        }
    }

    /* compiled from: FragmentManager */
    private class PopBackStackState implements OpGenerator {
        final int mFlags;
        final int mId;
        final String mName;

        PopBackStackState(String str, int i, int i2) {
            this.mName = str;
            this.mId = i;
            this.mFlags = i2;
        }

        public boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
            if (FragmentManagerImpl.this.mPrimaryNav != null && this.mId < 0 && this.mName == null) {
                FragmentManager peekChildFragmentManager = FragmentManagerImpl.this.mPrimaryNav.peekChildFragmentManager();
                if (peekChildFragmentManager != null && peekChildFragmentManager.popBackStackImmediate()) {
                    return false;
                }
            }
            return FragmentManagerImpl.this.popBackStackState(arrayList, arrayList2, this.mName, this.mId, this.mFlags);
        }
    }

    /* compiled from: FragmentManager */
    static class StartEnterTransitionListener implements OnStartEnterTransitionListener {
        private final boolean mIsBack;
        private int mNumPostponed;
        private final BackStackRecord mRecord;

        StartEnterTransitionListener(BackStackRecord backStackRecord, boolean z) {
            this.mIsBack = z;
            this.mRecord = backStackRecord;
        }

        public void onStartEnterTransition() {
            this.mNumPostponed--;
            if (this.mNumPostponed == 0) {
                this.mRecord.mManager.scheduleCommit();
            }
        }

        public void startListening() {
            this.mNumPostponed++;
        }

        public boolean isReady() {
            return this.mNumPostponed == 0;
        }

        public void completeTransaction() {
            int i = 0;
            boolean z = true;
            int i2 = this.mNumPostponed > 0 ? z : i;
            FragmentManagerImpl fragmentManagerImpl = this.mRecord.mManager;
            int size = fragmentManagerImpl.mAdded.size();
            while (i < size) {
                Fragment fragment = (Fragment) fragmentManagerImpl.mAdded.get(i);
                fragment.setOnStartEnterTransitionListener(null);
                if (i2 != 0 && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
                i++;
            }
            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, i2 ^ z, z);
        }

        public void cancelTransaction() {
            boolean z = false;
            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, z, z);
        }
    }

    public static int reverseTransit(int i) {
        int i2 = 8194;
        int i3 = 4099;
        int i4 = 4097;
        return i != i4 ? i != i3 ? i != i2 ? 0 : i4 : i3 : i2;
    }

    public static int transitToStyleIndex(int i, boolean z) {
        return i != 4097 ? i != 4099 ? i != 8194 ? -1 : z ? 3 : 4 : z ? 5 : 6 : z ? 1 : 2;
    }

    Factory2 getLayoutInflaterFactory() {
        return this;
    }

    FragmentManagerImpl() {
        int i = 0;
        this.mNextFragmentIndex = i;
        this.mCurState = i;
        Object obj = null;
        this.mStateBundle = obj;
        this.mStateArray = obj;
        this.mExecCommit = new Runnable() {
            public void run() {
                FragmentManagerImpl.this.execPendingActions();
            }
        };
    }

    static boolean modifiesAlpha(AnimationOrAnimator animationOrAnimator) {
        boolean z = true;
        if (animationOrAnimator.animation instanceof AlphaAnimation) {
            return z;
        }
        if (!(animationOrAnimator.animation instanceof AnimationSet)) {
            return modifiesAlpha(animationOrAnimator.animator);
        }
        List animations = ((AnimationSet) animationOrAnimator.animation).getAnimations();
        boolean z2 = false;
        for (int i = z2; i < animations.size(); i++) {
            if (animations.get(i) instanceof AlphaAnimation) {
                return z;
            }
        }
        return z2;
    }

    static boolean modifiesAlpha(Animator animator) {
        boolean z = false;
        if (animator == null) {
            return z;
        }
        boolean z2 = true;
        int i;
        if (animator instanceof ValueAnimator) {
            PropertyValuesHolder[] values = ((ValueAnimator) animator).getValues();
            for (i = z; i < values.length; i++) {
                if ("alpha".equals(values[i].getPropertyName())) {
                    return z2;
                }
            }
        } else if (animator instanceof AnimatorSet) {
            List childAnimations = ((AnimatorSet) animator).getChildAnimations();
            for (i = z; i < childAnimations.size(); i++) {
                if (modifiesAlpha((Animator) childAnimations.get(i))) {
                    return z2;
                }
            }
        }
        return z;
    }

    static boolean shouldRunOnHWLayer(View view, AnimationOrAnimator animationOrAnimator) {
        boolean z = false;
        if (view == null || animationOrAnimator == null) {
            return z;
        }
        if (VERSION.SDK_INT >= 19 && view.getLayerType() == 0 && ViewCompat.hasOverlappingRendering(view) && modifiesAlpha(animationOrAnimator)) {
            z = true;
        }
        return z;
    }

    private void throwException(RuntimeException runtimeException) {
        Log.e("FragmentManager", runtimeException.getMessage());
        Log.e("FragmentManager", "Activity state:");
        PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
        int i = 0;
        FileDescriptor fileDescriptor = null;
        if (this.mHost != null) {
            try {
                this.mHost.onDump("  ", fileDescriptor, printWriter, new String[i]);
            } catch (Throwable e) {
                Log.e("FragmentManager", "Failed dumping state", e);
            }
        } else {
            try {
                dump("  ", fileDescriptor, printWriter, new String[i]);
            } catch (Throwable e2) {
                Log.e("FragmentManager", "Failed dumping state", e2);
            }
        }
        throw runtimeException;
    }

    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public boolean executePendingTransactions() {
        boolean execPendingActions = execPendingActions();
        forcePostponedTransactions();
        return execPendingActions;
    }

    public void popBackStack() {
        boolean z = false;
        enqueueAction(new PopBackStackState(null, -1, z), z);
    }

    public boolean popBackStackImmediate() {
        checkStateLoss();
        return popBackStackImmediate(null, -1, 0);
    }

    public void popBackStack(String str, int i) {
        enqueueAction(new PopBackStackState(str, -1, i), false);
    }

    public boolean popBackStackImmediate(String str, int i) {
        checkStateLoss();
        return popBackStackImmediate(str, -1, i);
    }

    public void popBackStack(int i, int i2) {
        if (i < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Bad id: ");
            stringBuilder.append(i);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        enqueueAction(new PopBackStackState(null, i, i2), false);
    }

    public boolean popBackStackImmediate(int i, int i2) {
        checkStateLoss();
        execPendingActions();
        if (i >= 0) {
            return popBackStackImmediate(null, i, i2);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bad id: ");
        stringBuilder.append(i);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private boolean popBackStackImmediate(String str, int i, int i2) {
        execPendingActions();
        boolean z = true;
        ensureExecReady(z);
        if (this.mPrimaryNav != null && i < 0 && str == null) {
            FragmentManager peekChildFragmentManager = this.mPrimaryNav.peekChildFragmentManager();
            if (peekChildFragmentManager != null && peekChildFragmentManager.popBackStackImmediate()) {
                return z;
            }
        }
        boolean popBackStackState = popBackStackState(this.mTmpRecords, this.mTmpIsPop, str, i, i2);
        if (popBackStackState) {
            this.mExecutingActions = z;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
            } finally {
                cleanupExec();
            }
        }
        doPendingDeferredStart();
        burpActive();
        return popBackStackState;
    }

    public int getBackStackEntryCount() {
        return this.mBackStack != null ? this.mBackStack.size() : 0;
    }

    public BackStackEntry getBackStackEntryAt(int i) {
        return (BackStackEntry) this.mBackStack.get(i);
    }

    public void addOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    public void removeOnBackStackChangedListener(OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners != null) {
            this.mBackStackChangeListeners.remove(onBackStackChangedListener);
        }
    }

    public void putFragment(Bundle bundle, String str, Fragment fragment) {
        if (fragment.mIndex < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment ");
            stringBuilder.append(fragment);
            stringBuilder.append(" is not currently in the FragmentManager");
            throwException(new IllegalStateException(stringBuilder.toString()));
        }
        bundle.putInt(str, fragment.mIndex);
    }

    public Fragment getFragment(Bundle bundle, String str) {
        int i = -1;
        int i2 = bundle.getInt(str, i);
        if (i2 == i) {
            return null;
        }
        Fragment fragment = (Fragment) this.mActive.get(i2);
        if (fragment == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment no longer exists for key ");
            stringBuilder.append(str);
            stringBuilder.append(": index ");
            stringBuilder.append(i2);
            throwException(new IllegalStateException(stringBuilder.toString()));
        }
        return fragment;
    }

    public List<Fragment> getFragments() {
        if (this.mAdded.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<Fragment> list;
        synchronized (this.mAdded) {
            list = (List) this.mAdded.clone();
        }
        return list;
    }

    List<Fragment> getActiveFragments() {
        if (this.mActive == null) {
            return null;
        }
        int size = this.mActive.size();
        List arrayList = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            arrayList.add(this.mActive.valueAt(i));
        }
        return arrayList;
    }

    int getActiveFragmentCount() {
        if (this.mActive == null) {
            return 0;
        }
        return this.mActive.size();
    }

    public SavedState saveFragmentInstanceState(Fragment fragment) {
        if (fragment.mIndex < 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment ");
            stringBuilder.append(fragment);
            stringBuilder.append(" is not currently in the FragmentManager");
            throwException(new IllegalStateException(stringBuilder.toString()));
        }
        SavedState savedState = null;
        if (fragment.mState <= 0) {
            return savedState;
        }
        Bundle saveFragmentBasicState = saveFragmentBasicState(fragment);
        if (saveFragmentBasicState != null) {
            savedState = new SavedState(saveFragmentBasicState);
        }
        return savedState;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("FragmentManager{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        stringBuilder.append(" in ");
        if (this.mParent != null) {
            DebugUtils.buildShortClassTag(this.mParent, stringBuilder);
        } else {
            DebugUtils.buildShortClassTag(this.mHost, stringBuilder);
        }
        stringBuilder.append("}}");
        return stringBuilder.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int size;
        int i;
        Fragment fragment;
        int size2;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append("    ");
        String stringBuilder2 = stringBuilder.toString();
        int i2 = 0;
        if (this.mActive != null) {
            size = this.mActive.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.print("Active Fragments in ");
                printWriter.print(Integer.toHexString(System.identityHashCode(this)));
                printWriter.println(":");
                for (i = i2; i < size; i++) {
                    fragment = (Fragment) this.mActive.valueAt(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment);
                    if (fragment != null) {
                        fragment.dump(stringBuilder2, fileDescriptor, printWriter, strArr);
                    }
                }
            }
        }
        size = this.mAdded.size();
        if (size > 0) {
            printWriter.print(str);
            printWriter.println("Added Fragments:");
            for (i = i2; i < size; i++) {
                fragment = (Fragment) this.mAdded.get(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(fragment.toString());
            }
        }
        if (this.mCreatedMenus != null) {
            size = this.mCreatedMenus.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.println("Fragments Created Menus:");
                for (i = i2; i < size; i++) {
                    fragment = (Fragment) this.mCreatedMenus.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment.toString());
                }
            }
        }
        if (this.mBackStack != null) {
            size = this.mBackStack.size();
            if (size > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack:");
                for (i = i2; i < size; i++) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(i);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(stringBuilder2, fileDescriptor, printWriter, strArr);
                }
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null) {
                size2 = this.mBackStackIndices.size();
                if (size2 > 0) {
                    printWriter.print(str);
                    printWriter.println("Back Stack Indices:");
                    for (int i3 = i2; i3 < size2; i3++) {
                        BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStackIndices.get(i3);
                        printWriter.print(str);
                        printWriter.print("  #");
                        printWriter.print(i3);
                        printWriter.print(": ");
                        printWriter.println(backStackRecord2);
                    }
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                printWriter.print(str);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        if (this.mPendingActions != null) {
            size2 = this.mPendingActions.size();
            if (size2 > 0) {
                printWriter.print(str);
                printWriter.println("Pending Actions:");
                while (i2 < size2) {
                    OpGenerator opGenerator = (OpGenerator) this.mPendingActions.get(i2);
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    printWriter.println(opGenerator);
                    i2++;
                }
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            printWriter.print(str);
            printWriter.print("  mNoTransactionsBecause=");
            printWriter.println(this.mNoTransactionsBecause);
        }
    }

    static {
        float f = 2.5f;
        DECELERATE_QUINT = new DecelerateInterpolator(f);
        float f2 = 1.5f;
        DECELERATE_CUBIC = new DecelerateInterpolator(f2);
        ACCELERATE_QUINT = new AccelerateInterpolator(f);
        ACCELERATE_CUBIC = new AccelerateInterpolator(f2);
    }

    static AnimationOrAnimator makeOpenCloseAnimation(Context context, float f, float f2, float f3, float f4) {
        Animation animationSet = new AnimationSet(false);
        Animation scaleAnimation = new ScaleAnimation(f, f2, f, f2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(DECELERATE_QUINT);
        long j = 220;
        scaleAnimation.setDuration(j);
        animationSet.addAnimation(scaleAnimation);
        Animation alphaAnimation = new AlphaAnimation(f3, f4);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(j);
        animationSet.addAnimation(alphaAnimation);
        return new AnimationOrAnimator(animationSet, null);
    }

    static AnimationOrAnimator makeFadeAnimation(Context context, float f, float f2) {
        Animation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220);
        return new AnimationOrAnimator(alphaAnimation, null);
    }

    AnimationOrAnimator loadAnimation(Fragment fragment, int i, boolean z, int i2) {
        int nextAnim = fragment.getNextAnim();
        Animation onCreateAnimation = fragment.onCreateAnimation(i, z, nextAnim);
        AnonymousClass1 anonymousClass1 = null;
        if (onCreateAnimation != null) {
            return new AnimationOrAnimator(onCreateAnimation, anonymousClass1);
        }
        Animator onCreateAnimator = fragment.onCreateAnimator(i, z, nextAnim);
        if (onCreateAnimator != null) {
            return new AnimationOrAnimator(onCreateAnimator, anonymousClass1);
        }
        if (nextAnim != 0) {
            boolean equals = "anim".equals(this.mHost.getContext().getResources().getResourceTypeName(nextAnim));
            Object obj = null;
            if (equals) {
                try {
                    Animation loadAnimation = AnimationUtils.loadAnimation(this.mHost.getContext(), nextAnim);
                    if (loadAnimation != null) {
                        return new AnimationOrAnimator(loadAnimation, anonymousClass1);
                    }
                    obj = 1;
                } catch (NotFoundException e) {
                    throw e;
                } catch (RuntimeException unused) {
                    if (obj == null) {
                        try {
                            Animator loadAnimator = AnimatorInflater.loadAnimator(this.mHost.getContext(), nextAnim);
                            if (loadAnimator != null) {
                                return new AnimationOrAnimator(loadAnimator, anonymousClass1);
                            }
                        } catch (RuntimeException e2) {
                            if (equals) {
                                throw e2;
                            }
                            Animation loadAnimation2 = AnimationUtils.loadAnimation(this.mHost.getContext(), nextAnim);
                            if (loadAnimation2 != null) {
                                return new AnimationOrAnimator(loadAnimation2, anonymousClass1);
                            }
                        }
                    }
                }
            }
        }
        if (i == 0) {
            return anonymousClass1;
        }
        int transitToStyleIndex = transitToStyleIndex(i, z);
        if (transitToStyleIndex < 0) {
            return anonymousClass1;
        }
        float f = 0.975f;
        float f2 = 0.0f;
        float f3 = 1.0f;
        switch (transitToStyleIndex) {
            case 1:
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125f, f3, f2, f3);
            case 2:
                return makeOpenCloseAnimation(this.mHost.getContext(), f3, f, f3, f2);
            case 3:
                return makeOpenCloseAnimation(this.mHost.getContext(), f, f3, f2, f3);
            case 4:
                return makeOpenCloseAnimation(this.mHost.getContext(), f3, 1.075f, f3, f2);
            case 5:
                return makeFadeAnimation(this.mHost.getContext(), f2, f3);
            case 6:
                return makeFadeAnimation(this.mHost.getContext(), f3, f2);
            default:
                if (i2 == 0 && this.mHost.onHasWindowAnimations()) {
                    i2 = this.mHost.onGetWindowAnimations();
                }
                return i2 == 0 ? anonymousClass1 : anonymousClass1;
        }
    }

    public void performPendingDeferredStart(Fragment fragment) {
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            moveToState(fragment, this.mCurState, 0, 0, false);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void setHWLayerAnimListenerIfAlpha(android.view.View r3, android.support.v4.app.FragmentManagerImpl.AnimationOrAnimator r4) {
        /*
        if (r3 == 0) goto L_0x0030;
    L_0x0002:
        if (r4 != 0) goto L_0x0005;
    L_0x0004:
        goto L_0x0030;
    L_0x0005:
        r0 = shouldRunOnHWLayer(r3, r4);
        if (r0 == 0) goto L_0x002f;
    L_0x000b:
        r0 = r4.animator;
        if (r0 == 0) goto L_0x001a;
    L_0x000f:
        r4 = r4.animator;
        r0 = new android.support.v4.app.FragmentManagerImpl$AnimatorOnHWLayerIfNeededListener;
        r0.<init>(r3);
        r4.addListener(r0);
        goto L_0x002f;
    L_0x001a:
        r0 = r4.animation;
        r0 = getAnimationListener(r0);
        r1 = 2;
        r2 = 0;
        r3.setLayerType(r1, r2);
        r4 = r4.animation;
        r1 = new android.support.v4.app.FragmentManagerImpl$AnimateOnHWLayerIfNeededListener;
        r1.<init>(r3, r0);
        r4.setAnimationListener(r1);
    L_0x002f:
        return;
    L_0x0030:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.setHWLayerAnimListenerIfAlpha(android.view.View, android.support.v4.app.FragmentManagerImpl$AnimationOrAnimator):void");
    }

    private static AnimationListener getAnimationListener(Animation animation) {
        try {
            if (sAnimationListenerField == null) {
                sAnimationListenerField = Animation.class.getDeclaredField("mListener");
                sAnimationListenerField.setAccessible(true);
            }
            return (AnimationListener) sAnimationListenerField.get(animation);
        } catch (Throwable e) {
            Log.e("FragmentManager", "No field with the name mListener is found in Animation class", e);
            return null;
        } catch (Throwable e2) {
            Log.e("FragmentManager", "Cannot access Animation's mListener field", e2);
            return null;
        }
    }

    boolean isStateAtLeast(int i) {
        return this.mCurState >= i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void moveToState(android.support.v4.app.Fragment r16, int r17, int r18, int r19, boolean r20) {
        /*
        r15 = this;
        r6 = r15;
        r7 = r16;
        r0 = r7.mAdded;
        r8 = 1;
        if (r0 == 0) goto L_0x0010;
    L_0x0008:
        r0 = r7.mDetached;
        if (r0 == 0) goto L_0x000d;
    L_0x000c:
        goto L_0x0010;
    L_0x000d:
        r0 = r17;
        goto L_0x0015;
    L_0x0010:
        r0 = r17;
        if (r0 <= r8) goto L_0x0015;
    L_0x0014:
        r0 = r8;
    L_0x0015:
        r1 = r7.mRemoving;
        if (r1 == 0) goto L_0x002b;
    L_0x0019:
        r1 = r7.mState;
        if (r0 <= r1) goto L_0x002b;
    L_0x001d:
        r0 = r7.mState;
        if (r0 != 0) goto L_0x0029;
    L_0x0021:
        r0 = r16.isInBackStack();
        if (r0 == 0) goto L_0x0029;
    L_0x0027:
        r0 = r8;
        goto L_0x002b;
    L_0x0029:
        r0 = r7.mState;
    L_0x002b:
        r1 = r7.mDeferStart;
        r9 = 4;
        r10 = 3;
        if (r1 == 0) goto L_0x0039;
    L_0x0031:
        r1 = r7.mState;
        if (r1 >= r9) goto L_0x0039;
    L_0x0035:
        if (r0 <= r10) goto L_0x0039;
    L_0x0037:
        r11 = r10;
        goto L_0x003a;
    L_0x0039:
        r11 = r0;
    L_0x003a:
        r0 = r7.mState;
        r12 = 2;
        r13 = 0;
        r14 = 0;
        if (r0 > r11) goto L_0x02e8;
    L_0x0041:
        r0 = r7.mFromLayout;
        if (r0 == 0) goto L_0x004a;
    L_0x0045:
        r0 = r7.mInLayout;
        if (r0 != 0) goto L_0x004a;
    L_0x0049:
        return;
    L_0x004a:
        r0 = r16.getAnimatingAway();
        if (r0 != 0) goto L_0x0056;
    L_0x0050:
        r0 = r16.getAnimator();
        if (r0 == 0) goto L_0x0068;
    L_0x0056:
        r7.setAnimatingAway(r13);
        r7.setAnimator(r13);
        r2 = r16.getStateAfterAnimating();
        r3 = 0;
        r4 = 0;
        r5 = 1;
        r0 = r6;
        r1 = r7;
        r0.moveToState(r1, r2, r3, r4, r5);
    L_0x0068:
        r0 = r7.mState;
        switch(r0) {
            case 0: goto L_0x006f;
            case 1: goto L_0x019d;
            case 2: goto L_0x029a;
            case 3: goto L_0x029e;
            case 4: goto L_0x02c0;
            default: goto L_0x006d;
        };
    L_0x006d:
        goto L_0x044d;
    L_0x006f:
        if (r11 <= 0) goto L_0x019d;
    L_0x0071:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x008b;
    L_0x0075:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "moveto CREATED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x008b:
        r0 = r7.mSavedFragmentState;
        if (r0 == 0) goto L_0x00d3;
    L_0x008f:
        r0 = r7.mSavedFragmentState;
        r1 = r6.mHost;
        r1 = r1.getContext();
        r1 = r1.getClassLoader();
        r0.setClassLoader(r1);
        r0 = r7.mSavedFragmentState;
        r1 = "android:view_state";
        r0 = r0.getSparseParcelableArray(r1);
        r7.mSavedViewState = r0;
        r0 = r7.mSavedFragmentState;
        r1 = "android:target_state";
        r0 = r6.getFragment(r0, r1);
        r7.mTarget = r0;
        r0 = r7.mTarget;
        if (r0 == 0) goto L_0x00c0;
    L_0x00b6:
        r0 = r7.mSavedFragmentState;
        r1 = "android:target_req_state";
        r0 = r0.getInt(r1, r14);
        r7.mTargetRequestCode = r0;
    L_0x00c0:
        r0 = r7.mSavedFragmentState;
        r1 = "android:user_visible_hint";
        r0 = r0.getBoolean(r1, r8);
        r7.mUserVisibleHint = r0;
        r0 = r7.mUserVisibleHint;
        if (r0 != 0) goto L_0x00d3;
    L_0x00ce:
        r7.mDeferStart = r8;
        if (r11 <= r10) goto L_0x00d3;
    L_0x00d2:
        r11 = r10;
    L_0x00d3:
        r0 = r6.mHost;
        r7.mHost = r0;
        r0 = r6.mParent;
        r7.mParentFragment = r0;
        r0 = r6.mParent;
        if (r0 == 0) goto L_0x00e4;
    L_0x00df:
        r0 = r6.mParent;
        r0 = r0.mChildFragmentManager;
        goto L_0x00ea;
    L_0x00e4:
        r0 = r6.mHost;
        r0 = r0.getFragmentManagerImpl();
    L_0x00ea:
        r7.mFragmentManager = r0;
        r0 = r7.mTarget;
        if (r0 == 0) goto L_0x0134;
    L_0x00f0:
        r0 = r6.mActive;
        r1 = r7.mTarget;
        r1 = r1.mIndex;
        r0 = r0.get(r1);
        r1 = r7.mTarget;
        if (r0 == r1) goto L_0x0124;
    L_0x00fe:
        r0 = new java.lang.IllegalStateException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Fragment ";
        r1.append(r2);
        r1.append(r7);
        r2 = " declared target fragment ";
        r1.append(r2);
        r2 = r7.mTarget;
        r1.append(r2);
        r2 = " that does not belong to this FragmentManager!";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0124:
        r0 = r7.mTarget;
        r0 = r0.mState;
        if (r0 >= r8) goto L_0x0134;
    L_0x012a:
        r1 = r7.mTarget;
        r2 = 1;
        r3 = 0;
        r4 = 0;
        r5 = 1;
        r0 = r6;
        r0.moveToState(r1, r2, r3, r4, r5);
    L_0x0134:
        r0 = r6.mHost;
        r0 = r0.getContext();
        r6.dispatchOnFragmentPreAttached(r7, r0, r14);
        r7.mCalled = r14;
        r0 = r6.mHost;
        r0 = r0.getContext();
        r7.onAttach(r0);
        r0 = r7.mCalled;
        if (r0 != 0) goto L_0x0168;
    L_0x014c:
        r0 = new android.support.v4.app.SuperNotCalledException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Fragment ";
        r1.append(r2);
        r1.append(r7);
        r2 = " did not call through to super.onAttach()";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        throw r0;
    L_0x0168:
        r0 = r7.mParentFragment;
        if (r0 != 0) goto L_0x0172;
    L_0x016c:
        r0 = r6.mHost;
        r0.onAttachFragment(r7);
        goto L_0x0177;
    L_0x0172:
        r0 = r7.mParentFragment;
        r0.onAttachFragment(r7);
    L_0x0177:
        r0 = r6.mHost;
        r0 = r0.getContext();
        r6.dispatchOnFragmentAttached(r7, r0, r14);
        r0 = r7.mIsCreated;
        if (r0 != 0) goto L_0x0194;
    L_0x0184:
        r0 = r7.mSavedFragmentState;
        r6.dispatchOnFragmentPreCreated(r7, r0, r14);
        r0 = r7.mSavedFragmentState;
        r7.performCreate(r0);
        r0 = r7.mSavedFragmentState;
        r6.dispatchOnFragmentCreated(r7, r0, r14);
        goto L_0x019b;
    L_0x0194:
        r0 = r7.mSavedFragmentState;
        r7.restoreChildFragmentState(r0);
        r7.mState = r8;
    L_0x019b:
        r7.mRetaining = r14;
    L_0x019d:
        r15.ensureInflatedFragmentView(r16);
        if (r11 <= r8) goto L_0x029a;
    L_0x01a2:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x01bc;
    L_0x01a6:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "moveto ACTIVITY_CREATED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x01bc:
        r0 = r7.mFromLayout;
        if (r0 != 0) goto L_0x0285;
    L_0x01c0:
        r0 = r7.mContainerId;
        if (r0 == 0) goto L_0x0234;
    L_0x01c4:
        r0 = r7.mContainerId;
        r1 = -1;
        if (r0 != r1) goto L_0x01e7;
    L_0x01c9:
        r0 = new java.lang.IllegalArgumentException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "Cannot create fragment ";
        r1.append(r2);
        r1.append(r7);
        r2 = " for a container view with no id";
        r1.append(r2);
        r1 = r1.toString();
        r0.<init>(r1);
        r6.throwException(r0);
    L_0x01e7:
        r0 = r6.mContainer;
        r1 = r7.mContainerId;
        r0 = r0.onFindViewById(r1);
        r0 = (android.view.ViewGroup) r0;
        if (r0 != 0) goto L_0x0235;
    L_0x01f3:
        r1 = r7.mRestored;
        if (r1 != 0) goto L_0x0235;
    L_0x01f7:
        r1 = r16.getResources();	 Catch:{ NotFoundException -> 0x0202 }
        r2 = r7.mContainerId;	 Catch:{ NotFoundException -> 0x0202 }
        r1 = r1.getResourceName(r2);	 Catch:{ NotFoundException -> 0x0202 }
        goto L_0x0204;
    L_0x0202:
        r1 = "unknown";
    L_0x0204:
        r2 = new java.lang.IllegalArgumentException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "No view found for id 0x";
        r3.append(r4);
        r4 = r7.mContainerId;
        r4 = java.lang.Integer.toHexString(r4);
        r3.append(r4);
        r4 = " (";
        r3.append(r4);
        r3.append(r1);
        r1 = ") for fragment ";
        r3.append(r1);
        r3.append(r7);
        r1 = r3.toString();
        r2.<init>(r1);
        r6.throwException(r2);
        goto L_0x0235;
    L_0x0234:
        r0 = r13;
    L_0x0235:
        r7.mContainer = r0;
        r1 = r7.mSavedFragmentState;
        r1 = r7.performGetLayoutInflater(r1);
        r2 = r7.mSavedFragmentState;
        r1 = r7.performCreateView(r1, r0, r2);
        r7.mView = r1;
        r1 = r7.mView;
        if (r1 == 0) goto L_0x0283;
    L_0x0249:
        r1 = r7.mView;
        r7.mInnerView = r1;
        r1 = r7.mView;
        r1.setSaveFromParentEnabled(r14);
        if (r0 == 0) goto L_0x0259;
    L_0x0254:
        r1 = r7.mView;
        r0.addView(r1);
    L_0x0259:
        r0 = r7.mHidden;
        if (r0 == 0) goto L_0x0264;
    L_0x025d:
        r0 = r7.mView;
        r1 = 8;
        r0.setVisibility(r1);
    L_0x0264:
        r0 = r7.mView;
        r1 = r7.mSavedFragmentState;
        r7.onViewCreated(r0, r1);
        r0 = r7.mView;
        r1 = r7.mSavedFragmentState;
        r6.dispatchOnFragmentViewCreated(r7, r0, r1, r14);
        r0 = r7.mView;
        r0 = r0.getVisibility();
        if (r0 != 0) goto L_0x027f;
    L_0x027a:
        r0 = r7.mContainer;
        if (r0 == 0) goto L_0x027f;
    L_0x027e:
        goto L_0x0280;
    L_0x027f:
        r8 = r14;
    L_0x0280:
        r7.mIsNewlyAdded = r8;
        goto L_0x0285;
    L_0x0283:
        r7.mInnerView = r13;
    L_0x0285:
        r0 = r7.mSavedFragmentState;
        r7.performActivityCreated(r0);
        r0 = r7.mSavedFragmentState;
        r6.dispatchOnFragmentActivityCreated(r7, r0, r14);
        r0 = r7.mView;
        if (r0 == 0) goto L_0x0298;
    L_0x0293:
        r0 = r7.mSavedFragmentState;
        r7.restoreViewState(r0);
    L_0x0298:
        r7.mSavedFragmentState = r13;
    L_0x029a:
        if (r11 <= r12) goto L_0x029e;
    L_0x029c:
        r7.mState = r10;
    L_0x029e:
        if (r11 <= r10) goto L_0x02c0;
    L_0x02a0:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x02ba;
    L_0x02a4:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "moveto STARTED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x02ba:
        r16.performStart();
        r6.dispatchOnFragmentStarted(r7, r14);
    L_0x02c0:
        if (r11 <= r9) goto L_0x044d;
    L_0x02c2:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x02dc;
    L_0x02c6:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "moveto RESUMED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x02dc:
        r16.performResume();
        r6.dispatchOnFragmentResumed(r7, r14);
        r7.mSavedFragmentState = r13;
        r7.mSavedViewState = r13;
        goto L_0x044d;
    L_0x02e8:
        r0 = r7.mState;
        if (r0 <= r11) goto L_0x044d;
    L_0x02ec:
        r0 = r7.mState;
        switch(r0) {
            case 1: goto L_0x03d7;
            case 2: goto L_0x0357;
            case 3: goto L_0x0338;
            case 4: goto L_0x0316;
            case 5: goto L_0x02f3;
            default: goto L_0x02f1;
        };
    L_0x02f1:
        goto L_0x044d;
    L_0x02f3:
        r0 = 5;
        if (r11 >= r0) goto L_0x0316;
    L_0x02f6:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x0310;
    L_0x02fa:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "movefrom RESUMED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x0310:
        r16.performPause();
        r6.dispatchOnFragmentPaused(r7, r14);
    L_0x0316:
        if (r11 >= r9) goto L_0x0338;
    L_0x0318:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x0332;
    L_0x031c:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "movefrom STARTED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x0332:
        r16.performStop();
        r6.dispatchOnFragmentStopped(r7, r14);
    L_0x0338:
        if (r11 >= r10) goto L_0x0357;
    L_0x033a:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x0354;
    L_0x033e:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "movefrom STOPPED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x0354:
        r16.performReallyStop();
    L_0x0357:
        if (r11 >= r12) goto L_0x03d7;
    L_0x0359:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x0373;
    L_0x035d:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "movefrom ACTIVITY_CREATED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x0373:
        r0 = r7.mView;
        if (r0 == 0) goto L_0x0386;
    L_0x0377:
        r0 = r6.mHost;
        r0 = r0.onShouldSaveFragmentState(r7);
        if (r0 == 0) goto L_0x0386;
    L_0x037f:
        r0 = r7.mSavedViewState;
        if (r0 != 0) goto L_0x0386;
    L_0x0383:
        r15.saveFragmentViewState(r16);
    L_0x0386:
        r16.performDestroyView();
        r6.dispatchOnFragmentViewDestroyed(r7, r14);
        r0 = r7.mView;
        if (r0 == 0) goto L_0x03cf;
    L_0x0390:
        r0 = r7.mContainer;
        if (r0 == 0) goto L_0x03cf;
    L_0x0394:
        r0 = r7.mView;
        r0.clearAnimation();
        r0 = r7.mContainer;
        r1 = r7.mView;
        r0.endViewTransition(r1);
        r0 = r6.mCurState;
        r1 = 0;
        if (r0 <= 0) goto L_0x03c0;
    L_0x03a5:
        r0 = r6.mDestroyed;
        if (r0 != 0) goto L_0x03c0;
    L_0x03a9:
        r0 = r7.mView;
        r0 = r0.getVisibility();
        if (r0 != 0) goto L_0x03c0;
    L_0x03b1:
        r0 = r7.mPostponedAlpha;
        r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
        if (r0 < 0) goto L_0x03c0;
    L_0x03b7:
        r0 = r18;
        r2 = r19;
        r0 = r6.loadAnimation(r7, r0, r14, r2);
        goto L_0x03c1;
    L_0x03c0:
        r0 = r13;
    L_0x03c1:
        r7.mPostponedAlpha = r1;
        if (r0 == 0) goto L_0x03c8;
    L_0x03c5:
        r6.animateRemoveFragment(r7, r0, r11);
    L_0x03c8:
        r0 = r7.mContainer;
        r1 = r7.mView;
        r0.removeView(r1);
    L_0x03cf:
        r7.mContainer = r13;
        r7.mView = r13;
        r7.mInnerView = r13;
        r7.mInLayout = r14;
    L_0x03d7:
        if (r11 >= r8) goto L_0x044d;
    L_0x03d9:
        r0 = r6.mDestroyed;
        if (r0 == 0) goto L_0x03fe;
    L_0x03dd:
        r0 = r16.getAnimatingAway();
        if (r0 == 0) goto L_0x03ee;
    L_0x03e3:
        r0 = r16.getAnimatingAway();
        r7.setAnimatingAway(r13);
        r0.clearAnimation();
        goto L_0x03fe;
    L_0x03ee:
        r0 = r16.getAnimator();
        if (r0 == 0) goto L_0x03fe;
    L_0x03f4:
        r0 = r16.getAnimator();
        r7.setAnimator(r13);
        r0.cancel();
    L_0x03fe:
        r0 = r16.getAnimatingAway();
        if (r0 != 0) goto L_0x0449;
    L_0x0404:
        r0 = r16.getAnimator();
        if (r0 == 0) goto L_0x040b;
    L_0x040a:
        goto L_0x0449;
    L_0x040b:
        r0 = DEBUG;
        if (r0 == 0) goto L_0x0425;
    L_0x040f:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "movefrom CREATED: ";
        r1.append(r2);
        r1.append(r7);
        r1 = r1.toString();
        android.util.Log.v(r0, r1);
    L_0x0425:
        r0 = r7.mRetaining;
        if (r0 != 0) goto L_0x0430;
    L_0x0429:
        r16.performDestroy();
        r6.dispatchOnFragmentDestroyed(r7, r14);
        goto L_0x0432;
    L_0x0430:
        r7.mState = r14;
    L_0x0432:
        r16.performDetach();
        r6.dispatchOnFragmentDetached(r7, r14);
        if (r20 != 0) goto L_0x044d;
    L_0x043a:
        r0 = r7.mRetaining;
        if (r0 != 0) goto L_0x0442;
    L_0x043e:
        r15.makeInactive(r16);
        goto L_0x044d;
    L_0x0442:
        r7.mHost = r13;
        r7.mParentFragment = r13;
        r7.mFragmentManager = r13;
        goto L_0x044d;
    L_0x0449:
        r7.setStateAfterAnimating(r11);
        goto L_0x044e;
    L_0x044d:
        r8 = r11;
    L_0x044e:
        r0 = r7.mState;
        if (r0 == r8) goto L_0x0481;
    L_0x0452:
        r0 = "FragmentManager";
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "moveToState: Fragment state for ";
        r1.append(r2);
        r1.append(r7);
        r2 = " not updated inline; ";
        r1.append(r2);
        r2 = "expected state ";
        r1.append(r2);
        r1.append(r8);
        r2 = " found ";
        r1.append(r2);
        r2 = r7.mState;
        r1.append(r2);
        r1 = r1.toString();
        android.util.Log.w(r0, r1);
        r7.mState = r8;
    L_0x0481:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    private void animateRemoveFragment(@NonNull final Fragment fragment, @NonNull AnimationOrAnimator animationOrAnimator, int i) {
        final View view = fragment.mView;
        fragment.setStateAfterAnimating(i);
        if (animationOrAnimator.animation != null) {
            Animation animation = animationOrAnimator.animation;
            fragment.setAnimatingAway(fragment.mView);
            animation.setAnimationListener(new AnimationListenerWrapper(getAnimationListener(animation)) {
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    if (fragment.getAnimatingAway() != null) {
                        fragment.setAnimatingAway(null);
                        FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                    }
                }
            });
            setHWLayerAnimListenerIfAlpha(view, animationOrAnimator);
            fragment.mView.startAnimation(animation);
            return;
        }
        Animator animator = animationOrAnimator.animator;
        fragment.setAnimator(animationOrAnimator.animator);
        final ViewGroup viewGroup = fragment.mContainer;
        if (viewGroup != null) {
            viewGroup.startViewTransition(view);
        }
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                if (viewGroup != null) {
                    viewGroup.endViewTransition(view);
                }
                if (fragment.getAnimator() != null) {
                    fragment.setAnimator(null);
                    FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                }
            }
        });
        animator.setTarget(fragment.mView);
        setHWLayerAnimListenerIfAlpha(fragment.mView, animationOrAnimator);
        animator.start();
    }

    void moveToState(Fragment fragment) {
        moveToState(fragment, this.mCurState, 0, 0, false);
    }

    void ensureInflatedFragmentView(Fragment fragment) {
        if (fragment.mFromLayout && !fragment.mPerformedCreateView) {
            View view = null;
            fragment.mView = fragment.performCreateView(fragment.performGetLayoutInflater(fragment.mSavedFragmentState), view, fragment.mSavedFragmentState);
            if (fragment.mView != null) {
                fragment.mInnerView = fragment.mView;
                boolean z = false;
                fragment.mView.setSaveFromParentEnabled(z);
                if (fragment.mHidden) {
                    fragment.mView.setVisibility(8);
                }
                fragment.onViewCreated(fragment.mView, fragment.mSavedFragmentState);
                dispatchOnFragmentViewCreated(fragment, fragment.mView, fragment.mSavedFragmentState, z);
                return;
            }
            fragment.mInnerView = view;
        }
    }

    void completeShowHideFragment(final Fragment fragment) {
        boolean z = true;
        boolean z2 = false;
        if (fragment.mView != null) {
            AnimationOrAnimator loadAnimation = loadAnimation(fragment, fragment.getNextTransition(), fragment.mHidden ^ z, fragment.getNextTransitionStyle());
            if (loadAnimation == null || loadAnimation.animator == null) {
                if (loadAnimation != null) {
                    setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                    fragment.mView.startAnimation(loadAnimation.animation);
                    loadAnimation.animation.start();
                }
                int i = (!fragment.mHidden || fragment.isHideReplaced()) ? z2 : 8;
                fragment.mView.setVisibility(i);
                if (fragment.isHideReplaced()) {
                    fragment.setHideReplaced(z2);
                }
            } else {
                loadAnimation.animator.setTarget(fragment.mView);
                if (!fragment.mHidden) {
                    fragment.mView.setVisibility(z2);
                } else if (fragment.isHideReplaced()) {
                    fragment.setHideReplaced(z2);
                } else {
                    final ViewGroup viewGroup = fragment.mContainer;
                    final View view = fragment.mView;
                    viewGroup.startViewTransition(view);
                    loadAnimation.animator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
                            viewGroup.endViewTransition(view);
                            animator.removeListener(this);
                            if (fragment.mView != null) {
                                fragment.mView.setVisibility(8);
                            }
                        }
                    });
                }
                setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                loadAnimation.animator.start();
            }
        }
        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = z;
        }
        fragment.mHiddenChanged = z2;
        fragment.onHiddenChanged(fragment.mHidden);
    }

    void moveFragmentToExpectedState(Fragment fragment) {
        if (fragment != null) {
            int i = this.mCurState;
            boolean z = false;
            boolean z2 = true;
            if (fragment.mRemoving) {
                if (fragment.isInBackStack()) {
                    i = Math.min(i, z2);
                } else {
                    i = Math.min(i, z);
                }
            }
            moveToState(fragment, i, fragment.getNextTransition(), fragment.getNextTransitionStyle(), false);
            if (fragment.mView != null) {
                Fragment findFragmentUnder = findFragmentUnder(fragment);
                if (findFragmentUnder != null) {
                    View view = findFragmentUnder.mView;
                    ViewGroup viewGroup = fragment.mContainer;
                    i = viewGroup.indexOfChild(view);
                    int indexOfChild = viewGroup.indexOfChild(fragment.mView);
                    if (indexOfChild < i) {
                        viewGroup.removeViewAt(indexOfChild);
                        viewGroup.addView(fragment.mView, i);
                    }
                }
                if (fragment.mIsNewlyAdded && fragment.mContainer != null) {
                    float f = 0.0f;
                    if (fragment.mPostponedAlpha > f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    fragment.mPostponedAlpha = f;
                    fragment.mIsNewlyAdded = z;
                    AnimationOrAnimator loadAnimation = loadAnimation(fragment, fragment.getNextTransition(), z2, fragment.getNextTransitionStyle());
                    if (loadAnimation != null) {
                        setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                        if (loadAnimation.animation != null) {
                            fragment.mView.startAnimation(loadAnimation.animation);
                        } else {
                            loadAnimation.animator.setTarget(fragment.mView);
                            loadAnimation.animator.start();
                        }
                    }
                }
            }
            if (fragment.mHiddenChanged) {
                completeShowHideFragment(fragment);
            }
        }
    }

    void moveToState(int i, boolean z) {
        if (this.mHost == null && i != 0) {
            throw new IllegalStateException("No activity");
        } else if (z || i != this.mCurState) {
            this.mCurState = i;
            if (this.mActive != null) {
                Fragment fragment;
                i = this.mAdded.size();
                z = false;
                int i2 = z;
                int i3 = i2;
                while (i2 < i) {
                    fragment = (Fragment) this.mAdded.get(i2);
                    moveFragmentToExpectedState(fragment);
                    if (fragment.mLoaderManager != null) {
                        i3 |= fragment.mLoaderManager.hasRunningLoaders();
                    }
                    i2++;
                }
                i = this.mActive.size();
                for (i2 = z; i2 < i; i2++) {
                    fragment = (Fragment) this.mActive.valueAt(i2);
                    if (fragment != null && ((fragment.mRemoving || fragment.mDetached) && !fragment.mIsNewlyAdded)) {
                        moveFragmentToExpectedState(fragment);
                        if (fragment.mLoaderManager != null) {
                            i3 |= fragment.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (i3 == 0) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
                    this.mHost.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = z;
                }
            }
        }
    }

    void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment fragment = (Fragment) this.mActive.valueAt(i);
                if (fragment != null) {
                    performPendingDeferredStart(fragment);
                }
            }
        }
    }

    void makeActive(Fragment fragment) {
        if (fragment.mIndex < 0) {
            int i = this.mNextFragmentIndex;
            this.mNextFragmentIndex = i + 1;
            fragment.setIndex(i, this.mParent);
            if (this.mActive == null) {
                this.mActive = new SparseArray();
            }
            this.mActive.put(fragment.mIndex, fragment);
            if (DEBUG) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Allocated fragment index ");
                stringBuilder.append(fragment);
                Log.v("FragmentManager", stringBuilder.toString());
            }
        }
    }

    void makeInactive(Fragment fragment) {
        if (fragment.mIndex >= 0) {
            if (DEBUG) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Freeing fragment index ");
                stringBuilder.append(fragment);
                Log.v("FragmentManager", stringBuilder.toString());
            }
            this.mActive.put(fragment.mIndex, null);
            this.mHost.inactivateFragment(fragment.mWho);
            fragment.initState();
        }
    }

    public void addFragment(Fragment fragment, boolean z) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("add: ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        makeActive(fragment);
        if (!fragment.mDetached) {
            if (this.mAdded.contains(fragment)) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Fragment already added: ");
                stringBuilder2.append(fragment);
                throw new IllegalStateException(stringBuilder2.toString());
            }
            synchronized (this.mAdded) {
                this.mAdded.add(fragment);
            }
            boolean z2 = true;
            fragment.mAdded = z2;
            boolean z3 = false;
            fragment.mRemoving = z3;
            if (fragment.mView == null) {
                fragment.mHiddenChanged = z3;
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = z2;
            }
            if (z) {
                moveToState(fragment);
            }
        }
    }

    public void removeFragment(Fragment fragment) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("remove: ");
            stringBuilder.append(fragment);
            stringBuilder.append(" nesting=");
            stringBuilder.append(fragment.mBackStackNesting);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        boolean z = true;
        int isInBackStack = fragment.isInBackStack() ^ z;
        if (!fragment.mDetached || isInBackStack != 0) {
            synchronized (this.mAdded) {
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = z;
            }
            fragment.mAdded = false;
            fragment.mRemoving = z;
        }
    }

    public void hideFragment(Fragment fragment) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("hide: ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        if (!fragment.mHidden) {
            boolean z = true;
            fragment.mHidden = z;
            fragment.mHiddenChanged = z ^ fragment.mHiddenChanged;
        }
    }

    public void showFragment(Fragment fragment) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("show: ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            fragment.mHiddenChanged ^= 1;
        }
    }

    public void detachFragment(Fragment fragment) {
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("detach: ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        if (!fragment.mDetached) {
            boolean z = true;
            fragment.mDetached = z;
            if (fragment.mAdded) {
                if (DEBUG) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("remove from detach: ");
                    stringBuilder2.append(fragment);
                    Log.v("FragmentManager", stringBuilder2.toString());
                }
                synchronized (this.mAdded) {
                    this.mAdded.remove(fragment);
                }
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = z;
                }
                fragment.mAdded = false;
            }
        }
    }

    public void attachFragment(Fragment fragment) {
        StringBuilder stringBuilder;
        if (DEBUG) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("attach: ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                if (this.mAdded.contains(fragment)) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Fragment already added: ");
                    stringBuilder.append(fragment);
                    throw new IllegalStateException(stringBuilder.toString());
                }
                if (DEBUG) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("add from attach: ");
                    stringBuilder.append(fragment);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
                synchronized (this.mAdded) {
                    this.mAdded.add(fragment);
                }
                boolean z = true;
                fragment.mAdded = z;
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = z;
                }
            }
        }
    }

    public Fragment findFragmentById(int i) {
        int size;
        Fragment fragment;
        for (size = this.mAdded.size() - 1; size >= 0; size--) {
            fragment = (Fragment) this.mAdded.get(size);
            if (fragment != null && fragment.mFragmentId == i) {
                return fragment;
            }
        }
        if (this.mActive != null) {
            for (size = this.mActive.size() - 1; size >= 0; size--) {
                fragment = (Fragment) this.mActive.valueAt(size);
                if (fragment != null && fragment.mFragmentId == i) {
                    return fragment;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByTag(String str) {
        int size;
        Fragment fragment;
        if (str != null) {
            for (size = this.mAdded.size() - 1; size >= 0; size--) {
                fragment = (Fragment) this.mAdded.get(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        if (!(this.mActive == null || str == null)) {
            for (size = this.mActive.size() - 1; size >= 0; size--) {
                fragment = (Fragment) this.mActive.valueAt(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        return null;
    }

    public Fragment findFragmentByWho(String str) {
        if (!(this.mActive == null || str == null)) {
            for (int size = this.mActive.size() - 1; size >= 0; size--) {
                Fragment fragment = (Fragment) this.mActive.valueAt(size);
                if (fragment != null) {
                    fragment = fragment.findFragmentByWho(str);
                    if (fragment != null) {
                        return fragment;
                    }
                }
            }
        }
        return null;
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        } else if (this.mNoTransactionsBecause != null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Can not perform this action inside of ");
            stringBuilder.append(this.mNoTransactionsBecause);
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public boolean isStateSaved() {
        return this.mStateSaved;
    }

    public void enqueueAction(OpGenerator opGenerator, boolean z) {
        if (!z) {
            checkStateLoss();
        }
        synchronized (this) {
            if (!this.mDestroyed && this.mHost != null) {
                if (this.mPendingActions == null) {
                    this.mPendingActions = new ArrayList();
                }
                this.mPendingActions.add(opGenerator);
                scheduleCommit();
            } else if (z) {
            } else {
                throw new IllegalStateException("Activity has been destroyed");
            }
        }
    }

    private void scheduleCommit() {
        synchronized (this) {
            int i = 0;
            int i2 = 1;
            int i3 = (this.mPostponedTransactions == null || this.mPostponedTransactions.isEmpty()) ? i : i2;
            if (this.mPendingActions != null && this.mPendingActions.size() == i2) {
                i = i2;
            }
            if (!(i3 == 0 && i == 0)) {
                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                this.mHost.getHandler().post(this.mExecCommit);
            }
        }
    }

    public int allocBackStackIndex(BackStackRecord backStackRecord) {
        synchronized (this) {
            int size;
            StringBuilder stringBuilder;
            if (this.mAvailBackStackIndices == null || this.mAvailBackStackIndices.size() <= 0) {
                if (this.mBackStackIndices == null) {
                    this.mBackStackIndices = new ArrayList();
                }
                size = this.mBackStackIndices.size();
                if (DEBUG) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Setting back stack index ");
                    stringBuilder.append(size);
                    stringBuilder.append(" to ");
                    stringBuilder.append(backStackRecord);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
                this.mBackStackIndices.add(backStackRecord);
                return size;
            }
            size = ((Integer) this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1)).intValue();
            if (DEBUG) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Adding back stack index ");
                stringBuilder.append(size);
                stringBuilder.append(" with ");
                stringBuilder.append(backStackRecord);
                Log.v("FragmentManager", stringBuilder.toString());
            }
            this.mBackStackIndices.set(size, backStackRecord);
            return size;
        }
    }

    public void setBackStackIndex(int i, BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList();
            }
            int size = this.mBackStackIndices.size();
            StringBuilder stringBuilder;
            if (i < size) {
                if (DEBUG) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Setting back stack index ");
                    stringBuilder.append(i);
                    stringBuilder.append(" to ");
                    stringBuilder.append(backStackRecord);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
                this.mBackStackIndices.set(i, backStackRecord);
            } else {
                while (size < i) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList();
                    }
                    if (DEBUG) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Adding available back stack index ");
                        stringBuilder2.append(size);
                        Log.v("FragmentManager", stringBuilder2.toString());
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(size));
                    size++;
                }
                if (DEBUG) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Adding back stack index ");
                    stringBuilder.append(i);
                    stringBuilder.append(" with ");
                    stringBuilder.append(backStackRecord);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
                this.mBackStackIndices.add(backStackRecord);
            }
        }
    }

    public void freeBackStackIndex(int i) {
        synchronized (this) {
            this.mBackStackIndices.set(i, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList();
            }
            if (DEBUG) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Freeing back stack index ");
                stringBuilder.append(i);
                Log.v("FragmentManager", stringBuilder.toString());
            }
            this.mAvailBackStackIndices.add(Integer.valueOf(i));
        }
    }

    private void ensureExecReady(boolean z) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        } else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        } else {
            if (!z) {
                checkStateLoss();
            }
            if (this.mTmpRecords == null) {
                this.mTmpRecords = new ArrayList();
                this.mTmpIsPop = new ArrayList();
            }
            this.mExecutingActions = true;
            z = false;
            ArrayList arrayList = null;
            try {
                executePostponedTransaction(arrayList, arrayList);
            } finally {
                this.mExecutingActions = z;
            }
        }
    }

    public void execSingleAction(OpGenerator opGenerator, boolean z) {
        if (!z || (this.mHost != null && !this.mDestroyed)) {
            ensureExecReady(z);
            if (opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
                this.mExecutingActions = true;
                try {
                    removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                } finally {
                    cleanupExec();
                }
            }
            doPendingDeferredStart();
            burpActive();
        }
    }

    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }

    public boolean execPendingActions() {
        boolean z = true;
        ensureExecReady(z);
        boolean z2 = false;
        while (generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = z;
            try {
                removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
                cleanupExec();
                z2 = z;
            } catch (Throwable th) {
                cleanupExec();
                throw th;
            }
        }
        doPendingDeferredStart();
        burpActive();
        return z2;
    }

    private void executePostponedTransaction(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        int i = 0;
        int size = this.mPostponedTransactions == null ? i : this.mPostponedTransactions.size();
        int i2 = i;
        while (i2 < size) {
            int indexOf;
            StartEnterTransitionListener startEnterTransitionListener = (StartEnterTransitionListener) this.mPostponedTransactions.get(i2);
            int i3 = -1;
            if (!(arrayList == null || startEnterTransitionListener.mIsBack)) {
                indexOf = arrayList.indexOf(startEnterTransitionListener.mRecord);
                if (indexOf != i3 && ((Boolean) arrayList2.get(indexOf)).booleanValue()) {
                    startEnterTransitionListener.cancelTransaction();
                    i2++;
                }
            }
            if (startEnterTransitionListener.isReady() || (arrayList != null && startEnterTransitionListener.mRecord.interactsWith(arrayList, i, arrayList.size()))) {
                this.mPostponedTransactions.remove(i2);
                i2--;
                size--;
                if (!(arrayList == null || startEnterTransitionListener.mIsBack)) {
                    indexOf = arrayList.indexOf(startEnterTransitionListener.mRecord);
                    if (indexOf != i3 && ((Boolean) arrayList2.get(indexOf)).booleanValue()) {
                        startEnterTransitionListener.cancelTransaction();
                    }
                }
                startEnterTransitionListener.completeTransaction();
            }
            i2++;
        }
    }

    private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        if (arrayList != null && !arrayList.isEmpty()) {
            if (arrayList2 == null || arrayList.size() != arrayList2.size()) {
                throw new IllegalStateException("Internal error with the back stack records");
            }
            executePostponedTransaction(arrayList, arrayList2);
            int size = arrayList.size();
            int i = 0;
            int i2 = i;
            while (i < size) {
                if (!((BackStackRecord) arrayList.get(i)).mReorderingAllowed) {
                    if (i2 != i) {
                        executeOpsTogether(arrayList, arrayList2, i2, i);
                    }
                    i2 = i + 1;
                    if (((Boolean) arrayList2.get(i)).booleanValue()) {
                        while (i2 < size && ((Boolean) arrayList2.get(i2)).booleanValue() && !((BackStackRecord) arrayList.get(i2)).mReorderingAllowed) {
                            i2++;
                        }
                    }
                    executeOpsTogether(arrayList, arrayList2, i, i2);
                    i = i2 - 1;
                }
                i++;
            }
            if (i2 != size) {
                executeOpsTogether(arrayList, arrayList2, i2, size);
            }
        }
    }

    private void executeOpsTogether(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2) {
        boolean z;
        int i3;
        ArrayList<BackStackRecord> arrayList3 = arrayList;
        ArrayList<Boolean> arrayList4 = arrayList2;
        int i4 = i;
        int i5 = i2;
        boolean z2 = ((BackStackRecord) arrayList3.get(i4)).mReorderingAllowed;
        if (this.mTmpAddedFragments == null) {
            r6.mTmpAddedFragments = new ArrayList();
        } else {
            r6.mTmpAddedFragments.clear();
        }
        r6.mTmpAddedFragments.addAll(r6.mAdded);
        boolean z3 = false;
        Fragment primaryNavigationFragment = getPrimaryNavigationFragment();
        boolean z4 = z3;
        int i6 = i4;
        while (true) {
            z = true;
            if (i6 >= i5) {
                break;
            }
            BackStackRecord backStackRecord = (BackStackRecord) arrayList3.get(i6);
            if (((Boolean) arrayList4.get(i6)).booleanValue()) {
                primaryNavigationFragment = backStackRecord.trackAddedFragmentsInPop(r6.mTmpAddedFragments, primaryNavigationFragment);
            } else {
                primaryNavigationFragment = backStackRecord.expandOps(r6.mTmpAddedFragments, primaryNavigationFragment);
            }
            z4 = (z4 || backStackRecord.mAddToBackStack) ? z : z3;
            i6++;
        }
        r6.mTmpAddedFragments.clear();
        if (!z2) {
            FragmentTransition.startTransitions(r6, arrayList3, arrayList4, i4, i5, false);
        }
        executeOps(arrayList, arrayList2, i, i2);
        if (z2) {
            ArraySet arraySet = new ArraySet();
            addAddedFragments(arraySet);
            i6 = postponePostponableTransactions(arrayList3, arrayList4, i4, i5, arraySet);
            makeRemovedFragmentsInvisible(arraySet);
            i3 = i6;
        } else {
            i3 = i5;
        }
        if (i3 != i4 && z2) {
            FragmentTransition.startTransitions(r6, arrayList3, arrayList4, i4, i3, true);
            moveToState(r6.mCurState, z);
        }
        while (i4 < i5) {
            BackStackRecord backStackRecord2 = (BackStackRecord) arrayList3.get(i4);
            if (((Boolean) arrayList4.get(i4)).booleanValue() && backStackRecord2.mIndex >= 0) {
                freeBackStackIndex(backStackRecord2.mIndex);
                backStackRecord2.mIndex = -1;
            }
            backStackRecord2.runOnCommitRunnables();
            i4++;
        }
        if (z4) {
            reportBackStackChanged();
        }
    }

    private void makeRemovedFragmentsInvisible(ArraySet<Fragment> arraySet) {
        int size = arraySet.size();
        for (int i = 0; i < size; i++) {
            Fragment fragment = (Fragment) arraySet.valueAt(i);
            if (!fragment.mAdded) {
                View view = fragment.getView();
                fragment.mPostponedAlpha = view.getAlpha();
                view.setAlpha(0.0f);
            }
        }
    }

    private int postponePostponableTransactions(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2, ArraySet<Fragment> arraySet) {
        int i3 = i2;
        for (int i4 = i2 - 1; i4 >= i; i4--) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i4);
            boolean booleanValue = ((Boolean) arrayList2.get(i4)).booleanValue();
            boolean z = false;
            boolean z2 = (!backStackRecord.isPostponed() || backStackRecord.interactsWith(arrayList, i4 + 1, i2)) ? z : true;
            if (z2) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList();
                }
                OnStartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(backStackRecord, booleanValue);
                this.mPostponedTransactions.add(startEnterTransitionListener);
                backStackRecord.setOnStartPostponedListener(startEnterTransitionListener);
                if (booleanValue) {
                    backStackRecord.executeOps();
                } else {
                    backStackRecord.executePopOps(z);
                }
                i3--;
                if (i4 != i3) {
                    arrayList.remove(i4);
                    arrayList.add(i3, backStackRecord);
                }
                addAddedFragments(arraySet);
            }
        }
        return i3;
    }

    private void completeExecute(BackStackRecord backStackRecord, boolean z, boolean z2, boolean z3) {
        if (z) {
            backStackRecord.executePopOps(z3);
        } else {
            backStackRecord.executeOps();
        }
        boolean z4 = true;
        ArrayList arrayList = new ArrayList(z4);
        ArrayList arrayList2 = new ArrayList(z4);
        arrayList.add(backStackRecord);
        arrayList2.add(Boolean.valueOf(z));
        if (z2) {
            FragmentTransition.startTransitions(this, arrayList, arrayList2, 0, 1, true);
        }
        if (z3) {
            moveToState(this.mCurState, z4);
        }
        if (this.mActive != null) {
            int size = this.mActive.size();
            z2 = false;
            for (int i = z2; i < size; i++) {
                Fragment fragment = (Fragment) this.mActive.valueAt(i);
                if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && backStackRecord.interactsWith(fragment.mContainerId)) {
                    float f = 0.0f;
                    if (fragment.mPostponedAlpha > f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    if (z3) {
                        fragment.mPostponedAlpha = f;
                    } else {
                        fragment.mPostponedAlpha = -1.0f;
                        fragment.mIsNewlyAdded = z2;
                    }
                }
            }
        }
    }

    private Fragment findFragmentUnder(Fragment fragment) {
        ViewGroup viewGroup = fragment.mContainer;
        View view = fragment.mView;
        Fragment fragment2 = null;
        if (viewGroup == null || view == null) {
            return fragment2;
        }
        for (int indexOf = this.mAdded.indexOf(fragment) - 1; indexOf >= 0; indexOf--) {
            Fragment fragment3 = (Fragment) this.mAdded.get(indexOf);
            if (fragment3.mContainer == viewGroup && fragment3.mView != null) {
                return fragment3;
            }
        }
        return fragment2;
    }

    private static void executeOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, int i, int i2) {
        while (i < i2) {
            BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i);
            boolean z = true;
            if (((Boolean) arrayList2.get(i)).booleanValue()) {
                backStackRecord.bumpBackStackNesting(-1);
                if (i != i2 - 1) {
                    z = false;
                }
                backStackRecord.executePopOps(z);
            } else {
                backStackRecord.bumpBackStackNesting(z);
                backStackRecord.executeOps();
            }
            i++;
        }
    }

    private void addAddedFragments(ArraySet<Fragment> arraySet) {
        if (this.mCurState >= 1) {
            int min = Math.min(this.mCurState, 4);
            int size = this.mAdded.size();
            for (int i = 0; i < size; i++) {
                Fragment fragment = (Fragment) this.mAdded.get(i);
                if (fragment.mState < min) {
                    moveToState(fragment, min, fragment.getNextAnim(), fragment.getNextTransition(), false);
                    if (!(fragment.mView == null || fragment.mHidden || !fragment.mIsNewlyAdded)) {
                        arraySet.add(fragment);
                    }
                }
            }
        }
    }

    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                ((StartEnterTransitionListener) this.mPostponedTransactions.remove(0)).completeTransaction();
            }
        }
    }

    private void endAnimatingAwayFragments() {
        int i = 0;
        int size = this.mActive == null ? i : this.mActive.size();
        while (i < size) {
            Fragment fragment = (Fragment) this.mActive.valueAt(i);
            if (fragment != null) {
                if (fragment.getAnimatingAway() != null) {
                    int stateAfterAnimating = fragment.getStateAfterAnimating();
                    View animatingAway = fragment.getAnimatingAway();
                    fragment.setAnimatingAway(null);
                    Animation animation = animatingAway.getAnimation();
                    if (animation != null) {
                        animation.cancel();
                        animatingAway.clearAnimation();
                    }
                    moveToState(fragment, stateAfterAnimating, 0, 0, false);
                } else if (fragment.getAnimator() != null) {
                    fragment.getAnimator().end();
                }
            }
            i++;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean generateOpsForPendingActions(java.util.ArrayList<android.support.v4.app.BackStackRecord> r5, java.util.ArrayList<java.lang.Boolean> r6) {
        /*
        r4 = this;
        monitor-enter(r4);
        r0 = r4.mPendingActions;	 Catch:{ all -> 0x003c }
        r1 = 0;
        if (r0 == 0) goto L_0x003a;
    L_0x0006:
        r0 = r4.mPendingActions;	 Catch:{ all -> 0x003c }
        r0 = r0.size();	 Catch:{ all -> 0x003c }
        if (r0 != 0) goto L_0x000f;
    L_0x000e:
        goto L_0x003a;
    L_0x000f:
        r0 = r4.mPendingActions;	 Catch:{ all -> 0x003c }
        r0 = r0.size();	 Catch:{ all -> 0x003c }
        r2 = r1;
    L_0x0016:
        if (r1 >= r0) goto L_0x0028;
    L_0x0018:
        r3 = r4.mPendingActions;	 Catch:{ all -> 0x003c }
        r3 = r3.get(r1);	 Catch:{ all -> 0x003c }
        r3 = (android.support.v4.app.FragmentManagerImpl.OpGenerator) r3;	 Catch:{ all -> 0x003c }
        r3 = r3.generateOps(r5, r6);	 Catch:{ all -> 0x003c }
        r2 = r2 | r3;
        r1 = r1 + 1;
        goto L_0x0016;
    L_0x0028:
        r5 = r4.mPendingActions;	 Catch:{ all -> 0x003c }
        r5.clear();	 Catch:{ all -> 0x003c }
        r5 = r4.mHost;	 Catch:{ all -> 0x003c }
        r5 = r5.getHandler();	 Catch:{ all -> 0x003c }
        r6 = r4.mExecCommit;	 Catch:{ all -> 0x003c }
        r5.removeCallbacks(r6);	 Catch:{ all -> 0x003c }
        monitor-exit(r4);	 Catch:{ all -> 0x003c }
        return r2;
    L_0x003a:
        monitor-exit(r4);	 Catch:{ all -> 0x003c }
        return r1;
    L_0x003c:
        r5 = move-exception;
        monitor-exit(r4);	 Catch:{ all -> 0x003c }
        throw r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.generateOpsForPendingActions(java.util.ArrayList, java.util.ArrayList):boolean");
    }

    void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            boolean z = false;
            int i = z;
            int i2 = i;
            while (i < this.mActive.size()) {
                Fragment fragment = (Fragment) this.mActive.valueAt(i);
                if (!(fragment == null || fragment.mLoaderManager == null)) {
                    i2 |= fragment.mLoaderManager.hasRunningLoaders();
                }
                i++;
            }
            if (i2 == 0) {
                this.mHavePendingDeferredStart = z;
                startPendingDeferredFragments();
            }
        }
    }

    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                ((OnBackStackChangedListener) this.mBackStackChangeListeners.get(i)).onBackStackChanged();
            }
        }
    }

    void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList();
        }
        this.mBackStack.add(backStackRecord);
    }

    boolean popBackStackState(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2, String str, int i, int i2) {
        boolean z = false;
        if (this.mBackStack == null) {
            return z;
        }
        boolean z2 = true;
        int size;
        if (str == null && i < 0 && (i2 & 1) == 0) {
            size = this.mBackStack.size() - z2;
            if (size < 0) {
                return z;
            }
            arrayList.add(this.mBackStack.remove(size));
            arrayList2.add(Boolean.valueOf(z2));
        } else {
            int size2;
            if (str != null || i >= 0) {
                size2 = this.mBackStack.size() - z2;
                while (size2 >= 0) {
                    BackStackRecord backStackRecord = (BackStackRecord) this.mBackStack.get(size2);
                    if ((str != null && str.equals(backStackRecord.getName())) || (i >= 0 && i == backStackRecord.mIndex)) {
                        break;
                    }
                    size2--;
                }
                if (size2 < 0) {
                    return z;
                }
                if ((i2 & z2) != 0) {
                    size2--;
                    while (size2 >= 0) {
                        BackStackRecord backStackRecord2 = (BackStackRecord) this.mBackStack.get(size2);
                        if ((str == null || !str.equals(backStackRecord2.getName())) && (i < 0 || i != backStackRecord2.mIndex)) {
                            break;
                        }
                        size2--;
                    }
                }
            } else {
                size2 = -1;
            }
            if (size2 == this.mBackStack.size() - z2) {
                return z;
            }
            for (size = this.mBackStack.size() - z2; size > size2; size--) {
                arrayList.add(this.mBackStack.remove(size));
                arrayList2.add(Boolean.valueOf(z2));
            }
        }
        return z2;
    }

    FragmentManagerNonConfig retainNonConfig() {
        setRetaining(this.mSavedNonConfig);
        return this.mSavedNonConfig;
    }

    private static void setRetaining(FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (fragmentManagerNonConfig != null) {
            List<Fragment> fragments = fragmentManagerNonConfig.getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    fragment.mRetaining = true;
                }
            }
            List<FragmentManagerNonConfig> childNonConfigs = fragmentManagerNonConfig.getChildNonConfigs();
            if (childNonConfigs != null) {
                for (FragmentManagerNonConfig retaining : childNonConfigs) {
                    setRetaining(retaining);
                }
            }
        }
    }

    void saveNonConfig() {
        List list;
        List list2;
        FragmentManagerNonConfig fragmentManagerNonConfig = null;
        if (this.mActive != null) {
            int i = 0;
            list = fragmentManagerNonConfig;
            list2 = list;
            for (int i2 = i; i2 < this.mActive.size(); i2++) {
                Fragment fragment = (Fragment) this.mActive.valueAt(i2);
                if (fragment != null) {
                    Object obj;
                    if (fragment.mRetainInstance) {
                        if (list == null) {
                            list = new ArrayList();
                        }
                        list.add(fragment);
                        fragment.mTargetIndex = fragment.mTarget != null ? fragment.mTarget.mIndex : -1;
                        if (DEBUG) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("retainNonConfig: keeping retained ");
                            stringBuilder.append(fragment);
                            Log.v("FragmentManager", stringBuilder.toString());
                        }
                    }
                    if (fragment.mChildFragmentManager != null) {
                        fragment.mChildFragmentManager.saveNonConfig();
                        obj = fragment.mChildFragmentManager.mSavedNonConfig;
                    } else {
                        obj = fragment.mChildNonConfig;
                    }
                    if (list2 == null && obj != null) {
                        list2 = new ArrayList(this.mActive.size());
                        for (int i3 = i; i3 < i2; i3++) {
                            list2.add(fragmentManagerNonConfig);
                        }
                    }
                    if (list2 != null) {
                        list2.add(obj);
                    }
                }
            }
        } else {
            list = fragmentManagerNonConfig;
            list2 = list;
        }
        if (list == null && list2 == null) {
            this.mSavedNonConfig = fragmentManagerNonConfig;
        } else {
            this.mSavedNonConfig = new FragmentManagerNonConfig(list, list2);
        }
    }

    void saveFragmentViewState(Fragment fragment) {
        if (fragment.mInnerView != null) {
            if (this.mStateArray == null) {
                this.mStateArray = new SparseArray();
            } else {
                this.mStateArray.clear();
            }
            fragment.mInnerView.saveHierarchyState(this.mStateArray);
            if (this.mStateArray.size() > 0) {
                fragment.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }

    Bundle saveFragmentBasicState(Fragment fragment) {
        Bundle bundle;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        dispatchOnFragmentSaveInstanceState(fragment, this.mStateBundle, false);
        Bundle bundle2 = null;
        if (this.mStateBundle.isEmpty()) {
            bundle = bundle2;
        } else {
            bundle = this.mStateBundle;
            this.mStateBundle = bundle2;
        }
        if (fragment.mView != null) {
            saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray("android:view_state", fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean("android:user_visible_hint", fragment.mUserVisibleHint);
        }
        return bundle;
    }

    Parcelable saveAllState() {
        forcePostponedTransactions();
        endAnimatingAwayFragments();
        execPendingActions();
        boolean z = true;
        this.mStateSaved = z;
        BackStackState[] backStackStateArr = null;
        this.mSavedNonConfig = backStackStateArr;
        if (this.mActive == null || this.mActive.size() <= 0) {
            return backStackStateArr;
        }
        int size = this.mActive.size();
        FragmentState[] fragmentStateArr = new FragmentState[size];
        int i = 0;
        int i2 = i;
        int i3 = i2;
        while (i2 < size) {
            Fragment fragment = (Fragment) this.mActive.valueAt(i2);
            if (fragment != null) {
                StringBuilder stringBuilder;
                if (fragment.mIndex < 0) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Failure saving state: active ");
                    stringBuilder2.append(fragment);
                    stringBuilder2.append(" has cleared index: ");
                    stringBuilder2.append(fragment.mIndex);
                    throwException(new IllegalStateException(stringBuilder2.toString()));
                }
                FragmentState fragmentState = new FragmentState(fragment);
                fragmentStateArr[i2] = fragmentState;
                if (fragment.mState <= 0 || fragmentState.mSavedFragmentState != null) {
                    fragmentState.mSavedFragmentState = fragment.mSavedFragmentState;
                } else {
                    fragmentState.mSavedFragmentState = saveFragmentBasicState(fragment);
                    if (fragment.mTarget != null) {
                        if (fragment.mTarget.mIndex < 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Failure saving state: ");
                            stringBuilder.append(fragment);
                            stringBuilder.append(" has target not in fragment manager: ");
                            stringBuilder.append(fragment.mTarget);
                            throwException(new IllegalStateException(stringBuilder.toString()));
                        }
                        if (fragmentState.mSavedFragmentState == null) {
                            fragmentState.mSavedFragmentState = new Bundle();
                        }
                        putFragment(fragmentState.mSavedFragmentState, "android:target_state", fragment.mTarget);
                        if (fragment.mTargetRequestCode != 0) {
                            fragmentState.mSavedFragmentState.putInt("android:target_req_state", fragment.mTargetRequestCode);
                        }
                    }
                }
                if (DEBUG) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Saved state of ");
                    stringBuilder.append(fragment);
                    stringBuilder.append(": ");
                    stringBuilder.append(fragmentState.mSavedFragmentState);
                    Log.v("FragmentManager", stringBuilder.toString());
                }
                i3 = z;
            }
            i2++;
        }
        if (i3 == 0) {
            if (DEBUG) {
                Log.v("FragmentManager", "saveAllState: no fragments!");
            }
            return backStackStateArr;
        }
        int[] iArr;
        int size2 = this.mAdded.size();
        if (size2 > 0) {
            iArr = new int[size2];
            for (i2 = i; i2 < size2; i2++) {
                StringBuilder stringBuilder3;
                iArr[i2] = ((Fragment) this.mAdded.get(i2)).mIndex;
                if (iArr[i2] < 0) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("Failure saving state: active ");
                    stringBuilder3.append(this.mAdded.get(i2));
                    stringBuilder3.append(" has cleared index: ");
                    stringBuilder3.append(iArr[i2]);
                    throwException(new IllegalStateException(stringBuilder3.toString()));
                }
                if (DEBUG) {
                    stringBuilder3 = new StringBuilder();
                    stringBuilder3.append("saveAllState: adding fragment #");
                    stringBuilder3.append(i2);
                    stringBuilder3.append(": ");
                    stringBuilder3.append(this.mAdded.get(i2));
                    Log.v("FragmentManager", stringBuilder3.toString());
                }
            }
        } else {
            iArr = backStackStateArr;
        }
        if (this.mBackStack != null) {
            size2 = this.mBackStack.size();
            if (size2 > 0) {
                backStackStateArr = new BackStackState[size2];
                while (i < size2) {
                    backStackStateArr[i] = new BackStackState((BackStackRecord) this.mBackStack.get(i));
                    if (DEBUG) {
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append("saveAllState: adding back stack #");
                        stringBuilder4.append(i);
                        stringBuilder4.append(": ");
                        stringBuilder4.append(this.mBackStack.get(i));
                        Log.v("FragmentManager", stringBuilder4.toString());
                    }
                    i++;
                }
            }
        }
        Parcelable fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = fragmentStateArr;
        fragmentManagerState.mAdded = iArr;
        fragmentManagerState.mBackStack = backStackStateArr;
        if (this.mPrimaryNav != null) {
            fragmentManagerState.mPrimaryNavActiveIndex = this.mPrimaryNav.mIndex;
        }
        fragmentManagerState.mNextFragmentIndex = this.mNextFragmentIndex;
        saveNonConfig();
        return fragmentManagerState;
    }

    void restoreAllState(Parcelable parcelable, FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (parcelable != null) {
            FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
            if (fragmentManagerState.mActive != null) {
                List childNonConfigs;
                int i;
                List list = null;
                boolean z = false;
                if (fragmentManagerNonConfig != null) {
                    List fragments = fragmentManagerNonConfig.getFragments();
                    childNonConfigs = fragmentManagerNonConfig.getChildNonConfigs();
                    int size = fragments != null ? fragments.size() : z;
                    for (int i2 = z; i2 < size; i2++) {
                        Fragment fragment = (Fragment) fragments.get(i2);
                        if (DEBUG) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("restoreAllState: re-attaching retained ");
                            stringBuilder.append(fragment);
                            Log.v("FragmentManager", stringBuilder.toString());
                        }
                        int i3 = z;
                        while (i3 < fragmentManagerState.mActive.length && fragmentManagerState.mActive[i3].mIndex != fragment.mIndex) {
                            i3++;
                        }
                        if (i3 == fragmentManagerState.mActive.length) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append("Could not find active fragment with index ");
                            stringBuilder2.append(fragment.mIndex);
                            throwException(new IllegalStateException(stringBuilder2.toString()));
                        }
                        FragmentState fragmentState = fragmentManagerState.mActive[i3];
                        fragmentState.mInstance = fragment;
                        fragment.mSavedViewState = list;
                        fragment.mBackStackNesting = z;
                        fragment.mInLayout = z;
                        fragment.mAdded = z;
                        fragment.mTarget = list;
                        if (fragmentState.mSavedFragmentState != null) {
                            fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                            fragment.mSavedViewState = fragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                            fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
                        }
                    }
                } else {
                    childNonConfigs = list;
                }
                this.mActive = new SparseArray(fragmentManagerState.mActive.length);
                int i4 = z;
                while (i4 < fragmentManagerState.mActive.length) {
                    FragmentState fragmentState2 = fragmentManagerState.mActive[i4];
                    if (fragmentState2 != null) {
                        FragmentManagerNonConfig fragmentManagerNonConfig2 = (childNonConfigs == null || i4 >= childNonConfigs.size()) ? list : (FragmentManagerNonConfig) childNonConfigs.get(i4);
                        Fragment instantiate = fragmentState2.instantiate(this.mHost, this.mContainer, this.mParent, fragmentManagerNonConfig2);
                        if (DEBUG) {
                            StringBuilder stringBuilder3 = new StringBuilder();
                            stringBuilder3.append("restoreAllState: active #");
                            stringBuilder3.append(i4);
                            stringBuilder3.append(": ");
                            stringBuilder3.append(instantiate);
                            Log.v("FragmentManager", stringBuilder3.toString());
                        }
                        this.mActive.put(instantiate.mIndex, instantiate);
                        fragmentState2.mInstance = list;
                    }
                    i4++;
                }
                if (fragmentManagerNonConfig != null) {
                    List fragments2 = fragmentManagerNonConfig.getFragments();
                    i4 = fragments2 != null ? fragments2.size() : z;
                    for (int i5 = z; i5 < i4; i5++) {
                        Fragment fragment2 = (Fragment) fragments2.get(i5);
                        if (fragment2.mTargetIndex >= 0) {
                            fragment2.mTarget = (Fragment) this.mActive.get(fragment2.mTargetIndex);
                            if (fragment2.mTarget == null) {
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("Re-attaching retained fragment ");
                                stringBuilder4.append(fragment2);
                                stringBuilder4.append(" target no longer exists: ");
                                stringBuilder4.append(fragment2.mTargetIndex);
                                Log.w("FragmentManager", stringBuilder4.toString());
                            }
                        }
                    }
                }
                this.mAdded.clear();
                if (fragmentManagerState.mAdded != null) {
                    for (i = z; i < fragmentManagerState.mAdded.length; i++) {
                        StringBuilder stringBuilder5;
                        Fragment fragment3 = (Fragment) this.mActive.get(fragmentManagerState.mAdded[i]);
                        if (fragment3 == null) {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append("No instantiated fragment for index #");
                            stringBuilder5.append(fragmentManagerState.mAdded[i]);
                            throwException(new IllegalStateException(stringBuilder5.toString()));
                        }
                        fragment3.mAdded = true;
                        if (DEBUG) {
                            stringBuilder5 = new StringBuilder();
                            stringBuilder5.append("restoreAllState: added #");
                            stringBuilder5.append(i);
                            stringBuilder5.append(": ");
                            stringBuilder5.append(fragment3);
                            Log.v("FragmentManager", stringBuilder5.toString());
                        }
                        if (this.mAdded.contains(fragment3)) {
                            throw new IllegalStateException("Already added!");
                        }
                        synchronized (this.mAdded) {
                            this.mAdded.add(fragment3);
                        }
                    }
                }
                if (fragmentManagerState.mBackStack != null) {
                    this.mBackStack = new ArrayList(fragmentManagerState.mBackStack.length);
                    for (i = z; i < fragmentManagerState.mBackStack.length; i++) {
                        BackStackRecord instantiate2 = fragmentManagerState.mBackStack[i].instantiate(this);
                        if (DEBUG) {
                            StringBuilder stringBuilder6 = new StringBuilder();
                            stringBuilder6.append("restoreAllState: back stack #");
                            stringBuilder6.append(i);
                            stringBuilder6.append(" (index ");
                            stringBuilder6.append(instantiate2.mIndex);
                            stringBuilder6.append("): ");
                            stringBuilder6.append(instantiate2);
                            Log.v("FragmentManager", stringBuilder6.toString());
                            PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
                            instantiate2.dump("  ", printWriter, z);
                            printWriter.close();
                        }
                        this.mBackStack.add(instantiate2);
                        if (instantiate2.mIndex >= 0) {
                            setBackStackIndex(instantiate2.mIndex, instantiate2);
                        }
                    }
                } else {
                    this.mBackStack = list;
                }
                if (fragmentManagerState.mPrimaryNavActiveIndex >= 0) {
                    this.mPrimaryNav = (Fragment) this.mActive.get(fragmentManagerState.mPrimaryNavActiveIndex);
                }
                this.mNextFragmentIndex = fragmentManagerState.mNextFragmentIndex;
            }
        }
    }

    private void burpActive() {
        if (this.mActive != null) {
            for (int size = this.mActive.size() - 1; size >= 0; size--) {
                if (this.mActive.valueAt(size) == null) {
                    this.mActive.delete(this.mActive.keyAt(size));
                }
            }
        }
    }

    public void attachController(FragmentHostCallback fragmentHostCallback, FragmentContainer fragmentContainer, Fragment fragment) {
        if (this.mHost != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mHost = fragmentHostCallback;
        this.mContainer = fragmentContainer;
        this.mParent = fragment;
    }

    public void noteStateNotSaved() {
        this.mSavedNonConfig = null;
        int i = 0;
        this.mStateSaved = i;
        int size = this.mAdded.size();
        while (i < size) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null) {
                fragment.noteStateNotSaved();
            }
            i++;
        }
    }

    public void dispatchCreate() {
        this.mStateSaved = false;
        dispatchStateChange(1);
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        dispatchStateChange(2);
    }

    public void dispatchStart() {
        this.mStateSaved = false;
        dispatchStateChange(4);
    }

    public void dispatchResume() {
        this.mStateSaved = false;
        dispatchStateChange(5);
    }

    public void dispatchPause() {
        dispatchStateChange(4);
    }

    public void dispatchStop() {
        this.mStateSaved = true;
        dispatchStateChange(3);
    }

    public void dispatchReallyStop() {
        dispatchStateChange(2);
    }

    public void dispatchDestroyView() {
        dispatchStateChange(1);
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        dispatchStateChange(0);
        Object obj = null;
        this.mHost = obj;
        this.mContainer = obj;
        this.mParent = obj;
    }

    private void dispatchStateChange(int i) {
        boolean z = false;
        try {
            this.mExecutingActions = true;
            moveToState(i, z);
            execPendingActions();
        } finally {
            this.mExecutingActions = z;
        }
    }

    public void dispatchMultiWindowModeChanged(boolean z) {
        for (int size = this.mAdded.size() - 1; size >= 0; size--) {
            Fragment fragment = (Fragment) this.mAdded.get(size);
            if (fragment != null) {
                fragment.performMultiWindowModeChanged(z);
            }
        }
    }

    public void dispatchPictureInPictureModeChanged(boolean z) {
        for (int size = this.mAdded.size() - 1; size >= 0; size--) {
            Fragment fragment = (Fragment) this.mAdded.get(size);
            if (fragment != null) {
                fragment.performPictureInPictureModeChanged(z);
            }
        }
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        for (int i = 0; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null) {
                fragment.performConfigurationChanged(configuration);
            }
        }
    }

    public void dispatchLowMemory() {
        for (int i = 0; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null) {
                fragment.performLowMemory();
            }
        }
    }

    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        int i = 0;
        boolean z = i;
        ArrayList arrayList = null;
        for (int i2 = z; i2 < this.mAdded.size(); i2++) {
            Fragment fragment = (Fragment) this.mAdded.get(i2);
            if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(fragment);
                z = true;
            }
        }
        if (this.mCreatedMenus != null) {
            while (i < this.mCreatedMenus.size()) {
                Fragment fragment2 = (Fragment) this.mCreatedMenus.get(i);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
                i++;
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        int i = 0;
        boolean z = i;
        while (i < this.mAdded.size()) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                z = true;
            }
            i++;
        }
        return z;
    }

    public boolean dispatchOptionsItemSelected(MenuItem menuItem) {
        boolean z = false;
        for (int i = z; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                return true;
            }
        }
        return z;
    }

    public boolean dispatchContextItemSelected(MenuItem menuItem) {
        boolean z = false;
        for (int i = z; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                return true;
            }
        }
        return z;
    }

    public void dispatchOptionsMenuClosed(Menu menu) {
        for (int i = 0; i < this.mAdded.size(); i++) {
            Fragment fragment = (Fragment) this.mAdded.get(i);
            if (fragment != null) {
                fragment.performOptionsMenuClosed(menu);
            }
        }
    }

    public void setPrimaryNavigationFragment(Fragment fragment) {
        if (fragment == null || (this.mActive.get(fragment.mIndex) == fragment && (fragment.mHost == null || fragment.getFragmentManager() == this))) {
            this.mPrimaryNav = fragment;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fragment ");
        stringBuilder.append(fragment);
        stringBuilder.append(" is not an active fragment of FragmentManager ");
        stringBuilder.append(this);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public Fragment getPrimaryNavigationFragment() {
        return this.mPrimaryNav;
    }

    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks, boolean z) {
        this.mLifecycleCallbacks.add(new Pair(fragmentLifecycleCallbacks, Boolean.valueOf(z)));
    }

    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        synchronized (this.mLifecycleCallbacks) {
            int size = this.mLifecycleCallbacks.size();
            for (int i = 0; i < size; i++) {
                if (((Pair) this.mLifecycleCallbacks.get(i)).first == fragmentLifecycleCallbacks) {
                    this.mLifecycleCallbacks.remove(i);
                    break;
                }
            }
        }
    }

    void dispatchOnFragmentPreAttached(Fragment fragment, Context context, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentPreAttached(fragment, context, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentPreAttached(this, fragment, context);
            }
        }
    }

    void dispatchOnFragmentAttached(Fragment fragment, Context context, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentAttached(fragment, context, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentAttached(this, fragment, context);
            }
        }
    }

    void dispatchOnFragmentPreCreated(Fragment fragment, Bundle bundle, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentPreCreated(fragment, bundle, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentPreCreated(this, fragment, bundle);
            }
        }
    }

    void dispatchOnFragmentCreated(Fragment fragment, Bundle bundle, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentCreated(fragment, bundle, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentCreated(this, fragment, bundle);
            }
        }
    }

    void dispatchOnFragmentActivityCreated(Fragment fragment, Bundle bundle, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentActivityCreated(fragment, bundle, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentActivityCreated(this, fragment, bundle);
            }
        }
    }

    void dispatchOnFragmentViewCreated(Fragment fragment, View view, Bundle bundle, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentViewCreated(fragment, view, bundle, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentViewCreated(this, fragment, view, bundle);
            }
        }
    }

    void dispatchOnFragmentStarted(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentStarted(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentStarted(this, fragment);
            }
        }
    }

    void dispatchOnFragmentResumed(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentResumed(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentResumed(this, fragment);
            }
        }
    }

    void dispatchOnFragmentPaused(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentPaused(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentPaused(this, fragment);
            }
        }
    }

    void dispatchOnFragmentStopped(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentStopped(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentStopped(this, fragment);
            }
        }
    }

    void dispatchOnFragmentSaveInstanceState(Fragment fragment, Bundle bundle, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentSaveInstanceState(fragment, bundle, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentSaveInstanceState(this, fragment, bundle);
            }
        }
    }

    void dispatchOnFragmentViewDestroyed(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentViewDestroyed(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentViewDestroyed(this, fragment);
            }
        }
    }

    void dispatchOnFragmentDestroyed(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentDestroyed(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentDestroyed(this, fragment);
            }
        }
    }

    void dispatchOnFragmentDetached(Fragment fragment, boolean z) {
        if (this.mParent != null) {
            FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl) fragmentManager).dispatchOnFragmentDetached(fragment, true);
            }
        }
        Iterator it = this.mLifecycleCallbacks.iterator();
        while (it.hasNext()) {
            Pair pair = (Pair) it.next();
            if (!z || ((Boolean) pair.second).booleanValue()) {
                ((FragmentLifecycleCallbacks) pair.first).onFragmentDetached(this, fragment);
            }
        }
    }

    public View onCreateView(View view, String str, Context context, AttributeSet attributeSet) {
        FragmentManagerImpl fragmentManagerImpl = this;
        Context context2 = context;
        AttributeSet attributeSet2 = attributeSet;
        Fragment fragment = null;
        if (!"fragment".equals(str)) {
            return fragment;
        }
        String attributeValue = attributeSet2.getAttributeValue(fragment, "class");
        TypedArray obtainStyledAttributes = context2.obtainStyledAttributes(attributeSet2, FragmentTag.Fragment);
        int i = 0;
        if (attributeValue == null) {
            attributeValue = obtainStyledAttributes.getString(i);
        }
        String str2 = attributeValue;
        int i2 = -1;
        boolean z = true;
        int resourceId = obtainStyledAttributes.getResourceId(z, i2);
        String string = obtainStyledAttributes.getString(2);
        obtainStyledAttributes.recycle();
        if (!Fragment.isSupportFragmentClass(fragmentManagerImpl.mHost.getContext(), str2)) {
            return fragment;
        }
        if (view != null) {
            i = view.getId();
        }
        StringBuilder stringBuilder;
        if (i == i2 && resourceId == i2 && string == null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attributeSet.getPositionDescription());
            stringBuilder.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
            stringBuilder.append(str2);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        Fragment fragment2;
        Fragment findFragmentById = resourceId != i2 ? findFragmentById(resourceId) : fragment;
        if (findFragmentById == null && string != null) {
            findFragmentById = findFragmentByTag(string);
        }
        if (findFragmentById == null && i != i2) {
            findFragmentById = findFragmentById(i);
        }
        if (DEBUG) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("onCreateView: id=0x");
            stringBuilder2.append(Integer.toHexString(resourceId));
            stringBuilder2.append(" fname=");
            stringBuilder2.append(str2);
            stringBuilder2.append(" existing=");
            stringBuilder2.append(findFragmentById);
            Log.v("FragmentManager", stringBuilder2.toString());
        }
        if (findFragmentById == null) {
            Fragment instantiate = fragmentManagerImpl.mContainer.instantiate(context2, str2, fragment);
            instantiate.mFromLayout = z;
            instantiate.mFragmentId = resourceId != 0 ? resourceId : i;
            instantiate.mContainerId = i;
            instantiate.mTag = string;
            instantiate.mInLayout = z;
            instantiate.mFragmentManager = fragmentManagerImpl;
            instantiate.mHost = fragmentManagerImpl.mHost;
            instantiate.onInflate(fragmentManagerImpl.mHost.getContext(), attributeSet2, instantiate.mSavedFragmentState);
            addFragment(instantiate, z);
            fragment2 = instantiate;
        } else if (findFragmentById.mInLayout) {
            stringBuilder = new StringBuilder();
            stringBuilder.append(attributeSet.getPositionDescription());
            stringBuilder.append(": Duplicate id 0x");
            stringBuilder.append(Integer.toHexString(resourceId));
            stringBuilder.append(", tag ");
            stringBuilder.append(string);
            stringBuilder.append(", or parent id 0x");
            stringBuilder.append(Integer.toHexString(i));
            stringBuilder.append(" with another fragment for ");
            stringBuilder.append(str2);
            throw new IllegalArgumentException(stringBuilder.toString());
        } else {
            findFragmentById.mInLayout = z;
            findFragmentById.mHost = fragmentManagerImpl.mHost;
            if (!findFragmentById.mRetaining) {
                findFragmentById.onInflate(fragmentManagerImpl.mHost.getContext(), attributeSet2, findFragmentById.mSavedFragmentState);
            }
            fragment2 = findFragmentById;
        }
        if (fragmentManagerImpl.mCurState >= z || !fragment2.mFromLayout) {
            moveToState(fragment2);
        } else {
            moveToState(fragment2, 1, 0, 0, false);
        }
        if (fragment2.mView == null) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Fragment ");
            stringBuilder3.append(str2);
            stringBuilder3.append(" did not create a view.");
            throw new IllegalStateException(stringBuilder3.toString());
        }
        if (resourceId != 0) {
            fragment2.mView.setId(resourceId);
        }
        if (fragment2.mView.getTag() == null) {
            fragment2.mView.setTag(string);
        }
        return fragment2.mView;
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        return onCreateView(null, str, context, attributeSet);
    }
}
