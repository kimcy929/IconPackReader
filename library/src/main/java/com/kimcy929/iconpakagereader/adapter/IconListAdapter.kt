package com.kimcy929.iconpakagereader.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kimcy929.iconpakagereader.R
import com.kimcy929.iconpakagereader.customview.SquareImageView
import com.kimcy929.iconpakagereader.iconhelper.IconPackHelper
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

/**
 * Created by Kimcy929 on 07/11/2016.
 */
class IconListAdapter(private val itemViewClickListener: ItemViewClickListener) :
    RecyclerView.Adapter<IconListAdapter.ViewHolder>() {

    private var icons: List<String>? = null
    private val originIcons = ArrayList<String>()
    private var iconPackHelper: IconPackHelper? = null
    private var compositeDisposable: CompositeDisposable? = null

    fun addData(icons: List<String>, iconPackHelper: IconPackHelper, compositeDisposable: CompositeDisposable) {
        this.iconPackHelper = iconPackHelper
        this.icons = icons
        this.compositeDisposable = compositeDisposable

        if (originIcons.isEmpty()) {
            originIcons.addAll(icons)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindIcon(position)
    }

    override fun getItemCount(): Int {
        return if (icons == null) 0 else icons!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var imageIcon: SquareImageView = itemView.findViewById(R.id.imageIcon)

        init {
            itemView.setOnClickListener { itemViewClickListener.onClick(icons!![adapterPosition], imageIcon) }
        }

        fun bindIcon(position: Int) {
            compositeDisposable!!.add(
                Single.fromCallable<Drawable> { iconPackHelper!!.loadDrawable(icons!![position]) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { drawable -> imageIcon.setImageDrawable(drawable) },
                        { error -> Timber.e(error, "Error load drawable -> ") }
                    )
            )
        }
    }

    interface ItemViewClickListener {
        fun onClick(drawableName: String, imageView: SquareImageView)
    }

    fun filter(query: String): Observable<DiffUtil.DiffResult> {
        return Observable.fromCallable<List<String>> {
            val newIcons: List<String>
            if (query.isEmpty() || query.isBlank()) {
                newIcons = originIcons
            } else {
                newIcons = originIcons.filter { it.contains(query.replace(" ", "_"), true) }
            }
            newIcons
        }
        .map<DiffUtil.DiffResult> { newIcons ->
            val diffResult = DiffIcon(newIcons, icons).let {
                DiffUtil.calculateDiff(it)
            }
            icons = newIcons
            diffResult
        }
        .subscribeOn(Schedulers.computation())
    }

    private inner class DiffIcon(internal var newIcons: List<String>?, internal var oldIcons: List<String>?) :
        DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return if (oldIcons != null) oldIcons!!.size else 0
        }

        override fun getNewListSize(): Int {
            return if (newIcons != null) newIcons!!.size else 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldIcons!![oldItemPosition] == newIcons!![newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldIcons!![oldItemPosition] == newIcons!![newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldIcons!![oldItemPosition] == newIcons!![newItemPosition]) null else Any()
        }
    }
}
