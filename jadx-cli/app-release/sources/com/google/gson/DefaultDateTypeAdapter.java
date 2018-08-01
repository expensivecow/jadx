package com.google.gson;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

final class DefaultDateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
    private final DateFormat enUsFormat;
    private final DateFormat iso8601Format;
    private final DateFormat localFormat;

    DefaultDateTypeAdapter() {
        int i = 2;
        this(DateFormat.getDateTimeInstance(i, i, Locale.US), DateFormat.getDateTimeInstance(i, i));
    }

    DefaultDateTypeAdapter(String str) {
        this(new SimpleDateFormat(str, Locale.US), new SimpleDateFormat(str));
    }

    DefaultDateTypeAdapter(int i) {
        this(DateFormat.getDateInstance(i, Locale.US), DateFormat.getDateInstance(i));
    }

    public DefaultDateTypeAdapter(int i, int i2) {
        this(DateFormat.getDateTimeInstance(i, i2, Locale.US), DateFormat.getDateTimeInstance(i, i2));
    }

    DefaultDateTypeAdapter(DateFormat dateFormat, DateFormat dateFormat2) {
        this.enUsFormat = dateFormat;
        this.localFormat = dateFormat2;
        this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonPrimitive;
        synchronized (this.localFormat) {
            jsonPrimitive = new JsonPrimitive(this.enUsFormat.format(date));
        }
        return jsonPrimitive;
    }

    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement instanceof JsonPrimitive) {
            Date deserializeToDate = deserializeToDate(jsonElement);
            if (type == Date.class) {
                return deserializeToDate;
            }
            if (type == Timestamp.class) {
                return new Timestamp(deserializeToDate.getTime());
            }
            if (type == java.sql.Date.class) {
                return new java.sql.Date(deserializeToDate.getTime());
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getClass());
            stringBuilder.append(" cannot deserialize to ");
            stringBuilder.append(type);
            throw new IllegalArgumentException(stringBuilder.toString());
        }
        throw new JsonParseException("The date should be a string value");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Date deserializeToDate(com.google.gson.JsonElement r4) {
        /*
        r3 = this;
        r0 = r3.localFormat;
        monitor-enter(r0);
        r1 = r3.localFormat;	 Catch:{ ParseException -> 0x0011 }
        r2 = r4.getAsString();	 Catch:{ ParseException -> 0x0011 }
        r1 = r1.parse(r2);	 Catch:{ ParseException -> 0x0011 }
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        return r1;
    L_0x000f:
        r4 = move-exception;
        goto L_0x0034;
    L_0x0011:
        r1 = r3.enUsFormat;	 Catch:{ ParseException -> 0x001d }
        r2 = r4.getAsString();	 Catch:{ ParseException -> 0x001d }
        r1 = r1.parse(r2);	 Catch:{ ParseException -> 0x001d }
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        return r1;
    L_0x001d:
        r1 = r3.iso8601Format;	 Catch:{ ParseException -> 0x0029 }
        r2 = r4.getAsString();	 Catch:{ ParseException -> 0x0029 }
        r1 = r1.parse(r2);	 Catch:{ ParseException -> 0x0029 }
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        return r1;
    L_0x0029:
        r1 = move-exception;
        r2 = new com.google.gson.JsonSyntaxException;	 Catch:{ all -> 0x000f }
        r4 = r4.getAsString();	 Catch:{ all -> 0x000f }
        r2.<init>(r4, r1);	 Catch:{ all -> 0x000f }
        throw r2;	 Catch:{ all -> 0x000f }
    L_0x0034:
        monitor-exit(r0);	 Catch:{ all -> 0x000f }
        throw r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.DefaultDateTypeAdapter.deserializeToDate(com.google.gson.JsonElement):java.util.Date");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DefaultDateTypeAdapter.class.getSimpleName());
        stringBuilder.append('(');
        stringBuilder.append(this.localFormat.getClass().getSimpleName());
        stringBuilder.append(')');
        return stringBuilder.toString();
    }
}
