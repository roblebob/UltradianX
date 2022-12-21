@file:JvmMultifileClass

package com.roblebob.ultradianx.util

import com.roblebob.ultradianx.repository.model.Adventure


object UtilKt {

    @JvmStatic
    fun getRidOfMillis(s: String?): String? {
        return s?.replace("\\.[0-9]*Z".toRegex(), "Z") ?: s
    }

    @JvmStatic
    fun list2String(list: List<String>): String = list.joinToString(" ")

    @JvmStatic
    fun adventureList2Titles( list: List<Adventure>) : String {
        val newList = list.map { it.title }
        return newList.joinToString("\n")
    }
}