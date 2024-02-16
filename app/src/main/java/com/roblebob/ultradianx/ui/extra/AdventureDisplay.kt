package com.roblebob.ultradianx.ui.extra

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import com.roblebob.ultradianx.R
import com.roblebob.ultradianx.repository.model.Adventure
import java.util.function.Function
import kotlin.math.pow

class AdventureDisplay(adventure: Adventure, context: Context) {
    private var mAdventure: Adventure
    private var mContext: Context

    init {
        mContext = context

        //mAdventure = Adventure(adventure)
        mAdventure = adventure
    }
    
    fun update(adventure: Adventure) {
        //mAdventure = Adventure(adventure)
        mAdventure = adventure
    }

    @JvmOverloads
    fun titleToSpannableStringBuilder(minFontSize: Int = MIN_FONT_SIZE): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(mAdventure.title)
        val maxFontSize: Int = mAdventure.priority.toInt()
        for (i in 0 until mAdventure.title.length) {
            val fontSize = maxFontSize - 3 * i
            spannableStringBuilder.setSpan(
                AbsoluteSizeSpan(fontSize.coerceAtLeast(minFontSize), true),
                i,
                i + 1,
                0
            )
        }
        val fcs = ForegroundColorSpan(color)
        val bss = StyleSpan(Typeface.BOLD)
        spannableStringBuilder.setSpan(fcs, 0, mAdventure.title.length, 0)
        return spannableStringBuilder
    }

    fun tagToSpannableStringBuilder(): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        spannableStringBuilder.append(mAdventure.tag)
        for (i in 0 until mAdventure.tag.length) {
            val fontSize = MIN_FONT_SIZE + 3 * i
            spannableStringBuilder.setSpan(AbsoluteSizeSpan(fontSize, true), i, i + 1, 0)
            val topAlignLineHeightSpan = TopAlignLineHeightSpan(3 * i)
            spannableStringBuilder.setSpan(topAlignLineHeightSpan, i, i + 1, 0)
        }
        val fcs = ForegroundColorSpan(color)
        val bss = StyleSpan(Typeface.BOLD)
        spannableStringBuilder.setSpan(fcs, 0, mAdventure.tag.length, 0)
        return spannableStringBuilder
    }

    val color: Int
        // TODO: only temporary
        get() {
            var color = mContext.getColor(R.color.tag_neutral)
            when (mAdventure.tag) {
                "health" -> color = mContext.getColor(R.color.tag_health)
                "theory" -> color = mContext.getColor(R.color.tag_theory)
                "coding" -> color = mContext.getColor(R.color.tag_coding)
                "music"  -> color = mContext.getColor(R.color.tag_music)
            }
            return color
        }

    fun priorityToTv(): String {
        return java.lang.String.valueOf(mAdventure.priority.toInt())
    }

    fun targetToSlider(): Float {
        return targetToSlider.apply(mAdventure.target)
    }

    fun targetToBar(): Int {
        var x = targetToSlider.apply(mAdventure.target)
        x *= 25.0f // mapping 0..4 to 0..100
        return x.toInt()
    }

    fun targetToTv(): String {
        val target = mAdventure.target
        return if (target <= 90) {
            target.toString()
        } else String.format(
            "%02d:%02d",
            mAdventure.target / 60,
            mAdventure.target % 60
        )
    }



    companion object {
        val TAG: String = AdventureDisplay::class.java.simpleName
        const val MIN_FONT_SIZE = 12

        /**
         * maps  0..1..2..3..4  to  0..10..90..360..1000
         */
        @JvmField
        val targetFromSlider = Function { `in`: Float ->
            var x = `in`.toDouble()
            x *= (x + 1.0)
            x /= 2.0
            x = x.pow(2.0)
            x *= 10.0
            x.toInt()
        }

        /**
         * maps  0..10..90..360..1000  to  0..1..2..3..4
         */
        private val targetToSlider = Function { `in`: Int ->
            var x = `in`.toDouble()
            x /= 10.0
            x = x.pow(0.5)
            x *= 2.0
            x = 0.5 * (-1.0 + (1.0 + 4.0 * x).pow(0.5))
            x.toFloat()
        }
    }
}