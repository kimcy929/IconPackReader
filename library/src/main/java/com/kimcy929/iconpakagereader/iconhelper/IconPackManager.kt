package com.kimcy929.iconpakagereader.iconhelper

import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Created by kimcy929 08/11/2016
 */
object IconPackManager {

    private val sSupportedActions = arrayOf("org.adw.launcher.THEMES", "com.gau.go.launcherex.theme")

    private val sSupportedCategories =
        arrayOf("com.fede.launcher.THEME_ICONPACK", "com.anddoes.launcher.THEME", "com.teslacoilsw.launcher.THEME")

    fun getSupportedPackages(context: Context): Map<String, IconPackInfo> {
        var iconIntent = Intent()
        val packages = HashMap<String, IconPackInfo>()
        val packageManager = context.packageManager
        for (action in sSupportedActions) {
            iconIntent.action = action
            for (r in packageManager.queryIntentActivities(iconIntent, 0)) {
                val info = IconPackInfo(r, packageManager)
                packages[r.activityInfo.packageName] = info
            }
        }
        iconIntent = Intent(Intent.ACTION_MAIN)
        for (category in sSupportedCategories) {
            iconIntent.addCategory(category)
            for (r in packageManager.queryIntentActivities(iconIntent, 0)) {
                val info = IconPackInfo(r, packageManager)
                packages[r.activityInfo.packageName] = info
            }
            iconIntent.removeCategory(category)
        }
        return packages
    }
}
