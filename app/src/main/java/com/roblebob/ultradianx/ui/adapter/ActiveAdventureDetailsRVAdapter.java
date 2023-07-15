package com.roblebob.ultradianx.ui.adapter;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;

import java.util.ArrayList;
import java.util.List;



public class ActiveAdventureDetailsRVAdapter
        extends RecyclerView.Adapter<ActiveAdventureDetailsRVAdapter.ActiveAdventureDetailsRVAdapterViewHolder> {

    List< String> mDetails = new ArrayList<>();
    public void submit( List<String> details) {
        ListDiffCallback< String> listDiffCallback = new ListDiffCallback<String>(mDetails, details);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mDetails.clear();
        mDetails.addAll( details);
        diffResult.dispatchUpdatesTo( this);
    }


    @NonNull
    @Override
    public ActiveAdventureDetailsRVAdapter.ActiveAdventureDetailsRVAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater .from(parent.getContext()) .inflate(R.layout.single_item_active_adventure_details, parent, false);
        return new ActiveAdventureDetailsRVAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveAdventureDetailsRVAdapter.ActiveAdventureDetailsRVAdapterViewHolder holder, int position) {
        holder.textView.setText(Html.fromHtml( mDetails.get(position), Html.FROM_HTML_MODE_COMPACT));
    }

    @Override
    public int getItemCount() {
        return mDetails.size();
    }


    public static class ActiveAdventureDetailsRVAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ActiveAdventureDetailsRVAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_item_active_adventure_details_tv);
            textView.setMovementMethod( LinkMovementMethod.getInstance());
        }
    }
}
