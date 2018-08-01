package android.support.v4.app;

import android.app.RemoteInput.Builder;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput;
import android.support.v4.app.RemoteInputCompatBase.RemoteInput.Factory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@RequiresApi(20)
class RemoteInputCompatApi20 {
    private static final String EXTRA_DATA_TYPE_RESULTS_DATA = "android.remoteinput.dataTypeResultsData";

    RemoteInputCompatApi20() {
    }

    static RemoteInput[] toCompat(android.app.RemoteInput[] remoteInputArr, Factory factory) {
        if (remoteInputArr == null) {
            return null;
        }
        RemoteInput[] newArray = factory.newArray(remoteInputArr.length);
        for (int i = 0; i < remoteInputArr.length; i++) {
            android.app.RemoteInput remoteInput = remoteInputArr[i];
            newArray[i] = factory.build(remoteInput.getResultKey(), remoteInput.getLabel(), remoteInput.getChoices(), remoteInput.getAllowFreeFormInput(), remoteInput.getExtras(), null);
        }
        return newArray;
    }

    static android.app.RemoteInput[] fromCompat(RemoteInput[] remoteInputArr) {
        if (remoteInputArr == null) {
            return null;
        }
        android.app.RemoteInput[] remoteInputArr2 = new android.app.RemoteInput[remoteInputArr.length];
        for (int i = 0; i < remoteInputArr.length; i++) {
            RemoteInput remoteInput = remoteInputArr[i];
            remoteInputArr2[i] = new Builder(remoteInput.getResultKey()).setLabel(remoteInput.getLabel()).setChoices(remoteInput.getChoices()).setAllowFreeFormInput(remoteInput.getAllowFreeFormInput()).addExtras(remoteInput.getExtras()).build();
        }
        return remoteInputArr2;
    }

    static Bundle getResultsFromIntent(Intent intent) {
        return android.app.RemoteInput.getResultsFromIntent(intent);
    }

    static Map<String, Uri> getDataResultsFromIntent(Intent intent, String str) {
        intent = getClipDataIntentFromIntent(intent);
        Map<String, Uri> map = null;
        if (intent == null) {
            return map;
        }
        Map<String, Uri> hashMap = new HashMap();
        for (String str2 : intent.getExtras().keySet()) {
            String str22;
            if (str22.startsWith("android.remoteinput.dataTypeResultsData")) {
                String substring = str22.substring("android.remoteinput.dataTypeResultsData".length());
                if (substring != null) {
                    if (!substring.isEmpty()) {
                        str22 = intent.getBundleExtra(str22).getString(str);
                        if (str22 != null) {
                            if (!str22.isEmpty()) {
                                hashMap.put(substring, Uri.parse(str22));
                            }
                        }
                    }
                }
            }
        }
        if (!hashMap.isEmpty()) {
            map = hashMap;
        }
        return map;
    }

    static void addResultsToIntent(RemoteInput[] remoteInputArr, Intent intent, Bundle bundle) {
        Bundle resultsFromIntent = getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            resultsFromIntent.putAll(bundle);
            bundle = resultsFromIntent;
        }
        for (RemoteInput remoteInput : remoteInputArr) {
            Map dataResultsFromIntent = getDataResultsFromIntent(intent, remoteInput.getResultKey());
            android.app.RemoteInput.addResultsToIntent(fromCompat(new RemoteInput[]{remoteInput}), intent, bundle);
            if (dataResultsFromIntent != null) {
                addDataResultToIntent(remoteInput, intent, dataResultsFromIntent);
            }
        }
    }

    public static void addDataResultToIntent(RemoteInput remoteInput, Intent intent, Map<String, Uri> map) {
        Intent clipDataIntentFromIntent = getClipDataIntentFromIntent(intent);
        if (clipDataIntentFromIntent == null) {
            clipDataIntentFromIntent = new Intent();
        }
        for (Entry entry : map.entrySet()) {
            String str = (String) entry.getKey();
            Uri uri = (Uri) entry.getValue();
            if (str != null) {
                Bundle bundleExtra = clipDataIntentFromIntent.getBundleExtra(getExtraResultsKeyForData(str));
                if (bundleExtra == null) {
                    bundleExtra = new Bundle();
                }
                bundleExtra.putString(remoteInput.getResultKey(), uri.toString());
                clipDataIntentFromIntent.putExtra(getExtraResultsKeyForData(str), bundleExtra);
            }
        }
        intent.setClipData(ClipData.newIntent("android.remoteinput.results", clipDataIntentFromIntent));
    }

    private static String getExtraResultsKeyForData(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("android.remoteinput.dataTypeResultsData");
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    private static Intent getClipDataIntentFromIntent(Intent intent) {
        ClipData clipData = intent.getClipData();
        Intent intent2 = null;
        if (clipData == null) {
            return intent2;
        }
        ClipDescription description = clipData.getDescription();
        if (description.hasMimeType("text/vnd.android.intent") && description.getLabel().equals("android.remoteinput.results")) {
            return clipData.getItemAt(0).getIntent();
        }
        return intent2;
    }
}
