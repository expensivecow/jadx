package com.android.volley.toolbox;

import android.os.SystemClock;
import android.text.TextUtils;
import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.Header;
import com.android.volley.VolleyLog;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiskBasedCache implements Cache {
    private static final int CACHE_MAGIC = 538247942;
    private static final int DEFAULT_DISK_USAGE_BYTES = 5242880;
    private static final float HYSTERESIS_FACTOR = 0.9f;
    private final Map<String, CacheHeader> mEntries;
    private final int mMaxCacheSizeInBytes;
    private final File mRootDirectory;
    private long mTotalSize;

    static class CacheHeader {
        final List<Header> allResponseHeaders;
        final String etag;
        final String key;
        final long lastModified;
        final long serverDate;
        long size;
        final long softTtl;
        final long ttl;

        private CacheHeader(String str, String str2, long j, long j2, long j3, long j4, List<Header> list) {
            this.key = str;
            if ("".equals(str2)) {
                str2 = null;
            }
            this.etag = str2;
            this.serverDate = j;
            this.lastModified = j2;
            this.ttl = j3;
            this.softTtl = j4;
            this.allResponseHeaders = list;
        }

        CacheHeader(String str, Entry entry) {
            this(str, entry.etag, entry.serverDate, entry.lastModified, entry.ttl, entry.softTtl, getAllResponseHeaders(entry));
            this.size = (long) entry.data.length;
        }

        private static List<Header> getAllResponseHeaders(Entry entry) {
            if (entry.allResponseHeaders != null) {
                return entry.allResponseHeaders;
            }
            return HttpHeaderParser.toAllHeaderList(entry.responseHeaders);
        }

        static CacheHeader readHeader(CountingInputStream countingInputStream) throws IOException {
            if (DiskBasedCache.readInt(countingInputStream) == 538247942) {
                return new CacheHeader(DiskBasedCache.readString(countingInputStream), DiskBasedCache.readString(countingInputStream), DiskBasedCache.readLong(countingInputStream), DiskBasedCache.readLong(countingInputStream), DiskBasedCache.readLong(countingInputStream), DiskBasedCache.readLong(countingInputStream), DiskBasedCache.readHeaderList(countingInputStream));
            }
            throw new IOException();
        }

        Entry toCacheEntry(byte[] bArr) {
            Entry entry = new Entry();
            entry.data = bArr;
            entry.etag = this.etag;
            entry.serverDate = this.serverDate;
            entry.lastModified = this.lastModified;
            entry.ttl = this.ttl;
            entry.softTtl = this.softTtl;
            entry.responseHeaders = HttpHeaderParser.toHeaderMap(this.allResponseHeaders);
            entry.allResponseHeaders = Collections.unmodifiableList(this.allResponseHeaders);
            return entry;
        }

        boolean writeHeader(OutputStream outputStream) {
            boolean z = true;
            try {
                DiskBasedCache.writeInt(outputStream, 538247942);
                DiskBasedCache.writeString(outputStream, this.key);
                DiskBasedCache.writeString(outputStream, this.etag == null ? "" : this.etag);
                DiskBasedCache.writeLong(outputStream, this.serverDate);
                DiskBasedCache.writeLong(outputStream, this.lastModified);
                DiskBasedCache.writeLong(outputStream, this.ttl);
                DiskBasedCache.writeLong(outputStream, this.softTtl);
                DiskBasedCache.writeHeaderList(this.allResponseHeaders, outputStream);
                outputStream.flush();
                return z;
            } catch (IOException e) {
                Object[] objArr = new Object[z];
                boolean z2 = false;
                objArr[z2] = e.toString();
                VolleyLog.d("%s", objArr);
                return z2;
            }
        }
    }

    static class CountingInputStream extends FilterInputStream {
        private long bytesRead;
        private final long length;

        CountingInputStream(InputStream inputStream, long j) {
            super(inputStream);
            this.length = j;
        }

        public int read() throws IOException {
            int read = super.read();
            if (read != -1) {
                this.bytesRead++;
            }
            return read;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            int read = super.read(bArr, i, i2);
            if (read != -1) {
                this.bytesRead += (long) read;
            }
            return read;
        }

        long bytesRead() {
            return this.bytesRead;
        }

        long bytesRemaining() {
            return this.length - this.bytesRead;
        }
    }

    public DiskBasedCache(File file, int i) {
        this.mEntries = new LinkedHashMap(16, 0.75f, true);
        this.mTotalSize = 0;
        this.mRootDirectory = file;
        this.mMaxCacheSizeInBytes = i;
    }

    public DiskBasedCache(File file) {
        this(file, 5242880);
    }

    public synchronized void clear() {
        File[] listFiles = this.mRootDirectory.listFiles();
        int i = 0;
        if (listFiles != null) {
            int length = listFiles.length;
            for (int i2 = i; i2 < length; i2++) {
                listFiles[i2].delete();
            }
        }
        this.mEntries.clear();
        this.mTotalSize = 0;
        VolleyLog.d("Cache cleared.", new Object[i]);
    }

    public synchronized Entry get(String str) {
        CacheHeader cacheHeader = (CacheHeader) this.mEntries.get(str);
        Entry entry = null;
        if (cacheHeader == null) {
            return entry;
        }
        File fileForKey = getFileForKey(str);
        int i = 2;
        int i2 = 1;
        int i3 = 0;
        CountingInputStream countingInputStream;
        try {
            countingInputStream = new CountingInputStream(new BufferedInputStream(createInputStream(fileForKey)), fileForKey.length());
            if (TextUtils.equals(str, CacheHeader.readHeader(countingInputStream).key)) {
                Entry toCacheEntry = cacheHeader.toCacheEntry(streamToBytes(countingInputStream, countingInputStream.bytesRemaining()));
                countingInputStream.close();
                return toCacheEntry;
            }
            VolleyLog.d("%s: key=%s, found=%s", fileForKey.getAbsolutePath(), str, CacheHeader.readHeader(countingInputStream).key);
            removeEntry(str);
            countingInputStream.close();
            return entry;
        } catch (IOException e) {
            Object[] objArr = new Object[i];
            objArr[i3] = fileForKey.getAbsolutePath();
            objArr[i2] = e.toString();
            VolleyLog.d("%s: %s", objArr);
            remove(str);
            return entry;
        } catch (Throwable th) {
            countingInputStream.close();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void initialize() {
        /*
        r9 = this;
        monitor-enter(r9);
        r0 = r9.mRootDirectory;	 Catch:{ all -> 0x0061 }
        r0 = r0.exists();	 Catch:{ all -> 0x0061 }
        r1 = 0;
        if (r0 != 0) goto L_0x0024;
    L_0x000a:
        r0 = r9.mRootDirectory;	 Catch:{ all -> 0x0061 }
        r0 = r0.mkdirs();	 Catch:{ all -> 0x0061 }
        if (r0 != 0) goto L_0x0022;
    L_0x0012:
        r0 = "Unable to create cache dir %s";
        r2 = 1;
        r2 = new java.lang.Object[r2];	 Catch:{ all -> 0x0061 }
        r3 = r9.mRootDirectory;	 Catch:{ all -> 0x0061 }
        r3 = r3.getAbsolutePath();	 Catch:{ all -> 0x0061 }
        r2[r1] = r3;	 Catch:{ all -> 0x0061 }
        com.android.volley.VolleyLog.e(r0, r2);	 Catch:{ all -> 0x0061 }
    L_0x0022:
        monitor-exit(r9);
        return;
    L_0x0024:
        r0 = r9.mRootDirectory;	 Catch:{ all -> 0x0061 }
        r0 = r0.listFiles();	 Catch:{ all -> 0x0061 }
        if (r0 != 0) goto L_0x002e;
    L_0x002c:
        monitor-exit(r9);
        return;
    L_0x002e:
        r2 = r0.length;	 Catch:{ all -> 0x0061 }
    L_0x002f:
        if (r1 >= r2) goto L_0x005f;
    L_0x0031:
        r3 = r0[r1];	 Catch:{ all -> 0x0061 }
        r4 = r3.length();	 Catch:{ IOException -> 0x0059 }
        r6 = new com.android.volley.toolbox.DiskBasedCache$CountingInputStream;	 Catch:{ IOException -> 0x0059 }
        r7 = new java.io.BufferedInputStream;	 Catch:{ IOException -> 0x0059 }
        r8 = r9.createInputStream(r3);	 Catch:{ IOException -> 0x0059 }
        r7.<init>(r8);	 Catch:{ IOException -> 0x0059 }
        r6.<init>(r7, r4);	 Catch:{ IOException -> 0x0059 }
        r7 = com.android.volley.toolbox.DiskBasedCache.CacheHeader.readHeader(r6);	 Catch:{ all -> 0x0054 }
        r7.size = r4;	 Catch:{ all -> 0x0054 }
        r4 = r7.key;	 Catch:{ all -> 0x0054 }
        r9.putEntry(r4, r7);	 Catch:{ all -> 0x0054 }
        r6.close();	 Catch:{ IOException -> 0x0059 }
        goto L_0x005c;
    L_0x0054:
        r4 = move-exception;
        r6.close();	 Catch:{ IOException -> 0x0059 }
        throw r4;	 Catch:{ IOException -> 0x0059 }
    L_0x0059:
        r3.delete();	 Catch:{ all -> 0x0061 }
    L_0x005c:
        r1 = r1 + 1;
        goto L_0x002f;
    L_0x005f:
        monitor-exit(r9);
        return;
    L_0x0061:
        r0 = move-exception;
        monitor-exit(r9);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.DiskBasedCache.initialize():void");
    }

    public synchronized void invalidate(String str, boolean z) {
        Entry entry = get(str);
        if (entry != null) {
            long j = 0;
            entry.softTtl = j;
            if (z) {
                entry.ttl = j;
            }
            put(str, entry);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void put(java.lang.String r7, com.android.volley.Cache.Entry r8) {
        /*
        r6 = this;
        monitor-enter(r6);
        r0 = r8.data;	 Catch:{ all -> 0x0059 }
        r0 = r0.length;	 Catch:{ all -> 0x0059 }
        r6.pruneIfNeeded(r0);	 Catch:{ all -> 0x0059 }
        r0 = r6.getFileForKey(r7);	 Catch:{ all -> 0x0059 }
        r1 = 0;
        r2 = 1;
        r3 = new java.io.BufferedOutputStream;	 Catch:{ IOException -> 0x0044 }
        r4 = r6.createOutputStream(r0);	 Catch:{ IOException -> 0x0044 }
        r3.<init>(r4);	 Catch:{ IOException -> 0x0044 }
        r4 = new com.android.volley.toolbox.DiskBasedCache$CacheHeader;	 Catch:{ IOException -> 0x0044 }
        r4.<init>(r7, r8);	 Catch:{ IOException -> 0x0044 }
        r5 = r4.writeHeader(r3);	 Catch:{ IOException -> 0x0044 }
        if (r5 != 0) goto L_0x0037;
    L_0x0021:
        r3.close();	 Catch:{ IOException -> 0x0044 }
        r7 = "Failed to write header for %s";
        r8 = new java.lang.Object[r2];	 Catch:{ IOException -> 0x0044 }
        r3 = r0.getAbsolutePath();	 Catch:{ IOException -> 0x0044 }
        r8[r1] = r3;	 Catch:{ IOException -> 0x0044 }
        com.android.volley.VolleyLog.d(r7, r8);	 Catch:{ IOException -> 0x0044 }
        r7 = new java.io.IOException;	 Catch:{ IOException -> 0x0044 }
        r7.<init>();	 Catch:{ IOException -> 0x0044 }
        throw r7;	 Catch:{ IOException -> 0x0044 }
    L_0x0037:
        r8 = r8.data;	 Catch:{ IOException -> 0x0044 }
        r3.write(r8);	 Catch:{ IOException -> 0x0044 }
        r3.close();	 Catch:{ IOException -> 0x0044 }
        r6.putEntry(r7, r4);	 Catch:{ IOException -> 0x0044 }
        monitor-exit(r6);
        return;
    L_0x0044:
        r7 = r0.delete();	 Catch:{ all -> 0x0059 }
        if (r7 != 0) goto L_0x0057;
    L_0x004a:
        r7 = "Could not clean up file %s";
        r8 = new java.lang.Object[r2];	 Catch:{ all -> 0x0059 }
        r0 = r0.getAbsolutePath();	 Catch:{ all -> 0x0059 }
        r8[r1] = r0;	 Catch:{ all -> 0x0059 }
        com.android.volley.VolleyLog.d(r7, r8);	 Catch:{ all -> 0x0059 }
    L_0x0057:
        monitor-exit(r6);
        return;
    L_0x0059:
        r7 = move-exception;
        monitor-exit(r6);
        throw r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.DiskBasedCache.put(java.lang.String, com.android.volley.Cache$Entry):void");
    }

    public synchronized void remove(String str) {
        boolean delete = getFileForKey(str).delete();
        removeEntry(str);
        if (!delete) {
            VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", str, getFilenameForKey(str));
        }
    }

    private String getFilenameForKey(String str) {
        int length = str.length() / 2;
        String valueOf = String.valueOf(str.substring(0, length).hashCode());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(valueOf);
        stringBuilder.append(String.valueOf(str.substring(length).hashCode()));
        return stringBuilder.toString();
    }

    public File getFileForKey(String str) {
        return new File(this.mRootDirectory, getFilenameForKey(str));
    }

    private void pruneIfNeeded(int i) {
        long j = (long) i;
        if (this.mTotalSize + j >= ((long) this.mMaxCacheSizeInBytes)) {
            int i2;
            int i3;
            long j2;
            int i4 = 0;
            if (VolleyLog.DEBUG) {
                VolleyLog.v("Pruning old cache entries.", new Object[i4]);
            }
            long j3 = r0.mTotalSize;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Iterator it = r0.mEntries.entrySet().iterator();
            int i5 = i4;
            while (true) {
                i2 = 2;
                i3 = 1;
                if (!it.hasNext()) {
                    j2 = elapsedRealtime;
                    break;
                }
                long j4;
                CacheHeader cacheHeader = (CacheHeader) ((Map.Entry) it.next()).getValue();
                if (getFileForKey(cacheHeader.key).delete()) {
                    j4 = j;
                    j2 = elapsedRealtime;
                    r0.mTotalSize -= cacheHeader.size;
                } else {
                    j4 = j;
                    j2 = elapsedRealtime;
                    Object[] objArr = new Object[i2];
                    objArr[0] = cacheHeader.key;
                    objArr[i3] = getFilenameForKey(cacheHeader.key);
                    VolleyLog.d("Could not delete cache entry for key=%s, filename=%s", objArr);
                }
                it.remove();
                i5++;
                if (((float) (r0.mTotalSize + j4)) < ((float) r0.mMaxCacheSizeInBytes) * 0.9f) {
                    break;
                }
                j = j4;
                elapsedRealtime = j2;
                Object obj = null;
            }
            if (VolleyLog.DEBUG) {
                r2 = new Object[3];
                r2[0] = Integer.valueOf(i5);
                r2[i3] = Long.valueOf(r0.mTotalSize - j3);
                r2[i2] = Long.valueOf(SystemClock.elapsedRealtime() - j2);
                VolleyLog.v("pruned %d files, %d bytes, %d ms", r2);
            }
        }
    }

    private void putEntry(String str, CacheHeader cacheHeader) {
        if (this.mEntries.containsKey(str)) {
            this.mTotalSize += cacheHeader.size - ((CacheHeader) this.mEntries.get(str)).size;
        } else {
            this.mTotalSize += cacheHeader.size;
        }
        this.mEntries.put(str, cacheHeader);
    }

    private void removeEntry(String str) {
        CacheHeader cacheHeader = (CacheHeader) this.mEntries.remove(str);
        if (cacheHeader != null) {
            this.mTotalSize -= cacheHeader.size;
        }
    }

    static byte[] streamToBytes(CountingInputStream countingInputStream, long j) throws IOException {
        long bytesRemaining = countingInputStream.bytesRemaining();
        if (j >= 0 && j <= bytesRemaining) {
            int i = (int) j;
            if (((long) i) == j) {
                byte[] bArr = new byte[i];
                new DataInputStream(countingInputStream).readFully(bArr);
                return bArr;
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("streamToBytes length=");
        stringBuilder.append(j);
        stringBuilder.append(", maxLength=");
        stringBuilder.append(bytesRemaining);
        throw new IOException(stringBuilder.toString());
    }

    InputStream createInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    OutputStream createOutputStream(File file) throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    private static int read(InputStream inputStream) throws IOException {
        int read = inputStream.read();
        if (read != -1) {
            return read;
        }
        throw new EOFException();
    }

    static void writeInt(OutputStream outputStream, int i) throws IOException {
        outputStream.write((i >> 0) & 255);
        outputStream.write((i >> 8) & 255);
        outputStream.write((i >> 16) & 255);
        outputStream.write((i >> 24) & 255);
    }

    static int readInt(InputStream inputStream) throws IOException {
        int i = 0;
        return (read(inputStream) << 24) | ((((read(inputStream) << i) | i) | (read(inputStream) << 8)) | (read(inputStream) << 16));
    }

    static void writeLong(OutputStream outputStream, long j) throws IOException {
        outputStream.write((byte) ((int) (j >>> null)));
        outputStream.write((byte) ((int) (j >>> 8)));
        outputStream.write((byte) ((int) (j >>> 16)));
        outputStream.write((byte) ((int) (j >>> 24)));
        outputStream.write((byte) ((int) (j >>> 32)));
        outputStream.write((byte) ((int) (j >>> 40)));
        outputStream.write((byte) ((int) (j >>> 48)));
        outputStream.write((byte) ((int) (j >>> 56)));
    }

    static long readLong(InputStream inputStream) throws IOException {
        long j = 255;
        return (((((((0 | ((((long) read(inputStream)) & j) << null)) | ((((long) read(inputStream)) & j) << 8)) | ((((long) read(inputStream)) & j) << 16)) | ((((long) read(inputStream)) & j) << 24)) | ((((long) read(inputStream)) & j) << 32)) | ((((long) read(inputStream)) & j) << 40)) | ((((long) read(inputStream)) & j) << 48)) | ((((long) read(inputStream)) & j) << 56);
    }

    static void writeString(OutputStream outputStream, String str) throws IOException {
        byte[] bytes = str.getBytes("UTF-8");
        writeLong(outputStream, (long) bytes.length);
        outputStream.write(bytes, 0, bytes.length);
    }

    static String readString(CountingInputStream countingInputStream) throws IOException {
        return new String(streamToBytes(countingInputStream, readLong(countingInputStream)), "UTF-8");
    }

    static void writeHeaderList(List<Header> list, OutputStream outputStream) throws IOException {
        if (list != null) {
            writeInt(outputStream, list.size());
            for (Header header : list) {
                writeString(outputStream, header.getName());
                writeString(outputStream, header.getValue());
            }
            return;
        }
        writeInt(outputStream, 0);
    }

    static List<Header> readHeaderList(CountingInputStream countingInputStream) throws IOException {
        int readInt = readInt(countingInputStream);
        List<Header> emptyList = readInt == 0 ? Collections.emptyList() : new ArrayList(readInt);
        for (int i = 0; i < readInt; i++) {
            emptyList.add(new Header(readString(countingInputStream).intern(), readString(countingInputStream).intern()));
        }
        return emptyList;
    }
}
