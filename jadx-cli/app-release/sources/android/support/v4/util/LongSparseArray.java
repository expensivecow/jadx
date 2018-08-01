package android.support.v4.util;

public class LongSparseArray<E> implements Cloneable {
    private static final Object DELETED = new Object();
    private boolean mGarbage;
    private long[] mKeys;
    private int mSize;
    private Object[] mValues;

    public LongSparseArray() {
        this(10);
    }

    public LongSparseArray(int i) {
        boolean z = false;
        this.mGarbage = z;
        if (i == 0) {
            this.mKeys = ContainerHelpers.EMPTY_LONGS;
            this.mValues = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            i = ContainerHelpers.idealLongArraySize(i);
            this.mKeys = new long[i];
            this.mValues = new Object[i];
        }
        this.mSize = z;
    }

    public android.support.v4.util.LongSparseArray<E> clone() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v4.util.LongSparseArray.clone():android.support.v4.util.LongSparseArray<E>, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
        /*
        r2 = this;
        r0 = 0;
        r1 = super.clone();	 Catch:{ CloneNotSupportedException -> 0x001c }
        r1 = (android.support.v4.util.LongSparseArray) r1;	 Catch:{ CloneNotSupportedException -> 0x001c }
        r0 = r2.mKeys;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r0.clone();	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = (long[]) r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r1.mKeys = r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r2.mValues;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = r0.clone();	 Catch:{ CloneNotSupportedException -> 0x001d }
        r0 = (java.lang.Object[]) r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        r1.mValues = r0;	 Catch:{ CloneNotSupportedException -> 0x001d }
        goto L_0x001d;
    L_0x001c:
        r1 = r0;
    L_0x001d:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.util.LongSparseArray.clone():android.support.v4.util.LongSparseArray<E>");
    }

    public E get(long j) {
        return get(j, null);
    }

    public E get(long j, E e) {
        int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        return (binarySearch < 0 || this.mValues[binarySearch] == DELETED) ? e : this.mValues[binarySearch];
    }

    public void delete(long j) {
        int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (binarySearch >= 0 && this.mValues[binarySearch] != DELETED) {
            this.mValues[binarySearch] = DELETED;
            this.mGarbage = true;
        }
    }

    public void remove(long j) {
        delete(j);
    }

    public void removeAt(int i) {
        if (this.mValues[i] != DELETED) {
            this.mValues[i] = DELETED;
            this.mGarbage = true;
        }
    }

    private void gc() {
        int i = this.mSize;
        long[] jArr = this.mKeys;
        Object[] objArr = this.mValues;
        boolean z = false;
        int i2 = z;
        int i3 = i2;
        while (i2 < i) {
            Object obj = objArr[i2];
            if (obj != DELETED) {
                if (i2 != i3) {
                    jArr[i3] = jArr[i2];
                    objArr[i3] = obj;
                    objArr[i2] = null;
                }
                i3++;
            }
            i2++;
        }
        this.mGarbage = z;
        this.mSize = i3;
    }

    public void put(long j, E e) {
        int binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
        if (binarySearch >= 0) {
            this.mValues[binarySearch] = e;
        } else {
            binarySearch ^= -1;
            if (binarySearch >= this.mSize || this.mValues[binarySearch] != DELETED) {
                if (this.mGarbage && this.mSize >= this.mKeys.length) {
                    gc();
                    binarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, j) ^ -1;
                }
                if (this.mSize >= this.mKeys.length) {
                    int idealLongArraySize = ContainerHelpers.idealLongArraySize(this.mSize + 1);
                    Object obj = new long[idealLongArraySize];
                    Object obj2 = new Object[idealLongArraySize];
                    int i = 0;
                    System.arraycopy(this.mKeys, i, obj, i, this.mKeys.length);
                    System.arraycopy(this.mValues, i, obj2, i, this.mValues.length);
                    this.mKeys = obj;
                    this.mValues = obj2;
                }
                if (this.mSize - binarySearch != 0) {
                    int i2 = binarySearch + 1;
                    System.arraycopy(this.mKeys, binarySearch, this.mKeys, i2, this.mSize - binarySearch);
                    System.arraycopy(this.mValues, binarySearch, this.mValues, i2, this.mSize - binarySearch);
                }
                this.mKeys[binarySearch] = j;
                this.mValues[binarySearch] = e;
                this.mSize++;
            } else {
                this.mKeys[binarySearch] = j;
                this.mValues[binarySearch] = e;
            }
        }
    }

    public int size() {
        if (this.mGarbage) {
            gc();
        }
        return this.mSize;
    }

    public long keyAt(int i) {
        if (this.mGarbage) {
            gc();
        }
        return this.mKeys[i];
    }

    public E valueAt(int i) {
        if (this.mGarbage) {
            gc();
        }
        return this.mValues[i];
    }

    public void setValueAt(int i, E e) {
        if (this.mGarbage) {
            gc();
        }
        this.mValues[i] = e;
    }

    public int indexOfKey(long j) {
        if (this.mGarbage) {
            gc();
        }
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, j);
    }

    public int indexOfValue(E e) {
        if (this.mGarbage) {
            gc();
        }
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == e) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        int i = this.mSize;
        Object[] objArr = this.mValues;
        boolean z = false;
        for (int i2 = z; i2 < i; i2++) {
            objArr[i2] = null;
        }
        this.mSize = z;
        this.mGarbage = z;
    }

    public void append(long j, E e) {
        if (this.mSize == 0 || j > this.mKeys[this.mSize - 1]) {
            if (this.mGarbage && this.mSize >= this.mKeys.length) {
                gc();
            }
            int i = this.mSize;
            if (i >= this.mKeys.length) {
                int idealLongArraySize = ContainerHelpers.idealLongArraySize(i + 1);
                Object obj = new long[idealLongArraySize];
                Object obj2 = new Object[idealLongArraySize];
                int i2 = 0;
                System.arraycopy(this.mKeys, i2, obj, i2, this.mKeys.length);
                System.arraycopy(this.mValues, i2, obj2, i2, this.mValues.length);
                this.mKeys = obj;
                this.mValues = obj2;
            }
            this.mKeys[i] = j;
            this.mValues[i] = e;
            this.mSize = i + 1;
            return;
        }
        put(j, e);
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(this.mSize * 28);
        stringBuilder.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            stringBuilder.append(keyAt(i));
            stringBuilder.append('=');
            LongSparseArray valueAt = valueAt(i);
            if (valueAt != this) {
                stringBuilder.append(valueAt);
            } else {
                stringBuilder.append("(this Map)");
            }
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
