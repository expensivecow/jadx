package android.support.v4.util;

public final class CircularArray<E> {
    private int mCapacityBitmask;
    private E[] mElements;
    private int mHead;
    private int mTail;

    private void doubleCapacity() {
        int length = this.mElements.length;
        int i = length - this.mHead;
        int i2 = length << 1;
        if (i2 < 0) {
            throw new RuntimeException("Max array capacity exceeded");
        }
        Object obj = new Object[i2];
        int i3 = 0;
        System.arraycopy(this.mElements, this.mHead, obj, i3, i);
        System.arraycopy(this.mElements, i3, obj, i, this.mHead);
        this.mElements = (Object[]) obj;
        this.mHead = i3;
        this.mTail = length;
        this.mCapacityBitmask = i2 - 1;
    }

    public CircularArray() {
        this(8);
    }

    public CircularArray(int i) {
        int i2 = 1;
        if (i < i2) {
            throw new IllegalArgumentException("capacity must be >= 1");
        } else if (i > 1073741824) {
            throw new IllegalArgumentException("capacity must be <= 2^30");
        } else {
            if (Integer.bitCount(i) != i2) {
                i = Integer.highestOneBit(i - 1) << i2;
            }
            this.mCapacityBitmask = i - 1;
            this.mElements = new Object[i];
        }
    }

    public void addFirst(E e) {
        this.mHead = (this.mHead - 1) & this.mCapacityBitmask;
        this.mElements[this.mHead] = e;
        if (this.mHead == this.mTail) {
            doubleCapacity();
        }
    }

    public void addLast(E e) {
        this.mElements[this.mTail] = e;
        this.mTail = (this.mTail + 1) & this.mCapacityBitmask;
        if (this.mTail == this.mHead) {
            doubleCapacity();
        }
    }

    public E popFirst() {
        if (this.mHead == this.mTail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        E e = this.mElements[this.mHead];
        this.mElements[this.mHead] = null;
        this.mHead = (this.mHead + 1) & this.mCapacityBitmask;
        return e;
    }

    public E popLast() {
        if (this.mHead == this.mTail) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i = (this.mTail - 1) & this.mCapacityBitmask;
        E e = this.mElements[i];
        this.mElements[i] = null;
        this.mTail = i;
        return e;
    }

    public void clear() {
        removeFromStart(size());
    }

    public void removeFromStart(int i) {
        if (i > 0) {
            if (i > size()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            Object obj;
            int length = this.mElements.length;
            if (i < length - this.mHead) {
                length = this.mHead + i;
            }
            int i2 = this.mHead;
            while (true) {
                obj = null;
                if (i2 >= length) {
                    break;
                }
                this.mElements[i2] = obj;
                i2++;
            }
            length -= this.mHead;
            i -= length;
            this.mHead = this.mCapacityBitmask & (this.mHead + length);
            if (i > 0) {
                for (length = 0; length < i; length++) {
                    this.mElements[length] = obj;
                }
                this.mHead = i;
            }
        }
    }

    public void removeFromEnd(int i) {
        if (i > 0) {
            if (i > size()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            Object obj;
            int i2 = 0;
            if (i < this.mTail) {
                i2 = this.mTail - i;
            }
            int i3 = i2;
            while (true) {
                obj = null;
                if (i3 >= this.mTail) {
                    break;
                }
                this.mElements[i3] = obj;
                i3++;
            }
            i3 = this.mTail - i2;
            i -= i3;
            this.mTail -= i3;
            if (i > 0) {
                this.mTail = this.mElements.length;
                i2 = this.mTail - i;
                for (i = i2; i < this.mTail; i++) {
                    this.mElements[i] = obj;
                }
                this.mTail = i2;
            }
        }
    }

    public E getFirst() {
        if (this.mHead != this.mTail) {
            return this.mElements[this.mHead];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public E getLast() {
        if (this.mHead != this.mTail) {
            return this.mElements[(this.mTail - 1) & this.mCapacityBitmask];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    public E get(int i) {
        if (i < 0 || i >= size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.mElements[this.mCapacityBitmask & (this.mHead + i)];
    }

    public int size() {
        return (this.mTail - this.mHead) & this.mCapacityBitmask;
    }

    public boolean isEmpty() {
        return this.mHead == this.mTail;
    }
}
