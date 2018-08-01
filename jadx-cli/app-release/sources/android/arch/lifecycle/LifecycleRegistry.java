package android.arch.lifecycle;

import android.arch.core.internal.FastSafeIterableMap;
import android.arch.lifecycle.Lifecycle.Event;
import android.arch.lifecycle.Lifecycle.State;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

public class LifecycleRegistry extends Lifecycle {
    private int mAddingObserverCounter;
    private boolean mHandlingEvent;
    private final LifecycleOwner mLifecycleOwner;
    private boolean mNewEventOccurred;
    private FastSafeIterableMap<LifecycleObserver, ObserverWithState> mObserverMap = new FastSafeIterableMap();
    private ArrayList<State> mParentStates;
    private State mState;

    /* renamed from: android.arch.lifecycle.LifecycleRegistry$1 */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$arch$lifecycle$Lifecycle$Event = new int[Event.values().length];
        static final /* synthetic */ int[] $SwitchMap$android$arch$lifecycle$Lifecycle$State = new int[State.values().length];

        static {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.arch.lifecycle.LifecycleRegistry.1.<clinit>():void, dom blocks: []
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
	at jadx.core.ProcessClass.lambda$0(ProcessClass.java:51)
	at java.lang.Iterable.forEach(Iterable.java:75)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
            /*
            r0 = android.arch.lifecycle.Lifecycle.State.values();
            r0 = r0.length;
            r0 = new int[r0];
            $SwitchMap$android$arch$lifecycle$Lifecycle$State = r0;
            r0 = 1;
            r1 = $SwitchMap$android$arch$lifecycle$Lifecycle$State;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = android.arch.lifecycle.Lifecycle.State.INITIALIZED;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = r2.ordinal();	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1[r2] = r0;	 Catch:{ NoSuchFieldError -> 0x0014 }
        L_0x0014:
            r1 = 2;
            r2 = $SwitchMap$android$arch$lifecycle$Lifecycle$State;	 Catch:{ NoSuchFieldError -> 0x001f }
            r3 = android.arch.lifecycle.Lifecycle.State.CREATED;	 Catch:{ NoSuchFieldError -> 0x001f }
            r3 = r3.ordinal();	 Catch:{ NoSuchFieldError -> 0x001f }
            r2[r3] = r1;	 Catch:{ NoSuchFieldError -> 0x001f }
        L_0x001f:
            r2 = 3;
            r3 = $SwitchMap$android$arch$lifecycle$Lifecycle$State;	 Catch:{ NoSuchFieldError -> 0x002a }
            r4 = android.arch.lifecycle.Lifecycle.State.STARTED;	 Catch:{ NoSuchFieldError -> 0x002a }
            r4 = r4.ordinal();	 Catch:{ NoSuchFieldError -> 0x002a }
            r3[r4] = r2;	 Catch:{ NoSuchFieldError -> 0x002a }
        L_0x002a:
            r3 = 4;
            r4 = $SwitchMap$android$arch$lifecycle$Lifecycle$State;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r5 = android.arch.lifecycle.Lifecycle.State.RESUMED;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r5 = r5.ordinal();	 Catch:{ NoSuchFieldError -> 0x0035 }
            r4[r5] = r3;	 Catch:{ NoSuchFieldError -> 0x0035 }
        L_0x0035:
            r4 = 5;
            r5 = $SwitchMap$android$arch$lifecycle$Lifecycle$State;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r6 = android.arch.lifecycle.Lifecycle.State.DESTROYED;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r6 = r6.ordinal();	 Catch:{ NoSuchFieldError -> 0x0040 }
            r5[r6] = r4;	 Catch:{ NoSuchFieldError -> 0x0040 }
        L_0x0040:
            r5 = android.arch.lifecycle.Lifecycle.Event.values();
            r5 = r5.length;
            r5 = new int[r5];
            $SwitchMap$android$arch$lifecycle$Lifecycle$Event = r5;
            r5 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x0053 }
            r6 = android.arch.lifecycle.Lifecycle.Event.ON_CREATE;	 Catch:{ NoSuchFieldError -> 0x0053 }
            r6 = r6.ordinal();	 Catch:{ NoSuchFieldError -> 0x0053 }
            r5[r6] = r0;	 Catch:{ NoSuchFieldError -> 0x0053 }
        L_0x0053:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x005d }
            r5 = android.arch.lifecycle.Lifecycle.Event.ON_STOP;	 Catch:{ NoSuchFieldError -> 0x005d }
            r5 = r5.ordinal();	 Catch:{ NoSuchFieldError -> 0x005d }
            r0[r5] = r1;	 Catch:{ NoSuchFieldError -> 0x005d }
        L_0x005d:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x0067 }
            r1 = android.arch.lifecycle.Lifecycle.Event.ON_START;	 Catch:{ NoSuchFieldError -> 0x0067 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0067 }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0067 }
        L_0x0067:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x0071 }
            r1 = android.arch.lifecycle.Lifecycle.Event.ON_PAUSE;	 Catch:{ NoSuchFieldError -> 0x0071 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0071 }
            r0[r1] = r3;	 Catch:{ NoSuchFieldError -> 0x0071 }
        L_0x0071:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x007b }
            r1 = android.arch.lifecycle.Lifecycle.Event.ON_RESUME;	 Catch:{ NoSuchFieldError -> 0x007b }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x007b }
            r0[r1] = r4;	 Catch:{ NoSuchFieldError -> 0x007b }
        L_0x007b:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x0086 }
            r1 = android.arch.lifecycle.Lifecycle.Event.ON_DESTROY;	 Catch:{ NoSuchFieldError -> 0x0086 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0086 }
            r2 = 6;	 Catch:{ NoSuchFieldError -> 0x0086 }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0086 }
        L_0x0086:
            r0 = $SwitchMap$android$arch$lifecycle$Lifecycle$Event;	 Catch:{ NoSuchFieldError -> 0x0091 }
            r1 = android.arch.lifecycle.Lifecycle.Event.ON_ANY;	 Catch:{ NoSuchFieldError -> 0x0091 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0091 }
            r2 = 7;	 Catch:{ NoSuchFieldError -> 0x0091 }
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0091 }
        L_0x0091:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.arch.lifecycle.LifecycleRegistry.1.<clinit>():void");
        }
    }

    static class ObserverWithState {
        GenericLifecycleObserver mLifecycleObserver;
        State mState;

        ObserverWithState(LifecycleObserver lifecycleObserver, State state) {
            this.mLifecycleObserver = Lifecycling.getCallback(lifecycleObserver);
            this.mState = state;
        }

        void dispatchEvent(LifecycleOwner lifecycleOwner, Event event) {
            State stateAfter = LifecycleRegistry.getStateAfter(event);
            this.mState = LifecycleRegistry.min(this.mState, stateAfter);
            this.mLifecycleObserver.onStateChanged(lifecycleOwner, event);
            this.mState = stateAfter;
        }
    }

    public LifecycleRegistry(@NonNull LifecycleOwner lifecycleOwner) {
        boolean z = false;
        this.mAddingObserverCounter = z;
        this.mHandlingEvent = z;
        this.mNewEventOccurred = z;
        this.mParentStates = new ArrayList();
        this.mLifecycleOwner = lifecycleOwner;
        this.mState = State.INITIALIZED;
    }

    public void markState(State state) {
        this.mState = state;
    }

    public void handleLifecycleEvent(Event event) {
        this.mState = getStateAfter(event);
        boolean z = true;
        if (this.mHandlingEvent || this.mAddingObserverCounter != 0) {
            this.mNewEventOccurred = z;
            return;
        }
        this.mHandlingEvent = z;
        sync();
        this.mHandlingEvent = false;
    }

    private boolean isSynced() {
        boolean z = true;
        if (this.mObserverMap.size() == 0) {
            return z;
        }
        State state = ((ObserverWithState) this.mObserverMap.eldest().getValue()).mState;
        State state2 = ((ObserverWithState) this.mObserverMap.newest().getValue()).mState;
        if (!(state == state2 && this.mState == state2)) {
            z = false;
        }
        return z;
    }

    private State calculateTargetState(LifecycleObserver lifecycleObserver) {
        Entry ceil = this.mObserverMap.ceil(lifecycleObserver);
        State state = null;
        State state2 = ceil != null ? ((ObserverWithState) ceil.getValue()).mState : state;
        if (!this.mParentStates.isEmpty()) {
            state = (State) this.mParentStates.get(this.mParentStates.size() - 1);
        }
        return min(min(this.mState, state2), state);
    }

    public void addObserver(LifecycleObserver lifecycleObserver) {
        ObserverWithState observerWithState = new ObserverWithState(lifecycleObserver, this.mState == State.DESTROYED ? State.DESTROYED : State.INITIALIZED);
        if (((ObserverWithState) this.mObserverMap.putIfAbsent(lifecycleObserver, observerWithState)) == null) {
            int i = 1;
            int i2 = (this.mAddingObserverCounter != 0 || this.mHandlingEvent) ? i : 0;
            Enum calculateTargetState = calculateTargetState(lifecycleObserver);
            this.mAddingObserverCounter += i;
            while (observerWithState.mState.compareTo(calculateTargetState) < 0 && this.mObserverMap.contains(lifecycleObserver)) {
                pushParentState(observerWithState.mState);
                observerWithState.dispatchEvent(this.mLifecycleOwner, upEvent(observerWithState.mState));
                popParentState();
                calculateTargetState = calculateTargetState(lifecycleObserver);
            }
            if (i2 == 0) {
                sync();
            }
            this.mAddingObserverCounter -= i;
        }
    }

    private void popParentState() {
        this.mParentStates.remove(this.mParentStates.size() - 1);
    }

    private void pushParentState(State state) {
        this.mParentStates.add(state);
    }

    public void removeObserver(LifecycleObserver lifecycleObserver) {
        this.mObserverMap.remove(lifecycleObserver);
    }

    public int getObserverCount() {
        return this.mObserverMap.size();
    }

    public State getCurrentState() {
        return this.mState;
    }

    static State getStateAfter(Event event) {
        switch (AnonymousClass1.$SwitchMap$android$arch$lifecycle$Lifecycle$Event[event.ordinal()]) {
            case 1:
            case 2:
                return State.CREATED;
            case 3:
            case 4:
                return State.STARTED;
            case 5:
                return State.RESUMED;
            case 6:
                return State.DESTROYED;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected event value ");
                stringBuilder.append(event);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static Event downEvent(State state) {
        switch (AnonymousClass1.$SwitchMap$android$arch$lifecycle$Lifecycle$State[state.ordinal()]) {
            case 1:
                throw new IllegalArgumentException();
            case 2:
                return Event.ON_DESTROY;
            case 3:
                return Event.ON_STOP;
            case 4:
                return Event.ON_PAUSE;
            case 5:
                throw new IllegalArgumentException();
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected state value ");
                stringBuilder.append(state);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private static Event upEvent(State state) {
        switch (AnonymousClass1.$SwitchMap$android$arch$lifecycle$Lifecycle$State[state.ordinal()]) {
            case 1:
            case 5:
                return Event.ON_CREATE;
            case 2:
                return Event.ON_START;
            case 3:
                return Event.ON_RESUME;
            case 4:
                throw new IllegalArgumentException();
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Unexpected state value ");
                stringBuilder.append(state);
                throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    private void forwardPass() {
        Iterator iteratorWithAdditions = this.mObserverMap.iteratorWithAdditions();
        while (iteratorWithAdditions.hasNext() && !this.mNewEventOccurred) {
            Entry entry = (Entry) iteratorWithAdditions.next();
            ObserverWithState observerWithState = (ObserverWithState) entry.getValue();
            while (observerWithState.mState.compareTo(this.mState) < 0 && !this.mNewEventOccurred && this.mObserverMap.contains(entry.getKey())) {
                pushParentState(observerWithState.mState);
                observerWithState.dispatchEvent(this.mLifecycleOwner, upEvent(observerWithState.mState));
                popParentState();
            }
        }
    }

    private void backwardPass() {
        Iterator descendingIterator = this.mObserverMap.descendingIterator();
        while (descendingIterator.hasNext() && !this.mNewEventOccurred) {
            Entry entry = (Entry) descendingIterator.next();
            ObserverWithState observerWithState = (ObserverWithState) entry.getValue();
            while (observerWithState.mState.compareTo(this.mState) > 0 && !this.mNewEventOccurred && this.mObserverMap.contains(entry.getKey())) {
                Event downEvent = downEvent(observerWithState.mState);
                pushParentState(getStateAfter(downEvent));
                observerWithState.dispatchEvent(this.mLifecycleOwner, downEvent);
                popParentState();
            }
        }
    }

    private void sync() {
        while (true) {
            boolean z = false;
            if (isSynced()) {
                this.mNewEventOccurred = z;
                return;
            }
            this.mNewEventOccurred = z;
            if (this.mState.compareTo(((ObserverWithState) this.mObserverMap.eldest().getValue()).mState) < 0) {
                backwardPass();
            }
            Entry newest = this.mObserverMap.newest();
            if (!(this.mNewEventOccurred || newest == null || this.mState.compareTo(((ObserverWithState) newest.getValue()).mState) <= 0)) {
                forwardPass();
            }
        }
    }

    static State min(@NonNull State state, @Nullable State state2) {
        return (state2 == null || state2.compareTo(state) >= 0) ? state : state2;
    }
}
