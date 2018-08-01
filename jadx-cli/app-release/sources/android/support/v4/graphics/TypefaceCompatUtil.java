package android.support.v4.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

@RestrictTo({Scope.LIBRARY_GROUP})
public class TypefaceCompatUtil {
    private static final String CACHE_FILE_PREFIX = ".font";
    private static final String TAG = "TypefaceCompatUtil";

    private TypefaceCompatUtil() {
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.File getTempFile(android.content.Context r5) {
        /*
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = ".font";
        r0.append(r1);
        r1 = android.os.Process.myPid();
        r0.append(r1);
        r1 = "-";
        r0.append(r1);
        r1 = android.os.Process.myTid();
        r0.append(r1);
        r1 = "-";
        r0.append(r1);
        r0 = r0.toString();
        r1 = 0;
    L_0x0027:
        r2 = 100;
        if (r1 >= r2) goto L_0x004d;
    L_0x002b:
        r2 = new java.io.File;
        r3 = r5.getCacheDir();
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r4.append(r0);
        r4.append(r1);
        r4 = r4.toString();
        r2.<init>(r3, r4);
        r3 = r2.createNewFile();	 Catch:{ IOException -> 0x004a }
        if (r3 == 0) goto L_0x004a;
    L_0x0049:
        return r2;
    L_0x004a:
        r1 = r1 + 1;
        goto L_0x0027;
    L_0x004d:
        r5 = 0;
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.getTempFile(android.content.Context):java.io.File");
    }

    @RequiresApi(19)
    private static ByteBuffer mmap(File file) {
        Throwable th;
        Throwable th2;
        ByteBuffer byteBuffer = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                FileChannel channel = fileInputStream.getChannel();
                ByteBuffer map = channel.map(MapMode.READ_ONLY, 0, channel.size());
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                return map;
            } catch (Throwable th22) {
                Throwable th3 = th22;
                th22 = th;
                th = th3;
            }
            throw th;
            if (fileInputStream != null) {
                if (th22 != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th4) {
                        th22.addSuppressed(th4);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th;
        } catch (IOException unused) {
            return byteBuffer;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.RequiresApi(19)
    public static java.nio.ByteBuffer mmap(android.content.Context r8, android.os.CancellationSignal r9, android.net.Uri r10) {
        /*
        r8 = r8.getContentResolver();
        r0 = 0;
        r1 = "r";
        r8 = r8.openFileDescriptor(r10, r1, r9);	 Catch:{ IOException -> 0x0063 }
        r9 = new java.io.FileInputStream;	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r10 = r8.getFileDescriptor();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r9.<init>(r10);	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        r1 = r9.getChannel();	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r5 = r1.size();	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r2 = java.nio.channels.FileChannel.MapMode.READ_ONLY;	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        r3 = 0;
        r10 = r1.map(r2, r3, r5);	 Catch:{ Throwable -> 0x0032, all -> 0x002f }
        if (r9 == 0) goto L_0x0029;
    L_0x0026:
        r9.close();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0029:
        if (r8 == 0) goto L_0x002e;
    L_0x002b:
        r8.close();	 Catch:{ IOException -> 0x0063 }
    L_0x002e:
        return r10;
    L_0x002f:
        r10 = move-exception;
        r1 = r0;
        goto L_0x0038;
    L_0x0032:
        r10 = move-exception;
        throw r10;	 Catch:{ all -> 0x0034 }
    L_0x0034:
        r1 = move-exception;
        r7 = r1;
        r1 = r10;
        r10 = r7;
    L_0x0038:
        if (r9 == 0) goto L_0x0048;
    L_0x003a:
        if (r1 == 0) goto L_0x0045;
    L_0x003c:
        r9.close();	 Catch:{ Throwable -> 0x0040, all -> 0x0049 }
        goto L_0x0048;
    L_0x0040:
        r9 = move-exception;
        r1.addSuppressed(r9);	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
        goto L_0x0048;
    L_0x0045:
        r9.close();	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0048:
        throw r10;	 Catch:{ Throwable -> 0x004c, all -> 0x0049 }
    L_0x0049:
        r9 = move-exception;
        r10 = r0;
        goto L_0x0052;
    L_0x004c:
        r9 = move-exception;
        throw r9;	 Catch:{ all -> 0x004e }
    L_0x004e:
        r10 = move-exception;
        r7 = r10;
        r10 = r9;
        r9 = r7;
    L_0x0052:
        if (r8 == 0) goto L_0x0062;
    L_0x0054:
        if (r10 == 0) goto L_0x005f;
    L_0x0056:
        r8.close();	 Catch:{ Throwable -> 0x005a }
        goto L_0x0062;
    L_0x005a:
        r8 = move-exception;
        r10.addSuppressed(r8);	 Catch:{ IOException -> 0x0063 }
        goto L_0x0062;
    L_0x005f:
        r8.close();	 Catch:{ IOException -> 0x0063 }
    L_0x0062:
        throw r9;	 Catch:{ IOException -> 0x0063 }
    L_0x0063:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.mmap(android.content.Context, android.os.CancellationSignal, android.net.Uri):java.nio.ByteBuffer");
    }

    @RequiresApi(19)
    public static ByteBuffer copyToDirectBuffer(Context context, Resources resources, int i) {
        File tempFile = getTempFile(context);
        ByteBuffer byteBuffer = null;
        if (tempFile == null) {
            return byteBuffer;
        }
        try {
            if (!copyToFile(tempFile, resources, i)) {
                return byteBuffer;
            }
            ByteBuffer mmap = mmap(tempFile);
            tempFile.delete();
            return mmap;
        } finally {
            tempFile.delete();
        }
    }

    public static boolean copyToFile(File file, InputStream inputStream) {
        IOException e;
        StringBuilder stringBuilder;
        Throwable th;
        boolean z = false;
        Closeable closeable = null;
        try {
            Closeable fileOutputStream = new FileOutputStream(file, z);
            try {
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = inputStream.read(bArr);
                    if (read != -1) {
                        fileOutputStream.write(bArr, z, read);
                    } else {
                        boolean z2 = true;
                        closeQuietly(fileOutputStream);
                        return z2;
                    }
                }
            } catch (IOException e2) {
                e = e2;
                closeable = fileOutputStream;
                try {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Error copying resource contents to temp file: ");
                    stringBuilder.append(e.getMessage());
                    Log.e("TypefaceCompatUtil", stringBuilder.toString());
                    closeQuietly(closeable);
                    return z;
                } catch (Throwable th2) {
                    th = th2;
                    closeQuietly(closeable);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                closeable = fileOutputStream;
                closeQuietly(closeable);
                throw th;
            }
        } catch (IOException e3) {
            e = e3;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Error copying resource contents to temp file: ");
            stringBuilder.append(e.getMessage());
            Log.e("TypefaceCompatUtil", stringBuilder.toString());
            closeQuietly(closeable);
            return z;
        }
    }

    public static boolean copyToFile(File file, Resources resources, int i) {
        Throwable th;
        Closeable openRawResource;
        try {
            openRawResource = resources.openRawResource(i);
            try {
                boolean copyToFile = copyToFile(file, openRawResource);
                closeQuietly(openRawResource);
                return copyToFile;
            } catch (Throwable th2) {
                th = th2;
                closeQuietly(openRawResource);
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            openRawResource = null;
            closeQuietly(openRawResource);
            throw th;
        }
    }

    public static void closeQuietly(java.io.Closeable r0) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Exception block dominator not found, method:android.support.v4.graphics.TypefaceCompatUtil.closeQuietly(java.io.Closeable):void, dom blocks: []
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:89)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
        /*
        if (r0 == 0) goto L_0x0005;
    L_0x0002:
        r0.close();	 Catch:{ IOException -> 0x0005 }
    L_0x0005:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.TypefaceCompatUtil.closeQuietly(java.io.Closeable):void");
    }
}
