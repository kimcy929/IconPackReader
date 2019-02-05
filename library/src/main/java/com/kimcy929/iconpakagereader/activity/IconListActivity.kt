package com.kimcy929.iconpakagereader.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kimcy929.iconpakagereader.R
import com.kimcy929.iconpakagereader.adapter.IconListAdapter
import com.kimcy929.iconpakagereader.customview.SquareImageView
import com.kimcy929.iconpakagereader.customview.WrapContentGirdLayoutManager
import com.kimcy929.iconpakagereader.iconhelper.IconPackHelper
import com.kimcy929.iconpakagereader.utils.IconPackConstant
import com.kimcy929.iconpakagereader.utils.IconPackageUtils
import com.kimcy929.iconpakagereader.utils.RxSearch
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class IconListActivity : AppCompatActivity(), IconListAdapter.ItemViewClickListener {

    private var progressBar: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private var toolbar: Toolbar? = null

    private var iconPackPackageName: String? = null
    private var iconPackName: String? = null

    private var adapter: IconListAdapter? = null

    private val compositeDisposable = CompositeDisposable()

    private val iconPackHelper = IconPackHelper()

    private val isNightModeEnable: Boolean
        get() = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getBoolean("icon_pack_night_mode", false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_icon_pack)

        getIntentData()

        configView()

        loadIcons(savedInstanceState)
    }

    private fun loadIcons(savedInstanceState: Bundle?) {
        Single.fromCallable<List<String>> {
            val packagesDrawables = ArrayList<String>()
            iconPackHelper.apply {
                packageName = iconPackPackageName
                setContext(this@IconListActivity)
                loadDrawableFile(packagesDrawables)
            }
            packagesDrawables
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(object : SingleObserver<List<String>> {

            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onSuccess(icons: List<String>) {
                if (!icons.isEmpty()) {
                    adapter!!.addData(icons, iconPackHelper, compositeDisposable)
                }
                progressBar!!.visibility = View.GONE

                val layoutManager = recyclerView!!.layoutManager
                if (savedInstanceState != null && layoutManager != null) {
                    layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable<Parcelable>(IconPackConstant.ICON_PACK_EXTRA_SCROLL_POSITION))
                }
            }

            override fun onError(e: Throwable) {
                progressBar!!.visibility = View.GONE
                Timber.e(e, "Error load icon from icon package -> %s", iconPackPackageName)
            }
        })
    }

    private fun configView() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)

        progressBar = findViewById(R.id.progressBar)

        var spanCount = 5
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 8
        }
        adapter = IconListAdapter(this)
        recyclerView!!.apply {
            setHasFixedSize(true)
            layoutManager = WrapContentGirdLayoutManager(this@IconListActivity, spanCount)
            adapter = this@IconListActivity.adapter
        }
    }

    private fun getIntentData() {
        val intent = intent
        if (intent.extras != null) {
            iconPackName = intent.getStringExtra(IconPackConstant.ICON_PACK_NAME_EXTRA)
            iconPackPackageName = intent.getStringExtra(IconPackConstant.ICON_PACK_PACKAGENAME_EXTRA)
            setTitleActionBar()
        }
    }

    private fun setTitleActionBar() {
        supportActionBar?.apply {
            title = iconPackName
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onClick(drawableName: String, imageView: SquareImageView) {

        @SuppressLint("InflateParams") val view = LayoutInflater.from(this).inflate(R.layout.preivew_icon_layout, null)
        val squareImageView = view.findViewById<ImageView>(R.id.icon)
        squareImageView.setImageDrawable(imageView.drawable)

        val dialog = MaterialAlertDialogBuilder(this, R.style.AlertDialogIconPackReaderStyle)
            .setTitle(IconPackageUtils.capitalizeWord(drawableName))
            .setView(view)
            .setNegativeButton(R.string.dismiss, null)
            .setPositiveButton(R.string.ok) { _, _ ->
                val intent = Intent(this@IconListActivity, IconListActivity::class.java)
                val bm = (squareImageView.drawable as BitmapDrawable).bitmap
                intent.putExtra(IconPackConstant.ICON_PACK_BITMAP_ICON_EXTRA, bm)
                      .putExtra(IconPackConstant.ICON_PACK_ICON_NAME_EXTRA, drawableName)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            .create()

        dialog.delegate.setLocalNightMode(if (isNightModeEnable) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)

        dialog.show()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        val layoutManager = recyclerView!!.layoutManager
        if (layoutManager != null) {
            outState.putParcelable(IconPackConstant.ICON_PACK_EXTRA_SCROLL_POSITION, layoutManager.onSaveInstanceState())
        }
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView

        val disposable = RxSearch.stringQuery(searchView)
            .debounce(100, TimeUnit.MILLISECONDS)
            .switchMap<DiffUtil.DiffResult> { query -> adapter!!.filter(query) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { diffResult -> diffResult.dispatchUpdatesTo(adapter!!) },
                { error -> Timber.e(error, "Error search icon -> ") }
            )
        compositeDisposable.add(disposable)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        } else if (id == R.id.action_night_mode) {
            if (!isNightModeEnable) {
                PreferenceManager
                    .getDefaultSharedPreferences(applicationContext)
                    .edit()
                    .putBoolean("icon_pack_night_mode", true)
                    .apply()
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                PreferenceManager
                    .getDefaultSharedPreferences(applicationContext)
                    .edit()
                    .putBoolean("icon_pack_night_mode", false)
                    .apply()
                delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
