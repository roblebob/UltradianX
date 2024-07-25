package com.roblebob.ultradianx.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.roblebob.ultradianx.R
import com.roblebob.ultradianx.repository.model.Adventure
import com.roblebob.ultradianx.ui.adapter.OverviewRVAdapter.OverviewRVViewHolder
import com.roblebob.ultradianx.ui.extra.AdventureDisplay
import java.util.Objects

class OverviewRVAdapter(fragment: Fragment) : RecyclerView.Adapter<OverviewRVViewHolder>() {
    fun getAdventureId(position: Int): Int = mAdventureList[position].id
    fun getAdventurePriority(position: Int): Double = mAdventureList[position].priority

    fun setAdventurePriority(position: Int, priority: Double) {
        mAdventureList[position].priority = priority
        notifyItemChanged(position)
    }




    private val mAdventureList = mutableListOf<Adventure>()
    fun submit(adventureList: List<Adventure>) {
        val listDiffCallback = ListDiffCallback( mAdventureList, adventureList)
        val diffResult = DiffUtil.calculateDiff( listDiffCallback)
        mAdventureList.clear()
        mAdventureList.addAll( adventureList)
        diffResult.dispatchUpdatesTo(this)
    }

    private var mExtended = false
    var isExtended: Boolean
        get() = mExtended
        set(extended) {
            mExtended = extended
            notifyItemChanged(itemCount)
        }

    interface Callback {
        fun onItemClickListener(id: Int, position: Int)
        fun onNewAdventureCreated(title: String)
    }

    private val mCallback: Callback = fragment as Callback
    private val mContext: Context = fragment.requireContext()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OverviewRVViewHolder {
        return OverviewRVViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.single_item_adventures, parent, false)
        )
    }

    override fun onBindViewHolder(holder: OverviewRVViewHolder, position: Int) {
        if (position == mAdventureList.size && mExtended) {
            holder.textInputLayout.visibility = View.VISIBLE
            holder.textView.visibility = View.INVISIBLE
            holder.textInputLayout.setEndIconOnClickListener { v: View? ->
                val title = Objects.requireNonNull(holder.textInputEditText.text).toString()
                if (title.isNotEmpty()) {
                    mCallback.onNewAdventureCreated(title)
                    holder.textInputEditText.onEditorAction(EditorInfo.IME_ACTION_DONE)
                }
            }
            return
        }
        val adventure = mAdventureList[position]
        val adventureDisplay = AdventureDisplay(adventure, mContext)
        holder.textInputLayout.visibility = View.INVISIBLE
        holder.textView.visibility = View.VISIBLE
        holder.textView.text = adventureDisplay.titleToSpannableStringBuilder(3)
        holder.itemView.setOnClickListener { _: View? -> mCallback.onItemClickListener( adventure.id, position) }


        // displacing the text to the left depending on the priority
        val constraintLayout = holder.itemView as ConstraintLayout
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.setHorizontalBias(R.id.single_item_adventures_tv, 1.0f - adventure.priority.toFloat())
        constraintSet.applyTo(constraintLayout)
    }

    override fun getItemCount(): Int = mAdventureList.size + (if (isExtended) 1 else 0)


    class OverviewRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.single_item_adventures_tv)
        var textInputLayout: TextInputLayout = itemView.findViewById(R.id.single_item_adventures_text_input_layout)
        var textInputEditText: TextInputEditText = itemView.findViewById(R.id.single_item_adventures_text_input_edit_text)
    }
}