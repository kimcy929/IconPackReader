package com.example.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kimcy929.iconpakagereader.activity.IconPackNameActivity;
import com.kimcy929.iconpakagereader.utils.Constant;
import com.kimcy929.iconpakagereader.utils.IconPackageUtils;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_ICON = 1;

    private ImageView previewIcon;
    private TextView txtIconName;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewIcon = findViewById(R.id.icon);
        txtIconName = findViewById(R.id.txtIconName);

        button = findViewById(R.id.button);
        button.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), IconPackNameActivity.class), REQUEST_ICON));
    }

    private Bitmap bm;

    @Override
    protected void onDestroy() {
        if (bm != null && !bm.isRecycled()) {
            Log.d("Tag", "onDestroy Recycle bitmap");
            bm.recycle();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ICON && resultCode == RESULT_OK) {
            bm = data.getParcelableExtra(Constant.BITMAP_ICON_EXTRA);
            if (bm != null) {
                previewIcon.setImageBitmap(bm);
                txtIconName.setText(IconPackageUtils.capitalizeWord(data.getStringExtra(Constant.ICON_NAME_EXTRA)));
            }
        }
    }
}
