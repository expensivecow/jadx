package com.google.gson.stream;

import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class JsonReader implements Closeable {
    private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
    private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
    private static final int NUMBER_CHAR_DECIMAL = 3;
    private static final int NUMBER_CHAR_DIGIT = 2;
    private static final int NUMBER_CHAR_EXP_DIGIT = 7;
    private static final int NUMBER_CHAR_EXP_E = 5;
    private static final int NUMBER_CHAR_EXP_SIGN = 6;
    private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
    private static final int NUMBER_CHAR_NONE = 0;
    private static final int NUMBER_CHAR_SIGN = 1;
    private static final int PEEKED_BEGIN_ARRAY = 3;
    private static final int PEEKED_BEGIN_OBJECT = 1;
    private static final int PEEKED_BUFFERED = 11;
    private static final int PEEKED_DOUBLE_QUOTED = 9;
    private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
    private static final int PEEKED_END_ARRAY = 4;
    private static final int PEEKED_END_OBJECT = 2;
    private static final int PEEKED_EOF = 17;
    private static final int PEEKED_FALSE = 6;
    private static final int PEEKED_LONG = 15;
    private static final int PEEKED_NONE = 0;
    private static final int PEEKED_NULL = 7;
    private static final int PEEKED_NUMBER = 16;
    private static final int PEEKED_SINGLE_QUOTED = 8;
    private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
    private static final int PEEKED_TRUE = 5;
    private static final int PEEKED_UNQUOTED = 10;
    private static final int PEEKED_UNQUOTED_NAME = 14;
    private final char[] buffer = new char[1024];
    private final Reader in;
    private boolean lenient;
    private int limit;
    private int lineNumber;
    private int lineStart;
    private int[] pathIndices;
    private String[] pathNames;
    private int peeked;
    private long peekedLong;
    private int peekedNumberLength;
    private String peekedString;
    private int pos;
    private int[] stack;
    private int stackSize;

    static {
        JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
            public void promoteNameToValue(JsonReader jsonReader) throws IOException {
                if (jsonReader instanceof JsonTreeReader) {
                    ((JsonTreeReader) jsonReader).promoteNameToValue();
                    return;
                }
                int access$000 = jsonReader.peeked;
                if (access$000 == 0) {
                    access$000 = jsonReader.doPeek();
                }
                if (access$000 == 13) {
                    jsonReader.peeked = 9;
                } else if (access$000 == 12) {
                    jsonReader.peeked = 8;
                } else if (access$000 == 14) {
                    jsonReader.peeked = 10;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Expected a name but was ");
                    stringBuilder.append(jsonReader.peek());
                    stringBuilder.append(" ");
                    stringBuilder.append(" at line ");
                    stringBuilder.append(jsonReader.getLineNumber());
                    stringBuilder.append(" column ");
                    stringBuilder.append(jsonReader.getColumnNumber());
                    stringBuilder.append(" path ");
                    stringBuilder.append(jsonReader.getPath());
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
        };
    }

    public JsonReader(Reader reader) {
        boolean z = false;
        this.lenient = z;
        this.pos = z;
        this.limit = z;
        this.lineNumber = z;
        this.lineStart = z;
        this.peeked = z;
        int i = 32;
        this.stack = new int[i];
        this.stackSize = z;
        int[] iArr = this.stack;
        int i2 = this.stackSize;
        this.stackSize = i2 + 1;
        iArr[i2] = 6;
        this.pathNames = new String[i];
        this.pathIndices = new int[i];
        if (reader == null) {
            throw new NullPointerException("in == null");
        }
        this.in = reader;
    }

    public final void setLenient(boolean z) {
        this.lenient = z;
    }

    public final boolean isLenient() {
        return this.lenient;
    }

    public void beginArray() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 3) {
            i = 1;
            push(i);
            int i2 = this.stackSize - i;
            i = 0;
            this.pathIndices[i2] = i;
            this.peeked = i;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected BEGIN_ARRAY but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void endArray() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 4) {
            this.stackSize--;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected END_ARRAY but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void beginObject() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 1) {
            push(3);
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected BEGIN_OBJECT but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public void endObject() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 2) {
            this.stackSize--;
            this.pathNames[this.stackSize] = null;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            this.peeked = 0;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected END_OBJECT but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public boolean hasNext() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        return (i == 2 || i == 4) ? false : true;
    }

    public JsonToken peek() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        switch (i) {
            case 1:
                return JsonToken.BEGIN_OBJECT;
            case 2:
                return JsonToken.END_OBJECT;
            case 3:
                return JsonToken.BEGIN_ARRAY;
            case 4:
                return JsonToken.END_ARRAY;
            case 5:
            case 6:
                return JsonToken.BOOLEAN;
            case 7:
                return JsonToken.NULL;
            case 8:
            case 9:
            case 10:
            case 11:
                return JsonToken.STRING;
            case 12:
            case 13:
            case 14:
                return JsonToken.NAME;
            case 15:
            case 16:
                return JsonToken.NUMBER;
            case 17:
                return JsonToken.END_DOCUMENT;
            default:
                throw new AssertionError();
        }
    }

    private int doPeek() throws IOException {
        int nextNonWhitespace;
        int i;
        boolean z = true;
        boolean z2 = this.stack[this.stackSize - z];
        int i2 = 39;
        int i3 = 34;
        boolean z3 = true;
        boolean z4 = true;
        int i4 = 93;
        boolean z5 = true;
        int i5 = 59;
        int i6 = 44;
        boolean z6 = true;
        boolean z7 = true;
        if (z2 == z) {
            this.stack[this.stackSize - z] = z7;
        } else if (z2 == z7) {
            nextNonWhitespace = nextNonWhitespace(z);
            if (nextNonWhitespace != i6) {
                if (nextNonWhitespace == i5) {
                    checkLenient();
                } else if (nextNonWhitespace != i4) {
                    throw syntaxError("Unterminated array");
                } else {
                    this.peeked = z6;
                    return z6;
                }
            }
        } else {
            boolean z8 = true;
            if (z2 == z4 || z2 == z8) {
                int nextNonWhitespace2;
                this.stack[this.stackSize - z] = z6;
                int i7 = 125;
                if (z2 == z8) {
                    nextNonWhitespace2 = nextNonWhitespace(z);
                    if (nextNonWhitespace2 != i6) {
                        if (nextNonWhitespace2 == i5) {
                            checkLenient();
                        } else if (nextNonWhitespace2 != i7) {
                            throw syntaxError("Unterminated object");
                        } else {
                            this.peeked = z7;
                            return z7;
                        }
                    }
                }
                nextNonWhitespace2 = nextNonWhitespace(z);
                if (nextNonWhitespace2 == i3) {
                    i = 13;
                    this.peeked = i;
                    return i;
                } else if (nextNonWhitespace2 == i2) {
                    checkLenient();
                    i = 12;
                    this.peeked = i;
                    return i;
                } else if (nextNonWhitespace2 != i7) {
                    checkLenient();
                    this.pos -= z;
                    if (isLiteral((char) nextNonWhitespace2)) {
                        i = 14;
                        this.peeked = i;
                        return i;
                    }
                    throw syntaxError("Expected name");
                } else if (z2 != z8) {
                    this.peeked = z7;
                    return z7;
                } else {
                    throw syntaxError("Expected name");
                }
            } else if (z2 == z6) {
                this.stack[this.stackSize - z] = z8;
                nextNonWhitespace = nextNonWhitespace(z);
                if (nextNonWhitespace != 58) {
                    if (nextNonWhitespace != 61) {
                        throw syntaxError("Expected ':'");
                    }
                    checkLenient();
                    if ((this.pos < this.limit || fillBuffer(z)) && this.buffer[this.pos] == '>') {
                        this.pos += z;
                    }
                }
            } else if (z2) {
                if (this.lenient) {
                    consumeNonExecutePrefix();
                }
                this.stack[this.stackSize - z] = z5;
            } else if (z2 == z5) {
                if (nextNonWhitespace(false) == -1) {
                    i = 17;
                    this.peeked = i;
                    return i;
                }
                checkLenient();
                this.pos -= z;
            } else if (z2 == z3) {
                throw new IllegalStateException("JsonReader is closed");
            }
        }
        nextNonWhitespace = nextNonWhitespace(z);
        if (nextNonWhitespace == i3) {
            if (this.stackSize == z) {
                checkLenient();
            }
            i = 9;
            this.peeked = i;
            return i;
        } else if (nextNonWhitespace != i2) {
            if (!(nextNonWhitespace == i6 || nextNonWhitespace == i5)) {
                if (nextNonWhitespace == 91) {
                    this.peeked = z4;
                    return z4;
                } else if (nextNonWhitespace != i4) {
                    if (nextNonWhitespace != 123) {
                        this.pos -= z;
                        if (this.stackSize == z) {
                            checkLenient();
                        }
                        i = peekKeyword();
                        if (i != 0) {
                            return i;
                        }
                        i = peekNumber();
                        if (i != 0) {
                            return i;
                        }
                        if (isLiteral(this.buffer[this.pos])) {
                            checkLenient();
                            i = 10;
                            this.peeked = i;
                            return i;
                        }
                        throw syntaxError("Expected value");
                    }
                    this.peeked = z;
                    return z;
                } else if (z2 == z) {
                    this.peeked = z6;
                    return z6;
                }
            }
            if (z2 == z || z2 == z7) {
                checkLenient();
                this.pos -= z;
                this.peeked = z5;
                return z5;
            }
            throw syntaxError("Unexpected value");
        } else {
            checkLenient();
            this.peeked = z3;
            return z3;
        }
    }

    private int peekKeyword() throws IOException {
        String str;
        int i;
        char c = this.buffer[this.pos];
        int i2 = 0;
        String str2;
        if (c == 't' || c == 'T') {
            str = "true";
            str2 = "TRUE";
            i = 5;
        } else if (c == 'f' || c == 'F') {
            str = "false";
            str2 = "FALSE";
            i = 6;
        } else if (c != 'n' && c != 'N') {
            return i2;
        } else {
            str = "null";
            str2 = "NULL";
            i = 7;
        }
        int length = str.length();
        int i3 = 1;
        while (i3 < length) {
            if (this.pos + i3 >= this.limit && !fillBuffer(i3 + 1)) {
                return i2;
            }
            char c2 = this.buffer[this.pos + i3];
            if (c2 != str.charAt(i3) && c2 != str2.charAt(i3)) {
                return i2;
            }
            i3++;
        }
        if ((this.pos + length < this.limit || fillBuffer(length + 1)) && isLiteral(this.buffer[this.pos + length])) {
            return i2;
        }
        this.pos += length;
        this.peeked = i;
        return i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int peekNumber() throws java.io.IOException {
        /*
        r21 = this;
        r0 = r21;
        r1 = r0.buffer;
        r2 = r0.pos;
        r3 = r0.limit;
        r6 = 1;
        r7 = 0;
        r8 = r3;
        r10 = r6;
        r3 = r7;
        r9 = r3;
        r13 = r9;
        r11 = 0;
    L_0x0011:
        r14 = r2 + r3;
        r15 = 2;
        if (r14 != r8) goto L_0x0028;
    L_0x0016:
        r2 = r1.length;
        if (r3 != r2) goto L_0x001a;
    L_0x0019:
        return r7;
    L_0x001a:
        r2 = r3 + 1;
        r2 = r0.fillBuffer(r2);
        if (r2 != 0) goto L_0x0024;
    L_0x0022:
        goto L_0x0099;
    L_0x0024:
        r2 = r0.pos;
        r8 = r0.limit;
    L_0x0028:
        r14 = r2 + r3;
        r14 = r1[r14];
        r7 = 43;
        r4 = 3;
        r5 = 5;
        if (r14 == r7) goto L_0x00e9;
    L_0x0032:
        r7 = 69;
        if (r14 == r7) goto L_0x00dd;
    L_0x0036:
        r7 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r14 == r7) goto L_0x00dd;
    L_0x003a:
        switch(r14) {
            case 45: goto L_0x00d0;
            case 46: goto L_0x00c9;
            default: goto L_0x003d;
        };
    L_0x003d:
        r7 = 48;
        if (r14 < r7) goto L_0x0093;
    L_0x0041:
        r7 = 57;
        if (r14 <= r7) goto L_0x0046;
    L_0x0045:
        goto L_0x0093;
    L_0x0046:
        if (r9 == r6) goto L_0x0088;
    L_0x0048:
        if (r9 != 0) goto L_0x004b;
    L_0x004a:
        goto L_0x0088;
    L_0x004b:
        if (r9 != r15) goto L_0x0077;
    L_0x004d:
        r18 = 0;
        r4 = (r11 > r18 ? 1 : (r11 == r18 ? 0 : -1));
        if (r4 != 0) goto L_0x0055;
    L_0x0053:
        r4 = 0;
        return r4;
    L_0x0055:
        r4 = 10;
        r4 = r4 * r11;
        r14 = r14 + -48;
        r14 = (long) r14;
        r16 = r4 - r14;
        r4 = -922337203685477580; // 0xf333333333333334 float:4.1723254E-8 double:-8.390303882365713E246;
        r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1));
        if (r7 > 0) goto L_0x0071;
    L_0x0066:
        r7 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1));
        if (r7 != 0) goto L_0x006f;
    L_0x006a:
        r4 = (r16 > r11 ? 1 : (r16 == r11 ? 0 : -1));
        if (r4 >= 0) goto L_0x006f;
    L_0x006e:
        goto L_0x0071;
    L_0x006f:
        r4 = 0;
        goto L_0x0072;
    L_0x0071:
        r4 = r6;
    L_0x0072:
        r4 = r4 & r10;
        r10 = r4;
        r11 = r16;
        goto L_0x0090;
    L_0x0077:
        r18 = 0;
        if (r9 != r4) goto L_0x007f;
    L_0x007b:
        r7 = 0;
        r9 = 4;
        goto L_0x00f0;
    L_0x007f:
        if (r9 == r5) goto L_0x0084;
    L_0x0081:
        r4 = 6;
        if (r9 != r4) goto L_0x0090;
    L_0x0084:
        r7 = 0;
        r9 = 7;
        goto L_0x00f0;
    L_0x0088:
        r18 = 0;
        r14 = r14 + -48;
        r4 = -r14;
        r4 = (long) r4;
        r11 = r4;
        r9 = r15;
    L_0x0090:
        r7 = 0;
        goto L_0x00f0;
    L_0x0093:
        r1 = r0.isLiteral(r14);
        if (r1 != 0) goto L_0x00c7;
    L_0x0099:
        if (r9 != r15) goto L_0x00b5;
    L_0x009b:
        if (r10 == 0) goto L_0x00b5;
    L_0x009d:
        r1 = -9223372036854775808;
        r4 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1));
        if (r4 != 0) goto L_0x00a5;
    L_0x00a3:
        if (r13 == 0) goto L_0x00b5;
    L_0x00a5:
        if (r13 == 0) goto L_0x00a8;
    L_0x00a7:
        goto L_0x00a9;
    L_0x00a8:
        r11 = -r11;
    L_0x00a9:
        r0.peekedLong = r11;
        r1 = r0.pos;
        r1 = r1 + r3;
        r0.pos = r1;
        r1 = 15;
        r0.peeked = r1;
        return r1;
    L_0x00b5:
        if (r9 == r15) goto L_0x00c0;
    L_0x00b7:
        r1 = 4;
        if (r9 == r1) goto L_0x00c0;
    L_0x00ba:
        r1 = 7;
        if (r9 != r1) goto L_0x00be;
    L_0x00bd:
        goto L_0x00c0;
    L_0x00be:
        r7 = 0;
        return r7;
    L_0x00c0:
        r0.peekedNumberLength = r3;
        r1 = 16;
        r0.peeked = r1;
        return r1;
    L_0x00c7:
        r7 = 0;
        return r7;
    L_0x00c9:
        r7 = 0;
        r18 = 0;
        if (r9 != r15) goto L_0x00cf;
    L_0x00ce:
        goto L_0x00ef;
    L_0x00cf:
        return r7;
    L_0x00d0:
        r4 = 6;
        r7 = 0;
        r18 = 0;
        if (r9 != 0) goto L_0x00d9;
    L_0x00d6:
        r9 = r6;
        r13 = r9;
        goto L_0x00f0;
    L_0x00d9:
        if (r9 != r5) goto L_0x00dc;
    L_0x00db:
        goto L_0x00ef;
    L_0x00dc:
        return r7;
    L_0x00dd:
        r7 = 0;
        r18 = 0;
        if (r9 == r15) goto L_0x00e7;
    L_0x00e2:
        r4 = 4;
        if (r9 != r4) goto L_0x00e6;
    L_0x00e5:
        goto L_0x00e7;
    L_0x00e6:
        return r7;
    L_0x00e7:
        r9 = r5;
        goto L_0x00f0;
    L_0x00e9:
        r4 = 6;
        r7 = 0;
        r18 = 0;
        if (r9 != r5) goto L_0x00f4;
    L_0x00ef:
        r9 = r4;
    L_0x00f0:
        r3 = r3 + 1;
        goto L_0x0011;
    L_0x00f4:
        return r7;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.peekNumber():int");
    }

    private boolean isLiteral(char c) throws IOException {
        switch (c) {
            case 9:
            case 10:
            case 12:
            case 13:
            case ' ':
            case ',':
            case ':':
            case '[':
            case ']':
            case '{':
            case '}':
                break;
            case '#':
            case '/':
            case ';':
            case '=':
            case '\\':
                checkLenient();
                break;
            default:
                return true;
        }
        return false;
    }

    public String nextName() throws IOException {
        String nextUnquotedValue;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 14) {
            nextUnquotedValue = nextUnquotedValue();
        } else if (i == 12) {
            nextUnquotedValue = nextQuotedValue('\'');
        } else if (i == 13) {
            nextUnquotedValue = nextQuotedValue('\"');
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a name but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at line ");
            stringBuilder.append(getLineNumber());
            stringBuilder.append(" column ");
            stringBuilder.append(getColumnNumber());
            stringBuilder.append(" path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        this.peeked = 0;
        this.pathNames[this.stackSize - 1] = nextUnquotedValue;
        return nextUnquotedValue;
    }

    public String nextString() throws IOException {
        String nextUnquotedValue;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 10) {
            nextUnquotedValue = nextUnquotedValue();
        } else if (i == 8) {
            nextUnquotedValue = nextQuotedValue('\'');
        } else if (i == 9) {
            nextUnquotedValue = nextQuotedValue('\"');
        } else if (i == 11) {
            nextUnquotedValue = this.peekedString;
            this.peekedString = null;
        } else if (i == 15) {
            nextUnquotedValue = Long.toString(this.peekedLong);
        } else if (i == 16) {
            nextUnquotedValue = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a string but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at line ");
            stringBuilder.append(getLineNumber());
            stringBuilder.append(" column ");
            stringBuilder.append(getColumnNumber());
            stringBuilder.append(" path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
        this.peeked = 0;
        int[] iArr = this.pathIndices;
        int i2 = this.stackSize - 1;
        iArr[i2] = iArr[i2] + 1;
        return nextUnquotedValue;
    }

    public boolean nextBoolean() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        boolean z = false;
        boolean z2 = true;
        int[] iArr;
        int i2;
        if (i == 5) {
            this.peeked = z;
            iArr = this.pathIndices;
            i2 = this.stackSize - z2;
            iArr[i2] = iArr[i2] + z2;
            return z2;
        } else if (i == 6) {
            this.peeked = z;
            iArr = this.pathIndices;
            i2 = this.stackSize - z2;
            iArr[i2] = iArr[i2] + z2;
            return z;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a boolean but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at line ");
            stringBuilder.append(getLineNumber());
            stringBuilder.append(" column ");
            stringBuilder.append(getColumnNumber());
            stringBuilder.append(" path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public void nextNull() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        if (i == 7) {
            this.peeked = 0;
            int[] iArr = this.pathIndices;
            int i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Expected null but was ");
        stringBuilder.append(peek());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new IllegalStateException(stringBuilder.toString());
    }

    public double nextDouble() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        int i2 = 0;
        int i3;
        if (i == 15) {
            this.peeked = i2;
            int[] iArr = this.pathIndices;
            i3 = this.stackSize - 1;
            iArr[i3] = iArr[i3] + 1;
            return (double) this.peekedLong;
        }
        int i4 = 11;
        if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            i3 = 8;
            if (i == i3 || i == 9) {
                this.peekedString = nextQuotedValue(i == i3 ? '\'' : '\"');
            } else if (i == 10) {
                this.peekedString = nextUnquotedValue();
            } else if (i != i4) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Expected a double but was ");
                stringBuilder.append(peek());
                stringBuilder.append(" at line ");
                stringBuilder.append(getLineNumber());
                stringBuilder.append(" column ");
                stringBuilder.append(getColumnNumber());
                stringBuilder.append(" path ");
                stringBuilder.append(getPath());
                throw new IllegalStateException(stringBuilder.toString());
            }
        }
        this.peeked = i4;
        double parseDouble = Double.parseDouble(this.peekedString);
        if (this.lenient || !(Double.isNaN(parseDouble) || Double.isInfinite(parseDouble))) {
            this.peekedString = null;
            this.peeked = i2;
            int[] iArr2 = this.pathIndices;
            i4 = this.stackSize - 1;
            iArr2[i4] = iArr2[i4] + 1;
            return parseDouble;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append("JSON forbids NaN and infinities: ");
        stringBuilder2.append(parseDouble);
        stringBuilder2.append(" at line ");
        stringBuilder2.append(getLineNumber());
        stringBuilder2.append(" column ");
        stringBuilder2.append(getColumnNumber());
        stringBuilder2.append(" path ");
        stringBuilder2.append(getPath());
        throw new MalformedJsonException(stringBuilder2.toString());
    }

    public long nextLong() throws IOException {
        StringBuilder stringBuilder;
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        int i2 = 0;
        int[] iArr;
        int i3;
        if (i == 15) {
            this.peeked = i2;
            iArr = this.pathIndices;
            i3 = this.stackSize - 1;
            iArr[i3] = iArr[i3] + 1;
            return this.peekedLong;
        } else if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            i3 = 8;
            if (i == i3 || i == 9) {
                this.peekedString = nextQuotedValue(i == i3 ? '\'' : '\"');
                try {
                    long parseLong = Long.parseLong(this.peekedString);
                    this.peeked = i2;
                    int[] iArr2 = this.pathIndices;
                    int i4 = this.stackSize - 1;
                    iArr2[i4] = iArr2[i4] + 1;
                    return parseLong;
                } catch (NumberFormatException unused) {
                    this.peeked = 11;
                    double parseDouble = Double.parseDouble(this.peekedString);
                    long j = (long) parseDouble;
                    if (((double) j) != parseDouble) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Expected a long but was ");
                        stringBuilder.append(this.peekedString);
                        stringBuilder.append(" at line ");
                        stringBuilder.append(getLineNumber());
                        stringBuilder.append(" column ");
                        stringBuilder.append(getColumnNumber());
                        stringBuilder.append(" path ");
                        stringBuilder.append(getPath());
                        throw new NumberFormatException(stringBuilder.toString());
                    }
                    this.peekedString = null;
                    this.peeked = i2;
                    iArr = this.pathIndices;
                    i3 = this.stackSize - 1;
                    iArr[i3] = iArr[i3] + 1;
                    return j;
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Expected a long but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at line ");
            stringBuilder.append(getLineNumber());
            stringBuilder.append(" column ");
            stringBuilder.append(getColumnNumber());
            stringBuilder.append(" path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    private String nextQuotedValue(char c) throws IOException {
        char[] cArr = this.buffer;
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        do {
            int i2 = this.pos;
            int i3 = this.limit;
            while (true) {
                int i4;
                int i5 = i2;
                while (true) {
                    i = 1;
                    if (i2 < i3) {
                        i4 = i2 + 1;
                        char c2 = cArr[i2];
                        if (c2 == c) {
                            this.pos = i4;
                            stringBuilder.append(cArr, i5, (i4 - i5) - i);
                            return stringBuilder.toString();
                        } else if (c2 == '\\') {
                            break;
                        } else {
                            if (c2 == 10) {
                                this.lineNumber += i;
                                this.lineStart = i4;
                            }
                            i2 = i4;
                        }
                    } else {
                        stringBuilder.append(cArr, i5, i2 - i5);
                        this.pos = i2;
                    }
                }
                this.pos = i4;
                stringBuilder.append(cArr, i5, (i4 - i5) - i);
                stringBuilder.append(readEscapeCharacter());
                i2 = this.pos;
                i3 = this.limit;
            }
        } while (fillBuffer(i));
        throw syntaxError("Unterminated string");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String nextUnquotedValue() throws java.io.IOException {
        /*
        r5 = this;
        r0 = 0;
        r1 = 0;
        r2 = r1;
    L_0x0003:
        r1 = r0;
    L_0x0004:
        r3 = r5.pos;
        r3 = r3 + r1;
        r4 = r5.limit;
        if (r3 >= r4) goto L_0x001c;
    L_0x000b:
        r3 = r5.buffer;
        r4 = r5.pos;
        r4 = r4 + r1;
        r3 = r3[r4];
        switch(r3) {
            case 9: goto L_0x002a;
            case 10: goto L_0x002a;
            case 12: goto L_0x002a;
            case 13: goto L_0x002a;
            case 32: goto L_0x002a;
            case 35: goto L_0x0018;
            case 44: goto L_0x002a;
            case 47: goto L_0x0018;
            case 58: goto L_0x002a;
            case 59: goto L_0x0018;
            case 61: goto L_0x0018;
            case 91: goto L_0x002a;
            case 92: goto L_0x0018;
            case 93: goto L_0x002a;
            case 123: goto L_0x002a;
            case 125: goto L_0x002a;
            default: goto L_0x0015;
        };
    L_0x0015:
        r1 = r1 + 1;
        goto L_0x0004;
    L_0x0018:
        r5.checkLenient();
        goto L_0x002a;
    L_0x001c:
        r3 = r5.buffer;
        r3 = r3.length;
        if (r1 >= r3) goto L_0x002c;
    L_0x0021:
        r3 = r1 + 1;
        r3 = r5.fillBuffer(r3);
        if (r3 == 0) goto L_0x002a;
    L_0x0029:
        goto L_0x0004;
    L_0x002a:
        r0 = r1;
        goto L_0x0046;
    L_0x002c:
        if (r2 != 0) goto L_0x0033;
    L_0x002e:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
    L_0x0033:
        r3 = r5.buffer;
        r4 = r5.pos;
        r2.append(r3, r4, r1);
        r3 = r5.pos;
        r3 = r3 + r1;
        r5.pos = r3;
        r1 = 1;
        r1 = r5.fillBuffer(r1);
        if (r1 != 0) goto L_0x0003;
    L_0x0046:
        if (r2 != 0) goto L_0x0052;
    L_0x0048:
        r1 = new java.lang.String;
        r2 = r5.buffer;
        r3 = r5.pos;
        r1.<init>(r2, r3, r0);
        goto L_0x005d;
    L_0x0052:
        r1 = r5.buffer;
        r3 = r5.pos;
        r2.append(r1, r3, r0);
        r1 = r2.toString();
    L_0x005d:
        r2 = r5.pos;
        r2 = r2 + r0;
        r5.pos = r2;
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.stream.JsonReader.nextUnquotedValue():java.lang.String");
    }

    private void skipQuotedValue(char c) throws IOException {
        char[] cArr = this.buffer;
        int i;
        do {
            int i2 = this.pos;
            int i3 = this.limit;
            while (true) {
                i = 1;
                if (i2 < i3) {
                    int i4 = i2 + 1;
                    char c2 = cArr[i2];
                    if (c2 == c) {
                        this.pos = i4;
                        return;
                    } else if (c2 == '\\') {
                        this.pos = i4;
                        readEscapeCharacter();
                        i2 = this.pos;
                        i3 = this.limit;
                    } else {
                        if (c2 == 10) {
                            this.lineNumber += i;
                            this.lineStart = i4;
                        }
                        i2 = i4;
                    }
                } else {
                    this.pos = i2;
                }
            }
        } while (fillBuffer(i));
        throw syntaxError("Unterminated string");
    }

    private void skipUnquotedValue() throws IOException {
        do {
            int i = 0;
            while (this.pos + i < this.limit) {
                switch (this.buffer[this.pos + i]) {
                    case 9:
                    case 10:
                    case 12:
                    case 13:
                    case ' ':
                    case ',':
                    case ':':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        break;
                    case '#':
                    case '/':
                    case ';':
                    case '=':
                    case '\\':
                        checkLenient();
                        break;
                    default:
                        i++;
                }
                this.pos += i;
                return;
            }
            this.pos += i;
        } while (fillBuffer(1));
    }

    public int nextInt() throws IOException {
        int i = this.peeked;
        if (i == 0) {
            i = doPeek();
        }
        int i2 = 0;
        StringBuilder stringBuilder;
        int[] iArr;
        if (i == 15) {
            i = (int) this.peekedLong;
            if (this.peekedLong != ((long) i)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Expected an int but was ");
                stringBuilder.append(this.peekedLong);
                stringBuilder.append(" at line ");
                stringBuilder.append(getLineNumber());
                stringBuilder.append(" column ");
                stringBuilder.append(getColumnNumber());
                stringBuilder.append(" path ");
                stringBuilder.append(getPath());
                throw new NumberFormatException(stringBuilder.toString());
            }
            this.peeked = i2;
            iArr = this.pathIndices;
            i2 = this.stackSize - 1;
            iArr[i2] = iArr[i2] + 1;
            return i;
        } else if (i == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
        } else {
            int i3 = 8;
            if (i == i3 || i == 9) {
                this.peekedString = nextQuotedValue(i == i3 ? '\'' : '\"');
                int i4;
                try {
                    i = Integer.parseInt(this.peekedString);
                    this.peeked = i2;
                    iArr = this.pathIndices;
                    i4 = this.stackSize - 1;
                    iArr[i4] = iArr[i4] + 1;
                    return i;
                } catch (NumberFormatException unused) {
                    this.peeked = 11;
                    double parseDouble = Double.parseDouble(this.peekedString);
                    i4 = (int) parseDouble;
                    if (((double) i4) != parseDouble) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Expected an int but was ");
                        stringBuilder.append(this.peekedString);
                        stringBuilder.append(" at line ");
                        stringBuilder.append(getLineNumber());
                        stringBuilder.append(" column ");
                        stringBuilder.append(getColumnNumber());
                        stringBuilder.append(" path ");
                        stringBuilder.append(getPath());
                        throw new NumberFormatException(stringBuilder.toString());
                    }
                    this.peekedString = null;
                    this.peeked = i2;
                    int[] iArr2 = this.pathIndices;
                    i3 = this.stackSize - 1;
                    iArr2[i3] = iArr2[i3] + 1;
                    return i4;
                }
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Expected an int but was ");
            stringBuilder.append(peek());
            stringBuilder.append(" at line ");
            stringBuilder.append(getLineNumber());
            stringBuilder.append(" column ");
            stringBuilder.append(getColumnNumber());
            stringBuilder.append(" path ");
            stringBuilder.append(getPath());
            throw new IllegalStateException(stringBuilder.toString());
        }
    }

    public void close() throws IOException {
        int i = 0;
        this.peeked = i;
        this.stack[i] = 8;
        this.stackSize = 1;
        this.in.close();
    }

    public void skipValue() throws IOException {
        int i;
        int i2 = 0;
        int i3 = i2;
        do {
            int i4 = this.peeked;
            if (i4 == 0) {
                i4 = doPeek();
            }
            int i5 = 3;
            i = 1;
            if (i4 == i5) {
                push(i);
                i3++;
            } else if (i4 == i) {
                push(i5);
                i3++;
            } else if (i4 == 4) {
                this.stackSize -= i;
                i3--;
            } else if (i4 == 2) {
                this.stackSize -= i;
                i3--;
            } else if (i4 == 14 || i4 == 10) {
                skipUnquotedValue();
            } else if (i4 == 8 || i4 == 12) {
                skipQuotedValue('\'');
            } else if (i4 == 9 || i4 == 13) {
                skipQuotedValue('\"');
            } else if (i4 == 16) {
                this.pos += this.peekedNumberLength;
            }
            this.peeked = i2;
        } while (i3 != 0);
        int[] iArr = this.pathIndices;
        i3 = this.stackSize - i;
        iArr[i3] = iArr[i3] + i;
        this.pathNames[this.stackSize - i] = "null";
    }

    private void push(int i) {
        if (this.stackSize == this.stack.length) {
            Object obj = new int[(this.stackSize * 2)];
            Object obj2 = new int[(this.stackSize * 2)];
            Object obj3 = new String[(this.stackSize * 2)];
            int i2 = 0;
            System.arraycopy(this.stack, i2, obj, i2, this.stackSize);
            System.arraycopy(this.pathIndices, i2, obj2, i2, this.stackSize);
            System.arraycopy(this.pathNames, i2, obj3, i2, this.stackSize);
            this.stack = obj;
            this.pathIndices = obj2;
            this.pathNames = obj3;
        }
        int[] iArr = this.stack;
        int i3 = this.stackSize;
        this.stackSize = i3 + 1;
        iArr[i3] = i;
    }

    private boolean fillBuffer(int i) throws IOException {
        boolean z;
        Object obj = this.buffer;
        this.lineStart -= this.pos;
        boolean z2 = false;
        if (this.limit != this.pos) {
            this.limit -= this.pos;
            System.arraycopy(obj, this.pos, obj, z2, this.limit);
        } else {
            this.limit = z2;
        }
        this.pos = z2;
        do {
            int read = this.in.read(obj, this.limit, obj.length - this.limit);
            if (read == -1) {
                return z2;
            }
            this.limit += read;
            z = true;
            if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && obj[z2] == 65279) {
                this.pos += z;
                this.lineStart += z;
                i++;
            }
        } while (this.limit < i);
        return z;
    }

    private int getLineNumber() {
        return this.lineNumber + 1;
    }

    private int getColumnNumber() {
        return (this.pos - this.lineStart) + 1;
    }

    private int nextNonWhitespace(boolean z) throws IOException {
        char[] cArr = this.buffer;
        int i = this.pos;
        int i2 = this.limit;
        while (true) {
            int i3 = 1;
            if (i == i2) {
                this.pos = i;
                if (fillBuffer(i3)) {
                    i = this.pos;
                    i2 = this.limit;
                } else if (!z) {
                    return -1;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("End of input at line ");
                    stringBuilder.append(getLineNumber());
                    stringBuilder.append(" column ");
                    stringBuilder.append(getColumnNumber());
                    throw new EOFException(stringBuilder.toString());
                }
            }
            int i4 = i + 1;
            char c = cArr[i];
            if (c == 10) {
                this.lineNumber += i3;
                this.lineStart = i4;
            } else if (!(c == ' ' || c == 13 || c == 9)) {
                char c2 = '/';
                if (c == c2) {
                    this.pos = i4;
                    int i5 = 2;
                    if (i4 == i2) {
                        this.pos -= i3;
                        boolean fillBuffer = fillBuffer(i5);
                        this.pos += i3;
                        if (!fillBuffer) {
                            return c;
                        }
                    }
                    checkLenient();
                    char c3 = cArr[this.pos];
                    if (c3 == '*') {
                        this.pos += i3;
                        if (skipTo("*/")) {
                            i = this.pos + i5;
                            i2 = this.limit;
                        } else {
                            throw syntaxError("Unterminated comment");
                        }
                    } else if (c3 != c2) {
                        return c;
                    } else {
                        this.pos += i3;
                        skipToEndOfLine();
                        i = this.pos;
                        i2 = this.limit;
                    }
                } else if (c == '#') {
                    this.pos = i4;
                    checkLenient();
                    skipToEndOfLine();
                    i = this.pos;
                    i2 = this.limit;
                } else {
                    this.pos = i4;
                    return c;
                }
            }
            i = i4;
        }
    }

    private void checkLenient() throws IOException {
        if (!this.lenient) {
            throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
        }
    }

    private void skipToEndOfLine() throws IOException {
        char c;
        do {
            int i = 1;
            if (this.pos < this.limit || fillBuffer(i)) {
                char[] cArr = this.buffer;
                int i2 = this.pos;
                this.pos = i2 + 1;
                c = cArr[i2];
                if (c == 10) {
                    this.lineNumber += i;
                    this.lineStart = this.pos;
                    return;
                }
            } else {
                return;
            }
        } while (c != 13);
    }

    private boolean skipTo(String str) throws IOException {
        while (true) {
            int i = 0;
            if (this.pos + str.length() > this.limit && !fillBuffer(str.length())) {
                return i;
            }
            boolean z = true;
            if (this.buffer[this.pos] == 10) {
                this.lineNumber += z;
                this.lineStart = this.pos + z;
            } else {
                while (i < str.length()) {
                    if (this.buffer[this.pos + i] == str.charAt(i)) {
                        i++;
                    }
                }
                return z;
            }
            this.pos += z;
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getClass().getSimpleName());
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        return stringBuilder.toString();
    }

    public String getPath() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('$');
        int i = this.stackSize;
        for (int i2 = 0; i2 < i; i2++) {
            switch (this.stack[i2]) {
                case 1:
                case 2:
                    stringBuilder.append('[');
                    stringBuilder.append(this.pathIndices[i2]);
                    stringBuilder.append(']');
                    break;
                case 3:
                case 4:
                case 5:
                    stringBuilder.append('.');
                    if (this.pathNames[i2] == null) {
                        break;
                    }
                    stringBuilder.append(this.pathNames[i2]);
                    break;
                default:
                    break;
            }
        }
        return stringBuilder.toString();
    }

    private char readEscapeCharacter() throws IOException {
        int i = 1;
        if (this.pos != this.limit || fillBuffer(i)) {
            char[] cArr = this.buffer;
            int i2 = this.pos;
            this.pos = i2 + 1;
            char c = cArr[i2];
            char c2 = 10;
            if (c == c2) {
                this.lineNumber += i;
                this.lineStart = this.pos;
            } else if (c == 'b') {
                return 8;
            } else {
                char c3 = 'f';
                if (c == c3) {
                    return 12;
                }
                if (c == 'n') {
                    return c2;
                }
                if (c == 'r') {
                    return 13;
                }
                switch (c) {
                    case 't':
                        return 9;
                    case 'u':
                        int i3 = 4;
                        if (this.pos + i3 <= this.limit || fillBuffer(i3)) {
                            c = 0;
                            int i4 = this.pos;
                            int i5 = i4 + 4;
                            while (i4 < i5) {
                                int i6;
                                char c4 = this.buffer[i4];
                                c = (char) (c << 4);
                                if (c4 >= '0' && c4 <= '9') {
                                    i6 = c4 - 48;
                                } else if (c4 >= 'a' && c4 <= c3) {
                                    i6 = (c4 - 97) + c2;
                                } else if (c4 < 'A' || c4 > 'F') {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("\\u");
                                    stringBuilder.append(new String(this.buffer, this.pos, i3));
                                    throw new NumberFormatException(stringBuilder.toString());
                                } else {
                                    i6 = (c4 - 65) + c2;
                                }
                                c = (char) (c + i6);
                                i4++;
                            }
                            this.pos += i3;
                            return c;
                        }
                        throw syntaxError("Unterminated escape sequence");
                }
            }
            return c;
        }
        throw syntaxError("Unterminated escape sequence");
    }

    private IOException syntaxError(String str) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(" at line ");
        stringBuilder.append(getLineNumber());
        stringBuilder.append(" column ");
        stringBuilder.append(getColumnNumber());
        stringBuilder.append(" path ");
        stringBuilder.append(getPath());
        throw new MalformedJsonException(stringBuilder.toString());
    }

    private void consumeNonExecutePrefix() throws IOException {
        boolean z = true;
        nextNonWhitespace(z);
        this.pos -= z;
        if (this.pos + NON_EXECUTE_PREFIX.length <= this.limit || fillBuffer(NON_EXECUTE_PREFIX.length)) {
            int i = 0;
            while (i < NON_EXECUTE_PREFIX.length) {
                if (this.buffer[this.pos + i] == NON_EXECUTE_PREFIX[i]) {
                    i++;
                } else {
                    return;
                }
            }
            this.pos += NON_EXECUTE_PREFIX.length;
        }
    }
}
