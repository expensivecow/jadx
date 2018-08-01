package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;

@RequiresApi(19)
class DocumentsContractApi19 {
    private static final int FLAG_VIRTUAL_DOCUMENT = 512;
    private static final String TAG = "DocumentFile";

    DocumentsContractApi19() {
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        return DocumentsContract.isDocumentUri(context, uri);
    }

    public static boolean isVirtual(Context context, Uri uri) {
        boolean z = false;
        if (!isDocumentUri(context, uri)) {
            return z;
        }
        if ((getFlags(context, uri) & 512) != 0) {
            z = true;
        }
        return z;
    }

    public static String getName(Context context, Uri uri) {
        return queryForString(context, uri, "_display_name", null);
    }

    private static String getRawType(Context context, Uri uri) {
        return queryForString(context, uri, "mime_type", null);
    }

    public static String getType(Context context, Uri uri) {
        String rawType = getRawType(context, uri);
        return "vnd.android.document/directory".equals(rawType) ? null : rawType;
    }

    public static long getFlags(Context context, Uri uri) {
        return queryForLong(context, uri, "flags", 0);
    }

    public static boolean isDirectory(Context context, Uri uri) {
        return "vnd.android.document/directory".equals(getRawType(context, uri));
    }

    public static boolean isFile(Context context, Uri uri) {
        CharSequence rawType = getRawType(context, uri);
        return ("vnd.android.document/directory".equals(rawType) || TextUtils.isEmpty(rawType)) ? false : true;
    }

    public static long lastModified(Context context, Uri uri) {
        return queryForLong(context, uri, "last_modified", 0);
    }

    public static long length(Context context, Uri uri) {
        return queryForLong(context, uri, "_size", 0);
    }

    public static boolean canRead(Context context, Uri uri) {
        boolean z = true;
        boolean z2 = false;
        return (context.checkCallingOrSelfUriPermission(uri, z) == 0 && !TextUtils.isEmpty(getRawType(context, uri))) ? z : z2;
    }

    public static boolean canWrite(Context context, Uri uri) {
        int i = 2;
        boolean z = false;
        if (context.checkCallingOrSelfUriPermission(uri, i) != 0) {
            return z;
        }
        CharSequence rawType = getRawType(context, uri);
        int queryForInt = queryForInt(context, uri, "flags", z);
        if (TextUtils.isEmpty(rawType)) {
            return z;
        }
        boolean z2 = true;
        if ((queryForInt & 4) != 0) {
            return z2;
        }
        if (!"vnd.android.document/directory".equals(rawType) || (queryForInt & 8) == 0) {
            return (TextUtils.isEmpty(rawType) || (queryForInt & i) == 0) ? z : z2;
        } else {
            return z2;
        }
    }

    public static boolean delete(Context context, Uri uri) {
        try {
            return DocumentsContract.deleteDocument(context.getContentResolver(), uri);
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean exists(Context context, Uri uri) {
        Object e;
        StringBuilder stringBuilder;
        Throwable th;
        ContentResolver contentResolver = context.getContentResolver();
        boolean z = true;
        boolean z2 = false;
        AutoCloseable autoCloseable = null;
        try {
            String[] strArr = new String[z];
            strArr[z2] = "document_id";
            AutoCloseable query = contentResolver.query(uri, strArr, null, null, null);
            try {
                if (query.getCount() <= 0) {
                    z = z2;
                }
                closeQuietly(query);
                return z;
            } catch (Exception e2) {
                e = e2;
                autoCloseable = query;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed query: ");
                    stringBuilder.append(e);
                    Log.w("DocumentFile", stringBuilder.toString());
                    closeQuietly(autoCloseable);
                    return z2;
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(autoCloseable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                autoCloseable = query;
                closeQuietly(autoCloseable);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed query: ");
            stringBuilder.append(e);
            Log.w("DocumentFile", stringBuilder.toString());
            closeQuietly(autoCloseable);
            return z2;
        }
    }

    private static String queryForString(Context context, Uri uri, String str, String str2) {
        Object e;
        StringBuilder stringBuilder;
        Throwable th;
        ContentResolver contentResolver = context.getContentResolver();
        AutoCloseable autoCloseable = null;
        try {
            String[] strArr = new String[1];
            int i = 0;
            strArr[i] = str;
            AutoCloseable query = contentResolver.query(uri, strArr, null, null, null);
            try {
                if (!query.moveToFirst() || query.isNull(i)) {
                    closeQuietly(query);
                    return str2;
                }
                String string = query.getString(i);
                closeQuietly(query);
                return string;
            } catch (Exception e2) {
                e = e2;
                autoCloseable = query;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed query: ");
                    stringBuilder.append(e);
                    Log.w("DocumentFile", stringBuilder.toString());
                    closeQuietly(autoCloseable);
                    return str2;
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(autoCloseable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                autoCloseable = query;
                closeQuietly(autoCloseable);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed query: ");
            stringBuilder.append(e);
            Log.w("DocumentFile", stringBuilder.toString());
            closeQuietly(autoCloseable);
            return str2;
        }
    }

    private static int queryForInt(Context context, Uri uri, String str, int i) {
        return (int) queryForLong(context, uri, str, (long) i);
    }

    private static long queryForLong(Context context, Uri uri, String str, long j) {
        Object e;
        StringBuilder stringBuilder;
        Throwable th;
        ContentResolver contentResolver = context.getContentResolver();
        AutoCloseable autoCloseable = null;
        try {
            String[] strArr = new String[1];
            int i = 0;
            strArr[i] = str;
            AutoCloseable query = contentResolver.query(uri, strArr, null, null, null);
            try {
                if (!query.moveToFirst() || query.isNull(i)) {
                    closeQuietly(query);
                    return j;
                }
                long j2 = query.getLong(i);
                closeQuietly(query);
                return j2;
            } catch (Exception e2) {
                e = e2;
                autoCloseable = query;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Failed query: ");
                    stringBuilder.append(e);
                    Log.w("DocumentFile", stringBuilder.toString());
                    closeQuietly(autoCloseable);
                    return j;
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(autoCloseable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                autoCloseable = query;
                closeQuietly(autoCloseable);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed query: ");
            stringBuilder.append(e);
            Log.w("DocumentFile", stringBuilder.toString());
            closeQuietly(autoCloseable);
            return j;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void closeQuietly(java.lang.AutoCloseable r0) {
        /*
        if (r0 == 0) goto L_0x0008;
    L_0x0002:
        r0.close();	 Catch:{ RuntimeException -> 0x0006, Exception -> 0x0008 }
        goto L_0x0008;
    L_0x0006:
        r0 = move-exception;
        throw r0;
    L_0x0008:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.DocumentsContractApi19.closeQuietly(java.lang.AutoCloseable):void");
    }
}
