package com.android.volley.toolbox;

import com.android.volley.Cache.Entry;
import com.android.volley.Header;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyLog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class HttpHeaderParser {
    private static final String DEFAULT_CONTENT_CHARSET = "ISO-8859-1";
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String RFC1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static Entry parseCacheHeaders(NetworkResponse networkResponse) {
        int i;
        long j;
        long j2;
        int i2;
        NetworkResponse networkResponse2 = networkResponse;
        long currentTimeMillis = System.currentTimeMillis();
        Map map = networkResponse2.headers;
        String str = (String) map.get("Date");
        long parseDateAsEpoch = str != null ? parseDateAsEpoch(str) : 0;
        str = (String) map.get("Cache-Control");
        int i3 = 0;
        if (str != null) {
            String[] split = str.split(",");
            i = i3;
            j = 0;
            j2 = 0;
            while (i3 < split.length) {
                String trim = split[i3].trim();
                if (trim.equals("no-cache") || trim.equals("no-store")) {
                    return null;
                }
                if (trim.startsWith("max-age=")) {
                    try {
                        j = Long.parseLong(trim.substring(8));
                    } catch (Exception unused) {
                        i3++;
                    }
                } else if (trim.startsWith("stale-while-revalidate=")) {
                    j2 = Long.parseLong(trim.substring(23));
                } else if (trim.equals("must-revalidate") || trim.equals("proxy-revalidate")) {
                    i = 1;
                }
            }
            i2 = 1;
        } else {
            i = i3;
            i2 = i;
            j = 0;
            j2 = 0;
        }
        str = (String) map.get("Expires");
        long parseDateAsEpoch2 = str != null ? parseDateAsEpoch(str) : 0;
        str = (String) map.get("Last-Modified");
        long parseDateAsEpoch3 = str != null ? parseDateAsEpoch(str) : 0;
        str = (String) map.get("ETag");
        long j3;
        if (i2 != 0) {
            parseDateAsEpoch2 = 1000;
            j3 = currentTimeMillis + (j * parseDateAsEpoch2);
            currentTimeMillis = i != 0 ? j3 : j3 + (j2 * parseDateAsEpoch2);
            parseDateAsEpoch2 = j3;
        } else {
            j3 = 0;
            if (parseDateAsEpoch <= j3 || parseDateAsEpoch2 < parseDateAsEpoch) {
                currentTimeMillis = j3;
                parseDateAsEpoch2 = currentTimeMillis;
            } else {
                parseDateAsEpoch2 = currentTimeMillis + (parseDateAsEpoch2 - parseDateAsEpoch);
                currentTimeMillis = parseDateAsEpoch2;
            }
        }
        Entry entry = new Entry();
        entry.data = networkResponse2.data;
        entry.etag = str;
        entry.softTtl = parseDateAsEpoch2;
        entry.ttl = currentTimeMillis;
        entry.serverDate = parseDateAsEpoch;
        entry.lastModified = parseDateAsEpoch3;
        entry.responseHeaders = map;
        entry.allResponseHeaders = networkResponse2.allHeaders;
        return entry;
    }

    public static long parseDateAsEpoch(String str) {
        try {
            return newRfc1123Formatter().parse(str).getTime();
        } catch (Throwable e) {
            VolleyLog.e(e, "Unable to parse dateStr: %s, falling back to 0", str);
            return 0;
        }
    }

    static String formatEpochAsRfc1123(long j) {
        return newRfc1123Formatter().format(new Date(j));
    }

    private static SimpleDateFormat newRfc1123Formatter() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat;
    }

    public static String parseCharset(Map<String, String> map, String str) {
        String str2 = (String) map.get("Content-Type");
        if (str2 != null) {
            String[] split = str2.split(";");
            int i = 1;
            for (int i2 = i; i2 < split.length; i2++) {
                String[] split2 = split[i2].trim().split("=");
                if (split2.length == 2 && split2[0].equals("charset")) {
                    return split2[i];
                }
            }
        }
        return str;
    }

    public static String parseCharset(Map<String, String> map) {
        return parseCharset(map, "ISO-8859-1");
    }

    static Map<String, String> toHeaderMap(List<Header> list) {
        Map<String, String> treeMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
        for (Header header : list) {
            treeMap.put(header.getName(), header.getValue());
        }
        return treeMap;
    }

    static List<Header> toAllHeaderList(Map<String, String> map) {
        List<Header> arrayList = new ArrayList(map.size());
        for (Map.Entry entry : map.entrySet()) {
            arrayList.add(new Header((String) entry.getKey(), (String) entry.getValue()));
        }
        return arrayList;
    }
}
