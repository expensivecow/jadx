package android.support.v4.app;

import android.os.Build.VERSION;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v4.util.LogWriter;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

final class BackStackRecord extends FragmentTransaction implements BackStackEntry, OpGenerator {
    static final int OP_ADD = 1;
    static final int OP_ATTACH = 7;
    static final int OP_DETACH = 6;
    static final int OP_HIDE = 4;
    static final int OP_NULL = 0;
    static final int OP_REMOVE = 3;
    static final int OP_REPLACE = 2;
    static final int OP_SET_PRIMARY_NAV = 8;
    static final int OP_SHOW = 5;
    static final int OP_UNSET_PRIMARY_NAV = 9;
    static final boolean SUPPORTS_TRANSITIONS = (VERSION.SDK_INT >= 21);
    static final String TAG = "FragmentManager";
    boolean mAddToBackStack;
    boolean mAllowAddToBackStack = true;
    int mBreadCrumbShortTitleRes;
    CharSequence mBreadCrumbShortTitleText;
    int mBreadCrumbTitleRes;
    CharSequence mBreadCrumbTitleText;
    ArrayList<Runnable> mCommitRunnables;
    boolean mCommitted;
    int mEnterAnim;
    int mExitAnim;
    int mIndex = -1;
    final FragmentManagerImpl mManager;
    String mName;
    ArrayList<Op> mOps = new ArrayList();
    int mPopEnterAnim;
    int mPopExitAnim;
    boolean mReorderingAllowed = false;
    ArrayList<String> mSharedElementSourceNames;
    ArrayList<String> mSharedElementTargetNames;
    int mTransition;
    int mTransitionStyle;

    static final class Op {
        int cmd;
        int enterAnim;
        int exitAnim;
        Fragment fragment;
        int popEnterAnim;
        int popExitAnim;

        Op() {
        }

        Op(int i, Fragment fragment) {
            this.cmd = i;
            this.fragment = fragment;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(128);
        stringBuilder.append("BackStackEntry{");
        stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
        if (this.mIndex >= 0) {
            stringBuilder.append(" #");
            stringBuilder.append(this.mIndex);
        }
        if (this.mName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.mName);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        dump(str, printWriter, true);
    }

    public void dump(String str, PrintWriter printWriter, boolean z) {
        if (z) {
            printWriter.print(str);
            printWriter.print("mName=");
            printWriter.print(this.mName);
            printWriter.print(" mIndex=");
            printWriter.print(this.mIndex);
            printWriter.print(" mCommitted=");
            printWriter.println(this.mCommitted);
            if (this.mTransition != 0) {
                printWriter.print(str);
                printWriter.print("mTransition=#");
                printWriter.print(Integer.toHexString(this.mTransition));
                printWriter.print(" mTransitionStyle=#");
                printWriter.println(Integer.toHexString(this.mTransitionStyle));
            }
            if (!(this.mEnterAnim == 0 && this.mExitAnim == 0)) {
                printWriter.print(str);
                printWriter.print("mEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mEnterAnim));
                printWriter.print(" mExitAnim=#");
                printWriter.println(Integer.toHexString(this.mExitAnim));
            }
            if (!(this.mPopEnterAnim == 0 && this.mPopExitAnim == 0)) {
                printWriter.print(str);
                printWriter.print("mPopEnterAnim=#");
                printWriter.print(Integer.toHexString(this.mPopEnterAnim));
                printWriter.print(" mPopExitAnim=#");
                printWriter.println(Integer.toHexString(this.mPopExitAnim));
            }
            if (!(this.mBreadCrumbTitleRes == 0 && this.mBreadCrumbTitleText == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbTitleRes));
                printWriter.print(" mBreadCrumbTitleText=");
                printWriter.println(this.mBreadCrumbTitleText);
            }
            if (!(this.mBreadCrumbShortTitleRes == 0 && this.mBreadCrumbShortTitleText == null)) {
                printWriter.print(str);
                printWriter.print("mBreadCrumbShortTitleRes=#");
                printWriter.print(Integer.toHexString(this.mBreadCrumbShortTitleRes));
                printWriter.print(" mBreadCrumbShortTitleText=");
                printWriter.println(this.mBreadCrumbShortTitleText);
            }
        }
        if (!this.mOps.isEmpty()) {
            printWriter.print(str);
            printWriter.println("Operations:");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(str);
            stringBuilder.append("    ");
            stringBuilder.toString();
            int size = this.mOps.size();
            for (int i = 0; i < size; i++) {
                String str2;
                Op op = (Op) this.mOps.get(i);
                switch (op.cmd) {
                    case 0:
                        str2 = "NULL";
                        break;
                    case 1:
                        str2 = "ADD";
                        break;
                    case 2:
                        str2 = "REPLACE";
                        break;
                    case 3:
                        str2 = "REMOVE";
                        break;
                    case 4:
                        str2 = "HIDE";
                        break;
                    case 5:
                        str2 = "SHOW";
                        break;
                    case 6:
                        str2 = "DETACH";
                        break;
                    case 7:
                        str2 = "ATTACH";
                        break;
                    case 8:
                        str2 = "SET_PRIMARY_NAV";
                        break;
                    case 9:
                        str2 = "UNSET_PRIMARY_NAV";
                        break;
                    default:
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("cmd=");
                        stringBuilder2.append(op.cmd);
                        str2 = stringBuilder2.toString();
                        break;
                }
                printWriter.print(str);
                printWriter.print("  Op #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.print(str2);
                printWriter.print(" ");
                printWriter.println(op.fragment);
                if (z) {
                    if (!(op.enterAnim == 0 && op.exitAnim == 0)) {
                        printWriter.print(str);
                        printWriter.print("enterAnim=#");
                        printWriter.print(Integer.toHexString(op.enterAnim));
                        printWriter.print(" exitAnim=#");
                        printWriter.println(Integer.toHexString(op.exitAnim));
                    }
                    if (op.popEnterAnim != 0 || op.popExitAnim != 0) {
                        printWriter.print(str);
                        printWriter.print("popEnterAnim=#");
                        printWriter.print(Integer.toHexString(op.popEnterAnim));
                        printWriter.print(" popExitAnim=#");
                        printWriter.println(Integer.toHexString(op.popExitAnim));
                    }
                }
            }
        }
    }

    public BackStackRecord(FragmentManagerImpl fragmentManagerImpl) {
        this.mManager = fragmentManagerImpl;
    }

    public int getId() {
        return this.mIndex;
    }

    public int getBreadCrumbTitleRes() {
        return this.mBreadCrumbTitleRes;
    }

    public int getBreadCrumbShortTitleRes() {
        return this.mBreadCrumbShortTitleRes;
    }

    public CharSequence getBreadCrumbTitle() {
        if (this.mBreadCrumbTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbTitleRes);
        }
        return this.mBreadCrumbTitleText;
    }

    public CharSequence getBreadCrumbShortTitle() {
        if (this.mBreadCrumbShortTitleRes != 0) {
            return this.mManager.mHost.getContext().getText(this.mBreadCrumbShortTitleRes);
        }
        return this.mBreadCrumbShortTitleText;
    }

    void addOp(Op op) {
        this.mOps.add(op);
        op.enterAnim = this.mEnterAnim;
        op.exitAnim = this.mExitAnim;
        op.popEnterAnim = this.mPopEnterAnim;
        op.popExitAnim = this.mPopExitAnim;
    }

    public FragmentTransaction add(Fragment fragment, String str) {
        doAddOp(0, fragment, str, 1);
        return this;
    }

    public FragmentTransaction add(int i, Fragment fragment) {
        doAddOp(i, fragment, null, 1);
        return this;
    }

    public FragmentTransaction add(int i, Fragment fragment, String str) {
        doAddOp(i, fragment, str, 1);
        return this;
    }

    private void doAddOp(int i, Fragment fragment, String str, int i2) {
        Class cls = fragment.getClass();
        int modifiers = cls.getModifiers();
        if (cls.isAnonymousClass() || !Modifier.isPublic(modifiers) || (cls.isMemberClass() && !Modifier.isStatic(modifiers))) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Fragment ");
            stringBuilder.append(cls.getCanonicalName());
            stringBuilder.append(" must be a public static class to be  properly recreated from");
            stringBuilder.append(" instance state.");
            throw new IllegalStateException(stringBuilder.toString());
        }
        StringBuilder stringBuilder2;
        fragment.mFragmentManager = this.mManager;
        if (str != null) {
            if (fragment.mTag == null || str.equals(fragment.mTag)) {
                fragment.mTag = str;
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Can't change tag of fragment ");
                stringBuilder2.append(fragment);
                stringBuilder2.append(": was ");
                stringBuilder2.append(fragment.mTag);
                stringBuilder2.append(" now ");
                stringBuilder2.append(str);
                throw new IllegalStateException(stringBuilder2.toString());
            }
        }
        if (i != 0) {
            if (i == -1) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Can't add fragment ");
                stringBuilder2.append(fragment);
                stringBuilder2.append(" with tag ");
                stringBuilder2.append(str);
                stringBuilder2.append(" to container view with no id");
                throw new IllegalArgumentException(stringBuilder2.toString());
            } else if (fragment.mFragmentId == 0 || fragment.mFragmentId == i) {
                fragment.mFragmentId = i;
                fragment.mContainerId = i;
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Can't change container ID of fragment ");
                stringBuilder2.append(fragment);
                stringBuilder2.append(": was ");
                stringBuilder2.append(fragment.mFragmentId);
                stringBuilder2.append(" now ");
                stringBuilder2.append(i);
                throw new IllegalStateException(stringBuilder2.toString());
            }
        }
        addOp(new Op(i2, fragment));
    }

    public FragmentTransaction replace(int i, Fragment fragment) {
        return replace(i, fragment, null);
    }

    public FragmentTransaction replace(int i, Fragment fragment, String str) {
        if (i == 0) {
            throw new IllegalArgumentException("Must use non-zero containerViewId");
        }
        doAddOp(i, fragment, str, 2);
        return this;
    }

    public FragmentTransaction remove(Fragment fragment) {
        addOp(new Op(3, fragment));
        return this;
    }

    public FragmentTransaction hide(Fragment fragment) {
        addOp(new Op(4, fragment));
        return this;
    }

    public FragmentTransaction show(Fragment fragment) {
        addOp(new Op(5, fragment));
        return this;
    }

    public FragmentTransaction detach(Fragment fragment) {
        addOp(new Op(6, fragment));
        return this;
    }

    public FragmentTransaction attach(Fragment fragment) {
        addOp(new Op(7, fragment));
        return this;
    }

    public FragmentTransaction setPrimaryNavigationFragment(Fragment fragment) {
        addOp(new Op(8, fragment));
        return this;
    }

    public FragmentTransaction setCustomAnimations(int i, int i2) {
        int i3 = 0;
        return setCustomAnimations(i, i2, i3, i3);
    }

    public FragmentTransaction setCustomAnimations(int i, int i2, int i3, int i4) {
        this.mEnterAnim = i;
        this.mExitAnim = i2;
        this.mPopEnterAnim = i3;
        this.mPopExitAnim = i4;
        return this;
    }

    public FragmentTransaction setTransition(int i) {
        this.mTransition = i;
        return this;
    }

    public FragmentTransaction addSharedElement(View view, String str) {
        if (SUPPORTS_TRANSITIONS) {
            String transitionName = ViewCompat.getTransitionName(view);
            if (transitionName == null) {
                throw new IllegalArgumentException("Unique transitionNames are required for all sharedElements");
            }
            StringBuilder stringBuilder;
            if (this.mSharedElementSourceNames == null) {
                this.mSharedElementSourceNames = new ArrayList();
                this.mSharedElementTargetNames = new ArrayList();
            } else if (this.mSharedElementTargetNames.contains(str)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("A shared element with the target name '");
                stringBuilder.append(str);
                stringBuilder.append("' has already been added to the transaction.");
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (this.mSharedElementSourceNames.contains(transitionName)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("A shared element with the source name '");
                stringBuilder.append(transitionName);
                stringBuilder.append(" has already been added to the transaction.");
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.mSharedElementSourceNames.add(transitionName);
            this.mSharedElementTargetNames.add(str);
        }
        return this;
    }

    public FragmentTransaction setTransitionStyle(int i) {
        this.mTransitionStyle = i;
        return this;
    }

    public FragmentTransaction addToBackStack(String str) {
        if (this.mAllowAddToBackStack) {
            this.mAddToBackStack = true;
            this.mName = str;
            return this;
        }
        throw new IllegalStateException("This FragmentTransaction is not allowed to be added to the back stack.");
    }

    public boolean isAddToBackStackAllowed() {
        return this.mAllowAddToBackStack;
    }

    public FragmentTransaction disallowAddToBackStack() {
        if (this.mAddToBackStack) {
            throw new IllegalStateException("This transaction is already being added to the back stack");
        }
        this.mAllowAddToBackStack = false;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(int i) {
        this.mBreadCrumbTitleRes = i;
        this.mBreadCrumbTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbTitle(CharSequence charSequence) {
        this.mBreadCrumbTitleRes = 0;
        this.mBreadCrumbTitleText = charSequence;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(int i) {
        this.mBreadCrumbShortTitleRes = i;
        this.mBreadCrumbShortTitleText = null;
        return this;
    }

    public FragmentTransaction setBreadCrumbShortTitle(CharSequence charSequence) {
        this.mBreadCrumbShortTitleRes = 0;
        this.mBreadCrumbShortTitleText = charSequence;
        return this;
    }

    void bumpBackStackNesting(int i) {
        if (this.mAddToBackStack) {
            if (FragmentManagerImpl.DEBUG) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Bump nesting in ");
                stringBuilder.append(this);
                stringBuilder.append(" by ");
                stringBuilder.append(i);
                Log.v("FragmentManager", stringBuilder.toString());
            }
            int size = this.mOps.size();
            for (int i2 = 0; i2 < size; i2++) {
                Op op = (Op) this.mOps.get(i2);
                if (op.fragment != null) {
                    Fragment fragment = op.fragment;
                    fragment.mBackStackNesting += i;
                    if (FragmentManagerImpl.DEBUG) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("Bump nesting of ");
                        stringBuilder2.append(op.fragment);
                        stringBuilder2.append(" to ");
                        stringBuilder2.append(op.fragment.mBackStackNesting);
                        Log.v("FragmentManager", stringBuilder2.toString());
                    }
                }
            }
        }
    }

    public FragmentTransaction runOnCommit(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("runnable cannot be null");
        }
        disallowAddToBackStack();
        if (this.mCommitRunnables == null) {
            this.mCommitRunnables = new ArrayList();
        }
        this.mCommitRunnables.add(runnable);
        return this;
    }

    public void runOnCommitRunnables() {
        if (this.mCommitRunnables != null) {
            int size = this.mCommitRunnables.size();
            for (int i = 0; i < size; i++) {
                ((Runnable) this.mCommitRunnables.get(i)).run();
            }
            this.mCommitRunnables = null;
        }
    }

    public int commit() {
        return commitInternal(false);
    }

    public int commitAllowingStateLoss() {
        return commitInternal(true);
    }

    public void commitNow() {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, false);
    }

    public void commitNowAllowingStateLoss() {
        disallowAddToBackStack();
        this.mManager.execSingleAction(this, true);
    }

    public FragmentTransaction setReorderingAllowed(boolean z) {
        this.mReorderingAllowed = z;
        return this;
    }

    public FragmentTransaction setAllowOptimization(boolean z) {
        return setReorderingAllowed(z);
    }

    int commitInternal(boolean z) {
        if (this.mCommitted) {
            throw new IllegalStateException("commit already called");
        }
        if (FragmentManagerImpl.DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Commit: ");
            stringBuilder.append(this);
            Log.v("FragmentManager", stringBuilder.toString());
            PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
            FileDescriptor fileDescriptor = null;
            dump("  ", fileDescriptor, printWriter, fileDescriptor);
            printWriter.close();
        }
        this.mCommitted = true;
        if (this.mAddToBackStack) {
            this.mIndex = this.mManager.allocBackStackIndex(this);
        } else {
            this.mIndex = -1;
        }
        this.mManager.enqueueAction(this, z);
        return this.mIndex;
    }

    public boolean generateOps(ArrayList<BackStackRecord> arrayList, ArrayList<Boolean> arrayList2) {
        if (FragmentManagerImpl.DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Run: ");
            stringBuilder.append(this);
            Log.v("FragmentManager", stringBuilder.toString());
        }
        arrayList.add(this);
        arrayList2.add(Boolean.valueOf(false));
        if (this.mAddToBackStack) {
            this.mManager.addBackStackState(this);
        }
        return true;
    }

    boolean interactsWith(int i) {
        int size = this.mOps.size();
        boolean z = false;
        for (int i2 = z; i2 < size; i2++) {
            Op op = (Op) this.mOps.get(i2);
            int i3 = op.fragment != null ? op.fragment.mContainerId : z;
            if (i3 != 0 && i3 == i) {
                return true;
            }
        }
        return z;
    }

    boolean interactsWith(ArrayList<BackStackRecord> arrayList, int i, int i2) {
        boolean z = false;
        if (i2 == i) {
            return z;
        }
        int size = this.mOps.size();
        int i3 = -1;
        for (int i4 = z; i4 < size; i4++) {
            Op op = (Op) this.mOps.get(i4);
            int i5 = op.fragment != null ? op.fragment.mContainerId : z;
            if (!(i5 == 0 || i5 == i3)) {
                for (i3 = i; i3 < i2; i3++) {
                    BackStackRecord backStackRecord = (BackStackRecord) arrayList.get(i3);
                    int size2 = backStackRecord.mOps.size();
                    for (int i6 = z; i6 < size2; i6++) {
                        Op op2 = (Op) backStackRecord.mOps.get(i6);
                        if ((op2.fragment != null ? op2.fragment.mContainerId : z) == i5) {
                            return true;
                        }
                    }
                }
                i3 = i5;
            }
        }
        return z;
    }

    void executeOps() {
        int size = this.mOps.size();
        boolean z = false;
        int i = z;
        while (true) {
            boolean z2 = true;
            if (i < size) {
                Op op = (Op) this.mOps.get(i);
                Fragment fragment = op.fragment;
                if (fragment != null) {
                    fragment.setNextTransition(this.mTransition, this.mTransitionStyle);
                }
                boolean z3 = op.cmd;
                if (z3 != z2) {
                    switch (z3) {
                        case true:
                            fragment.setNextAnim(op.exitAnim);
                            this.mManager.removeFragment(fragment);
                            break;
                        case true:
                            fragment.setNextAnim(op.exitAnim);
                            this.mManager.hideFragment(fragment);
                            break;
                        case true:
                            fragment.setNextAnim(op.enterAnim);
                            this.mManager.showFragment(fragment);
                            break;
                        case true:
                            fragment.setNextAnim(op.exitAnim);
                            this.mManager.detachFragment(fragment);
                            break;
                        case true:
                            fragment.setNextAnim(op.enterAnim);
                            this.mManager.attachFragment(fragment);
                            break;
                        case true:
                            this.mManager.setPrimaryNavigationFragment(fragment);
                            break;
                        case true:
                            this.mManager.setPrimaryNavigationFragment(null);
                            break;
                        default:
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Unknown cmd: ");
                            stringBuilder.append(op.cmd);
                            throw new IllegalArgumentException(stringBuilder.toString());
                    }
                }
                fragment.setNextAnim(op.enterAnim);
                this.mManager.addFragment(fragment, z);
                if (!(this.mReorderingAllowed || op.cmd == z2 || fragment == null)) {
                    this.mManager.moveFragmentToExpectedState(fragment);
                }
                i++;
            } else if (!this.mReorderingAllowed) {
                this.mManager.moveToState(this.mManager.mCurState, z2);
                return;
            } else {
                return;
            }
        }
    }

    void executePopOps(boolean z) {
        boolean z2 = true;
        for (int size = this.mOps.size() - z2; size >= 0; size--) {
            Op op = (Op) this.mOps.get(size);
            Fragment fragment = op.fragment;
            if (fragment != null) {
                fragment.setNextTransition(FragmentManagerImpl.reverseTransit(this.mTransition), this.mTransitionStyle);
            }
            boolean z3 = op.cmd;
            if (z3 != z2) {
                switch (z3) {
                    case true:
                        fragment.setNextAnim(op.popEnterAnim);
                        this.mManager.addFragment(fragment, false);
                        break;
                    case true:
                        fragment.setNextAnim(op.popEnterAnim);
                        this.mManager.showFragment(fragment);
                        break;
                    case true:
                        fragment.setNextAnim(op.popExitAnim);
                        this.mManager.hideFragment(fragment);
                        break;
                    case true:
                        fragment.setNextAnim(op.popEnterAnim);
                        this.mManager.attachFragment(fragment);
                        break;
                    case true:
                        fragment.setNextAnim(op.popExitAnim);
                        this.mManager.detachFragment(fragment);
                        break;
                    case true:
                        this.mManager.setPrimaryNavigationFragment(null);
                        break;
                    case true:
                        this.mManager.setPrimaryNavigationFragment(fragment);
                        break;
                    default:
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Unknown cmd: ");
                        stringBuilder.append(op.cmd);
                        throw new IllegalArgumentException(stringBuilder.toString());
                }
            }
            fragment.setNextAnim(op.popExitAnim);
            this.mManager.removeFragment(fragment);
            if (!(this.mReorderingAllowed || op.cmd == 3 || fragment == null)) {
                this.mManager.moveFragmentToExpectedState(fragment);
            }
        }
        if (!this.mReorderingAllowed && z) {
            this.mManager.moveToState(this.mManager.mCurState, z2);
        }
    }

    Fragment expandOps(ArrayList<Fragment> arrayList, Fragment fragment) {
        int i = 0;
        Fragment fragment2 = fragment;
        int i2 = i;
        while (i2 < this.mOps.size()) {
            Op op = (Op) this.mOps.get(i2);
            Fragment fragment3 = null;
            int i3 = 9;
            int i4 = 1;
            switch (op.cmd) {
                case 1:
                case 7:
                    arrayList.add(op.fragment);
                    break;
                case 2:
                    Fragment fragment4 = op.fragment;
                    int i5 = fragment4.mContainerId;
                    Fragment fragment5 = fragment2;
                    int i6 = i2;
                    i2 = i;
                    for (int size = arrayList.size() - i4; size >= 0; size--) {
                        Fragment fragment6 = (Fragment) arrayList.get(size);
                        if (fragment6.mContainerId == i5) {
                            if (fragment6 == fragment4) {
                                i2 = i4;
                            } else {
                                if (fragment6 == fragment5) {
                                    this.mOps.add(i6, new Op(i3, fragment6));
                                    i6++;
                                    fragment5 = fragment3;
                                }
                                Op op2 = new Op(3, fragment6);
                                op2.enterAnim = op.enterAnim;
                                op2.popEnterAnim = op.popEnterAnim;
                                op2.exitAnim = op.exitAnim;
                                op2.popExitAnim = op.popExitAnim;
                                this.mOps.add(i6, op2);
                                arrayList.remove(fragment6);
                                i6 += i4;
                            }
                        }
                    }
                    if (i2 != 0) {
                        this.mOps.remove(i6);
                        i6--;
                    } else {
                        op.cmd = i4;
                        arrayList.add(fragment4);
                    }
                    i2 = i6;
                    fragment2 = fragment5;
                    break;
                case 3:
                case 6:
                    arrayList.remove(op.fragment);
                    if (op.fragment != fragment2) {
                        break;
                    }
                    this.mOps.add(i2, new Op(i3, op.fragment));
                    i2++;
                    fragment2 = fragment3;
                    break;
                case 8:
                    this.mOps.add(i2, new Op(i3, fragment2));
                    i2++;
                    fragment2 = op.fragment;
                    break;
                default:
                    break;
            }
            i2 += i4;
        }
        return fragment2;
    }

    Fragment trackAddedFragmentsInPop(ArrayList<Fragment> arrayList, Fragment fragment) {
        for (int i = 0; i < this.mOps.size(); i++) {
            Op op = (Op) this.mOps.get(i);
            int i2 = op.cmd;
            if (i2 != 1) {
                if (i2 != 3) {
                    switch (i2) {
                        case 6:
                            break;
                        case 7:
                            break;
                        case 8:
                            fragment = null;
                            break;
                        case 9:
                            fragment = op.fragment;
                            break;
                        default:
                            break;
                    }
                }
                arrayList.add(op.fragment);
            }
            arrayList.remove(op.fragment);
        }
        return fragment;
    }

    boolean isPostponed() {
        boolean z = false;
        for (int i = z; i < this.mOps.size(); i++) {
            if (isFragmentPostponed((Op) this.mOps.get(i))) {
                return true;
            }
        }
        return z;
    }

    void setOnStartPostponedListener(OnStartEnterTransitionListener onStartEnterTransitionListener) {
        for (int i = 0; i < this.mOps.size(); i++) {
            Op op = (Op) this.mOps.get(i);
            if (isFragmentPostponed(op)) {
                op.fragment.setOnStartEnterTransitionListener(onStartEnterTransitionListener);
            }
        }
    }

    private static boolean isFragmentPostponed(Op op) {
        Fragment fragment = op.fragment;
        return (fragment == null || !fragment.mAdded || fragment.mView == null || fragment.mDetached || fragment.mHidden || !fragment.isPostponed()) ? false : true;
    }

    public String getName() {
        return this.mName;
    }

    public int getTransition() {
        return this.mTransition;
    }

    public int getTransitionStyle() {
        return this.mTransitionStyle;
    }

    public boolean isEmpty() {
        return this.mOps.isEmpty();
    }
}
