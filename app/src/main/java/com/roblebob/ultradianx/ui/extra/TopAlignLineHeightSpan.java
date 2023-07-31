package com.roblebob.ultradianx.ui.extra;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

public class TopAlignLineHeightSpan implements LineHeightSpan {

    private int lineHeight;

    public TopAlignLineHeightSpan(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    @Override
    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
        fm.ascent = fm.top - lineHeight;
    }
}
