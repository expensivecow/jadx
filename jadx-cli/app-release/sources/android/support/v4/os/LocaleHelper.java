package android.support.v4.os;

import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import java.util.Locale;

@RestrictTo({Scope.LIBRARY_GROUP})
final class LocaleHelper {
    LocaleHelper() {
    }

    static Locale forLanguageTag(String str) {
        int i = 2;
        int i2 = 0;
        int i3 = 1;
        String[] split;
        if (str.contains("-")) {
            split = str.split("-");
            if (split.length > i) {
                return new Locale(split[i2], split[i3], split[i]);
            }
            if (split.length > i3) {
                return new Locale(split[i2], split[i3]);
            }
            if (split.length == i3) {
                return new Locale(split[i2]);
            }
        } else if (!str.contains("_")) {
            return new Locale(str);
        } else {
            split = str.split("_");
            if (split.length > i) {
                return new Locale(split[i2], split[i3], split[i]);
            }
            if (split.length > i3) {
                return new Locale(split[i2], split[i3]);
            }
            if (split.length == i3) {
                return new Locale(split[i2]);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Can not parse language tag: [");
        stringBuilder.append(str);
        stringBuilder.append("]");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    static String toLanguageTag(Locale locale) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(locale.getLanguage());
        String country = locale.getCountry();
        if (!(country == null || country.isEmpty())) {
            stringBuilder.append("-");
            stringBuilder.append(locale.getCountry());
        }
        return stringBuilder.toString();
    }
}
