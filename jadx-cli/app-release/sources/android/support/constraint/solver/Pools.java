package android.support.constraint.solver;

final class Pools {
    private static final boolean DEBUG = false;

    interface Pool<T> {
        T acquire();

        boolean release(T t);

        void releaseAll(T[] tArr, int i);
    }

    static class SimplePool<T> implements Pool<T> {
        private final Object[] mPool;
        private int mPoolSize;

        SimplePool(int i) {
            if (i <= 0) {
                throw new IllegalArgumentException("The max pool size must be > 0");
            }
            this.mPool = new Object[i];
        }

        public T acquire() {
            T t = null;
            if (this.mPoolSize <= 0) {
                return t;
            }
            int i = this.mPoolSize - 1;
            T t2 = this.mPool[i];
            this.mPool[i] = t;
            this.mPoolSize--;
            return t2;
        }

        public boolean release(T t) {
            if (this.mPoolSize >= this.mPool.length) {
                return false;
            }
            this.mPool[this.mPoolSize] = t;
            boolean z = true;
            this.mPoolSize += z;
            return z;
        }

        public void releaseAll(T[] tArr, int i) {
            if (i > tArr.length) {
                i = tArr.length;
            }
            for (int i2 = 0; i2 < i; i2++) {
                T t = tArr[i2];
                if (this.mPoolSize < this.mPool.length) {
                    this.mPool[this.mPoolSize] = t;
                    this.mPoolSize++;
                }
            }
        }

        private boolean isInPool(T t) {
            boolean z = false;
            for (int i = z; i < this.mPoolSize; i++) {
                if (this.mPool[i] == t) {
                    return true;
                }
            }
            return z;
        }
    }

    private Pools() {
    }
}
