package android.support.v4.util;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.io.PrintWriter;

@RestrictTo({Scope.LIBRARY_GROUP})
public final class TimeUtils {
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final int HUNDRED_DAY_FIELD_LEN = 19;
    private static final int SECONDS_PER_DAY = 86400;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    private static char[] sFormatStr = new char[24];
    private static final Object sFormatSync = new Object();

    private static int accumField(int i, int i2, boolean z, int i3) {
        int i4 = 3;
        if (i > 99 || (z && i3 >= i4)) {
            return i4 + i2;
        }
        int i5 = 2;
        return (i > 9 || (z && i3 >= i5)) ? i5 + i2 : (z || i > 0) ? 1 + i2 : 0;
    }

    private static int printField(char[] cArr, int i, char c, int i2, boolean z, int i3) {
        if (!z && i <= 0) {
            return i2;
        }
        int i4;
        if ((!z || i3 < 3) && i <= 99) {
            i4 = i2;
        } else {
            int i5 = i / 100;
            cArr[i2] = (char) (i5 + 48);
            i4 = i2 + 1;
            i -= i5 * 100;
        }
        if ((z && i3 >= 2) || i > 9 || i2 != i4) {
            i2 = i / 10;
            cArr[i4] = (char) (i2 + 48);
            i4++;
            i -= i2 * 10;
        }
        cArr[i4] = (char) (i + 48);
        i4++;
        cArr[i4] = c;
        return i4 + 1;
    }

    private static int formatDurationLocked(long j, int i) {
        long j2 = j;
        int i2 = i;
        if (sFormatStr.length < i2) {
            sFormatStr = new char[i2];
        }
        char[] cArr = sFormatStr;
        long j3 = 0;
        char c = ' ';
        boolean z = true;
        boolean z2 = false;
        int i3;
        if (j2 == j3) {
            i3 = i2 - 1;
            while (i3 > 0) {
                cArr[z2] = c;
            }
            cArr[z2] = '0';
            return z;
        }
        char c2;
        int i4;
        int i5;
        int i6;
        int i7;
        if (j2 > j3) {
            c2 = '+';
        } else {
            c2 = '-';
            j2 = -j2;
        }
        long j4 = 1000;
        int i8 = (int) (j2 % j4);
        i3 = (int) Math.floor((double) (j2 / j4));
        int i9 = 86400;
        if (i3 > i9) {
            i4 = i3 / i9;
            i3 -= i9 * i4;
        } else {
            i4 = z2;
        }
        if (i3 > 3600) {
            i9 = i3 / 3600;
            i3 -= i9 * 3600;
        } else {
            i9 = z2;
        }
        if (i3 > 60) {
            i5 = i3 / 60;
            i6 = i3 - (i5 * 60);
            i3 = i5;
        } else {
            i6 = i3;
            i3 = z2;
        }
        int i10 = 3;
        int i11 = 2;
        if (i2 != 0) {
            i5 = accumField(i4, z, z2, z2);
            i5 += accumField(i9, z, i5 > 0 ? z : z2, i11);
            i5 += accumField(i3, z, i5 > 0 ? z : z2, i11);
            i5 += accumField(i6, z, i5 > 0 ? z : z2, i11);
            i7 = z2;
            for (i5 += accumField(i8, i11, z, i5 > 0 ? i10 : z2) + z; i5 < i2; i5++) {
                cArr[i7] = c;
                i7++;
            }
        } else {
            i7 = z2;
        }
        cArr[i7] = c2;
        int i12 = i7 + 1;
        boolean z3 = i2 != 0 ? z : z2;
        int i13 = i12;
        int printField = printField(cArr, i4, 'd', i12, false, 0);
        printField = printField(cArr, i9, 'h', printField, printField != i13 ? z : false, z3 ? i11 : 0);
        printField = printField(cArr, i3, 'm', printField, printField != i13 ? z : false, z3 ? i11 : 0);
        printField = printField(cArr, i6, 's', printField, printField != i13 ? z : false, z3 ? i11 : 0);
        char c3 = 'm';
        boolean z4 = true;
        i12 = (!z3 || printField == i13) ? 0 : i10;
        i3 = printField(cArr, i8, c3, printField, z4, i12);
        cArr[i3] = 's';
        return i3 + z;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, StringBuilder stringBuilder) {
        synchronized (sFormatSync) {
            int i = 0;
            stringBuilder.append(sFormatStr, i, formatDurationLocked(j, i));
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, PrintWriter printWriter, int i) {
        synchronized (sFormatSync) {
            printWriter.print(new String(sFormatStr, 0, formatDurationLocked(j, i)));
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, PrintWriter printWriter) {
        formatDuration(j, printWriter, 0);
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static void formatDuration(long j, long j2, PrintWriter printWriter) {
        if (j == 0) {
            printWriter.print("--");
        } else {
            formatDuration(j - j2, printWriter, 0);
        }
    }

    private TimeUtils() {
    }
}
