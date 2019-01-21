package com.kimcy929.iconpakagereader.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import io.reactivex.subjects.BehaviorSubject;

public class RxSearch {
    public static BehaviorSubject<String> stringQuery(@NonNull SearchView searchView) {

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                behaviorSubject.hasComplete();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    behaviorSubject.onNext("");
                } else {
                    behaviorSubject.onNext(newText);
                }
                return true;
            }
        });

        return behaviorSubject;
    }
}
