package com.roblebob.ultradianx.ui.adapter;


import android.content.res.ColorStateList;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.roblebob.ultradianx.R;

import java.util.ArrayList;
import java.util.List;


public class DetailsRVAdapter extends RecyclerView.Adapter<DetailsRVAdapter.DetailsRVViewHolder> {
    private int mColor;
    public void setColor(int color) {
        mColor = color;
    }


    List<String> mDetails = new ArrayList<>();

    public void submit(List<String> details) {

        ListDiffCallback< String> listDiffCallback = new ListDiffCallback<>(mDetails, details);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mDetails.clear();
        mDetails.addAll( details);
        diffResult.dispatchUpdatesTo( this);
    }


    @NonNull
    @Override
    public DetailsRVViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_details, parent, false);
        return new DetailsRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsRVViewHolder holder, int position) {
        holder.textView.setText(Html.fromHtml( mDetails.get(position), Html.FROM_HTML_MODE_COMPACT));
        holder.textView.setTextColor( mColor);
        holder.imageView.setImageTintList( ColorStateList.valueOf( mColor));
    }



    @Override
    public int getItemCount() {
        return mDetails.size();
    }





    public static class DetailsRVViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public DetailsRVViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
