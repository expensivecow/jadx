package android.support.v4.os;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IResultReceiver extends IInterface {

    public static abstract class Stub extends Binder implements IResultReceiver {
        private static final String DESCRIPTOR = "android.support.v4.os.IResultReceiver";
        static final int TRANSACTION_send = 1;

        private static class Proxy implements IResultReceiver {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return "android.support.v4.os.IResultReceiver";
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void send(int i, Bundle bundle) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("android.support.v4.os.IResultReceiver");
                    obtain.writeInt(i);
                    i = 0;
                    int i2 = 1;
                    if (bundle != null) {
                        obtain.writeInt(i2);
                        bundle.writeToParcel(obtain, i);
                    } else {
                        obtain.writeInt(i);
                    }
                    this.mRemote.transact(i2, obtain, null, i2);
                } finally {
                    obtain.recycle();
                }
            }
        }

        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, "android.support.v4.os.IResultReceiver");
        }

        public static IResultReceiver asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("android.support.v4.os.IResultReceiver");
            if (queryLocalInterface == null || !(queryLocalInterface instanceof IResultReceiver)) {
                return new Proxy(iBinder);
            }
            return (IResultReceiver) queryLocalInterface;
        }

        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            boolean z = true;
            if (i == z) {
                parcel.enforceInterface("android.support.v4.os.IResultReceiver");
                send(parcel.readInt(), parcel.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(parcel) : null);
                return z;
            } else if (i != 1598968902) {
                return super.onTransact(i, parcel, parcel2, i2);
            } else {
                parcel2.writeString("android.support.v4.os.IResultReceiver");
                return z;
            }
        }
    }

    void send(int i, Bundle bundle) throws RemoteException;
}
