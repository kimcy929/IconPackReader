package com.kimcy929.iconpakagereader.utils

import android.text.TextUtils
import androidx.appcompat.widget.SearchView
import io.reactivex.subjects.BehaviorSubject

object RxSearch {
    fun stringQuery(searchView: SearchView): BehaviorSubject<String> {

        val behaviorSubject = BehaviorSubject.create<String>()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                behaviorSubject.hasComplete()
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    behaviorSubject.onNext("")
                } else {
                    behaviorSubject.onNext(newText)
                }
                return true
            }
        })

        return behaviorSubject
    }
}
