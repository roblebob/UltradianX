@file:JvmMultifileClass

package com.roblebob.ultradianx.util




object UtilKt {

    @JvmStatic
    fun getRidOfNanos(s: String?): String? {
        return s?.replace("\\.[0-9]*Z".toRegex(), "Z") ?: s
    }

    @JvmStatic
    fun list2String(list: List<String>): String = list.joinToString(" ")


}