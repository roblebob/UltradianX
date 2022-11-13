package com.roblebob.ultradianx.ui.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TagsRVAdapter extends RecyclerView.Adapter<TagsRVAdapter.TagsRVViewHolder> {



    @NonNull
    @Override
    public TagsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TagsRVViewHolder holder, int position) {

    }



    @Override
    public int getItemCount() {
        return 0;
    }

    public static class TagsRVViewHolder extends RecyclerView.ViewHolder {

        public TagsRVViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
