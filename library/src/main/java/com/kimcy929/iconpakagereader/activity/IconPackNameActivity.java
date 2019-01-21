package com.kimcy929.iconpakagereader.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kimcy929.iconpakagereader.R;
import com.kimcy929.iconpakagereader.adapter.IconPackNameAdapter;
import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo;
import com.kimcy929.iconpakagereader.iconhelper.IconPackManager;
import com.kimcy929.iconpakagereader.utils.Constant;
import com.kimcy929.iconpakagereader.utils.IconPackageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class IconPackNameActivity extends AppCompatActivity
        implements IconPackNameAdapter.ClickListener {

    private List<IconPackInfo> listIconPack;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_pack_name);

        PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .edit().putBoolean("icon_pack_night_mode", isNightModeEnable())
                .apply();

        configureRecyclerView();

        getListIconPack();
    }

    private boolean isNightModeEnable() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void configureRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    private void getListIconPack() {
        Map<String, IconPackInfo> packInfoMap = IconPackManager.getSupportedPackages(this);
        listIconPack = new ArrayList<>(packInfoMap.values());
        Collections.sort(listIconPack, IconPackageUtils.ALPHA_ICONPACK_NAME_COMPARATOR);

        final String playStorePackageName = "com.android.vending";
        final String playStoreSearchUri = "market://search?q=Icon+Pack";

        Drawable drawable = IconPackageUtils.getDrawableFromPackageName(this, playStorePackageName);
        String playStoreName = IconPackageUtils.getAppNameFromPackageName(this, playStorePackageName);
        if (TextUtils.isEmpty(playStoreName)) {
            playStoreName = "Google Play Store";
        }
        IconPackInfo playStoreInfo = new IconPackInfo(playStoreSearchUri, playStoreName, drawable);

        listIconPack.add(playStoreInfo);

        recyclerView.setAdapter(new IconPackNameAdapter(this, listIconPack, this));
    }

    @Override
    public void onClick(int position) {
        if (position < listIconPack.size() - 1) {
            IconPackInfo iconPack = listIconPack.get(position);
            Intent intent = new Intent(this, IconListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            intent.putExtra(Constant.ICON_PACK_NAME_EXTRA, iconPack.getLabel());
            intent.putExtra(Constant.ICON_PACK_PACKAGENAME_EXTRA, iconPack.getPackageName());
            startActivity(intent);
        } else {
            IconPackInfo iconPack = listIconPack.get(position);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(iconPack.getPackageName()));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Timber.tag(IconPackNameActivity.class.getSimpleName()).e(e.getLocalizedMessage());
            }
        }
    }
}
