package android.support.v4.database;

import android.text.TextUtils;

public class DatabaseUtilsCompat {
    private DatabaseUtilsCompat() {
    }

    public static String concatenateWhere(String a, String b) {
        if (TextUtils.isEmpty(a)) {
            return b;
        }
        if (TextUtils.isEmpty(b)) {
            return a;
        }
        return "(" + a + ") AND (" + b + ")";
    }

    public static String[] appendSelectionArgs(String[] originalValues, String[] newValues) {
        int i = 0;
        if (originalValues == null || originalValues.length == 0) {
            return newValues;
        }
        String[] result = new String[(originalValues.length + newValues.length)];
        System.arraycopy(originalValues, i, result, i, originalValues.length);
        System.arraycopy(newValues, i, result, originalValues.length, newValues.length);
        return result;
    }
}
