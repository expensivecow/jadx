package android.support.v4.util;

import java.util.ConcurrentModificationException;
import java.util.Map;

public class SimpleArrayMap<K, V> {
    private static final int BASE_SIZE = 4;
    private static final int CACHE_SIZE = 10;
    private static final boolean CONCURRENT_MODIFICATION_EXCEPTIONS = true;
    private static final boolean DEBUG = false;
    private static final String TAG = "ArrayMap";
    static Object[] mBaseCache;
    static int mBaseCacheSize;
    static Object[] mTwiceBaseCache;
    static int mTwiceBaseCacheSize;
    Object[] mArray;
    int[] mHashes;
    int mSize;

    private static int binarySearchHashes(int[] iArr, int i, int i2) {
        try {
            return ContainerHelpers.binarySearch(iArr, i, i2);
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new ConcurrentModificationException();
        }
    }

    int indexOf(Object obj, int i) {
        int i2 = this.mSize;
        int i3 = -1;
        if (i2 == 0) {
            return i3;
        }
        int binarySearchHashes = binarySearchHashes(this.mHashes, i2, i);
        if (binarySearchHashes < 0 || obj.equals(this.mArray[binarySearchHashes << 1])) {
            return binarySearchHashes;
        }
        int i4 = binarySearchHashes + 1;
        while (i4 < i2 && this.mHashes[i4] == i) {
            if (obj.equals(this.mArray[i4 << 1])) {
                return i4;
            }
            i4++;
        }
        binarySearchHashes--;
        while (binarySearchHashes >= 0 && this.mHashes[binarySearchHashes] == i) {
            if (obj.equals(this.mArray[binarySearchHashes << 1])) {
                return binarySearchHashes;
            }
            binarySearchHashes--;
        }
        return i4 ^ -1;
    }

    int indexOfNull() {
        int i = this.mSize;
        int i2 = -1;
        if (i == 0) {
            return i2;
        }
        int binarySearchHashes = binarySearchHashes(this.mHashes, i, 0);
        if (binarySearchHashes < 0 || this.mArray[binarySearchHashes << 1] == null) {
            return binarySearchHashes;
        }
        int i3 = binarySearchHashes + 1;
        while (i3 < i && this.mHashes[i3] == 0) {
            if (this.mArray[i3 << 1] == null) {
                return i3;
            }
            i3++;
        }
        binarySearchHashes--;
        while (binarySearchHashes >= 0 && this.mHashes[binarySearchHashes] == 0) {
            if (this.mArray[binarySearchHashes << 1] == null) {
                return binarySearchHashes;
            }
            binarySearchHashes--;
        }
        return i3 ^ -1;
    }

    private void allocArrays(int i) {
        Object obj = null;
        int i2 = 0;
        int i3 = 1;
        Object[] objArr;
        if (i == 8) {
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCache != null) {
                    objArr = mTwiceBaseCache;
                    this.mArray = objArr;
                    mTwiceBaseCache = (Object[]) objArr[i2];
                    this.mHashes = (int[]) objArr[i3];
                    objArr[i3] = obj;
                    objArr[i2] = obj;
                    mTwiceBaseCacheSize -= i3;
                    return;
                }
            }
        } else if (i == 4) {
            synchronized (ArrayMap.class) {
                if (mBaseCache != null) {
                    objArr = mBaseCache;
                    this.mArray = objArr;
                    mBaseCache = (Object[]) objArr[i2];
                    this.mHashes = (int[]) objArr[i3];
                    objArr[i3] = obj;
                    objArr[i2] = obj;
                    mBaseCacheSize -= i3;
                    return;
                }
            }
        }
        this.mHashes = new int[i];
        this.mArray = new Object[(i << i3)];
    }

    private static void freeArrays(int[] iArr, Object[] objArr, int i) {
        Object obj = null;
        int i2 = 2;
        int i3 = 0;
        int i4 = 10;
        int i5 = 1;
        int i6;
        if (iArr.length == 8) {
            synchronized (ArrayMap.class) {
                if (mTwiceBaseCacheSize < i4) {
                    objArr[i3] = mTwiceBaseCache;
                    objArr[i5] = iArr;
                    for (i6 = (i << 1) - i5; i6 >= i2; i6--) {
                        objArr[i6] = obj;
                    }
                    mTwiceBaseCache = objArr;
                    mTwiceBaseCacheSize += i5;
                }
            }
        } else if (iArr.length == 4) {
            synchronized (ArrayMap.class) {
                if (mBaseCacheSize < i4) {
                    objArr[i3] = mBaseCache;
                    objArr[i5] = iArr;
                    for (i6 = (i << 1) - i5; i6 >= i2; i6--) {
                        objArr[i6] = obj;
                    }
                    mBaseCache = objArr;
                    mBaseCacheSize += i5;
                }
            }
        }
    }

    public SimpleArrayMap() {
        this.mHashes = ContainerHelpers.EMPTY_INTS;
        this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        this.mSize = 0;
    }

    public SimpleArrayMap(int i) {
        if (i == 0) {
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            allocArrays(i);
        }
        this.mSize = 0;
    }

    public SimpleArrayMap(SimpleArrayMap<K, V> simpleArrayMap) {
        this();
        if (simpleArrayMap != null) {
            putAll(simpleArrayMap);
        }
    }

    public void clear() {
        if (this.mSize > 0) {
            int[] iArr = this.mHashes;
            Object[] objArr = this.mArray;
            int i = this.mSize;
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
            this.mSize = 0;
            freeArrays(iArr, objArr, i);
        }
        if (this.mSize > 0) {
            throw new ConcurrentModificationException();
        }
    }

    public void ensureCapacity(int i) {
        int i2 = this.mSize;
        if (this.mHashes.length < i) {
            Object obj = this.mHashes;
            Object obj2 = this.mArray;
            allocArrays(i);
            if (this.mSize > 0) {
                int i3 = 0;
                System.arraycopy(obj, i3, this.mHashes, i3, i2);
                System.arraycopy(obj2, i3, this.mArray, i3, i2 << 1);
            }
            freeArrays(obj, obj2, i2);
        }
        if (this.mSize != i2) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean containsKey(Object obj) {
        return indexOfKey(obj) >= 0;
    }

    public int indexOfKey(Object obj) {
        return obj == null ? indexOfNull() : indexOf(obj, obj.hashCode());
    }

    int indexOfValue(Object obj) {
        int i = this.mSize * 2;
        Object[] objArr = this.mArray;
        int i2 = 1;
        if (obj == null) {
            for (int i3 = i2; i3 < i; i3 += 2) {
                if (objArr[i3] == null) {
                    return i3 >> i2;
                }
            }
        } else {
            for (int i4 = i2; i4 < i; i4 += 2) {
                if (obj.equals(objArr[i4])) {
                    return i4 >> 1;
                }
            }
        }
        return -1;
    }

    public boolean containsValue(Object obj) {
        return indexOfValue(obj) >= 0;
    }

    public V get(Object obj) {
        int indexOfKey = indexOfKey(obj);
        return indexOfKey >= 0 ? this.mArray[(indexOfKey << 1) + 1] : null;
    }

    public K keyAt(int i) {
        return this.mArray[i << 1];
    }

    public V valueAt(int i) {
        return this.mArray[(i << 1) + 1];
    }

    public V setValueAt(int i, V v) {
        i = (i << 1) + 1;
        V v2 = this.mArray[i];
        this.mArray[i] = v;
        return v2;
    }

    public boolean isEmpty() {
        return this.mSize <= 0;
    }

    public V put(K k, V v) {
        int indexOfNull;
        int i;
        int i2 = this.mSize;
        int i3 = 0;
        if (k == null) {
            indexOfNull = indexOfNull();
            i = i3;
        } else {
            indexOfNull = k.hashCode();
            i = indexOfNull;
            indexOfNull = indexOf(k, indexOfNull);
        }
        if (indexOfNull >= 0) {
            int i4 = (indexOfNull << 1) + 1;
            V v2 = this.mArray[i4];
            this.mArray[i4] = v;
            return v2;
        }
        int i5;
        indexOfNull ^= -1;
        if (i2 >= this.mHashes.length) {
            int i6 = 4;
            i5 = 8;
            if (i2 >= i5) {
                i6 = (i2 >> 1) + i2;
            } else if (i2 >= i6) {
                i6 = i5;
            }
            Object obj = this.mHashes;
            Object obj2 = this.mArray;
            allocArrays(i6);
            if (i2 != this.mSize) {
                throw new ConcurrentModificationException();
            }
            if (this.mHashes.length > 0) {
                System.arraycopy(obj, i3, this.mHashes, i3, obj.length);
                System.arraycopy(obj2, i3, this.mArray, i3, obj2.length);
            }
            freeArrays(obj, obj2, i2);
        }
        if (indexOfNull < i2) {
            i5 = indexOfNull + 1;
            System.arraycopy(this.mHashes, indexOfNull, this.mHashes, i5, i2 - indexOfNull);
            System.arraycopy(this.mArray, indexOfNull << 1, this.mArray, i5 << 1, (this.mSize - indexOfNull) << 1);
        }
        if (i2 != this.mSize || indexOfNull >= this.mHashes.length) {
            throw new ConcurrentModificationException();
        }
        this.mHashes[indexOfNull] = i;
        i3 = indexOfNull << 1;
        this.mArray[i3] = k;
        this.mArray[i3 + 1] = v;
        this.mSize++;
        return null;
    }

    public void putAll(SimpleArrayMap<? extends K, ? extends V> simpleArrayMap) {
        int i = simpleArrayMap.mSize;
        ensureCapacity(this.mSize + i);
        int i2 = 0;
        if (this.mSize != 0) {
            while (i2 < i) {
                put(simpleArrayMap.keyAt(i2), simpleArrayMap.valueAt(i2));
                i2++;
            }
        } else if (i > 0) {
            System.arraycopy(simpleArrayMap.mHashes, i2, this.mHashes, i2, i);
            System.arraycopy(simpleArrayMap.mArray, i2, this.mArray, i2, i << 1);
            this.mSize = i;
        }
    }

    public V remove(Object obj) {
        int indexOfKey = indexOfKey(obj);
        return indexOfKey >= 0 ? removeAt(indexOfKey) : null;
    }

    public V removeAt(int i) {
        int i2 = i << 1;
        V v = this.mArray[i2 + 1];
        int i3 = this.mSize;
        int i4 = 0;
        int i5 = 1;
        if (i3 <= i5) {
            freeArrays(this.mHashes, this.mArray, i3);
            this.mHashes = ContainerHelpers.EMPTY_INTS;
            this.mArray = ContainerHelpers.EMPTY_OBJECTS;
        } else {
            int i6 = i3 - 1;
            int i7 = 8;
            if (this.mHashes.length <= i7 || this.mSize >= this.mHashes.length / 3) {
                if (i < i6) {
                    int i8 = i + 1;
                    int i9 = i6 - i;
                    System.arraycopy(this.mHashes, i8, this.mHashes, i, i9);
                    System.arraycopy(this.mArray, i8 << 1, this.mArray, i2, i9 << 1);
                }
                i2 = i6 << 1;
                Object obj = null;
                this.mArray[i2] = obj;
                this.mArray[i2 + i5] = obj;
            } else {
                if (i3 > i7) {
                    i7 = i3 + (i3 >> 1);
                }
                Object obj2 = this.mHashes;
                Object obj3 = this.mArray;
                allocArrays(i7);
                if (i3 != this.mSize) {
                    throw new ConcurrentModificationException();
                }
                if (i > 0) {
                    System.arraycopy(obj2, i4, this.mHashes, i4, i);
                    System.arraycopy(obj3, i4, this.mArray, i4, i2);
                }
                if (i < i6) {
                    i4 = i + 1;
                    int i10 = i6 - i;
                    System.arraycopy(obj2, i4, this.mHashes, i, i10);
                    System.arraycopy(obj3, i4 << 1, this.mArray, i2, i10 << 1);
                }
            }
            i4 = i6;
        }
        if (i3 != this.mSize) {
            throw new ConcurrentModificationException();
        }
        this.mSize = i4;
        return v;
    }

    public int size() {
        return this.mSize;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return z;
        }
        boolean z2 = false;
        int i;
        Object keyAt;
        Object valueAt;
        Object obj2;
        if (obj instanceof SimpleArrayMap) {
            SimpleArrayMap simpleArrayMap = (SimpleArrayMap) obj;
            if (size() != simpleArrayMap.size()) {
                return z2;
            }
            i = z2;
            while (i < this.mSize) {
                try {
                    keyAt = keyAt(i);
                    valueAt = valueAt(i);
                    obj2 = simpleArrayMap.get(keyAt);
                    if (valueAt == null) {
                        if (obj2 != null || !simpleArrayMap.containsKey(keyAt)) {
                            return z2;
                        }
                    } else if (!valueAt.equals(obj2)) {
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
        } else if (!(obj instanceof Map)) {
            return z2;
        } else {
            Map map = (Map) obj;
            if (size() != map.size()) {
                return z2;
            }
            i = z2;
            while (i < this.mSize) {
                try {
                    keyAt = keyAt(i);
                    valueAt = valueAt(i);
                    obj2 = map.get(keyAt);
                    if (valueAt == null) {
                        if (obj2 != null || !map.containsKey(keyAt)) {
                            return z2;
                        }
                    } else if (!valueAt.equals(obj2)) {
                        return z2;
                    }
                    i++;
                } catch (NullPointerException unused3) {
                    return z2;
                } catch (ClassCastException unused4) {
                    return z2;
                }
            }
            return z;
        }
    }

    public int hashCode() {
        int[] iArr = this.mHashes;
        Object[] objArr = this.mArray;
        int i = this.mSize;
        int i2 = 0;
        int i3 = 1;
        int i4 = i2;
        int i5 = i4;
        while (i4 < i) {
            Object obj = objArr[i3];
            i5 += (obj == null ? i2 : obj.hashCode()) ^ iArr[i4];
            i4++;
            i3 += 2;
        }
        return i5;
    }

    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(this.mSize * 28);
        stringBuilder.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                stringBuilder.append(", ");
            }
            SimpleArrayMap keyAt = keyAt(i);
            if (keyAt != this) {
                stringBuilder.append(keyAt);
            } else {
                stringBuilder.append("(this Map)");
            }
            stringBuilder.append('=');
            keyAt = valueAt(i);
            if (keyAt != this) {
                stringBuilder.append(keyAt);
            } else {
                stringBuilder.append("(this Map)");
            }
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }
}
