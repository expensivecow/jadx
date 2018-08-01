package com.google.gson.internal;

public abstract class UnsafeAllocator {
    public abstract <T> T newInstance(Class<T> cls) throws Exception;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.google.gson.internal.UnsafeAllocator create() {
        /*
        r0 = 0;
        r1 = 0;
        r2 = 1;
        r3 = "sun.misc.Unsafe";
        r3 = java.lang.Class.forName(r3);	 Catch:{ Exception -> 0x0028 }
        r4 = "theUnsafe";
        r4 = r3.getDeclaredField(r4);	 Catch:{ Exception -> 0x0028 }
        r4.setAccessible(r2);	 Catch:{ Exception -> 0x0028 }
        r4 = r4.get(r0);	 Catch:{ Exception -> 0x0028 }
        r5 = "allocateInstance";
        r6 = new java.lang.Class[r2];	 Catch:{ Exception -> 0x0028 }
        r7 = java.lang.Class.class;
        r6[r1] = r7;	 Catch:{ Exception -> 0x0028 }
        r3 = r3.getMethod(r5, r6);	 Catch:{ Exception -> 0x0028 }
        r5 = new com.google.gson.internal.UnsafeAllocator$1;	 Catch:{ Exception -> 0x0028 }
        r5.<init>(r3, r4);	 Catch:{ Exception -> 0x0028 }
        return r5;
    L_0x0028:
        r3 = 2;
        r4 = java.io.ObjectStreamClass.class;
        r5 = "getConstructorId";
        r6 = new java.lang.Class[r2];	 Catch:{ Exception -> 0x0065 }
        r7 = java.lang.Class.class;
        r6[r1] = r7;	 Catch:{ Exception -> 0x0065 }
        r4 = r4.getDeclaredMethod(r5, r6);	 Catch:{ Exception -> 0x0065 }
        r4.setAccessible(r2);	 Catch:{ Exception -> 0x0065 }
        r5 = new java.lang.Object[r2];	 Catch:{ Exception -> 0x0065 }
        r6 = java.lang.Object.class;
        r5[r1] = r6;	 Catch:{ Exception -> 0x0065 }
        r0 = r4.invoke(r0, r5);	 Catch:{ Exception -> 0x0065 }
        r0 = (java.lang.Integer) r0;	 Catch:{ Exception -> 0x0065 }
        r0 = r0.intValue();	 Catch:{ Exception -> 0x0065 }
        r4 = java.io.ObjectStreamClass.class;
        r5 = "newInstance";
        r6 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0065 }
        r7 = java.lang.Class.class;
        r6[r1] = r7;	 Catch:{ Exception -> 0x0065 }
        r7 = java.lang.Integer.TYPE;	 Catch:{ Exception -> 0x0065 }
        r6[r2] = r7;	 Catch:{ Exception -> 0x0065 }
        r4 = r4.getDeclaredMethod(r5, r6);	 Catch:{ Exception -> 0x0065 }
        r4.setAccessible(r2);	 Catch:{ Exception -> 0x0065 }
        r5 = new com.google.gson.internal.UnsafeAllocator$2;	 Catch:{ Exception -> 0x0065 }
        r5.<init>(r4, r0);	 Catch:{ Exception -> 0x0065 }
        return r5;
    L_0x0065:
        r0 = java.io.ObjectInputStream.class;
        r4 = "newInstance";
        r3 = new java.lang.Class[r3];	 Catch:{ Exception -> 0x0080 }
        r5 = java.lang.Class.class;
        r3[r1] = r5;	 Catch:{ Exception -> 0x0080 }
        r1 = java.lang.Class.class;
        r3[r2] = r1;	 Catch:{ Exception -> 0x0080 }
        r0 = r0.getDeclaredMethod(r4, r3);	 Catch:{ Exception -> 0x0080 }
        r0.setAccessible(r2);	 Catch:{ Exception -> 0x0080 }
        r1 = new com.google.gson.internal.UnsafeAllocator$3;	 Catch:{ Exception -> 0x0080 }
        r1.<init>(r0);	 Catch:{ Exception -> 0x0080 }
        return r1;
    L_0x0080:
        r0 = new com.google.gson.internal.UnsafeAllocator$4;
        r0.<init>();
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.UnsafeAllocator.create():com.google.gson.internal.UnsafeAllocator");
    }
}
