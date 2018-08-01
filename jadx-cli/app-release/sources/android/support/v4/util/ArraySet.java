package android.support.v4.util;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class ArraySet<E> implements Collection<E>, Set<E> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean DEBUG = false;
    private static final int[] INT;
    private static final Object[] OBJECT;
    private static final String TAG = "ArraySet";
    static Object[] sBaseCache;
    static int sBaseCacheSize;
    static Object[] sTwiceBaseCache;
    static int sTwiceBaseCacheSize;
    Object[] mArray;
    MapCollections<E, E> mCollections;
    int[] mHashes;
    final boolean mIdentityHashCode;
    int mSize;

    static {
        int i = 0;
        INT = new int[i];
        OBJECT = new Object[i];
    }

    private int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        int i3 = -1;
        if (i2 == 0) {
            return i3;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i2, i);
        if (binarySearch < 0 || obj.equals(this.mArray[binarySearch])) {
            return binarySearch;
        }
        int i4 = binarySearch + 1;
        while (i4 < i2 && this.mHashes[i4] == i) {
            if (obj.equals(this.mArray[i4])) {
                return i4;
            }
            i4++;
        }
        binarySearch--;
        while (binarySearch >= 0 && this.mHashes[binarySearch] == i) {
            if (obj.equals(this.mArray[binarySearch])) {
                return binarySearch;
            }
            binarySearch--;
        }
        return i4 ^ -1;
    }

    private int indexOfNull() {
        int i = this.mSize;
        int i2 = -1;
        if (i == 0) {
            return i2;
        }
        int binarySearch = ContainerHelpers.binarySearch(this.mHashes, i, 0);
        if (binarySearch < 0 || this.mArray[binarySearch] == null) {
            return binarySearch;
        }
        int i3 = binarySearch + 1;
        while (i3 < i && this.mHashes[i3] == 0) {
            if (this.mArray[i3] == null) {
                return i3;
            }
            i3++;
        }
        binarySearch--;
        while (binarySearch >= 0 && this.mHashes[binarySearch] == 0) {
            if (this.mArray[binarySearch] == null) {
                return binarySearch;
            }
            binarySearch--;
        }
        return i3 ^ -1;
    }

    private void allocArrays(int i) {
        Object obj = null;
        int i2 = 0;
        int i3 = 1;
        Object[] objArr;
        if (i == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCache != null) {
                    objArr = sTwiceBaseCache;
                    this.mArray = objArr;
                    sTwiceBaseCache = (Object[]) objArr[i2];
                    this.mHashes = (int[]) objArr[i3];
                    objArr[i3] = obj;
                    objArr[i2] = obj;
                    sTwiceBaseCacheSize -= i3;
                    return;
                }
            }
        } else if (i == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCache != null) {
                    objArr = sBaseCache;
                    this.mArray = objArr;
                    sBaseCache = (Object[]) objArr[i2];
                    this.mHashes = (int[]) objArr[i3];
                    objArr[i3] = obj;
                    objArr[i2] = obj;
                    sBaseCacheSize -= i3;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[i];
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        Object obj = null;
        int i2 = 2;
        int i3 = 0;
        int i4 = 10;
        int i5 = 1;
        if (iArr.length == 8) {
            synchronized (ArraySet.class) {
                if (sTwiceBaseCacheSize < i4) {
                    objArr[i3] = sTwiceBaseCache;
                    objArr[i5] = iArr;
                    for (i -= i5; i >= i2; i--) {
                        objArr[i] = obj;
                    }
                    sTwiceBaseCache = objArr;
                    sTwiceBaseCacheSize += i5;
                }
            }
        } else if (iArr.length == 4) {
            synchronized (ArraySet.class) {
                if (sBaseCacheSize < i4) {
                    objArr[i3] = sBaseCache;
                    objArr[i5] = iArr;
                    for (i -= i5; i >= i2; i--) {
                        objArr[i] = obj;
                    }
                    sBaseCache = objArr;
                    sBaseCacheSize += i5;
                }
            }
        }
    }

    public ArraySet() {
        boolean z = false;
        this(z, z);
    }

    public ArraySet(int i) {
        this(i, false);
    }

    public ArraySet(int i, boolean z) {
        this.mIdentityHashCode = z;
        if (i == 0) {
            this.mHashes = INT;
            this.mArray = OBJECT;
        } else {
            allocArrays(i);
        }
        this.mSize = 0;
    }

    public ArraySet(ArraySet<E> arraySet) {
        this();
        if (arraySet != null) {
            addAll((ArraySet) arraySet);
        }
    }

    public ArraySet(Collection<E> collection) {
        this();
        if (collection != null) {
            addAll((Collection) collection);
        }
    }

    public void clear() {
        if (this.mSize != 0) {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = INT;
            this.mArray = OBJECT;
            this.mSize = 0;
        }
    }

    public void ensureCapacity(int i) {
        if (this.mHashes.length < i) {
            Object obj = this.mHashes;
            Object obj2 = this.mArray;
            allocArrays(i);
            if (this.mSize > 0) {
                int i2 = 0;
                System.arraycopy(obj, i2, this.mHashes, i2, this.mSize);
                System.arraycopy(obj2, i2, this.mArray, i2, this.mSize);
            }
            freeArrays(obj, obj2, this.mSize);
        }
    }

    public boolean contains(Object obj) {
        return indexOf(obj) >= 0;
    }

    public int indexOf(Object obj) {
        if (obj == null) {
            return indexOfNull();
        }
        return indexOf(obj, this.mIdentityHashCode ? System.identityHashCode(obj) : obj.hashCode());
    }

    public E valueAt(int i) {
        return this.mArray[i];
    }

    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    public boolean add(E e) {
        int indexOfNull;
        boolean z;
        boolean z2 = false;
        if (e == null) {
            indexOfNull = indexOfNull();
            z = z2;
        } else {
            boolean identityHashCode = this.mIdentityHashCode ? System.identityHashCode(e) : e.hashCode();
            z = identityHashCode;
            indexOfNull = indexOf(e, identityHashCode);
        }
        if (indexOfNull >= 0) {
            return z2;
        }
        int i;
        indexOfNull ^= -1;
        boolean z3 = true;
        if (this.mSize >= this.mHashes.length) {
            i = 4;
            int i2 = 8;
            if (this.mSize >= i2) {
                i = (this.mSize >> z3) + this.mSize;
            } else if (this.mSize >= i) {
                i = i2;
            }
            Object obj = this.mHashes;
            Object obj2 = this.mArray;
            allocArrays(i);
            if (this.mHashes.length > 0) {
                System.arraycopy(obj, z2, this.mHashes, z2, obj.length);
                System.arraycopy(obj2, z2, this.mArray, z2, obj2.length);
            }
            freeArrays(obj, obj2, this.mSize);
        }
        if (indexOfNull < this.mSize) {
            i = indexOfNull + 1;
            System.arraycopy(this.mHashes, indexOfNull, this.mHashes, i, this.mSize - indexOfNull);
            System.arraycopy(this.mArray, indexOfNull, this.mArray, i, this.mSize - indexOfNull);
        }
        this.mHashes[indexOfNull] = z;
        this.mArray[indexOfNull] = e;
        this.mSize += z3;
        return z3;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public void append(E e) {
        int i = this.mSize;
        int identityHashCode = e == null ? 0 : this.mIdentityHashCode ? System.identityHashCode(e) : e.hashCode();
        if (i >= this.mHashes.length) {
            throw new IllegalStateException("Array is full");
        } else if (i <= 0 || this.mHashes[i - 1] <= identityHashCode) {
            this.mSize = i + 1;
            this.mHashes[i] = identityHashCode;
            this.mArray[i] = e;
        } else {
            add(e);
        }
    }

    public void addAll(ArraySet<? extends E> arraySet) {
        int i = arraySet.mSize;
        ensureCapacity(this.mSize + i);
        int i2 = 0;
        if (this.mSize != 0) {
            while (i2 < i) {
                add(arraySet.valueAt(i2));
                i2++;
            }
        } else if (i > 0) {
            System.arraycopy(arraySet.mHashes, i2, this.mHashes, i2, i);
            System.arraycopy(arraySet.mArray, i2, this.mArray, i2, i);
            this.mSize = i;
        }
    }

    public boolean remove(Object obj) {
        int indexOf = indexOf(obj);
        if (indexOf < 0) {
            return false;
        }
        removeAt(indexOf);
        return true;
    }

    public E removeAt(int i) {
        E e = this.mArray[i];
        int i2 = 0;
        int i3 = 1;
        if (this.mSize <= i3) {
            freeArrays(this.mHashes, this.mArray, this.mSize);
            this.mHashes = INT;
            this.mArray = OBJECT;
            this.mSize = i2;
        } else {
            int i4 = 8;
            if (this.mHashes.length <= i4 || this.mSize >= this.mHashes.length / 3) {
                this.mSize -= i3;
                if (i < this.mSize) {
                    i2 = i + 1;
                    System.arraycopy(this.mHashes, i2, this.mHashes, i, this.mSize - i);
                    System.arraycopy(this.mArray, i2, this.mArray, i, this.mSize - i);
                }
                this.mArray[this.mSize] = null;
            } else {
                if (this.mSize > i4) {
                    i4 = (this.mSize >> i3) + this.mSize;
                }
                Object obj = this.mHashes;
                Object obj2 = this.mArray;
                allocArrays(i4);
                this.mSize -= i3;
                if (i > 0) {
                    System.arraycopy(obj, i2, this.mHashes, i2, i);
                    System.arraycopy(obj2, i2, this.mArray, i2, i);
                }
                if (i < this.mSize) {
                    i2 = i + 1;
                    System.arraycopy(obj, i2, this.mHashes, i, this.mSize - i);
                    System.arraycopy(obj2, i2, this.mArray, i, this.mSize - i);
                }
            }
        }
        return e;
    }

    public boolean removeAll(ArraySet<? extends E> arraySet) {
        int i = arraySet.mSize;
        int i2 = this.mSize;
        boolean z = false;
        for (int i3 = z; i3 < i; i3++) {
            remove(arraySet.valueAt(i3));
        }
        return i2 != this.mSize ? true : z;
    }

    public int size() {
        return this.mSize;
    }

    public Object[] toArray() {
        Object obj = new Object[this.mSize];
        int i = 0;
        System.arraycopy(this.mArray, i, obj, i, this.mSize);
        return obj;
    }

    public <T> T[] toArray(T[] tArr) {
        Object tArr2;
        if (tArr2.length < this.mSize) {
            tArr2 = (Object[]) Array.newInstance(tArr2.getClass().getComponentType(), this.mSize);
        }
        int i = 0;
        System.arraycopy(this.mArray, i, tArr2, i, this.mSize);
        if (tArr2.length > this.mSize) {
            tArr2[this.mSize] = null;
        }
        return tArr2;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return z;
        }
        boolean z2 = false;
        if (!(obj instanceof Set)) {
            return z2;
        }
        Set set = (Set) obj;
        if (size() != set.size()) {
            return z2;
        }
        int i = z2;
        while (i < this.mSize) {
            try {
                if (!set.contains(valueAt(i))) {
                    return z2;
                }
                i++;
            } catch (NullPointerException unused) {
                return z2;
            } catch (ClassCastException unused2) {
                return z2;
            }
        }
        return z;
    }

    public int hashCode() {
        int[] iArr = this.mHashes;
        int i = 0;
        int i2 = i;
        while (i < this.mSize) {
            i2 += iArr[i];
            i++;
        }
        return i2;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(this.mSize * 14);
        stringBuilder.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            ArraySet valueAt = valueAt(i);
            if (valueAt != this) {
                stringBuilder.append(valueAt);
            } else {
                stringBuilder.append("(this Set)");
            }
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private MapCollections<E, E> getCollection() {
        if (this.mCollections == null) {
            this.mCollections = new MapCollections<E, E>() {
                protected int colGetSize() {
                    return ArraySet.this.mSize;
                }

                protected Object colGetEntry(int i, int i2) {
                    return ArraySet.this.mArray[i];
                }

                protected int colIndexOfKey(Object obj) {
                    return ArraySet.this.indexOf(obj);
                }

                protected int colIndexOfValue(Object obj) {
                    return ArraySet.this.indexOf(obj);
                }

                protected Map<E, E> colGetMap() {
                    throw new UnsupportedOperationException("not a map");
                }

                protected void colPut(E e, E e2) {
                    ArraySet.this.add(e);
                }

                protected E colSetValue(int i, E e) {
                    throw new UnsupportedOperationException("not a map");
                }

                protected void colRemoveAt(int i) {
                    ArraySet.this.removeAt(i);
                }

                protected void colClear() {
                    ArraySet.this.clear();
                }
            };
        }
        return this.mCollections;
    }

    public Iterator<E> iterator() {
        return getCollection().getKeySet().iterator();
    }

    public boolean containsAll(Collection<?> collection) {
        for (Object contains : collection) {
            if (!contains(contains)) {
                return false;
            }
        }
        return true;
    }

    public boolean addAll(Collection<? extends E> collection) {
        ensureCapacity(this.mSize + collection.size());
        boolean z = false;
        for (Object add : collection) {
            z |= add(add);
        }
        return z;
    }

    public boolean removeAll(Collection<?> collection) {
        boolean z = false;
        for (Object remove : collection) {
            z |= remove(remove);
        }
        return z;
    }

    public boolean retainAll(Collection<?> collection) {
        boolean z = true;
        boolean z2 = false;
        for (int i = this.mSize - z; i >= 0; i--) {
            if (!collection.contains(this.mArray[i])) {
                removeAt(i);
                z2 = z;
            }
        }
        return z2;
    }
}
