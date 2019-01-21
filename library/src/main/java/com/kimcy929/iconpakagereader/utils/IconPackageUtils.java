package com.kimcy929.iconpakagereader.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo;
import com.kimcy929.iconpakagereader.iconhelper.IconPackManager;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Kimcy929 on 07/11/2016.
 */
public class IconPackageUtils {

    public static final Comparator<IconPackInfo> ALPHA_ICONPACK_NAME_COMPARATOR = new Comparator<IconPackInfo>() {
        Collator sCollator = Collator.getInstance();

        @Override
        public int compare(IconPackInfo iconPackInfo, IconPackInfo t1) {
            return sCollator.compare(iconPackInfo.getLabel(), t1.getLabel());
        }
    };

    public static final Comparator<String> DRAWABLE_NAME_COMPARATOR = new Comparator<String>() {
        Collator sCollator = Collator.getInstance();

        @Override
        public int compare(String drawableName1, String drawableName2) {
            return sCollator.compare(drawableName1, drawableName2);
        }
    };

    //http://stackoverflow.com/a/20229254
    public static String capitalizeWord(String str) {
        str = str.replaceAll("_", " ");
        Pattern pattern = Pattern.compile("\\b([a-z])([\\w]*)");
        Matcher matcher = pattern.matcher(str);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, matcher.group(1).toUpperCase() + matcher.group(2));
        }
        return matcher.appendTail(buffer).toString();
    }

    public static Drawable getDrawableFromPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAppNameFromPackageName(Context context, String packageName) {
        try {
            PackageManager packageManager= context.getPackageManager();
            return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<IconPackInfo> getIconPackages(Context context) {
        Map<String, IconPackInfo> packInfoMap = IconPackManager.getSupportedPackages(context);
        List<IconPackInfo> listIconPack = new ArrayList<>(packInfoMap.values());
        Collections.sort(listIconPack, IconPackageUtils.ALPHA_ICONPACK_NAME_COMPARATOR);

        final String playStorePackageName = "com.android.vending";
        final String playStoreSearchUri = "market://search?q=Icon+Pack";

        Drawable drawable = IconPackageUtils.getDrawableFromPackageName(context, playStorePackageName);
        String playStoreName = IconPackageUtils.getAppNameFromPackageName(context, playStorePackageName);
        if (TextUtils.isEmpty(playStoreName)) {
            playStoreName = "Google Play Store";
        }
        IconPackInfo playStoreInfo = new IconPackInfo(playStoreSearchUri, playStoreName, drawable);
        listIconPack.add(playStoreInfo);

        return listIconPack;
    }
}
