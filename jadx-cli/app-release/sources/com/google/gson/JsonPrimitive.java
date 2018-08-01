package com.google.gson;

import com.google.gson.internal.C$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
    private static final Class<?>[] PRIMITIVE_TYPES;
    private Object value;

    JsonPrimitive deepCopy() {
        return this;
    }

    static {
        r0 = new Class[16];
        r0[0] = Integer.TYPE;
        r0[1] = Long.TYPE;
        r0[2] = Short.TYPE;
        r0[3] = Float.TYPE;
        r0[4] = Double.TYPE;
        r0[5] = Byte.TYPE;
        r0[6] = Boolean.TYPE;
        r0[7] = Character.TYPE;
        r0[8] = Integer.class;
        r0[9] = Long.class;
        r0[10] = Short.class;
        r0[11] = Float.class;
        r0[12] = Double.class;
        r0[13] = Byte.class;
        r0[14] = Boolean.class;
        r0[15] = Character.class;
        PRIMITIVE_TYPES = r0;
    }

    public JsonPrimitive(Boolean bool) {
        setValue(bool);
    }

    public JsonPrimitive(Number number) {
        setValue(number);
    }

    public JsonPrimitive(String str) {
        setValue(str);
    }

    public JsonPrimitive(Character ch) {
        setValue(ch);
    }

    JsonPrimitive(Object obj) {
        setValue(obj);
    }

    void setValue(Object obj) {
        if (obj instanceof Character) {
            this.value = String.valueOf(((Character) obj).charValue());
            return;
        }
        boolean z = (obj instanceof Number) || isPrimitiveOrString(obj);
        C$Gson$Preconditions.checkArgument(z);
        this.value = obj;
    }

    public boolean isBoolean() {
        return this.value instanceof Boolean;
    }

    Boolean getAsBooleanWrapper() {
        return (Boolean) this.value;
    }

    public boolean getAsBoolean() {
        if (isBoolean()) {
            return getAsBooleanWrapper().booleanValue();
        }
        return Boolean.parseBoolean(getAsString());
    }

    public boolean isNumber() {
        return this.value instanceof Number;
    }

    public Number getAsNumber() {
        return this.value instanceof String ? new LazilyParsedNumber((String) this.value) : (Number) this.value;
    }

    public boolean isString() {
        return this.value instanceof String;
    }

    public String getAsString() {
        if (isNumber()) {
            return getAsNumber().toString();
        }
        if (isBoolean()) {
            return getAsBooleanWrapper().toString();
        }
        return (String) this.value;
    }

    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    public BigDecimal getAsBigDecimal() {
        return this.value instanceof BigDecimal ? (BigDecimal) this.value : new BigDecimal(this.value.toString());
    }

    public BigInteger getAsBigInteger() {
        return this.value instanceof BigInteger ? (BigInteger) this.value : new BigInteger(this.value.toString());
    }

    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }

    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    public char getAsCharacter() {
        return getAsString().charAt(0);
    }

    private static boolean isPrimitiveOrString(Object obj) {
        boolean z = true;
        if (obj instanceof String) {
            return z;
        }
        Class cls = obj.getClass();
        Class[] clsArr = PRIMITIVE_TYPES;
        boolean z2 = false;
        int length = clsArr.length;
        for (int i = z2; i < length; i++) {
            if (clsArr[i].isAssignableFrom(cls)) {
                return z;
            }
        }
        return z2;
    }

    public int hashCode() {
        if (this.value == null) {
            return 31;
        }
        Object obj = 32;
        long longValue;
        if (isIntegral(this)) {
            longValue = getAsNumber().longValue();
            return (int) (longValue ^ (longValue >>> obj));
        } else if (!(this.value instanceof Number)) {
            return this.value.hashCode();
        } else {
            longValue = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (longValue ^ (longValue >>> obj));
        }
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return z;
        }
        boolean z2 = false;
        if (obj == null || getClass() != obj.getClass()) {
            return z2;
        }
        JsonPrimitive jsonPrimitive = (JsonPrimitive) obj;
        if (this.value == null) {
            if (jsonPrimitive.value != null) {
                z = z2;
            }
            return z;
        } else if (isIntegral(this) && isIntegral(jsonPrimitive)) {
            if (getAsNumber().longValue() != jsonPrimitive.getAsNumber().longValue()) {
                z = z2;
            }
            return z;
        } else if (!(this.value instanceof Number) || !(jsonPrimitive.value instanceof Number)) {
            return this.value.equals(jsonPrimitive.value);
        } else {
            double doubleValue = getAsNumber().doubleValue();
            double doubleValue2 = jsonPrimitive.getAsNumber().doubleValue();
            if (!(doubleValue == doubleValue2 || (Double.isNaN(doubleValue) && Double.isNaN(doubleValue2)))) {
                z = z2;
            }
            return z;
        }
    }

    private static boolean isIntegral(JsonPrimitive jsonPrimitive) {
        boolean z = false;
        if (!(jsonPrimitive.value instanceof Number)) {
            return z;
        }
        Number number = (Number) jsonPrimitive.value;
        if ((number instanceof BigInteger) || (number instanceof Long) || (number instanceof Integer) || (number instanceof Short) || (number instanceof Byte)) {
            z = true;
        }
        return z;
    }
}
