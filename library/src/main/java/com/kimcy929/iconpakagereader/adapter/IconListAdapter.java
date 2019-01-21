package com.kimcy929.iconpakagereader.adapter;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.kimcy929.iconpakagereader.R;
import com.kimcy929.iconpakagereader.customview.SquareImageView;
import com.kimcy929.iconpakagereader.iconhelper.IconPackHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Kimcy929 on 07/11/2016.
 */
public class IconListAdapter extends RecyclerView.Adapter<IconListAdapter.ViewHolder> {

    private List<String> icons;
    private List<String> originIcons;
    private IconPackHelper iconPackHelper;
    private ItemViewClickListener itemViewClickListener;

    public IconListAdapter(ItemViewClickListener itemViewClickListener) {
        this.itemViewClickListener = itemViewClickListener;
    }

    public void addData(List<String> icons, IconPackHelper iconPackHelper) {
        this.iconPackHelper = iconPackHelper;
        this.icons = icons;
        if (originIcons == null || originIcons.isEmpty()) {
            originIcons = new ArrayList<>();
            originIcons.addAll(icons);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.bindIcon(position);
    }

    @Override
    public int getItemCount() {
        return icons == null ? 0 : icons.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {

        public SquareImageView imageIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIcon);
            itemView.setOnClickListener(view -> itemViewClickListener.onClick(icons.get(getAdapterPosition()), imageIcon));
        }

        public void bindIcon(int position) {
            new GetDrawableTask(this).execute(icons.get(position));
        }

        public IconPackHelper getIconPackHelper() {
            return iconPackHelper;
        }
    }

    public interface ItemViewClickListener {
        void onClick(String drawableName, SquareImageView imageView);
    }

    @SuppressWarnings("WeakerAccess")
    private static class GetDrawableTask extends AsyncTask<String, Void, Drawable> {

        private WeakReference<ViewHolder> viewHolderWeakReference;

        public GetDrawableTask(ViewHolder viewHolder) {
            viewHolderWeakReference = new WeakReference<>(viewHolder);
        }

        @Override
        protected Drawable doInBackground(String... strings) {
            if (viewHolderWeakReference.get() != null) {
                return viewHolderWeakReference.get().getIconPackHelper().loadDrawable(strings[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            if (viewHolderWeakReference.get() != null) {
                if (drawable != null) {
                    viewHolderWeakReference.get().imageIcon.setImageDrawable(drawable);
                }
            }
        }
    }

    public Observable<DiffUtil.DiffResult> filter(String query) {
        return Observable.fromCallable(() -> {
            List<String> newIcons = new ArrayList<>();
            if (TextUtils.isEmpty(query)) {
                newIcons = originIcons;
            } else {
                Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
                for (String icon : originIcons) {
                    Matcher m = p.matcher(icon);
                    if (m.find()) {
                        newIcons.add(icon);
                    }
                }
            }
            return newIcons;
        })
        .map(newIcons -> {
            DiffIcon diffIcon = new DiffIcon(newIcons, icons);
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffIcon);
            icons = newIcons;
            return diffResult;
        })
        .subscribeOn(Schedulers.computation());
    }

    private class DiffIcon extends DiffUtil.Callback {
        List<String> newIcons;
        List<String> oldIcons;

        @SuppressWarnings("WeakerAccess")
        public DiffIcon(List<String> newIcons, List<String> oldIcons) {
            this.newIcons = newIcons;
            this.oldIcons = oldIcons;
        }

        @Override
        public int getOldListSize() {
            return oldIcons != null ? oldIcons.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newIcons != null ? newIcons.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(oldIcons.get(oldItemPosition), newIcons.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return TextUtils.equals(oldIcons.get(oldItemPosition), newIcons.get(newItemPosition));
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            if (TextUtils.equals(oldIcons.get(oldItemPosition), newIcons.get(newItemPosition))) return null;
            return new Object();
        }
    }
}
