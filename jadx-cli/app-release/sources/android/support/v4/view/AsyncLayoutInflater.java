package android.support.v4.view;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.util.Pools.SynchronizedPool;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.ArrayBlockingQueue;

public final class AsyncLayoutInflater {
    private static final String TAG = "AsyncLayoutInflater";
    Handler mHandler;
    private Callback mHandlerCallback = new Callback() {
        public boolean handleMessage(Message message) {
            InflateRequest inflateRequest = (InflateRequest) message.obj;
            if (inflateRequest.view == null) {
                inflateRequest.view = AsyncLayoutInflater.this.mInflater.inflate(inflateRequest.resid, inflateRequest.parent, false);
            }
            inflateRequest.callback.onInflateFinished(inflateRequest.view, inflateRequest.resid, inflateRequest.parent);
            AsyncLayoutInflater.this.mInflateThread.releaseRequest(inflateRequest);
            return true;
        }
    };
    InflateThread mInflateThread;
    LayoutInflater mInflater;

    private static class BasicInflater extends LayoutInflater {
        private static final String[] sClassPrefixList;

        static {
            String[] strArr = new String[3];
            strArr[0] = "android.widget.";
            strArr[1] = "android.webkit.";
            strArr[2] = "android.app.";
            sClassPrefixList = strArr;
        }

        BasicInflater(Context context) {
            super(context);
        }

        public LayoutInflater cloneInContext(Context context) {
            return new BasicInflater(context);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        protected android.view.View onCreateView(java.lang.String r5, android.util.AttributeSet r6) throws java.lang.ClassNotFoundException {
            /*
            r4 = this;
            r0 = sClassPrefixList;
            r1 = 0;
            r2 = r0.length;
        L_0x0004:
            if (r1 >= r2) goto L_0x0012;
        L_0x0006:
            r3 = r0[r1];
            r3 = r4.createView(r5, r3, r6);	 Catch:{ ClassNotFoundException -> 0x000f }
            if (r3 == 0) goto L_0x000f;
        L_0x000e:
            return r3;
        L_0x000f:
            r1 = r1 + 1;
            goto L_0x0004;
        L_0x0012:
            r5 = super.onCreateView(r5, r6);
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.view.AsyncLayoutInflater.BasicInflater.onCreateView(java.lang.String, android.util.AttributeSet):android.view.View");
        }
    }

    private static class InflateRequest {
        OnInflateFinishedListener callback;
        AsyncLayoutInflater inflater;
        ViewGroup parent;
        int resid;
        View view;

        InflateRequest() {
        }
    }

    private static class InflateThread extends Thread {
        private static final InflateThread sInstance = new InflateThread();
        private ArrayBlockingQueue<InflateRequest> mQueue;
        private SynchronizedPool<InflateRequest> mRequestPool;

        private InflateThread() {
            int i = 10;
            this.mQueue = new ArrayBlockingQueue(i);
            this.mRequestPool = new SynchronizedPool(i);
        }

        static {
            sInstance.start();
        }

        public static InflateThread getInstance() {
            return sInstance;
        }

        public void runInner() {
            try {
                InflateRequest inflateRequest = (InflateRequest) this.mQueue.take();
                boolean z = false;
                try {
                    inflateRequest.view = inflateRequest.inflater.mInflater.inflate(inflateRequest.resid, inflateRequest.parent, z);
                } catch (Throwable e) {
                    Log.w("AsyncLayoutInflater", "Failed to inflate resource in the background! Retrying on the UI thread", e);
                }
                Message.obtain(inflateRequest.inflater.mHandler, z, inflateRequest).sendToTarget();
            } catch (Throwable e2) {
                Log.w("AsyncLayoutInflater", e2);
            }
        }

        public void run() {
            while (true) {
                runInner();
            }
        }

        public InflateRequest obtainRequest() {
            InflateRequest inflateRequest = (InflateRequest) this.mRequestPool.acquire();
            return inflateRequest == null ? new InflateRequest() : inflateRequest;
        }

        public void releaseRequest(InflateRequest inflateRequest) {
            View view = null;
            inflateRequest.callback = view;
            inflateRequest.inflater = view;
            inflateRequest.parent = view;
            inflateRequest.resid = 0;
            inflateRequest.view = view;
            this.mRequestPool.release(inflateRequest);
        }

        public void enqueue(InflateRequest inflateRequest) {
            try {
                this.mQueue.put(inflateRequest);
            } catch (Throwable e) {
                throw new RuntimeException("Failed to enqueue async inflate request", e);
            }
        }
    }

    public interface OnInflateFinishedListener {
        void onInflateFinished(View view, int i, ViewGroup viewGroup);
    }

    public AsyncLayoutInflater(@NonNull Context context) {
        this.mInflater = new BasicInflater(context);
        this.mHandler = new Handler(this.mHandlerCallback);
        this.mInflateThread = InflateThread.getInstance();
    }

    @UiThread
    public void inflate(@LayoutRes int i, @Nullable ViewGroup viewGroup, @NonNull OnInflateFinishedListener onInflateFinishedListener) {
        if (onInflateFinishedListener == null) {
            throw new NullPointerException("callback argument may not be null!");
        }
        InflateRequest obtainRequest = this.mInflateThread.obtainRequest();
        obtainRequest.inflater = this;
        obtainRequest.resid = i;
        obtainRequest.parent = viewGroup;
        obtainRequest.callback = onInflateFinishedListener;
        this.mInflateThread.enqueue(obtainRequest);
    }
}
