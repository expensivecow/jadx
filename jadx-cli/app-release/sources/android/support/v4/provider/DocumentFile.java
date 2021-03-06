package android.support.v4.provider;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import java.io.File;

public abstract class DocumentFile {
    static final String TAG = "DocumentFile";
    private final DocumentFile mParent;

    public abstract boolean canRead();

    public abstract boolean canWrite();

    public abstract DocumentFile createDirectory(String str);

    public abstract DocumentFile createFile(String str, String str2);

    public abstract boolean delete();

    public abstract boolean exists();

    public abstract String getName();

    public abstract String getType();

    public abstract Uri getUri();

    public abstract boolean isDirectory();

    public abstract boolean isFile();

    public abstract boolean isVirtual();

    public abstract long lastModified();

    public abstract long length();

    public abstract DocumentFile[] listFiles();

    public abstract boolean renameTo(String str);

    DocumentFile(DocumentFile documentFile) {
        this.mParent = documentFile;
    }

    public static DocumentFile fromFile(File file) {
        return new RawDocumentFile(null, file);
    }

    public static DocumentFile fromSingleUri(Context context, Uri uri) {
        DocumentFile documentFile = null;
        return VERSION.SDK_INT >= 19 ? new SingleDocumentFile(documentFile, context, uri) : documentFile;
    }

    public static DocumentFile fromTreeUri(Context context, Uri uri) {
        DocumentFile documentFile = null;
        return VERSION.SDK_INT >= 21 ? new TreeDocumentFile(documentFile, context, DocumentsContractApi21.prepareTreeUri(uri)) : documentFile;
    }

    public static boolean isDocumentUri(Context context, Uri uri) {
        return VERSION.SDK_INT >= 19 ? DocumentsContractApi19.isDocumentUri(context, uri) : false;
    }

    public DocumentFile getParentFile() {
        return this.mParent;
    }

    public DocumentFile findFile(String str) {
        for (DocumentFile documentFile : listFiles()) {
            if (str.equals(documentFile.getName())) {
                return documentFile;
            }
        }
        return null;
    }
}
