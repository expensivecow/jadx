package android.arch.lifecycle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@RestrictTo({Scope.LIBRARY_GROUP})
class Lifecycling {
    private static Map<Class, Constructor<? extends GenericLifecycleObserver>> sCallbackCache = new HashMap();
    private static Constructor<? extends GenericLifecycleObserver> sREFLECTIVE;

    Lifecycling() {
    }

    static {
        try {
            sREFLECTIVE = ReflectiveGenericLifecycleObserver.class.getDeclaredConstructor(new Class[]{Object.class});
        } catch (NoSuchMethodException unused) {
        }
    }

    @NonNull
    static GenericLifecycleObserver getCallback(Object obj) {
        if (obj instanceof GenericLifecycleObserver) {
            return (GenericLifecycleObserver) obj;
        }
        try {
            Class cls = obj.getClass();
            Constructor constructor = (Constructor) sCallbackCache.get(cls);
            int i = 0;
            boolean z = true;
            Object[] objArr;
            if (constructor != null) {
                objArr = new Object[z];
                objArr[i] = obj;
                return (GenericLifecycleObserver) constructor.newInstance(objArr);
            }
            constructor = getGeneratedAdapterConstructor(cls);
            if (constructor == null) {
                constructor = sREFLECTIVE;
            } else if (!constructor.isAccessible()) {
                constructor.setAccessible(z);
            }
            sCallbackCache.put(cls, constructor);
            objArr = new Object[z];
            objArr[i] = obj;
            return (GenericLifecycleObserver) constructor.newInstance(objArr);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } catch (Throwable e2) {
            throw new RuntimeException(e2);
        } catch (Throwable e22) {
            throw new RuntimeException(e22);
        }
    }

    @Nullable
    private static Constructor<? extends GenericLifecycleObserver> getGeneratedAdapterConstructor(Class<?> cls) {
        Package packageR = cls.getPackage();
        String name = packageR != null ? packageR.getName() : "";
        String canonicalName = cls.getCanonicalName();
        Constructor<? extends GenericLifecycleObserver> constructor = null;
        if (canonicalName == null) {
            return constructor;
        }
        int i = 1;
        if (!name.isEmpty()) {
            canonicalName = canonicalName.substring(name.length() + i);
        }
        canonicalName = getAdapterName(canonicalName);
        try {
            if (!name.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(name);
                stringBuilder.append(".");
                stringBuilder.append(canonicalName);
                canonicalName = stringBuilder.toString();
            }
            Class cls2 = Class.forName(canonicalName);
            Class[] clsArr = new Class[i];
            clsArr[0] = cls;
            return cls2.getDeclaredConstructor(clsArr);
        } catch (ClassNotFoundException unused) {
            Class superclass = cls.getSuperclass();
            return superclass != null ? getGeneratedAdapterConstructor(superclass) : constructor;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static String getAdapterName(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.replace(".", "_"));
        stringBuilder.append("_LifecycleAdapter");
        return stringBuilder.toString();
    }
}
