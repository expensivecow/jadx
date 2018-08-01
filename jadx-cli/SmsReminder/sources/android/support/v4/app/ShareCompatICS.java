package android.support.v4.app;

import android.app.Activity;
import android.content.Intent;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

class ShareCompatICS {
    private static final String HISTORY_FILENAME_PREFIX = ".sharecompat_";

    ShareCompatICS() {
    }

    public static void configureMenuItem(MenuItem item, Activity callingActivity, Intent intent) {
        ActionProvider itemProvider = item.getActionProvider();
        ShareActionProvider provider = null;
        if (itemProvider instanceof ShareActionProvider) {
            provider = (ShareActionProvider) itemProvider;
        } else {
            provider = new ShareActionProvider(callingActivity);
        }
        provider.setShareHistoryFileName(".sharecompat_" + callingActivity.getClass().getName());
        provider.setShareIntent(intent);
        item.setActionProvider(provider);
    }
}
