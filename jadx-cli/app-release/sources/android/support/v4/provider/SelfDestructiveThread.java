package android.support.v4.provider;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.GuardedBy;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@RestrictTo({Scope.LIBRARY_GROUP})
public class SelfDestructiveThread {
    private static final int MSG_DESTRUCTION = 0;
    private static final int MSG_INVOKE_RUNNABLE = 1;
    private Callback mCallback = new Callback() {
        public boolean handleMessage(Message message) {
            boolean z = true;
            switch (message.what) {
                case 0:
                    SelfDestructiveThread.this.onDestruction();
                    return z;
                case 1:
                    SelfDestructiveThread.this.onInvokeRunnable((Runnable) message.obj);
                    return z;
                default:
                    return z;
            }
        }
    };
    private final int mDestructAfterMillisec;
    @GuardedBy("mLock")
    private int mGeneration;
    @GuardedBy("mLock")
    private Handler mHandler;
    private final Object mLock = new Object();
    private final int mPriority;
    @GuardedBy("mLock")
    private HandlerThread mThread;
    private final String mThreadName;

    public interface ReplyCallback<T> {
        void onReply(T t);
    }

    public SelfDestructiveThread(String str, int i, int i2) {
        this.mThreadName = str;
        this.mPriority = i;
        this.mDestructAfterMillisec = i2;
        this.mGeneration = 0;
    }

    @VisibleForTesting
    public boolean isRunning() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mThread != null;
        }
        return z;
    }

    @VisibleForTesting
    public int getGeneration() {
        int i;
        synchronized (this.mLock) {
            i = this.mGeneration;
        }
        return i;
    }

    private void post(Runnable runnable) {
        synchronized (this.mLock) {
            int i = 1;
            if (this.mThread == null) {
                this.mThread = new HandlerThread(this.mThreadName, this.mPriority);
                this.mThread.start();
                this.mHandler = new Handler(this.mThread.getLooper(), this.mCallback);
                this.mGeneration += i;
            }
            this.mHandler.removeMessages(0);
            this.mHandler.sendMessage(this.mHandler.obtainMessage(i, runnable));
        }
    }

    public <T> void postAndReply(final Callable<T> callable, final ReplyCallback<T> replyCallback) {
        final Handler handler = new Handler();
        post(new Runnable() {
            public void run() {
                Object call;
                try {
                    call = callable.call();
                } catch (Exception unused) {
                    call = null;
                }
                handler.post(new Runnable() {
                    public void run() {
                        replyCallback.onReply(call);
                    }
                });
            }
        });
    }

    public <T> T postAndWait(Callable<T> callable, int i) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition newCondition = reentrantLock.newCondition();
        AtomicReference atomicReference = new AtomicReference();
        AtomicBoolean atomicBoolean = new AtomicBoolean(true);
        final AtomicReference atomicReference2 = atomicReference;
        final Callable<T> callable2 = callable;
        final ReentrantLock reentrantLock2 = reentrantLock;
        final AtomicBoolean atomicBoolean2 = atomicBoolean;
        final Condition condition = newCondition;
        post(new Runnable() {
            public void run() {
                try {
                    atomicReference2.set(callable2.call());
                } catch (Exception unused) {
                    reentrantLock2.lock();
                    atomicBoolean2.set(false);
                    condition.signal();
                } finally {
                    reentrantLock2.unlock();
                }
            }
        });
        reentrantLock.lock();
        long toNanos;
        T t;
        try {
            if (atomicBoolean.get()) {
                toNanos = TimeUnit.MILLISECONDS.toNanos((long) i);
                while (true) {
                    toNanos = newCondition.awaitNanos(toNanos);
                }
                return t;
            }
            t = atomicReference.get();
            return t;
        } catch (InterruptedException unused) {
            if (!atomicBoolean.get()) {
                t = atomicReference.get();
                break;
            } else if (toNanos <= 0) {
                throw new InterruptedException("timeout");
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    private void onInvokeRunnable(Runnable runnable) {
        runnable.run();
        synchronized (this.mLock) {
            int i = 0;
            this.mHandler.removeMessages(i);
            this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(i), (long) this.mDestructAfterMillisec);
        }
    }

    private void onDestruction() {
        synchronized (this.mLock) {
            if (this.mHandler.hasMessages(1)) {
                return;
            }
            this.mThread.quit();
            Object obj = null;
            this.mThread = obj;
            this.mHandler = obj;
        }
    }
}