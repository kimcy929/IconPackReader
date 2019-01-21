package com.kimcy929.iconpakagereader.iconhelper;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Kimcy929 on 08/11/2016.
 */
public class IconPackInfo {
    private String packageName;
    private CharSequence label;
    private Drawable icon;

    IconPackInfo(ResolveInfo r, PackageManager packageManager) {
        packageName = r.activityInfo.packageName;
        icon = r.loadIcon(packageManager);
        label = r.loadLabel(packageManager);
    }

    public IconPackInfo(String packageName, CharSequence label, Drawable icon) {
        this.packageName = packageName;
        this.label = label;
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public CharSequence getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }
}
