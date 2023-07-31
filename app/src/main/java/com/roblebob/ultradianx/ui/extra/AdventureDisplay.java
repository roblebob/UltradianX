package com.roblebob.ultradianx.ui.extra;

import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;

public class AdventureDisplay {

    Adventure mAdventure;
    Context mContext;

    public AdventureDisplay(Adventure adventure, Context context) {
        mAdventure = adventure;
        mContext = context;
    }



    public SpannableStringBuilder titleToSpannableStringBuilder(int minFontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append( mAdventure.getTitle());

        int priority = min(100, mAdventure.getPriority().intValue());
        int maxFontSize = priority;


        for (int i = 0; i < mAdventure.getTitle().length(); i++) {
            int fontSize = maxFontSize - 3*i;
            spannableStringBuilder.setSpan( new AbsoluteSizeSpan( max( fontSize , minFontSize), true), i, i + 1, 0);
        }

        final ForegroundColorSpan fcs = new ForegroundColorSpan(getColor());
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        spannableStringBuilder.setSpan( fcs, 0, mAdventure.getTitle().length(), 0);


        return spannableStringBuilder;
    }

    public SpannableStringBuilder titleToSpannableStringBuilder() {
        return titleToSpannableStringBuilder( MIN_FONT_SIZE);
    }


    public SpannableStringBuilder tagToSpannableStringBuilder() {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append( mAdventure.getTags());

        for (int i = 0; i < mAdventure.getTags().length(); i++) {
            int fontSize = MIN_FONT_SIZE + 3*i;
            spannableStringBuilder.setSpan( new AbsoluteSizeSpan( fontSize, true), i, i + 1, 0);

            TopAlignLineHeightSpan topAlignLineHeightSpan = new TopAlignLineHeightSpan(3*i);
            spannableStringBuilder.setSpan(topAlignLineHeightSpan, i, i+1, 0);


        }





        final ForegroundColorSpan fcs = new ForegroundColorSpan(getColor());
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        spannableStringBuilder.setSpan( fcs, 0, mAdventure.getTags().length(), 0);



        return spannableStringBuilder;
    }






    public static final int MIN_FONT_SIZE = 12;


    // TODO: only temporary
    public int getColor() {
        int color = mContext.getColor(R.color.tag_neutral);
        switch (mAdventure.getTags()) {
            case "health":
                color = mContext.getColor(R.color.tag_health);
                break;
            case "theory":
                color = mContext.getColor(R.color.tag_theory);

                break;
            case "coding":
                color = mContext.getColor(R.color.tag_coding);
                break;
            case "music":
                color = mContext.getColor(R.color.tag_music);
                break;
        }
        return color;
    }


}
