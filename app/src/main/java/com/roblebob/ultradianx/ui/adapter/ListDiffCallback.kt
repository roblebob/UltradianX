package com.roblebob.ultradianx.ui.adapter

import androidx.recyclerview.widget.DiffUtil

class ListDiffCallback<T>(private val mOld: List<T>, private val mNew: List<T>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return mOld.size
    }

    override fun getNewListSize(): Int {
        return mNew.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOld[oldItemPosition] == mNew[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOld[oldItemPosition] == mNew[newItemPosition]
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}