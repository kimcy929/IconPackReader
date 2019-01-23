package com.kimcy929.iconpakagereader.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.kimcy929.iconpakagereader.R;
import com.kimcy929.iconpakagereader.adapter.IconListAdapter;
import com.kimcy929.iconpakagereader.customview.WrapContentGirdLayoutManager;
import com.kimcy929.iconpakagereader.customview.SquareImageView;
import com.kimcy929.iconpakagereader.iconhelper.IconPackHelper;
import com.kimcy929.iconpakagereader.utils.Constant;
import com.kimcy929.iconpakagereader.utils.IconPackageUtils;
import com.kimcy929.iconpakagereader.utils.RxSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class IconListActivity extends AppCompatActivity implements IconListAdapter.ItemViewClickListener {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private String iconPackPackageName;
    private String iconPackName;

    private IconListAdapter adapter;

   private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private IconPackHelper iconPackHelper = new IconPackHelper();

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_icon_pack);

        getIntentData();

        configView();

        loadIcons(savedInstanceState);
    }

    private void loadIcons(Bundle savedInstanceState) {
        Single.fromCallable(() -> {
            List<String> packagesDrawables = new ArrayList<>();
            iconPackHelper.packageName = iconPackPackageName;
            iconPackHelper.setContext(this);
            iconPackHelper.loadDrawableFile(packagesDrawables);
            return packagesDrawables;
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<String>>() {

            @Override
            public void onSubscribe(Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onSuccess(List<String> icons) {
                if (!icons.isEmpty()) {
                    adapter.addData(icons, iconPackHelper, compositeDisposable);
                }
                progressBar.setVisibility(View.GONE);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (savedInstanceState != null && layoutManager != null) {
                    layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(Constant.EXTRA_SCROLL_POSITION));
                }
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                Timber.e(e, "Error load icon from icon package -> %s", iconPackPackageName);
            }
        });
    }

    private void configView() {
        recyclerView = findViewById(R.id.recyclerView);

        progressBar = findViewById(R.id.progressBar);

        int spanCount = 5;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 8;
        }
        recyclerView.setHasFixedSize(true);
        WrapContentGirdLayoutManager gridLayoutManager = new WrapContentGirdLayoutManager(this, spanCount);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new IconListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            iconPackName = intent.getStringExtra(Constant.ICON_PACK_NAME_EXTRA);
            iconPackPackageName = intent.getStringExtra(Constant.ICON_PACK_PACKAGENAME_EXTRA);
            setTitleActionBar();
        }
    }

    private void setTitleActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(iconPackName);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onClick(final String drawableName, SquareImageView imageView) {

        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.preivew_icon_layout, null);
        final ImageView squareImageView = view.findViewById(R.id.icon);
        squareImageView.setImageDrawable(imageView.getDrawable());

        AlertDialog dialog = new AlertDialog.Builder(this, R.style.AlertDialogIconPackReaderStyle)
                .setTitle(IconPackageUtils.capitalizeWord(drawableName))
                .setView(view)
                .setNegativeButton(getString(R.string.dismiss), null)
                .setPositiveButton(getString(R.string.ok), (dialog1, which) -> {
                    Intent intent = new Intent(IconListActivity.this, IconListActivity.class);
                    Bitmap bm = ((BitmapDrawable) squareImageView.getDrawable()).getBitmap();
                    intent.putExtra(Constant.BITMAP_ICON_EXTRA, bm);
                    intent.putExtra(Constant.ICON_NAME_EXTRA, drawableName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                })
                .create();

        dialog.getDelegate().setLocalNightMode(isNightModeEnable() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        dialog.show();

    }

    private boolean isNightModeEnable() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("icon_pack_night_mode", false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null) {
            outState.putParcelable(Constant.EXTRA_SCROLL_POSITION, layoutManager.onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        Disposable disposable = RxSearch.stringQuery(searchView)
            .debounce(100, TimeUnit.MILLISECONDS)
            .switchMap(query -> adapter.filter(query))
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    diffResult -> diffResult.dispatchUpdatesTo(adapter),
                    error -> Timber.e(error, "Error search icon -> ")
            );
        compositeDisposable.add(disposable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_night_mode) {
            if (!isNightModeEnable()) {
                PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putBoolean("icon_pack_night_mode", true)
                        .apply();
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putBoolean("icon_pack_night_mode", false)
                        .apply();
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }
}
