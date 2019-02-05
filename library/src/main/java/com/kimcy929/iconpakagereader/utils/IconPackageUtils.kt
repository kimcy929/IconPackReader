package com.kimcy929.iconpakagereader.utils

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo
import com.kimcy929.iconpakagereader.iconhelper.IconPackManager
import java.text.Collator
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Kimcy929 on 07/11/2016.
 */
object IconPackageUtils {

    val ALPHA_ICONPACK_NAME_COMPARATOR: Comparator<IconPackInfo> = object : Comparator<IconPackInfo> {
        var sCollator = Collator.getInstance()

        override fun compare(iconPackInfo: IconPackInfo, t1: IconPackInfo): Int {
            return sCollator.compare(iconPackInfo.label, t1.label)
        }
    }

    val DRAWABLE_NAME_COMPARATOR: Comparator<String> = object : Comparator<String> {
        var sCollator = Collator.getInstance()

        override fun compare(drawableName1: String, drawableName2: String): Int {
            return sCollator.compare(drawableName1, drawableName2)
        }
    }

    //http://stackoverflow.com/a/20229254
    fun capitalizeWord(str: String): String {
        var str1 = str
        str1 = str1.replace("_".toRegex(), " ")
        val pattern = Pattern.compile("\\b([a-z])([\\w]*)")
        val matcher = pattern.matcher(str1)
        val buffer = StringBuffer()
        while (matcher.find()) {
            matcher.appendReplacement(buffer, matcher.group(1).toUpperCase() + matcher.group(2))
        }
        return matcher.appendTail(buffer).toString()
    }

    fun getDrawableFromPackageName(context: Context, packageName: String): Drawable? {
        try {
            return context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    fun getAppNameFromPackageName(context: Context, packageName: String): String? {
        try {
            val packageManager = context.packageManager
            return packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(
                    packageName,
                    PackageManager.GET_META_DATA
                )
            ) as String
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return null
    }

    fun getIconPackages(context: Context): List<IconPackInfo> {
        val listIconPack = arrayListOf<IconPackInfo>().apply {
            addAll(IconPackManager.getSupportedPackages(context).values.sortedWith(IconPackageUtils.ALPHA_ICONPACK_NAME_COMPARATOR))
        }
        val playStorePackageName = "com.android.vending"
        val playStoreSearchUri = "market://search?q=Icon+Pack"

        val drawable = IconPackageUtils.getDrawableFromPackageName(context, playStorePackageName)
        var playStoreName = IconPackageUtils.getAppNameFromPackageName(context, playStorePackageName)
        if (playStoreName.isNullOrEmpty()) {
            playStoreName = "Google Play Store"
        }
        val playStoreInfo = IconPackInfo(playStoreSearchUri, playStoreName, drawable!!)
        listIconPack.add(playStoreInfo)

        return listIconPack
    }
}
