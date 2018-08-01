package android.support.v4.content;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.support.v4.util.DebugUtils;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class Loader<D> {
    boolean mAbandoned;
    boolean mContentChanged;
    Context mContext;
    int mId;
    OnLoadCompleteListener<D> mListener;
    boolean mReset = true;
    boolean mStarted;

    public interface OnLoadCompleteListener<D> {
        void onLoadComplete(Loader<D> loader, D d);
    }

    public final class ForceLoadContentObserver extends ContentObserver {
        public ForceLoadContentObserver() {
            super(new Handler());
        }

        public boolean deliverSelfNotifications() {
            return true;
        }

        public void onChange(boolean selfChange) {
            Loader.this.onContentChanged();
        }
    }

    public Loader(Context context) {
        boolean z = false;
        this.mStarted = z;
        this.mAbandoned = z;
        this.mContentChanged = z;
        this.mContext = context.getApplicationContext();
    }

    public void deliverResult(D data) {
        if (this.mListener != null) {
            this.mListener.onLoadComplete(this, data);
        }
    }

    public Context getContext() {
        return this.mContext;
    }

    public int getId() {
        return this.mId;
    }

    public void registerListener(int id, OnLoadCompleteListener<D> listener) {
        if (this.mListener != null) {
            throw new IllegalStateException("There is already a listener registered");
        }
        this.mListener = listener;
        this.mId = id;
    }

    public void unregisterListener(OnLoadCompleteListener<D> listener) {
        if (this.mListener == null) {
            throw new IllegalStateException("No listener register");
        } else if (this.mListener != listener) {
            throw new IllegalArgumentException("Attempting to unregister the wrong listener");
        } else {
            this.mListener = null;
        }
    }

    public boolean isStarted() {
        return this.mStarted;
    }

    public boolean isAbandoned() {
        return this.mAbandoned;
    }

    public boolean isReset() {
        return this.mReset;
    }

    public final void startLoading() {
        boolean z = false;
        this.mStarted = true;
        this.mReset = z;
        this.mAbandoned = z;
        onStartLoading();
    }

    protected void onStartLoading() {
    }

    public void forceLoad() {
        onForceLoad();
    }

    protected void onForceLoad() {
    }

    public void stopLoading() {
        this.mStarted = false;
        onStopLoading();
    }

    protected void onStopLoading() {
    }

    public void abandon() {
        this.mAbandoned = true;
        onAbandon();
    }

    protected void onAbandon() {
    }

    public void reset() {
        boolean z = false;
        onReset();
        this.mReset = true;
        this.mStarted = z;
        this.mAbandoned = z;
        this.mContentChanged = z;
    }

    protected void onReset() {
    }

    public boolean takeContentChanged() {
        boolean res = this.mContentChanged;
        this.mContentChanged = false;
        return res;
    }

    public void onContentChanged() {
        if (this.mStarted) {
            forceLoad();
        } else {
            this.mContentChanged = true;
        }
    }

    public String dataToString(D data) {
        StringBuilder sb = new StringBuilder(64);
        DebugUtils.buildShortClassTag(data, sb);
        sb.append("}");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        DebugUtils.buildShortClassTag(this, sb);
        sb.append(" id=");
        sb.append(this.mId);
        sb.append("}");
        return sb.toString();
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.print(prefix);
        writer.print("mId=");
        writer.print(this.mId);
        writer.print(" mListener=");
        writer.println(this.mListener);
        writer.print(prefix);
        writer.print("mStarted=");
        writer.print(this.mStarted);
        writer.print(" mContentChanged=");
        writer.print(this.mContentChanged);
        writer.print(" mAbandoned=");
        writer.print(this.mAbandoned);
        writer.print(" mReset=");
        writer.println(this.mReset);
    }
}
