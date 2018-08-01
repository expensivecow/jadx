package android.support.v4.provider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.annotation.GuardedBy;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.FontResourcesParserCompat;
import android.support.v4.graphics.TypefaceCompat;
import android.support.v4.graphics.TypefaceCompatUtil;
import android.support.v4.provider.SelfDestructiveThread.ReplyCallback;
import android.support.v4.util.LruCache;
import android.support.v4.util.Preconditions;
import android.support.v4.util.SimpleArrayMap;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class FontsContractCompat {
    private static final int BACKGROUND_THREAD_KEEP_ALIVE_DURATION_MS = 10000;
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final String PARCEL_FONT_RESULTS = "font_results";
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_PROVIDER_NOT_FOUND = -1;
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static final int RESULT_CODE_WRONG_CERTIFICATES = -2;
    private static final String TAG = "FontsContractCompat";
    private static final SelfDestructiveThread sBackgroundThread = new SelfDestructiveThread("fonts", 10, 10000);
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator<byte[]>() {
        public int compare(byte[] bArr, byte[] bArr2) {
            if (bArr.length != bArr2.length) {
                return bArr.length - bArr2.length;
            }
            int i = 0;
            for (int i2 = i; i2 < bArr.length; i2++) {
                if (bArr[i2] != bArr2[i2]) {
                    return bArr[i2] - bArr2[i2];
                }
            }
            return i;
        }
    };
    private static final Object sLock = new Object();
    @GuardedBy("sLock")
    private static final SimpleArrayMap<String, ArrayList<ReplyCallback<Typeface>>> sPendingReplies = new SimpleArrayMap();
    private static final LruCache<String, Typeface> sTypefaceCache = new LruCache(16);

    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";
    }

    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        @RestrictTo({Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        @interface FontResultStatus {
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public FontFamilyResult(int i, @Nullable FontInfo[] fontInfoArr) {
            this.mStatusCode = i;
            this.mFonts = fontInfoArr;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    public static class FontInfo {
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        @RestrictTo({Scope.LIBRARY_GROUP})
        public FontInfo(@NonNull Uri uri, @IntRange(from = 0) int i, @IntRange(from = 1, to = 1000) int i2, boolean z, int i3) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = i;
            this.mWeight = i2;
            this.mItalic = z;
            this.mResultCode = i3;
        }

        @NonNull
        public Uri getUri() {
            return this.mUri;
        }

        @IntRange(from = 0)
        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        @IntRange(from = 1, to = 1000)
        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;

        @RestrictTo({Scope.LIBRARY_GROUP})
        @Retention(RetentionPolicy.SOURCE)
        @interface FontRequestFailReason {
        }

        public void onTypefaceRequestFailed(int i) {
        }

        public void onTypefaceRetrieved(Typeface typeface) {
        }
    }

    private FontsContractCompat() {
    }

    private static Typeface getFontInternal(Context context, FontRequest fontRequest, int i) {
        Typeface typeface = null;
        try {
            FontFamilyResult fetchFonts = fetchFonts(context, typeface, fontRequest);
            return fetchFonts.getStatusCode() == 0 ? TypefaceCompat.createFromFontInfo(context, typeface, fetchFonts.getFonts(), i) : typeface;
        } catch (NameNotFoundException unused) {
            return typeface;
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static Typeface getFontSync(final Context context, final FontRequest fontRequest, @Nullable final TextView textView, int i, int i2, final int i3) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(fontRequest.getIdentifier());
        stringBuilder.append("-");
        stringBuilder.append(i3);
        final String stringBuilder2 = stringBuilder.toString();
        Typeface typeface = (Typeface) sTypefaceCache.get(stringBuilder2);
        if (typeface != null) {
            return typeface;
        }
        Object obj = i == 0 ? 1 : null;
        if (obj != null && i2 == -1) {
            return getFontInternal(context, fontRequest, i3);
        }
        Callable anonymousClass1 = new Callable<Typeface>() {
            public Typeface call() throws Exception {
                Typeface access$000 = FontsContractCompat.getFontInternal(context, fontRequest, i3);
                if (access$000 != null) {
                    FontsContractCompat.sTypefaceCache.put(stringBuilder2, access$000);
                }
                return access$000;
            }
        };
        Typeface typeface2 = null;
        if (obj != null) {
            try {
                return (Typeface) sBackgroundThread.postAndWait(anonymousClass1, i2);
            } catch (InterruptedException unused) {
                return typeface2;
            }
        }
        final WeakReference weakReference = new WeakReference(textView);
        AnonymousClass2 anonymousClass2 = new ReplyCallback<Typeface>() {
            public void onReply(Typeface typeface) {
                if (((TextView) weakReference.get()) != null) {
                    textView.setTypeface(typeface, i3);
                }
            }
        };
        synchronized (sLock) {
            if (sPendingReplies.containsKey(stringBuilder2)) {
                ((ArrayList) sPendingReplies.get(stringBuilder2)).add(anonymousClass2);
                return typeface2;
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(anonymousClass2);
            sPendingReplies.put(stringBuilder2, arrayList);
            sBackgroundThread.postAndReply(anonymousClass1, new ReplyCallback<Typeface>() {
                public void onReply(Typeface typeface) {
                    ArrayList arrayList;
                    synchronized (FontsContractCompat.sLock) {
                        arrayList = (ArrayList) FontsContractCompat.sPendingReplies.get(stringBuilder2);
                        FontsContractCompat.sPendingReplies.remove(stringBuilder2);
                    }
                    for (int i = 0; i < arrayList.size(); i++) {
                        ((ReplyCallback) arrayList.get(i)).onReply(typeface);
                    }
                }
            });
            return typeface2;
        }
    }

    public static void requestFont(@NonNull final Context context, @NonNull final FontRequest fontRequest, @NonNull final FontRequestCallback fontRequestCallback, @NonNull Handler handler) {
        final Handler handler2 = new Handler();
        handler.post(new Runnable() {
            public void run() {
                try {
                    CancellationSignal cancellationSignal = null;
                    FontFamilyResult fetchFonts = FontsContractCompat.fetchFonts(context, cancellationSignal, fontRequest);
                    if (fetchFonts.getStatusCode() != 0) {
                        switch (fetchFonts.getStatusCode()) {
                            case 1:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-2);
                                    }
                                });
                                return;
                            case 2:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                            default:
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-3);
                                    }
                                });
                                return;
                        }
                    }
                    FontInfo[] fonts = fetchFonts.getFonts();
                    if (fonts == null || fonts.length == 0) {
                        handler2.post(new Runnable() {
                            public void run() {
                                fontRequestCallback.onTypefaceRequestFailed(1);
                            }
                        });
                        return;
                    }
                    for (FontInfo fontInfo : fonts) {
                        if (fontInfo.getResultCode() != 0) {
                            final int resultCode = fontInfo.getResultCode();
                            if (resultCode < 0) {
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(-3);
                                    }
                                });
                            } else {
                                handler2.post(new Runnable() {
                                    public void run() {
                                        fontRequestCallback.onTypefaceRequestFailed(resultCode);
                                    }
                                });
                            }
                            return;
                        }
                    }
                    final Typeface buildTypeface = FontsContractCompat.buildTypeface(context, cancellationSignal, fonts);
                    if (buildTypeface == null) {
                        handler2.post(new Runnable() {
                            public void run() {
                                fontRequestCallback.onTypefaceRequestFailed(-3);
                            }
                        });
                    } else {
                        handler2.post(new Runnable() {
                            public void run() {
                                fontRequestCallback.onTypefaceRetrieved(buildTypeface);
                            }
                        });
                    }
                } catch (NameNotFoundException unused) {
                    handler2.post(new Runnable() {
                        public void run() {
                            fontRequestCallback.onTypefaceRequestFailed(-1);
                        }
                    });
                }
            }
        });
    }

    public static Typeface buildTypeface(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontInfo[] fontInfoArr) {
        return TypefaceCompat.createFromFontInfo(context, cancellationSignal, fontInfoArr, 0);
    }

    @RequiresApi(19)
    @RestrictTo({Scope.LIBRARY_GROUP})
    public static Map<Uri, ByteBuffer> prepareFontData(Context context, FontInfo[] fontInfoArr, CancellationSignal cancellationSignal) {
        Map hashMap = new HashMap();
        for (FontInfo fontInfo : fontInfoArr) {
            if (fontInfo.getResultCode() == 0) {
                Uri uri = fontInfo.getUri();
                if (!hashMap.containsKey(uri)) {
                    hashMap.put(uri, TypefaceCompatUtil.mmap(context, cancellationSignal, uri));
                }
            }
        }
        return Collections.unmodifiableMap(hashMap);
    }

    @NonNull
    public static FontFamilyResult fetchFonts(@NonNull Context context, @Nullable CancellationSignal cancellationSignal, @NonNull FontRequest fontRequest) throws NameNotFoundException {
        ProviderInfo provider = getProvider(context.getPackageManager(), fontRequest, context.getResources());
        if (provider == null) {
            return new FontFamilyResult(1, null);
        }
        return new FontFamilyResult(0, getFontFromProvider(context, fontRequest, provider.authority, cancellationSignal));
    }

    @VisibleForTesting
    @RestrictTo({Scope.LIBRARY_GROUP})
    @Nullable
    public static ProviderInfo getProvider(@NonNull PackageManager packageManager, @NonNull FontRequest fontRequest, @Nullable Resources resources) throws NameNotFoundException {
        String providerAuthority = fontRequest.getProviderAuthority();
        int i = 0;
        ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(providerAuthority, i);
        if (resolveContentProvider == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("No package found for authority: ");
            stringBuilder.append(providerAuthority);
            throw new NameNotFoundException(stringBuilder.toString());
        } else if (resolveContentProvider.packageName.equals(fontRequest.getProviderPackage())) {
            List convertToByteArrayList = convertToByteArrayList(packageManager.getPackageInfo(resolveContentProvider.packageName, 64).signatures);
            Collections.sort(convertToByteArrayList, sByteArrayComparator);
            List certificates = getCertificates(fontRequest, resources);
            while (i < certificates.size()) {
                List arrayList = new ArrayList((Collection) certificates.get(i));
                Collections.sort(arrayList, sByteArrayComparator);
                if (equalsByteArrayList(convertToByteArrayList, arrayList)) {
                    return resolveContentProvider;
                }
                i++;
            }
            return null;
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Found content provider ");
            stringBuilder2.append(providerAuthority);
            stringBuilder2.append(", but package was not ");
            stringBuilder2.append(fontRequest.getProviderPackage());
            throw new NameNotFoundException(stringBuilder2.toString());
        }
    }

    private static List<List<byte[]>> getCertificates(FontRequest fontRequest, Resources resources) {
        if (fontRequest.getCertificates() != null) {
            return fontRequest.getCertificates();
        }
        return FontResourcesParserCompat.readCerts(resources, fontRequest.getCertificatesArrayResId());
    }

    private static boolean equalsByteArrayList(List<byte[]> list, List<byte[]> list2) {
        boolean z = false;
        if (list.size() != list2.size()) {
            return z;
        }
        for (int i = z; i < list.size(); i++) {
            if (!Arrays.equals((byte[]) list.get(i), (byte[]) list2.get(i))) {
                return z;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatureArr) {
        List<byte[]> arrayList = new ArrayList();
        for (Signature toByteArray : signatureArr) {
            arrayList.add(toByteArray.toByteArray());
        }
        return arrayList;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.support.annotation.VisibleForTesting
    @android.support.annotation.NonNull
    static android.support.v4.provider.FontsContractCompat.FontInfo[] getFontFromProvider(android.content.Context r22, android.support.v4.provider.FontRequest r23, java.lang.String r24, android.os.CancellationSignal r25) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessVariables.addToUsageMap(ProcessVariables.java:271)
	at jadx.core.dex.visitors.regions.ProcessVariables.access$0(ProcessVariables.java:266)
	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processInsn(ProcessVariables.java:163)
	at jadx.core.dex.visitors.regions.ProcessVariables$CollectUsageRegionVisitor.processBlockTraced(ProcessVariables.java:129)
	at jadx.core.dex.visitors.regions.TracedRegionVisitor.processBlock(TracedRegionVisitor.java:23)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:53)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1080)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$0(DepthRegionTraversal.java:57)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:57)
	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:18)
	at jadx.core.dex.visitors.regions.ProcessVariables.visit(ProcessVariables.java:183)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
	at jadx.core.dex.visitors.DepthTraversal.lambda$1(DepthTraversal.java:14)
	at java.util.ArrayList.forEach(ArrayList.java:1257)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:32)
	at jadx.core.ProcessClass.lambda$0(ProcessClass.java:51)
	at java.lang.Iterable.forEach(Iterable.java:75)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$0(JadxDecompiler.java:201)
*/
        /*
        r1 = r24;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new android.net.Uri$Builder;
        r3.<init>();
        r4 = "content";
        r3 = r3.scheme(r4);
        r3 = r3.authority(r1);
        r3 = r3.build();
        r4 = new android.net.Uri$Builder;
        r4.<init>();
        r5 = "content";
        r4 = r4.scheme(r5);
        r1 = r4.authority(r1);
        r4 = "file";
        r1 = r1.appendPath(r4);
        r1 = r1.build();
        r11 = 0;
        r4 = android.os.Build.VERSION.SDK_INT;	 Catch:{ all -> 0x0144 }
        r5 = 16;	 Catch:{ all -> 0x0144 }
        r6 = 6;	 Catch:{ all -> 0x0144 }
        r7 = 5;	 Catch:{ all -> 0x0144 }
        r8 = 4;	 Catch:{ all -> 0x0144 }
        r9 = 3;	 Catch:{ all -> 0x0144 }
        r10 = 2;	 Catch:{ all -> 0x0144 }
        r12 = 7;	 Catch:{ all -> 0x0144 }
        r13 = 1;	 Catch:{ all -> 0x0144 }
        r14 = 0;	 Catch:{ all -> 0x0144 }
        if (r4 <= r5) goto L_0x0079;	 Catch:{ all -> 0x0144 }
    L_0x0042:
        r4 = r22.getContentResolver();	 Catch:{ all -> 0x0144 }
        r12 = new java.lang.String[r12];	 Catch:{ all -> 0x0144 }
        r5 = "_id";	 Catch:{ all -> 0x0144 }
        r12[r14] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "file_id";	 Catch:{ all -> 0x0144 }
        r12[r13] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_ttc_index";	 Catch:{ all -> 0x0144 }
        r12[r10] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_variation_settings";	 Catch:{ all -> 0x0144 }
        r12[r9] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_weight";	 Catch:{ all -> 0x0144 }
        r12[r8] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_italic";	 Catch:{ all -> 0x0144 }
        r12[r7] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "result_code";	 Catch:{ all -> 0x0144 }
        r12[r6] = r5;	 Catch:{ all -> 0x0144 }
        r7 = "query = ?";	 Catch:{ all -> 0x0144 }
        r8 = new java.lang.String[r13];	 Catch:{ all -> 0x0144 }
        r5 = r23.getQuery();	 Catch:{ all -> 0x0144 }
        r8[r14] = r5;	 Catch:{ all -> 0x0144 }
        r9 = 0;	 Catch:{ all -> 0x0144 }
        r5 = r3;	 Catch:{ all -> 0x0144 }
        r6 = r12;	 Catch:{ all -> 0x0144 }
        r10 = r25;	 Catch:{ all -> 0x0144 }
        r4 = r4.query(r5, r6, r7, r8, r9, r10);	 Catch:{ all -> 0x0144 }
    L_0x0077:
        r11 = r4;	 Catch:{ all -> 0x0144 }
        goto L_0x00ad;	 Catch:{ all -> 0x0144 }
    L_0x0079:
        r4 = r22.getContentResolver();	 Catch:{ all -> 0x0144 }
        r12 = new java.lang.String[r12];	 Catch:{ all -> 0x0144 }
        r5 = "_id";	 Catch:{ all -> 0x0144 }
        r12[r14] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "file_id";	 Catch:{ all -> 0x0144 }
        r12[r13] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_ttc_index";	 Catch:{ all -> 0x0144 }
        r12[r10] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_variation_settings";	 Catch:{ all -> 0x0144 }
        r12[r9] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_weight";	 Catch:{ all -> 0x0144 }
        r12[r8] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "font_italic";	 Catch:{ all -> 0x0144 }
        r12[r7] = r5;	 Catch:{ all -> 0x0144 }
        r5 = "result_code";	 Catch:{ all -> 0x0144 }
        r12[r6] = r5;	 Catch:{ all -> 0x0144 }
        r7 = "query = ?";	 Catch:{ all -> 0x0144 }
        r8 = new java.lang.String[r13];	 Catch:{ all -> 0x0144 }
        r5 = r23.getQuery();	 Catch:{ all -> 0x0144 }
        r8[r14] = r5;	 Catch:{ all -> 0x0144 }
        r9 = 0;	 Catch:{ all -> 0x0144 }
        r5 = r3;	 Catch:{ all -> 0x0144 }
        r6 = r12;	 Catch:{ all -> 0x0144 }
        r4 = r4.query(r5, r6, r7, r8, r9);	 Catch:{ all -> 0x0144 }
        goto L_0x0077;	 Catch:{ all -> 0x0144 }
    L_0x00ad:
        if (r11 == 0) goto L_0x0135;	 Catch:{ all -> 0x0144 }
    L_0x00af:
        r4 = r11.getCount();	 Catch:{ all -> 0x0144 }
        if (r4 <= 0) goto L_0x0135;	 Catch:{ all -> 0x0144 }
    L_0x00b5:
        r2 = "result_code";	 Catch:{ all -> 0x0144 }
        r2 = r11.getColumnIndex(r2);	 Catch:{ all -> 0x0144 }
        r4 = new java.util.ArrayList;	 Catch:{ all -> 0x0144 }
        r4.<init>();	 Catch:{ all -> 0x0144 }
        r5 = "_id";	 Catch:{ all -> 0x0144 }
        r5 = r11.getColumnIndex(r5);	 Catch:{ all -> 0x0144 }
        r6 = "file_id";	 Catch:{ all -> 0x0144 }
        r6 = r11.getColumnIndex(r6);	 Catch:{ all -> 0x0144 }
        r7 = "font_ttc_index";	 Catch:{ all -> 0x0144 }
        r7 = r11.getColumnIndex(r7);	 Catch:{ all -> 0x0144 }
        r8 = "font_weight";	 Catch:{ all -> 0x0144 }
        r8 = r11.getColumnIndex(r8);	 Catch:{ all -> 0x0144 }
        r9 = "font_italic";	 Catch:{ all -> 0x0144 }
        r9 = r11.getColumnIndex(r9);	 Catch:{ all -> 0x0144 }
    L_0x00de:
        r10 = r11.moveToNext();	 Catch:{ all -> 0x0144 }
        if (r10 == 0) goto L_0x0134;	 Catch:{ all -> 0x0144 }
    L_0x00e4:
        r10 = -1;	 Catch:{ all -> 0x0144 }
        if (r2 == r10) goto L_0x00ee;	 Catch:{ all -> 0x0144 }
    L_0x00e7:
        r12 = r11.getInt(r2);	 Catch:{ all -> 0x0144 }
        r20 = r12;	 Catch:{ all -> 0x0144 }
        goto L_0x00f0;	 Catch:{ all -> 0x0144 }
    L_0x00ee:
        r20 = r14;	 Catch:{ all -> 0x0144 }
    L_0x00f0:
        if (r7 == r10) goto L_0x00f9;	 Catch:{ all -> 0x0144 }
    L_0x00f2:
        r12 = r11.getInt(r7);	 Catch:{ all -> 0x0144 }
        r17 = r12;	 Catch:{ all -> 0x0144 }
        goto L_0x00fb;	 Catch:{ all -> 0x0144 }
    L_0x00f9:
        r17 = r14;	 Catch:{ all -> 0x0144 }
    L_0x00fb:
        if (r6 != r10) goto L_0x0108;	 Catch:{ all -> 0x0144 }
    L_0x00fd:
        r14 = r11.getLong(r5);	 Catch:{ all -> 0x0144 }
        r12 = android.content.ContentUris.withAppendedId(r3, r14);	 Catch:{ all -> 0x0144 }
    L_0x0105:
        r16 = r12;	 Catch:{ all -> 0x0144 }
        goto L_0x0111;	 Catch:{ all -> 0x0144 }
    L_0x0108:
        r14 = r11.getLong(r6);	 Catch:{ all -> 0x0144 }
        r12 = android.content.ContentUris.withAppendedId(r1, r14);	 Catch:{ all -> 0x0144 }
        goto L_0x0105;	 Catch:{ all -> 0x0144 }
    L_0x0111:
        if (r8 == r10) goto L_0x0118;	 Catch:{ all -> 0x0144 }
    L_0x0113:
        r12 = r11.getInt(r8);	 Catch:{ all -> 0x0144 }
        goto L_0x011a;	 Catch:{ all -> 0x0144 }
    L_0x0118:
        r12 = 400; // 0x190 float:5.6E-43 double:1.976E-321;	 Catch:{ all -> 0x0144 }
    L_0x011a:
        r18 = r12;	 Catch:{ all -> 0x0144 }
        if (r9 == r10) goto L_0x0127;	 Catch:{ all -> 0x0144 }
    L_0x011e:
        r10 = r11.getInt(r9);	 Catch:{ all -> 0x0144 }
        if (r10 != r13) goto L_0x0127;	 Catch:{ all -> 0x0144 }
    L_0x0124:
        r19 = r13;	 Catch:{ all -> 0x0144 }
        goto L_0x0129;	 Catch:{ all -> 0x0144 }
    L_0x0127:
        r19 = 0;	 Catch:{ all -> 0x0144 }
    L_0x0129:
        r10 = new android.support.v4.provider.FontsContractCompat$FontInfo;	 Catch:{ all -> 0x0144 }
        r15 = r10;	 Catch:{ all -> 0x0144 }
        r15.<init>(r16, r17, r18, r19, r20);	 Catch:{ all -> 0x0144 }
        r4.add(r10);	 Catch:{ all -> 0x0144 }
        r14 = 0;
        goto L_0x00de;
    L_0x0134:
        r2 = r4;
    L_0x0135:
        if (r11 == 0) goto L_0x013a;
    L_0x0137:
        r11.close();
    L_0x013a:
        r1 = 0;
        r1 = new android.support.v4.provider.FontsContractCompat.FontInfo[r1];
        r1 = r2.toArray(r1);
        r1 = (android.support.v4.provider.FontsContractCompat.FontInfo[]) r1;
        return r1;
    L_0x0144:
        r0 = move-exception;
        r1 = r0;
        if (r11 == 0) goto L_0x014b;
    L_0x0148:
        r11.close();
    L_0x014b:
        throw r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.provider.FontsContractCompat.getFontFromProvider(android.content.Context, android.support.v4.provider.FontRequest, java.lang.String, android.os.CancellationSignal):android.support.v4.provider.FontsContractCompat$FontInfo[]");
    }
}
