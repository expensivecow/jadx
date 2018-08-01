package android.support.v4.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.util.ArrayList;

@RequiresApi(21)
class DocumentsContractApi21 {
    private static final String TAG = "DocumentFile";

    DocumentsContractApi21() {
    }

    public static Uri createFile(Context context, Uri uri, String str, String str2) {
        try {
            return DocumentsContract.createDocument(context.getContentResolver(), uri, str, str2);
        } catch (Exception unused) {
            return null;
        }
    }

    public static Uri createDirectory(Context context, Uri uri, String str) {
        return createFile(context, uri, "vnd.android.document/directory", str);
    }

    public static Uri prepareTreeUri(Uri uri) {
        return DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri));
    }

    public static Uri[] listFiles(Context context, Uri uri) {
        Object e;
        StringBuilder stringBuilder;
        Throwable th;
        ContentResolver contentResolver = context.getContentResolver();
        Uri buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uri, DocumentsContract.getDocumentId(uri));
        ArrayList arrayList = new ArrayList();
        AutoCloseable autoCloseable = null;
        try {
            String[] strArr = new String[1];
            int i = 0;
            strArr[i] = "document_id";
            AutoCloseable query = contentResolver.query(buildChildDocumentsUriUsingTree, strArr, null, null, null);
            while (query.moveToNext()) {
                try {
                    arrayList.add(DocumentsContract.buildDocumentUriUsingTree(uri, query.getString(i)));
                } catch (Exception e2) {
                    e = e2;
                    autoCloseable = query;
                    try {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Failed query: ");
                        stringBuilder.append(e);
                        Log.w("DocumentFile", stringBuilder.toString());
                        closeQuietly(autoCloseable);
                        return (Uri[]) arrayList.toArray(new Uri[arrayList.size()]);
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
            }
            closeQuietly(query);
        } catch (Exception e3) {
            e = e3;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Failed query: ");
            stringBuilder.append(e);
            Log.w("DocumentFile", stringBuilder.toString());
            closeQuietly(autoCloseable);
            return (Uri[]) arrayList.toArray(new Uri[arrayList.size()]);
        }
        return (Uri[]) arrayList.toArray(new Uri[arrayList.size()]);
    }

    public static Uri renameTo(Context context, Uri uri, String str) {
        try {
            return DocumentsContract.renameDocument(context.getContentResolver(), uri, str);
        } catch (Exception unused) {
            return null;
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
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.DocumentsContractApi21.closeQuietly(java.lang.AutoCloseable):void");
    }
}
