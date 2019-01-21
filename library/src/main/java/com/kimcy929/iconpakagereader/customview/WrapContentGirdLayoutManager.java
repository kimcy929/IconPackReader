package com.kimcy929.iconpakagereader.customview;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WrapContentGirdLayoutManager extends GridLayoutManager {

    public WrapContentGirdLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }
}