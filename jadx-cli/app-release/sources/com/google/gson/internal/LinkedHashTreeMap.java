package com.google.gson.internal;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public final class LinkedHashTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
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
    int size;
    Node<K, V>[] table;
    int threshold;

    static final class AvlBuilder<K, V> {
        private int leavesSkipped;
        private int leavesToSkip;
        private int size;
        private Node<K, V> stack;

        AvlBuilder() {
        }

        void reset(int i) {
            this.leavesToSkip = ((Integer.highestOneBit(i) * 2) - 1) - i;
            i = 0;
            this.size = i;
            this.leavesSkipped = i;
            this.stack = null;
        }

        void add(Node<K, V> node) {
            Node node2 = null;
            node.right = node2;
            node.parent = node2;
            node.left = node2;
            int i = 1;
            node.height = i;
            if (this.leavesToSkip > 0 && (this.size & i) == 0) {
                this.size += i;
                this.leavesToSkip -= i;
                this.leavesSkipped += i;
            }
            node.parent = this.stack;
            this.stack = node;
            this.size += i;
            if (this.leavesToSkip > 0 && (this.size & i) == 0) {
                this.size += i;
                this.leavesToSkip -= i;
                this.leavesSkipped += i;
            }
            int i2 = 4;
            while (true) {
                int i3 = i2 - 1;
                if ((this.size & i3) == i3) {
                    Node node3;
                    Node node4;
                    if (this.leavesSkipped == 0) {
                        node3 = this.stack;
                        Node node5 = node3.parent;
                        node4 = node5.parent;
                        node5.parent = node4.parent;
                        this.stack = node5;
                        node5.left = node4;
                        node5.right = node3;
                        node5.height = node3.height + i;
                        node4.parent = node5;
                        node3.parent = node5;
                    } else {
                        i3 = 0;
                        if (this.leavesSkipped == i) {
                            node3 = this.stack;
                            node4 = node3.parent;
                            this.stack = node4;
                            node4.right = node3;
                            node4.height = node3.height + i;
                            node3.parent = node4;
                            this.leavesSkipped = i3;
                        } else if (this.leavesSkipped == 2) {
                            this.leavesSkipped = i3;
                        }
                    }
                    i2 *= 2;
                } else {
                    return;
                }
            }
        }

        Node<K, V> root() {
            Node<K, V> node = this.stack;
            if (node.parent == null) {
                return node;
            }
            throw new IllegalStateException();
        }
    }

    static class AvlIterator<K, V> {
        private Node<K, V> stackTop;

        AvlIterator() {
        }

        void reset(Node<K, V> node) {
            Node node2 = null;
            while (true) {
                Node node3 = node2;
                Node<K, V> node22 = node;
                Node node4 = node3;
                if (node22 != null) {
                    node22.parent = node4;
                    node = node22.left;
                } else {
                    this.stackTop = node4;
                    return;
                }
            }
        }

        public Node<K, V> next() {
            Node<K, V> node = this.stackTop;
            Node<K, V> node2 = null;
            if (node == null) {
                return node2;
            }
            Node node3 = node.parent;
            node.parent = node2;
            Node node4 = node.right;
            while (true) {
                Node node5 = node3;
                node3 = node4;
                node4 = node5;
                if (node3 != null) {
                    node3.parent = node4;
                    node4 = node3.left;
                } else {
                    this.stackTop = node4;
                    return node;
                }
            }
        }
    }

    final class EntrySet extends AbstractSet<Entry<K, V>> {
        EntrySet() {
        }

        public int size() {
            return LinkedHashTreeMap.this.size;
        }

        public Iterator<Entry<K, V>> iterator() {
            return new LinkedTreeMapIterator<Entry<K, V>>() {
                {
                    LinkedHashTreeMap linkedHashTreeMap = LinkedHashTreeMap.this;
                    AnonymousClass1 anonymousClass1 = null;
                }

                public Entry<K, V> next() {
                    return nextNode();
                }
            };
        }

        public boolean contains(Object obj) {
            return (obj instanceof Entry) && LinkedHashTreeMap.this.findByEntry((Entry) obj) != null;
        }

        public boolean remove(Object obj) {
            boolean z = false;
            if (!(obj instanceof Entry)) {
                return z;
            }
            Node findByEntry = LinkedHashTreeMap.this.findByEntry((Entry) obj);
            if (findByEntry == null) {
                return z;
            }
            z = true;
            LinkedHashTreeMap.this.removeInternal(findByEntry, z);
            return z;
        }

        public void clear() {
            LinkedHashTreeMap.this.clear();
        }
    }

    final class KeySet extends AbstractSet<K> {
        KeySet() {
        }

        public int size() {
            return LinkedHashTreeMap.this.size;
        }

        public Iterator<K> iterator() {
            return new LinkedTreeMapIterator<K>() {
                {
                    LinkedHashTreeMap linkedHashTreeMap = LinkedHashTreeMap.this;
                    AnonymousClass1 anonymousClass1 = null;
                }

                public K next() {
                    return nextNode().key;
                }
            };
        }

        public boolean contains(Object obj) {
            return LinkedHashTreeMap.this.containsKey(obj);
        }

        public boolean remove(Object obj) {
            return LinkedHashTreeMap.this.removeInternalByKey(obj) != null;
        }

        public void clear() {
            LinkedHashTreeMap.this.clear();
        }
    }

    private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
        int expectedModCount;
        Node<K, V> lastReturned;
        Node<K, V> next;

        private LinkedTreeMapIterator() {
            this.next = LinkedHashTreeMap.this.header.next;
            this.lastReturned = null;
            this.expectedModCount = LinkedHashTreeMap.this.modCount;
        }

        /* synthetic */ LinkedTreeMapIterator(LinkedHashTreeMap linkedHashTreeMap, AnonymousClass1 anonymousClass1) {
            this();
        }

        public final boolean hasNext() {
            return this.next != LinkedHashTreeMap.this.header;
        }

        final Node<K, V> nextNode() {
            Node<K, V> node = this.next;
            if (node == LinkedHashTreeMap.this.header) {
                throw new NoSuchElementException();
            } else if (LinkedHashTreeMap.this.modCount != this.expectedModCount) {
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
            LinkedHashTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedHashTreeMap.this.modCount;
        }
    }

    static final class Node<K, V> implements Entry<K, V> {
        final int hash;
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
            this.hash = -1;
            this.prev = this;
            this.next = this;
        }

        Node(Node<K, V> node, K k, int i, Node<K, V> node2, Node<K, V> node3) {
            this.parent = node;
            this.key = k;
            this.hash = i;
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

    private static int secondaryHash(int i) {
        i ^= (i >>> 20) ^ (i >>> 12);
        return (i >>> 4) ^ ((i >>> 7) ^ i);
    }

    public LinkedHashTreeMap() {
        this(NATURAL_ORDER);
    }

    public LinkedHashTreeMap(Comparator<? super K> comparator) {
        Comparator comparator2;
        int i = 0;
        this.size = i;
        this.modCount = i;
        if (comparator2 == null) {
            comparator2 = NATURAL_ORDER;
        }
        this.comparator = comparator2;
        this.header = new Node();
        this.table = new Node[16];
        this.threshold = (this.table.length / 2) + (this.table.length / 4);
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
        Node node = null;
        Arrays.fill(this.table, node);
        this.size = 0;
        this.modCount++;
        Node node2 = this.header;
        Node node3 = node2.next;
        while (node3 != node2) {
            Node node4 = node3.next;
            node3.prev = node;
            node3.next = node;
            node3 = node4;
        }
        node2.prev = node2;
        node2.next = node2;
    }

    public V remove(Object obj) {
        Node removeInternalByKey = removeInternalByKey(obj);
        return removeInternalByKey != null ? removeInternalByKey.value : null;
    }

    com.google.gson.internal.LinkedHashTreeMap.Node<K, V> find(K r14, boolean r15) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r15_4 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>) in PHI: PHI: (r15_6 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>) = (r15_4 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>), (r15_5 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>) binds: {(r15_4 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>)=B:28:0x006e, (r15_5 com.google.gson.internal.LinkedHashTreeMap$Node<K, V>)=B:33:0x008c}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:78)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
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
        r13 = this;
        r0 = r13.comparator;
        r1 = r13.table;
        r2 = r14.hashCode();
        r6 = secondaryHash(r2);
        r2 = 0;
        r9 = 1;
        r3 = r1.length;
        r3 = r3 - r9;
        r10 = r6 & r3;
        r3 = r1[r10];
        if (r3 == 0) goto L_0x003d;
    L_0x0016:
        r4 = NATURAL_ORDER;
        if (r0 != r4) goto L_0x001e;
    L_0x001a:
        r4 = r14;
        r4 = (java.lang.Comparable) r4;
        goto L_0x001f;
    L_0x001e:
        r4 = r2;
    L_0x001f:
        if (r4 == 0) goto L_0x0028;
    L_0x0021:
        r5 = r3.key;
        r5 = r4.compareTo(r5);
        goto L_0x002e;
    L_0x0028:
        r5 = r3.key;
        r5 = r0.compare(r14, r5);
    L_0x002e:
        if (r5 != 0) goto L_0x0031;
    L_0x0030:
        return r3;
    L_0x0031:
        if (r5 >= 0) goto L_0x0036;
    L_0x0033:
        r7 = r3.left;
        goto L_0x0038;
    L_0x0036:
        r7 = r3.right;
    L_0x0038:
        if (r7 != 0) goto L_0x003b;
    L_0x003a:
        goto L_0x003e;
    L_0x003b:
        r3 = r7;
        goto L_0x001f;
    L_0x003d:
        r5 = 0;
    L_0x003e:
        r11 = r3;
        r12 = r5;
        if (r15 != 0) goto L_0x0043;
    L_0x0042:
        return r2;
    L_0x0043:
        r7 = r13.header;
        if (r11 != 0) goto L_0x007b;
    L_0x0047:
        r15 = NATURAL_ORDER;
        if (r0 != r15) goto L_0x006e;
    L_0x004b:
        r15 = r14 instanceof java.lang.Comparable;
        if (r15 != 0) goto L_0x006e;
    L_0x004f:
        r15 = new java.lang.ClassCastException;
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r14 = r14.getClass();
        r14 = r14.getName();
        r0.append(r14);
        r14 = " is not Comparable";
        r0.append(r14);
        r14 = r0.toString();
        r15.<init>(r14);
        throw r15;
    L_0x006e:
        r15 = new com.google.gson.internal.LinkedHashTreeMap$Node;
        r8 = r7.prev;
        r3 = r15;
        r4 = r11;
        r5 = r14;
        r3.<init>(r4, r5, r6, r7, r8);
        r1[r10] = r15;
        goto L_0x008f;
    L_0x007b:
        r15 = new com.google.gson.internal.LinkedHashTreeMap$Node;
        r8 = r7.prev;
        r3 = r15;
        r4 = r11;
        r5 = r14;
        r3.<init>(r4, r5, r6, r7, r8);
        if (r12 >= 0) goto L_0x008a;
    L_0x0087:
        r11.left = r15;
        goto L_0x008c;
    L_0x008a:
        r11.right = r15;
    L_0x008c:
        r13.rebalance(r11, r9);
    L_0x008f:
        r14 = r13.size;
        r0 = r14 + 1;
        r13.size = r0;
        r0 = r13.threshold;
        if (r14 <= r0) goto L_0x009c;
    L_0x0099:
        r13.doubleCapacity();
    L_0x009c:
        r14 = r13.modCount;
        r14 = r14 + r9;
        r13.modCount = r14;
        return r15;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.LinkedHashTreeMap.find(java.lang.Object, boolean):com.google.gson.internal.LinkedHashTreeMap$Node<K, V>");
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
        Node node2 = null;
        if (z) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = node2;
            node.next = node2;
        }
        Node node3 = node.left;
        Node node4 = node.right;
        Node node5 = node.parent;
        int i = 0;
        if (node3 == null || node4 == null) {
            if (node3 != null) {
                replaceInParent(node, node3);
                node.left = node2;
            } else if (node4 != null) {
                replaceInParent(node, node4);
                node.right = node2;
            } else {
                replaceInParent(node, node2);
            }
            rebalance(node5, i);
            this.size--;
            this.modCount++;
            return;
        }
        int i2;
        node3 = node3.height > node4.height ? node3.last() : node4.first();
        removeInternal(node3, i);
        node4 = node.left;
        if (node4 != null) {
            i2 = node4.height;
            node3.left = node4;
            node4.parent = node3;
            node.left = node2;
        } else {
            i2 = i;
        }
        node4 = node.right;
        if (node4 != null) {
            i = node4.height;
            node3.right = node4;
            node4.parent = node3;
            node.right = node2;
        }
        node3.height = Math.max(i2, i) + 1;
        replaceInParent(node, node3);
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
            this.table[node.hash & (this.table.length - 1)] = node2;
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

    private void doubleCapacity() {
        this.table = doubleCapacity(this.table);
        this.threshold = (this.table.length / 2) + (this.table.length / 4);
    }

    static <K, V> Node<K, V>[] doubleCapacity(Node<K, V>[] nodeArr) {
        int i = 0;
        int length = nodeArr.length;
        Node<K, V>[] nodeArr2 = new Node[(length * 2)];
        AvlIterator avlIterator = new AvlIterator();
        AvlBuilder avlBuilder = new AvlBuilder();
        AvlBuilder avlBuilder2 = new AvlBuilder();
        for (int i2 = i; i2 < length; i2++) {
            Node node = nodeArr[i2];
            if (node != null) {
                avlIterator.reset(node);
                int i3 = i;
                int i4 = i3;
                while (true) {
                    Node next = avlIterator.next();
                    if (next == null) {
                        break;
                    } else if ((next.hash & length) == 0) {
                        i3++;
                    } else {
                        i4++;
                    }
                }
                avlBuilder.reset(i3);
                avlBuilder2.reset(i4);
                avlIterator.reset(node);
                while (true) {
                    node = avlIterator.next();
                    if (node == null) {
                        break;
                    } else if ((node.hash & length) == 0) {
                        avlBuilder.add(node);
                    } else {
                        avlBuilder2.add(node);
                    }
                }
                node = null;
                nodeArr2[i2] = i3 > 0 ? avlBuilder.root() : node;
                i3 = i2 + length;
                if (i4 > 0) {
                    node = avlBuilder2.root();
                }
                nodeArr2[i3] = node;
            }
        }
        return nodeArr2;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new LinkedHashMap(this);
    }
}
