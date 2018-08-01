package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
        public int compare(Comparable comparable, Comparable comparable2) {
            return comparable.compareTo(comparable2);
        }
    };
    Comparator<? super K> comparator;
    private EntrySet entrySet;
    final Node<K, V> header;
    private KeySet keySet;
    int modCount;
    Node<K, V> root;
    int size;

    class EntrySet extends AbstractSet<Entry<K, V>> {
        EntrySet() {
        }

        public int size() {
            return LinkedTreeMap.this.size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new LinkedTreeMapIterator<Entry<K, V>>() {
                {
                    LinkedTreeMap linkedTreeMap = LinkedTreeMap.this;
                    AnonymousClass1 anonymousClass1 = null;
                }

                public Entry<K, V> next() {
                    return nextNode();
                }
            };
        }

        public boolean contains(Object obj) {
            return (obj instanceof Entry) && LinkedTreeMap.this.findByEntry((Entry) obj) != null;
        }

        public boolean remove(Object obj) {
            boolean z = false;
            if (!(obj instanceof Entry)) {
                return z;
            }
            Node findByEntry = LinkedTreeMap.this.findByEntry((Entry) obj);
            if (findByEntry == null) {
                return z;
            }
            z = true;
            LinkedTreeMap.this.removeInternal(findByEntry, z);
            return z;
        }

        public void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        public int size() {
            return LinkedTreeMap.this.size;
        }

        public Iterator<K> iterator() {
            return new LinkedTreeMapIterator<K>() {
                {
                    LinkedTreeMap linkedTreeMap = LinkedTreeMap.this;
                    AnonymousClass1 anonymousClass1 = null;
                }

                public K next() {
                    return nextNode().key;
                }
            };
        }

        public boolean contains(Object obj) {
            return LinkedTreeMap.this.containsKey(obj);
        }

        public boolean remove(Object obj) {
            return LinkedTreeMap.this.removeInternalByKey(obj) != null;
        }

        public void clear() {
            LinkedTreeMap.this.clear();
        }
    }

    private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
        int expectedModCount;
        Node<K, V> lastReturned;
        Node<K, V> next;

        private LinkedTreeMapIterator() {
            this.next = LinkedTreeMap.this.header.next;
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
        }

        /* synthetic */ LinkedTreeMapIterator(LinkedTreeMap linkedTreeMap, AnonymousClass1 anonymousClass1) {
            this();
        }

        public final boolean hasNext() {
            return this.next != LinkedTreeMap.this.header;
        }

        final Node<K, V> nextNode() {
            Node<K, V> node = this.next;
            if (node == LinkedTreeMap.this.header) {
                throw new NoSuchElementException();
            } else if (LinkedTreeMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            } else {
                this.next = node.next;
                this.lastReturned = node;
                return node;
            }
        }

        public final void remove() {
            if (this.lastReturned == null) {
                throw new IllegalStateException();
            }
            LinkedTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
        }
    }

    static final class Node<K, V> implements Entry<K, V> {
        int height;
        final K key;
        Node<K, V> left;
        Node<K, V> next;
        Node<K, V> parent;
        Node<K, V> prev;
        Node<K, V> right;
        V value;

        Node() {
            this.key = null;
            this.prev = this;
            this.next = this;
        }

        Node(Node<K, V> node, K k, Node<K, V> node2, Node<K, V> node3) {
            this.parent = node;
            this.key = k;
            this.height = 1;
            this.next = node2;
            this.prev = node3;
            node3.next = this;
            node2.prev = this;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V v) {
            V v2 = this.value;
            this.value = v;
            return v2;
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!(obj instanceof Entry)) {
                return z;
            }
            Entry entry = (Entry) obj;
            if (this.key != null ? !this.key.equals(entry.getKey()) : entry.getKey() != null) {
                if (this.value != null ? !this.value.equals(entry.getValue()) : entry.getValue() != null) {
                    z = true;
                }
            }
            return z;
        }

        public int hashCode() {
            int i = 0;
            int hashCode = this.key == null ? i : this.key.hashCode();
            if (this.value != null) {
                i = this.value.hashCode();
            }
            return hashCode ^ i;
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.key);
            stringBuilder.append("=");
            stringBuilder.append(this.value);
            return stringBuilder.toString();
        }

        public Node<K, V> first() {
            Node<K, V> node = this;
            for (Node<K, V> node2 = this.left; node2 != null; node2 = node2.left) {
                node = node2;
            }
            return node;
        }

        public Node<K, V> last() {
            Node<K, V> node = this;
            for (Node<K, V> node2 = this.right; node2 != null; node2 = node2.right) {
                node = node2;
            }
            return node;
        }
    }

    public LinkedTreeMap() {
        this(NATURAL_ORDER);
    }

    public LinkedTreeMap(Comparator<? super K> comparator) {
        Comparator comparator2;
        int i = 0;
        this.size = i;
        this.modCount = i;
        this.header = new Node();
        if (comparator2 == null) {
            comparator2 = NATURAL_ORDER;
        }
        this.comparator = comparator2;
    }

    public int size() {
        return this.size;
    }

    public V get(Object obj) {
        Node findByObject = findByObject(obj);
        return findByObject != null ? findByObject.value : null;
    }

    public boolean containsKey(Object obj) {
        return findByObject(obj) != null;
    }

    public V put(K k, V v) {
        if (k == null) {
            throw new NullPointerException("key == null");
        }
        Node find = find(k, true);
        V v2 = find.value;
        find.value = v;
        return v2;
    }

    public void clear() {
        this.root = null;
        this.size = 0;
        this.modCount++;
        Node node = this.header;
        node.prev = node;
        node.next = node;
    }

    public V remove(Object obj) {
        Node removeInternalByKey = removeInternalByKey(obj);
        return removeInternalByKey != null ? removeInternalByKey.value : null;
    }

    Node<K, V> find(K k, boolean z) {
        int compareTo;
        Comparator comparator = this.comparator;
        Node node = this.root;
        Node<K, V> node2 = null;
        if (node != null) {
            Comparable comparable = comparator == NATURAL_ORDER ? (Comparable) k : node2;
            while (true) {
                compareTo = comparable != null ? comparable.compareTo(node.key) : comparator.compare(k, node.key);
                if (compareTo == 0) {
                    return node;
                }
                Node node3;
                if (compareTo < 0) {
                    node3 = node.left;
                } else {
                    node3 = node.right;
                }
                if (node3 == null) {
                    break;
                }
                node = node3;
            }
        } else {
            compareTo = 0;
        }
        if (!z) {
            return node2;
        }
        Node<K, V> node4;
        Node node5 = this.header;
        boolean z2 = true;
        if (node != null) {
            node4 = new Node(node, k, node5, node5.prev);
            if (compareTo < 0) {
                node.left = node4;
            } else {
                node.right = node4;
            }
            rebalance(node, z2);
        } else if (comparator != NATURAL_ORDER || (k instanceof Comparable)) {
            node4 = new Node(node, k, node5, node5.prev);
            this.root = node4;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(k.getClass().getName());
            stringBuilder.append(" is not Comparable");
            throw new ClassCastException(stringBuilder.toString());
        }
        this.size += z2;
        this.modCount += z2;
        return node4;
    }

    Node<K, V> findByObject(Object obj) {
        Node<K, V> find;
        Node<K, V> node = null;
        if (obj != null) {
            try {
                find = find(obj, false);
            } catch (ClassCastException unused) {
                return node;
            }
        }
        find = node;
        return find;
    }

    Node<K, V> findByEntry(Entry<?, ?> entry) {
        Node<K, V> findByObject = findByObject(entry.getKey());
        Object obj = (findByObject == null || !equal(findByObject.value, entry.getValue())) ? null : 1;
        return obj != null ? findByObject : null;
    }

    private boolean equal(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    void removeInternal(Node<K, V> node, boolean z) {
        if (z) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        Node node2 = node.left;
        Node node3 = node.right;
        Node node4 = node.parent;
        int i = 0;
        Node node5 = null;
        if (node2 == null || node3 == null) {
            if (node2 != null) {
                replaceInParent(node, node2);
                node.left = node5;
            } else if (node3 != null) {
                replaceInParent(node, node3);
                node.right = node5;
            } else {
                replaceInParent(node, node5);
            }
            rebalance(node4, i);
            this.size--;
            this.modCount++;
            return;
        }
        int i2;
        node2 = node2.height > node3.height ? node2.last() : node3.first();
        removeInternal(node2, i);
        node3 = node.left;
        if (node3 != null) {
            i2 = node3.height;
            node2.left = node3;
            node3.parent = node2;
            node.left = node5;
        } else {
            i2 = i;
        }
        node3 = node.right;
        if (node3 != null) {
            i = node3.height;
            node2.right = node3;
            node3.parent = node2;
            node.right = node5;
        }
        node2.height = Math.max(i2, i) + 1;
        replaceInParent(node, node2);
    }

    Node<K, V> removeInternalByKey(Object obj) {
        Node<K, V> findByObject = findByObject(obj);
        if (findByObject != null) {
            removeInternal(findByObject, true);
        }
        return findByObject;
    }

    private void replaceInParent(Node<K, V> node, Node<K, V> node2) {
        Node node3 = node.parent;
        node.parent = null;
        if (node2 != null) {
            node2.parent = node3;
        }
        if (node3 == null) {
            this.root = node2;
        } else if (node3.left == node) {
            node3.left = node2;
        } else {
            node3.right = node2;
        }
    }

    private void rebalance(Node<K, V> node, boolean z) {
        Node node2;
        while (node2 != null) {
            Node node3 = node2.left;
            Node node4 = node2.right;
            int i = 0;
            int i2 = node3 != null ? node3.height : i;
            int i3 = node4 != null ? node4.height : i;
            int i4 = i2 - i3;
            Node node5;
            if (i4 == -2) {
                node3 = node4.left;
                node5 = node4.right;
                i2 = node5 != null ? node5.height : i;
                if (node3 != null) {
                    i = node3.height;
                }
                i -= i2;
                if (i == -1 || (i == 0 && !z)) {
                    rotateLeft(node2);
                } else {
                    rotateRight(node4);
                    rotateLeft(node2);
                }
                if (z) {
                    return;
                }
            } else {
                int i5 = 1;
                if (i4 == 2) {
                    node4 = node3.left;
                    node5 = node3.right;
                    i2 = node5 != null ? node5.height : i;
                    if (node4 != null) {
                        i = node4.height;
                    }
                    i -= i2;
                    if (i == i5 || (i == 0 && !z)) {
                        rotateRight(node2);
                    } else {
                        rotateLeft(node3);
                        rotateRight(node2);
                    }
                    if (z) {
                        return;
                    }
                } else if (i4 == 0) {
                    node2.height = i2 + 1;
                    if (z) {
                        return;
                    }
                } else {
                    node2.height = Math.max(i2, i3) + i5;
                    if (!z) {
                        return;
                    }
                }
            }
            node2 = node2.parent;
        }
    }

    private void rotateLeft(Node<K, V> node) {
        Node node2 = node.left;
        Node node3 = node.right;
        Node node4 = node3.left;
        Node node5 = node3.right;
        node.right = node4;
        if (node4 != null) {
            node4.parent = node;
        }
        replaceInParent(node, node3);
        node3.left = node;
        node.parent = node3;
        int i = 0;
        node.height = Math.max(node2 != null ? node2.height : i, node4 != null ? node4.height : i) + 1;
        int i2 = node.height;
        if (node5 != null) {
            i = node5.height;
        }
        node3.height = Math.max(i2, i) + 1;
    }

    private void rotateRight(Node<K, V> node) {
        Node node2 = node.left;
        Node node3 = node.right;
        Node node4 = node2.left;
        Node node5 = node2.right;
        node.left = node5;
        if (node5 != null) {
            node5.parent = node;
        }
        replaceInParent(node, node2);
        node2.right = node;
        node.parent = node2;
        int i = 0;
        node.height = Math.max(node3 != null ? node3.height : i, node5 != null ? node5.height : i) + 1;
        int i2 = node.height;
        if (node4 != null) {
            i = node4.height;
        }
        node2.height = Math.max(i2, i) + 1;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = this.entrySet;
        if (set != null) {
            return set;
        }
        Set entrySet = new EntrySet();
        this.entrySet = entrySet;
        return entrySet;
    }

    public Set<K> keySet() {
        Set<K> set = this.keySet;
        if (set != null) {
            return set;
        }
        Set keySet = new KeySet();
        this.keySet = keySet;
        return keySet;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap(this);
    }
}
