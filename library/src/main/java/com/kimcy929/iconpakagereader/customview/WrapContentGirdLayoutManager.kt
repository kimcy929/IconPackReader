package com.kimcy929.iconpakagereader.customview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WrapContentGirdLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (ignored: IndexOutOfBoundsException) {
        }

    }
}