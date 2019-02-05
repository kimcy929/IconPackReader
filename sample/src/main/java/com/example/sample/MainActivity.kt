package com.example.sample

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kimcy929.iconpakagereader.activity.IconPackNameActivity
import com.kimcy929.iconpakagereader.utils.IconPackConstant
import com.kimcy929.iconpakagereader.utils.IconPackageUtils


class MainActivity : AppCompatActivity() {

    private val REQUEST_ICON = 1

    private var previewIcon: ImageView? = null
    private var txtIconName: TextView? = null
    private var button: Button? = null

    private var bm: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewIcon = findViewById(R.id.icon)
        txtIconName = findViewById(R.id.txtIconName)

        button = findViewById(R.id.button)
        button!!.setOnClickListener { v -> startActivityForResult(Intent(applicationContext, IconPackNameActivity::class.java), REQUEST_ICON) }
    }

    override fun onDestroy() {
        if (bm != null && !bm!!.isRecycled) {
            Log.d("Tag", "onDestroy Recycle bitmap")
            bm!!.recycle()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ICON && resultCode == Activity.RESULT_OK) {
            bm = data!!.getParcelableExtra(IconPackConstant.ICON_PACK_BITMAP_ICON_EXTRA)
            if (bm != null) {
                previewIcon!!.setImageBitmap(bm)
                txtIconName!!.text = IconPackageUtils.capitalizeWord(data.getStringExtra(IconPackConstant.ICON_PACK_ICON_NAME_EXTRA))
            }
        }
    }
}
