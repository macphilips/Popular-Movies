package com.rmhub.popularmovies.ui;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * Created by MOROLANI on 4/16/2017
 * <p>
 * owm
 * .
 */

public class MySpanable extends ClickableSpan {
    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpanable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {

        ds.setUnderlineText(isUnderline);

    }

    @Override
    public void onClick(View widget) {

    }
}
