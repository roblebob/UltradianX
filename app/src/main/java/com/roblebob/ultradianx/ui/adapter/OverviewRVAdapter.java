package com.roblebob.ultradianx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;
import com.roblebob.ultradianx.ui.extra.AdventureDisplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OverviewRVAdapter extends RecyclerView.Adapter<OverviewRVAdapter.OverviewRVViewHolder> {
    private final List<Adventure> mAdventureList = new ArrayList<>();
    public void submit( List<Adventure> adventureList) {

        ListDiffCallback< Adventure> listDiffCallback = new ListDiffCallback<>(mAdventureList, adventureList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff( listDiffCallback);
        mAdventureList.clear();
        mAdventureList.addAll( adventureList);
        diffResult.dispatchUpdatesTo( this);
    }

    private boolean mExtended = false;
    public void setExtended(boolean extended) {
        mExtended = extended;
        notifyItemChanged(getItemCount());
    }
    public boolean isExtended() {
        return mExtended;
    }

    public interface Callback {
        void onItemClickListener(Adventure adventure, Integer integer);
        void onNewAdventureCreated(String title);
    }
    Callback mCallback;
    Context mContext;
    public OverviewRVAdapter(Fragment fragment) {
        this.mCallback = fragment instanceof Callback ? (Callback) fragment : null;
        this.mContext = fragment.getContext();
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
        if (position == mAdventureList.size() && mExtended) {
            holder.textInputLayout.setVisibility( View.VISIBLE);
            holder.textView.setVisibility( View.INVISIBLE);
            holder.textInputLayout.setEndIconOnClickListener((v) -> {
                String title = Objects.requireNonNull(holder.textInputEditText.getText()).toString();
                if (!title.isEmpty()) {
                    mCallback.onNewAdventureCreated(title);
                    holder.textInputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);
                }
            } );
            return;
        }

        Adventure adventure = mAdventureList.get(position);
        AdventureDisplay adventureDisplay = new AdventureDisplay( adventure, mContext);

        holder.textInputLayout.setVisibility( View.INVISIBLE);
        holder.textView.setVisibility( View.VISIBLE);
        holder.textView.setText( adventureDisplay.titleToSpannableStringBuilder(3) );
        holder.itemView.setOnClickListener( v -> mCallback.onItemClickListener( adventure, position));

        // displacing the text to the left depending on the priority
        ConstraintLayout constraintLayout = (ConstraintLayout) holder.itemView;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone( constraintLayout);
        constraintSet.setHorizontalBias( R.id.single_item_adventures_tv, 1 - adventure.getPriority().floatValue() / 100);
        constraintSet.applyTo( constraintLayout);
    }

    @Override
    public int getItemCount() {
        return (mExtended)  ?  mAdventureList.size() + 1  :  mAdventureList.size();
    }


    public static class OverviewRVViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextInputLayout textInputLayout;
        TextInputEditText textInputEditText;

        public OverviewRVViewHolder( View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.single_item_adventures_tv);
            textInputLayout = itemView.findViewById(R.id.single_item_adventures_text_input_layout);
            textInputEditText = itemView.findViewById(R.id.single_item_adventures_text_input_edit_text);
        }
    }


}
