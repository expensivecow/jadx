package android.support.v4.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

abstract class MapCollections<K, V> {
    EntrySet mEntrySet;
    KeySet mKeySet;
    ValuesCollection mValues;

    final class ArrayIterator<T> implements Iterator<T> {
        boolean mCanRemove = false;
        int mIndex;
        final int mOffset;
        int mSize;

        ArrayIterator(int i) {
            this.mOffset = i;
            this.mSize = MapCollections.this.colGetSize();
        }

        public boolean hasNext() {
            return this.mIndex < this.mSize;
        }

        public T next() {
            if (hasNext()) {
                T colGetEntry = MapCollections.this.colGetEntry(this.mIndex, this.mOffset);
                boolean z = true;
                this.mIndex += z;
                this.mCanRemove = z;
                return colGetEntry;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.mCanRemove) {
                this.mIndex--;
                this.mSize--;
                this.mCanRemove = false;
                MapCollections.this.colRemoveAt(this.mIndex);
                return;
            }
            throw new IllegalStateException();
        }
    }

    final class EntrySet implements Set<Entry<K, V>> {
        EntrySet() {
        }

        public boolean add(Entry<K, V> entry) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends Entry<K, V>> collection) {
            int colGetSize = MapCollections.this.colGetSize();
            for (Entry entry : collection) {
                MapCollections.this.colPut(entry.getKey(), entry.getValue());
            }
            return colGetSize != MapCollections.this.colGetSize();
        }

        public void clear() {
            MapCollections.this.colClear();
        }

        public boolean contains(Object obj) {
            boolean z = false;
            if (!(obj instanceof Entry)) {
                return z;
            }
            Entry entry = (Entry) obj;
            int colIndexOfKey = MapCollections.this.colIndexOfKey(entry.getKey());
            if (colIndexOfKey < 0) {
                return z;
            }
            return ContainerHelpers.equal(MapCollections.this.colGetEntry(colIndexOfKey, 1), entry.getValue());
        }

        public boolean containsAll(Collection<?> collection) {
            for (Object contains : collection) {
                if (!contains(contains)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new MapIterator();
        }

        public boolean remove(Object obj) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> collection) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return MapCollections.this.colGetSize();
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }

        public <T> T[] toArray(T[] tArr) {
            throw new UnsupportedOperationException();
        }

        public boolean equals(Object obj) {
            return MapCollections.equalsSetHelper(this, obj);
        }

        public int hashCode() {
            int i = 1;
            int i2 = 0;
            int i3 = i2;
            for (int colGetSize = MapCollections.this.colGetSize() - i; colGetSize >= 0; colGetSize--) {
                int i4;
                int i5;
                Object colGetEntry = MapCollections.this.colGetEntry(colGetSize, i2);
                Object colGetEntry2 = MapCollections.this.colGetEntry(colGetSize, i);
                if (colGetEntry == null) {
                    i4 = i2;
                } else {
                    i4 = colGetEntry.hashCode();
                }
                if (colGetEntry2 == null) {
                    i5 = i2;
                } else {
                    i5 = colGetEntry2.hashCode();
                }
                i3 += i4 ^ i5;
            }
            return i3;
        }
    }

    final class KeySet implements Set<K> {
        KeySet() {
        }

        public boolean add(K k) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends K> collection) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            MapCollections.this.colClear();
        }

        public boolean contains(Object obj) {
            return MapCollections.this.colIndexOfKey(obj) >= 0;
        }

        public boolean containsAll(Collection<?> collection) {
            return MapCollections.containsAllHelper(MapCollections.this.colGetMap(), collection);
        }

        public boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public Iterator<K> iterator() {
            return new ArrayIterator(0);
        }

        public boolean remove(Object obj) {
            int colIndexOfKey = MapCollections.this.colIndexOfKey(obj);
            if (colIndexOfKey < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(colIndexOfKey);
            return true;
        }

        public boolean removeAll(Collection<?> collection) {
            return MapCollections.removeAllHelper(MapCollections.this.colGetMap(), collection);
        }

        public boolean retainAll(Collection<?> collection) {
            return MapCollections.retainAllHelper(MapCollections.this.colGetMap(), collection);
        }

        public int size() {
            return MapCollections.this.colGetSize();
        }

        public Object[] toArray() {
            return MapCollections.this.toArrayHelper(0);
        }

        public <T> T[] toArray(T[] tArr) {
            return MapCollections.this.toArrayHelper(tArr, 0);
        }

        public boolean equals(Object obj) {
            return MapCollections.equalsSetHelper(this, obj);
        }

        public int hashCode() {
            int i = 0;
            int i2 = i;
            for (int colGetSize = MapCollections.this.colGetSize() - 1; colGetSize >= 0; colGetSize--) {
                int i3;
                Object colGetEntry = MapCollections.this.colGetEntry(colGetSize, i);
                if (colGetEntry == null) {
                    i3 = i;
                } else {
                    i3 = colGetEntry.hashCode();
                }
                i2 += i3;
            }
            return i2;
        }
    }

    final class MapIterator implements Iterator<Entry<K, V>>, Entry<K, V> {
        int mEnd;
        boolean mEntryValid = false;
        int mIndex;

        MapIterator() {
            this.mEnd = MapCollections.this.colGetSize() - 1;
            this.mIndex = -1;
        }

        public boolean hasNext() {
            return this.mIndex < this.mEnd;
        }

        public Entry<K, V> next() {
            if (hasNext()) {
                boolean z = true;
                this.mIndex += z;
                this.mEntryValid = z;
                return this;
            }
            throw new NoSuchElementException();
        }

        public void remove() {
            if (this.mEntryValid) {
                MapCollections.this.colRemoveAt(this.mIndex);
                this.mIndex--;
                this.mEnd--;
                this.mEntryValid = false;
                return;
            }
            throw new IllegalStateException();
        }

        public K getKey() {
            if (this.mEntryValid) {
                return MapCollections.this.colGetEntry(this.mIndex, 0);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public V getValue() {
            if (this.mEntryValid) {
                return MapCollections.this.colGetEntry(this.mIndex, 1);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public V setValue(V v) {
            if (this.mEntryValid) {
                return MapCollections.this.colSetValue(this.mIndex, v);
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final boolean equals(Object obj) {
            if (this.mEntryValid) {
                boolean z = false;
                if (!(obj instanceof Entry)) {
                    return z;
                }
                Entry entry = (Entry) obj;
                boolean z2 = true;
                if (ContainerHelpers.equal(entry.getKey(), MapCollections.this.colGetEntry(this.mIndex, z)) && ContainerHelpers.equal(entry.getValue(), MapCollections.this.colGetEntry(this.mIndex, z2))) {
                    z = z2;
                }
                return z;
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final int hashCode() {
            if (this.mEntryValid) {
                int i;
                int i2 = 0;
                Object colGetEntry = MapCollections.this.colGetEntry(this.mIndex, i2);
                Object colGetEntry2 = MapCollections.this.colGetEntry(this.mIndex, 1);
                if (colGetEntry == null) {
                    i = i2;
                } else {
                    i = colGetEntry.hashCode();
                }
                if (colGetEntry2 != null) {
                    i2 = colGetEntry2.hashCode();
                }
                return i ^ i2;
            }
            throw new IllegalStateException("This container does not support retaining Map.Entry objects");
        }

        public final String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getKey());
            stringBuilder.append("=");
            stringBuilder.append(getValue());
            return stringBuilder.toString();
        }
    }

    final class ValuesCollection implements Collection<V> {
        ValuesCollection() {
        }

        public boolean add(V v) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends V> collection) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            MapCollections.this.colClear();
        }

        public boolean contains(Object obj) {
            return MapCollections.this.colIndexOfValue(obj) >= 0;
        }

        public boolean containsAll(Collection<?> collection) {
            for (Object contains : collection) {
                if (!contains(contains)) {
                    return false;
                }
            }
            return true;
        }

        public boolean isEmpty() {
            return MapCollections.this.colGetSize() == 0;
        }

        public Iterator<V> iterator() {
            return new ArrayIterator(1);
        }

        public boolean remove(Object obj) {
            int colIndexOfValue = MapCollections.this.colIndexOfValue(obj);
            if (colIndexOfValue < 0) {
                return false;
            }
            MapCollections.this.colRemoveAt(colIndexOfValue);
            return true;
        }

        public boolean removeAll(Collection<?> collection) {
            int colGetSize = MapCollections.this.colGetSize();
            int i = 0;
            boolean z = i;
            while (i < colGetSize) {
                boolean z2 = true;
                if (collection.contains(MapCollections.this.colGetEntry(i, z2))) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    colGetSize--;
                    z = z2;
                }
                i += z2;
            }
            return z;
        }

        public boolean retainAll(Collection<?> collection) {
            int colGetSize = MapCollections.this.colGetSize();
            int i = 0;
            boolean z = i;
            while (i < colGetSize) {
                boolean z2 = true;
                if (!collection.contains(MapCollections.this.colGetEntry(i, z2))) {
                    MapCollections.this.colRemoveAt(i);
                    i--;
                    colGetSize--;
                    z = z2;
                }
                i += z2;
            }
            return z;
        }

        public int size() {
            return MapCollections.this.colGetSize();
        }

        public Object[] toArray() {
            return MapCollections.this.toArrayHelper(1);
        }

        public <T> T[] toArray(T[] tArr) {
            return MapCollections.this.toArrayHelper(tArr, 1);
        }
    }

    protected abstract void colClear();

    protected abstract Object colGetEntry(int i, int i2);

    protected abstract Map<K, V> colGetMap();

    protected abstract int colGetSize();

    protected abstract int colIndexOfKey(Object obj);

    protected abstract int colIndexOfValue(Object obj);

    protected abstract void colPut(K k, V v);

    protected abstract void colRemoveAt(int i);

    protected abstract V colSetValue(int i, V v);

    MapCollections() {
    }

    public static <K, V> boolean containsAllHelper(Map<K, V> map, Collection<?> collection) {
        for (Object containsKey : collection) {
            if (!map.containsKey(containsKey)) {
                return false;
            }
        }
        return true;
    }

    public static <K, V> boolean removeAllHelper(Map<K, V> map, Collection<?> collection) {
        int size = map.size();
        for (Object remove : collection) {
            map.remove(remove);
        }
        return size != map.size();
    }

    public static <K, V> boolean retainAllHelper(Map<K, V> map, Collection<?> collection) {
        int size = map.size();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            if (!collection.contains(it.next())) {
                it.remove();
            }
        }
        return size != map.size();
    }

    public Object[] toArrayHelper(int i) {
        int colGetSize = colGetSize();
        Object[] objArr = new Object[colGetSize];
        for (int i2 = 0; i2 < colGetSize; i2++) {
            objArr[i2] = colGetEntry(i2, i);
        }
        return objArr;
    }

    public <T> T[] toArrayHelper(T[] tArr, int i) {
        int colGetSize = colGetSize();
        if (tArr.length < colGetSize) {
            tArr = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), colGetSize);
        }
        for (int i2 = 0; i2 < colGetSize; i2++) {
            tArr[i2] = colGetEntry(i2, i);
        }
        if (tArr.length > colGetSize) {
            tArr[colGetSize] = null;
        }
        return tArr;
    }

    public static <T> boolean equalsSetHelper(Set<T> set, Object obj) {
        boolean z = true;
        if (set == obj) {
            return z;
        }
        boolean z2 = false;
        if (!(obj instanceof Set)) {
            return z2;
        }
        Set set2 = (Set) obj;
        try {
            if (!(set.size() == set2.size() && set.containsAll(set2))) {
                z = z2;
            }
            return z;
        } catch (NullPointerException unused) {
            return z2;
        } catch (ClassCastException unused2) {
            return z2;
        }
    }

    public Set<Entry<K, V>> getEntrySet() {
        if (this.mEntrySet == null) {
            this.mEntrySet = new EntrySet();
        }
        return this.mEntrySet;
    }

    public Set<K> getKeySet() {
        if (this.mKeySet == null) {
            this.mKeySet = new KeySet();
        }
        return this.mKeySet;
    }

    public Collection<V> getValues() {
        if (this.mValues == null) {
            this.mValues = new ValuesCollection();
        }
        return this.mValues;
    }
}
