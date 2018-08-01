package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({Scope.LIBRARY_GROUP})
public class PathParser {
    private static final String LOGTAG = "PathParser";

    private static class ExtractFloatResult {
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    public static class PathDataNode {
        @RestrictTo({Scope.LIBRARY_GROUP})
        public float[] mParams;
        @RestrictTo({Scope.LIBRARY_GROUP})
        public char mType;

        PathDataNode(char c, float[] fArr) {
            this.mType = c;
            this.mParams = fArr;
        }

        PathDataNode(PathDataNode pathDataNode) {
            this.mType = pathDataNode.mType;
            this.mParams = PathParser.copyOfRange(pathDataNode.mParams, 0, pathDataNode.mParams.length);
        }

        public static void nodesToPath(PathDataNode[] pathDataNodeArr, Path path) {
            float[] fArr = new float[6];
            char c = 'm';
            for (int i = 0; i < pathDataNodeArr.length; i++) {
                addCommand(path, fArr, c, pathDataNodeArr[i].mType, pathDataNodeArr[i].mParams);
                c = pathDataNodeArr[i].mType;
            }
        }

        public void interpolatePathDataNode(PathDataNode pathDataNode, PathDataNode pathDataNode2, float f) {
            for (int i = 0; i < pathDataNode.mParams.length; i++) {
                this.mParams[i] = (pathDataNode.mParams[i] * (1.0f - f)) + (pathDataNode2.mParams[i] * f);
            }
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static void addCommand(android.graphics.Path r27, float[] r28, char r29, char r30, float[] r31) {
            /*
            r10 = r27;
            r13 = r31;
            r14 = 0;
            r0 = r28[r14];
            r15 = 1;
            r1 = r28[r15];
            r16 = 2;
            r2 = r28[r16];
            r17 = 3;
            r3 = r28[r17];
            r18 = 4;
            r4 = r28[r18];
            r19 = 5;
            r5 = r28[r19];
            switch(r30) {
                case 65: goto L_0x0033;
                case 67: goto L_0x0031;
                case 72: goto L_0x002e;
                case 76: goto L_0x001d;
                case 77: goto L_0x001d;
                case 81: goto L_0x002b;
                case 83: goto L_0x002b;
                case 84: goto L_0x001d;
                case 86: goto L_0x002e;
                case 90: goto L_0x0020;
                case 97: goto L_0x0033;
                case 99: goto L_0x0031;
                case 104: goto L_0x002e;
                case 108: goto L_0x001d;
                case 109: goto L_0x001d;
                case 113: goto L_0x002b;
                case 115: goto L_0x002b;
                case 116: goto L_0x001d;
                case 118: goto L_0x002e;
                case 122: goto L_0x0020;
                default: goto L_0x001d;
            };
        L_0x001d:
            r20 = r16;
            goto L_0x0036;
        L_0x0020:
            r27.close();
            r10.moveTo(r4, r5);
            r0 = r4;
            r2 = r0;
            r1 = r5;
            r3 = r1;
            goto L_0x001d;
        L_0x002b:
            r20 = r18;
            goto L_0x0036;
        L_0x002e:
            r20 = r15;
            goto L_0x0036;
        L_0x0031:
            r6 = 6;
            goto L_0x0034;
        L_0x0033:
            r6 = 7;
        L_0x0034:
            r20 = r6;
        L_0x0036:
            r8 = r0;
            r7 = r1;
            r21 = r4;
            r22 = r5;
            r9 = r14;
            r0 = r29;
        L_0x003f:
            r1 = r13.length;
            if (r9 >= r1) goto L_0x02dd;
        L_0x0042:
            r4 = 99;
            r5 = 84;
            r6 = 81;
            r15 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
            r14 = 113; // 0x71 float:1.58E-43 double:5.6E-322;
            r23 = 1073741824; // 0x40000000 float:2.0 double:5.304989477E-315;
            r1 = 0;
            switch(r30) {
                case 65: goto L_0x029b;
                case 67: goto L_0x0271;
                case 72: goto L_0x0263;
                case 76: goto L_0x0250;
                case 77: goto L_0x022e;
                case 81: goto L_0x020d;
                case 83: goto L_0x01d0;
                case 84: goto L_0x01a8;
                case 86: goto L_0x019a;
                case 97: goto L_0x014e;
                case 99: goto L_0x0122;
                case 104: goto L_0x0116;
                case 108: goto L_0x0103;
                case 109: goto L_0x00e1;
                case 113: goto L_0x00c1;
                case 115: goto L_0x0088;
                case 116: goto L_0x0063;
                case 118: goto L_0x0058;
                default: goto L_0x0052;
            };
        L_0x0052:
            r12 = r7;
            r11 = r8;
        L_0x0054:
            r26 = r9;
            goto L_0x02d5;
        L_0x0058:
            r0 = r9 + 0;
            r4 = r13[r0];
            r10.rLineTo(r1, r4);
            r0 = r13[r0];
            r7 = r7 + r0;
            goto L_0x0054;
        L_0x0063:
            if (r0 == r14) goto L_0x006e;
        L_0x0065:
            if (r0 == r15) goto L_0x006e;
        L_0x0067:
            if (r0 == r6) goto L_0x006e;
        L_0x0069:
            if (r0 != r5) goto L_0x006c;
        L_0x006b:
            goto L_0x006e;
        L_0x006c:
            r0 = r1;
            goto L_0x0072;
        L_0x006e:
            r1 = r8 - r2;
            r0 = r7 - r3;
        L_0x0072:
            r2 = r9 + 0;
            r3 = r13[r2];
            r4 = r9 + 1;
            r5 = r13[r4];
            r10.rQuadTo(r1, r0, r3, r5);
            r1 = r1 + r8;
            r0 = r0 + r7;
            r2 = r13[r2];
            r8 = r8 + r2;
            r2 = r13[r4];
            r7 = r7 + r2;
            r3 = r0;
            r2 = r1;
            goto L_0x0054;
        L_0x0088:
            if (r0 == r4) goto L_0x0099;
        L_0x008a:
            r4 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
            if (r0 == r4) goto L_0x0099;
        L_0x008e:
            r4 = 67;
            if (r0 == r4) goto L_0x0099;
        L_0x0092:
            r4 = 83;
            if (r0 != r4) goto L_0x0097;
        L_0x0096:
            goto L_0x0099;
        L_0x0097:
            r2 = r1;
            goto L_0x009f;
        L_0x0099:
            r0 = r8 - r2;
            r1 = r7 - r3;
            r2 = r1;
            r1 = r0;
        L_0x009f:
            r14 = r9 + 0;
            r3 = r13[r14];
            r15 = r9 + 1;
            r4 = r13[r15];
            r23 = r9 + 2;
            r5 = r13[r23];
            r24 = r9 + 3;
            r6 = r13[r24];
            r0 = r10;
            r0.rCubicTo(r1, r2, r3, r4, r5, r6);
            r0 = r13[r14];
            r0 = r0 + r8;
            r1 = r13[r15];
            r1 = r1 + r7;
            r2 = r13[r23];
            r8 = r8 + r2;
            r2 = r13[r24];
            r7 = r7 + r2;
            goto L_0x014a;
        L_0x00c1:
            r0 = r9 + 0;
            r1 = r13[r0];
            r2 = r9 + 1;
            r3 = r13[r2];
            r4 = r9 + 2;
            r5 = r13[r4];
            r6 = r9 + 3;
            r14 = r13[r6];
            r10.rQuadTo(r1, r3, r5, r14);
            r0 = r13[r0];
            r0 = r0 + r8;
            r1 = r13[r2];
            r1 = r1 + r7;
            r2 = r13[r4];
            r8 = r8 + r2;
            r2 = r13[r6];
            r7 = r7 + r2;
            goto L_0x014a;
        L_0x00e1:
            r0 = r9 + 0;
            r1 = r13[r0];
            r8 = r8 + r1;
            r1 = r9 + 1;
            r4 = r13[r1];
            r7 = r7 + r4;
            if (r9 <= 0) goto L_0x00f6;
        L_0x00ed:
            r0 = r13[r0];
            r1 = r13[r1];
            r10.rLineTo(r0, r1);
            goto L_0x0054;
        L_0x00f6:
            r0 = r13[r0];
            r1 = r13[r1];
            r10.rMoveTo(r0, r1);
            r22 = r7;
            r21 = r8;
            goto L_0x0054;
        L_0x0103:
            r0 = r9 + 0;
            r1 = r13[r0];
            r4 = r9 + 1;
            r5 = r13[r4];
            r10.rLineTo(r1, r5);
            r0 = r13[r0];
            r8 = r8 + r0;
            r0 = r13[r4];
            r7 = r7 + r0;
            goto L_0x0054;
        L_0x0116:
            r0 = r9 + 0;
            r4 = r13[r0];
            r10.rLineTo(r4, r1);
            r0 = r13[r0];
            r8 = r8 + r0;
            goto L_0x0054;
        L_0x0122:
            r0 = r9 + 0;
            r1 = r13[r0];
            r0 = r9 + 1;
            r2 = r13[r0];
            r14 = r9 + 2;
            r3 = r13[r14];
            r15 = r9 + 3;
            r4 = r13[r15];
            r23 = r9 + 4;
            r5 = r13[r23];
            r24 = r9 + 5;
            r6 = r13[r24];
            r0 = r10;
            r0.rCubicTo(r1, r2, r3, r4, r5, r6);
            r0 = r13[r14];
            r0 = r0 + r8;
            r1 = r13[r15];
            r1 = r1 + r7;
            r2 = r13[r23];
            r8 = r8 + r2;
            r2 = r13[r24];
            r7 = r7 + r2;
        L_0x014a:
            r2 = r0;
            r3 = r1;
            goto L_0x0054;
        L_0x014e:
            r14 = r9 + 5;
            r0 = r13[r14];
            r3 = r0 + r8;
            r15 = r9 + 6;
            r0 = r13[r15];
            r4 = r0 + r7;
            r0 = r9 + 0;
            r5 = r13[r0];
            r0 = r9 + 1;
            r6 = r13[r0];
            r0 = r9 + 2;
            r23 = r13[r0];
            r0 = r9 + 3;
            r0 = r13[r0];
            r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
            if (r0 == 0) goto L_0x0171;
        L_0x016e:
            r24 = 1;
            goto L_0x0173;
        L_0x0171:
            r24 = 0;
        L_0x0173:
            r0 = r9 + 4;
            r0 = r13[r0];
            r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
            if (r0 == 0) goto L_0x017e;
        L_0x017b:
            r25 = 1;
            goto L_0x0180;
        L_0x017e:
            r25 = 0;
        L_0x0180:
            r0 = r10;
            r1 = r8;
            r2 = r7;
            r12 = r7;
            r7 = r23;
            r11 = r8;
            r8 = r24;
            r26 = r9;
            r9 = r25;
            drawArc(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9);
            r0 = r13[r14];
            r8 = r11 + r0;
            r0 = r13[r15];
            r7 = r12 + r0;
            goto L_0x02d3;
        L_0x019a:
            r11 = r8;
            r26 = r9;
            r9 = r26 + 0;
            r0 = r13[r9];
            r10.lineTo(r11, r0);
            r7 = r13[r9];
            goto L_0x02d5;
        L_0x01a8:
            r12 = r7;
            r11 = r8;
            r26 = r9;
            if (r0 == r14) goto L_0x01b4;
        L_0x01ae:
            if (r0 == r15) goto L_0x01b4;
        L_0x01b0:
            if (r0 == r6) goto L_0x01b4;
        L_0x01b2:
            if (r0 != r5) goto L_0x01bd;
        L_0x01b4:
            r8 = r23 * r11;
            r8 = r8 - r2;
            r23 = r23 * r12;
            r7 = r23 - r3;
            r12 = r7;
            r11 = r8;
        L_0x01bd:
            r9 = r26 + 0;
            r0 = r13[r9];
            r1 = r26 + 1;
            r2 = r13[r1];
            r10.quadTo(r11, r12, r0, r2);
            r8 = r13[r9];
            r7 = r13[r1];
            r2 = r11;
            r3 = r12;
            goto L_0x02d5;
        L_0x01d0:
            r12 = r7;
            r11 = r8;
            r26 = r9;
            if (r0 == r4) goto L_0x01e6;
        L_0x01d6:
            r1 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
            if (r0 == r1) goto L_0x01e6;
        L_0x01da:
            r1 = 67;
            if (r0 == r1) goto L_0x01e6;
        L_0x01de:
            r1 = 83;
            if (r0 != r1) goto L_0x01e3;
        L_0x01e2:
            goto L_0x01e6;
        L_0x01e3:
            r1 = r11;
            r2 = r12;
            goto L_0x01f0;
        L_0x01e6:
            r8 = r23 * r11;
            r8 = r8 - r2;
            r23 = r23 * r12;
            r23 = r23 - r3;
            r1 = r8;
            r2 = r23;
        L_0x01f0:
            r9 = r26 + 0;
            r3 = r13[r9];
            r7 = r26 + 1;
            r4 = r13[r7];
            r8 = r26 + 2;
            r5 = r13[r8];
            r11 = r26 + 3;
            r6 = r13[r11];
            r0 = r10;
            r0.cubicTo(r1, r2, r3, r4, r5, r6);
            r0 = r13[r9];
            r1 = r13[r7];
            r8 = r13[r8];
            r7 = r13[r11];
            goto L_0x022a;
        L_0x020d:
            r26 = r9;
            r9 = r26 + 0;
            r0 = r13[r9];
            r1 = r26 + 1;
            r2 = r13[r1];
            r3 = r26 + 2;
            r4 = r13[r3];
            r5 = r26 + 3;
            r6 = r13[r5];
            r10.quadTo(r0, r2, r4, r6);
            r0 = r13[r9];
            r1 = r13[r1];
            r8 = r13[r3];
            r7 = r13[r5];
        L_0x022a:
            r2 = r0;
            r3 = r1;
            goto L_0x02d5;
        L_0x022e:
            r26 = r9;
            r9 = r26 + 0;
            r8 = r13[r9];
            r0 = r26 + 1;
            r7 = r13[r0];
            if (r26 <= 0) goto L_0x0243;
        L_0x023a:
            r1 = r13[r9];
            r0 = r13[r0];
            r10.lineTo(r1, r0);
            goto L_0x02d5;
        L_0x0243:
            r1 = r13[r9];
            r0 = r13[r0];
            r10.moveTo(r1, r0);
            r22 = r7;
            r21 = r8;
            goto L_0x02d5;
        L_0x0250:
            r26 = r9;
            r9 = r26 + 0;
            r0 = r13[r9];
            r1 = r26 + 1;
            r4 = r13[r1];
            r10.lineTo(r0, r4);
            r8 = r13[r9];
            r7 = r13[r1];
            goto L_0x02d5;
        L_0x0263:
            r12 = r7;
            r26 = r9;
            r9 = r26 + 0;
            r0 = r13[r9];
            r10.lineTo(r0, r12);
            r8 = r13[r9];
            goto L_0x02d5;
        L_0x0271:
            r26 = r9;
            r9 = r26 + 0;
            r1 = r13[r9];
            r9 = r26 + 1;
            r2 = r13[r9];
            r9 = r26 + 2;
            r3 = r13[r9];
            r7 = r26 + 3;
            r4 = r13[r7];
            r8 = r26 + 4;
            r5 = r13[r8];
            r11 = r26 + 5;
            r6 = r13[r11];
            r0 = r10;
            r0.cubicTo(r1, r2, r3, r4, r5, r6);
            r8 = r13[r8];
            r0 = r13[r11];
            r1 = r13[r9];
            r2 = r13[r7];
            r7 = r0;
            r3 = r2;
            r2 = r1;
            goto L_0x02d5;
        L_0x029b:
            r12 = r7;
            r11 = r8;
            r26 = r9;
            r14 = r26 + 5;
            r3 = r13[r14];
            r15 = r26 + 6;
            r4 = r13[r15];
            r9 = r26 + 0;
            r5 = r13[r9];
            r9 = r26 + 1;
            r6 = r13[r9];
            r9 = r26 + 2;
            r7 = r13[r9];
            r9 = r26 + 3;
            r0 = r13[r9];
            r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
            if (r0 == 0) goto L_0x02bd;
        L_0x02bb:
            r8 = 1;
            goto L_0x02be;
        L_0x02bd:
            r8 = 0;
        L_0x02be:
            r9 = r26 + 4;
            r0 = r13[r9];
            r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));
            if (r0 == 0) goto L_0x02c8;
        L_0x02c6:
            r9 = 1;
            goto L_0x02c9;
        L_0x02c8:
            r9 = 0;
        L_0x02c9:
            r0 = r10;
            r1 = r11;
            r2 = r12;
            drawArc(r0, r1, r2, r3, r4, r5, r6, r7, r8, r9);
            r8 = r13[r14];
            r7 = r13[r15];
        L_0x02d3:
            r3 = r7;
            r2 = r8;
        L_0x02d5:
            r9 = r26 + r20;
            r0 = r30;
            r14 = 0;
            r15 = 1;
            goto L_0x003f;
        L_0x02dd:
            r12 = r7;
            r1 = r14;
            r28[r1] = r8;
            r1 = 1;
            r28[r1] = r12;
            r28[r16] = r2;
            r28[r17] = r3;
            r28[r18] = r21;
            r28[r19] = r22;
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.PathParser.PathDataNode.addCommand(android.graphics.Path, float[], char, char, float[]):void");
        }

        private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean z, boolean z2) {
            float f8 = f;
            float f9 = f3;
            float f10 = f5;
            float f11 = f6;
            boolean z3 = z2;
            double toRadians = Math.toRadians((double) f7);
            double cos = Math.cos(toRadians);
            double sin = Math.sin(toRadians);
            double d = (double) f8;
            double d2 = toRadians;
            toRadians = (double) f2;
            double d3 = d;
            d = (double) f10;
            double d4 = ((d * cos) + (toRadians * sin)) / d;
            double d5 = toRadians;
            toRadians = (double) f11;
            double d6 = ((((double) (-f8)) * sin) + (toRadians * cos)) / toRadians;
            double d7 = (double) f4;
            double d8 = ((((double) f9) * cos) + (d7 * sin)) / d;
            double d9 = d;
            d = ((((double) (-f9)) * sin) + (d7 * cos)) / toRadians;
            d7 = d4 - d8;
            double d10 = d6 - d;
            double d11 = 2.0d;
            double d12 = (d4 + d8) / d11;
            double d13 = (d6 + d) / d11;
            double d14 = sin;
            sin = (d7 * d7) + (d10 * d10);
            d11 = 0.0d;
            if (sin == d11) {
                Log.w("PathParser", " Points are coincident");
                return;
            }
            double d15 = cos;
            cos = (1.0d / sin) - 0.25d;
            if (cos < d11) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Points are too far apart ");
                stringBuilder.append(sin);
                Log.w("PathParser", stringBuilder.toString());
                f8 = (float) (Math.sqrt(sin) / 1.99999d);
                drawArc(path, f, f2, f9, f4, f10 * f8, f11 * f8, f7, z, z2);
                return;
            }
            boolean z4 = z2;
            double sqrt = Math.sqrt(cos);
            d7 *= sqrt;
            sqrt *= d10;
            if (z == z4) {
                d12 -= sqrt;
                d13 += d7;
            } else {
                d12 += sqrt;
                d13 -= d7;
            }
            sqrt = Math.atan2(d6 - d13, d4 - d12);
            double atan2 = Math.atan2(d - d13, d8 - d12) - sqrt;
            if (z4 != (atan2 >= d11)) {
                double d16 = 6.283185307179586d;
                atan2 = atan2 > d11 ? atan2 - d16 : atan2 + d16;
            }
            d12 *= d9;
            d13 *= toRadians;
            arcToBezier(path, (d12 * d15) - (d13 * d14), (d12 * d14) + (d13 * d15), d9, toRadians, d3, d5, d2, sqrt, atan2);
        }

        private static void arcToBezier(Path path, double d, double d2, double d3, double d4, double d5, double d6, double d7, double d8, double d9) {
            double d10 = d3;
            int ceil = (int) Math.ceil(Math.abs((d9 * 4.0d) / 3.141592653589793d));
            double cos = Math.cos(d7);
            double sin = Math.sin(d7);
            double cos2 = Math.cos(d8);
            double sin2 = Math.sin(d8);
            double d11 = -d10;
            double d12 = d11 * cos;
            double d13 = d4 * sin;
            d11 *= sin;
            double d14 = d4 * cos;
            sin2 = (sin2 * d11) + (cos2 * d14);
            double d15 = d9 / ((double) ceil);
            int i = 0;
            double d16 = d6;
            double d17 = sin2;
            double d18 = (d12 * sin2) - (d13 * cos2);
            double d19 = d5;
            double d20 = d8;
            while (i < ceil) {
                double d21 = d11;
                d11 = d20 + d15;
                double sin3 = Math.sin(d11);
                double cos3 = Math.cos(d11);
                double d22 = d15;
                d15 = (d + ((d10 * cos) * cos3)) - (d13 * sin3);
                d10 = (d2 + ((d10 * sin) * cos3)) + (d14 * sin3);
                double d23 = (d12 * sin3) - (d13 * cos3);
                sin3 = (sin3 * d21) + (cos3 * d14);
                d20 = d11 - d20;
                double d24 = d14;
                d14 = Math.tan(d20 / 2.0d);
                cos3 = 3.0d;
                double d25 = d11;
                d20 = (Math.sin(d20) * (Math.sqrt(4.0d + ((cos3 * d14) * d14)) - 1.0d)) / cos3;
                d11 = d19 + (d18 * d20);
                d14 = d16 + (d17 * d20);
                int i2 = ceil;
                double d26 = cos;
                double d27 = d15 - (d20 * d23);
                d20 = d10 - (d20 * sin3);
                float f = 0.0f;
                double d28 = sin;
                Path path2 = path;
                path2.rLineTo(f, f);
                path2.cubicTo((float) d11, (float) d14, (float) d27, (float) d20, (float) d15, (float) d10);
                i++;
                d16 = d10;
                d19 = d15;
                d11 = d21;
                d17 = sin3;
                d18 = d23;
                d15 = d22;
                d14 = d24;
                d20 = d25;
                ceil = i2;
                cos = d26;
                sin = d28;
                d10 = d3;
            }
        }
    }

    static float[] copyOfRange(float[] fArr, int i, int i2) {
        if (i > i2) {
            throw new IllegalArgumentException();
        }
        int length = fArr.length;
        if (i < 0 || i > length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        i2 -= i;
        length = Math.min(i2, length - i);
        Object obj = new float[i2];
        System.arraycopy(fArr, i, obj, 0, length);
        return obj;
    }

    public static Path createPathFromPathData(String str) {
        Path path = new Path();
        PathDataNode[] createNodesFromPathData = createNodesFromPathData(str);
        if (createNodesFromPathData == null) {
            return null;
        }
        try {
            PathDataNode.nodesToPath(createNodesFromPathData, path);
            return path;
        } catch (Throwable e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Error in parsing ");
            stringBuilder.append(str);
            throw new RuntimeException(stringBuilder.toString(), e);
        }
    }

    public static PathDataNode[] createNodesFromPathData(String str) {
        if (str == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = 1;
        int i2 = 0;
        int i3 = i;
        int i4 = i2;
        while (i3 < str.length()) {
            i3 = nextStart(str, i3);
            String trim = str.substring(i4, i3).trim();
            if (trim.length() > 0) {
                addNode(arrayList, trim.charAt(i2), getFloats(trim));
            }
            i4 = i3;
            i3++;
        }
        if (i3 - i4 == i && i4 < str.length()) {
            addNode(arrayList, str.charAt(i4), new float[i2]);
        }
        return (PathDataNode[]) arrayList.toArray(new PathDataNode[arrayList.size()]);
    }

    public static PathDataNode[] deepCopyNodes(PathDataNode[] pathDataNodeArr) {
        if (pathDataNodeArr == null) {
            return null;
        }
        PathDataNode[] pathDataNodeArr2 = new PathDataNode[pathDataNodeArr.length];
        for (int i = 0; i < pathDataNodeArr.length; i++) {
            pathDataNodeArr2[i] = new PathDataNode(pathDataNodeArr[i]);
        }
        return pathDataNodeArr2;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canMorph(android.support.v4.graphics.PathParser.PathDataNode[] r4, android.support.v4.graphics.PathParser.PathDataNode[] r5) {
        /*
        r0 = 0;
        if (r4 == 0) goto L_0x002c;
    L_0x0003:
        if (r5 != 0) goto L_0x0006;
    L_0x0005:
        goto L_0x002c;
    L_0x0006:
        r1 = r4.length;
        r2 = r5.length;
        if (r1 == r2) goto L_0x000b;
    L_0x000a:
        return r0;
    L_0x000b:
        r1 = r0;
    L_0x000c:
        r2 = r4.length;
        if (r1 >= r2) goto L_0x002a;
    L_0x000f:
        r2 = r4[r1];
        r2 = r2.mType;
        r3 = r5[r1];
        r3 = r3.mType;
        if (r2 != r3) goto L_0x0029;
    L_0x0019:
        r2 = r4[r1];
        r2 = r2.mParams;
        r2 = r2.length;
        r3 = r5[r1];
        r3 = r3.mParams;
        r3 = r3.length;
        if (r2 == r3) goto L_0x0026;
    L_0x0025:
        goto L_0x0029;
    L_0x0026:
        r1 = r1 + 1;
        goto L_0x000c;
    L_0x0029:
        return r0;
    L_0x002a:
        r4 = 1;
        return r4;
    L_0x002c:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.PathParser.canMorph(android.support.v4.graphics.PathParser$PathDataNode[], android.support.v4.graphics.PathParser$PathDataNode[]):boolean");
    }

    public static void updateNodes(PathDataNode[] pathDataNodeArr, PathDataNode[] pathDataNodeArr2) {
        int i = 0;
        for (int i2 = i; i2 < pathDataNodeArr2.length; i2++) {
            pathDataNodeArr[i2].mType = pathDataNodeArr2[i2].mType;
            for (int i3 = i; i3 < pathDataNodeArr2[i2].mParams.length; i3++) {
                pathDataNodeArr[i2].mParams[i3] = pathDataNodeArr2[i2].mParams[i3];
            }
        }
    }

    private static int nextStart(String str, int i) {
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (((charAt - 65) * (charAt - 90) <= 0 || (charAt - 97) * (charAt - 122) <= 0) && charAt != 'e' && charAt != 'E') {
                return i;
            }
            i++;
        }
        return i;
    }

    private static void addNode(ArrayList<PathDataNode> arrayList, char c, float[] fArr) {
        arrayList.add(new PathDataNode(c, fArr));
    }

    private static float[] getFloats(String str) {
        int i = 0;
        if (str.charAt(i) == 'z' || str.charAt(i) == 'Z') {
            return new float[i];
        }
        try {
            float[] fArr = new float[str.length()];
            ExtractFloatResult extractFloatResult = new ExtractFloatResult();
            int length = str.length();
            int i2 = 1;
            int i3 = i;
            while (i2 < length) {
                extract(str, i2, extractFloatResult);
                int i4 = extractFloatResult.mEndPosition;
                if (i2 < i4) {
                    int i5 = i3 + 1;
                    fArr[i3] = Float.parseFloat(str.substring(i2, i4));
                    i3 = i5;
                }
                i2 = extractFloatResult.mEndWithNegOrDot ? i4 : i4 + 1;
            }
            return copyOfRange(fArr, i, i3);
        } catch (Throwable e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("error in parsing \"");
            stringBuilder.append(str);
            stringBuilder.append("\"");
            throw new RuntimeException(stringBuilder.toString(), e);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void extract(java.lang.String r8, int r9, android.support.v4.graphics.PathParser.ExtractFloatResult r10) {
        /*
        r0 = 0;
        r10.mEndWithNegOrDot = r0;
        r1 = r9;
        r2 = r0;
        r3 = r2;
        r4 = r3;
    L_0x0007:
        r5 = r8.length();
        if (r1 >= r5) goto L_0x003d;
    L_0x000d:
        r5 = r8.charAt(r1);
        r6 = 32;
        r7 = 1;
        if (r5 == r6) goto L_0x0035;
    L_0x0016:
        r6 = 69;
        if (r5 == r6) goto L_0x0033;
    L_0x001a:
        r6 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r5 == r6) goto L_0x0033;
    L_0x001e:
        switch(r5) {
            case 44: goto L_0x0035;
            case 45: goto L_0x002a;
            case 46: goto L_0x0022;
            default: goto L_0x0021;
        };
    L_0x0021:
        goto L_0x0031;
    L_0x0022:
        if (r3 != 0) goto L_0x0027;
    L_0x0024:
        r2 = r0;
        r3 = r7;
        goto L_0x0037;
    L_0x0027:
        r10.mEndWithNegOrDot = r7;
        goto L_0x0035;
    L_0x002a:
        if (r1 == r9) goto L_0x0031;
    L_0x002c:
        if (r2 != 0) goto L_0x0031;
    L_0x002e:
        r10.mEndWithNegOrDot = r7;
        goto L_0x0035;
    L_0x0031:
        r2 = r0;
        goto L_0x0037;
    L_0x0033:
        r2 = r7;
        goto L_0x0037;
    L_0x0035:
        r2 = r0;
        r4 = r7;
    L_0x0037:
        if (r4 == 0) goto L_0x003a;
    L_0x0039:
        goto L_0x003d;
    L_0x003a:
        r1 = r1 + 1;
        goto L_0x0007;
    L_0x003d:
        r10.mEndPosition = r1;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.graphics.PathParser.extract(java.lang.String, int, android.support.v4.graphics.PathParser$ExtractFloatResult):void");
    }
}
