package com.kimcy929.iconpakagereader.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.kimcy929.iconpakagereader.R
import com.kimcy929.iconpakagereader.adapter.IconPackNameAdapter
import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo
import com.kimcy929.iconpakagereader.iconhelper.IconPackManager
import com.kimcy929.iconpakagereader.utils.IconPackConstant
import com.kimcy929.iconpakagereader.utils.IconPackageUtils
import timber.log.Timber
import java.util.*

class IconPackNameActivity : AppCompatActivity(), IconPackNameAdapter.ClickListener {

    private var listIconPack: MutableList<IconPackInfo>? = null
    private var recyclerView: RecyclerView? = null

    private val isNightModeEnable: Boolean
        get() {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return currentNightMode == Configuration.UI_MODE_NIGHT_YES
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_icon_pack_name)

        PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .edit().putBoolean("icon_pack_night_mode", isNightModeEnable)
            .apply()

        configureRecyclerView()

        getListIconPack()
    }

    private fun configureRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView!!.setHasFixedSize(true)
    }

    private fun getListIconPack() {
        val packInfoMap = IconPackManager.getSupportedPackages(this)
        listIconPack = ArrayList(packInfoMap.values)
        Collections.sort(listIconPack!!, IconPackageUtils.ALPHA_ICONPACK_NAME_COMPARATOR)

        val playStorePackageName = "com.android.vending"
        val playStoreSearchUri = "market://search?q=Icon+Pack"

        val drawable = IconPackageUtils.getDrawableFromPackageName(this, playStorePackageName)
        var playStoreName = IconPackageUtils.getAppNameFromPackageName(this, playStorePackageName)
        if (TextUtils.isEmpty(playStoreName)) {
            playStoreName = "Google Play Store"
        }
        val playStoreInfo = IconPackInfo(playStoreSearchUri, playStoreName!!, drawable!!)

        listIconPack!!.add(playStoreInfo)

        recyclerView!!.adapter = IconPackNameAdapter(this, listIconPack as ArrayList<IconPackInfo>, this)
    }

    override fun onClick(position: Int) {
        if (position < listIconPack!!.size - 1) {
            val iconPack = listIconPack!![position]
            val intent = Intent(this, IconListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                .putExtra(IconPackConstant.ICON_PACK_NAME_EXTRA, iconPack.label)
                .putExtra(IconPackConstant.ICON_PACK_PACKAGENAME_EXTRA, iconPack.packageName)
                .flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
            startActivity(intent)
        } else {
            val iconPack = listIconPack!![position]
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(iconPack.packageName)
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Timber.tag(IconPackNameActivity::class.java.simpleName).e(e.localizedMessage)
            }
        }
    }
}
