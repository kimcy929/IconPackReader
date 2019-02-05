package com.kimcy929.iconpakagereader.iconhelper

import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable

/**
 * Created by Kimcy929 on 08/11/2016.
 */


class IconPackInfo {
    var packageName: String? = null
        private set
    var label: CharSequence? = null
        private set
    var icon: Drawable? = null
        private set

    internal constructor(resolveInfo: ResolveInfo, packageManager: PackageManager) {
        packageName = resolveInfo.activityInfo.packageName
        icon = resolveInfo.loadIcon(packageManager)
        label = resolveInfo.loadLabel(packageManager)
    }

    constructor(packageName: String, label: CharSequence, icon: Drawable) {
        this.packageName = packageName
        this.label = label
        this.icon = icon
    }
}
