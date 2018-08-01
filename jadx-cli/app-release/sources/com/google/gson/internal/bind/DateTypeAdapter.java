package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTypeAdapter extends TypeAdapter<Date> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
            return typeToken.getRawType() == Date.class ? new DateTypeAdapter() : null;
        }
    };
    private final DateFormat enUsFormat;
    private final DateFormat iso8601Format = buildIso8601Format();
    private final DateFormat localFormat;

    public DateTypeAdapter() {
        int i = 2;
        this.enUsFormat = DateFormat.getDateTimeInstance(i, i, Locale.US);
        this.localFormat = DateFormat.getDateTimeInstance(i, i);
    }

    private static DateFormat buildIso8601Format() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat;
    }

    public Date read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() != JsonToken.NULL) {
            return deserializeToDate(jsonReader.nextString());
        }
        jsonReader.nextNull();
        return null;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized java.util.Date deserializeToDate(java.lang.String r3) {
        /*
        r2 = this;
        monitor-enter(r2);
        r0 = r2.localFormat;	 Catch:{ ParseException -> 0x000b }
        r0 = r0.parse(r3);	 Catch:{ ParseException -> 0x000b }
        monitor-exit(r2);
        return r0;
    L_0x0009:
        r3 = move-exception;
        goto L_0x0022;
    L_0x000b:
        r0 = r2.enUsFormat;	 Catch:{ ParseException -> 0x0013 }
        r0 = r0.parse(r3);	 Catch:{ ParseException -> 0x0013 }
        monitor-exit(r2);
        return r0;
    L_0x0013:
        r0 = r2.iso8601Format;	 Catch:{ ParseException -> 0x001b }
        r0 = r0.parse(r3);	 Catch:{ ParseException -> 0x001b }
        monitor-exit(r2);
        return r0;
    L_0x001b:
        r0 = move-exception;
        r1 = new com.google.gson.JsonSyntaxException;	 Catch:{ all -> 0x0009 }
        r1.<init>(r3, r0);	 Catch:{ all -> 0x0009 }
        throw r1;	 Catch:{ all -> 0x0009 }
    L_0x0022:
        monitor-exit(r2);
        throw r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.bind.DateTypeAdapter.deserializeToDate(java.lang.String):java.util.Date");
    }

    public synchronized void write(JsonWriter jsonWriter, Date date) throws IOException {
        if (date == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(this.enUsFormat.format(date));
        }
    }
}
