package android.support.v4.text.util;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v4.util.PatternsCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.text.util.Linkify.MatchFilter;
import android.text.util.Linkify.TransformFilter;
import android.widget.TextView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LinkifyCompat {
    private static final Comparator<LinkSpec> COMPARATOR = new Comparator<LinkSpec>() {
        public final int compare(LinkSpec linkSpec, LinkSpec linkSpec2) {
            int i = -1;
            if (linkSpec.start < linkSpec2.start) {
                return i;
            }
            int i2 = 1;
            if (linkSpec.start <= linkSpec2.start && linkSpec.end >= linkSpec2.end) {
                return linkSpec.end > linkSpec2.end ? i : 0;
            } else {
                return i2;
            }
        }
    };
    private static final String[] EMPTY_STRING = new String[0];

    private static class LinkSpec {
        int end;
        URLSpan frameworkAddedSpan;
        int start;
        String url;

        LinkSpec() {
        }
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkifyMask {
    }

    public static final boolean addLinks(@NonNull Spannable spannable, int i) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, i);
        }
        boolean z = false;
        if (i == 0) {
            return z;
        }
        Pattern pattern;
        String[] strArr;
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(z, spannable.length(), URLSpan.class);
        boolean z2 = true;
        for (int length = uRLSpanArr.length - z2; length >= 0; length--) {
            spannable.removeSpan(uRLSpanArr[length]);
        }
        if ((i & 4) != 0) {
            Linkify.addLinks(spannable, 4);
        }
        ArrayList arrayList = new ArrayList();
        if ((i & 1) != 0) {
            pattern = PatternsCompat.AUTOLINK_WEB_URL;
            strArr = new String[3];
            strArr[z] = "http://";
            strArr[z2] = "https://";
            strArr[2] = "rtsp://";
            gatherLinks(arrayList, spannable, pattern, strArr, Linkify.sUrlMatchFilter, null);
        }
        if ((i & 2) != 0) {
            pattern = PatternsCompat.AUTOLINK_EMAIL_ADDRESS;
            strArr = new String[z2];
            strArr[z] = "mailto:";
            gatherLinks(arrayList, spannable, pattern, strArr, null, null);
        }
        if ((i & 8) != 0) {
            gatherMapLinks(arrayList, spannable);
        }
        pruneOverlaps(arrayList, spannable);
        if (arrayList.size() == 0) {
            return z;
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            LinkSpec linkSpec = (LinkSpec) it.next();
            if (linkSpec.frameworkAddedSpan == null) {
                applyLink(linkSpec.url, linkSpec.start, linkSpec.end, spannable);
            }
        }
        return z2;
    }

    public static final boolean addLinks(@NonNull TextView textView, int i) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(textView, i);
        }
        boolean z = false;
        if (i == 0) {
            return z;
        }
        CharSequence text = textView.getText();
        boolean z2 = true;
        if (!(text instanceof Spannable)) {
            Spannable valueOf = SpannableString.valueOf(text);
            if (!addLinks(valueOf, i)) {
                return z;
            }
            addLinkMovementMethod(textView);
            textView.setText(valueOf);
            return z2;
        } else if (!addLinks((Spannable) text, i)) {
            return z;
        } else {
            addLinkMovementMethod(textView);
            return z2;
        }
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str);
            return;
        }
        addLinks(textView, pattern, str, null, null, null);
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str, matchFilter, transformFilter);
            return;
        }
        addLinks(textView, pattern, str, null, matchFilter, transformFilter);
    }

    public static final void addLinks(@NonNull TextView textView, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            Linkify.addLinks(textView, pattern, str, strArr, matchFilter, transformFilter);
            return;
        }
        CharSequence valueOf = SpannableString.valueOf(textView.getText());
        if (addLinks((Spannable) valueOf, pattern, str, strArr, matchFilter, transformFilter)) {
            textView.setText(valueOf);
            addLinkMovementMethod(textView);
        }
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str);
        }
        return addLinks(spannable, pattern, str, null, null, null);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str, matchFilter, transformFilter);
        }
        return addLinks(spannable, pattern, str, null, matchFilter, transformFilter);
    }

    public static final boolean addLinks(@NonNull Spannable spannable, @NonNull Pattern pattern, @Nullable String str, @Nullable String[] strArr, @Nullable MatchFilter matchFilter, @Nullable TransformFilter transformFilter) {
        if (VERSION.SDK_INT >= 26) {
            return Linkify.addLinks(spannable, pattern, str, strArr, matchFilter, transformFilter);
        }
        if (str == null) {
            str = "";
        }
        boolean z = true;
        if (strArr == null || strArr.length < z) {
            strArr = EMPTY_STRING;
        }
        boolean z2 = false;
        String[] strArr2 = new String[(strArr.length + z)];
        strArr2[z2] = str.toLowerCase(Locale.ROOT);
        int i = z2;
        while (i < strArr.length) {
            String str2 = strArr[i];
            i++;
            if (str2 == null) {
                str2 = "";
            } else {
                str2 = str2.toLowerCase(Locale.ROOT);
            }
            strArr2[i] = str2;
        }
        Matcher matcher = pattern.matcher(spannable);
        boolean z3 = z2;
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter != null ? matchFilter.acceptMatch(spannable, start, end) : z) {
                applyLink(makeUrl(matcher.group(z2), strArr2, matcher, transformFilter), start, end, spannable);
                z3 = z;
            }
        }
        return z3;
    }

    private static void addLinkMovementMethod(@NonNull TextView textView) {
        MovementMethod movementMethod = textView.getMovementMethod();
        if ((movementMethod == null || !(movementMethod instanceof LinkMovementMethod)) && textView.getLinksClickable()) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private static String makeUrl(@NonNull String str, @NonNull String[] strArr, Matcher matcher, @Nullable TransformFilter transformFilter) {
        int i;
        if (transformFilter != null) {
            str = transformFilter.transformUrl(matcher, str);
        }
        int i2 = 0;
        int i3 = i2;
        while (true) {
            i = 1;
            if (i3 >= strArr.length) {
                i = i2;
                break;
            }
            if (str.regionMatches(true, 0, strArr[i3], 0, strArr[i3].length())) {
                if (!str.regionMatches(false, 0, strArr[i3], 0, strArr[i3].length())) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(strArr[i3]);
                    stringBuilder.append(str.substring(strArr[i3].length()));
                    str = stringBuilder.toString();
                }
            } else {
                i3++;
            }
        }
        if (i != 0 || strArr.length <= 0) {
            return str;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(strArr[i2]);
        stringBuilder2.append(str);
        return stringBuilder2.toString();
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, Spannable spannable, Pattern pattern, String[] strArr, MatchFilter matchFilter, TransformFilter transformFilter) {
        Matcher matcher = pattern.matcher(spannable);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(spannable, start, end)) {
                LinkSpec linkSpec = new LinkSpec();
                linkSpec.url = makeUrl(matcher.group(0), strArr, matcher, transformFilter);
                linkSpec.start = start;
                linkSpec.end = end;
                arrayList.add(linkSpec);
            }
        }
    }

    private static void applyLink(String str, int i, int i2, Spannable spannable) {
        spannable.setSpan(new URLSpan(str), i, i2, 33);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final void gatherMapLinks(java.util.ArrayList<android.support.v4.text.util.LinkifyCompat.LinkSpec> r5, android.text.Spannable r6) {
        /*
        r6 = r6.toString();
        r0 = 0;
    L_0x0005:
        r1 = android.webkit.WebView.findAddress(r6);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        if (r1 == 0) goto L_0x0043;
    L_0x000b:
        r2 = r6.indexOf(r1);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        if (r2 >= 0) goto L_0x0012;
    L_0x0011:
        goto L_0x0043;
    L_0x0012:
        r3 = new android.support.v4.text.util.LinkifyCompat$LinkSpec;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.<init>();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = r1.length();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = r4 + r2;
        r2 = r2 + r0;
        r3.start = r2;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r0 = r0 + r4;
        r3.end = r0;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r6 = r6.substring(r4);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2 = "UTF-8";
        r1 = java.net.URLEncoder.encode(r1, r2);	 Catch:{ UnsupportedEncodingException -> 0x0005 }
        r2 = new java.lang.StringBuilder;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2.<init>();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r4 = "geo:0,0?q=";
        r2.append(r4);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r2.append(r1);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r1 = r2.toString();	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r3.url = r1;	 Catch:{ UnsupportedOperationException -> 0x0044 }
        r5.add(r3);	 Catch:{ UnsupportedOperationException -> 0x0044 }
        goto L_0x0005;
    L_0x0043:
        return;
    L_0x0044:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.text.util.LinkifyCompat.gatherMapLinks(java.util.ArrayList, android.text.Spannable):void");
    }

    private static final void pruneOverlaps(ArrayList<LinkSpec> arrayList, Spannable spannable) {
        int i;
        int i2 = 0;
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(i2, spannable.length(), URLSpan.class);
        for (i = i2; i < uRLSpanArr.length; i++) {
            LinkSpec linkSpec = new LinkSpec();
            linkSpec.frameworkAddedSpan = uRLSpanArr[i];
            linkSpec.start = spannable.getSpanStart(uRLSpanArr[i]);
            linkSpec.end = spannable.getSpanEnd(uRLSpanArr[i]);
            arrayList.add(linkSpec);
        }
        Collections.sort(arrayList, COMPARATOR);
        int size = arrayList.size();
        while (i2 < size - 1) {
            LinkSpec linkSpec2 = (LinkSpec) arrayList.get(i2);
            int i3 = i2 + 1;
            LinkSpec linkSpec3 = (LinkSpec) arrayList.get(i3);
            if (linkSpec2.start <= linkSpec3.start && linkSpec2.end > linkSpec3.start) {
                int i4 = -1;
                i = (linkSpec3.end > linkSpec2.end && linkSpec2.end - linkSpec2.start <= linkSpec3.end - linkSpec3.start) ? linkSpec2.end - linkSpec2.start < linkSpec3.end - linkSpec3.start ? i2 : i4 : i3;
                if (i != i4) {
                    URLSpan uRLSpan = ((LinkSpec) arrayList.get(i)).frameworkAddedSpan;
                    if (uRLSpan != null) {
                        spannable.removeSpan(uRLSpan);
                    }
                    arrayList.remove(i);
                    size--;
                }
            }
            i2 = i3;
        }
    }

    private LinkifyCompat() {
    }
}
