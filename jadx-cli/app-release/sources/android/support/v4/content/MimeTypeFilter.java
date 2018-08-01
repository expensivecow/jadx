package android.support.v4.content;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;

public final class MimeTypeFilter {
    private MimeTypeFilter() {
    }

    private static boolean mimeTypeAgainstFilter(@NonNull String[] strArr, @NonNull String[] strArr2) {
        int i = 2;
        if (strArr2.length != i) {
            throw new IllegalArgumentException("Ill-formatted MIME type filter. Must be type/subtype.");
        }
        boolean z = false;
        if (!strArr2[z].isEmpty()) {
            boolean z2 = true;
            if (!strArr2[z2].isEmpty()) {
                if (strArr.length != i) {
                    return z;
                }
                if ("*".equals(strArr2[z]) || strArr2[z].equals(strArr[z])) {
                    return ("*".equals(strArr2[z2]) || strArr2[z2].equals(strArr[z2])) ? z2 : z;
                } else {
                    return z;
                }
            }
        }
        throw new IllegalArgumentException("Ill-formatted MIME type filter. Type or subtype empty.");
    }

    public static boolean matches(@Nullable String str, @NonNull String str2) {
        if (str == null) {
            return false;
        }
        return mimeTypeAgainstFilter(str.split("/"), str2.split("/"));
    }

    public static String matches(@Nullable String str, @NonNull String[] strArr) {
        String str2 = null;
        if (str == null) {
            return str2;
        }
        String[] split = str.split("/");
        for (String str3 : strArr) {
            if (mimeTypeAgainstFilter(split, str3.split("/"))) {
                return str3;
            }
        }
        return str2;
    }

    public static String matches(@Nullable String[] strArr, @NonNull String str) {
        String str2 = null;
        if (strArr == null) {
            return str2;
        }
        String[] split = str.split("/");
        for (String str3 : strArr) {
            if (mimeTypeAgainstFilter(str3.split("/"), split)) {
                return str3;
            }
        }
        return str2;
    }

    public static String[] matchesMany(@Nullable String[] strArr, @NonNull String str) {
        int i = 0;
        if (strArr == null) {
            return new String[i];
        }
        ArrayList arrayList = new ArrayList();
        String[] split = str.split("/");
        int length = strArr.length;
        while (i < length) {
            String str2 = strArr[i];
            if (mimeTypeAgainstFilter(str2.split("/"), split)) {
                arrayList.add(str2);
            }
            i++;
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }
}
