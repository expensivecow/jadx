package android.support.v7.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.v7.appcompat.R;
import android.view.ViewConfiguration;

@RestrictTo({Scope.LIBRARY_GROUP})
public class ActionBarPolicy {
    private Context mContext;

    public static ActionBarPolicy get(Context context) {
        return new ActionBarPolicy(context);
    }

    private ActionBarPolicy(Context context) {
        this.mContext = context;
    }

    public int getMaxActionButtons() {
        Configuration configuration = this.mContext.getResources().getConfiguration();
        int i = configuration.screenWidthDp;
        int i2 = configuration.screenHeightDp;
        int i3 = 600;
        if (configuration.smallestScreenWidthDp <= i3 && i <= i3) {
            int i4 = 720;
            i3 = 960;
            if ((i <= i3 || i2 <= i4) && (i <= i4 || i2 <= i3)) {
                if (i < 500) {
                    i4 = 480;
                    i3 = 640;
                    if ((i <= i3 || i2 <= i4) && (i <= i4 || i2 <= i3)) {
                        return i >= 360 ? 3 : 2;
                    }
                }
                return 4;
            }
        }
        return 5;
    }

    public boolean showsOverflowMenuButton() {
        boolean z = true;
        if (VERSION.SDK_INT >= 19) {
            return z;
        }
        return ViewConfiguration.get(this.mContext).hasPermanentMenuKey() ^ z;
    }

    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }

    public boolean hasEmbeddedTabs() {
        return this.mContext.getResources().getBoolean(R.bool.abc_action_bar_embed_tabs);
    }

    public int getTabContainerHeight() {
        int i = 0;
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(null, R.styleable.ActionBar, R.attr.actionBarStyle, i);
        int layoutDimension = obtainStyledAttributes.getLayoutDimension(R.styleable.ActionBar_height, i);
        Resources resources = this.mContext.getResources();
        if (!hasEmbeddedTabs()) {
            layoutDimension = Math.min(layoutDimension, resources.getDimensionPixelSize(R.dimen.abc_action_bar_stacked_max_height));
        }
        obtainStyledAttributes.recycle();
        return layoutDimension;
    }

    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }

    public int getStackedTabMaxWidth() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_stacked_tab_max_width);
    }
}
