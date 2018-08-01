package android.support.v4.net;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ConnectivityManagerCompatHoneycombMR2 {
    ConnectivityManagerCompatHoneycombMR2() {
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager cm) {
        boolean z = true;
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return z;
        }
        switch (info.getType()) {
            case 1:
            case 7:
            case 9:
                return false;
            default:
                return z;
        }
    }
}
