package com.android.volley.toolbox;

import android.os.SystemClock;
import com.android.volley.Cache.Entry;
import com.android.volley.Header;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class BasicNetwork implements Network {
    protected static final boolean DEBUG = VolleyLog.DEBUG;
    private static final int DEFAULT_POOL_SIZE = 4096;
    private static final int SLOW_REQUEST_THRESHOLD_MS = 3000;
    private final BaseHttpStack mBaseHttpStack;
    @Deprecated
    protected final HttpStack mHttpStack;
    protected final ByteArrayPool mPool;

    @Deprecated
    public BasicNetwork(HttpStack httpStack) {
        this(httpStack, new ByteArrayPool(4096));
    }

    @Deprecated
    public BasicNetwork(HttpStack httpStack, ByteArrayPool byteArrayPool) {
        this.mHttpStack = httpStack;
        this.mBaseHttpStack = new AdaptedHttpStack(httpStack);
        this.mPool = byteArrayPool;
    }

    public BasicNetwork(BaseHttpStack baseHttpStack) {
        this(baseHttpStack, new ByteArrayPool(4096));
    }

    public BasicNetwork(BaseHttpStack baseHttpStack, ByteArrayPool byteArrayPool) {
        this.mBaseHttpStack = baseHttpStack;
        this.mHttpStack = baseHttpStack;
        this.mPool = byteArrayPool;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.android.volley.NetworkResponse performRequest(com.android.volley.Request<?> r29) throws com.android.volley.VolleyError {
        /*
        r28 = this;
        r7 = r28;
        r8 = r29;
        r9 = android.os.SystemClock.elapsedRealtime();
    L_0x0008:
        r1 = java.util.Collections.emptyList();
        r11 = 0;
        r2 = 0;
        r3 = r29.getCacheEntry();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00c0 }
        r3 = r7.getCacheHeaders(r3);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00c0 }
        r4 = r7.mBaseHttpStack;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00c0 }
        r12 = r4.executeRequest(r8, r3);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00c0 }
        r14 = r12.getStatusCode();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00bb }
        r13 = r12.getHeaders();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00bb }
        r1 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        if (r14 != r1) goto L_0x0064;
    L_0x0028:
        r1 = r29.getCacheEntry();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        if (r1 != 0) goto L_0x0043;
    L_0x002e:
        r1 = new com.android.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r16 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        r17 = 0;
        r18 = 1;
        r3 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r19 = r3 - r9;
        r15 = r1;
        r21 = r13;
        r15.<init>(r16, r17, r18, r19, r21);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        return r1;
    L_0x0043:
        r27 = combineHeaders(r13, r1);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r3 = new com.android.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r22 = 304; // 0x130 float:4.26E-43 double:1.5E-321;
        r1 = r1.data;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r24 = 1;
        r4 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r25 = r4 - r9;
        r21 = r3;
        r23 = r1;
        r21.<init>(r22, r23, r24, r25, r27);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        return r3;
    L_0x005d:
        r0 = move-exception;
        r1 = r0;
        r15 = r2;
        r19 = r13;
        goto L_0x00c6;
    L_0x0064:
        r1 = r12.getContent();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00b4 }
        if (r1 == 0) goto L_0x0073;
    L_0x006a:
        r3 = r12.getContentLength();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        r1 = r7.inputStreamToBytes(r1, r3);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x005d }
        goto L_0x0075;
    L_0x0073:
        r1 = new byte[r11];	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00b4 }
    L_0x0075:
        r20 = r1;
        r1 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00ac }
        r3 = r1 - r9;
        r1 = r7;
        r2 = r3;
        r4 = r8;
        r5 = r20;
        r6 = r14;
        r1.logSlowRequests(r2, r4, r5, r6);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00ac }
        r1 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r14 < r1) goto L_0x00a3;
    L_0x008a:
        r1 = 299; // 0x12b float:4.19E-43 double:1.477E-321;
        if (r14 <= r1) goto L_0x008f;
    L_0x008e:
        goto L_0x00a3;
    L_0x008f:
        r1 = new com.android.volley.NetworkResponse;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00ac }
        r16 = 0;
        r2 = android.os.SystemClock.elapsedRealtime();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00ac }
        r17 = r2 - r9;
        r3 = r13;
        r13 = r1;
        r15 = r20;
        r19 = r3;
        r13.<init>(r14, r15, r16, r17, r19);	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00aa }
        return r1;
    L_0x00a3:
        r3 = r13;
        r1 = new java.io.IOException;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00aa }
        r1.<init>();	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00aa }
        throw r1;	 Catch:{ SocketTimeoutException -> 0x016a, MalformedURLException -> 0x014d, IOException -> 0x00aa }
    L_0x00aa:
        r0 = move-exception;
        goto L_0x00ae;
    L_0x00ac:
        r0 = move-exception;
        r3 = r13;
    L_0x00ae:
        r1 = r0;
        r19 = r3;
        r15 = r20;
        goto L_0x00c6;
    L_0x00b4:
        r0 = move-exception;
        r3 = r13;
        r1 = r0;
        r15 = r2;
        r19 = r3;
        goto L_0x00c6;
    L_0x00bb:
        r0 = move-exception;
        r19 = r1;
        r15 = r2;
        goto L_0x00c5;
    L_0x00c0:
        r0 = move-exception;
        r19 = r1;
        r12 = r2;
        r15 = r12;
    L_0x00c5:
        r1 = r0;
    L_0x00c6:
        if (r12 == 0) goto L_0x0147;
    L_0x00c8:
        r1 = r12.getStatusCode();
        r2 = "Unexpected response code %d for %s";
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = java.lang.Integer.valueOf(r1);
        r3[r11] = r4;
        r4 = 1;
        r5 = r29.getUrl();
        r3[r4] = r5;
        com.android.volley.VolleyLog.e(r2, r3);
        if (r15 == 0) goto L_0x013b;
    L_0x00e3:
        r2 = new com.android.volley.NetworkResponse;
        r16 = 0;
        r3 = android.os.SystemClock.elapsedRealtime();
        r17 = r3 - r9;
        r13 = r2;
        r14 = r1;
        r13.<init>(r14, r15, r16, r17, r19);
        r3 = 401; // 0x191 float:5.62E-43 double:1.98E-321;
        if (r1 == r3) goto L_0x012f;
    L_0x00f6:
        r3 = 403; // 0x193 float:5.65E-43 double:1.99E-321;
        if (r1 != r3) goto L_0x00fb;
    L_0x00fa:
        goto L_0x012f;
    L_0x00fb:
        r3 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        if (r1 < r3) goto L_0x0109;
    L_0x00ff:
        r3 = 499; // 0x1f3 float:6.99E-43 double:2.465E-321;
        if (r1 > r3) goto L_0x0109;
    L_0x0103:
        r1 = new com.android.volley.ClientError;
        r1.<init>(r2);
        throw r1;
    L_0x0109:
        r3 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
        if (r1 < r3) goto L_0x0129;
    L_0x010d:
        r3 = 599; // 0x257 float:8.4E-43 double:2.96E-321;
        if (r1 > r3) goto L_0x0129;
    L_0x0111:
        r1 = r29.shouldRetryServerErrors();
        if (r1 == 0) goto L_0x0123;
    L_0x0117:
        r1 = "server";
        r3 = new com.android.volley.ServerError;
        r3.<init>(r2);
        attemptRetryOnException(r1, r8, r3);
        goto L_0x0008;
    L_0x0123:
        r1 = new com.android.volley.ServerError;
        r1.<init>(r2);
        throw r1;
    L_0x0129:
        r1 = new com.android.volley.ServerError;
        r1.<init>(r2);
        throw r1;
    L_0x012f:
        r1 = "auth";
        r3 = new com.android.volley.AuthFailureError;
        r3.<init>(r2);
        attemptRetryOnException(r1, r8, r3);
        goto L_0x0008;
    L_0x013b:
        r1 = "network";
        r2 = new com.android.volley.NetworkError;
        r2.<init>();
        attemptRetryOnException(r1, r8, r2);
        goto L_0x0008;
    L_0x0147:
        r2 = new com.android.volley.NoConnectionError;
        r2.<init>(r1);
        throw r2;
    L_0x014d:
        r0 = move-exception;
        r1 = r0;
        r2 = new java.lang.RuntimeException;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Bad URL ";
        r3.append(r4);
        r4 = r29.getUrl();
        r3.append(r4);
        r3 = r3.toString();
        r2.<init>(r3, r1);
        throw r2;
    L_0x016a:
        r1 = "socket";
        r2 = new com.android.volley.TimeoutError;
        r2.<init>();
        attemptRetryOnException(r1, r8, r2);
        goto L_0x0008;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.volley.toolbox.BasicNetwork.performRequest(com.android.volley.Request):com.android.volley.NetworkResponse");
    }

    private void logSlowRequests(long j, Request<?> request, byte[] bArr, int i) {
        if (DEBUG || j > 3000) {
            String str = "HTTP response for request=<%s> [lifetime=%d], [size=%s], [rc=%d], [retryCount=%s]";
            Object[] objArr = new Object[5];
            objArr[0] = request;
            objArr[1] = Long.valueOf(j);
            objArr[2] = bArr != null ? Integer.valueOf(bArr.length) : "null";
            objArr[3] = Integer.valueOf(i);
            objArr[4] = Integer.valueOf(request.getRetryPolicy().getCurrentRetryCount());
            VolleyLog.d(str, objArr);
        }
    }

    private static void attemptRetryOnException(String str, Request<?> request, VolleyError volleyError) throws VolleyError {
        RetryPolicy retryPolicy = request.getRetryPolicy();
        int timeoutMs = request.getTimeoutMs();
        int i = 1;
        int i2 = 0;
        int i3 = 2;
        try {
            retryPolicy.retry(volleyError);
            Object[] objArr = new Object[i3];
            objArr[i2] = str;
            objArr[i] = Integer.valueOf(timeoutMs);
            request.addMarker(String.format("%s-retry [timeout=%s]", objArr));
        } catch (VolleyError volleyError2) {
            Object[] objArr2 = new Object[i3];
            objArr2[i2] = str;
            objArr2[i] = Integer.valueOf(timeoutMs);
            request.addMarker(String.format("%s-timeout-giveup [timeout=%s]", objArr2));
            throw volleyError2;
        }
    }

    private Map<String, String> getCacheHeaders(Entry entry) {
        if (entry == null) {
            return Collections.emptyMap();
        }
        Map<String, String> hashMap = new HashMap();
        if (entry.etag != null) {
            hashMap.put("If-None-Match", entry.etag);
        }
        if (entry.lastModified > 0) {
            hashMap.put("If-Modified-Since", HttpHeaderParser.formatEpochAsRfc1123(entry.lastModified));
        }
        return hashMap;
    }

    protected void logError(String str, String str2, long j) {
        r3 = new Object[3];
        int i = 1;
        r3[i] = Long.valueOf(SystemClock.elapsedRealtime() - j);
        r3[2] = str2;
        VolleyLog.v("HTTP ERROR(%s) %d ms to fetch %s", r3);
    }

    private byte[] inputStreamToBytes(InputStream inputStream, int i) throws IOException, ServerError {
        Throwable th;
        PoolingByteArrayOutputStream poolingByteArrayOutputStream = new PoolingByteArrayOutputStream(this.mPool, i);
        i = 0;
        byte[] bArr = null;
        if (inputStream == null) {
            try {
                throw new ServerError();
            } catch (Throwable th2) {
                th = th2;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException unused) {
                        VolleyLog.v("Error occurred when closing InputStream", new Object[i]);
                    }
                }
                this.mPool.returnBuf(bArr);
                poolingByteArrayOutputStream.close();
                throw th;
            }
        }
        byte[] buf = this.mPool.getBuf(1024);
        while (true) {
            try {
                int read = inputStream.read(buf);
                if (read == -1) {
                    break;
                }
                poolingByteArrayOutputStream.write(buf, i, read);
            } catch (Throwable th3) {
                byte[] bArr2 = buf;
                th = th3;
                bArr = bArr2;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException unused2) {
                        VolleyLog.v("Error occurred when closing InputStream", new Object[i]);
                    }
                }
                this.mPool.returnBuf(bArr);
                poolingByteArrayOutputStream.close();
                throw th;
            }
        }
        bArr = poolingByteArrayOutputStream.toByteArray();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException unused3) {
                VolleyLog.v("Error occurred when closing InputStream", new Object[i]);
            }
        }
        this.mPool.returnBuf(buf);
        poolingByteArrayOutputStream.close();
        return bArr;
    }

    @Deprecated
    protected static Map<String, String> convertHeaders(Header[] headerArr) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < headerArr.length; i++) {
            treeMap.put(headerArr[i].getName(), headerArr[i].getValue());
        }
        return treeMap;
    }

    private static List<Header> combineHeaders(List<Header> list, Entry entry) {
        Set treeSet = new TreeSet(String.CASE_INSENSITIVE_ORDER);
        if (!list.isEmpty()) {
            for (Header name : list) {
                treeSet.add(name.getName());
            }
        }
        List<Header> arrayList = new ArrayList(list);
        if (entry.allResponseHeaders != null) {
            if (!entry.allResponseHeaders.isEmpty()) {
                for (Header header : entry.allResponseHeaders) {
                    if (!treeSet.contains(header.getName())) {
                        arrayList.add(header);
                    }
                }
            }
        } else if (!entry.responseHeaders.isEmpty()) {
            for (Map.Entry entry2 : entry.responseHeaders.entrySet()) {
                if (!treeSet.contains(entry2.getKey())) {
                    arrayList.add(new Header((String) entry2.getKey(), (String) entry2.getValue()));
                }
            }
        }
        return arrayList;
    }
}
