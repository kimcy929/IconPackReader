package com.kimcy929.iconpakagereader.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kimcy929.iconpakagereader.R;
import com.kimcy929.iconpakagereader.iconhelper.IconPackInfo;

import java.util.List;

/**
 * Created by Kimcy929 on 07/11/2016.
 */
public class IconPackNameAdapter extends RecyclerView.Adapter<IconPackNameAdapter.ViewHolder> {

    private List<IconPackInfo> listIconPack;
    private int iconSize;
    private ClickListener clickListener;

    public IconPackNameAdapter(Context context, List<IconPackInfo> listIconPack, ClickListener clickListener) {
        this.listIconPack = listIconPack;
        this.clickListener = clickListener;
        iconSize = context.getResources().getDimensionPixelSize(android.R.dimen.app_icon_size);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_pack_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IconPackInfo iconPack = listIconPack.get(position);
        holder.txtIconPackName.setText(iconPack.getLabel());
        Drawable drawable = iconPack.getIcon();
        if (drawable != null) {
            drawable.setBounds(0, 0, iconSize, iconSize);
            holder.txtIconPackName.setCompoundDrawables(drawable, null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return listIconPack.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtIconPackName;

        public ViewHolder(View itemView) {
            super(itemView);
            txtIconPackName = itemView.findViewById(R.id.txtIconPackName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface ClickListener {
        void onClick(int position);
    }

}
