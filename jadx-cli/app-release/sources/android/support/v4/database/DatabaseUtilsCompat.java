package android.support.v4.database;

import android.text.TextUtils;

public final class DatabaseUtilsCompat {
    private DatabaseUtilsCompat() {
    }

    public static String concatenateWhere(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return str2;
        }
        if (TextUtils.isEmpty(str2)) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        stringBuilder.append(str);
        stringBuilder.append(") AND (");
        stringBuilder.append(str2);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public static String[] appendSelectionArgs(String[] strArr, String[] strArr2) {
        if (strArr == null || strArr.length == 0) {
            return strArr2;
        }
        int i = 0;
        Object obj = new String[(strArr.length + strArr2.length)];
        System.arraycopy(strArr, i, obj, i, strArr.length);
        System.arraycopy(strArr2, i, obj, strArr.length, strArr2.length);
        return obj;
    }
}
