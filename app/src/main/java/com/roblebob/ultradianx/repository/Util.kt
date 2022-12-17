@file:JvmMultifileClass

package com.roblebob.ultradianx.repository

class Util {

    fun list2String(list: List<String>): String = list.joinToString(" ")

    fun getRidOfNanos(s: String?): String? {
        return s?.replace("\\.[0-9]*Z".toRegex(), "Z") ?: s
    }
}