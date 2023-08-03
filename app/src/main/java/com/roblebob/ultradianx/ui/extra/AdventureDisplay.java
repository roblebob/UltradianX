package com.roblebob.ultradianx.ui.extra;

import static java.lang.Math.max;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import com.roblebob.ultradianx.R;
import com.roblebob.ultradianx.repository.model.Adventure;

import java.util.function.Function;

public class AdventureDisplay {
    Adventure mAdventure;
    Context mContext;

    public AdventureDisplay(Adventure adventure, Context context) {
        mAdventure = adventure;
        mContext = context;
    }

    public void update(Adventure adventure) {
        mAdventure = adventure;
    }




    public SpannableStringBuilder titleToSpannableStringBuilder(int minFontSize) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append( mAdventure.getTitle());

        int maxFontSize = mAdventure.getPriority().intValue();


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
        spannableStringBuilder.append( mAdventure.getTag());

        for (int i = 0; i < mAdventure.getTag().length(); i++) {
            int fontSize = MIN_FONT_SIZE + 3*i;
            spannableStringBuilder.setSpan( new AbsoluteSizeSpan( fontSize, true), i, i + 1, 0);

            TopAlignLineHeightSpan topAlignLineHeightSpan = new TopAlignLineHeightSpan(3*i);
            spannableStringBuilder.setSpan(topAlignLineHeightSpan, i, i+1, 0);


        }





        final ForegroundColorSpan fcs = new ForegroundColorSpan(getColor());
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        spannableStringBuilder.setSpan( fcs, 0, mAdventure.getTag().length(), 0);



        return spannableStringBuilder;
    }






    public static final int MIN_FONT_SIZE = 12;


    // TODO: only temporary
    public int getColor() {
        int color = mContext.getColor(R.color.tag_neutral);
        switch (mAdventure.getTag()) {
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




    public String priorityToTv() {
        return String.valueOf( mAdventure.getPriority().intValue());
    }


    public float targetToSlider() {
        return targetToSlider.apply(mAdventure.getTarget());
    }

    public int targetToBar() {
        Float x = targetToSlider.apply(mAdventure.getTarget());
        x = x * 25.0f;   // mapping 0..4 to 0..100
        return x.intValue();
    }

    public String targetToTv() {
        Integer target = mAdventure.getTarget();

        if (target <= 90) {
            return target.toString();
        }
        return String.format("%02d:%02d", mAdventure.getTarget() / 60, mAdventure.getTarget() % 60);
    }


    /**
     * maps  0..1..2..3..4  to  0..10..90..360..1000
     */
    public final static Function<Float, Integer> targetFromSlider = (in) -> {
        Double x = in.doubleValue();
        x = x * (x + 1.0);
        x = x / 2.0;
        x = Math.pow(x, 2.0);
        x = 10.0 * x;
        return x.intValue();
    };
    /**
     * maps  0..10..90..360..1000  to  0..1..2..3..4
     */
    private final static Function<Integer, Float> targetToSlider = (in) -> {
        Double x = in.doubleValue();
        x = x / 10.0;
        x = Math.pow(x, 0.5);
        x = x * 2.0;
        x = 0.5 * (-1.0 + Math.pow(1.0 + 4.0 * x, 0.5));
        return x.floatValue();
    };
}
