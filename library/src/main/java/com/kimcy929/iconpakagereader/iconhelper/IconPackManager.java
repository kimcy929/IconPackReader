package com.kimcy929.iconpakagereader.iconhelper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kimcy929 08/11/2016
 */
public class IconPackManager {

    private final static String[] sSupportedActions = new String[]{
            "org.adw.launcher.THEMES",
            "com.gau.go.launcherex.theme"
    };

    private static final String[] sSupportedCategories = new String[]{
            "com.fede.launcher.THEME_ICONPACK",
            "com.anddoes.launcher.THEME",
            "com.teslacoilsw.launcher.THEME"
    };

    public static Map<String, IconPackInfo> getSupportedPackages(Context context) {
        Intent iconIntent = new Intent();
        Map<String, IconPackInfo> packages = new HashMap<>();
        PackageManager packageManager = context.getPackageManager();
        for (String action : sSupportedActions) {
            iconIntent.setAction(action);
            for (ResolveInfo r : packageManager.queryIntentActivities(iconIntent, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
        }
        iconIntent = new Intent(Intent.ACTION_MAIN);
        for (String category : sSupportedCategories) {
            iconIntent.addCategory(category);
            for (ResolveInfo r : packageManager.queryIntentActivities(iconIntent, 0)) {
                IconPackInfo info = new IconPackInfo(r, packageManager);
                packages.put(r.activityInfo.packageName, info);
            }
            iconIntent.removeCategory(category);
        }
        return packages;
    }
}
