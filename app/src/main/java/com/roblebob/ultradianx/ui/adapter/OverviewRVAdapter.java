package com.roblebob.ultradianx.ui.adapter;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;

import java.util.ArrayList;
import java.util.List;


public class OverviewRVAdapter extends RecyclerView.Adapter<OverviewRVAdapter.OverviewRVViewHolder> {
    private List<Adventure> mAdventureList = new ArrayList<>();
    public void submit( List<Adventure> adventureList) {
        mAdventureList = new ArrayList<>(adventureList);
        notifyDataSetChanged();
    }


    public interface ItemClickListener {
        void onItemClickListener(Adventure adventure, Integer integer);
    }
    ItemClickListener mItemClickListener;
    public OverviewRVAdapter(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public OverviewRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OverviewRVViewHolder(
                LayoutInflater
                        .from( parent.getContext())
                        .inflate( R.layout.single_item_adventures, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OverviewRVViewHolder holder, int position) {
        holder.textView.setText( Html.fromHtml( mAdventureList.get(position).getTitle() , Html.FROM_HTML_MODE_COMPACT));
        holder.itemView.setOnClickListener( v -> mItemClickListener.onItemClickListener( mAdventureList.get(position), position));
    }

    @Override
    public int getItemCount() {
        return mAdventureList.size();
    }


    public static class OverviewRVViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public OverviewRVViewHolder( View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_item_adventures_tv);
        }
    }
}
