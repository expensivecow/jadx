package android.support.v7.app;

import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LongSparseArray;
import java.lang.reflect.Field;
import java.util.Map;

class ResourcesFlusher {
    private static final String TAG = "ResourcesFlusher";
    private static Field sDrawableCacheField;
    private static boolean sDrawableCacheFieldFetched;
    private static Field sResourcesImplField;
    private static boolean sResourcesImplFieldFetched;
    private static Class sThemedResourceCacheClazz;
    private static boolean sThemedResourceCacheClazzFetched;
    private static Field sThemedResourceCache_mUnthemedEntriesField;
    private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;

    ResourcesFlusher() {
    }

    static boolean flush(@NonNull Resources resources) {
        if (VERSION.SDK_INT >= 24) {
            return flushNougats(resources);
        }
        if (VERSION.SDK_INT >= 23) {
            return flushMarshmallows(resources);
        }
        return VERSION.SDK_INT >= 21 ? flushLollipops(resources) : false;
    }

    @RequiresApi(21)
    private static boolean flushLollipops(@NonNull Resources resources) {
        boolean z = true;
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(z);
            } catch (Throwable e) {
                Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", e);
            }
            sDrawableCacheFieldFetched = z;
        }
        if (sDrawableCacheField != null) {
            Map map;
            Map map2 = null;
            try {
                map = (Map) sDrawableCacheField.get(resources);
            } catch (Throwable e2) {
                Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", e2);
                map = map2;
            }
            if (map != null) {
                map.clear();
                return z;
            }
        }
        return false;
    }

    @RequiresApi(23)
    private static boolean flushMarshmallows(@NonNull Resources resources) {
        Object obj;
        boolean z;
        boolean z2 = true;
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(z2);
            } catch (Throwable e) {
                Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", e);
            }
            sDrawableCacheFieldFetched = z2;
        }
        Object obj2 = null;
        if (sDrawableCacheField != null) {
            try {
                obj = sDrawableCacheField.get(resources);
            } catch (Throwable e2) {
                Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", e2);
            }
            z = false;
            if (obj == null) {
                return z;
            }
            if (obj != null && flushThemedResourcesCache(obj)) {
                z = z2;
            }
            return z;
        }
        obj = obj2;
        z = false;
        if (obj == null) {
            return z;
        }
        if (obj != null && flushThemedResourcesCache(obj)) {
            z = z2;
        }
        return z;
    }

    @RequiresApi(24)
    private static boolean flushNougats(@NonNull Resources resources) {
        boolean z = true;
        if (!sResourcesImplFieldFetched) {
            try {
                sResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
                sResourcesImplField.setAccessible(z);
            } catch (Throwable e) {
                Log.e("ResourcesFlusher", "Could not retrieve Resources#mResourcesImpl field", e);
            }
            sResourcesImplFieldFetched = z;
        }
        boolean z2 = false;
        if (sResourcesImplField == null) {
            return z2;
        }
        Object obj;
        Object obj2 = null;
        try {
            obj = sResourcesImplField.get(resources);
        } catch (Throwable e2) {
            Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mResourcesImpl", e2);
            obj = obj2;
        }
        if (obj == null) {
            return z2;
        }
        if (!sDrawableCacheFieldFetched) {
            try {
                sDrawableCacheField = obj.getClass().getDeclaredField("mDrawableCache");
                sDrawableCacheField.setAccessible(z);
            } catch (Throwable e3) {
                Log.e("ResourcesFlusher", "Could not retrieve ResourcesImpl#mDrawableCache field", e3);
            }
            sDrawableCacheFieldFetched = z;
        }
        if (sDrawableCacheField != null) {
            try {
                obj = sDrawableCacheField.get(obj);
            } catch (Throwable e22) {
                Log.e("ResourcesFlusher", "Could not retrieve value from ResourcesImpl#mDrawableCache", e22);
            }
            if (obj == null || !flushThemedResourcesCache(obj)) {
                z = z2;
            }
            return z;
        }
        obj = obj2;
        z = z2;
        return z;
    }

    @RequiresApi(16)
    private static boolean flushThemedResourcesCache(@NonNull Object obj) {
        boolean z = true;
        if (!sThemedResourceCacheClazzFetched) {
            try {
                sThemedResourceCacheClazz = Class.forName("android.content.res.ThemedResourceCache");
            } catch (Throwable e) {
                Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", e);
            }
            sThemedResourceCacheClazzFetched = z;
        }
        boolean z2 = false;
        if (sThemedResourceCacheClazz == null) {
            return z2;
        }
        if (!sThemedResourceCache_mUnthemedEntriesFieldFetched) {
            try {
                sThemedResourceCache_mUnthemedEntriesField = sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries");
                sThemedResourceCache_mUnthemedEntriesField.setAccessible(z);
            } catch (Throwable e2) {
                Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", e2);
            }
            sThemedResourceCache_mUnthemedEntriesFieldFetched = z;
        }
        if (sThemedResourceCache_mUnthemedEntriesField == null) {
            return z2;
        }
        LongSparseArray longSparseArray;
        LongSparseArray longSparseArray2 = null;
        try {
            longSparseArray = (LongSparseArray) sThemedResourceCache_mUnthemedEntriesField.get(obj);
        } catch (Throwable e3) {
            Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", e3);
            longSparseArray = longSparseArray2;
        }
        if (longSparseArray == null) {
            return z2;
        }
        longSparseArray.clear();
        return z;
    }
}
