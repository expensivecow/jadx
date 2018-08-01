package android.support.v4.media;

import android.os.Bundle;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.LIBRARY_GROUP})
public class MediaBrowserCompatUtils {
    public static boolean areSameOptions(Bundle bundle, Bundle bundle2) {
        boolean z = true;
        if (bundle == bundle2) {
            return z;
        }
        boolean z2 = false;
        int i = -1;
        if (bundle == null) {
            if (!(bundle2.getInt("android.media.browse.extra.PAGE", i) == i && bundle2.getInt("android.media.browse.extra.PAGE_SIZE", i) == i)) {
                z = z2;
            }
            return z;
        } else if (bundle2 == null) {
            if (!(bundle.getInt("android.media.browse.extra.PAGE", i) == i && bundle.getInt("android.media.browse.extra.PAGE_SIZE", i) == i)) {
                z = z2;
            }
            return z;
        } else {
            if (!(bundle.getInt("android.media.browse.extra.PAGE", i) == bundle2.getInt("android.media.browse.extra.PAGE", i) && bundle.getInt("android.media.browse.extra.PAGE_SIZE", i) == bundle2.getInt("android.media.browse.extra.PAGE_SIZE", i))) {
                z = z2;
            }
            return z;
        }
    }

    public static boolean hasDuplicatedItems(Bundle bundle, Bundle bundle2) {
        int i;
        int i2;
        int i3;
        int i4 = -1;
        int i5 = bundle == null ? i4 : bundle.getInt("android.media.browse.extra.PAGE", i4);
        if (bundle2 == null) {
            i = i4;
        } else {
            i = bundle2.getInt("android.media.browse.extra.PAGE", i4);
        }
        if (bundle == null) {
            i2 = i4;
        } else {
            i2 = bundle.getInt("android.media.browse.extra.PAGE_SIZE", i4);
        }
        if (bundle2 == null) {
            i3 = i4;
        } else {
            i3 = bundle2.getInt("android.media.browse.extra.PAGE_SIZE", i4);
        }
        int i6 = Integer.MAX_VALUE;
        boolean z = false;
        boolean z2 = true;
        if (i5 == i4 || i2 == i4) {
            i2 = i6;
            i5 = z;
        } else {
            i5 *= i2;
            i2 = (i2 + i5) - z2;
        }
        if (i == i4 || i3 == i4) {
            i4 = z;
        } else {
            i4 = i3 * i;
            i6 = (i3 + i4) - 1;
        }
        if (i5 > i4 || i4 > i2) {
            return (i5 > i6 || i6 > i2) ? z : z2;
        } else {
            return z2;
        }
    }
}
